// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import LocalAuthentication

class APZBioAuthentication: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "I", message: "APZBioAuthentication--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        authenticateBiometric(jsonDict: jsonDict)
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZBioAuthentication--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Utility Methods
    private func authenticateBiometric(jsonDict: [AnyHashable: Any]) {
        let authenticationContext = LAContext()
        var error: NSError?
        if authenticationContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) {
            let bioAuthVal = KeychainUtility.shared.getDataFromKeychainWithBiometric(key: StringConstants.SecureStorage.isBiometricNeeded+"APZAuthValue")
            if bioAuthVal == StringConstants.Generic.yes {
                self.sendSuccessCallback()
            } else {
                self.sendFailureCallback(errorCode: StringConstants.Biometric.authenticationErrorCode,
                                        errorMessage: "Biometric Failed")
            }
        } else {
            self.sendFailureCallback(errorCode: StringConstants.Biometric.notEnrolledErrorCode,
                                     errorMessage: error?.localizedDescription ?? StringConstants.Generic.emptyString)
        }
    }
    private func sendSuccessCallback() {
        let resultKeys = [StringConstants.Generic.text]
        let resultValues = [StringConstants.Biometric.authenticationSuccess]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
    private func sendFailureCallback(errorCode: String, errorMessage: String) {
        let resultKeys = [StringConstants.Generic.errorCode, StringConstants.Generic.errorMessage]
        let resultValues = [errorCode, errorMessage]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
}
