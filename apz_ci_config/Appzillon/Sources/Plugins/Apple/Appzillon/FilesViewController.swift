// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import QuickLook

class FilesViewController: UITableViewController, QLPreviewControllerDataSource, QLPreviewControllerDelegate {
    var filesArray: [String] = []
    var dirPath: String = StringConstants.Generic.emptyString
    var dirPathUrl = URL(fileURLWithPath: StringConstants.Generic.emptyString)
    var fileOpenType: String  = StringConstants.Generic.emptyString
    var sandboxPath: String  = StringConstants.Generic.emptyString
    var filters: String = StringConstants.Generic.emptyString
    var selectedFile: String?
    var prevCellIndexPath: IndexPath?
    var documentURLs = [String]()
    var delegate: FilesViewDelegate?
    // MARK: Initialization
     override init(style: UITableView.Style) {
        super.init(style: style)
    }
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
     func initFiles(fileArray: [String], directoryPath: String, openType: String, fileFilters: String, isCreate: Bool) {
        filesArray = fileArray
        dirPath = directoryPath
        dirPathUrl = URL(fileURLWithPath: directoryPath)
        sandboxPath = dirPathUrl.deletingLastPathComponent().path
        filters = fileFilters
        fileOpenType = (openType == StringConstants.Generic.emptyString) ? StringConstants.Generic.no : openType
        if isCreate {
            delegate?.selectedFile(fileName: sandboxPath, filePath: dirPath, isCreate: isCreate)
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = StringConstants.Generic.filesHeading
        if fileOpenType == StringConstants.Generic.no {
            navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .done,
                                                                target: self,
                                                                action: #selector(doneButtonClicked))
        }
        let backButton = UIBarButtonItem(title: StringConstants.Generic.back,
                                         style: .plain, target: self,
                                         action: #selector(backButtonClicked))
        navigationItem.leftBarButtonItem = backButton
    }
    override var shouldAutorotate: Bool {
        return true
    }
    @objc func doneButtonClicked() {
        var selectedElement: String
        if var selectedFile = selectedFile {
            selectedElement = (!selectedFile.isEmpty) ? dirPathUrl.appendingPathComponent(selectedFile).path : dirPath
            var isDirectory: ObjCBool = false
            FileManager.default.fileExists(atPath: selectedElement, isDirectory: &isDirectory)
            if isDirectory.boolValue {
                selectedFile = dirPathUrl.lastPathComponent
            } else {
                dirPath = dirPathUrl.appendingPathComponent(selectedFile).path
            }
            delegate?.selectedFile(fileName: selectedFile, filePath: dirPath, isCreate: false)
            navigationController?.popViewController(animated: true)
            self.selectedFile = nil
        }
    }
    @objc func backButtonClicked() {
        dirPath = dirPathUrl.deletingLastPathComponent().path
        if dirPath == sandboxPath {
            navigationController?.popViewController(animated: true)
        } else {
            DispatchQueue.main.async(execute: { [self] in
                filesArray = getAllDirectoryPlusFiles()
                tableView.reloadData()
            })
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
            let imgView = UIImageView(frame: CGRect(x: 0, y: 0, width: 48, height: 48))
            imgView.tag = 1
            cell?.contentView.addSubview(imgView)
            let label = UILabel(frame: CGRect(x: 50, y: 0, width: view.frame.size.width - 50, height: 50))
            label.backgroundColor = UIColor.clear
            label.tag = 2
            cell?.contentView.addSubview(label)
        }
        let lbl = cell?.contentView.viewWithTag(2) as? UILabel
        lbl?.text = filesArray[indexPath.row]
        let imgView = cell?.contentView.viewWithTag(1) as? UIImageView
        let imagePath = StringConstants.FilesViewController.fileBrowserIcons +
            StringConstants.Generic.pathSeperator +
            MiscellaneousMethod.shared.getItemsImage(item: (filesArray[indexPath.row]))
        imgView?.image = UIImage(named: imagePath)
        cell?.accessoryType = (fileOpenType == StringConstants.Generic.yes) ? .disclosureIndicator : .none
        return cell ?? UITableViewCell()
    }
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let prevCellIndexPath = prevCellIndexPath {
            let oldCell = tableView.cellForRow(at: prevCellIndexPath)
            if oldCell?.accessoryType == .checkmark {
                oldCell?.accessoryType = .none
            }
        }
        selectedFile = filesArray[indexPath.row]
        if fileOpenType == StringConstants.Generic.no {
            let filePath = dirPath + StringConstants.Generic.pathSeperator + filesArray[indexPath.row]
            var isDirectory: ObjCBool = false
            FileManager.default.fileExists(atPath: filePath, isDirectory: &isDirectory)
            if isDirectory.boolValue {
                showDirectory(selectedFilePath: filePath)
            } else {
                tableView.cellForRow(at: indexPath)?.accessoryType = .checkmark
            }
        } else {
            openFile(filesArray[indexPath.row], forRow: indexPath.row)
        }
        prevCellIndexPath = indexPath
    }
    // MARK: Helper methods
    func openFile(_ selectedFile: String, forRow row: Int) {
        let selFileExtension = StringConstants.Generic.dot + URL(fileURLWithPath: selectedFile).pathExtension
        let isImage: Bool = selFileExtension ==
            StringConstants.FileExtensions.jpg ||
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
    func showDirectory(selectedFilePath: String) {
        DispatchQueue.main.async(execute: { [self] in
            dirPath = selectedFilePath
            filesArray = getAllDirectoryPlusFiles()
            selectedFile = nil
            tableView.reloadData()
        })
    }
    func getAllDirectoryPlusFiles() -> [String] {
        var listOfFilesAndDirectory: [String] = []
        do {
            listOfFilesAndDirectory = try FileManager.default.contentsOfDirectory(atPath: dirPath)
            if listOfFilesAndDirectory.count > 0 && !filters.isEmpty {
                let extensions = filters.components(separatedBy: StringConstants.Generic.comma)
                var subpredicates: [NSPredicate] = []
                for extn in extensions {
                    subpredicates.append(NSPredicate(format: StringConstants.FilesViewController.filterPredicate, extn))
                }
                let filter = NSCompoundPredicate(orPredicateWithSubpredicates: subpredicates)
                listOfFilesAndDirectory = listOfFilesAndDirectory.filter {filter.evaluate(with: $0)}
            } else {
                APZLogger.log(logLvl: "I", message: "FileViewController-No Extension to filter by extensions")
            }
        } catch {
            APZLogger.log(logLvl: "E", message: "Error in content")
        }
        return listOfFilesAndDirectory
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
}
