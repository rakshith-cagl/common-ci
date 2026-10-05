//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import Alamofire

class APZNetworkMonitor {
    
    static let shared = APZNetworkMonitor()
    private init(){}
    let manager = NetworkReachabilityManager(host: "www.apple.com")
    
    func startMonitoring(pluginid: String, wbView: WKWebView) {
        manager?.startListening(onQueue: DispatchQueue.main, onUpdatePerforming: { (status) in
            switch status {
            case .notReachable:
                self.sendCallBack(pluginid: pluginid, resultValues: ["off"], webView: wbView)
            case .reachable(.ethernetOrWiFi), .reachable(.cellular):
                self.sendCallBack(pluginid: pluginid, resultValues: ["on"], webView: wbView)
            default:
                break
            }
        })
    }
    
    func stopMonitoring() {
        manager?.stopListening()
    }

    fileprivate func sendCallBack(pluginid: String, resultValues: [Any], webView: WKWebView) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginid,
                                                                    status: true,
                                                                    keepAlive: true,
                                                                    responseKeys: [StringConstants.Generic.cbEvent],
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}

