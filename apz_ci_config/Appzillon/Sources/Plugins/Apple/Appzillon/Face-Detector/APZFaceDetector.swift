//Copyright (c) 2021 Appzillon. All rights reserved.

import AVFoundation
import Foundation

class APZFaceDetector: APZPlugin, DelegateMainFaceDetectorViewController {
    var viewController: AppzillonViewController?
    var mainFaceDetectorViewController: MainFaceDetectorViewController?
    var webView: WKWebView
    var requestJson: [AnyHashable: Any] = [:]
    var pluginId: String = StringConstants.Generic.emptyString

    // MARK: - Plugin lifecycle methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, requestJson)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "I", message: "APZFaceDetector--execute")
        if !jsonDict.isEmpty {
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            self.requestJson = jsonDict
            print("APZFaceDetector--execute")
            requestPermission()
        } else {
            self.cleanPlugin()
        }
    }
    fileprivate func requestPermission() {
        AVCaptureDevice.requestAccess(for: AVMediaType.video) { granted in
            DispatchQueue.main.async {
                if granted {
                    if let dict = self.requestJson as? [String: Any] {
                        self.mainFaceDetectorViewController = MainFaceDetectorViewController.init(requestJSON: dict)
                        self.mainFaceDetectorViewController?.delegate = self
                        if let vc = self.mainFaceDetectorViewController {
                            let navController = CustomNavigationViewController(rootViewController: vc)
                            navController.orientation = "PORTRAIT"
                            navController.shouldAutoRotate = false
                            navController.modalPresentationStyle = .fullScreen
                            self.viewController?.present(navController, animated: true)
                        }
                    }
                } else {
                    self.cancelledOperation(via: "PERMISSION")
                }
            }
        }
    }

    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZFaceDetector--Done")
        self.viewController = nil
        self.mainFaceDetectorViewController = nil
        self.delegate.donePlugin(self)
        self.delegate = nil
    }

    // MARK: - Delegate methods of MainFaceDetectorViewController
    func deliverCapturedImage(capturedImage: UIImage) {
        let targetHeightStr = self.requestJson["targetHeight"] as? String ?? "0.00"
        let targetWidthStr = self.requestJson["targetWidth"] as? String ?? "0.00"
        var targetHeight = Double(targetHeightStr) ?? 0.00
        var targetWidth = Double(targetWidthStr) ?? 0.00
        var finalImg = capturedImage

        if targetHeight > 0.0 && targetWidth > 0.0 {
            if targetWidth <= targetHeight {
                targetHeight = targetWidth * (capturedImage.size.height / capturedImage.size.width)
            }
            else if targetWidth > targetHeight {
                targetWidth = targetHeight * (capturedImage.size.width / capturedImage.size.height)
            }
            if let resizedImg = AppzillonImageUtility.shared.resizeImage(image: capturedImage, targetWidth: targetWidth, targetHeight: targetHeight) {
                finalImg = resizedImg
            }
        }
        self.finaliseImage(finalImg: finalImg)
        self.cleanPlugin()
    }

    func finaliseImage(finalImg: UIImage) {
        var outputFile: [String: Any] = [:]
        if let type = self.requestJson["type"] as? String, type == "base64" {
            var base64String = StringConstants.Generic.emptyString
            if let encodingType = self.requestJson["encodingType"] as? String, encodingType.caseInsensitiveCompare("PNG") == .orderedSame {
                base64String = finalImg.toBase64PNG() ?? StringConstants.Generic.emptyString
            }
            else {
                var qualityCompression: Float = 1.0
                if let quality = self.requestJson["quality"] as? String {
                    qualityCompression = (Float(quality) ?? 100.0) / 100.0
                }
                base64String = finalImg.toBase64JPEG(quality: CGFloat(qualityCompression)) ?? StringConstants.Generic.emptyString
            }
            //base64String = base64String.components(separatedBy: CharacterSet.newlines).joined(separator: "")
            outputFile = [ "type" : "base64", "data" : base64String ]
        } else {
            let imagePath = self.saveImage(image: finalImg)
            outputFile = [ "type" : "file", "data" : imagePath]
        }
        self.callBackMethod(resultKeys: ["outputFile"], resultValues: [outputFile] as [Any], status: true)
        APZLogger.log(logLvl: "I", message: "APZVision--success")
    }

    func cancelledOperation(via: String) {
        if via == "PERMISSION" {
            self.callBackMethod(resultKeys: [StringConstants.Generic.errorCode, StringConstants.Generic.errorMessage], resultValues: ["APZ-CNT-329","Camera Permission Denied"], status: false)
        } else {
            self.callBackMethod(resultKeys: ["event", "via"], resultValues: ["cancel", via], status: false)
        }
        APZLogger.log(logLvl: "I", message: "APZFaceDetector--cancel")
        self.cleanPlugin()
    }
    // MARK: - Save image
    func saveImage(image: UIImage) -> String {
        var encodingType = self.requestJson["encodingType"] as? String ?? StringConstants.Generic.emptyString
        var imgData: Data?
        let docDirectory = FileManagerUtility.documentDirectory()

        if encodingType.caseInsensitiveCompare("PNG") == .orderedSame{
            encodingType = "png"
            imgData = image.pngData()
        } else {
            encodingType = "jpeg"
            var qualityCompression: Float = 1.0
            if let qualityOfImage = self.requestJson["quality"] as? String {
                qualityCompression = (Float(qualityOfImage) ?? 100.0) / 100.0
            }
            imgData = image.jpegData(compressionQuality: CGFloat(qualityCompression))
        }
        var pathToScannedDocuments = StringConstants.Generic.emptyString

        if let appString = viewController?.appString {
            pathToScannedDocuments = URL(fileURLWithPath: viewController?.sandboxPath ?? "\(docDirectory)").appendingPathComponent("Assets/apps/\(appString)/Selfies").path
        }
        try? FileManager.default.createDirectory(atPath: pathToScannedDocuments, withIntermediateDirectories: true, attributes: nil)

        var finalPath = StringConstants.Generic.emptyString
        if let fileName = self.requestJson["fileName"] as? String, !fileName.isEmpty {
            do {
                finalPath = URL(fileURLWithPath: pathToScannedDocuments).appendingPathComponent("\(fileName).\(encodingType)").path
            }
        }
        else {
            finalPath = URL(fileURLWithPath: pathToScannedDocuments).appendingPathComponent("\("\(Date().timeIntervalSince1970 * 1000)").\(encodingType)").path
        }
        if FileManagerUtility.write(imgData, toFile: URL(fileURLWithPath: finalPath)) {
            return finalPath
        }
        return StringConstants.Generic.emptyString
    }

    func callBackMethod(resultKeys: [String], resultValues: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: self.pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView, jsFunctionName: StringConstants.Generic.jscallBackMethod, parameter: params)

    }
}
extension UIImage {
    func toBase64PNG() -> String? {
        guard let imageData = self.pngData() else { return nil }
        return imageData.base64EncodedString(options: .init(rawValue: 0))
    }
    func toBase64JPEG(quality: CGFloat) -> String? {
        guard let imageData = self.jpegData(compressionQuality: quality) else { return nil }
        return imageData.base64EncodedString(options: .init(rawValue: 0))
    }
}
