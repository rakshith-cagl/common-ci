// swiftlint:disable all
// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZProcessImage: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin Life Cycle Method
    override init(plugin webView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = webView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        if let inputFile = jsonDict[StringConstants.ProcessImage.inputFile] as? [AnyHashable: Any],
           let inputFileType = inputFile[StringConstants.Generic.type] as? String,
           let originalImage = getOriginalImage(inputFile: inputFile, inputFileType: inputFileType),
           let outPutFile = jsonDict[StringConstants.Generic.outputFile] as? [AnyHashable: Any] {
            if let thersoldValue = outPutFile[StringConstants.Generic.threshold] as? String,
               let imageAction = jsonDict[StringConstants.ProcessImage.imageAction] as? String {
                if let processedImage = getProcessedImage(imageAction: imageAction,
                                                          originalImage: originalImage,
                                                          threshold: thersoldValue) {
                    processImageAndSendCallback(outPutFile, processedImage, imageAction)
                } else {
                    callBack(resultKeys: [StringConstants.Generic.errorMessage],
                             resultValues: [StringConstants.ProcessImage.imageCouldNotProcess],
                             status: false)
                    APZLogger.log(logLvl: "E", message: "APZProcessImage--could not process image")
                }
            } else {
                callBack(resultKeys: [StringConstants.Generic.errorMessage],
                         resultValues: [StringConstants.ProcessImage.thersholdOrImageActionMissing],
                         status: false)
                APZLogger.log(logLvl: "E", message: "APZProcessImage--ThersoldValue or ImageAction not Available")
            }
        } else {
            callBack(resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.Generic.invalidSrc],
                     status: false)
            APZLogger.log(logLvl: "E", message: "APZProcessImage--Incorrect image source")
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZProcessImage--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    
    fileprivate func processImageAndSendCallback(_ outPutFile: [AnyHashable : Any], _ processedImage: UIImage, _ imageAction: String) {
        if let dataFromProcessedImage = getDataFromProcessedImage(outPutFile: outPutFile,
                                                                  processedImage: processedImage) {
            if (outPutFile[StringConstants.Generic.type]) as? String == StringConstants.Generic.base64 {
                sendBase64ToCallBack(dataFromProcessedImage: dataFromProcessedImage,
                                     outputFile: outPutFile,
                                     imageAction: imageAction)
            } else {
                sendFileToCallBack(dataFromProcessedImage: dataFromProcessedImage,
                                   outputFile: outPutFile,
                                   imageAction: imageAction)
            }
        } else {
            callBack(resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.Generic.imageConversionErr],
                     status: false)
            APZLogger.log(logLvl: "E", message: "APZProcessImage--could not convert image")
        }
    }
    // MARK: ProcessImage Methods
    fileprivate func getOriginalImage(inputFile: [AnyHashable: Any], inputFileType: String) -> UIImage? {
        var originalImage: UIImage?
        if inputFileType == StringConstants.Generic.base64 {
            if let dataStr = inputFile[StringConstants.Generic.data] as? String,
               let data = Data(base64Encoded: dataStr, options: .ignoreUnknownCharacters) {
                originalImage = UIImage(data: data)
            }
        } else {
            if let dataStr = inputFile[StringConstants.Generic.data] as? String {
                originalImage = UIImage(contentsOfFile: dataStr)
            }
        }
        return originalImage
    }
    fileprivate func getProcessedImage(imageAction: String, originalImage: UIImage, threshold: String ) -> UIImage? {
        var processedImage: UIImage?
        if imageAction == StringConstants.ProcessImage.blackAndWhite {
            processedImage = AppzillonImageUtility.shared.convertToBlackAndWhiteImage(image: originalImage,
                                                                                      threshold: threshold)
        } else {
            processedImage = AppzillonImageUtility.shared.grayScaleImage(image: originalImage)
        }
        return processedImage
    }
    fileprivate func getDataFromProcessedImage(outPutFile: [AnyHashable: Any], processedImage: UIImage) -> Data? {
        var dataFromProcessedImage: Data?
        let fileEncoding = outPutFile[StringConstants.Generic.encodingTyp] as? String
        if fileEncoding == StringConstants.Generic.png {
            dataFromProcessedImage = processedImage.pngData()
        } else {
            let quality = outPutFile[StringConstants.Generic.quality]
                as? String ?? StringConstants.ProcessImage.defaultQuality
            let qualityFloat = (Float(quality) ?? 100.0)/100.0
            dataFromProcessedImage = processedImage.jpegData(compressionQuality: CGFloat(qualityFloat))
        }
        return dataFromProcessedImage
    }
    fileprivate func sendBase64ToCallBack(dataFromProcessedImage: Data,
                                          outputFile: [AnyHashable: Any],
                                          imageAction: String) {
        let imageBase64 = dataFromProcessedImage.base64EncodedString(options: .init(rawValue: 0))
        if !imageBase64.isEmpty {
            let outputFile = [StringConstants.Generic.type: StringConstants.Generic.base64,
                              StringConstants.Generic.data: imageBase64]
            callBack(resultKeys: [StringConstants.Generic.outputFile,
                                  StringConstants.ProcessImage.imageAction],
                     resultValues: [outputFile, imageAction],
                     status: true)
        } else {
            callBack(resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.Generic.encodeFailBase64],
                     status: false)
            APZLogger.log(logLvl: "E", message: "APZProcessImage--could not process image")
        }
    }
    fileprivate func sendFileToCallBack(dataFromProcessedImage: Data,
                                        outputFile: [AnyHashable: Any],
                                        imageAction: String) {
        var pathToPhotos: String = StringConstants.Generic.emptyString
        if let appString = viewController?.appString ,
           let sandBoxPath = viewController?.sandboxPath {
            pathToPhotos = URL(fileURLWithPath: sandBoxPath).appendingPathComponent("\(StringConstants.Generic.sandBoxPath)\(appString)/\(StringConstants.Generic.photo)").path
            do {
                try FileManager.default.createDirectory(atPath: pathToPhotos,
                                                        withIntermediateDirectories: true,
                                                        attributes: nil)
            } catch {
                callBack(resultKeys: [StringConstants.Generic.errorCode],
                         resultValues: [StringConstants.Generic.couldNotCreatedirectory],
                         status: false)
            }
            if let fileEncoding = outputFile[StringConstants.Generic.encodingTyp] as? String,
               let fileNameExtension = outputFile[StringConstants.Generic.fileName] {
                let fileNameWithExtension = "/\(fileNameExtension).\(String(describing: fileEncoding))"
                let finalPath = pathToPhotos.appending(fileNameWithExtension)
                if FileManagerUtility.write(dataFromProcessedImage,
                                            toFile: URL(fileURLWithPath: finalPath)) {
                    let outputFile = [StringConstants.Generic.type: StringConstants.Generic.file,
                                      StringConstants.Generic.data: finalPath]
                    callBack(resultKeys: [StringConstants.Generic.outputFile,
                                          StringConstants.ProcessImage.imageAction],
                             resultValues: [outputFile, imageAction],
                             status: true)
                } else {
                    callBack(resultKeys: [StringConstants.Generic.errorMessage],
                             resultValues: [StringConstants.Generic.fileWriteFailure],
                             status: false)
                    APZLogger.log(logLvl: "E", message: "APZProcessImage--Could not write image file")
                }
            } else {
                callBack(resultKeys: [StringConstants.Generic.errorMessage],
                         resultValues: [StringConstants.ProcessImage.fileNameorFileEncoding],
                         status: false)
                APZLogger.log(logLvl: "E", message: "APZProcessImage--FineName or FileEncoding not Available")
            }
        }
    }
    // MARK: callBack Method
    fileprivate func callBack(resultKeys: [String], resultValues: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
}
// swiftlint:enable all
