// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit
import MediaPlayer

class MusicGalleryController: APZPlugin, MPMediaPickerControllerDelegate, MusicDelegate {
    var viewController: AppzillonViewController?
    var webView: WKWebView
    var jsonDict: [AnyHashable: Any] = [:]
    var pluginId: String = StringConstants.Generic.emptyString
    var fileBrowserOpenFile: String?
    var filterByExtensions: String?
    @objc var musicDelegate: MusicGalleryDoneDelegate?
    // MARK: - Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView!, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "MusicGalleryController --Execute")
        if !jsonDict.isEmpty {
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            fileBrowserOpenFile = jsonDict[StringConstants.VideoGalleryController.fileBrowserOpenFile] as? String
            filterByExtensions = jsonDict[StringConstants.VideoGalleryController.fileBrowserFilter] as? String
            musicGallery()
        } else {
            cleanUp()
        }
    }
    private func cleanUp() {
        fileBrowserOpenFile = nil
        filterByExtensions = nil
        musicDelegate?.doneMusicGallery(self)
    }
    private func cleanUp(orientation: UIInterfaceOrientation) {
        fileBrowserOpenFile = nil
        filterByExtensions = nil
        musicDelegate?.doneMusicGallery(with: orientation)
    }
    // MARK: MusicGallery method
    private func musicGallery() {
        let mediaPickerController = MPMediaPickerController.init(mediaTypes: .anyAudio)
        mediaPickerController.delegate = self
        mediaPickerController.allowsPickingMultipleItems = false
        mediaPickerController.modalPresentationStyle = UIModalPresentationStyle.fullScreen
        self.viewController?.present(mediaPickerController, animated: true, completion: nil)
    }
    // MARK: Media picker delegate methods
    func mediaPicker(_ mediaPicker: MPMediaPickerController,
                     didPickMediaItems mediaItemCollection: MPMediaItemCollection) {
        if fileBrowserOpenFile == StringConstants.Generic.yes {
            let musicController = MusicViewController.init()
            musicController.musicDelegate = self
            musicController.mediaItemCollection = mediaItemCollection
            let navigationController = UINavigationController.init(rootViewController: musicController)
            self.viewController?.dismiss(animated: true, completion: nil)
            navigationController.modalPresentationStyle = UIModalPresentationStyle.fullScreen
            self.viewController?.present(navigationController, animated: true, completion: nil)
        } else {
            if let thisItem = mediaItemCollection.items.first,
               let itemUrl = thisItem.value(forProperty: MPMediaItemPropertyAssetURL) as? URL {
                callBack(resultKey: ["filePath"], resultValue: [itemUrl.absoluteString], status: true)
                viewController?.dismiss(animated: true, completion: nil)
                cleanUp()
            }
        }
    }
    func mediaPickerDidCancel(_ mediaPicker: MPMediaPickerController) {
        mediaPicker.dismiss(animated: true) {
            self.cleanUp(orientation: WindowUtility.getUiInterfaceOrientation())
        }
        APZLogger.log(logLvl: "I", message: "MusicGalleryController--cancelled")
        callBack(resultKey: [], resultValue: [], status: false)
        cleanUp()
    }
    func cancelMusicView(interface: UIInterfaceOrientation) {
        cleanUp(orientation: interface)
    }
    private func callBack(resultKey: [String], resultValue: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKey,
                                                                    responseValues: resultValue)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
