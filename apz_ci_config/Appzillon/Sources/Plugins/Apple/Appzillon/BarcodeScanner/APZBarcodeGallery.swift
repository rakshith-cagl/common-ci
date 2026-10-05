//
//  BarCodeGallery.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 06/07/21.
//

import Foundation
import AVFoundation

class APZBarcodeGallery: APZPlugin {
    var viewController: AppzillonViewController?
    var webView: WKWebView
    var requestJson: [AnyHashable: Any] = [:]
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: - Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, requestJson)
    }
    open override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            self.requestJson = jsonDict
            print("APZBarcodeGallery--Execute")
            self.pluginId = self.requestJson[StringConstants.Generic.pluginId]
                as? String ?? StringConstants.Generic.emptyString
            if let action = requestJson[StringConstants.Generic.action] as? String,
               action == StringConstants.BarcodeScanner.photoGallery {
                self.openPhotoGallery()
            } else {
                self.scanImageUsingPath(jsonDict: self.requestJson)
            }
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        print("APZBarcodeGallery--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: - Implementation
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
    func openPhotoGallery() {
        let imagePicker = UIImagePickerController()
        imagePicker.delegate = self
        imagePicker.allowsEditing = false
        imagePicker.isEditing = true
        imagePicker.sourceType = .photoLibrary
        imagePicker.modalPresentationStyle = .fullScreen
        self.requestForAccess { [self] (accessGranted) -> Void in
            if accessGranted {
                DispatchQueue.main.async { [weak self] in
                    self?.viewController?.present(imagePicker, animated: true)
                }
            } else {
                self.callBackForScannedCode(message: StringConstants.Generic.cameraNotFound, status: false)
            }
        }
        print("BarCode Gallery---Open")
    }
    func scanImageUsingPath(jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            if let imagePath = jsonDict[StringConstants.Generic.filePath] as? String,
               imagePath != StringConstants.Generic.emptyString {
                if FileManager.default.fileExists(atPath: imagePath) {
                    readBarcode(imagePath)
                } else {
                    self.callBackForScannedCode(message: StringConstants.BarcodeScanner.imagePathNotFound,
                                                status: false)
                }
            } else {
                self.callBackForScannedCode(message: StringConstants.BarcodeScanner.imagePathNotFound, status: false)
            }
        } else {
            cleanPlugin()
        }
    }
    fileprivate func readBarcode(_ imagePath: String) {
        let url = URL(fileURLWithPath: imagePath)
        do {
            let resData = try Data(contentsOf: url)
            if let imageFromGallery = UIImage(data: resData) {
                self.getBarcodeDataUsingImage(image: imageFromGallery)
            } else {
                self.callBackForScannedCode(message: StringConstants.BarcodeScanner.scanFailed,
                                            status: false)
            }
        } catch {
            self.callBackForScannedCode(message: StringConstants.BarcodeScanner.scanFailed, status: false)
        }
    }
    func getBarcodeDataUsingImage(image: UIImage) {
        if let decodedData = detectQRCode(image: image), !decodedData.isEmpty {
                decodedData.forEach { feature in
                    if let featureMessage = feature.messageString {
                        self.callBackForScannedCode(message: featureMessage, status: true)
                    }
                }
        } else {
            self.callBackForScannedCode(message: StringConstants.BarcodeScanner.scanFailed, status: false)
        }
    }
    func detectQRCode(image: UIImage) -> [CIQRCodeFeature]? {
        autoreleasepool {
            var ciImage = CIImage()
            if let cgImage = image.cgImage {
                ciImage = CIImage(cgImage: cgImage)
            }
            var options: [String: Any]?
            let context = CIContext()
            options = [CIDetectorAccuracy: CIDetectorAccuracyHigh]
            let qrDetector = CIDetector(ofType: CIDetectorTypeQRCode, context: context, options: options)
            let value = ciImage.properties.keys.contains(kCGImagePropertyOrientation as String)
            if value {
                options = [CIDetectorImageOrientation: value]
            } else {
                options = [CIDetectorImageOrientation: NSNumber(value: 1)]
            }
            let features = qrDetector?.features(in: ciImage, options: options) as? [CIQRCodeFeature]
            return features
        }
    }
    // MARK: - Callback
    func callBackForScannedCode(message: String, status: Bool) {
        if status {
            let resultkeys = [StringConstants.Generic.text]
            let returnResult = [message]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: returnResult)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        } else {
            let resultkeys = [StringConstants.Generic.errorMessage]
            let returnResult = [message]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: returnResult)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
        cleanPlugin()
    }
}

// MARK: Image Picker Delegate

extension APZBarcodeGallery: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    public func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        picker.dismiss(animated: true, completion: {
            let resultkeys = [StringConstants.Generic.errorMessage]
            let result = [StringConstants.BarcodeScanner.errorMessageString]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: self.pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: result)
            MiscellaneousMethod.shared.jsLayerCall(webView: self.webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            self.viewController?.dismiss(animated: true)
            self.cleanPlugin()
        })
    }
    public func imagePickerController(_ picker: UIImagePickerController,
                                      didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
        picker.dismiss(animated: true)
        if let image = info[UIImagePickerController.InfoKey.originalImage] as? UIImage {
            picker.dismiss(animated: true) { [self] in
                self.getBarcodeDataUsingImage(image: image)
            }
        }
    }
}
