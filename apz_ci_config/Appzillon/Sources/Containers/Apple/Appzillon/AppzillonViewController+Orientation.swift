// Copyright (c) 2021 Appzillon. All rights reserved.
// swiftlint:disable all
import Foundation

extension AppzillonViewController {
    // MARK: Rotation
    open override var shouldAutorotate: Bool {
        return self.rotationPluginFlag
    }
    override public var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        switch self.appOrientation {
        case "PORTRAIT":
            return [.portrait, .portraitUpsideDown]
        case "LANDSCAPE":
            return .landscapeLeft
        default:
            return .all
        }
    }
    func setOrientation(orientation: [AnyHashable: Any]) {
        if let pluginId = orientationDictionary[StringConstants.Generic.pluginId] as? String,
           let userOrientation = orientation["orientation"] as? String {
            isForceOrientation = true
            rotationPluginFlag = true
            if userOrientation == "PORTRAIT" {
                let value = NSNumber(value: UIInterfaceOrientation.portrait.rawValue)
                sendDeviceOrientationCallBack(toOrientation: .portrait,
                                              value: value,
                                              forceOrien: "Portrait",
                                              appOrien: "PORTRAIT")
                callBack(pluginId: pluginId, orient: "PORTRAIT")
            } else if userOrientation == "LANDSCAPE" {
                let value = NSNumber(value: UIInterfaceOrientation.landscapeLeft.rawValue)
                sendDeviceOrientationCallBack(toOrientation: .landscapeLeft,
                                              value: value,
                                              forceOrien: "Landscape",
                                              appOrien: "LANDSCAPE")
                callBack(pluginId: pluginId, orient: "LANDSCAPE")
            } else {
                appOrientation = "ANY"
                rotationPluginFlag = true
            }
        }
    }
    fileprivate func sendDeviceOrientationCallBack(toOrientation: UIInterfaceOrientation,
                                                   value: NSNumber,
                                                   forceOrien: String,
                                                   appOrien: String) {
        forceOrientation = forceOrien
        appOrientation = appOrien
        UserDefaults.standard.removeObject(forKey: "orientation")

        //        UIDevice.current.setValue(value, forKey: "orientation")
        //        UIApplication.shared.setStatusBarOrientation(toOrientation, animated: true)

        if #available(iOS 16.0, *) {
            scenes = UIApplication.shared.connectedScenes.first as? UIWindowScene
            let mask = (appOrien == "LANDSCAPE") ? UIInterfaceOrientationMask.landscape : UIInterfaceOrientationMask.portrait
            DispatchQueue.main.async {
                UIViewController.attemptRotationToDeviceOrientation()
                self.scenes?.requestGeometryUpdate(.iOS(interfaceOrientations: mask), errorHandler: { error in
                    print("Error - \(error.localizedDescription)")
                })
                self.navigationController?.topViewController?.setNeedsUpdateOfSupportedInterfaceOrientations()
            }
        } else {
            //Fallback on earlier versions
            UIDevice.current.setValue(value, forKey: "orientation")
        }
        AppzillonMainUtility.shared.sendDeviceOrientationNotification(toOrientation: toOrientation,
                                                                      webView: webView,
                                                                      orientationDictionary: orientationDictionary,
                                                                      cbEvent: "orientation_change")
    }
    fileprivate func callBack(pluginId: String, orient: String) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: true,
                                                                    responseKeys: ["orientation"],
                                                                    responseValues: [orient])
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        rotationPluginFlag = false
    }
    open override func willAnimateRotation(to toInterfaceOrientation: UIInterfaceOrientation, duration: TimeInterval) {
        update(toInterfaceOrientation)
    }
    open override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        coordinator.animate(alongsideTransition: { (uiViewControllerTransitionCoordinatorContext) -> Void in
            if #available(iOS 13.0, *) {
                self.scenes = UIApplication.shared.connectedScenes.first as? UIWindowScene
                let toInterfaceOrientation = self.scenes?.interfaceOrientation
                self.update(toInterfaceOrientation ?? UIApplication.shared.statusBarOrientation)
                print("toInterfaceOrientation =\(String(describing: toInterfaceOrientation?.rawValue))")
            } else {
                let toInterfaceOrientation = UIApplication.shared.statusBarOrientation
                self.update(toInterfaceOrientation)
                print("toInterfaceOrientation =\(toInterfaceOrientation.rawValue)")
            }
        }, completion: { (uiViewControllerTransitionCoordinatorContext) -> Void in
            //do nothing
        })
        super.viewWillTransition(to: size, with: coordinator)
    }
    func update(_ toInterfaceOrientation: UIInterfaceOrientation) {
        if rotationPluginFlag {
            if appOrientation == StringConstants.Generic.any {
                if orientationDictionary != nil {
                    AppzillonMainUtility.shared.sendDeviceOrientationNotification(toOrientation: toInterfaceOrientation,
                                                                                  webView: webView,
                                                                                  orientationDictionary: orientationDictionary,
                                                                                  cbEvent: "orientation_change")
                }
            }
            if splashcount != 0 && (splashScreenManual || splashScreenAuto) {
                showSplashScreenWithRotation()
            }
        } else {
            print("--Ignoring Orientation---")
        }
    }
    // MARK: setOrientationValue
    func setOrientationValue() {
        if self.appOrientation != "ANY" {
            var value: Int
            if UIDevice.current.orientation == .landscapeLeft {
                value = UIInterfaceOrientation.landscapeRight.rawValue
            } else if UIDevice.current.orientation == .landscapeRight {
                value = UIInterfaceOrientation.landscapeLeft.rawValue
            } else {
                value = WindowUtility.getUiInterfaceOrientation().rawValue
            }
            print("Valeu: \(value)")
            UIDevice.current.setValue(value, forKey: "orientation")
        }
        UIApplication.shared.setStatusBarOrientation(self.interfaceOrientation, animated: true)
    }
    // MARK: setOrientationAfterSubView
    @objc func setOrientationAfterSubview(_ interfaceOrientation: UIInterfaceOrientation) {
        self.isForceOrientation = true
        if interfaceOrientation.isPortrait {
            self.forceOrientation = "Portrait"
            let value = interfaceOrientation.rawValue
            UserDefaults.standard.removeObject(forKey: "orientation")
            UIDevice.current.setValue(value, forKey: "orientation")
            UIApplication.shared.setStatusBarOrientation(interfaceOrientation, animated: true)
        } else if interfaceOrientation.isLandscape {
            self.forceOrientation = "Landscape"
            let value = interfaceOrientation.rawValue
            UserDefaults.standard.removeObject(forKey: "orientation")
            UIDevice.current.setValue(value, forKey: "orientation")
            UIApplication.shared.setStatusBarOrientation(interfaceOrientation, animated: true)
        } else {
            self.appOrientation="ANY"
            self.rotationPluginFlag = true
        }
    }
}
extension UIWindow {
    static var isLandscape: Bool {
        if #available(iOS 13.0, *) {
            let scenes = UIApplication.shared.connectedScenes
            let windowScenes = scenes.first as? UIWindowScene
            return windowScenes?
                .interfaceOrientation
                .isLandscape ?? false
        } else {
            return UIApplication.shared.statusBarOrientation.isLandscape
        }
    }
}
// swiftlint:enable all
