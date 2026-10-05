//
//  APZSignaturePad.swift
//  Appzillon
//
//  Created by Bhavya V on 13/10/21.
//

import Foundation
import UIKit

class APZSignaturePad: APZPlugin, SignaturePadHandler {
    var webVW: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin Life Cycle
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webVW = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webVW, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            let signaturePadViewController = SignaturePadViewController.init()
            signaturePadViewController.view.backgroundColor = UIColor.gray
            signaturePadViewController.view.window?.makeKeyAndVisible()
            signaturePadViewController.delegate = self

            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
           let navigateSignaturePad = UINavigationController.init(rootViewController: signaturePadViewController)
            navigateSignaturePad.modalPresentationStyle = UIModalPresentationStyle.fullScreen
            viewController?.present(navigateSignaturePad, animated: false, completion: nil)
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        print("CleanPlugin for APZDeepLinkingSwift")
        self.delegate.donePlugin(self)
        self.viewController = nil
    }
    func cleanPlugin(interfaceOrientation: UIInterfaceOrientation) {
        self.delegate.donePluginWithOrientaion(self, interfaceOrientation)
    }
    func saveCallBack(base64String: String, interfaceOrientation: UIInterfaceOrientation) {
        let resultKeys = [StringConstants.Generic.jsonPath, StringConstants.Generic.encodedImage]
        let resultValues = [StringConstants.Generic.emptyString, base64String]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        print("Save call back")
        cleanPlugin(interfaceOrientation: interfaceOrientation)
    }
    func cancelCallBack(interfaceOrientation: UIInterfaceOrientation) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: [],
                                                                    responseValues: [])
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin(interfaceOrientation: interfaceOrientation)
        print("Cancel call back")
    }
}
