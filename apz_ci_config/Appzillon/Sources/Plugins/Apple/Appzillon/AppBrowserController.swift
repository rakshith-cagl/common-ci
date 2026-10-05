// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit

class AppBrowserController: UITableViewController, FilesViewDelegate {
    var fileOpenType: String  = StringConstants.Generic.emptyString
    var filters: String = StringConstants.Generic.emptyString
    var directories: [AnyHashable: Any] = [:]
    var selectedFile: String = StringConstants.Generic.emptyString
    var selectedFilePath: String = StringConstants.Generic.emptyString
    var delegate: AppBrowserDelegate?
    var appID: String?
    override init(style: UITableView.Style) {
        super.init(style: style)
    }
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    func initDirectories(_ fileDirectories: [AnyHashable: Any], openType: String, filters: String) {
        directories = fileDirectories
        fileOpenType = openType
        self.filters = filters
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = StringConstants.AppBrowser.title
        navigationItem.leftBarButtonItem = UIBarButtonItem(barButtonSystemItem: .cancel,
                                                           target: self, action: #selector(cancel))
        selectedFile = StringConstants.Generic.hyphen
        if fileOpenType == StringConstants.Generic.no {
            navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .done,
                                                                target: self, action: #selector(done))
        }
    }
    override var shouldAutorotate: Bool {
        return true
    }
    @objc func cancel() {
        self.dismiss(animated: true) {
            self.delegate?.cancelBrowser(WindowUtility.getUiInterfaceOrientation())
        }
    }
    @objc func done() {
        self.dismiss(animated: true) {
            self.delegate?.selectedFile(self.selectedFilePath, WindowUtility.getUiInterfaceOrientation())
        }
    }
    override func numberOfSections(in tableView: UITableView) -> Int {
        return fileOpenType == StringConstants.Generic.no ? 2 : 1
    }
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if fileOpenType == StringConstants.Generic.no {
            return section == 0 ? 1 : directories.count
        } else {
            return directories.count
        }
    }
    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if fileOpenType == StringConstants.Generic.no {
            return section == 0 ? StringConstants.AppBrowser.selectedFile : StringConstants.AppBrowser.directories
        } else {
            return StringConstants.AppBrowser.directories
        }
    }
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let selectedDir = Array(directories.keys)[indexPath.row]
        if let filesArray = directories[selectedDir] as? [String], let appID = appID {
            let finalDir = FileManagerUtility.documentDirectory()
                .appendingPathComponent("\(StringConstants.Generic.sandBoxPath)\(appID)/\(selectedDir)/").path
            let filesViewController = FilesViewController(style: .grouped)
            filesViewController.delegate = self
            if filesArray.first  == StringConstants.AppBrowser.createFile {
                filesViewController.initFiles(fileArray: filesArray,
                                              directoryPath: finalDir,
                                              openType: fileOpenType,
                                              fileFilters: filters, isCreate: true)
            } else {
                filesViewController.initFiles(fileArray: filesArray,
                                              directoryPath: finalDir,
                                              openType: fileOpenType,
                                              fileFilters: filters, isCreate: false)
            }
            self.navigationController?.pushViewController(filesViewController, animated: true)
        }
    }
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell: UITableViewCell?
        var cellText: String
        if fileOpenType == StringConstants.Generic.no && indexPath.section == 0 {
            cell = tableView.dequeueReusableCell(withIdentifier: StringConstants.AppBrowser.selectedIdentifier)
            if cell == nil {
                cell = UITableViewCell(style: .default, reuseIdentifier: StringConstants.AppBrowser.selectedIdentifier)
            }
            cellText = selectedFile
        } else {
            cell = tableView.dequeueReusableCell(withIdentifier: StringConstants.Generic.cellIdentifier)
            if cell == nil {
                cell = UITableViewCell(style: .default, reuseIdentifier: StringConstants.Generic.cellIdentifier)
            }
            cellText = Array(directories.keys)[indexPath.row] as? String ?? StringConstants.Generic.emptyString
        }
        let imgView = UIImageView(frame: CGRect(x: 0, y: 0, width: 48, height: 48))
        imgView.tag = 1
        cell?.contentView.addSubview(imgView)
        let label = UILabel(frame: CGRect(x: 50, y: 0, width: view.frame.size.width - 50, height: 50))
        label.backgroundColor = UIColor.clear
        label.tag = 2
        cell?.contentView.addSubview(label)
        label.text = cellText
        let imagePath = StringConstants.FilesViewController.fileBrowserIcons
            +  StringConstants.Generic.pathSeperator +  MiscellaneousMethod.shared.getItemsImage(item: cellText)
        imgView.image = UIImage(named: imagePath)
        return cell ?? UITableViewCell()
    }
    func selectedFile(fileName: String, filePath: String, isCreate: Bool) {
        selectedFile = fileName
        selectedFilePath = filePath
        if isCreate {
            done()
        }
    }
}
