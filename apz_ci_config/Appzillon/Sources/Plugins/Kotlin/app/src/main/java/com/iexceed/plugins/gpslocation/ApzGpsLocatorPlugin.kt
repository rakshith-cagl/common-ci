package com.iexceed.plugins.gpslocation

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.iexceed.common.ApzActivity
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzGpsLocatorPlugin private constructor (val webView: WebView,
                          val activity: ApzActivity<*>,
                          override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    lateinit var action: String

    private lateinit var mJsonObj: JSONObject

    var mGpsLocator: GpsLocator? = null

    override var TAG = "ApzGpsLocatorPlugin"

    override fun execute(params: JSONObject) {
        try {
            action = params.getString("action")
            callbackId = params.getString("id")
            this.mJsonObj = params
            if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestForPermission(activity)
            } else {
                enableGPS(action, mJsonObj)
            }
        } catch (ex: JSONException) {
            ApzLogger.e(TAG, ex.toString())
            sendError("APZ-CNT-077")
        }
    }

    private fun enableGPS(action: String, mJsonObj: JSONObject) {
        if (action == "START") {
            when {
                mGpsLocator == null -> {
                    mGpsLocator = GpsLocator(activity, webView, apzPluginUtil)
                    mGpsLocator?.getCoordinates(mJsonObj)
                }
                GpsLocator.isRunning -> {

                    sendError("APZ-CNT-017")
//                    apzPluginUtil.sendError(
//                        callbackId,
//                        "APZ-CNT-017",
//                        null,
//                        activity,
//                        webView,
//                        true
//                    ) //GPS Running
                }
                else -> {
                    mGpsLocator?.getCoordinates(mJsonObj)
                }
            }
        } else if (action == "STOP") {
            ApzLogger.d(TAG, "stopGPS")
            if (GpsLocator.isRunning) {

                stopGPSLocator(mGpsLocator)
                mGpsLocator = null
            } else {
                if (mGpsLocator != null) mGpsLocator = null

                sendError("APZ-CNT-057")
            }
        }
    }

    private fun stopGPSLocator(locator: GpsLocator?){

        locator?.stop()
        val json = JSONObject()
        try {
            json.put("text", "GPS stopped")
        } catch (e: JSONException) {
            //Sonar fix
        }
        apzPluginUtil.sendSuccess(callbackId, json, false, activity, webView, true)
    }

    fun requestForPermission(activity: ApzActivity<*>) {
        activity.startOnPermissionForResult(activity, arrayOf(
            ACCESS_FINE_LOCATION
        ), PluginConstants.APZ_REQ_FINE_LOCATION, object : OnPermissionsResultHandler() {
            override fun handlePermissionResult(
                requestCode: Int,
                permissions: Array<String?>,
                grantResults: IntArray
            ) {
                if (requestCode == PluginConstants.APZ_REQ_FINE_LOCATION) {
                    handleFineLocation(permissions, activity)
                } else {
                    permissionDeniedCallback()
                }
            }
        }
        )
    }

    private fun handleFineLocation(
        permissions: Array<String?>,
        activity: ApzActivity<*>
    ) {
        var denied = false
        var neverAskAgain = false
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permission!!
                )
            ) {
                denied = true
            } else {
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    neverAskAgain = true
                }
            }
        }
        when {
            neverAskAgain -> {
                permissionDeniedCallback()
            }
            denied -> {
                displayReconfirmationMessageAlert().show()
            }
            else -> {
                enableGPS(action, mJsonObj)
            }
        }
    }

    private fun displayReconfirmationMessageAlert(): AlertDialog {
        val message = "To get location, grant permission for app to access gps"
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle("Permission Denied")
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.cancel()
                requestForPermission(activity)
            }.setNegativeButton("Deny") { dialog, _ ->
                dialog.cancel()
                permissionDeniedCallback()
            }
        return alertDialogBuilder.create()
    }

    private fun sendError(errorCode: String){

        apzPluginUtil.sendError(
            callbackId,
            errorCode,
            null,
            activity,
            webView,
            true
        )
    }

    private fun permissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied("GPS location ", callbackId, activity, webView)
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzGpsLocatorPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}
