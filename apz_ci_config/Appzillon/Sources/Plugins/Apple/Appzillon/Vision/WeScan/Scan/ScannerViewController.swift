// swiftlint:disable all
//
//  ScannerViewController.swift
//  WeScan
//
//  Created by Boris Emorine on 2/8/18.
//  Copyright © 2018 WeTransfer. All rights reserved.
//

import LocalAuthentication
import UIKit
import AVFoundation

/// The `ScannerViewController` offers an interface to give feedback to the user regarding quadrilaterals that are detected. It also gives the user the opportunity to capture an image with a detected rectangle.
final class ScannerViewController: UIViewController {
    // OverLay layer
    var captureDeviceResolution: CGSize = CGSize()
    var myOriginalRect = CGRect()
    var captureDevice: AVCaptureDevice?
    // Layer UI for drawing Vision results
    var rootLayer: CALayer?
    var detectionOverlayLayer: CALayer?
    var detectedFaceRectangleShapeLayer: CAShapeLayer?
    var detectedFaceLandmarksShapeLayer: CAShapeLayer?
    var appzillonVerification: AppzillonDocumentVerification!
    // Appzillon requirement
    var previewView: UIView!
    // UILabels
    var pageTitleLabel = UILabel()
    var pageMessageTitleLabel = UILabel()
    var messageLabel = UILabel()
    var statusLabel = UILabel()
    // UIColors
    var backgroundColor = UIColor()
    var textColor = UIColor()
    // Timer to send timeout callback
    var myTimer: Timer?
    private var captureSessionManager: CaptureSessionManager?
    private let videoPreviewLayer = AVCaptureVideoPreviewLayer()
    public var requestJson = [String: Any]()
    var uiParams = [String: Any]()
    /// The view that shows the focus rectangle (when the user taps to focus, similar to the Camera app)
    private var focusRectangle: FocusRectangleView!
    /// The view that draws the detected rectangles.
    private let quadView = QuadrilateralView()
    private var quad: Quadrilateral? = nil
    /// Whether flash is enabled
    private var flashEnabled = false
    /// The original bar style that was set by the host app
    private var originalBarStyle: UIBarStyle?
    init (requestJson: [String: Any]) {
        self.requestJson = requestJson
        super.init(nibName: nil, bundle: nil)
    }
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    lazy private var shutterButton: ShutterButton = {
        //        let button = ShutterButton()
        let rect = CGRect(x: 0, y: 0, width: 0, height: 0)
        let button = ShutterButton(frame: rect, lensColor: self.textColor)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.addTarget(self, action: #selector(captureImage(_:)), for: .touchUpInside)
        return button
    }()
    lazy private var cancelButton: UIButton = {
        let button = UIButton()
        button.setTitle(NSLocalizedString("wescan.scanning.cancel",
                                          tableName: nil, bundle: Bundle(for: ScannerViewController.self),
                                          value: "Cancel", comment: "The cancel button"), for: .normal)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.setTitleColor(self.textColor, for: .normal)
        button.addTarget(self, action: #selector(cancelImageScannerController), for: .touchUpInside)
        return button
    }()
    lazy private var autoScanButton: UIBarButtonItem = {
        let title = NSLocalizedString("wescan.scanning.auto",
                                      tableName: nil, bundle: Bundle(for: ScannerViewController.self),
                                      value: "Manual", comment: "The auto button state")
        let button = UIBarButtonItem(title: title, style: .plain, target: self, action: #selector(toggleAutoScan))
        button.tintColor = .white
        return button
    }()
    lazy private var flashButton: UIBarButtonItem = {
        let image = UIImage(named: "flash", in: Bundle(for: ScannerViewController.self), compatibleWith: nil)
        let button = UIBarButtonItem(image: image, style: .plain, target: self, action: #selector(toggleFlash))
        button.tintColor = .white
        return button
    }()
    lazy private var activityIndicator: UIActivityIndicatorView = {
        let activityIndicator = UIActivityIndicatorView(style: .gray)
        activityIndicator.hidesWhenStopped = true
        activityIndicator.translatesAutoresizingMaskIntoConstraints = false
        return activityIndicator
    }()
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        title = nil
        appzillonVerification = AppzillonDocumentVerification()
        if let myUiParams = self.requestJson["UIParams"] {
            uiParams = myUiParams as! [String: Any]
        }
        var myBackgroundColor = "FFFFFF"
        var myTextColor = "000000"
        if let backgrndColour = uiParams["overlayColor"] {
            myBackgroundColor = (backgrndColour as! String)
        }
        if let textclr = uiParams["fontColor"] {
            myTextColor = (textclr as! String)
        }
        self.backgroundColor = self.getUIColorFromHexadecimal(hex: myBackgroundColor, viewType: "background")
        self.textColor = self.getUIColorFromHexadecimal(hex: myTextColor, viewType: "text")
        setupViews()
        setupNavigationBar()
        setupConstraints()
        captureSessionManager = CaptureSessionManager(videoPreviewLayer: videoPreviewLayer)
        captureSessionManager?.delegate = self
        originalBarStyle = navigationController?.navigationBar.barStyle
        self.previewView = UIView(frame: CGRect(x: 0,
                                                y: self.videoPreviewLayer.frame.origin.y,
                                                width:
                                                    self.videoPreviewLayer.frame.size.width,
                                                height: self.videoPreviewLayer.frame.size.height))
        //        self.view.addSubview(self.previewView!)
        self.quadView.addSubview(self.previewView!)
        guard let device = AVCaptureDevice.default(for: AVMediaType.video) else { return }
        self.captureDevice = device
        if let previewRootLayer = self.previewView?.layer {
            self.rootLayer = previewRootLayer
        }
        NotificationCenter.default.addObserver(self,
                                               selector: #selector(subjectAreaDidChange),
                                               name: Notification.Name.AVCaptureDeviceSubjectAreaDidChange, object: nil)
    }
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        setNeedsStatusBarAppearanceUpdate()
        CaptureSession.current.isEditing = false
        quadView.removeQuadrilateral()
        captureSessionManager?.start()
        UIApplication.shared.isIdleTimerDisabled = true
        navigationController?.navigationBar.barStyle = .blackTranslucent
        setupOverLay()
    }
    override func viewDidAppear(_ animated: Bool) {
        // Start the timer
        if let timeout = self.requestJson["timeOutForCapture"] {
            if !((timeout as! String) == "") {
                let timeoutRequired = (timeout as! String)
                let myTimeoutInt = Double(timeoutRequired)
                myTimer = Timer.scheduledTimer(timeInterval: myTimeoutInt!,
                                               target: self,
                                               selector: #selector(timeoutAction),
                                               userInfo: nil, repeats: false)
            }
        }
    }
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        videoPreviewLayer.frame = view.layer.bounds
    }
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        UIApplication.shared.isIdleTimerDisabled = false
        navigationController?.navigationBar.isTranslucent = false
        navigationController?.navigationBar.barStyle = originalBarStyle ?? .default
        captureSessionManager?.stop()
        guard let device = AVCaptureDevice.default(for: AVMediaType.video) else { return }
        if device.torchMode == .on {
            toggleFlash()
        }
    }
    // MARK: - Setups
    private func setupViews() {
        view.backgroundColor = .darkGray
        view.layer.addSublayer(videoPreviewLayer)
        quadView.translatesAutoresizingMaskIntoConstraints = false
        quadView.editable = false
        view.addSubview(quadView)
        view.addSubview(cancelButton)
        view.addSubview(shutterButton)
        view.addSubview(activityIndicator)
    }
    fileprivate func radiansForDegrees(_ degrees: CGFloat) -> CGFloat {
        //        print("radiansForDegrees")
        return CGFloat(Double(degrees) * Double.pi / 180.0)
    }
    // Appzillon Customization
    func getUIColorFromHexadecimal(hex: String, viewType: String) -> UIColor {
        var color = UIColor()
        if hex.count>2 {
            color = UIColor.colorFromHex(hex: hex)
        } else {
            if viewType == "text"{
                color = UIColor.black
            } else if viewType == "background" {
                color = UIColor.gray
            }
        }
        return color
    }
    func setUpLabels() {
        var availableSpaceForLabels: CGFloat
        let deviceHeight = self.view.frame.size.height
        let myDeviceHeight = CGFloat(deviceHeight)
        availableSpaceForLabels = myDeviceHeight - 108 - 60 - 36 - 20 -
            (self.myOriginalRect.origin.y + self.myOriginalRect.size.height)
        let spaceBetweenLabels = availableSpaceForLabels/4
        var actualSpaceRequired: CGFloat
        if spaceBetweenLabels > 25.0 {
            actualSpaceRequired = spaceBetweenLabels
        } else {
            actualSpaceRequired = 25.0
        }
        let faceID = getBiometricType()
        if faceID == .face {
            statusLabel = UILabel(frame: CGRect(x: 15,
                                                y: self.view.frame.size.height - actualSpaceRequired - 108 - 20,
                                                width: self.view.frame.size.width - 30,
                                                height: 20))
        } else {
            statusLabel = UILabel(frame: CGRect(x: 15,
                                                y: self.view.frame.size.height - actualSpaceRequired - 73 - 20,
                                                width: self.view.frame.size.width - 30,
                                                height: 20))
        }
        messageLabel = UILabel(frame: CGRect(x: 15, y: self.statusLabel.frame.origin.y - actualSpaceRequired - 60,
                                             width: self.view.frame.size.width - 30,
                                             height: 60))
        pageMessageTitleLabel = UILabel(frame: CGRect(x: 20,
                                                      y: self.messageLabel.frame.origin.y - actualSpaceRequired - 36,
                                                      width: self.view.frame.size.width - 40,
                                                      height: 36))
        pageTitleLabel = UILabel(frame: CGRect(x: 20, y: 25,
                                               width: self.view.frame.size.width - 40, height: 30))
        //        let uiParams : Dictionary<String,Any> = self.requestJson["UIParams"] as! Dictionary<String, Any>
        pageTitleLabel.font = UIFont.systemFont(ofSize: 18)
        pageTitleLabel.textAlignment = .center
        pageTitleLabel.textColor = self.textColor
        pageMessageTitleLabel.font = UIFont.systemFont(ofSize: 30)
        pageMessageTitleLabel.textAlignment = .center
        pageMessageTitleLabel.textColor = self.textColor
        messageLabel.font = UIFont.systemFont(ofSize: 22)
        messageLabel.numberOfLines = 0
        messageLabel.textAlignment = .center
        messageLabel.textColor = self.textColor
        statusLabel.textAlignment = .center
        statusLabel.font = UIFont.systemFont(ofSize: 16)
        statusLabel.textColor = self.textColor
        DispatchQueue.main.async {
            if let pageTitle = self.uiParams["pageTitle"] {
                self.pageTitleLabel.text = (pageTitle as! String)
            }
            if let pageMessageTitle = self.uiParams["messageTitle"] {
                self.pageMessageTitleLabel.text = (pageMessageTitle as! String)
            }
            if let messageTitle = self.uiParams["message"] {
                self.messageLabel.text = (messageTitle as! String)
            }
            if let statusTitle = self.uiParams["scanStatus1"] {
                self.statusLabel.text = (statusTitle as! String)
            } else {
                self.statusLabel.text = "Scanning..."
            }
        }
        self.previewView!.addSubview(pageTitleLabel)
        self.previewView!.addSubview(pageMessageTitleLabel)
        self.previewView!.addSubview(messageLabel)
        self.previewView!.addSubview(statusLabel)
    }
    func updateScanStatusLabel() {
        //        let uiParams : Dictionary<String,Any> = self.requestJson["UIParams"] as! Dictionary<String, Any>
        DispatchQueue.main.async {
            if let statusTitle = self.uiParams["scanStatus3"] {
                self.statusLabel.text = (statusTitle as! String)
            } else {
                self.statusLabel.text = "Hold Steady..."
            }
        }
    }
    private func setupOverLay() {
        self.captureDeviceResolution = CGSize.init(width: self.view.frame.size.width,
                                                   height: self.view.frame.size.height)
        let captureDeviceResolution = self.captureDeviceResolution
        let captureDeviceBounds = CGRect(x: 0,
                                         y: 0,
                                         width: captureDeviceResolution.width,
                                         height: captureDeviceResolution.height)
        let captureDeviceBoundsCenterPoint = CGPoint(x: captureDeviceBounds.midX,
                                                     y: captureDeviceBounds.midY)
        let normalizedCenterPoint = CGPoint(x: 0.5, y: 0.5)
        guard let rootLayer = self.rootLayer else {
            print("view was not property initialized")
            return
        }
        let overlayLayer = CALayer()
        overlayLayer.name = "DetectionOverlay"
        overlayLayer.masksToBounds = true
        overlayLayer.anchorPoint = normalizedCenterPoint
        overlayLayer.bounds = captureDeviceBounds
        overlayLayer.position = CGPoint(x: rootLayer.bounds.midX, y: rootLayer.bounds.midY)
        let cardRectangleShapeLayer = CAShapeLayer()
        cardRectangleShapeLayer.name = "cardRectangleShapeLayer"
        cardRectangleShapeLayer.bounds = captureDeviceBounds
        cardRectangleShapeLayer.anchorPoint = normalizedCenterPoint
        cardRectangleShapeLayer.position = captureDeviceBoundsCenterPoint
        cardRectangleShapeLayer.fillColor = nil
        //        faceRectangleShapeLayer.strokeColor = UIColor.green.withAlphaComponent(0.7).cgColor
        cardRectangleShapeLayer.lineWidth = 5
        cardRectangleShapeLayer.shadowOpacity = 0.7
        cardRectangleShapeLayer.shadowRadius = 5
        let cardLandmarksShapeLayer = CAShapeLayer()
        cardLandmarksShapeLayer.name = "cardLandmarksShapeLayer"
        cardLandmarksShapeLayer.bounds = captureDeviceBounds
        cardLandmarksShapeLayer.anchorPoint = normalizedCenterPoint
        cardLandmarksShapeLayer.position = captureDeviceBoundsCenterPoint
        cardLandmarksShapeLayer.fillColor = nil
        //        faceLandmarksShapeLayer.strokeColor = UIColor.yellow.withAlphaComponent(0.7).cgColor
        cardLandmarksShapeLayer.lineWidth = 3
        cardLandmarksShapeLayer.shadowOpacity = 0.7
        cardLandmarksShapeLayer.shadowRadius = 5
        let ellipseWidth = CGFloat(self.view.frame.size.width)
        let ellipseHieght = CGFloat(self.view.frame.size.height)
        let path = UIBezierPath(roundedRect: CGRect(x: 0,
                                                    y: 0,
                                                    width: ellipseWidth,
                                                    height: ellipseHieght), cornerRadius: 0)
        //        let uiParams : Dictionary<String,Any> = self.requestJson["UIParams"] as! Dictionary<String, Any>
        let deviceWidth = self.view.frame.size.width
        let deviceHeight = self.view.frame.size.height
        var portraitXMarginPercent = "10"
        if let portraitMargin = uiParams["portraitMarginPercent"] {
            if !((portraitMargin as! String) == "") {
                portraitXMarginPercent = (portraitMargin as! String)
            }
        }
        var myPortraitPercent = (portraitXMarginPercent as NSString).floatValue
        myPortraitPercent = (myPortraitPercent/100)
        let myPotraitXvalue = ((myPortraitPercent) * Float(deviceWidth)) / 2
        let myPortraitWidth = Float(deviceWidth) - (myPotraitXvalue * 2)
        var widthHeightRatio = "3:2"
        if let whRatio = self.requestJson["documentWHRatio"] {
            if !((whRatio as! String) == "") {
                widthHeightRatio = whRatio as! String
            }
        }
        //        let widthHeightRatio = self.requestJson["documentWHRatio"] as! String
        let widthHeightArray = widthHeightRatio.components(separatedBy: ":")
        let myWidthFloat = (widthHeightArray[0] as NSString).floatValue
        let myHeightFloat = (widthHeightArray[1] as NSString).floatValue
        var myPortraitHeight = (myPortraitWidth / myWidthFloat)  * myHeightFloat
        var portraitYMarginPercent = "20"
        if let portraitYMargin = uiParams["topMarginPercent"] {
            if !((portraitYMargin as! String) == "") {
                portraitYMarginPercent = (portraitYMargin as! String)
            }
        }
        var myPortraitYvalue = (portraitYMarginPercent as NSString).floatValue
        myPortraitYvalue = ((myPortraitYvalue/100)/2) * Float(deviceHeight)
        let minimuY: Float = 60.0
        if myPortraitYvalue < minimuY {
            myPortraitYvalue = minimuY
        }
        let minButtomSpace: Float = 115.0
        let myDeviceHeight = Float(deviceHeight)
        let availableHeight = myDeviceHeight - minButtomSpace - myPortraitYvalue
        if myPortraitHeight > availableHeight {
            myPortraitHeight = availableHeight
        }
        let myRequiredRectOriginal = CGRect(x: CGFloat(myPotraitXvalue),
                                            y: CGFloat(myPortraitYvalue),
                                            width: CGFloat(myPortraitWidth),
                                            height: CGFloat(myPortraitHeight))
        self.myOriginalRect = myRequiredRectOriginal
        let circlePath = UIBezierPath(rect: myRequiredRectOriginal)
        path.append(circlePath)
        path.usesEvenOddFillRule = true
        let fillLayer = CAShapeLayer()
        fillLayer.path = path.cgPath
        fillLayer.anchorPoint = normalizedCenterPoint
        fillLayer.name = "VideoPreviewFill"
        fillLayer.fillRule = .evenOdd
        let blackGGColor = self.backgroundColor.cgColor
        fillLayer.fillColor = blackGGColor
        fillLayer.opacity = 1.0
        self.previewView.layer.addSublayer(fillLayer)
        rootLayer.addSublayer(fillLayer)
        overlayLayer.addSublayer(cardRectangleShapeLayer)
        cardRectangleShapeLayer.addSublayer(cardLandmarksShapeLayer)
        fillLayer.addSublayer(overlayLayer)
        self.detectionOverlayLayer = overlayLayer
        self.detectedFaceRectangleShapeLayer = cardRectangleShapeLayer
        self.detectedFaceLandmarksShapeLayer = cardLandmarksShapeLayer
        self.setUpLabels()
    }
    func getBiometricType() -> BiometricType {
        let authenticationContext = LAContext()
        _ = authenticationContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics, error: nil)
        switch authenticationContext.biometryType {
        case .faceID:
            return .face
        case .touchID:
            return .touch
        default:
            return .none
        }
    }
    enum BiometricType {
        case touch
        case face
        case none
    }
    private func setupNavigationBar() {
        //        navigationItem.setLeftBarButton(flashButton, animated: false)
        //        navigationItem.setRightBarButton(autoScanButton, animated: false)
        //
        //        if UIImagePickerController.isFlashAvailable(for: .rear) == false {
        //            let flashOffImage = UIImage(named: "flashUnavailable",
        //                                        in: Bundle(for:                                                                 ScannerViewController.self),
        //                                        compatibleWith: nil)
        //            flashButton.image = flashOffImage
        //            flashButton.tintColor = UIColor.lightGray
        //        }
        //        let uiParams : Dictionary<String,Any> = self.requestJson["UIParams"] as! Dictionary<String, Any>
        if let toggleButton = uiParams["toggleButton"] {
            let toggleButtonRequired = (toggleButton as! String)
            if toggleButtonRequired == "Y"{
                navigationItem.setRightBarButton(autoScanButton, animated: false)
                shutterButton.isUserInteractionEnabled = false
                shutterButton.isHidden = true
            } else {
                // Hiding shutter button as per user requirement
                shutterButton.isUserInteractionEnabled = false
                shutterButton.isHidden = true
            }
        } else {
            // Hiding shutter button as per user requirement
            shutterButton.isUserInteractionEnabled = false
            shutterButton.isHidden = true
        }
    }
    private func setupConstraints() {
        var quadViewConstraints = [NSLayoutConstraint]()
        var cancelButtonConstraints = [NSLayoutConstraint]()
        var shutterButtonConstraints = [NSLayoutConstraint]()
        var activityIndicatorConstraints = [NSLayoutConstraint]()
        quadViewConstraints = [
            quadView.topAnchor.constraint(equalTo: view.topAnchor),
            view.bottomAnchor.constraint(equalTo: quadView.bottomAnchor),
            view.trailingAnchor.constraint(equalTo: quadView.trailingAnchor),
            quadView.leadingAnchor.constraint(equalTo: view.leadingAnchor)
        ]
        shutterButtonConstraints = [
            shutterButton.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            shutterButton.widthAnchor.constraint(equalToConstant: 65.0),
            shutterButton.heightAnchor.constraint(equalToConstant: 65.0)
        ]
        activityIndicatorConstraints = [
            activityIndicator.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            activityIndicator.centerYAnchor.constraint(equalTo: view.centerYAnchor)
        ]
        if #available(iOS 11.0, *) {
            cancelButtonConstraints = [
                cancelButton.leftAnchor.constraint(equalTo: view.safeAreaLayoutGuide.leftAnchor, constant: 24.0),
                view.safeAreaLayoutGuide.bottomAnchor.constraint(equalTo: cancelButton.bottomAnchor,
                                                                 constant: (45.0 / 2) - 15.0)
            ]
            let shutterButtonBottomConstraint = view.safeAreaLayoutGuide.bottomAnchor.constraint(
                equalTo: shutterButton.bottomAnchor, constant: 8.0)
            shutterButtonConstraints.append(shutterButtonBottomConstraint)
        } else {
            cancelButtonConstraints = [
                cancelButton.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 24.0),
                view.bottomAnchor.constraint(equalTo: cancelButton.bottomAnchor, constant: (65.0 / 2) - 10.0)
            ]
            let shutterButtonBottomConstraint = view.bottomAnchor.constraint(equalTo: shutterButton.bottomAnchor,
                                                                             constant: 8.0)
            shutterButtonConstraints.append(shutterButtonBottomConstraint)
        }
        NSLayoutConstraint.activate(quadViewConstraints
                                        + cancelButtonConstraints +
                                        shutterButtonConstraints +
                                        activityIndicatorConstraints)
    }
    // MARK: - Tap to Focus
    /// Called when the AVCaptureDevice detects that the subject
    ///  area has changed significantly. When it's called, we reset the focus so the camera is no longer out of focus.
    @objc private func subjectAreaDidChange() {
        /// Reset the focus and exposure back to automatic
        do {
            try CaptureSession.current.resetFocusToAuto()
        } catch {
            let error = ImageScannerControllerError.inputDevice
            guard let captureSessionManager = captureSessionManager else { return }
            captureSessionManager.delegate?.captureSessionManager(captureSessionManager, didFailWithError: error)
            return
        }
        /// Remove the focus rectangle if one exists
        CaptureSession.current.removeFocusRectangleIfNeeded(focusRectangle, animated: true)
    }
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesBegan(touches, with: event)
        guard  let touch = touches.first else { return }
        let touchPoint = touch.location(in: view)
        let convertedTouchPoint: CGPoint = videoPreviewLayer.captureDevicePointConverted(fromLayerPoint: touchPoint)
        CaptureSession.current.removeFocusRectangleIfNeeded(focusRectangle, animated: false)
        focusRectangle = FocusRectangleView(touchPoint: touchPoint)
        view.addSubview(focusRectangle)
        do {
            try CaptureSession.current.setFocusPointToTapPoint(convertedTouchPoint)
        } catch {
            let error = ImageScannerControllerError.inputDevice
            guard let captureSessionManager = captureSessionManager else { return }
            captureSessionManager.delegate?.captureSessionManager(captureSessionManager, didFailWithError: error)
            return
        }
    }
    // MARK: - Actions
    @objc private func captureImage(_ sender: UIButton) {
        (navigationController as? ImageScannerController)?.flashToBlack()
        shutterButton.isUserInteractionEnabled = false
        captureSessionManager?.capturePhoto()
    }
    @objc private func toggleAutoScan() {
        if CaptureSession.current.isAutoScanEnabled {
            CaptureSession.current.isAutoScanEnabled = false
            autoScanButton.title = NSLocalizedString("wescan.scanning.manual",
                                                     tableName: nil,
                                                     bundle: Bundle(for: ScannerViewController.self),
                                                     value: "Auto",
                                                     comment: "The manual button state")
            // open shutter button as per user requirement
            shutterButton.isUserInteractionEnabled = true
            shutterButton.isHidden = false
        } else {
            CaptureSession.current.isAutoScanEnabled = true
            autoScanButton.title = NSLocalizedString("wescan.scanning.auto",
                                                     tableName: nil,
                                                     bundle: Bundle(for: ScannerViewController.self),
                                                     value: "Manual",
                                                     comment: "The auto button state")
            // hide shutter button as per user requirement
            shutterButton.isUserInteractionEnabled = false
            shutterButton.isHidden = true
        }
    }
    @objc private func toggleFlash() {
        let state = CaptureSession.current.toggleFlash()
        let flashImage = UIImage(named: "flash", in: Bundle(for: ScannerViewController.self), compatibleWith: nil)
        let flashOffImage = UIImage(named: "flashUnavailable",
                                    in: Bundle(for: ScannerViewController.self),
                                    compatibleWith: nil)
        switch state {
        case .on:
            flashEnabled = true
            flashButton.image = flashImage
            flashButton.tintColor = .yellow
        case .off:
            flashEnabled = false
            flashButton.image = flashImage
            flashButton.tintColor = .white
        case .unknown, .unavailable:
            flashEnabled = false
            flashButton.image = flashOffImage
            flashButton.tintColor = UIColor.lightGray
        }
    }
    @objc private func cancelImageScannerController() {
        self.myTimer?.invalidate()
        guard let imageScannerController = navigationController as? ImageScannerController else { return }
        imageScannerController.imageScannerDelegate?.imageScannerControllerDidCancel(imageScannerController)
    }
    @objc func timeoutAction() {
        myTimer?.invalidate()
        activityIndicator.stopAnimating()
        guard let imageScannerController = navigationController as? ImageScannerController else {
            return
        }
        imageScannerController.imageScannerDelegate?.imageScannerControllerTimeout(imageScannerController)
    }
    @objc func firebaseSetupFailed() {
        myTimer?.invalidate()
        activityIndicator.stopAnimating()
        guard let imageScannerController = navigationController as? ImageScannerController else {
            return
        }
        imageScannerController.imageScannerDelegate?.imageScannerControllerFirebaseSetupFailed(imageScannerController)
    }
}

extension ScannerViewController: RectangleDetectionDelegateProtocol {
    func captureSessionManager(_ captureSessionManager: CaptureSessionManager, didFailWithError error: Error) {
        activityIndicator.stopAnimating()
        shutterButton.isUserInteractionEnabled = true
        guard let imageScannerController = navigationController as? ImageScannerController else { return }
        imageScannerController.imageScannerDelegate?.imageScannerController(imageScannerController,
                                    didFailWithError: error)
    }
    func didStartCapturingPicture(for captureSessionManager: CaptureSessionManager) {
        activityIndicator.startAnimating()
        captureSessionManager.stop()
        shutterButton.isUserInteractionEnabled = false
    }
    func cropImage(image: UIImage, toRect: CGRect) -> UIImage? {
        // Cropping is available trhough CGGraphics
        let cgImage: CGImage! = image.cgImage
        let croppedCGImage: CGImage! = cgImage.cropping(to: toRect)
        return UIImage(cgImage: croppedCGImage)
    }
    // Rotate
    func rotateImage(radians: Float, image: UIImage) -> UIImage? {
        var newSize = CGRect(origin: CGPoint.zero,
                             size: image.size).applying(CGAffineTransform(rotationAngle: CGFloat(radians))).size
        // Trim off the extremely small float value to prevent core graphics from rounding it up
        newSize.width = floor(newSize.width)
        newSize.height = floor(newSize.height)
        UIGraphicsBeginImageContextWithOptions(newSize, false, image.scale)
        let context = UIGraphicsGetCurrentContext()!
        // Move origin to middle
        context.translateBy(x: newSize.width/2, y: newSize.height/2)
        // Rotate around middle
        context.rotate(by: CGFloat(radians))
        // Draw the image at its center
        image.draw(in: CGRect(x: -image.size.width/2,
                              y: -image.size.height/2,
                              width: image.size.width,
                              height: image.size.height))
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return newImage
    }
    func captureSessionManager(_ captureSessionManager: CaptureSessionManager,
                               didCapturePicture picture: UIImage,
                               withQuad quad: Quadrilateral?) {
        activityIndicator.stopAnimating()
        let quad1 = quad
        if  quad1 != nil {
            self.updateScanStatusLabel()
            let width1: CGFloat = (quad1?.topRight.y)! - (quad1?.topLeft.y)!
            let height1: CGFloat = (quad1?.topLeft.x)! - (quad1?.bottomLeft.x)!
            let x1 = (quad1?.topLeft.y)!
            let y1: CGFloat = (CGFloat(picture.size.width)) - (quad1?.topLeft.x)!
            let myRect1 = CGRect.init(x: x1, y: y1, width: width1, height: height1)
            let myCroppedImage = cropImage(image: picture, toRect: myRect1)
            let myRequiredImage = rotateImage(radians: .pi/2, image: myCroppedImage!)
            // Custom changes for text Recognization using firebase
            if self.requestJson["textToDetect"] != nil || self.requestJson["documentType"] != nil {
                var textToDetect: [String: Any] = ["": ""]
                if self.requestJson["textToDetect"] != nil {
                    textToDetect = self.requestJson["textToDetect"] as! [String: Any]
                }
                if (textToDetect.count != 2 || textToDetect.keys.count != 2) &&
                    (self.requestJson["documentType"] == nil) {
                    myTimer?.invalidate()
                    let editVC = EditScanViewController(image: picture, quad: quad, requestJson: self.requestJson)
                    navigationController?.pushViewController(editVC, animated: false)
                } else {
                    if Bundle.main.path(forResource: "GoogleServiceInfo", ofType: "plist") != nil {
                        self.appzillonVerification.verifyDocument(requestJson:
                                                                    self.requestJson,
                                                                  myRequiredImage:
                                                                    myRequiredImage!) { (firebaseResult) in
                            let validation: Bool = firebaseResult["validation"] as! Bool
                            print("validation = \(validation)")
                            if validation == true {
                                DispatchQueue.main.async {
                                    self.myTimer?.invalidate()
                                    let editVC = EditScanViewController(image: picture,
                                                                        quad: quad,
                                                                        requestJson: self.requestJson,
                                                                        firebaseResult: firebaseResult)
                                    self.navigationController?.pushViewController(editVC, animated: false)
                                }
                            } else {
                                DispatchQueue.main.async {
                                    // Restart the session
                                    self.viewWillAppear(true)
                                }
                            }
                        }
                    } else {
                        self.firebaseSetupFailed()
                    }
                }
            } else {
                myTimer?.invalidate()
                let editVC = EditScanViewController(image: picture, quad: quad, requestJson: self.requestJson)
                navigationController?.pushViewController(editVC, animated: false)
            }
        } else {
            DispatchQueue.main.async {
                // Restart the session
                self.viewWillAppear(true)
            }
        }
        shutterButton.isUserInteractionEnabled = true
    }
    func captureSessionManager(_ captureSessionManager: CaptureSessionManager,
                               didDetectQuad quad: Quadrilateral?,
                               _ imageSize: CGSize) {
        guard let quad = quad else {
            // If no quad has been detected, we remove the currently displayed on on the quadView.
            quadView.removeQuadrilateral()
            return
        }
        let portraitImageSize = CGSize(width: imageSize.height, height: imageSize.width)
        let scaleTransform = CGAffineTransform.scaleTransform(forSize:
                                                            portraitImageSize,
                                                              aspectFillInSize: quadView.bounds.size)
        let scaledImageSize = imageSize.applying(scaleTransform)
        let rotationTransform = CGAffineTransform(rotationAngle: CGFloat.pi / 2.0)
        let imageBounds = CGRect(origin: .zero, size: scaledImageSize).applying(rotationTransform)
        let translationTransform = CGAffineTransform.translateTransform(fromCenterOfRect: imageBounds,
                                                 toCenterOfRect: quadView.bounds)
        let transforms = [scaleTransform, rotationTransform, translationTransform]
        let transformedQuad = quad.applyTransforms(transforms)
        quadView.drawQuadrilateral(quad: transformedQuad, animated: true)
    }
    func pushReviewController(image: UIImage, withQuad quad1: Quadrilateral?) {
        self.quad = quad1!
        guard let quad = quad1,
            let ciImage = CIImage(image: image) else {
                if let imageScannerController = navigationController as? ImageScannerController {
                    let error = ImageScannerControllerError.ciImageCreation
                    imageScannerController.imageScannerDelegate?.imageScannerController(
                        imageScannerController, didFailWithError: error)
                }
                return
        }
        let cgOrientation = CGImagePropertyOrientation(image.imageOrientation)
        let orientedImage = ciImage.oriented(forExifOrientation: Int32(cgOrientation.rawValue))
        let scaledQuad = quad.scale(quadView.bounds.size, image.size)
        self.quad = scaledQuad
        // Cropped Image
        var cartesianScaledQuad = scaledQuad.toCartesian(withHeight: image.size.height)
        cartesianScaledQuad.reorganize()
        let filteredImage = orientedImage.applyingFilter("CIPerspectiveCorrection", parameters: [
            "inputTopLeft": CIVector(cgPoint: cartesianScaledQuad.bottomLeft),
            "inputTopRight": CIVector(cgPoint: cartesianScaledQuad.bottomRight),
            "inputBottomLeft": CIVector(cgPoint: cartesianScaledQuad.topLeft),
            "inputBottomRight": CIVector(cgPoint: cartesianScaledQuad.topRight)
        ])
        let croppedImage = UIImage.from(ciImage: filteredImage)
        // Enhanced Image
        let enhancedImage = filteredImage.applyingAdaptiveThreshold()?.withFixedOrientation()
        let enhancedScan = enhancedImage.flatMap { ImageScannerScan(image: $0) }
        let results = ImageScannerResults(detectedRectangle: scaledQuad,
                                          originalScan: ImageScannerScan(image: image),
                                          croppedScan: ImageScannerScan(image: croppedImage),
                                          enhancedScan: enhancedScan)
        let reviewViewController = ReviewViewController(results: results)
        navigationController?.pushViewController(reviewViewController, animated: true)
    }
}
// swiftlint:enable all
