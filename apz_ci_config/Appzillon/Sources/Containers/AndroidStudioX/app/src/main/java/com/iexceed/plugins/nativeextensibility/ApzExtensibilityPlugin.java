package com.iexceed.plugins.nativeextensibility;

import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzExtensibilityPlugin extends ApzPlugin{

	
	private static ApzPlugin pluginObj;
	private ApzExtensibilityPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){ 
		pluginObj = new ApzExtensibilityPlugin(webView, activity); 
	}  
	return pluginObj; 
	}


	@Override
	public void execute(JSONObject params) {
		// TODO Auto-generated method stub
		try {
			this.callbackId = params.getString("id");
			JSONObject jsonResult = NativeService.nativeServiceEntry(webView, activity, params);
			if(jsonResult != null){
			   ApzPluginUtil.sendSuccess(callbackId, jsonResult, false, activity, webView, true);
			}

		} catch (JSONException e) {
			ApzLogger.e("ApzExtensibilityPlugin",e.toString());
		}
		
	}

}
