//
//  APZActivityShare.swift
//  Appzillon
//
//  Created by Thanmai M S on 10/28/21.
//

import Foundation
import UIKit

 class APZActivityShare: APZPlugin {
    var webVW: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin Life Cycle Method
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webVW = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webVW, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        if !jsonDict.isEmpty {
            var dataToShare: [Any] = []
            let action = jsonDict[StringConstants.Generic.action]
                as? String ?? StringConstants.Generic.emptyString
            if !action.isEmpty && action ==  StringConstants.Generic.file {
                if let filePath = jsonDict[StringConstants.Generic.filePath] as? String {
                    if FileManager.default.fileExists(atPath: filePath) {
                        let fileUrl = URL(fileURLWithPath: filePath)
                        dataToShare = [fileUrl]
                    } else {
                        self.failureCallBack(failureMsg: StringConstants.ActivityShare.errorFile)
                        APZLogger.log(logLvl: "E", message: StringConstants.ActivityShare.errorFile)
                    }
                } else {
                    self.failureCallBack(failureMsg: StringConstants.ActivityShare.errorFilePath)
                    APZLogger.log(logLvl: "E", message: StringConstants.ActivityShare.errorFilePath)
                }
            } else {
                let textToShare = APZNetworkUtility.shared.getStringObject(content: jsonDict[StringConstants.ActivityShare.textToShare] as Any)
                dataToShare = [textToShare]
            }
            self.presentActivityVC(dataToShare: dataToShare)
        } else {
            self.failureCallBack(failureMsg: StringConstants.ActivityShare.errorMessage)
            APZLogger.log(logLvl: "E", message: StringConstants.ActivityShare.errorMessage)
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "D", message: "NativeShare Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Private Methods
    private func presentActivityVC(dataToShare: [Any]) {
        if dataToShare.count > 0 {
            let activityViewController = UIActivityViewController(activityItems: dataToShare,
                                                                  applicationActivities: nil)
            activityViewController.excludedActivityTypes = [ UIActivity.ActivityType.airDrop,
                                                             UIActivity.ActivityType.print,
                                                             UIActivity.ActivityType.copyToPasteboard,
                                                             UIActivity.ActivityType.assignToContact,
                                                             UIActivity.ActivityType.saveToCameraRoll]
            activityViewController.completionWithItemsHandler = { _, completed, _, error  in
                if error != nil, !completed {
                    self.failureCallBack(failureMsg: StringConstants.ActivityShare.errorMessage)
                    APZLogger.log(logLvl: "E", message: StringConstants.ActivityShare.errorMessage)
                } else {
                    self.successCallBack()
                }
            }
            if UIDevice.current.userInterfaceIdiom == .pad {
                if let popup = activityViewController.popoverPresentationController {
                    popup.sourceRect = CGRect(x: (self.viewController?.view.frame.size.width ?? 0) / 2,
                                              y: (self.viewController?.view.frame.size.height ?? 0) / 4,
                                              width: 0,
                                              height: 0)
                    popup.sourceView = self.viewController?.view
                    self.viewController?.present(activityViewController, animated: true, completion: nil)
                }
            } else {
                self.viewController?.present(activityViewController, animated: true, completion: nil)
            }
        } else {
            self.failureCallBack(failureMsg: StringConstants.ActivityShare.errorMessage)
            APZLogger.log(logLvl: "E", message: StringConstants.ActivityShare.errorMessage)
        }
    }
    // MARK: CallBack Methods
    fileprivate func successCallBack() {
        let resultKeys = [StringConstants.Generic.text]
        let resultValues = [StringConstants.ActivityShare.successMessage]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        self.cleanPlugin()
    }
    fileprivate func failureCallBack(failureMsg: String) {
        let resultKeys = [StringConstants.Generic.errorMessage]
        let resultValues = [failureMsg]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        self.cleanPlugin()
    }
}
