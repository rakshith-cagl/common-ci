// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit

protocol APZGestureRecognizable {
    func createGestureObject(type: GestureType, selector: Selector?) -> UIGestureRecognizer
}
