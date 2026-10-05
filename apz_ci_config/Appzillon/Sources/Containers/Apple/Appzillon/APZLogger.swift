// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation

class APZLogger: NSObject {
    static var staticWebView = WKWebView()
    static var logLevel: Int = 0
    @objc static func log(logLvl: String, message: String) {
        if getIntValueOfLogLvl(logLvl: logLvl) >= APZLogger.logLevel {
            logToConsole(message: message, logLvl: logLvl)
            DispatchQueue.main.async {
                APZLogger.staticWebView.evaluateJavaScript(String(format: "Apz.appendLog('%@','%@');",
                                                                  logLvl, message),
                                                           completionHandler: nil)
            }
        }
    }
    static func logToConsole(message: String, logLvl: String) {
        print(String(format: "%@ %@ %@--%@", getDateAndTime(), appName(), getMsgType(logLvl: logLvl), message))
    }
    static func getIntValueOfLogLvl(logLvl: String) -> Int {
        var intValueofDebugLvl: Int
        if logLvl == StringConstants.Logger.runTimeDebugSeverityFatal {
            intValueofDebugLvl = 0
        } else if logLvl == StringConstants.Logger.runTimeDebugSeverityError {
            intValueofDebugLvl = 1
        } else if logLvl == StringConstants.Logger.runTimeDebugSeverityWarn {
            intValueofDebugLvl = 2
        } else if logLvl == StringConstants.Logger.runTimeDebugSeverityInfo {
            intValueofDebugLvl = 3
        } else {
            intValueofDebugLvl = 4
        }
        return intValueofDebugLvl
    }
    static func getMsgType(logLvl: String) -> String {
        var msgType = StringConstants.Generic.emptyString
        if logLvl == StringConstants.Logger.runTimeDebugSeverityError {
            msgType = "ERROR"
        } else if logLvl == StringConstants.Logger.runTimeDebugSeverityInfo {
            msgType = "INFO"
        } else if logLvl == StringConstants.Logger.runTimeDebugSeverityFatal {
            msgType = "FATAL"
        } else if logLvl == StringConstants.Logger.runTimeDebugSeverityWarn {
            msgType = "WARN"
        } else {
            msgType = "DEBUG"
        }
        return msgType
    }
    static func setStaticVoidWebView(webView: WKWebView) {
        APZLogger.staticWebView = webView
    }
    static func setLogLevel(logLvl: String) {
        APZLogger.logLevel = getIntValueOfLogLvl(logLvl: logLvl)
    }
    static func getDateAndTime() -> String {
            let date = Date()
            let format = DateFormatter()
            format.dateFormat = "yyyy-MM-dd HH:mm:ss"
            let timestamp = format.string(from: date)
            return timestamp
        }
    static func appName() -> String {
        return Bundle.main.object(forInfoDictionaryKey: "CFBundleName") as?
        String ?? StringConstants.Generic.emptyString
    }
}
