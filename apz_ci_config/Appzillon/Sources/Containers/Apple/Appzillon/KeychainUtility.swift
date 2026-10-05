// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import LocalAuthentication

class KeychainUtility: NSObject {
    @objc static let shared = KeychainUtility()
    private override init() {
        //do nothing
    }
     func setDataToKeychain(key: String, value: String) -> Bool {
         let aesEncryptedString = CryptoSwiftManager.shared.encryptPlainString(key: key, value: value)
         if let passwordData = aesEncryptedString.data(using: .utf8) {
             let query = [kSecClass: kSecClassInternetPassword as String,
                         kSecAttrServer: key,
                         kSecValueData: passwordData,
                 kSecAttrAccessible: kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly] as [String: Any]
             // Add properties
             SecItemDelete(query as CFDictionary)
             if SecItemAdd(query as CFDictionary, nil) == errSecSuccess {
                 return true
             }
         }
         return false
     }
    @objc func getDataFromKeychain(key: String) -> String {
        let query = [kSecClass as String: kSecClassInternetPassword as String,
                     kSecAttrServer as String: key,
                     kSecReturnAttributes as String: kCFBooleanTrue!,
                     kSecReturnData as String: kCFBooleanTrue!,
                     kSecMatchLimit as String: kSecMatchLimitOne ] as [String: Any]
        var dataTypeRef: AnyObject?
        let status: OSStatus = SecItemCopyMatching(query as CFDictionary, &dataTypeRef)
        if status == noErr,
           let responseDict = dataTypeRef as? [AnyHashable: Any],
           let decodedData = responseDict[kSecValueData] as? Data,
           let decodedStr = String(data: decodedData, encoding: .utf8) {
            return CryptoSwiftManager.shared.decryptCipherString(key: key, value: decodedStr)
        }
        return StringConstants.Generic.emptyString
    }
    @objc func generateRandomString() -> String {
        var bytes = [UInt8](repeating: 0, count: 24)
        let result = SecRandomCopyBytes(kSecRandomDefault, bytes.count, &bytes)
        guard result == errSecSuccess else {
            return StringConstants.Generic.emptyString
        }
        return Data(bytes).base64EncodedString()
    }
    // MARK: RemoveOldDataFromKeychain used in AppDelegate didFinishLaunching
     func removeDataFromKeychain() {
        let query = [kSecClass as String: kSecClassInternetPassword as String,
                     kSecReturnAttributes as String: kCFBooleanTrue!,
                     kSecReturnData as String: kCFBooleanTrue! ] as [String: Any]
        let _: OSStatus = SecItemDelete(query as CFDictionary)
    }
    func setDataToKeychainWithBiometric(key: String, value: String) -> Bool {
        let aesEncryptedString = CryptoSwiftManager.shared.encryptPlainString(key: key, value: value)
        if let passwordData = aesEncryptedString.data(using: .utf8) {
            let access = SecAccessControlCreateWithFlags(nil,
                                                         kSecAttrAccessibleWhenPasscodeSetThisDeviceOnly,
                                                         .userPresence,
                                                         nil)
            let context = LAContext()
            context.touchIDAuthenticationAllowableReuseDuration = 5
            let query: [String: Any] = [kSecClass as String: kSecClassInternetPassword,
                                        kSecAttrServer as String: key,
                                        kSecAttrAccessControl as String: access as Any,
                                        kSecUseAuthenticationContext as String: context,
                                        kSecValueData as String: passwordData]
            SecItemDelete(query as CFDictionary)
            if SecItemAdd(query as CFDictionary, nil) == errSecSuccess {
                return true
            }
        }
        return false
    }
    func getDataFromKeychainWithBiometric(key: String) -> String {
        
        let appzillonViewController = MiscellaneousMethod.shared.getAppzillonViewController()
        var userPrompt = "Authenticate With Biometric To Unlock."
        if appzillonViewController?.languageDataDictionary != nil {
            if let langString = appzillonViewController?.languageDataDictionary[StringConstants.Biometric.fingerprintSub] as? String, !langString.isEmpty {
                userPrompt = langString
            }
        }
        let query: [String: Any] = [kSecClass as String: kSecClassInternetPassword as String,
                     kSecAttrServer as String: key,
                     kSecReturnAttributes as String: true,
                     kSecReturnData as String: true,
                     kSecMatchLimit as String: kSecMatchLimitOne ,
                     kSecUseOperationPrompt as String: userPrompt] as [String: Any]
        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)
        if status == errSecSuccess,
           let existingItem = item as? [String: Any],
            let responseData = existingItem[kSecValueData as String] as? Data,
            let responseString = String(data: responseData, encoding: String.Encoding.utf8) {
            return CryptoSwiftManager.shared.decryptCipherString(key: key, value: responseString)
        }
        return StringConstants.Generic.emptyString
    }
}
