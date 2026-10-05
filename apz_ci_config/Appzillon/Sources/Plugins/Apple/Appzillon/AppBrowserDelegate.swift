// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

protocol AppBrowserDelegate: AnyObject {
    func selectedFile(_ fileName: String, _ interfaceOrientation: UIInterfaceOrientation)
    func cancelBrowser(_ interfaceOrientation: UIInterfaceOrientation)
}
