//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZFileDownload: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var fileName: String = StringConstants.Generic.emptyString
    var destinationPath: String = StringConstants.Generic.emptyString
    var base64Enabled: String = StringConstants.Generic.emptyString
    var serverURL: String = StringConstants.Generic.emptyString
    var sessionReq: String = StringConstants.Generic.emptyString
    var jsonDict: [AnyHashable: Any]
    
    //MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable : Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        self.jsonDict = jsonDict
        super.init(plugin: webView, jsonDict)
    }
    
    override func execute(_ jsonDict: [AnyHashable : Any]) {
        APZLogger.log(logLvl: "D", message: "APZFileDownload--execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        base64Enabled = jsonDict["base64"] as? String ?? StringConstants.Generic.emptyString
        serverURL = viewController?.apzServerURL ?? StringConstants.Generic.emptyString
        fileName = (jsonDict["fileName"] as? String ?? StringConstants.Generic.emptyString).replacingOccurrences(of: "\n", with: "")
        let filePath = jsonDict["filePath"] as? String ?? StringConstants.Generic.emptyString
        sessionReq = jsonDict["sessionReq"] as? String ?? StringConstants.Generic.emptyString
        destinationPath = jsonDict["destinationPath"] as? String ?? StringConstants.Generic.emptyString
        
        let isSessionRequired = (sessionReq == StringConstants.Generic.yes)
        let appzillonBodyKey = isSessionRequired ? "appzillonFilePushServiceRequest" : "appzillonFilePushServiceWSRequest"
        
        var apzBodyDictionary: [AnyHashable : Any] = [:]
        var appzillonFilePushServiceRequest: [AnyHashable : Any] = [:]
        appzillonFilePushServiceRequest["fileName"] = fileName
        appzillonFilePushServiceRequest["filePath"] = filePath
        appzillonFilePushServiceRequest["base64"] = StringConstants.Generic.yes
        apzBodyDictionary[appzillonBodyKey] = NSMutableDictionary(dictionary: appzillonFilePushServiceRequest)
        
        
        var appzillonRequest: [AnyHashable : Any] = [:]
        let apzHeaderDictionary = jsonDict["appzillonHeader"] as? [AnyHashable : Any] ?? [:]
        appzillonRequest["appzillonHeader"] = NSMutableDictionary(dictionary: apzHeaderDictionary)
        appzillonRequest["appzillonBody"] = NSMutableDictionary(dictionary: apzBodyDictionary)
        if let appID = apzHeaderDictionary[StringConstants.Generic.appId] as? String {
            CallServer.callServer(withRequest: NSMutableDictionary(dictionary: appzillonRequest),
                                  serverURL,
                                  appID,
                                  self.viewController) { [weak self] status, responseDict in
                if status {
                    self?.downloadRequestSuccess(messageDict: responseDict ?? [:])
                } else {
                    self?.downloadRequestFail(messageDict: responseDict ?? [:])
                }
            }
            APZLogger.log(logLvl: "I", message: "APZFileDownload--file Request is sent")
        }
    }
    
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZVibrate--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    
    
    //MARK: Server Callback Methods
    func downloadRequestSuccess(messageDict: [AnyHashable : Any]) {
        APZLogger.log(logLvl: "I", message: "APZFileDownload--response arrived")
        let downloadUtil = APZFileDownloadUtil(webView: webView, jsonDict, viewController: viewController)
        downloadUtil.downloadSuccess(messageDict: messageDict)
        cleanPlugin()
    }
    
    func downloadRequestFail(messageDict: [AnyHashable : Any]) {
        APZLogger.log(logLvl: "E", message: "APZFileDownload-- error")
        let downloadUtil = APZFileDownloadUtil(webView: webView, jsonDict, viewController: viewController)
        downloadUtil.downloadFailed(messageDict: messageDict)
        cleanPlugin()
    }
}
