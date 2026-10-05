package com.iexceed.plugins.appzillonsdk;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

import org.json.JSONObject;

public class ApzSDK extends ApzPlugin {


    public ApzSDK(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        return null;
    }

    @Override
    public void execute(JSONObject params) {

    }

    public static boolean isApzSDK() {
        // TODO Auto-generated method stub
        return false;
    }
}
