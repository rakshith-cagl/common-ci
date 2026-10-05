// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import QuickLook

class APZOpenFile: APZPlugin, FileOpenDelegate {

    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var filePath: String = StringConstants.Generic.emptyString
    let fileOpenUtility = FileOpenUtility()
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            filePath = jsonDict[StringConstants.Generic.filePath] as? String ?? StringConstants.Generic.emptyString
            filePath = filePath.trimmingCharacters(in: .whitespaces)
            if !filePath.isEmpty && FileManager.default.fileExists(atPath: filePath) {
                fileOpenUtility.filePathUrl = URL(fileURLWithPath: filePath)
                fileOpenUtility.delegate = self
                fileOpenUtility.viewDocument(viewController: viewController, presentationStyle: .fullScreen)
            } else {
                docViewerCallback(resultKeys: [StringConstants.Generic.errorCode],
                                  resultValues: [StringConstants.FileOperation.invalidSrc],
                                  status: false)
                APZLogger.log(logLvl: "E", message: "APZOpenFile--No valid file path")
            }
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "D", message: "FileSize Done")
        viewController = nil
        self.delegate.donePlugin(self)
    }
    func cleanPluginWithOrientation(_ orientation: UIInterfaceOrientation) {
        APZLogger.log(logLvl: "D", message: "FileSize Done")
        viewController = nil
        delegate.donePluginWithOrientaion(self, orientation)
    }
    // MARK: File View Method Callbacks
    func cancelDocViewerCallback() {
        viewController?.dismiss(animated: true, completion: {
            self.docViewerCallback(resultKeys: [StringConstants.Generic.message],
                                   resultValues: [StringConstants.FileOperation.documentCancelled],
                                   status: true)
            self.cleanPluginWithOrientation(WindowUtility.getUiInterfaceOrientation())
        })
    }
    func docViewerCallback(resultKeys: [String], resultValues: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
}
