package com.iexceed.plugins.security;

import android.webkit.WebView;

import com.google.android.gms.safetynet.HarmfulAppsData;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class DetectHarmfulApps {
    ApzActivity mActivity;
    JSONObject mJsonObject;
    WebView mWebView;
    String mCallbackId;

    public DetectHarmfulApps(ApzActivity activity, WebView webView, JSONObject jsonObject) {
        mActivity = activity;
        mWebView = webView;
        mJsonObject = jsonObject;
        try {
            mCallbackId = (String) jsonObject.get("id");
        } catch (JSONException e) {
        }
    }

    public void detectHarmfulApps() {
        final JSONObject obj = new JSONObject();
        SafetyNet.getClient(mActivity.getApplicationContext())
                .listHarmfulApps()
                .addOnCompleteListener(new OnCompleteListener<SafetyNetApi.HarmfulAppsResponse>() {
                    @Override
                    public void onComplete(Task<SafetyNetApi.HarmfulAppsResponse> task) {
                      //  Log.d("Harmfulapps", "Received listHarmfulApps() result");

                        if (task.isSuccessful()) {
                            SafetyNetApi.HarmfulAppsResponse result = task.getResult();
                            long scanTimeMs = result.getLastScanTimeMs();

                            List<HarmfulAppsData> appList = result.getHarmfulAppsList();
                            if (appList.isEmpty()) {
                                try {
                                    obj.put("isHarmfulAppDetected", false);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                sendSuccess(obj);
                             /*   Log.d("MY_APP_TAG", "There are no known " +
                                        "potentially harmful apps installed.");*/
                            } else {
                               /* Log.e("MY_APP_TAG",
                                        "Potentially harmful apps are installed!");*/
                                JSONArray arr = new JSONArray();

                                for (HarmfulAppsData harmfulApp : appList) {
                                    arr.put(harmfulApp.apkPackageName);
/*
                                    Log.e("MY_APP_TAG", "Information about a harmful app:");
                                    Log.e("MY_APP_TAG",
                                            "  APK: " + harmfulApp.apkPackageName);*/
                                }
                                try {
                                    obj.put("isHarmfulAppDetected", true);
                                    obj.put("harmfulAppsList", arr);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                sendSuccess(obj);
                            }
                        } else {
                            sendFailure("App protection is disabled");
                        }
                    }
                });
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
