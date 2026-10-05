//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

@objc class APZFileDownloadUtil: NSObject {
    var viewController: AppzillonViewController?
    var webView: WKWebView
    var jsonDict: [AnyHashable: Any]
    @objc init(webView: WKWebView, _ jsonDict: [AnyHashable: Any], viewController: AppzillonViewController?) {
        self.webView = webView
        self.jsonDict = jsonDict
        self.viewController = viewController
    }
    @objc func downloadSuccess(messageDict: [AnyHashable: Any]) {
        let base64Enabled = jsonDict["base64"] as? String ?? StringConstants.Generic.emptyString
        let fileName = jsonDict["fileName"] as? String ?? StringConstants.Generic.emptyString
        let sessionReq = jsonDict["sessionReq"] as? String ?? StringConstants.Generic.emptyString
        let destinationPath = jsonDict["destinationPath"] as? String ?? StringConstants.Generic.emptyString
        let appzillonHeaderDict = messageDict[StringConstants.SecureStorage.appzillonHeader] as? [AnyHashable: Any] ?? [:]
        if let status = appzillonHeaderDict[StringConstants.SecureStorage.status] as? NSNumber, status.boolValue {
            let appzillonBodyDict = messageDict["appzillonBody"] as? [AnyHashable: Any] ?? [:]
            let isSessionRequired = (sessionReq == StringConstants.Generic.yes)
            let apzBodyResponseKey = isSessionRequired ? "appzillonFilePushServiceResponse" : "appzillonFilePushServiceWSResponse"
            let downloadResponseDict: [AnyHashable: Any] = appzillonBodyDict[apzBodyResponseKey] as? [AnyHashable: Any] ?? [:]
            if base64Enabled == StringConstants.Generic.yes {
                sendCallback(resultKeys: [StringConstants.Generic.base64],
                             resultValues: [downloadResponseDict[StringConstants.Generic.file] as? String ?? StringConstants.Generic.emptyString],
                             status: true)
                APZLogger.log(logLvl: "I", message: "APZFileDownload--base64 success")
            } else {
                if let respDict = downloadResponseDict as? [String: Any],
                   let fileDataStr = respDict[StringConstants.Generic.file] as? String {
                    let fileData = Base64.decode(fileDataStr)
                    let downloadedFolderPath = createFolderForDownloadFiles(destinationFolder: destinationPath)
                    let finalDestinationPath = downloadedFolderPath + String(format: "/%@", fileName)
                    if FileManagerUtility.write(fileData, toFile: URL(fileURLWithPath: finalDestinationPath)) {
                        APZLogger.log(logLvl: "I", message: "APZFileDownload--file write success")
                        sendCallback(resultKeys: [StringConstants.Generic.filePath],
                                     resultValues: [finalDestinationPath],
                                     status: true)
                    }
                }
            }
        } else {
            let errorArray = messageDict[StringConstants.SecureStorage.appzillonErrors] as? Array ?? []
            if let errorDict = errorArray.first as? [AnyHashable: Any],
               let errorCode = errorDict[StringConstants.Generic.errorCode] as? String,
               let errorMsg = errorDict[StringConstants.Generic.errorMessage] as? String {
                sendCallback(resultKeys: [StringConstants.Generic.errorCode], resultValues: [errorCode], status: false)
                APZLogger.log(logLvl: "E", message: String(format: "APZFileDownload--%@", errorMsg))
            }
        }
    }
    @objc func downloadFailed(messageDict: [AnyHashable: Any]) {
        let errorCode = messageDict[StringConstants.Generic.errorCode] as? String ?? StringConstants.Generic.emptyString
        sendCallback(resultKeys: [StringConstants.Generic.errorCode], resultValues: [errorCode], status: false)
        APZLogger.log(logLvl: "E", message: "APZFileDownload--network error")
    }
    // MARK: Utility Methods
    private func sendCallback(resultKeys: [String], resultValues: [Any], status: Bool) {
        let pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
    private func createFolderForDownloadFiles(destinationFolder: String) -> String {
        let appString = viewController?.appString ?? StringConstants.Generic.emptyString
        let appzillonAppSandboxPath = FileManagerUtility.documentDirectory().appendingPathComponent("Assets/apps/\(appString)/").path
        let folderName = destinationFolder.isEmpty ? "downloads" : destinationFolder
        let downloadedFolderPath = appzillonAppSandboxPath + String(format: "/%@", folderName)
        if !FileManager.default.fileExists(atPath: downloadedFolderPath) {
            do {
                try FileManager.default.createDirectory(at: URL(fileURLWithPath: downloadedFolderPath), withIntermediateDirectories: true, attributes: nil)
            } catch {
                APZLogger.log(logLvl: "E", message: "APZFileDownload--could not create \(folderName) directory")
            }
        }
        return downloadedFolderPath
    }
}
