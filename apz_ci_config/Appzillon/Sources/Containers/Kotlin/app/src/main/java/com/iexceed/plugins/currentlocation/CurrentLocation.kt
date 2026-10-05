package com.iexceed.plugins.currentlocation

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import com.iexceed.common.ApzLocationManager
/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
object CurrentLocation {
    private val locationManager: LocationManager? = null
    var isNetworkEnabled = false

    // The minimum distance to change Updates in meters
    private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 1 // 10 meters

    // The minimum time between updates in milliseconds
    private const val MIN_TIME_BW_UPDATES: Long = 1 // 1 minute
    private const val TAG = ""

    // flag for ApzLocationManager status
    private const val canGetLocation = false
    private val location: Location? = null // location
    private var thresholdDistance = 0.0
    private var presentLoc: Location? = null

    fun getLocation(activity: Activity): Location?
    {
        val gps = ApzLocationManager(activity)
        val loc: Location? = gps.getDeviceLocation()
        if (thresholdDistance <= 0.0) stopLocationListener()
        return loc
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    private fun showSettingsAlert(mActivity: Activity) {
        val alertDialog = AlertDialog.Builder(mActivity)

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings")

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings"
        ) { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mActivity.startActivity(intent)
        }

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel"
        ) { dialog, _ -> dialog.cancel() }

        // Showing Alert Message
        if (mActivity.toString().contains("LandingPageActivity")) {
            //Sonar fix
        } else {
            alertDialog.show()
        }
    }

    /**
     * Stop using Location listener Calling this function will stop using ApzLocationManager in app
     */
    fun stopLocationListener() {
        if (locationManager != null) {
            //handle exception
        }
    }

    private val mLocationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            //For Augmented Reality
            if (presentLoc != null) {
                location.distanceTo(presentLoc!!).toDouble()
                setPresentLocation(location)
            } else {
                //handle exception as loc is null
            }
        }

        override fun onProviderDisabled(provider: String) {
            //Sonar fix
        }
        override fun onProviderEnabled(provider: String) {
            //Sonar fix
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            //Sonar fix
        }
    }

    fun setThreshold(th: Double) {
        thresholdDistance = th
    }

    fun setPresentLocation(loc: Location?) {
        presentLoc = loc
    }
}