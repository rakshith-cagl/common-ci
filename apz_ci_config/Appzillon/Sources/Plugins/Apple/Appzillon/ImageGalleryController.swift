// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit
import QuickLook

 class ImageGalleryController: APZPlugin {
    var viewController: AppzillonViewController?
    var webView: WKWebView
     var jsonDict: [AnyHashable: Any] = [:]
    var pluginId: String = StringConstants.Generic.emptyString
    var fileBrowserOpenFile: String?
    var filterByExtensions: String?
    var videoViewController: VideoViewController?
    var popOverController: UIPopoverPresentationController?
    var imageDelegate: ImageGalleryDoneDelegate?
    // MARK: - Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView!, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "ImageGalleryController --Execute")
        if !jsonDict.isEmpty {
            self.jsonDict = jsonDict
            self.pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            self.fileBrowserOpenFile = jsonDict[StringConstants.VideoGalleryController.fileBrowserOpenFile] as? String
            self.filterByExtensions = jsonDict[StringConstants.VideoGalleryController.fileBrowserFilter] as? String
            imageGallery()
        } else {
            cleanUp()
        }
    }
    // MARK: Image Gallery Permissions
    fileprivate func isPhotoLibraryAvailable() -> Bool {
        return UIImagePickerController.isSourceTypeAvailable(.photoLibrary)
    }
    fileprivate func isPhotoAlbumsAvailable() -> Bool {
        return UIImagePickerController.isSourceTypeAvailable(.savedPhotosAlbum)
    }
    fileprivate func canUserPickPhotosFromPhotoLibrary() -> Bool {
        return self.cameraSupportsMedia(paramMediaType: kUTTypeImage as String,
                                        sourceType: UIImagePickerController.SourceType.photoLibrary)
    }
    fileprivate func canUserPickPhotosFromAlbums() -> Bool {
        return self.cameraSupportsMedia(paramMediaType: kUTTypeImage as String,
                                        sourceType: UIImagePickerController.SourceType.savedPhotosAlbum)
    }
    fileprivate func cameraSupportsMedia(paramMediaType: String?,
                                         sourceType paramSourceType: UIImagePickerController.SourceType) -> Bool {
        var result = false
        if let paramMediaType = paramMediaType {
            if let availableMediaTypes = UIImagePickerController.availableMediaTypes(for: paramSourceType) {
                for (_, item) in availableMediaTypes.enumerated() where item == paramMediaType {
                        result = true
                }
            }
            return result
        } else {
            APZLogger.log(logLvl: "I", message: "VideoGalleryController--Media Files not Present")
            return false
        }
    }
    // MARK: Image Gallery Browser
    private func imageGallery() {
        var mediaTypes: [String] = []
        let imagePickerController = UIImagePickerController()
        imagePickerController.sourceType = .photoLibrary
        mediaTypes.append(kUTTypeImage as String)
        imagePickerController.mediaTypes = mediaTypes
        imagePickerController.delegate = self
        let status = isPhotoAlbumsAvailable() && canUserPickPhotosFromAlbums()
        if UIDevice.current.userInterfaceIdiom == .pad {
            if status {
                DispatchQueue.main.async {
                    imagePickerController.modalPresentationStyle = .popover
                    self.viewController?.present(imagePickerController, animated: true, completion: nil)
                    self.popOverController = imagePickerController.popoverPresentationController
                    self.popOverController?.sourceView = self.viewController?.view
                    self.popOverController?.sourceRect = CGRect(x: 0, y: 0, width: 400, height: 400)
                    self.popOverController?.permittedArrowDirections = .any
                }
            } else {
                dismissVCAndNoImageGalleryAccess()
            }
        } else {
            if status {
                imagePickerController.modalPresentationStyle = .fullScreen
                self.viewController?.present(imagePickerController, animated: true, completion: nil)
            } else {
                dismissVCAndNoImageGalleryAccess()
            }
        }
    }
    private func showImage(selectedImagePath: String) {
        let imgViewController = ImageViewController(imagePath: selectedImagePath)
        imgViewController.setupImage(customNavigationItem: true, withJson: self.jsonDict)
        imgViewController.delegate = self
        self.viewController?.dismiss(animated: true, completion: nil)
        self.popOverController = nil
        let navigator = UINavigationController(rootViewController: imgViewController)
        navigator.modalPresentationStyle = .fullScreen
        self.viewController?.present(navigator, animated: false, completion: nil)
    }
    private func saveImageSelected(imgData: Data) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = StringConstants.FileBrowser.dateFormat
        let currentDateString = dateFormatter.string(from: Date())
        let fileName = "image_" + currentDateString + ".jpg"
        let appString = self.viewController?.appString ?? StringConstants.Generic.emptyString
        var appSandboxPath = FileManagerUtility.documentDirectory().appendingPathComponent("Assets/apps/\(appString)/").path
        let photoDir = FileManagerUtility.documentDirectory().appendingPathComponent("Assets/apps/\(appString)/photo/")
        do {
            try FileManager.default.createDirectory(atPath: photoDir.path,
                                                    withIntermediateDirectories: true,
                                                    attributes: nil)
            appSandboxPath = photoDir.appendingPathComponent(fileName).path
            let isImageSaved = FileManagerUtility.write(imgData, toFile: URL(fileURLWithPath: appSandboxPath))
            if isImageSaved {
                APZLogger.log(logLvl: "I", message: "ImageGalleryController --Image Saved to temp location")
            } else {
                APZLogger.log(logLvl: "E", message: "ImageGalleryController --Could not save images")
                appSandboxPath = StringConstants.Generic.emptyString // In objc they are sending nil
            }
        } catch let error {
            APZLogger.log(logLvl: "E", message: "ImageGalleryController --Unable to save image to url")
            print(error.localizedDescription)
        }
        return appSandboxPath
    }
    private func noImageSavedToTemp() {
        callBack(resultKey: [StringConstants.Generic.errorCode],
                 resultValue: [StringConstants.FileBrowser.imageAccessFailed], status: false)
    }
    private func dismissVCAndNoImageGalleryAccess() {
        callBack(resultKey: [StringConstants.Generic.errorCode],
                 resultValue: [StringConstants.FileBrowser.imageAccessDeined], status: false)
        viewController?.dismiss(animated: true, completion: nil)
        APZLogger.log(logLvl: "E", message: "ImageGalleryController --User cannot pick photos from Gallery")
        cleanUp()
    }
     private func callBack(resultKey: [String], resultValue: [Any], status: Bool ) {
         let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                     status: status,
                                                                     keepAlive: false,
                                                                     responseKeys: resultKey,
                                                                     responseValues: resultValue)
         MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                parameter: params)
     }
    private func cleanUp() {
        fileBrowserOpenFile = nil
        filterByExtensions = nil
        popOverController = nil
        imageDelegate?.doneImageGallery(sender: self)
    }
    private func cleanUp(orientation: UIInterfaceOrientation) {
        fileBrowserOpenFile = nil
        filterByExtensions = nil
        popOverController = nil
        imageDelegate?.doneImageGalleryWithOrientation(orientation: orientation)
    }
}

extension ImageGalleryController: UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    fileprivate func dismissPicker(_ deviceIsIpad: (Bool), _ picker: UIImagePickerController) {
        if deviceIsIpad {
            self.viewController?.dismiss(animated: true, completion: nil)
            self.popOverController = nil
        } else {
            picker.dismiss(animated: true, completion: nil)
        }
    }
    func imagePickerController(_ picker: UIImagePickerController,
                               didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
        var imagePath = StringConstants.Generic.emptyString
        let deviceIsIpad = (UIDevice.current.userInterfaceIdiom == .pad)
        if let orgImage = info[.originalImage] as? UIImage {
            if let dataImg = orgImage.jpegData(compressionQuality: 1.0) {
                imagePath = saveImageSelected(imgData: dataImg)
                if !imagePath.isEmpty && self.fileBrowserOpenFile == StringConstants.Generic.no {
                    callBack(resultKey: [StringConstants.Generic.filePath], resultValue: [imagePath], status: true)
                    self.viewController?.dismiss(animated: true, completion: nil)
                    cleanUp()
                } else if !imagePath.isEmpty && self.fileBrowserOpenFile == StringConstants.Generic.yes {
                    if !deviceIsIpad {
                        picker.dismiss(animated: true, completion: nil)
                    }
                    showImage(selectedImagePath: imagePath)
                } else {
                    noImageSavedToTemp()
                    cleanUp()
                }
                dismissPicker(deviceIsIpad, picker)
            }
        } else {
            APZLogger.log(logLvl: "E", message: "Unable to fetch photo from Image Picker Delegate")
        }
    }
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        if UIDevice.current.userInterfaceIdiom == .pad {
            self.viewController?.dismiss(animated: true, completion: nil)
        } else {
            picker.dismiss(animated: true) {
                self.cleanUp(orientation: WindowUtility.getUiInterfaceOrientation())
            }
        }
        callBack(resultKey: [], resultValue: [], status: false)
        APZLogger.log(logLvl: "I", message: "ImageGalleryController --Cancelled")
        cleanUp()
    }
}
extension ImageGalleryController: ImageViewDelegate {
    func cancelImageView(_ interfaceOrientation: UIInterfaceOrientation) {
        callBack(resultKey: ["message"], resultValue: ["Image view Cancelled"], status: true)
        cleanUp(orientation: interfaceOrientation)
    }
    func imageViewCallback(_ imagePath: String, status: Bool) {
        if status {
            callBack(resultKey: [StringConstants.Generic.filePath], resultValue: [imagePath], status: status)
        } else {
            callBack(resultKey: [StringConstants.Generic.errorCode],
                     resultValue: [StringConstants.FileBrowser.imageAccessFailed], status: status)
        }
        cleanUp()
    }
}
