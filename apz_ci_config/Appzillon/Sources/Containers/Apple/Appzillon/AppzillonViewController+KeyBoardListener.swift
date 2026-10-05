// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
extension AppzillonViewController {
     // MARK: - KeyBoardLister Methods
     func keyBoardHandleInWebView() {
        let center = NotificationCenter.default
        center.addObserver(self,
                           selector: #selector(handleKeyboardWillShowAtContainer),
                           name: UIResponder.keyboardWillShowNotification,
                           object: nil)
        center.addObserver(self,
                           selector: #selector(handleKeyboardWillHideAtContainer),
                           name: UIResponder.keyboardWillHideNotification,
                           object: nil)
    }

     @objc func handleKeyboardWillShowAtContainer(paramNotification: Notification) {
        if #available(iOS 11.0, *) {
            webView.scrollView.contentInsetAdjustmentBehavior = .automatic
        }
    }

    @objc func handleKeyboardWillHideAtContainer(paramNotification: Notification) {
        if #available(iOS 11.0, *) {
            webView.scrollView.contentInsetAdjustmentBehavior = .never
        }
    }
     func keyBoardStartListener(result: [AnyHashable: Any]) {
        if !result.isEmpty {
            self.keyBoardDictionary = result
            let center = NotificationCenter.default
            center.addObserver(self,
                               selector: #selector(handleKeyboardWillShow),
                               name: UIResponder.keyboardWillShowNotification,
                               object: nil)
            center.addObserver(self,
                               selector: #selector(handleKeyboardWillHide),
                               name: UIResponder.keyboardWillHideNotification,
                               object: nil)
            callBack(pluginId: result[StringConstants.Generic.pluginId] as?
                     String ?? StringConstants.Generic.emptyString,
                     resultKeys: [StringConstants.Generic.cbEvent],
                     resultValues: [StringConstants.Generic.started], keepAlive: true)
        }
    }
     func keyBoardStopListener(result: [AnyHashable: Any]) {
        if !result.isEmpty {
            self.keyBoardDictionary = nil
            NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillShowNotification, object: nil)
            NotificationCenter.default.removeObserver(self, name: UIResponder.keyboardWillHideNotification, object: nil)
            callBack(pluginId: result[StringConstants.Generic.pluginId] as?
                     String ?? StringConstants.Generic.emptyString,
                     resultKeys: [StringConstants.Generic.cbEvent],
                     resultValues: [StringConstants.Generic.stopped], keepAlive: false)
        }
    }
    @objc func handleKeyboardWillShow(paramNotification: Notification) {
        if !keyBoardDictionary.isEmpty {
            callBack(pluginId: keyBoardDictionary[StringConstants.Generic.pluginId] as?
                     String ?? StringConstants.Generic.emptyString,
                     resultKeys: [StringConstants.Generic.cbEvent],
                     resultValues: [StringConstants.KeyBoardListner.show], keepAlive: true)
        }
    }
    @objc func handleKeyboardWillHide(paramSender: Notification) {
        if !keyBoardDictionary.isEmpty {
            callBack(pluginId: keyBoardDictionary[StringConstants.Generic.pluginId] as?
                     String ?? StringConstants.Generic.emptyString,
                     resultKeys: [StringConstants.Generic.cbEvent],
                     resultValues: [StringConstants.KeyBoardListner.hide], keepAlive: true)
        }
    }
    fileprivate func callBack(pluginId: String, resultKeys: [String], resultValues: [Any], keepAlive: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: keepAlive,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
