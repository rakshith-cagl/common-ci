// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZFileSize: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            APZLogger.log(logLvl: "I", message: "FileSize Called")
            var filePath = jsonDict[StringConstants.Generic.filePath] as? String ?? StringConstants.Generic.emptyString
            filePath = filePath.trimmingCharacters(in: .whitespaces)
            if !filePath.isEmpty && FileManager.default.fileExists(atPath: filePath) {
                getFileSize(filePath)
            } else {
                fileSizeCallback(resultKeys: [StringConstants.Generic.errorCode],
                                 resultValues: [StringConstants.FileOperation.invalidSrc],
                                 status: false)
            }
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "FileSize Done")
        viewController = nil
        delegate.donePlugin(self)
    }
    // MARK: File Size Methods and Callback
    func getFileSize(_ filePath: String) {
        do {
            let fileSize = try FileManager.default.attributesOfItem(atPath: filePath)[FileAttributeKey.size]
                as? UInt64 ?? 0
            let fileSizeKB = fileSize / 1024
            fileSizeCallback(resultKeys: [StringConstants.Generic.fileSize],
                             resultValues: [fileSizeKB], status: true)
            APZLogger.log(logLvl: "I", message: "FileSize Success")
        } catch let error {
            fileSizeCallback(resultKeys: [StringConstants.Generic.errorCode],
                             resultValues: [StringConstants.FileOperation.fileSizeError],
                             status: false)
            APZLogger.log(logLvl: "E", message: "FileSize--\(error)")
        }
    }
    func fileSizeCallback(resultKeys: [String], resultValues: [Any], status: Bool) {
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
