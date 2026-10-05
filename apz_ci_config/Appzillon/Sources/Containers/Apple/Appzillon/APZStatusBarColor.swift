// Copyright (c) 2021 Appzillon. All rights reserved.
import Foundation

class APZStatusBarColor: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "I", message: "APZStatusBarColor--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        if let action = jsonDict["action"] as? String, action == "STATUS_BAR_COLOR_GRADIENT" {
            setStatusBarGradientColor(jsonDict)
        } else {
            setStatusBarColour(jsonDict)
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZStatusBarColor--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: StatusBar Color Change Methods
    private func setStatusBarColour(_ jsonDict: [AnyHashable: Any]) {
        if let colorCode = jsonDict["color"] as? String {
            if #available(iOS 13.0, *) {
                let statusBar = UIView(frame: UIApplication.shared.windows.filter {$0.isKeyWindow}.first?
                    .windowScene?.statusBarManager?.statusBarFrame ?? CGRect.zero)
                statusBar.backgroundColor = UIColor.colorFromHex(hex: colorCode)
                UIApplication.shared.windows.filter {$0.isKeyWindow}.first?.addSubview(statusBar)
            } else {
                let statusBar = UIApplication.shared.value(forKeyPath: "statusBarWindow.statusBar") as? UIView
                statusBar?.backgroundColor = UIColor.colorFromHex(hex: colorCode)
            }
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys:
                                                                            [StringConstants.Generic.text],
                                                                        responseValues:
                                                                            ["StatusBar color change success"])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            cleanPlugin()
        } else {
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys:
                                                                            [StringConstants.Generic.errorMessage],
                                                                        responseValues:
                                                                            ["StatusBar color change failed"])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)

            cleanPlugin()
        }
    }
    private func setStatusBarGradientColor(_ jsonDict: [AnyHashable: Any]) {
        if let startColor = jsonDict["startColor"] as? String, let endColor = jsonDict["endColor"] as? String,
           let direction = jsonDict["direction"] as? String {
            if #available(iOS 13.0, *) {
                let statusBar = UIView(frame: UIApplication.shared.windows.filter {$0.isKeyWindow}.first?
                    .windowScene?.statusBarManager?.statusBarFrame ?? CGRect.zero)
                let gradient = self.addGradientLayer(startColor: startColor,
                                                     endColor: endColor,
                                                     view: statusBar,
                                                     direction: direction)
                statusBar.layer.addSublayer(gradient)
                UIApplication.shared.windows.filter {$0.isKeyWindow}.first?.addSubview(statusBar)
            } else {
                let statusBar = UIView(frame: UIApplication.shared.windows.filter {$0.isKeyWindow}.first?
                    .windowScene?.statusBarManager?.statusBarFrame ?? CGRect.zero)
                let gradient = self.addGradientLayer(startColor: startColor,
                                                     endColor: endColor,
                                                     view: statusBar,
                                                     direction: direction)
                statusBar.layer.addSublayer(gradient)
            }
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: true,
                                                                        keepAlive: false,
                                                                        responseKeys: [StringConstants.Generic.text],
                                                                        responseValues:
                                                                            ["StatusBar Gradient change success"])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            cleanPlugin()
        } else {
            let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                        status: false,
                                                                        keepAlive: false,
                                                                        responseKeys:
                                                                            [StringConstants.Generic.errorMessage],
                                                                        responseValues:
                                                                            ["StatusBar Gradient change failed"])
            MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                                   jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                                   parameter: params)
            cleanPlugin()
        }
    }
    private func addGradientLayer(startColor: String,
                                  endColor: String,
                                  view: UIView,
                                  direction: String) -> CAGradientLayer {
        let gradient = CAGradientLayer()
        gradient.frame = view.bounds
        
        if direction == "toRight" {
            gradient.startPoint = CGPoint(x: 0, y: 0.5)
            gradient.endPoint = CGPoint(x: 1, y: 0.5)
        } else {
            gradient.startPoint = CGPoint(x: 0, y: 0)
            gradient.endPoint = CGPoint(x: 0, y: 1)
        }
        gradient.colors = [UIColor.colorFromHex(hex: startColor).cgColor, UIColor.colorFromHex(hex: endColor).cgColor]
        return gradient
    }
}
