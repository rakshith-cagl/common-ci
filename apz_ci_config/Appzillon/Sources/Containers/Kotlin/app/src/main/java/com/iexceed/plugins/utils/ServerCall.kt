package com.iexceed.plugins.utils

import android.webkit.WebView
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.iexceed.common.ApzActivity
import com.iexceed.common.ServerUtilities
import com.iexceed.common.StringUtils
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder
import com.iexceed.retrofitmvvm.data.model.logout.ApzServerCallResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/

class ServerCall(
    val aActivity: ApzActivity<*>,
    val aWebview: WebView,
    val aCallbackId: String?,
    val aReqID: String?,
    apzPluginUtil: IapzPluginUtil
) {
    val TAG = "ServerCall"
    private var mApzPluginUtil = apzPluginUtil
    fun execute(aRequest: JSONObject?) {
        val interfaceId: String? = aRequest?.getJSONObject("appzillonHeader")?.getString("interfaceId")
        if (interfaceId.isNullOrEmpty()) {
            val lRes = JSONObject()
            lRes.put("errormessage", "InterfaceId is null")
            mApzPluginUtil.sendError(
                aCallbackId,
                "APZ-CNT-330",
                lRes,
                aActivity,
                aWebview,
                true
            )
            return
        }
        sendReq(aRequest)
    }

    private fun sendReq(aRequest: JSONObject?) {
        val lPayLoadEncryption = StringUtils.getString("payloadEncryption")
        val lGson = Gson()
        // Change this approach after DI integration
        // val apiInterface = RetrofitBuilder.apiService
        val apiInterface = RetrofitBuilder.getRetrofit(aActivity).create(ApiService::class.java)
        var clientNonce: String
        val interfaceId: String? = aRequest?.getJSONObject("appzillonHeader")?.getString("interfaceId")
        aActivity.lifecycleScope.launch(IO) {

            val finalPayloadRequest = ServerUtilities.getEncryptedRequest(
                aActivity,
                aRequest.toString(),
                isCNonceRequired = true
            )

            clientNonce =if ("Y".equals(StringUtils.getString("dataIntegrity"), ignoreCase = true)) finalPayloadRequest.getString("localCNonce") else ""
            if(finalPayloadRequest.has("localCNonce")) {
                finalPayloadRequest.remove("localCNonce")
            }
           val lReqObj = finalPayloadRequest.toString().toRequestBody()

            val call = apiInterface.serverCallRequest(lReqObj)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val lCallbackResObj = JSONObject()
                    val lRes = response
                    val lApzResponse = lRes.body()?.string()
                    lCallbackResObj.put("id", aCallbackId)
                    lCallbackResObj.put("keepAlive", false)
                    lCallbackResObj.put("reqId", aReqID)

                    if (lRes.isSuccessful && lApzResponse != null) {
                        handlePayloadEncryption(
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

    private fun handlePayloadEncryption(
        lApzResponse: String?,
        clientNonce: String,
        interfaceId: String?,
        lCallbackResObj: JSONObject
    ) {
        val lResponseObj = ServerUtilities.getDecryptedResponseObj(
            aActivity,
            lApzResponse!!,
            clientNonce,
            interfaceId.toString()
        )
        if (lResponseObj != null) {
            if (lResponseObj.has("errorCode")) {
                sendError(
                    lResponseObj.getString("errorCode"),
                    lApzResponse,
                    lCallbackResObj
                )
            } else {
                sendEncryptedSuccess(lResponseObj, lCallbackResObj)
            }
        } else {
            sendError(lCallbackResObj)
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

        mApzPluginUtil.sendSuccess(
            aCallbackId,
            lCallbackResObj,
            false,
            aActivity,
            aWebview,
            true
        )
    }

    private fun sendSuccess(
        response: ApzServerCallResponse,
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

        mApzPluginUtil.sendSuccess(
            aCallbackId,
            lCallbackResObj,
            false,
            aActivity,
            aWebview,
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

        mApzPluginUtil.sendError(
            aCallbackId,
            aErrorCode,
            lCallbackResObj,
            aActivity,
            aWebview,
            true
        )
    }

    private fun sendError(lCallbackResObj: JSONObject) {
        lCallbackResObj.put("id", aCallbackId)
        lCallbackResObj.put("keepAlive", false)
        lCallbackResObj.put("reqId", aReqID)

        val lErrorObj = JSONObject()
        lErrorObj.put("status", false)
        lErrorObj.put("resFull", JSONObject())
        lCallbackResObj.put("params", lErrorObj)
        lCallbackResObj.put("status", false)

        mApzPluginUtil.sendError(
            aCallbackId,
            "",
            lCallbackResObj,
            aActivity,
            aWebview,
            true
        )
    }
}
