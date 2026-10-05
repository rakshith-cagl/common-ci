package com.iexceed.plugins.call

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
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

class ApzCallPlugin private constructor(val webView: WebView, val activity: ApzActivity<*>,
                    override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    private var phoneNumber: String? = null

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzCallPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }

    }

    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            this.phoneNumber = params.getString("phoneNo")
            if (isNetworkAvailable()) {
                makeCall()
            } else {
                ApzLogger.i(TAG, "No Network available.")
                val json = JSONObject()
                try {
                    json.put("text", "Cellular network not available")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                apzPluginUtil.sendError(
                    callbackId, "APZ-CNT-059", json, activity,
                    webView, true
                )
            }
        } catch (e: JSONException) {
            ApzLogger.e("ApzCallPlugin", e.toString())
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", params, activity,
                webView, true
            )
        }

    }

    fun makeCall() {
        try {
            val contact: String? = phoneNumber
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$contact")
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            ApzLogger.e(TAG, "Making Call, Call failed$e")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetwork
        if (networkInfo != null) {
            val nc = cm.getNetworkCapabilities(networkInfo)
            //It will check for both wifi and cellular network
            return nc!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
        return false
    }

}
