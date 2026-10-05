//
//  APZVision.swift
//  Appzillon
//
//

import Foundation
import AVFoundation

class APZVision: APZPlugin, delegateHookToAPZVision, delegateMainViewController {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var requestJson: [AnyHashable: Any] = [:]
    var scannerViewController: HookClassFromImageScanner?
    var mainViewController: MainViewController?
    
    // MARK: Plugin Life Cycle
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        requestJson = jsonDict
        let typeOfDetection = requestJson["typeOfDetection"] as? String ?? StringConstants.Generic.emptyString
        if !typeOfDetection.isEmpty  && typeOfDetection == "new" {
            callMainViewController(jsonDict: jsonDict)
        } else {
            callScannerViewController(jsonDict: jsonDict)
        }
    }
    
    func cleanPlugin() {
        self.viewController = nil
        self.mainViewController = nil
        self.scannerViewController = nil
        self.delegate.donePlugin(self)
    }
    
    
    // MARK: Plugin functionality
    func callMainViewController(jsonDict: [AnyHashable: Any]) {
        AVCaptureDevice.requestAccess(for: AVMediaType.video, completionHandler: { [self] (granted: Bool) -> Void in
            if granted {
                if let dict = jsonDict as? [String: Any] {
                    DispatchQueue.main.async {
                        let detailView = UIViewController()
                        self.mainViewController = MainViewController(requestJSON: dict)
                        self.mainViewController?.delegate = self
                        let navController = CustomNavigationViewController(rootViewController: self.mainViewController ?? detailView)
                        navController.orientation = "PORTRAIT"
                        navController.shouldAutoRotate = false
                        navController.modalPresentationStyle = .fullScreen
                        self.viewController?.present(navController, animated: true, completion: nil)
                    }
                }
            } else {
                self.failedDueToCameraPermissionDenied()
            }
        })
    }
    
    func callScannerViewController(jsonDict: [AnyHashable: Any]) {
        let source = jsonDict["source"] as? String ?? StringConstants.Generic.emptyString
        if !source.isEmpty && source == "album" {
            if let vc = viewController {
                self.scannerViewController = HookClassFromImageScanner(viewController: vc)
                scannerViewController?.delegate = self
                scannerViewController?.selectImage(viewController: vc)
            }
        } else {
            scanImage()
        }
    }
    func scanImage() {
        AVCaptureDevice.requestAccess(for: AVMediaType.video, completionHandler: { [self] (granted: Bool) -> Void in
            if granted {
                if let reqJson = self.requestJson as? [String: Any], let vc = self.viewController {
                    self.scannerViewController = HookClassFromImageScanner(viewController: vc)
                    self.scannerViewController?.delegate = self
                    DispatchQueue.main.async {
                        self.scannerViewController?.scanImage(viewController: vc, requestJson: reqJson)
                    }
                }
            } else {
                self.failedDueToCameraPermissionDenied()
            }
        })
    }
    
    //MARK: ScannerViewController delegates
    func deliverScannedImage(scannedImage: UIImage, firebaseResult: [String : Any]) {
        var outputFile: [AnyHashable: Any] = [:]
        var dataImg = Data()
        if let type = requestJson["type"] as? String, type == "base64" {
            if let encodingType = requestJson["encodingType"] as? String,
             encodingType.caseInsensitiveCompare("png") == .orderedSame {
                if let imageData = scannedImage.pngData() {
                    dataImg = imageData
                }
            } else {
                var qualityCompression: Float = 1.0
                if let quality = requestJson["quality"] as? String {
                    qualityCompression = (Float(quality) ?? 100.0)/100.0
                }
                if let jpegImgData = scannedImage.jpegData(compressionQuality: CGFloat(qualityCompression)) {
                    dataImg = jpegImgData
                }
            }
            let base64String = dataImg.base64EncodedString(options: .init(rawValue: 0))
            outputFile = base64FireBaseResult(firebaseResult: firebaseResult, base64String: base64String)
            
        } else {
            outputFile = fileFireBaseResult(firebaseResult: firebaseResult, scannedImage: scannedImage)
        }
        callBack(status: true, resultKeys: ["outputFile"], resultValues: [outputFile])
    }
    
    func cancelledOperation() {
        callBack(status: false, resultKeys: ["event"], resultValues: ["cancel"])
    }
    
    func failedDueToCameraPermissionDenied() {
        callBack(status: false, resultKeys: [StringConstants.Generic.errorCode, StringConstants.Generic.errorMessage], resultValues: ["APZ-CNT-329", "Camera Permission Denied"])
    }
    
    func captureSessionTimeout() {
        callBack(status: false,resultKeys: [StringConstants.Generic.errorCode, StringConstants.Generic.errorMessage], resultValues: ["APZ-CNT-340", "Auto capture timed out"])
    }
    
    func captureSessionFirebaseSetupFailed() {
        callBack(status: false, resultKeys: ["event", StringConstants.Generic.errorMessage], resultValues: ["cancel", "Firebase GoogleServiceInfo.plist Missing"])
    }
    
    //MARK: Delegate methods of MainViewController
    func deliverFinalResults(finalResult: Dictionary<String, Any>) {
        var outputFile: [AnyHashable: Any] = [:]
        var dataImg = Data()
        let scannedImage = finalResult["image"] as? UIImage
        if finalResult["image"]  != nil {
            if let type = requestJson["type"] as? String, type == "base64" {
                var encodingType = requestJson["encodingType"] as? String ?? StringConstants.Generic.emptyString
                if encodingType.caseInsensitiveCompare("png") == .orderedSame {
                    if let imageData = scannedImage?.pngData() {
                        dataImg = imageData
                    }
                } else {
                    var qualityCompression: Float = 1.0
                    if let quality = requestJson["quality"] as? String {
                        qualityCompression = (Float(quality) ?? 100.0)/100.0
                    }
                    if let jpegImgData = scannedImage?.jpegData(compressionQuality: CGFloat(qualityCompression)) {
                        dataImg = jpegImgData
                    }
                }
                let base64String = dataImg.base64EncodedString(options: .init(rawValue: 0))
                outputFile = base64FireBaseResult(firebaseResult: finalResult, base64String: base64String)
            }  else {
                if let finalImage = finalResult["image"] as? UIImage {
                    outputFile = fileFireBaseResult(firebaseResult: finalResult, scannedImage: finalImage)
                }
            }
        } else {
            if let object = finalResult["ocrWholeText"] {
                outputFile = [
                    "ocrWholeText" : object
                ]
            }
        }
        callBack(status: true, resultKeys: ["outputFile"], resultValues: [outputFile])
    }
        
    func cancelledOperation(via: String) {
        if via == "TIMEOUT" {
            callBack(status: false, resultKeys: [StringConstants.Generic.errorCode, StringConstants.Generic.errorMessage], resultValues: ["APZ-CNT-340", "Auto capture timed out"])
        } else if via == "FIREBASE" {
            callBack(status: false, resultKeys: ["event", StringConstants.Generic.errorMessage], resultValues: ["cancel", "Firebase GoogleServiceInfo.plist Missing"])
        } else {
            callBack(status: false, resultKeys: ["event"], resultValues: ["cancel"])
        }
    }
    
    //MARK: Utility Methods
    func base64FireBaseResult(firebaseResult: [String : Any], base64String: String) -> [AnyHashable: Any] {
        var outputFile: [AnyHashable: Any] = [:]
        if firebaseResult.count != 0 && firebaseResult.keys.count != 0 {
            if let ocrText = firebaseResult["ocrText"], let ocrWholeText = firebaseResult["ocrWholeText"] {
                outputFile = [
                    "type" : "base64",
                    "data" : base64String,
                    "ocrText" : ocrText,
                    "ocrWholeText" : ocrWholeText
                ]
            }
        } else {
            outputFile = [
                "type" : "base64",
                "data" : base64String
            ]
        }
        return outputFile
    }
    
    func fileFireBaseResult(firebaseResult: [String: Any], scannedImage: UIImage) -> [AnyHashable: Any] {
        var outputFile: [AnyHashable: Any] = [:]
        let imagePath = saveImage(scannedImage: scannedImage)
        if firebaseResult.count != 0 && firebaseResult.keys.count != 0 {
            if let object = firebaseResult["ocrText"], let anObject = firebaseResult["ocrWholeText"] {
                outputFile = [
                    "type" : "file",
                    "data" : imagePath,
                    "ocrText" : object,
                    "ocrWholeText" : anObject
                ]
            }
        } else {
            outputFile = [
                "type" : "file",
                "data" : imagePath
            ]
        }
        return outputFile
    }
    
    //MARK: SaveImage
    func saveImage(scannedImage: UIImage?) -> String {
        var dataImg = Data()
        var encodingType = requestJson["encodingType"] as? String ?? StringConstants.Generic.emptyString
        if encodingType.caseInsensitiveCompare("png") == .orderedSame {
            encodingType = "png"
            if let imageData = scannedImage?.pngData() {
                dataImg = imageData
            }
        } else {
            encodingType = "jpeg"
            var qualityCompression: Float = 1.0
            if let quality = requestJson["quality"] as? String {
                qualityCompression = (Float(quality) ?? 100.0)/100.0
            }
            if let jpegImgData = scannedImage?.jpegData(compressionQuality: CGFloat(qualityCompression)) {
                dataImg = jpegImgData
            }
        }
        if let appString = viewController?.appString {
            let finalPath = pathToScanDocuments(appString: appString, encodingType: encodingType)
            if FileManagerUtility.write(dataImg, toFile: URL(fileURLWithPath: finalPath)) {
                return finalPath
            }
        }
        return StringConstants.Generic.emptyString
    }
    
    func pathToScanDocuments(appString: String, encodingType: String) -> String {
        let docDirPath = FileManagerUtility.documentDirectory()
        var finalPath = StringConstants.Generic.emptyString
        let filePathOfTheScannedDoc = URL(fileURLWithPath: viewController?.sandboxPath ?? "\(docDirPath)").appendingPathComponent("Assets/apps/\(appString)/ScannedDocuments").path
        do {
            try FileManager.default.createDirectory(atPath: filePathOfTheScannedDoc,
                                                    withIntermediateDirectories: true,
                                                    attributes: nil)
        } catch {
            APZLogger.log(logLvl: "E", message: "APZVision--could not create file")
        }
        
        if let fileName = requestJson["fileName"] as? String, !fileName.isEmpty {
            finalPath = URL(fileURLWithPath: filePathOfTheScannedDoc).appendingPathComponent("\(fileName).\(String(describing: encodingType))").path
        } else {
            finalPath = URL(fileURLWithPath: filePathOfTheScannedDoc).appendingPathComponent("\("\(Date().timeIntervalSince1970 * 1000)").\(String(describing: encodingType))").path
        }
        return finalPath
    }
    
   // MARK: CallBack Method
    func callBack(status: Bool, resultKeys: [String], resultValues: [Any]) {
        let param = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId, status: status, keepAlive: false,
                                                                   responseKeys: resultKeys, responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod, parameter: param)
        cleanPlugin()
    }
}

