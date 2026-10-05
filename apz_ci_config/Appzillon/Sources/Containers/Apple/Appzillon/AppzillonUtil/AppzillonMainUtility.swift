// swiftlint:disable all
// Copyright (c) 2021 Appzillon. All rights reserved.
import Foundation
class AppzillonMainUtility: NSObject {
    @objc static let shared = AppzillonMainUtility()
    private override init() {
        //empty
    }
    // MARK: Main Utility Methods
     func upgradeRequired(result: [AnyHashable: Any], webView: WKWebView) {
        let upgradeReqStr = StringConstants.Generic.upgradeRequired
        let pluginId = result[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        let appID = result[StringConstants.Generic.appId] as? String ?? StringConstants.Generic.emptyString
        let appDictionary = NSMutableDictionary(contentsOfFile: getAppPropertiesPath(appID: appID)) ?? [:]
        let isUpgradeRequiredCrypted = appDictionary[upgradeReqStr] as? String ?? StringConstants.Generic.emptyString
        let isUpgradeRequired = CryptoSwiftManager.shared.decryptSingleValue(value: isUpgradeRequiredCrypted)
         let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                     status: true,
                                                                     keepAlive: false,
                                                                     responseKeys: [upgradeReqStr],
                                                                     responseValues: [isUpgradeRequired])
         MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                parameter: params)
    }
    
     func getUpdateActionRequired(result: [AnyHashable: Any],
                                  containerPropsDictionary: [AnyHashable: Any],
                                  webView: WKWebView) {
        let updateActionStr = StringConstants.Generic.updateAction
        let pluginId = result[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        let mainAppID = containerPropsDictionary[StringConstants.Generic.mainAppId] as? String ?? StringConstants.Generic.emptyString
        let appDictionary = NSMutableDictionary(contentsOfFile: getAppPropertiesPath(appID: mainAppID)) ?? [:]
        let isUpdateActionCrypted = appDictionary[updateActionStr] as? String ?? StringConstants.Generic.emptyString
        let isUpdateAction = CryptoSwiftManager.shared.decryptSingleValue(value: isUpdateActionCrypted)
         let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                     status: true,
                                                                     keepAlive: false,
                                                                     responseKeys: [updateActionStr],
                                                                     responseValues: [isUpdateAction])
         MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                parameter: params)
    }
    
     func getAppVersion(result: [AnyHashable: Any], webView: WKWebView) {
         if let appId = result[StringConstants.Generic.appId] as? String,
            let pluginId = result[StringConstants.Generic.pluginId] as? String {
            let appPropertiesPath = getAppPropertiesPath(appID: appId)
             if let appPropsDictionary = NSDictionary(contentsOfFile: appPropertiesPath) as? [AnyHashable: Any],
                let appVersion = appPropsDictionary[StringConstants.Generic.appVersion] as? String {
                 let installedVersion = CryptoSwiftManager.shared.decryptSingleValue(value: appVersion)
                 let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                             status: true,
                                                                             keepAlive: false,
                                                                             responseKeys: [StringConstants.Generic.appVersion],
                                                                             responseValues: [installedVersion])
                 MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                        jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                        parameter: params)
             }
         }
     }

    func getIPAddressCall()-> String? {
        let wifi: [String] = ["en0"]
        let knownWiredIFS: [String] = ["en2", "en3", "en4"]
        let knownCellIFS: [String] = ["pdp_ip0","pdp_ip1","pdp_ip2","pdp_ip3"]
        var addresses: [String: String] = ["wireless": "",
                                             "wired": "",
                                             "cell": ""]
        var ifaddr: UnsafeMutablePointer<ifaddrs>? = nil
        if getifaddrs(&ifaddr) == 0 {
            var ptr = ifaddr
            while ptr != nil {
                defer { ptr = ptr?.pointee.ifa_next }
                let interface = ptr?.pointee
                let addrFamily = interface?.ifa_addr.pointee.sa_family
                if addrFamily == UInt8(AF_INET) {
                    getNetworkAddress(interface, wifi, knownWiredIFS, knownCellIFS, &addresses)
                }
            }
        }
        freeifaddrs(ifaddr)
        var ipAddressString = "error"
        let wirelessString = addresses["wireless"]
        let wiredString = addresses["wired"]
        let cellString = addresses["cell"]
        if let wirelessString = wirelessString, wirelessString.count > 0 {
            ipAddressString = wirelessString
        } else if let wiredString = wiredString, wiredString.count > 0 {
            ipAddressString = wiredString
        } else if let cellString = cellString, cellString.count > 0 {
            ipAddressString = cellString
        }
        return ipAddressString
    }
    
    fileprivate func getNetworkAddress(_ interface: ifaddrs?, _ wifi: [String], _ knownWiredIFS: [String], _ knownCellIFS: [String], _ addresses: inout [String : String]) {
        let name: String = String(cString: (interface?.ifa_name)!)
        if (wifi.contains(name) || knownWiredIFS.contains(name) || knownCellIFS.contains(name)) {
            var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
            getnameinfo(interface?.ifa_addr,
                        socklen_t((interface?.ifa_addr.pointee.sa_len)!),
                        &hostname, socklen_t(hostname.count),
                        nil, socklen_t(0), NI_NUMERICHOST)
            let address = String(cString: hostname)
            if wifi.contains(name){
                addresses["wireless"] =  address
            }else if knownWiredIFS.contains(name){
                addresses["wired"] =  address
            }else if knownCellIFS.contains(name){
                addresses["cell"] =  address
            }
        }
    }

    // MARK: Get App Token & Set App Token Methods
    @objc func setAppToken() {
        UserDefaults.standard.set(CryptoSwiftManager.shared.encryptSingleValue(value: StringConstants.Generic.yes),
                                  forKey: StringConstants.Generic.onAppLoaded)
        UserDefaults.standard.synchronize()
    }
     func getAppToken(_ jsonDict: [AnyHashable: Any], webView: WKWebView) {
         if !jsonDict.isEmpty, let pluginId = jsonDict[StringConstants.Generic.pluginId] as? String,
            let appToken = UserDefaults.standard.value(forKey: StringConstants.Generic.onAppLoaded) as? String {
             let onAppLoaded = CryptoSwiftManager.shared.decryptSingleValue(value: appToken)
             let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId, status: true, keepAlive: true, responseKeys: [StringConstants.AppzillonMain.appTokenStatus], responseValues: [onAppLoaded])
             MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                    jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                    parameter: params)
         }
     }
    
    @objc func emptySandbox(webView: WKWebView) {
        DispatchQueue.main.async {
            webView.evaluateJavaScript(StringConstants.AppzillonMain.localStorage, completionHandler: nil)
        }
        let fileMgr = FileManager.default
        let dirPath = FileManagerUtility.documentDirectory().path
        if let directoryContents = try? fileMgr.contentsOfDirectory(atPath: dirPath), !directoryContents.isEmpty {
            for path in directoryContents {
                let fullPath = (dirPath as NSString).appendingPathComponent(path)
                        do {
                    try fileMgr.removeItem(atPath: fullPath)
                }
                catch let error {
                    print("Error deleting: \(error.localizedDescription)")
                }
            }
        }
        UserDefaults.standard.set("YES", forKey: StringConstants.AppzillonMain.wipeOut)
    }
    
    // MARK: loadAppProperties method
     func loadAppProperties(appString: String) -> NSDictionary {
        var settingsPath: String = ""
        let appPropertiesPath = FileManagerUtility.documentDirectory().appendingPathComponent("/Assets/apps/\(appString)/plist/AppProperties.plist").path
        let containerPropDict = APZAppDelegateUtility.shared.getContainerPropsDict()
        if !MiscellaneousMethod.shared.getAppHasLaunchedBefore(mainAppId: appString) {
            if containerPropDict[StringConstants.Generic.otaRequired] as? String == StringConstants.Generic.yes {
                let path = String(format: "Assets/apps/%@/screens/config/appprops.json", appString)
                settingsPath = FileManagerUtility.documentDirectory().appendingPathComponent(path).path

            } else {
                if let path = MiscellaneousMethod.shared.getAppBundle().path(forResource: "appprops",
                                                                             ofType: "json",
                                                                             inDirectory: "Assets/apps/\(appString)/screens/config") {
                    settingsPath = path
                }
            }
            if FileManager.default.fileExists(atPath: settingsPath) {
                do {
                    let fileContent = try String.init(contentsOfFile: settingsPath, encoding: .utf8)
                    if let data = fileContent.data(using: .utf8),
                       let jsonDict = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: Any],
                       let encryptDict = CryptoSwiftManager.shared.encryptPlistData(dictionary: jsonDict) as? NSDictionary {
                        encryptDict.write(toFile: appPropertiesPath, atomically: true)
                        MiscellaneousMethod.shared.setAppHasLaunchedBefore(mainAppId: appString)
                    }
                    
                } catch let error {
                    print(error.localizedDescription)
                }
                
            }
        }
        if MiscellaneousMethod.shared.getAppHasLaunchedOnOlderVersion() {
            MiscellaneousMethod.shared.setAppHasLaunchedBefore(mainAppId: appString)
        }
        let appPropDict = NSDictionary.init(contentsOfFile: appPropertiesPath)
        let decryptDict = CryptoSwiftManager.shared.decryptPlistData(dictionary: appPropDict as! [AnyHashable : Any]) as? NSDictionary
        if let dict = decryptDict {
            return dict
        }
        return [:]
    }
    
    // MARK: getUUID Method
     func getUUID() -> String {
        if let uuid = UserDefaults.standard.object(forKey: StringConstants.Generic.uniqueId) as? String {
            return uuid
        } else {
            let uuid = UUID().uuidString.replacingOccurrences(of: StringConstants.AppzillonMain.hyphen,
                                                              with: StringConstants.Generic.emptyString)
            UserDefaults.standard.setValue(uuid, forKey: StringConstants.Generic.uniqueId)
            return uuid
        }
    }
    // MARK: Device Orientation Method
     func sendDeviceOrientationNotification(toOrientation: UIInterfaceOrientation, webView: WKWebView, orientationDictionary: [AnyHashable: Any], cbEvent: String) {
        var orientation = StringConstants.Generic.emptyString
        let pluginId = orientationDictionary[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        let deviceOrientation = toOrientation
        let isCurOrientationPortrait = (deviceOrientation == .portrait) || (deviceOrientation == .portraitUpsideDown)
        let isCurOrientationLandscape = (deviceOrientation == .landscapeLeft) || (deviceOrientation == .landscapeRight)
        let isCallbackRequired = isCurOrientationPortrait || isCurOrientationLandscape
        if isCallbackRequired {
            if deviceOrientation == .portrait || deviceOrientation == .portraitUpsideDown {
                orientation = StringConstants.Generic.portrait
            } else if deviceOrientation == .landscapeLeft || deviceOrientation == .landscapeRight {
                orientation = StringConstants.Generic.landScape
            }
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: true,
                                                                        responseKeys: [StringConstants.Generic.orientation,StringConstants.Generic.cbEvent],
                                                                        responseValues: [orientation,cbEvent])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        } else {
            APZLogger.log(logLvl: "I", message: "--Notification callback not required--")
        }
    }
    
    // MARK: Private Methods
    func getAppPropertiesPath(appID: String) -> String {
        let documentsPath = FileManagerUtility.documentDirectory()
        let appPropertyUrl = documentsPath.appendingPathComponent("\(StringConstants.Generic.sandBoxPath)\(appID)\(StringConstants.Generic.appPropertiesPlistPath)")
        return appPropertyUrl.path
    }
    
    func getAssosiatedDomains() -> [String]  {
       let containerPropDict = APZAppDelegateUtility.shared.getContainerPropsDict()
       if let appID = containerPropDict[StringConstants.Generic.mainAppId] as? String,
          let path = MiscellaneousMethod.shared.getAppBundle().path(forResource: "appprops", ofType: "json", inDirectory: "Assets/apps/\(appID)/screens/config"), FileManager.default.fileExists(atPath: path) {
           do {
               let fileContent = try String.init(contentsOfFile: path, encoding: .utf8)
               if let data = fileContent.data(using: .utf8),
                  let jsonDict = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: Any],
                  let assosiatedDomainsArray = jsonDict["AssositatedDomains"] as? [String] {
                   return assosiatedDomainsArray
               }
           }  catch let error {
               print(error.localizedDescription)
           }
       }
       return []
   }
    
    
    func checkInternetAvailability(result: [AnyHashable: Any], webView: WKWebView) {
        let pluginId = result[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        let isInternetAvailable = APZNetworkUtility.shared.isConnectedToNetwork()
        if (isInternetAvailable){
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: ["isNetworkAvailable"],
                                                                        responseValues: ["Y"])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }else{
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: ["isNetworkAvailable"],
                                                                        responseValues: ["N"])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
        
    }
}
