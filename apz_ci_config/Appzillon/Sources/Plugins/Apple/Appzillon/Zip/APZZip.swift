//
//  APZZip.swift
//  Appzillon
//
//  Created by Bhavya V on 10/08/21.
//
// swiftlint:disable all

import Foundation

class APZZip: APZPlugin {
    var webVW: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var appString: String
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView!, _ jsonDict: [AnyHashable: Any]) {
        self.webVW = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        self.appString = viewController?.appString ?? ""
        super.init(plugin: webVW, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            if let actionType = jsonDict[StringConstants.Generic.actionType] as? String {
                if  actionType == StringConstants.Zip.zip {
                    archive(jsonDict: jsonDict)
                } else if actionType == StringConstants.Zip.unZip {
                    unArchive(jsonDict: jsonDict)
                }
            }
        } else {
            cleanPlugin()
        }
    }
    // MARK: Function archive
    func archive(jsonDict: [AnyHashable: Any]) {
        var isDir: ObjCBool = false
        if var sourcePath = jsonDict[StringConstants.Generic.sourcePath] as? String,
           FileManager.default.fileExists(atPath: sourcePath, isDirectory: &isDir),
           let destFolderName = jsonDict[StringConstants.Generic.destinationPath] as? String {
            if isDir.boolValue {
                sourcePath = createDummyFileIfEmptyDirectory(srcPath: sourcePath)
            }
            let sourceURL = URL(fileURLWithPath: sourcePath).absoluteURL
            let fileManager = FileManager()
            do {
                let zipPath = zipFilePath(destFolder: destFolderName)
                deleteIfAlreadyExists(fileMngr: fileManager, destPath: zipPath.path)
                try fileManager.zipItem(at: sourceURL,
                                        to: zipPath,
                                        shouldKeepParent: true,
                                        compressionMethod: .deflate,
                                        progress: nil)
                let resultKeys = [StringConstants.Generic.filePath]
                let resultValues = [zipPath.path]
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: true,
                                                                            keepAlive: false,
                                                                            responseKeys: resultKeys,
                                                                            responseValues: resultValues)
                MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
                cleanPlugin()
            } catch {
                print("Creation of ZIP archive failed with error:\(error)")
                //                return ArchiveResult(path: EMPTY_STRING, status: .failureWithStorageError)
                let resultKeys = [StringConstants.Generic.errorCode]
                let resultValues = [StringConstants.Generic.storageErrorCode]
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: false,
                                                                            keepAlive: false,
                                                                            responseKeys: resultKeys,
                                                                            responseValues: resultValues)
                MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
                cleanPlugin()
            }
        }
        let resultKeys = [StringConstants.Generic.errorCode]
        let resultValues = [StringConstants.Generic.fileNotFoundCode]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
    func cleanPlugin() {
        print("CleanPlugin for APZZip")
        self.delegate.donePlugin(self)
        self.viewController = nil
    }
    // MARK: Function unarchive
    func unArchive(jsonDict: [AnyHashable: Any]) {
        if let sourcePath = jsonDict[StringConstants.Generic.sourcePath] as? String,
           FileManager.default.fileExists(atPath: sourcePath),
           let destFolderName = jsonDict[StringConstants.Generic.destinationPath] as? String {
            do {
                let sourceURL = URL(fileURLWithPath: sourcePath).absoluteURL
                let fileManager = FileManager()
                let unzipPath = unzipFilePath(destFolder: destFolderName)
                try fileManager.unzipItem(at: sourceURL, to: unzipPath)
                let resultKeys = [StringConstants.Generic.filePath]
                let resultValues = [unzipPath.path]
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: true,
                                                                            keepAlive: false,
                                                                            responseKeys: resultKeys,
                                                                            responseValues: resultValues)
                MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
                cleanPlugin()
            } catch {
                print("UnArchive failed with error:\(error)")
                //                return ArchiveResult(path: EMPTY_STRING, status: .failureWithStorageError)
                let resultKeys = [StringConstants.Generic.errorCode]
                let resultValues = [StringConstants.Generic.storageErrorCode]
                let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                            status: false,
                                                                            keepAlive: false,
                                                                            responseKeys: resultKeys,
                                                                            responseValues: resultValues)
                MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                                       jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                       parameter: params)
                cleanPlugin()
            }
        }
        let resultKeys = [StringConstants.Generic.errorCode]
        let resultValues = [StringConstants.Generic.fileNotFoundCode]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
    // MARK: Utility Methods
    private func documentDirectoryPath() -> URL {
        let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let documentsDirectory = urls[0]
        return documentsDirectory
    }
    private func zipFilePath(destFolder: String) -> URL {
        var documentPath = documentDirectoryPath()
        let sandBoxPath = StringConstants.Generic.sandBoxPath
        let zipExtension = StringConstants.Zip.zipExtension
        documentPath.appendPathComponent("\(sandBoxPath)\(appString)/\(destFolder)\(zipExtension)")
        return documentPath
    }
    private func unzipFilePath(destFolder: String) -> URL {
        var documentPath = documentDirectoryPath()
        documentPath.appendPathComponent("\(StringConstants.Generic.sandBoxPath)\(appString)/\(destFolder)")
        return documentPath
    }
    private func deleteIfAlreadyExists(fileMngr: FileManager, destPath: String) {
        do {
            if fileMngr.fileExists(atPath: destPath) {
                try fileMngr.removeItem(atPath: destPath)
                print("IsFileExist: \(fileMngr.fileExists(atPath: destPath))")
            }
        } catch {
            print("Error during deleting file at existing path: \(error)")
        }
    }
    private func createDummyFileIfEmptyDirectory(srcPath: String) -> String {
        // It is a directory so check for empty content
        do {
            let directoryContents = try FileManager.default.contentsOfDirectory(atPath: srcPath)
            if directoryContents.isEmpty {
                // create a empty dummy file
                let url = URL(fileURLWithPath: srcPath).appendingPathComponent(StringConstants.Zip.apzDummy)
                try StringConstants.Generic.emptyString.write(to: url, atomically: true, encoding: .utf8)
                return url.path
            }
        } catch {
            print(error.localizedDescription)
            return srcPath
        }
        return srcPath
    }
}
// swiftlint:enable all
