// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

protocol FileLocationDelegate: AnyObject {
    func selectedFile(fileName: String, filePath: String, interfaceOrienation: UIInterfaceOrientation)
    func cancelBrowser(_ interfaceOrientation: UIInterfaceOrientation)
}
