//
//  APZVibrate.swift
//  Appzillon
//
//  Created by manjunath.ramesh on 29/10/21.
//

import Foundation
import AudioToolbox
import CoreHaptics

class APZVibrate: APZPlugin {
    var webVW: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webVW = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webVW, jsonDict)
    }

    override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            if UIDevice.current.userInterfaceIdiom == .phone {
                let typeOfFeedback = jsonDict[StringConstants.Generic.hapticFeedback] as? String ?? StringConstants.Generic.emptyString
                let deviceSupportsHaptic = CHHapticEngine.capabilitiesForHardware().supportsHaptics
                if deviceSupportsHaptic {
                    performHapticFeedback(typeOfFeedback)
                } else {
                    //Play default sound with vibration on iPhone 6S and below
                    AudioServicesPlayAlertSoundWithCompletion(1005, nil)
                }
            } else {
                sendFailureCallback()
                APZLogger.log(logLvl: "E", message: "APZVibrate--device not supported")
            }
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "E", message: "APZVibrate--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Utility Methods

    fileprivate func performHapticFeedback(_ typeOfFeedback: String) {
        switch typeOfFeedback {
        case "notificationFeedbackError":
            let generator = UINotificationFeedbackGenerator()
            generator.prepare()
            generator.notificationOccurred(.error)

        case "notificationFeedbackSuccess":
            let generator = UINotificationFeedbackGenerator()
            generator.prepare()
            generator.notificationOccurred(.success)

        case "notificationFeedbackWarning":
            let generator = UINotificationFeedbackGenerator()
            generator.prepare()
            generator.notificationOccurred(.warning)

        case "impactFeedbackGeneratorLight":
            let generator = UIImpactFeedbackGenerator(style: .light)
            generator.prepare()
            generator.impactOccurred()

        case "impactFeedbackGeneratorMedium":
            let generator = UIImpactFeedbackGenerator(style: .medium)
            generator.prepare()
            generator.impactOccurred()

        case "impactFeedbackGeneratorHeavy":
            let generator = UIImpactFeedbackGenerator(style: .heavy)
            generator.prepare()
            generator.impactOccurred()

        case "selectionFeedback":
            let generator = UISelectionFeedbackGenerator()
            generator.prepare()
            generator.selectionChanged()

        default:
            AudioServicesPlayAlertSoundWithCompletion(1005, nil)
        }
    }
    
    private func sendFailureCallback() {
        let resultKeys = [StringConstants.Generic.errorCode]
        let resultValues = [StringConstants.Vibrate.unsupportedDevice]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
