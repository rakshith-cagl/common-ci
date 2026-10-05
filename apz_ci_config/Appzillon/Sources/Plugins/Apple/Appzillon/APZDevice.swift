// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import CoreTelephony

class APZDevice: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var networkInfo: CTTelephonyNetworkInfo?
    // MARK: - Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: wbView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "I", message: "APZDevice--execute")
        if !jsonDict.isEmpty {
            self.pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            self.getDeviceParams()
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZDevice--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: - Device Details
    func getDeviceParams() {
        let device = UIDevice.current
        let screenBounds = UIScreen.main.bounds
        let screenScale = UIScreen.main.scale
        let screenSize = CGSize(width: screenBounds.size.width * screenScale,
                                height: screenBounds.size.height * screenScale)
        device.isBatteryMonitoringEnabled = true
        var batteryLevel = device.batteryLevel
        batteryLevel *= 100
        device.isBatteryMonitoringEnabled = false
        let connectionType = self.getConnectionType()
        let resultKeys = [StringConstants.DeviceDetails.osName,
                          StringConstants.Generic.deviceName,
                          StringConstants.DeviceDetails.osVersion,
                          StringConstants.DeviceDetails.deviceType,
                          StringConstants.DeviceDetails.screenResolution,
                          StringConstants.DeviceDetails.connectiontype,
                          StringConstants.DeviceDetails.batteryStatus]
        let result = [device.systemName,
                      device.name,
                      device.systemVersion,
                      device.model,
                      String(format: "%.0fx%.0f", screenSize.width,
                                           screenSize.height),
                      connectionType, String(format: "%.0f%%", batteryLevel)]
        APZLogger.log(logLvl: "I", message: "APZDevice--Suceess")
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: result)
        MiscellaneousMethod.shared.jsLayerCall(webView: self.webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        self.cleanPlugin()
    }
    fileprivate func getConnectionType() -> String {
        var connectionType:String = StringConstants.Generic.emptyString
        do{
            let reachability =  try APZReachability()
            switch reachability.connection {
            case.unavailable:
                connectionType = StringConstants.Connection.none
            case .wifi:
                connectionType = StringConstants.Connection.wifi
            case .cellular:
                connectionType = self.getConnectionTypeForWAN()
            }
        } catch let error {
            APZLogger.log(logLvl: "E", message: error.localizedDescription)
        }
        return connectionType
    }
    fileprivate  func getConnectionTypeForWAN() -> String {
        var connectionType: String = StringConstants.Generic.emptyString
        let netInfo = CTTelephonyNetworkInfo()
        let carrierType = netInfo.serviceCurrentRadioAccessTechnology
        guard let carrierTypeName = carrierType?.first?.value else {
            return StringConstants.Generic.none
        }
        if [CTRadioAccessTechnologyGPRS,
            CTRadioAccessTechnologyEdge,
            CTRadioAccessTechnologyCDMA1x].contains(carrierTypeName) {
            connectionType = StringConstants.Connection.type2g
        } else if [CTRadioAccessTechnologyWCDMA,
                   CTRadioAccessTechnologyHSDPA,
                   CTRadioAccessTechnologyHSUPA,
                   CTRadioAccessTechnologyCDMAEVDORev0,
                   CTRadioAccessTechnologyCDMAEVDORevA,
                   CTRadioAccessTechnologyCDMAEVDORevB,
                   CTRadioAccessTechnologyeHRPD].contains(carrierTypeName) {
            connectionType = StringConstants.Connection.type3g
        } else if [CTRadioAccessTechnologyLTE].contains(carrierTypeName) {
            connectionType = StringConstants.Connection.type4g
        } else if #available(iOS 14.1, *) {
            if [CTRadioAccessTechnologyNRNSA,
                CTRadioAccessTechnologyNR].contains(carrierTypeName) {
                connectionType = StringConstants.Connection.type5g
            }
        } else {
            connectionType = StringConstants.Connection.type4g
        }
        return connectionType
    }
}
