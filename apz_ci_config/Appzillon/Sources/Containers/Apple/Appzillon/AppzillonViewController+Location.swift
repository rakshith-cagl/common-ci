// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import CoreLocation

@objc extension AppzillonViewController: CLLocationManagerDelegate {
    func locationPermission() {
        locationManager = CLLocationManager()
        locationManager.delegate = self
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
    }
    public func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let newLocation = locations.last
        if !self.deviceRegistrationCalled {
            self.deviceRegistrationCalled = true
            if let appString = appString {
                let pathToDictionary = sandboxPath + "/Assets/apps/\(appString)/plist/AppProperties.plist"
                let settingsDictionary = NSDictionary(contentsOfFile: pathToDictionary) as? [AnyHashable: Any] ?? [:]
                if let trackLocEnc = settingsDictionary["trackLocation"] as? String,
                   CryptoSwiftManager.shared.decryptSingleValue(value: trackLocEnc) == StringConstants.Generic.yes {
                    callDeviceRegistration(newLocation)
                } else {
                    callDeviceRegistration(nil)
                }
            }
            locationManager.stopUpdatingLocation()
            locationManager = nil
        }
    }
    public func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        if !self.deviceRegistrationCalled {
            self.deviceRegistrationCalled = true
            self.callDeviceRegistration(nil)
        }
        locationManager.stopUpdatingLocation()
        locationManager = nil
    }
}
