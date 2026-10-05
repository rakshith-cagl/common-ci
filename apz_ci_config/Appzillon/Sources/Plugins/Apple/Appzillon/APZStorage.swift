// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import SQLite3

class APZStorage: APZPlugin {
    var webVW: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    let storageErrorCode = "APZ-CNT-056"
    
    // MARK: Plugin Life Cycle
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]!) {
        self.webVW = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webVW, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
        if !jsonDict.isEmpty {
            APZLogger.log(logLvl: "D", message: "APZStorage--execute")
            runSql(jsonDict: jsonDict)
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "D", message: "APZStorage--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    func runSql(jsonDict: [AnyHashable: Any]) {
        let query = jsonDict["executeQuery"] as? String ?? StringConstants.Generic.emptyString
        let queryType = query.components(separatedBy: " ").first
        if !query.isEmpty, let queryType = queryType?.lowercased() {
            switch queryType {
            case "create", "insert", "update", "delete":
                APZLogger.log(logLvl: "I", message: "APZStorage--\(queryType) query")
                executeQuery(jsonDict: jsonDict, queryType: queryType)
            case "select":
                APZLogger.log(logLvl: "I", message: "APZStorage--selectQuery")
                selectQuery(jsonDict: jsonDict)
            default:
                let queryId = jsonDict["queryId"] as? String ?? StringConstants.Generic.emptyString
                callBack(resultKey: [StringConstants.Generic.errorCode, "queryId"],
                         resultValue: [storageErrorCode, queryId],
                         status: false)
            }
        }
    }
    func executeQuery(jsonDict: [AnyHashable: Any], queryType: String) {
        let dbPathInDocDirectory = getDataBasePath(jsonDict: jsonDict)
        var dbRef: OpaquePointer?
        var sqlStatus = sqlite3_open(dbPathInDocDirectory, &dbRef)
        if sqlStatus == SQLITE_OK,
           let query = jsonDict["executeQuery"] as? String,
           let queryId = jsonDict["queryId"] as? String {
            var queryStatement: OpaquePointer?
            sqlStatus = sqlite3_prepare_v2(dbRef, query.cString(using: .utf8), -1, &queryStatement, nil)
            if sqlStatus == SQLITE_OK {
                let sqlResp = sqlite3_step(queryStatement)
                if sqlResp == SQLITE_DONE {
                    callBack(resultKey: ["queryId", "sqlResult"],
                             resultValue: [queryId, "success"],
                             status: true)
                    sqlite3_finalize(queryStatement)
                    sqlite3_close(dbRef)
                    APZLogger.log(logLvl: "I", message: "APZStorage--\(queryType) success")
                } else {
                    let errorCode = getErrorCode(queryType: queryType)
                    callBack(resultKey: [StringConstants.Generic.errorCode, "queryId", "sqlResult"],
                             resultValue: [errorCode, queryId, "failure"],
                             status: false)
                    sqlite3_finalize(queryStatement)
                    sqlite3_close(dbRef)
                    APZLogger.log(logLvl: "E", message: "APZStorage--\(queryType) Table query failed")
                }
            } else {
                callBack(resultKey: [StringConstants.Generic.errorCode, "queryId", "sqlResult"],
                         resultValue: [storageErrorCode, queryId, "failure"],
                         status: false)
                APZLogger.log(logLvl: "E",
                              message: "APZStorage--\(queryType) Table query failed with status code: \(sqlStatus)")
                sqlite3_close(dbRef)
            }
        } else {
            if let queryId = jsonDict["queryId"] as? String {
                callBack(resultKey: [StringConstants.Generic.errorCode, "queryId", "sqlResult"],
                         resultValue: ["APZ-CNT-051", queryId, "failure"], status: false)
                sqlite3_close(dbRef)
                APZLogger.log(logLvl: "E", message: "APZStorage--Unable to open DB file")
            }
        }
    }
    func selectQuery(jsonDict: [AnyHashable: Any]) {
        let dbPathInDocDirectory = getDataBasePath(jsonDict: jsonDict)
        var dbRunSql: OpaquePointer?
        var sqlStatus = sqlite3_open(dbPathInDocDirectory, &dbRunSql)
        if sqlStatus == SQLITE_OK, let query = jsonDict["executeQuery"] as? String,
           let queryId = jsonDict["queryId"] as? String {
            var selectStatement: OpaquePointer?
            sqlStatus = sqlite3_prepare_v2(dbRunSql, query.cString(using: .utf8), -1, &selectStatement, nil)
            if sqlStatus == SQLITE_OK {
                let sqlResp = sqlite3_step(selectStatement)
                if sqlResp == SQLITE_ROW {
                    var outputDic: [AnyHashable: Any] = [:]
                    var outputRowDic: [AnyHashable: Any] = [:]
                    var outputArray: [[AnyHashable: Any]] = []
                    var columnName = StringConstants.Generic.emptyString
                    for column in 0..<sqlite3_column_count(selectStatement) {
                        columnName = String(cString: sqlite3_column_name(selectStatement, column))
                        let dataType = sqlite3_column_type(selectStatement, column)
                        switch dataType {
                        case SQLITE_INTEGER:
                            outputDic[columnName] = String(format: "%d", sqlite3_column_int(selectStatement, column))
                        case SQLITE_FLOAT:
                            outputDic[columnName] = String(format: "%f", sqlite3_column_double(selectStatement, column))
                        case SQLITE_TEXT:
                            if let textValue = sqlite3_column_text(selectStatement, column) {
                                outputDic[columnName] = String(cString: textValue)
                            } else {
                                outputDic[columnName] = StringConstants.Generic.emptyString
                            }
                        default:
                            outputDic[columnName] = StringConstants.Generic.emptyString
                        }
                    }
                    outputArray.append(outputDic)
                    while sqlite3_step(selectStatement) == SQLITE_ROW {
                        for column in 0..<sqlite3_column_count(selectStatement) {
                            columnName = String(cString: sqlite3_column_name(selectStatement, column))
                            let dataType = sqlite3_column_type(selectStatement, column)
                            switch dataType {
                            case SQLITE_INTEGER:
                                outputRowDic[columnName] = String(format: "%d",
                                                                  sqlite3_column_int(selectStatement, column))
                            case SQLITE_FLOAT:
                                outputRowDic[columnName] = String(format: "%f",
                                                                  sqlite3_column_double(selectStatement, column))
                            case SQLITE_TEXT:
                                if let textValue = sqlite3_column_text(selectStatement, column) {
                                    outputRowDic[columnName] = String(cString: textValue)
                                } else {
                                    outputRowDic[columnName] = StringConstants.Generic.emptyString
                                    break
                                }
                            default:
                                outputRowDic[columnName] = StringConstants.Generic.emptyString
                            }
                        }
                        outputArray.append(outputRowDic)
                    }
                    if query.contains("tb_notifications") {
                        for index in 0..<outputArray.count {
                            var tempOutputDict: [AnyHashable: Any] = [:]
                            tempOutputDict["id"] = outputArray[index]["id"]
                            let message = outputArray[index]["message"] as? String ?? ""
                            let decryptedMessage = CryptoSwiftManager.shared.encryptPlainString(key: STORAGE_AES_KEY, value: message)
                            tempOutputDict["message"] = decryptedMessage
                            tempOutputDict["readFlag"] = outputArray[index]["readFlag"]
                            tempOutputDict["timeStamp"] = outputArray[index]["timeStamp"]
                            outputArray[index] = tempOutputDict
                        }
                    }
                    do {
                        let outputArrayString = try String.init(data:
                                                                    JSONSerialization.data(withJSONObject: outputArray,
                                                options: .withoutEscapingSlashes),
                                                encoding: .utf8)
                        callBack(resultKey: ["queryId", "sqlResult"],
                                 resultValue: [queryId, outputArrayString as Any],
                                 status: true)
                        sqlite3_finalize(selectStatement)
                        sqlite3_close(dbRunSql)
                        APZLogger.log(logLvl: "I", message: "APZStorage--Select Query Success")
                    } catch let error {
                        APZLogger.log(logLvl: "E", message: error.localizedDescription)
                    }
                } else {
                    callBack(resultKey: ["queryId", "sqlResult"],
                             resultValue: [queryId, "{}"],
                             status: true)
                    sqlite3_finalize(selectStatement)
                    sqlite3_close(dbRunSql)
                    APZLogger.log(logLvl: "I", message: "APZStorage--Select Query Success with no rows in table")
                }
            } else {
                callBack(resultKey: [StringConstants.Generic.errorCode, "queryId", "sqlResult"],
                         resultValue: [storageErrorCode, queryId, "failure"],
                         status: false)
                APZLogger.log(logLvl: "E", message: "APZStorage--select failed with error code: \(sqlStatus)")
                sqlite3_close(dbRunSql)
            }
        } else {
            if let queryId = jsonDict["queryId"] as? String {
                callBack(resultKey: [StringConstants.Generic.errorCode, "queryId", "sqlResult"],
                         resultValue: ["APZ-CNT-051", queryId, "failure"], status: false)
                sqlite3_close(dbRunSql)
                APZLogger.log(logLvl: "E", message: "APZStorage--Select Unable to open DB file")
            }
        }
    }
    func callBack(resultKey: [String], resultValue: [Any], status: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKey,
                                                                    responseValues: resultValue)
        MiscellaneousMethod.shared.jsLayerCall(webView: webVW,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
    func getDataBasePath(jsonDict: [AnyHashable: Any]) -> String {
        if let appString = self.viewController?.appString,
            let dbName = jsonDict["databaseName"] as? String {
            let documentsDirectory = FileManagerUtility.documentDirectory().path
            let dbPath = documentsDirectory.appendingFormat("/Assets/apps/%@/sqlite/%@.sqlite3", appString, dbName)
            return dbPath
        }
        return StringConstants.Generic.emptyString
    }
    func getErrorCode(queryType: String) -> String {
        var errorCode = StringConstants.Generic.emptyString
        switch queryType {
        case "create":
            errorCode = "APZ-CNT-052"
        case "insert":
            errorCode = "APZ-CNT-053"
        case "update":
           errorCode = "APZ-CNT-054"
        case "delete":
           errorCode = "APZ-CNT-055"
        default:
            errorCode = StringConstants.Generic.emptyString
        }
        return errorCode
    }
}
