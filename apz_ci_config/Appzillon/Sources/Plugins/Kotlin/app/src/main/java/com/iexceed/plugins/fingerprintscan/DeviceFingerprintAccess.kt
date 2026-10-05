package com.iexceed.plugins.fingerprintscan

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.webkit.WebView
import androidx.biometric.BiometricManager
import com.iexceed.common.ApzActivity
import com.iexceed.common.FingerprintUtils
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.dataSecurity.BiometricAuth
import com.iexceed.plugins.dataSecurity.BiometricHandler
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.KeyGenerator


/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class DeviceFingerprintAccess private constructor(webView: WebView,
                                                  activity: ApzActivity<*>,
                                                  override val apzPluginUtil: IapzPluginUtil) : ApzPlugin()
{
    private lateinit var fingerprintManager: BiometricManager
    var keyguardManager: KeyguardManager? = null
    private var keyStore: KeyStore? = null
    private var keyGenerator: KeyGenerator? = null
    override var TAG = "DeviceFingerprintAccess"
    private var mCallbackId: String? = null

    var mJSONreq: JSONObject? =  null

    init {
        super.aActivity = activity
        super.aWebview = webView
    }

   private fun enableFingerScanner(
       activity: ApzActivity<*>, webView: WebView,
       callbackID: String?
   )
    {

            keyguardManager = getKeyguardManager(activity)
            fingerprintManager = getBiometricManager(activity)
            keyStore = fetchKeyStore()
            keyGenerator = fetchKeyGenerator()
            proceedKeyGeneration(keyStore, keyGenerator)
            if (FingerprintUtils.cipherInit(keyStore,"appzillon")) {

                val helper = getBiometricHandler(webView, activity, callbackID)
                helper.startAuth(fingerprintManager, BiometricAuth.cryptoObject,  mJSONreq!!)
            }

    }

    private fun getBiometricHandler(webView: WebView, activity: ApzActivity<*>, callbackID: String?): BiometricHandler{

        return BiometricHandler(webView, activity, callbackID!!,null)
    }

    private fun getBiometricManager(activity: Activity): BiometricManager {

        return BiometricManager.from(activity)
    }

    private fun getKeyguardManager(activity: Activity): KeyguardManager?{

        return activity.applicationContext.
        getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager?
    }

    private fun fetchKeyStore(): KeyStore?{

        return KeyStore.getInstance("AndroidKeyStore")
    }

    private fun fetchKeyGenerator(): KeyGenerator?{

        var keyGenerator: KeyGenerator? = null
        try {
            keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        } catch (e: Exception){

            ApzLogger.e(TAG, e.toString())
        }

        return keyGenerator
    }

    private fun proceedKeyGeneration(keyStore: KeyStore?, keyGenerator: KeyGenerator?)
    {
        try {
            keyStore?.load(null)
            keyGenerator?.init(
                KeyGenParameterSpec.Builder(
                    KEY_NAME, KeyProperties.PURPOSE_ENCRYPT
                            or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7).build()
            )
            keyGenerator?.generateKey()
        }catch (e: Exception){

            sendError(null, e.message.toString())
        }
    }

    override fun execute(params: JSONObject)
    {
        ApzLogger.d(TAG,"Inside Execute")
        try {
            mJSONreq= params
            mCallbackId = params.getString("id")
            aActivity.runOnUiThread {

                enableFingerScanner(aActivity, aWebview, mCallbackId)

            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApzLogger.e(TAG, e.toString())
            sendError("APZ-CNT-077", null)
        }
    }

    private fun sendError(errorCode: String?, errorMessage: String?){

        val jsonObject = JSONObject()

        if (errorMessage != null){

            jsonObject.put("error", errorMessage)
        }
        apzPluginUtil.sendError(
            mCallbackId, errorCode, jsonObject, aActivity, aWebview,
            true)
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        const val KEY_NAME = "appzillon"
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = DeviceFingerprintAccess(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        val isPlugin: Boolean get() = true
    }
}
