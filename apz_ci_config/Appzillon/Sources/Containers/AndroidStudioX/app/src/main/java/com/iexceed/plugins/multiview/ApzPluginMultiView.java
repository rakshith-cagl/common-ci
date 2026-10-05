package com.iexceed.plugins.multiview;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzPluginMultiView extends ApzPlugin {

	private ApzPluginMultiView(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	private static ApzPlugin pluginObj;

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){ 
		pluginObj = new ApzPluginMultiView(webView, activity); 
	}  
	return pluginObj; 
	}

	@Override
	public void execute(final JSONObject params) {
		String action;
		try {
			final String callbackId = (String) params.get("id");
			action = (String) params.get("action");
			if(action.equalsIgnoreCase("Open")){
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ApzPlugin.jsBridge.enableMultiView(params , callbackId );
					}
				});
			}else if(action.equalsIgnoreCase("Close")){
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ApzPlugin.jsBridge.killMultiView(params , callbackId );
					}
				});
			}else if(action.equalsIgnoreCase("Resize")){
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ApzPlugin.jsBridge.resetSize(params , callbackId );
					}
				});
			}
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}
		
	}
	
	public static void resizeMultiviewOnOrientationChange(Activity activity) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ApzPlugin.jsBridge.resizeMain();
			}
		});
	}

}
