// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

extension AppzillonAppDelegate {
    // MARK: getMainApp method
     func getMainApp() {
        if UIDevice.current.userInterfaceIdiom == .pad {
            self.isiPad = true
        }
        self.viewController = AppzillonViewController(nibName: "Azuritei_phoneViewController", bundle: nil)
    }
    func initializeWindowForAlert() {
        self.window = UIWindow(frame: UIScreen.main.bounds)
        self.window?.makeKeyAndVisible()
        self.window?.rootViewController = UIViewController()
    }
    func performAppSetUpOperations(application: UIApplication,
                                   launchOptions: [AnyHashable: Any]) {
        containerPropsDictionary = APZAppDelegateUtility.shared.getContainerPropsDict()
        if let wipeOutStatus = APZUserDefaults.get("wipedOut") as? String, wipeOutStatus == "YES"
        || isAppExpired {
            initializeWindowForAlert()
            APZAppDelegateUtility.shared.showAlert(message: "Application has been Disabled, Reinstall the Application",
                                                   window: self.window!)
        } else {
            if let notificationStatus = containerPropsDictionary["NOTIFICATION"] as? String,
                notificationStatus == StringConstants.Generic.yes {
                self.checkIfNotifcationRecieved(application: application, launchOptions: launchOptions)
            }
            let mainAppId = containerPropsDictionary["MAINAPPID"] as? String ?? StringConstants.Generic.emptyString
            appPath = FileManagerUtility.documentDirectory().appendingPathComponent("/Assets/apps/\(mainAppId)").path
            if !MiscellaneousMethod.shared.getAppHasLaunchedBefore(mainAppId: mainAppId) {
                let otaRequired = containerPropsDictionary["OTAREQUIRED"] as? String ??
                StringConstants.Generic.emptyString
                APZAppDelegateUtility.shared.copyAppzAppFiles(otaRequired)
                createPushNotificationTable()
                let runTimeDictPath = String(format: "%@/Assets/apps/%@/plist/Container.plist",
                                             FileManagerUtility.documentDirectory().path,
                                             mainAppId)
                if FileManager.default.fileExists(atPath: runTimeDictPath),
                   var runtimeDict = NSDictionary(contentsOfFile: runTimeDictPath) as? [AnyHashable: Any] {
                    runtimeDict["PLISTENCRYPTKEY"] =
                    CryptoSwiftManager.shared .encryptPlainString(key: "PLISTENCRYPTKEY",
                                                                  value: KeychainUtility.shared.generateRandomString())
                     _ = (runtimeDict as NSDictionary).write(toFile: runTimeDictPath, atomically: true)
                }
                // AppVersion check from the Appzillon-Info.plist(Bundle)
                let infoDictionary = MiscellaneousMethod.shared.getAppBundle().infoDictionary ?? [:]
                let storedBundleVersion = infoDictionary["CFBundleVersion"] as? String ??
                StringConstants.Generic.emptyString
                let storedBundleShortVersion = infoDictionary["CFBundleShortVersionString"] as? String ??
                StringConstants.Generic.emptyString
                APZUserDefaults.set(storedBundleVersion, forKey: "storedBundleVersion")
                APZUserDefaults.set(storedBundleShortVersion, forKey: "storedBundleShortVersion")
                APZUserDefaults.set("N", forKey: "updateAppVersion")
            } else {
                if MiscellaneousMethod.shared.getAppHasLaunchedOnOlderVersion() {
//                    let encryptKey = KeychainUtility.shared.generateRandomString()
//                    let _ = KeychainUtility.shared.setDataToKeychain(key: "PLISTENCRYPTKEY", value: encryptKey)
                }
                APZAppDelegateUtility.shared.plistKeyManagementForOlderVersion()
                APZAppDelegateUtility.shared.storeValueToPlist()
            }
            // For App Flow changes
            APZUserDefaults.set(CryptoSwiftManager.shared.encryptSingleValue(value: "N"), forKey: "ON_APP_LOADED")
            self.window = UIWindow(frame: UIScreen.main.bounds)
            isiPad = false
            self.getMainApp()
            self.window?.rootViewController = self.viewController
            self.window?.makeKeyAndVisible()
        }
    }
    func createPushNotificationTable() {
        let mainAppId = containerPropsDictionary["MAINAPPID"] as? String ??
        StringConstants.Generic.emptyString
        let docDirectory = FileManagerUtility.documentDirectory()
        let appzillonAppDocumentDirectory =
        docDirectory.appendingPathComponent("Assets/apps/\(mainAppId)/sqlite/APPSDB.sqlite3").path
        var dbRef: OpaquePointer?
        if sqlite3_open(appzillonAppDocumentDirectory.cString(using: .utf8), &dbRef) == SQLITE_OK {
            var createTable: OpaquePointer?
            let query = "CREATE TABLE tb_notifications(id integer primary key autoincrement, message varchar(500),timeStamp varchar(8),readFlag varchar(1))"
            sqlite3_prepare_v2(dbRef, query.cString(using: .utf8), -1, &createTable, nil)
            if sqlite3_step(createTable) == SQLITE_DONE {
                sqlite3_close(dbRef)
            } else {
                print("----error in creating table tb_notifications")
            }
        } else {
            print("----error in creating notification DB")
        }
    }
}
