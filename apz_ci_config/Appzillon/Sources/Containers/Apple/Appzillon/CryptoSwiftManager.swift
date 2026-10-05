//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import CryptoSwift

class CryptoSwiftManager: NSObject {
    @objc static let shared = CryptoSwiftManager()
    private override init() {
        //do nothing
    }
    
    // MARK: Cipher Methods
    @objc func getPlistEncryptKey() -> String {
        var plistEncryptKey = StringConstants.Generic.emptyString
        if let appDelegate = APZAppDelegateUtility.shared.appDelegate,
            let mainAppID = appDelegate.containerPropsDictionary[StringConstants.Generic.mainAppId] as? String {
            let documentDirectory = FileManagerUtility.documentDirectory().path
            let containerPlistPath = "%@/Assets/apps/%@/plist/Container.plist"
            let runTimeDictPath = String(format: containerPlistPath, documentDirectory, mainAppID)
            if FileManager().fileExists(atPath: runTimeDictPath),
               let containerPlistDict = NSDictionary(contentsOfFile: runTimeDictPath) as? [AnyHashable: Any],
                let value = containerPlistDict["PLISTENCRYPTKEY"] {
                plistEncryptKey = getAESDecryptedStringForServerCalls(dataToDecrypt: value, keyStr: "PLISTENCRYPTKEY")
            }
        }
        return plistEncryptKey
    }
    
    // MARK: APZEncrypt and APZDercypt Content
    @objc func encryptedContent(dataToEncrypt: Any, keyStr: String) -> String {
        let stringValue = getStringObject(content: dataToEncrypt)
        var base64String = encryptPlainString(key: keyStr, value: stringValue)
        return base64String
    }
    @objc func decryptContent(dataToDecrypt: Any, keyStr: String) -> String {
        let stringValue = APZNetworkUtility.shared.getStringObject(content: dataToDecrypt)
        let decStr = decryptCipherString(key: keyStr, value: stringValue)
        return decStr
    }
    
    
    // MARK: Server Related Cipher Operations
    @objc func getAESEncryptedStringForServerCalls(dataToEncrypt: Any, keyStr: String) -> String {
        var base64String = StringConstants.Generic.emptyString
        let stringValue = getStringObject(content: dataToEncrypt)
        print(keyStr)
        let key = checkKeyLength(keyStr: keyStr, encodingType: 16)
        if let keyData = passwordBasedKeyDerivation(passwordKey: key) {
            let iv = checkKeyLength(keyStr: getRandomString(length: 12), encodingType: 12)

            //AES-GCM: No Padding
            let gcm = GCM(iv: iv.bytes, mode: .combined)
            let encryptedBytes = try? AES(key: keyData.bytes, blockMode: gcm, padding: .noPadding).encrypt(stringValue.bytes)
            if let encryptedData = encryptedBytes?.data {
                base64String = Base64.encode(iv.bytes.data) + Base64.encode(encryptedData)
            }
        }
        return base64String
    }
    
    @objc func getAESDecryptedStringForServerCalls(dataToDecrypt: Any, keyStr: String) -> String {
        let stringValue = APZNetworkUtility.shared.getStringObject(content: dataToDecrypt)
        let data = Base64.decode(stringValue)
        // First 12 bytes is the Initialisation Vector in the encrypted data
        // and rest is actual data to be encrypted
        let iv = Array(data.bytes.prefix(12))
        let finalCyData = Array(data.bytes.suffix(data.bytes.count-12))
        let key = checkKeyLength(keyStr: keyStr, encodingType: 16)
        if let keyData = passwordBasedKeyDerivation(passwordKey: key) {
            //AES-GCM: No Padding
            let gcm = GCM(iv: iv, mode: .combined)
            let decryptedBytes = try? AES(key: keyData.bytes, blockMode: gcm, padding: .noPadding).decrypt(finalCyData)
            if let decBytes = decryptedBytes ,let decStr = String(bytes: decBytes, encoding: .utf8) {
                return decStr
            }
        }
        return StringConstants.Generic.emptyString
    }
    
    @objc func encryptSingleValue(value: String) -> String {
        let keyStr = checkKeyLength(keyStr: getPlistEncryptKey(), encodingType: 16)
        var base64String = StringConstants.Generic.emptyString
        let rIv = String(keyStr.reversed())
        //AES-GCM: No Padding
        let gcm = GCM(iv: rIv.bytes, mode: .combined)
        let encryptedBytes = try? AES(key: keyStr.bytes, blockMode: gcm, padding: .noPadding).encrypt(value.bytes)
        if let encryptedData = encryptedBytes?.data {
            base64String = Base64.encode(encryptedData)
        }
        return base64String
    }
    
    @objc func decryptSingleValue(value: String) -> String {
        let keyStr = checkKeyLength(keyStr: getPlistEncryptKey(), encodingType: 16)
        let rIv = String(keyStr.reversed())

        let decGcm = GCM(iv: rIv.bytes, mode: .combined)
        let decryptedBytes = try? AES(key: keyStr.bytes, blockMode: decGcm, padding: .noPadding).decrypt(Base64.decode(value).bytes)
        if let decBytes = decryptedBytes ,let decStr = String(bytes: decBytes, encoding: .utf8) {
            return decStr
        }
        return StringConstants.Generic.emptyString
    }
    
    @objc func encryptPlainString(key: String, value: String) -> String {
        let keyStr = checkKeyLength(keyStr: key, encodingType: 16)
        var base64String = StringConstants.Generic.emptyString
        let rIv = String(keyStr.reversed())
        //AES-GCM: No Padding
        let gcm = GCM(iv: rIv.bytes, mode: .combined)
        let encryptedBytes = try? AES(key: keyStr.bytes, blockMode: gcm, padding: .noPadding).encrypt(value.bytes)
        if let encryptedData = encryptedBytes?.data {
            base64String = Base64.encode(encryptedData)
        }
        return base64String
    }
    
    @objc func decryptCipherString(key: String, value: String) -> String {
        let keyStr = checkKeyLength(keyStr: key, encodingType: 16)
        let rIv = String(keyStr.reversed())

        let decGcm = GCM(iv: rIv.bytes, mode: .combined)
        let decryptedBytes = try? AES(key: keyStr.bytes, blockMode: decGcm, padding: .noPadding).decrypt(Base64.decode(value).bytes)
        if let decBytes = decryptedBytes ,let decStr = String(bytes: decBytes, encoding: .utf8) {
            return decStr
        }
        return StringConstants.Generic.emptyString
    }
    
    // MARK: File Encryption Methods
    @objc func encryptDataWithAES(key: String, dataToEncrypt: Data) -> Data? {
        if let keyData = passwordBasedKeyDerivation(passwordKey: key) {
            let rIv = String(key.reversed())

            //AES-GCM: No Padding
            let gcm = GCM(iv: rIv.bytes, mode: .combined)
            let encryptedBytes = try? AES(key: keyData.bytes, blockMode: gcm, padding: .noPadding).encrypt(dataToEncrypt.bytes)
            return encryptedBytes?.data
        }
        return nil
    }
    
    @objc func decryptDataWithAES(key: String, dataToDecrypt: Data) -> Data? {
        if let keyData = passwordBasedKeyDerivation(passwordKey: key) {
            let rIv = String(key.reversed())

            //AES-GCM: No Padding
            let gcm = GCM(iv: rIv.bytes, mode: .combined)
            let decryptedBytes = try? AES(key: keyData.bytes, blockMode: gcm, padding: .noPadding).decrypt(dataToDecrypt.bytes)
            return decryptedBytes?.data
        }
        return nil
    }
    
    // MARK: Plist Encryption Methods
    @objc func encryptPlistData(dictionary: [AnyHashable: Any]) -> [AnyHashable: Any] {
        var encryptedDict: [String: Any] = [:]
        let encryptKey = getPlistEncryptKey()
        for key in dictionary.keys {
            var encryptedValue = StringConstants.Generic.emptyString
            let value = dictionary[key]
            if value is String || value is NSString {
                if ["serverToken", "serverUrl"].contains(key) {
                    encryptedValue = value as? String ?? StringConstants.Generic.emptyString
                } else {
                    encryptedValue = encryptPlainString(key: encryptKey, value: value as? String ?? "")
                }
                if let keyVal  = key as? String {
                    encryptedDict[keyVal] = encryptedValue
                }

            } else if value is [String: Any] {
                var masterDict = dictionary[key] as? [String: Any] ?? [:]
                for key in masterDict.keys {
                    let value = masterDict[key]
                    if value is String || value is NSString {
                        masterDict[key] = encryptPlainString(key: encryptKey, value: value as? String ?? "")
                    } else {
                        let encryptedChildDict = encryptPlistData(dictionary: value as? [String: Any] ?? [:])
                        masterDict[key] = encryptedChildDict
                    }
                }
                if let keyVal  = key as? String {
                    encryptedDict[keyVal] = masterDict
                }
            } else {
                if let keyVal  = key as? String {
                    encryptedDict[keyVal] = encryptPlainString(key: encryptKey, value: String(format: "%@", value as! CVarArg))
                }
            }
        }
        return encryptedDict
    }

    @objc func decryptPlistData(dictionary: [AnyHashable: Any]) -> [AnyHashable: Any] {
        var decryptedDict: [String: Any] = [:]
        let decryptKey = getPlistEncryptKey()
        for key in dictionary.keys {
            let value = dictionary[key]
            if value is String || value is NSString {
                var decryptedValue = StringConstants.Generic.emptyString

                if ["serverToken", "serverUrl"].contains(key) {
                    decryptedValue = value as? String ?? StringConstants.Generic.emptyString
                } else {
                    decryptedValue = decryptCipherString(key: decryptKey, value: value as? String ?? "")
                }
                if let keyVal  = key as? String {
                    decryptedDict[keyVal] = decryptedValue
                }
            } else if value is [String: Any] {
                var masterDict = dictionary[key] as? [String: Any] ?? [:]
                for key in masterDict.keys {
                    let value = masterDict[key]
                    if value is String || value is NSString {
                        masterDict[key] = decryptCipherString(key: decryptKey, value: value as? String ?? "")
                    } else {
                        let encryptedChildDict = decryptPlistData(dictionary: value as? [String: Any] ?? [:])
                        masterDict[key] = encryptedChildDict
                    }
                }
                if let keyVal  = key as? String {
                    decryptedDict[keyVal] = masterDict
                }
            } else {
                if let keyVal  = key as? String {
                    decryptedDict[keyVal] = decryptCipherString(key: decryptKey, value: String(format: "%@", value as! CVarArg))
                }
            }
        }
        return decryptedDict
    }
    
    // MARK: Utility Methods
    fileprivate func passwordBasedKeyDerivation(passwordKey: String) -> Data? {
        let keyData = passwordKey.data(using: .utf8)
        if var saltCharBytes = keyData?.bytes {
            let saltLen = passwordKey.count
            let firstChar = saltCharBytes[0]
            saltCharBytes[0] = saltCharBytes[1]
            saltCharBytes[1] = firstChar
            let lastChar = saltCharBytes[saltLen-1]
            saltCharBytes[saltLen-1] = saltCharBytes[saltLen-2];
            saltCharBytes[saltLen-2] = lastChar;
            
            let derivedKeyBytes = try? PKCS5.PBKDF2(password: passwordKey.bytes,
                                             salt: saltCharBytes,
                                             iterations: 2,
                                             keyLength: saltLen,
                                             variant: .sha2(.sha256)).calculate()
            return derivedKeyBytes?.data
        }
        print("Derived Key is nil")
        return nil
    }
    fileprivate func getRandomString(length: Int) -> String {
        let letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return String((0..<length).map{ _ in letters.randomElement()! })
    }
    
    fileprivate func getStringObject(content: Any) -> String {
        var convertedString = StringConstants.Generic.emptyString
        if content is String || content is NSString {
            convertedString = content as? String ?? StringConstants.Generic.emptyString
        } else {
            if let dictData = try? JSONSerialization.data(withJSONObject: content, options: .fragmentsAllowed) {
                convertedString = String(data: dictData, encoding: .utf8) ?? StringConstants.Generic.emptyString
            }
        }
        return convertedString
    }
    
    fileprivate func checkKeyLength(keyStr: String, encodingType: Int) -> String {
        var finalKeyStr = keyStr
        var keyLen = keyStr.count
        if keyLen < encodingType {
            let nPadding = encodingType - keyLen
            for _ in 0..<nPadding where keyLen < encodingType {
                    finalKeyStr.insert("$", at: keyStr.endIndex)
                    keyLen += 1
                }
        } else if keyLen > encodingType {
            finalKeyStr = String(keyStr.prefix(encodingType))
        }
        return finalKeyStr
    }
}

extension Data {
    var bytes: [UInt8] {
        return [UInt8](self)
    }
}

extension Array where Element == UInt8 {
    var data: Data {
        return Data(self)
    }
}
