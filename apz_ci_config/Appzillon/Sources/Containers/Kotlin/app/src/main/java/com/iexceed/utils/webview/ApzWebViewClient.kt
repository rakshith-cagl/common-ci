package com.iexceed.utils.webview

import android.app.Activity
import android.net.http.SslError
import android.text.TextUtils
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.GetSSLStatus
import com.iexceed.common.GetSSlStatusInterface

class ApzWebViewClient(private val activity: Activity, private val webView: WebView) :
    WebViewClient() {
    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        if (AppzillonMainScreen.isSecured) {
            handler.proceed()
        } else {
            AppzillonMainScreen.globalSSLHandler = handler
            GetSSLStatus(activity as AppCompatActivity).execute(activity as GetSSlStatusInterface)
        }
    }

    override fun onPageFinished(view: WebView, url: String) {
        AppzillonMainScreen.APP_LAUNCHED = true
        // set SNONCE value to infra changes start
        if (!TextUtils.isEmpty(AppzillonConstants.SNONCE)) {
            webView.loadUrl("javascript:apz.server.setAppSecToken('Y');")
        } else {
            webView.loadUrl("javascript:apz.server.setAppSecToken('N');")
        }
        // internal Apz SDK call
        // Can be removed since there is no support to SDK
        /*if (MAIN_APP_PARAMS != null) {
            val JS_CALLBACK = "apz.ns.setMainAppParams"
            val msg = "$JS_CALLBACK($MAIN_APP_PARAMS);"
            mWebView.evaluateJavascript(msg, null)
        }*/
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }
}