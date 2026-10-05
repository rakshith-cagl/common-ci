package com.iexceed.plugins.nfcsupport

import android.webkit.WebView
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONException
import org.json.JSONObject

class ApzIsNfcSupported private constructor(
    var webView: WebView,
    var activity: ApzActivity<*>,
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
                pluginObj = ApzIsNfcSupported(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }

    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
        } catch (e: JSONException) {

            apzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity, webView, true)
            return
        }

        val param2 = AppzillonUtils.isNFCSupported(aActivity)
        isNfcDeviceSupported(params, param2)
    }


    private fun isNfcDeviceSupported(json: JSONObject?, checkNfcSupport: Boolean) {

        if (checkNfcSupport) {
            val successMsg = "Device Supported NFC"
            json!!.put("text", successMsg)
            apzPluginUtil.sendSuccess(callbackId, json, false, aActivity, aWebview, true)

        } else {
            val successMsg = "Device Not Supported NFC"
            json!!.put("text", successMsg)
            apzPluginUtil.sendError(callbackId, "", json, aActivity, aWebview, true)
        }

    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }
}
