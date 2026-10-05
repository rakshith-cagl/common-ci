//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import CoreNFC

class APZCheckNFCSupport: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    
    // MARK: - Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable : Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: wbView, jsonDict)
    }
    
    override func execute(_ jsonDict: [AnyHashable : Any]) {
        APZLogger.log(logLvl: "D", message: "APZCheckNFCSupport--execute")
        if !jsonDict.isEmpty {
            self.pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            if NFCNDEFReaderSession.readingAvailable {
                callBack(resultKeys: ["NFCSupported"], resultValues: [StringConstants.Generic.yes], status: true)
            } else {
                callBack(resultKeys: ["NFCSupported"], resultValues: [StringConstants.Generic.no], status: false)
            }
        }else{
            cleanPlugin()
        }
    }
    
    func cleanPlugin() {
        APZLogger.log(logLvl: "D", message: "APZCheckNFCSupport--Done")
        self.viewController = nil;
        self.delegate.donePlugin(self)
    }
    
    //MARK: Callback Method
    fileprivate func callBack(resultKeys: [String], resultValues: [Any], status: Bool) {
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
