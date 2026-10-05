// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class Base64: NSObject {
    @objc static func encode(_ rawBytes: Data) -> String {
        let base64Encoded = rawBytes.base64EncodedString(options: Data.Base64EncodingOptions(rawValue: 0 ))
        return base64Encoded
    }
    @objc static func decode(_ string: String) -> Data {
        return Data(base64Encoded: string, options: Data.Base64DecodingOptions(rawValue: 0))!
    }
}
