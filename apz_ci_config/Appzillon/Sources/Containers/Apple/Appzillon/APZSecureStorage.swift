// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import LocalAuthentication

class APZSecureStorage: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var jsonDict: [AnyHashable: Any] = [:]
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "I", message: "APZSecureStorage--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        let command = jsonDict[StringConstants.SecureStorage.command] as? String ?? StringConstants.Generic.emptyString
        switch command {
        case StringConstants.SecureStorage.storeCredentialsSecurely:
            storeCredentailsSecurely(jsonDict: jsonDict)
        case StringConstants.SecureStorage.storeSecurely:
            storeSecurely(jsonDict: jsonDict)
        case StringConstants.SecureStorage.retrieveSecurely:
            retrieveSecurely(jsonDict: jsonDict)
        case StringConstants.SecureStorage.loginSecurely :
            loginSecurely(jsonDict: jsonDict)
        default:
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZSecureStorage--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Utility methods
    fileprivate func storeCredentailsSecurely(jsonDict: [AnyHashable: Any]) {
        if let userId = jsonDict[StringConstants.SecureStorage.userId] as? String,
           let password = jsonDict[StringConstants.SecureStorage.password] as? String {
            let value = userId+StringConstants.SecureStorage.hashValue+password
            if let uniqueID = self.viewController?.uniqueID {
                let key = StringConstants.SecureStorage.keyUnderscore+uniqueID
                saveDataIntoKeychain(key: key, value: value, isBiometricNeeded: StringConstants.Generic.yes)
            }
        } else {
            callBack(resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.SecureStorage.keyOrUserIdMissing],
                     status: false)
        }
    }
    fileprivate func storeSecurely(jsonDict: [AnyHashable: Any]) {
        let value = APZNetworkUtility.shared.getStringObject(content: jsonDict[StringConstants.Generic.value] as Any)
        if let key = jsonDict[StringConstants.Generic.key] as? String {
            let biometricNeeded = jsonDict[StringConstants.SecureStorage.promptBiometric]
                as? String ?? StringConstants.Generic.no
            saveDataIntoKeychain(key: key, value: value, isBiometricNeeded: biometricNeeded)
        } else {
            callBack(resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.SecureStorage.keyOrUserIdMissing],
                     status: false)
        }
    }

    fileprivate func retrieveSecurely(jsonDict: [AnyHashable: Any]) {
        if let key = jsonDict[StringConstants.Generic.key] as? String {
            let keychainValue = retrieveDataFromKeychain(key: key)
            if keychainValue.isEmpty {
                sendKeychainFailCallback()
            } else {
                callBack(resultKeys: [StringConstants.Generic.text], resultValues: [keychainValue], status: true)
            }
        } else {
            callBack(resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.SecureStorage.keyOrUserIdMissing],
                     status: false)
        }
    }
    fileprivate func loginSecurely(jsonDict: [AnyHashable: Any]) {
        if let dict = jsonDict[StringConstants.SecureStorage.params] as? [AnyHashable: Any],
           let vc = self.viewController {
            let biometricNeeded = dict[StringConstants.SecureStorage.isBiometric]
                as? String ?? StringConstants.Generic.no
            if biometricNeeded == StringConstants.Generic.yes {
                self.jsonDict = jsonDict
                let keychainValue = retrieveDataFromKeychainForLogin(key: getUserKey())
                if keychainValue.isEmpty {
                    sendKeychainFailCallback()
                } else {
                    let values = keychainValue.components(separatedBy: StringConstants.SecureStorage.hashValue)
                    let userID = values[0]
                    let password = values[1]
                    if !userID.isEmpty && !password.isEmpty {
                        APZLoginUtility.loginWithBiometric(jsonDict: jsonDict,
                                                           userId: userID,
                                                           userPin: password,
                                                           viewController: vc,
                                                           webView: webView)
                    }
                }
            } else {
                APZLoginUtility.loginWithCredentials(jsonDict: jsonDict, viewController: vc, webView: self.webView)
            }
        }
    }
    // MARK: Keychain operations
    fileprivate func saveDataIntoKeychain(key: String, value: String, isBiometricNeeded: String) {
        if isBiometricNeeded == StringConstants.Generic.yes {
            if checkForBiometricSupport() {
                if KeychainUtility.shared.setDataToKeychainWithBiometric(key: key, value: value) {
                    UserDefaults.standard.set(StringConstants.Generic.yes,
                                              forKey: (StringConstants.SecureStorage.isBiometricNeeded+key))
                    callBack(resultKeys: [StringConstants.SecureStorage.result],
                             resultValues: [StringConstants.SecureStorage.successful],
                             status: true)
                } else {
                    sendKeychainFailCallback()
                }
            } else {
                callBack(resultKeys: [StringConstants.Generic.errorCode],
                         resultValues: [StringConstants.SecureStorage.biometricNotEnrolled],
                         status: false)
            }
        } else {
            if KeychainUtility.shared.setDataToKeychain(key: key, value: value) {
                UserDefaults.standard.set(StringConstants.Generic.no,
                                          forKey: (StringConstants.SecureStorage.isBiometricNeeded+key))
                callBack(resultKeys: [StringConstants.SecureStorage.result],
                         resultValues: [StringConstants.SecureStorage.successful], status: true)
            } else {
                sendKeychainFailCallback()
            }
        }
    }
    fileprivate func retrieveDataFromKeychain(key: String) -> String {
        let keyStr = StringConstants.SecureStorage.isBiometricNeeded+key
        if let isBiometricNeeded = UserDefaults.standard.value(forKey: keyStr) as? String {
            if isBiometricNeeded == StringConstants.Generic.yes {
                if checkForBiometricSupport() {
                    return KeychainUtility.shared.getDataFromKeychainWithBiometric(key: key)
                } else {
                    callBack(resultKeys: [StringConstants.Generic.errorCode],
                             resultValues: [StringConstants.SecureStorage.biometricNotEnrolled],
                             status: false)
                }
            } else {
                return KeychainUtility.shared.getDataFromKeychain(key: key)
            }
        }
        return StringConstants.Generic.emptyString
    }
    fileprivate func retrieveDataFromKeychainForLogin(key: String) -> String {
        let isBiometricNeeded = StringConstants.SecureStorage.isBiometricNeeded
        if let isBiometricNeeded = UserDefaults.standard.value(forKey: (isBiometricNeeded+key)) as? String,
           isBiometricNeeded == StringConstants.Generic.yes {
            let isBiometricChanged = checkForChangesInDeviceBiometric()
            if isBiometricChanged {
                return KeychainUtility.shared.getDataFromKeychainWithBiometric(key: key)
            } else {
                callBack(resultKeys: [StringConstants.Generic.errorCode],
                         resultValues: [StringConstants.SecureStorage.biometricNotEnrolled],
                         status: false)
            }
        }
        return StringConstants.Generic.emptyString
    }
    fileprivate func getUserKey() -> String {
        if let uniqueID = self.viewController?.uniqueID {
            let key = StringConstants.SecureStorage.keyUnderscore+uniqueID
            return key
        }
        return StringConstants.Generic.emptyString
    }
    // MARK: Check biometric availability
    fileprivate func checkForBiometricSupport() -> Bool {
        let context = LAContext()
        var error: NSError?
        if context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) {
            if let domainStateData = context.evaluatedPolicyDomainState {
                UserDefaults.standard.set(domainStateData, forKey: StringConstants.SecureStorage.bioMetricDomainState)
                UserDefaults.standard.synchronize()
            }
            return true
        } else {
            return false
        }
    }
    fileprivate func checkForChangesInDeviceBiometric() -> Bool {
        let context = LAContext()
        var error: NSError?
        if context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) {
            let bioMetricDomainState = StringConstants.SecureStorage.bioMetricDomainState
            if let domainStateData = context.evaluatedPolicyDomainState,
               let oldDomainStateData = UserDefaults.standard.value(forKey: bioMetricDomainState) as? Data {
                if oldDomainStateData == domainStateData {
                    return true
                } else {
                    UserDefaults.standard.removeObject(forKey: StringConstants.SecureStorage.bioMetricDomainState)
                    UserDefaults.standard.set(domainStateData,
                                              forKey: StringConstants.SecureStorage.bioMetricDomainState)

                    sendBiometricChangedCallback()
                }
            }
        } else {
            return false
        }
        return false
    }
    // MARK: Callback methods
    fileprivate func callBack(resultKeys: [String], resultValues: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
    fileprivate func sendKeychainFailCallback() {
        callBack(resultKeys: [StringConstants.Generic.errorCode],
                 resultValues: [StringConstants.SecureStorage.keychainOperationFailed],
                 status: false)
    }
    fileprivate func sendBiometricChangedCallback() {
        if let paramsDict = self.jsonDict[StringConstants.SecureStorage.params] as? [AnyHashable: Any],
           var callbackDict = paramsDict[StringConstants.SecureStorage.resFull] as? [AnyHashable: Any],
           var headerDict = callbackDict[StringConstants.SecureStorage.appzillonHeader] as? [AnyHashable: Any] {
            headerDict[StringConstants.SecureStorage.status] = 0
            callbackDict.updateValue(headerDict, forKey: StringConstants.SecureStorage.appzillonHeader)
            var errorDict: [AnyHashable: Any] = [:]
            errorDict[StringConstants.Generic.errorCode] = StringConstants.SecureStorage.deviceBiometricChanged
            errorDict[StringConstants.Generic.errorMessage] = StringConstants.SecureStorage.fingerPrintChanged
            callbackDict[StringConstants.SecureStorage.appzillonErrors] = [errorDict]
            var callbackDictFinal: [AnyHashable: Any] = [:]
            callbackDictFinal[StringConstants.SecureStorage.resFull] = callbackDict
            callbackDictFinal[StringConstants.SecureStorage.status] = 0
            if let reqID = self.jsonDict[StringConstants.SecureStorage.reqId] as? String {
                callBack(resultKeys: [StringConstants.SecureStorage.params,
                                     StringConstants.SecureStorage.reqId,
                                     StringConstants.Generic.errorCode],
                         resultValues: [callbackDictFinal,
                                        reqID,
                                        StringConstants.SecureStorage.deviceBiometricChanged],
                                        status: false)
            }
        }
    }
}
