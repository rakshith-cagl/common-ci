package com.iexceed.common

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzLocationManager(private val mActivity: Activity) : LocationListener
{
	private var isGPSEnabled = false
    private var canGetLocation = false
    private var isPassiveEnabled = false
    private var isNetworkEnabled = false
    private var location: Location? = null
    private var gpsLocation: Location? = null
    private var passiveLocation: Location? = null
    private var networkLocation: Location? = null
    private lateinit var locationManager: LocationManager
    private var retryCount = 0

    fun getDeviceLocation(): Location?
    {
        val minTimeBetweenUpdates = 0
        val minDistanceChangeForUpdates = 0
        try {
            locationManager = mActivity.applicationContext
                .getSystemService(Service.LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)
            isPassiveEnabled = locationManager
                .isProviderEnabled(LocationManager.PASSIVE_PROVIDER)
            isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (isGPSEnabled || isNetworkEnabled || isPassiveEnabled) {
                if (handleIfAnyOperatorAvailable(
                        minTimeBetweenUpdates,
                        minDistanceChangeForUpdates
                    )
                ) return null
            } else {
                return location
            }
        } catch (e: Exception) {
            //handle exception
        }
        if (location == null) {
            if (retryCount < 5) {
                retryCount++
                getDeviceLocation()
            } else {
                retryCount = 0
                return null
            }
        }
        return location
    }

    private fun handleIfAnyOperatorAvailable(
        minTimeBetweenUpdates: Int,
        minDistanceChangeForUpdates: Int
    ): Boolean {
        canGetLocation = true
        if (isGPSEnabled) {
            if (ActivityCompat.checkSelfPermission(
                    mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    mActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return true
            }
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, minTimeBetweenUpdates.toLong(),
                minDistanceChangeForUpdates.toFloat(), this
            )
            gpsLocation = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        if (isPassiveEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                minTimeBetweenUpdates.toLong(),
                minDistanceChangeForUpdates.toFloat(), this
            )
            passiveLocation = locationManager
                .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        }
        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTimeBetweenUpdates.toLong(),
                minDistanceChangeForUpdates.toFloat(), this
            )
            networkLocation = locationManager
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        checkLocationFromIndividualOperator()
        return false
    }

    private fun checkLocationFromIndividualOperator() {
        var gpsAccuracy = 0f
        var passiveAccuracy = 0f
        var networkAccuracy = 0f
        var mostAccurate = 0f
        if (gpsLocation != null && gpsLocation!!.hasAccuracy()) {
            gpsAccuracy = gpsLocation!!.accuracy
            location = gpsLocation
            mostAccurate = gpsAccuracy
        }
        if (passiveLocation != null && passiveLocation!!.hasAccuracy()) {
            passiveAccuracy = passiveLocation!!.accuracy
            if (passiveAccuracy < mostAccurate || mostAccurate == 0f) {
                location = passiveLocation
                mostAccurate = passiveAccuracy
            }
        }
        if (networkLocation != null && networkLocation!!.hasAccuracy()) {
            networkAccuracy = networkLocation!!.accuracy
            if (networkAccuracy < mostAccurate || mostAccurate == 0f) {
                location = networkLocation
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        // no need to implement
    }
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // no need to implement
    }
    override fun onProviderEnabled(provider: String) {
        // no need to implement
    }
    override fun onProviderDisabled(provider: String) {
        // no need to implement
    }
}
