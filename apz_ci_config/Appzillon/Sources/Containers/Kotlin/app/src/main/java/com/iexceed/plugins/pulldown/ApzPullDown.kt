package com.iexceed.plugins.pulldown

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject

class ApzPullDown private constructor( webView: WebView,
                   activity: ApzActivity<*>,
                  override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {


    init {
        super.aActivity = activity
        super.aWebview = webView
    }


    override fun execute(params: JSONObject) {
        val action: String
        try {
            callbackId = params["id"] as String
            action = params["action"] as String
            when {
                action.equals("Enable", ignoreCase = true) -> {
                    try {
                        enablePullDown(params)
                    } catch (e: Exception) {
                        ApzLogger.e("ApzPullDown", e.toString())
                    }
                }
                action.equals("Disable", ignoreCase = true) -> {
                    try {
                        disablePullDown()
                    } catch (e: Exception) {
                        ApzLogger.e("ApzPullDown", e.toString())
                    }
                }
                action.equals("HideRefresh", ignoreCase = true) -> {
                    hideRefreshIcon()
                }
            }
        } catch (e: JSONException) {
            ApzLogger.e("ApzPullDown", e.toString())
            apzPluginUtil.sendError(callbackId, "APZ-CNT-077",
                null, aActivity, aWebview, true)
        }
    }

    private fun hideRefreshIcon() {
        aActivity.runOnUiThread {
            if (aActivity.swipeLayout?.isRefreshing ==true)
                aActivity.swipeLayout?.isRefreshing = false
        }
    }

    private fun disablePullDown() {

        aActivity.runOnUiThread {
            try {
                aActivity.swipeLayout?.isEnabled = false
                aActivity.swipeLayout?.isRefreshing = false
                aActivity.swipeLayout?.setOnRefreshListener(null)
                val cBackObj = JSONObject()
                try {
                    cBackObj.put("event", "stopped")
                } catch (e: JSONException) {
                    //Sonar fix
                }
                apzPluginUtil.sendSuccess(callbackId, cBackObj, false, aActivity, aWebview, true
                )
            } catch (e: java.lang.Exception) {
                apzPluginUtil.sendError(callbackId, "", JSONObject(), aActivity, aWebview, true
                )
            }
        }
    }

    private fun enablePullDown(json: JSONObject) {
        var callId: String? = null
        var screenId: String? = null

        try {
            callId = json.getString("callId")
            screenId = json.getString("screenId")
        } catch (e: JSONException) {
            ApzLogger.e("PullDown", e.toString())
        }
        val cId = callId
        val sId = screenId

        aActivity.runOnUiThread {
            try {
                aActivity.swipeLayout?.isEnabled = true

                val cBackObj = JSONObject()
                aActivity.swipeLayout?.setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light)

                aActivity.swipeLayout?.setOnRefreshListener {
                    try {
                        cBackObj.put("callId", cId)
                        cBackObj.put("screeId", sId)
                        cBackObj.put("event", "pullDown")
                    } catch (e: JSONException) {
                        ApzLogger.e("PullDown", e.toString())
                    }
                    apzPluginUtil.sendSuccess(callbackId, cBackObj, true, aActivity, aWebview, true)
                }

                try {
                    cBackObj.put("event", "started")
                } catch (e: JSONException) {
                    //Sonar fix
                }

                apzPluginUtil.sendSuccess(callbackId, cBackObj, true, aActivity, aWebview, true)

            } catch (e: Exception) {
                apzPluginUtil.sendError(callbackId, "", JSONObject(), aActivity, aWebview, true)
            }
        }

    }





    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzPullDown(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}
