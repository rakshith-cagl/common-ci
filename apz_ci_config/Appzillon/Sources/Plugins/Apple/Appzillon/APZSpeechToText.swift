// Copyright (c) 2021 Appzillon. All rights reserved.

import Foundation
import Speech

class APZSpeechToText: APZPlugin {
    var webView: WKWebView
    var viewController: AppzillonViewController?
    var pluginId: String = StringConstants.Generic.emptyString
    var jsonDict: [AnyHashable: Any] = [:]
    var recognitionRequest = SFSpeechAudioBufferRecognitionRequest()
    var audioEngine = AVAudioEngine()
    var recognitionTask: SFSpeechRecognitionTask?
    var speechResult: String?
    var inputNode: AVAudioInputNode?
    var speechRecognizer: SFSpeechRecognizer?
    var timer: Timer?
    var timerDuration = 0
    var listenerStartSent: Bool = false
    let genericErrorCode = "APZ-CNT-333"
    // MARK: Plugin LifeCycle Methods
    override init(plugin wbView: WKWebView, _ jsonDict: [AnyHashable: Any]) {
        self.webView = wbView
        self.viewController = MiscellaneousMethod.shared.getAppzillonViewController()
        super.init(plugin: webView, jsonDict)
    }
    override func execute(_ jsonDict: [AnyHashable: Any]) {
        APZLogger.log(logLvl: "D", message: "APZSpeechToText--execute")
        if !jsonDict.isEmpty {
            pluginId = jsonDict[StringConstants.Generic.pluginId] as? String ?? StringConstants.Generic.emptyString
            self.jsonDict = jsonDict
            if jsonDict[StringConstants.Generic.action] as? String == "START" ||
                jsonDict[StringConstants.Generic.action] as? String == "RESUME" {
                listenerStartSent = false
                timerDuration = Int(jsonDict["timerDuration"] as? String ?? "0") ?? 0
                callSpeechToTextEvent()
            } else if jsonDict[StringConstants.Generic.action] as? String == "STOP" {
                stopListening()
            }
        } else {
            cleanPlugin()
        }
    }
    func cleanPlugin() {
        self.viewController = nil
        self.delegate.donePlugin(self)
        self.speechRecognizer = nil
        self.recognitionTask = nil
        self.timer = nil
        self.inputNode = nil
        self.jsonDict = [:]
    }
    // MARK: Speech Request
    func callSpeechToTextEvent() {
        initializeSpeechRecognizer()
        audioEngine = AVAudioEngine()
        speechRecognizer?.delegate = self
        // MARK: Do not remove this line,
        // keep checking Apple documentation for
        // onDeviceRecognition flag as till ios 14 this flag returns correct value only after 2nd access.
         _ = checkForOnDeviceRecognition()
        SFSpeechRecognizer.requestAuthorization({ [self] status in
            switch status {
            case .authorized:
                print("Authorized")
                checkForLanguageSupport()
            case .denied:
                print("Denied")
                speechToTextCallBack(resultKeys: [StringConstants.Generic.errorCode],
                                     resultvalues: ["APZ-CNT-329"],
                                     status: false,
                                     keepAlive: false)
                cleanPlugin()
            case .notDetermined:
                print("Not Determined")
            case .restricted:
                print("Restricted")
            default:
                break
            }
        })
    }
    func initializeSpeechRecognizer() {
        if let languageCode = jsonDict["languageCode"] as? String {
            speechRecognizer = SFSpeechRecognizer(locale: NSLocale(localeIdentifier: languageCode) as Locale)
        } else {
            let language = NSLocale.preferredLanguages.first
            if let language = language {
                speechRecognizer = SFSpeechRecognizer(locale: NSLocale(localeIdentifier: language) as Locale)
            }
        }
    }
    // MARK: Listening Methods
    func checkForLanguageSupport() {
        if speechRecognizer == nil {
            speechToTextCallBack(resultKeys: [StringConstants.Generic.errorCode],
                                 resultvalues: ["APZ-CNT-331"], status: false, keepAlive: false)
        } else {
            if audioEngine.isRunning {
                audioEngine.stop()
                recognitionRequest.endAudio()
            } else {
                startListening()
            }
            if jsonDict[StringConstants.Generic.action] as? String == "RESUME" {
                speechToTextCallBack(resultKeys: [StringConstants.Generic.action,
                                                  StringConstants.Generic.text],
                                     resultvalues: ["RESUMED"], status: true, keepAlive: true)
            }
        }
    }
    func checkForOnDeviceRecognition() -> Bool {
        if #available(iOS 13, *) {
            return speechRecognizer?.supportsOnDeviceRecognition ?? false
        }
        return false
    }
    func startListening() {
        DispatchQueue.main.async(execute: { [self] in
            if recognitionTask != nil {
                recognitionTask?.cancel()
                recognitionTask = nil
                speechResult = nil
            }
            let audioSession = AVAudioSession.sharedInstance()
            do {
                try audioSession.setCategory(.playAndRecord)
                try audioSession.setActive(true, options: .notifyOthersOnDeactivation)
                recognitionRequest = SFSpeechAudioBufferRecognitionRequest()
                inputNode = audioEngine.inputNode
                recognitionRequest.shouldReportPartialResults = true
                if jsonDict["supportsOnDeviceRecognition"] as? String == StringConstants.Generic.yes {
                    callOnDeviceRecognization()
                }
                // Inform App that vioce listener started
                if jsonDict[StringConstants.Generic.action] as? String == "START" && !listenerStartSent {
                    speechToTextCallBack(resultKeys: [StringConstants.Generic.action,
                                                      StringConstants.Generic.text],
                                         resultvalues: ["STARTED", ""],
                                         status: true, keepAlive: true)
                }
                if !jsonDict.isEmpty {
                    startRecognizationTask()
                }
            } catch {
                print("error in audio session")
            }
        })
    }
    fileprivate func startRecognizationTask() {
        startTimer()
        recognitionTask = speechRecognizer?.recognitionTask(with: recognitionRequest, resultHandler: { result, error in
            if let result = result {
                print("RESULT:\(String(describing: result.bestTranscription.formattedString))")
                let currentResult = result.bestTranscription.formattedString
                if self.speechResult != currentResult {
                    self.speechResult = result.bestTranscription.formattedString
                    self.invalidateTimer()
                    self.startTimer()
                }
            }
            if let error = error {
                self.handleErrorForSpeechRecognization(error: error)
            }
        })
        // Sets the recording format
        let recordingFormat = inputNode?.outputFormat(forBus: AVAudioNodeBus(0))
        inputNode?.removeTap(onBus: AVAudioNodeBus(0))
        inputNode?.installTap(onBus: AVAudioNodeBus(0),
                              bufferSize: AVAudioFrameCount(1024),
                              format: recordingFormat) { [self] buffer, _ in
            recognitionRequest.append(buffer)
        }
        // Starts the audio engine, i.e. it starts listening.
        audioEngine.prepare()
        do {
            try audioEngine.start()
            print("Say Something, I'm listening")
        } catch {
            print("error in audio engine")
        }
    }
    fileprivate func handleErrorForSpeechRecognization(error: Error) {
        if (error as NSError).code == 601 {
            invalidateTimer()
            cancelRegTask()
            speechToTextCallBack(resultKeys: [StringConstants.Generic.errorCode],
                                 resultvalues: [genericErrorCode],
                                 status: false,
                                 keepAlive: false)
            cleanPlugin()
        } else if (error as NSError).code == 4 {
            let connectionError = (error as NSError).userInfo["NSUnderlyingError"] as? NSError
            if connectionError?.code == 16 {
                let networkError = connectionError?.userInfo["NSUnderlyingError"] as? NSError
                if networkError?.code == 50 {
                    invalidateTimer()
                    cancelRegTask()
                    speechToTextCallBack(resultKeys: [StringConstants.Generic.errorCode],
                                         resultvalues: ["APZ-CNT-332"],
                                         status: false,
                                         keepAlive: false)
                    cleanPlugin()
                }
            }
        }
        if jsonDict["pauseRecognize"] as? String == StringConstants.Generic.yes {
            invalidateTimer()
            cancelRegTask()
            speechToTextCallBack(resultKeys: [StringConstants.Generic.errorCode],
                                 resultvalues: ["APZ-CNT-203"],
                                 status: false,
                                 keepAlive: false)
            cleanPlugin()
        }
    }
    func callOnDeviceRecognization() {
        if #available(iOS 13, *) {
            if checkForOnDeviceRecognition() {
                recognitionRequest.requiresOnDeviceRecognition = true
            } else {
                invalidateTimer()
                cancelRegTask()
                speechToTextCallBack(resultKeys: [StringConstants.Generic.errorCode,
                                                  StringConstants.Generic.errorMessage],
                                     resultvalues: [genericErrorCode, "OnDeviceRecognition is not supported"],
                                     status: false,
                                     keepAlive: false)
                cleanPlugin()
            }
        } else {
            speechToTextCallBack(resultKeys: [StringConstants.Generic.errorCode,
                                              StringConstants.Generic.errorMessage],
                                 resultvalues: [genericErrorCode, "OnDeviceRecognition is not supported"],
                                 status: false,
                                 keepAlive: false)
            cleanPlugin()
        }
    }
    // MARK: Stop Listening Methods
    func stopListening() {
        invalidateTimer()
        cancelRegTask()
        let resultkeys = [StringConstants.Generic.action, StringConstants.Generic.text]
        var result: [Any] = []
        if let speechResult = speechResult, !speechResult.isEmpty {
            result = ["STOPPED", speechResult]
        } else {
            result = ["STOPPED", ""]
        }
        speechToTextCallBack(resultKeys: resultkeys, resultvalues: result, status: true, keepAlive: false)
        cleanPlugin()
    }
    func stopAudio() {
        if audioEngine.isRunning {
            audioEngine.stop()
            recognitionRequest.endAudio()
        }
    }
    func cancelRegTask() {
        DispatchQueue.main.async(execute: { [self] in
            if let recognitionTask = recognitionTask {
                recognitionTask.finish()
                recognitionTask.cancel()
                speechResult = nil
            }
            stopAudio()
            inputNode?.removeTap(onBus: AVAudioNodeBus(0))
        })
    }
    // MARK: Timer Methods
    func startTimer() {
        DispatchQueue.main.async(execute: { [self] in
            if timer == nil {
                if timerDuration != 0 {
                    timer = Timer.scheduledTimer(timeInterval: TimeInterval(timerDuration),
                                                 target: self, selector: #selector(timerAction),
                                                 userInfo: nil, repeats: false)
                } else {
                    timer = Timer.scheduledTimer(timeInterval: 2.0, target: self,
                                                 selector: #selector(timerAction),
                                                 userInfo: nil, repeats: false)
                }
            }
        })
    }
    @objc func timerAction() {
        let resultkeys = [StringConstants.Generic.action, StringConstants.Generic.text]
        var result: [Any] = []
        if jsonDict["pauseRecognize"] as? String == StringConstants.Generic.yes {
            if let speechResult = speechResult, !speechResult.isEmpty {
                result = ["PAUSED", speechResult]
            } else {
                result = ["PAUSED", ""]
            }
            speechToTextCallBack(resultKeys: resultkeys, resultvalues: result, status: true, keepAlive: false)
            invalidateTimer()
            cancelRegTask()
            cleanPlugin()
        } else {
            if let speechResult = speechResult, !speechResult.isEmpty {
                result = ["PAUSED", speechResult]
                speechToTextCallBack(resultKeys: resultkeys, resultvalues: result, status: true, keepAlive: true)
            }
            cancelRegTask()
            startTimer()
            startListening()
        }
    }
    func invalidateTimer() {
        timer?.invalidate()
        timer = nil
    }
    // MARK: Callback Methods
    func speechToTextCallBack(resultKeys: [String], resultvalues: [Any], status: Bool, keepAlive: Bool) {
        let params = APZJsonUtility.shared.createResponseJSONString(pluginId: pluginId,
                                                                    status: status,
                                                                    keepAlive: keepAlive,
                                                                    responseKeys: resultKeys,
                                                                    responseValues: resultvalues)
        MiscellaneousMethod.shared.jsLayerCall(webView: self.webView,
                                               jsFunctionName: StringConstants.Generic.jscallBackMethod,
                                               parameter: params)
    }
}
extension APZSpeechToText: SFSpeechRecognizerDelegate, SFSpeechRecognitionTaskDelegate {
    func speechRecognitionTaskWasCancelled(_ task: SFSpeechRecognitionTask) {
        print("speechRecognitionTaskWasCancelled", task)
        startListening()
    }
}
