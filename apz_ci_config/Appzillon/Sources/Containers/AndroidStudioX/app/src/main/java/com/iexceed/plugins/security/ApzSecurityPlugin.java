package com.iexceed.plugins.security;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

import org.json.JSONException;
import org.json.JSONObject;

public class ApzSecurityPlugin extends ApzPlugin {

    private static ApzPlugin pluginObj;
    private JSONObject mJson;
    private String action;
    public ApzSecurityPlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }
    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzSecurityPlugin(webView, activity);
        }
        return pluginObj;
    }


    @Override
    public void execute(JSONObject params) {

        try {
            this.callbackId = params.getString("id");
            this.mJson = params;
            action = params.getString("action");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if ("getWifiStatus".equalsIgnoreCase(action)) {

WifiSecurity wifi =new WifiSecurity(activity,webView,params);
wifi.requestGPSpermission();
        }else  if (action.equalsIgnoreCase("isPlayProtectEnabled")) {

            PlayProtectCheck playprotect =new PlayProtectCheck(activity,webView,params);
            playprotect.checkIsPlayProtectEnabled();
        }else  if (action.equalsIgnoreCase("detectHarmfulApps")) {

            DetectHarmfulApps harmfulApps =new DetectHarmfulApps(activity,webView,params);
            harmfulApps.detectHarmfulApps();
        }
    }
}
