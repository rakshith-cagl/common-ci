// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

protocol FilesViewDelegate: AnyObject {
    func selectedFile(fileName: String, filePath: String, isCreate: Bool)
}
