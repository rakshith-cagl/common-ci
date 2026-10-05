// Copyright (c) 2021 Appzillon. All rights reserved.
import Foundation
import MobileCoreServices
import UniformTypeIdentifiers
class APZAccessDeviceFiles: APZPlugin, UIDocumentPickerDelegate {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "I", message: "APZAccessDeviceFiles--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        importDeviceFiles()
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZAccessDeviceFiles--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Utility Methods
    private func importDeviceFiles() {
        var documentPicker: UIDocumentPickerViewController
        if #available(iOS 14.0, *) {
            let supportedTypes: [UTType] = [UTType.data]
            documentPicker = UIDocumentPickerViewController(forOpeningContentTypes: supportedTypes, asCopy: true)
        } else {
            documentPicker = UIDocumentPickerViewController(documentTypes: ["public.item"],
                                                            in: UIDocumentPickerMode.import)
        }
        documentPicker.delegate = self
        documentPicker.modalPresentationStyle = .fullScreen
        documentPicker.allowsMultipleSelection = false
        self.viewController?.present(documentPicker, animated: true, completion: nil)
    }
    // MARK: UIDocumentPickerDelegate Methods
    func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
        callBack(resultKey: [StringConstants.Generic.errorMessage], resultValue: ["User Cancelled"], status: false)
    }
    func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
        guard let url = urls.first else {return}
        let fileUrl = FileManagerUtility.documentDirectory().appendingPathComponent(url.lastPathComponent)
        if !fileUrl.hasDirectoryPath {
            do {
                try FileManager.default.copyItem(at: url, to: fileUrl)
                callBack(resultKey: [StringConstants.Generic.filePath],
                         resultValue: [fileUrl.path], status: true)
            } catch {
                callBack(resultKey: [StringConstants.Generic.filePath],
                         resultValue: [fileUrl.path], status: true)
            }
        }
    }
    // MARK: Callback Method
    fileprivate func callBack(resultKey: [String], resultValue: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKey,
                                                                    responseValues: resultValue)
        MiscellaneousMethod.shared.jsLayerCall(webView: self.webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
        cleanPlugin()
    }
}
