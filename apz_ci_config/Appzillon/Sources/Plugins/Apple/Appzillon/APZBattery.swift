// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZBattery: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var batteryState: String = StringConstants.Generic.emptyString
    var batteryLevel: String = StringConstants.Generic.emptyString
    var batteryStateValue: String = StringConstants.Generic.emptyString
    var batteryLevelValue: String = StringConstants.Generic.emptyString
    var batteryThersholdValue: String = StringConstants.Generic.emptyString
    var batteryTimer: Timer?
    var batteryAction: String = StringConstants.Generic.emptyString
    var batteryTime = StringConstants.Generic.emptyString
    var batteryTimerValue: Int = 0
    // MARK: Plugin Life Cycle Method
    override init(plugin webView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = webView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "APZBattery--Execute")
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        batteryAction = jsonDict[StringConstants.Generic.actionType] as? String ?? StringConstants.Generic.emptyString
        self.batteryAction = batteryAction.lowercased()
        batteryState = jsonDict["state"] as? String ?? StringConstants.Generic.emptyString
        batteryLevel = jsonDict["level"] as? String ?? StringConstants.Generic.emptyString
        batteryTime = jsonDict["time"] as? String ?? StringConstants.Generic.emptyString
        batteryTimerValue = Int(batteryTime) ?? 0
        batteryThersholdValue = jsonDict["threshold"] as? String ?? StringConstants.Generic.emptyString
        if batteryAction == "start" {
            UIDevice.current.isBatteryMonitoringEnabled = true
            if UIDevice.current.batteryState == UIDevice.BatteryState.charging {
                batteryStateValue = "plugged"
            } else {
                batteryStateValue = "unplugged"
            }
            let batteryLev = UIDevice.current.batteryLevel
            if batteryLev > 0.0 {
                let batteryValue =  Int(round(batteryLev * 100))
                batteryLevelValue = String(format: "%i", batteryValue)
            }
            // Set Up notification
            if batteryState == StringConstants.Generic.yes {
                NotificationCenter.default.addObserver(self, selector: #selector(batteryStateChanged(_:)),
                                                       name: UIDevice.batteryStateDidChangeNotification, object: nil)
            }
            if batteryLevel == StringConstants.Generic.yes {
                NotificationCenter.default.addObserver(self, selector: #selector(batteryLevelChanged(_:)),
                                                       name: UIDevice.batteryLevelDidChangeNotification, object: nil)
            }
            if batteryTimerValue != 0 {
                startBatteryTimer()
            }
            callBack(status: true, keepAlive: true,
                     resultKeys: [StringConstants.Generic.cbEvent],
                     resultValues: [StringConstants.Generic.started])
            APZLogger.log(logLvl: "I", message: "APZBattery--Battery Monitor")
        } else {
            callBack(status: false, keepAlive: false,
                     resultKeys: [StringConstants.Generic.errorCode],
                     resultValues: ["APZ-CNT-241"])
            APZLogger.log(logLvl: "E", message: "APZBattery--Invalid Action")
        }
    }
    // MARK: Plugin Clean
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZBattery--Battery Monitor cleanup")
        self.viewController = nil
        if self.delegate != nil {
            self.delegate.donePlugin(self)
            self.delegate = nil
        }
    }
    // MARK: Timer
    func startBatteryTimer() {
        if batteryTimerValue != 0 {
            DispatchQueue.main.async {
                self.batteryTimer = Timer.scheduledTimer(timeInterval: TimeInterval(self.batteryTimerValue),
                                                         target: self, selector: #selector(self.timerSuccesscallback),
                                                         userInfo: nil, repeats: true)
            }
        }
    }
    func stopTimer() {
        DispatchQueue.main.async {
            self.batteryTimer?.invalidate()
            self.batteryTimer = nil
        }
    }
    @objc func timerSuccesscallback() {
        callBack(status: true, keepAlive: true,
                 resultKeys: [StringConstants.Generic.cbEvent, "state", "level"],
                 resultValues: ["TIME", self.batteryStateValue, self.batteryLevelValue])
        APZLogger.log(logLvl: "I", message: "APZBattery--Timer Callback")
    }
    // MARK: Notifications
    @objc func batteryStateChanged(_ notification: Notification?) {
        if notification != nil {
            updateBatteryState()
        } else {
            UIDevice.current.isBatteryMonitoringEnabled = false
        }
    }
    @objc func batteryLevelChanged(_ notification: Notification) {
        updateBatteryLevel()
    }
    func updateBatteryState() {
        if UIDevice.current.batteryState == UIDevice.BatteryState.charging {
            batteryStateValue = "plugged"
        } else {
            batteryStateValue = "unplugged"
        }
        callBack(status: true,
                 keepAlive: true,
                 resultKeys: [StringConstants.Generic.cbEvent, "state", "level"],
                 resultValues: ["STATE", self.batteryStateValue,
                                self.batteryLevelValue])
        APZLogger.log(logLvl: "I", message: "APZBattery--State Callback")
    }
    func updateBatteryLevel() {
        let batteryLev = UIDevice.current.batteryLevel
        if batteryLev > 0.0 {
            let batteryValue = Int(round(batteryLev * 100))
            batteryLevelValue = String(format: "%i", batteryValue)
            if batteryLevelValue == batteryThersholdValue {
                callBack(status: true,
                         keepAlive: true,
                         resultKeys: [StringConstants.Generic.cbEvent, "state", "level"],
                         resultValues: ["THRESHOLD", self.batteryStateValue, self.batteryLevelValue])
                APZLogger.log(logLvl: "I", message: "APZBattery--Threshold Callback")
            } else {
                callBack(status: true,
                         keepAlive: true,
                         resultKeys: [StringConstants.Generic.cbEvent, "state", "level"],
                         resultValues: ["LEVEL", self.batteryStateValue, self.batteryLevelValue])
                APZLogger.log(logLvl: "I", message: "APZBattery--Level Callback")
            }
        }
    }
    override func stop(_ jsonDict: [AnyHashable: Any]?) {
        pluginId = jsonDict?[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        stopTimer()
        NotificationCenter.default.removeObserver(self)
        UIDevice.current.isBatteryMonitoringEnabled = false
        callBack(status: true,
                 keepAlive: false,
                 resultKeys: [StringConstants.Generic.cbEvent],
                 resultValues: [StringConstants.Generic.stopped])
        APZLogger.log(logLvl: "I", message: "APZBattery--Battery Monitor Stopped")
        cleanPlugin()
    }
    // MARK: common CallBack Method
    func callBack(status: Bool, keepAlive: Bool, resultKeys: [String], resultValues: [Any]) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: keepAlive,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
