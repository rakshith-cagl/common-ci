package com.iexceed.plugins.currentlocation

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.utils.location.LocationHelper
import org.json.JSONObject
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzFusedLocation(val activity: ApzActivity<*>,
val apzPluginUtil: IapzPluginUtil) {

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest
    private var mCurrentLocation: Location? = null
    private lateinit var locationCallback: LocationCallback

    init {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setIntervalMillis(5000)
            setWaitForAccurateLocation(true)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        mCurrentLocation = location
                        if (mFusedLocationClient != null) {
                            mFusedLocationClient?.removeLocationUpdates(locationCallback)
                        }
                    }
                }
            }
        }
    }

    fun requestLocation(callbackId: String?, webView: WebView) {
        var latDeg = ""
        var longDeg = ""
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            apzPluginUtil.sendPermissionDenied("Location", callbackId, activity, webView)
        }else{
            mFusedLocationClient?.lastLocation?.addOnSuccessListener(activity) { location ->
                if (location != null) {
                    val latD = location.latitude
                    val lngD = location.longitude
                    val stringLatLng = LocationHelper.getFormattedLocationInDegree(latD,lngD)
                    val pair = updateFormattedCoordinates(stringLatLng, latDeg, longDeg)
                    latDeg = pair.first
                    longDeg = pair.second
                    val successJson = JSONObject()
                    successJson.put("latitude", location.latitude.toString())
                    successJson.put("longitude", location.longitude.toString())
                    successJson.put("accuracy", location.accuracy.toString())
                    successJson.put("formattedLatitude",latDeg)
                    successJson.put("formattedLongitude",longDeg)
                    val geocoder = Geocoder(activity, Locale.getDefault())
                    val addresses: List<Address>
                    try {
                        addresses =
                            LocationHelper.getCurrentLocationFromGeocode(geocoder, location)
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
                    mFusedLocationClient?.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                }
            }
        }

    }

    private fun updateFormattedCoordinates(
        stringLatLng: String?,
        latDeg: String,
        longDeg: String
    ): Pair<String, String> {
        var latDeg1 = latDeg
        var longDeg1 = longDeg
        if (null != stringLatLng && stringLatLng.contains(";")) {
            val latLngArr = stringLatLng.split(";")
            latDeg1 = latLngArr[0]
            longDeg1 = latLngArr[1]
        }
        return Pair(latDeg1, longDeg1)
    }

}
