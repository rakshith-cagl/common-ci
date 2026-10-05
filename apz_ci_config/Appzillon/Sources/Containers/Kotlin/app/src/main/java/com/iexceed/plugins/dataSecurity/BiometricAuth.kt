package com.iexceed.plugins.dataSecurity

import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Log
import android.webkit.WebView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat
import com.iexceed.common.ApzActivity
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.dataSecurity.ApzBiometricActivty.Companion.init
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class BiometricAuth(
    private val webView: WebView,
    private val activity: ApzActivity<*>,
    private val callbackId: String,
    private val apzPluginUtil: IapzPluginUtil
) {
    private var cipher: Cipher? = null
    var TAG = "DeviceFingerprintAccess"
    private val value: String? = null
    private lateinit var permissions: Array<String>
    lateinit var jsonReq: JSONObject

    private fun enableFingerScanner() {
        Log.w(TAG, " Inside enableFingerScanner ")
        biometricManager = BiometricManager.from(activity)
        val biometricStatus =
            biometricManager?.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        val keyguardManager = activity.applicationContext
            .getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (biometricStatus == BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ||
            biometricStatus == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
        ) {
            callErrorCallback("No FingerPrint hardware found!", "APZ-CNT-215")
            return
        }
        if (!keyguardManager.isKeyguardSecure) {
            callErrorCallback("Lock screen security not enabled in Settings", "APZ-CNT-324")
            return
        }
        if (biometricStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
            callErrorCallback("Register at least one biometric in Settings", "APZ-CNT-218")
            return
        }
        if (cipherInit()) {
            init(activity, webView, callbackId, jsonReq, apzPluginUtil)
            cryptoObject = cipher?.let { BiometricPrompt.CryptoObject(it) }
            val intent = Intent(activity, ApzBiometricActivty::class.java)
            activity.startActivity(intent)
        } else {
            Log.w(TAG, " Inside ELSE NO ERROR ")

            val resFull = JSONObject()
            val params = JSONObject()
            try {
                val paramsJson = jsonReq.getJSONObject("params")
                val reqFullJson = paramsJson.getJSONObject("reqFull")
                val appzillonHeader = reqFullJson.getJSONObject("appzillonHeader")
                appzillonHeader.put("status", false)
                reqFullJson.put("appzillonHeader", appzillonHeader)
                val jsonArray = JSONArray()
                val appzillonErrors = JSONObject()
                appzillonErrors.put("errorMessage", "Lock screen security changed in Settings.")
                appzillonErrors.put("errorCode", "APZ-CNT-325")
                jsonArray.put(appzillonErrors)
                reqFullJson.put("appzillonErrors", jsonArray)
                resFull.put("resFull", reqFullJson)
                resFull.put("status", false)
                resFull.put("ignoreDispMsg", true)
                params.put("params", resFull)
                params.put("reqId", jsonReq.getString("reqId"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            apzPluginUtil.sendError(callbackId, "APZ-CNT-325", params, activity, webView, true)
        }
    }

    private fun cipherInit(): Boolean {
        cipher = try {
            Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e)
        }
        var lSecretKey = getSecretKey()
        if (lSecretKey == null) {
            generateSecretKey(
                KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true) // Invalidate the keys if the user has registered a new biometric
                    // credential, such as a new fingerprint. Can call this method only
                    // on Android 7.0 (API level 24) or higher. The variable
                    .setInvalidatedByBiometricEnrollment(true)
                    .build()
            )
            lSecretKey = getSecretKey()

        }
        return try {
            cipher?.init(Cipher.ENCRYPT_MODE, lSecretKey)
            true
        } catch (e: KeyPermanentlyInvalidatedException) {
            //Sonar fix
            generateSecretKey(
                KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true) // Invalidate the keys if the user has registered a new biometric
                    .setInvalidatedByBiometricEnrollment(true)
                    .build()
            )
            false
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        var keyGenerator: KeyGenerator? = null
        try {
            keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: NoSuchProviderException) {
            e.printStackTrace()
        } catch (e: InvalidAlgorithmParameterException) {
            e.printStackTrace()
        }
    }

    // Before the keystore can be accessed, it must be loaded.
    private fun getSecretKey(): SecretKey? {
        var keyStore: KeyStore? = null
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            // Before the keystore can be accessed, it must be loaded.
            keyStore.load(null)
            val lKey = keyStore.getKey(KEY_NAME, null)
            if (lKey != null) {
                return lKey as SecretKey
            }
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        } catch (e: UnrecoverableKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
            return null
        }
        return null
    }

    private fun callErrorCallback(errorMsg: String?, errorCode: String?) {
        val json = JSONObject()
        try {
            json.put("text", errorMsg)
            json.put("reqId", jsonReq.getString("reqId"))
        } catch (e: JSONException) {
            //Sonar fix
        }
        apzPluginUtil.sendError(callbackId, errorCode, json, activity, webView, true)
        return
    }

    fun biometricAuthentication(params: JSONObject) {
        Log.w(TAG, " Inside biometricAuthentication")

        try {
            jsonReq = params
            Log.w(TAG, " Inside SDK_INT >= Build.VERSION_CODES.P ")
            enableFingerScanner()

        } catch (e: Exception) {
            //Sonar fix
        }
    }

    private fun requestForPermission() {
        activity.startOnPermissionForResult(activity, permissions,
            PluginConstants.APZ_REQ_FINGERPRINT, object : OnPermissionsResultHandler() {
                override fun handlePermissionResult(
                    requestCode: Int,
                    permissions: Array<String?>,
                    grantResults: IntArray
                ) {
                    if (requestCode == PluginConstants.APZ_REQ_FINGERPRINT) {
                        handleFingerprintPermissionResult(permissions)
                    } else {
                        PermissionDeniedCallback()
                    }
                }
            }
        )
    }

    private fun handleFingerprintPermissionResult(permissions: Array<String?>) {
        var denied = false
        var neverAskAgain = false
        for (permission in permissions) {
            if (!permission.isNullOrEmpty()) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        permission
                    )
                ) {
                    denied = true
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            activity,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        neverAskAgain = true
                    }
                }
            }
        }
        when {
            neverAskAgain -> {
                PermissionDeniedCallback()
            }
            denied -> {
                displayReconfirmationMessage()
            }
            else -> {
                enableFingerScanner()
            }
        }
    }

    private fun displayReconfirmationMessage() {
        val message = "To access fingerprint ,allow app to access by granting requested permissions"
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle("Permission Denied")
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.cancel()
                requestForPermission()
            }.setNegativeButton("Deny") { dialog, _ ->
                dialog.cancel()
                PermissionDeniedCallback()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun PermissionDeniedCallback() {
        //Sonar fix
    }

    companion object {
        private const val KEY_NAME = "appzillon"
        var biometricManager: BiometricManager? = null
        var cryptoObject: BiometricPrompt.CryptoObject? = null
    }
}
