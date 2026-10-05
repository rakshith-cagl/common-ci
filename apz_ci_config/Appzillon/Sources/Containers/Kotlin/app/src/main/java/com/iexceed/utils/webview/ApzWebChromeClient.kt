package com.iexceed.utils.webview

import android.app.Activity
import android.app.AlertDialog
import android.webkit.ConsoleMessage
import android.webkit.GeolocationPermissions
import android.webkit.WebChromeClient
import com.iexceed.appzillonapp.R

class ApzWebChromeClient(private val activity: Activity) : WebChromeClient() {
    override fun onGeolocationPermissionsShowPrompt(
        origin: String,
        callback: GeolocationPermissions.Callback
    ) {
        val remember = false
        val builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.geolocation_title)
        builder.setMessage(R.string.share_geoloc_msg)
            .setCancelable(true)
            .setPositiveButton(
                R.string.allow_geolocation
            ) { _, _ -> // origin, allow, remember
                callback.invoke(origin, true, remember)
            }
            .setNegativeButton(
                R.string.not_allow_geolocation
            ) { _, _ -> // origin,not allow, remember
                callback.invoke(origin, false, remember)
            }
        val alert = builder.create()
        alert.show()
    }

    override fun onConsoleMessage(cm: ConsoleMessage): Boolean {
        return true
    }
}