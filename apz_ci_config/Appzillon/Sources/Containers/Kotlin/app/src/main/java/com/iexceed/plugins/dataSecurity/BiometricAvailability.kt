package com.iexceed.plugins.dataSecurity

import android.app.KeyguardManager
import android.content.Context
import android.webkit.WebView
import androidx.biometric.BiometricManager
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class BiometricAvailability(webView: WebView,
                            activity: ApzActivity<*>,
                            override val apzPluginUtil: IapzPluginUtil) : ApzPlugin()
{
    init {
        super.aActivity = activity
        super.aWebview = webView
    }

    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
        } catch (e: JSONException) {
            //Sonar fix
        }
        checkBiometric()
    }

    private fun checkBiometric() {
        val biometricManager = BiometricManager.from(aActivity.applicationContext)
        val biometricStatus =
            biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        val keyguardManager = aActivity.applicationContext
            .getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (biometricStatus == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ||
            biometricStatus == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
            callback("NOTSUPPORTED")
        } else if (!keyguardManager.isKeyguardSecure) {
            callback("NOTSUPPORTED")
        } /*else if (biometricStatus ==BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ) {
            callback("NOTSUPPORTED");
        } */ else if (biometricStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            callback("NOTCONFIGURED")
        } else {
            callback("TOUCHID")
        }
    }

    fun callback(message: String?) {
        val json = JSONObject()
        json.put("biometricStatus", message)
        apzPluginUtil.sendSuccess(callbackId, json, false, aActivity, aWebview, true)
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = BiometricAvailability(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }
}
