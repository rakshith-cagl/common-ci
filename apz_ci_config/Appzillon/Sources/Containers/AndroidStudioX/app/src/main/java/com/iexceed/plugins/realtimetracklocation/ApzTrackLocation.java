package com.iexceed.plugins.realtimetracklocation;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;


import org.json.JSONObject;

/**
 * Created by mishra.abhishek on 14/11/17.
 */

public class ApzTrackLocation extends ApzPlugin {    

    public ApzTrackLocation(WebView webView, ApzActivity activity){
        super(webView,activity);
    }

    @Override
    public void execute(JSONObject params) {}
    

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        return null;
    }

    public static boolean isPlugin() {
        return false;
    }
}
