package com.iexceed.plugins.fingerprintscan;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

@TargetApi(23)
public class DeviceFingerprintAccess extends ApzPlugin {

	public DeviceFingerprintAccess(WebView webView, ApzActivity activity) {
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

	public static boolean isPlugin() {
		// TODO Auto-generated method stub
		return false;
	}}
