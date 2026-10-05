// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZUserDefaults: NSObject {
     static func set(_ value: Any?, forKey key: String) {
        UserDefaults.standard.set(value, forKey: key)
    }
      static func setBool(_ value: Bool, forKey key: String) {
        UserDefaults.standard.set(value, forKey: key)
    }
     static func get(_ key: String) -> Any? {
        return UserDefaults.standard.value(forKey: key)
    }
      static func getBool(_ key: String) -> Bool {
        return UserDefaults.standard.bool(forKey: key)
    }
}
