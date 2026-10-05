package com.iexceed.plugins.zip;

import java.io.IOException;

import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzZipPlugin extends ApzPlugin{
	
	private static ApzPlugin pluginObj;

	private ApzZipPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){ 
		pluginObj = new ApzZipPlugin(webView, activity); 
	}  
	return pluginObj; 
	}

	@Override
	public void execute(JSONObject params) {
		try{
			this.callbackId = params.getString("id");
			String action = params.getString("action");
			if(action.equalsIgnoreCase("zip")){
				ZipPlugin zipobj = new ZipPlugin(this.callbackId , this.activity, this.webView);
				try {
					zipobj.zip(params.toString());
				} catch (IOException e) {
					ApzLogger.i(TAG,e.toString());
				}
			}else if(action.equalsIgnoreCase("unzip")){
				ZipPlugin zipobj = new ZipPlugin(this.callbackId , this.activity, this.webView);
				try {
					zipobj.unzip(params.toString());
				} catch (IOException e) {
					ApzLogger.i(TAG,e.toString());
				}
			}
		}catch(Exception e){
			ApzLogger.i(TAG,e.toString());
		}
	}
	
	public static boolean isZipPlugin() {
		return true;
	}
}
