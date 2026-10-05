package com.iexceed.plugins.launchwebview

import android.app.Activity
import android.content.Intent
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class ApzLaunchWebView private constructor (val webView: WebView, val activity: ApzActivity<*>,
                       override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    lateinit var url: String
    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            val action = params.getString("action")

            if (action.equals("Open", ignoreCase = true)) {

                openWebView(params, activity, callbackId, webView)

            } else if (action.equals("Close", ignoreCase = true)) {
                LaunchWebViewActivity.webViewActivity.finish()
                val successCallbackRes = JSONObject()
                try {
                    successCallbackRes.put("text", "webView Closed.")
                } catch (e: JSONException) {
                    ApzLogger.e(TAG, e.toString())
                }
                apzPluginUtil.sendSuccess(callbackId, successCallbackRes, false, activity, webView, true)
            }
        } catch (e: Exception) {

            apzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity, webView, true)
        }
    }

    fun openWebView(params: JSONObject, activity: Activity, callbackID: String?, webView: WebView){

        url = params.getString("URL")
        val postData = params.optJSONObject("postData")
        val trackURL = params.opt("trackURL")
        trackURLStringArray = emptyArray()
        trackURLString = ""
        val closeEnabled = params.optString("cancelButton")
        if (trackURL != null) {
            if (trackURL is JSONArray) {
                val jsonArray = params.getJSONArray("trackURL")
                trackURLStringArray = arrayOfNulls(jsonArray.length())
                for (i in 0 until jsonArray.length()) {
                    trackURLStringArray[i] = jsonArray.getString(i)
                }
            } else if (trackURL is JSONObject) {

                trackURLString = params.getString("trackURL")
            }
        }

        LaunchWebViewActivity.setWebView(webView)
        LaunchWebViewActivity.setApzPluginUtil(apzPluginUtil)
        val intent = Intent(activity, LaunchWebViewActivity::class.java)
        intent.putExtra("callbackId", callbackID)
        intent.putExtra("URL", url)
        intent.putExtra("cancel_btn", closeEnabled)
        if (postData == null) intent.putExtra("postData", "")
        else intent.putExtra("postData", postData.toString())
        activity.startActivity(intent)
    }

    companion object {
        private var pluginObj: ApzPlugin? = null

        lateinit var trackURLStringArray: Array<String?>
        var trackURLString = ""

        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzLaunchWebView(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}
