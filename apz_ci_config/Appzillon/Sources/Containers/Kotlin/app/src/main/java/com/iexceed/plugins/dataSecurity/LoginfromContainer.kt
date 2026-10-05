package com.iexceed.plugins.dataSecurity

import android.app.Activity
import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.LifecycleCoroutineScope
import com.iexceed.common.ServerUtilities
import com.iexceed.common.StringUtils
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class LoginfromContainer(
    val webView: WebView,
    val context: Context,
    val requestId: String?,
    val callbackId: String?,
    val apzPluginUtil: IapzPluginUtil
) {
    val TAG = "LoginfrmContainer"
    fun execute(activityLifecycle: LifecycleCoroutineScope, aRequest: JSONObject) {
        activityLifecycle.launch(IO) {
            // Change this approach after DI integration
            // val apiInterface = RetrofitBuilder.apiService
            val apiInterface = RetrofitBuilder.getRetrofit(context).create(ApiService::class.java)
            val interfaceId = "appzillonReLoginRequest"
            var clientNonce: String

            val finalPayloadRequest = ServerUtilities.getEncryptedRequest(
                context,
                aRequest.toString(),
                isCNonceRequired = true
            )
            clientNonce = if ("Y".equals(StringUtils.getString("dataIntegrity"), ignoreCase = true)) finalPayloadRequest.getString("localCNonce") else ""
            if (finalPayloadRequest.has("localCNonce")) {
                finalPayloadRequest.remove("localCNonce")
            }

            val lReqObj = finalPayloadRequest.toString().toRequestBody()

            ApzLogger.initialTime(TAG, " RequestSent ")
            val call = apiInterface.requestLogin(lReqObj)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    ApzLogger.endTime(TAG, "Response ")
                    val lCallbackResObj = JSONObject()
                    val lRes = response
                    val lApzResponse = lRes.body()?.string()
                    lCallbackResObj.put("id", callbackId)
                    lCallbackResObj.put("keepAlive", false)
                    lCallbackResObj.put("reqId", requestId)

                    if (lRes.isSuccessful && lApzResponse != null) {
                        ApzLogger.d(TAG, " lRes Successful")

                        handlePayload(
                            lApzResponse,
                            clientNonce,
                            interfaceId,
                            lCallbackResObj
                        )
                    } else {
                        // If server response is null
                        sendError(lCallbackResObj)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    sendError(JSONObject())
                }
            })
        }
    }

    private fun handlePayload(
        lApzResponse: String?,
        clientNonce: String,
        interfaceId: String,
        lCallbackResObj: JSONObject
    ) {
        val lResponseObj = ServerUtilities.getDecryptedResponseObj(
            context,
            lApzResponse!!,
            clientNonce,
            interfaceId
        )
        if (lResponseObj != null) {
            if (lResponseObj.has("errorCode")) {
                sendError(lResponseObj.getString("errorCode"), lApzResponse, lCallbackResObj)
            } else {
                lResponseObj.let { sendSuccess(it, lCallbackResObj) }
            }
        } else {
            // If server response is null
            sendError(lCallbackResObj)
        }
    }

    private fun sendError(lCallbackResObj: JSONObject) {
        lCallbackResObj.put("id", callbackId)
        lCallbackResObj.put("keepAlive", false)
        lCallbackResObj.put("reqId", requestId)

        val lErrorObj = JSONObject()
        lErrorObj.put("status", false)
        lErrorObj.put("resFull", JSONObject())
        lCallbackResObj.put("params", lErrorObj)
        lCallbackResObj.put("status", false)
        apzPluginUtil.sendError(
            callbackId,
            "",
            lCallbackResObj,
            context as Activity,
            webView,
            true
        )
    }

    // Normal
    private fun sendError(
        aErrorCode: String,
        response: String?,
        lCallbackResObj: JSONObject
    ) {
        val lErrorObj = JSONObject()
        lErrorObj.put("status", false)
        lErrorObj.put("resFull", response)
        lCallbackResObj.put("params", lErrorObj)
        lCallbackResObj.put("status", false)
        lCallbackResObj.put("errorCode", aErrorCode)

        apzPluginUtil.sendError(
            callbackId,
            aErrorCode,
            lCallbackResObj,
            context as Activity,
            webView,
            true
        )
    }

    private fun sendSuccess(
        response: JSONObject,
        lCallbackResObj: JSONObject
    ) {
        val lSuccessObj = JSONObject()
        lSuccessObj.put("status", true)
        lSuccessObj.put("resFull", response)
        lCallbackResObj.put("params", lSuccessObj)
        lCallbackResObj.put("status", true)

        apzPluginUtil.sendSuccess(
            callbackId,
            lCallbackResObj,
            false,
            context as Activity,
            webView,
            true
        )
    }
}
