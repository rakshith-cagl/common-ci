package com.iexceed.plugins.openbrowser;

import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;


public class ApzOpenBrowserPlugin extends ApzPlugin{
	
	private static ApzPlugin pluginObj;

	private ApzOpenBrowserPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){ 
		pluginObj = new ApzOpenBrowserPlugin(webView, activity); 
	}  
	return pluginObj; 
	}

	@Override
	public void execute(JSONObject params) {
		try {
			this.callbackId = params.getString("id");
			OpenBrowser.openBrowser(ApzOpenBrowserPlugin.this.callbackId, ApzOpenBrowserPlugin.this.webView, ApzOpenBrowserPlugin.this.activity,params);
		} catch (JSONException e) {
			ApzLogger.e("ApzOpenBrowserPlugin",e.toString());
		}
	}

}
