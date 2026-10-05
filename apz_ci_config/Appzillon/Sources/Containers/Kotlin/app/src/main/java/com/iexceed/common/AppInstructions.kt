package com.iexceed.common

import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.iexceed.common.AppzillonUtils.getCurrentAppStoreVersion
import com.iexceed.common.StringUtils.getString
import com.iexceed.common.UserSettings.getAppVersionCode
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.AppzillonMainScreen.Companion.REMOTE_DEBUG_VALUE
import com.iexceed.appzillonapp.BuildConfig
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder.getRetrofit
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class AppInstructions(
    aActivity: AppCompatActivity,
    val appId: String,
    val parentAppId: String,
    private val apzPluginUtil: IapzPluginUtil
) {
    var mActivity: AppCompatActivity = aActivity
    private val TAG = "AppInstructions"
    var sendResp = false
    lateinit var webView: WebView
    lateinit var callbackId: String

    fun execute(statusInterface: GetSSlStatusInterface?) {
        try {
            val appId = getString(StringUtils.APP_ID)
            val currAppStoreVersion = getCurrentAppStoreVersion(mActivity)
            val lReqBody = getAppInstructions(appId, appId)
            var clientNonce = ""
            val finalPayloadRequest = ServerUtilities.getEncryptedRequest(
                mActivity,
                lReqBody.toString(),
                isCNonceRequired = true
            )
            clientNonce = if ("Y".equals(StringUtils.getString("dataIntegrity"), ignoreCase = true)) finalPayloadRequest.getString("localCNonce") else ""
            if (finalPayloadRequest.has("localCNonce")) {
                finalPayloadRequest.remove("localCNonce")
            }

            val lReqObj = finalPayloadRequest.toString().toRequestBody()

            // Change this approach after DI integration
            // val apiInterface = RetrofitBuilder.apiService
            val apiInterface = getRetrofit(mActivity).create(ApiService::class.java)
            val call = apiInterface.getAppInstructionReq(lReqObj)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val lRes = response.body()
                    val lResponseBody = lRes?.string()
                    val interfaceId = "appzillonGetAppMasterDetails"
                    var lJSONObj = JSONObject()

                    if (response.isSuccessful && lResponseBody != null) {
                        handleSuccessCallback(
                            lResponseBody,
                            clientNonce,
                            interfaceId,
                            lJSONObj,
                            statusInterface,
                            currAppStoreVersion
                        )
                    } else {
                        if (sendResp) {
                            apzPluginUtil.sendError(callbackId, "APZ-CNT-304", null, mActivity, webView, true)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(mActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                    Log.d("getAppInstructionReq", " appInstructionHeader : error ")
                    t.printStackTrace()
                    if (sendResp) {
                        apzPluginUtil.sendError(callbackId, "APZ-CNT-304", null, mActivity, webView, true)
                    }
                }
            })
        } catch (jsonException: Exception) {
            jsonException.printStackTrace()
        }
    }

    private fun handleSuccessCallback(
        lResponseBody: String?,
        clientNonce: String,
        interfaceId: String,
        lJSONObj: JSONObject,
        statusInterface: GetSSlStatusInterface?,
        currAppStoreVersion: String
    ) {
        var lJSONObj1 = lJSONObj
        if ("Y".equals(getString("payloadEncryption"), ignoreCase = true)) {
            val lResponseObj = ServerUtilities.getDecryptedResponseObj(
                mActivity,
                lResponseBody!!,
                clientNonce,
                interfaceId
            )
            if (lResponseObj != null) {
                lJSONObj1 = lResponseObj
            }
        } else {
            lJSONObj1 = JSONObject(lResponseBody!!)
        }

        val lAppzillonHeaderObj = lJSONObj1.getJSONObject("appzillonHeader")
        val lStatus = lAppzillonHeaderObj.getBoolean("status")
        if (lStatus) {
            handleSuccessStatus(statusInterface, lJSONObj1, currAppStoreVersion)
        } else {
            lJSONObj1.getJSONArray("appzillonErrors")
            if (sendResp) {
                apzPluginUtil.sendError(callbackId, "", null, mActivity, webView, true)
            }
        }
    }

    private fun handleSuccessStatus(
        statusInterface: GetSSlStatusInterface?,
        lJSONObj1: JSONObject,
        currAppStoreVersion: String
    ) {
        statusInterface?.onResult(true)
        val lApzBodyObj = lJSONObj1.getJSONObject("appzillonBody")
        val lApzBodyAppIdObj = lApzBodyObj.getJSONObject("appInstruction")

        if (AppzillonConstants.OTAREQUIRED == "Y") {
            if (!currAppStoreVersion.equals(
                    lApzBodyAppIdObj.getString("appVersion"),
                    ignoreCase = true
                )
            ) {
                AppzillonConstants.UPDATE_REQUEST = "Y"
                lApzBodyAppIdObj.put("upgradeRequired", "Y")
            } else {
                AppzillonConstants.UPDATE_REQUEST = "N"
                lApzBodyAppIdObj.put("upgradeRequired", "N")
            }
        } else {
            AppzillonConstants.UPDATE_REQUEST = "N"
            lApzBodyAppIdObj.put("upgradeRequired", "N")
        }

        // check for force update Action
        REMOTE_DEBUG_VALUE = lApzBodyAppIdObj.getString(AppzillonConstants.REMOTE_DEBUG)
        if (REMOTE_DEBUG_VALUE.equals("Y", ignoreCase = true)) {
            ApzPlugin.debugLevel = 5
        }

        lApzBodyAppIdObj.getString(AppzillonConstants.EXPIRY_DATE)

        if (lApzBodyAppIdObj.getString(AppzillonConstants.CONTAINER_APP)
            .equals("Y", ignoreCase = true)
        ) {
            // handle containerApp
        }

        if (lApzBodyAppIdObj.getString(AppzillonConstants.WIPE_OUT)
            .equals("Y", ignoreCase = true)
        ) {
            AppzillonUtils.wipeOutMainApp(mActivity.applicationContext)
            mActivity.finish()
        }

        // check for force update Action
        if (lApzBodyAppIdObj.has("updateAction")) {
            AppzillonConstants.UPDATE_ACTION = lApzBodyAppIdObj.getString("updateAction")
        }
        // check for force update Action
        if (sendResp) {
            apzPluginUtil.sendSuccess(
                callbackId,
                lApzBodyAppIdObj,
                false,
                mActivity,
                webView,
                true
            )
        }
    }

    fun executeForResp(callbackId: String?, wV: WebView) {
        sendResp = true
        this.webView = wV
        this.callbackId = callbackId.toString()
        execute(null)
    }

    private fun getAppInstructions(
        appName: String?,
        presentAppID: String?
    ): JSONObject {
        val jsonObject = JSONObject()
        val ip = AppzillonUtils.ipAddress()
        try {
            val header = JSONObject()
            header.put(AppzillonConstants.PRE_LOGIN, "true")
            header.put(AppzillonConstants.APP_ID, presentAppID)
            header.put(AppzillonConstants.SESSION_ID, "")
            header.put(AppzillonConstants.INTERFACE_ID, "appzillonGetAppMasterDetails")
            header.put(AppzillonConstants.SCREEN_ID, "login")
            header.put(AppzillonConstants.DEVICE_ID, AppzillonConstants.ANDROID_OS)
            header.put(AppzillonConstants.REQUEST_KEY, "")
            header.put(AppzillonConstants.USER_ID, AppzillonConstants.USER_ID_FOR_OTA)
            header.put(AppzillonConstants.REQ_STATUS, true) // "success" 3.2 changes
            header.put("origination", ip)
            header.put("source", "APPZILLON")
            val reqBody = JSONObject()
            val appFileReq = JSONObject()
            appFileReq.put(AppzillonConstants.APP_ID, appName)
            val phoneDeviceId =
                AppzillonUtils.getDeviceId()
            val currAppStoreVersion =
                getCurrentAppStoreVersion(mActivity)
            var updateAppVersion = "N"
            val appVersionCodeStr =
                getAppVersionCode(AppzillonMainScreen.APP_NAME, EncryptedPrefHelper.getPrefs())
            if (!appVersionCodeStr.equals("0", ignoreCase = true)) {
                val currentAppVersionCode = appVersionCodeStr.toInt()
                updateAppVersion =
                    if (BuildConfig.VERSION_CODE > currentAppVersionCode) {
                        "Y"
                    } else {
                        "N"
                    }
            }
            appFileReq.put(AppzillonConstants.DEVICE_ID, phoneDeviceId)
            appFileReq.put(AppzillonConstants.APP_STORE_VERSION, currAppStoreVersion)
            appFileReq.put(AppzillonConstants.UPDATE_APP_STORE_VERSION, updateAppVersion)
            appFileReq.put(AppzillonConstants.OS, AppzillonConstants.ANDROID_OS)
            reqBody.put("appzillonAppMasterRequest", appFileReq)
            jsonObject.put(AppzillonConstants.APPZILLON_HEADER, header)
            jsonObject.put(AppzillonConstants.APPZILLON_BODY, reqBody)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }
}
