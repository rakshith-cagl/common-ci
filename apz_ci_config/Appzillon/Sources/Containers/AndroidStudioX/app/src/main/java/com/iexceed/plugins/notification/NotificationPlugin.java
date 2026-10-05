package com.iexceed.plugins.notification;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

import org.json.JSONObject;

public class NotificationPlugin extends ApzPlugin {


	public NotificationPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	@Override
	public void execute(JSONObject params) {

	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		return null;
	}

	public static boolean isPlugin(){
		return false;
	}
}
