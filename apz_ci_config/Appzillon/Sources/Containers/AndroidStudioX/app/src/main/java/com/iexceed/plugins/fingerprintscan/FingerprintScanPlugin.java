package com.iexceed.plugins.fingerprintscan;

import org.json.JSONObject;

import android.content.Intent;
import android.util.Log;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.ExternalActivityResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.camera.ApzCameraPlugin;

public class FingerprintScanPlugin extends ApzPlugin {

	public FingerprintScanPlugin(WebView webView, ApzActivity activity) {
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
}
