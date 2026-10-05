// swiftlint:disable all
// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit
extension AppzillonViewController {
     func getUserPrefs(_ result: [String: Any]) {
        let pluginId = result[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        let appStr = self.appString ?? StringConstants.Generic.emptyString
         let docDirectory = FileManagerUtility.documentDirectory()
        let userSettingsPath =
         docDirectory.appendingPathComponent("/Assets/apps/\(appStr)/plist/UserSettings.plist").path
        var finalResponse: [String: Any] = [:]
        if let userSettingsDictionary = NSDictionary(contentsOfFile: userSettingsPath) as? [AnyHashable: Any] {
            finalResponse["userprefs"] = userSettingsDictionary
            finalResponse[StringConstants.Generic.pluginId] = pluginId
        } else {
            finalResponse["userprefs"] = StringConstants.Generic.emptyString
            finalResponse[StringConstants.Generic.pluginId] = pluginId
        }
        if let userSettingsData = try? JSONSerialization.data(withJSONObject: finalResponse, options: .prettyPrinted),
            let userSettingsJSONStr = String.init(data: userSettingsData, encoding: .utf8) {
            DispatchQueue.main.async {
                self.webView.evaluateJavaScript(String(format: "%@(%@);",
                                                       StringConstants.Generic.jscallBackMethod,
                                                       userSettingsJSONStr),
                                                completionHandler: nil)
                }
            }
    }
    @objc func setUserPrefs(_ result: [String: Any]) {
        let pluginId = result[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        let appStr = self.appString ?? StringConstants.Generic.emptyString
        let documentDirectoryPath = FileManagerUtility.documentDirectory()
        let userPrefPath = documentDirectoryPath.appendingPathComponent("\(StringConstants.Generic.sandBoxPath)\(appStr)\(StringConstants.UserPreferences.plistPath)").path
        var dict = NSDictionary(contentsOfFile: userPrefPath) as? [AnyHashable: Any]
        var userPrefs = result["userPrefs"] as? [AnyHashable : Any] ?? [:]
        for key in userPrefs.keys {
            if let value = userPrefs[key] {
                userPrefs[key] = value
                dict?[StringConstants.UserPreferences.userPref] = userPrefs
            }
        }
        if let userPrefDictionary = dict {
            let success = (userPrefDictionary as NSDictionary).write(toFile: userPrefPath, atomically: true)
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: success,
                                                                        keepAlive: false,
                                                                        responseKeys: [], responseValues: [])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
    }
    // MARK: getDeviceMapping
     func getDeviceMapping() {
        self.appString =
         APZAppDelegateUtility.shared.appDelegate?.containerPropsDictionary[StringConstants.Generic.mainAppId]
         as? String
        if let appString = appString {
            var pathSettingJson: String
            if APZAppDelegateUtility.shared.appDelegate?.containerPropsDictionary[StringConstants.Generic.otaRequired]
                as? String == StringConstants.Generic.yes {
                pathSettingJson = FileManagerUtility.documentDirectory().appendingPathComponent("Assets/apps/\(appString)/screens/config/devicegroups.json").path
            } else {
                pathSettingJson = MiscellaneousMethod.shared.getAppBundle().path(forResource: "devicegroups",
                                                                           ofType: "json",
                                                                           inDirectory: "Assets/apps/\(appString)/screens/config") ?? StringConstants.Generic.emptyString
            }
            if FileManager.default.fileExists(atPath: pathSettingJson) {
                do {
                    let fileContent = try String(contentsOfFile: pathSettingJson, encoding: .utf8)
                    if let contentData = fileContent.data(using: .utf8),
                       let jsonDict = try JSONSerialization.jsonObject(with: contentData,
                                                                       options: .mutableContainers) as? [String: Any],
                       let data = jsonDict["deviceGroups"] as? [[String: Any]] {
                        print("---Copied JSON from Appzillon---")
                        var width: CGFloat
                        var height: CGFloat
                        let orientation: UIInterfaceOrientation = WindowUtility.getUiInterfaceOrientation()
                        if orientation.isLandscape {
                            height = UIScreen.main.bounds.size.width
                            width = UIScreen.main.bounds.size.height
                        } else {
                            height = UIScreen.main.bounds.size.height
                            width = UIScreen.main.bounds.size.width
                        }
                        var temp = 0
                        var count = 0
                        for device in data {
                            let jsonHeight = device["height"] as? String
                            let jsonWidth = device["width"] as? String
                            if let jsonHeight = jsonHeight, let jsonWidth = jsonWidth,
                               let intJsonHeight = Int(jsonHeight), let intJsonWidth = Int(jsonWidth) {
                                var deltaHeight = Int(height) - intJsonHeight
                                var deltaWidth = Int(width) - intJsonWidth
                                if deltaWidth == 0 && deltaHeight == 0 {
                                    self.deviceGroup = device["name"] as? String
                                    self.appOrientation = device["orientation"] as? String
                                    break
                                }
                                if deltaWidth < 0 {
                                    deltaWidth = 0 - deltaWidth
                                }
                                if deltaHeight < 0 {
                                    deltaHeight = 0 - deltaHeight
                                }
                                var deltaWidthHeight = deltaWidth + deltaHeight
                                if deltaWidthHeight < 0 {
                                    deltaWidthHeight = 0 - deltaWidthHeight
                                }
                                if count == 0, let name = device["name"] as? String,
                                    let orientation = device["orientation"] as? String {
                                    temp = deltaWidthHeight
                                    count += 1
                                    self.deviceGroup = name
                                    self.appOrientation = orientation
                                }
                                if temp > deltaWidthHeight, let name = device["name"] as? String,
                                   let orientation = device["orientation"] as? String {
                                    temp = deltaWidthHeight
                                    self.deviceGroup = name
                                    self.appOrientation = orientation
                                }
                            }
                        }
                    }
                } catch let error {
                    print(error.localizedDescription)
                }
            }
        }
    }
    // MARK: IPAddress
     func getIPAddress(result: [AnyHashable: Any]) {
        if let pluginId = result[StringConstants.Generic.pluginId] as? String {
            ipAddress = AppzillonMainUtility.shared.getIPAddressCall()
            let responseKeys = [StringConstants.Generic.errorCode]
            let responseValues = [StringConstants.Sensor.gpsNetworkOff]
            if ipAddress == "error" {
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: false,
                                                                            keepAlive: false,
                                                                            responseKeys: responseKeys,
                                                                            responseValues: responseValues)
                MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
            } else {
                let responseValues = [ipAddress ?? StringConstants.Generic.emptyString]
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: true,
                                                                            keepAlive: false,
                                                                            responseKeys: ["ip"],
                                                                            responseValues: responseValues)
                MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
            }
        }
    }
    // MARK: remoteDebug
     func remoteDebug(debugInfo: [AnyHashable: Any]) {
        if let remoteDebugStatus = debugInfo["debug"] as? String {
            DispatchQueue.main.async {
                self.webView.evaluateJavaScript(String(format: "appzillon.user.sendLog='%@'",
                                                       remoteDebugStatus),
                                                completionHandler: nil)
            }
            let appStr = self.appString ?? StringConstants.Generic.emptyString
            let pathToDictionary = FileManagerUtility.documentDirectory().appendingPathComponent("Assets/apps/\(appStr)/plist/AppProperties.plist").path
            if var settingsDictionary = NSDictionary(contentsOfFile: pathToDictionary) as? [AnyHashable: Any] {
                settingsDictionary["SENDLOG"] = CryptoSwiftManager.shared.encryptSingleValue(value: remoteDebugStatus)
                (settingsDictionary as NSDictionary).write(toFile: pathToDictionary, atomically: false)
            }
        }
    }
    // MARK: Notification Related Methods
    @objc func notifCallback() {
        if let notifDict = self.notificationDictionary, !notifDict.isEmpty {
            let pluginId = notifDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            let resultKeys = ["message",
                              "title",
                              "subtitle",
                              "image_url",
                              "notification_code",
                              "action_code",
                              "params",
                              StringConstants.Generic.cbEvent]
            let resultValues = [APZAppDelegateUtility.shared.appDelegate?.pushMessege,
                                APZAppDelegateUtility.shared.appDelegate?.pushTitle,
                                APZAppDelegateUtility.shared.appDelegate?.pushSubTitle,
                                APZAppDelegateUtility.shared.appDelegate?.pushImgURL,
                                APZAppDelegateUtility.shared.appDelegate?.pushCategory,
                                APZAppDelegateUtility.shared.appDelegate?.pushActionType,
                                APZAppDelegateUtility.shared.appDelegate?.pushParams, "notification"]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: true,
                                                                        responseKeys: resultKeys,
                                                                        responseValues: resultValues as [Any])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
//            self.addNotification(toDB: APZAppDelegateUtility.shared.appDelegate?.pushMessege)
            if let message = APZAppDelegateUtility.shared.appDelegate?.pushMessege {
            addNotificationToDB(message: message)
            }
            APZAppDelegateUtility.shared.appDelegate?.pushMessege = StringConstants.Generic.emptyString
            APZAppDelegateUtility.shared.appDelegate?.notificationCenter = StringConstants.Generic.emptyString
            APZAppDelegateUtility.shared.appDelegate?.pushCategory = StringConstants.Generic.emptyString
            APZAppDelegateUtility.shared.appDelegate?.pushActionType = StringConstants.Generic.emptyString
            APZAppDelegateUtility.shared.appDelegate?.pushParams = StringConstants.Generic.emptyString
            APZAppDelegateUtility.shared.appDelegate?.pushTitle = StringConstants.Generic.emptyString
            APZAppDelegateUtility.shared.appDelegate?.pushSubTitle = StringConstants.Generic.emptyString
            APZAppDelegateUtility.shared.appDelegate?.pushImgURL = StringConstants.Generic.emptyString
        }
    }
        // MARK: Shortcut listner Callback Method
         func shortcutActionCallback() {
            if let shortcutDict = self.shortcutItemDictionary, !shortcutDict.isEmpty {
                let pluginId = shortcutDict[StringConstants.Generic.pluginId] as?
                String ?? StringConstants.Generic.emptyString
                let resultKeys = ["shortcutID", StringConstants.Generic.cbEvent]
                let resultValues = [APZAppDelegateUtility.shared.appDelegate?.shortcutID, "shortcutItem"]
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: true,
                                                                            keepAlive: true,
                                                                            responseKeys: resultKeys,
                                                                            responseValues: resultValues as [Any])
                MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
                APZAppDelegateUtility.shared.appDelegate?.shortcutID = StringConstants.Generic.emptyString
                APZAppDelegateUtility.shared.appDelegate?.shortcutTitle = StringConstants.Generic.emptyString
                APZAppDelegateUtility.shared.appDelegate?.shortcutSubTitle = StringConstants.Generic.emptyString
            }
        }
     func addNotificationToDB(message: String) {
         let mainAppId =
         APZAppDelegateUtility.shared.appDelegate?.containerPropsDictionary[StringConstants.Generic.mainAppId]
         as? String ?? StringConstants.Generic.emptyString
         let dbPath = String(format: "%@/Assets/apps/%@/sqlite/APPSDB.sqlite3", sandboxPath, mainAppId)
         var dbRef: OpaquePointer?
         if sqlite3_open(dbPath, &dbRef) == SQLITE_OK {
             let currentDate = Date()
             let dFormatter = DateFormatter()
             dFormatter.dateFormat = "hh:mm a"
             var insertToTable: OpaquePointer?
             let encryptedMessage = CryptoSwiftManager.shared.encryptPlainString(key: StringConstants.Generic.key, value: message)
             let query = String(format: StringConstants.AddNotificationDB.insertQuery,
                                encryptedMessage,
                                dFormatter.string(from: currentDate))
             sqlite3_prepare_v2(dbRef, query.cString(using: .utf8), -1, &insertToTable, nil)
             if sqlite3_step(insertToTable) == SQLITE_DONE {
                 sqlite3_close(dbRef)
             } else {
                 print("----error in creating table tb_notifications")
             }
         } else {
             print("----error in Opening notification DB")
         }
     }
    
    func universalLinkCallback() {
        if universalLinkDictionary != nil {
            let appDelegate = APZAppDelegateUtility.shared.appDelegate
            if appDelegate?.universalLinkURLString ==  StringConstants.Generic.emptyString {
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: self.universalLinkDictionary["id"] as? String ?? StringConstants.Generic.emptyString,
                                                                            status: false,
                                                                            keepAlive: false,
                                                                            responseKeys: ["universalLink"],
                                                                            responseValues: [NSNumber(value: false)])
                MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
            } else {
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: self.universalLinkDictionary["id"] as? String ?? StringConstants.Generic.emptyString,
                                                                            status: true,
                                                                            keepAlive: false,
                                                                            responseKeys: ["universalLink", "refNoURL"],
                                                                            responseValues: [NSNumber(value: true), appDelegate?.universalLinkURLString ?? StringConstants.Generic.emptyString])
                MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
                appDelegate?.universalLinkURLString = StringConstants.Generic.emptyString

            }
        }
    }

    func sendUniversalLinkCallback (jsonDict: [AnyHashable: Any]) {
        if universalLinkDictionary != nil {
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: self.universalLinkDictionary["id"] as? String ?? StringConstants.Generic.emptyString,
                                                                        status: true,
                                                                        keepAlive: true,
                                                                        responseKeys: ["event", "universalDeepLinkResponse"],
                                                                        responseValues: ["universalDeepLink", jsonDict])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
    }
}
// swiftlint:enable` all
