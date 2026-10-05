package com.iexceed.plugins.dataSecurity

import android.content.Context
import android.os.CancellationSignal
import android.webkit.WebView
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.iexceed.appzillonapp.R
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.dataSecurity.ApzBiometricActivty.Companion.callFailureCB
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class BiometricHandler(
    webView: WebView,
    activity: ApzActivity<*>,
    callerId: String,
    aFingerprintActivity: FragmentActivity?) : BiometricPrompt.AuthenticationCallback()
{
    private val appContext: Context = activity.applicationContext
    private val mWebView: WebView = webView
    private val mActivity: ApzActivity<*> = activity
    private val TAG = "FingerprintHandler"
    private val errorCode232 = "APZ-CNT-232"
    private val errorCode235 = "APZ-CNT-235"
    private val errorCode233 = "APZ-CNT-233"

    //  TextView textView;
    var mFingerprintActivity: FragmentActivity? = null

    //   ImageView imageView;
    var biometricPrompt: BiometricPrompt? = null
    var promptInfo: PromptInfo? = null
    fun startAuth(
        manager: BiometricManager,
        cryptoObject: BiometricPrompt.CryptoObject?,
        params : JSONObject
    ) {
        cancellationSignal = CancellationSignal()
        val biometricType = params.optString("biometricType")
        if (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
          if(mFingerprintActivity == null){
              biometricPrompt = BiometricPrompt(
                  mActivity,
                  ContextCompat.getMainExecutor(appContext),
                  this)
              biometricPrompt?.authenticate(getPromptInfo(biometricType),cryptoObject!!)
          }else{
              biometricPrompt = BiometricPrompt(
                  mFingerprintActivity!!,
                  ContextCompat.getMainExecutor(appContext),
                  this)
              biometricPrompt?.authenticate(getPromptInfo(biometricType),cryptoObject!!)
          }
        }
    }

    @JvmName("getPromptInfo1")
    private fun getPromptInfo(biometricType: String): PromptInfo {
        promptInfo = PromptInfo.Builder()
            .setTitle(mActivity.resources.getString(R.string.biotitle))
            .setSubtitle(mActivity.resources.getString(R.string.biosubtitle))
            .setDescription(mActivity.resources.getString(R.string.biodescription))
            .apply {
                when(biometricType) {
                    "strong", "" -> {
                        setNegativeButtonText(mActivity.resources.getString(R.string.bionegative))
                        setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    }
                    "weak" -> {
                        setNegativeButtonText(mActivity.resources.getString(R.string.bionegative))
                        setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
                    }
                    "weak_passcode" -> {
                        setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    }
                }
            }
            .build()
        return promptInfo!!
    }


    override fun onAuthenticationError(
        errorCode: Int,
        errString: CharSequence
    ) {
        when (errorCode)
        {
            BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
                //the device does not have a biometric sensor
                callFailureCB(errorCode232, "Device does not have a biometric sensor.")
            }

            BiometricPrompt.ERROR_HW_UNAVAILABLE  -> {
                //the hardware is unavailable. Try again later.
                callFailureCB(errorCode232, "Fingerprint hardware is unavailable.")
            }

            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                // user clicked negative/cancel button
                callFailureCB(errorCode232, "Fingerprint operation canceled.")
            }

            BiometricPrompt.ERROR_NO_SPACE -> {
                // the operation cannot be completed because there’s not enough storage remaining to complete the operation.
                callFailureCB(errorCode232, "There’s no enough storage remaining to complete the operation.")
            }

            BiometricPrompt.ERROR_LOCKOUT  -> {
                //the operation was cancelled because the API is locked out due to too many attempts.
                // This occurs after 5 failed attempts, and lasts for 30 seconds.
                callFailureCB(errorCode232, "Too many attempts. Try again later.")
            }
            else -> {
                callFailureCB(errorCode235, "Too many attempts. Try again later.")
            }
        }
    }

    override fun onAuthenticationFailed() {
        callFailureCB(errorCode233, "Fingerprint authentication wrong.")
        //Sonar fix
    }

    override fun onAuthenticationSucceeded(
        result: BiometricPrompt.AuthenticationResult) {
        if(result.cryptoObject != null && ApzDataSecurity.getPlugin() is ApzDataSecurity){
            (ApzDataSecurity.getPlugin() as ApzDataSecurity).secureData()
        } else {
            callFailureCB(errorCode232, "Fingerprint operation failed.")
        }
        mFingerprintActivity?.finish()
    }

    companion object {
        var cancellationSignal: CancellationSignal? = null
        private lateinit var mCallerId: String
    }

    init {
        mCallerId = callerId
        mFingerprintActivity = aFingerprintActivity
    }
}
