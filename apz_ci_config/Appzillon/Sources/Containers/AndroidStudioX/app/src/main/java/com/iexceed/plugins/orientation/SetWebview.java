package com.iexceed.plugins.orientation;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.content.pm.ActivityInfo;
import android.webkit.WebView;

public class SetWebview extends ApzPlugin {
	
	private static ApzPlugin pluginObj;
	
	private String TAG = "SetWebView";
	
	private SetWebview(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity){
		if(pluginObj == null)
	{
		pluginObj = new SetWebview(webView, activity);
		}
	return pluginObj;
	}

	public void webviewMode(String mode) {
		if (mode.equalsIgnoreCase("LANDSCAPE")) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else if (mode.equalsIgnoreCase("PORTRAIT")) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		} else {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}

	@Override
	public void execute(JSONObject params) {
		String mode = "";
		try {
			mode = params.getString("orientation");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}
		webviewMode(mode);
	}
}

