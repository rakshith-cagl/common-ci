//Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation


class NativeService: NSObject {
    var webView: WKWebView?
    var viewController: AppzillonViewController?
    
    func execute(jsonDict: [AnyHashable : Any], webView: WKWebView) {
        self.webView = webView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
    }
    
}
