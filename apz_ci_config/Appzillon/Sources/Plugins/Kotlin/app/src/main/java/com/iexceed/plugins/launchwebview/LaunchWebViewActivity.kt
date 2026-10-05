package com.iexceed.plugins.launchwebview

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature
import com.iexceed.appzillonapp.R
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.plugins.launchwebview.ApzLaunchWebView.Companion.trackURLString
import com.iexceed.plugins.launchwebview.ApzLaunchWebView.Companion.trackURLStringArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URLEncoder

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class LaunchWebViewActivity : AppCompatActivity() {

    private val TAG = "LaunchWebViewActivity"

    private var URL: String? = null

    private var callbackId: String? = null

    private var postData: JSONObject? = null

    private var postStr = ""

    private var safeBrowsingIsInitialized = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_launchwebview)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.hide()
        webViewActivity = this
        val intent = intent
        URL = intent.getStringExtra("URL")
        callbackId = intent.getStringExtra("callbackId")
        try {
            if (intent.getStringExtra("cancel_btn").equals("Y", ignoreCase = true)) {
                supportActionBar?.show()
            }
            val result = intent.getStringExtra("postData")
            if (!result.equals("", ignoreCase = true)) {
                postData = JSONObject(result)
            }
            if (postData != null) {

                postStr = getPostString(postData!!)

            }
        } catch (e: Exception) {
            ApzLogger.d(TAG, "Exception $e")
            val failureCallbackRes = JSONObject()
            try {
                failureCallbackRes.put("Error", "Failed$e")
            } catch (ex: JSONException) {
                ApzLogger.e(TAG, ex.toString())
            }

            sendError(callbackId, webViewActivity, mWebView, failureCallbackRes)
            webViewActivity.finish()
        }
        val webview = WebView(this)
        setWebViewProps(webview)

        val pData = URLEncoder.encode(postStr, "UTF-8")
        if (postData != null) webview.postUrl(
            URL!!,pData.toByteArray()
        ) else webview.loadUrl(URL!!)

        setContentView(webview)

        webview.setDownloadListener(getDownloadListener())

        safeBrowsingIsInitialized = false
        if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
            WebViewCompat.startSafeBrowsing(this) { success ->
                safeBrowsingIsInitialized = true
                if (!success!!) {
                    ApzLogger.e(TAG, "Unable to initialize Safe Browsing!")
                }
            }
        }

        webview.webViewClient = getWebClient(webview, webViewActivity, trackURLStringArray, trackURLString, callbackId)

    }

    private fun getDownloadListener(): DownloadListener{

        val listener = DownloadListener { url,_,_,_,_ ->
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        return listener
    }

    private fun getPostString(jsonObject: JSONObject): String {

        var postString = ""
        val itr: Iterator<*> = jsonObject.keys()
        var i = 0
        var key: String
        var value: Any
        while (itr.hasNext()) {
            if (i == 0) {
                key = itr.next() as String
                value = jsonObject[key]
                postString = key + "=" + URLEncoder.encode(value.toString(), "UTF-8")
            } else {
                key = itr.next() as String
                value = jsonObject[key]
                postString = postString + "&" + key + "=" + URLEncoder.encode(value.toString(), "UTF-8")
            }
            i++
        }

        return postString
    }

    private fun sendError(callbackID: String?, activity: Activity,
                          webView: WebView, errorJSON: JSONObject){

        mApzPluginUtil.sendError(
            callbackID,
            "",
            errorJSON,
            activity,
            webView,
            true
        )
    }

    private fun getWebClient(webView: WebView, activity: Activity,
                             trackURLStringArray: Array<String?>?,
                             trackURL: String?, callbackID: String?): WebViewClient{

        val client = object : WebViewClient() {

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {

                val alertDialog = getAlertDialog(error.primaryError, handler, webViewActivity)
                alertDialog.show()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
            }

            override fun onLoadResource(view: WebView, url: String) {
                super.onLoadResource(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                finalURL = url
                var callback = false
                view.loadUrl(url)
                if (trackURLStringArray != null || trackURL != "") {

                    callback = isCallback(trackURLStringArray, url, trackURL)

                    if (callback) {

                        sendSuccessCallback(callbackID, url, activity, webView)

                    }
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onSafeBrowsingHit(
                view: WebView,
                request: WebResourceRequest,
                threatType: Int,
                callback: SafeBrowsingResponse
            ) {
                // The "true" argument indicates that your app reports incidents like
                // this one to Safe Browsing.
                redirectToSafetyBrowsing(activity, callback)
            }
        }

        return client
    }

    fun redirectToSafetyBrowsing(activity: Activity, safeBrowsingResponse: SafeBrowsingResponse){

        if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
            safeBrowsingResponse.backToSafety(true)

            Toast.makeText(activity, "Unsafe web page blocked.", Toast.LENGTH_LONG).show()
        }
    }

    private fun isCallback(trackURLStringArray: Array<String?>?, finalURL: String?, trackURL: String?): Boolean{

        var isCallback = false
        if (trackURLStringArray != null) {
            for (element in trackURLStringArray) {
                if (element.equals(finalURL,ignoreCase = true)) {
                    isCallback = true
                }
            }
        } else if (trackURL.equals(finalURL,ignoreCase = true)) {
            isCallback = true
        }
        return isCallback
    }

    private fun sendSuccessCallback(callbackID: String?, url: String?, activity: Activity, webView: WebView){

        val successCallbackRes = JSONObject()
        try {
            successCallbackRes.put("URL", url)
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }
        mApzPluginUtil.sendSuccess(
            callbackID, successCallbackRes, true, activity,
            webView, true
        )
    }

    private fun getAlertDialog(error: Int, handler: SslErrorHandler?, activity: Activity): AlertDialog {

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        var message = ""
        when (error) {

            SslError.SSL_UNTRUSTED -> message = "Certificate is untrusted."
            SslError.SSL_EXPIRED -> message = "Certificate has expired."
            SslError.SSL_IDMISMATCH -> message = "Certificate ID is mismatched."
            SslError.SSL_NOTYETVALID -> message = "Certificate is not yet valid."
        }
        message += " Do you want to continue anyway?"
        builder.setTitle("SSL Certificate Error")
        builder.setMessage(message)
        builder.setPositiveButton(
            "Ok"
        ) { _, _ -> handler?.proceed() }
        builder.setNegativeButton(
            "Cancel"
        ) { _, _ ->
            handler?.cancel()
            activity.finish()
        }
        return builder.create()
    }

    @SuppressWarnings("SonarLint:Ignore", "SonarQube:Ignore")
    private fun setWebViewProps(wv: WebView){
        val webSettings = wv.settings
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = false
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true
        webSettings.setGeolocationEnabled(true)
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.setGeolocationDatabasePath("")
    }

    override fun onDestroy() {
        super.onDestroy()
        mApzPluginUtil.sendSuccess(callbackId, null, false, webViewActivity, mWebView, true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Take appropriate action for each action item click
        if (item.itemId == R.id.action_close) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.web_activity_close, menu)
        return super.onCreateOptionsMenu(menu)
    }

    companion object{
        lateinit var mWebView: WebView
        lateinit var webViewActivity: Activity
        lateinit var mApzPluginUtil: IapzPluginUtil

        fun setWebView(wb: WebView) {
            mWebView = wb
        }

        fun setApzPluginUtil(apzPluginUtil :IapzPluginUtil){
            mApzPluginUtil = apzPluginUtil
        }
    }

}
