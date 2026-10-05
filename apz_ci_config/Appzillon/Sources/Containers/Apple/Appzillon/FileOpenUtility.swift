// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import QuickLook

protocol FileOpenDelegate: AnyObject {
    func cancelDocViewerCallback()
    func docViewerCallback(resultKeys: [String], resultValues: [Any], status: Bool)
}

class FileOpenUtility: NSObject, QLPreviewControllerDataSource,
                        QLPreviewControllerDelegate, UINavigationControllerDelegate {
    var filePathUrl: URL!
    var delegate: FileOpenDelegate?
    // MARK: File View Method
    func viewDocument(viewController: AppzillonViewController?,
                      presentationStyle: UIModalPresentationStyle = .formSheet) {
        APZLogger.log(logLvl: "I", message: "Opening Document")
        let previewController = QLPreviewController()
        previewController.dataSource = self
        previewController.delegate = self
        previewController.currentPreviewItemIndex = 0
        let infoButton = UIBarButtonItem(barButtonSystemItem: .cancel,
                                         target: self,
                                         action: #selector(self.cancelDocViewer))
        previewController.navigationItem.leftBarButtonItem = infoButton
        if QLPreviewController.canPreview(URL(fileURLWithPath: filePathUrl.path) as QLPreviewItem) {
            let navigationController = UINavigationController(rootViewController: previewController)
            navigationController.modalPresentationStyle = presentationStyle
            navigationController.delegate = self
            viewController?.present(navigationController, animated: true)
        } else {
            self.delegate?.docViewerCallback(resultKeys: [StringConstants.Generic.errorCode],
                                             resultValues: [StringConstants.FileOperation.invalidFileFormat],
                                             status: false)
        }
    }
    // MARK: Delegate Methods
    func numberOfPreviewItems(in controller: QLPreviewController) -> Int {
        return 1
    }
    func previewController(_ controller: QLPreviewController, previewItemAt index: Int) -> QLPreviewItem {
        return filePathUrl as QLPreviewItem
    }
    func previewControllerDidDismiss(_ controller: QLPreviewController) {
        controller.dismiss(animated: true) {
            self.delegate?.docViewerCallback(resultKeys: [StringConstants.Generic.message],
                                             resultValues: [StringConstants.FileOperation.documentDismiss],
                                             status: false)
            APZLogger.log(logLvl: "I", message: "Preview dismiss")
        }
    }
    @objc func cancelDocViewer() {
        self.delegate?.cancelDocViewerCallback()
    }
}
