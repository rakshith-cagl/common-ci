package com.iexceed.plugins.deeplinking;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

public class ApzDeeplinkingPlugin extends ApzPlugin {

	private static ApzPlugin pluginObj;

	private ApzDeeplinkingPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity){
		if(pluginObj == null){
			pluginObj = new ApzDeeplinkingPlugin(webView, activity);
		}
		return pluginObj;
	}

	@Override
	public void execute(JSONObject params) {
		try {
			callbackId = params.getString("id");
			String packageName = params.getString("packageName");
			startNewActivity(packageName);
			ApzPluginUtil.sendSuccess(callbackId, null, false, activity, webView, true);

		} catch (JSONException e) {
			
		}
	}

	public void startNewActivity(String packageName) {
		try {
			Intent intent = activity.getPackageManager()
					.getLaunchIntentForPackage(packageName);
			if (intent == null) {
				// Bring user to the market or let them choose an app?
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=" + packageName));
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
		} catch (final Exception e) {
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity, webView,
					true);
		}
	}
}
