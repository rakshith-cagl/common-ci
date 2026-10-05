package com.iexceed.plugins.dataSecurity

import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.StringUtils
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.encryption.ApzEncryptionPlugin
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class StoreCredentialsSecurely(aWebView: WebView,
                               aActivity: AppCompatActivity,
                               val apzPluginUtil: IapzPluginUtil
){
    private val errorCode334 = "APZ-CNT-334"
    init {
        activity = aActivity
        webView = aWebView
    }
    companion object
    {
        lateinit var activity: AppCompatActivity
        private lateinit var iv: ByteArray
        lateinit var webView: WebView
        private var callBackId: String? = null
        val TAG = StoreCredentialsSecurely::class.java.simpleName
    }


    fun storeCredentialsSecurely(callBackId: String?, userId: String, password: String)
    {
        if (userId.isEmpty() || password.isEmpty())
        {
            apzPluginUtil.sendError(callBackId, "APZ-CNT-231", null, activity, webView, true)
        } else {
            EncryptedPrefHelper.save("credentials", "$userId###@@@###$password")
            apzPluginUtil.sendSuccess(callBackId, null, false, activity, webView, true)
        }
    }

    fun loginSecurely(
        callBackId: String?,
        aReqId: String,
        params: JSONObject, isBiometric: Boolean)
    {
        var userName: String? = null
        var password: String? = null
        var finalPassword: String? = null
        var sysDate = ""
        var loginRequest = JSONObject()
        var appzillonHeader = JSONObject()
        val pair = getLoginRequest(params, appzillonHeader, loginRequest)
        appzillonHeader = pair.first
        loginRequest = pair.second

        if (isBiometric)
        {
            val credentials = EncryptedPrefHelper.read("credentials", "")
            if (!credentials.isNullOrEmpty())
            {
                try {
                    val arr = credentials.split("###@@@###").toTypedArray()
                    userName = arr[0]
                    password = arr[1]
                } catch (ae: ArrayIndexOutOfBoundsException) {
                    handleError(callBackId, aReqId)
                    return
                }
            }else{
                handleError(callBackId, aReqId)
                return
            }
        } else {
            try {
                userName = loginRequest.getString("userId")
                password = loginRequest.getString("pwd")
            } catch (ex: Exception) {
                //Sonar fix
            }
        }
        try {
            sysDate = loginRequest.getString("sysDate")
            finalPassword = getFinalPassword(userName, password, sysDate)
            loginRequest.put("userId", userName)
            loginRequest.put("pwd", finalPassword)
            appzillonHeader.put("userId", userName)
        } catch (ex: Exception) {
            //Sonar fix
        }

        verifyCredentials(userName, finalPassword, callBackId, aReqId, params)
    }

    private fun handleError(callBackId: String?, aReqId: String) {
        if (callBackId != null) {

            sendError(callBackId, errorCode334, aReqId)
        }
    }

    private fun getFinalPassword(
        userName: String?,
        password: String?,
        sysDate: String
    ) = if ("#DeviceId".equals(
            StringUtils.getString("authenticationType"),
            ignoreCase = true
        )
    ) {
        val json = JSONObject()
        json.put("userId", userName)
        json.put("pwd", password)
        json.put("date", sysDate) //new Date().
        ApzEncryptionPlugin.hashPwdforNativeLogin(json)
    } else {
        password
    }

    private fun getLoginRequest(
        params: JSONObject,
        appzillonHeader: JSONObject,
        loginRequest: JSONObject
    ): Pair<JSONObject, JSONObject> {
        var appzillonBody1: JSONObject
        var appzillonHeader1 = appzillonHeader
        var loginRequest1 = loginRequest
        try {
            if (params.has(AppzillonConstants.APPZILLON_BODY) && params.has(AppzillonConstants.APPZILLON_HEADER)) {
                appzillonBody1 = params.getJSONObject(AppzillonConstants.APPZILLON_BODY)
                appzillonHeader1 = params.getJSONObject(AppzillonConstants.APPZILLON_HEADER)
                if (appzillonBody1.has("loginRequest")) {
                    loginRequest1 = appzillonBody1.getJSONObject("loginRequest")
                }
            }
        } catch (e: Exception) {
            //Sonar fix
        }
        return Pair(appzillonHeader1, loginRequest1)
    }

    private fun verifyCredentials(
        userName: String?,
        finalPassword: String?,
        callBackId: String?,
        aReqId: String,
        params: JSONObject
    ) {
        if (userName.isNullOrEmpty() && finalPassword.isNullOrEmpty()) {
            apzPluginUtil.sendError(
                callBackId,
                errorCode334,
                null,
                activity,
                webView,
                true
            )
        } else {
            LoginfromContainer(webView, activity, aReqId, callBackId, apzPluginUtil).execute(
                activity.lifecycleScope,
                params
            )
        }
    }

    fun storeSecurely(callBackId: String?, key: String, value: String, promptBiometric: String?)
    {
        val jsonObject = JSONObject()
        jsonObject.put("value", value)
        jsonObject.put("promptBiometric", promptBiometric)
        EncryptedPrefHelper.save(key, jsonObject.toString())
        apzPluginUtil.sendSuccess(callBackId, jsonObject, false, activity, webView, true)
    }

    fun retrieveSecurely(callBackId: String?, key: String)
    {
        val credentials = EncryptedPrefHelper.read(key, "")
        var jsonObject: JSONObject? = null
        var value: String? = ""
        if (!credentials.isNullOrEmpty()) {
            try {
                jsonObject = JSONObject(credentials)
                value = jsonObject.getString("value")
                jsonObject.remove("value")
                jsonObject.put("text", value)
            } catch (ex: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendSuccess(callBackId, jsonObject, false, activity, webView, true)
        } else {
            apzPluginUtil.sendError(callBackId, "APZ-CNT-000", null, activity, webView, true)
        }
    }

    //Function to send error to the JS side when the isBiometric is true & credentials stored are empty/null.
    private fun sendError(callBackId: String, error: String, requestId: String) {
        val json = JSONObject()
        val params = JSONObject()
        try {
            json.put("error", error)
            json.put("id", callBackId)
            json.put("keepAlive", false)
            json.put("reqId", requestId)
            params.put("status", false)
            params.put("resFull", JSONObject())
            json.put("params", params)
            json.put("status", false)
            apzPluginUtil.sendSuccess(callBackId, json, false, activity, webView, true)
        } catch (ignored: JSONException) {
            //Sonar fix
        }
    }
}
