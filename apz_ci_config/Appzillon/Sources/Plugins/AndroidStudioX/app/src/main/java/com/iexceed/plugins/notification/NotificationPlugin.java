package com.iexceed.plugins.notification;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.ApzFirebaseMessagingService;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationPlugin extends ApzPlugin {

	private static ApzPlugin pluginObj;
	
	String TAG = "NotificationPlugin";

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new NotificationPlugin(webView, activity);
		}
		return pluginObj;
	}

	private NotificationPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String msg = intent.getStringExtra("message");
			boolean stat = intent.getBooleanExtra("appstatus", true);
			String param = intent.getStringExtra("msgParameters");
			String notification_code = "";
			String action_code = "";
            int notif_id = intent.getIntExtra("notif_id",-1);
			if(intent.hasExtra("notification_code")){
				notification_code = intent.getStringExtra("notification_code");
				action_code = intent.getStringExtra("action_code");
			}
			String title = intent.getStringExtra("title");
			String image_url = intent.getStringExtra("image_url");
			if(ApzFirebaseMessagingService.notificationManager != null && notif_id != -1) {
                ApzFirebaseMessagingService.notificationManager.cancel(notif_id);
            }
			AppzillonMainScreen.pushmessage(msg, stat, param, callbackId,notification_code, action_code,title,image_url);
		}
	};

	IntentFilter intentFilter = new IntentFilter("com.iexceed.notification");

	public void startListener() {
		//Natasha's Changes 30/06/2017 Security Changes
		//activity.registerReceiver(broadcastReceiver, intentFilter);
		LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiver, intentFilter);
		JSONObject res = new JSONObject();
		try {
			res.put("event", "started");
		}catch (JSONException ex){

		}


		//Sending broadcast when the app is not active
		if( AppzillonMainScreen.PUSH_MESSAGE != null){
			Intent brIntent = new Intent();
			brIntent.setAction("com.iexceed.notification");
			if(!"".equals(AppzillonMainScreen.NOTIFICATION_CODE)) {
				brIntent.putExtra("notification_code", AppzillonMainScreen.NOTIFICATION_CODE);
				brIntent.putExtra("action_code", AppzillonMainScreen.ACTION_CODE);
			}
			brIntent.putExtra("appstatus", "");
			brIntent.putExtra("msgParameters", AppzillonMainScreen.NOTIF_PARAMS);
			brIntent.putExtra("message",AppzillonMainScreen.PUSH_MESSAGE);
			brIntent.putExtra("notif_id", AppzillonMainScreen.NOTIF_CODE);
			brIntent.putExtra("action_code",AppzillonMainScreen.ACTION_CODE);
			brIntent.putExtra("title", AppzillonMainScreen.NOTIF_TITLE);
			brIntent.putExtra("image_url", AppzillonMainScreen.NOTIF_IMG_URL);
			//Natasha's Changes 30/06/2017 Security Changes
			//AppzillonMainScreen.activity.sendBroadcast(brIntent);
			 LocalBroadcastManager.getInstance(activity).sendBroadcast(brIntent);
			AppzillonMainScreen.PUSH_MESSAGE = null;
			AppzillonMainScreen.ACTION_CODE = null;
			AppzillonMainScreen.NOTIFICATION_CODE = null;
			AppzillonMainScreen.NOTIF_CODE = -1;
			AppzillonMainScreen.NOTIF_TITLE = null;
			AppzillonMainScreen.NOTIF_IMG_URL = null;

		}
		else
			{
				ApzPluginUtil.sendSuccess(callbackId,res,true,activity,webView,true);
			}
	}

	public void stopListener() {
		//Natasha's Changes 30/06/2017 Security Changes
		//activity.unregisterReceiver(broadcastReceiver);
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver);
		broadcastReceiver = null;
		JSONObject res = new JSONObject();
		try {
			res.put("event", "stopped");
		}catch (JSONException ex){

		}
		ApzPluginUtil.sendSuccess(callbackId,res,false,activity,webView,true);
	}

	@Override
	public void execute(JSONObject params) {
		String action = "";
		try {
			action = params.getString("action");
			callbackId = params.getString("id");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}
		if (action.equals("STARTLISTENER")) {
				startListener();
		} else if (action.equals("STOPLISTENER")) {
			stopListener();
		}

	}

	public static boolean isPlugin(){
		return true;
	}

}
