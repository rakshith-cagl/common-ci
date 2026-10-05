// Copyright (c) 2021 Appzillon. All rights reserved.

import UIKit
import WebKit
import MapKit
import CoreLocation

class APZMapViewController: UIViewController, CLLocationManagerDelegate,
                            UIGestureRecognizerDelegate, MKMapViewDelegate {
    var userLocation: CLLocation?
    var mapView: MKMapView?
    var clLocationManager: CLLocationManager = CLLocationManager.init()
    var isFirstLaunch: Bool = false
    var annotationArray: [MKAnnotation] = []
    var point = MKPointAnnotation()
    var latitude: String = StringConstants.Generic.emptyString
    var longitude: String = StringConstants.Generic.emptyString
    var mapLocations: [AnyHashable] = []
    var delegate: APZMapDelegate?
    override init(nibName nibNameOrNil: String?, bundle nibBundleOrNil: Bundle?) {
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Map View"
        navigationItem.rightBarButtonItem = UIBarButtonItem.init(barButtonSystemItem: .cancel,
                                                                 target: self,
                                                                 action: #selector(cancel))
        mapView = MKMapView.init(frame: self.view.bounds)
        if let mapView = mapView {
            mapView.mapType = MKMapType.standard
            mapView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
            clLocationManager.delegate = self
            clLocationManager.desiredAccuracy = kCLLocationAccuracyBestForNavigation
            clLocationManager.requestAlwaysAuthorization()
            clLocationManager.startUpdatingLocation()
            isFirstLaunch = true
            self.view.addSubview(mapView)
            mapView.delegate = self
            let doubleTap = UITapGestureRecognizer.init(target: self, action: #selector(handleDoubleTap(tap:)))
            doubleTap.numberOfTapsRequired = 2
            doubleTap.delegate = self
            mapView.addGestureRecognizer(doubleTap)
        }
    }
    // MARK: Selector methods
    @objc func cancel() {
        applyMapViewMemoryCleaning()
        cleanUp()
        self.dismiss(animated: true) {
            self.delegate?.mapCancelled(WindowUtility.getUiInterfaceOrientation())
        }
    }
    @objc func handleDoubleTap(tap: UITapGestureRecognizer) {
        if let mapView = mapView {
            if !annotationArray.isEmpty {
               mapView.removeAnnotations(annotationArray)
            }
            mapView.removeAnnotation(point)
            point = MKPointAnnotation()
            point.coordinate = mapView.convert(tap.location(in: mapView), toCoordinateFrom: mapView)
            point.title = "Get Location"
            point.subtitle = String(format: "Lat: %.3f, Long: %.3f",
                                    point.coordinate.latitude,
                                    point.coordinate.longitude)
            latitude = String(point.coordinate.latitude)
            longitude = String(point.coordinate.longitude)
            mapView.addAnnotation(point)
            mapView.selectAnnotation(point, animated: true)
        }
    }
    func applyMapViewMemoryCleaning() {
        if let mapView = mapView {
            switch mapView.mapType {
            case .hybrid: mapView.mapType = .standard
            case .standard: mapView.mapType = .hybrid
            case .satellite: mapView.mapType = .standard
            default:
                break
            }
            mapView.mapType = .standard
        }
    }
    // MARK: Utitlity methods
    func cleanUp() {
        if let mapView = mapView {
            mapView.removeFromSuperview()
        }
            mapView = nil
    }
    func setRegion() {
        let annotation = MKPointAnnotation.init()
        let latitudeCoordinate = clLocationManager.location?.coordinate.latitude
        let longitudeCoordinate = clLocationManager.location?.coordinate.longitude
        if let latitudeCoordinate = latitudeCoordinate, let longitudeCoordinate = longitudeCoordinate {
            annotation.coordinate = CLLocationCoordinate2DMake(latitudeCoordinate, longitudeCoordinate)
            annotationArray.append(annotation)
            if let mapView = mapView {
                mapView.addAnnotations(annotationArray)
                mapView.showsUserLocation =  true
                mapView.region = zoomToFitMapAnnotations()
                mapView.setRegion(mapView.region, animated: true)
                clLocationManager.stopUpdatingLocation()
            }
        }
    }
    func zoomToFitMapAnnotations() -> MKCoordinateRegion {
        var topLeftCoord = CLLocationCoordinate2DMake(-90, 180)
        var bottomRightCoord = CLLocationCoordinate2DMake(90, -180)
        if let mapView = mapView {
            for annotation in mapView.annotations {
                topLeftCoord.longitude = fmin(topLeftCoord.longitude, annotation.coordinate.longitude)
                topLeftCoord.latitude = fmax(topLeftCoord.latitude, annotation.coordinate.latitude)
                bottomRightCoord.longitude = fmax(bottomRightCoord.longitude, annotation.coordinate.longitude)
                bottomRightCoord.latitude = fmin(bottomRightCoord.latitude, annotation.coordinate.latitude)
            }
        }
        var region = MKCoordinateRegion()
        region.center.latitude = topLeftCoord.latitude - (topLeftCoord.latitude - bottomRightCoord.latitude) * 0.5
        region.center.longitude = topLeftCoord.longitude + (bottomRightCoord.longitude - topLeftCoord.longitude) * 0.5
        region.span.latitudeDelta = fabs(topLeftCoord.latitude - bottomRightCoord.latitude) * 1.1
        region.span.longitudeDelta = fabs(bottomRightCoord.longitude - topLeftCoord.longitude) * 1.1
        return region
    }
    func setMarkUpLocations(locations: [AnyHashable]) {
        self.mapLocations = locations
    }
    @objc func sendLocationCordinates() {
        if longitude != "", latitude != "" {
            self.delegate?.getLocationCordinates(latitude,
                                                 longitude: longitude,
                                                 with: WindowUtility.getUiInterfaceOrientation())
        }
    }
    // MARK: Delegate methods
    func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer,
                           shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return true
    }
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        if annotation.isKind(of: MKUserLocation.self) {
            return nil
        }
        let myAnnotationId = "myAnnotation"
        let annotationView = MKMarkerAnnotationView.init(annotation: annotation, reuseIdentifier: myAnnotationId)
        let getLocation = UIButton.init(type: .infoDark)
        getLocation.addTarget(self, action: #selector(sendLocationCordinates), for: .touchUpInside)
        annotationView.rightCalloutAccessoryView = getLocation
        annotationView.isEnabled = true
        annotationView.tintColor = .red
        annotationView.canShowCallout = true
        annotationView.isMultipleTouchEnabled = false
        return annotationView
    }
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if locations.count > 0 {
            userLocation = locations.first
            if isFirstLaunch {
                setRegion()
                isFirstLaunch = false
            }
        }
    }
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("----locationManager manager didFailWithError: \(error.localizedDescription)----")
    }
}
