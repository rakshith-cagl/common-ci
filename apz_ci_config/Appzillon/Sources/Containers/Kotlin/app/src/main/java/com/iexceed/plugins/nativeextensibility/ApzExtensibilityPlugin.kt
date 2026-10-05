package com.iexceed.plugins.nativeextensibility

import android.app.Activity
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONObject

class ApzExtensibilityPlugin private constructor(
    private val webView: WebView,
    private val activity: Activity,
    override val apzPluginUtil: IapzPluginUtil
) : ApzPlugin() {

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(
            webView: WebView,
            activity: ApzActivity<*>,
            apzPluginUtil: IapzPluginUtil
        ): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzExtensibilityPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }

    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            val jsonResult: JSONObject? =
                NativeService.nativeServiceEntry(webView, activity, params, apzPluginUtil)
            if (jsonResult != null) {
                apzPluginUtil.sendSuccess(callbackId, jsonResult, false, activity, webView, true)
            } else {
                apzPluginUtil.sendError(
                    callbackId,
                    "APZ-CNT-171",
                    null,
                    activity,
                    webView,
                    true
                )
            }
        } catch (e: Exception) {
            ApzLogger.e("ApzExtensibilityPlugin", e.toString())
            apzPluginUtil.sendError(
                callbackId,
                "APZ-CNT-171",
                null,
                activity,
                webView,
                true
            )
        }
    }
}
