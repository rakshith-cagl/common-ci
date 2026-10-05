package com.iexceed.plugins.openbrowser

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject


class ApzOpenBrowserPlugin private constructor(val webView: WebView, val activity: ApzActivity<*>,
                            override val apzPluginUtil: IapzPluginUtil) : ApzPlugin()  {

    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            val url = params.getString("url");
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            activity.startActivity(intent)

            apzPluginUtil.sendSuccess(callbackId, null, false, activity, webView, true)
        } catch (e: Exception) {
            val json = JSONObject()
            try {
                json.put("errorDescription", e.toString())
                apzPluginUtil.sendError(callbackId, "APZ-CNT-077", json, activity, webView, true)
            } catch (e: JSONException) {
                ApzLogger.e("OpenBrowser", e.toString())
            }
        }
    }
    companion object {
        private var pluginObj: ApzPlugin? = null

        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzOpenBrowserPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }

}
