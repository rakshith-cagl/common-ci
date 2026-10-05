// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

 class APZFileBrowser: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var fileBrowserOpenFile: String = StringConstants.Generic.emptyString
    var filterByExtensions: String = StringConstants.Generic.emptyString
    let fileManager = FileManager.default
    var videoGallery: VideoGalleryController?
    var imageGallery: ImageGalleryController?
    var musicGallery: MusicGalleryController?
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "FileBrowser--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        processFileBrowser(jsonDict: jsonDict)
    }

    fileprivate func processFileBrowser(jsonDict: [AnyHashable: Any]) {
        let fileCategory = jsonDict["fileCategory"] as? String ?? StringConstants.Generic.emptyString
        switch fileCategory {
        case StringConstants.FileBrowser.photoCategory:
            imageGallery = ImageGalleryController(plugin: webView, jsonDict)
            imageGallery?.imageDelegate = self
            imageGallery?.execute(jsonDict)
        case StringConstants.FileBrowser.audioCategory:
            musicGallery = MusicGalleryController(plugin: webView, jsonDict)
            musicGallery?.execute(jsonDict)
            musicGallery?.musicDelegate = self
        case StringConstants.FileBrowser.videoCategory:
            videoGallery = VideoGalleryController(plugin: webView, jsonDict)
            videoGallery?.execute(jsonDict)
            videoGallery?.videoDelegate = self
        case StringConstants.FileBrowser.defaultCategory, StringConstants.Generic.emptyString:
            if fileCategory == StringConstants.FileBrowser.defaultCategory {
                var finalDict = jsonDict
                finalDict["location"] = StringConstants.Generic.emptyString
                appBrowser(jsonDict: finalDict)
            } else {
                appBrowser(jsonDict: jsonDict)
            }
        default:
            fileBrowserError(errorCode: StringConstants.FileBrowser.invalidFileCategory,
                             errorMessage: "Invalid FileCategory")
        }
    }
    fileprivate func appBrowser(jsonDict: [AnyHashable: Any]) {
        let fileOpenType = jsonDict["openFile"] as? String ?? StringConstants.Generic.no
        let fileFilters = jsonDict["filter"] as? String ?? StringConstants.Generic.emptyString
        var fileLocation = jsonDict["location"] as? String ?? StringConstants.Generic.emptyString
        let fileLocArr = fileLocation.components(separatedBy: StringConstants.FileBrowser.locationSeperator)
        var isRootLocation = false
        for path in fileLocArr where path.hasPrefix(StringConstants.FileBrowser.locationiOS) {
                if path == StringConstants.FileBrowser.locationiOS {
                    isRootLocation = true
                } else {
                    fileLocation = (path as NSString).lastPathComponent
                    isRootLocation = false
                }
                break
            }
        if fileLocation.isEmpty || isRootLocation || fileLocation == StringConstants.FileBrowser.locationiOS {
            let appBrowserDict = getAllDirectory(filters: fileFilters)
            if !appBrowserDict.isEmpty {
                let browserController = AppBrowserController(style: .grouped)
                browserController.delegate = self
                browserController.initDirectories(appBrowserDict, openType: fileOpenType, filters: fileFilters)
                browserController.appID = self.viewController?.appString
                let navigationController = UINavigationController(rootViewController: browserController)
                navigationController.modalPresentationStyle = .fullScreen
                viewController?.present(navigationController, animated: true, completion: nil)
            } else {
                fileBrowserError(errorCode: StringConstants.FileBrowser.appFilesEmpty,
                                 errorMessage: "No Files Present in Sandbox")
            }
        } else {
            let documentsDirectory = appDocumentsDirectory().appending("/\(fileLocation)")
            var isDirectory: ObjCBool = false
            let isLocationPresent = fileManager.fileExists(atPath: documentsDirectory, isDirectory: &isDirectory)
            if isDirectory.boolValue && isLocationPresent {
                let locationFiles = getLocationFiles(location: documentsDirectory, filters: fileFilters)
                if !locationFiles.isEmpty {
                    let fileController = FileLocationController(style: .grouped)
                    fileController.delegate = self
                    fileController.initFiles(fileArray: locationFiles,
                                             directoryPath: documentsDirectory,
                                             type: fileOpenType)
                    let navigationController = UINavigationController(rootViewController: fileController)
                    navigationController.modalPresentationStyle = .fullScreen
                    viewController?.present(navigationController, animated: true, completion: nil)
                } else {
                    fileBrowserError(errorCode: StringConstants.FileBrowser.fileNotFound,
                                     errorMessage: String(format: "No Files Present in Location = %@", fileLocation))
                }
            } else {
                fileBrowserError(errorCode: StringConstants.FileBrowser.fileNotFound,
                                 errorMessage: String(format: "No Location %@ Present in Sandbox", fileLocation))
            }
        }
    }
     fileprivate func fileBrowserError(errorCode: String, errorMessage: String) {
         let resultkeys = [StringConstants.Generic.errorCode]
         let resultValues = [errorCode]
         let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                     status: false,
                                                                     keepAlive: false,
                                                                     responseKeys: resultkeys,
                                                                     responseValues: resultValues)
         MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                parameter: params)
         APZLogger.log(logLvl: "E", message: String(format: "FileBrowser--%@", errorMessage))
     }
    func cleanPlugin(interfaceOrientation: UIInterfaceOrientation) {
        self.delegate.donePluginWithOrientaion(self, interfaceOrientation)
    }
}

extension APZFileBrowser {
    fileprivate func appDocumentsDirectory() -> String {
        let appString = self.viewController?.appString ?? StringConstants.Generic.emptyString
        return FileManagerUtility.documentDirectory().appendingPathComponent("Assets/apps/\(appString)/").path
    }
    fileprivate func getAllDirectory(filters: String) -> [AnyHashable: Any] {
        let appdocumentsDirectory = appDocumentsDirectory()
        var finalFiles: [AnyHashable: Any] = [:]
        do {
            var appDirectoryContent = try fileManager.contentsOfDirectory(atPath: appdocumentsDirectory)
            let ignoreList = ["scripts", "screens", "plist", "sqlite", "styles", "sslCertificates"]
            for dir in ignoreList {
                if let index = appDirectoryContent.firstIndex(of: dir) {
                    appDirectoryContent.remove(at: index)
                }
            }
            for fileStr in appDirectoryContent {
                let path = appdocumentsDirectory.appending("/\(fileStr)")
                finalFiles[fileStr] = "createFile"
                var isDirectory: ObjCBool = false
                fileManager.fileExists(atPath: path, isDirectory: &isDirectory)
                if isDirectory.boolValue {
                    let subDirContent = try fileManager.contentsOfDirectory(atPath: path)
                    if !subDirContent.isEmpty {
                        let filteredFiles = getFilteredFiles(location: path, filters: filters)
                        if !filteredFiles.isEmpty {
                            finalFiles[fileStr] = filteredFiles
                        }
                    } else {
                        APZLogger.log(logLvl: "I",
                                      message: String(format: "FileBrowser--sub Directory is empty %@", fileStr))
                    }
                }
            }
        } catch let error {
            APZLogger.log(logLvl: "E", message: error.localizedDescription)
        }
        return finalFiles
}
    fileprivate func getLocationFiles(location: String, filters: String) -> [String] {
        var filteredFiles: [String] = []
        do {
            let directoryContent = try fileManager.contentsOfDirectory(atPath: location)
            if !directoryContent.isEmpty {
                filteredFiles = getFilteredFiles(location: location, filters: filters)
                if !filteredFiles.isEmpty {
                    APZLogger.log(logLvl: "I",
                                  message: String(format: "FileBrowser--Filtered Files in SubDirectory %@",
                                                  filteredFiles))
                } else {
                    APZLogger.log(logLvl: "I",
                                  message: String(format: "FileBrowser--No files in SubDirectory %@", filteredFiles))
                }
            } else {
                APZLogger.log(logLvl: "I", message: "SubDirectory  is empty")
            }
        } catch let error {
            APZLogger.log(logLvl: "E", message: error.localizedDescription)
        }
        return filteredFiles
    }

    fileprivate func getFilteredFiles(location: String, filters: String) -> [String] {
        var filesArr: [String] = []
        if !location.isEmpty {
            do {
                filesArr = try fileManager.contentsOfDirectory(atPath: location)
                if !filesArr.isEmpty && !filters.isEmpty {
                    let extensions = filters.components(separatedBy: ",")
                    var subpredicates: [NSPredicate] = []
                    for extn in extensions {
                        subpredicates.append(NSPredicate(format: "SELF ENDSWITH %@", extn))
                    }
                    let filter = NSCompoundPredicate(orPredicateWithSubpredicates: subpredicates)
                    filesArr = filesArr.filter({
                        filter.evaluate(with: $0)
                    })
                } else {
                    APZLogger.log(logLvl: "I", message: "No Extension to filter by extensions")
                }
            } catch let error {
                APZLogger.log(logLvl: "E",
                              message: String(format: "FileBrowser--An error happened = %@",
                                              error.localizedDescription))
            }
        }
        return filesArr
    }
}

extension APZFileBrowser: ImageGalleryDoneDelegate {
    func doneImageGallery(sender: Any) {
        APZLogger.log(logLvl: "D",
                      message: String(format: "FileBrowser Done--%@", String(describing: type(of: sender.self))))
    }

    func doneImageGalleryWithOrientation(orientation: UIInterfaceOrientation) {
        cleanPlugin(interfaceOrientation: orientation)
    }
}

extension APZFileBrowser: MusicGalleryDoneDelegate {
    func doneMusicGallery(_ sender: Any) {
        APZLogger.log(logLvl: "D",
                      message: String(format: "FileBrowser-Done--%@",
                                      String(describing: type(of: sender.self))))
    }
    func doneMusicGallery(with orientation: UIInterfaceOrientation) {
        cleanPlugin(interfaceOrientation: orientation)
    }
}
extension APZFileBrowser: VideoGalleryDoneDelegate {
    func doneVideoGallery(sender: Any?) {
        APZLogger.log(logLvl: "D",
                      message: String(format: "FileBrowser--Done-%@", String(describing: type(of: sender.self))))
    }
    func doneVideoGalleryWithOrientation(sender: Any?, orientation: UIInterfaceOrientation) {
        cleanPlugin(interfaceOrientation: orientation)
    }
}
extension APZFileBrowser: AppBrowserDelegate {
    func selectedFile(_ fileName: String, _ interfaceOrientation: UIInterfaceOrientation) {
        let resultkeys = [StringConstants.Generic.filePath]
        let resultValues = [fileName]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin(interfaceOrientation: interfaceOrientation)
    }
    func cancelBrowser(_ interfaceOrientation: UIInterfaceOrientation) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: false,
                                                                    responseKeys: [],
                                                                    responseValues: [])
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        APZLogger.log(logLvl: "E", message: "FileBrowser--Cancelled")
        cleanPlugin(interfaceOrientation: interfaceOrientation)
    }
}
extension APZFileBrowser: FileLocationDelegate {
    func selectedFile(fileName: String, filePath: String, interfaceOrienation: UIInterfaceOrientation) {
        let resultkeys = [StringConstants.Generic.filePath]
        let resultValues = [filePath]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: resultkeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin(interfaceOrientation: interfaceOrienation)
    }
}
