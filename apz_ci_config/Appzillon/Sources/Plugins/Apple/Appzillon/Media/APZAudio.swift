//
//  APZAudio.swift
//  Appzillon
//
//  Created by Bhavya V on 12/11/21.
//

import Foundation

class APZAudio: APZPlugin, AudioResponseDelegate {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var appString: String
    var audioFileExtension: String = StringConstants.Generic.emptyString
    let audioPlayer: AudioHelper
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView!, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        self.appString = viewController?.appString ?? StringConstants.Generic.emptyString
        self.audioPlayer = AudioHelper.init(appString: appString)
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        if !jsonDict.isEmpty {
            APZLogger.log(logLvl: "D", message: "APZAudio--execute")
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            audioPlayer.audioDelegate = self
            audioFileExtension = getAudioExtension(jsonDict: jsonDict)
            let action = jsonDict[StringConstants.Generic.action] as? String
            switch action {
            case StringConstants.Audio.play: audioPlayer.audioPlay(jsonDict: jsonDict)
            case StringConstants.Audio.record: do {
                    let result = audioPlayer.audioRecord(jsonDict: jsonDict)
                    recordAndPauseCallBacks(result: result)
                }
            case StringConstants.Generic.save: audioPlayer.audioSave(jsonDict: jsonDict)
            case StringConstants.Audio.pause: do {
                let result = audioPlayer.audioPause(jsonDict: jsonDict)
                recordAndPauseCallBacks(result: result)
            }
            default: APZLogger.log(logLvl: "E", message: "APZAudio--Invalid Action")
            }
        }
    }
    func cleanPlugin() {
        APZLogger.log(logLvl: "I", message: "APZAudio--Done")
        self.viewController = nil
        self.delegate.donePlugin(self)
    }
    // MARK: Callback methods
    func successCallBack(status: Bool, keepAlive: Bool) {
        let resultKey = [StringConstants.Generic.message]
        let resultValue = [StringConstants.Generic.success]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: keepAlive,
                                                                    responseKeys: resultKey,
                                                                    responseValues: resultValue)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
    func errorCallBack(errorCode: String, keepAlive: Bool) {
        let resultKey = [StringConstants.Generic.errorCode]
        let resultValue = [errorCode]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: false,
                                                                    keepAlive: keepAlive,
                                                                    responseKeys: resultKey,
                                                                    responseValues: resultValue)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
    func saveCallBack(key: String, value: String) {
        let resultKey = [key]
        let resultValue = [value]
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: true,
                                                                    keepAlive: false,
                                                                    responseKeys: resultKey,
                                                                    responseValues: resultValue)
        MiscellaneousMethod.shared.jsLayerCall(webView: webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
    func recordAndPauseCallBacks(result: AudioRecorderAndPlayerResult) {
        switch result.status {
        case .success: successCallBack(status: true, keepAlive: true)
        case .failure: errorCallBack(errorCode: result.message, keepAlive: false)
            cleanPlugin()
        default:
            break
        }
    }
    // MARK: Utility methods
    func getAudioExtension(jsonDict: [AnyHashable: Any]) -> String {
        let wavFileFormat: String = jsonDict[StringConstants.Audio.wavFileFormat] as? String ??
        StringConstants.Generic.emptyString
        let fileFormat: String = (wavFileFormat == StringConstants.Generic.yes) ?
        StringConstants.Audio.wav : StringConstants.Audio.m4a
        return fileFormat
    }

    func sendStatus(status: AudioStatus, message: String) {
        switch status {
        case .success: do {
            if message == StringConstants.Generic.emptyString {
                successCallBack(status: true, keepAlive: false)
            } else if message.contains(audioFileExtension) {
                saveCallBack(key: StringConstants.Generic.filePath, value: message)
            } else {
                saveCallBack(key: StringConstants.Generic.base64, value: message)
            }
        }
        case .failure: do {
            if message == StringConstants.Audio.errorRecorderBusy {
                errorCallBack(errorCode: message, keepAlive: true)
            } else {
                errorCallBack(errorCode: message, keepAlive: false)
                cleanPlugin()
            }
        }
        default:
            break
        }
    }
}
