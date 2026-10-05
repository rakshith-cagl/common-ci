//
//  APZPullDown.swift
//  Appzillon
//
//  Created by Thanmai M S on 11/16/21.
//

import Foundation

class APZPullDown: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var refreshControl = UIRefreshControl()
    var screenIdstr: String = StringConstants.Generic.emptyString
    var callIdstr: String = StringConstants.Generic.emptyString
    // MARK: Plugin Life Cycle Method
    override init(plugin webView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = webView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        self.pullDown(jsonDict: jsonDict)
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "D", message: "PullDown To Refresh Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: PullDown Method
    func pullDown(jsonDict: [AnyHashable: Any]) {
        let refreshStr  = jsonDict[StringConstants.PullDown.refreshStatus]
            as? String ?? StringConstants.Generic.emptyString
        if refreshStr == StringConstants.PullDown.enable {
            self.screenIdstr = jsonDict[StringConstants.PullDown.screenId]
                as? String ?? StringConstants.Generic.emptyString
            self.callIdstr = jsonDict[StringConstants.PullDown.callId]
                as? String ?? StringConstants.Generic.emptyString
            self.refreshControl.addTarget(self, action: #selector(enablePullDown), for: .valueChanged)
            self.webView.scrollView.addSubview(self.refreshControl)
            self.webView.scrollView.bounces = true
            self.callBack(resultKeys: [StringConstants.Generic.cbEvent],
                          resultValues: [StringConstants.Generic.started],
                          status: true,
                          keepAlive: true)
        } else if refreshStr == StringConstants.PullDown.hideRefresh {
            refreshControl.endRefreshing()
            self.webView.setNeedsDisplay()
            self.callBack(resultKeys: [StringConstants.Generic.success],
                          resultValues: [StringConstants.PullDown.refreshHiddenSuccess],
                          status: true,
                          keepAlive: true)
        } else {
            self.refreshControl.removeFromSuperview()
            self.refreshControl.endRefreshing()
            self.webView.scrollView.bounces = false
            self.callBack(resultKeys: [StringConstants.Generic.cbEvent],
                          resultValues: [StringConstants.Generic.stopped],
                          status: true,
                          keepAlive: false)
            self.cleanPlugin()
        }
    }
    @objc func enablePullDown() {
        let myAttribute = [NSAttributedString.Key.foregroundColor: UIColor.darkGray]
        self.refreshControl.attributedTitle = NSAttributedString(string: StringConstants.PullDown.refreshing,
                                                                 attributes: myAttribute)
        self.refreshControl.beginRefreshing()
        let resultKeys = [StringConstants.PullDown.screenId,
                          StringConstants.PullDown.callId,
                          StringConstants.Generic.cbEvent]
        let resultValues = [self.screenIdstr, self.callIdstr, StringConstants.PullDown.pluginName]
        self.callBack(resultKeys: resultKeys, resultValues: resultValues, status: true, keepAlive: true)
        }
    // MARK: CallBack Method
    func callBack(resultKeys: [String], resultValues: [Any], status: Bool, keepAlive: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: keepAlive,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
