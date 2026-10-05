// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit
import WebKit
import CoreImage

class APZWebview: APZPlugin, UINavigationControllerDelegate, APZWebviewDelegate {
    var webView: WKWebView
    var webViewController: WebViewController?
    var viewController: AppzillonViewController?
    var requestJson: [AnyHashable: Any] // Optional
    var pluginId: String = StringConstants.Generic.emptyString
    var urlStr: String = StringConstants.Generic.emptyString
    var cancelButtonRequired: String = StringConstants.Generic.emptyString
    var successResponseForUrl: AnyObject?
    var postData: [AnyHashable: Any] = [:]
    // MARK: Plugin Lifecycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.requestJson = jsonDict
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        requestJson = jsonDict
        pluginId = requestJson[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        urlStr = requestJson[StringConstants.Generic.webViewUrl] as? String ?? StringConstants.Generic.emptyString
        postData = requestJson[StringConstants.WebView.postData] as? [AnyHashable: Any] ?? [:]
        successResponseForUrl = requestJson[StringConstants.WebView.trackURL] as AnyObject?
        cancelButtonRequired = requestJson[StringConstants.WebView.cancelButton]
            as? String ?? StringConstants.Generic.emptyString
        callWebview()
    }
    override func stop(_ jsonDict: [AnyHashable: Any]?) {
        pluginId = requestJson[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        closeWebView()
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZWebView--Done")
        self.webViewController?.mainWeb = nil
        self.webViewController = nil
        self.delegate.donePlugin(self)
    }
    func cleanPlugin(interfaceOrientation: UIInterfaceOrientation) {
        APZLogger.log(logLvl: "I", message: "APZWebView--Done")
        self.webViewController?.mainWeb = nil
        self.webViewController = nil
        self.delegate.donePluginWithOrientaion(self, interfaceOrientation)
    }
    // MARK: Plugin Private Methods
    private func callWebview() {
        webViewController = WebViewController()
        webViewController?.urlString = urlStr
        webViewController?.pluginId = pluginId
        webViewController?.mainWeb = webView
        webViewController?.delegate = self
        webViewController?.successResponseForUrl = successResponseForUrl
        webViewController?.cancelButtonRequired = cancelButtonRequired
        if postData.count > 0 {
            webViewController?.postData = postData
        }
        if let webVC = webViewController, let viewController = self.viewController {
            let navigationController = UINavigationController(rootViewController: webVC)
            navigationController.modalPresentationStyle = .fullScreen
            viewController.present(navigationController, animated: true, completion: nil)
        }
    }
    private func closeWebView() {
        if let webVC = self.webViewController {
            webVC.dismiss(animated: true) {
                self.webViewController = nil
                self.callback(status: true)
                self.cleanPlugin()
            }
        } else {
            callback(status: false)
        }
    }
    private func callback(status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: [],
                                                                    responseValues: [])
        MiscellaneousMethod.shared.jsLayerCall(webView: self.webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
    // MARK: APZWebView Delegate Methods
    func viewCancelled(interfaceOrientation: UIInterfaceOrientation) {
        callback(status: false)
        APZLogger.log(logLvl: "I", message: "APZWebView--Cancelled")
        cleanPlugin(interfaceOrientation: interfaceOrientation)
    }
}
