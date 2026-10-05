// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import CoreLocation

 class APZLocationPermission: NSObject, CLLocationManagerDelegate {
    var delegate: APZLocationDelegate
    var locationManager: CLLocationManager?
    @objc init(delegate: APZLocationDelegate) {
        self.delegate = delegate
        super.init()
    }
    @objc  func askPermission() {
        if locationManager == nil {
            locationManager = CLLocationManager()
            locationManager?.delegate = self
        }
        locationManager?.requestWhenInUseAuthorization()
        locationManager?.startUpdatingLocation()
    }
     func askAlwaysPermission() {
        if locationManager == nil {
            locationManager = CLLocationManager()
            locationManager?.delegate = self
        }
        locationManager?.requestAlwaysAuthorization()
        locationManager?.startUpdatingLocation()
    }
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if locationManager != nil {
            locationManager?.stopUpdatingLocation()
            locationManager = nil
            delegate.locationPermission(true)
        }
    }
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        if locationManager != nil {
            locationManager?.stopUpdatingLocation()
            locationManager = nil
            delegate.locationPermission(false)
        }
    }
}
