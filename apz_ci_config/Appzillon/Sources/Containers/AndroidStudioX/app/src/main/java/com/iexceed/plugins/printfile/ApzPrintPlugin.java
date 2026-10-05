package com.iexceed.plugins.printfile;

import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class ApzPrintPlugin extends ApzPlugin {


	public ApzPrintPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		return null;
	}

	public static boolean isPlugin() {
		return false;
	}

	@Override
	public void execute(JSONObject params) {

	}

}