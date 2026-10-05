// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import StoreKit
class APZAppstoreReview: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "I", message: "APZAppstoreReview--Execute")
        SKStoreReviewController.requestReview()
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZAppstoreReview--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
}
