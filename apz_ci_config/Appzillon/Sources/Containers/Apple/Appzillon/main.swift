//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit

autoreleasepool {
    //comment below line to enable debugging
    disable_debugging()
    UIApplicationMain(
        CommandLine.argc,
        UnsafeMutableRawPointer(CommandLine.unsafeArgv)
            .bindMemory(
                to: UnsafeMutablePointer<Int8>.self,
                capacity: Int(CommandLine.argc)),
        nil,
        NSStringFromClass(AppzillonAppDelegate.self)
    )
}
