// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import CallKit
import CoreTelephony
import WebKit

class APZTelephony: NSObject, CXCallObserverDelegate, MFMessageComposeViewControllerDelegate {
    static let shared = APZTelephony()
    var pluginId: String = StringConstants.Generic.emptyString
    var viewController: AppzillonViewController?
    var webView = WKWebView()
    let callObserver = CXCallObserver()
    var smsPluginId: String?
    func executePhoneCall(jsonDict: [AnyHashable: Any],
                          webView: WKWebView,
                          viewController: AppzillonViewController) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        self.webView = webView
        self.viewController = viewController
        // Callkit Framework disabled in China region
        let isChinaRegion = (Locale.current.regionCode == "CN" || Locale.current.regionCode == "CHN")
        if cellularNetworkExist() && !isChinaRegion {
            callObserver.setDelegate(self, queue: nil)
            if let phoneNo = jsonDict[StringConstants.PhoneCallAndSMS.phone] as? String,
               !phoneNo.isEmpty,
               let url = URL(string: StringConstants.PhoneCallAndSMS.telePrompt + phoneNo.removingAllWhitespaces) {
                UIApplication.shared.open(url, options: [:])
            } else {
                phoneNotsupported()
            }
        } else {
            phoneNotsupported()
        }
    }
    func callObserver(_ callObserver: CXCallObserver, callChanged call: CXCall) {
        if call.hasEnded {
            handleCallBacks(message: StringConstants.PhoneCallAndSMS.disconnected, keepAlive: false)
        } else if call.hasConnected && viewController?.isAppCallStartMonitored == true {
            handleCallBacks(message: StringConstants.PhoneCallAndSMS.connected, keepAlive: true)
        } else if call.isOutgoing && viewController?.isAppCallStartMonitored == true {
            handleCallBacks(message: StringConstants.PhoneCallAndSMS.typing, keepAlive: true)
        } else if (!call.isOutgoing && !call.hasConnected && !call.hasEnded)
                    && viewController?.isAppCallStartMonitored == true {
            handleCallBacks(message: StringConstants.PhoneCallAndSMS.incoming, keepAlive: true)
        }
    }
    func cellularNetworkExist() -> Bool {
        var isCallPossible: Bool = false
        if let url = URL(string: StringConstants.PhoneCallAndSMS.telePrompt),
           UIApplication.shared.canOpenURL(url) {
            if let carrierDict = CTTelephonyNetworkInfo().serviceSubscriberCellularProviders?.values,
               carrierDict.count > 0 {
                carrierDict.forEach({ carrier in
                    if carrier.mobileNetworkCode != nil {
                        isCallPossible = true
                    }
                })
            } else {
                isCallPossible = false
            }
        } else {
            isCallPossible = false
        }
        return isCallPossible
    }
    func handleCallBacks(message: String, keepAlive: Bool) {
        let returnResultkeys = [StringConstants.Generic.cbEvent]
        let returnResult = [message]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: keepAlive,
                                                                    responseKeys: returnResultkeys,
                                                                    responseValues: returnResult)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
    func phoneNotsupported() {
        let returnResultkeys = [StringConstants.Generic.errorCode]
        let returnResult = [StringConstants.PhoneCallAndSMS.notSupported]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: returnResultkeys,
                                                                    responseValues: returnResult)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
    // MARK: - SendSMS Methods
    func sendSMS(jsonDict: [AnyHashable: Any], apzViewController: AppzillonViewController, webView: WKWebView) {
        smsPluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        self.webView = webView
        if let url = URL(string: StringConstants.PhoneCallAndSMS.sms),
           UIApplication.shared.canOpenURL(url) {
            if cellularNetworkExist() {
                presentSMSWindow(jsonDict: jsonDict, apzViewController: apzViewController)
            } else {
                let responseKeys = [StringConstants.Generic.errorMessage]
                let responseValues = [StringConstants.PhoneCallAndSMS.simAvailability]
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: smsPluginId ?? "",
                                                                            status: false, keepAlive: false,
                                                                            responseKeys: responseKeys,
                                                                            responseValues: responseValues)
                MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
            }
        } else {
            let responseKeys = [StringConstants.Generic.errorMessage]
            let responseValues = [StringConstants.PhoneCallAndSMS.unableToSendSMS]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: smsPluginId ?? "",
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: responseKeys,
                                                                        responseValues: responseValues)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
    }
    fileprivate func presentSMSWindow(jsonDict: [AnyHashable: Any], apzViewController: AppzillonViewController) {
        if let phoneNo = jsonDict[StringConstants.PhoneCallAndSMS.phone] as? String, !phoneNo.isEmpty {
            let smsMessenger = MFMessageComposeViewController()
            smsMessenger.messageComposeDelegate = self
            smsMessenger.recipients = [phoneNo]
            smsMessenger.body = jsonDict[StringConstants.Generic.message] as? String
            smsMessenger.modalPresentationStyle = .formSheet
            apzViewController.present(smsMessenger, animated: true, completion: nil)
        } else {
            let responseKeys = [StringConstants.Generic.errorMessage]
            let responseValues = [StringConstants.PhoneCallAndSMS.phoneNoDoesNotExist]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: smsPluginId ?? "",
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: responseKeys,
                                                                        responseValues: responseValues)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
    }
    // MARK: - MessageComposeViewController Delegate Methods
    func messageComposeViewController(_ controller: MFMessageComposeViewController,
                                      didFinishWith result: MessageComposeResult) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: smsPluginId ?? "",
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: [],
                                                                    responseValues: [])
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        smsPluginId = nil
        controller.dismiss(animated: true, completion: nil)
    }
}

extension StringProtocol where Self: RangeReplaceableCollection {
    var removingAllWhitespaces: Self {
        filter(\.isWhitespace.negated)
    }
    mutating func removeAllWhitespaces() {
        removeAll(where: \.isWhitespace)
    }
}

extension Bool {
    var negated: Bool { !self }
}
