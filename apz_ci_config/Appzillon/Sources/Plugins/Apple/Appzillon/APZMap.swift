// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZMap: APZPlugin, APZMapDelegate {
    var viewController: AppzillonViewController?
    var webView: WKWebView
    var mapViewController = APZMapViewController.init()
    var pluginId = StringConstants.Generic.emptyString
    override init(plugin webView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = webView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "I", message: "APZMap--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        if let markerInfo = jsonDict["markerInfo"] as? [AnyHashable] {
            showMap(mapLocation: markerInfo)
        }
    }
    // MARK: Plugin Clean
    func cleanPlugin(interfaceOrientation: UIInterfaceOrientation) {
        APZLogger.log(logLvl: "I", message: "APZMap--Done")
        self.delegate.donePluginWithOrientaion(self, interfaceOrientation)
        self.mapViewController.delegate = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Maps Method
    func showMap(mapLocation: [AnyHashable]) {
        if CLLocationManager.locationServicesEnabled() {
            mapViewController.delegate = self
            mapViewController.setMarkUpLocations(locations: mapLocation)
            let navigationController = UINavigationController.init(rootViewController: mapViewController)
            navigationController.modalPresentationStyle = .fullScreen
            viewController?.present(navigationController, animated: true, completion: nil)
        } else {
            callBack(status: false, resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.Sensor.gpsOffError])
            APZLogger.log(logLvl: "E", message: "APZMap--Enable Location Services")
        }
    }
    func mapCancelled(_ interfaceOrientation: UIInterfaceOrientation) {
        callBack(status: false, resultKeys: [StringConstants.Generic.errorMessage], resultValues: ["Map Cancelled"])
        APZLogger.log(logLvl: "E", message: "APZMap--Cancelled")
        cleanPlugin(interfaceOrientation: interfaceOrientation)
    }
    func getLocationCordinates(_ latitude: String, longitude: String,
                               with interfaceOrientation: UIInterfaceOrientation) {
        viewController?.dismiss(animated: true, completion: nil)
        callBack(status: true, resultKeys: ["latitude", "longitude"], resultValues: [latitude, longitude])
        APZLogger.log(logLvl: "E", message: "APZMap--Success")
        cleanPlugin(interfaceOrientation: interfaceOrientation)
    }
    // MARK: CallBack Method
    fileprivate func callBack(status: Bool, resultKeys: [String], resultValues: [Any]) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
