package com.iexceed.plugins.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.AppzillonUtils.validateMapURL
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.currentlocation.CurrentLocation
import org.json.JSONException
import org.json.JSONObject

@SuppressLint("SetJavaScriptEnabled")
class Map : Activity() {
    lateinit var mwebview: WebView
    lateinit var cancel:TextView
    var jsontext: String = ""
    var jobj: JSONObject = JSONObject()
    var headerText: String? = null
    var headerColor: String = ""

    private  lateinit var webSettings: WebSettings
    private val TAG = "MAP"
    private var callbackId: String? = null
    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        webViewActivity =this
        mwebview = findViewById(R.id.webView1)
        cancel = findViewById(R.id.cancelbtn)
        val i: Intent = getIntent()

        i.getStringExtra("mapval")?. let {
            jsontext = it
        }

        try {

            jobj = JSONObject(jsontext)
            headerText = jobj.optString("headerText")
            headerColor = jobj.optString("headerColor")
            mapkey = jobj.optString("mapKey")
            callbackId = jobj.getString("id")


        } catch (e: Exception) {
            //Do Nothing
        }
        if (headerColor.trim().isNotEmpty()) {
            val textLayout: LinearLayout = findViewById<View>(R.id.logo) as LinearLayout
            textLayout.setVisibility(View.VISIBLE)
            textLayout.setBackgroundColor(Color.parseColor(headerColor))
            val title: TextView = findViewById<View>(R.id.headerText) as TextView
            title.setText(headerText)
        }
        val cancel:TextView=findViewById(R.id.cancelbtn)
        cancel.setOnClickListener(View.OnClickListener {
            failureCallback("MapView Closed")
            finish() })
        val connectivityManager: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo: NetworkInfo? = connectivityManager.getActiveNetworkInfo()
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            val location: Location? =  CurrentLocation.getLocation(this)
            if(isLocationEnabled()&&hasLocationPermissions()) {
                if (location != null) {
                    try {
                        jobj.put("currLat", location.latitude)
                        jobj.put("currLng", location.longitude)
                        jobj.put("locationName", "me")
                        jobj.put("locationDescription", "I am here")
                        jsontext = jobj.toString()
                    } catch (e: JSONException) {
                        //Do Nothing
                    }
                    mwebview.addJavascriptInterface(this, "markerLocation")
                    webSettings = mwebview.getSettings()
                    webSettings.setJavaScriptEnabled(true)
                    webSettings.setBuiltInZoomControls(false)
                    webSettings.setDomStorageEnabled(true)
                    webSettings.setLoadsImagesAutomatically(true)
                    webSettings.setGeolocationEnabled(true)
                    webSettings.setJavaScriptCanOpenWindowsAutomatically(true)
                    webSettings.setGeolocationDatabasePath("")

                    var url = ""

                    if (AppzillonMainScreen.OTAREQUIRED.equals("Y",true)) {
                        url =
                            "file:///" + AppzillonMainScreen.SANDBOX_LOC + "/" + AppzillonMainScreen.ASSET_APP_LOC + "screens/Map.html"
                    } else {
                        url =
                            "file:///android_asset/" + AppzillonMainScreen.ASSET_APP_LOC + "screens/Map.html"
                    }
                    mwebview.loadUrl(AppzillonUtils.validateApzWebViewURL(url).toString())
                    mwebview.setWebChromeClient(object : WebChromeClient() {})
                    mwebview.webViewClient = object : WebViewClient() {
                        override fun onReceivedSslError(
                            view: WebView?,
                            handler: SslErrorHandler?,
                            error: SslError?
                        ) {
                            super.onReceivedSslError(view, handler, error)
                            failureCallback("Could not retrieve current location.")
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)

                            //mwebview.loadUrl("javascript:(function() { showPosition('" + jsontext + "')})()");
                            mwebview.loadUrl(validateMapURL("javascript:(function() { showPosition('$jsontext')})()")!!)
                        }
                    }

                } else {
                    failureCallback("Could not retrieve current location.")
                }
            }
            else {
                failureCallback("Location not enabled")
            }
        } else {
            failureCallback("No network available.")
        }
    }

    protected override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBackPressed() {

        failureCallback("User cancelled.")
        super.onBackPressed()
    }

    fun failureCallback(jsonText: String?) {
        webViewActivity.runOnUiThread(Runnable {
            val json = JSONObject()
            try {
                if (jsonText.equals("MapView Closed")) {
                    json.put("message", jsonText)
                } else {
                    json.put("errorMessage", jsonText)
                }
            } catch (e: JSONException) {
                //Do Nothing
            }

            mApzPluginUtil.sendError(
                callbackId,
                "",
                json,
                webViewActivity,
                mWebView,
                true
            )
            finish()
        })
    }

    companion object {
        private var mapkey = ""
        lateinit var mWebView: WebView
        lateinit var webViewActivity: Activity
        lateinit var mApzPluginUtil: IapzPluginUtil
        fun setWebView(wb: WebView) {
            mWebView = wb
        }
        fun setApzPluginUtil(apzPluginUtil : IapzPluginUtil){
            mApzPluginUtil = apzPluginUtil
        }
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            webViewActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

}
