package com.iexceed.plugins.dataSecurity

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class ApzDataSecurity private constructor(webView: WebView,
                                          activity: ApzActivity<*>,
                                          override val apzPluginUtil: IapzPluginUtil
) : ApzPlugin()
{
    override var TAG = "ApzDataSecurity"
    private var  mStoreCredentialsSecurely :StoreCredentialsSecurely
    init {
        super.aActivity = activity
        super.aWebview = webView
        mStoreCredentialsSecurely = StoreCredentialsSecurely(aWebview, aActivity, apzPluginUtil)
    }

    override fun execute(params: JSONObject)
    {
        var action = ""
        try {
            json = params
            super.callbackId = params.getString("id")
            promptBiometric = params.optString("promptBiometric")
            action = params.getString("action")
            params.getString("reqId")
        } catch (ex: JSONException) {
            //Sonar fix
        }
        //store key value Securely
        if ("STORESECURELY".equals(action, ignoreCase = true)) {
            handleStoreSecurely(params)
        } else if ("RETRIEVECREDENTIALSSECURELY".equals(action, ignoreCase = true)) {
            handleRetrieveCredentialSecurely(params)
        } else if ("STORECREDENTIALSSECURELY".equals(action, ignoreCase = true)) {
            handleStoreCredentailSecurely(params)
        } else {
            try {
                if ("RETRIEVESECURELY".equals(json.getString("action"), ignoreCase = true)) {
                    val credentials = EncryptedPrefHelper.read(json.getString("key"), "")

                    if (credentials == "") {
                        val json = JSONObject()
                          json.put("text", "Please call storeSecurely before retrieveSecurely")
                          apzPluginUtil.sendError(callbackId, "", json, aActivity, aWebview, true)
                    }else{
                        val jsonObject = JSONObject(credentials!!)
                        promptBiometric = jsonObject.optString("promptBiometric")
                        handleStoreSecurely(params)
                    }
                }
            } catch (ex: JSONException) {
                val json = JSONObject()
                 json.put("text", "expected value not found")
                apzPluginUtil.sendError(callbackId, "", json, aActivity, aWebview, true)
            }
        }
    }

    private fun handleStoreCredentailSecurely(params: JSONObject) {
        if (params.get("isBiometric").equals("Y")) {
            callBiometric(params)
        } else {
            secureData()
        }
    }

    private fun handleRetrieveCredentialSecurely(params: JSONObject) {
        try {
            val isBiometric = params.getJSONObject("params").optString("isBiometric")
            if ("Y".equals(isBiometric, ignoreCase = true)) {
                callBiometric(params)
            } else {
                secureData()
            }
        } catch (ex: JSONException) {
            //Sonar fix
        }
    }

    private fun handleStoreSecurely(params: JSONObject) {
        if ("Y".equals(promptBiometric, ignoreCase = false)) {
            callBiometric(params)
        } else {
            secureData()
        }
    }

    companion object {
        lateinit var json: JSONObject
        private var pluginObj: ApzPlugin? = null
        var promptBiometric : String = ""

        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzDataSecurity(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun getPlugin(): ApzPlugin?{
            return pluginObj
        }

        val isPlugin: Boolean
            get() = true
    }

    fun secureData() {
        var action: String? = ""
        var key: String? = ""
        var value: String? = ""
        try {
            action = json.getString("action")
            when {
                "STORESECURELY".equals(action, ignoreCase = true) -> {
                    key = json.getString("key")
                    value = json.getString("value")
                    mStoreCredentialsSecurely.storeSecurely(callbackId, key, value, promptBiometric)
                }
                "STORECREDENTIALSSECURELY".equals(action, ignoreCase = true) -> {
                    key = json.getString("userId")
                    value = json.getString("password")
                    mStoreCredentialsSecurely.storeCredentialsSecurely(callbackId,key, value)
                }
                "RETRIEVESECURELY".equals(action, ignoreCase = true) -> {
                    if(json.has("key")){
                        key = json.getString("key")
                        mStoreCredentialsSecurely.retrieveSecurely(callbackId, key)
                    }
                }
                "RETRIEVECREDENTIALSSECURELY".equals(action, ignoreCase = true) -> {
                    if(json.has("params")){
                        val reqFull: JSONObject = json.getJSONObject("params").getJSONObject("reqFull")
                        val isBiometric = json.getJSONObject("params").optString("isBiometric")
                        val isBiometricbool: Boolean = "Y".equals(isBiometric, ignoreCase = true)
                        val reqId = json.getString("reqId")
                        mStoreCredentialsSecurely.loginSecurely(callbackId, reqId, reqFull, isBiometricbool)
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun callBiometric(params: JSONObject) {
        val biometricAuth = BiometricAuth(super.aWebview, super.aActivity, super.callbackId!!, apzPluginUtil)
        biometricAuth.biometricAuthentication(params)
    }
}
