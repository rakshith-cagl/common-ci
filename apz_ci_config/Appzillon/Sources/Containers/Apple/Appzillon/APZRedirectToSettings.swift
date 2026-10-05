//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZRedirectToSettings: APZPlugin {
    var webView: WKWebView      
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    
    //MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable : Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    
    override func execute(_ jsonDict: [AnyHashable : Any]) {
        APZLogger.log(logLvl: "D", message: "APZRedirectToSettings--startExecute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        redirectUserToDeviceSettings()
    }
    
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZRedirectToSettings--Done")
        self.delegate.donePlugin(self)
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    
    //MARK: Utility Method
    fileprivate func redirectUserToDeviceSettings() {
        if let url = URL(string: UIApplication.openSettingsURLString){
            UIApplication.shared.open(url, options: [:], completionHandler: { (success) in
                if success {
                    self.callBack(status: true, resultKeys: [StringConstants.Generic.text], resultValues: [StringConstants.GpsStatus.redirectSuccess])
                } else {
                    self.callBack(status: false, resultKeys: [StringConstants.Generic.errorMessage], resultValues: [StringConstants.GpsStatus.redirectFailed])
                }
            })
        }
    }
    
    //MARK: CallBack Method

    func callBack(status: Bool, resultKeys: [String], resultValues: [Any]) {
        let param = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId, status: status, keepAlive: false,
                                                                   responseKeys: resultKeys, responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod, parameter: param)
        
            cleanPlugin()
        }
}

