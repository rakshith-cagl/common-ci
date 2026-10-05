package com.iexceed.plugins.appidletimeout

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.common.StringUtils
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class AppIdleTimeOut private constructor (
    private val webView: WebView,
    private val activity: ApzActivity<*>,
    override val apzPluginUtil: IapzPluginUtil
) : ApzPlugin() {

    private val apzTAG = "AppIdleTimeOut"
    var appTimer: Timer? = null
    var result: JSONObject? = null

    var APPIDLE_TIMEOUT = 0

    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            startTimeClocking()
        } catch (e: JSONException) {
            sendError()
        }
    }

    private fun sendError() {
        ApzLogger.e(apzTAG, "Exception")
        apzPluginUtil.sendError(
            callbackId,
            "APZ-CNT-200",
            null,
            activity,
            webView,
            true
        )
    }

    private fun startTimeClocking() {
            APPIDLE_TIMEOUT = StringUtils.getString(StringUtils.APP_IDLE_TIME_OUT).toInt() * 1000
            startTimerForAppIdleTimeout(appTimer)
    }

    private fun startTimerForAppIdleTimeout(appTimer: Timer?) {
        if (appTimer != null) {
            this.appTimer = appTimer
            this.appTimer!!.cancel()
            this.appTimer = null
            ApzLogger.d(apzTAG, "Inside appTimer called")
        }
        if (this.appTimer == null && APPIDLE_TIMEOUT > 0) {
            this.appTimer = Timer(true)
            result = JSONObject()
            result?.put("event", "timerExceeds")
            this.appTimer!!.schedule(AppIdleTimerTask(), APPIDLE_TIMEOUT.toLong())
            val successMsg = "started"
            val resultEvent = JSONObject()
            resultEvent.put("event", successMsg)
            apzPluginUtil.sendSuccess(callbackId, resultEvent, true, activity, webView, true)
        } else {
            sendError()
        }
    }


    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(
            webView: WebView,
            activity: ApzActivity<*>,
            apzPluginUtil: IapzPluginUtil
        ): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = AppIdleTimeOut(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }

    inner class AppIdleTimerTask : TimerTask() {
        override fun run() {
            APPIDLE_TIMEOUT = 0
            ApzLogger.d(apzTAG, "Idle timer executed")
            apzPluginUtil.sendSuccess(callbackId, result, false, activity, webView, true)
        }
    }
}
