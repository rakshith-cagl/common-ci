// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import UIKit

enum GestureType {
    case singleTap
    case doubleTap
    case tripleTap
    case longPress
    case pinch
    case swipeLeft
    case swipeRight
    case swipeUp
    case swipeDown
}

extension AppzillonViewController: APZGestureRecognizable, UIGestureRecognizerDelegate {
    // MARK: Gesture Main Methods
    // Factory method to instantiate different gesture objects
    func createGestureObject(type: GestureType, selector: Selector?) -> UIGestureRecognizer {
        switch type {
        case .singleTap:
            let singleTapAction = self.getTapGestureRecognizer(numberOfTapsRequired: 1, selector: selector)
            return singleTapAction
        case .doubleTap:
            let doubleTapAction = self.getTapGestureRecognizer(numberOfTapsRequired: 2, selector: selector)
            return doubleTapAction
        case .tripleTap:
            let tripleTapAction = self.getTapGestureRecognizer(numberOfTapsRequired: 3, selector: selector)
            return tripleTapAction
        case .longPress:
            let longPressAction = UILongPressGestureRecognizer.init(target: self, action: selector)
            longPressAction.delegate = self
            return longPressAction
        case .pinch:
            let pinchAction = UIPinchGestureRecognizer.init(target: self, action: selector)
            pinchAction.delegate = self
            return pinchAction
        case .swipeLeft:
            let swipeLeftGesture = UISwipeGestureRecognizer.init(target: self, action: selector)
            swipeLeftGesture.delegate = self
            return swipeLeftGesture
        case .swipeRight:
            let swipeRightGesture = UISwipeGestureRecognizer.init(target: self, action: selector)
            swipeRightGesture.delegate = self
            return swipeRightGesture
        case .swipeUp:
            let swipeUpGesture = UISwipeGestureRecognizer.init(target: self, action: selector)
            swipeUpGesture.delegate = self
            return swipeUpGesture
        case .swipeDown:
            let swipeDownGesture = UISwipeGestureRecognizer.init(target: self, action: selector)
            swipeDownGesture.delegate = self
            return swipeDownGesture
        }
    }
    func getTapGestureRecognizer(numberOfTapsRequired: Int, selector: Selector?) -> UIGestureRecognizer {
        let tapGestureRecognizer = UITapGestureRecognizer.init(target: self, action: selector)
        tapGestureRecognizer.numberOfTapsRequired = numberOfTapsRequired
        tapGestureRecognizer.delegate = self
        return tapGestureRecognizer
    }
    // MARK: Start gesture
     func startGesture(result: [AnyHashable: Any], webView: WKWebView, callee: String) {
        let isSingleTap = result[StringConstants.GestureRecognizer.singleTap] as?
        String ?? StringConstants.Generic.emptyString
        let isDoubleTap = result[StringConstants.GestureRecognizer.doubleTap] as?
        String ?? StringConstants.Generic.emptyString
        let isTripleTap = result[StringConstants.GestureRecognizer.tripleTap] as?
        String ?? StringConstants.Generic.emptyString
        let isPinch = result[StringConstants.GestureRecognizer.pinch] as?
        String ?? StringConstants.Generic.emptyString
        let isLongPress = result[StringConstants.GestureRecognizer.longPress] as?
        String ?? StringConstants.Generic.emptyString
        let isSwipe = result[StringConstants.GestureRecognizer.swipe] as?
        String ?? StringConstants.Generic.emptyString
        webView.isUserInteractionEnabled = true
        webView.scrollView.bounces = false
        checkForSingleTap(isSingleTap, callee, webView)
        checkForDoubleTap(isDoubleTap, callee, webView)
        checkForTripleTap(isTripleTap, callee, webView)
        checkForPinch(isPinch, callee, webView)
        checkForLongPress(isLongPress, callee, webView)
        checkForSwipes(isSwipe, callee, webView)
        if callee == StringConstants.GestureRecognizer.timer {
            self.gestCalleeTimer = true
        } else {
            self.calleeGest = true
        }
    }
    // MARK: Stop gesture
     func stopGesture(result: [AnyHashable: Any], webView: WKWebView, callee: String) {
         if callee == StringConstants.GestureRecognizer.calleeGesture {
             self.stCalleeGest = false
             self.dtCalleeGest = false
             self.ttCalleeGest = false
             self.pnCalleeGest = false
             self.lpCalleeGest = false
             self.swCalleeGest = false
             self.calleeGest = false
         }
         if !self.gestCalleeTimer && !self.calleeGest {
             webView.scrollView.bounces = false
             removeTapGestureFromWebView(result, webView)
         }
         if self.calleeGest {
             let resultValues = [StringConstants.Generic.stopped]
             self.gestureCallBack(resultValues: resultValues, keepAlive: false)
         }
     }
    fileprivate func removeTapGestureFromWebView(_ result: [AnyHashable : Any], _ webView: WKWebView) {
        let isSingleTap = result[StringConstants.GestureRecognizer.singleTap]
        let isDoubleTap = result[StringConstants.GestureRecognizer.doubleTap]
        let isTripleTap = result[StringConstants.GestureRecognizer.tripleTap]
        let isPinch = result[StringConstants.GestureRecognizer.pinch]
        let isLongPress = result[StringConstants.GestureRecognizer.longPress]
        let isSwipe = result[StringConstants.GestureRecognizer.swipe]
        if isSingleTap == nil && self.singleTap != nil {
            webView.removeGestureRecognizer(self.singleTap)
            self.singleTap = nil
        }
        if isDoubleTap == nil && self.doubleTap != nil {
            webView.removeGestureRecognizer(self.doubleTap)
            self.doubleTap = nil
        }
        if isTripleTap == nil && self.tripleTap != nil {
            webView.removeGestureRecognizer(self.tripleTap)
            self.tripleTap = nil
        }
        if isPinch == nil && self.pinch != nil {
            webView.removeGestureRecognizer(self.pinch)
            self.pinch = nil
        }
        if isLongPress == nil && self.longPress != nil {
            webView.removeGestureRecognizer(self.longPress)
            self.longPress = nil
        }
        if isSwipe == nil && self.swipeEnabled {
            webView.scrollView.removeGestureRecognizer(self.leftSwipe)
            webView.scrollView.removeGestureRecognizer(self.rightSwipe)
            webView.scrollView.removeGestureRecognizer(self.upSwipe)
            webView.scrollView.removeGestureRecognizer(self.downSwipe)
            self.leftSwipe = nil
            self.rightSwipe = nil
            self.upSwipe = nil
            self.downSwipe = nil
            self.swipeEnabled = false
        }
    }
    // MARK: Gesture Initializer Methods
    fileprivate func checkForSingleTap(_ isSingleTap: String, _ callee: String, _ webView: WKWebView) {
        if isSingleTap == StringConstants.Generic.yes {
            if callee == StringConstants.GestureRecognizer.calleeGesture {
                self.stCalleeGest = true
            }
            if self.singleTap == nil {
                self.singleTap = self.createGestureObject(type: GestureType.singleTap,
                                                          selector: #selector(singleTapHandler(_:)))
                                                            as? UITapGestureRecognizer
                webView.addGestureRecognizer(self.singleTap)
            }
        }
    }
    fileprivate func checkForDoubleTap(_ isDoubleTap: String, _ callee: String, _ webView: WKWebView) {
        if isDoubleTap == StringConstants.Generic.yes {
            if callee == StringConstants.GestureRecognizer.calleeGesture {
                self.dtCalleeGest = true
            }
            if self.doubleTap == nil {
                self.doubleTap =  self.createGestureObject(type: GestureType.doubleTap,
                                  selector: #selector(doubleTapHandler(_:)))
                                as? UITapGestureRecognizer
                webView.addGestureRecognizer(self.doubleTap)
                self.singleTap.require(toFail: self.doubleTap)
            }
        }
    }
    fileprivate func checkForTripleTap(_ isTripleTap: String, _ callee: String, _ webView: WKWebView) {
        if isTripleTap == StringConstants.Generic.yes {
            if callee == StringConstants.GestureRecognizer.calleeGesture {
                self.ttCalleeGest = true
            }
            if self.tripleTap == nil {
                self.tripleTap = self.createGestureObject(type: GestureType.tripleTap,
                                 selector: #selector(tripleTapHandler(_:)))
                                    as? UITapGestureRecognizer
                webView.addGestureRecognizer(self.tripleTap)
                self.singleTap.require(toFail: self.tripleTap)
                self.doubleTap.require(toFail: self.tripleTap)
            }
        }
    }
    fileprivate func checkForPinch(_ isPinch: String, _ callee: String, _ webView: WKWebView) {
        if isPinch == StringConstants.Generic.yes {
            if callee == StringConstants.GestureRecognizer.calleeGesture {
                self.pnCalleeGest = true
            }
            if self.pinch == nil {
                self.pinch = self.createGestureObject(type: GestureType.pinch,
                                                      selector: #selector(pinchHandler(_:)))
                                                        as? UIPinchGestureRecognizer
                webView.addGestureRecognizer(self.pinch)
            }
        }
    }
    fileprivate func checkForLongPress(_ isLongPress: String, _ callee: String, _ webView: WKWebView) {
        if isLongPress == StringConstants.Generic.yes {
            if callee == StringConstants.GestureRecognizer.calleeGesture {
                self.lpCalleeGest = true
            }
            if self.longPress == nil {
                self.longPress = self.createGestureObject(type: GestureType.longPress,
                                                          selector: #selector(longPressHandler(_:)))
                                                            as? UILongPressGestureRecognizer
                webView.addGestureRecognizer(self.longPress)
                self.singleTap.require(toFail: self.longPress)
            }
        }
    }
    fileprivate func checkForSwipes(_ isSwipe: String, _ callee: String, _ webView: WKWebView) {
        if isSwipe == StringConstants.Generic.yes {
            if callee == StringConstants.GestureRecognizer.calleeGesture {
                self.swCalleeGest = true
                webView.scrollView.panGestureRecognizer.cancelsTouchesInView = false
                self.upSwipe =  self.createGestureObject(type: GestureType.swipeUp,
                                                         selector: #selector(upSwipeHandler(_:)))
                                                            as? UISwipeGestureRecognizer
                self.downSwipe =  self.createGestureObject(type: GestureType.swipeDown,
                                                           selector: #selector(downSwipeHandler(_:)))
                                                            as? UISwipeGestureRecognizer
                self.upSwipe.direction = .up
                self.upSwipe.cancelsTouchesInView = false
                webView.scrollView.addGestureRecognizer(self.upSwipe)
                self.downSwipe.direction = .down
                self.downSwipe.cancelsTouchesInView = false
                webView.scrollView.addGestureRecognizer(self.downSwipe)
            }
            self.leftSwipe =  self.createGestureObject(type: GestureType.swipeLeft,
                                                       selector: #selector(leftSwipeHandler(_:)))
                                                            as? UISwipeGestureRecognizer
            self.rightSwipe =  self.createGestureObject(type: GestureType.swipeRight,
                                                        selector: #selector(rightSwipeHandler(_:)))
                                                            as? UISwipeGestureRecognizer
            webView.scrollView.panGestureRecognizer.cancelsTouchesInView = false
            self.leftSwipe.direction = .left
            self.leftSwipe.cancelsTouchesInView = false
            webView.scrollView.addGestureRecognizer(self.leftSwipe)
            self.rightSwipe.direction = .right
            self.rightSwipe.cancelsTouchesInView = false
            webView.scrollView.addGestureRecognizer(self.rightSwipe)
            self.swipeEnabled = true
        }
    }
    // MARK: Gesture Delegate Method
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer,
                                  shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer)
                                                           -> Bool {
        return true
    }
    // MARK: Gesture Selector Methods
    @objc func singleTapHandler(_ sender: UITapGestureRecognizer) {
        if sender.state == .recognized {
            if self.stCalleeGest {
                let resultValues = [StringConstants.GestureRecognizer.firstTap]
                self.gestureCallBack(resultValues: resultValues, keepAlive: true)
            }
            if self.gestCalleeTimer {
                self.resetIdleTimer()
            }
        }
    }
    @objc func doubleTapHandler(_ sender: UITapGestureRecognizer) {
        if sender.state == .recognized {
            if self.dtCalleeGest {
                let resultValues = [StringConstants.GestureRecognizer.secondTap]
                self.gestureCallBack(resultValues: resultValues, keepAlive: true)
            }
            if self.gestCalleeTimer {
                self.resetIdleTimer()
            }
        }
    }
    @objc  func tripleTapHandler(_ sender: UITapGestureRecognizer) {
        if sender.state == .recognized {
            if self.ttCalleeGest {
                let resultValues = [StringConstants.GestureRecognizer.treeTaps]
                self.gestureCallBack(resultValues: resultValues, keepAlive: true)
            }
            if self.gestCalleeTimer {
                self.resetIdleTimer()
            }
        }
    }
    @objc  func longPressHandler(_ sender: UILongPressGestureRecognizer) {
        if sender.state == .began {
            if self.lpCalleeGest {
                let resultValues = [StringConstants.GestureRecognizer.longPressed]
                self.gestureCallBack(resultValues: resultValues, keepAlive: true)
            }
            if self.gestCalleeTimer {
                self.resetIdleTimer()
            }
        }
    }
    @objc  func pinchHandler(_ sender: UIPinchGestureRecognizer) {
        if sender.state == .recognized {
            if self.pnCalleeGest {
                let resultValues = [StringConstants.GestureRecognizer.pinch]
                self.gestureCallBack(resultValues: resultValues, keepAlive: true)
            }
            if self.gestCalleeTimer {
                self.resetIdleTimer()
            }
        }
    }
    @objc  func leftSwipeHandler(_ sender: UISwipeGestureRecognizer) {
        if self.swCalleeGest {
            let resultValues = [StringConstants.GestureRecognizer.swipeLeft]
            self.gestureCallBack(resultValues: resultValues, keepAlive: true)
        }
        if self.gestCalleeTimer {
            self.resetIdleTimer()
        }
    }
    @objc  func rightSwipeHandler(_ sender: UISwipeGestureRecognizer) {
        if self.swCalleeGest {
            let resultValues = [StringConstants.GestureRecognizer.swipeRight]
            self.gestureCallBack(resultValues: resultValues, keepAlive: true)
        }
        if self.gestCalleeTimer {
            self.resetIdleTimer()
        }
    }
    @objc  func upSwipeHandler(_ sender: UISwipeGestureRecognizer) {
        if self.swCalleeGest {
            let resultValues = [StringConstants.GestureRecognizer.swipeUp]
            self.gestureCallBack(resultValues: resultValues, keepAlive: true)
        }
        if self.gestCalleeTimer {
            self.resetIdleTimer()
        }
    }
    @objc func downSwipeHandler(_ sender: UISwipeGestureRecognizer) {
        if self.swCalleeGest {
            let resultValues = [StringConstants.GestureRecognizer.swipeDown]
            self.gestureCallBack(resultValues: resultValues, keepAlive: true)
        }
        if self.gestCalleeTimer {
            self.resetIdleTimer()
        }
    }
    // MARK: Reset Idle Timer Methods
     func resetIdleTimer() {
        if (self.idleTimer == nil) && !self.calledFromPlugin {
            self.idleTimer = Timer.scheduledTimer(timeInterval: self.appIdleMaxTime,
                                                  target: self,
                                                  selector: #selector(idleTimerExceeded),
                                                  userInfo: nil, repeats: false)
            self.calledFromPlugin = true
            let resultValues = [StringConstants.Generic.started]
            self.timerCallBack(resultValues: resultValues, keepAlive: true)
        } else {
            if fabs(self.idleTimer.fireDate.timeIntervalSinceNow) < self.appIdleMaxTime-1.0 {
                self.idleTimer.fireDate =  NSDate(timeIntervalSinceNow: self.appIdleMaxTime) as Date
            }
        }
    }
    @objc func idleTimerExceeded() {
        self.idleTimer = nil
        self.timerStop = true
        self.gestCalleeTimer = false
        self.stopGesture(result: self.inputTimerJSON, webView: webView, callee: StringConstants.GestureRecognizer.timer)
        let resultValues = [StringConstants.GestureRecognizer.timerExceeds]
        self.timerCallBack(resultValues: resultValues, keepAlive: false)
        self.inputTimerJSON = nil
    }
    // MARK: CallBack Method
    func gestureCallBack(resultValues: [Any], keepAlive: Bool) {
        let resultKeys = [StringConstants.Generic.cbEvent]
        let pluginId = self.inputGestJSON[StringConstants.Generic.pluginId] as?
        String ?? StringConstants.Generic.emptyString
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: keepAlive,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: self.webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
    func timerCallBack(resultValues: [Any], keepAlive: Bool) {
        let resultKeys = [StringConstants.Generic.cbEvent]
        let pluginId = self.inputTimerJSON[StringConstants.Generic.pluginId] as?
        String ?? StringConstants.Generic.emptyString
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: keepAlive,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultValues)
        MiscellaneousMethod.shared.jsLayerCall(webView: self.webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
