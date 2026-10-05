// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZBase64File: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin Life Cycle Method
    override init(plugin webView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = webView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "APZBase64File--Execute")
        if !jsonDict.isEmpty, let action = jsonDict[StringConstants.Generic.action] as? String {
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            if action == StringConstants.Base64_File.filetoB64 {
                convertFiletoBase64(jsonDict: jsonDict)
            } else {
                convertBase64toFile(jsonDict: jsonDict)
            }
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZBase64File--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: convertFiletoBase64 Method
    fileprivate func convertFiletoBase64(jsonDict: [AnyHashable: Any]) {
        if let filePath = jsonDict[StringConstants.Generic.filePath] as? String,
           let fileData = NSData(contentsOfFile: filePath) {
            let base64String = Base64.encode(fileData as Data)
            callBack(resultKeys: [StringConstants.Generic.text],
                     resultValues: [base64String], status: true,
                     keepAlive: false)
        } else {
            callBack(resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.Base64_File.fileToBase64Error],
                     status: false,
                     keepAlive: false)
        }
    }
    // MARK: convertBase64toFile Methods
    fileprivate func convertBase64toFile(jsonDict: [AnyHashable: Any]) {
        if let base64String = jsonDict[StringConstants.Generic.base64] as? String,
           let appString = self.viewController?.appString,
           let fileName = jsonDict[StringConstants.Generic.fileName] as? String,
           !fileName.isEmpty,
           let relativePath = jsonDict[StringConstants.Generic.filePath] as? String {
            let fileNameArray = fileName.split(separator: ".")
            if fileNameArray.count != 2 {
                callBack(resultKeys: [StringConstants.Generic.errorCode],
                         resultValues: [StringConstants.Base64_File.fileNameMissing],
                         status: false,
                         keepAlive: false)
                APZLogger.log(logLvl: "E", message: StringConstants.Base64_File.pathExtensionMissing)
            } else {
                let fileData = Base64.decode(base64String)
                writeFileToDestination(appString, relativePath, fileName, fileData)
            }
        } else {
            callBack(resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.Base64_File.base64ToFileFailed],
                     status: false,
                     keepAlive: false)
            APZLogger.log(logLvl: "E", message: "Base64 to file failed")
        }
    }
    fileprivate func writeFileToDestination(_ appString: String, _ relativePath: String,
                                            _ fileName: String, _ fileData: Data) {
        var filePathUrl = FileManagerUtility.documentDirectory()
        filePathUrl.appendPathComponent("\(StringConstants.Generic.sandBoxPath)\(appString)/\(relativePath)")
        let fileManager = FileManager.default
        do {
            try fileManager.createDirectory(atPath: filePathUrl.path,
                                            withIntermediateDirectories: true,
                                            attributes: nil)
        } catch {
            callBack(resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.Base64_File.directoryCouldNotCreate],
                     status: false,
                     keepAlive: false)
            APZLogger.log(logLvl: "E", message: "Could not create intermediate directory")
        }
        let filePath = filePathUrl.appendingPathComponent("\(fileName)")
        if FileManagerUtility.write(fileData, toFile: filePath) {
            callBack(resultKeys: [StringConstants.Generic.filePath],
                     resultValues: [filePath.path],
                     status: true,
                     keepAlive: false)
            APZLogger.log(logLvl: "I", message: "Base64 to file created successfully")
        } else {
            callBack(resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.Base64_File.base64ToFileFailed],
                     status: false, keepAlive: false)
        }
    }
    // MARK: callBack Method
    fileprivate func callBack(resultKeys: [String], resultValues: [Any], status: Bool, keepAlive: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: keepAlive,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
}
