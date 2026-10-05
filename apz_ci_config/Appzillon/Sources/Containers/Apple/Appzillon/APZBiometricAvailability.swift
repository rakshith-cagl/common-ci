// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import LocalAuthentication

class APZBiometricAvailability: APZPlugin {
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
        APZLogger.log(logLvl: "I", message: "APZBiometricAvailability--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        getBiometricType()
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZBiometricAvailability--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Utility Methods
    private func getBiometricType() {
        let context = LAContext()
        var error: NSError?
        if context.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: &error) {
            switch context.biometryType {
            case .faceID:
                sendCallback(biometricType: StringConstants.Biometric.face)
            case .touchID:
                sendCallback(biometricType: StringConstants.Biometric.touch)
            default:
                sendCallback(biometricType: StringConstants.Biometric.none)
            }
        } else {
            if error?.localizedDescription == StringConstants.Biometric.biometricNotEnrolled {
                sendCallback(biometricType: StringConstants.Biometric.notConfigured)
            } else {
                sendCallback(biometricType: StringConstants.Biometric.notSupported)
            }
        }
    }
    private func sendCallback(biometricType: String) {
        let resultKeys = [StringConstants.Biometric.biometricStatus]
        let resultValues = [biometricType]
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
}
