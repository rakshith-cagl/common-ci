// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZBrowser: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: wbView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            APZLogger.log(logLvl: "I", message: "APZBrowser--Execute")
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            let browserUrlString = jsonDict[StringConstants.Generic.url]
                as? String ?? StringConstants.Generic.emptyString
            openBrowser(browserUrlString)
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZBrowser--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Browser methods
    func openBrowser(_ browserUrlString: String) {
        let browserUrl = URL(string: browserUrlString)
        if let browserUrl = browserUrl, browserUrl.scheme != nil, browserUrl.host != nil {
            UIApplication.shared.open(browserUrl, options: [:]) { [ weak self] completed in
                if completed {
                    self?.handleBrowserCallBack(resultKeys: [StringConstants.Generic.message],
                                                resultValues: [StringConstants.Browser.browserSuccess],
                                                status: true)
                    APZLogger.log(logLvl: "I", message: "APZBrowser--URL Open Success")
                } else {
                    self?.handleBrowserCallBack(resultKeys: [StringConstants.Generic.errorCode],
                                                resultValues: [StringConstants.Browser.invalidUrl],
                                                status: false)
                    APZLogger.log(logLvl: "E", message: "APZBrowser--URL Open Failure")
                }
            }
        } else {
            handleBrowserCallBack(resultKeys: [StringConstants.Generic.errorCode],
                                  resultValues: [StringConstants.Browser.invalidUrl],
                                  status: false)
            APZLogger.log(logLvl: "E", message: "APZBrowser--URL Open Failure")
        }
    }
    private func handleBrowserCallBack(resultKeys: [String], resultValues: [Any], status: Bool) {
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
