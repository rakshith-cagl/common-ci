package com.iexceed.plugins.calllogs;

import android.webkit.WebView;
import org.json.JSONObject;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class CallLogs extends ApzPlugin {

    private CallLogs(WebView webView, ApzActivity activity){
        super(webView, activity);
		// TODO Auto-generated constructor stub

    }
    @Override
    public void execute(JSONObject params) {
		
		// TODO Auto-generated constructor stub
    }
    public static boolean IsCallLog() {
		return true;
	    }
    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		// TODO Auto-generated method stub
		return null;
	}

}

