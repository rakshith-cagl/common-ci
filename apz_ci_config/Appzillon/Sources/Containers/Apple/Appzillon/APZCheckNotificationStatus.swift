//
//  APZLocale.swift
//  Appzillon
//
//  Created by Thanmai M S on 04/01/2022.
//

import Foundation

class APZCheckNotificationStatus: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        UNUserNotificationCenter.current().getNotificationSettings { [self] (settings) in
                if settings.authorizationStatus == .authorized {
                    // if Notifications is allowed
                    self.callBack(resultValues: [StringConstants.Generic.yes], status: true)
                } else {
                    // if Notification is Either denied or notDetermined
                    self.callBack(resultValues: [StringConstants.Generic.no], status: false)
                }
        }
    }
    func cleanPlugin() {
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    
    //MARK: Callback Method
    fileprivate func callBack(resultValues: [Any], status: Bool) {
        let param = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId, status: status, keepAlive: false,
                                                                   responseKeys: ["notificationStatus"], responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod, parameter: param)
        cleanPlugin()
    }
}
