import Foundation
import CoreTelephony

class APZGetSimDetails: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    
    // MARK: - Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable : Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    
    override func execute(_ jsonDict: [AnyHashable : Any]) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            checkAndGetSimCardDetails()
    }
    
    func cleanPlugin() {
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    
    // MARK: - HasCellularCoverage Method
    func checkAndGetSimCardDetails() {
        let networkInfo = CTTelephonyNetworkInfo()
        var info: [AnyHashable: Any] = [:]
        if #available(iOS 12.0, *) {
            if let cellularProvider = networkInfo.serviceSubscriberCellularProviders {
                info = cellularProvider
            }
        } else {
            info["0000000100000001"] = networkInfo.subscriberCellularProvider
        }
        let allKeys = info.keys
        var isSimCardAvailable = false
        var multiNetworkArrDetails: [[AnyHashable: Any]] = []
        for key in allKeys {
            guard let key = key as? String else { return }
            var networkDetails: [AnyHashable: Any] = [:]
            if let carrier = info[key] as? CTCarrier, let networkCode = carrier.mobileNetworkCode {
                isSimCardAvailable = true
                networkDetails["networkCode"] = networkCode
                networkDetails["allowsVOIP"] = carrier.allowsVOIP
                networkDetails["mobileNetworkCode"] = carrier.mobileNetworkCode
                networkDetails["isoCountryCode"] = carrier.isoCountryCode
                networkDetails["simNetworkName"] = carrier.carrierName
                multiNetworkArrDetails.append(networkDetails)
            }
        }
        
        if isSimCardAvailable {
            if !multiNetworkArrDetails.isEmpty {
                callBack(resultKeys: ["simDetails"], resultValues: [multiNetworkArrDetails], status: true)
            } else {
                callBack(resultKeys: ["failure"], resultValues: ["Sim not Detected"], status: true)
            }
        } else {
            callBack(resultKeys: ["failure"], resultValues: ["Sim not Detected"], status: false)
        }
    }
    
    //MARK: Callback Method
    fileprivate func callBack(resultKeys: [String], resultValues: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
}
