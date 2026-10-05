// Copyright (c) 2021 Appzillon. All rights reserved.
// swiftlint:disable all
import Foundation
class MiscellaneousMethod: NSObject {
    @objc static let shared = MiscellaneousMethod()
    private override init() {
        //do nothing
    }
     func getItemsImage(item: String) -> String {
         var fileExtension = URL(fileURLWithPath: item).pathExtension.lowercased()
        if fileExtension.count == 0 {
            fileExtension = "folder"
        } else {
            if fileExtension.caseInsensitiveCompare("pdf") == .orderedSame {
                fileExtension = "pdf"
            } else if ["txt", "text"].contains(fileExtension) {
                fileExtension = "text"
            } else if ["doc", "docx"].contains(fileExtension) {
                fileExtension = "doc"
            } else if ["ppt", "pptx", "pptm"].contains(fileExtension) {
                fileExtension = "ppt"
            } else if ["war", "jar", "zip"].contains(fileExtension) {
                fileExtension = "archive"
            } else if ["html", "htm"].contains(fileExtension) {
                fileExtension = "html"
            } else if fileExtension.caseInsensitiveCompare("apk") == .orderedSame {
                fileExtension = "apk"
            } else if ["xls", "xlsx"].contains(fileExtension) {
                fileExtension = "xlsx"
            } else if ["png", "jpeg", "jpg"].contains(fileExtension) {
                fileExtension = "image"
            } else if fileExtension.caseInsensitiveCompare("mp3") == .orderedSame {
                fileExtension = "audio"
            } else if fileExtension.caseInsensitiveCompare("mp4") == .orderedSame {
                fileExtension = "video"
            } else if fileExtension.caseInsensitiveCompare("xml") == .orderedSame {
                fileExtension = "xml"
            } else {
                fileExtension = "unknownfile"
            }
        }
        return fileExtension
    }
     func getAppHasLaunchedBefore(mainAppId: String) -> Bool {
        let allKeysInDefaultLocation = UserDefaults.standard.dictionaryRepresentation().keys
        var launchedBefore = false
        if allKeysInDefaultLocation.contains("\(mainAppId).HasLaunchedOnce") {
            launchedBefore = ((UserDefaults.standard.object(forKey: "\(mainAppId).HasLaunchedOnce")) != nil)
        } else if allKeysInDefaultLocation.contains("HasLaunchedOnce") {
            launchedBefore = (UserDefaults.standard.object(forKey: "HasLaunchedOnce") != nil)
        } else {
            return false
        }
        return launchedBefore
    }

     func getAppHasLaunchedOnOlderVersion() -> Bool {
        return UserDefaults.standard.dictionaryRepresentation().keys.contains("HasLaunchedOnce")
    }

     func setAppHasLaunchedBefore(mainAppId: String) {
        let launchedOnOlderVersion = UserDefaults.standard.dictionaryRepresentation().keys.contains("HasLaunchedOnce")
        if launchedOnOlderVersion {
            UserDefaults.standard.removeObject(forKey: "HasLaunchedOnce")
        }
        UserDefaults.standard.set(true, forKey: "\(mainAppId).HasLaunchedOnce")
    }

    // MARK: JS CALLBACK FUNCTION
    @objc func jsLayerCall(webView: WKWebView, jsFunctionName: String, parameter param: String) {
        DispatchQueue.main.async {
            webView.evaluateJavaScript(String(format: "%@(%@)", jsFunctionName, param), completionHandler: nil)
        }
    }

    // MARK: Bundle related function
    @objc func getAppBundle() -> Bundle {
        return Bundle.main
    }
//
    // MARK: Get Main ViewController function
    @objc func getAppzillonViewController() -> AppzillonViewController? {
        let appDelegate = UIApplication.shared.delegate as? AppzillonAppDelegate
        return appDelegate?.viewController
    }
}
// swiftlint:enable all
