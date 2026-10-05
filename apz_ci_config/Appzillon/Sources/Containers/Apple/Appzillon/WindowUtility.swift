// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class WindowUtility: NSObject {
    static func getUiInterfaceOrientation() -> UIInterfaceOrientation {
        var interfaceOrientation: UIInterfaceOrientation = .unknown
        if let orientation = UIApplication.shared.windows.first?.windowScene?.interfaceOrientation {
            interfaceOrientation = orientation
        }
        return interfaceOrientation
    }
}
