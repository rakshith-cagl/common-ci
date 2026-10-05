//
//  FileManagerUtility.swift
//  Appzillon
//
//  Created by Nidhi Agrawal on 29/10/21.
//

import Foundation

class FileManagerUtility: NSObject {
    @objc static func write(_ data: Data?, toFile filePath: URL) -> Bool {
        // Can't have two overloaded methods exposed to Obj-C with @objc tag
        var writeOperationSuccess = false
        do {
            if try data?.write(to: filePath, options: .completeFileProtection) != nil {
                writeOperationSuccess = true
            }
        } catch {
            print("File write failed")
        }
        return writeOperationSuccess
    }

    @objc static func documentDirectory() -> URL {
        let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        let documentsDirectoryUrl = urls[0]
        return documentsDirectoryUrl
    }
    static func checkKeyLength(keyStr: String?, encodingType: Int) -> String? {
        var finalKeyStr = keyStr
        var keyLen = keyStr?.count ?? 0
        if keyLen < encodingType {
            let nPadding = encodingType - keyLen
            for _ in 0..<nPadding where keyLen < encodingType {
                    finalKeyStr?.insert("$", at: keyStr!.endIndex)
                    keyLen += 1
                }
        } else if keyLen > encodingType {
            finalKeyStr = (finalKeyStr as NSString?)?.substring(to: encodingType)
        }
        return finalKeyStr
    }
}
