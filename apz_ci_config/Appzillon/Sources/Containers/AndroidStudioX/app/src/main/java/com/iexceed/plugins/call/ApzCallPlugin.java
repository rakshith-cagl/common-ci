package com.iexceed.plugins.call;


import org.json.JSONObject;

import android.webkit.WebView;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class ApzCallPlugin extends ApzPlugin {

    private ApzCallPlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        // TODO Auto-generated method stub
		return null;
    }

    @Override
    public void execute(JSONObject params) {
       // TODO Auto-generated method stub
    }

    
    public static boolean IsCall() {
		// TODO Auto-generated method stub
		return false;
    }
}
