package com.iexceed.common

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonConstants.ANDROID_OS
import com.iexceed.common.AppzillonUtils.getCurrentSong
import com.iexceed.common.AppzillonUtils.getDeviceId
import com.iexceed.common.AppzillonUtils.ipAddress
import com.iexceed.common.ServerUtilities.getEncryptedRequest
import com.iexceed.common.StringUtils.getString
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder
import com.iexceed.retrofitmvvm.data.response.getappsectoken.AppsectokenResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
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
class GetAppSecToken(val webView: WebView, val context: Context,
                     val postAppSecToken: PostAppSecToken,
                     val refreshServerNonce: Boolean) {

    private val postObj: PostAppSecToken = postAppSecToken
    private val TAG = "GetAppSecToken"
    var errorCode = ""
    private val dErrorMessage = "Application tempered, press ok to exit"
    private val dErrorTitle = "Error"
    private val eCode = "APZ-APP-SIGN-FAULT"
    private var isWebViewLoaded = false
    private var isRefreshServerNonce = false

    private val activity = context as ApzActivity<*>

    fun execute()
    {

        activity.lifecycleScope.launch(IO) {
            AppzillonConstants.SNONCE = ""
            try {
                val appId = getString(StringUtils.APP_ID)
                val deviceId = getDeviceId()
                val requestId = "CSNONCE"
                val clientNonce = System.currentTimeMillis().toString()
                val interfaceId = "appzillonGetAppSecTokens"
                val origination = ipAddress()
                val os = ANDROID_OS
                val screenId = "Login"
                val signture = getCurrentSong(context)
                val source = "APPZILLON"
                val lPayLoadEncryption = getString("payloadEncryption");

                //Req formation
                val jsonObject = JSONObject()
                try {
                    val header = JSONObject()
                    header.put(AppzillonConstants.APP_ID, appId)
                    header.put(AppzillonConstants.SESSION_ID, "")
                    header.put(AppzillonConstants.DEVICE_ID, deviceId)
                    header.put("async", false)
                    header.put(AppzillonConstants.USER_ID, "")
                    header.put("screenId", screenId)
                    header.put(AppzillonConstants.REQ_STATUS, true)
                    header.put("source", source)
                    header.put("clientNonce", clientNonce)
                    header.put(AppzillonConstants.INTERFACE_ID, interfaceId)
                    header.put(AppzillonConstants.OS, os)
                    header.put("origination", origination)
                    header.put("signture", signture)

                    val reqBody = JSONObject()
                    val appFileReq = JSONObject()
                    appFileReq.put(AppzillonConstants.APP_ID, appId)
                    appFileReq.put(AppzillonConstants.DEVICE_ID, deviceId)
                    appFileReq.put("requestId", requestId)
                    reqBody.put("appzillonGetAppSecTokensRequest", appFileReq)
                    jsonObject.put(AppzillonConstants.APPZILLON_HEADER, header)
                    jsonObject.put(AppzillonConstants.APPZILLON_BODY, reqBody)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val lReq: RequestBody = getRequestBody(lPayLoadEncryption, jsonObject)

                // Change this approach after DI integration
                //val apiInterface = RetrofitBuilder.apiService
                val apiInterface = RetrofitBuilder.getRetrofit(activity).create(ApiService::class.java)
                //val apiInterface = RetrofitBuilder.apiService
                val call = apiInterface.getAppSecToken(lReq)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>) {
                        val lRes = response
                        val lApzResponse = lRes.body()?.string()
                        if (lRes.isSuccessful && lApzResponse != null)
                        {
                            if (lPayLoadEncryption == "Y")
                            {
                                handleResponseWithEncryption(lApzResponse, clientNonce, interfaceId)
                            }else{
                                handleResponseWithoutEncryption(lApzResponse)
                            }
                        } else {
                            handleUnSuccessfulResponse()
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        t.printStackTrace()
                    }
                })
            } catch (jsonException: Exception) {
                jsonException.printStackTrace()
            }
        }
    }

    private fun handleUnSuccessfulResponse() {
        if (!TextUtils.isEmpty(AppzillonConstants.SNONCE)) {
            if (!isWebViewLoaded) {
                postObj.executeAfterAppSecToken(refreshServerNonce)
            }
            if (AppzillonMainScreen.IS_APP_INITIALIZED && !isRefreshServerNonce) {
                webView.loadUrl("javascript:apz.server.setAppSecToken('Y');")
            }
        } else if (errorCode.isNotEmpty() && errorCode.length > 1 && errorCode.contains(eCode)) {
            ApzLogger.e(TAG, " errorCode : $errorCode")
            //Show Alertdialog
            showTamperedDialog()
        } else {
            if (
                (context.resources.getString(R.string.OFFLINESUPPORT)
                    .equals("Y", ignoreCase = true)
                        ||context.resources.getString(R.string.APPOFFLINESUPPORT)
                    .equals("Y", ignoreCase = true)
                        )&& !isWebViewLoaded
            ) {
                isWebViewLoaded = true
                postObj.executeAfterAppSecToken(refreshServerNonce)
            }
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    GetAppSecToken(webView, context, postAppSecToken, false).execute()
                },
                5000
            )
        }
    }

    private fun handleResponseWithoutEncryption(lApzResponse: String?) {
        val lAppsecTokenResponse = Gson().fromJson(
            lApzResponse, AppsectokenResponse::class.java
        )

        if (lAppsecTokenResponse.appzillonHeader.status) {
            val sessionToken =
                lAppsecTokenResponse.appzillonBody.appzillonGetAppSecTokensResponse.sessionToken
            val serverNonce =
                lAppsecTokenResponse.appzillonBody.appzillonGetAppSecTokensResponse.serverNonce
            val safeToken =
                lAppsecTokenResponse.appzillonBody.appzillonGetAppSecTokensResponse.safeToken

            AppzillonConstants.SNONCE = serverNonce
            AppzillonConstants.SESSIONTOKEN = sessionToken
            AppzillonConstants.APPZILLONSAFE = safeToken
            postObj.executeAfterAppSecToken(refreshServerNonce)
        } else {
            val error = lAppsecTokenResponse.appzillonErrors?.get(0)
            val errorMessage = error?.errorMessage
            errorCode = error?.errorCode.toString()
            ApzLogger.e(TAG, " errorMessage : $errorMessage")
        }
    }

    private fun handleResponseWithEncryption(
        lApzResponse: String?,
        clientNonce: String,
        interfaceId: String
    ) {
        val lResponseObj = ServerUtilities.getDecryptedResponseObj(
            activity, lApzResponse!!, clientNonce, interfaceId
        )
        if (lResponseObj != null) {
            val lAppsecTokenResponse = Gson().fromJson(
                lResponseObj.toString(),
                AppsectokenResponse::class.java
            )

            val lResponseTokenObj = lAppsecTokenResponse.appzillonBody
                .appzillonGetAppSecTokensResponse
            AppzillonConstants.SNONCE = lResponseTokenObj.serverNonce
            AppzillonConstants.SESSIONTOKEN = lResponseTokenObj.sessionToken

            if ("Y".equals(getString("payloadEncryption"), ignoreCase = true)) {
                AppzillonConstants.APPZILLONSAFE = lResponseTokenObj.safeToken
            }
            if (lAppsecTokenResponse.appzillonHeader.status) {
                postObj.executeAfterAppSecToken(refreshServerNonce)
            } else {
                val error = lAppsecTokenResponse.appzillonErrors?.get(0)
                error?.errorMessage
                errorCode = error?.errorCode.toString()
            }
        }
    }

    private fun getRequestBody(
        lPayLoadEncryption: String,
        jsonObject: JSONObject
    ) = if ("Y".equals(lPayLoadEncryption, ignoreCase = true)) {
        val lJSONObject = getEncryptedRequest(context, jsonObject.toString())
        lJSONObject.toString().toRequestBody()
    } else {
        jsonObject.toString().toRequestBody()
    }

    private fun showTamperedDialog()
    {
        val lDialogBuilder = AlertDialog.Builder(context)
        lDialogBuilder.setTitle(dErrorTitle)
        lDialogBuilder.setMessage(dErrorMessage)
        lDialogBuilder.setCancelable(false)
        lDialogBuilder.setNegativeButton(
            context.resources.getString(R.string.ok)) { dialog, _ ->
            dialog.cancel()
            activity.finish()
        }
        lDialogBuilder.create()
    }

    init {
        isRefreshServerNonce=refreshServerNonce
    }
}


