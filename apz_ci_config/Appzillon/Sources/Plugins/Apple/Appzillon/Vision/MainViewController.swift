/*
 See LICENSE folder for this sample’s licensing information.
 
 Abstract:
 Contains the main app implementation using Vision.
 */
// swiftlint:disable all
import UIKit
import AVKit
import Vision
import LocalAuthentication

@objc protocol delegateMainViewController {
    func deliverFinalResults(finalResult: Dictionary<String,Any>)
    func cancelledOperation(via: String)
}

@objc class MainViewController: UIViewController,
                                AVCaptureVideoDataOutputSampleBufferDelegate,
                                delegateMyPreviewController {
    var appzillonVerification: AppzillonDocumentVerification!
    // UIColors
    var backgroundColor = UIColor()
    var textColor = UIColor()
    var autoManualButton: UIBarButtonItem!
    // Timer to send timeout callback
    var myTimer: Timer?
    var uiParams: Dictionary<String,Any> = [:]
    var myOriginalRect: CGRect = CGRect()
    public var requestJson: Dictionary<String,Any>=[:]
    var sentToPreview = false
    var startTimestamp: Double = 0.0
    var autoCaptureOn = true
    var isInProcess: Bool = false
    var previewView: UIView!
    // AVCapture variables to hold sequence data
    var session: AVCaptureSession?
    var previewLayer: AVCaptureVideoPreviewLayer?
    // UILabels
    var pageTitleLabel = UILabel()
    var pageMessageTitleLabel = UILabel()
    var messageLabel = UILabel()
    var statusLabel = UILabel()
    var videoDataOutput: AVCaptureVideoDataOutput?
    var videoDataOutputQueue: DispatchQueue?
    var captureDevice: AVCaptureDevice?
    var captureDeviceResolution: CGSize = CGSize()
    // Layer UI for drawing Vision results
    var rootLayer: CALayer?
    // Appzillon Customization
    @objc var delegate: delegateMainViewController?
    //    var sampleBufferLive = AnyObject?.self
    var sampleBufferLive: CMSampleBuffer?

    lazy private var shutterButton: ShutterButton = {
        //        let button = ShutterButton()
        let rect = CGRect(x: 0, y: 0, width: 0, height: 0)
        let button = ShutterButton(frame: rect, lensColor: self.textColor)
        button.translatesAutoresizingMaskIntoConstraints = false
        button.addTarget(self, action: #selector(captureImage(_:)), for: .touchUpInside)
        return button
    }()
    // MARK: Init methods
    @objc init(requestJSON: Dictionary<String,Any>) {
        self.requestJson = requestJSON
        super.init(nibName: nil, bundle: nil)
    }
    @objc  required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    // MARK: UIViewController overrides
    override func viewDidLoad() {
        super.viewDidLoad()
        appzillonVerification = AppzillonDocumentVerification()
        if let myUiParams = self.requestJson["UIParams"] {
            uiParams = myUiParams as! Dictionary<String, Any>
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
        self.setUpViews()
    }
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(false, animated: true)
        self.session = self.setupAVCaptureSession()
        self.session?.startRunning()
        self.sentToPreview = false
        startTimestamp = NSDate().timeIntervalSince1970
    }
    override func viewDidAppear(_ animated: Bool) {
        self.isInProcess = false
        let value = UIInterfaceOrientation.portrait.rawValue
        UIDevice.current.setValue(value, forKey: "orientation")
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
        if let toggleButton = uiParams["toggleButton"] {
            let toggleButtonRequired = (toggleButton as! String)
            if toggleButtonRequired == "Y"{
                if self.autoCaptureOn {
                    autoManualButton.title = "Manual"
                    shutterButton.isUserInteractionEnabled = false
                    shutterButton.isHidden = true
                } else {
                    autoManualButton.title = "Auto"
                }
            }
        }
    }
    override func didReceiveMemoryWarning() {
        print("didReceiveMemoryWarning")
        super.didReceiveMemoryWarning()
    }
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return UIInterfaceOrientationMask.portrait
    }
    override var shouldAutorotate: Bool {
        return false
    }
    // Ensure that the interface stays locked in Portrait.
    override var preferredInterfaceOrientationForPresentation: UIInterfaceOrientation {
        return .portrait
    }
    // MARK: Appzillon Customizations ---- View Setup
    func setUpViews() {
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
        myPortraitYvalue = (myPortraitYvalue/100) * Float(deviceHeight)
        let minimumY: Float = 130.0
        // (first label y starts from 90, 30 height, 10 gap = 90+30+10 = 130)
        if myPortraitYvalue < minimumY {
            myPortraitYvalue = minimumY
        }
        let minButtomSpace: Float = 50.0 + 65
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
        self.previewView = UIView(frame: myRequiredRectOriginal)
        self.previewView.backgroundColor = self.backgroundColor
        self.view.backgroundColor = self.backgroundColor
        self.view.addSubview(self.previewView!)
        let backButton: UIBarButtonItem = UIBarButtonItem(title: "Cancel",
                                                          style: .plain,
                                                          target: self,
                                                          action: #selector(cancelButton))
        self.navigationItem.leftBarButtonItem = backButton
        self.navigationController?.setNavigationBarHidden(false, animated: true)
        if let toggleButton = uiParams["toggleButton"] {
            let toggleButtonRequired = (toggleButton as! String)
            if toggleButtonRequired == "Y"{
                autoManualButton = UIBarButtonItem(title: "Auto",
                                                   style: .plain,
                                                   target: self,
                                                   action: #selector(autoManualButtonAction))
                self.navigationItem.rightBarButtonItem = autoManualButton
                view.addSubview(shutterButton)
                shutterButton.isUserInteractionEnabled = false
                shutterButton.isHidden = true
                setupConstraints()
                if self.autoCaptureOn {
                    autoManualButton.title = "Manual"
                } else {
                    autoManualButton.title = "Auto"
                }
            }
        }
        self.setUpLabels()
    }
    private func setupConstraints() {
        var shutterButtonConstraints = [NSLayoutConstraint]()
        shutterButtonConstraints = [
            shutterButton.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            shutterButton.widthAnchor.constraint(equalToConstant: 65.0),
            shutterButton.heightAnchor.constraint(equalToConstant: 65.0)
        ]
        if #available(iOS 11.0, *) {
            let shutterButtonBottomConstraint = view.safeAreaLayoutGuide.bottomAnchor.constraint(equalTo:
                                                                shutterButton.bottomAnchor,
                                                                 constant: 8.0)
            shutterButtonConstraints.append(shutterButtonBottomConstraint)
        } else {
            let shutterButtonBottomConstraint = view.bottomAnchor.constraint(equalTo:
                                                                            shutterButton.bottomAnchor,
                                                                             constant: 8.0)
            shutterButtonConstraints.append(shutterButtonBottomConstraint)
        }
        NSLayoutConstraint.activate(shutterButtonConstraints)
    }
    func setUpLabels() {
        pageTitleLabel = UILabel(frame: CGRect(x: 20,
                                               y: 90 ,
                                               width: self.view.frame.size.width - 40,
                                               height: 30))
        pageTitleLabel.font = UIFont.systemFont(ofSize: 18)
        pageTitleLabel.textAlignment = .center
        pageTitleLabel.textColor = self.textColor
        let deviceHeight = self.view.frame.size.height
        let myDeviceHeight = CGFloat(deviceHeight)
        var availableSpaceForLabels: CGFloat
        // 65 shutterButton, 60 messageLabel, 36 pageMessageTitleLabel, 20 statusLabel - Heights
        availableSpaceForLabels = myDeviceHeight - 108 - 60
            - 36 - 20 - (self.myOriginalRect.origin.y + self.myOriginalRect.size.height)
        let spaceBetweenLabels = availableSpaceForLabels/4
        var actualSpaceRequired: CGFloat
        if spaceBetweenLabels > 25.0 {
            actualSpaceRequired = spaceBetweenLabels
        } else {
            actualSpaceRequired = 25.0
        }
        let faceID = getBiometricType()
        if faceID == .face {
            statusLabel = UILabel(frame: CGRect(x: 20,
                                                y: self.view.frame.size.height - actualSpaceRequired - 20 - 108,
                                                width: self.view.frame.size.width - 40,
                                                height: 20))
        } else {
            statusLabel = UILabel(frame: CGRect(x: 20,
                                                y: self.view.frame.size.height - actualSpaceRequired - 20 - 65,
                                                width: self.view.frame.size.width - 40,
                                                height: 20))
        }
        statusLabel.textAlignment = .center
        statusLabel.font = UIFont.systemFont(ofSize: 16)
        statusLabel.textColor = self.textColor
        messageLabel = UILabel(frame: CGRect(x: 15,
                                             y: statusLabel.frame.origin.y - actualSpaceRequired - 60,
                                             width: self.view.frame.size.width - 30,
                                             height: 60))
        messageLabel.font = UIFont.systemFont(ofSize: 22)
        messageLabel.numberOfLines = 0
        messageLabel.textAlignment = .center
        messageLabel.textColor = self.textColor
        pageMessageTitleLabel = UILabel(frame: CGRect(x: 20,
                                                      y: messageLabel.frame.origin.y - actualSpaceRequired - 36,
                                                      width: self.view.frame.size.width - 40,
                                                      height: 36))
        pageMessageTitleLabel.font = UIFont.systemFont(ofSize: 30)
        pageMessageTitleLabel.textAlignment = .center
        pageMessageTitleLabel.textColor = self.textColor
        //        let uiParams : Dictionary<String,Any> = self.requestJson["UIParams"] as! Dictionary<String, Any>
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
        self.view.addSubview(pageTitleLabel)
        self.view.addSubview(pageMessageTitleLabel)
        self.view.addSubview(messageLabel)
        self.view.addSubview(statusLabel)
    }
    // MARK: AVCapture Setup
    /// - Tag: CreateCaptureSession
    fileprivate func setupAVCaptureSession() -> AVCaptureSession? {
        let captureSession = AVCaptureSession()
        do {
            let inputDevice = try self.configureFrontCamera(for: captureSession)
            self.configureVideoDataOutput(for: inputDevice.device,
                                          resolution: inputDevice.resolution,
                                          captureSession: captureSession)
            self.designatePreviewLayer(for: captureSession)
            captureSession.sessionPreset = .photo
            return captureSession
        } catch let executionError as NSError {
            self.presentError(executionError)
        } catch {
            self.presentErrorAlert(message: "An unexpected failure has occured")
        }
        self.teardownAVCapture()
        return nil
    }
    /// - Tag: ConfigureDeviceResolution
    fileprivate func highestResolution420Format(for device: AVCaptureDevice) -> (format: AVCaptureDevice.Format,
                                                                                 resolution: CGSize)? {
        var highestResolutionFormat: AVCaptureDevice.Format? = nil
        var highestResolutionDimensions = CMVideoDimensions(width: 0, height: 0)
        for format in device.formats {
            let deviceFormat = format as AVCaptureDevice.Format
            let deviceFormatDescription = deviceFormat.formatDescription
            if CMFormatDescriptionGetMediaSubType(deviceFormatDescription)
                == kCVPixelFormatType_420YpCbCr8BiPlanarFullRange {
                let candidateDimensions = CMVideoFormatDescriptionGetDimensions(deviceFormatDescription)
                if (highestResolutionFormat == nil) || (candidateDimensions.width > highestResolutionDimensions.width) {
                    highestResolutionFormat = deviceFormat
                    highestResolutionDimensions = candidateDimensions
                }
            }
        }
        if highestResolutionFormat != nil {
            let resolution = CGSize(width: CGFloat(highestResolutionDimensions.width),
                                    height: CGFloat(highestResolutionDimensions.height))
            return (highestResolutionFormat!, resolution)
        }
        return nil
    }
    fileprivate func configureFrontCamera(for captureSession: AVCaptureSession)
    throws -> (device: AVCaptureDevice, resolution: CGSize) {
        let deviceDiscoverySession = AVCaptureDevice.DiscoverySession(deviceTypes: [.builtInWideAngleCamera],
                                                                      mediaType: .video,
                                                                      position: .back)
        if let device = deviceDiscoverySession.devices.first {
            if let deviceInput = try? AVCaptureDeviceInput(device: device) {
                if captureSession.canAddInput(deviceInput) {
                    captureSession.addInput(deviceInput)
                }
                if let highestResolution = self.highestResolution420Format(for: device) {
                    try device.lockForConfiguration()
                    device.activeFormat = highestResolution.format
                    //                    device.activeVideoMinFrameDuration = CMTimeMake(value: 1, timescale: 2)
                    //                    device.activeVideoMaxFrameDuration = CMTimeMake(value: 1, timescale: 2)
                    device.unlockForConfiguration()
                    return (device, highestResolution.resolution)
                }
            }
        }
        throw NSError(domain: "ViewController", code: 1, userInfo: nil)
    }
    /// - Tag: CreateSerialDispatchQueue
    fileprivate func configureVideoDataOutput(for inputDevice: AVCaptureDevice,
                                              resolution: CGSize,
                                              captureSession: AVCaptureSession) {
        //        print("configureVideoDataOutput")
        let videoDataOutput = AVCaptureVideoDataOutput()
        videoDataOutput.alwaysDiscardsLateVideoFrames = true
        // Create a serial dispatch queue used for the sample buffer delegate as well as when a still image is captured.
        // A serial dispatch queue must be used to guarantee that video frames will be delivered in order.
        let videoDataOutputQueue = DispatchQueue(label: "com.example.apple-samplecode.VisionFaceTrack")
        videoDataOutput.setSampleBufferDelegate(self, queue: videoDataOutputQueue)
        if captureSession.canAddOutput(videoDataOutput) {
            captureSession.addOutput(videoDataOutput)
        }
        videoDataOutput.connection(with: .video)?.isEnabled = true
        videoDataOutput.videoSettings = [((kCVPixelBufferPixelFormatTypeKey as NSString)
                                            as String): NSNumber(value: kCVPixelFormatType_32BGRA as UInt32)]
        videoDataOutput.alwaysDiscardsLateVideoFrames = true
        if let captureConnection = videoDataOutput.connection(with: AVMediaType.video) {
            if captureConnection.isCameraIntrinsicMatrixDeliverySupported {
                captureConnection.isCameraIntrinsicMatrixDeliveryEnabled = true
            }
        }
        self.videoDataOutput = videoDataOutput
        self.videoDataOutputQueue = videoDataOutputQueue
        self.captureDevice = inputDevice
        self.captureDeviceResolution = resolution
    }
    /// - Tag: DesignatePreviewLayer
    fileprivate func designatePreviewLayer(for captureSession: AVCaptureSession) {
        let videoPreviewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        self.previewLayer = videoPreviewLayer
        videoPreviewLayer.name = "CameraPreview"
        videoPreviewLayer.backgroundColor = UIColor.white.cgColor
        videoPreviewLayer.videoGravity = AVLayerVideoGravity.resizeAspectFill
        if let previewRootLayer = self.previewView?.layer {
            self.rootLayer = previewRootLayer
            previewRootLayer.masksToBounds = true
            videoPreviewLayer.frame = previewRootLayer.bounds
            previewRootLayer.addSublayer(videoPreviewLayer)
        }
    }
    // Removes infrastructure for AVCapture as part of cleanup.
    fileprivate func teardownAVCapture() {
        self.sampleBufferLive = nil
        self.videoDataOutput = nil
        self.videoDataOutputQueue = nil
        if let previewLayer = self.previewLayer {
            previewLayer.removeFromSuperlayer()
            self.previewLayer = nil
        }
    }
    // MARK: Helper Methods for Error Presentation
    fileprivate func presentErrorAlert(withTitle title: String = "Unexpected Failure", message: String) {
        //        print("presentErrorAlert")
        let alertController = UIAlertController(title: title, message: message, preferredStyle: .alert)
        self.present(alertController, animated: true)
    }
    fileprivate func presentError(_ error: NSError) {
        //        print("presentError")
        self.presentErrorAlert(withTitle: "Failed with error \(error.code)", message: error.localizedDescription)
    }
    // MARK: Helper Methods for Handling Device Orientation & EXIF
    func exifOrientationForDeviceOrientation(_ deviceOrientation: UIDeviceOrientation) -> CGImagePropertyOrientation {
        switch deviceOrientation {
        case .portraitUpsideDown:
            return .rightMirrored
        case .landscapeLeft:
            return .downMirrored
        case .landscapeRight:
            return .upMirrored
        default:
            return .leftMirrored
        }
    }
    func exifOrientationForCurrentDeviceOrientation() -> CGImagePropertyOrientation {
        return exifOrientationForDeviceOrientation(UIDevice.current.orientation)
    }
    // MARK: Appzillon Customizations ---- AVCaptureVideoDataOutputSampleBufferDelegate
    var lastSampleTimestamp = CACurrentMediaTime()
    var sampleRate =  2.0
    public func captureOutput(_ output: AVCaptureOutput,
                              didOutput sampleBuffer: CMSampleBuffer,
                              from connection: AVCaptureConnection) {
        if ((NSDate().timeIntervalSince1970) - (startTimestamp)) < 5.0 {
            return
        }
        if !autoCaptureOn {
            self.sampleBufferLive = sampleBuffer
            return
        }
        if isInProcess {
            return
        }
        self.sampleBufferLive = sampleBuffer
        if lastSampleTimestamp==0 {
            self.lastSampleTimestamp = CACurrentMediaTime()
        } else {
            let now = CACurrentMediaTime()
            let timePassedSinceLastSample = now - self.lastSampleTimestamp
//            if timePassedSinceLastSample < self.sampleRate {
//                //                        return;
//            } else {
//            }
            self.lastSampleTimestamp = now
        }
        DispatchQueue.main.async {
            if self.sampleBufferLive != nil {
                self.isInProcess = true
                guard let image = self.getImageFromSampleBuffer(sampleBuffer: self.sampleBufferLive!)
                    else {
                        self.isInProcess = false
                        return
                }
                let myRequiredImage = image
                if (self.requestJson["textToDetect"] != nil) || (self.requestJson["documentType"] != nil) {
                    var textToDetect: Dictionary<String,Any> = ["":""]
                    if self.requestJson["textToDetect"] != nil {
                        textToDetect  = self.requestJson["textToDetect"] as! Dictionary<String, Any>
                    }
                    if (textToDetect.count != 2 || textToDetect.keys.count != 2)
                        &&  (self.requestJson["documentType"] == nil) {
                        var firebaseResult: Dictionary<String,Any> = [:]
                        firebaseResult["image"] = myRequiredImage
                        self.sendImageToClient(results: firebaseResult)
                    } else {
                        if Bundle.main.path(forResource: "GoogleServiceInfo", ofType: "plist") != nil {
                            self.appzillonVerification.verifyDocument(requestJson:
                                                                        self.requestJson,
                                                                      myRequiredImage:
                                                                        myRequiredImage) { (firebaseResult) in
                                let validation: Bool = firebaseResult["validation"] as! Bool
                                print("validation = \(validation)")
                                if validation == true {
                                    var myFirebaseResult = firebaseResult
                                    myFirebaseResult["image"] = myRequiredImage
                                    self.sendImageToClient(results: myFirebaseResult)
                                } else {
                                    self.isInProcess = false
                                }
                            }
                        } else {
                            self.firebaseSetupFailed()
                        }
                    }
                } else {
                    var firebaseResult: Dictionary<String,Any> = [:]
                    firebaseResult["image"] = myRequiredImage
                    self.sendImageToClient(results: firebaseResult)
                }
            } else {
                return
            }
        }
    }
    // MARK: Appzillon Customizations ----- Send image to client
    func sendImageToClient(results: Dictionary<String,Any>) {
        self.isInProcess = true
        self.myTimer?.invalidate()
        self.session?.stopRunning()
        self.teardownAVCapture()
        if let previewRequired = self.requestJson["nativePreviewScreen"] {
            let previewScreenRequired = (previewRequired as! String)
            if previewScreenRequired == "Y"{
                if !self.sentToPreview {
                    self.sentToPreview = true
                    let myPreview = MyPreviewController(image: results["image"] as! UIImage, firebaseResult: results)
                    myPreview.delegate = self
                    self.navigationController?.pushViewController(myPreview, animated: false)
                }
            } else {
                self.dismiss(animated: true, completion: {
                    self.delegate?.deliverFinalResults(finalResult: results)
                    self.delegate = nil
                })
            }
        } else {
            self.dismiss(animated: true, completion: {
                self.delegate?.deliverFinalResults(finalResult: results)
                self.delegate = nil
            })
        }
    }
    // MARK: Appzillon Customizations ----- convert buffer to image to save it
    func getImageFromSampleBuffer(sampleBuffer: CMSampleBuffer) -> UIImage? {
        guard let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else {
            return nil
        }
        CVPixelBufferLockBaseAddress(pixelBuffer, .readOnly)
        defer { CVPixelBufferUnlockBaseAddress(pixelBuffer, .readOnly) }
        let baseAddress = CVPixelBufferGetBaseAddress(pixelBuffer)
        let width = CVPixelBufferGetWidth(pixelBuffer)
        let height = CVPixelBufferGetHeight(pixelBuffer)
        let bytesPerRow = CVPixelBufferGetBytesPerRow(pixelBuffer)
        let colorSpace = CGColorSpaceCreateDeviceRGB()
        let bitmapInfo = CGBitmapInfo(rawValue: CGImageAlphaInfo.premultipliedFirst.rawValue
                                        | CGBitmapInfo.byteOrder32Little.rawValue)
        guard let context = CGContext(data: baseAddress, width: width, height: height,
                                      bitsPerComponent: 8, bytesPerRow: bytesPerRow,
                                      space: colorSpace, bitmapInfo: bitmapInfo.rawValue) else { return nil }
        guard let cgImage = context.makeImage() else { return nil }
        let image = UIImage(cgImage: cgImage, scale: 1, orientation: .right)
        return (self.resizeImage(originalImage: image))
    }
    func resizeImage(originalImage: UIImage) -> UIImage {
        let size = originalImage.size
        let widthRatio  = self.view.frame.width*UIScreen.main.scale  / size.width
        let heightRatio = self.view.frame.height*UIScreen.main.scale / size.height
        let newSize = widthRatio > heightRatio ?  CGSize(width: size.width * heightRatio,
                                                         height: size.height * heightRatio):
                                                         CGSize(width: size.width * widthRatio,
                                                         height: size.height * widthRatio)
        let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)
        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        originalImage.draw(in: rect)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return newImage!
    }
    // MARK: Appzillon Customizations ----- user cancelled operation
    @objc func cancelButton() {
        self.myTimer?.invalidate()
        self.session?.stopRunning()
        self.teardownAVCapture()
        self.dismiss(animated: true, completion: {
            self.delegate?.cancelledOperation(via: "BUTTON")
        })
    }
    @objc func timeoutAction() {
        self.myTimer?.invalidate()
        self.session?.stopRunning()
        self.teardownAVCapture()
        self.dismiss(animated: true, completion: {
            self.delegate?.cancelledOperation(via: "TIMEOUT")
        })
    }
    @objc func firebaseSetupFailed() {
        self.myTimer?.invalidate()
        self.session?.stopRunning()
        self.teardownAVCapture()
        self.dismiss(animated: true, completion: {
            self.delegate?.cancelledOperation(via: "FIREBASE")
        })
    }
    // MARK: Appzillon Customizations ----- Manual Capture
    @objc func autoManualButtonAction() {
        if self.autoCaptureOn {
            autoManualButton.title = "Auto"
            self.isInProcess = true
            shutterButton.isUserInteractionEnabled = true
            shutterButton.isHidden = false
        } else {
            autoManualButton.title = "Manual"
            self.isInProcess = false
            shutterButton.isUserInteractionEnabled = false
            shutterButton.isHidden = true
        }
        self.autoCaptureOn = !self.autoCaptureOn
    }
    @objc private func captureImage(_ sender: UIButton) {
        if self.sampleBufferLive != nil {
            guard let image = self.getImageFromSampleBuffer(sampleBuffer: self.sampleBufferLive!)
                else {
                    return
            }
            self.isInProcess = true
            let myRequiredImage = image
            if self.requestJson["textToDetect"] != nil || self.requestJson["documentType"] != nil {
                var textToDetect: Dictionary<String,Any> = ["":""]
                if self.requestJson["textToDetect"] != nil {
                    textToDetect = self.requestJson["textToDetect"] as! Dictionary<String, Any>
                }
                if textToDetect.count != 2 || textToDetect.keys.count != 2
                    && (self.requestJson["documentType"] == nil) {
                    var firebaseResult: Dictionary<String,Any>=[:]
                    firebaseResult["image"] = myRequiredImage
                    self.sendImageToClient(results: firebaseResult)
                } else {
                    if Bundle.main.path(forResource: "GoogleServiceInfo", ofType: "plist") != nil {
                        self.appzillonVerification.verifyDocument(requestJson:
                                                            self.requestJson,
                                                                  myRequiredImage:
                                                                    myRequiredImage) { (firebaseResult) in
                            let validation: Bool = firebaseResult["validation"] as! Bool
                            print("validation = \(validation)")
                            if validation == true {
                                var myFirebaseResult = firebaseResult
                                myFirebaseResult["image"] = myRequiredImage
                                self.sendImageToClient(results: myFirebaseResult)
                            } else {
                                self.isInProcess = false
                            }
                        }
                    } else {
                       self.firebaseSetupFailed()
                    }
                }
            } else {
                var firebaseResult: Dictionary<String, Any>=[:]
                firebaseResult["image"] = myRequiredImage
                self.sendImageToClient(results: firebaseResult)
            }
        } else {
            return
        }
    }
    // MARK: Preview Screen delegate
    func deliverFirebaseResultsWithImage(finalResult: Dictionary<String, Any>) {
        self.dismiss(animated: true, completion: {
            self.delegate?.deliverFinalResults(finalResult: finalResult)
            self.delegate = nil
        })
    }
    // MARK: Color extraction methods
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
}
// swiftlint:enable all
