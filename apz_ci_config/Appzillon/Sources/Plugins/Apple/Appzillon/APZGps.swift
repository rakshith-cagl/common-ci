// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import CoreLocation

class APZGps: APZPlugin, APZLocationDelegate, CLLocationManagerDelegate {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var jsonDict: [AnyHashable: Any] = [:]
    var locationPermission: APZLocationPermission?
    var timeBasedGPSActivated: Bool = false
    var locationManagerGPS: CLLocationManager?
    var onChangeGPS: Bool = false
    var gpsRunning: Bool = false
    var timeAcc: NSNumber?
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "APZGps--startExecute")
        self.jsonDict = jsonDict
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        locationPermission = APZLocationPermission.init(delegate: self)
        locationPermission?.askPermission()
    }
    func cleanPlugin() {
        locationPermission = nil
        locationManagerGPS = nil
        if self.delegate != nil {
            self.delegate.donePlugin(self)
        }
    }
    func locationPermission(_ permission: Bool) {
        if permission {
            callGPS()
        } else {
            errorCallBack(resultMsg: StringConstants.Sensor.gpsOffError)
            APZLogger.log(logLvl: "E", message: "APZGps--Enable Location Services")
            cleanPlugin()
        }
    }
    func callGPS() {
        if CLLocationManager.locationServicesEnabled() {
            if locationManagerGPS == nil {
                gpsRunning = false
                locationManagerGPS = CLLocationManager()
                locationManagerGPS?.delegate = self
                if jsonDict[StringConstants.GPS.periodicity] as? String == StringConstants.GPS.onChange {
                    onChangePeriodicity()
                } else if jsonDict[StringConstants.GPS.periodicity] as? String == StringConstants.GPS.timed {
                    timedPeriodicity()
                } else {
                    nonePeriodicity()
                }
            } else {
                gpsRunning = true
                errorCallBack(resultMsg: StringConstants.Sensor.gpsRunningError, keepAlive: true)
                APZLogger.log(logLvl: "E", message: "APZGps--GPS is running already")
            }
        } else {
            errorCallBack(resultMsg: StringConstants.Sensor.gpsOffError)
            APZLogger.log(logLvl: "E", message: "APZGps--Application don't have access to run GPS")
            cleanPlugin()
        }
    }
    func onChangePeriodicity() {
        locationManagerGPS?.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        onChangeGPS = true
        locationManagerGPS?.requestAlwaysAuthorization()
        locationManagerGPS?.startUpdatingLocation()
    }
    func nonePeriodicity() {
        locationManagerGPS?.desiredAccuracy = kCLLocationAccuracyBest
        onChangeGPS = false
        locationManagerGPS?.requestAlwaysAuthorization()
        locationManagerGPS?.startUpdatingLocation()
    }
    func timedPeriodicity() {
        let tempDistanceAcc = Int(jsonDict[StringConstants.GPS.distanceAccuracy] as? String ?? "0") ?? 0
        let tempTimeAcc = Int(jsonDict[StringConstants.GPS.timeAccuracy] as? String ?? "0") ?? 0
        if tempDistanceAcc == 0 && tempTimeAcc == 0 {
            errorCallBack(resultMsg: StringConstants.Sensor.invalidTimeInterval)
            APZLogger.log(logLvl: "E", message: "APZGps--Atleast one Entry is required from Time or Distance")
            cleanPlugin()
        } else {
            onChangeGPS = true
            if jsonDict[StringConstants.GPS.timeAccuracy] as? String == StringConstants.Generic.emptyString {
                timeBasedGPSActivated = false
            } else {
                timeBasedGPSActivated = true
                var timeInt = tempTimeAcc
                timeInt /= 1000
                let tempTimeInterval = TimeInterval(timeInt)
                DispatchQueue.global(qos: .default).async(execute: {
                    let timer = Timer.scheduledTimer(timeInterval: tempTimeInterval,
                                                     target: self, selector: #selector(self.startTracking),
                                                     userInfo: nil, repeats: true)
                    RunLoop.current.add(timer, forMode: .default)
                    RunLoop.current.run()
                })
            }
            handleAccuracy(tempDistanceAcc: tempDistanceAcc)
            locationManagerGPS?.requestAlwaysAuthorization()
            locationManagerGPS?.startUpdatingLocation()
        }
    }
    func handleAccuracy(tempDistanceAcc: Int) {
        if jsonDict[StringConstants.GPS.periodicity] as? String == StringConstants.GPS.onChange {
            locationManagerGPS?.desiredAccuracy = kCLLocationAccuracyBestForNavigation
            onChangeGPS = true
        } else if jsonDict[StringConstants.GPS.periodicity] as? String == StringConstants.Generic.none {
            locationManagerGPS?.desiredAccuracy = kCLLocationAccuracyBest
            onChangeGPS = false
        } else if tempDistanceAcc==0 {
            onChangeGPS = true
            locationManagerGPS?.desiredAccuracy = kCLLocationAccuracyBest
        } else if tempDistanceAcc<10 {
            onChangeGPS = true
            locationManagerGPS?.desiredAccuracy = kCLLocationAccuracyBestForNavigation
        } else if tempDistanceAcc==10 {
            onChangeGPS = true
            locationManagerGPS?.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
        } else if tempDistanceAcc<=100 {
            onChangeGPS = true
            locationManagerGPS?.desiredAccuracy = kCLLocationAccuracyHundredMeters
        } else if tempDistanceAcc<=1000 {
            onChangeGPS = true
            locationManagerGPS?.desiredAccuracy = kCLLocationAccuracyKilometer
        } else {
            onChangeGPS = true
            locationManagerGPS?.desiredAccuracy = kCLLocationAccuracyThreeKilometers
        }
    }
    @objc func startTracking() {
        if gpsRunning {
            locationManagerGPS?.stopUpdatingLocation()
            gpsRunning = false
        } else {
            locationManagerGPS?.requestAlwaysAuthorization()
            locationManagerGPS?.startUpdatingLocation()
            gpsRunning = true
        }
    }
    override func stop(_ jsonDict: [AnyHashable: Any]!) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        if timeBasedGPSActivated {
            CFRunLoopStop(CFRunLoopGetCurrent())
        }
        if locationManagerGPS == nil {
            APZLogger.log(logLvl: "E", message: "APZGps--GPS is not running")
            errorCallBack(resultMsg: StringConstants.Sensor.gpsStoppedError)
            cleanPlugin()
        } else {
            locationManagerGPS?.stopUpdatingLocation()
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: [],
                                                                        responseValues: [])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            APZLogger.log(logLvl: "E", message: "APZGps--GPS stopped")
            cleanPlugin()
        }
    }
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let resultKeys = [StringConstants.Generic.lattitude, StringConstants.Generic.longitude,
                          StringConstants.GPS.altitude, StringConstants.GPS.accuracy,
                          StringConstants.GPS.altitudeAcc, StringConstants.GPS.speed]
        let userLocation: CLLocation = locations[0] as CLLocation
        let result = [String(format: "%f", userLocation.coordinate.latitude),
                      String(format: "%f", userLocation.coordinate.longitude),
                      String(format: "%f", userLocation.altitude),
                      String(format: "%f", userLocation.horizontalAccuracy),
                      String(format: "%f", userLocation.verticalAccuracy),
                      String(format: "%f", userLocation.speed)]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: true,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: result)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        if !onChangeGPS {
            manager.stopUpdatingLocation()
        }
    }
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        locationManagerGPS?.stopUpdatingLocation()
        errorCallBack(resultMsg: StringConstants.Sensor.gpsNetworkOff)
        APZLogger.log(logLvl: "E", message: error.localizedDescription)
        cleanPlugin()
    }
    func errorCallBack(resultMsg: String, keepAlive: Bool? = false) {
        let resultkeys = [StringConstants.Generic.errorCode]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: keepAlive ?? false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: [resultMsg])
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
