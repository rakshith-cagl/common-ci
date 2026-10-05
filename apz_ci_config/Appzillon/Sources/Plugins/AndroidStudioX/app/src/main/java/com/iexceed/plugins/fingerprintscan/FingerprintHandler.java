package com.iexceed.plugins.fingerprintscan;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import androidx.core.app.ActivityCompat;
import android.webkit.WebView;

import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends
        FingerprintManager.AuthenticationCallback {

    private CancellationSignal cancellationSignal;
    private Context appContext;
    private WebView mWebView;
    private Activity mActivity;
    private static String mCallerId;
    private String TAG = "FingerprintHandler";

    public FingerprintHandler(WebView webView , Activity activity , String callerId) {
        appContext = activity.getApplicationContext();
        mWebView = webView;
        mActivity = activity;
        mCallerId = callerId;
    }

    public void startAuth(FingerprintManager manager,
                          FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();

        if (ActivityCompat.checkSelfPermission(appContext,
                Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        JSONObject json = new JSONObject();
        try {
			json.put("event", "started");
		} catch (JSONException e) {
		}
        ApzPluginUtil.sendSuccess(mCallerId, json, true,
				mActivity, mWebView, true);
    }

    @Override
    public void onAuthenticationError(int errMsgId,
                                      CharSequence errString) {
    	onErrorMessage("Authentication error\n" + errString);
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId,
                                     CharSequence helpString) {
    	onErrorMessage("Authentication help\n" + helpString);
    }

    @Override
    public void onAuthenticationFailed() {
    	onErrorMessage("Authentication failed");
    }

    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {
        JSONObject json = new JSONObject();
		try {
			json.put("text", "Authentication succeeded");
			ApzLogger.d(TAG, "Authentication Succeeded");
		} catch (JSONException e) {
		}
        ApzPluginUtil.sendSuccess(mCallerId, json, false,
				mActivity, mWebView, true);
    }
    
    private void onErrorMessage(String errorMsg){
    	ApzLogger.d(TAG, "Error in the fingerprint"+errorMsg);
        JSONObject json = new JSONObject();
		try {
			json.put("text", errorMsg);
		} catch (JSONException e) {
			ApzLogger.e(TAG, e.toString());
		}
    	ApzPluginUtil.sendError(mCallerId, "APZ-CNT-305", json, mActivity,
				mWebView, true);
    }
}