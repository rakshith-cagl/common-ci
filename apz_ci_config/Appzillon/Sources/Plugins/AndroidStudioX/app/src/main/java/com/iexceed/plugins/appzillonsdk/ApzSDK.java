package com.iexceed.plugins.appzillonsdk;

import android.webkit.WebView;

import com.iexceed.app.ApzApp;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONObject;

public class ApzSDK extends ApzPlugin {
    private static ApzPlugin pluginObj;
    public ApzSDK(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzSDK(webView, activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {
        String callbackId = null;
        try {
            callbackId = params.getString("id");
            ApzApp.get().SendMessageToParent(params);
            ApzPluginUtil.sendSuccess(callbackId,
                    new JSONObject(), false, activity,
                    webView, true);
        } catch (Exception e) {
            ApzPluginUtil.sendError(callbackId, "", null, activity, webView, true);
        }
    }
	
	public static boolean isApzSDK() {
        // TODO Auto-generated method stub
        return true;
    }


}
