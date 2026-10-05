package com.iexceed.plugins.gpslocation

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.iexceed.common.ApzActivity
import com.iexceed.common.ApzLocationManager
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class GpsLocator(val activity: ApzActivity<*>,
                 val webView: WebView,
                 private val apzPluginUtil: IapzPluginUtil)
    : Service(), LocationListener {

    private var locationManager: LocationManager? = null

    private var mPeriodicity = "none"

    private var mDistanceInterval:Float = 0F //meters

    private var mTimeInterval:Long = 0 //milliSeconds

    companion object{
        var isRunning = false
    }

    private var location: Location? = null

    private var canGetLocation = false

    private var callbackId: String? = null

    private var gpsRes: JSONObject = JSONObject()

    private var myTimer: Timer? = null

    private var isTimerRunning = false

    fun getCoordinates(jsonData: JSONObject) {
        val isGPSEnabled: Boolean
        val isNetworkEnabled: Boolean
//        var periodicityPresent: String
        val gps = ApzLocationManager(activity)
        try {
//            gpsRes = JSONObject()
            callbackId = jsonData.getString("id")
            mPeriodicity = jsonData.optString("periodicity")
            if (mPeriodicity.equals(
                    "intervalBased",
                    ignoreCase = true
                ) || mPeriodicity.equals("timed", ignoreCase = true)
            ) {
                mDistanceInterval = jsonData.optString("distanceInterval").toFloat()
                val ti = jsonData.optString("timeInterval")
                if (ti.isNotEmpty() && !ti.equals("", ignoreCase = true)) {
                    mTimeInterval = ti.toLong()
                }
            }
        } catch (e: JSONException) {
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", null,
                activity, webView, true
            )
            return
        }
        try {
            locationManager = getLocationManager(activity)
//            locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
            isGPSEnabled = getIsGPSEnabled(locationManager)
            isNetworkEnabled = getIsNetworkEnabled(locationManager)
//            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
//            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled) {
                enableProvider()
                return
            } else {
                canGetLocation = true
                updateLocation(isNetworkEnabled, gps, isGPSEnabled)
            }
        } catch (e: Exception) {
            //Sonar fix
        }
        isRunning = true
        try {
            setLocationData(gpsRes, location)
        } catch (e: JSONException) {
            return
        }
        if (mPeriodicity == "none") {
            pause(locationManager)
            callSuccessCallback(gpsRes)
        } else if (isRunning) {
            callSuccessCallback(gpsRes)
        }
    }

    private fun updateLocation(
        isNetworkEnabled: Boolean,
        gps: ApzLocationManager,
        isGPSEnabled: Boolean
    ) {
        if (isNetworkEnabled) {
            location =
                getLocationFromNetwork(locationManager, mTimeInterval, mDistanceInterval, gps)
        } else if (isGPSEnabled) {
            location = getLocationFromGPS(locationManager, mTimeInterval, mDistanceInterval, gps)
        }
    }

    private fun getLocationManager(activity: Activity): LocationManager{

        return activity.getSystemService(LOCATION_SERVICE) as LocationManager
    }

    private fun getLocationFromGPS(locationManager: LocationManager?,
                                   timeInterval: Long, distanceInterval: Float,
                                   apzLocationManager: ApzLocationManager): Location?{

        var location: Location? = null

        try {
            locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                timeInterval,
                distanceInterval,
                this)
            location = apzLocationManager.getDeviceLocation()!!
        } catch (e: Exception) {
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-059", null,
                activity, webView, true
            )
        }

        return location
    }

    private fun getLocationFromNetwork(locationManager: LocationManager?,
                                       timeInterval: Long, distanceInterval: Float,
    apzLocationManager: ApzLocationManager): Location?{

        var location: Location? = null
        try {
            if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                locationManager!!.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    timeInterval,
                    distanceInterval,
                    this)
                location = apzLocationManager.getDeviceLocation()!!
            }
        } catch (e: Exception) {
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", null,
                activity, webView, false
            )
        }

        return location
    }

    private fun setLocationData(jsonData: JSONObject, location: Location?){

        jsonData.put("latitude", location?.latitude.toString())
        jsonData.put("longitude", location?.longitude.toString())
        jsonData.put("altitude", location?.altitude.toString())
        jsonData.put("accuracy", location?.accuracy.toString())
        jsonData.put("altitudeaccuracy", "")
        jsonData.put("heading", "")
        jsonData.put("speed", location?.speed.toString())
    }

    private fun getIsGPSEnabled(locationManager: LocationManager?): Boolean{

        var isEnabled = false

        if (locationManager != null){

            isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        }
        return isEnabled
    }

    private fun getIsNetworkEnabled(locationManager: LocationManager?): Boolean{

        var isNetworkEnabled = false
        if (locationManager != null){

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        }

        return isNetworkEnabled
    }

    private fun enableProvider() {
        apzPluginUtil.sendError(
            callbackId,
            "APZ-CNT-057",
            null,
            activity,
            webView,
            true
        )
    }

    private fun timerMethod(jsonData: JSONObject) {
        activity.runOnUiThread { callSuccessCallback(jsonData) }
    }

    private fun callSuccessCallback(jsonData: JSONObject) {
        apzPluginUtil.sendSuccess(
            callbackId, jsonData, true,
            activity, webView, true
        )
    }

    fun pause(locationManager: LocationManager?) {
        if (isRunning) {

            locationManager?.removeUpdates(this)
            isRunning = false
        }
    }

    fun stop() {
        if (isRunning) {

            locationManager?.removeUpdates(this)
            isRunning = false
            try {
                if (myTimer != null) {
                    myTimer!!.cancel()
                    myTimer = null
                    isTimerRunning = false
                }
            } catch (e: Exception) {
                //Sonar fix
            }
        }
    }

    override fun onLocationChanged(newLocation: Location) {
        synchronized(this) {
            try {
                setLocationData(gpsRes, newLocation)
            } catch (e: JSONException) {
                return
            }
            if (mPeriodicity == "none") {
                pause(locationManager)
                callSuccessCallback(gpsRes)
            } else if (isRunning) {
                if (mTimeInterval > 0 && !mPeriodicity.equals("onChange", ignoreCase = true)) {
                    if (!isTimerRunning) {
                        isTimerRunning = true
                        myTimer = Timer()
                        myTimer!!.schedule(object : TimerTask() {
                            override fun run() {
                                timerMethod(gpsRes)
                            }
                        }, 0, mTimeInterval)
                    }
                } else {
                    callSuccessCallback(gpsRes)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
