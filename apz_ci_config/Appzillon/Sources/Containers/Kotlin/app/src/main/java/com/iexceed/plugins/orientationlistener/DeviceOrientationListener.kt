package com.iexceed.plugins.orientationlistener

import android.content.res.Configuration
import android.util.Log
import android.webkit.WebView
import androidx.lifecycle.Observer
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject


class DeviceOrientationListener private constructor(val webView: WebView, val activity: ApzActivity<*>,
                                override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    val observer = Observer<String> {  orientation ->
        onOrientationReceived(orientation)
    }

    override fun execute(params: JSONObject) {
        try {
            if (params.getString("action").equals("STARTLISTENER", ignoreCase = true)) {
                startListener()
            } else if (params.getString("action").equals("STOPLISTENER", ignoreCase = true)) {
                stopListener()
            }
            callbackId = params.getString("id")
        } catch (e: JSONException) {

            apzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity, webView, true)
        }
    }

    private fun onOrientationReceived(orientation: String) {
        val json = JSONObject()
        try {
            json.put("event", "orientation_change")
            json.put("orientation", orientation)
        } catch (e: Exception) {
            ApzLogger.e(TAG, e.toString())
        }
        apzPluginUtil.sendSuccess(callbackId, json, true, activity, webView, true)
    }

    private fun startListener() {
        try {
            activity.runOnUiThread{
                activity.viewModel.orientationListenerLiveData.observe(activity,observer)
            }
        } catch (e: Exception) {
            Log.d("TAG","")
        }

        var orientation = ""
        if (activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            orientation = "LANDSCAPE"
        } else if (activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            orientation = "PORTRAIT"
        }
        val json = JSONObject()
        try {
            json.put("event", "started")
            json.put("orientation", orientation)
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }
        apzPluginUtil.sendSuccess(callbackId, json, true, activity, webView, true)
    }

    fun stopListener() {
        try {
            activity.runOnUiThread{
                activity.viewModel.orientationListenerLiveData.removeObserver(observer)
            }
        } catch (e: Exception) {
            Log.d("TAG","")
        }
        val json = JSONObject()
        try {
            json.put("event", "stopped")
        } catch (e: JSONException) {
            //Sonar fix
        }
        apzPluginUtil.sendSuccess(callbackId, json, false, activity, webView, true)
    }

    companion object {
        private var pluginObj: ApzPlugin? = null

        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = DeviceOrientationListener(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}
