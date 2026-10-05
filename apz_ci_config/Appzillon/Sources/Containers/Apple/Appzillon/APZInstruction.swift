//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation


class APZInstruction: APZPlugin {
    var webVW: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var path = StringConstants.Generic.emptyString
    var settingsDictionary: NSMutableDictionary = NSMutableDictionary(dictionary: [:])
    
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webVW = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webVW, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "APZInstruction--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        
        if let appId = jsonDict["appId"] as? String,
           let containerPropsPath = MiscellaneousMethod.shared.getAppBundle().path(forResource: "containerprops", ofType: "plist"),
           let containerPropsDictionary = NSDictionary(contentsOfFile: containerPropsPath),
           let mainAppId = containerPropsDictionary["MAINAPPID"] as? String {
            if mainAppId == appId {
                getMainAppsInstruction(mainAppId: mainAppId)
            } else {
                instructionForApp(appId: appId)
            }
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "D", message: "APZInstruction--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    
    // MARK: Utility Methods
    func getMainAppsInstruction(mainAppId: String) {
        let appPropertiesFilePath = APZNetworkUtility.shared.getAppPropertiesDictionaryPath(appID: mainAppId)
        
        if let appProperties = NSDictionary(contentsOfFile: appPropertiesFilePath),
           let masterDetailResponse = appProperties["masterDetailsResponse"] as? [String: Any] {
            let decryptedMasterDetailsResponse = CryptoSwiftManager.shared.decryptPlistData(dictionary: masterDetailResponse as [AnyHashable : Any]) as? NSDictionary
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: ["appDetails"],
                                                                        responseValues: [decryptedMasterDetailsResponse])
            MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
    }
    
    func instructionForApp(appId: String) {
        let deviceID = UserDefaults.standard.object(forKey: "uniqueID") ?? StringConstants.Generic.emptyString
        self.path = APZNetworkUtility.shared.getAppPropertiesDictionaryPath(appID: appId)
        let ipAddress = self.viewController?.ipAddress
        let ipUrl = self.viewController?.apzServerURL
        self.settingsDictionary = NSMutableDictionary(contentsOfFile: self.path) ?? NSMutableDictionary(dictionary: [:])
        
        var appzillonHeader: [AnyHashable : Any] = [:]
        appzillonHeader["preLogin"] = "true"
        appzillonHeader["appId"] = appId
        appzillonHeader["deviceId"] = deviceID
        appzillonHeader["sessionId"] = "asa"
        appzillonHeader["source"] = "APPZILLON"
        appzillonHeader["userId"] = "IOS"
        appzillonHeader["status"] = NSNumber(booleanLiteral: true)
        appzillonHeader["requestKey"] = "sad"
        appzillonHeader["origination"] = ipAddress
        appzillonHeader["interfaceId"] = "appzillonGetAppMasterDetails"
        appzillonHeader["screenId"] = "login"
        
        var appzillonAppMasterRequest: [AnyHashable : Any] = [:]
        appzillonAppMasterRequest["appId"] = appId
        appzillonAppMasterRequest["deviceId"] = deviceID
        appzillonAppMasterRequest["os"] = "IOS"
        
        var appzillonBody: [AnyHashable : Any] = [:]
        appzillonBody["appzillonAppMasterRequest"] = NSMutableDictionary(dictionary: appzillonAppMasterRequest)
        
        var appzillonRequest: [AnyHashable : Any] = [:]
        appzillonRequest["appzillonHeader"] = NSMutableDictionary(dictionary: appzillonHeader)
        appzillonRequest["appzillonBody"] = NSMutableDictionary(dictionary: appzillonBody)
        
        CallServer.callServer(withRequest: NSMutableDictionary(dictionary: appzillonRequest),
                              ipUrl,
                              appId,
                              self.viewController) {[weak self] status, responseDictionary in
            if status {
                self?.afRequestSuccessfulForContainerUpdates(jsonDict: responseDictionary ?? [:])
            } else {
                self?.afRequestFailureForContainerUpdates(jsonDict: responseDictionary ?? [:])
            }
        }
    }
    
    fileprivate func afRequestSuccessfulForContainerUpdates(jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "I", message: "APZInstruction--response recieved")
        if let jsonBody = jsonDict["appzillonBody"] as? [AnyHashable: Any],
           let jsonHeader = jsonDict["appzillonHeader"] as? [AnyHashable: Any],
           let mainApp = self.settingsDictionary["appId"] as? String,
           let status = jsonHeader["status"] as? NSNumber {
            let mainAppId = CryptoSwiftManager.shared.decryptSingleValue(value: mainApp)
            if status.boolValue {
                var mainAppInfo = jsonBody[mainApp] as? [AnyHashable: Any] ?? [:]
                let appVersion = mainAppInfo["appVersion"] as? String ?? StringConstants.Generic.emptyString
                let storedAppVersion = CryptoSwiftManager.shared.decryptSingleValue(value: self.settingsDictionary["appVersion"] as? String ?? "")
                
                let isUpgradeRequired = appVersion != storedAppVersion ? "Y" : "N"
                mainAppInfo["upgradeRequired"] = isUpgradeRequired
                self.settingsDictionary["upgradeRequired"] = CryptoSwiftManager.shared.encryptSingleValue(value: isUpgradeRequired)
                if let url = URL(string: self.path) {
                    self.settingsDictionary.write(to: url, atomically: true)
                    self.settingsDictionary["masterDetailsResponse"] = CryptoSwiftManager.shared.encryptPlistData(dictionary: mainAppInfo)
                    self.settingsDictionary.write(to: url, atomically: true)
                }
                sendSuccessCallback()
                APZLogger.log(logLvl: "I", message: "APZInstruction--Success")
            } else {
                sendFailureCallback()
            }
        }
    }
    
    fileprivate func afRequestFailureForContainerUpdates(jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "E", message: "APZInstruction--Instruction Failure")
        sendFailureCallback()
    }
    fileprivate func sendSuccessCallback() {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: [],
                                                                    responseValues: [])
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
    fileprivate func sendFailureCallback() {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: [StringConstants.Generic.errorCode],
                                                                    responseValues: ["APZ_RS_002"])
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
