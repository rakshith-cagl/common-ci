// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
protocol APZMapDelegate: AnyObject {
    func mapCancelled(_ interfaceOrientation: UIInterfaceOrientation)
    func getLocationCordinates(_ latitude: String, longitude: String,
                               with interfaceOrientation: UIInterfaceOrientation)
}
