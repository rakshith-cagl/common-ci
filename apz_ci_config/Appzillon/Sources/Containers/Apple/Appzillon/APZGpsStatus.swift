// swiftlint:disable all
//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZGpsStatus: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable : Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable : Any]) {
        APZLogger.log(logLvl: "I", message: "APZGpsStatus--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        if let action = jsonDict[StringConstants.Generic.action] as? String {
            if action == StringConstants.GpsStatus.checkLocationAvailabitlty {
                checkDeviceLocationAvailability()
            } else if action == StringConstants.GpsStatus.redirectToSettings {
                redirectUserToDeviceSettings()
            }
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZGpsStatus--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Utility Methods
    private func checkDeviceLocationAvailability() {
        if CLLocationManager.locationServicesEnabled() {
            callBack(resultKeys: [StringConstants.Generic.text],
                     resultValues: [StringConstants.GpsStatus.gpsEnabled],
                     status: true)
            cleanPlugin()
        } else {
            callBack(resultKeys: [StringConstants.Generic.errorMessage, StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.GpsStatus.gpsDisabled,
                                    StringConstants.UserLocation.gpsServicesOffError],
                     status: false)
            cleanPlugin()
        }
    }
    
    private func redirectUserToDeviceSettings() {
        if CLLocationManager.locationServicesEnabled() {
            callBack(resultKeys: [StringConstants.Generic.text],
                     resultValues: ["GPS Already Enabled"],
                     status: true)
            cleanPlugin()
        } else {
            redirectToSetting()
        }
    }
    fileprivate func redirectToSetting() {
        if let url = URL(string: UIApplication.openSettingsURLString) {
            UIApplication.shared.open(url, options: [:], completionHandler: { (success) in
                if success {
                    self.callBack(resultKeys: [StringConstants.Generic.text],
                                  resultValues: [StringConstants.GpsStatus.redirectSuccess],
                                  status: true)
                    self.cleanPlugin()
                } else {
                    self.callBack(resultKeys: [StringConstants.Generic.errorMessage],
                                  resultValues: [StringConstants.GpsStatus.redirectFailed],
                                  status: false)
                    self.cleanPlugin()
                }
            })
        }
    }
    fileprivate func callBack(resultKeys: [String], resultValues: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: self.webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
// swiftlint:enable all
