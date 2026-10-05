//
//  AudioHelper.swift
//  Appzillon
//  Created by Bhavya V on 10/11/21.

import Foundation
import UIKit
import AVFoundation

public enum AudioStatus: Int, RawRepresentable {
    case success
    case failure
    case none
}

protocol AudioResponseDelegate: AnyObject {
    func sendStatus(status: AudioStatus, message: String)
}
public class AudioRecorderAndPlayerResult: NSObject {
    var status: AudioStatus = AudioStatus.none
    var message: String = StringConstants.Generic.emptyString
    init(status: AudioStatus, message: String) {
        self.status = status
        self.message = message
    }
}

class AudioHelper: NSObject, AVAudioPlayerDelegate, AVAudioRecorderDelegate {
    var audioRecorder: AVAudioRecorder?
    var audioPlayer: AVAudioPlayer?
    var isRecorderPaused: Bool = false
    var isPlayerPaused: Bool = false
    let audioSession = AVAudioSession.sharedInstance()
    var recordTimeInterval: TimeInterval = 0
    var jsonDict: [AnyHashable: Any] = [:]
    var appString: String = StringConstants.Generic.emptyString
    var audioFileName: String = StringConstants.Generic.emptyString
    var createAudioFolder: Bool = false
    var timeDuration: String = StringConstants.Generic.emptyString
    var isTimerEnabled: Bool = false
    var defaultAudioFileName: String = StringConstants.Generic.emptyString
    var audioFileExtension: String = StringConstants.Generic.emptyString
    var audioFileUrl: URL!
    weak var audioDelegate: AudioResponseDelegate?
    init(appString: String) {
        super.init()
        self.appString = appString
    }
    // MARK: Record functionality
    func audioRecord(jsonDict: [AnyHashable: Any]) -> AudioRecorderAndPlayerResult {
        self.jsonDict = jsonDict
        timeDuration = jsonDict[StringConstants.Audio.timeDuration] as? String ?? StringConstants.Generic.emptyString
        recordTimeInterval = TimeInterval(timeDuration) ?? TimeInterval(StringConstants.Audio.maxTimeLimit)
        defaultAudioFileName = StringConstants.Generic.appzillon + getTimeStampForFileName()
        audioFileName = jsonDict[StringConstants.Generic.fileName] as? String ??  defaultAudioFileName
        audioFileExtension = getAudioExtension()
        // If audio is playing, then stop and start recording
        if let player = audioPlayer {
            if player.isPlaying || isPlayerPaused {
                player.stop()
            } else {
                APZLogger.log(logLvl: "I", message: "APZAudio--Audio Player is not playing")
            }
        }
        if let recorder = audioRecorder {
            isRecorderPaused = false
            print("Recording!!")
            recorder.record(forDuration: recordTimeInterval)
            APZLogger.log(logLvl: "I", message: "APZAudio--Media Record Resumed")
            return AudioRecorderAndPlayerResult(status: .success, message: StringConstants.Generic.emptyString)
        } else {
            return setupAndRecordNewAudio()
        }
    }
    fileprivate func setupAndRecordNewAudio() -> AudioRecorderAndPlayerResult {
        audioRecorderConfig(jsonDict: jsonDict)
        if let recorder = audioRecorder, !recorder.isRecording {
            do {
                try audioSession.setActive(true)
                recorder.record(forDuration: recordTimeInterval)
                isTimerEnabled = true
                isRecorderPaused = false
                APZLogger.log(logLvl: "I", message: "APZAudio--Media Record Started")
                return AudioRecorderAndPlayerResult(status: .success, message: StringConstants.Generic.emptyString)
            } catch {
                APZLogger.log(logLvl: "E", message: "APZAudio--Media Record Failed \(error)")
                return AudioRecorderAndPlayerResult(status: .failure, message: AUDIO_EXCEPTION_RECORDFAIL)
            }
        } else {
            APZLogger.log(logLvl: "E", message: "APZAudio--Failed to create an instance of the audio recorder")
            return AudioRecorderAndPlayerResult(status: .failure, message: AUDIO_EXCEPTION_RECORDFAIL)
        }
    }
    fileprivate func audioRecorderConfig(jsonDict: [AnyHashable: Any]) {
        let channelName: String = jsonDict[StringConstants.Audio.channel] as? String ??
            StringConstants.Generic.emptyString
        let audioChannel: Int = (channelName == StringConstants.Audio.stereo) ? 2 : 1
        let samplingRate: Double = jsonDict[StringConstants.Audio.samplingRate] as? Double ?? 44100.0
        let bitRate: Int = jsonDict[StringConstants.Audio.bitRate] as? Int ?? 16
        do {
            createAudioFolder = true
            let audioDirectoryPath = getAudioDirectoryPath()
            audioFileUrl = getAudioPathWithExtension(audioDirecotry: audioDirectoryPath)
            // Setting for recorder
            let recordSettings = [AVEncoderAudioQualityKey: AVAudioQuality.min.rawValue,
                                  AVEncoderBitRateKey: bitRate,
                                  AVNumberOfChannelsKey: audioChannel,
                                  AVSampleRateKey: samplingRate] as [String: Any]
            try audioSession.setCategory(AVAudioSession.Category.playAndRecord)
            try audioRecorder = AVAudioRecorder(url: audioFileUrl, settings: recordSettings as [String: AnyObject])
            audioRecorder?.delegate = self
        } catch {
            print("Issue during the initial setup: \(error)")
        }
    }
    func audioRecorderDidFinishRecording(_ recorder: AVAudioRecorder, successfully flag: Bool) {
        if flag {
            if !isTimerEnabled {
                APZLogger.log(logLvl: "I", message: "APZAudio--Audio Recorded Saved succesfully")
                audioDelegate?.sendStatus(status: .success, message: audioFileUrl.path)
            } else {
                saveTimerAudio(jsonDict)
            }
        } else {
            APZLogger.log(logLvl: "E", message: "APZAudio--Media Record Failed")
            audioDelegate?.sendStatus(status: .failure, message: AUDIO_EXCEPTION_RECORDFAIL)
        }
    }
    func audioRecorderEncodeErrorDidOccur(_ recorder: AVAudioRecorder, error: Error?) {
        APZLogger.log(logLvl: "E", message: "APZAudio--Media Record Encoding Error")
        audioDelegate?.sendStatus(status: .failure, message: AUDIO_EXCEPTION_ENCODEFAIL)
    }
    // MARK: Save functionality
    func audioSave(jsonDict: [AnyHashable: Any]) {
        self.jsonDict = jsonDict
        if let recorder = audioRecorder, recorder.isRecording || isRecorderPaused {
            do {
                isRecorderPaused = false
                isTimerEnabled = false
                recorder.stop()
                try audioSession.setActive(false)
                if jsonDict[StringConstants.Generic.base64] as?
                    String == StringConstants.Generic.yes {
                    let audioBase64 = convertAudioToBase64()
                    audioDelegate?.sendStatus(status: audioBase64.status, message: audioBase64.message)
                }
            } catch {
                APZLogger.log(logLvl: "E", message: "APZAudio--Error occured while saving the audio \(error)")
            }
        } else if let player = audioPlayer, player.isPlaying || isPlayerPaused {
            do {
                isTimerEnabled = false
                player.stop()
                isPlayerPaused = false
                try audioSession.setActive(false)
                if jsonDict[StringConstants.Generic.base64] as?
                    String == StringConstants.Generic.yes {
                    let audioBase64 = convertAudioToBase64()
                    audioDelegate?.sendStatus(status: audioBase64.status, message: audioBase64.message)
                }
            } catch {
                APZLogger.log(logLvl: "E", message: "APZAudio--Error occured while saving the audio \(error)")
            }
        } else {
            errorWhileSaving()
        }
    }
    fileprivate func saveTimerAudio(_ jsonDict: [AnyHashable: Any]) {
        do {
            isRecorderPaused = false
            isPlayerPaused  = false
            audioFileName = jsonDict[StringConstants.Generic.fileName] as? String ?? defaultAudioFileName
            let audioDirectoryPath = getAudioDirectoryPath()
            let audioFileURL = getAudioPathWithExtension(audioDirecotry: audioDirectoryPath)
            isTimerEnabled = false
            try audioSession.setActive(false)
            if let recorder = audioRecorder {
                recorder.stop()
            }
            if let player = audioPlayer {
                player.stop()
            }
            if jsonDict[StringConstants.Generic.base64] as?
                String == StringConstants.Generic.yes {
                let result: AudioRecorderAndPlayerResult
                result = convertAudioToBase64()
                audioDelegate?.sendStatus(status: result.status, message: result.message)
            } else {
                audioDelegate?.sendStatus(status: .success, message: audioFileURL.path)
            }
        } catch {
            APZLogger.log(logLvl: "E", message: "APZAudio--Error occured while saving the audio with time\(error)")
            audioDelegate?.sendStatus(status: .failure, message: AUDIO_EXCEPTION_RECORDFAIL)
        }
    }
    fileprivate func convertAudioToBase64() -> AudioRecorderAndPlayerResult {
        do {
            let audioDirectoryPath = getAudioDirectoryPath()
            let audioFileURL = getAudioPathWithExtension(audioDirecotry: audioDirectoryPath)
            let audioData =  try Data(contentsOf: audioFileURL)
            let encodedAudioString = audioData.base64EncodedString()
            return AudioRecorderAndPlayerResult(status: .success, message: encodedAudioString)
        } catch {
            APZLogger.log(logLvl: "E", message: "APZAudio--Error occured while converting audio to base64 \(error)")
            return AudioRecorderAndPlayerResult(status: .failure, message: StringConstants.Generic.emptyString)
        }
    }
    fileprivate func errorWhileSaving() {
        isPlayerPaused = false
        isRecorderPaused = false
        APZLogger.log(logLvl: "E", message: "APZAudio--No Recording or Playing occuring")
        audioDelegate?.sendStatus(status: .failure, message: AUDIO_EXCEPTION_NO_REC_PLAY)
    }
    // MARK: Play functionality
    func audioPlay(jsonDict: [AnyHashable: Any]) {
        self.jsonDict = jsonDict
        audioFileName = jsonDict[StringConstants.Generic.fileName] as? String ??  defaultAudioFileName
        if let recorder = audioRecorder, recorder.isRecording || isRecorderPaused {
            APZLogger.log(logLvl: "I", message: "APZAudio--recording is happening,stop it for recording")
            audioDelegate?.sendStatus(status: .failure, message: AUDIO_EXCEPTION_RECBUSY)
        } else {
            if let player = audioPlayer {
                isPlayerPaused = false
                isPlayerPaused = false
                player.play()
                APZLogger.log(logLvl: "I", message: "APZAudio--Audio Player Resumed")
                audioDelegate?.sendStatus(status: .success, message: StringConstants.Generic.emptyString)
            } else {
                playNewOrRecentAudio()
            }
        }
    }
    private func playNewOrRecentAudio() {
        audioFileName = jsonDict[StringConstants.Generic.fileName] as? String ??  defaultAudioFileName
        let audioDirectoryPath = getAudioDirectoryPath()
        let audioFileURL = getAudioPathWithExtensionToPlay(documentsDirectory: audioDirectoryPath)
        let fileManager = FileManager.default
        if fileManager.fileExists(atPath: audioFileURL.path) {
            audioPlayer = try? AVAudioPlayer(contentsOf: audioFileURL)
            audioPlayer?.delegate = self
            isPlayerPaused = false
            isRecorderPaused = false
            if let player = audioPlayer {
                if player.play() {
                    APZLogger.log(logLvl: "I", message: "APZAudio--Playing audio")
                } else {
                    APZLogger.log(logLvl: "E", message: "APZAudio--Failed to play Audio")
                    audioDelegate?.sendStatus(status: .failure, message: AUDIO_EXCEPTION_PLAYFAIL)
                }
            } else {
                APZLogger.log(logLvl: "E", message: "APZAudio--Media play instance creation failed")
                audioDelegate?.sendStatus(status: .failure, message: AUDIO_EXCEPTION_PLAYFAIL)
            }
        } else {
            APZLogger.log(logLvl: "E", message: "APZAudio--Media Play File not Found")
            audioDelegate?.sendStatus(status: .failure, message: FILE_NOT_FOUND_ERROR)
        }
    }
    func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        APZLogger.log(logLvl: "I", message: "APZAudio--Successfully Played Audio")
        audioDelegate?.sendStatus(status: .success, message: StringConstants.Generic.emptyString)
    }
    func audioPlayerDecodeErrorDidOccur(_ player: AVAudioPlayer, error: Error?) {
        APZLogger.log(logLvl: "E", message: "APZAudio--Media player Decode Error")
        audioDelegate?.sendStatus(status: .failure, message: AUDIO_EXCEPTION_DECODEFAIL)
    }
    // MARK: Pause functionality
    func audioPause(jsonDict: [AnyHashable: Any]) -> AudioRecorderAndPlayerResult {
        self.jsonDict = jsonDict
        if let recorder = audioRecorder, recorder.isRecording {
            recorder.pause()
            isRecorderPaused = true
            return AudioRecorderAndPlayerResult(status: .success, message: StringConstants.Generic.emptyString)
        } else if let audioPlayer = audioPlayer, audioPlayer.isPlaying {
            audioPlayer.pause()
            isPlayerPaused = true
            return AudioRecorderAndPlayerResult(status: .success, message: StringConstants.Generic.emptyString)
        } else {
            APZLogger.log(logLvl: "E", message: "APZAudio--Error while pausing the audio")
            return AudioRecorderAndPlayerResult(status: .failure, message: AUDIO_EXCEPTION_NO_REC_PLAY)
        }
    }
    // MARK: Utility methods
    fileprivate func getAudioExtension() -> String {
        let wavFileFormat: String = self.jsonDict[StringConstants.Audio.wavFileFormat] as?
            String ?? StringConstants.Generic.emptyString
        let fileFormat: String = (wavFileFormat == StringConstants.Generic.yes) ?
            StringConstants.Audio.wav : StringConstants.Audio.m4a
        return fileFormat
    }
    fileprivate func getAudioDirectoryPath() -> URL {
        let urls = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask)
        var documentsDirectory = urls[0]
        let audioDirectory = StringConstants.Audio.audioDirectory
        documentsDirectory.appendPathComponent("\(StringConstants.Generic.sandBoxPath)\(appString)\(audioDirectory)")
        return documentsDirectory
    }
    fileprivate func getAudioPathWithExtension(audioDirecotry: URL) -> URL {
        var audioFileURL: URL!
        do {
            if createAudioFolder {
                let fileMngr = FileManager.default
                try fileMngr.createDirectory(at: audioDirecotry, withIntermediateDirectories: true, attributes: nil)
                createAudioFolder.toggle()
            }
            let audioFileNameWithExtension = audioFileName + StringConstants.Generic.dot + audioFileExtension
            audioFileURL = audioDirecotry.appendingPathComponent(audioFileNameWithExtension)
            print("audioFileURL: \(String(describing: audioFileURL))")
        } catch {
            APZLogger.log(logLvl: "E", message: "Error while accessing audio path \(error)")
        }
        return audioFileURL
    }
    fileprivate func getAudioPathWithExtensionToPlay(documentsDirectory: URL) -> URL {
        var audioFileURL: URL!
        var audioFileNameWithExtension = audioFileName + StringConstants.Generic.dot + StringConstants.Audio.wav
        audioFileURL = documentsDirectory.appendingPathComponent(audioFileNameWithExtension)
        if !FileManager.default.fileExists(atPath: audioFileURL.path) {
            audioFileNameWithExtension = audioFileName + StringConstants.Generic.dot + StringConstants.Audio.m4a
            audioFileURL = documentsDirectory.appendingPathComponent(audioFileNameWithExtension)
        }
        return audioFileURL
    }
    fileprivate func getTimeStampForFileName() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = StringConstants.Audio.dateFormat
        let timeStamp = dateFormatter.string(from: Date())
        return timeStamp
    }
}
