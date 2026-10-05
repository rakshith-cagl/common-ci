//
//  APZBarcodeGenerator.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 20/07/21.
//

import Foundation

class APZBarcodeGenerator: APZPlugin {
    var viewController: AppzillonViewController?
    var fileJsonPath: String?
    var imageView: UIImageView?
    var qrCodeImage: UIImage?
    var fileName: String?
    var filePathUrl: URL?
    var webView: WKWebView
    var requestJson: [AnyHashable: Any] = [:]
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: - Plugin LifeCycle Methods
    public override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, requestJson)
    }
    open override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            APZLogger.log(logLvl: "D", message: "APZBarcodeGenerator--Execute")
            self.requestJson = jsonDict
            self.pluginId = self.requestJson[StringConstants.Generic.pluginId]
                as? String ?? StringConstants.Generic.emptyString
            self.setupForQrCode(jsonDict: jsonDict)
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        self.viewController = nil
        self.imageView = nil
        self.delegate.donePlugin(self)
        APZLogger.log(logLvl: "I", message: "APZBarcodeGenerator--Done")
    }
    // MARK: - QRCode Setup
    func setupForQrCode(jsonDict: [AnyHashable: Any]) {
        var inputString: String?
        var jsonData: Data?
        do {
            if let inputData = self.requestJson[StringConstants.BarcodeGenerator.inputString] {
                if inputData is [AnyHashable: Any] {
                    jsonData = try JSONSerialization.data(withJSONObject: inputData, options: [])
                    if let data = jsonData {
                        inputString = String(data: data, encoding: .utf8)
                    }
                } else {
                    inputString = inputData as? String
                }
            } else {
                cleanPlugin()
            }
        } catch let error {
            APZLogger.log(logLvl: "E", message: error.localizedDescription)
        }
        let imageSize = ceil((self.viewController?.view.bounds.size.width ?? 0 ) * 0.6)
        self.imageView = UIImageView(frame: CGRect(x:
                                                    (self.viewController?.view.bounds.size.width ?? 0)
                                                    * 0.5 - imageSize,
                                                   y:
                                                    (self.viewController?.view.bounds.size.height ?? 0)
                                                    * 0.5 - imageSize * 0.5,
                                                   width: imageSize,
                                                   height: imageSize))
        if let generatedQrImage = generateQRCode(from: inputString) {
            if let targetWidthStr = jsonDict["targetWidth"] as? String, !targetWidthStr.isEmpty,
                let targetHeightStr = jsonDict["targetHeight"] as? String, !targetHeightStr.isEmpty,
               let width = Double(targetWidthStr), let height = Double(targetHeightStr) {
                self.qrCodeImage = AppzillonImageUtility.shared.resizeImage(image: generatedQrImage,
                                                                            targetWidth: width,
                                                                            targetHeight: height)
            } else {
                let defaultSize = UIDevice.current.userInterfaceIdiom == .pad ? 300.0 : 200.0
                self.qrCodeImage = AppzillonImageUtility.shared.resizeImage(image: generatedQrImage,
                                                                            targetWidth: defaultSize,
                                                                            targetHeight: defaultSize)
            }
        }
        self.handleBarcodeFile()
    }
    // MARK: - Helper methods
    fileprivate func handleBarcodeFile() {
        if let base64String = self.requestJson[StringConstants.Generic.base64]
            as? String, base64String == StringConstants.Generic.no {
            self.setFilePath()
            self.setFileName()
            self.fileCreate()
        } else {
            let imageData = self.qrCodeImage?.pngData()
            if let barcodeBase64String = imageData?.base64EncodedString(options: .init(rawValue: 0)) {
                self.barCodeCallBack(status: true, message: barcodeBase64String)
            } else {
                self.barCodeCallBack(status: false, message: StringConstants.Generic.fileCreationFailure)
            }
        }
    }
    fileprivate func setFileName() {
        if let fileName = self.requestJson[StringConstants.Generic.fileName] as? String,
           !fileName.isEmpty {
            self.fileName = self.requestJson[StringConstants.Generic.fileName] as? String
        } else {
            let date = Date()
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = StringConstants.BarcodeGenerator.dateFormat
            self.fileName = dateFormatter.string(from: date)
        }
    }
    fileprivate  func setFilePath() {
        if let filePath = self.requestJson[StringConstants.Generic.fileDestinationPath] as? String, !filePath.isEmpty {
            fileJsonPath = self.requestJson[StringConstants.Generic.fileDestinationPath] as? String
        } else {
            fileJsonPath = StringConstants.BarcodeGenerator.tempFolderQRCode
        }
    }
    // MARK: - Generate QR code
    fileprivate func resizeImgIfExists(_ resizedImage: inout UIImage?) {
        if let logoImg = UIImage(named: "QRCodeLogo") {
            let actualWidth: CGFloat = (UIDevice.current.userInterfaceIdiom == .pad) ? 120 : 60
            resizedImage = resizeImage(image: logoImg, newWidth: actualWidth)
        }
    }
    func generateQRCode(from inputString: String?) -> UIImage? {
        guard let input = inputString else {
            return nil
        }
        let data = input.data(using: String.Encoding.utf8)
        if let filter = CIFilter(name: StringConstants.BarcodeGenerator.codeGenerator) {
            filter.setValue(data, forKey: StringConstants.BarcodeGenerator.inputMessage)
            let scaleX = (self.imageView?.frame.size.width ?? 0) / (filter.outputImage?.extent.size.width ?? 0)
            let scaleY = (self.imageView?.frame.size.height ?? 0) / (filter.outputImage?.extent.size.height ?? 0)
            let transform = CGAffineTransform(scaleX: scaleX, y: scaleY)
            if let output = filter.outputImage?.transformed(by: transform) {
                // Uncomment if Qr code is required in dark grey color
                //                let colorParameters = [
                //                    "inputColor0": CIColor(color: UIColor.darkGray), // Foreground
                //                    "inputColor1": CIColor(color: UIColor.clear) // Background
                //                ]
                //                let colored = output.applyingFilter("CIFalseColor", parameters: colorParameters)
                if let isLogoPresent = self.requestJson["isLogoImagePresent"] as? String,
                   isLogoPresent == StringConstants.Generic.yes {
                    // check if logo image exists
                    var resizedImage : UIImage?
                    resizeImgIfExists(&resizedImage)
                    if let logo = resizedImage?.cgImage, let qrCIImgWithLogo = output.combined(with: CIImage(cgImage: logo)) {
                        return UIImage(ciImage: qrCIImgWithLogo)
                    }
                } else {
                    return UIImage(ciImage: output)
                }
            }
        }
        return nil
    }

    fileprivate func resizeImage(image: UIImage, newWidth: CGFloat) -> UIImage? {
        let scale = newWidth / image.size.width
        let newHeight = image.size.height * scale
        UIGraphicsBeginImageContext(CGSize(width: newWidth, height: newHeight))
        image.draw(in: CGRect(x: 0, y: 0, width: newWidth, height: newHeight))
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return newImage
    }
    // MARK: - Create File
    fileprivate  func fileCreate() {
        if let filePathUrl = fileOpnPath(self.fileName), !filePathUrl.absoluteString.isEmpty {
            createImageFile(filePathUrl)
        } else {
            APZLogger.log(logLvl: "E", message: "APZBarcodeGenerator--could not open file")
            self.barCodeCallBack(status: false, message: StringConstants.Generic.fileCreationFailure)
        }
}
    fileprivate  func createImageFile(_ filePath: URL) {
        let imageData = self.qrCodeImage?.jpegData(compressionQuality: 1.0)
        if FileManagerUtility.write(imageData, toFile: filePath) {
            self.barCodeCallBack(status: true, message: filePath.path)
        } else {
            self.barCodeCallBack(status: false, message: StringConstants.Generic.fileCreationFailure)
        }
    }
    fileprivate func fileOpnPath(_ fileName: String?) -> URL? {
        guard let fileOpenDir = fileOpnDirectory() else {
            return nil
        }
        if !fileOpenDir.isEmpty {
            var isPreviousFilesDeleted = false
            do {
                try FileManager.default.removeItem(atPath: fileOpenDir)
                isPreviousFilesDeleted = true
            } catch {
                APZLogger.log(logLvl: "E", message: "APZBarcodeGenerator--could not delete file")
            }
            if isPreviousFilesDeleted {
                do {
                    try FileManager.default.createDirectory(atPath: fileOpenDir,
                                                            withIntermediateDirectories: true,
                                                            attributes: nil)
                } catch {
                    APZLogger.log(logLvl: "E", message: "APZBarcodeGenerator--could not create Dir")
                }
                let fileExtension = self.requestJson[StringConstants.Generic.encodingTyp]
                    as? String ?? StringConstants.FileExtensions.jpeg
                if let fileName = self.fileName {
                    let completeFileName = fileName + fileExtension
                    filePathUrl = URL(fileURLWithPath: fileOpenDir).appendingPathComponent(completeFileName)
                } else {
                    filePathUrl = nil
                }
            }
        } else {
            filePathUrl = nil
        }
        return filePathUrl
    }
    fileprivate  func fileOpnDirectory() -> String? {
        let dirPaths = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).map(\.path)
        let appzillonAppSandbox = URL(fileURLWithPath:
                                        dirPaths[0]).appendingPathComponent(StringConstants.Generic.sandBoxPath).path
        let appString = self.viewController?.appString ?? StringConstants.Generic.emptyString
        // If file path is not given, file will be in appString folder
        let fileOpnDir = appzillonAppSandbox + "/\(appString)/"
            + (self.fileJsonPath ?? StringConstants.Generic.emptyString)
        do {
            try FileManager.default.createDirectory(atPath: fileOpnDir,
                                                    withIntermediateDirectories: true, attributes: nil)
        } catch {
            APZLogger.log(logLvl: "E", message: "APZBarcodeGenerator--could not create directory")
        }
        return fileOpnDir
    }
    // MARK: - callbacks
    fileprivate  func barCodeCallBack(status: Bool, message: String) {
        if status {
            let resultkeys = [StringConstants.Generic.text]
            let returnResult = [message]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: returnResult)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)

            APZLogger.log(logLvl: "I", message: "APZBarcodeGenerator--Successfully Created File")
        } else {
            let resultkeys = [StringConstants.Generic.errorCode]
            let resultMess = [message]
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys: resultkeys,
                                                                        responseValues: resultMess)
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            APZLogger.log(logLvl: "E", message: "APZBarcodeGenerator--couldn't create file")
        }
        self.cleanPlugin()
    }
}

extension CIImage {
    /// Combines the current image with the given image centered.
    func combined(with image: CIImage) -> CIImage? {
        guard let combinedFilter = CIFilter(name: "CISourceOverCompositing") else { return nil }
        let centerTransform = CGAffineTransform(translationX: extent.midX - (image.extent.size.width / 2),
                                                y: extent.midY - (image.extent.size.height / 2))
        combinedFilter.setValue(image.transformed(by: centerTransform), forKey: "inputImage")
        combinedFilter.setValue(self, forKey: "inputBackgroundImage")
        return combinedFilter.outputImage!
    }
}
