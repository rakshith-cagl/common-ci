// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import QuickLook

class FileLocationController: UITableViewController, QLPreviewControllerDataSource,
                              QLPreviewControllerDelegate, UIDocumentInteractionControllerDelegate {
    var filesArray: [String] = []
    var dirPath: String = StringConstants.Generic.emptyString
    var openType: String = StringConstants.Generic.emptyString
    var selectedFile: String?
    var prevCellIndexPath: IndexPath?
    var documentURLs = [String]()
    var delegate: FileLocationDelegate?
    // MARK: Initialization
     override init(style: UITableView.Style) {
        super.init(style: style)
     }
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
     func initFiles(fileArray: [String], directoryPath: String, type: String) {
        filesArray = fileArray
        dirPath = directoryPath
        openType = (type == StringConstants.Generic.emptyString) ? StringConstants.Generic.no : type
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = StringConstants.Generic.filesHeading
        if openType == StringConstants.Generic.no {
            navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .done,
                                                                target: self,
                                                                action: #selector(doneButtonClicked))
        } else {
            navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .cancel,
                                                               target: self,
                                                               action: #selector(cancelButtonClicked))
        }
    }
    @objc func doneButtonClicked() {
        if let selectedFile = selectedFile {
            let finalPath = URL(fileURLWithPath: dirPath).appendingPathComponent(selectedFile).path
            dismiss(animated: true) { [weak self] in
                self?.delegate?.selectedFile(fileName: selectedFile,
                                             filePath: finalPath,
                                             interfaceOrienation: WindowUtility.getUiInterfaceOrientation())
            }
        }
        selectedFile = nil
    }
    @objc func cancelButtonClicked() {
        dismiss(animated: true) { [weak self] in
            self?.delegate?.cancelBrowser(WindowUtility.getUiInterfaceOrientation())
        }
    }
    // MARK: Tableview methods
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return filesArray.count
    }
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell = tableView.dequeueReusableCell(withIdentifier: StringConstants.Generic.cellIdentifier)
        if cell == nil {
            cell = UITableViewCell(style: .default, reuseIdentifier: StringConstants.Generic.cellIdentifier)
        }
        cell?.textLabel?.text = filesArray[indexPath.row]
        cell?.accessoryType = (openType == StringConstants.Generic.yes) ? .disclosureIndicator : .none
        return cell ?? UITableViewCell()
    }
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let prevCellIndexPath = prevCellIndexPath {
            let oldCell = tableView.cellForRow(at: prevCellIndexPath)
            if oldCell?.accessoryType == .checkmark {
                oldCell?.accessoryType = .none
            }
        }
        if openType == StringConstants.Generic.no {
            tableView.cellForRow(at: indexPath)?.accessoryType = .checkmark
            let filePath = dirPath + StringConstants.Generic.pathSeperator + filesArray[indexPath.row]
            var isDirectory: ObjCBool = false
            FileManager.default.fileExists(atPath: filePath, isDirectory: &isDirectory)
            if isDirectory.boolValue {
                showDirectory(selectedFilePath: filePath)
            } else {
                selectedFile = filesArray[indexPath.row]
            }
        } else {
            openFile(filesArray[indexPath.row], forRow: indexPath.row)
        }
        prevCellIndexPath = indexPath
    }
    // MARK: Helper methods
    func openFile(_ selectedFile: String, forRow row: Int) {
        let selFileExtension = StringConstants.Generic.dot + URL(fileURLWithPath: selectedFile).pathExtension
        let isImage: Bool = selFileExtension == StringConstants.FileExtensions.jpg ||
            selFileExtension == StringConstants.FileExtensions.png
        let isVideo: Bool = selFileExtension == StringConstants.FileExtensions.mp4 ||
        selFileExtension == StringConstants.FileExtensions.mpg  ||
        selFileExtension == StringConstants.FileExtensions.amr ||
        selFileExtension == StringConstants.FileExtensions.mpeg
        let filePath = dirPath + StringConstants.Generic.pathSeperator + selectedFile
        var isDirectory: ObjCBool = false
        FileManager.default.fileExists(atPath: filePath, isDirectory: &isDirectory)
        if isImage {
            showImage(selectedFilePath: filePath)
        } else if isVideo {
            showVideo(selectedFilePath: filePath)
        } else if isDirectory.boolValue {
            showDirectory(selectedFilePath: filePath)
        } else {
            showDocs(index: row)
        }
    }
    func showImage(selectedFilePath: String) {
        let imageViewController = ImageViewController.init(imagePath: selectedFilePath)
        navigationController?.pushViewController(imageViewController, animated: true)
    }
    func showDirectory(selectedFilePath: String) {
        DispatchQueue.main.async(execute: { [self] in
            dirPath = selectedFilePath
            do {
                try filesArray = FileManager.default.contentsOfDirectory(atPath: dirPath)
            } catch {
                APZLogger.log(logLvl: "E", message: "Error in content")
            }
            tableView.reloadData()
        })
    }
    func showVideo(selectedFilePath: String) {
        let videoViewController = VideoViewController.init(videoPath: selectedFilePath)
        navigationController?.pushViewController(videoViewController, animated: true)
    }
    func showDocs(index: Int) {
        documentURLs = []
        initDocumentsURL()
        let previewController = QLPreviewController()
        previewController.dataSource = self
        previewController.delegate = self
        previewController.currentPreviewItemIndex = index
        navigationController?.pushViewController(previewController, animated: true)
    }
    func initDocumentsURL() {
        for index in 0..<filesArray.count {
            let filePath = dirPath + StringConstants.Generic.pathSeperator + filesArray[index]
            documentURLs.append(filePath)
        }
    }
    // MARK: File view methods
    func numberOfPreviewItems(in controller: QLPreviewController) -> Int {
        documentURLs.count
    }
    func previewController(_ controller: QLPreviewController, previewItemAt index: Int) -> QLPreviewItem {
        let selectedIndexPath = tableView.indexPathForSelectedRow
        let fileURL = URL(fileURLWithPath: documentURLs[selectedIndexPath?.row ?? 0])
        return fileURL as QLPreviewItem
    }
    func previewControllerDidDismiss(_ controller: QLPreviewController) {
        controller.dismiss(animated: true, completion: nil)
    }
    // MARK: Other delegate methods
     func documentInteractionControllerViewControllerForPreview(
        _ controller: UIDocumentInteractionController) -> UIViewController {
        return self
    }
    override var shouldAutorotate: Bool {
        return true
    }
}
