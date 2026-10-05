//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZModifyShortCutItems: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    
    // MARK: - Plugin LifeCycle Methods
    
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable : Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: wbView, jsonDict)
    }
    
    override func execute(_ jsonDict: [AnyHashable : Any]) {
        APZLogger.log(logLvl: "D", message: "APZModifyShortCutItems--execute")
        if !jsonDict.isEmpty {
            self.pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            setShortCutItems(jsonDict: jsonDict)
        }else{
            cleanPlugin()
        }
    }
    
    func cleanPlugin() {
        APZLogger.log(logLvl: "D", message: "APZModifyShortCutItems--Done")
        self.viewController = nil;
        self.delegate.donePlugin(self)
    }
    
    //MARK: AES Decrypt Method
    fileprivate func setShortCutItems(jsonDict: [AnyHashable : Any]) {
        if let shortcutItemsArray = jsonDict["shortcutItems"] as? [[String: String]], !shortcutItemsArray.isEmpty {
            var shortCutItems: [UIApplicationShortcutItem] = []
            for item in shortcutItemsArray {
                let id = item["shortcutID"] ?? StringConstants.Generic.emptyString
                let title = item["title"] ?? StringConstants.Generic.emptyString
                let subtitle = item["subtitle"] ?? StringConstants.Generic.emptyString
                let iconfilename = item["iconfilename"] ?? StringConstants.Generic.emptyString
                let shortCutitem = UIApplicationShortcutItem(type: id, localizedTitle: title, localizedSubtitle: subtitle, icon: UIApplicationShortcutIcon(templateImageName: iconfilename))
                shortCutItems.append(shortCutitem)
            }
            UIApplication.shared.shortcutItems = shortCutItems
            callBack(resultKey: [], resultValue: [], status: true)
            APZLogger.log(logLvl: "I", message: "APZModifyShortCutItems--success")
        } else {
            callBack(resultKey: [StringConstants.Generic.errorMessage],
                     resultValue: ["Set Shortcut Items Failed"],
                     status: false)
            APZLogger.log(logLvl: "E", message: "APZModifyShortCutItems--Generic error")
        }
    }
    
    //MARK: Callback Method
    fileprivate func callBack(resultKey: [String], resultValue: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKey,
                                                                    responseValues: resultValue)
        MiscellaneousMethod.shared.jsLayerCall(webView: self.webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
}
