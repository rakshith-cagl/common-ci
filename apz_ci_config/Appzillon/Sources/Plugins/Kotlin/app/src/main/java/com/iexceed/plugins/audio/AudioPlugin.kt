package com.iexceed.plugins.audio

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.Resources.NotFoundException
import android.media.*
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebView
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.*
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.FileAccessHelper
import org.json.JSONObject
import java.io.*
import java.util.*


/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class AudioPlugin private constructor (private var webView: WebView,
                  private var activity: ApzActivity<*>,
                  override val apzPluginUtil: IapzPluginUtil
                  ) : ApzPlugin()
{
    private var isAndroidQ: String? = null

    private var EXTERNAL_AUDIO_DIRECTORY = ""

    var mRecorder: MediaRecorder? = null

    var mPlayer: MediaPlayer? = null

    var mAction: String? = null

    var isPaused = false

    var mAudioFileLocation = ""

    private var mAudiofile: File? = null

    private val apzTAG = "AudioPlugin"

    var mBase64 = ""
    var mSampleRate = ""
    var mBitRate = ""
    var mChannel = ""
    private var mDuration = "20000"

    var mCallbackId: String? = null

    var mFileName = ""
    var wavFileFormat: String? = null
    private var audioTimer: Timer? = null

    lateinit var mJsonObject: JSONObject
    lateinit var permissions: Array<String>

    private var RECORDER_BPP = 16
    private val AUDIO_RECORDER_FOLDER = "Audio"
    private val AUDIO_RECORDER_TEMP_FILE = "record_temp.raw"
    private var RECORDER_SAMPLERATE = 0
    private var RECORDER_CHANNELS = 0
    private val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT

    var recorder: AudioRecord? = null
    private var bufferSize = 0
    private var recordingThread: Thread? = null
    private var isRecording = false


    private var output: String? = null

    private val errorCode095 = "APZ-CNT-095"
    private val errorCode127 = "APZ-CNT-127"
    private val errorCode278 = "APZ-CNT-278"
    private val errorCode029 = "APZ-CNT-029"

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = AudioPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isAudioPlugin(): Boolean {
            return true
        }
    }

    private fun audioPlugin(jsonObj: JSONObject)
    {
        try {
            mCallbackId = jsonObj.getString("id")
            mAction = jsonObj.getString("action")
            mBase64 = jsonObj.optString("base64")
            mSampleRate = jsonObj.optString("samplingRate")
            mBitRate = jsonObj.optString("bitRate")
            mChannel = jsonObj.optString("channel")
            mDuration = jsonObj.optString("timeDuration")
            mFileName = jsonObj.getString("fileName")
            wavFileFormat = jsonObj.getString("wavFileFormat")
            mAudioFileLocation = jsonObj.getString("location")
            if (mSampleRate.equals("", ignoreCase = true)) mSampleRate = "16000"
            if (mBitRate.equals("", ignoreCase = true)) mBitRate = "16"
            if (mChannel.equals("", ignoreCase = true)) mChannel = "mono"

            EXTERNAL_AUDIO_DIRECTORY = FileAccessHelper.getExternalFileDirFile(aActivity)
            EXTERNAL_AUDIO_DIRECTORY = EXTERNAL_AUDIO_DIRECTORY.substring(0, EXTERNAL_AUDIO_DIRECTORY.lastIndexOf("Android"))
            EXTERNAL_AUDIO_DIRECTORY += Environment.DIRECTORY_MUSIC + "/"

            requestVersionPermission()
        } catch (e: Exception) {
            ApzLogger.e(apzTAG, e.toString())
            sendErrorCallBack("APZ-CNT-077")
            return
        }
    }

    private fun requestVersionPermission() {
        if ("N".equals(activity.resources.getString(R.string.INTERNALSANDBOX), ignoreCase = true)
            || mAudioFileLocation.equals("external", ignoreCase = true)
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                verifyPermissionsBeforeTiramisu()
            } else {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.READ_MEDIA_AUDIO
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissions = arrayOf(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_MEDIA_AUDIO
                    )
                    requestForPermission()
                } else {
                    startAudioRecording()
                }
            }
        } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO
            )
            requestForPermission()
        } else {
            startAudioRecording()
        }
    }

    private fun verifyPermissionsBeforeTiramisu() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            requestForPermission()
        } else {
            startAudioRecording()
        }
    }

    private fun requestForPermission()
    {
        aActivity.startOnPermissionForResult(
            aActivity,
            permissions,
            PluginConstants.APZ_REQ_RECORD_AUDIO,
            object : OnPermissionsResultHandler() {
                override fun handlePermissionResult(
                    requestCode: Int,
                    permissions: Array<String?>,
                    grantResults: IntArray
                ) {
                    if (requestCode == PluginConstants.APZ_REQ_RECORD_AUDIO) {
                        handleAudioPermissionResponse(permissions)
                    } else {
                        permissionDeniedCallback()
                    }
                }
            }
        )
    }

    private fun handleAudioPermissionResponse(permissions: Array<String?>) {
        var denied = false
        var neverAskAgain = false
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(aActivity, permission!!)) {
                denied = true
            } else {
                if (ActivityCompat.checkSelfPermission(
                        aActivity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    neverAskAgain = true
                }
            }
        }
        when {
            neverAskAgain -> {
                permissionDeniedCallback()
            }
            denied -> {
                displayReconfirmationMessage()
            }
            else -> {
                startAudioRecording()
            }
        }
    }

    private fun displayReconfirmationMessage() {
        val message = "To record audio,allow app to access by granting permissions"
        val alertDialogBuilder = AlertDialog.Builder(aActivity)
        alertDialogBuilder.setTitle("Permission Denied")
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.cancel()
                requestForPermission()
            }.setNegativeButton("Deny") { dialog, _ ->
                dialog.cancel()
                permissionDeniedCallback()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun permissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied("Audio", mCallbackId, aActivity, aWebview)
    }

    private fun startAudioRecording()
    {
        try {
            if (mAction == "record")
            {
                if (handleActionRecord()) return
            } else if (mAction == "save")
            { // for stop also playing also
                saveAudioRecorded()
            } else if (mAction == "play")
            {
                mAudioFileLocation = try {
                    mJsonObject.getString("location")
                } catch (e: Exception) {
                    ApzLogger.e(apzTAG, e.toString())
                    sendErrorCallBack("APZ-CNT-030")
                    return
                }
                ApzLogger.d(apzTAG, " Got Location : $mAudioFileLocation")
                if (AppzillonMainScreen.recordingState) {
                    //Save then Play
                    sendErrorCallBack(errorCode029)
                    return
                }
                if (!isPaused)
                    startPlaying(mFileName)
                else resumePlay()
            } else if (mAction == "pause")
            {
                audioPause()
                //else wav error
            } else
            {
                //Invalid Action
                sendErrorCallBack("APZ-CNT-082")
            }
        } catch (e: Exception) {
            sendErrorCallBack("APZ-CNT-082")
        }
    }

    private fun handleActionRecord(): Boolean {
        if (wavFileFormat.equals("N", ignoreCase = true)) {
            try {
                isPaused = false
                mAudioFileLocation = mJsonObject.getString("location")
                if (mFileName.isEmpty() && mAudioFileLocation.isEmpty()) {
                    sendErrorCallBack("APZ-CNT-031")
                    pluginObj = null
                    return true
                }
            } catch (e: Exception) {
                sendErrorCallBack("APZ-CNT-031")
                return true
            }
            startRecording(mFileName)
        } else {
            handleRecording()
        }
        return false
    }

    private fun saveAudioRecorded() {
        if (AppzillonMainScreen.playingState) {
            releaseAudioPlayer()
            val result = JSONObject()
            result.put("event", "Paused")
            apzPluginUtil.sendSuccess(mCallbackId, result, false, aActivity, aWebview, true)
            isPaused = false
        } else {
            handleIfNotPlaying()
        }
    }

    private fun handleIfNotPlaying() {
        if (wavFileFormat.equals("N", ignoreCase = true)) {
            stopRecording()
        } else {
            stopWavRecording(mFileName, mBase64)
        }
    }

    private fun handleRecording() {
        if (ActivityCompat.checkSelfPermission(
                aActivity,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startWavRecording()
        } else {
            permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO
            )
            requestForPermission()
        }
    }

    private fun resumePlay()
    {
        ApzLogger.i(apzTAG, "Resume Play")
        try {
            if (mPlayer != null) {
                mPlayer?.start()
                isPaused = false
                val result = JSONObject()
                try {
                    result.put("event", "Playing")
                } catch (e: Exception) {
                    //Sonar fix
                }
                apzPluginUtil.sendSuccess(mCallbackId, result, false, aActivity, aWebview, true)
            }
        } catch (e: IllegalStateException) {
            ApzLogger.i(apzTAG, "Resume Failed")
            //Resume Failed
            sendErrorCallBack("APZ-CNT-093")
        }
    }

    private fun audioPause()
    {
        ApzLogger.i(apzTAG, "Audio Pause")
        if (AppzillonMainScreen.playingState) {
            mPlayer!!.pause()
            isPaused = true
            val result = JSONObject()
            try {
                result.put("event", "Paused")
            } catch (e: Exception) {
                //Sonar fix
            }
            apzPluginUtil.sendSuccess(mCallbackId, result, false, aActivity, aWebview, true)
        } else if (AppzillonMainScreen.recordingState) {
            //Save then play
            sendErrorCallBack(errorCode029)
        } else {
            //Media Player not running
            sendErrorCallBack("APZ-CNT-033")
        }
    }

    private fun startRecording(fileName: String)
    {
        ApzLogger.i(apzTAG, "Start recording")
        try {
            if (MediaUtils.isSDCardPresent) {
                if (setFileLocation(fileName)) return
            } else {
                sendErrorCallBack(errorCode127)
                //Mount SD card
                return
            }
            try {
                if (AppzillonMainScreen.playingState) {
                    //stop and then record
                    sendErrorCallBack(errorCode029)
                    return
                }
                if (!AppzillonMainScreen.recordingState)
                {
                    handleAudioRecording()
                } else {
                    sendErrorCallBack(errorCode029)
                    return
                }
            } catch (e: IOException) {
                sendErrorCallBack(errorCode127)
                return
            } catch (e: Exception) {
                //time duration and sampling rate mismatch causes Exception (check value)
                sendErrorCallBack(errorCode095)
                ApzLogger.e(apzTAG, "AudioPlugin Exception: $e")
                e.printStackTrace()
            }
        } catch (e: Exception) {
            sendErrorCallBack(errorCode095)
            e.printStackTrace()
            ApzLogger.e(apzTAG, "AudioPlugin Exception: $e")
        }
    }

    private fun handleAudioRecording() {
        val sampling: Int = mSampleRate.toInt()
        if (Build.VERSION.SDK_INT >= 31) {
            mRecorder = MediaRecorder(activity)
            mRecorder?.reset()
        } else {
            mRecorder = MediaRecorder()
        }
        mRecorder!!.setOnInfoListener { _, what, _ ->
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                stopRecording()
            }
        }
        mRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)

        if (!mDuration.equals("", ignoreCase = true)) {
            mRecorder?.setMaxDuration(mDuration.toInt())
        }

        ApzLogger.d(apzTAG, " MFileName $mFileName")
        val lFile = File(mFileName)
        if (lFile.exists() && !lFile.delete()) {
            //handle if not deleted
        }
        mRecorder?.setOutputFile(mFileName)

        mRecorder?.setAudioEncodingBitRate(mBitRate.toInt())
        if (mChannel.equals("mono", ignoreCase = true)) {
            mRecorder?.setAudioChannels(1)
        } else if (mChannel.equals("stereo", ignoreCase = true)) {
            mRecorder?.setAudioChannels(2)
        }
        if (sampling == 8000) {
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mRecorder!!.setAudioSamplingRate(sampling)
        } else if (sampling == 16000) {
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
            mRecorder!!.setAudioSamplingRate(sampling)
        } else if (sampling in 8001..95999) {
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            mRecorder!!.setAudioSamplingRate(sampling)
        } else {
            mRecorder!!.setAudioSamplingRate(16000)
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
        }
        mRecorder!!.prepare()
        mRecorder!!.start()
        AppzillonMainScreen.recordingState = true
        sendEventStartedCallback()
    }

    private fun setFileLocation(fileName: String): Boolean {
        when {
            mAudioFileLocation.lowercase().equals("external", ignoreCase = true) -> {
                val sampleDir = File(EXTERNAL_AUDIO_DIRECTORY)
                mFileName = sampleDir.absolutePath + "/" + fileName + ".mp3"
                ApzLogger.d(apzTAG, "fINAL FILE PATH rec$mFileName")
            }
            mAudioFileLocation.equals("default", ignoreCase = true) -> {
                mFileName =
                    AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + "audio"
                val directory: File = AppzillonUtils.getApzFile(mFileName, null)
                directory.mkdirs()
                mFileName += "/$fileName.mp3"
                ApzLogger.d(apzTAG, "FILE PATH Audio$mFileName")
            }
            else -> {
                //Location value can be external or default
                sendErrorCallBack(errorCode278)
                return true
            }
        }
        return false
    }

    private fun sendEventStartedCallback() {
        val result = JSONObject()
        try {
            result.put("event", activity.getString(R.string.audio_recording_started))
        } catch (e: Exception) {
            //Sonar fix
        }
        apzPluginUtil.sendSuccess(mCallbackId, result, true, aActivity, aWebview, true)
    }

    private fun sendErrorCallBack(s: String) {
        apzPluginUtil.sendError(mCallbackId, s, null, aActivity, aWebview, true)
    }

    private fun stopRecording()
    {
        ApzLogger.d(apzTAG, "Stop Recording")
        try {
            if (mRecorder != null) {
                releaseMediaRecorder()
                processAudioFile()
                if (mBase64.equals("Y", ignoreCase = true))
                {
                    val result = JSONObject()
                    try {
                        result.put("event", activity.getString(R.string.audio_stopped_and_saved))
                        apzPluginUtil.sendSuccess(
                            mCallbackId, result, false,
                            aActivity, aWebview, true)
                    } catch (nfe: NotFoundException) {
                        ApzLogger.e(apzTAG, nfe.message!!)
                    } catch (e: Exception) {
                        //Sonar fix
                    }
                } else {
                    ApzLogger.d(apzTAG, "stopRecording : mBase64 : "+mBase64)
                    val result = JSONObject()
                    try {
                        result.put("event", activity.getString(R.string.audio_stopped_and_saved))
                    } catch (ex: Exception) {
                        ex.message
                    }
                    apzPluginUtil.sendSuccess(mCallbackId, result, false, aActivity, aWebview, true)
                }
                return
            }
        } catch (i: IllegalStateException) {
            //Save Failed
            sendErrorCallBack(errorCode095)
        }
    }

    /**
     * Process the recorded audio
     */
    private fun processAudioFile()
    {
        ApzLogger.i(apzTAG, "Process Audio")
        val audiofile = File(mFileName)
        val newUri: Uri?
        if (isAndroidQ.equals("N", ignoreCase = true)) {
            val values = ContentValues(3)
            val current = System.currentTimeMillis()
            values.put(MediaStore.Audio.Media.TITLE, audiofile.name)
            values.put(MediaStore.Audio.Media.DATE_ADDED, (current / 1000).toInt())
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3")
            values.put(MediaStore.Audio.Media.DATA, audiofile.absolutePath)
            val contentResolver = aActivity.contentResolver
            val base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            newUri = contentResolver.insert(base, values)
            MediaScannerConnection.scanFile(aActivity, arrayOf(File(newUri.toString()).absolutePath), null) { _, _ ->
                // Scanning is complete, and the file is now available in the media database
                Log.d("TAG","SUCCESS")
            }
        }
    }

    @Throws(IllegalStateException::class)
    fun releaseMediaRecorder()
    {
        ApzLogger.i(apzTAG, "Release Media Recorder")
        if (mRecorder != null) {
            mRecorder!!.stop()
            mRecorder!!.release()
            mRecorder = null
        }
        AppzillonMainScreen.recordingState = false
    }

    private fun startPlaying(fileName: String)
    {
        try {
            ApzLogger.i(apzTAG, "Start Playing")
            if (AppzillonMainScreen.playingState) {
                //Audio Play on
                sendErrorCallBack(errorCode029)
                return
            }
            if (MediaUtils.isSDCardPresent) {
                if (handleWhenSDCardisPresent(fileName)) return
            } else {
                //SD Card unavailable
                sendErrorCallBack(errorCode127)
                return
            }
            /* create media player when it is null */
            if (mPlayer == null) {
                mPlayer = MediaPlayer()
            }
            try {
                mPlayer!!.setOnCompletionListener(OnCompletionListener {
                    ApzLogger.d(apzTAG, " Inside OnCompletionListener ")
                    val result = JSONObject()
                    result.put("event", "Play completed")
                    apzPluginUtil.sendSuccess(mCallbackId, result, false, aActivity, aWebview, true)
                    isPaused = false
                    releaseAudioPlayer()
                })
                mPlayer?.setDataSource(mFileName)
                mPlayer!!.prepare()
                mPlayer!!.start()
                AppzillonMainScreen.playingState = true
                val result = JSONObject()
                result.put("event", "Playing")
                apzPluginUtil.sendSuccess(mCallbackId, result, true, aActivity, aWebview, true)
                ApzLogger.d(apzTAG, "Playing Recorded File")
            } catch (e: FileNotFoundException) {
                //File not found
                sendErrorCallBack("APZ-CNT-002")
                releaseAudioPlayer()
                return
            } catch (e: IOException) {
                //SD card unavailable
                sendErrorCallBack(errorCode127)
                releaseAudioPlayer()
            }
        } catch (e: Exception) {
            //SD card unavailable
            sendErrorCallBack(errorCode127)
            ApzLogger.e(apzTAG, "prepare() failed " + e.message)
            releaseAudioPlayer()
        }
    }

    private fun handleWhenSDCardisPresent(fileName: String): Boolean {
        if (mAudioFileLocation.equals("external", ignoreCase = true)) {
            if (handleExternalFileLocation(fileName)) return true
        } else if (mAudioFileLocation.equals("default", ignoreCase = true)) {
            mFileName =
                AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + "audio"
            val directory: File = AppzillonUtils.getApzFile(mFileName, null)
            directory.mkdirs()
            mFileName += if (wavFileFormat.equals(
                    "Y",
                    ignoreCase = true
                )
            ) "/$fileName.wav" else "/$fileName.mp3"
            ApzLogger.d("FILE PATH Play", mFileName)
        } else {
            //Location value can be external or default
            sendErrorCallBack(errorCode278)
            return true
        }
        return false
    }

    private fun handleExternalFileLocation(fileName: String): Boolean {
        val sampleDir: File
        try {
            if (wavFileFormat.equals("Y", ignoreCase = true)) {
                val filepath: String = EXTERNAL_AUDIO_DIRECTORY
                mAudiofile = File(filepath, "$fileName.wav")
            } else {
                sampleDir = File(EXTERNAL_AUDIO_DIRECTORY)
                mAudiofile = File(sampleDir, "$fileName.mp3")
            }
        } catch (e: NullPointerException) {
            ApzLogger.e(apzTAG, "sdcard access error")
            return true
        }
        mFileName = mAudiofile!!.absolutePath
        ApzLogger.d(apzTAG, "FILE PATH EXTERNAL Play $mFileName")
        return false
    }

    @Throws(IllegalStateException::class)
    private fun releaseAudioPlayer()
    {
        ApzLogger.i(apzTAG, "releaseAudioPlayer")
        if (mPlayer != null) {
            AppzillonMainScreen.playingState = false
            mPlayer!!.stop()
            mPlayer!!.release()
            mPlayer = null
        }
    }
    
    override fun execute(params: JSONObject) {
        mJsonObject = params
        audioPlugin(params)
    }

    ///////////////////////////////////////////////////wav format classssssss//////////////
    private fun getFilename(): String {
        return try {
            var file: File? = null
            var filepath: String? = ""
            if (mAudioFileLocation.equals("external", ignoreCase = true)) {
                filepath = EXTERNAL_AUDIO_DIRECTORY
                file = File(filepath, AUDIO_RECORDER_FOLDER)
                ApzLogger.d(apzTAG, "FILE PATH EXTERNAL rec$mFileName")
            } else if (mAudioFileLocation.equals("default", ignoreCase = true)) {
                filepath =
                    AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + "audio"
                file = File(filepath)
                file.mkdirs()
                ApzLogger.d(apzTAG, "FILE PATH Audio$mFileName")
            } else {
                //if location other than external or dafault Location value can be external or default
                sendErrorCallBack(errorCode278)
            }
            if (!file!!.exists()) {
                file.mkdirs()
            }
            file.absolutePath + "/" + output
        } catch (e: Exception) {
            ""
        }
    }

    private fun getTempFilename(): String {
        val filepath = FileAccessHelper.getExternalFileDirFileWithType(aActivity)
        val file = File(filepath, AUDIO_RECORDER_FOLDER)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + AUDIO_RECORDER_TEMP_FILE
    }

    @RequiresPermission("android.permission.RECORD_AUDIO")
    private fun startWavRecording()
    {
        try {
            RECORDER_SAMPLERATE = mSampleRate.toInt()
            if (mChannel.equals("mono", ignoreCase = true)) {
                RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
            } else if (mChannel.equals("stereo", ignoreCase = true)) {
                RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO
            }
            RECORDER_BPP = mBitRate.toInt()
            bufferSize = AudioRecord.getMinBufferSize(
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING
            ) * 3

            recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, bufferSize)

            val i = recorder!!.state
            if (i == 1) {
                if (!mDuration.equals(
                        "",
                        ignoreCase = true)) startWavTimer(mDuration.toInt())
                recorder!!.startRecording()
            }
            isRecording = true
            val result = JSONObject()
            try {
                result.put("event", activity.getString(R.string.audio_recording_started))
            } catch (e: Exception) {
                //Sonar fix
            }
            apzPluginUtil.sendSuccess(mCallbackId, result, true, aActivity, aWebview, true)
            recordingThread = Thread({ writeAudioDataToFile() }, "AudioRecorder Thread")
            recordingThread!!.start()
        } catch (e: Exception) {
            sendErrorCallBack(errorCode029)
            //Still Recording
        }
    }

    private fun writeAudioDataToFile()
    {
        val data = ByteArray(bufferSize)
        val filename = getTempFilename()
        var os: FileOutputStream? = null
        try {
            os = FileAccessHelper.getFileOutPutStream(File(filename))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        var read: Int
        if (null != os) {
            while (isRecording) {
                read = recorder!!.read(data, 0, bufferSize)

                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            try {
                os.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun startWavTimer(duration: Int) {
        try {
            if (audioTimer != null) {
                audioTimer?.cancel()
                audioTimer = null
            }
            audioTimer = Timer(true)
            audioTimer?.schedule(object : TimerTask() {
                override fun run() {
                    stopWavRecording(mFileName, mBase64)
                }
            }, duration.toLong())
        } catch (e: Exception) {
            //Sonar fix
        }
    }

    fun stopWavRecording(fileName: String, isBase64: String) {
        try {
            output = "$fileName.wav"
            if (null != recorder) {
                isRecording = false
                val i = recorder!!.state
                if (i == 1) recorder!!.stop()
                recorder!!.release()
                recorder = null
                recordingThread = null
            }
            AppzillonMainScreen.recordingState = false
            copyWaveFile(getTempFilename(), getFilename())
            deleteTempFile()
            if (isBase64.equals("Y", ignoreCase = true)) {
                //Sonar fix
                val result1 = JSONObject()
                result1.put("event", activity.getString(R.string.audio_stopped_and_saved))
                apzPluginUtil.sendSuccess(mCallbackId, result1, false, aActivity, aWebview, true)
            } else {
                val result = JSONObject()
                result.put("event", activity.getString(R.string.audio_stopped_and_saved))
                apzPluginUtil.sendSuccess(mCallbackId, result, false, aActivity, aWebview, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteTempFile() {
        val file = File(getTempFilename())
        try {
            if (!file.delete()) {
                //handle if not deleted
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun copyWaveFile(inFilename: String, outFilename: String) {
        var `in`: FileInputStream? = null
        var out: FileOutputStream? = null
        var totalAudioLen: Long = 0
        var totalDataLen = totalAudioLen + 36
        val longSampleRate: Long = RECORDER_SAMPLERATE.toLong()
        val channels = if (RECORDER_CHANNELS == AudioFormat.CHANNEL_IN_MONO) 1 else 2
        val byteRate: Long = (RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8).toLong()
        val data = ByteArray(bufferSize)
        try {
            `in` = FileInputStream(inFilename)
            out = FileAccessHelper.getFileOutPutStream(File(outFilename))
            totalAudioLen = `in`.channel.size()
            totalDataLen = totalAudioLen + 36
            WriteWaveFileHeader(
                out,
                totalAudioLen,
                totalDataLen,
                longSampleRate,
                channels,
                byteRate)
            while (`in`.read(data) != -1) {
                out.write(data)
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            `in`?.close()
            out?.close()
        }
    }

    @Throws(IOException::class)
    private fun WriteWaveFileHeader(
        out: FileOutputStream,
        totalAudioLen: Long,
        totalDataLen: Long,
        longSampleRate: Long,
        channels: Int,
        byteRate: Long
    ) {
        val header = ByteArray(44)
        header[0] = 'R'.code.toByte() // RIFF/WAVE header
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xff).toByte()
        header[25] = (longSampleRate shr 8 and 0xff).toByte()
        header[26] = (longSampleRate shr 16 and 0xff).toByte()
        header[27] = (longSampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        header[32] =
            ((if (RECORDER_CHANNELS == AudioFormat.CHANNEL_IN_MONO) 1 else 2) * 16 / 8).toByte() // block align
        header[33] = 0
        header[34] = RECORDER_BPP.toByte() // bits per sample
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        out.write(header, 0, 44)
    }


    init {
        super.aActivity = activity
        super.aWebview = webView
    }

}
