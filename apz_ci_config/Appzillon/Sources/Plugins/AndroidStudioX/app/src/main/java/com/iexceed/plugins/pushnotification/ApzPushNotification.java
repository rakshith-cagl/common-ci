package com.iexceed.plugins.pushnotification;

import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzPushNotification extends ApzPlugin {
	private static ApzPlugin pluginObj;
	private ApzPushNotification(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){ 
		pluginObj = new ApzPushNotification(webView, activity); 
	}  
	return pluginObj; 
	}

	@Override
	public void execute(JSONObject params) {
		try{
			//this.callbackId = params.getString("id");
			String action = params.getString("action");
			if(action.equalsIgnoreCase("Show")){
				PushNotification mPushNotification = new PushNotification(ApzPushNotification.this.activity.getApplicationContext(), ApzPushNotification.this.activity, ApzPushNotification.this.webView);
				mPushNotification.getAllPushNotes(params);
				mPushNotification = null;
			}else if(action.equalsIgnoreCase("Delete")){
				PushNotification mPushNotification = new PushNotification(ApzPushNotification.this.activity.getApplicationContext(), ApzPushNotification.this.activity, ApzPushNotification.this.webView);
				mPushNotification.deletePushNotes(params);
				mPushNotification = null;
			}
		}catch(Exception e){
			ApzLogger.e("ApzPushNotification",e.toString());
		}
		
	}
	
	public static boolean isPushNotification() {
		return true;
	}

}
