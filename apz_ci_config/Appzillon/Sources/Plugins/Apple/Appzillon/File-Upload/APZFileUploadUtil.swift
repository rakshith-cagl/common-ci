// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

@objc class APZFileUploadUtil: NSObject {
    var viewController: AppzillonViewController?
    var webView: WKWebView
    var jsonDict: [AnyHashable: Any]
    @objc init(webView: WKWebView, _ jsonDict: [AnyHashable: Any], viewController: AppzillonViewController?) {
        self.webView = webView
        self.jsonDict = jsonDict
        self.viewController = viewController
    }
    @objc func uploadRequestSuccess(messageDict: [AnyHashable: Any]) {
        let header = messageDict["appzillonHeader"] as? [AnyHashable: Any]
        if let statusNumber = header?["status"] as? NSNumber, statusNumber.boolValue {
            uploadSuccessWithoutErrors(messageDict: messageDict)
        } else {
            uploadSuccessWithError(messageDict: messageDict)
        }
    }
    @objc func uploadFailed(messageDict: [AnyHashable: Any]) {
        let errorCode = messageDict[StringConstants.Generic.errorCode] as? String ?? StringConstants.Generic.emptyString
        sendCallback(resultKeys: [StringConstants.Generic.errorCode], resultValues: [errorCode], status: false)
        APZLogger.log(logLvl: "E", message: "APZFileUpload--network error")
    }
    // MARK: Server Callback Helper Methods
    private func uploadSuccessWithoutErrors(messageDict: [AnyHashable: Any]) {
        
        let appzillonBodyDict = messageDict["appzillonBody"] as? [AnyHashable: Any] ?? [:]
        //Changes for custom interfaceID in fileUpload plugin
        if let customIntId = jsonDict["customInterfaceId"] as? String, !customIntId.isEmpty {
            sendCallback(resultKeys: ["uploadResponse", "successMessage"], resultValues: [appzillonBodyDict, "Upload Success"], status: true)
            APZLogger.log(logLvl: "I", message: "APZFileUpload--response arrived")
        }else{
            let sessionReq = jsonDict["sessionReq"] as? String ?? StringConstants.Generic.emptyString
            let isSessionRequired = (sessionReq == StringConstants.Generic.yes)
            let uploadResponseDict = isSessionRequired ? appzillonBodyDict["appzillonUploadFileResponse"] :
            appzillonBodyDict["appzillonUploadFileWSResponse"]
            if let respDict = uploadResponseDict as? [String: Any] {
                for key in respDict.keys {
                    let value = respDict[key] as? String ?? StringConstants.Generic.emptyString
                    if value == StringConstants.Generic.success {
                        sendCallback(resultKeys: [], resultValues: [], status: true)
                        APZLogger.log(logLvl: "I", message: "APZFileUpload--response arrived")
                    } else {
                        sendCallback(resultKeys: [StringConstants.Generic.errorCode],
                                     resultValues: ["APZ-FL001"],
                                     status: false)
                        APZLogger.log(logLvl: "E", message: "APZFileUpload--File upload failed")
                    }
                }
            }
        }
    }
    private func uploadSuccessWithError(messageDict: [AnyHashable: Any]) {
        let errorArray = messageDict["appzillonErrors"] as? Array ?? []
        if let errorDict = errorArray.first as? [AnyHashable: Any],
           let errorCode = errorDict[StringConstants.Generic.errorCode] as? String,
           let errorMsg = errorDict[StringConstants.Generic.errorMessage] as? String {
            sendCallback(resultKeys: [StringConstants.Generic.errorCode], resultValues: [errorCode], status: false)
            APZLogger.log(logLvl: "E", message: String(format: "APZFileUpload--%@", errorMsg))
        }
    }
    private func sendCallback(resultKeys: [String], resultValues: [Any], status: Bool) {
        let pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys, responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
