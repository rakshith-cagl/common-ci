package com.iexceed.plugins.dataSecurity

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONArray
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class ApzBiometricActivty : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        ApzBiometricAct = this
        supportActionBar?.hide()
        setFinishOnTouchOutside(false)
        val helper = BiometricHandler(
            webView,
            activity, callbackId, this
        )
        helper.startAuth(BiometricManager.from(this), BiometricAuth.cryptoObject , mJsonReq!!)
    }

    private fun cancelFingerprint() {
        BiometricHandler.cancellationSignal?.cancel()
        BiometricHandler.cancellationSignal = null
    }

    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        cancelFingerprint()
    }

    companion object {
        lateinit var activity: ApzActivity<*>
        lateinit var apzPluginUtil: IapzPluginUtil
        lateinit var webView: WebView
        lateinit var callbackId: String
        lateinit var ApzBiometricAct: AppCompatActivity
        private var mJsonReq: JSONObject? = null

        @JvmStatic
        fun init(
            aActivity: ApzActivity<*>,
            aWebView: WebView,
            aCallbackId: String,
            aReqObj: JSONObject,
            mApzPluginUtil: IapzPluginUtil
        ) {
            activity = aActivity
            callbackId = aCallbackId
            webView = aWebView
            mJsonReq = aReqObj
            apzPluginUtil = mApzPluginUtil
        }

        @JvmStatic
        fun callFailureCB(errorCode: String?, errorMessage: String?) {
            val resFull = JSONObject()
            val params = JSONObject()
            try {
                val paramsJson = mJsonReq!!.getJSONObject("params")
                val reqFullJson = paramsJson.getJSONObject("reqFull")
                val appzillonHeader = reqFullJson.getJSONObject("appzillonHeader")
                appzillonHeader.put("status", false)
                reqFullJson.put("appzillonHeader", appzillonHeader)
                val jsonArray = JSONArray()
                val appzillonErrors = JSONObject()
                appzillonErrors.put("errorMessage", errorMessage)
                appzillonErrors.put("errorCode", errorCode)
                jsonArray.put(appzillonErrors)
                reqFullJson.put("appzillonErrors", jsonArray)
                resFull.put("resFull", reqFullJson)
                resFull.put("status", false)
                params.put("params", resFull)
                params.put("reqId", mJsonReq?.getString("reqId"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            apzPluginUtil.sendError(callbackId, errorCode, params, activity, webView, true)
            ApzBiometricAct.finish()
        }
    }
}
