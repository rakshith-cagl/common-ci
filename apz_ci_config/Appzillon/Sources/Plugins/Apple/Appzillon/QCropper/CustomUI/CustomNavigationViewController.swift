//Copyright (c) 2021 Appzillon. All rights reserved.

import UIKit

class CustomNavigationViewController: UINavigationController {

    var orientation: String?
    var shouldAutoRotate = false
    
    override var shouldAutorotate: Bool {
        return shouldAutoRotate
    }
    override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        if orientation == "PORTRAIT" {
            return [.portrait, .portraitUpsideDown]
        } else if orientation == "LANDSCAPE" {
            return .landscape
        } else {
            return .all
        }
    }
}
