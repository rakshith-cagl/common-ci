//
//  APZDeleteSubApp.swift
//  Appzillon
//
//  Created by manjunath.ramesh on 03/11/21.
//

import Foundation

class APZDeleteSubApp: APZPlugin {
    var webVW: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webVW = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webVW, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "APZDeleteSubApp--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        let appName = jsonDict[StringConstants.Generic.appId] as? String ?? StringConstants.Generic.emptyString
        if !appName.isEmpty {
            deleteSubApp(appName: appName)
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "D", message: "APZDeleteSubApp--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Utility Methods
    private func deleteSubApp(appName: String) {
        let fileManager = FileManager()
        let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        var documentsDirectory = urls[0]
        documentsDirectory.appendPathComponent("\(StringConstants.DeleteSubApp.appPath)/\(appName)/")
        var isDir: ObjCBool = false
        let fileExists = fileManager.fileExists(atPath: documentsDirectory.path, isDirectory: &isDir)
        if fileExists {
            do {
                try fileManager.removeItem(at: documentsDirectory)
                sendSuccessCallback()
                APZLogger.log(logLvl: "I", message: "APZDeleteSubApp--deleted")
            } catch let error {
                sendFailureCallback()
                APZLogger.log(logLvl: "E", message: "APZDeleteSubApp--\(error.localizedDescription)")

            }
        } else {
            sendFailureCallback()
            APZLogger.log(logLvl: "E", message: "APZDeleteSubApp--filePath Does not Exist")
        }
    }
    private func sendSuccessCallback() {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: [],
                                                                    responseValues: [])
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
    private func sendFailureCallback() {
        let resultKeys = [StringConstants.Generic.errorCode]
        let resultValues = [StringConstants.DeleteSubApp.wipeOutFailure]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)

        cleanPlugin()
    }
}
