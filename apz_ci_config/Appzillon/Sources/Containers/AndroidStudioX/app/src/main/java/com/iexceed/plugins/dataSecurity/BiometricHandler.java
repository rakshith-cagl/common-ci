package com.iexceed.plugins.dataSecurity;

import android.app.Activity;
import android.content.Context;
import android.os.CancellationSignal;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.iexceed.common.ApzActivity;
import com.iexceed.appzillonapp.R;


public class BiometricHandler extends
        BiometricPrompt.AuthenticationCallback {

    public static CancellationSignal cancellationSignal;
    private final Context appContext;
    private final Activity mActivity;
    FragmentActivity fingerprintActivity;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;

    public BiometricHandler(WebView webView , ApzActivity activity , String callerId,Activity mfingerprintActivity) {
        appContext = activity.getApplicationContext();
        mActivity = activity;
        fingerprintActivity = (FragmentActivity) mfingerprintActivity;
    }

    public void startAuth(BiometricManager manager,
                          BiometricPrompt.CryptoObject cryptoObject, String biometricType) {

        cancellationSignal = new CancellationSignal();

   
        if (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS){
             biometricPrompt = new BiometricPrompt( fingerprintActivity,
                    ContextCompat.getMainExecutor(appContext),
                    this);
            biometricPrompt.authenticate(getPromptInfo(biometricType));
        }


    }

    private BiometricPrompt.PromptInfo getPromptInfo(String biometricType) {
        BiometricPrompt.PromptInfo.Builder builder = new BiometricPrompt.PromptInfo.Builder();
        builder.setTitle(mActivity.getResources().getString(R.string.biotitle));
        builder.setSubtitle(mActivity.getResources().getString(R.string.biosubtitle));
        builder.setDescription(mActivity.getResources().getString(R.string.biodescription));
        if (biometricType.equalsIgnoreCase("weak")) {
            builder.setNegativeButtonText(mActivity.getResources().getString(R.string.bionegative));
            builder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK);
        } else if (biometricType.equalsIgnoreCase("weak_passcode")) {
            builder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
        } else {
            builder.setNegativeButtonText(mActivity.getResources().getString(R.string.bionegative));
            builder.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        }

        promptInfo = builder.build();
        return promptInfo;
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      @NonNull CharSequence errString) {
        if (errMsgId == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
            // user clicked negative/cancel button
            ApzBiometricActivty.callFailureCB("APZ-CNT-231","Fingerprint operation canceled.");
        } else {
            ApzBiometricActivty.callFailureCB("APZ-CNT-232","Too many attempts. Try again later.");
        }

    }

    @Override
    public void onAuthenticationFailed() {
    }

    @Override
    public void onAuthenticationSucceeded(
            @NonNull BiometricPrompt.AuthenticationResult result) {
        fingerprintActivity.finish();
        ApzDataSecurity.secureData();
    }

}
