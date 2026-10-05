package com.iexceed.plugins.sms;

import org.json.JSONObject;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

import android.webkit.WebView;

public class Sms extends ApzPlugin {

	
	private Sms(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {		
		return null;
	}
	

	@Override
	public void execute(JSONObject params) {
		// TODO Auto-generated method stub
	}
	
	public static boolean IsSMS() {
		// TODO Auto-generated method stub
		return false;
	}

}

