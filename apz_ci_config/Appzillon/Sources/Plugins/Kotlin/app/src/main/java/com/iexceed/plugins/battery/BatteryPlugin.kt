package com.iexceed.plugins.battery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class BatteryPlugin private constructor (var webView: WebView, var activity: ApzActivity<*>, override val apzPluginUtil: IapzPluginUtil): ApzPlugin() {
    private var batteryState: String? = null

    private var batteryLevel: String? = null

    private var threshold: String? = null

    private var time: String? = null

    private var oldChargingLevel = 0

    private var oldChargingStatus = 0

    lateinit var batteryTimer: Timer

    override var TAG = "BatteryPlugin"

    var ifilter: IntentFilter? = null

    private val errorCode = "APZ-CNT-077"

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = BatteryPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isBatteryPlugin(): Boolean {
            return true
        }
    }

    fun registerBatteryReceiver(jsonObj: JSONObject) {
        var result: JSONObject? = null
        try {
            result = JSONObject()
            batteryState = jsonObj.getString("state")
            batteryLevel = jsonObj.getString("level")
            threshold = jsonObj.getString("threshold")
            if ("".equals(threshold, ignoreCase = true)) {
                threshold = "0"
            }
            time = jsonObj.getString("time")
            if ("".equals(time, ignoreCase = true)) {
                time = "0"
            }
            AppzillonMainScreen.IS_BATTERY_CALLBACK_ENABLED = batteryLevel
                .equals("Y", ignoreCase = true)
            AppzillonMainScreen.IS_STATE_CALLBACK_ENABLED = batteryState
                .equals("Y", ignoreCase = true)
            AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED = isEnabled(threshold!!)

            // For Time
            if (isEnabled(time!!)) {
                startTimer(time!!)
            }
            if (!AppzillonMainScreen.IS_REGISTERED) {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    regPowerConnectionReceiver()
                }, 1000)
                oldChargingLevel = getPresentChargingLevel()
                oldChargingStatus = getPresentState()
                AppzillonMainScreen.IS_REGISTERED = true
                try {
                    result.put("event", "started")
                } catch (ex: Exception) {
                    ex.stackTrace
                }
                apzPluginUtil.sendSuccess(
                    callbackId, result, true,
                    aActivity, aWebview, true
                )
            } else {
                ApzLogger.i(TAG, "Already Registered, Please Stop and Start again.")
                apzPluginUtil.sendError(
                    callbackId, "APZ-CNT-241", null,
                    aActivity, aWebview, true
                )
            }
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(
                callbackId, errorCode, null,
                aActivity, aWebview, true
            ) // Invalid JSON
        }
    }

    private fun regPowerConnectionReceiver() {
        ifilter = IntentFilter()
        ifilter?.addAction(Intent.ACTION_BATTERY_CHANGED)
        aActivity.registerReceiver(powerConnectionReceiver, ifilter, AppzillonConstants.APPZILLON_BROADCAST_PERMISSION , null)
    }

    // For checking if there is any value present or not
    private fun isEnabled(value: String): Boolean {
        try {
            if (!"".equals(value, ignoreCase = true)) {
                val tHold = value.toInt()
                if (tHold > 0) {
                    return true
                }
            }
            return false
        } catch (e: NumberFormatException) {
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-240", null,
                aActivity, aWebview, true
            )
            return false
        }
    }

    var powerConnectionReceiver: BroadcastReceiver? = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val result = JSONObject()
            if (AppzillonMainScreen.IS_REGISTERED) {
                val status = getBatteryStatus(intent)
                val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                val bState = if (isCharging) "plugged" else "unplugged"
                val bLevel = getBatteryLevel(intent)
                val isChargingValueChanged = oldChargingLevel != bLevel
                val isChargingStatusChanged = oldChargingStatus != status
                if (AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED) {
                    ApzLogger.i(TAG, "Threshold callback enabled")
                    handleThreshold(bLevel, status, result, bState)
                } else {
                    if (bLevel > threshold!!.toInt()) {
                        ApzLogger.i(TAG, "BLevel is greater then Threshold.")
                        AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED = isEnabled((threshold)!!)
                    }
                }
                if ((AppzillonMainScreen.IS_BATTERY_CALLBACK_ENABLED
                            && isChargingValueChanged)
                ) {
                    onChargingValueChanged(bLevel, status, result, bState)
                } else if ((AppzillonMainScreen.IS_STATE_CALLBACK_ENABLED
                            && isChargingStatusChanged)
                ) {
                    onStateValueChanged(bLevel, status, result, bState)
                }
            } else {
                ApzLogger.i(TAG, "Battery is not registered")
                context.unregisterReceiver(this)
            }
        }
    }

    private fun onStateValueChanged(
        bLevel: Int,
        status: Int,
        result: JSONObject,
        bState: String
    ) {
        oldChargingLevel = bLevel
        oldChargingStatus = status
        try {
            result.put("event", "state")
            result.put("level", bLevel)
            result.put("state", bState)
            result.put("command", "BATTERY_VALUE")
        } catch (e: JSONException) {
            //Sonar fix
        }
        apzPluginUtil.sendSuccess(
            callbackId, result, true,
            aActivity, aWebview, false
        )
    }

    private fun onChargingValueChanged(
        bLevel: Int,
        status: Int,
        result: JSONObject,
        bState: String
    ) {
        if ((bLevel == threshold!!.toInt()
                    && AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED)
        ) {
            ApzLogger.i(TAG, "SHOW Threshold")
        } else {
            oldChargingLevel = bLevel
            oldChargingStatus = status
            try {
                result.put("event", "battery")
                result.put("level", bLevel)
                result.put("state", bState)
                result.put("command", "BATTERY_VALUE")
            } catch (e: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendSuccess(
                callbackId, result, true,
                aActivity, aWebview, false
            )
        }
    }

    private fun handleThreshold(
        bLevel: Int,
        status: Int,
        result: JSONObject,
        bState: String
    ) {
        if (bLevel < threshold!!.toInt()) {
            aActivity.runOnUiThread {
                oldChargingLevel = bLevel
                oldChargingStatus = status
                try {
                    result.put("event", "threshold")
                    result.put("level", bLevel)
                    result.put("state", bState)
                    result.put("command", "BATTERY_VALUE")
                } catch (e: Exception) {
                    //Sonar fix
                }
                apzPluginUtil.sendSuccess(
                    callbackId, result,
                    true, aActivity, aWebview, false
                )
                AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED = false
            }
        }
    }

    private fun getBatteryStatus(intent: Intent): Int {
        var status = intent
            .getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        if (status == BatteryManager.BATTERY_STATUS_CHARGING
            || status == BatteryManager.BATTERY_STATUS_FULL
        ) status = BatteryManager.BATTERY_STATUS_CHARGING else status =
            BatteryManager.BATTERY_STATUS_NOT_CHARGING
        return status
    }

    private fun getBatteryLevel(intent: Intent): Int {
        val scale = intent.getIntExtra(
            BatteryManager.EXTRA_SCALE, -1
        )
        val level = intent.getIntExtra(
            BatteryManager.EXTRA_LEVEL, -1
        )
        val bLevel = (level.toFloat() / scale.toFloat() * 100.0f).toInt()
        return bLevel
    }

    fun unregisterBatteryReceiver(jsonObj: JSONObject?) {
        val resultStopped = JSONObject()
        try {

            AppzillonMainScreen.IS_STATE_CALLBACK_ENABLED = false
            AppzillonMainScreen.IS_BATTERY_CALLBACK_ENABLED = false
            AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED = false
            stopTimer()
            AppzillonMainScreen.IS_REGISTERED = false
            if (powerConnectionReceiver != null) {
                aActivity.unregisterReceiver(powerConnectionReceiver)
                powerConnectionReceiver = null
                pluginObj = null
            }
            //}
            try {
                resultStopped.put("event", "stopped")
            } catch (e: JSONException) {
                apzPluginUtil.sendError(
                    callbackId, errorCode, null,
                    aActivity, aWebview, true
                )
            }
            apzPluginUtil.sendSuccess(
                callbackId, resultStopped, false,
                aActivity, aWebview, true
            )
        } catch (e: IllegalArgumentException) {
            powerConnectionReceiver = null
            try {
                resultStopped.put("text", e.message)
            } catch (e1: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendError(
                callbackId, errorCode, null,
                aActivity, aWebview, true
            )
            return
        }
    }

    private fun startTimer(callBackTime: String) {
        val period = callBackTime.toInt() * 1000 // Converting into
        batteryTimer = Timer(true) // milliseconds
        batteryTimer.scheduleAtFixedRate(BatteryTimerTask(), period.toLong(), period.toLong())
    }

    private fun stopTimer() {
        if (batteryTimer != null)
            batteryTimer.cancel()
        ApzLogger.e(TAG, "Battery stopped")
    }

    private fun getPresentChargingLevel(): Int {
        val batteryIntent = aActivity.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            AppzillonConstants.APPZILLON_BROADCAST_PERMISSION,
            null
        )
        val scale = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        return ((level.toFloat() / scale.toFloat()) * 100.0f).toInt()
    }

    private fun getPresentState(): Int {
        val batteryIntent = aActivity.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            AppzillonConstants.APPZILLON_BROADCAST_PERMISSION,
            null
        )
        var status = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        if ((status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL)
        ) status = BatteryManager.BATTERY_STATUS_CHARGING else status =
            BatteryManager.BATTERY_STATUS_NOT_CHARGING
        return status
    }


    override fun execute(params: JSONObject) {
        var action = ""

        try {
            callbackId = params.getString("id")
            action = params.getString("action")
            if (action.equals("START", ignoreCase = true)) {
                registerBatteryReceiver(params)
            } else if (action.equals("STOP", ignoreCase = true)) {
                unregisterBatteryReceiver(params)
            }
        } catch (ex: Exception) {
            apzPluginUtil.sendError(
                callbackId, errorCode, null,
                this.activity, this.webView, true
            )
            return
        }
    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }

    inner class BatteryTimerTask : TimerTask() {
        override fun run() {
            val status = getPresentState()
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
            val bState = if (isCharging) "plugged" else "unplugged"
            val bLevel = getPresentChargingLevel()
            val resObj = JSONObject()
            try {
                resObj.put("event", "time")
                resObj.put("level", bLevel)
                resObj.put("state", bState)
            } catch (e: JSONException) {
                ApzLogger.e(TAG, "JSONException$e")
            }
            apzPluginUtil.sendSuccess(
                callbackId, resObj, true, aActivity,
                aWebview, true
            )
        }
    }
}
