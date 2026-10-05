// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

extension AppzillonViewController {
    func initEventMonitoring() {
       isAppPausedMonitored = false
       isAppResumeMonitored = false
       isAppCallStartMonitored = false
       isAppCallEndMonitored = false
   }
    func controlEvents(_ jsonDict: [AnyHashable: Any]) {
       if let allEvents = jsonDict[StringConstants.EventMonitor.allEvents] as? String {
           initializeAllEvents(allEvents)
       } else {
           let appPausedEvent = jsonDict[StringConstants.EventMonitor.appPausedEvent] as? String
           pauseEvent(appPausedEvent)
           let appResumedEvent = jsonDict[StringConstants.EventMonitor.appPausedEvent] as? String
           resumeEvent(appResumedEvent)
           let callStartEvent = jsonDict[StringConstants.EventMonitor.appCallStartEvent] as? String
           startEvent(callStartEvent)
           let callEndEvent = jsonDict[StringConstants.EventMonitor.appCallEndEvent] as? String
           endEvent(callEndEvent)
       }
   }
   func initializeAllEvents(_ allEvents: String?) {
       if allEvents == StringConstants.EventMonitor.on {
           startAppResumedMonitoring()
           startAppPausedMonitoring()
           isAppCallStartMonitored = true
           isAppCallEndMonitored = true
       } else if allEvents == StringConstants.EventMonitor.off {
           stopAppResumedMonitoring()
           stopAppPausedMonitoring()
           isAppCallStartMonitored = false
           isAppCallEndMonitored = false
       }
   }
   func pauseEvent(_ appPausedEvent: String?) {
       if appPausedEvent == StringConstants.EventMonitor.on {
           startAppPausedMonitoring()
       } else if appPausedEvent == StringConstants.EventMonitor.off {
           stopAppPausedMonitoring()
       }
   }
   func resumeEvent(_ appResumedEvent: String?) {
       if appResumedEvent == StringConstants.EventMonitor.on {
           startAppResumedMonitoring()
       } else if appResumedEvent == StringConstants.EventMonitor.off {
           stopAppResumedMonitoring()
       }
   }
   func startEvent(_ callStartEvent: String?) {
       if callStartEvent == StringConstants.EventMonitor.on {
           isAppCallStartMonitored = true
       } else if callStartEvent == StringConstants.EventMonitor.off {
           isAppCallStartMonitored = false
       }
   }
   func endEvent(_ callEndEvent: String?) {
       if callEndEvent == StringConstants.EventMonitor.on {
           isAppCallEndMonitored = true
       } else if callEndEvent == StringConstants.EventMonitor.off {
           isAppCallEndMonitored = false
       }
   }
   func startAppPausedMonitoring() {
       if !isAppPausedMonitored {
           NotificationCenter.default.addObserver(self,
                                                  selector: #selector(onAppPaused),
                                                  name: UIApplication.didEnterBackgroundNotification,
                                                  object: nil)
           isAppPausedMonitored = true
       } else {
           print("---Already Monitoring App Paused Event---")
       }
   }
   @objc func onAppPaused(_ notification: Notification?) {
       let returnResultkeys = [StringConstants.Generic.cbEvent]
       let returnResult = [StringConstants.EventMonitor.appPaused]
       let plgId = controlEventRequest[StringConstants.Generic.pluginId] as? String ?? ""
       let params = APZJsonUtility.shared.createResponseJSONString(pluginId: plgId,
                                                                   status: true,
                                                                   keepAlive: true,
                                                                   responseKeys: returnResultkeys,
                                                                   responseValues: returnResult)
       MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                              jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                              parameter: params)
   }
   func stopAppPausedMonitoring() {
       if isAppPausedMonitored {
           NotificationCenter.default.removeObserver(self,
                                                     name: UIApplication.didEnterBackgroundNotification,
                                                     object: nil)
           isAppPausedMonitored = false
       } else {
           print("--Already Stopped App Pause Event--")
       }
   }
   func startAppResumedMonitoring() {
       if !isAppResumeMonitored {
           NotificationCenter.default.addObserver(self,
                                                  selector: #selector(onAppWillResume),
                                                  name: UIApplication.willEnterForegroundNotification,
                                                  object: nil)
           isAppResumeMonitored = true
       } else {
           print("--Already Started AppResumedMonitoring--")
       }
   }
   @objc func onAppWillResume(_ notification: Notification?) {
       let returnResultkeys = [StringConstants.Generic.cbEvent]
       let returnResult = [StringConstants.EventMonitor.appResumed]
       let plgId = controlEventRequest[StringConstants.Generic.pluginId] as? String ?? ""
       let params = APZJsonUtility.shared.createResponseJSONString(pluginId: plgId,
                                                                   status: true,
                                                                   keepAlive: true,
                                                                   responseKeys: returnResultkeys,
                                                                   responseValues: returnResult)
       MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                              jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                              parameter: params)
   }
   func stopAppResumedMonitoring() {
       if isAppResumeMonitored {
           NotificationCenter.default.removeObserver(self,
                                                     name: UIApplication.willEnterForegroundNotification,
                                                     object: nil)
           isAppResumeMonitored = false
       } else {
           print("---Already Stopped App Resume Monitoring---")
       }
   }
}
