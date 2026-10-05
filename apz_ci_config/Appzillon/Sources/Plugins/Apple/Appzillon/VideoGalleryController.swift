//
//  VideoGalleryController.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 10/11/21.
//

import Foundation
import UIKit

protocol VideoGalleryDoneDelegate: AnyObject {
    func doneVideoGallery(sender: Any?)
    func doneVideoGalleryWithOrientation(sender: Any?, orientation: UIInterfaceOrientation)
}

 class VideoGalleryController: APZPlugin, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    var viewController: AppzillonViewController?
    var webView: WKWebView
     var requestJson: [AnyHashable: Any] = [:]
    var pluginId: String = StringConstants.Generic.emptyString
    var fileBrowserOpenFile: String?
    var filterByExtensions: String?
    var videoViewController: VideoViewController?
    var popOverController: UIPopoverPresentationController?
    var videoDelegate: VideoGalleryDoneDelegate?
    // MARK: - Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView!, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            self.requestJson = jsonDict
            self.pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            self.fileBrowserOpenFile = jsonDict[StringConstants.VideoGalleryController.fileBrowserOpenFile] as? String
            self.filterByExtensions = jsonDict[StringConstants.VideoGalleryController.fileBrowserFilter] as? String
            self.openVideoGallery()
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        self.viewController = nil
        self.fileBrowserOpenFile = nil
        self.filterByExtensions = nil
        self.popOverController = nil
        self.videoDelegate?.doneVideoGallery(sender: self)
    }
    func cleanUp(_ orientation: UIInterfaceOrientation) {
        self.viewController = nil
        self.fileBrowserOpenFile = nil
        self.filterByExtensions = nil
        self.popOverController = nil
        self.videoDelegate?.doneVideoGalleryWithOrientation(sender: self, orientation: orientation)
    }
    // MARK: - Video Gallery Permissions
    fileprivate func isPhotoLibraryAvailable() -> Bool {
        return UIImagePickerController.isSourceTypeAvailable(.photoLibrary)
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
    fileprivate func canUserPickVideosFromPhotoLibrary() -> Bool {
        return self.cameraSupportsMedia(paramMediaType: kUTTypeMovie as String,
                                        sourceType: UIImagePickerController.SourceType.savedPhotosAlbum)
    }
    // MARK: - Video Gallery
    fileprivate func openVideoGallery() {
        if self.isPhotoLibraryAvailable() && self.canUserPickVideosFromPhotoLibrary() {
            if UIDevice.current.userInterfaceIdiom == .pad {
                self.openVideoGalleryForIpad()
            } else {
                self.openVideoGalleryForIphone()
            }
        } else {
            self.videoGalleryAccessDenied()
            self.viewController?.dismiss(animated: true, completion: nil)
            self.cleanPlugin()
        }
    }
    fileprivate func openVideoGalleryForIpad() {
        let imagePickerController =  UIImagePickerController()
        imagePickerController.sourceType = .photoLibrary
        var mediaTypes = [String]()
        mediaTypes.append(kUTTypeMovie as String)
        imagePickerController.mediaTypes = mediaTypes
        imagePickerController.delegate = self
        imagePickerController.modalPresentationStyle = .popover
        self.popOverController = imagePickerController.popoverPresentationController
        self.popOverController?.sourceRect = CGRect(x: 0, y: 0, width: 400, height: 400)
        self.popOverController?.sourceView = viewController?.view
        self.popOverController?.permittedArrowDirections = .any
        viewController?.present(imagePickerController, animated: true)
    }
    fileprivate func openVideoGalleryForIphone() {
        let imagePickerController =  UIImagePickerController()
        imagePickerController.sourceType = .photoLibrary
        var mediaTypes = [String]()
        mediaTypes.append(kUTTypeMovie as String)
        imagePickerController.mediaTypes = mediaTypes
        imagePickerController.delegate = self
        imagePickerController.modalPresentationStyle = .fullScreen
        viewController?.present(imagePickerController, animated: true)
    }
    // MARK: - Image Picker Delegate
     func imagePickerController(_ picker: UIImagePickerController,
                                didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
        let videoURL = info[UIImagePickerController.InfoKey.mediaURL] as? URL
        var videoData: Data?
        if let videoURL = videoURL {
            do {
                videoData = try Data(contentsOf: videoURL)
                if let videoPath = saveSelectedVideo(videoData: videoData), !videoPath.isEmpty {
                    picker.allowsEditing = false
                    picker.videoQuality = .typeMedium
                    if UIDevice.current.userInterfaceIdiom == .pad {
                        self.handleVideoPathForIpad(videoPath: videoPath)
                    } else {
                        self.handleVideoPathForIphone(videoPath: videoPath, picker: picker)
                    }
                }
            } catch {
                APZLogger.log(logLvl: "E", message: "VideoGalleryController--Unable to get content")
            }
        }
        self.cleanPlugin()
    }
      func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        self.viewController?.dismiss(animated: true)
        picker.dismiss(animated: true) { [self] in
            self.cleanUp(WindowUtility.getUiInterfaceOrientation())
        }
        let resultkeys = [StringConstants.Generic.errorMessage]
        let result = [StringConstants.VideoGalleryController.galleryClosed]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: result)
          MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                 jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                 parameter: params)

    }
    // MARK: - Helper Methods
    fileprivate  func handleVideoPathForIpad(videoPath: String) {
        if !videoPath.isEmpty && self.fileBrowserOpenFile == StringConstants.Generic.no {
            self.videoFilePathCallBack(videoPath: videoPath)
        } else if !videoPath.isEmpty && self.fileBrowserOpenFile == StringConstants.Generic.yes {
            self.showVideo(videoPath)
        } else {
            APZLogger.log(logLvl: "I", message: "VideoGalleryController--Unable to save Video Selected to tmp location")
        }
        self.viewController?.dismiss(animated: true, completion: nil)
    }
    fileprivate func handleVideoPathForIphone(videoPath: String,
                                              picker: UIImagePickerController) {
        if !videoPath.isEmpty && self.fileBrowserOpenFile == StringConstants.Generic.no {
            self.videoFilePathCallBack(videoPath: videoPath)
        } else if !videoPath.isEmpty && self.fileBrowserOpenFile == StringConstants.Generic.yes {
            picker.dismiss(animated: true, completion: nil)
            self.showVideo(videoPath)
        } else {
            APZLogger.log(logLvl: "I", message: "VideoGalleryController--Unable to save Video Selected to tmp location")
        }
        picker.dismiss(animated: true, completion: nil)
    }
    fileprivate func fetchFileName() -> String {
        let currDate = Date()
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = StringConstants.VideoGalleryController.dateFormat
        let dateString = dateFormatter.string(from: currDate)
        var fileName = StringConstants.VideoGalleryController.videoGalleryFileName
        fileName +=  dateString
        fileName += StringConstants.FileExtensions.mp4
        return fileName
    }
    fileprivate  func crateFilePathAndwrite(videoData: Data) -> (status: Bool, appSandboxPath: String) {
        var saveImage: Bool = false
        var appSandboxPath: String = StringConstants.Generic.emptyString
        let fileName = self.fetchFileName()
        let paths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).map(\.path)
        let docDirectory = paths[0]
        if let appString = viewController?.appString {
            appSandboxPath = URL(fileURLWithPath: docDirectory).appendingPathComponent("Assets/apps/\(appString)/").path
            let videoGalleryDirName = StringConstants.VideoGalleryController.videoGalleryDirName
            let photosDir = URL(fileURLWithPath: appSandboxPath).appendingPathComponent(videoGalleryDirName).path
            do {
                try FileManager.default.createDirectory(atPath: photosDir,
                                                        withIntermediateDirectories: true,
                                                        attributes: nil)
            } catch {
                APZLogger.log(logLvl: "E", message: "Unable to create directory")
            }
            appSandboxPath = URL(fileURLWithPath: photosDir).appendingPathComponent(fileName).path
            saveImage = FileManagerUtility.write(videoData, toFile: URL(fileURLWithPath: appSandboxPath))
        }
        return (saveImage, appSandboxPath)
    }
    fileprivate func saveSelectedVideo(videoData: Data?) -> String? {
        var appSandboxPath: String = StringConstants.Generic.emptyString
        if let videoData = videoData {
            let result = self.crateFilePathAndwrite(videoData: videoData)
            if result.status {
                let msgString = "VedioGalleryController--Saved video to  path\(result.appSandboxPath))"
                appSandboxPath = result.appSandboxPath
                APZLogger.log(logLvl: "I", message: msgString)
            } else {
                APZLogger.log(logLvl: "I", message: "Not able to save images to sandbox")
                appSandboxPath = StringConstants.Generic.emptyString
            }
        } else {
            APZLogger.log(logLvl: "I", message: "Unable to save video to url")
            appSandboxPath = StringConstants.Generic.emptyString
        }
        return appSandboxPath
    }
    fileprivate  func showVideo(_ videoPath: String) {
        videoViewController = VideoViewController(videoPath: videoPath)
        self.popOverController = nil
        videoViewController?.modalPresentationStyle = .fullScreen
        viewController?.present(videoViewController!, animated: false)
    }
    // MARK: - Callbacks
    fileprivate func videoGalleryAccessDenied() {
        let resultkeys = [StringConstants.Generic.errorCode]
        let result = [StringConstants.VideoGalleryController.videoAccessDenied]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: result)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        APZLogger.log(logLvl: "I", message: "VidoGalleryController-No Accees to video gallery")
    }
     fileprivate  func videoFilePathCallBack(videoPath: String) {
        let resultkeys = [StringConstants.Generic.filePath]
        let resultValue = [videoPath]
         let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                     status: true,
                                                                     keepAlive: false,
                                                                     responseKeys: resultkeys,
                                                                     responseValues: resultValue)
         MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                parameter: params)

    }
}
