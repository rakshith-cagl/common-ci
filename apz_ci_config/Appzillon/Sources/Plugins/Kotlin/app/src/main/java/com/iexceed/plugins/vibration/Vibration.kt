package com.iexceed.plugins.vibration

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class Vibration private constructor(webView: WebView, activity: ApzActivity<*>,
                                    override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    init {
        super.aActivity = activity
        super.aWebview = webView
    }

    lateinit var mVibrator : Vibrator
    var mVibrateTime = 5000
    val TA = "vibrationPlugin"

    private fun vibrate(jsonobj: JSONObject)
    {
        var lAction = ""
        try {
            callbackId = jsonobj.getString("id")
            mVibrateTime = jsonobj.optInt("time")
            lAction = jsonobj.getString("action")
        } catch (e: JSONException) {
            ApzLogger.d(TA, e.toString())
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", null,
                this.aActivity, this.aWebview, true)
        }

        ApzLogger.d(TA, " lAction : $lAction")
        if(mVibrator.hasVibrator())
        {
            if(lAction == "START_VIBRATE")
            {
                ApzLogger.d(TA, " Inside : START_VIBRATE")
                startVibrate()
            } else if(lAction == "STOP_VIBRATE"){
                ApzLogger.d(TA, " Inside : STOP_VIBRATE ")
                stopVibrate()
            }
        }else{
            val lError = JSONObject()
            lError.put("message" ,"Hardware not present")
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-134", null,
                this.aActivity, this.aWebview, true)
        }
    }

    private fun startVibrate()
    {
        ApzLogger.d(TA, " Inside : Vibrating")

        mVibrator.vibrate(VibrationEffect.createOneShot(mVibrateTime.toLong(), 10))

        val result = JSONObject()
        result.put("successMessage", "Vibration Started")
        apzPluginUtil.sendSuccess(callbackId, result, false, aActivity, aWebview, true)
    }

    private fun stopVibrate()
    {
        ApzLogger.d(TA, " Vibration stopped ")
        mVibrator.cancel()
    }

    override fun execute(params: JSONObject)
    {
        mVibrator = if (Build.VERSION.SDK_INT >= VERSION_CODES.S) {
            val vibratorManager =  aActivity.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        }else{
            aActivity.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vibrate(params)
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = Vibration(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        val isVibratePlugin: Boolean
            get() = true
    }
}
