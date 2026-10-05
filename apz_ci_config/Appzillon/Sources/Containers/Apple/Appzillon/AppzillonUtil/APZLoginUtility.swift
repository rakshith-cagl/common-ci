//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZLoginUtility: NSObject {
    static func loginWithCredentials(jsonDict: [AnyHashable: Any], viewController: AppzillonViewController, webView: WKWebView) {
        let finalDict = NSMutableDictionary(dictionary: jsonDict)
        let dict = jsonDict["params"] as? NSMutableDictionary ?? [:]
        let reqFull = dict["reqFull"] as? NSMutableDictionary ?? [:]
        let appzillonBody = reqFull["appzillonBody"] as? NSMutableDictionary ?? [:]
        let appzillonHeader = reqFull["appzillonHeader"] as? NSMutableDictionary ?? [:]
        let loginRequest = appzillonBody["loginRequest"] as? NSMutableDictionary ?? [:]
        let userid = loginRequest["userId"] as? String ?? ""
        var pin = loginRequest["pwd"] as? String ?? ""
        let authenticationType = viewController.appPropertyDictionary["authenticationType"] as? String ?? ""
            
        if authenticationType == "#DeviceId" {
            let dateInStringFormated = loginRequest["sysDate"] as? String ?? ""
            let serverToken = viewController.apzServerToken ?? ""
            let salt = userid + serverToken
            pin = HashUtility.genHash(pin, salt)
            let otpVal = HashUtility.genHex(userid, pin, "", dateInStringFormated)
            loginRequest["pwd"] = otpVal
        } else {
            loginRequest["pwd"] = pin
        }
        appzillonBody["loginRequest"] = loginRequest
        reqFull["appzillonBody"] = appzillonBody
        
        appzillonHeader["userId"] = userid
        reqFull["appzillonHeader"] = appzillonHeader
        
        dict["reqFull"] = reqFull
        finalDict["params"] = dict
        CallServer.callServerFromInfra(withRequest: finalDict, viewController, webView, viewController.appString)
    }
    
    static func loginWithBiometric(jsonDict: [AnyHashable: Any], userId: String, userPin: String, viewController: AppzillonViewController, webView: WKWebView) {
        let finalDict = NSMutableDictionary(dictionary: jsonDict)
        let dict = jsonDict["params"] as? NSMutableDictionary ?? [:]
        let reqFull = dict["reqFull"] as? NSMutableDictionary ?? [:]
        let appzillonBody = reqFull["appzillonBody"] as? NSMutableDictionary ?? [:]
        let appzillonHeader = reqFull["appzillonHeader"] as? NSMutableDictionary ?? [:]
        let loginRequest = appzillonBody["loginRequest"] as? NSMutableDictionary ?? [:]
        loginRequest["userId"] = userId
        
        let authenticationType = viewController.appPropertyDictionary["authenticationType"] as? String ?? ""
        if authenticationType == "#DeviceId" {
            let dateInStringFormated = loginRequest["sysDate"] as? String ?? ""
            let serverToken = viewController.apzServerToken ?? ""
            let salt = userId + serverToken
            let pin = HashUtility.genHash(userPin, salt)
            let otpVal = HashUtility.genHex(userId, pin, "", dateInStringFormated)
            loginRequest["pwd"] = otpVal
        } else {
            loginRequest["pwd"] = userPin
        }
        appzillonBody["loginRequest"] = loginRequest
        reqFull["appzillonBody"] = appzillonBody
        
        appzillonHeader["userId"] = userId
        reqFull["appzillonHeader"] = appzillonHeader
        
        dict["reqFull"] = reqFull
        finalDict["params"] = dict
        CallServer.callServerFromInfra(withRequest: finalDict, viewController, webView, viewController.appString)
    }
    
    static func changePassword(jsonDict: [AnyHashable: Any], viewController: AppzillonViewController, webView: WKWebView) {
        let updatedRequest = NSMutableDictionary(dictionary: jsonDict)
        let dict = jsonDict["params"] as? NSMutableDictionary ?? [:]
        let reqFull = dict["reqFull"] as? NSMutableDictionary ?? [:]
        let appzillonBody = reqFull["appzillonBody"] as? NSMutableDictionary ?? [:]
        let appzillonHeader = reqFull["appzillonHeader"] as? NSMutableDictionary ?? [:]
        let changePasswordRequest = appzillonBody["changePasswordRequest"] as? NSMutableDictionary ?? [:]
        let newPassword = changePasswordRequest["newPassword"] as? String ?? ""
        var oldPassword = changePasswordRequest["pwd"] as? String ?? ""
        let userId = changePasswordRequest["userId"] as? String ?? ""
        let sysDate = changePasswordRequest["sysDate"] as? String ?? ""
        let serverToken = viewController.apzServerToken ?? ""
        let salt = userId + serverToken
        //Encrypt new password
        let newEnPassword = CryptoSwiftManager.shared.getAESEncryptedStringForServerCalls(dataToEncrypt: newPassword, keyStr: serverToken)
        let authenticationType = viewController.appPropertyDictionary["authenticationType"] as? String ?? ""
        if authenticationType == "#DeviceId" {
            //Hash old password salt as server token
            oldPassword = HashUtility.genHash(oldPassword, salt)
            let hashedOldPassword = HashUtility.genHex(userId, oldPassword, "", sysDate)
            changePasswordRequest["newPassword"] = newEnPassword
            changePasswordRequest["pwd"] = hashedOldPassword
        } else {
            changePasswordRequest["newPassword"] = newEnPassword
            changePasswordRequest["pin"] = oldPassword
        }
        appzillonBody["changePasswordRequest"] = changePasswordRequest
        reqFull["appzillonBody"] = appzillonBody
        
        //These flags are needed for the server from iOS, to decrypt the password using SHA2 and keyLength 128.
        appzillonHeader["appzillonSafeBit"] = "1"
        appzillonHeader["encMode"] = "1"
        appzillonHeader["hash"] = "0"
        
        reqFull["appzillonHeader"] = appzillonHeader
        
        dict["reqFull"] = reqFull
        updatedRequest["params"] = dict
        CallServer.callServerFromInfra(withRequest: updatedRequest, viewController, webView, viewController.appString)
    }
}
