package com.iexceed.plugins.orientationlistener;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.res.Configuration;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class DeviceOrientationListener extends ApzPlugin{

	private static ApzPlugin pluginObj;
	public DeviceOrientationListener(WebView webView, ApzActivity activity) {
		super(webView, activity);
		// TODO Auto-generated constructor stub
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new DeviceOrientationListener(webView, activity);
		}
		return pluginObj;
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String orientation = intent.getExtras().getString("orientation");
			JSONObject json = new JSONObject();
			try {
				json.put("event","orientation_change");
				json.put("orientation", orientation);
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
			}
			ApzPluginUtil.sendSuccess(DeviceOrientationListener.this.callbackId, json, true, DeviceOrientationListener.this.activity, DeviceOrientationListener.this.webView, true);
		}

	};


	IntentFilter intentFilter = new IntentFilter("com.iexceed.orientation");
	public void startListener(JSONObject params){
		//Natasha's changes 30/6/2017 Security Changes
		//activity.registerReceiver(broadcastReceiver, intentFilter);
		LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiver, intentFilter);
		String orientation = "";
		if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			orientation = "LANDSCAPE";
		}else if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
			orientation = "PORTRAIT";
		}
		JSONObject json = new JSONObject();
		try {
			json.put("event", "started");
			json.put("orientation", orientation);
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}
		ApzPluginUtil.sendSuccess(this.callbackId, json, true, this.activity, this.webView, true);
	}

	public void stopListener(JSONObject result){
		//Natasha's changes 30/6/2017 Security Changes
		//activity.unregisterReceiver(broadcastReceiver);
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver);
		JSONObject json = new JSONObject();
		try {
			json.put("event", "stopped");
		} catch (JSONException e) {
		}
		ApzPluginUtil.sendSuccess(this.callbackId, json, false, this.activity, this.webView, true);

	}

	@Override
	public void execute(JSONObject params) {
		String action = "";
		try {
			action = params.getString("action");
			callbackId = params.getString("id");
		} catch (JSONException e) {
			
		}

		if(action.equals("STARTLISTENER")){
			startListener(params);
		}else if(action.equals("STOPLISTENER")){
			stopListener(params);
		}

	}

}
