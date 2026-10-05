// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit
class APZAppDelegateUtility: NSObject {
    @objc static let shared = APZAppDelegateUtility()
    private override init() {
        //do nothing
    }
    let fileManager = FileManager.default
    @objc var appDelegate: AppzillonAppDelegate? {
            UIApplication.shared.delegate as? AppzillonAppDelegate
     }

     func getContainerPropsDict() -> NSDictionary {
        if let path = Bundle.main.path(forResource: StringConstants.Generic.containerProps,
                                       ofType: StringConstants.Generic.plist),
           let plistDict = NSDictionary(contentsOfFile: path) {
            return plistDict
        }
        return [:]
    }
     func showAlert(message: String, window: UIWindow) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: UIAlertController.Style.alert)
         alert.addAction(UIAlertAction(title: StringConstants.Generic.ucOk,
                                       style: UIAlertAction.Style.default,
                                       handler: nil))
        window.rootViewController?.present(alert, animated: true, completion: nil)
    }
    // MARK: Copy appzillon app files into app sandbox
     func copyAppzAppFiles(_ otaStatus: String) {
        let sandBoxAssetsFolder = FileManagerUtility.documentDirectory().appendingPathComponent("Assets/").path
        if let resourcePath = MiscellaneousMethod.shared.getAppBundle().resourcePath {
            let bundlePathToAssetFolder = resourcePath.appending("/Assets")
            loop(destination: sandBoxAssetsFolder, source: bundlePathToAssetFolder, otaStatus: otaStatus)
            do {
                let listOfApps = try fileManager.contentsOfDirectory(atPath: bundlePathToAssetFolder.appending("/apps"))
                for appId in listOfApps {
                    copyUsersFiles(appString: appId)
                }
            } catch let error {
                APZLogger.log(logLvl: "E", message: error.localizedDescription)
            }
        }
    }
     func loop(destination: String, source: String, otaStatus: String) {
        var isDir: ObjCBool = false
        // in case OTA is disabled do not copy content from these folders to sandbox
        let nonOTAExceptionPath = ["screens", "scripts", "styles", "appzillon", "staticfiles", "sslCertificates"]
        // in case new updates are availabe do not copy folders below to sandbox
        let onUpdateExceptionPath = ["sqlite", "plist"]
        do {
            let sourceContent = try fileManager.contentsOfDirectory(atPath: source)
            for sourceElement in sourceContent {
                let destinationAppended = destination.appendingFormat("%@%@", "/", sourceElement)
                let sourceAppended = source.appendingFormat("%@%@", "/", sourceElement)
                if fileManager.fileExists(atPath: sourceAppended, isDirectory: &isDir) {
                    if isDir.boolValue {
                        if sourceElement == StringConstants.AppDelegateUtility.staticfiles {
                            continue
                        }
                        if let mainAppID = getContainerPropsDict()[StringConstants.Generic.mainAppId] as? String,
                           MiscellaneousMethod.shared.getAppHasLaunchedBefore(mainAppId: mainAppID),
                           onUpdateExceptionPath.contains(sourceElement) {
                            continue
                        }
                        if otaStatus == StringConstants.Generic.no,
                           nonOTAExceptionPath.contains(sourceElement) {
                            continue
                        }
                        loop(destination: destinationAppended,
                             source: sourceAppended, otaStatus: otaStatus)
                    } else {
                        do {
                            if fileManager.fileExists(atPath: destinationAppended) {
                                try fileManager.removeItem(atPath: destinationAppended)
                            }
                            try fileManager.createDirectory(atPath: destination,
                                                            withIntermediateDirectories: true,
                                                            attributes: nil)
                            try fileManager.copyItem(atPath: sourceAppended, toPath: destinationAppended)
                        } catch let error {
                            APZLogger.log(logLvl: "E", message: error.localizedDescription)
                        }
                    }

                }
            }
        } catch let error {
            APZLogger.log(logLvl: "E", message: error.localizedDescription)
        }
    }
     func copyUsersFiles(appString: String) {
        let domainPath = String(format: "/Assets/apps/%@/screens/config/", appString)
         let appzillonAppDocumentDirectory =
         FileManagerUtility.documentDirectory().appendingPathComponent("Assets/apps/\(appString)/")
        let staticFilePath = String(format: "/Assets/apps/%@/staticfiles", appString)
        let docsPath = MiscellaneousMethod.shared.getAppBundle().paths(forResourcesOfType: nil,
                                                                       inDirectory: staticFilePath)
         let files = StringConstants.AppDelegateUtility.files
        let pathSettingsJson = MiscellaneousMethod.shared.getAppBundle().path(forResource: files,
                                                                              ofType: StringConstants.Generic.json,
                                                                              inDirectory: domainPath)
        if let pathSettingsJson = pathSettingsJson, fileManager.fileExists(atPath: pathSettingsJson) {
            do {
                let fileContent = try String(contentsOfFile: pathSettingsJson, encoding: .utf8)
                if let data = fileContent.data(using: .utf8),
                   let jsonDict = try JSONSerialization.jsonObject(with: data,
                                                                   options: .mutableContainers) as? [String: Any],
                   let fileAndPathDict = jsonDict[StringConstants.AppDelegateUtility.files] as? [String: Any] {
                    for fileName in fileAndPathDict.keys {
                        let pathFromJson = fileAndPathDict[fileName] as? String ?? StringConstants.Generic.emptyString
                        let actualFolder = appzillonAppDocumentDirectory.appendingPathComponent(pathFromJson)
                        if !(fileManager.fileExists(atPath: actualFolder.path)) {
                            try fileManager.createDirectory(atPath: actualFolder.path,
                                                            withIntermediateDirectories: true,
                                                            attributes: nil)
                        }
                        for index in 0..<docsPath.count {
                            if let properUrl =
                                docsPath[index].addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed),
                               let fileNameStatic = URL(string: properUrl)?.lastPathComponent,
                               fileName == fileNameStatic {
                                let dataPath = docsPath[index]
                                let actualPath = actualFolder.appendingPathComponent(fileName)
                                if fileManager.fileExists(atPath: actualPath.path) {
                                    try fileManager.removeItem(atPath: actualPath.path)
                                }
                                try fileManager.copyItem(atPath: dataPath, toPath: actualPath.path)
                            }
                        }
                    }
                }
            } catch let error {
                APZLogger.log(logLvl: "E", message: error.localizedDescription)
            }
        }
    }
    // MARK: PlistKey Management
    func plistKeyManagementForOlderVersion() {
        let containerPropDict = getContainerPropsDict()
        if let infoDictionary = MiscellaneousMethod.shared.getAppBundle().infoDictionary {
            let currentBundleShortVersion = infoDictionary["CFBundleShortVersionString"] as? String
            let currentBundleVersion = infoDictionary["CFBundleVersion"] as? String
            let oldBundleVersion = UserDefaults.standard.object(forKey: "storedBundleVersion") as? String
            let oldBundleShortVersion = UserDefaults.standard.object(forKey: "storedBundleShortVersion") as? String
            if (oldBundleVersion != currentBundleVersion) || (oldBundleShortVersion != currentBundleShortVersion),
                let mainAppID = containerPropDict[StringConstants.Generic.mainAppId] as? String {
                 let documentDirectory = FileManagerUtility.documentDirectory().path
                 let containerPlistPath = "%@/Assets/apps/%@/plist/Container.plist"
                 let runTimeDictPath = String(format: containerPlistPath, documentDirectory, mainAppID)
                 if fileManager.fileExists(atPath: runTimeDictPath),
                    var runtimeDict = NSDictionary(contentsOfFile: runTimeDictPath) as? [AnyHashable: Any],
                     let encKeyCipher = runtimeDict["PLISTENCRYPTKEY"] as? String {
                     let encKey = CryptoSwiftManager.shared.decryptCipherString(key: "PLISTENCRYPTKEY", value: encKeyCipher)
                     let encryptKeyInvalid = encKey.isEmpty
                     if runtimeDict["PLISTENCRYPTKEY"] == nil {
                         let generateRandomStr = KeychainUtility.shared.generateRandomString()
                         let plistKey = CryptoSwiftManager.shared.encryptPlainString(key: "PLISTENCRYPTKEY", value: generateRandomStr)
                         runtimeDict["PLISTENCRYPTKEY"] = plistKey
                          _ = (runtimeDict as NSDictionary).write(toFile: runTimeDictPath, atomically: true)
                     } else if runtimeDict["PLISTENCRYPTKEY"] != nil && encryptKeyInvalid {
                         let randomString = KeychainUtility.shared.generateRandomString()
                         let plistKey = CryptoSwiftManager.shared.encryptPlainString(key: "PLISTENCRYPTKEY", value: randomString)
                         runtimeDict["PLISTENCRYPTKEY"] = plistKey
                          _ = (runtimeDict as NSDictionary).write(toFile: runTimeDictPath, atomically: true)
                     }
                 }
             }
         }
     }

     // MARK: Storevalue to plsit
    fileprivate func writeToAppProps(_ settingsPath: String, _ mainAppID: String) {
        do {
            let fileContent = try  String.init(contentsOfFile: settingsPath, encoding: String.Encoding.utf8)
            if let data = fileContent.data(using: .utf8) ,
               let appPropsDict = try JSONSerialization.jsonObject(with: data,
                                                                   options: .mutableContainers)
                as? [String: Any],
               let encryptedAppPropsDict = CryptoSwiftManager.shared.encryptPlistData(dictionary: appPropsDict) as? NSDictionary {
                let appPropertiesPath = String(format: "%@/Assets/apps/%@/plist/AppProperties.plist",
                                               FileManagerUtility.documentDirectory().path,
                                               mainAppID)
                encryptedAppPropsDict.write(toFile: appPropertiesPath, atomically: true)
            }
        } catch let error {
            APZLogger.log(logLvl: "E", message: error.localizedDescription)
        }
    }
     func storeValueToPlist() {
         let containerPropDict = getContainerPropsDict()
         if let infoDictionary = MiscellaneousMethod.shared.getAppBundle().infoDictionary {
             let currentBundleShortVersion = infoDictionary["CFBundleShortVersionString"] as? String
             let currentBundleVersion = infoDictionary["CFBundleVersion"] as? String
             let oldBundleVersion = UserDefaults.standard.object(forKey: "storedBundleVersion") as? String
             let oldBundleShortVersion = UserDefaults.standard.object(forKey: "storedBundleShortVersion") as? String
             if (oldBundleVersion != currentBundleVersion) || (oldBundleShortVersion != currentBundleShortVersion),
                let mainAppID = containerPropDict[StringConstants.Generic.mainAppId] as? String {
                 if MiscellaneousMethod.shared.getAppHasLaunchedBefore(mainAppId: mainAppID),
                    let otaStatus = containerPropDict[StringConstants.Generic.otaRequired] as? String {
                     copyAppzAppFiles(otaStatus)
                 }
                 let path = String(format: "Assets/apps/%@/screens/config", mainAppID)
                 let settingsPath = MiscellaneousMethod.shared.getAppBundle().path(forResource: "appprops",
                                                                                   ofType: "json",
                                                                                   inDirectory: path)
                 if let settingsPath = settingsPath, fileManager.fileExists(atPath: settingsPath) {
                     writeToAppProps(settingsPath, mainAppID)
                 }
                 UserDefaults.standard.set(currentBundleVersion, forKey: "storedBundleVersion")
                 UserDefaults.standard.set(currentBundleShortVersion, forKey: "storedBundleShortVersion")
                 UserDefaults.standard.set("Y", forKey: "updateAppVersion")
                 UserDefaults.standard.synchronize()
             } else {
                 UserDefaults.standard.set("N", forKey: "updateAppVersion")
                 UserDefaults.standard.synchronize()
             }
         }
     }
    // MARK: applicationDidEnterBackground functionality
    func performBackgroundTask(viewController: AppzillonViewController, application: UIApplication) {
        var bgTask: UIBackgroundTaskIdentifier = .invalid
        bgTask = application.beginBackgroundTask {
            application.endBackgroundTask(bgTask)
            bgTask = UIBackgroundTaskIdentifier.invalid
        }
        DispatchQueue.global(qos: .default).async {
            do {
                let tempDir = NSTemporaryDirectory()
                let tempFileArr = try FileManager.default.contentsOfDirectory(atPath: tempDir)
                for filePath in tempFileArr {
                    try FileManager.default.removeItem(atPath: tempDir + filePath)
                }
            } catch let error {
                print(error.localizedDescription)
            }
        }
  }
    func disableSnapshot(viewController: AppzillonViewController) {
        let containerPropDict = getContainerPropsDict()
        if let preventScreenShot = containerPropDict["PREVENTSCREENSHOT"] as? String,
           preventScreenShot == StringConstants.Generic.yes,
           viewController.view.window != nil {
            viewController.hideScreenInBackGround()
        }
    }
    
    func setShortCutItems() {
        //if shortcut items are already set, do nothing
        if let shortcutItems = UIApplication.shared.shortcutItems, !shortcutItems.isEmpty {
            return
        }
        let containerPropDict = APZAppDelegateUtility.shared.getContainerPropsDict()
        if let appID = containerPropDict[StringConstants.Generic.mainAppId] as? String,
           let path = MiscellaneousMethod.shared.getAppBundle().path(forResource: "appprops", ofType: "json", inDirectory: "Assets/apps/\(appID)/screens/config"), FileManager.default.fileExists(atPath: path) {
                do {
                    let fileContent = try String.init(contentsOfFile: path, encoding: .utf8)
                    if let data = fileContent.data(using: .utf8),
                       let jsonDict = try JSONSerialization.jsonObject(with: data, options: .mutableContainers) as? [String: Any],
                       let shortcutItemsArray = jsonDict["shortcutItems"] as? [[String: String]], !shortcutItemsArray.isEmpty {
                        var shortCutItems: [UIApplicationShortcutItem] = []
                        for item in shortcutItemsArray {
                            let id = item["shortcutID"] ?? StringConstants.Generic.emptyString
                            let title = item["title"] ?? StringConstants.Generic.emptyString
                            let subtitle = item["subtitle"] ?? StringConstants.Generic.emptyString
                            let iconfilename = item["iconfilename"] ?? StringConstants.Generic.emptyString
                            let shortCutitem = UIApplicationShortcutItem(type: id, localizedTitle: title, localizedSubtitle: subtitle, icon: UIApplicationShortcutIcon(templateImageName: iconfilename))
                            shortCutItems.append(shortCutitem)
                        }
                        UIApplication.shared.shortcutItems = shortCutItems
                    }
                    
                } catch let error {
                    print(error.localizedDescription)
                }
        }
    }
}
