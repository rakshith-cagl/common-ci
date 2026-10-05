// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit

@objc protocol MusicGalleryDoneDelegate {
    func doneMusicGallery(_ sender: Any)
    func doneMusicGallery(with orientation: UIInterfaceOrientation)
}
