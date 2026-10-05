//
//  APZDeepLinking.swift
//  Appzillon
//
//  Created by Bhavya V on 22/09/21.
//
import Foundation
import CoreAudioTypes

class APZDeepLinking: APZPlugin {
    var webVW: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = ""
    // MARK: Plugin Life Cycle
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]!) {
        self.webVW = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webVW, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? ""
            let packageName = jsonDict[StringConstants.DeepLink.packageName] as? String
            print("Plugin \(String(describing: pluginId)) Started")
            if packageName != nil {
                navigateToApp(jsonDict: jsonDict)
            } else {
                let resultKeys = [StringConstants.Generic.errorCode]
                let resultValues = [StringConstants.Generic.errorCode]
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
    }
    func cleanPlugin() {
        print("CleanPlugin for APZDeepLinkingSwift")
        self.delegate.donePlugin(self)
    }
    // MARK: Function navigateToApp
    func navigateToApp(jsonDict: [AnyHashable: Any]!) {
        let packageScheme = jsonDict[StringConstants.DeepLink.packageName] as? String
        let appStoreLink = jsonDict[StringConstants.DeepLink.appStoreLink] as? String
        if let packageScheme = packageScheme,
           let appUrl = URL(string: packageScheme + "://") {
            if UIApplication.shared.canOpenURL(appUrl) {
                UIApplication.shared.open(appUrl, options: [:], completionHandler: nil)
                let resultKeys = [StringConstants.DeepLink.packageScheme]
                let resultValues = [packageScheme]
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: true,
                                                                            keepAlive: false,
                                                                            responseKeys: resultKeys,
                                                                            responseValues: resultValues)
                MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
                cleanPlugin()
            } else if let appStoreLink = appStoreLink {
                if appStoreLink != "" {
                    let appStoreURL = URL(string: appStoreLink)
                    UIApplication.shared.open(appStoreURL!, options: [:], completionHandler: nil)
                    let resultKeys = [StringConstants.Generic.webViewUrl]
                    let resultValues = [appStoreLink]
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
                    let resultValues = [StringConstants.Generic.errorCode]
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
            } else {
                let resultKeys = [StringConstants.Generic.errorCode]
                let resultValues = [StringConstants.Generic.errorCode]
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
   }
}
