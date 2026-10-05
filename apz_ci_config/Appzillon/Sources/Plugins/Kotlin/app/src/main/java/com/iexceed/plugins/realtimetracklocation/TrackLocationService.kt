package com.iexceed.plugins.realtimetracklocation

import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.AppzillonUtils.getDeviceId
import com.iexceed.common.ServerUtilities
import com.iexceed.common.StringUtils
import com.iexceed.common.StringUtils.getString
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class TrackLocationService : Service()
{
    private var mLocationRequest: LocationRequest? = null
    private var mLastLocation: Location? = null
    private var isIntervalBased: String? = "Y"
    private var LONGITUDE = 0.0
    private var LATITUDE = 0.0
    private val TAG = "APZ_LOC_SERVICE"
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int
    {
        Log.i(TAG, "Service onStartCommand")
        if (intent != null) {
            isIntervalBased = intent.getStringExtra("isIntervalBased")
            //appId = intent.getStringExtra("appid");
//            deviceId = intent.getStringExtra("deviceid");
//            serverUrl = intent.getStringExtra("serverUrl");
            UPDATE_INTERVAL = intent.getIntExtra("updateInterval", 0)
            FATEST_INTERVAL = intent.getIntExtra("fastestInterval", 0)
            DISPLACEMENT = intent.getIntExtra("displacement", 0)
        } else {
            Log.e(TAG, "No Values from Intent")
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service onCreate")
        if (checkPlayServices()) {
            onConnected()
        } else {
            Log.e(TAG, "Play services Not Available")
            this.stopSelf()
        }
        startForeground()
    }


    private fun startForeground()
    {
        var lCategory = Notification.CATEGORY_SERVICE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            lCategory = Notification.CATEGORY_LOCATION_SHARING
        }
        val channelId =
            createNotificationChannel("Track_location_service",
                applicationContext.getString(R.string.tracklocation_notification_title))
        //This is the intent of PendingIntent
        val intentAction = Intent(applicationContext, NotificationActionsReceiver::class.java)
        //This is optional if you have more than one buttons and want to differentiate between two
        intentAction.putExtra("action", "close")
        val pIntent =
            PendingIntent.getBroadcast(applicationContext, 101, intentAction, PendingIntent.FLAG_IMMUTABLE)
        val notificationBuilder = NotificationCompat.Builder(this, channelId )

        val notification = notificationBuilder.setOngoing(true).setAutoCancel(true)
            .setDefaults(Notification.FLAG_AUTO_CANCEL)
            .setSmallIcon(R.drawable.notification)
            .setPriority(PRIORITY_MAX)
            .setCategory(lCategory)
            //.setDeleteIntent(pIntent)
            .setContentTitle(applicationContext.getString(R.string.tracklocation_notification_title))
            .setContentText(applicationContext.getString(R.string.tracklocaction_notification_message))
            .addAction(0, applicationContext.getString(R.string.notification_stop_sharing_action_text), pIntent)

            .build()
        startForeground(101, notification)
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_HIGH)
        chan.description = channelName
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        chan.importance = NotificationManager.IMPORTANCE_HIGH
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


    /**
     * Method to verify google play services on the device
     */
    private fun checkPlayServices(): Boolean
    {
        val resultCode = GoogleApiAvailability.
        getInstance().isGooglePlayServicesAvailable(applicationContext)
        return resultCode == ConnectionResult.SUCCESS
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }


    private fun onConnected() {
        Log.i(TAG, "onConnected")
        startLocationUpdates()
        sendUpdatedLocation()
    }

    /**
     * Method to display the location on UI
     */
    private fun sendUpdatedLocation()
    {
        Log.i(TAG, "sendUpdate")
        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    onNewLocation(locationResult.lastLocation!!)
                }
            }

            mFusedLocationClient?.requestLocationUpdates(mLocationRequest!!,
                mLocationCallback!!, Looper.myLooper()!!)
        } catch (ex: SecurityException) {
            Log.e(TAG, "SecurityException : " + ex.message)
        }
    }

    private fun onNewLocation(location: Location)
    {
        Log.d(TAG, "New location: latitude :  ${location.latitude}")
        Log.d(TAG, "New location: longitude :  ${location.longitude}")
        mLastLocation = location
        if (mLastLocation != null)
        {
            LATITUDE = mLastLocation!!.latitude
            LONGITUDE = mLastLocation!!.longitude
            updateLocToServer()
        } else {
            Log.e(
                TAG,
                "Couldn't get the location. Make sure location is enabled on the device")
        }
    }

    /**
     * Starting the location updates
     */
    private fun startLocationUpdates()
    {
        try {
            mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000).apply {
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setIntervalMillis(5000)
                setWaitForAccurateLocation(true)
            }.build()
        } catch (ex: SecurityException) {
            Log.e(TAG, "SecurityException : " + ex.message)
        } catch (e: Exception) {
            Log.e(TAG, "Exception : " + e.message)
        }
    }

    private fun removeLocationUpdates() {
        Log.i(TAG, "Removing location updates")
        try {
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback!!)
            stopSelf()
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not remove updates. $unlikely")
        }
    }

    private fun updateLocToServer ()
    {
        val jsonObject = JSONObject()
        try {
            val header = JSONObject()
            header.put("appId", getString(StringUtils.APP_ID))
            header.put("sessionId", "")
            header.put("deviceId", getDeviceId())
            header.put("requestID", "")
            header.put("async", true)
            header.put("userId", "")
            header.put("screenId", "trackLocationService")
            header.put("status", true)
            header.put("source", "Appzillon")
            header.put("interfaceId", "appzillonTrackLocation")
            header.put("os", "Android")
            header.put("longitude", "" + LONGITUDE)
            header.put("latitude", "" + LATITUDE)
            val reqBody = JSONObject()
            val appFileReq = JSONObject()
            appFileReq.put("appId", getString(StringUtils.APP_ID))
            appFileReq.put("deviceId", getDeviceId())
            appFileReq.put("longitude", "" + LONGITUDE)
            appFileReq.put("latitude", "" + LATITUDE)
            reqBody.put("appzillonTrackLocationRequest", appFileReq)
            jsonObject.put(AppzillonConstants.APPZILLON_HEADER, header)
            jsonObject.put(AppzillonConstants.APPZILLON_BODY, reqBody)
        } catch (e: JSONException) {
            //Sonar fix
        }

        val lPayLoadEncryption = getString("payloadEncryption");
        // Change this approach after DI integration
        //val apiInterface = RetrofitBuilder.apiService
        val apiInterface = RetrofitBuilder.getRetrofit(applicationContext).create(ApiService::class.java)
        val clientNonce = System.currentTimeMillis().toString()
        val interfaceId  = "appzillonTrackLocation"

        val lReqBody: RequestBody = if(lPayLoadEncryption == "Y") {
            Log.d(TAG, " Encryption Enabled")
            val lEncryptedObj = ServerUtilities.getEncryptedRequest(applicationContext, jsonObject.toString())
            lEncryptedObj.toString().toRequestBody()
        } else{
            jsonObject.toString().toRequestBody()
        }

        val call = apiInterface.requestTrackLocation(lReqBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>,
                                    response: Response<ResponseBody>)
            {
                Log.d(TAG, "  TrackLocation  response ")
                val lRes = response
                val lApzResponse = lRes.body()?.string()
                if (lRes.isSuccessful && lApzResponse != null)
                {
                    if(lPayLoadEncryption == "Y")
                    {
                        val lResponseObj = ServerUtilities.getDecryptedResponseObj(
                            applicationContext as (AppzillonMainScreen),
                            lApzResponse,
                            clientNonce, interfaceId)
                        if (lResponseObj != null)
                        {
                            Log.d(TAG, " Decrypted response Obj : $lResponseObj")
                        }
                    }else{
                        Log.d(TAG, " Decrypted response Obj : $lApzResponse")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(TAG, "onFailure error ")
                t.printStackTrace()
            }
        })
    }

    override fun onDestroy() {
        Log.d(TAG," INSIDE onDestroy SERVICE")
        removeLocationUpdates()
    }


    companion object {
        private var UPDATE_INTERVAL = 1 * 60 * 100 // 5 minutes
        private var FATEST_INTERVAL = 1 * 60 * 100 // 2 minutes
        private var DISPLACEMENT = 100 // meters
    }
}


