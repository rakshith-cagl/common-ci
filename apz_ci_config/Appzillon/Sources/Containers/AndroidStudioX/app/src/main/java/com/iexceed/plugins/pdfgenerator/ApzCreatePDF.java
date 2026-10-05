package com.iexceed.plugins.pdfgenerator;


import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

import org.json.JSONObject;

public class ApzCreatePDF extends ApzPlugin{

    private static ApzPlugin pluginObj;

    private ApzCreatePDF(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    @Override
    public void execute(JSONObject params) {

    }


    public static boolean isPlugin() {
        return false;
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity){
        return null;
    }


}

