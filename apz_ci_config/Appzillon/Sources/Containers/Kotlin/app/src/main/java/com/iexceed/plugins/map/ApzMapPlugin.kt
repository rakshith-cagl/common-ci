package com.iexceed.plugins.map

import android.content.Intent
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject

class ApzMapPlugin private constructor (val webView: WebView, val activity: ApzActivity<*>,
                                        override val apzPluginUtil: IapzPluginUtil
) : ApzPlugin() {
    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            val action = params.getString("action")
            if (action.equals("LOADMAP", ignoreCase = true)) {
                Map.setWebView(webView)
                Map.setApzPluginUtil(apzPluginUtil)
                val intent = Intent(activity.applicationContext, Map::class.java)
                intent.putExtra("mapval", params.toString())
                activity.startActivity(intent)
            }
        } catch (e: Exception) {
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", null, aActivity,
                aWebview, true
            )

        }
    }

    companion object {
        private var pluginObj: ApzPlugin? = null

        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzMapPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}