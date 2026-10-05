// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit
import UserNotifications

class AppzillonAppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?
    @objc var viewController: AppzillonViewController?
    @objc var containerPropsDictionary: NSDictionary = [:]
    var isAppActive = false
    var isAppExpired = false
    var isiPad = false
    var regComplete: String = StringConstants.Generic.emptyString
    @objc var regID: String = StringConstants.Generic.emptyString
    var shortcutID: String = StringConstants.Generic.emptyString
    var shortcutTitle: String = StringConstants.Generic.emptyString
    var shortcutSubTitle: String = StringConstants.Generic.emptyString
    var pushTitle: String = StringConstants.Generic.emptyString
    var pushSubTitle: String = StringConstants.Generic.emptyString
    @objc var pushMessege: String = StringConstants.Generic.emptyString
    var pushImgURL: String = StringConstants.Generic.emptyString
    var pushCategory: String = StringConstants.Generic.emptyString
    var pushParams: String = StringConstants.Generic.emptyString
    var pushActionType: String = StringConstants.Generic.emptyString
    var notificationCenter: String = StringConstants.Generic.emptyString
    var appPath: String = StringConstants.Generic.emptyString
    @objc var appIDNotes: String = StringConstants.Generic.emptyString
    var universalLinkURLString: String = StringConstants.Generic.emptyString
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions
                     launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil) -> Bool {
        containerPropsDictionary = APZAppDelegateUtility.shared.getContainerPropsDict()
        #if TARGET_IPHONE_SIMULATOR
        if let buildMode = containerPropsDictionary["APZBUILDMODE"] as? String, buildMode == "RELEASE" {
            self.initializeWindowForAlert()
            APZAppDelegateUtility.shared.showAlert(message: "App is not supported on Simulator.", window: self.window!)
            return false
        }
        #endif
        
        // changes for universalDeeplink plugin
        if let activityDictionary = launchOptions?[UIApplication.LaunchOptionsKey.userActivityDictionary] as? [AnyHashable : Any],
           let activity = activityDictionary["UIApplicationLaunchOptionsUserActivityKey"] as? NSUserActivity,
           activity.activityType.isEqual(NSUserActivityTypeBrowsingWeb),
           let webpageURL = activity.webpageURL {
            universalLinkURLString = webpageURL.absoluteString
        }
       isAppActive = true
        if APZUserDefaults.get("FirstRun") == nil {
            // Delete values from keychain here
            KeychainUtility.shared.removeDataFromKeychain()
            APZUserDefaults.set("1strun", forKey: "FirstRun")
        }
        // jail broken device condition
        if APZJBDetector().checkForJailBrokenDevice() {
            self.initializeWindowForAlert()
            APZAppDelegateUtility.shared.showAlert(message: "This Device is jail broken. Application cannot be run",
                                                   window: self.window!)
            return false
        }
        self.performAppSetUpOperations(application: application, launchOptions: launchOptions ?? [:])
        APZAppDelegateUtility.shared.setShortCutItems()
        let _ = KeychainUtility.shared.setDataToKeychainWithBiometric(key: StringConstants.SecureStorage.isBiometricNeeded+"APZAuthValue",
                                                              value: StringConstants.Generic.yes)
        return true
    }
    func applicationWillResignActive(_ application: UIApplication) {
        if let viewController = self.viewController {
            APZAppDelegateUtility.shared.disableSnapshot(viewController: viewController)
        }
    }
    func applicationDidBecomeActive(_ application: UIApplication) {
        if self.viewController?.view.window != nil {
            self.viewController?.revealTheScreen()
        }
    }
    func applicationDidEnterBackground(_ application: UIApplication) {
        APZAppDelegateUtility.shared.performBackgroundTask(viewController: self.viewController!,
                                                           application: application)
    }
    func applicationWillEnterForeground(_ application: UIApplication) {
        DispatchQueue.main.async {
            if !self.pushMessege.isEmpty {
                self.viewController?.notifCallback()
            }
        }
    }
    func application(_ application: UIApplication,
                     performActionFor shortcutItem: UIApplicationShortcutItem,
                     completionHandler: @escaping (Bool) -> Void) {
        print(String(format: "performActionForShortcutItem = %@, %@, %@",
                     shortcutItem.type, shortcutItem.localizedTitle,
                     shortcutItem.localizedSubtitle ?? StringConstants.Generic.emptyString))
        self.shortcutID = shortcutItem.type
        self.shortcutTitle = shortcutItem.localizedTitle
        self.shortcutSubTitle = shortcutItem.localizedSubtitle ?? StringConstants.Generic.emptyString
        self.viewController?.shortcutActionCallback()
    }
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        regID = HashUtility.getHashString(from: deviceToken)
        if regComplete != "serverReg" {
            regComplete = "deviceReg"
        }
        self.viewController?.notificationRegWithToken()
    }
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        regComplete = "deviceReg"
        regID = ""
        // For new Appzillon request
        self.viewController?.notificationRegWithToken()
    }
    func application(_ application: UIApplication,
                     didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        if let aps = userInfo["aps"] as? [AnyHashable: Any], let alert = aps["alert"] as? [AnyHashable: Any] {
            pushTitle = alert["title"] as? String ?? StringConstants.Generic.emptyString
            pushSubTitle = alert["subtitle"] as? String ?? StringConstants.Generic.emptyString
            pushMessege = alert["body"] as? String ?? StringConstants.Generic.emptyString
            pushCategory = aps["category"] as? String ?? StringConstants.Generic.emptyString
            pushImgURL = userInfo["image_url"] as? String ?? StringConstants.Generic.emptyString
            pushParams = userInfo["message_param"] as? String ?? StringConstants.Generic.emptyString
        }
        DispatchQueue.main.async { [weak self] in
            if self?.notificationCenter != "fromCenter" {
                self?.viewController?.notifCallback()
            }
        }
        NotificationCenter.default.post(name: NSNotification.Name(rawValue: "updateRoot"),
                                        object: nil)
    }
    func application(_ application: UIApplication,
                     handleActionWithIdentifier identifier: String?,
                     forRemoteNotification userInfo: [AnyHashable: Any],
                     completionHandler: @escaping () -> Void) {
        pushActionType = identifier ?? StringConstants.Generic.emptyString
        if let aps = userInfo["aps"] as? [AnyHashable: Any], let alert = aps["alert"] as? [AnyHashable: Any] {
            pushTitle = alert["title"] as? String ?? StringConstants.Generic.emptyString
            pushSubTitle = alert["subtitle"] as? String ?? StringConstants.Generic.emptyString
            pushMessege = alert["body"] as? String ?? StringConstants.Generic.emptyString
            pushCategory = aps["category"] as? String ?? StringConstants.Generic.emptyString
            pushImgURL = userInfo["image_url"] as? String ?? StringConstants.Generic.emptyString
            pushParams = userInfo["message_param"] as? String ?? StringConstants.Generic.emptyString
        }
    }
    
    // Universal DeepLinking Delegate Method
    func application(_ application: UIApplication, continue userActivity:
                     NSUserActivity, restorationHandler: @escaping ([UIUserActivityRestoring]?) -> Void) -> Bool {
        if let webPageURL = userActivity.webpageURL, userActivity.activityType.isEqual(NSUserActivityTypeBrowsingWeb)  {
            handleUniversalLinkData(url: webPageURL)
            return true
        }
        return false
    }

    func handleUniversalLinkData(url: URL) {
        if let hostString = url.host {
            var responseDict: [AnyHashable : Any] = [:]
            let assosiatedDomainsArray = AppzillonMainUtility.shared.getAssosiatedDomains()
            if !assosiatedDomainsArray.isEmpty, assosiatedDomainsArray.contains(hostString)  {
                let components =  NSURLComponents(url: url, resolvingAgainstBaseURL: true)
                for item in components?.queryItems ?? [] {
                    responseDict[item.name] = item.value
                }
                viewController?.sendUniversalLinkCallback(jsonDict: responseDict)
            }
        }
    }
}
