package com.iexceed.plugins.dataSecurity;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Objects;

/**
 * Created by @appzillon.
 */

public class ApzBiometricActivty extends AppCompatActivity {

    private static ApzActivity activity;
    private static WebView webView;
    private static String callbackId;
    private static AppCompatActivity ApzBiometricAct;
    private static JSONObject mJsonReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApzBiometricAct = this;
        if (getSupportActionBar() != null) {
            Objects.requireNonNull(getSupportActionBar()).hide();
        }

        String biometricType = "strong";
        if (null != mJsonReq) {
            if (mJsonReq.has("params")) {
                try {
                    biometricType = mJsonReq.getJSONObject("params").optString("biometricType");
                } catch (JSONException ignored) {
                }
            } else {
                biometricType = mJsonReq.optString("biometricType");
            }
        }

        this.setFinishOnTouchOutside(false);
        BiometricHandler helper = new BiometricHandler(webView,
                activity, callbackId, this);
        helper.startAuth(BiometricManager.from(this), BiometricAuth.cryptoObject, biometricType);

    }

    public static void init(ApzActivity mActivity, WebView mWebView, String mCallbackId, JSONObject jsonObject) {
        activity = mActivity;
        callbackId = mCallbackId;
        webView = mWebView;
        mJsonReq = jsonObject;

    }

    public void cancelFingerprint() {
        BiometricHandler.cancellationSignal.cancel();
        BiometricHandler.cancellationSignal = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelFingerprint();
    }

    public static void callFailureCB(String errorCode, String errorMessage) {
        JSONObject resFull = new JSONObject();
        JSONObject params = new JSONObject();
        try {

            JSONObject paramsJson = mJsonReq.getJSONObject("params");
            JSONObject reqFullJson = paramsJson.getJSONObject("reqFull");
            JSONObject appzillonHeader = reqFullJson.getJSONObject("appzillonHeader");
            appzillonHeader.put("status", false);
            reqFullJson.put("appzillonHeader", appzillonHeader);
            JSONArray jsonArray = new JSONArray();
            JSONObject appzillonErrors = new JSONObject();
            appzillonErrors.put("errorMessage", errorMessage);
            appzillonErrors.put("errorCode", errorCode);
            jsonArray.put(appzillonErrors);

            reqFullJson.put("appzillonErrors", jsonArray);
            resFull.put("resFull", reqFullJson);
            resFull.put("status", false);
            params.put("params", resFull);
            params.put("reqId", mJsonReq.getString("reqId"));
        } catch (Exception ignored) {}
        ApzPluginUtil.sendError(callbackId, errorCode, params, activity, webView, true);
        ApzBiometricAct.finish();
    }
}
