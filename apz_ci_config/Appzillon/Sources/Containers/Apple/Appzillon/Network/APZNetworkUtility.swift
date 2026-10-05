// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit

class APZNetworkUtility: NSObject {
    @objc static let shared = APZNetworkUtility()
    private override init() {
        //do nothing
    }
    var networkAlert: UIAlertController?
    // MARK: Objc Methods
    @objc func getAppPropertiesDictionaryPath(appID: String) -> String {
        let inAppPlistFilePath = String(format: "Assets/apps/%@/plist/AppProperties.plist", appID)
        let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let documentsDirectoryPath = urls[0]
        return documentsDirectoryPath.appendingPathComponent(inAppPlistFilePath).path
    }
    @objc func getCurrentDateTime() -> String {
        let timeInMiliseconds = Int64(Date().timeIntervalSince1970 * 1000)
        let currentTime = String(format: "%ld", timeInMiliseconds)
        return currentTime
    }
    @objc func getUpdatedAppzillonHeader(_ requestDict: NSMutableDictionary, clientNonce: String,
                                         apzViewController: AppzillonViewController) -> NSMutableDictionary {
        var appzillonHeader = requestDict["appzillonHeader"] as? NSMutableDictionary ?? [:]
        if let serverNonce = apzViewController.apzServerNonce {
            appzillonHeader["serverNonce"] = serverNonce
            appzillonHeader["sessionToken"] = apzViewController.apzSessionToken
            appzillonHeader["clientNonce"] = clientNonce
        }
        return appzillonHeader
    }
    @objc func getStringObject(content: Any) -> String {
        var convertedString = StringConstants.Generic.emptyString
        if content is String || content is NSString {
            convertedString = content as? String ?? StringConstants.Generic.emptyString
        } else {
            if let dictData = try? JSONSerialization.data(withJSONObject: content, options: []) {
                convertedString = String(data: dictData, encoding: .utf8) ?? StringConstants.Generic.emptyString
            }
        }
        return convertedString
    }
    @objc func nativeAlertRequired(_ interfaceId: String) -> Bool {
        let startUpServerCallList = ["appzillonGetAppSecTokens", "appzillonOnAppLaunch",
                                     "appzillonDeviceRegistration", "appzillonGetAppMasterDetails",
                                     "appzillonNotificationRegistration"]
        return startUpServerCallList.contains(interfaceId)
    }
    @objc func showAlertMessage(_ isReachable: Bool, interfaceId: String) {
        if let containerPropsPath = MiscellaneousMethod.shared.getAppBundle().path(forResource: "containerprops",
                                                                                   ofType: "plist"),
           let containerPropsDictionary = NSDictionary(contentsOfFile: containerPropsPath){
            let offlineSupport = containerPropsDictionary["OFFLINESUPPORT"] as? String ?? "N"
            let appOfflineSupport = containerPropsDictionary["APPOFFLINESUPPORT"] as? String ?? "N"
            let isOfflineSupported =  (offlineSupport == StringConstants.Generic.yes) ||
            (offlineSupport == StringConstants.WebView.lowercaseY)
            let isAppOfflineSupported =  (appOfflineSupport == StringConstants.Generic.yes) ||
            (appOfflineSupport == StringConstants.WebView.lowercaseY)
            if(isOfflineSupported || isAppOfflineSupported){
                return
            }
             if let networkAlertController = networkAlert {
                 networkAlertController.dismiss(animated: true, completion: nil)
                 networkAlert = nil
             }
             let window = UIWindow(frame: UIScreen.main.bounds)
             window.rootViewController = UIViewController()
             window.windowLevel = UIWindow.Level.alert + 1
             if (isConnectedToNetwork() && interfaceId == "appzillonGetAppSecTokens")
                 || (isConnectedToNetwork() && interfaceId == "appzillonOnAppLaunch") {
                 networkAlert = getUnableToConnectServerAlertController()
             } else {
                 networkAlert = UIAlertController(title: "Network Error",
                                                  message: "No internet connection found",
                                                  preferredStyle: .alert)
             }
             networkAlert?.addAction(UIAlertAction(title: "OK",
                                                   style: .cancel, handler: { _ in
                 window.isHidden = true
             }))
             window.makeKeyAndVisible()
             window.rootViewController?.present(networkAlert!, animated: true, completion: nil)
        }
    }
    private func getUnableToConnectServerAlertController() -> UIAlertController {
        if let containerPropsPath = MiscellaneousMethod.shared.getAppBundle().path(forResource: "containerprops",
                                                                                   ofType: "plist"),
           let containerPropsDictionary = NSDictionary(contentsOfFile: containerPropsPath),
           let alertTitle = containerPropsDictionary["SERVERCONNECTFAILURETITLE"] as? String,
           let alertMsg = containerPropsDictionary["SERVERCONNECTFAILUREMESSAGE"] as? String, !alertMsg.isEmpty {
            return UIAlertController(title: alertTitle,
                                     message: alertMsg,
                                     preferredStyle: .alert)
        } else {
            // deafult, if not customised in containerProps.plist
            return UIAlertController(title: "Network Error",
                                     message: "Unable to connect to the server. Retrying in 5 seconds.",
                                     preferredStyle: .alert)
        }
    }
    @objc func stringByRemovingUnwantedChars(_ stringWithChars: String) -> NSMutableString {
        return NSMutableString(string: stringWithChars)
    }
    @objc func getHashForJSONString(jsonString: String, hashedPin: String) -> String {
        if let jsondata = jsonString.data(using: .utf8) {
            let jsonStringBase64 = Base64.encode(jsondata)
            let hashedPayload = HashUtility.genHash(jsonStringBase64, hashedPin)
            return hashedPayload ?? StringConstants.Generic.emptyString
        }
        return StringConstants.Generic.emptyString
    }
    @objc func createHttpHeader(request: NSMutableURLRequest, httpMethod: String) -> NSMutableURLRequest {
        request.httpMethod = httpMethod
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        return request
    }
    @objc func getRandomString() -> String {
        let uuid = UUID().uuidString
        return uuid.replacingOccurrences(of: "-", with: StringConstants.Generic.emptyString)
    }

    // MARK: Swift Invocation Methods
    func loadCertificates(appID: String) -> [SecCertificate] {
        let inAppPath = String(format: "/Assets/apps/%@/sslCertificates", appID)
        let certDirectory = MiscellaneousMethod.shared.getAppBundle().bundleURL.appendingPathComponent(inAppPath)
        do {
            let certArray = try FileManager.default.contentsOfDirectory(atPath: certDirectory.path)
            //var certDataArray: [Data] = []
            var secCertificateArray: [SecCertificate] = []
            if !certArray.isEmpty {
                for fileName in certArray {
                    if let fileData = try? Data(contentsOf: certDirectory.appendingPathComponent(fileName)) as CFData,
                       let cert = SecCertificateCreateWithData(nil, fileData) {
                        secCertificateArray.append(cert)
                    }
                }
            }
            return secCertificateArray
        }
        catch {
            print(error.localizedDescription)
            return []
        }
    }
    func loadConfiguration() -> URLSessionConfiguration {
        let config = URLSessionConfiguration.default
        config.timeoutIntervalForRequest = Double(CONTAINERTIMEOUT)
        if #available(iOS 14.0, *) {
            config.waitsForConnectivity = true
            config.timeoutIntervalForResource = Double(CONTAINERTIMEOUT)
        }
        return config
    }
    @objc func isConnectedToNetwork() -> Bool {
        do {
        let reachability = try APZReachability()
        return reachability.connection != .unavailable
        } catch let error {
            APZLogger.log(logLvl: "E", message: error.localizedDescription)
        }
        return false
    }
    @objc func mimeTypeForFile(path: String) -> String {
        if let url = URL(string: path) {
            let pathExtension = url.pathExtension
            if let uti = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, pathExtension as NSString, nil)?.takeRetainedValue(), let mimetype = UTTypeCopyPreferredTagWithClass(uti, kUTTagClassMIMEType)?.takeRetainedValue() {
                return mimetype as String
            }
        }
        return "application/octet-stream"
    }
}
