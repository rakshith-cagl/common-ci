package com.iexceed.common

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.AppzillonMainScreen.Companion.alertDialogBuilder
import com.iexceed.appzillonapp.AppzillonMainScreen.Companion.customAlertDialog
import com.iexceed.appzillonapp.AppzillonMainScreen.Companion.isAppFirstTime
import com.iexceed.appzillonapp.BuildConfig
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonConstants.ANDROID_OS
import com.iexceed.common.AppzillonUtils.getCurrentSong
import com.iexceed.common.ServerUtilities.getEncryptedRequest
import com.iexceed.common.StringUtils.getString
import com.iexceed.common.UserSettings.getAppVersion
import com.iexceed.common.UserSettings.getNotificationToken
import com.iexceed.common.UserSettings.setMultiFactorRegistered
import com.iexceed.common.UserSettings.setOSVersion
import com.iexceed.plugins.ApzPlugin
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder
import com.iexceed.retrofitmvvm.data.model.mergeApi.MergeApiEncryptedReqBody
import com.iexceed.retrofitmvvm.data.model.mergeApi.MergeApiReqBody
import com.iexceed.retrofitmvvm.data.response.mergeApi.MergeApiEncryptedResponse
import com.iexceed.retrofitmvvm.data.response.mergeApi.MergeApiResponse
import com.iexceed.utils.common.ApzHelperUtils
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class LaunchMergedInterface(aActivity: AppCompatActivity, val webView: WebView) {
    private val TAG = "LaunchMergedInterface"
    var mActivity: AppCompatActivity = aActivity
    private var onLaunchErrorCode = ""
    var errorCode = ""

    // Change this approach after DI integration
    //val apiInterface = RetrofitBuilder.apiService
    val apiInterface = RetrofitBuilder.getRetrofit(mActivity).create(ApiService::class.java)
    val lPayLoadEncryption = getString("payloadEncryption");
    var respStatus = false
    val lGson = Gson()
    val currAppStoreVersion = AppzillonUtils.getCurrentAppStoreVersion(mActivity)
    val os = ANDROID_OS
    val screenId = "login"
    val source = "APPZILLON"
    val userId = AppzillonConstants.USER_ID_FOR_OTA
    val serverNonce = AppzillonConstants.SNONCE
    val clientNonce = System.currentTimeMillis().toString() + ""
    val sessionToken = AppzillonConstants.SESSIONTOKEN

    val key = UUID.randomUUID().toString()
    val interfaceId = "appzillonOnAppLaunch"

    fun execute() {
        //mActivity.lifecycleScope.launch(Dispatchers.IO) {
        alertDialogBuilder = AlertDialog.Builder(mActivity)
    }

    fun processResponse(call: Call<MergeApiResponse>) {
        call.enqueue(object : Callback<MergeApiResponse> {
            override fun onResponse(
                call: Call<MergeApiResponse>,
                response: Response<MergeApiResponse>
            ) {
                val lRes = response.body()

                if (response.isSuccessful && lRes != null) {
                    try {
                        val lGsonBody = Gson()
                        val bodyObject = lGsonBody.toJson(lRes).toString()

                        val lJSONObj = JSONObject(bodyObject)
                        val lAppzillonHeaderObj =
                            lJSONObj.getJSONObject(AppzillonConstants.APPZILLON_HEADER)
                        val lAppzillonBodyObj =
                            lJSONObj.getJSONObject(AppzillonConstants.APPZILLON_BODY)

                        val lStatus = lAppzillonHeaderObj.getBoolean("status")

                        if (lStatus) {
                            handleSuccessStatus(lAppzillonBodyObj, lAppzillonHeaderObj, lJSONObj)

                            respStatus = true

                        } else {
                            val lApzErrorArray =
                                lJSONObj.getJSONArray(AppzillonConstants.APPZILLON_ERRORS)

                            val error: JSONObject = lApzErrorArray.getJSONObject(0)
                            onLaunchErrorCode = error.getString("errorCode")
                            respStatus = false
                        }
                    } catch (jsonException: Exception) {
                        jsonException.printStackTrace()
                    }
                } else {
                    respStatus = false
                }
                postExecute(respStatus)
            }

            override fun onFailure(call: Call<MergeApiResponse>, t: Throwable) {
                postExecute(false)
            }
        })
    }

    private fun handleSuccessStatus(
        lAppzillonBodyObj: JSONObject,
        lAppzillonHeaderObj: JSONObject,
        lJSONObj: JSONObject
    ) {
        if (lAppzillonBodyObj.has("appzillonGetAppSecTokensResponse")) {
            try {
                val apzResponse: JSONObject =
                    lAppzillonBodyObj.getJSONObject("appzillonGetAppSecTokensResponse")
                AppzillonConstants.SNONCE =
                    apzResponse.getString("serverNonce")
                AppzillonConstants.SESSIONTOKEN =
                    apzResponse.getString("sessionToken")
                if ("Y".equals(
                        getString("payloadEncryption"),
                        ignoreCase = true
                    )
                ) {
                    AppzillonConstants.APPZILLONSAFE =
                        apzResponse.getString("safeToken")
                }

            } catch (jsonException: Exception) {
                jsonException.printStackTrace()
            }

        }

        if (lAppzillonBodyObj.has("appzillonAppMasterResponse")) {
            handleAppMasterResponse(lAppzillonBodyObj, lAppzillonHeaderObj)

        }

        if (lAppzillonBodyObj.has("deviceRegisterResponse")) {
            handleDeviceRegResponse(lAppzillonBodyObj, lJSONObj)

        }

        if (lAppzillonBodyObj.has("appzillonNotificationRegistrationResponse")) {
            handleNotifRegResponse(lAppzillonBodyObj)

        }
    }

    private fun handleDeviceRegResponse(
        lAppzillonBodyObj: JSONObject,
        lJSONObj: JSONObject
    ) {
        try {
            val deviceRegisterStatus: Boolean =
                lAppzillonBodyObj.getJSONObject("deviceRegisterResponse")
                    .getBoolean("status")
            /*if Device registered then update then save os version*/
            if (deviceRegisterStatus) {
                updateUserSettings()
            } else {
                val lApzErrorArray =
                    lJSONObj.getJSONArray(AppzillonConstants.APPZILLON_ERRORS)
                val error: JSONObject =
                    lApzErrorArray.getJSONObject(0)
                val errorMessage =
                    error.getString(mActivity.getString(R.string.already_registered))

                if (error.getString("errorMessage")
                        .contains(errorMessage) ||
                    error.getString("errorMessage")
                        .equals(errorMessage, ignoreCase = true)
                ) {
                    updateUserSettings()
                }
            }

        } catch (jsonException: Exception) {
            jsonException.printStackTrace()
        }
    }

    private fun handleNotifRegResponse(lAppzillonBodyObj: JSONObject) {
        try {
            val notificationRegistrationStatus: String =
                lAppzillonBodyObj.getJSONObject("appzillonNotificationRegistrationResponse")
                    .getString("status")
            if (notificationRegistrationStatus == "success") {
                val apps = EncryptedPrefHelper.getPrefs()
                UserSettings.setIsNotificationRegistered(
                    getString(
                        StringUtils.APP_ID
                    ), "true", apps
                )
            }
        } catch (jsonException: Exception) {
            jsonException.printStackTrace()
        }
    }

    private fun handleAppMasterResponse(
        lAppzillonBodyObj: JSONObject,
        lAppzillonHeaderObj: JSONObject
    ) {
        try {
            val apzResponse: JSONObject =
                lAppzillonBodyObj.getJSONObject("appzillonAppMasterResponse")
            val appId = lAppzillonHeaderObj.getString("appId")


            val appInstructionsResponseBody =
                apzResponse.getJSONObject("appInstruction")

            AppzillonMainScreen.MAIN_APP_NAME =
                appInstructionsResponseBody.getString(
                    AppzillonConstants.APP_ID
                )

            appInstructionsResponseBody.getString(
                AppzillonConstants.EXPIRY_DATE
            )
            var lVersion = getAppVersion(
                AppzillonMainScreen.MAIN_APP_NAME,
                EncryptedPrefHelper.getPrefs()
            )


            if (lVersion.equals("0.0.0", ignoreCase = true)) {
                lVersion = getString(StringUtils.APP_VERSION)
            }

            if (AppzillonConstants.OTAREQUIRED.equals("Y")) {
                if (!currAppStoreVersion.equals(
                        appInstructionsResponseBody.getString("appVersion"),
                        ignoreCase = true
                    )
                ) {
                    AppzillonConstants.UPDATE_REQUEST = "Y"
                    appInstructionsResponseBody.put(
                        "upgradeRequired",
                        "Y"
                    )
                } else {
                    AppzillonConstants.UPDATE_REQUEST = "N"
                    appInstructionsResponseBody.put(
                        "upgradeRequired",
                        "N"
                    )
                }

            } else {
                AppzillonConstants.UPDATE_REQUEST = "N"
                appInstructionsResponseBody.put(
                    "upgradeRequired",
                    "N"
                )
            }

            //check for force update Action
            if (appInstructionsResponseBody.has("updateAction")) {
                AppzillonConstants.UPDATE_ACTION =
                    appInstructionsResponseBody.getString("updateAction")
            }

            //check for force update Action
            AppzillonMainScreen.REMOTE_DEBUG_VALUE =
                appInstructionsResponseBody.getString(
                    AppzillonConstants.REMOTE_DEBUG
                )
            if (AppzillonMainScreen.REMOTE_DEBUG_VALUE.equals(
                    "Y",
                    ignoreCase = true
                )
            ) {
                ApzPlugin.debugLevel = 5
            }

            if (appInstructionsResponseBody.getString(
                    AppzillonConstants.WIPE_OUT
                ).equals("Y", ignoreCase = true)
            ) {
                AppzillonUtils.wipeOutMainApp(mActivity.applicationContext)
                mActivity.finish()
            }


        } catch (jsonException: Exception) {
            jsonException.printStackTrace()
        }
    }

    fun getFinalEncryptedRequest() : MergeApiEncryptedReqBody {
        val request = prepareMergedInterfaceRequest()
        val lJSONObject: JSONObject = getEncryptedRequest(mActivity, request.toString())

        val lFinalEncryptedRequest =
            lGson.fromJson(lJSONObject.toString(), MergeApiEncryptedReqBody::class.java)

        return lFinalEncryptedRequest
    }

    fun getFinalRequest(): MergeApiReqBody {
        val request = prepareMergedInterfaceRequest()
        return lGson.fromJson(request.toString(), MergeApiReqBody::class.java)
    }

    fun processEncryptedResponse(call :  Call<MergeApiEncryptedResponse>) {
            call.enqueue(object : Callback<MergeApiEncryptedResponse> {
                override fun onResponse(
                    call: Call<MergeApiEncryptedResponse>,
                    response: Response<MergeApiEncryptedResponse>
                ) {
                    val lRes = response.body()
                    if (response.isSuccessful && lRes != null) {
                        val lResponseObj = ServerUtilities.getDecryptedResponseObj(
                            mActivity, lGson.toJson(lRes), clientNonce, interfaceId
                        )
                        try {
                            lGson.fromJson(
                                lResponseObj.toString(),
                                MergeApiResponse::class.java
                            )
                            val lAppzillonHeaderObj =
                                lResponseObj!!.getJSONObject(AppzillonConstants.APPZILLON_HEADER)
                            val lAppzillonBodyObj =
                                lResponseObj.getJSONObject(AppzillonConstants.APPZILLON_BODY)

                            val lStatus = lAppzillonHeaderObj.getBoolean("status")

                            if (lStatus) {

                                handleEncryptedSuccessStatus(
                                    lAppzillonBodyObj,
                                    lAppzillonHeaderObj,
                                    lResponseObj
                                )


                                respStatus = true

                            } else {
                                val lApzErrorArray =
                                    lResponseObj.getJSONArray(AppzillonConstants.APPZILLON_ERRORS)

                                val error: JSONObject = lApzErrorArray.getJSONObject(0)
                                onLaunchErrorCode = error.getString("errorCode")
                                respStatus = false

                            }


                        } catch (jsonException: Exception) {
                            jsonException.printStackTrace()
                        }

                    } else {
                        respStatus = false

                    }
                    postExecute(respStatus)

                }

                override fun onFailure(call: Call<MergeApiEncryptedResponse>, t: Throwable) {
                    postExecute(false)
                }
            })
    }

    private fun handleEncryptedSuccessStatus(
        lAppzillonBodyObj: JSONObject,
        lAppzillonHeaderObj: JSONObject,
        lResponseObj: JSONObject?
    ) {
        if (lAppzillonBodyObj.has("appzillonGetAppSecTokensResponse")) {
            handleEncryptedAppSecTokenResponse(lAppzillonBodyObj)
        }
        if (lAppzillonBodyObj.has("appzillonAppMasterResponse")) {
            handleEncryptedMasterResponse(
                lAppzillonBodyObj,
                lAppzillonHeaderObj
            )

        }

        if (lAppzillonBodyObj.has("deviceRegisterResponse")) {
            handleEncryptedRegResponse(lAppzillonBodyObj, lResponseObj!!)

        }
        if (lAppzillonBodyObj.has("appzillonNotificationRegistrationResponse")) {
            handleEncryptedNotifResponse(lAppzillonBodyObj)

        }
    }

    private fun handleEncryptedAppSecTokenResponse(lAppzillonBodyObj: JSONObject) {
        try {
            val apzResponse: JSONObject =
                lAppzillonBodyObj.getJSONObject("appzillonGetAppSecTokensResponse")
            AppzillonConstants.SNONCE =
                apzResponse.getString("serverNonce")
            AppzillonConstants.SESSIONTOKEN =
                apzResponse.getString("sessionToken")
            if ("Y".equals(
                    getString("payloadEncryption"),
                    ignoreCase = true
                )
            ) {
                AppzillonConstants.APPZILLONSAFE =
                    apzResponse.getString("safeToken")
            }

        } catch (jsonException: Exception) {
            jsonException.printStackTrace()
        }
    }

    private fun handleEncryptedMasterResponse(
        lAppzillonBodyObj: JSONObject,
        lAppzillonHeaderObj: JSONObject
    ) {
        try {
            val apzResponse: JSONObject =
                lAppzillonBodyObj.getJSONObject("appzillonAppMasterResponse")
            val appId = lAppzillonHeaderObj.getString("appId")


            val appInstructionsResponseBody =
                apzResponse.getJSONObject("appInstruction")

            AppzillonMainScreen.MAIN_APP_NAME =
                appInstructionsResponseBody.getString(
                    AppzillonConstants.APP_ID
                )

            appInstructionsResponseBody.getString(
                AppzillonConstants.EXPIRY_DATE
            )
            var lVersion = getAppVersion(
                AppzillonMainScreen.MAIN_APP_NAME,
                EncryptedPrefHelper.getPrefs()
            )


            if (lVersion.equals("0.0.0", ignoreCase = true)) {
                lVersion = getString(StringUtils.APP_VERSION)
            }

            if (AppzillonConstants.OTAREQUIRED.equals("Y")) {
                if (!currAppStoreVersion.equals(
                        appInstructionsResponseBody.getString("appVersion"),
                        ignoreCase = true
                    )
                ) {
                    AppzillonConstants.UPDATE_REQUEST = "Y"
                    appInstructionsResponseBody.put(
                        "upgradeRequired",
                        "Y"
                    )
                } else {
                    AppzillonConstants.UPDATE_REQUEST = "N"
                    appInstructionsResponseBody.put(
                        "upgradeRequired",
                        "N"
                    )
                }

            } else {
                AppzillonConstants.UPDATE_REQUEST = "N"
                appInstructionsResponseBody.put(
                    "upgradeRequired",
                    "N"
                )
            }

            //check for force update Action
            if (appInstructionsResponseBody.has("updateAction")) {
                AppzillonConstants.UPDATE_ACTION =
                    appInstructionsResponseBody.getString("updateAction")
            }

            //check for force update Action
            AppzillonMainScreen.REMOTE_DEBUG_VALUE =
                appInstructionsResponseBody.getString(
                    AppzillonConstants.REMOTE_DEBUG
                )
            if (AppzillonMainScreen.REMOTE_DEBUG_VALUE.equals(
                    "Y",
                    ignoreCase = true
                )
            ) {
                ApzPlugin.debugLevel = 5
            }

            if (appInstructionsResponseBody.getString(
                    AppzillonConstants.WIPE_OUT
                ).equals("Y", ignoreCase = true)
            ) {
                AppzillonUtils.wipeOutMainApp(mActivity.applicationContext)
                mActivity.finish()
            }


        } catch (jsonException: Exception) {
            jsonException.printStackTrace()
        }
    }

    private fun handleEncryptedRegResponse(
        lAppzillonBodyObj: JSONObject,
        lResponseObj: JSONObject
    ) {
        try {

            val deviceRegisterStatus: Boolean =
                lAppzillonBodyObj.getJSONObject("deviceRegisterResponse")
                    .getBoolean("status")
            /*if Device registered then update then save os version*/
            /*if Device registered then update then save os version*/
            if (deviceRegisterStatus) {
                updateUserSettings()
            } else {
                val lApzErrorArray =
                    lResponseObj.getJSONArray(AppzillonConstants.APPZILLON_ERRORS)
                val error: JSONObject =
                    lApzErrorArray.getJSONObject(0)
                val errorMessage =
                    error.getString(mActivity.getString(R.string.already_registered))

                if (error.getString("errorMessage")
                        .contains(errorMessage) ||
                    error.getString("errorMessage")
                        .equals(errorMessage, ignoreCase = true)
                ) {
                    updateUserSettings()
                }
            }

        } catch (jsonException: Exception) {
            jsonException.printStackTrace()
        }
    }

    private fun handleEncryptedNotifResponse(lAppzillonBodyObj: JSONObject) {
        try {
            val notificationRegistrationStatus: String =
                lAppzillonBodyObj.getJSONObject("appzillonNotificationRegistrationResponse")
                    .getString("status")
            if (notificationRegistrationStatus.equals("success")) {
                val apps = EncryptedPrefHelper.getPrefs()
                UserSettings.setIsNotificationRegistered(
                    getString(
                        StringUtils.APP_ID
                    ), "true", apps
                )
            }
        } catch (jsonException: Exception) {
            jsonException.printStackTrace()
        }
    }

    fun prepareMergedInterfaceRequest(): JSONObject {
        val onLaunchRequest = JSONObject()
        try {
            val appId = getString(StringUtils.APP_ID)
            val deviceId = AppzillonUtils.getDeviceId()
            val origination = AppzillonUtils.ipAddress()

            var updateAppVersion = "N"
            try {
                val appVersionCodeStr = FileUtils.oldAppVersion
                if (!appVersionCodeStr.equals("0", ignoreCase = true)) {
                    val currentAppVersionCode = appVersionCodeStr.toInt()
                    updateAppVersion = if (BuildConfig.VERSION_CODE > currentAppVersionCode) {
                        "Y"
                    } else {
                        "N"
                    }
                }
            } catch (nfe: java.lang.Exception) {
                //Sonar fix
            }
            //check for notification token
            val token = getNotificationToken(
                getString(StringUtils.APP_ID),
                EncryptedPrefHelper.getPrefs()
            )

            val bluetoothName: String = AppzillonUtils.getBluetoothName(mActivity)
            val osVersion: String = AppzillonUtils.getOsDetails(mActivity)
            val deviceModel: String = AppzillonUtils.getDeviceType()
            val deviceMake: String = AppzillonUtils.getDeviceMake()
            val screenResolution: String = AppzillonUtils.getScreenSize(mActivity)


            try {
                val appzillonHeader = JSONObject()
                appzillonHeader.put("serverNonce", serverNonce)
                appzillonHeader.put("requestKey", "")
                appzillonHeader.put("clientNonce", clientNonce)
                appzillonHeader.put("sessionToken", sessionToken)
                appzillonHeader.put(AppzillonConstants.APP_ID, appId)
                appzillonHeader.put(AppzillonConstants.DEVICE_ID, deviceId)
                appzillonHeader.put("source", "APPZILLON")
                appzillonHeader.put(AppzillonConstants.USER_ID, userId)
                appzillonHeader.put("screenId", "Login")
                appzillonHeader.put(AppzillonConstants.SESSION_ID, "")
                appzillonHeader.put("origination", origination)
                appzillonHeader.put(AppzillonConstants.INTERFACE_ID, interfaceId)
                appzillonHeader.put(AppzillonConstants.REQ_STATUS, true)
                appzillonHeader.put("longitude", "")
                appzillonHeader.put("latitude", "")
                appzillonHeader.put(AppzillonConstants.REQUEST_KEY, "000NEW")
                appzillonHeader.put(AppzillonConstants.SCREEN_ID, "launchapp")

                if (isAppFirstTime.equals("YES", ignoreCase = true)) {
                    if (token.equals("", ignoreCase = true)) {
                        appzillonHeader.put("appLaunch", "FIRST")
                    } else {
                        appzillonHeader.put("appLaunch", "NOTIFY")
                    }
                } else {
                    appzillonHeader.put("appLaunch", "SECOND")
                }

                appzillonHeader.put(
                    AppzillonConstants.APZ_SIGNATURE,
                    getCurrentSong(mActivity)
                )
                appzillonHeader.put(AppzillonConstants.PRE_LOGIN, "true")

                val appzillonBody = JSONObject()
                val appReqDetails1 = JSONObject()
                val appReqDetails2 = JSONObject()
                val appReqDetails3 = JSONObject()
                val appReqDetails4 = JSONObject()

                appReqDetails1.put(AppzillonConstants.APP_ID, appId)
                appReqDetails1.put(AppzillonConstants.DEVICE_ID, deviceId)
                appReqDetails1.put(
                    AppzillonConstants.APP_STORE_VERSION,
                    currAppStoreVersion
                )
                appReqDetails1.put(
                    AppzillonConstants.UPDATE_APP_STORE_VERSION,
                    updateAppVersion
                )
                appReqDetails1.put(AppzillonConstants.OS, os)
                appzillonBody.put("appzillonAppMasterRequest", appReqDetails1)

                if (isAppFirstTime.equals("YES", ignoreCase = true)) {
                    appReqDetails2.put(AppzillonConstants.APP_ID, appId)
                    appReqDetails2.put(AppzillonConstants.OS, os)
                    appReqDetails2.put(AppzillonConstants.OS_VERSION, osVersion)
                    appReqDetails2.put(AppzillonConstants.DEVICE_ID, deviceId)
                    appReqDetails2.put(AppzillonConstants.MOBILE_ONE, "UNKNOWN")
                    appReqDetails2.put(AppzillonConstants.MOBILE_TWO, "UNKNOWN")
                    appReqDetails2.put(AppzillonConstants.DEVICE_MODEL, deviceModel)
                    appReqDetails2.put(AppzillonConstants.DEVICE_MAKE, deviceMake)
                    appReqDetails2.put(AppzillonConstants.SCREEN_RESOLUTION, screenResolution)
                    appReqDetails2.put(AppzillonConstants.APP_STORE_VERSION, currAppStoreVersion)
                    appReqDetails2.put(AppzillonConstants.DEVICE_NAME, bluetoothName)
                    appReqDetails2.put("longitude", "")
                    appReqDetails2.put("latitude", "")
                    appzillonBody.put("deviceRegisterRequest", appReqDetails2)
                    if (!token.equals("", ignoreCase = true)) {
                        appReqDetails3.put(AppzillonConstants.DEVICE_ID, deviceId)
                        appReqDetails3.put(AppzillonConstants.DEVICE_NAME, bluetoothName)
                        appReqDetails3.put(AppzillonConstants.OS_ID, os)
                        appReqDetails3.put("regId", token)
                        appReqDetails3.put(AppzillonConstants.OS_VERSION, osVersion)
                        appReqDetails3.put(AppzillonConstants.APP_ID, appId)
                        appzillonBody.put(
                            "appzillonNotificationRegistrationRequest",
                            appReqDetails3
                        )
                    }
                }

                appReqDetails4.put(AppzillonConstants.APP_ID, appId)
                appReqDetails4.put(AppzillonConstants.DEVICE_ID, deviceId)
                appzillonBody.put("appzillonGetAppSecTokensRequest", appReqDetails4)

                onLaunchRequest.put("appzillonHeader", appzillonHeader)
                onLaunchRequest.put("appzillonBody", appzillonBody)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            //Sonar fix
        }
        return onLaunchRequest
    }

    fun postExecute(respStatus: Boolean) {
        val nonceStatus = booleanArrayOf(false)
        Handler(Looper.getMainLooper()).post {
            verifyNonce(nonceStatus)
            //check for proper response
            if (respStatus && nonceStatus[0]) {
                ApzHelperUtils.onContainerLoaded("Y")

            } else {
                if (onLaunchErrorCode.length > 1 && onLaunchErrorCode.contains("APZ-APP-SIGN-FAULT")) {
                    val builder = AlertDialog.Builder(mActivity)
                    val customAlertDialog: AlertDialog
                    builder.setMessage("Application tempered, press ok to exit")
                        .setTitle("Error")
                    builder.setCancelable(false)
                    builder.setNegativeButton(
                        "Ok"
                    ) { dialog, _ ->
                        dialog.cancel()
                        mActivity.finish()
                    }
                    customAlertDialog = builder.create()
                    customAlertDialog.show()
                } else {
                    if (!mActivity.isFinishing) {
                        showErrorDialog("Server Error", "Unable to connect to the server.")
                    }
                }
                ApzHelperUtils.onContainerLoaded("N")
            }
        }

    }

    private fun verifyNonce(nonceStatus: BooleanArray) {
        if (TextUtils.isEmpty(AppzillonConstants.SNONCE)) {
            nonceStatus[0] = (mActivity.resources.getString(R.string.OFFLINESUPPORT)
                .equals("Y", ignoreCase = true)
                    ||mActivity.resources.getString(R.string.APPOFFLINESUPPORT)
                .equals("Y", ignoreCase = true))
        } else {
            webView.loadUrl("javascript:appzillon.server.setAppSecToken('Y');")
            nonceStatus[0] = true
        }
    }


    private fun showErrorDialog(title: String, msg: String) {
        if (alertDialogBuilder != null) {
            alertDialogBuilder!!.setMessage(msg).setTitle(title)
            alertDialogBuilder!!.setCancelable(false)
            alertDialogBuilder!!.setPositiveButton("Try Again",
                DialogInterface.OnClickListener { _, _ ->
                    LaunchMergedInterface(
                        mActivity,
                        webView
                    ).execute()
                })
            alertDialogBuilder!!.setNegativeButton("Cancel",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                    mActivity.finish()
                })

            customAlertDialog = alertDialogBuilder!!.create()
            customAlertDialog?.show()
        }
    }

    private fun updateUserSettings() {
        setMultiFactorRegistered(
            getString(StringUtils.APP_ID),
            "true",
            EncryptedPrefHelper.getPrefs()
        )
        setOSVersion(AppzillonUtils.getOsDetails(mActivity), EncryptedPrefHelper.getPrefs())
    }


}
