package com.iexceed.plugins.deviceinfo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.telephony.TelephonyManager
import android.telephony.TelephonyManager.*
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.AppzillonConstants.ANDROID_OS
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.AppzillonUtils.getDeviceType
import com.iexceed.common.AppzillonUtils.getOsDetails
import com.iexceed.common.AppzillonUtils.getScreenSize
import com.iexceed.common.AppzillonUtils.isEmulator
import com.iexceed.common.AppzillonUtils.getDeviceMake
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class DeviceInfo private constructor(
    val webView: WebView,
    val activity: ApzActivity<*>,
    override val apzPluginUtil: IapzPluginUtil
) : ApzPlugin() {

    private val TEMP_NETWORK_TYPE_4G = 19

    override fun execute(params: JSONObject) {
        getDeviceDetails(params)
    }

    fun getDeviceDetails(jsonObj: JSONObject) {
        val obj = JSONObject()
        try {
            callbackId = jsonObj.getString("id")
            obj.put("osName", ANDROID_OS)
            obj.put("deviceName", AppzillonUtils.getBluetoothName(activity))
            obj.put("osVersion", getOsDetails(activity))
            obj.put("devType", getDeviceType())
            obj.put("isEmulator", isEmulator()) //3.2 changes
            //obj.put("screenResolution", getScreenSize());
            obj.put("screenResolution", getScreenSize(activity))
            obj.put("connectionType", getNetworkType())
            obj.put("batteryStatus", getBatteryLevel().toString() + "%")
            obj.put("devManufacturer", getDeviceMake())
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity, webView, true)
        }
        apzPluginUtil.sendSuccess(callbackId, obj, false, activity, webView, true)
    }

    private fun getNetworkType(): String {
        var networkTypeStr = "No-Connection"
        val cm =
            activity.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            getNetworkTypeBelowQ(activity)
        } else {
            val networkCapabilities = cm.activeNetwork ?: return networkTypeStr
            val actNw =
                cm.getNetworkCapabilities(networkCapabilities) ?: return networkTypeStr
            networkTypeStr = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "CELLULAR"
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET"
                else -> "-"
            }
        }

        return networkTypeStr
    }

    private fun getNetworkTypeBelowQ(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

        if (networkCapabilities != null) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return "WIFI"
            } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return getCellularNetworkType(context)
            }
        }
        return "UNKNOWN"
    }

    private fun getCellularNetworkType(context: Context): String {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        return when (telephonyManager.dataNetworkType) {
            NETWORK_TYPE_GPRS,
            NETWORK_TYPE_EDGE,
            NETWORK_TYPE_CDMA,
            NETWORK_TYPE_1xRTT,
            NETWORK_TYPE_IDEN -> "2G"
            NETWORK_TYPE_UMTS,
            NETWORK_TYPE_EVDO_0,
            NETWORK_TYPE_EVDO_A,
            NETWORK_TYPE_HSDPA,
            NETWORK_TYPE_HSUPA,
            NETWORK_TYPE_HSPA,
            NETWORK_TYPE_EVDO_B,
            NETWORK_TYPE_EHRPD,
            NETWORK_TYPE_HSPAP -> "3G"
            NETWORK_TYPE_LTE -> "4G"
            NETWORK_TYPE_NR -> "5G"
            else -> {
                // For newer Android versions that may have additional network types
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_PHONE_STATE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return "UNKNOWN"
                    }
                    when (telephonyManager.dataNetworkType) {
                        NETWORK_TYPE_NR -> "5G"
                        else -> "UNKNOWN"
                    }
                } else {
                    "UNKNOWN"
                }
            }
        }
    }


    /**
     * Retrieves Battery Level of the device
     * @return batteryLevel as float
     */
    private fun getBatteryLevel(): Float {
        val batteryIntent = activity.applicationContext.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED),
            AppzillonConstants.APPZILLON_BROADCAST_PERMISSION, null
        )
        val level = batteryIntent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        // Error checking that probably isn't needed but I added just in case.
        return if (level == -1 || scale == -1) {
            50.0f
        } else level.toFloat() / scale.toFloat() * 100.0f
    }


    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(
            webView: WebView,
            activity: ApzActivity<*>,
            apzPluginUtil: IapzPluginUtil
        ): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = DeviceInfo(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }

    }
}
