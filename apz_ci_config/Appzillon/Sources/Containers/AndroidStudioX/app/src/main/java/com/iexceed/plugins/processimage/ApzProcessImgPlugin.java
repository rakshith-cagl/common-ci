package com.iexceed.plugins.processimage;


import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class ApzProcessImgPlugin extends ApzPlugin {
    
    public ApzProcessImgPlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
       // TODO Auto-generated method stub
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
       // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void execute(JSONObject params) {
       // TODO Auto-generated method stub
    }
    public static boolean IsImgProcessing() {
        // TODO Auto-generated method stub
        return false;
    }
}
