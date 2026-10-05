// Copyright (c) 2021 Appzillon. All rights reserved.
// swiftlint:disable all
import Foundation

extension AppzillonViewController: WKUIDelegate, WKNavigationDelegate, WKScriptMessageHandler {
    // MARK: Webview Related Function
    
     public func webView(_ webView: WKWebView,
                              decidePolicyFor navigationAction: WKNavigationAction,
                              decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        if !self.appWiped {
            self.navItem?.leftBarButtonItem?.isEnabled = true
            self.url = navigationAction.request.url as NSURL?
            urlString = self.url.absoluteString as NSString?
            self.pageName = self.urlString.lastPathComponent as NSString
            if urlString.contains("command") {
                let result = self.parseJSON(pageName as String?) ?? [:]
                let commandString = self.getClassNameForPlugin(result["command"] as? String ?? "")
                if commandString == "callServerforInfra" {
                    let callServerDictionary = self.parseJSON(pageName as String?) ?? [:]
                    let requestJson = NSMutableDictionary(dictionary: callServerDictionary)
                    CallServer.callServerFromInfra(withRequest: requestJson, self, webView, appString)
                    return decisionHandler(WKNavigationActionPolicy.cancel)
                }
                else if commandString == "getDeviceInfo" {
                    DispatchQueue.main.async {
                        self.webView.evaluateJavaScript(String(format: "apz.server.setAppSecToken('%@');", self.isServerNonceReceived))
                    }
                    self.getDeviceInfo(result: result)
                    return decisionHandler(.cancel)
                }
                else if commandString == "getServerNonce" {
                    self.serverNonceJSON = result
                    self.getServerNonce()
                    return decisionHandler(.cancel)
                }
                else if commandString == "GetUserPrefs" {
                    self.getUserPrefs(result as? [String:Any] ?? [:])
                    return decisionHandler(.cancel)
                }
                else if commandString == "SetUserPrefs", !result.isEmpty {
                    self.setUserPrefs(result as? [String:Any] ?? [:])
                    return decisionHandler(.cancel)
                }
                else if commandString == "customHTTPRequest" {
                    APZNetworkManager.shared.performNonAppzillonRequest(requestDictionary: result as? [String:Any] ?? [:], webView: webView, appID: appString)
                    return decisionHandler(.cancel)
                }
                else if commandString.hasPrefix("upgradeRequired") {
                    if !result.isEmpty {
                    AppzillonMainUtility.shared.upgradeRequired(result: result, webView: webView)
                    } else {
                        print("-APZ json is empty--")
                    }
                    return decisionHandler(.cancel)
                }
                else if commandString.hasPrefix("getUpdateActionRequired") {
                    if let contPropsDict = APZAppDelegateUtility.shared.appDelegate?.containerPropsDictionary as? [AnyHashable: Any], !result.isEmpty {
                        AppzillonMainUtility.shared.getUpdateActionRequired(result: result, containerPropsDictionary: contPropsDict, webView: webView)
                    } else {
                        print("--APZ json is empty")
                    }
                    return decisionHandler(.cancel)
                }
                else if commandString == "lockRotation", !result.isEmpty {
                    self.rotationPluginFlag = false

                    self.scenes = UIApplication.shared.connectedScenes.first as? UIWindowScene
                    let currentInterfaceOrientation = self.scenes.interfaceOrientation
                    if currentInterfaceOrientation == .landscapeLeft || currentInterfaceOrientation == .landscapeRight {
                        self.appOrientation = "LANDSCAPE"
                    } else if currentInterfaceOrientation == .portrait {
                        self.appOrientation = "PORTRAIT"
                    }
                    
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: false, responseKeys: [], responseValues: []))
                    
                    return decisionHandler(.cancel)
                }
                else if commandString == "unlockRotation", !result.isEmpty {
                    self.rotationPluginFlag = true
                    self.appOrientation = "ANY"
                    self.forceOrientation = nil
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: false, responseKeys: [], responseValues: []))
                    return decisionHandler(.cancel)
                }
                else if commandString == "showSplashScreen" {
                    self.splashScreenManual = true
                    self.showSplashScreen()
                    return decisionHandler(.cancel)
                }
                else if commandString == "controlEvents" {
                    if !result.isEmpty {
                    self.controlEventRequest = result
                    self.controlEvents(result)
                    } else {
                        print("--error in parsing json--")
                    }
                    return decisionHandler(.cancel)
                }
                else if commandString == "nativeService" {
                    NativeService().execute(jsonDict: result, webView: webView)
                    return decisionHandler(.cancel)
                }
                else if commandString == "MakePhoneCall" {
                    APZTelephony.shared.executePhoneCall(jsonDict: result, webView: webView, viewController: self)
                    return decisionHandler(.cancel)
                }
                else if commandString == "sendSMS" {
                    APZTelephony.shared.sendSMS(jsonDict: result, apzViewController: self, webView: webView)
                    return decisionHandler(.cancel)
                }
                else if commandString == "chgPassword" {
                    if !result.isEmpty {
                        APZLoginUtility.changePassword(jsonDict: result, viewController: self, webView: webView)
                    } else {
                        print("--error in parsing GenerateOTP changePassword json--")
                    }
                    return decisionHandler(.cancel)
                }
                else if commandString == "getPref", !result.isEmpty {
                    UserPreferenceUtility.sharedInstance.getUserPreference(result, appString: appString, webView: webView)
                    return decisionHandler(.cancel)
                }
                else if commandString == "setPref" , !result.isEmpty{
                    UserPreferenceUtility.sharedInstance.setUserPreference(result, appString: appString, webView: webView)
                    return decisionHandler(.cancel)
                }
                else if commandString == "setOrientation" {
                    if !result.isEmpty {
                        self.setOrientation(orientation: result)
                    } else {
                        print("--error in parsing json--")
                    }
                    
                    return decisionHandler(.cancel)
                }
                else if commandString == "clearAppData" {
                    AppzillonMainUtility.shared.emptySandbox(webView: webView)
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: false, responseKeys: [], responseValues: []))
                    return decisionHandler(.cancel)
                    
                }
                else if commandString == "appIdleTimeOut" {
                    if let appIdleMaxTimeString = self.appPropertyDictionary["idleTimeOut"] as? String, !result.isEmpty,
                    let doubleVal =  Double(appIdleMaxTimeString) {
                        self.appIdleMaxTime = doubleVal
                        calledFromPlugin = false
                        self.inputTimerJSON = result
                        self.startGesture(result: self.inputTimerJSON, webView: webView, callee: "Timer")
                        self.resetIdleTimer()
                    } else {
                        let resultKey = [StringConstants.Generic.errorCode]
                        let resultValue = ["APZ-CNT-132"]
                        MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                               parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: false, keepAlive: false, responseKeys: resultKey, responseValues: resultValue))
                    }
                }
                else if commandString == "GestureSupportStart" {
                    if !result.isEmpty {
                        self.inputGestJSON = result
                        self.startGesture(result: result, webView: webView, callee: "gesture")
                        let resultKey = [StringConstants.Generic.cbEvent]
                        let resultValue = [StringConstants.Generic.started]
                        MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                               parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: true, responseKeys: resultKey, responseValues: resultValue))
                    } else {
                        print("--JSON Parsing Error--")
                    }
                    return decisionHandler(.cancel)
                    
                }
                else if commandString == "GestureSupportStop" {
                    if !result.isEmpty {
                        self.stopGesture(result: result, webView: webView, callee: "gesture")
                        let resultKey = [StringConstants.Generic.cbEvent]
                        let resultValue = [StringConstants.Generic.stopped]
                        MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                               parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: false, responseKeys: resultKey, responseValues: resultValue))
                    }
                    else {
                        print("--JSON Parsing Error--")
                    }
                    return decisionHandler(.cancel)
                }
                else if commandString == "remoteDebug" ,!result.isEmpty {
                    self.remoteDebug(debugInfo: result)
                    return decisionHandler(.cancel)
                }
                else if commandString == "appVersion", let _ = result["appId"] as? String {
                    AppzillonMainUtility.shared.getAppVersion(result: result, webView: webView)
                    return decisionHandler(.cancel)
                }
                else if commandString == "getIP", !result.isEmpty {
                    self.getIPAddress(result: result)
                    return decisionHandler(.cancel)
                }
                else if commandString == "disableBounce", !result.isEmpty {
                    self.setBounce(result)
                    return decisionHandler(.cancel)
                }
                else if commandString == "KBListStart" {
                    self.keyBoardStartListener(result: result)
                    return decisionHandler(.cancel)
                }
                else if commandString == "KBListStop" {
                    self.keyBoardStopListener(result: result)
                    return decisionHandler(.cancel)
                    
                }
                else if commandString == "loadLanguageStrings"{
                    self.loadLanguageLocalisedStrings(result)
                    return decisionHandler(.cancel)
                }
                else if commandString == "checkNetworkAvailability"{
                    AppzillonMainUtility.shared.checkInternetAvailability(result: result, webView: webView)
                    return decisionHandler(.cancel)
                }
                else if commandString == "notifListStart", !result.isEmpty {
                    self.notificationDictionary = result
                    let resultKey = [StringConstants.Generic.cbEvent]
                    let resultValue = [StringConstants.Generic.started]
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: true, responseKeys: resultKey, responseValues: resultValue))
                    if APZAppDelegateUtility.shared.appDelegate?.pushMessege != StringConstants.Generic.emptyString {
                        self.notifCallback()
                    }
                    return decisionHandler(.cancel)
                }
                else if commandString == "notifListStop", !result.isEmpty {
                    self.notificationDictionary = nil;
                    let resultKey = [StringConstants.Generic.cbEvent]
                    let resultValue = [StringConstants.Generic.stopped]
                    print("result: \(result)")
                    print("\(String(describing: result[StringConstants.Generic.pluginId]))")
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: false, responseKeys: resultKey, responseValues: resultValue))
                    return decisionHandler(.cancel)
                }
                else if commandString == "shortcutListStart", !result.isEmpty  {
                    self.shortcutItemDictionary = result
                    let resultKey = [StringConstants.Generic.cbEvent]
                    let resultValue = [StringConstants.Generic.started]
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: true, responseKeys: resultKey, responseValues: resultValue))
                    if APZAppDelegateUtility.shared.appDelegate?.shortcutID != StringConstants.Generic.emptyString {
                        self.shortcutActionCallback()
                    }
                    return decisionHandler(.cancel)
                }
                else if commandString == "shortcutListStop" {
                    self.shortcutItemDictionary = nil;
                    let resultKey = [StringConstants.Generic.cbEvent]
                    let resultValue = [StringConstants.Generic.stopped]
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: false, responseKeys: resultKey, responseValues: resultValue))
                    print("succesfull")
                    return decisionHandler(.cancel)
                }
                else if commandString == "universalLinkListnerStart", !result.isEmpty {
                    universalLinkDictionary = result
                    let resultKey = [StringConstants.Generic.cbEvent]
                    let resultValue = [StringConstants.Generic.started]
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString, status: true, keepAlive: true, responseKeys: resultKey, responseValues: resultValue))

                    let appDelegate = APZAppDelegateUtility.shared.appDelegate
                    if appDelegate?.universalLinkURLString != StringConstants.Generic.emptyString {
                        universalLinkCallback()
                    }
                    return decisionHandler(.cancel)

                }
                else if commandString == "universalLinkListnerStop", !result.isEmpty {
                    universalLinkDictionary = nil
                    let resultKey = [StringConstants.Generic.cbEvent]
                    let resultValue = [StringConstants.Generic.stopped]
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString, status: true, keepAlive: false, responseKeys: resultKey, responseValues: resultValue))
                    return decisionHandler(.cancel)

                }
                else if commandString == "nwListStart", !result.isEmpty  {
                    self.nwListenerDict = result
                    self.startNetworkReachabilityListener()
                    let resultKey = [StringConstants.Generic.cbEvent]
                    let resultValue = [StringConstants.Generic.started]
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: true, responseKeys: resultKey, responseValues: resultValue))
                    return decisionHandler(.cancel)
                }
                else if commandString == "nwListStop",!result.isEmpty {
                    self.nwListenerDict = nil;
                    self.stopNetworkReachabilityListener()
                    let resultKey = [StringConstants.Generic.cbEvent]
                    let resultValue = [StringConstants.Generic.stopped]
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: false, responseKeys: resultKey, responseValues: resultValue))
                    return decisionHandler(.cancel)
                }
                else if commandString == "startOrientList", !result.isEmpty {
                    self.orientationDictionary = result
                    AppzillonMainUtility.shared.sendDeviceOrientationNotification(toOrientation: WindowUtility.getUiInterfaceOrientation(),
                                                                                  webView: webView,
                                                                                  orientationDictionary: orientationDictionary,
                                                                                  cbEvent: "started")
                    return decisionHandler(.cancel)
                }
                else if commandString == "stopOrientList", !result.isEmpty {
                    self.orientationDictionary = nil;
                    let resultKey = [StringConstants.Generic.cbEvent]
                    let resultValue = [StringConstants.Generic.stopped]
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: true, keepAlive: false, responseKeys: resultKey, responseValues: resultValue))
                }
                else if commandString == "hideSplashScreen" {
                    self.splashScreenManual = false
                    self.hideSplashScreen()
                    return decisionHandler(.cancel)
                }
                else if commandString == "closeAppzillonSDK" , !result.isEmpty {
                    let resultKey = [StringConstants.Generic.errorCode]
                    let resultValue = ["APZ-CNT-299"]
                    MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                           parameter: APZJsonUtility.shared.createResponseJSONString(pluginId: result[StringConstants.Generic.pluginId] as? String ?? "", status: false, keepAlive: false, responseKeys: resultKey, responseValues: resultValue))
                    
                    return decisionHandler(.cancel)
                }
                else if commandString == "checkAppTokenSet", !result.isEmpty {
                    AppzillonMainUtility.shared.getAppToken(result, webView: webView)
                    return decisionHandler(.cancel)
                }
                else {
                    if !result.isEmpty {
                        let isExecuted = self.execute(commandString, jsonDict: result, wbView: webView)
                        if isExecuted {
                            print("--Plugin executed---")
                        } else {
                            print("--Plugin not executed---")
                        }
                    }
                    else {
                        print("--APZ json empty--")
                    }
                    return decisionHandler(.cancel)
                }
            }
            else {
                return decisionHandler(.allow)
            }
            return decisionHandler(.allow)
        } else {
            return decisionHandler(.allow)
        }
    }
  

     public func webView(_ webView: WKWebView, runJavaScriptAlertPanelWithMessage message: String, initiatedByFrame frame: WKFrameInfo, completionHandler: @escaping () -> Void) {
        let alert = UIAlertController(title: message, message: nil, preferredStyle: .alert)
        alert.addAction(UIAlertAction(title: StringConstants.Generic.ucOk, style: .cancel, handler: { action in
            completionHandler()
        }))
        self.present(alert, animated: true, completion: nil)
    }

     public func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        let text = message.body
        let alert = UIAlertController(title: "Message from JavaScript", message: text as? String, preferredStyle: UIAlertController.Style.alert)
        alert.addAction(UIAlertAction(title: StringConstants.Generic.ucOk, style: .cancel, handler: { action in
            print("OK")
        }))
        self.present(alert, animated: true, completion: nil)
    }
    
    //MARK: injectHtmlInWebview method
    @objc func injectHtmlInWebview() {
        if !self.isFirstPageLaunched {
            self.otaStatus = APZAppDelegateUtility.shared.appDelegate?.containerPropsDictionary["OTAREQUIRED"] as? String ?? StringConstants.Generic.emptyString
            if !self.isAppExpired {
                if self.otaStatus == StringConstants.Generic.yes {
                    let appzillonFirstPagePath = FileManagerUtility.documentDirectory().path.appendingFormat("/Assets/%@.html", appString)
                    let filePathUrl = NSURL.fileURL(withPath: appzillonFirstPagePath)
                    let fileDirectoryUrl = filePathUrl.deletingLastPathComponent()
                    webView.loadFileURL(filePathUrl, allowingReadAccessTo: fileDirectoryUrl)
                } else {
                    let bundlePath = MiscellaneousMethod.shared.getAppBundle().path(forResource: appString, ofType: "html", inDirectory: "Assets")
                    if let bundlePath = bundlePath {
                        let filePathUrl = NSURL.fileURL(withPath: bundlePath )
                        let fileDirectoryUrl = filePathUrl.deletingLastPathComponent()
                        webView.loadFileURL(filePathUrl, allowingReadAccessTo: fileDirectoryUrl)
                    }
                }
            }
            self.isFirstPageLaunched = true
        }
    }
    
    //MARK: SetupWebView method
     func setupWebview() {
        let config = WKWebViewConfiguration.init()
        config.preferences = WKPreferences.init()
        config.preferences.minimumFontSize = 10
        config.preferences.javaScriptEnabled = true
        config.preferences.javaScriptCanOpenWindowsAutomatically = false
        webView = WKWebView.init(frame: self.view.frame, configuration: config)
        webView.uiDelegate = self
        webView.navigationDelegate = self
        webView.allowsBackForwardNavigationGestures = true
        webView.scrollView.contentInsetAdjustmentBehavior = UIScrollView.ContentInsetAdjustmentBehavior.never
        webView.configuration.userContentController.add(self, name: "JavaScriptObserver")
        webView.configuration.preferences.setValue("YES", forKey: "allowFileAccessFromFileURLs")
        webView.allowsBackForwardNavigationGestures = true
        self.view.addSubview(webView)
    }
    func loadLanguageLocalisedStrings(_ dictionary: [AnyHashable: Any]) {
        if let lparams = dictionary["lparams"] {
            self.languageDataDictionary = lparams as? [AnyHashable: Any]
        }
    }
}
// swiftlint:enable all
