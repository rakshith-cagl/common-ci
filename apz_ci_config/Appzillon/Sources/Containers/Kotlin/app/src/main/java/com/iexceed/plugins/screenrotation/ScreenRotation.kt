package com.iexceed.plugins.screenrotation

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.view.Surface
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject


class ScreenRotation private constructor(val webView: WebView, val activity: ApzActivity<*>,
                     override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    override fun execute(params: JSONObject) {
        try {
            if (params.getString("action") == "LOCKROTATION") {
                enableLock()
            } else if (params.getString("action") == "UNLOCKROTATION") {
                disableLock()
            }
            callbackId = params.getString("id")
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }
    }

    private fun enableLock() {
        val orientation = activity.resources.configuration.orientation
        val rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.display!!.rotation
        } else {
            activity.windowManager.defaultDisplay.rotation
        }

        var appOrientation = ""

        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                appOrientation = "PORTRAIT"
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                appOrientation = "LANDSCAPE"
            }
        } else if (rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_270) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
                appOrientation = "REVERSE_PORTRAIT"
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                appOrientation = "REVERSE_LANDSCAPE"
            }
        }
        ApzLogger.i(TAG, "Orientation : $appOrientation")
        showCurrentOrientation(appOrientation)
    }

    private fun disableLock() {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        showCurrentOrientation("Successfully unlocked the rotation")
    }

    private fun showCurrentOrientation(s: String?) {
        val result = JSONObject()
        try {
            result.put("successMessage", s)
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }
        apzPluginUtil.sendSuccess(callbackId, result, false, activity, webView, true)
    }


    companion object {
        private var pluginObj: ApzPlugin? = null

        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ScreenRotation(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}
