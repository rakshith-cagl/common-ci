package com.iexceed.plugins

import android.app.Activity
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import org.json.JSONArray
import org.json.JSONObject

interface IapzPluginUtil {
    fun sendSuccess(
        callbackId: String?,
        aResult: JSONObject?,
        isKeepAlive: Boolean,
        activity: Activity, webView: WebView, isInUIThread: Boolean
    ){
        //Sonar fix
    }

    fun sendSuccess(
        callbackId: String?,
        aResult: JSONArray?,
        aResultKey: String?,
        isKeepAlive: Boolean,
        activity: Activity, webView: WebView, isInUIThread: Boolean
    ){
        //Sonar fix
    }

    fun sendError(
        callbackId: String?,
        errorCode: String?,
        aResult: JSONObject?,
        activity: Activity,
        webView: WebView, isInUIThread: Boolean
    ){
        //Sonar fix
    }

    fun sendMsgToJS(
        result: JSONObject,
        activity: Activity,
        webView: WebView,
        isInUIThread: Boolean
    ){
        //Sonar fix
    }

    fun sendPluginNotSupported(
        callbackId: String?,
        aActivity: ApzActivity<*>?,
        webView: WebView?, b: Boolean
    ){
        //Sonar fix
    }

    fun sendPermissionDenied(
        pluginName: String, callbackId: String?,
        activity: Activity?, webView: WebView?
    ){
        //Sonar fix
    }
}
