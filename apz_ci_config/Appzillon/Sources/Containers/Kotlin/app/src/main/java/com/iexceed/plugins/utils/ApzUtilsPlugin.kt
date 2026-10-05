package com.iexceed.plugins.utils

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.ApzActivity
import com.iexceed.common.UserSettings
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.plugins.networkmonitor.MonitorNetwork
import com.iexceed.utils.common.PackageInfoUtils
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import com.iexceed.utils.network.InternetCheck
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ApzUtilsPlugin private constructor(
    private var aWebView: WebView, private var aApzActivity: ApzActivity<*>,
    override val apzPluginUtil: IapzPluginUtil
) : ApzPlugin() {
    override fun execute(params: JSONObject) {
        try {
            callbackId = params.get("id") as String
            val command = params.get("command") as String
            if (command.isEmpty()) {
                validatePlugin()
            } else {
                when (command) {
                    PluginConstants.PLGN_DEV_INFO -> {
                        handlePluginDevInfo()
                    }

                    PluginConstants.APZ_APP_AVAILABILITY -> {
                        handlePluginAppAvailability(params)
                    }

                    PluginConstants.APZ_NOTIF_STATUS -> {
                        handlePluginNotificationStatus()
                    }

                    PluginConstants.PLGN_GET_USER_PREF -> {
                        handlePluginGetUserPref(params)
                    }

                    PluginConstants.PLGN_IS_APPTOKENSET -> {
                        handlePluginAppTokenSet()
                    }

                    PluginConstants.APZ_NETWORK_MONITOR -> {
                        handlePluginNetworkMonitor(params)
                    }

                    PluginConstants.APZ_PLUGIN_CLS_APPCTN -> {
                        AppzillonUtils.closeApplication(aApzActivity)
                        apzPluginUtil.sendSuccess(
                            callbackId,
                            JSONObject(),
                            false,
                            aApzActivity,
                            aWebView,
                            true
                        )
                    }

                    PluginConstants.APZ_PLUGIN_GET_IP -> {
                        val deviceIpAddress = AppzillonUtils.ipAddress()
                        val json = JSONObject()
                        json.put("ip", deviceIpAddress)
                        apzPluginUtil.sendSuccess(
                            callbackId,
                            json,
                            false,
                            aApzActivity,
                            aWebView,
                            true
                        )
                    }

                    PluginConstants.APZ_PLUGIN_APP_VERSION -> {
                        val appVersion = AppzillonUtils.getAppVersion(params)
                        val json = JSONObject()
                        json.put("appVersion", appVersion)
                        apzPluginUtil.sendSuccess(
                            callbackId,
                            json,
                            false,
                            aApzActivity,
                            aWebView,
                            true
                        )
                    }

                    PluginConstants.APZ_PLUGIN_SET_PREF -> {
                        handlePluginSetPref(params)
                    }

                    PluginConstants.APZ_PLUGIN_SETSETTINGS -> {
                        val result = AppzillonUtils.setSettingValue(aApzActivity, params)
                        if (result) {
                            apzPluginUtil.sendSuccess(
                                callbackId,
                                null,
                                false,
                                aApzActivity,
                                aWebView,
                                true
                            )
                        }
                    }

                    PluginConstants.APZ_PLUGIN_GET_PREF -> {
                        handleGetPrefPlugin(params)
                    }

                    PluginConstants.APZ_PLUGIN_STATUS_BAR_COLOR -> {
                        handleStatusBarPlugin(params)

                    }

                    PluginConstants.PLGN_IS_NETWORK_AVAILABLE -> {
                        checkNetworkAvailability()
                    }
                }
            }
        } catch (e: Exception) {
            ApzLogger.i("ApzUtilsPlugin", e.toString())
        }
    }

    private fun checkNetworkAvailability() {
        val result = JSONObject()
        if(InternetCheck.isNetworkAvailable(aApzActivity)) {
            result.put("isNetworkAvailable","Y")
        } else {
            result.put("isNetworkAvailable","N")
        }

        apzPluginUtil.sendSuccess(
            callbackId,
            result,
            false,
            aApzActivity,
            aWebView,
            true
        )
    }

    private fun handleStatusBarPlugin(params: JSONObject) {
        try {
            val isNavbar = false
            val color = params.optString("color")
            val startColor = params.optString("startColor")
            val endColor = params.optString("endColor")
            val direction = params.optString("direction")
            val window: Window = aApzActivity.window
            aApzActivity.runOnUiThread {
                try {
                    val statusBarGradientAction = params.optString("action")
                    if (statusBarGradientAction.equals(
                            "STATUS_BAR_COLOR_GRADIENT",
                            ignoreCase = true
                        )
                    ) {
                        val colors =
                            intArrayOf(Color.parseColor(startColor), Color.parseColor(endColor))

                        // clear FLAG_TRANSLUCENT_STATUS flag:
                        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

                        val orient: GradientDrawable.Orientation = when (direction) {
                            "toLeft" -> GradientDrawable.Orientation.RIGHT_LEFT
                            "toRight" -> GradientDrawable.Orientation.LEFT_RIGHT
                            "toBottom" -> GradientDrawable.Orientation.TOP_BOTTOM
                            "toTop" -> GradientDrawable.Orientation.BOTTOM_TOP
                            else -> GradientDrawable.Orientation.RIGHT_LEFT
                        }
                        val gd = GradientDrawable(orient, colors)
                        window.statusBarColor =
                            aApzActivity.resources.getColor(R.color.transparent, aApzActivity.theme)
                        if (isNavbar) {
                            window.navigationBarColor =
                                aApzActivity.resources.getColor(
                                    R.color.transparent,
                                    aApzActivity.theme
                                )    //if we want colored navigationBar also.
                        }
                        window.setBackgroundDrawable(gd)
                    } else {
                        // clear FLAG_TRANSLUCENT_STATUS flag:
                        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                        window.statusBarColor = Color.parseColor(color)
                    }
                    val resultObj = JSONObject()
                    resultObj.put("text", "StatusBar color change success.")
                    apzPluginUtil.sendSuccess(
                        callbackId,
                        resultObj,
                        false,
                        aApzActivity,
                        aWebView,
                        true
                    )

                } catch (e: java.lang.Exception) {
                    val resultObj = JSONObject()
                    try {
                        resultObj.put("errorMessage", "StatusBar color change failed $e")
                    } catch (ignored: JSONException) {
                        //Sonar fix
                    }
                    apzPluginUtil.sendError(callbackId, "", resultObj, aApzActivity, aWebView, true)
                }
            }
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "execute: " + e.message)
        }
    }

    private fun validatePlugin() {
        if (callbackId == null || callbackId.isNullOrEmpty()) {
            apzPluginUtil.sendPluginNotSupported(
                callbackId,
                aApzActivity, aWebView, true
            )
        }
    }

    private fun handleGetPrefPlugin(params: JSONObject) {
        val value = AppzillonUtils.getSetting(aApzActivity, params)
        val json = JSONObject()
        if (!"".equals(value, ignoreCase = true)) {
            json.put("value", value)
        } else {
            json.put("value", "")
        }
        apzPluginUtil.sendSuccess(callbackId, json, false, aApzActivity, aWebView, true)
    }

    private fun handlePluginSetPref(params: JSONObject) {
        var result = false
        try {
            val key = params.getString("key")
            val value = params.getString("value")
            result = AppzillonUtils.setSetting(key, value)
        } catch (e1: Exception) {
            //Sonar fix
        }
        if (result) {
            apzPluginUtil.sendSuccess(callbackId, null, false, aApzActivity, aWebView, true)
        }
    }

    private fun handlePluginNetworkMonitor(params: JSONObject) {
        val action = params.get("action") as String
        val networkJson = JSONObject()
        if (action.equals("start", ignoreCase = true)) {
            if (!monitoring) {
                MonitorNetwork(
                    aApzActivity,
                    aWebView,
                    callbackId!!,
                    apzPluginUtil
                ).startNetworkCallback()
                monitoring = true
                networkJson.put("event", "started")
            } else {
                networkJson.put("event", "already running.")
            }
        } else {
            if (monitoring) {
                MonitorNetwork(
                    aApzActivity,
                    aWebView,
                    callbackId!!,
                    apzPluginUtil
                ).stopNetworkCallback()
                monitoring = false
                networkJson.put("event", "stopped")
            } else {
                networkJson.put("event", "Not started Yet.")
            }
        }
        apzPluginUtil.sendSuccess(callbackId, networkJson, true, aApzActivity, aWebView, true)
    }

    private fun handlePluginAppTokenSet() {
        val defaultval = "N"
        val settings = EncryptedPrefHelper.getPrefs()
        val response = JSONObject()
        response.put(
            AppzillonConstants.IS_APP_TOKEN_STATUS,
            UserSettings.getAppValue(
                AppzillonMainScreen.APP_NAME,
                AppzillonConstants.IS_CONTAINER_LOADED,
                defaultval,
                settings
            )
        )
        apzPluginUtil.sendSuccess(
            this.callbackId,
            response, false, aApzActivity,
            aWebView, true
        )
    }

    private fun handlePluginGetUserPref(params: JSONObject) {
        val userPreJson = AppzillonUtils.getAllSettingsValue(
            params.toString(),
            EncryptedPrefHelper.getPrefs()
        )
        if (userPreJson != null) {
            apzPluginUtil.sendSuccess(
                callbackId,
                userPreJson,
                false,
                aApzActivity,
                aWebView,
                true
            )
        }
    }

    private fun handlePluginNotificationStatus() {

        val nm = aApzActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifEnabled = if (nm.areNotificationsEnabled()) "Y" else "N"
        val notificationStatus = JSONObject()

        notificationStatus.put("notificationStatus", notifEnabled)


        apzPluginUtil.sendSuccess(
            callbackId,
            notificationStatus, false, this.aApzActivity,
            this@ApzUtilsPlugin.aWebView, true
        )
    }

    private fun handlePluginAppAvailability(params: JSONObject) {
        val packageInfo = JSONObject()
        val uri: JSONArray = params.optJSONArray("package") as JSONArray
        try {
            val packageArr = JSONArray()
            var i = 0
            while (i < uri.length()) {
                val packageList = JSONObject()
                try {
                    PackageInfoUtils.getPackageDetails(aApzActivity)
                    packageList.put(uri.getString(i), "Installed")
                } catch (e: PackageManager.NameNotFoundException) {
                    packageList.put(uri.getString(i), "Not Installed")
                }
                packageArr.put(i, packageList)
                i++
            }
            packageInfo.put("result", packageArr)
        } catch (e: JSONException) {
            val resultObj = JSONObject()
            try {
                resultObj.put("errorMessage", "Error $e")
            } catch (ex: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendError(callbackId, "", resultObj, aApzActivity, aWebView, true)
        }
        apzPluginUtil.sendSuccess(
            this.callbackId,
            packageInfo, false, aApzActivity,
            aWebView, true
        )
    }

    private fun handlePluginDevInfo() {
        val deviceInfo = AppzillonUtils.getDeviceRunTimeInfo(aApzActivity)
        apzPluginUtil.sendSuccess(
            callbackId,
            deviceInfo,
            false,
            aApzActivity,
            aWebView,
            true
        )
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        var monitoring = false
        fun createPlugin(
            aWebView: WebView,
            activity: ApzActivity<*>,
            apzPluginUtil: IapzPluginUtil
        ): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzUtilsPlugin(aWebView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }
}
