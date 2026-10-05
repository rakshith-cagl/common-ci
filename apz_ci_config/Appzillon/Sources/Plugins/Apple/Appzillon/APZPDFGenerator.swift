// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZPDFGenerator: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var contentData = Data()
    var tempPDF: String = StringConstants.Generic.emptyString
    var currentPage: Int = 0
    var kDefaultPageHeight: CGFloat = 1934
    var kDefaultPageWidth: CGFloat = 800
    var kOriginX: CGFloat = 30
    var kOriginY: CGFloat = 30
    var requestJson: [AnyHashable: Any] = [:]
    // MARK: Plugin Life Cycle Method
    override init(plugin webView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = webView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
        tempPDF = getSandboxPath(relativePath: "temp.pdf")
        UIGraphicsBeginPDFContextToFile(tempPDF, CGRect.zero, nil)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "I", message: "APZPDFGenerator--execute")
        self.requestJson = jsonDict
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        pdfOperation()
    }
    // MARK: Plugin Clean
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZPDFGenerator--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: plugin recall
    fileprivate func pdfOperation() {
        if requestJson[StringConstants.Generic.action] as? String  == "createPdf" {
            generatePDF()
            callBack(status: true,
                     keepAlive: true,
                     resultKeys: [StringConstants.Generic.cbEvent],
                     resultValues: ["pdfGenerated"])
            return
        }
        if let action = requestJson[StringConstants.Generic.action] as? String, action == "addContent" {
            let contentType = requestJson["contentType"] as? String ?? StringConstants.Generic.emptyString
            if contentType == StringConstants.Generic.image {
                drawImage()
            } else if contentType == StringConstants.Generic.text {
                appendTextContentToPDF()
            }
        }
    }
    // MARK: String Insertion
    fileprivate func appendTextContentToPDF() {
        let content = requestJson[StringConstants.Generic.text] as? String
        if var fontType = requestJson["fontType"] as? String {
            if fontType.count == 0 {
                fontType = "Arial"
            }
            let fontTyp = fontType as NSString
            fontType = "\((fontType as NSString).substring(to: 1).uppercased())\((fontTyp).substring(from: 1))"
            let fontSize = requestJson["fontSize"] as? String ?? StringConstants.Generic.emptyString
            var fontSizeInFloat = CGFloat(Float(fontSize) ?? 0.0)
            if fontSizeInFloat == 0 {
                fontSizeInFloat = 12.0
            }
            let fontColor: CGColor
            if let hexaDecimalColor = requestJson["fontColor"] as? String {
                if hexaDecimalColor.isEmpty {
                    fontColor = UIColor.black.cgColor
                } else {
                    let colorfromHexValue = UIColor.colorFromHex(hex: hexaDecimalColor)
                    fontColor = colorfromHexValue.cgColor
                }
                let fontName: CFString = CFStringCreateWithCString(nil, fontType,
                                                                   CFStringBuiltInEncodings.macRoman.rawValue)
                let fontDetails = CTFontCreateWithName(fontName, fontSizeInFloat, nil)
                let paragraphStyle = NSMutableParagraphStyle()
                paragraphStyle.alignment = .natural
                paragraphStyle.lineBreakMode = .byWordWrapping
                let textAttributes = [
                    NSAttributedString.Key.paragraphStyle: paragraphStyle,
                    NSAttributedString.Key.font: fontDetails,
                    NSAttributedString.Key.foregroundColor: fontColor
                ] as [NSAttributedString.Key: Any]
                appendingCurrentText(content: content, textAttributes: textAttributes)
            }
        }
    }
    fileprivate func appendingCurrentText(content: String?, textAttributes: [NSAttributedString.Key: Any]) {
        if let currentText = CFAttributedStringCreate(nil,
                                                      content as CFString?,
                                                      textAttributes as CFDictionary) {
            let framesetter = CTFramesetterCreateWithAttributedString(currentText)
            var currentRange = CFRangeMake(0, 0)
            var done = false
            repeat {
                UIGraphicsBeginPDFPageWithInfo(CGRect(x: 0,
                                                      y: 0,
                                                      width: kDefaultPageWidth,
                                                      height: kDefaultPageHeight),
                                                      nil)
                drawPageNumber()
                currentRange = renderPage(currentPage, withTextRange: currentRange, andFramesetter: framesetter)
                if currentRange.location == CFAttributedStringGetLength(currentText as CFAttributedString) {
                    done = true
                }
            } while !done
            if done {
                callBack(status: true,
                         keepAlive: true,
                         resultKeys: [StringConstants.Generic.cbEvent],
                         resultValues: ["contentAdded"])
            }
        }
    }
    fileprivate func renderPage(_ pageNum: Int, withTextRange currentRange: CFRange,
                                andFramesetter framesetter: CTFramesetter) -> CFRange {
        var currentRange = currentRange
        if let currentContext = UIGraphicsGetCurrentContext() {
            currentContext.textMatrix = .identity
            let frameRect = CGRect(x: kOriginX, y: kOriginY,
                                   width: kDefaultPageWidth - kOriginX,
                                   height: kDefaultPageHeight - kOriginY)
            let framePath = CGMutablePath()
            framePath.addRect(frameRect, transform: .identity)
            let frameRef = CTFramesetterCreateFrame(framesetter, currentRange, framePath, nil)
            currentContext.translateBy(x: 0, y: kDefaultPageHeight)
            currentContext.scaleBy(x: 1.0, y: -1.0)
            CTFrameDraw(frameRef, currentContext)
            currentRange = CTFrameGetVisibleStringRange(frameRef)
            currentRange.location += currentRange.length
            currentRange.length = CFIndex(0)
        }
        return currentRange
    }
    fileprivate func drawImage() {
        if let image = UIImage(contentsOfFile: requestJson[StringConstants.Generic.imagePath]
                                as? String ?? StringConstants.Generic.emptyString) {
            let imageHeightStr = requestJson["imageHeight"] as? String ?? "0.0"
            let imageWidthStr = requestJson["imageWidth"] as? String ?? "0.0"
            var imgHeight = CGFloat(Float(imageHeightStr) ?? 0.0)
            var imgWidth =  CGFloat(Float(imageWidthStr) ?? 0.0)
            if imgHeight == 0.0 {
                imgHeight = image.size.height
            }
            if imgWidth == 0.0 {
                imgWidth = image.size.width
            }
            UIGraphicsBeginPDFPageWithInfo(CGRect(x: 0, y: 0,
                                                  width: kDefaultPageWidth,
                                                  height: kDefaultPageHeight), nil)
            drawPageNumber()
            image.draw(in: CGRect(x: kOriginX, y: kOriginX,
                                  width: imgWidth, height: imgHeight))
            callBack(status: true, keepAlive: true,
                     resultKeys: [StringConstants.Generic.cbEvent],
                     resultValues: ["contentAdded"])
        } else {
            callBack(status: false, keepAlive: true,
                     resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: [StringConstants.Generic.fileNotFoundCode])
        }
    }
    fileprivate func drawPageNumber() {
        currentPage += 1
        let theFont = UIFont.systemFont(ofSize: 12)
        let pageString = String(format: "%ld", currentPage)
        let pageStringSize = pageString.size(withAttributes: [NSAttributedString.Key.font: theFont])
        let stringRect = CGRect(x: (kDefaultPageWidth - pageStringSize.width) / 2.0,
                                y: kDefaultPageHeight - kOriginY - 5,
                                width: pageStringSize.width,
                                height: pageStringSize.height)
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineBreakMode = .byWordWrapping
        let attributes = [NSAttributedString.Key.font: UIFont.systemFont(ofSize: 15),
                          NSAttributedString.Key.paragraphStyle: paragraphStyle]
        pageString.draw(in: stringRect, withAttributes: attributes)
    }
    // MARK: File Utility methods
    fileprivate func getSandboxPath(relativePath: String) -> String {
        var fileOpnDir = StringConstants.Generic.emptyString
        let relativePathUrl = URL(fileURLWithPath: relativePath)
        let fileName = relativePathUrl.lastPathComponent
        let docDirectory =  FileManagerUtility.documentDirectory()
        let appzillonAppSandbox = docDirectory.appendingPathComponent(StringConstants.Generic.sandBoxPath).path
        if relativePath.isEmpty {
            let appString = self.viewController?.appString ?? StringConstants.Generic.emptyString
            fileOpnDir = appzillonAppSandbox + "/\(appString)/"
        } else {
            if !relativePathUrl.deletingLastPathComponent().path.isEmpty {
                let appString = self.viewController?.appString ?? StringConstants.Generic.emptyString
                fileOpnDir = appzillonAppSandbox + "/\(appString)\(relativePathUrl.deletingLastPathComponent().path)"
            } else {
                fileOpnDir = appzillonAppSandbox
            }
        }
        do {
            try FileManager.default.createDirectory(atPath: fileOpnDir,
                                                    withIntermediateDirectories: true,
                                                    attributes: nil)
        } catch {
            callBack(status: false, keepAlive: false,
                     resultKeys: [StringConstants.Generic.errorMessage],
                     resultValues: [StringConstants.Generic.couldNotCreatedirectory])
        }
        return URL(fileURLWithPath: fileOpnDir).appendingPathComponent(fileName).path
    }
    fileprivate func generatePDF() {
        UIGraphicsEndPDFContext()
        let fileManager = FileManager.default
        if let base64Required = requestJson[StringConstants.Generic.base64] as? String,
           base64Required == StringConstants.Generic.yes {
            if let fileData = NSData(contentsOfFile: self.tempPDF) {
                let fileBase64 = Base64.encode(fileData as Data)
                if !fileBase64.isEmpty {
                    callBack(status: true, keepAlive: false,
                             resultKeys: [StringConstants.Generic.text],
                             resultValues: [fileBase64])
                } else {
                    callBack(status: false, keepAlive: false,
                             resultKeys: [StringConstants.Generic.errorMessage],
                             resultValues: ["PDF Generation Failed"])
                }
            }
        } else {
            if var filePath = requestJson[StringConstants.Generic.filePath] as? String {
                if filePath.isEmpty {
                    let pdf = StringConstants.Generic.pdf
                    filePath = URL(fileURLWithPath: getDateTime()).appendingPathExtension(pdf).path                }
                filePath = getSandboxPath(relativePath: filePath)
                do {
                    try fileManager.copyItem(atPath: tempPDF, toPath: filePath)
                    callBack(status: true,
                             keepAlive: false,
                             resultKeys: [StringConstants.Generic.text],
                             resultValues: [filePath])
                 } catch {
                    callBack(status: false, keepAlive: false,
                             resultKeys: [StringConstants.Generic.errorMessage],
                             resultValues: ["PDF Generation Failed"])
                }
                do {
                    try fileManager.removeItem(atPath: tempPDF)
                } catch {
                    print("Cannot remove the item")
                }
            }
        }
        cleanPlugin()
    }
    fileprivate func getDateTime() -> String {
        let date = NSDate()
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = StringConstants.Generic.dateFormat
        let dateInFormat = dateFormatter.string(from: date as Date)
        return dateInFormat
    }
    fileprivate func callBack(status: Bool, keepAlive: Bool, resultKeys: [String], resultValues: [Any]) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: keepAlive,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
