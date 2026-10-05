// swiftlint:disable all
/*
 See LICENSE folder for this sample’s licensing information.
 
 Abstract:
 Contains the main app implementation using Vision.
 */

import UIKit
import AVKit
import Vision

@objc protocol DelegateMainFaceDetectorViewController {
    func deliverCapturedImage(capturedImage: UIImage)
    func cancelledOperation(via: String)
}

@objc class MainFaceDetectorViewController: UIViewController,
                                            AVCaptureVideoDataOutputSampleBufferDelegate,
                                            DelegatePreviewViewController {
    // Main view for showing camera content.
    @IBOutlet weak var mainView: UIView?
    var headerView: UIView!
    var previewView: UIView!
    var footerView: UIView!
    // AVCapture variables to hold sequence data
    var session: AVCaptureSession?
    var previewLayer: AVCaptureVideoPreviewLayer?
    var videoDataOutput: AVCaptureVideoDataOutput?
    var videoDataOutputQueue: DispatchQueue?
    var captureDevice: AVCaptureDevice?
    var captureDeviceResolution: CGSize = CGSize()
    // Layer UI for drawing Vision results
    var rootLayer: CALayer?
    var detectionOverlayLayer: CALayer?
    var detectedFaceRectangleShapeLayer: CAShapeLayer?
    var detectedFaceLandmarksShapeLayer: CAShapeLayer?
    // Vision requests
    private var detectionRequests: [VNDetectFaceRectanglesRequest]?
    private var trackingRequests: [VNTrackObjectRequest]?
    lazy var sequenceRequestHandler = VNSequenceRequestHandler()
    // Appzillon Customization
    @objc var delegate: DelegateMainFaceDetectorViewController?
    var  smallestRectangleContainingOval = CGRect()
    var  faceRectanlge = CGRect()
    var  scaledFaceRectanlged = CGRect()
    var requestJSON = [String: Any]()
    var headerLabel = UILabel()
    var footerLabel = UILabel()
    var maximumLeftEyeArea = CGFloat()
    var minimumleftEyeArea = CGFloat()
    var maximumRightEyeArea = CGFloat()
    var minimumRightEyeArea = CGFloat()
    //     var isPreviewScreenLoaded = false //Added this to avoid succesive frames to keep on loading previewcontroller
    var backgroundColor = UIColor()
    var textColor = UIColor()
    //    var sampleBufferLive = AnyObject?.self
    var sampleBufferLive: CMSampleBuffer?
    //    var isFaceDetected = true
    var isCapturingSelfie = false
    var isAlignedInOval = false
    var isAreaCheckSuccess = false
    var leftEyeOpen = false
    var rightEyeOpen = false
    var leftEyeClose = false
    var rightEyeClose = false
    // MARK: UIViewController overrides
    @objc init(requestJSON: [String: Any]) {
        super.init(nibName: nil, bundle: nil)
        self.requestJSON=requestJSON
        var myBackgroundColor = "FFFFFF"
        var myTextColor = "000000"
        if let backgrndColour = self.requestJSON["overlayColor"] {
            myBackgroundColor = (backgrndColour as! String)
        }
        if let textclr = self.requestJSON["fontColor"] {
            myTextColor = (textclr as! String)
        }
        self.backgroundColor = self.getUIColorFromHexadecimal(hex: myBackgroundColor, viewType: "background")
        self.textColor = self.getUIColorFromHexadecimal(hex: myTextColor, viewType: "text")
    }
    @objc  required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    // MARK: UIViewController overrides
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.setNavigationBarHidden(true, animated: true)
        let viewWidth = view.bounds.width
        let viewHeight = view.bounds.height
        let headerAndFooterHeight = viewHeight/100.0*10.0
        let previewViewHeight = viewHeight-(headerAndFooterHeight+headerAndFooterHeight)
        // Dont multiply else it will small gap in between 3 views
        self.headerView = UIView(frame: CGRect(x: 0, y: 0, width: viewWidth, height: headerAndFooterHeight))
        self.previewView = UIView(frame: CGRect(x: 0,
                                                y: headerAndFooterHeight,
                                                width: viewWidth,
                                                height: previewViewHeight))
        self.footerView = UIView(frame: CGRect(x: 0,
                                               y: headerAndFooterHeight+previewViewHeight,
                                               width: viewWidth,
                                               height: headerAndFooterHeight))
        var myInstructionPosition = "2"
        if let instructionPosition = self.requestJSON["instructionPosition"] {
            myInstructionPosition = (instructionPosition as! String)
        }
        if myInstructionPosition == "1" {
            self.footerView = UIView(frame: CGRect(x: 0,
                                                   y: headerAndFooterHeight,
                                                   width: viewWidth,
                                                   height: headerAndFooterHeight))
            self.previewView = UIView(frame: CGRect(x: 0,
                                                    y: headerAndFooterHeight+headerAndFooterHeight,
                                                    width: viewWidth,
                                                    height: previewViewHeight))
        } else {
            self.previewView = UIView(frame: CGRect(x: 0,
                                                    y: headerAndFooterHeight,
                                                    width: viewWidth,
                                                    height: previewViewHeight))
            self.footerView = UIView(frame: CGRect(x: 0,
                                                   y: headerAndFooterHeight+previewViewHeight,
                                                   width: viewWidth,
                                                   height: headerAndFooterHeight))
        }
        self.view.addSubview(self.headerView!)
        self.view.addSubview(self.previewView!)
        self.view.addSubview(self.footerView!)
        setUpHeaderAndFooter()
    }
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.setNavigationBarHidden(true, animated: true)
        self.isCapturingSelfie = false
        self.session = self.setupAVCaptureSession()
        self.prepareVisionRequest()
        self.session?.startRunning()
        self.leftEyeOpen = false
        self.rightEyeOpen = false
        self.leftEyeClose = false
        self.rightEyeClose = false
    }
    func setUpHeaderAndFooter() {
        // Header back label
        let headerBackLabel = UILabel(frame: CGRect(x: 0,
                                                    y: 0,
                                                    width: self.headerView.frame.width*0.2,
                                                    height: self.headerView.frame.height))
        headerBackLabel.textAlignment = .center
        headerBackLabel.text = ""
        headerBackLabel.backgroundColor = backgroundColor
        self.headerView!.addSubview(headerBackLabel)
        // header Title Label
        headerLabel = UILabel(frame: CGRect(x: self.headerView.frame.width*0.2,
                                            y: 0,
                                            width: self.headerView.frame.width*0.6,
                                            height: self.headerView.frame.height))
        headerLabel.textAlignment = .center
        DispatchQueue.main.async {
            self.headerLabel.text = self.requestJSON["pageTitle"] as? String
            self.headerLabel.textColor = self.textColor
        }
        headerView.backgroundColor = backgroundColor
        self.headerView!.addSubview(headerLabel)
//        //Header cancel label
//        let headerCancelLabel = UILabel(frame: CGRect(x: self.headerView.frame.width*0.8, y:0 , width: self.headerView.frame.width*0.2, height: self.headerView.frame.height))
//        headerCancelLabel.textAlignment = .center
//        DispatchQueue.main.async {
//            headerCancelLabel.text = "Cancel"
//            headerCancelLabel.textColor = self.textColor
//        }
//        headerCancelLabel.backgroundColor = backgroundColor
//        let tap = UITapGestureRecognizer(target: self, action: #selector(cancelButton))
//        headerCancelLabel.isUserInteractionEnabled = true
//        headerCancelLabel.addGestureRecognizer(tap)
//        self.headerView!.addSubview(headerCancelLabel)
        let cancelButton = UIButton(frame: CGRect(x: self.headerView.frame.width*0.8,
                                                  y: 0,
                                                  width: self.headerView.frame.width*0.2,
                                                  height: self.headerView.frame.height))
//         cancelButton.backgroundColor = .white
        cancelButton.setTitleColor(.black, for: .normal)
         cancelButton.setTitle("Cancel", for: .normal)
         cancelButton.addTarget(self, action: #selector(cancelButtonAction), for: .touchUpInside)
         self.headerView!.addSubview(cancelButton)
        // Footer LAbel
        footerLabel = UILabel(frame: self.headerView.frame)
        footerLabel.textAlignment = .center
        DispatchQueue.main.async {
            self.footerLabel.text = (self.requestJSON["faceInstruction1"] as? String ?? "")
            self.footerLabel.textColor = self.textColor
        }
        footerView.backgroundColor = backgroundColor
        self.footerView!.addSubview(footerLabel)
    }
    override func viewDidAppear(_ animated: Bool) {
        let value = UIInterfaceOrientation.portrait.rawValue
        UIDevice.current.setValue(value, forKey: "orientation")
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
    fileprivate func highestResolution420Format(for device: AVCaptureDevice)
    -> (format: AVCaptureDevice.Format, resolution: CGSize)? {
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
        let deviceDiscoverySession = AVCaptureDevice.DiscoverySession(deviceTypes:
                                                                        [.builtInWideAngleCamera],
                                                                      mediaType: .video,
                                                                      position: .front)
        if let device = deviceDiscoverySession.devices.first,
           let deviceInput = try? AVCaptureDeviceInput(device: device) {
            if captureSession.canAddInput(deviceInput) {
                captureSession.addInput(deviceInput)
            }
            if let highestResolution = self.highestResolution420Format(for: device) {
                try device.lockForConfiguration()
                device.activeFormat = highestResolution.format
                device.unlockForConfiguration()
                return (device, highestResolution.resolution)
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
        videoPreviewLayer.backgroundColor = backgroundColor.cgColor
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
        //        print("teardownAVCapture")
        self.videoDataOutput = nil
        self.videoDataOutputQueue = nil
        if let previewLayer = self.previewLayer {
            previewLayer.removeFromSuperlayer()
            self.previewLayer = nil
            self.sampleBufferLive = nil
            self.videoDataOutput = nil
            self.videoDataOutputQueue = nil
            self.session?.stopRunning()
            self.session = nil
            self.captureDevice = nil
            self.rootLayer = nil
            self.detectionOverlayLayer = nil
            self.detectedFaceRectangleShapeLayer = nil
            self.detectedFaceLandmarksShapeLayer = nil
            self.detectionRequests = nil
            self.trackingRequests = nil
            self.headerView .removeFromSuperview()
            self.previewView .removeFromSuperview()
            self.footerView .removeFromSuperview()
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
    fileprivate func radiansForDegrees(_ degrees: CGFloat) -> CGFloat {
        //        print("radiansForDegrees")
        return CGFloat(Double(degrees) * Double.pi / 180.0)
    }
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
    // MARK: Performing Vision Requests
    /// - Tag: WriteCompletionHandler
    fileprivate func prepareVisionRequest() {
        //        print("prepareVisionRequest")
        // self.trackingRequests = []
        var requests = [VNTrackObjectRequest]()
        let faceDetectionRequest = VNDetectFaceRectanglesRequest(completionHandler: { (request, error) in
            if error != nil {
                print("FaceDetection error: \(String(describing: error)).")
            }
            guard let faceDetectionRequest = request as? VNDetectFaceRectanglesRequest,
                let results = faceDetectionRequest.results else {
                    return
            }
            DispatchQueue.main.async {
                // Add the observations to the tracking list
                for observation in results {
                    let faceTrackingRequest = VNTrackObjectRequest(detectedObjectObservation: observation)
                    requests.append(faceTrackingRequest)
                }
                self.trackingRequests = requests
            }
        })
        // Start with detection.  Find face, then track it.
        self.detectionRequests = [faceDetectionRequest]
        self.sequenceRequestHandler = VNSequenceRequestHandler()
        self.setupVisionDrawingLayers()
    }
    // MARK: Drawing Vision Observations
    fileprivate func setupVisionDrawingLayers() {
        //        print("setupVisionDrawingLayers")
        let captureDeviceResolution = self.captureDeviceResolution
        let captureDeviceBounds = CGRect(x: 0,
                                         y: 0,
                                         width: captureDeviceResolution.width,
                                         height: captureDeviceResolution.height)
        let captureDeviceBoundsCenterPoint = CGPoint(x: captureDeviceBounds.midX,
                                                     y: captureDeviceBounds.midY)
        let normalizedCenterPoint = CGPoint(x: 0.5, y: 0.5)
        guard let rootLayer = self.rootLayer else {
            self.presentErrorAlert(message: "view was not property initialized")
            return
        }
        let overlayLayer = CALayer()
        overlayLayer.name = "DetectionOverlay"
        overlayLayer.masksToBounds = true
        overlayLayer.anchorPoint = normalizedCenterPoint
        overlayLayer.bounds = captureDeviceBounds
        overlayLayer.position = CGPoint(x: rootLayer.bounds.midX, y: rootLayer.bounds.midY)
        let faceRectangleShapeLayer = CAShapeLayer()
        faceRectangleShapeLayer.name = "RectangleOutlineLayer"
        faceRectangleShapeLayer.bounds = captureDeviceBounds
        faceRectangleShapeLayer.anchorPoint = normalizedCenterPoint
        faceRectangleShapeLayer.position = captureDeviceBoundsCenterPoint
        faceRectangleShapeLayer.fillColor = nil
        //        faceRectangleShapeLayer.strokeColor = UIColor.green.withAlphaComponent(0.7).cgColor
        faceRectangleShapeLayer.lineWidth = 5
        faceRectangleShapeLayer.shadowOpacity = 0.7
        faceRectangleShapeLayer.shadowRadius = 5
        //               let titleTextLayer = CATextLayer()
        ////                titleTextLayer.preferredFrameSize()
        //                titleTextLayer.string = "Capture Face"
        //        titleTextLayer.bounds = captureDeviceBounds
        //            titleTextLayer.anchorPoint = normalizedCenterPoint
        //            titleTextLayer.position = captureDeviceBoundsCenterPoint
        //                titleTextLayer.foregroundColor = UIColor.green.withAlphaComponent(0.7).cgColor
        ////            titleTextLayer.position = CGPoint(x: overlayLayer.bounds.origin.x, y: overlayLayer.frame.height)
        ////                titleTextLayer.bounds = CGRect(x: 0, y: 0, width: 30 , height: 50)
        //                overlayLayer.addSublayer(titleTextLayer)
        let faceLandmarksShapeLayer = CAShapeLayer()
        faceLandmarksShapeLayer.name = "FaceLandmarksLayer"
        faceLandmarksShapeLayer.bounds = captureDeviceBounds
        faceLandmarksShapeLayer.anchorPoint = normalizedCenterPoint
        faceLandmarksShapeLayer.position = captureDeviceBoundsCenterPoint
        faceLandmarksShapeLayer.fillColor = nil
        //        faceLandmarksShapeLayer.strokeColor = UIColor.yellow.withAlphaComponent(0.7).cgColor
        faceLandmarksShapeLayer.lineWidth = 3
        faceLandmarksShapeLayer.shadowOpacity = 0.7
        faceLandmarksShapeLayer.shadowRadius = 5
        let ellipseWidth = self.previewLayer?.frame.width ?? self.previewView.frame.width
        let ellipseHieght = self.previewLayer?.frame.height ?? self.previewView.frame.height
        let path = UIBezierPath(roundedRect: CGRect(x: 0,
                                                    y: 0,
                                                    width: ellipseWidth,
                                                    height: ellipseHieght),
                                                    cornerRadius: 0)
        let ovalWidth = ellipseWidth/100*80
        let ovalHeight = ovalWidth*8/7
        let ovalX = (ellipseWidth - ovalWidth) * 0.5
        let ovalY =  (ellipseHieght - ovalHeight) * 0.5
        let ovalRectOriginal = CGRect(x: ovalX, y: ovalY, width: ovalWidth, height: ovalHeight)
        let circlePath = UIBezierPath(ovalIn: ovalRectOriginal)
        smallestRectangleContainingOval = circlePath.bounds
        path.append(circlePath)
        path.usesEvenOddFillRule = true
        let fillLayer = CAShapeLayer()
        fillLayer.path = path.cgPath
        fillLayer.anchorPoint = normalizedCenterPoint
        fillLayer.name = "VideoPreviewFill"
        fillLayer.fillRule = .evenOdd
        let blackGGColor = backgroundColor.cgColor
        fillLayer.fillColor = blackGGColor
        fillLayer.opacity = 1.0
        previewView.layer.addSublayer(fillLayer)
        rootLayer.addSublayer(fillLayer)
        overlayLayer.addSublayer(faceRectangleShapeLayer)
        faceRectangleShapeLayer.addSublayer(faceLandmarksShapeLayer)
        fillLayer.addSublayer(overlayLayer)
        self.detectionOverlayLayer = overlayLayer
        self.detectedFaceRectangleShapeLayer = faceRectangleShapeLayer
        self.detectedFaceLandmarksShapeLayer = faceLandmarksShapeLayer
        self.updateLayerGeometry()
    }
    fileprivate func updateLayerGeometry() {
        //        print("updateLayerGeometry")
        guard let overlayLayer = self.detectionOverlayLayer,
            let rootLayer = self.rootLayer,
            let previewLayer = self.previewLayer
            else {
                return
        }
        CATransaction.setValue(NSNumber(value: true),
                               forKey: kCATransactionDisableActions)
        let videoPreviewRect = previewLayer.layerRectConverted(fromMetadataOutputRect:
                                                                CGRect(x: 0, y: 0, width: 1, height: 1))
        var rotation: CGFloat
        var scaleX: CGFloat
        var scaleY: CGFloat
        // Rotate the layer into screen orientation.
        switch UIDevice.current.orientation {
        case .portraitUpsideDown:
            rotation = 180
            scaleX = videoPreviewRect.width / captureDeviceResolution.width
            scaleY = videoPreviewRect.height / captureDeviceResolution.height
        case .landscapeLeft:
            rotation = 90
            scaleX = videoPreviewRect.height / captureDeviceResolution.width
            scaleY = scaleX
        case .landscapeRight:
            rotation = -90
            scaleX = videoPreviewRect.height / captureDeviceResolution.width
            scaleY = scaleX
        default:
            rotation = 0
            scaleX = videoPreviewRect.width / captureDeviceResolution.width
            scaleY = videoPreviewRect.height / captureDeviceResolution.height
        }
        // Scale and mirror the image to ensure upright presentation.
        let affineTransform = CGAffineTransform(rotationAngle: radiansForDegrees(rotation))
            .scaledBy(x: scaleX, y: -scaleY)
        overlayLayer.setAffineTransform(affineTransform)
        // Cover entire screen UI.
        let rootLayerBounds = rootLayer.bounds
        overlayLayer.position = CGPoint(x: rootLayerBounds.midX, y: rootLayerBounds.midY)
    }
    fileprivate func addIndicators(to faceRectanglePath: CGMutablePath,
                                   faceLandmarksPath: CGMutablePath,
                                   for faceObservation: VNFaceObservation,
                                   sampleBuffer: CMSampleBuffer) {
        let displaySize = self.captureDeviceResolution
        faceRectanlge = VNImageRectForNormalizedRect(faceObservation.boundingBox,
                                                     Int(displaySize.width),
                                                     Int(displaySize.height))
        faceRectanglePath.addRect(faceRectanlge)
        let size = CGSize(width: faceObservation.boundingBox.width * previewView.bounds.width,
                          height: faceObservation.boundingBox.height * previewView.bounds.height)
        let origin = CGPoint(x: faceObservation.boundingBox.minX * previewView.bounds.width,
                             y: (1 - faceObservation.boundingBox.minY) * previewView.bounds.height - size.height)
        scaledFaceRectanlged = CGRect(origin: origin, size: size)
    }
    /// - Tag: DrawPaths
    fileprivate func drawFaceObservations(_ faceObservations: [VNFaceObservation],
                                          sampleBuffer: CMSampleBuffer) {
        guard let faceRectangleShapeLayer = self.detectedFaceRectangleShapeLayer,
            let faceLandmarksShapeLayer = self.detectedFaceLandmarksShapeLayer
            else {
                return
        }
        CATransaction.begin()
        CATransaction.setValue(NSNumber(value: true), forKey: kCATransactionDisableActions)
        let faceRectanglePath = CGMutablePath()
        let faceLandmarksPath = CGMutablePath()
        for faceObservation in faceObservations {
            self.addIndicators(to: faceRectanglePath,
                               faceLandmarksPath: faceLandmarksPath,
                               for: faceObservation,
                               sampleBuffer: sampleBuffer)
        }
        faceRectangleShapeLayer.path = faceRectanglePath
        faceLandmarksShapeLayer.path = faceLandmarksPath
        self.updateLayerGeometry()
        CATransaction.commit()
    }
    // MARK: AVCaptureVideoDataOutputSampleBufferDelegate
    /// - Tag: PerformRequests
    var lastSampleTimestamp = CACurrentMediaTime()
    var sampleRate =  2.0
    public func captureOutput(_ output: AVCaptureOutput,
                              didOutput sampleBuffer: CMSampleBuffer,
                              from connection: AVCaptureConnection) {
        self.sampleBufferLive = sampleBuffer
        if lastSampleTimestamp == 0 {
            self.lastSampleTimestamp = CACurrentMediaTime()
        } else {
            let now = CACurrentMediaTime()
            self.lastSampleTimestamp = now
        }
        var requestHandlerOptions: [VNImageOption: AnyObject] = [:]
        let cameraIntrinsicData = CMGetAttachment(sampleBuffer,
                                                  key: kCMSampleBufferAttachmentKey_CameraIntrinsicMatrix,
                                                  attachmentModeOut: nil)
        if cameraIntrinsicData != nil {
            requestHandlerOptions[VNImageOption.cameraIntrinsics] = cameraIntrinsicData
        }
        guard let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer) else {
            print("Failed to obtain a CVPixelBuffer for the current output frame.")
            return
        }
        let exifOrientation = self.exifOrientationForCurrentDeviceOrientation()
        guard let requests = self.trackingRequests, !requests.isEmpty else {
            // No tracking object detected, so perform initial detection
            let imageRequestHandler = VNImageRequestHandler(cvPixelBuffer: pixelBuffer,
                                                            orientation: exifOrientation,
                                                            options: requestHandlerOptions)
            do {
                guard let detectRequests = self.detectionRequests else {
                    return
                }
                try imageRequestHandler.perform(detectRequests)
            } catch let error as NSError {
                NSLog("Failed to perform FaceRectangleRequest: %@", error)
            }
            return
        }
        do {
            try self.sequenceRequestHandler.perform(requests,
                                                    on: pixelBuffer,
                                                    orientation: exifOrientation)
        } catch let error as NSError {
            NSLog("Failed to perform SequenceRequest: %@", error)
        }
        // Setup the next round of tracking.
        var newTrackingRequests = [VNTrackObjectRequest]()
        for trackingRequest in requests {
            guard let results = trackingRequest.results else {
                return
            }
            guard let observation = results[0] as? VNDetectedObjectObservation else {
                return
            }
            if !trackingRequest.isLastFrame {
                if observation.confidence > 0.3 {
                    trackingRequest.inputObservation = observation
                } else {
                    trackingRequest.isLastFrame = true
                }
                newTrackingRequests.append(trackingRequest)
            }
        }
        self.trackingRequests = newTrackingRequests
        if newTrackingRequests.isEmpty {
            //            isFaceDetected = false
            // Nothing to track, so abort.
            return
        }
        // Perform face landmark tracking on detected faces.
        var faceLandmarkRequests = [VNDetectFaceLandmarksRequest]()
        // Perform landmark detection on tracked faces.
        for trackingRequest in newTrackingRequests {
            let faceLandmarksRequest = VNDetectFaceLandmarksRequest(completionHandler: { (request, error) in
                if error != nil {
                    print("FaceLandmarks error: \(String(describing: error)).")
                }
                guard let landmarksRequest = request as? VNDetectFaceLandmarksRequest,
                    let results = landmarksRequest.results else {
                        print("landmarksRequest --- No face found")
                        return
                }
                // Perform all UI updates (drawing) on the main queue, not the background queue on which this handler is being called.
                DispatchQueue.main.sync {
                    //                    self.isFaceDetected = true
                    self.drawFaceObservations(results, sampleBuffer: sampleBuffer)
                }
            })
            guard let trackingResults = trackingRequest.results else {
                return
        }
            guard let observation = trackingResults[0] as? VNDetectedObjectObservation else {
                return
            }
            let faceObservation = VNFaceObservation(boundingBox: observation.boundingBox)
            faceLandmarksRequest.inputFaceObservations = [faceObservation]
            // Continue to track detected facial landmarks.
            faceLandmarkRequests.append(faceLandmarksRequest)
            let imageRequestHandler = VNImageRequestHandler(cvPixelBuffer: pixelBuffer,
                                                            orientation: exifOrientation,
                                                            options: requestHandlerOptions)
            do {
                try imageRequestHandler.perform(faceLandmarkRequests)
            } catch let error as NSError {
                NSLog("Failed to perform FaceLandmarkRequest: %@", error)
            }
        }
        // MARK: Appzillon Customizations ----- inside capture output
        isAlignedInOval = isFaceAlignedInOval()
        isAreaCheckSuccess = areaCheck()
        if isAlignedInOval {
            if isAreaCheckSuccess {
                if isCapturingSelfie {
                } else {
                    DispatchQueue.main.sync {
                        if self.requestJSON["blinkEyeDetection"] as? String == "Y" {
                            self.footerLabel.text = self.requestJSON["blinkInstruction"] as? String
                            let faceIN = self.facialDetection(sampleBuffer: sampleBuffer)
                            if faceIN {
                                print("faceIN = \(faceIN)")
                                print("leftEyeOpen=\(self.leftEyeOpen),rightEyeOpen=\(self.rightEyeOpen),leftEyeClose=\(self.leftEyeClose),rightEyeClose=\(self.rightEyeClose)")
                                self.launchPreviewController(sampleBuffer: self.sampleBufferLive!)
                            }
                        } else {
                            print("Area condition check success!")
                            self.launchPreviewController(sampleBuffer: self.sampleBufferLive!)
                        }
                    }
                }
            } else {
                DispatchQueue.main.sync {
                    self.footerLabel.text = self.requestJSON["faceInstruction2"] as? String
                }
                //                    isCapturingSelfie = false
                isAlignedInOval = false
                isAreaCheckSuccess = false
                self.leftEyeOpen = false
                self.rightEyeOpen = false
                self.leftEyeClose = false
                self.rightEyeClose = false
            }
        } else {
            DispatchQueue.main.sync {
                self.footerLabel.text = (self.requestJSON["faceInstruction1"] as? String ?? "")
            }
            //                isCapturingSelfie = false
            isAlignedInOval = false
            isAreaCheckSuccess = false

            self.leftEyeOpen = false
            self.rightEyeOpen = false
            self.leftEyeClose = false
            self.rightEyeClose = false
        }
    }
    // MARK: ==================================Appzillon Customizations=================================
    func launchPreviewController(sampleBuffer: CMSampleBuffer) {
        self.isCapturingSelfie = true
        self.footerLabel.text = self.requestJSON["holdTimeInstruction"] as? String
        var holdTime = 2.0
        if let holdTimeForCapture = self.requestJSON["holdTimeForCapture"] as? String,
           let myHoldTime = Double(holdTimeForCapture) {
            holdTime = myHoldTime
        }
        DispatchQueue.main.asyncAfter(deadline: .now() + holdTime) {
            DispatchQueue.main.async {
                self.footerLabel.text = self.requestJSON["scanStatus"] as? String
                if self.isFrameValid() {
                    guard let image = self.getImageFromSampleBuffer(sampleBuffer: self.sampleBufferLive!)
                        else {
                            return
                    }
                    if self.requestJSON["nativePreviewScreen"] as? String == "Y" {
                        let previewViewController = PreviewViewController(capturedImage: image,
                                                                          bgColor: self.backgroundColor,
                                                                          txtColor: self.textColor)
                        previewViewController.edgesForExtendedLayout = []
                        previewViewController.previewDelegate = self
                        self.session?.stopRunning()
                        self.sampleBufferLive = nil
                        self.navigationController?.pushViewController(previewViewController, animated: true)
                    } else {
                        self.sendImageViaCallBack(capturedImage: image)
                    }
                } else {
                    DispatchQueue.main.async {
                        self.footerLabel.text = (self.requestJSON["faceInstruction1"] as? String ?? "")
                    }
                    self.isCapturingSelfie = false
                }
            }
        }
    }
    func isFrameValid() -> Bool {
        return  isAreaCheckSuccess && isAlignedInOval
    }
    // MARK: Appzillon Customizations ----- positioning validations
    func areaCheck() -> Bool {
        var isFaceBigEnoughToDetect = false
        let smallestRectangleContainingOvalArea = smallestRectangleContainingOval.size.width*smallestRectangleContainingOval.size.height
        let scaledFaceRectanlgeArea = scaledFaceRectanlged.size.width*scaledFaceRectanlged.size.height
        if scaledFaceRectanlgeArea > smallestRectangleContainingOvalArea*0.30 {
            isFaceBigEnoughToDetect = true
        } else {
        }
        return isFaceBigEnoughToDetect
    }
    func isFaceAlignedInOval() -> Bool {
        var faceAligned = false
        if smallestRectangleContainingOval.contains(scaledFaceRectanlged) {
            faceAligned = true
        } else {
        }
        return faceAligned
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
        let newSize = widthRatio > heightRatio ? CGSize(width: size.width * heightRatio,
                                                        height: size.height * heightRatio):    CGSize(width: size.width * widthRatio,
                                                        height: size.height * widthRatio)
        let rect = CGRect(x: 0, y: 0, width: newSize.width, height: newSize.height)
        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        originalImage.draw(in: rect)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return newImage!
    }
    // MARK: Appzillon Customizations ----- convert Hex string to color
    func getUIColorFromHexadecimal(hex: String, viewType: String) -> UIColor {
        var color = UIColor()
        if hex.count>2 {
            color = UIColor.colorFromHex(hex: hex)
        } else {
            if viewType == "text"{
                color = UIColor.black
            } else if viewType == "background" {
                color = UIColor.white
            }
        }
        return color
    }
    // MARK: Appzillon Customizations ----- check if user blinked
    func facialDetection(sampleBuffer: CMSampleBuffer) -> Bool {
        var eyeBlinked = false
        let pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer)!
        let attachments = CMCopyDictionaryOfAttachments(allocator: kCFAllocatorDefault,
                                                        target: sampleBuffer,
                                                        attachmentMode: kCMAttachmentMode_ShouldPropagate)
        let ciImage = CIImage(cvImageBuffer: pixelBuffer, options: attachments as? [CIImageOption: Any])
        let orientation = exifOrientation(orientation: UIDevice.current.orientation)
        let options: [String: Any] = [(CIDetectorImageOrientation as NSString)
                                        as String: NSNumber(value: orientation),
                                       CIDetectorSmile: true, CIDetectorEyeBlink: true]
        let faceDetector = CIDetector(ofType: CIDetectorTypeFace, context: nil, options: options)
        let faces = faceDetector!.features(in: ciImage, options: options)
        if let face = faces.first as? CIFaceFeature {
            if !face.leftEyeClosed {
                print("leftOpen")
                self.leftEyeOpen = true
            }
            if !face.rightEyeClosed {
               print("rightOpen")
                self.rightEyeOpen = true
            }
            if face.leftEyeClosed {
                 print("leftClose")
                self.leftEyeClose = true
            }
            if face.rightEyeClosed {
                print("rightclose")
                self.rightEyeClose = true
            }
            if self.leftEyeOpen && self.rightEyeOpen && self.leftEyeClose && self.rightEyeClose {
                eyeBlinked = true
                return eyeBlinked
            }
        }
        return false
    }
    func exifOrientation(orientation: UIDeviceOrientation) -> Int {
        switch orientation {
        case .portraitUpsideDown:
            return 8
        case .landscapeLeft:
            return 3
        case .landscapeRight:
            return 1
        default:
            return 6
        }
    }
    // MARK: Appzillon Customizations ----- user cancelled operation
    @objc func cancelButton() {
        self.teardownAVCapture()
        self.dismiss(animated: true, completion: {
            self.delegate?.cancelledOperation(via: "BUTTON")
        })
    }
    @objc func cancelButtonAction() {
        self.teardownAVCapture()
        self.dismiss(animated: true, completion: {
            self.delegate?.cancelledOperation(via: "BUTTON")
        })
    }
    // MARK: Appzillon Customizations ----- delegate from preview screen
    func sendImageViaCallBack(capturedImage: UIImage) {
        self.teardownAVCapture()
        self.dismiss(animated: true, completion: {
            self.delegate?.deliverCapturedImage(capturedImage: capturedImage)
        })
    }
    func userApprovesImage(capturedImage: UIImage) {
        self.teardownAVCapture()
        self.navigationController?.dismiss(animated: true, completion: {
            self.delegate?.deliverCapturedImage(capturedImage: capturedImage)
        })
    }
}
// swiftlint:enable all
