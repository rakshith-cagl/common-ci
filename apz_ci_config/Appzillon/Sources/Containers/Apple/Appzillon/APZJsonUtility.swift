// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZJsonUtility: NSObject {
    @objc static let shared = APZJsonUtility()
    private override init() {
        //Empty
    }
   @objc func createResponseJSONString(pluginId: String, status: Bool,
                                       keepAlive: Bool, responseKeys: [String], responseValues: [Any]) -> String {
            var pluginResponse = Dictionary(uniqueKeysWithValues: zip(responseKeys, responseValues))
            pluginResponse["id"] = pluginId
            pluginResponse["status"] = NSNumber(value: status)
            pluginResponse["keepAlive"] = NSNumber(value: keepAlive)
            if let data = try? JSONSerialization.data(withJSONObject: pluginResponse, options: .prettyPrinted),
            let jsonStr = String(data: data, encoding: .utf8) {
                return jsonStr
            }
            return StringConstants.Generic.emptyString
        }
}
