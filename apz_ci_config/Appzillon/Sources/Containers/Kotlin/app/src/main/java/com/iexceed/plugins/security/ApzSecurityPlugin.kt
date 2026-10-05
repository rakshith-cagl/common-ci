package com.iexceed.plugins.security

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONException
import org.json.JSONObject

class ApzSecurityPlugin private constructor(webView: WebView,
                        activity: ApzActivity<*>,
                        override val apzPluginUtil: IapzPluginUtil
) : ApzPlugin()
{
    private var mJson: JSONObject? = null
    private var action: String? = null

    init {
        super.aActivity = activity
        super.aWebview = webView
    }

    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            mJson = params
            action = params.getString("action")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if ("getWifiStatus".equals(action, ignoreCase = true)) {
            val wifi = WifiSecurity(aActivity, aWebview, params, apzPluginUtil)
            wifi.requestGPSpermission()
        }
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzSecurityPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }
}
