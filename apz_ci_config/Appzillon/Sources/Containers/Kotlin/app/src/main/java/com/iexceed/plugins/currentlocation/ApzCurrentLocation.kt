package com.iexceed.plugins.currentlocation

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.ApzActivity
import com.iexceed.common.ApzLocationManager
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.location.LocationHelper
import org.json.JSONObject
import java.util.*


/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzCurrentLocation private constructor (val webView: WebView, val activity: ApzActivity<*>,
                         override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    private var mJsonObj: JSONObject? = null

    private lateinit var permissions: Array<String>

    var isGPS: Boolean = false

    override var TAG = "ApzCurrentLocation"

    override fun execute(params: JSONObject) {
        try {
            this.callbackId = params.getString("id")
            mJsonObj = params
            val isSystemLocationEnabled = AppzillonUtils.isSystemLocationEnabled(activity)
            if(!isSystemLocationEnabled) {
                val eJson = JSONObject()
                eJson.put("errorCode", "")
                eJson.put("errorMessage", "No providers available to fetch location")
                apzPluginUtil.sendError(
                    callbackId, "APZ-CNT-274",
                    eJson, activity,
                    webView, true
                )
                return
            }
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                requestForPermission()
            } else {
                isGPS = AppzillonUtils.checkGooglePlayServicesAvailability(activity)
                fetchCurrentLocation()
            }
        } catch (e: Exception) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(
                callbackId = "", "APZ-CNT-077", null, activity,
                webView, true
            )
        }
    }

    private fun requestForPermission() {
        this.activity.startOnPermissionForResult(activity,
            permissions,
            PluginConstants.APZ_REQ_LOCATION,
            object : OnPermissionsResultHandler() {
                override fun handlePermissionResult(
                    requestCode: Int,
                    permissions: Array<String?>,
                    grantResults: IntArray
                ) {
                    if (requestCode == PluginConstants.APZ_REQ_LOCATION) {
                        handleLocationPermissionResult(permissions)
                    } else {
                        permissionDeniedCallback()
                    }
                }
            }
        )
    }

    private fun handleLocationPermissionResult(permissions: Array<String?>) {
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
                displayReconfirmationMessage()
            }
            else -> {
                isGPS = AppzillonUtils.checkGooglePlayServicesAvailability(activity)
                fetchCurrentLocation()
                hasLocationPermission = true
            }
        }
    }

    private fun displayReconfirmationMessage() {
        val message = "To access location ,allow app to access by granting requested permissions"
        val alertDialogBuilder = AlertDialog.Builder(activity)
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
        apzPluginUtil.sendPermissionDenied("Location", callbackId, this.activity, this.webView)
    }

    private fun fetchCurrentLocation() {
        var latDeg=""
        var longDeg=""
        if(isGPS){
            ApzFusedLocation(activity, apzPluginUtil).requestLocation(callbackId,webView)
        }else{
            val gps = ApzLocationManager(activity)
            val latLng = gps.getDeviceLocation()
            try {
                if (latLng != null) {
                    val successJson = JSONObject()
                    val latD = latLng.latitude
                    val lngD = latLng.longitude
                    val accuracy = latLng.accuracy.toDouble()
                    val stringLatLng = LocationHelper.getFormattedLocationInDegree(latD,lngD)
                    if(null != stringLatLng && stringLatLng.contains(";")) {
                        val latLngArr = stringLatLng.split(";")
                        latDeg = latLngArr[0]
                        longDeg = latLngArr[1]
                    }
                    successJson.put("latitude", latD.toString())
                    successJson.put("longitude", lngD.toString())
                    successJson.put("accuracy", accuracy.toString())
                    successJson.put("formattedLatitude",latDeg)
                    successJson.put("formattedLongitude",longDeg)
                    val geocoder = Geocoder(activity, Locale.getDefault())
                    val addresses: List<Address>
                    try {
                        addresses = LocationHelper.getCurrentLocationFromGeocode(geocoder, latLng)
                        val obj = addresses[0]
                        successJson.put("address", obj.getAddressLine(0))
                        successJson.put("countryCode", obj.countryCode)
                    } catch (e: Exception) {
                        //handle exception
                    }
                    apzPluginUtil.sendSuccess(
                        callbackId,
                        successJson, false, activity,
                        webView, true
                    )
                } else {
                    ApzLogger.e(TAG, "Unable to reach Location")
                    val eJson = JSONObject()
                    eJson.put("errorCode", "")
                    eJson.put("errorMessage", "No providers available to fetch location")
                    apzPluginUtil.sendError(
                        callbackId, "APZ-CNT-274",
                        eJson, activity,
                        webView, true
                    )
                }
            } catch (e: Exception) {
                ApzLogger.e(TAG, e.toString())
            }
        }

    }


    companion object {
        private var pluginObj: ApzPlugin? = null
        var hasLocationPermission = false
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzCurrentLocation(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }
}
