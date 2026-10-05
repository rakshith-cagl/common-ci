package com.iexceed.plugins.security;

import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class PlayProtectCheck {
    ApzActivity mActivity;
    JSONObject mJsonObject;
    WebView mWebView;
    String mCallbackId;

    public PlayProtectCheck(ApzActivity activity, WebView webView, JSONObject jsonObject) {
        mActivity = activity;
        mWebView = webView;
        mJsonObject = jsonObject;
        try {
            mCallbackId = (String) jsonObject.get("id");
        } catch (JSONException e) {

        }
    }
    /*RBI SECURITY CHANGES*/
    public boolean checkIsPlayProtectEnabled() {
        final boolean[] isEnabled = {false};
        final JSONObject obj = new JSONObject();
        try {

            SafetyNet.getClient(mActivity.getApplicationContext())
                    .isVerifyAppsEnabled()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            return;
                        }
                    })
                    .addOnCanceledListener(new OnCanceledListener() {
                        @Override
                        public void onCanceled() {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<SafetyNetApi.VerifyAppsUserResponse>() {
                @Override
                public void onSuccess(@NonNull SafetyNetApi.VerifyAppsUserResponse verifyAppsUserResponse) {
                    if (verifyAppsUserResponse.isVerifyAppsEnabled()) {
                        isEnabled[0] = true;
/*
                    Log.d("MY_APP_TAG", "The user gave consent " +
                            "to enable the Verify Apps feature.");*/
                    } else {
                        isEnabled[0] = false;/*
                    Log.d("MY_APP_TAG", "The user didn't give consent " +
                            "to enable the Verify Apps feature.");*/
                    }
                    try {
                        obj.put("isPlayProtectEnabled", isEnabled[0]);
                    } catch (JSONException e) {
                    }
                    sendSuccess(obj);

                }
            });
        }catch(Exception e){

        }
        return isEnabled[0];
    }
    private void sendFailure(String msg){
        JSONObject resultObj = new JSONObject();
        try {
            resultObj.put("error", msg);
        } catch (JSONException ex) {
        }
        ApzPluginUtil.sendError(mCallbackId, "", resultObj, mActivity, mWebView, true);
    }
    private void sendSuccess(JSONObject networkJson){
        ApzPluginUtil.sendSuccess(mCallbackId,
                networkJson, true, mActivity,
                mWebView, true);
    }
}
