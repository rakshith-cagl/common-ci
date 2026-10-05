// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZEncrypt: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: wbView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "APZEncrypt--execute")
        if !jsonDict.isEmpty {
            self.pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            aesEncrypt(jsonDict: jsonDict)
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "D", message: "APZEncrypt--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
}
    // MARK: AES Encryption
    fileprivate func aesEncrypt(jsonDict: [AnyHashable: Any]) {
        if let dataToEncrypt = jsonDict[StringConstants.Encrypt.stringToEncrypt],
          let key = jsonDict[StringConstants.Generic.key] as? String {
            let encryptedValue = CryptoSwiftManager.shared.encryptedContent(dataToEncrypt: dataToEncrypt, keyStr: key)
            let encryptId = jsonDict[StringConstants.Encrypt.encryptId]
                as? String ?? StringConstants.Generic.emptyString
            let resultKey = [StringConstants.Encrypt.encryptId, StringConstants.Generic.text]
            let resultValue = [encryptId, encryptedValue]
            callBack(resultKey: resultKey, resultValue: resultValue, status: true)
            APZLogger.log(logLvl: "I", message: "APZEncrypt--success")
       } else {
           callBack(resultKey: [StringConstants.Generic.errorCode],
                    resultValue: [StringConstants.FileEncryption.failCode],
                    status: false)
        APZLogger.log(logLvl: "E", message: "APZEncrypt--AES Encryption error")
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
