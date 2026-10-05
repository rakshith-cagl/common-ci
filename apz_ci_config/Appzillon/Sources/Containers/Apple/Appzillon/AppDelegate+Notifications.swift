// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UserNotifications

extension AppzillonAppDelegate: UNUserNotificationCenterDelegate {

    public func userNotificationCenter(_ center: UNUserNotificationCenter,
                                       willPresent notification: UNNotification,
                                       withCompletionHandler
                                       completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        notificationCenter = "appFG"
        completionHandler([.sound, .alert, .badge])
    }
    public func userNotificationCenter(_ center: UNUserNotificationCenter,
                                       didReceive response: UNNotificationResponse,
                                       withCompletionHandler completionHandler: @escaping () -> Void) {
        pushActionType = response.actionIdentifier
        let userInfo = response.notification.request.content.userInfo
        if let aps = userInfo["aps"] as? [AnyHashable: Any], let alert = aps["alert"] as? [AnyHashable: Any] {
            pushTitle = alert["title"] as? String ?? StringConstants.Generic.emptyString
            pushSubTitle = alert["subtitle"] as? String ?? StringConstants.Generic.emptyString
            pushMessege = alert["body"] as? String ?? StringConstants.Generic.emptyString
            pushCategory = aps["category"] as? String ?? StringConstants.Generic.emptyString
            pushImgURL = userInfo["image_url"] as? String ?? StringConstants.Generic.emptyString
            pushParams = userInfo["message_param"] as? String ?? StringConstants.Generic.emptyString
            completionHandler()
        }
        DispatchQueue.main.async { [weak self] in
            if self?.notificationCenter == "appFG" {
                self?.viewController?.notifCallback()
            }
        }
    }
    public func checkIfNotifcationRecieved(application: UIApplication, launchOptions: [AnyHashable: Any]) {
        if let remoteNotif = launchOptions[UIApplication.LaunchOptionsKey.remoteNotification] as? [AnyHashable: Any],
            let aps = remoteNotif["aps"] as? [AnyHashable: Any], let alert = aps["alert"] as? [AnyHashable: Any] {
            pushTitle = alert["title"] as? String ?? StringConstants.Generic.emptyString
            pushSubTitle = alert["subtitle"] as? String ?? StringConstants.Generic.emptyString
            pushMessege = alert["body"] as? String ?? StringConstants.Generic.emptyString
            pushCategory = aps["category"] as? String ?? StringConstants.Generic.emptyString
            pushImgURL = remoteNotif["image_url"] as? String ?? StringConstants.Generic.emptyString
            pushParams = remoteNotif["message_param"] as? String ?? StringConstants.Generic.emptyString
            notificationCenter = "fromCenter"
            self.application(application,
                             didReceiveRemoteNotification: remoteNotif,
                             fetchCompletionHandler: { result in
                print("result = \(result)")
            })
        }
    }
}
