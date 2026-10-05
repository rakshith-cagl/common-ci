package com.iexceed.plugins.orientation

import android.content.pm.ActivityInfo
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject


class SetWebView private constructor (val webView: WebView, val activity: ApzActivity<*>, val iapzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    override fun execute(params: JSONObject) {
        try {
            webViewMode(params.getString("orientation"))
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            iapzPluginUtil.sendError(null, "APZ-CNT-077", null, activity, webView, true)
        }
    }

    private fun webViewMode(mode: String) {
        when {
            mode.equals("LANDSCAPE", ignoreCase = true) -> {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            }
            mode.equals("PORTRAIT", ignoreCase = true) -> {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
            }
            else -> {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    companion object {
        private var pluginObj: ApzPlugin? = null

        fun createPlugin(webView: WebView, activity: ApzActivity<*>, iapzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = SetWebView(webView, activity, iapzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}
