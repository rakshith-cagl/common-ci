//
//  APZLocale.swift
//  Appzillon
//
//  Created by Thanmai M S on 9/29/21.
//

import Foundation

 class APZLocale: APZPlugin {
    var webVW: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webVW = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webVW, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        if !Locale.current.identifier.isEmpty {
            let resultKeys = [StringConstants.Locale.localJson]
            let resultValues = [Locale.current.identifier]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: resultKeys,
                                                                        responseValues: resultValues)
            MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            cleanPlugin()
        } else {
            let resultKeys = [StringConstants.Generic.errorCode]
            let resultValues = [StringConstants.Locale.failedGetLocale]
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
    func cleanPlugin() {
        print("CleanPlugin for APZLocaleSwift")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
}
