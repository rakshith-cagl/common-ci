//
//  APZCamera.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 30/07/21.
//

import Foundation
import UIKit
import AVFoundation

class APZCamera: APZPlugin, CropperViewControllerDelegate {
    var viewController: AppzillonViewController?
    var cropperViewController: CropperViewController?
    var isFlip: Bool = false
    var isRotate: Bool = false
    var isAspectRatio: Bool = false
    var isAnglurRule: Bool = false
    var webView: WKWebView
    var requestJson: [AnyHashable: Any] = [:]
    var pluginId: String = StringConstants.Generic.emptyString
    var fileName: String?
    var cameraAction: String?
    // MARK: - Plugin LifeCycle Methods
    public override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, requestJson)
    }
    open override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            print("APZCamera--execute")
            self.requestJson = jsonDict
            self.pluginId = requestJson[StringConstants.Generic.pluginId]
                as? String ?? StringConstants.Generic.emptyString
            self.fileName = self.requestJson[StringConstants.Generic.fileName] as? String
            self.cameraAction = self.requestJson[StringConstants.Generic.action] as? String
            if !self.checkCameraAvailability() {
                self.imageCapturedCallback(status: false, message: StringConstants.Generic.cameraNotFound)
            }
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        self.viewController = nil
        self.cropperViewController = nil
        print("APZCamera--Done")
        self.delegate.donePlugin(self)
    }
    // MARK: - Camera method
    func requestForAccess(completionHandler: @escaping (_ accessGranted: Bool) -> Void) {
        let cameraMediaType = AVMediaType.video
        let cameraAuthorizationStatus = AVCaptureDevice.authorizationStatus(for: cameraMediaType)
        switch cameraAuthorizationStatus {
        case .authorized:
            completionHandler(true)
        case .denied, .notDetermined:
            AVCaptureDevice.requestAccess(for: cameraMediaType) { granted in
                completionHandler(granted)
            }
        default:
            completionHandler(false)
        }
    }
    func checkCameraAvailability() -> Bool {
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            let frontCamera = self.requestJson[StringConstants.Camera.frontCamera] as? String
            let flashRequired = self.requestJson[StringConstants.Camera.flash] as? String
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.allowsEditing = false
            imagePicker.isEditing = true
            let sourceType = self.requestJson[StringConstants.Generic.sourceType] as? String
            switch sourceType {
            case StringConstants.Generic.album:
                imagePicker.modalPresentationStyle = .formSheet
                imagePicker.sourceType = UIImagePickerController.SourceType.savedPhotosAlbum
            case StringConstants.Generic.photo:
                imagePicker.modalPresentationStyle = .formSheet
                imagePicker.sourceType = UIImagePickerController.SourceType.photoLibrary
            default:
                imagePicker.sourceType = UIImagePickerController.SourceType.camera
                if frontCamera == StringConstants.Generic.yes {
                    imagePicker.cameraDevice = .front
                } else {
                    imagePicker.cameraDevice = .rear
                }
                if flashRequired == StringConstants.Generic.yes {
                    imagePicker.cameraFlashMode = .on
                }
            }
            imagePicker.modalPresentationStyle = .fullScreen
            self.requestForAccess { [self] (accessGranted) -> Void in
                if accessGranted {
                    DispatchQueue.main.async { [weak self] in
                        self?.viewController?.present(imagePicker, animated: true)
                    }
                } else {
                    self.imageCapturedCallback(status: false, message: StringConstants.Generic.cameraNotFound)
                }
            }
            print("APZCamera--camera open")
            return true
        } else {
            return false
        }
    }
    // MARK: - Crop
    fileprivate func setupCropController(image: UIImage) {
        if self.requestJson[StringConstants.Camera.flipRequired] as? String == StringConstants.Generic.yes {
            self.isFlip = true
        }
        if self.requestJson[StringConstants.Camera.rotationRequired] as? String == StringConstants.Generic.yes {
            self.isRotate = true
        }
        if self.requestJson[StringConstants.Camera.aspectRatioRequired] as? String == StringConstants.Generic.yes {
            self.isAspectRatio = true
        }
        if self.requestJson[StringConstants.Camera.angularScaleRequired] as? String == StringConstants.Generic.yes {
            self.isAnglurRule = true
        }
        self.cropperViewController = CropperViewController(originalImage: image,
                                                           isAngleRulerRequired: self.isAnglurRule,
                                                           isflipRequired: self.isFlip,
                                                           isAspectratioRequired: self.isAspectRatio,
                                                           isRotationRequired: self.isRotate)
        self.cropperViewController?.delegate = self
        self.viewController?.present(self.cropperViewController!, animated: true)
    }
    // MARK: - Helper Methods
    fileprivate func modifyImage(_ image: UIImage) {
        var targetHeight = (self.requestJson[StringConstants.Camera.targetHeight] as? NSNumber)?.doubleValue ?? 0.0
        var targetWidth = (self.requestJson[StringConstants.Camera.targetWidth] as? NSNumber)?.doubleValue ?? 0.0
        if targetHeight == 0 && targetWidth == 0 {
            targetWidth = 612
            targetHeight = 816
        } else if targetWidth == 0 {
            let ratio: Double = Double(image.size.width / image.size.height)
            targetWidth = ratio * targetHeight
        } else if targetHeight == 0 {
            let ratio: Double = Double(image.size.width / image.size.height)
            targetHeight = ratio * targetWidth
        }
        let dataImg = self.compressedImageData(image: image, targetWidth, targetHeight)
        self.handleCallBacks(dataImg: dataImg)
    }
    fileprivate func compressedImageData(image: UIImage, _ width: Double, _ height: Double) -> Data? {
        let compressedImage = modifiedImage(with: image, width, height)
        var quality = self.requestJson[StringConstants.Generic.quality] as? String
        if quality == nil || (quality == StringConstants.Generic.emptyString) {
            quality = StringConstants.Camera.maxQuality
        }
        var commpressionRate: CGFloat = 0
        if let qualityfactor = quality {
            commpressionRate = ( CGFloat(Int(qualityfactor) ?? 100) / 100)
        }
        let encodingType = self.requestJson[StringConstants.Generic.encodingTyp] as? String
        var dataImg: Data?
        if self.requestJson[StringConstants.Camera.uncompressed] as? String == StringConstants.Generic.yes {
            if encodingType == StringConstants.Generic.png {
                if let image = image.pngData() {
                    dataImg = image
                }
            } else {
                if let image = image.jpegData(compressionQuality: 1.0) {
                    dataImg = image
                }
            }
        } else {
            if encodingType == StringConstants.Generic.png {
                if let image = compressedImage?.pngData() {
                    dataImg = image
                }
            } else {
                if let rate = compressedImage?.jpegData(compressionQuality: commpressionRate) {
                    dataImg = rate
                }
            }
        }
        return dataImg
    }
    fileprivate func handleCallBacks(dataImg: Data?) {
        if dataImg != nil {
            if cameraAction == StringConstants.Generic.base64 || cameraAction == StringConstants.Camera.base64save {
                if let cameraImageString = dataImg?.base64EncodedString() {
                    if cameraAction == StringConstants.Generic.base64 {
                        self.base64ImageCallBack(status: true,
                                                 message: StringConstants.Camera.fileToBase64,
                                                 base64Image: cameraImageString, imagePath: nil)
                    } else if cameraAction == StringConstants.Camera.base64save {
                        self.saveBase64File(dataImg: dataImg, cameraImageString: cameraImageString)
                    }
                } else {
                    self.base64ImageCallBack(status: false,
                                             message: StringConstants.Camera.fileCreationFailure,
                                             base64Image: nil,
                                             imagePath: nil)
                }
            } else {
                self.saveFile(dataImg: dataImg)
            }
        } else {
            self.imageCapturedCallback(status: false, message: StringConstants.Camera.imageConversionErr)
        }
    }
    func saveFile(dataImg: Data?) {
        if let urlPath = path(for: fileName, forAction: cameraAction) {
            let completeUrlPath = urlPath + StringConstants.FileExtensions.jpg
            do {
                try dataImg?.write(to: URL(fileURLWithPath: completeUrlPath), options: .completeFileProtection)
                self.imageCallBackWithoutBase64(status: true,
                                                message: StringConstants.Camera.writeLocationSuccess,
                                                imagePath: completeUrlPath)
            } catch {
                self.imageCallBackWithoutBase64(status: false,
                                                message: StringConstants.Camera.writeLocationFailed, imagePath: nil)
            }
        }
    }
    func saveBase64File(dataImg: Data?, cameraImageString: String) {
        if let urlPath = path(for: fileName, forAction: cameraAction) {
            let completeUrlPath = urlPath + StringConstants.FileExtensions.jpg
            do {
                try dataImg?.write(to: URL(fileURLWithPath: completeUrlPath), options: .completeFileProtection)
                self.base64ImageCallBack(status: true,
                                         message: StringConstants.Camera.writeSuccess,
                                         base64Image: cameraImageString,
                                         imagePath: completeUrlPath)
            } catch {
                self.base64ImageCallBack(status: false,
                                         message: StringConstants.Camera.fileToBase64,
                                         base64Image: cameraImageString,
                                         imagePath: nil)
            }
        } else {
            self.base64ImageCallBack(status: false,
                                     message: StringConstants.Camera.fileCreationFailure,
                                     base64Image: cameraImageString, imagePath: nil)
        }
    }
    fileprivate func path(for filename: String?, forAction action: String?) -> String? {
        guard let fileName = filename else {
            return nil
        }
        var documentsDirectory: String?
        if action == StringConstants.Camera.srcUrl {
            documentsDirectory = NSTemporaryDirectory()
            documentsDirectory = (documentsDirectory ?? StringConstants.Generic.emptyString) + (fileName)
        } else if action == StringConstants.Generic.save || action == StringConstants.Camera.base64save {
            let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
            var photDir = urls[0]
            let sandBoxPath = StringConstants.Generic.sandBoxPath
            let photo = StringConstants.Generic.photo
            photDir.appendPathComponent("\(sandBoxPath)\(self.viewController?.appString ?? "")/\(photo)/")
            do {
                try FileManager.default.createDirectory(at: photDir, withIntermediateDirectories: true, attributes: nil)
            } catch {
                print("Error in creating image file")
            }
            documentsDirectory = photDir.appendingPathComponent(fileName).path
        } else {
            documentsDirectory = StringConstants.Generic.emptyString
        }
        return documentsDirectory
    }

    fileprivate func modifiedImage(with image: UIImage?, _ width: Double, _ height: Double) -> UIImage? {
        let newSize = CGSize(width: CGFloat(width), height: CGFloat(height))
        UIGraphicsBeginImageContext(newSize)
        image?.draw(in: CGRect(x: 0, y: 0, width: CGFloat(width), height: CGFloat(height)))
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return newImage
    }
    // MARK: - Delegate Methods
    public func cropperDidConfirm(_ cropper: CropperViewController, state: CropperState?) {
        cropper.dismiss(animated: true, completion: nil)
        if let state = state,
           let image = cropper.originalImage.cropped(withCropperState: state) {
            self.modifyImage(image)
        } else {
            self.imageCapturedCallback(status: false, message: StringConstants.Camera.editFailed)
        }
    }
    func cropperDidCancel(_ cropper: CropperViewController) {
        let resultkeys = [StringConstants.Generic.errorMessage]
        let result = [StringConstants.Camera.userCancelled]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: result)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        viewController?.dismiss(animated: true)
        print("APZCamera--cropper cancel")
        cleanPlugin()
    }
    // MARK: - callbacks
    func imageCallBackWithoutBase64(status: Bool, message: String?, imagePath: String?) {
        if status {
            if let imagePath = imagePath {
                let resultkeys = [StringConstants.Generic.jsonPath, StringConstants.Generic.encodedImage]
                let result = [imagePath, StringConstants.Generic.emptyString]
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: true,
                                                                            keepAlive: false,
                                                                            responseKeys: resultkeys,
                                                                            responseValues: result)
                MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
                print("APZCamera--write location success")
            }
        } else {
            let resultkeys = [StringConstants.Generic.errorCode]
            let result = [StringConstants.Camera.fileCouldNotBeCreated]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: result)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            print("APZCamera--write location failure")
        }
        cleanPlugin()
    }
    func base64ImageCallBack(status: Bool, message: String?, base64Image: String?, imagePath: String?) {
        if status {
            if let base64Image = base64Image {
                if let imagePath = imagePath {
                    let resultkeys = [StringConstants.Generic.jsonPath, StringConstants.Generic.encodedImage]
                    let result = [imagePath, base64Image]
                    let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                                status: true,
                                                                                keepAlive: false,
                                                                                responseKeys: resultkeys,
                                                                                responseValues: result)
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                           jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: params)
                    print("APZCamera--write success")
                } else {
                    let resultkeys = [StringConstants.Generic.jsonPath, StringConstants.Generic.encodedImage]
                    let result = [StringConstants.Generic.emptyString, "\(base64Image)"]
                    let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                                status: true,
                                                                                keepAlive: false,
                                                                                responseKeys: resultkeys,
                                                                                responseValues: result)
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                           jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: params)
                    print("APZCamera--base 64 success")
                }
            } else {
                let resultkeys = [StringConstants.Generic.errorCode]
                let result = [StringConstants.Camera.fileCouldNotBeCreated]
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: false,
                                                                            keepAlive: false,
                                                                            responseKeys: resultkeys,
                                                                            responseValues: result)
                MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
                print("APZCamera--file could not be created")
            }
        } else {
            let resultkeys = [StringConstants.Generic.errorCode]
            let result = [StringConstants.Camera.encodeFailBase64]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: result)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            print("APZCamera--Unable to encode Image in Base64")
        }
        cleanPlugin()
    }
    func imageCapturedCallback(status: Bool, message: String) {
        if !status {
            let resultkeys = [StringConstants.Generic.errorCode]
            let result = [message]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: result)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
        cleanPlugin()
    }
}

extension APZCamera: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    public func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        let resultkeys = [StringConstants.Generic.errorMessage]
        let result = [StringConstants.Camera.userCancelled]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: result)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        viewController?.dismiss(animated: true)
        print("APZCamera--Image Conversion Failure")
        cleanPlugin()
    }
    public func imagePickerController(_ picker: UIImagePickerController,
                                      didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
        print("APZCamera-- image captured")
        if let image = info[UIImagePickerController.InfoKey.originalImage] as? UIImage {
            if self.requestJson[StringConstants.Generic.crop] as? String == StringConstants.Generic.yes {
                picker.dismiss(animated: true) {
                    self.setupCropController(image: image)
                }
            } else {
                modifyImage(image)
                picker.dismiss(animated: true)
          }
        }
    }
}
