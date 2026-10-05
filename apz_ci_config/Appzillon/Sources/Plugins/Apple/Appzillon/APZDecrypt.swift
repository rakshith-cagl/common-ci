// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZDecrypt: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: - Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: wbView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "APZDecrypt--execute")
        if !jsonDict.isEmpty {
            self.pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            aesDecrypt(jsonDict: jsonDict)
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "D", message: "APZDecrypt--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: AES Decrypt Method
    fileprivate func aesDecrypt(jsonDict: [AnyHashable: Any]) {
        if let dataToDecrypt = jsonDict[StringConstants.Decrypt.stringToDecrypt] as? String,
           let key = jsonDict[StringConstants.Generic.key] as? String {
            let decryptedValue = CryptoSwiftManager.shared.decryptContent(dataToDecrypt: dataToDecrypt, keyStr: key)
            let decryptId = jsonDict[StringConstants.Decrypt.decryptId]
                as? String ?? StringConstants.Generic.emptyString
            let resultKeys = [StringConstants.Decrypt.decryptId, StringConstants.Generic.text]
            let resultValues = [decryptId, decryptedValue]
            callBack(resultKey: resultKeys, resultValue: resultValues, status: true)
            APZLogger.log(logLvl: "I", message: "APZDecrypt--success")
        } else {
            callBack(resultKey: [StringConstants.Generic.errorCode],
                     resultValue: [StringConstants.FileDecryption.failCode],
                     status: false)
            APZLogger.log(logLvl: "E", message: "APZDecrypt--AES Decryption error")
        }
    }
    // MARK: Callback Method
    fileprivate func callBack(resultKey: [String], resultValue: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKey,
                                                                    responseValues: resultValue)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
}
