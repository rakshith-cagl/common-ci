package com.iexceed.plugins.checkgpsstatus

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.webkit.WebView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.iexceed.common.ApzActivity
import com.iexceed.common.ExternalActivityResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 */
class CheckGpsStatus private constructor(webView: WebView,
                                         activity: ApzActivity<*>,
                                         override val apzPluginUtil: IapzPluginUtil
) :
    ApzPlugin()
{
    private var mLocationManager: LocationManager? = null

    //  mAction = "GET_GPS_STATUS"
    //  checks for GPS provider Enabled or Disabled.
    //  mAction = "GET_LOCATION"
    //  checks if device supports PLAY SERVICES and opens GPS enable dialog.
    //  if device not support PLAY SERVICES , redirects to GPS enable setting page.
    override fun execute(params: JSONObject)
    {
        Log.d(Companion.TAG, " INSIDE EXECUTE")
        mLocationManager = aActivity
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            val lAction = params.getString("action")
            callbackId = params.getString("id")
            if (lAction == "CHECK_LOCATION_AVAILABILITY") {
                sendResponse()
            } else if (lAction == "REDIRECT_TO_SETTINGS") {
                moveToSettings()
            }
        } catch (ex: JSONException) {
            invalidJson()
        }
    }

    private fun sendResponse() {
        try {
            val lJson = JSONObject()
            if (gpsStatus) {
                lJson.put("text", "GPS enabled")
                apzPluginUtil.sendSuccess(callbackId, lJson, false, aActivity, aWebview, true)
            } else {
                lJson.put("text", "GPS disabled")
                apzPluginUtil.sendError(
                    callbackId, "APZ-CNT-057", null,
                    aActivity, aWebview, true
                )
            }
        } catch (e: JSONException) {
            invalidJson()
        }
    }

    //   checks if device supports PLAY SERVICES and opens GPS enable dialog.
    //   if device not support PLAY SERVICES , redirects to GPS enable setting page.
    private fun moveToSettings() {
        val lIsGpsEnable = gpsStatus
        val lIsPlayServicesSupported = !checkPlayServices()
        if (!lIsGpsEnable) {
            if (lIsPlayServicesSupported) {
                openGpsDialog()
            } else {
                openGpsSetting()
            }
        } else {
            sendResponse()
        }
    }

    //  Redirects to GPS enable setting page.
    private fun openGpsSetting() {
        val lHandler: ExternalActivityResultHandler = object : ExternalActivityResultHandler() {
            override fun handleActivityResult(
                resultCode: Int, data: Intent?
            ) {
                sendResponse()
            }
        }
        val lSettingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        aActivity.startActivityForResult(lSettingsIntent, PluginConstants.REQUEST_GPS_SETTINGS, lHandler)
    }

    // // Invalid JSON...
    private fun invalidJson() {
        apzPluginUtil.sendError(
            callbackId, "APZ-CNT-077", null,
            aActivity, aWebview, true
        )
    }

    // Opens GPS enable dialog...
    private fun openGpsDialog() {
        val lHandler: ExternalActivityResultHandler = object : ExternalActivityResultHandler() {
            override fun handleActivityResult(
                resultCode: Int, data: Intent?
            ) {
                handleResultForGPS(resultCode)
            }
        }
        aActivity.addExternalActivityResultHandler(PluginConstants.REQUEST_GPS_DIALOG, lHandler)

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setIntervalMillis(5000)
            setWaitForAccurateLocation(true)
        }.build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(aActivity)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(
            aActivity,
            OnSuccessListener<LocationSettingsResponse?> { // All location settings are granted.
                // The client can initialize location requests here.
                try {
                    val lJson = JSONObject()
                    lJson.put("text", "Gps already enabled")
                    apzPluginUtil.sendSuccess(callbackId, lJson, false, aActivity, aWebview, true)
                } catch (je: JSONException) {
                    invalidJson()
                }
            })
        task.addOnFailureListener(aActivity, OnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied,
                // show the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(
                        aActivity,
                        PluginConstants.REQUEST_GPS_DIALOG
                    )
                    try {
                        val lJson = JSONObject()
                        lJson.put("text", "Redirected to Gps dialog")
                        apzPluginUtil.sendSuccess(callbackId, lJson, false, aActivity, aWebview, true)
                    } catch (je: JSONException) {
                        invalidJson()
                    }
                } catch (sendEx: SendIntentException) {
                    // Ignore the error.
                    try {
                        val lJson = JSONObject()
                        lJson.put("errorMessage ", "Failed to open Gps dialog")
                        apzPluginUtil.sendError(callbackId, null, lJson, aActivity, aWebview, true)
                    } catch (je: JSONException) {
                        invalidJson()
                    }
                }
            }
        })
    }

    private fun handleResultForGPS(resultCode: Int) {
        val lJson = JSONObject()
        if (resultCode == Activity.RESULT_OK) {
            try {
                lJson.put("text", "GPS enabled")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            apzPluginUtil.sendSuccess(callbackId, lJson, false, aActivity, aWebview, true)
        } else {
            try {
                lJson.put("errorMessage", "GPS disabled")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-057", lJson,
                aActivity, aWebview, true
            )
        }
    }

    // Checks GPS service status...
    private val gpsStatus: Boolean
        get() = mLocationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true

    // Checks PlayServices availability...
    private fun checkPlayServices(): Boolean {
        val resultCode = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(aActivity.getApplicationContext())
        return resultCode == ConnectionResult.SUCCESS
    }

    companion object {
        private var pluginObj: ApzPlugin? = null

        private const val TAG = "ApzEnableGpsPlugin"
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = CheckGpsStatus(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }
}
