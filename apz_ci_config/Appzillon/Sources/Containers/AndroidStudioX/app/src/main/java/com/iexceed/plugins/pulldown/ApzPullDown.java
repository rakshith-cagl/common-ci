package com.iexceed.plugins.pulldown;

import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzPullDown extends ApzPlugin{

	private static ApzPlugin pluginObj;
	private ApzPullDown(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  
	if(pluginObj == null){ 
		pluginObj = new ApzPullDown(webView, activity); 
	}  
	return pluginObj; 
	}

	@Override
	public void execute(final JSONObject params) {
		String action;
		try {
			this.callbackId = (String) params.get("id");
			action = (String) params.get("action");
			if(action.equalsIgnoreCase("Enable")){
				try {
					PullDown.enablePullDown(this.callbackId ,activity, webView,params.toString());
				} catch (Exception e) {
					ApzLogger.e("ApzPullDown",e.toString());
				}
			}else if(action.equalsIgnoreCase("Disable")){
				try {
					PullDown.disablePullDown(this.callbackId ,activity, webView,params.toString());
				} catch (Exception e) {
					ApzLogger.e("ApzPullDown",e.toString());
				}
			}else if(action.equalsIgnoreCase("HideRefresh")){
				PullDown.hideRefreshIcon(activity);
			}
		} catch (JSONException e) {
			ApzLogger.e("ApzPullDown",e.toString());
		}
		
	}

}
