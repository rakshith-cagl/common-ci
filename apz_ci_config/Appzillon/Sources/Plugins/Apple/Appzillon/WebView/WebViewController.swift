// Copyright (c) 2021 Appzillon. All rights reserved.

import UIKit
import WebKit

class WebViewController: UIViewController, WKNavigationDelegate {
    weak var delegate: APZWebviewDelegate?
    var wbView: WKWebView!
    var mainWeb: WKWebView!
    var activityIndicator: UIActivityIndicatorView!
    var backBarButtonItem: UIBarButtonItem = UIBarButtonItem()
    var urlString: String = StringConstants.Generic.emptyString
    var pluginId: String = StringConstants.Generic.emptyString
    var successResponseForUrl: AnyObject?
    var postData: [AnyHashable: Any] = [:]
    var cancelButtonRequired: String = StringConstants.Generic.emptyString
    override func viewDidLoad() {
        super.viewDidLoad()
        configureWebview()
        configureActivityIndicator()
        configureBarButtons()
        if postData.count > 0 {
            setUrlWithPostData(postData: postData)
        } else {
            setPlainUrl(urlString: self.urlString)
        }
    }
    // MARK: View Controller Private Methods
    private func configureWebview() {
        wbView = WKWebView(frame: CGRect(x: 0.0,
                                          y: 0.0,
                                          width: self.view.frame.size.width,
                                          height: self.view.frame.size.height))
        wbView.navigationDelegate = self
        wbView.contentMode = .scaleAspectFit
        self.view.addSubview(wbView)
    }
    private func configureActivityIndicator() {
        activityIndicator = UIActivityIndicatorView(style: .large)
        activityIndicator.style = .medium
        activityIndicator.autoresizingMask = [.flexibleBottomMargin, .flexibleTopMargin,
                                              .flexibleLeftMargin, .flexibleRightMargin]
        activityIndicator.center = view.center
        activityIndicator.hidesWhenStopped = true
        wbView.addSubview(activityIndicator)
    }
    private func configureBarButtons() {
        if (cancelButtonRequired == StringConstants.Generic.yes) ||
            (cancelButtonRequired == StringConstants.WebView.lowercaseY) {
            let cancelBarButtonItem: UIBarButtonItem = UIBarButtonItem(title: StringConstants.WebView.cancel,
                                                                       style: .plain,
                                                                       target: self,
                                                                       action: #selector(cancelTapped))
            navigationItem.rightBarButtonItems = [cancelBarButtonItem]
            navigationItem.leftBarButtonItems = [backBarButtonItem]
        }
    }
    private func setPlainUrl(urlString: String) {
        if let urlStr = urlString.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed),
           let targetURL = URL(string: urlStr) {
            wbView.load(URLRequest(url: targetURL))
        }
    }
    private func setUrlWithPostData(postData: [AnyHashable: Any]) {
        let keyValues: NSMutableArray = []
        let allKeys = postData.keys
        for key in allKeys {
            if let keyStr = key as? String {
                let value = postData[keyStr] as? String ?? StringConstants.Generic.emptyString
                let keyValueString = keyStr + StringConstants.WebView.equals + value
                keyValues.add(keyValueString)
            }
        }
        let paramsString = keyValues.componentsJoined(by: StringConstants.WebView.ampersand)
        let finalUrlString = self.urlString.appending(paramsString)
        if let targetURL = URL(string: finalUrlString) {
            let request = URLRequest(url: targetURL)
            wbView.load(request)
        }
    }
    // MARK: Obj-C Selecter Methods
    @objc func cancelTapped() {
        dismiss(animated: true) {
            self.delegate?.viewCancelled(interfaceOrientation: WindowUtility.getUiInterfaceOrientation())
        }
    }
    @objc private func goBack() {
        wbView.goBack()
    }
    // MARK: Callback Methods
    private func callBack(navigationAction: WKNavigationAction) {
        let resultKeys = [StringConstants.Generic.webViewUrl]
        let resultValues = [navigationAction.request.url?.absoluteString ?? StringConstants.Generic.emptyString]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: true,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: self.mainWeb,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
    // MARK: WKWebview Delegate Methods
    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        activityIndicator.startAnimating()
    }
    func webView(_ webView: WKWebView,
                 decidePolicyFor navigationAction: WKNavigationAction,
                 decisionHandler: (WKNavigationActionPolicy) -> Void) {
        let requestUrlArray = navigationAction.request.url?.absoluteString.components(separatedBy:
                              StringConstants.WebView.questionMark)
        if let responseString = self.successResponseForUrl as? String {
            let urlToTrackArray = responseString.components(separatedBy: StringConstants.WebView.questionMark)
            if let requestUrl = requestUrlArray?.first,
               let urlToTrack = urlToTrackArray.first,
               requestUrl.contains(urlToTrack) {
                callBack(navigationAction: navigationAction)
            }
        }
        if let responseArray = self.successResponseForUrl as? [String] {
            for successUrl in responseArray {
                let urlToTrackArray = successUrl.components(separatedBy: StringConstants.WebView.questionMark)
                if let requestUrl = requestUrlArray?.first,
                   let urlToTrack = urlToTrackArray.first,
                   requestUrl.contains(urlToTrack) {
                    callBack(navigationAction: navigationAction)
                }
            }
        }
        decisionHandler(.allow)
    }
    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        activityIndicator.stopAnimating()
        if wbView.canGoBack {
            backBarButtonItem.isEnabled = true
            backBarButtonItem = UIBarButtonItem(title: StringConstants.Generic.back,
                                                style: .plain, target: self,
                                                action: #selector(goBack))
            navigationItem.backBarButtonItem = backBarButtonItem
            navigationItem.leftBarButtonItems = [backBarButtonItem]
        } else {
            backBarButtonItem.isEnabled = false
            backBarButtonItem.tintColor = nil
            backBarButtonItem.style = .plain
            backBarButtonItem.title = StringConstants.Generic.emptyString
        }
    }
    // MARK: View Controller Delegate Methods
    override func willAnimateRotation(to toInterfaceOrientation: UIInterfaceOrientation, duration: TimeInterval) {
        wbView.frame = CGRect(x: 0, y: 0, width: view.frame.size.width, height: view.frame.size.height)
        wbView.setNeedsDisplay()
}
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        wbView.frame = CGRect(x: 0, y: 0, width: size.width, height: size.height)
        wbView.backgroundColor = .white
        wbView.setNeedsDisplay()
    }
}
