package com.iexceed.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.webkit.WebView
import com.iexceed.plugins.ApzPluginUtil
import org.json.JSONArray
import org.json.JSONObject
/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class JSMsgIntentRcvr(val activity: ApzActivity<*>, val webView: WebView): BroadcastReceiver() {
    private val apzPluginUtil = ApzPluginUtil()
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val type = intent!!.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_TYPE)
            val callbackId = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_CBID)
            val isInUIThread = intent.getBooleanExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_ISINUITHREAD, true)

            //If success message with JSON Object result
            when {
                type.equals(JSNotifier.APZ_INTENT_JSNOTIFY_SUCCESS_OBJECT, ignoreCase = true) -> {
                    val strResult = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_OBJ)
                    val resultObj = JSONObject(strResult)
                    val keepAlive = intent.getBooleanExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_KALIVE, false)
                    apzPluginUtil.sendSuccess(callbackId, resultObj, keepAlive, activity, webView, isInUIThread)
                }
                type.equals(JSNotifier.APZ_INTENT_JSNOTIFY_SUCCESS_ARRAY, ignoreCase = true) -> {
                    val strResult = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_ARRAY)
                    val resultObj = JSONArray(strResult)
                    val keepAlive = intent.getBooleanExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_KALIVE, false)
                    val resultKey = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_RESULTKEY)
                    apzPluginUtil.sendSuccess(callbackId, resultObj, resultKey, keepAlive, activity, webView, isInUIThread)
                }
                type.equals(JSNotifier.APZ_INTENT_JSNOTIFY_ERROR, ignoreCase = true) -> {
                    val strResult = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_OBJ)
                    val resultObj = JSONObject(strResult)
                    val errorCode = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_ERRORCODE)
                    apzPluginUtil.sendError(callbackId, errorCode, resultObj, activity, webView, isInUIThread)
                }
                else -> {
                    //If Plugin not supported error
                    apzPluginUtil.sendPluginNotSupported(callbackId, activity, webView, isInUIThread)
                }
            }


        } catch (e : Exception) {
            //Sonar fix
        }
    }
}
