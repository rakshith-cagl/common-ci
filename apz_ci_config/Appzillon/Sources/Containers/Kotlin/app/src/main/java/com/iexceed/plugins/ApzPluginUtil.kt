package com.iexceed.plugins

import android.app.Activity
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ApzPluginUtil : IapzPluginUtil {

    var TAG = "apzPluginUtil."
    var JS_CALLBACK = "Apz.nativeServiceCB"

    override fun sendSuccess(
        callbackId: String?,
        aResult: JSONObject?,
        isKeepAlive: Boolean,
        activity: Activity, webView: WebView, isInUIThread: Boolean
    ) {
        var result = aResult
        try {
            ApzLogger.d(TAG, "Send Success")
            if (result == null) result = JSONObject()
            result.put("id", callbackId)
            result.put("status", true)
            result.put("keepAlive", isKeepAlive)

            sendMsgToJS(
                result,
                activity,
                webView,
                isInUIThread
            )
        } catch (e: JSONException) {
            e.message?.let { ApzLogger.e(TAG, it) }
        }
    }

    override fun sendSuccess(
        callbackId: String?,
        aResult: JSONArray?,
        aResultKey: String?,
        isKeepAlive: Boolean,
        activity: Activity, webView: WebView, isInUIThread: Boolean
    ) {
        try {
            ApzLogger.d(TAG, "Send Success")
            val resultObj = JSONObject()
            resultObj.put("id", callbackId)
            resultObj.put("status", true)
            resultObj.put("keepAlive", isKeepAlive)
            resultObj.put(aResultKey, aResult)

            sendMsgToJS(
                resultObj,
                activity,
                webView,
                isInUIThread
            )
        } catch (e: JSONException) {
            e.message?.let { ApzLogger.e(TAG, it) }
        }
    }

    override fun sendError(
        callbackId: String?,
        errorCode: String?,
        aResult: JSONObject?,
        activity: Activity,
        webView: WebView, isInUIThread: Boolean
    ) {
        var result = aResult
        try {
            ApzLogger.d(TAG, "Send Error : $callbackId")
            if (result == null) {
                result = JSONObject()
            }
            result.put("id", callbackId)
            result.put("status", false)
            result.put("errorCode", errorCode)
            result.put("keepAlive", false)
            sendMsgToJS(result, activity, webView, isInUIThread)
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.message!!)
        }
    }


    override fun sendMsgToJS(
        result: JSONObject,
        activity: Activity,
        webView: WebView,
        isInUIThread: Boolean
    ) {
        val msg = "$JS_CALLBACK($result);"
        if (isInUIThread) {
            activity.runOnUiThread {
                webView.evaluateJavascript(msg, null)
            }
        } else {
            webView.evaluateJavascript(msg, null)
        }
    }

    override fun sendPluginNotSupported(
        callbackId: String?,
        aActivity: ApzActivity<*>?,
        webView: WebView?, b: Boolean
    ) {
        val result = JSONObject()
        result.put("text", "" + callbackId + "PluginNotSupported.")
        sendError(
            callbackId, "APZ-CNT-329", result, aActivity!!,
            webView!!, true
        )
    }


    override fun sendPermissionDenied(
        pluginName: String, callbackId: String?,
        activity: Activity?, webView: WebView?
    ) {
        val result = JSONObject()
        result.put("text", "" + pluginName + "access permissions Denied.")
        sendError(
            callbackId, "APZ-CNT-329", result, activity!!,
            webView!!, true
        )
    }
}
