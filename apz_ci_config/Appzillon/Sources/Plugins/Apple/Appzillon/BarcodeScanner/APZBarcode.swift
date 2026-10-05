//
//  APZBarcode.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 05/10/21.
//

import Foundation
import AVFoundation

class APZBarcode: APZPlugin, BarCodeControllerDelegate {
    var viewController: AppzillonViewController?
    var webView: WKWebView
    var requestJson: [AnyHashable: Any] = [:]
    var pluginId: String = StringConstants.Generic.emptyString
    var rotationFlag: Bool = false
    var barcodeScanner: BarCodeViewController?
    var reqFullScreen: String = StringConstants.Generic.emptyString
    var showGallery: String = StringConstants.Generic.emptyString
    var timer = Timer()
    // MARK: - Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, requestJson)
    }
    open override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            self.requestJson = jsonDict
            print("APZBarcode--Execute")
            pluginId = self.requestJson[StringConstants.Generic.pluginId]
            as? String ?? StringConstants.Generic.emptyString
            // Barcode scan will support only for Portrait mode
            rotationFlag = viewController?.rotationPluginFlag ?? false
            viewController?.rotationPluginFlag = false
            self.startScanBarcode(self.requestJson)
        } else {
            cleanPlugin()
        }
    }
    open override func stop(_ jsonDict: [AnyHashable: Any]?) {
        pluginId = jsonDict?[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        self.stopScanBarcode()
    }
    func cleanPlugin() {
        self.viewController?.rotationPluginFlag = self.rotationFlag
        self.viewController = nil
        if self.delegate != nil {
            self.delegate.donePlugin(self)
            self.delegate = nil
        }
    }
    // MARK: - Start barcode
    func requestForAccess(completionHandler: @escaping (_ accessGranted: Bool) -> Void) {
        let cameraMediaType = AVMediaType.video
        let cameraAuthorizationStatus = AVCaptureDevice.authorizationStatus(for: cameraMediaType)
        switch cameraAuthorizationStatus {
        case .authorized:
            completionHandler(true)
        case .denied, .notDetermined:
            AVCaptureDevice.requestAccess(for: cameraMediaType) { granted in
                completionHandler(granted)
            }
        default:
            completionHandler(false)
        }
    }
    func startScanBarcode(_ jsonDict: [AnyHashable: Any]) {
        let isCameraPresent = UIImagePickerController.isSourceTypeAvailable(.camera)
        if isCameraPresent {
            self.requestForAccess { [self] (accessGranted) -> Void in
                if accessGranted && self.barcodeScanner == nil {
                    DispatchQueue.main.async { [weak self] in
                        let frame = self?.viewController?.view.frame ?? CGRect(x: 0, y: 0, width: 0, height: 0)
                        self?.barcodeScanner = BarCodeViewController.init(frame:
                                                                            frame)
                        self?.barcodeScanner?.viewController = self?.viewController
                        self?.barcodeScanner?.delegate = self
                        self?.reqFullScreen = jsonDict[StringConstants.BarcodeScanner.reqFullScreen] as? String ?? "N"
                        self?.showGallery = jsonDict[StringConstants.BarcodeScanner.showGallery] as? String ?? "N"
                        self?.barcodeScanner?.initializeScannerView(jsonDict: self!.requestJson,
                                                                    fullScreen: self?.reqFullScreen ?? "N",
                                                                    gallery: self?.showGallery ?? "N")
                        //Invalidate if plugin is called repeatedly to avoid multpile timers
                        self?.timer.invalidate()
                        if let myTimeInString = jsonDict["timeout"] as? String, !myTimeInString.isEmpty , let timeInt = Int(myTimeInString) {
                            self?.timer = Timer.scheduledTimer(timeInterval: TimeInterval(timeInt), target: self, selector: #selector(self?.timerElapsed), userInfo: nil, repeats: false)
                        }
                    }
                } else if self.barcodeScanner != nil {
                    // do nothing if barcode scan is in progress already
                } else {
                    self.cameraCallBack(status: false)
                }
            }
        } else {
            self.cameraCallBack(status: false)
        }
    }

    @objc func timerElapsed() {
        self.timer.invalidate()
        let resultkeys = [StringConstants.Generic.errorCode, "errorMessage"]
        let returnResult = ["APZ-CNT-342", "QR code capture timed out"]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: returnResult)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        print("APZBarcode--R code capture timed out")
    }


    func scannedBarCode(_ barcode: String?) {
        if let barcode = barcode {
            self.timer.invalidate()
            let resultkeys = [StringConstants.Generic.text]
            let returnResult = [barcode]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: returnResult)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            print("APZBarcode--Scanner Success")
        }
    }
    func cameraCallBack(status: Bool) {
        if !status {
            let resultkeys = [StringConstants.Generic.errorCode]
            let result = [StringConstants.Generic.cameraNotFound]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: result)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            print("APZBarcode--Camera Permission Denied")
        }
        cleanPlugin()
    }
    // MARK: - Stop barcode
    func stopScanBarcode() {
        if let nativeReader = self.barcodeScanner {
            self.timer.invalidate()
            nativeReader.stopScanning()
            nativeReader.removeFromSuperview()
            let resultkeys = [StringConstants.Generic.text]
            let returnResult = [StringConstants.BarcodeScanner.cameraClosed]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: returnResult)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        } else {
            let resultkeys = [StringConstants.Generic.errorMessage]
            let resultMess = [StringConstants.BarcodeScanner.cameraCloseFailed]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: resultMess)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
        }
        cleanPlugin()
    }
    func galleryClicked() {
        self.timer.invalidate()
        let resultkeys = [StringConstants.Generic.text]
        let returnResult = [StringConstants.BarcodeScanner.galleryClicked]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: returnResult)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }

    func closeClicked() {
        self.timer.invalidate()
        let resultkeys = [StringConstants.Generic.text]
        let returnResult = [StringConstants.BarcodeScanner.closeClicked]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: returnResult)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
