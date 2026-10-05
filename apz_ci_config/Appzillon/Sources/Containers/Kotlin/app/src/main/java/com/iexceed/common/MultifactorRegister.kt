package com.iexceed.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.iexceed.common.AppzillonConstants.ANDROID_OS
import com.iexceed.common.AppzillonUtils.getCurrentAppStoreVersion
import com.iexceed.common.AppzillonUtils.getDeviceId
import com.iexceed.common.AppzillonUtils.getDeviceMake
import com.iexceed.common.AppzillonUtils.getDeviceType
import com.iexceed.common.AppzillonUtils.getOsDetails
import com.iexceed.common.AppzillonUtils.getScreenSize
import com.iexceed.common.AppzillonUtils.ipAddress
import com.iexceed.common.StringUtils.getString
import com.iexceed.common.UserSettings.setMultiFactorRegistered
import com.iexceed.common.UserSettings.setOSVersion
import com.iexceed.appzillonapp.R
import com.iexceed.plugins.currentlocation.CurrentLocation
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.retrofitmvvm.data.response.multifactor.MultiFactorResponse
import com.iexceed.utils.localstorage.EncryptedPrefHelper
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

class MultifactorRegister(aActivity: AppCompatActivity) {
    var mActivity: AppCompatActivity = aActivity
    private val TAG = "MultifactorRegistor"

    private var TRACKLOCATION = getString(StringUtils.TRACK_LOCATION)
    private var LATITUDE = ""
    private var LONGITUDE = ""
    var clientNonce = ""
    val interfaceId = "appzillonDeviceRegistration"
    val lGson = Gson()
    val lPayLoadEncryption = getString("payloadEncryption")

    fun execute() {
        // Change this approach after DI integration
        // val apiInterface = RetrofitBuilder.apiService
        /*val apiInterface = RetrofitBuilder.getRetrofit(mActivity).create(ApiService::class.java)
        val call = apiInterface.getMultiFactorReq(lReqBody)*/
    }

    fun prepareRequestBody(): RequestBody {
        fetchLatLongIfEnabled(TRACKLOCATION)
        val jsonObject = JSONObject()

        val reqBody = JSONObject()
        try {
            reqBody.put("deviceRegisterRequest", getRequiredDetails())
            jsonObject.put(AppzillonConstants.APPZILLON_HEADER, getHeader())
            jsonObject.put(AppzillonConstants.APPZILLON_BODY, reqBody)
        } catch (jsonException: JSONException) {
            jsonException.printStackTrace()
        }
        val finalPayloadRequest = ServerUtilities.getEncryptedRequest(
            mActivity,
            reqBody.toString(),
            isCNonceRequired = true
        )
        clientNonce = if ("Y".equals(StringUtils.getString("dataIntegrity"), ignoreCase = true)) finalPayloadRequest.getString("localCNonce") else ""
        if (finalPayloadRequest.has("localCNonce")) {
            finalPayloadRequest.remove("localCNonce")
        }

        return finalPayloadRequest.toString().toRequestBody()
    }

    private fun getRequiredDetails(): JSONObject {
        val bluetoothName: String = AppzillonUtils.getBluetoothName(mActivity)
        return JSONObject().apply {
            put(AppzillonConstants.APP_ID, StringUtils.getString(StringUtils.APP_ID))
            put(AppzillonConstants.OS, ANDROID_OS)
            put(AppzillonConstants.OS_VERSION, getOsDetails(mActivity))
            put(AppzillonConstants.DEVICE_ID, getDeviceId())
            put(AppzillonConstants.MOBILE_ONE, "UNKNOWN")
            put(AppzillonConstants.MOBILE_TWO, "UNKNOWN")
            put(AppzillonConstants.DEVICE_MODEL, getDeviceType())
            put(AppzillonConstants.DEVICE_MAKE, getDeviceMake())
            put(AppzillonConstants.SCREEN_RESOLUTION, getScreenSize(mActivity))
            put(
                AppzillonConstants.APP_STORE_VERSION,
                getCurrentAppStoreVersion(mActivity)
            )
            put(AppzillonConstants.DEVICE_NAME, bluetoothName)
            put("longitude", LONGITUDE)
            put("latitude", LATITUDE)
        }
    }

    private fun getHeader(): JSONObject {
        return JSONObject().apply {
            put(AppzillonConstants.PRE_LOGIN, "true")
            put(AppzillonConstants.APP_ID, StringUtils.getString(StringUtils.APP_ID))
            put(AppzillonConstants.SCREEN_ID, "lauchApp")
            put(AppzillonConstants.REQUEST_KEY, "000NEW")
            put(AppzillonConstants.INTERFACE_ID, "appzillonDeviceRegistration")
            put(AppzillonConstants.REQ_STATUS, true)
            put(AppzillonConstants.SESSION_ID, "")
            put(AppzillonConstants.DEVICE_ID, getDeviceId())
            put(AppzillonConstants.USER_ID, AppzillonConstants.USER_ID_FOR_OTA)
            put("longitude", LONGITUDE)
            put("latitude", LATITUDE)
            put("origination", ipAddress())
            put("source", "APPZILLON")
        }
    }

    private fun fetchLatLongIfEnabled(tracklocation: String) {
        if (tracklocation.equals("Y", ignoreCase = true)) {
            try {
                val manager: LocationManager =
                    mActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (ActivityCompat.checkSelfPermission(
                        mActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED &&
                    statusOfGPS
                ) {
                    val latlng: Location? = CurrentLocation.getLocation(mActivity)
                    val latd: Double
                    val lngd: Double
                    if (latlng != null) {
                        latd = latlng.latitude
                        lngd = latlng.longitude

                        LATITUDE = latd.toString()
                        LONGITUDE = lngd.toString()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.d(TAG, "TRACKLOCATION is : NO")
        }
    }

    private fun updateUserSettings() {
        setMultiFactorRegistered(
            getString(StringUtils.APP_ID),
            "true",
            EncryptedPrefHelper.getPrefs()
        )
        setOSVersion(getOsDetails(mActivity), EncryptedPrefHelper.getPrefs())
    }

    fun processMultiFactorResponseBody(multiFactorResponseBody: Call<ResponseBody>) {
        multiFactorResponseBody.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val lRes = response
                val lApzResponse = lRes.body()?.string()
                if (lRes.isSuccessful && lApzResponse != null) {
                    ApzLogger.d(TAG, " lRes Successful")
                    handleResponseWithEncryption(lApzResponse)
                } else {
                    Log.d(TAG, " lRes.isSuccessful failure ")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun updateMultifactor(multifactorResponse: MultiFactorResponse) {
        if (multifactorResponse.appzillonBody.deviceRegisterResponse != null) {
            if (multifactorResponse.appzillonBody.deviceRegisterResponse.status) {
                updateUserSettings()
            }
        }
    }

    private fun handleResponseWithEncryption(lApzResponse: String?) {
        val lResponseObj = ServerUtilities.getDecryptedResponseObj(
            mActivity,
            lApzResponse!!,
            clientNonce,
            interfaceId
        )

        if (lResponseObj != null) {
            val lMultiFactorResponse = lGson.fromJson(
                lResponseObj.toString(),
                MultiFactorResponse::class.java
            )
            if (lMultiFactorResponse.appzillonBody.deviceRegisterResponse.status) {
                updateUserSettings()
            }
        } else {
            mActivity.applicationContext.resources.getString(R.string.already_registered)
        }
    }
}
