package com.iexceed.plugins.savebase64topdf;

import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class Base64toPDFPlugin extends ApzPlugin {
	public static ApzPlugin pluginObj;

	private Base64toPDFPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		{
			if (pluginObj == null) {
				pluginObj = new Base64toPDFPlugin(webView, activity);
			}
			return pluginObj;
		}
	}

	@Override
	public void execute(JSONObject params) {
		SaveBase64toPDF.convertBase64toPDF(webView, activity, params);
	}

}
