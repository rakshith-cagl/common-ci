package com.iexceed.plugins.autocapturedocument;

import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class ApzAutoCapturePlugin extends ApzPlugin {

	public ApzAutoCapturePlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(JSONObject params) {
		// TODO Auto-generated method stub
		
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		// TODO Auto-generated method stub
		return null;
	}

	  public static boolean isAutoCapture() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
