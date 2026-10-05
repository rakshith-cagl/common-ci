// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

protocol ImageGalleryDoneDelegate: AnyObject {
    func doneImageGallery(sender: Any)
    func doneImageGalleryWithOrientation(orientation: UIInterfaceOrientation)
}
