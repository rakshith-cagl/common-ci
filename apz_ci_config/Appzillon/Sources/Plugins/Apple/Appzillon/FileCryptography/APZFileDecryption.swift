// swiftlint:disable all
// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZFileDecryption: APZPlugin {
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
        APZLogger.log(logLvl: "I", message: "APZFileDecryption--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        decryptFileData(jsonDict: jsonDict)
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZFileDecryption--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Utility Methods
    private func decryptFileData(jsonDict: [AnyHashable: Any]) {
        let fileSourcePath = jsonDict[StringConstants.Generic.sourcePath]
            as? String ?? StringConstants.Generic.emptyString
        if FileManager.default.fileExists(atPath: fileSourcePath) {
            do {
                let keyStr = jsonDict[StringConstants.Generic.key] as? String ?? StringConstants.Generic.emptyString
                let fileData = try Data(contentsOf: URL(fileURLWithPath: fileSourcePath))
                let key = FileManagerUtility.checkKeyLength(keyStr:
                                                                keyStr,
                                                            encodingType: 16) ?? ""
                if let decryptedFileData = CryptoSwiftManager.shared.decryptDataWithAES(key: key, dataToDecrypt: fileData) {
                    let destinationPath = jsonDict[StringConstants.Generic.destinationPath]
                        as? String ?? StringConstants.Generic.emptyString
                    writeFileToDestination(destinationPath: destinationPath, decryptedData: decryptedFileData)
                } else {
                    APZLogger.log(logLvl: "E", message: StringConstants.FileDecryption.failMessage)
                    failureCallback(errorMessage: StringConstants.FileDecryption.failMessage,
                                    errorCode: StringConstants.FileDecryption.failCode)
                }
            } catch let error {
                failureCallback(errorMessage: error.localizedDescription,
                                errorCode: StringConstants.FileDecryption.failCode)
            }
        } else {
            APZLogger.log(logLvl: "E", message: StringConstants.Generic.fileNotExist)
            failureCallback(errorMessage: StringConstants.Generic.fileNotExist,
                            errorCode: StringConstants.Generic.fileNotFoundCode)
        }
    }
    private func writeFileToDestination(destinationPath: String, decryptedData: Data) {
        let appString = self.viewController?.appString ?? ""
        let docDirectory = FileManagerUtility.documentDirectory()
        let finalPathToWrite = docDirectory.appendingPathComponent("\(StringConstants.Generic.sandBoxPath)\(appString)/\(destinationPath)")
        do {
            try FileManager.default.createDirectory(atPath: finalPathToWrite.deletingLastPathComponent().path,
                                                    withIntermediateDirectories: true,
                                                    attributes: nil)
            if FileManagerUtility.write(decryptedData, toFile: finalPathToWrite) {
                APZLogger.log(logLvl: "I", message: StringConstants.FileDecryption.successMessage)
                successCallback(filepath: finalPathToWrite.path)
            } else {
                APZLogger.log(logLvl: "E", message: StringConstants.Generic.fileWriteFailure)
                failureCallback(errorMessage: StringConstants.Generic.fileWriteFailure,
                                errorCode: StringConstants.FileDecryption.failCode)
            }
        } catch let error {
            failureCallback(errorMessage: error.localizedDescription,
                            errorCode: StringConstants.FileDecryption.failCode)
        }
    }
    // MARK: Callback Methods
    private func successCallback(filepath: String) {
        let resultKeys = [StringConstants.Generic.filePath]
        let resultValues = [filepath]
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
    private func failureCallback(errorMessage: String, errorCode: String) {
        let resultKeys = [StringConstants.Generic.errorMessage, StringConstants.Generic.errorCode]
        let resultValues = [errorMessage, errorCode]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
}
// swiftlint:enable all
