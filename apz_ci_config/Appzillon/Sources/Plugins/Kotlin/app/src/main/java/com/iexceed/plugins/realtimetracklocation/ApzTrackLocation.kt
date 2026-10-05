package com.iexceed.plugins.realtimetracklocation

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.ApzActivity
import com.iexceed.common.ExternalActivityResultHandler
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

class ApzTrackLocation private constructor (webView: WebView, activity: ApzActivity<*>,
                       override val apzPluginUtil: IapzPluginUtil) : ApzPlugin()
{
    private var mJsonObj: JSONObject? = null
    override var TAG = "ApzTrackLocation"
    lateinit var mLocationManager : LocationManager

    override fun execute(params: JSONObject)
    {
        mLocationManager = aActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var action = ""
        try {
            callbackId = params.getString("id")
            action = params.getString("action")
            mJsonObj = params
        } catch (e: JSONException) {
            //Sonar fix
        }

        if (action.equals("START", ignoreCase = true))
        {
            if (ActivityCompat.checkSelfPermission(
                    aActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestForPermission()
            } else if(!isGpsEnabled()!!) {
                enableGps()
            }else{
                startTrackingLocation()
            }
        } else {
            stopService("Stopped.")
        }
    }

    private val handler: ExternalActivityResultHandler = object : ExternalActivityResultHandler() {
        override fun handleActivityResult(resultCode: Int, data: Intent?) {
            ApzLogger.d(TAG, "handleActivityResult : $resultCode")
            if (resultCode == Activity.RESULT_OK) {
                ApzLogger.d(TAG, "Location Settings Enabled.")
                startTrackingLocation()
            } else {
                stopService("Location Request Cancelled")
            }
        }
    }

    private fun stopService(messsage: String)
    {
        ApzLogger.d(TAG," INSIDE STOP TrackLocation SERVICE")
        val intent = Intent(aActivity, TrackLocationService::class.java)
        aActivity.stopService(intent)
        val json = JSONObject()
        try {
            json.put("text", messsage)
        } catch (e: JSONException) {
            e.printStackTrace()
            apzPluginUtil.sendError(callbackId, "APZ-CNT-211", json, aActivity, aWebview, true)
        }
    }


    private fun isGpsEnabled():Boolean? {
        return mLocationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun isGpsHardWarePresent():Boolean {
        return AppzillonUtils.isGpsHardWarePresent(aActivity)
    }

    private fun startTrackingLocation()
    {
        val interval: Int
        val disp: Int
        val fastestInterval: Int
        try {
            this.aActivity.addExternalActivityResultHandler(REQUEST_CHECK_SETTINGS, handler)
            val intervalBased = mJsonObj!!.getString("isIntervalBased")
            if (intervalBased.equals("Y", ignoreCase = true)) {
                interval = mJsonObj!!.getInt("interval")
                disp = 0
                fastestInterval = interval / 2
            } else {
                interval = 0
                disp = mJsonObj!!.getInt("displacement")
                fastestInterval = 0
            }
            val intent = Intent(aActivity, TrackLocationService::class.java)
            intent.putExtra("updateInterval", interval)
            intent.putExtra("fastestInterval", fastestInterval)
            intent.putExtra("displacement", disp)
            intent.putExtra("isIntervalBased", intervalBased)
            aActivity.startForegroundService(intent)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun requestForPermission()
    {
        this.aActivity.startOnPermissionForResult(
            aActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PluginConstants.APZ_REQ_LOCATION,
            object : OnPermissionsResultHandler() {
                override fun handlePermissionResult(
                    requestCode: Int,
                    permissions: Array<String?>,
                    grantResults: IntArray) {
                    if (requestCode == PluginConstants.APZ_REQ_LOCATION) {
                        verifyLocationPermissionResult(permissions)
                    } else {
                        permissionDeniedCallback()
                    }
                }
            }
        )
    }

    private fun verifyLocationPermissionResult(permissions: Array<String?>) {
        var denied = false
        var neverAskAgain = false
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    aActivity,
                    permission!!
                )
            ) {
                denied = true
            } else {
                if (ActivityCompat.checkSelfPermission(
                        aActivity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    neverAskAgain = true
                }
            }
        }
        if (neverAskAgain) {
            permissionDeniedCallback()
        } else if (denied) {
            displayReconfirmationMessage()
        } else {
            startTrackingLocation()
        }
    }

    private fun displayReconfirmationMessage() {
        val message = "To access location ,allow app to access by requested permissions"
        val alertDialogBuilder = AlertDialog.Builder(aActivity)
        alertDialogBuilder.setTitle("Permission Denied")
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.cancel()
                requestForPermission()
            }.setNegativeButton("Deny") { dialog, _ ->
                dialog.cancel()
                permissionDeniedCallback()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun permissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied("Location", callbackId, this.aActivity, this.aWebview)
    }

    private fun enableGps()
    {
        val mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setIntervalMillis(5000)
            setWaitForAccurateLocation(true)
        }.build()

        val settingsBuilder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        settingsBuilder.setAlwaysShow(true)

        val result = LocationServices.getSettingsClient(aActivity).checkLocationSettings(settingsBuilder.build())
        result.addOnCompleteListener { task ->
            //getting the status code from exception
            try {
                task.getResult(ApiException::class.java)
            } catch (ex: ApiException)
            {
                when (ex.statusCode)
                {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try
                    {
                        // Show the dialog by calling startResolutionForResult(), and check the result
                        // in onActivityResult().
                        val resolvableApiException = ex as ResolvableApiException
                        resolvableApiException.startResolutionForResult(aActivity ,REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                        ApzLogger.e(TAG,"PendingIntent unable to execute request.")
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        ApzLogger.e(TAG, "Something is wrong in your GPS")
                    }
                }
            }
        }
    }


    init {
        this.aActivity = activity
        this.aWebview = webView
    }


    companion object {
        private var plugin: ApzPlugin? = null
        var REQUEST_CHECK_SETTINGS = 1010
        fun createPlugin(webView: WebView, aActivity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (plugin == null) {
                plugin = ApzTrackLocation(webView, aActivity, apzPluginUtil)
            }
            return plugin
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}
