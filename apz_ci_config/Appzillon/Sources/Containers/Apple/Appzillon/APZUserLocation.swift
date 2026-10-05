//
//  APZUserLocation.swift
//  Appzillon
//
//  Created by Thanmai M S on 10/19/21.
//

import Foundation
import CoreLocation

 class APZUserLocation: APZPlugin, CLLocationManagerDelegate {
    var webVW: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    let locationManager = CLLocationManager()
    var format: String = StringConstants.Generic.emptyString
    var formatLatVal: String = StringConstants.Generic.emptyString
    var formatLongVal: String = StringConstants.Generic.emptyString
    // MARK: - Plugin Life Cycle Method
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webVW = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webVW, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        format = jsonDict["format"] as? String ?? StringConstants.Generic.emptyString
        
        locationManager.requestAlwaysAuthorization()
        locationManager.requestWhenInUseAuthorization()
        if CLLocationManager.locationServicesEnabled() {
            locationManager.delegate = self
            locationManager.desiredAccuracy = kCLLocationAccuracyBest
            locationManager.distanceFilter = kCLDistanceFilterNone
            locationManager.startUpdatingLocation()
        }else{
            sendErrorCallback()
        }
    }
     
    func cleanPlugin() {
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
     fileprivate func sendErrorCallback() {
         let resultKeys = [StringConstants.Generic.errorCode, StringConstants.Generic.errorMessage]
         let resultValues = [StringConstants.UserLocation.gpsServicesOffError, StringConstants.UserLocation.errorMessage]
         let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                     status: false,
                                                                     keepAlive: false,
                                                                     responseKeys: resultKeys,
                                                                     responseValues: resultValues)
         MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                                jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                parameter: params)
         cleanPlugin()
     }
   
    // MARK: - Location Manager Delagates
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location: CLLocation = manager.location else { return }
        CLGeocoder().reverseGeocodeLocation(location) { placemarks, _ in
            let thoroughFare = placemarks?.first?.thoroughfare ?? StringConstants.Generic.emptyString
            let subThoroughFare = placemarks?.first?.subThoroughfare ?? StringConstants.Generic.emptyString
            let locality = placemarks?.first?.locality ?? StringConstants.Generic.emptyString
            let country = placemarks?.first?.country ?? StringConstants.Generic.emptyString
            let postalCode = placemarks?.first?.postalCode ?? StringConstants.Generic.emptyString
            let administrativeArea = placemarks?.first?.administrativeArea ?? StringConstants.Generic.emptyString
            let subLocality = placemarks?.first?.subLocality ?? StringConstants.Generic.emptyString
            let countryCode = placemarks?.first?.isoCountryCode ?? StringConstants.Generic.emptyString
            let locationAddress = "\(subThoroughFare), \(thoroughFare), \(subLocality), \(locality), \(administrativeArea), \(postalCode), \(country), \(countryCode)"
            let latitude: String = "\(Float(location.coordinate.latitude))"
            let longitude: String = "\(Float(location.coordinate.longitude))"
            if self.format != StringConstants.Generic.emptyString {
            let latLongVal = self.latLongFormats(latitude: location.coordinate.latitude,
                                                 longitude: location.coordinate.longitude)
                self.formatLatVal =  latLongVal.latitude
                self.formatLongVal = latLongVal.longitude
            }
            let resultKeys = [StringConstants.Generic.lattitude,
                              StringConstants.Generic.longitude,
                              "formattedLatitude",
                              "formattedLongitude",
                              StringConstants.Generic.address,
                              StringConstants.UserLocation.thoroughFare,
                              StringConstants.UserLocation.subThoroughfare,
                              StringConstants.UserLocation.subLocality,
                              StringConstants.UserLocation.locality,
                              StringConstants.UserLocation.administrativeArea,
                              StringConstants.UserLocation.postalCode,
                              StringConstants.UserLocation.country,
                              StringConstants.UserLocation.countryCode]
            let resultValues = [latitude, longitude, self.formatLatVal, self.formatLongVal,
                                locationAddress, thoroughFare, subThoroughFare, subLocality,
                                locality, administrativeArea, postalCode, country, countryCode]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: self.pluginId,
                                                                       status: true,
                                                                       keepAlive: false,
                                                                       responseKeys: resultKeys,
                                                                       responseValues: resultValues)
            MiscellaneousMethod.shared.jsLayerCall(webView: self.webVW,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            self.cleanPlugin()
        }
    }
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        let resultKeys = [StringConstants.Generic.errorCode, StringConstants.Generic.errorMessage]
        let resultValues = [StringConstants.UserLocation.gpsServicesOffError, StringConstants.UserLocation.errorMessage]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
    func latLongFormats(latitude: Double, longitude: Double) -> (latitude: String, longitude: String) {
        var latitudeMinutes: Double {
            return (latitude * 3600).truncatingRemainder(dividingBy: 3600) / 60 }
        var latitudeSeconds: Double {
            return ((latitude * 3600).truncatingRemainder(dividingBy: 3600)).truncatingRemainder(dividingBy: 60) }
        var longitudeMinutes: Double {
            return (longitude * 3600).truncatingRemainder(dividingBy: 3600) / 60 }
        var longitudeSeconds: Double {
            return ((longitude * 3600).truncatingRemainder(dividingBy: 3600)).truncatingRemainder(dividingBy: 60) }
        switch format {
        case "DD": var degrees: (latitude: String, longitude: String) {
            (String(format: "%.5f°", latitude), String(format: "%.5f°", longitude))
        }
            return (degrees.latitude, degrees.longitude)
        case "DMS": var dms: (latitude: String, longitude: String) {
            (String(format: "%d° %d' %.1f\" %@",
                    Int(abs(latitude)),
                    Int(abs(latitudeMinutes)),
                    abs(latitudeSeconds),
                    latitude >= 0 ? "N" : "S"),
             String(format: "%d° %d' %.1f\" %@",
                    Int(abs(longitude)),
                    Int(abs(longitudeMinutes)),
                    abs(longitudeSeconds),
                    longitude >= 0 ? "E" : "W"))
        }
            return (dms.latitude, dms.longitude)
        case "DDM": var ddm: (latitude: String, longitude: String) {
            (String(format: "%d° %.3f' %@",
                    Int(abs(latitude)),
                    abs(latitudeMinutes),
                    latitude >= 0 ? "N" : "S"),
             String(format: "%d° %.3f' %@",
                    Int(abs(longitude)),
                    abs(longitudeMinutes),
                    longitude >= 0 ? "E" : "W"))
        }
            return (ddm.latitude, ddm.longitude)
        default:
            return (String(latitude), String(longitude))
        }
    }
}
