//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZFileUpload: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var filePath: String = StringConstants.Generic.emptyString
    var fileID: String = StringConstants.Generic.emptyString
    var sessionReq: String = StringConstants.Generic.emptyString
    var jsonDict: [AnyHashable: Any]
    
    //MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable : Any]) {
        self.webView = wbView
        self.viewController =  MiscellaneousMethod.shared.getAppzillonViewController()
        self.jsonDict = jsonDict
        super.init(plugin: webView, jsonDict)
    }

    override func execute(_ jsonDict: [AnyHashable : Any]) {
        APZLogger.log(logLvl: "D", message: "APZFileUpload--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        if !jsonDict.isEmpty {
            let fileManager = FileManager.default
            let path = jsonDict["fieldID"] as? NSString ?? ""
            let fileName = path.lastPathComponent
            var overWrite = StringConstants.Generic.no
            if let overWriteFromJSON = jsonDict["overWrite"] as? String,
               overWriteFromJSON == StringConstants.Generic.yes {
                overWrite = overWriteFromJSON
            }
            
            let jsonHeader = jsonDict["appzillonHeader"] as? [AnyHashable : Any] ?? [:]
            sessionReq = jsonDict["sessionReq"] as? String ?? StringConstants.Generic.emptyString
            let urlString = viewController?.apzServerURL ?? StringConstants.Generic.emptyString
            let serverUrl = urlString.appending("/upload")
            if fileManager.fileExists(atPath: path as String) {
                let destDirectory = jsonDict["destination"] as? String ?? StringConstants.Generic.emptyString
                var jsonBody: [AnyHashable : Any] = [:]
                jsonBody["destination"] = destDirectory
                jsonBody["overWrite"] = overWrite
                
                //Changes for custom interfaceID in fileUpload plugin
                if let customIntId = jsonDict["customInterfaceId"] as? String, !customIntId.isEmpty {
                    jsonBody["interfaceId"] = customIntId
                }

                var fileDetails: [[AnyHashable : Any]] = []
                var fileDetailElement: [AnyHashable : Any] = [:]
                fileDetailElement["fileName"] = fileName
                fileDetailElement["fileSize"] = NSNumber(value: getFileSize(filePath:path as String))
                fileDetailElement["fileNo"] = NSNumber(value: 1)
                fileDetailElement["fileType"] = mimeTypeForFile(path: path as String)
                fileDetails.append(fileDetailElement)
                
                jsonBody["fileDetails"] = fileDetails
                var appzillonRequest: [AnyHashable : Any] = [:]
                appzillonRequest["appzillonHeader"] = NSMutableDictionary(dictionary: jsonHeader)
                appzillonRequest["appzillonBody"] = NSMutableDictionary(dictionary: jsonBody)
                let appID = jsonHeader[StringConstants.Generic.appId] as? String ?? ""
                CallServer.callServerForUpload(withRequest: NSMutableDictionary(dictionary: appzillonRequest),
                                               appID,
                                               serverUrl,
                                               path as String,
                                               fileName,
                                               mimeTypeForFile(path: path as String),
                                               viewController) {[weak self] status, responseDictionary in
                    if status {
                        self?.uploadRequestSuccess(messageDict: responseDictionary ?? [:])
                    } else {
                        self?.uploadRequestFail(messageDict: responseDictionary ?? [:])
                    }
                }
            }
        } else {
            cleanPlugin()
        }
    }
    
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZFileUpload--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    
    
    //MARK: Server Callback Methods
    func uploadRequestSuccess(messageDict: [AnyHashable : Any]) {
        APZLogger.log(logLvl: "I", message: "APZFileUpload--response arrived")
        let uploadUtil = APZFileUploadUtil(webView: webView, jsonDict, viewController: viewController)
        uploadUtil.uploadRequestSuccess(messageDict: messageDict)
        cleanPlugin()
    }
    
    func uploadRequestFail(messageDict: [AnyHashable : Any]) {
        APZLogger.log(logLvl: "E", message: "APZFileUpload-- error")
        let uploadUtil = APZFileUploadUtil(webView: webView, jsonDict, viewController: viewController)
        uploadUtil.uploadFailed(messageDict: messageDict)
        cleanPlugin()
    }
    
    //MARK: Utility Methods
    @objc func getFileSize(filePath: String) -> UInt64 {
        var fileSize: UInt64 = 0
        do {
            let attr = try FileManager.default.attributesOfItem(atPath: filePath)
            let dict = attr as NSDictionary
            fileSize = dict.fileSize()
            return fileSize
        } catch {
            print("Error: \(error)")
            return fileSize
        }
    }
    @objc func mimeTypeForFile(path: String) -> String {
        if let url = URL(string: path) {
            let pathExtension = url.pathExtension
            if let uti = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, pathExtension as NSString, nil)?.takeRetainedValue(), let mimetype = UTTypeCopyPreferredTagWithClass(uti, kUTTagClassMIMEType)?.takeRetainedValue() {
                return mimetype as String
            }
        }
        return "application/octet-stream"
    }
}
