package com.iexceed.plugins.whitelist

import android.app.Activity
import android.content.Context
import android.webkit.WebView

class EnableWhiteList(applicationContext: Context, activity: Activity?, webView: WebView) {

    companion object{
        var isDummyPlugin = true

        private var mWebView: WebView? = null

        private var mContext: Context? = null

        private var mActivity: Activity? = null

    }

     fun EnableWhiteList(context: Context?, activity: Activity?, webView: WebView?) {
        mWebView = webView
        mContext = context
        mActivity = activity
    }

    fun loadWhitelist() {
        //Sonar fix
    }



}