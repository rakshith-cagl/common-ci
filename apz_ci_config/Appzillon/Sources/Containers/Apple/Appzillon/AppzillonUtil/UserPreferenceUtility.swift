// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit
import WebKit

 class UserPreferenceUtility: NSObject {
    static let sharedInstance = UserPreferenceUtility()
    private override init() {
        //do nothing
    }
     func getUserPreference(_ jsonDict: [AnyHashable: Any], appString: String, webView: WKWebView) {
        let key = jsonDict[StringConstants.Generic.key] as? String ?? StringConstants.Generic.emptyString
        let documentDirectoryPath = FileManagerUtility.documentDirectory()
        let pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
         let plistPath = StringConstants.UserPreferences.plistPath
        let userPrefPath = documentDirectoryPath
             .appendingPathComponent("\(StringConstants.Generic.sandBoxPath)\(appString)\(plistPath)").path
        let userPrefDictionary = NSDictionary(contentsOfFile: userPrefPath) as? [AnyHashable: Any]
        let userSettings = userPrefDictionary?[StringConstants.UserPreferences.userPref] as? [AnyHashable: Any]
        if userSettings != nil {
            let valueForKey = userSettings?[key]
            if let valueForKey = valueForKey {
                getUserPreferenceCallback(webView: webView, status: true,
                                          resultvalue: [valueForKey], pluginId: pluginId)
            } else {
                getUserPreferenceCallback(webView: webView, status: true,
                                          resultvalue: [StringConstants.UserPreferences.null], pluginId: pluginId)
            }
        } else {
            getUserPreferenceCallback(webView: webView, status: false,
                                      resultvalue: [StringConstants.UserPreferences.null], pluginId: pluginId)
        }
    }
     func setUserPreference(_ jsonDict: [AnyHashable: Any], appString: String, webView: WKWebView) {
        let key = jsonDict[StringConstants.Generic.key] as? String ?? StringConstants.Generic.emptyString
        // let value = jsonDict[StringConstants.Generic.value] as? String ?? StringConstants.Generic.emptyString
        let documentDirectoryPath = FileManagerUtility.documentDirectory()
        let pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
         let plistPath = StringConstants.UserPreferences.plistPath
        let userPrefPath = documentDirectoryPath
             .appendingPathComponent("\(StringConstants.Generic.sandBoxPath)\(appString)\(plistPath)").path
        var userPrefDictionary = NSDictionary(contentsOfFile: userPrefPath) as? [AnyHashable: Any]
        if userPrefDictionary?.count == 0 {
            let userprefsSubDict: [AnyHashable: Any] = [:]
            userPrefDictionary?[StringConstants.UserPreferences.userPref] = userprefsSubDict
        }
        var userSettings = userPrefDictionary?[StringConstants.UserPreferences.userPref] as? [AnyHashable: Any]
        if let value = jsonDict[StringConstants.Generic.value] {
             userSettings?[key] = value
        }
        userPrefDictionary?[StringConstants.UserPreferences.userPref] = userSettings
        if let userPrefDictionary = userPrefDictionary {
            let success = (userPrefDictionary as NSDictionary).write(toFile: userPrefPath, atomically: true)
            if success {
                setUserPreferenceCallback(webView: webView, status: true,
                                          resultvalue: [StringConstants.Generic.success],
                                          pluginId: pluginId)
            } else {
                setUserPreferenceCallback(webView: webView, status: false,
                                          resultvalue: [StringConstants.Generic.failed],
                                          pluginId: pluginId)
            }
        }
    }
    // MARK: - Callback Methods
     private func getUserPreferenceCallback(webView: WKWebView, status: Bool, resultvalue: [Any], pluginId: String) {
         let resultkeys = [StringConstants.Generic.value]
         let resultMsg = resultvalue
         let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                     status: status,
                                                                     keepAlive: false,
                                                                     responseKeys: resultkeys,
                                                                     responseValues: resultMsg)
         MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                parameter: params)
     }
     private func setUserPreferenceCallback(webView: WKWebView, status: Bool, resultvalue: [Any], pluginId: String) {
         let resultkeys = [StringConstants.Generic.message]
         let resultMsg = resultvalue
         let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                     status: status,
                                                                     keepAlive: false,
                                                                     responseKeys: resultkeys,
                                                                     responseValues: resultMsg)
         MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                parameter: params)
     }
}
