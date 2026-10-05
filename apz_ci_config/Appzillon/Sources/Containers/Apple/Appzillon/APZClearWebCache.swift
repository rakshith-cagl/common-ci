// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
class APZClearWebCache: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    public override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init()
    }
    open override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            URLCache.shared.removeAllCachedResponses()
            self.removeCache()
            self.removeWebviewCache()
            } else {
            }
    }
func removeCache() {
    let cacheURL =  FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)
    let documentsDirectoryPath = cacheURL[0]
    let fileManager = FileManager.default
    do {
        let directoryContents = try FileManager.default.contentsOfDirectory(at: documentsDirectoryPath,
                                                                            includingPropertiesForKeys: nil,
                                                                            options: [])
        for file in directoryContents {
            do {
                try fileManager.removeItem(at: file)
            } catch let error as NSError {
                debugPrint("Ooops! Something went wrong: \(error)")
            }
        }
    } catch let error as NSError {
        print(error.localizedDescription)
    }
}
func removeWebviewCache() {
    let mySet = Set<String>()
      let websiteDataTypes = NSSet(array: [WKWebsiteDataTypeDiskCache,
                                           WKWebsiteDataTypeOfflineWebApplicationCache,
                                           WKWebsiteDataTypeMemoryCache,
                                           WKWebsiteDataTypeLocalStorage,
                                           WKWebsiteDataTypeCookies,
                                           WKWebsiteDataTypeSessionStorage,
                                           WKWebsiteDataTypeFetchCache,
                                           WKWebsiteDataTypeServiceWorkerRegistrations])
      let date = NSDate(timeIntervalSince1970: 0)
        WKWebsiteDataStore.default().removeData(ofTypes: websiteDataTypes as? Set<String> ?? mySet,
                                                modifiedSince: date as Date,
                                                completionHandler: { print("Deleted memory Dump")})
}
}
