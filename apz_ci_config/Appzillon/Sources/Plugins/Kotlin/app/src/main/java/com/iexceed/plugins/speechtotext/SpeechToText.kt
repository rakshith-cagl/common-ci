package com.iexceed.plugins.speechtotext

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.ApzActivity
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.auditlog.AuditLog.sendToJSON
import com.iexceed.plugins.errorlog.ApzLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

internal class SpeechToText private constructor(
    val webView: WebView,
    val activity: ApzActivity<*>,
    override val apzPluginUtil: IapzPluginUtil
) :
    ApzPlugin(), RecognitionListener, CoroutineScope {

    var speech: SpeechRecognizer? = null
    private var recognizerIntent: Intent? = null
    override var TAG = "SpeechToText"
    private var isRecording = false
    private var mParams: JSONObject? = null
    private var action: String? = null
    private var lCode = ""
    private var isPauseRecognize = ""
    private var speechTimer: Timer? = null
    var supportedLanguages: List<String>? = null
    private val errorCode203 = "APZ-CNT-203"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    fun startVoiceConversion(voiceJson: JSONObject?) {
        try {
            callbackId = voiceJson!!.getString("id")
            action = voiceJson.getString("action")
            lCode = voiceJson.getString("languageCode")
            isPauseRecognize = voiceJson.optString("pauseRecognize")
            lWords = ""
            appendWords = ""
            if (supportedLanguages != null) {
                handleLanguages()
            } else {
                isRecording = false
                if (speech != null) {
                    speech!!.stopListening()
                    speech!!.destroy()
                }
                apzPluginUtil.sendError(
                    callbackId, "APZ-CNT-077", null,
                    this.activity, this.webView, true
                )
            }
        } catch (e: Exception) {
            ApzLogger.e(TAG, e.toString())
            isRecording = false
            if (speech != null) {
                speech!!.stopListening()
                speech!!.destroy()
            }
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", null,
                this.activity, this.webView, true
            )
        }
    }

    private fun handleLanguages() {
        if (!supportedLanguages!!.contains(lCode)) {
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-331", null,
                this.activity, this.webView, true
            )
        } else {
            handleActions()
        }
    }

    private fun handleActions() {
        if (action.equals("stop", ignoreCase = true)) {
            if (speech != null) {
                isRecording = false
                speech!!.stopListening()
                speech!!.destroy()
            }
            if (speechTimer != null) {
                speechTimer!!.cancel()
                speechTimer = null
            }
            successCallback("STOPPED", "")
        } else if (action.equals("start", ignoreCase = true)) {
            speechRecognizerMethod()
            successCallbackKeepAlive("STARTED", "")
            if (isPauseRecognize.equals("Y", ignoreCase = true)) {
                startSpeechTimer()
            }
        } else if (action.equals("resume", ignoreCase = true)) {
            speechRecognizerMethod()
            successCallbackKeepAlive("RESUMED", "")
            if (isPauseRecognize.equals("Y", ignoreCase = true)) {
                startSpeechTimer()
            }
        }
    }

    fun startSpeechTimer() {
        try {
            if (speechTimer != null) {
                speechTimer!!.cancel()
                speechTimer = null
            }
            speechTimer = Timer(true)
            speechTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    if (speech != null) {
                        isRecording = false
                        speech!!.destroy()
                    }
                    if (!appendWords.equals("", ignoreCase = true) && !appendWords.equals(
                            lWords,
                            ignoreCase = true
                        )
                    ) {
                        appendWords = "$appendWords $lWords"
                        successCallback("PAUSED", appendWords)
                    } else {
                        successCallback("PAUSED", lWords)
                    }
                }
            }, 2000)
        } catch (e: Exception) {
            //Sonar fox
        }
    }

    fun speechRecognizerMethod() {
        //speechrecognizer intent
        try {
            speech = SpeechRecognizer.createSpeechRecognizer(activity.applicationContext)
            speech!!.setRecognitionListener(this)
            recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            recognizerIntent!!.putExtra(
                RecognizerIntent.EXTRA_CALLING_PACKAGE,
                activity.applicationContext.packageName
            )
            recognizerIntent!!.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, lCode)
            recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            recognizerIntent!!.putExtra(RecognizerIntent.EXTRA_RESULTS, 5)
            recognizerIntent!!.putExtra("android.speech.extra.DICTATION_MODE", true)
            if (!isRecording) {
                ApzLogger.i(TAG, "startVoiceConversion: startListening")
                if (appendWords.equals("", ignoreCase = true)) appendWords = lWords
                if (!appendWords.equals("", ignoreCase = true) && !appendWords.equals(
                        lWords,
                        ignoreCase = true
                    )
                ) {
                    appendWords = "$appendWords $lWords"
                }
                speech!!.startListening(recognizerIntent)
                isRecording = true
            } else {
                ApzLogger.i(TAG, "startVoiceConversion: " + "   stopListening")
                speech!!.stopListening()
                isRecording = false
            }
        } catch (ex: Exception) {
            ApzLogger.e(TAG, "speechRecognizerMethod: $ex")
            apzPluginUtil.sendError(
                callbackId, errorCode203, null, this.activity,
                this.webView, true
            )
        }
    }

    override fun onBeginningOfSpeech() {
        lWords = ""
        ApzLogger.i(TAG, "onBeginningOfSpeech")
    }

    override fun onBufferReceived(buffer: ByteArray) {
        //Sonar fix
    }

    override fun onEndOfSpeech() {
        ApzLogger.i(TAG, "onEndOfSpeech")
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        ApzLogger.i(TAG, "onEvent")
    }

    override fun onPartialResults(partialResults: Bundle) {
        try {
            ApzLogger.i(TAG, "onPartialResults")
            val matches = partialResults
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            ApzLogger.i(TAG, "startVoiceConversion: " + "onPartialResults   VALUE " + matches!![0])
            if (isPauseRecognize.equals("Y", ignoreCase = true)) {
                if (!matches[0].equals("", true)) {
                    lWords = matches[0]
                    startSpeechTimer()
                }
            } else {
                if (!matches[0].equals("", true)) {
                    lWords = matches[0]
                }
            }
            ApzLogger.i(TAG, "onPartialResults")
        } catch (ex: Exception) {
            if (speech != null) {
                speech!!.stopListening()
                speech!!.destroy()
            }
            ApzLogger.e(TAG, "speechRecognizerMethod: $ex")
            apzPluginUtil.sendError(
                callbackId, errorCode203, null, this.activity,
                this.webView, true
            )
        }
    }

    override fun onReadyForSpeech(params: Bundle) {
        ApzLogger.i(TAG, "onReadyForSpeech")
    }

    override fun onResults(results: Bundle) {
        try {
            isRecording = false
            speechRecognizerMethod()
        } catch (ex: Exception) {
            if (speech != null) {
                speech!!.stopListening()
                speech!!.destroy()
            }
            apzPluginUtil.sendError(
                callbackId, errorCode203, null, this.activity,
                this.webView, true
            )
        }
    }

    override fun onRmsChanged(rmsdB: Float) {
        //Sonar fix
    }

    override fun onError(error: Int) {
        ApzLogger.i(TAG, "error number $error")
        try {
            isRecording = false
            if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_CLIENT || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                if (!isPauseRecognize.equals("Y", ignoreCase = true)) {
                    if (appendWords.equals("", ignoreCase = true)) appendWords = lWords
                    if (!appendWords.equals("", ignoreCase = true) && !appendWords.equals(
                            lWords,
                            ignoreCase = true
                        )
                    ) {
                        appendWords = "$appendWords $lWords"
                    }
                    successCallbackKeepAlive("VALUE", appendWords)
                } else {
                    speechRecognizerMethod()
                }
            } else if (error == SpeechRecognizer.ERROR_SERVER) {
                handleServerErrors()
            } else {
                clearResources()
            }
        } catch (ex: Exception) {
            apzPluginUtil.sendError(
                callbackId, errorCode203, null, this.activity,
                this.webView, true
            )
        }
    }

    private fun handleServerErrors() {
        if (speech != null) {
            speech!!.stopListening()
            speech!!.destroy()
        }
        if (speechTimer != null) {
            speechTimer!!.cancel()
            speechTimer = null
        }

        apzPluginUtil.sendError(
            callbackId, "APZ-CNT-332", null, this.activity,
            this.webView, true
        )
    }

    private fun clearResources() {
        if (speech != null) {
            speech!!.stopListening()
            speech!!.destroy()
        }
        if (speechTimer != null) {
            speechTimer!!.cancel()
            speechTimer = null
        }
        apzPluginUtil.sendError(
            callbackId, errorCode203, null, this.activity,
            this.webView, true
        )
    }

    override fun execute(params: JSONObject) {
        mParams = params
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestForPermission()
        } else {
            callSpeechToText()
        }
    }

    private fun callSpeechToText() {
        /*get supported languages for the voice conversion */
        try {
            getSupportedLanguages()
        } catch (e: Exception) {
            apzPluginUtil.sendError(
                callbackId, errorCode203, null, this.activity,
                this.webView, true
            )
        }
    }

    private fun requestForPermission() {
        this.activity.startOnPermissionForResult(activity, arrayOf(
            Manifest.permission.RECORD_AUDIO
        ), PluginConstants.APZ_REQ_RECORD_AUDIO, object : OnPermissionsResultHandler() {
            override fun handlePermissionResult(
                requestCode: Int,
                permissions: Array<String?>,
                grantResults: IntArray
            ) {
                if (requestCode == PluginConstants.APZ_REQ_RECORD_AUDIO) {
                    handleRecordPermission(permissions)
                } else {
                    permissionDeniedCallback()
                }
            }
        }
        )
    }

    private fun handleRecordPermission(permissions: Array<String?>) {
        var denied = false
        var neverAskAgain = false
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permission!!
                )
            ) {
                denied = true
            } else {
                if (ActivityCompat.checkSelfPermission(
                        activity,
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
                callSpeechToText()
            }
        }
    }

    private fun displayReconfirmationMessage() {
        val message = "To record audio, grant permission for app to access microphone"
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
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
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun permissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied("Audio record ", callbackId, this.activity, this.webView)
    }

    fun successCallback(lAction: String?, lText: String?) {
        val result = JSONObject()
        val res = Handler(Looper.getMainLooper())
        res.postDelayed({
            launch {
                sendToJSON()
            }
            try {
                result.put("text", lText)
                result.put("action", lAction)
            } catch (e: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendSuccess(
                callbackId, result, false, activity,
                webView, true
            )
        }, 200)
    }

    private fun successCallbackKeepAlive(lAction: String?, lText: String?) {
        val result = JSONObject()
        val res = Handler(Looper.getMainLooper())
        res.postDelayed({
            launch {
                sendToJSON()
            }
            try {
                result.put("text", lText)
                result.put("action", lAction)
            } catch (e: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendSuccess(
                callbackId, result, true, activity,
                webView, true
            )
        }, 200)
    }

    fun getSupportedLanguages() {
        activity.sendOrderedBroadcast(
            RecognizerIntent.getVoiceDetailsIntent(activity.applicationContext),
            AppzillonConstants.APPZILLON_BROADCAST_PERMISSION,
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val extras = getResultExtras(true)
                    supportedLanguages =
                        extras.getStringArrayList(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES)
                    /*start voice listener*/
                    startVoiceConversion(mParams)
                }
            },
            null,
            -1,
            null,
            null
        )
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        private var lWords = ""
        private var appendWords = ""
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = SpeechToText(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isSpeechToTextPlugin(): Boolean{
            return true
        }

    }

    init {
        speechTimer = Timer(true)
    }
}

