package com.iexceed.plugins.changepassword

import android.app.Activity
import android.content.Context
import android.webkit.WebView
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.iexceed.common.ApzActivity
import com.iexceed.common.ServerUtilities
import com.iexceed.common.StringUtils
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder
import com.iexceed.retrofitmvvm.data.model.changepassword.ApzChangePasswordDecryptedResponse
import com.iexceed.retrofitmvvm.data.model.changepassword.ChangePasswordResponse
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

class SendChangePasswordRequest(
    val aReqId: String,
    val aCallBackId: String,
    val aActivity: ApzActivity<*>,
    val aWebView: WebView,
    val apzPluginUtil: IapzPluginUtil
) {
    val TAG = "SendChgPassReq"
    fun execute(context: Context, aRequestJson: JSONObject?) {
        val lPayLoadEncryption = StringUtils.getString("payloadEncryption")
        val lGson = Gson()
        // val apiInterface = RetrofitBuilder.apiService
        val apiInterface = RetrofitBuilder.getRetrofit(context).create(ApiService::class.java)
        var clientNonce = ""
        val interfaceId: String? = aRequestJson?.getJSONObject("appzillonHeader")?.getString("interfaceId")

        aActivity.lifecycleScope.launch(IO) {
            if (lPayLoadEncryption == "N") {
                aRequestJson?.put("encMode", 1)
            }
            val finalPayloadRequest = ServerUtilities.getEncryptedRequest(
                context,
                aRequestJson.toString(),
                isCNonceRequired = true
            )
            if (finalPayloadRequest.has("localCNonce")) {
                clientNonce = if ("Y".equals(StringUtils.getString("dataIntegrity"), ignoreCase = true)) finalPayloadRequest.getString("localCNonce") else ""
                finalPayloadRequest.remove("localCNonce")
            }

            val lReqObj = finalPayloadRequest.toString().toRequestBody()

            val call = apiInterface.requestChangePassword(lReqObj)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val lCallbackResObj = JSONObject()
                    val lRes = response
                    val lApzResponse = lRes.body()?.string()
                    if (lRes.isSuccessful && lApzResponse != null) {
                        handleWhenEncryptionEnabled(
                            interfaceId,
                            context,
                            lApzResponse,
                            clientNonce,
                            lCallbackResObj,
                            lGson
                        )
                    } else {
                        // If server response is null
                        sendError(aActivity, "", lCallbackResObj)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    sendError(aActivity, "", JSONObject())
                }
            })
        }
    }

    private fun handleResponse(
        lGson: Gson,
        lApzResponse: String?,
        lCallbackResObj: JSONObject
    ) {
        val lApzChgPasswordResponse = lGson.fromJson(
            lApzResponse,
            ChangePasswordResponse::class.java
        )

        if (lApzChgPasswordResponse?.appzillonHeader?.status == true) {
            // Proper respnse from server
            sendSuccess(aActivity, lApzChgPasswordResponse, lCallbackResObj)
        } else {
            sendError(
                aActivity,
                lApzChgPasswordResponse?.appzillonErrors?.get(0)?.errorCode.toString(),
                lApzResponse,
                lCallbackResObj
            )
        }
    }

    private fun handleWhenEncryptionEnabled(
        interfaceId: String?,
        context: Context,
        lApzResponse: String?,
        clientNonce: String,
        lCallbackResObj: JSONObject,
        lGson: Gson
    ) {
        val lResponseObj = ServerUtilities.getDecryptedResponseObj(
            context,
            lApzResponse!!,
            clientNonce,
            interfaceId.toString()
        )

        lCallbackResObj.put("id", aCallBackId)
        lCallbackResObj.put("keepAlive", false)
        lCallbackResObj.put("reqId", aReqId)
        if (lResponseObj != null) {
            val lApzChgPasswordResponse = lGson.fromJson(
                lResponseObj.toString(),
                ApzChangePasswordDecryptedResponse::class.java
            )
            if (lApzChgPasswordResponse.appzillonHeader.status) {
                // Proper respnse from server
                sendEncryptedSuccess(lResponseObj, lCallbackResObj)
            } else {
                sendEncryptedError(
                    aActivity,
                    lApzChgPasswordResponse.appzillonErrors[0].errorCode,
                    lResponseObj.toString(),
                    lCallbackResObj
                )
            }
        } else {
            // If server response is null
            sendError(aActivity, "", lCallbackResObj)
        }
    }

    private fun sendEncryptedSuccess(
        response: JSONObject?,
        lCallbackResObj: JSONObject
    ) {
        val lSuccessObj = JSONObject()
        lSuccessObj.put("status", true)
        lSuccessObj.put("resFull", response)
        lCallbackResObj.put("params", lSuccessObj)
        lCallbackResObj.put("status", true)

        apzPluginUtil.sendSuccess(
            aCallBackId,
            lCallbackResObj,
            false,
            aActivity,
            aWebView,
            true
        )
    }

    private fun sendEncryptedError(
        activity: Activity,
        aErrorCode: String,
        response: String,
        lCallbackResObj: JSONObject
    ) {
        val lErrorObj = JSONObject()
        lErrorObj.put("status", false)
        lErrorObj.put("resFull", response)
        lCallbackResObj.put("params", lErrorObj)
        lCallbackResObj.put("status", false)
        lCallbackResObj.put("errorCode", aErrorCode)

        apzPluginUtil.sendError(
            aCallBackId,
            aErrorCode,
            lCallbackResObj,
            activity,
            aWebView,
            true
        )
    }

    private fun sendSuccess(
        activity: Activity,
        response: ChangePasswordResponse,
        lCallbackResObj: JSONObject
    ) {
        val lObj = JSONObject()
        lObj.put("appzillonBody", JSONObject(Gson().toJson(response.appzillonBody)))
        lObj.put("appzillonHeader", JSONObject(Gson().toJson(response.appzillonHeader)))
        val lSuccessObj = JSONObject()
        lSuccessObj.put("status", true)
        lSuccessObj.put("resFull", lObj)
        lCallbackResObj.put("params", lSuccessObj)
        lCallbackResObj.put("status", true)

        apzPluginUtil.sendSuccess(
            aCallBackId,
            lCallbackResObj,
            false,
            activity,
            aWebView,
            true
        )
    }

    // Normal
    private fun sendError(
        activity: Activity,
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
            aCallBackId,
            aErrorCode,
            lCallbackResObj,
            activity,
            aWebView,
            true
        )
    }

    private fun sendError(activity: Activity, aErrorCode: String, lCallbackResObj: JSONObject) {
        lCallbackResObj.put("id", aCallBackId)
        lCallbackResObj.put("keepAlive", false)
        lCallbackResObj.put("reqId", aReqId)

        val lErrorObj = JSONObject()
        lErrorObj.put("status", false)
        lErrorObj.put("resFull", JSONObject())
        lCallbackResObj.put("params", lErrorObj)
        lCallbackResObj.put("status", false)

        apzPluginUtil.sendError(
            aCallBackId,
            aErrorCode,
            lCallbackResObj,
            activity,
            aWebView,
            true
        )
    }
}
