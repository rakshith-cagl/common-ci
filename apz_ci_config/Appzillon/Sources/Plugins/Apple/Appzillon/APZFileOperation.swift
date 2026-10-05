// swiftlint:disable all
//  APZFileOperation.swift
//  Appzillon
//
//  Created by Bhavya V on 18/10/21.
//

import Foundation
import PDFKit

class APZFileOperation: APZPlugin {
    var webVW: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var appString: String
    let fileManager = FileManager()
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView!, _ jsonDict: [AnyHashable: Any]) {
        self.webVW = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        self.appString = viewController?.appString ?? StringConstants.Generic.emptyString
        super.init(plugin: webVW, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            let openType = jsonDict[StringConstants.Generic.opnType] as? String ?? StringConstants.Generic.emptyString
            switch openType {
            case StringConstants.Generic.create:
                createFile(jsonDict: jsonDict)
            case StringConstants.Generic.delete:
                fileDelete(jsonDict: jsonDict)
            case StringConstants.FileOperation.actionRead:
                fileContent(jsonDict: jsonDict)
            default:
                callBack(status: false,
                         resultKey: [StringConstants.Generic.errorCode],
                         resultValue: [StringConstants.FileOperation.invalidFileOperation])
            }
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        print("CleanPlugin for APZFileoperation")
        self.delegate.donePlugin(self)
        self.viewController = nil
    }
    // MARK: - File Operation Methods
    func fileDelete(jsonDict: [AnyHashable: Any]) {
        var isDir: ObjCBool = false
        do {
            if let filePath = jsonDict[StringConstants.Generic.filePath] as? String,
               FileManager.default.fileExists(atPath: filePath, isDirectory: &isDir) {
                let filePathURL = URL(fileURLWithPath: filePath).absoluteURL
                try fileManager.removeItem(at: filePathURL)
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: true,
                                                                            keepAlive: false,
                                                                            responseKeys: [],
                                                                            responseValues: [])
                MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
            } else {
                callBack(status: false,
                         resultKey: [StringConstants.Generic.errorCode],
                         resultValue: [StringConstants.Generic.fileNotFoundCode])
            }
        } catch {
            print("File Delete Operation Failed\(error)")
            callBack(status: false,
                     resultKey: [StringConstants.Generic.errorCode],
                     resultValue: [StringConstants.FileOperation.fileDeletionFailure])
        }
    }
    func fileContent(jsonDict: [AnyHashable: Any]) {
        var content = StringConstants.Generic.emptyString
        do {
            if let openFilePath = jsonDict[StringConstants.Generic.filePath] as? String,
               FileManager.default.fileExists(atPath: openFilePath) {
                let openFilePathURL = URL(fileURLWithPath: openFilePath).absoluteURL
                if openFilePathURL.pathExtension == StringConstants.Generic.text {
                    // UTF8 encoding works only for txt files, not PDF
                    content = try String(contentsOf: openFilePathURL, encoding: .utf8)
                } else {
                    let pdf = PDFDocument(url: openFilePathURL)
                    if let pdfString = pdf?.string {
                        content = pdfString.replacingOccurrences(of: StringConstants.Generic.newLine,
                                                                 with: StringConstants.Generic.emptyString)
                    }
                }
                let resultKey = [StringConstants.Generic.filePath,
                                 StringConstants.Generic.content]
                let resultValue = [openFilePath, content]
                callBack(status: true,
                         resultKey: resultKey,
                         resultValue: resultValue)
            } else {
                callBack(status: false,
                         resultKey: [StringConstants.Generic.errorCode],
                         resultValue: [StringConstants.Generic.fileNotFoundCode])
            }
        } catch {
            print("File Get Content Operation Failed\(error)")
            callBack(status: false,
                     resultKey: [StringConstants.Generic.errorCode],
                     resultValue: [StringConstants.Generic.fileNotFoundCode])
        }
    }
    func createFile(jsonDict: [AnyHashable: Any]) {
        do {
            if jsonDict[StringConstants.Generic.fileName] as? String != StringConstants.Generic.emptyString {
                let filePath =  jsonDict[StringConstants.Generic.filePath]
                    as? String ?? StringConstants.Generic.emptyString
                let fileName = jsonDict[StringConstants.Generic.fileName]
                    as? String ?? StringConstants.Generic.emptyString
                let fileContent = jsonDict[StringConstants.Generic.fileContent]
                    as? String ?? StringConstants.Generic.emptyString
                let destPath = getDestPath(filePath: filePath, fileName: fileName)
                //            let destPathWithoutFilePrefix = destPath.path
                deleteIfAlreadyExists(destPath: destPath.path)
                switch destPath.pathExtension {
                case StringConstants.Generic.text:
                        try createTextFile(fileContent: fileContent,
                                           filePath: filePath,
                                           destPath: destPath.path)
                        callBack(status: true,
                                 resultKey: [StringConstants.Generic.filePath],
                                 resultValue: [destPath.path])
                case StringConstants.Generic.pdf:
                    try createPDF(fileContent: fileContent,
                                  filePath: filePath,
                                  destPath: destPath.path)
                    callBack(status: true,
                             resultKey: [StringConstants.Generic.filePath],
                             resultValue: [destPath.path])
                default:
                    callBack(status: false,
                             resultKey: [StringConstants.Generic.errorCode],
                             resultValue: [StringConstants.FileOperation.invalidFileExtension])
                }
            } else {
                callBack(status: false,
                         resultKey: [StringConstants.Generic.errorCode],
                         resultValue: [StringConstants.Generic.fileCreationFailure])
            }
        } catch {
            print("Error during creating a file \(error)")
            callBack(status: false,
                     resultKey: [StringConstants.Generic.errorCode],
                     resultValue: [StringConstants.Generic.fileCreationFailure])
        }
    }
    // MARK: - File Operation Utlity Helper Methods
    private func createTextFile(fileContent: String, filePath: String, destPath: String) throws {
        do {
            try fileManager.createDirectory(at: pathToCreateDirectory(filePath: filePath),
                                            withIntermediateDirectories: true, attributes: nil)
            let url = URL(fileURLWithPath: destPath)
            try fileContent.write(to: url, atomically: true, encoding: .utf8)
            print("Created text file")
        }
    }
    private func createPDF(fileContent: String, filePath: String, destPath: String) throws {
        do {
            let fmt = UIMarkupTextPrintFormatter(markupText: fileContent)
            let render = UIPrintPageRenderer()
            render.addPrintFormatter(fmt, startingAtPageAt: 0)
            let page = CGRect(x: 0, y: 0, width: 595.2, height: 841.8) // A4, 72 dpi
            let printable = page.insetBy(dx: 0, dy: 0)
            render.setValue(NSValue(cgRect: page), forKey: StringConstants.FileOperation.paperRect)
            render.setValue(NSValue(cgRect: printable), forKey: StringConstants.FileOperation.printableRect)
            let pdfData = NSMutableData()
            UIGraphicsBeginPDFContextToData(pdfData, .zero, nil)
            for index in 1...render.numberOfPages {
                UIGraphicsBeginPDFPage()
                let bounds = UIGraphicsGetPDFContextBounds()
                render.drawPage(at: index - 1, in: bounds)
            }
            UIGraphicsEndPDFContext()
            // Save PDF file
            try fileManager.createDirectory(at: pathToCreateDirectory(filePath: filePath),
                                            withIntermediateDirectories: true, attributes: nil)
            pdfData.write(toFile: destPath, atomically: true)
        }
    }
    private func deleteIfAlreadyExists(destPath: String) {
        do {
            if fileManager.fileExists(atPath: destPath) {
                try fileManager.removeItem(atPath: destPath)
            }
        } catch {
            print("Error during deleting existing file at path: \(error)")
        }
    }
    private func getDestPath(filePath: String, fileName: String) -> URL {
        let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        var documentsDirectory = urls[0]
        if filePath.isEmpty {
            documentsDirectory.appendPathComponent("\(StringConstants.Generic.sandBoxPath)\(appString)/\(fileName)")
        } else {
            documentsDirectory.appendPathComponent("\(StringConstants.Generic.sandBoxPath)\(appString)/\(filePath)/\(fileName)")
        }
        return documentsDirectory
    }
    private func pathToCreateDirectory(filePath: String) -> URL {
        let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        var documentsDirectory = urls[0]
        if filePath.isEmpty {
            documentsDirectory.appendPathComponent("\(StringConstants.Generic.sandBoxPath)\(appString)")
        } else {
            documentsDirectory.appendPathComponent("\(StringConstants.Generic.sandBoxPath)\(appString)/\(filePath)")
        }
        return documentsDirectory
    }
    private func callBack(status: Bool, resultKey: [String], resultValue: [Any]) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKey,
                                                                    responseValues: resultValue)
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
// swiftlint:enable all
