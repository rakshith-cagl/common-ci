package com.iexceed.plugins.utils

import android.text.TextUtils
import android.webkit.WebView
import com.iexceed.common.*
import com.iexceed.common.StringUtils.getString
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/

class NativeServerCall(webView: WebView, activity: ApzActivity<*>,
                       override val apzPluginUtil: IapzPluginUtil) : ApzPlugin()
{
    private var serverURL: String? = null
    private var reqID: String? = null
    private var reqJson: JSONObject? = null
    override var TAG = "NativeServerCall"
    init {
        super.aWebview = webView
        super.aActivity = activity
    }

    override fun execute(params: JSONObject)
    {
        val command: String
        try {
            callbackId = params.getString("id")
            command = params.getString("command")
            if ("PLGN_REFSVRNONCE".equals(command, ignoreCase = true)) {
                refreshServerNonce()
            } else {
                reqID = params.getString("reqId")
                serverURL = getString(StringUtils.SERVER_URL)
                reqJson = params.getJSONObject("params").getJSONObject("reqFull")
                ServerCall(aActivity, aWebview,callbackId, reqID, apzPluginUtil).execute(reqJson)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun refreshServerNonce()
    {
        val lPostAppSecToken :PostAppSecToken = object : PostAppSecToken {
            override fun executeAfterAppSecToken(refreshServerNonce: Boolean) {
                if (!TextUtils.isEmpty(AppzillonConstants.SNONCE)) {
                    apzPluginUtil.sendSuccess(callbackId,
                        null, false,
                        aActivity, aWebview, true);
                } else {
                    apzPluginUtil.sendError(callbackId, "APZ-CNT-330",
                        null, aActivity, aWebview, true);
                }
            }
        }

        GetAppSecToken(aWebview, aActivity, lPostAppSecToken, true).execute()
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = NativeServerCall(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }
}
