package com.scanlibrary;

import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class ApzDocScannerPlugin extends ApzPlugin {

	private static ApzPlugin pluginObj;

	private ApzDocScannerPlugin(WebView wv, ApzActivity act) {
		super(wv, act);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new ApzDocScannerPlugin(webView, activity);
		}
		return pluginObj;
	}

	@Override
	public void execute(JSONObject params) {}
	
	public static boolean isDocScannerPlugin() {
		return false;
	}
}

