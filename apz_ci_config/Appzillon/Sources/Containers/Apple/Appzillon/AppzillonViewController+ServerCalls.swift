// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

@objc extension AppzillonViewController {
    func callDeviceRegistration(_ location: CLLocation?) {
        locationForNewRequest = location
        let notificationStatus = APZAppDelegateUtility.shared.appDelegate?
            .containerPropsDictionary["NOTIFICATION"] as? String
        if notificationStatus == StringConstants.Generic.yes {
            apnsRegistration()
        } else {
            appzillonRequestForFirstLaunch()
        }
    }
    @objc func createAppzillonRequest() {
        self.runTimeDict = NSMutableDictionary(contentsOfFile: self.runTimeDictPath)
        let registrationDone = self.runTimeDict["deviceRegistrationDone"] as? String ?? StringConstants.Generic.emptyString
        if registrationDone != "YES" {
            let trackLocation = self.appPropertyDictionary["trackLocation"] as? String ?? StringConstants.Generic.emptyString
            if trackLocation == StringConstants.Generic.yes {
                self.locationPermission()
            } else {
                self.callDeviceRegistration(nil)
            }
        } else {
            self.appzillonRequestForSecondLaunch()
        }
    }
    func startNetworkReachabilityListener() {
        let pluginId = self.nwListenerDict[StringConstants.Generic.pluginId]
        APZNetworkMonitor.shared.startMonitoring(pluginid: pluginId as! String, wbView: webView)
    }
    func stopNetworkReachabilityListener() {
        APZNetworkMonitor.shared.stopMonitoring()
    }
    // MARK: Server Calls
    func getServerNonce() {
        var appzillonHeader: [AnyHashable : Any] = [:]
        appzillonHeader["appId"] = appString
        appzillonHeader["sessionId"] = "null"
        appzillonHeader["deviceId"] = uniqueID
        appzillonHeader["requestId"] = "CSNONCE"
        appzillonHeader["async"] = NSNumber(booleanLiteral: false)
        appzillonHeader["userId"] = "null"
        appzillonHeader["screenId"] = "Login"
        appzillonHeader["status"] = NSNumber(booleanLiteral: true)
        appzillonHeader["source"] = "APPZILLON"
        appzillonHeader["clientNonce"] = NSNumber(booleanLiteral: false)
        appzillonHeader["interfaceId"] = "appzillonGetAppSecTokens"
        appzillonHeader["os"] = "IOS"
        appzillonHeader["origination"] = ipAddress
        appzillonHeader["requestKey"] = ""
        
        var appzillonGetAppSecTokensRequest: [AnyHashable : Any] = [:]
        appzillonGetAppSecTokensRequest["appId"] = appString
        appzillonGetAppSecTokensRequest["deviceId"] = uniqueID
       
        var appzillonBody: [AnyHashable : Any] = [:]
        appzillonBody["appzillonGetAppSecTokensRequest"] = NSMutableDictionary(dictionary: appzillonGetAppSecTokensRequest)
        
        var getServerNonceRequest: [AnyHashable : Any] = [:]
        getServerNonceRequest["appzillonHeader"] = NSMutableDictionary(dictionary: appzillonHeader)
        getServerNonceRequest["appzillonBody"] = NSMutableDictionary(dictionary: appzillonBody)
        
        let ipUrl = self.apzServerURL
        CallServer.callServer(withRequest: NSMutableDictionary(dictionary: getServerNonceRequest),
                              ipUrl,
                              appString,
                              self) {[weak self] status, responseDictionary in
            if status {
                self?.getServerNonceRequestSuccess(jsonDict: responseDictionary ?? [:])
            } else {
                self?.getServerNonceRequestFailure(jsonDict: responseDictionary ?? [:])
            }
        }
    }
    
    fileprivate func getServerNonceRequestSuccess(jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "I", message: "getServerNonceRequestSuccess")
        
        if let jsonBody = jsonDict["appzillonBody"] as? [AnyHashable: Any],
           let appzillonGetAppSecTokensResponse = jsonBody["appzillonGetAppSecTokensResponse"] as? [AnyHashable: Any],
           let status = appzillonGetAppSecTokensResponse["status"] {
            print(status)
            self.isServerNonceReceived = StringConstants.Generic.yes
            //Hitting infra method to confirm that we received the serverNonce
            DispatchQueue.main.async {
                self.webView.evaluateJavaScript(String(format: "apz.server.setAppSecToken('%@');",
                                                  self.isServerNonceReceived),
                                           completionHandler: nil)
            }
            self.apzServerNonce = appzillonGetAppSecTokensResponse["serverNonce"] as? String ?? ""
            self.apzSafeToken = appzillonGetAppSecTokensResponse["safeToken"] as? String ?? ""
            self.apzSessionToken = appzillonGetAppSecTokensResponse["sessionToken"] as? String ?? ""
            if let serverToken = self.appPropertyDictionary["serverToken"] as? String {
                self.apzServerToken = CryptoSwiftManager.shared.getAESDecryptedStringForServerCalls(dataToDecrypt: serverToken,
                                                                                                    keyStr: SERVERTOKENDECRYPTKEY)
            }
            if let serverNonceJSON = self.serverNonceJSON {
                let pluginId = serverNonceJSON["id"] as? String ?? StringConstants.Generic.emptyString
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: true,
                                                                            keepAlive: false,
                                                                            responseKeys: [],
                                                                            responseValues: [])
                MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
            }
        }
    }
    
    fileprivate func getServerNonceRequestFailure(jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "E", message: "getServerNonceRequestFailure")
        self.isServerNonceReceived = StringConstants.Generic.no
        
        if let appDelegate = APZAppDelegateUtility.shared.appDelegate{
            let offlineSupport = appDelegate.containerPropsDictionary["OFFLINESUPPORT"] as? String ?? "N"
            let appOfflineSupport = appDelegate.containerPropsDictionary["APPOFFLINESUPPORT"] as? String ?? "N"
            if ((offlineSupport.caseInsensitiveCompare(StringConstants.Generic.yes) == .orderedSame) || (appOfflineSupport.caseInsensitiveCompare(StringConstants.Generic.yes) == .orderedSame)) {
                self.injectHtmlInWebview()
            }
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
            self.getServerNonce()
        }
    }
    
    // MARK: Devive registartion
    func multifactorReq(version: String, location: CLLocation?) {
        var latitude = StringConstants.Generic.emptyString
        var longitude = StringConstants.Generic.emptyString
        
        if let loc = location {
            latitude = String(format: "%f", loc.coordinate.latitude)
            longitude = String(format: "%f", loc.coordinate.longitude)
        }
        let deviceModel = UIDevice.current.model
        let deviceName = UIDevice.current.name
        let resolutionSize = UIScreen.main.bounds.size
        let systemResolution = String(format: "%ix%i", Int(ceil(resolutionSize.width)), Int(ceil(resolutionSize.height)))
        
        var appzillonHeader: [AnyHashable : Any] = [:]
        appzillonHeader["appId"] = appString
        appzillonHeader["preLogin"] = "true"
        appzillonHeader["screenId"] = "lauchApp"
        appzillonHeader["requestKey"] = "000NEW"
        appzillonHeader["interfaceId"] = "appzillonDeviceRegistration"
        appzillonHeader["status"] = NSNumber(booleanLiteral: true)
        appzillonHeader["sessionId"] = "null"
        appzillonHeader["source"] = "APPZILLON"
        appzillonHeader["deviceId"] = uniqueID
        appzillonHeader["origination"] = ipAddress
        appzillonHeader["latitude"] = latitude
        appzillonHeader["longitude"] = longitude
        appzillonHeader["userId"] = "IOS"
        
        
        var deviceRegisterRequestInBody: [AnyHashable : Any] = [:]
        deviceRegisterRequestInBody["os"] = "IOS"
        deviceRegisterRequestInBody["appId"] = appString
        deviceRegisterRequestInBody["osVersion"] = version
        deviceRegisterRequestInBody["deviceId"] = uniqueID
        deviceRegisterRequestInBody["mobile1"] = "null"
        deviceRegisterRequestInBody["mobile2"] = "null"
        deviceRegisterRequestInBody["model"] = deviceModel
        deviceRegisterRequestInBody["screenResolution"] = systemResolution
        deviceRegisterRequestInBody["deviceName"] = deviceName
        deviceRegisterRequestInBody["latitude"] = latitude
        deviceRegisterRequestInBody["longitude"] = longitude
        deviceRegisterRequestInBody["appVersion"] = UserDefaults.standard.value(forKey: "storedBundleShortVersion") as? String
                                                    ?? StringConstants.Generic.emptyString
        deviceRegisterRequestInBody["make"] = "Apple"
        
        var appzillonBody: [AnyHashable : Any] = [:]
        appzillonBody["deviceRegisterRequest"] = NSMutableDictionary(dictionary: deviceRegisterRequestInBody)
        
        var deviceRegisterRequest: [AnyHashable : Any] = [:]
        deviceRegisterRequest["appzillonHeader"] = NSMutableDictionary(dictionary: appzillonHeader)
        deviceRegisterRequest["appzillonBody"] = NSMutableDictionary(dictionary: appzillonBody)
        let ipUrl = self.apzServerURL
        
        CallServer.callServer(withRequest: NSMutableDictionary(dictionary: deviceRegisterRequest),
                              ipUrl,
                              appString,
                              self) {[weak self] status, responseDictionary in
            if status {
                self?.afRequestSuccessfulForDeviceReg(jsonDict: responseDictionary ?? [:])
            } else {
                self?.afRequestFailureForDeviceReg(jsonDict: responseDictionary ?? [:])
            }
        }
    }
    
    fileprivate func afRequestSuccessfulForDeviceReg(jsonDict: [AnyHashable: Any]) {
        if let jsonHeader = jsonDict["appzillonHeader"] as? [AnyHashable: Any],
           let status = jsonHeader["status"] as? NSNumber, status.boolValue {
            self.runTimeDict = NSMutableDictionary(contentsOfFile: self.runTimeDictPath)
            self.runTimeDict["deviceRegistrationDone"] = "YES"
            self.runTimeDict["registeredVersion"] = self.currentVersion
            if let url = URL(string: self.runTimeDictPath) {
                self.runTimeDict.write(to: url, atomically: true)
                APZLogger.log(logLvl: "I", message: "Device Registration Done")
            }
        } else {
            APZLogger.log(logLvl: "E", message: "Device Registration failure")
        }
    }

    fileprivate func afRequestFailureForDeviceReg(jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "E", message: "Device Registration failure")
    }
    
    // MARK: First Launch Methods
    @objc func appzillonRequestForFirstLaunch() {
        var appzillonHeader: [AnyHashable: Any] = [:]
        appzillonHeader["appId"] = appString
        appzillonHeader["sessionId"] = ""
        appzillonHeader["deviceId"] = uniqueID
        appzillonHeader["userId"] = "IOS"
        appzillonHeader["screenId"] = "Login"
        appzillonHeader["status"] = NSNumber(booleanLiteral: true)
        appzillonHeader["source"] = "APPZILLON"
        appzillonHeader["clientNonce"] = NSNumber(booleanLiteral: false)
        appzillonHeader["interfaceId"] = "appzillonOnAppLaunch"
        appzillonHeader["os"] = "IOS"
        appzillonHeader["requestKey"] = ""
        appzillonHeader["origination"] = ipAddress
        appzillonHeader["serverNonce"] = ""
        appzillonHeader["sessionToken"] = ""
        appzillonHeader["preLogin"] = "true"
        appzillonHeader["appLaunch"] = "FIRST"
        

        var appzillonBody: [AnyHashable: Any] = [:]
        //AppSecToken request
        var appzillonGetAppSecTokensRequest: [AnyHashable : Any] = [:]
        appzillonGetAppSecTokensRequest["appId"] = appString
        appzillonGetAppSecTokensRequest["deviceId"] = uniqueID
        appzillonBody["appzillonGetAppSecTokensRequest"] = NSMutableDictionary(dictionary: appzillonGetAppSecTokensRequest)
        
        //DeviceRegistration request
        self.currentVersion = UIDevice.current.systemVersion
        let deviceModel = UIDevice.current.model
        let deviceName = UIDevice.current.name
        let resolutionSize = UIScreen.main.bounds.size
        let systemResolution = String(format: "%ix%i", Int(ceil(resolutionSize.width)), Int(ceil(resolutionSize.height)))
        
        var latitude = StringConstants.Generic.emptyString
        var longitude = StringConstants.Generic.emptyString
        
        if let loc = self.locationForNewRequest {
            latitude = String(format: "%f", loc.coordinate.latitude)
            longitude = String(format: "%f", loc.coordinate.longitude)
        }
        var deviceRegisterRequest: [AnyHashable: Any] = [:]
        deviceRegisterRequest["os"] = "IOS"
        deviceRegisterRequest["appId"] = appString
        deviceRegisterRequest["osVersion"] = self.currentVersion
        deviceRegisterRequest["deviceId"] = uniqueID
        deviceRegisterRequest["mobile1"] = "null"
        deviceRegisterRequest["mobile2"] = "null"
        deviceRegisterRequest["model"] = deviceModel
        deviceRegisterRequest["screenResolution"] = systemResolution
        deviceRegisterRequest["deviceName"] = deviceName
        deviceRegisterRequest["latitude"] = latitude
        deviceRegisterRequest["longitude"] = longitude
        deviceRegisterRequest["appVersion"] = UserDefaults.standard.value(forKey: "storedBundleShortVersion") as? String
                                                    ?? StringConstants.Generic.emptyString
        deviceRegisterRequest["make"] = "Apple"
        appzillonBody["deviceRegisterRequest"] = NSMutableDictionary(dictionary: deviceRegisterRequest)
        
        //AppMasterDetails request
        var appzillonAppMasterRequest: [AnyHashable : Any] = [:]
        appzillonAppMasterRequest["appId"] = appString
        appzillonAppMasterRequest["os"] = "IOS"
        appzillonAppMasterRequest["deviceId"] = uniqueID
        appzillonAppMasterRequest["appVersion"] = UserDefaults.standard.value(forKey: "storedBundleShortVersion") as? String
                                                    ?? StringConstants.Generic.emptyString
        appzillonAppMasterRequest["updateAppVersion"] = UserDefaults.standard.value(forKey: "updateAppVersion") as? String
                                                    ?? StringConstants.Generic.emptyString
        appzillonBody["appzillonAppMasterRequest"] = NSMutableDictionary(dictionary: appzillonAppMasterRequest)
        
        
        //OPTIONAL NotificationRegistration request
        if let regId = APZAppDelegateUtility.shared.appDelegate?.regID, !regId.isEmpty {
            appzillonHeader.removeValue(forKey: "appLaunch")
            appzillonHeader["appLaunch"] = "NOTIFY"
            var notificationRegRequest: [AnyHashable: Any] = [:]
            notificationRegRequest["deviceId"] = uniqueID
            notificationRegRequest["source"] = "APPZILLON"
            notificationRegRequest["origination"] = ipAddress
            notificationRegRequest["osId"] = "IOS"
            notificationRegRequest["regId"] = regId
            notificationRegRequest["deviceName"] = deviceName
            notificationRegRequest["osVersion"] = self.currentVersion
            notificationRegRequest["appId"] = appString
            appzillonBody["appzillonNotificationRegistrationRequest"] = NSMutableDictionary(dictionary: notificationRegRequest)
        }
        
        var appzillonOnAppLaunchRequest:[AnyHashable: Any] = [:]
        appzillonOnAppLaunchRequest["appzillonHeader"] = NSMutableDictionary(dictionary: appzillonHeader)
        appzillonOnAppLaunchRequest["appzillonBody"] = NSMutableDictionary(dictionary: appzillonBody)
        let ipUrl = self.apzServerURL
        
        CallServer.callServer(withRequest: NSMutableDictionary(dictionary: appzillonOnAppLaunchRequest),
                              ipUrl,
                              appString,
                              self) {[weak self] status, responseDictionary in
            if status {
                self?.appzillonFirstLaunchRequestSuccess(successResult: responseDictionary ?? [:])
            } else {
                self?.appzillonFirstLaunchRequestFailure(failureResult: responseDictionary ?? [:])
            }
        }
    }

    @objc func appzillonFirstLaunchRequestSuccess(successResult: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "appzillonRequestForFirstLaunchSuccess")
        if let jsonHeader = successResult["appzillonHeader"] as? [AnyHashable: Any],
           let status = jsonHeader["status"] as? NSNumber, status.boolValue {
            let appzillonBody = successResult["appzillonBody"] as? [AnyHashable: Any] ?? [:]
            
            //Handle AppSecToken
            let appzillonGetAppSecTokensResponse = appzillonBody["appzillonGetAppSecTokensResponse"] as? [AnyHashable: Any] ?? [:]
            if let status = appzillonGetAppSecTokensResponse["status"] as? String, status == "success" {
                self.isServerNonceReceived = StringConstants.Generic.yes
                //Hitting infra method to confirm that we received the serverNonce
                DispatchQueue.main.async {
                    self.webView.evaluateJavaScript(String(format: "apz.server.setAppSecToken('%@');", self.isServerNonceReceived))
                }
                apzServerNonce = appzillonGetAppSecTokensResponse["serverNonce"] as? String ?? ""
                apzSafeToken = appzillonGetAppSecTokensResponse["safeToken"] as? String ?? ""
                apzSessionToken = appzillonGetAppSecTokensResponse["sessionToken"] as? String ?? ""
                if let encServerToken = self.appPropertyDictionary["serverToken"] {
                    apzServerToken = CryptoSwiftManager.shared.getAESDecryptedStringForServerCalls(dataToDecrypt: encServerToken,
                                                                                                   keyStr: SERVERTOKENDECRYPTKEY)
                }
            } else {
                APZLogger.log(logLvl: "E", message: "appzillonGetAppSecTokensResponse Failure")
            }
            
            
            //Handle DeviceRegistration
            let deviceRegistrationResponse = appzillonBody["deviceRegisterResponse"] as? [AnyHashable: Any] ?? [:]
            if let status = deviceRegistrationResponse["status"] as? NSNumber, status.boolValue {
                self.runTimeDict = NSMutableDictionary(contentsOfFile: self.runTimeDictPath)
                self.runTimeDict["deviceRegistrationDone"] = "YES"
                self.runTimeDict["registeredVersion"] = self.currentVersion
                self.runTimeDict.write(toFile: self.runTimeDictPath, atomically: true)
            } else {
                APZLogger.log(logLvl: "E", message: "deviceRegistrationResponse failure")
            }
            
            
            //Handle AppMasterDetails
            if let appMasterDetailsResponse = appzillonBody["appzillonAppMasterResponse"] as? [AnyHashable: Any] {
                var mainAppInfo = appMasterDetailsResponse["appInstruction"] as? [AnyHashable:Any] ?? [:]
                if let wipeOutFlag = mainAppInfo["wipeout"] as? String, wipeOutFlag == StringConstants.Generic.yes {
                    AppzillonMainUtility.shared.emptySandbox(webView: webView)
                } else {
                    let mainAppId = APZAppDelegateUtility.shared.appDelegate?.containerPropsDictionary["MAINAPPID"] as? String ?? ""
                    let mainAppPropertyPath = APZNetworkUtility.shared.getAppPropertiesDictionaryPath(appID: mainAppId)
                    let mainAppDictionary = NSMutableDictionary(contentsOfFile: mainAppPropertyPath) ?? NSMutableDictionary(dictionary: [:])
                    
                    if let otaRequired = APZAppDelegateUtility.shared.appDelegate?.containerPropsDictionary["OTAREQUIRED"] as? String, otaRequired == StringConstants.Generic.yes {
                        let encStoredAppVersion = mainAppDictionary["appVersion"] as? String ?? ""
                        let appVersion = mainAppInfo["appVersion"] as? String ?? StringConstants.Generic.emptyString
                        let storedAppVersion = CryptoSwiftManager.shared.decryptSingleValue(value: encStoredAppVersion)
                        
                        let isUpgradeRequired = appVersion != storedAppVersion ? "Y" : "N"
                        mainAppInfo["upgradeRequired"] = isUpgradeRequired
                        mainAppDictionary["upgradeRequired"] = CryptoSwiftManager.shared.encryptSingleValue(value: isUpgradeRequired)
                        mainAppDictionary.write(toFile: mainAppPropertyPath, atomically: true)
                    } else {
                        mainAppDictionary["upgradeRequired"] = CryptoSwiftManager.shared.encryptSingleValue(value: "N")
                        mainAppInfo["upgradeRequired"] = "N"
                        mainAppDictionary.write(toFile: mainAppPropertyPath, atomically: true)
                    }
                    
                    mainAppDictionary["masterDetailsResponse"] = CryptoSwiftManager.shared.encryptPlistData(dictionary: mainAppInfo)
                    mainAppDictionary["updateAction"] = CryptoSwiftManager.shared.encryptSingleValue(value: mainAppInfo["updateAction"] as? String ?? "")
                    mainAppDictionary.write(toFile: mainAppPropertyPath, atomically: true)
                    
                    if let dictData = try? JSONSerialization.data(withJSONObject: mainAppInfo, options: []) {
                        let jsonStr = String(data: dictData, encoding: .utf8) ?? StringConstants.Generic.emptyString
                        DispatchQueue.main.async {
                            self.webView.evaluateJavaScript(String(format: "appzillon.util.instructions(%@);", jsonStr))
                        }
                    }
                }
            } else {
                APZLogger.log(logLvl: "E", message: "appzillonAppMasterResponse failure")
            }

            //Handle NotificationRegistration
            let notificationRegistrationResponse = appzillonBody["appzillonNotificationRegistrationResponse"] as? [AnyHashable: Any] ?? [:]
            if let status = notificationRegistrationResponse["status"] as? String, status == "success" {
                self.runTimeDict = NSMutableDictionary(contentsOfFile: self.runTimeDictPath)
                self.runTimeDict["serverRegistrationDone"] = "YES"
                self.runTimeDict.write(toFile: self.runTimeDictPath, atomically: true)
            } else {
                APZLogger.log(logLvl: "E", message: "Registration Failed for Push Notification")
            }
            AppzillonMainUtility.shared.setAppToken()
        } else {
            APZLogger.log(logLvl: "E", message: "appzillonRequestForFirstLaunch - Failed")
        }
    }
    
    @objc func appzillonFirstLaunchRequestFailure(failureResult: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "E", message: "appzillonRequestForFirstLaunchFailure")
        self.isServerNonceReceived = StringConstants.Generic.no
        
        if let appDelegate = APZAppDelegateUtility.shared.appDelegate{
            let offlineSupport = appDelegate.containerPropsDictionary["OFFLINESUPPORT"] as? String ?? "N"
            let appOfflineSupport = appDelegate.containerPropsDictionary["APPOFFLINESUPPORT"] as? String ?? "N"
            if ((offlineSupport.caseInsensitiveCompare(StringConstants.Generic.yes) == .orderedSame) || (appOfflineSupport.caseInsensitiveCompare(StringConstants.Generic.yes) == .orderedSame)) {
                self.injectHtmlInWebview()
            }
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
            self.appzillonRequestForFirstLaunch()
        }
    }
    
    // MARK: Appzillon Second Launch
    @objc func appzillonRequestForSecondLaunch() {
        var appzillonHeader: [AnyHashable: Any] = [:]
        appzillonHeader["appId"] = appString
        appzillonHeader["sessionId"] = ""
        appzillonHeader["deviceId"] = uniqueID
        appzillonHeader["userId"] = "IOS"
        appzillonHeader["screenId"] = "Login"
        appzillonHeader["status"] = NSNumber(booleanLiteral: true)
        appzillonHeader["source"] = "APPZILLON"
        appzillonHeader["clientNonce"] = NSNumber(booleanLiteral: false)
        appzillonHeader["interfaceId"] = "appzillonOnAppLaunch"
        appzillonHeader["os"] = "IOS"
        appzillonHeader["requestKey"] = ""
        appzillonHeader["origination"] = ipAddress
        appzillonHeader["serverNonce"] = ""
        appzillonHeader["sessionToken"] = ""
        appzillonHeader["preLogin"] = "true"
        appzillonHeader["appLaunch"] = "SECOND"
        
        var appzillonBody: [AnyHashable: Any] = [:]
        
        //AppSecToken request
        var appzillonGetAppSecTokensRequest: [AnyHashable : Any] = [:]
        appzillonGetAppSecTokensRequest["appId"] = appString
        appzillonGetAppSecTokensRequest["deviceId"] = uniqueID
        appzillonBody["appzillonGetAppSecTokensRequest"] = NSMutableDictionary(dictionary: appzillonGetAppSecTokensRequest)
        
        
        //AppMasterDetails request
        var appzillonAppMasterRequest: [AnyHashable: Any] = [:]
        appzillonAppMasterRequest["appId"] = appString
        appzillonAppMasterRequest["os"] = "IOS"
        appzillonAppMasterRequest["deviceId"] = uniqueID
        appzillonAppMasterRequest["appVersion"] = UserDefaults.standard.value(forKey: "storedBundleShortVersion") as? String
                                                    ?? StringConstants.Generic.emptyString
        appzillonAppMasterRequest["updateAppVersion"] = UserDefaults.standard.value(forKey: "updateAppVersion") as? String
                                                    ?? StringConstants.Generic.emptyString
        appzillonBody["appzillonAppMasterRequest"] = NSMutableDictionary(dictionary: appzillonAppMasterRequest)
        
        
        var appzillonOnAppLaunchRequest: [AnyHashable: Any] = [:]
        appzillonOnAppLaunchRequest["appzillonHeader"] = NSMutableDictionary(dictionary: appzillonHeader)
        appzillonOnAppLaunchRequest["appzillonBody"] = NSMutableDictionary(dictionary: appzillonBody)
        let ipUrl = self.apzServerURL
        
        CallServer.callServer(withRequest: NSMutableDictionary(dictionary: appzillonOnAppLaunchRequest),
                              ipUrl,
                              appString,
                              self) {[weak self] status, responseDictionary in
            if status {
                self?.appzillonSecondLaunchRequestSuccess(successResult: responseDictionary ?? [:])
            } else {
                self?.appzillonSecondLaunchRequestFailure(failureResult: responseDictionary ?? [:])
            }
        }
    }

    @objc func appzillonSecondLaunchRequestSuccess(successResult: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "appzillonRequestForSecondLaunchSuccess")
        if let jsonHeader = successResult["appzillonHeader"] as? [AnyHashable: Any],
           let status = jsonHeader["status"] as? NSNumber, status.boolValue {
            let appzillonBody = successResult["appzillonBody"] as? [AnyHashable: Any] ?? [:]
            
            //Handle AppSecToken
            let appzillonGetAppSecTokensResponse = appzillonBody["appzillonGetAppSecTokensResponse"] as? [AnyHashable: Any] ?? [:]
            if let status = appzillonGetAppSecTokensResponse["status"] as? String, status == "success" {
                self.isServerNonceReceived = StringConstants.Generic.yes
                //Hitting infra method to confirm that we received the serverNonce
                DispatchQueue.main.async {
                    self.webView.evaluateJavaScript(String(format: "apz.server.setAppSecToken('%@');", self.isServerNonceReceived))
                }
                apzServerNonce = appzillonGetAppSecTokensResponse["serverNonce"] as? String ?? ""
                apzSafeToken = appzillonGetAppSecTokensResponse["safeToken"] as? String ?? ""
                apzSessionToken = appzillonGetAppSecTokensResponse["sessionToken"] as? String ?? ""
                if let encServerToken = self.appPropertyDictionary["serverToken"] {
                    apzServerToken = CryptoSwiftManager.shared.getAESDecryptedStringForServerCalls(dataToDecrypt: encServerToken,
                                                                                                   keyStr: SERVERTOKENDECRYPTKEY)
                }
                //Do the device registration again if iOS device updates its OS
                self.runTimeDict = NSMutableDictionary(contentsOfFile: self.runTimeDictPath)
                self.currentVersion = UIDevice.current.systemVersion
                if let registerdVersion = Int(self.runTimeDict["registeredVersion"] as? String ?? ""),
                   let versioninInt = Int(UIDevice.current.systemVersion),
                   let registrationDone = self.runTimeDict["deviceRegistrationDone"] as? String,
                   registerdVersion < versioninInt, registrationDone == "YES" {
                    self.multifactorReq(version: self.currentVersion, location: nil)
                }
                // Check for notification permission if enabled from device settings later after installation
                let center = UNUserNotificationCenter.current()
                center.getNotificationSettings { settings in
                    if let serverRegDone = self.runTimeDict["serverRegistrationDone"] as? String, serverRegDone != "YES",
                       settings.authorizationStatus == .authorized {
                        // this will fetch device token and call didRegisterForRemoteNotificationsWithDeviceToken
                        DispatchQueue.main.async {
                            UIApplication.shared.registerForRemoteNotifications()
                        }
                    }
                }
            } else {
                APZLogger.log(logLvl: "E", message: "appzillonGetAppSecTokensResponse Failure")
            }
            
            //Handle AppMasterDetails
            if let appMasterDetailsResponse = appzillonBody["appzillonAppMasterResponse"] as? [AnyHashable: Any] {
                var mainAppInfo = appMasterDetailsResponse["appInstruction"] as? [AnyHashable:Any] ?? [:]
                if let wipeOutFlag = mainAppInfo["wipeout"] as? String, wipeOutFlag == StringConstants.Generic.yes {
                    AppzillonMainUtility.shared.emptySandbox(webView: webView)
                } else {
                    let mainAppId = APZAppDelegateUtility.shared.appDelegate?.containerPropsDictionary["MAINAPPID"] as? String ?? ""
                    let mainAppPropertyPath = APZNetworkUtility.shared.getAppPropertiesDictionaryPath(appID: mainAppId)
                    let mainAppDictionary = NSMutableDictionary(contentsOfFile: mainAppPropertyPath) ??
                    NSMutableDictionary(dictionary: [:])
                    if let otaRequired = APZAppDelegateUtility.shared.appDelegate?.containerPropsDictionary["OTAREQUIRED"] as? String, otaRequired == StringConstants.Generic.yes {
                        let encStoredAppVersion = mainAppDictionary["appVersion"] as? String ?? ""
                        let appVersion = mainAppInfo["appVersion"] as? String ?? StringConstants.Generic.emptyString
                        let storedAppVersion = CryptoSwiftManager.shared.decryptSingleValue(value: encStoredAppVersion)
                        
                        let isUpgradeRequired = appVersion != storedAppVersion ? "Y" : "N"
                        mainAppInfo["upgradeRequired"] = isUpgradeRequired
                        mainAppDictionary["upgradeRequired"] = CryptoSwiftManager.shared.encryptSingleValue(value: isUpgradeRequired)
                        mainAppDictionary.write(toFile: mainAppPropertyPath, atomically: true)
                    } else {
                        mainAppDictionary["upgradeRequired"] = CryptoSwiftManager.shared.encryptSingleValue(value: "N")
                        mainAppInfo["upgradeRequired"] = "N"
                        mainAppDictionary.write(toFile: mainAppPropertyPath, atomically: true)
                    }
                    
                    mainAppDictionary["masterDetailsResponse"] = CryptoSwiftManager.shared.encryptPlistData(dictionary: mainAppInfo)
                    mainAppDictionary["updateAction"] = CryptoSwiftManager.shared.encryptSingleValue(value: mainAppInfo["updateAction"] as? String ?? "")
                    mainAppDictionary.write(toFile: mainAppPropertyPath, atomically: true)
                    
                    if let dictData = try? JSONSerialization.data(withJSONObject: mainAppInfo, options: []) {
                        let jsonStr = String(data: dictData, encoding: .utf8) ?? StringConstants.Generic.emptyString
                        DispatchQueue.main.async {
                            self.webView.evaluateJavaScript(String(format: "appzillon.util.instructions(%@);", jsonStr))
                        }
                    }
                }
            } else {
                APZLogger.log(logLvl: "E", message: "appzillonAppMasterResponse failure")
            }
            AppzillonMainUtility.shared.setAppToken()
        } else {
            APZLogger.log(logLvl: "E", message: "appzillonRequestForSecondLaunch - Failed")
        }
    }
    
    
    @objc func appzillonSecondLaunchRequestFailure(failureResult: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "E", message: "appzillonRequestForSecondLaunchFailure")
        self.isServerNonceReceived = StringConstants.Generic.no
        
        if let appDelegate = APZAppDelegateUtility.shared.appDelegate{
            let offlineSupport = appDelegate.containerPropsDictionary["OFFLINESUPPORT"] as? String ?? "N"
            let appOfflineSupport = appDelegate.containerPropsDictionary["APPOFFLINESUPPORT"] as? String ?? "N"
            if ((offlineSupport.caseInsensitiveCompare(StringConstants.Generic.yes) == .orderedSame) || (appOfflineSupport.caseInsensitiveCompare(StringConstants.Generic.yes) == .orderedSame)) {
                self.injectHtmlInWebview()
            }
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + 5.0) {
            self.appzillonRequestForSecondLaunch()
        }
    }
}
