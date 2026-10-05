//
//  BarCodeViewController.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 02/07/21.
//

import Foundation
import AVKit

protocol BarCodeControllerDelegate: AnyObject {
    func scannedBarCode(_ barcode: String?)
    func galleryClicked()
    func closeClicked()
}

class BarCodeViewController: UIView, AVCaptureMetadataOutputObjectsDelegate {
    var captureSession = AVCaptureSession()
    var videoPreviewLayer: AVCaptureVideoPreviewLayer?
    var flashButton: UIButton?
    var cancelButton: UIButton?
    var galleryButton: UIButton?
    var boundingBoxLayer: CAShapeLayer?
    var viewController: UIViewController?
    var delegate: BarCodeControllerDelegate?
    var logoImageView: UIImageView?
    let flashOn = "BarcodeFlashImages/flashOn.png"
    let flashOff = "BarcodeFlashImages/flashOff.png"

    // MARK: - Initialization
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    required public init(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    // MARK: - Start Scan
    fileprivate func showFullScreenWithGalleryCancelBtn(_ gallery: String, _ fullScreen: String, _ jsonDict: [AnyHashable : Any]) {
        if let barCodeController = self.viewController {
            
            self.frame = CGRect(x: 0, y: 0,
                                width: barCodeController.view.bounds.size.width,
                                height: UIScreen.main.bounds.height)
            self.viewController?.view.addSubview(self)
            cancelButton = UIButton(frame: CGRect(x: self.frame.size.width - 20 - 35,
                                                  y: 35, width: 35, height: 35))
            cancelButton?.setImage(UIImage(named: "BarcodeFlashImages/closeIconWhite.png"), for: .normal)
            cancelButton?.isUserInteractionEnabled = true
            cancelButton?.addTarget(self, action: #selector(cancelButtonTapped(_:)), for: .touchUpInside)
            
            flashButton = UIButton(frame: CGRect(x: self.frame.size.width - 20 - 35,
                                                 y: (self.frame.size.height / 2 ) - 5 - 35,
                                                 width: 35, height: 35))
            flashButton?.setImage(UIImage(named: flashOn), for: .normal)
            flashButton?.isUserInteractionEnabled = true
            flashButton?.addTarget(self, action: #selector(turnOnFlash(_:)), for: .touchUpInside)
            
            if gallery == "Y" {
                galleryButton = UIButton(frame: CGRect(x: self.frame.size.width - 20 - 35,
                                                       y: (self.frame.size.height / 2 ) + 35,
                                                       width: 35, height: 35))
                galleryButton?.setImage(UIImage(named: "BarcodeFlashImages/galleryIcon.png"), for: .normal)
                galleryButton?.isUserInteractionEnabled = true
                galleryButton?.addTarget(self, action: #selector(galleryIconTapped(_:)), for: .touchUpInside)
            }
            self.startScanning(fullScreen: fullScreen, gallery: gallery, jsonDict: jsonDict)
        }
    }
    
    fileprivate func showCustomScannerView(_ jsonDict: [AnyHashable : Any], _ fullScreen: String, _ gallery: String) {
        if let barCodeController = self.viewController {
            var originY = CGFloat((jsonDict[StringConstants.BarcodeScanner.originY] as? NSNumber)?.floatValue ?? 0.0)
            if originY == 0 {
                originY = (viewController?.view.bounds.size.height ?? 0.0)
                / 2 - (viewController?.view.bounds.size.width ?? 0.0) / 2
            }
            if UIDevice.current.userInterfaceIdiom == .pad {
                self.frame = CGRect(x: 0, y: originY, width: barCodeController.view.bounds.size.width,
                                    height: (barCodeController.view.bounds.size.height) * 0.6)
            } else {
                self.frame = CGRect(x: 0, y: originY, width: barCodeController.view.bounds.size.width,
                                    height: barCodeController.view.bounds.size.width)
            }
            self.center = CGPoint(
                x: (barCodeController.view.frame.size.width ) / 2,
                y: (barCodeController.view.frame.size.height) / 2)
            self.viewController?.view.addSubview(self)
            flashButton = UIButton(frame: CGRect(x: self.frame.size.width / 2 - 17,
                                                 y: 10, width: 35, height: 35))
            flashButton?.setImage(UIImage(named: flashOn), for: .normal)
            flashButton?.isUserInteractionEnabled = true
            flashButton?.addTarget(self, action: #selector(turnOnFlash(_:)), for: .touchUpInside)
            //To Add LogoImage for the Scanner
            if let isLogoPresent = jsonDict["showAppLogo"] as? String,
               isLogoPresent == StringConstants.Generic.yes,
               let image = UIImage(named: "QRCodeScannerLogo") {
                logoImageView = UIImageView(image: image)
                if let isHorizontal = jsonDict["isAppLogoHorizontal"] as? String,
                   isHorizontal == StringConstants.Generic.yes{
                    logoImageView?.frame = CGRect(x: self.frame.size.width / 2 - (80.0/2),
                                                  y: self.frame.size.height-10-30.0,
                                                  width: 80.0, height: 30.0)
                } else {
                    logoImageView?.frame = CGRect(x: self.frame.size.width / 2 - (36.0/2),
                                                  y: self.frame.size.height-8-36.0,
                                                  width: 36.0, height: 36.0)
                }
            }
            self.startScanning(fullScreen: fullScreen, gallery: gallery, jsonDict: jsonDict)
        }
    }
    
    open func initializeScannerView(jsonDict: [AnyHashable: Any], fullScreen: String, gallery: String) {
        if fullScreen == "Y" {
            showFullScreenWithGalleryCancelBtn(gallery, fullScreen, jsonDict)
        }
        else {
            showCustomScannerView(jsonDict, fullScreen, gallery)
        }
    }

    func startScanning(fullScreen: String, gallery: String, jsonDict: [AnyHashable: Any]) {
        let deviceDiscoverySession = AVCaptureDevice.DiscoverySession(deviceTypes: [.builtInDualCamera,
                                                                                    .builtInWideAngleCamera],
                                                                      mediaType: AVMediaType.video,
                                                                      position: .back)
        guard let captureDevice = deviceDiscoverySession.devices.first else {
            print("Failed to get the camera device")
            return
        }
        do {
            let input = try AVCaptureDeviceInput(device: captureDevice)
            var width: CGFloat = 0.0
            var height: CGFloat = 0.0
            if fullScreen == "Y" {
                width = self.bounds.size.width / 2 + 50
                height = self.bounds.size.width / 2 + 50
                
                let box = CGRect(x: CGFloat(self.bounds.size.width / 2 - width / 2),
                                 y: CGFloat(self.bounds.size.height / 2 - height / 2),
                                 width: CGFloat(width), height: CGFloat(height))
                
                captureSession.addInput(input)
                let captureMetadataOutput = AVCaptureMetadataOutput()
                captureMetadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
                
                captureSession.addOutput(captureMetadataOutput)
                captureMetadataOutput.metadataObjectTypes = captureMetadataOutput.availableMetadataObjectTypes
                videoPreviewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
                if let videoLayer = videoPreviewLayer {
                    videoLayer.videoGravity = AVLayerVideoGravity.resizeAspectFill
                    videoLayer.frame = self.layer.bounds
                    self.layer.addSublayer(videoLayer)
                }
                boundingBoxLayer = CAShapeLayer()
                if let boundingLayer = boundingBoxLayer, let flashButton = flashButton {
                    createBoundingLayer(box, boundingLayer)
                    self.addSubview(flashButton)
                }
                if let boundingLayer = boundingBoxLayer, let cancelButton = cancelButton {
                    createBoundingLayer(box, boundingLayer)
                    self.addSubview(cancelButton)
                }
                if let boundingLayer = boundingBoxLayer, let galleryButton = galleryButton, gallery == "Y" {
                    createBoundingLayer(box, boundingLayer)
                    self.addSubview(galleryButton)
                }
                captureSession.commitConfiguration()
                captureSession.startRunning()
                captureMetadataOutput.rectOfInterest = videoPreviewLayer?.metadataOutputRectConverted(fromLayerRect: box) ?? self.bounds
                
            } else {
                width = self.bounds.size.width - 100
                height = self.bounds.size.height - 100
                let box = CGRect(x: CGFloat(self.bounds.size.width / 2 - width / 2),
                                 y: CGFloat(self.bounds.size.height / 2 - height / 2),
                                 width: CGFloat(width), height: CGFloat(height))
                
                captureSession.addInput(input)
                let captureMetadataOutput = AVCaptureMetadataOutput()
                captureMetadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
                
                captureSession.addOutput(captureMetadataOutput)
                captureMetadataOutput.metadataObjectTypes = captureMetadataOutput.availableMetadataObjectTypes
                videoPreviewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
                if let videoLayer = videoPreviewLayer {
                    videoLayer.videoGravity = AVLayerVideoGravity.resizeAspectFill
                    videoLayer.frame = self.layer.bounds
                    self.layer.addSublayer(videoLayer)
                }
                boundingBoxLayer = CAShapeLayer()
                if let boundingLayer = boundingBoxLayer, let flashButton = flashButton {
                    createBoundingLayer(box, boundingLayer)
                    self.addSubview(flashButton)
                }
                if let isLogoPresent = jsonDict["showAppLogo"] as? String,
                   isLogoPresent == StringConstants.Generic.yes,
                   let boundingLayer = boundingBoxLayer, let image = logoImageView {
                    createBoundingLayer(box, boundingLayer)
                    self.addSubview(image)
                }
                captureSession.commitConfiguration()
                captureSession.startRunning()
                captureMetadataOutput.rectOfInterest = videoPreviewLayer?.metadataOutputRectConverted(fromLayerRect: box) ?? self.bounds
            }
        } catch {
            print(error)
            return
        }
    }
    fileprivate func createBoundingLayer(_ box: CGRect, _ boundingLayer: CAShapeLayer) {
        let boundingBoxPath = UIBezierPath(rect: box)
        boundingLayer.path = boundingBoxPath.cgPath
        boundingLayer.lineWidth = 2.0
        boundingLayer.strokeColor = UIColor.white.cgColor
        boundingLayer.fillColor = UIColor.clear.cgColor
        self.layer.addSublayer(boundingLayer)
    }
    // MARK: - Flash
    @objc func turnOnFlash(_ sender: UIButton) {
        let device = AVCaptureDevice.default(for: .video)
        if device?.hasFlash ?? false {
            if !sender.isSelected {
                sender.isSelected = true
                flashButton?.setImage(UIImage(named: flashOff), for: .selected)
                let captureDeviceClass: AnyClass? = NSClassFromString("AVCaptureDevice")
                if captureDeviceClass != nil {
                    do {
                        try device?.lockForConfiguration()
                        device?.torchMode = .on
                        device?.unlockForConfiguration()
                    } catch {
                        print("Error in Device flash")
                    }
                }
            } else {
                sender.isSelected = false
                flashButton?.setImage(UIImage(named: flashOn), for: .normal)
                let captureDeviceClass: AnyClass? = NSClassFromString("AVCaptureDevice")
                if captureDeviceClass != nil {
                    do {
                        try device?.lockForConfiguration()
                        device?.torchMode = .off
                        device?.unlockForConfiguration()
                    } catch {
                        print("Error in Device flash")
                    }
                }
            }
        }
    }
    // MARK: - Stop Scan
    open func stopScanning() {
        captureSession.stopRunning()
        boundingBoxLayer = nil
        videoPreviewLayer = nil
    }
    // MARK: - Delegates
    func sendBackBarcodeData(_ detectionString: String?) {
        self.delegate?.scannedBarCode(detectionString)
        self.delegate = nil
    }

    // MARK: - Gallery Icon Tapped
    @objc func galleryIconTapped(_ sender: UIButton) {
        self.delegate?.galleryClicked()
    }

    // MARK: - Cancel Icon Tapped
    @objc func cancelButtonTapped(_ sender: UIButton) {
        self.delegate?.closeClicked()
    }

    public func metadataOutput(_ output: AVCaptureMetadataOutput,
                               didOutput metadataObjects: [AVMetadataObject],
                               from connection: AVCaptureConnection) {
        var detectionString: String?
        if metadataObjects.count == 0 {
            print("No QR code is detected")
            return
        }

        let barCodeTypes = [AVMetadataObject.ObjectType.code39, .code39Mod43,
                            .ean13, .ean8, .code93, .aztec, .dataMatrix,
                            .pdf417, .interleaved2of5, .itf14, .qr,
                            .upce, .code128]
        for metadata in metadataObjects where metadata.type != .face {
            for type in barCodeTypes where metadata.type == type {
                _ = videoPreviewLayer?.transformedMetadataObject(for: metadata)
                detectionString = (metadata as? AVMetadataMachineReadableCodeObject)?.stringValue
                break
            }
        }
    ifStatement: if detectionString != nil {
        print("BarCode Detected")
        self.sendBackBarcodeData(detectionString)
        break ifStatement
    }
    }
    func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        let toInterfaceOrientation = UIInterfaceOrientation(rawValue: UIDevice.current.orientation.rawValue)
        let toRect = CGRect(
            origin: CGPoint(x: 0.0, y: 0.0),
            size: size
        )
        videoPreviewLayer?.frame = toRect
        if videoPreviewLayer?.connection?.isVideoOrientationSupported != nil {
            if toInterfaceOrientation == .landscapeLeft {
                videoPreviewLayer?.connection?.videoOrientation = .landscapeLeft
            } else if toInterfaceOrientation == .landscapeRight {
                videoPreviewLayer?.connection?.videoOrientation = .landscapeRight
            } else if toInterfaceOrientation == .portraitUpsideDown {
                videoPreviewLayer?.connection?.videoOrientation = .portraitUpsideDown
            } else {
                videoPreviewLayer?.connection?.videoOrientation = .portrait
            }
        }
        let width = size.width - 100
        let height = size.height - 100
        let box = CGRect(x: CGFloat(size.width / 2 - width / 2),
                         y: CGFloat(size.height / 2 - height / 2),
                         width: CGFloat(width),
                         height: CGFloat(height))
        let boundingBoxPath = UIBezierPath(rect: box)
        boundingBoxLayer?.path = boundingBoxPath.cgPath
        boundingBoxLayer?.lineWidth = 2.0
        boundingBoxLayer?.strokeColor = UIColor.green.cgColor
        boundingBoxLayer?.fillColor = UIColor.clear.cgColor
    }
    func willAnimateRotation(to toInterfaceOrientation: UIInterfaceOrientation, duration: TimeInterval) {
        videoPreviewLayer?.frame = self.bounds
        if videoPreviewLayer?.connection?.isVideoOrientationSupported != nil {
            if toInterfaceOrientation == .landscapeLeft {
                videoPreviewLayer?.connection?.videoOrientation = .landscapeLeft
            } else if toInterfaceOrientation == .landscapeRight {
                videoPreviewLayer?.connection?.videoOrientation = .landscapeRight
            } else if toInterfaceOrientation == .portraitUpsideDown {
                videoPreviewLayer?.connection?.videoOrientation = .portraitUpsideDown
            } else {
                videoPreviewLayer?.connection?.videoOrientation = .portrait
            }
        }
        let width = self.bounds.size.width - 100
        let height = self.bounds.size.height - 100
        let box = CGRect(x: CGFloat(self.bounds.size.width / 2 - width / 2),
                         y: CGFloat(self.bounds.size.height / 2 - height / 2),
                         width: CGFloat(width),
                         height: CGFloat(height))
        let boundingBoxPath = UIBezierPath(rect: box)
        boundingBoxLayer?.path = boundingBoxPath.cgPath
        boundingBoxLayer?.lineWidth = 2.0
        boundingBoxLayer?.strokeColor = UIColor.green.cgColor
        boundingBoxLayer?.fillColor = UIColor.clear.cgColor
    }
    open var shouldAutorotate: Bool {
        return true
    }
}
