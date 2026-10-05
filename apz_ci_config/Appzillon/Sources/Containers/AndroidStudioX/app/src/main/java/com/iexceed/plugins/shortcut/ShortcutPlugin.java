package com.iexceed.plugins.shortcut;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.iexceed.common.ApzActivity;
import com.iexceed.appzillonapp.AppzillonMainScreen;

import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

public class ShortcutPlugin extends ApzPlugin {

	private static ApzPlugin pluginObj;

	String TAG = "ShortcutPlugin";

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new ShortcutPlugin(webView, activity);
		}
		return pluginObj;
	}

	private ShortcutPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "on create onReceive: shorctcut");
			String shortcut_code = "";
			String action_code = "";

			if(intent.hasExtra("action")){
				action_code = intent.getStringExtra("action");

			}
			shortcut_code = "com.iexceed.shortcut";
			AppzillonMainScreen.sendShortcut(shortcut_code, action_code,callbackId);
		}
	};

	IntentFilter intentFilter = new IntentFilter("com.iexceed.shortcut");

	public void startListener() {
		//Natasha's Changes 30/06/2017 Security Changes
		//activity.registerReceiver(broadcastReceiver, intentFilter);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
			LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiver, intentFilter);
			JSONObject res = new JSONObject();
			try {
				res.put("event", "started");
			} catch (JSONException ex) {

			}


			//Sending broadcast when the app is not active
			if (AppzillonMainScreen.SHORTCUT_MSG != null) {
				Log.i(TAG, "startListener: oncreate" + " " + AppzillonMainScreen.SHORTCUT_MSG);
				Intent brIntent = new Intent();
				brIntent.putExtra("action", AppzillonMainScreen.SHORTCUT_MSG);
				//	brIntent.putExtra("ac",AppzillonMainScreen.SHORTCUT_MSG);
				brIntent.setAction("com.iexceed.shortcut");

				//Natasha's Changes 30/06/2017 Security Changes
				//AppzillonMainScreen.activity.sendBroadcast(brIntent);
				LocalBroadcastManager.getInstance(activity).sendBroadcast(brIntent);
				AppzillonMainScreen.SHORTCUT_MSG = null;

			} else {
				ApzPluginUtil.sendSuccess(callbackId, res, true, activity, webView, true);
			}
		}else{
			JSONObject resError = new JSONObject();
			try {
				resError.put("errorMessage", "Plugin is not supported for this device");
			} catch (JSONException ex) {

			}
			ApzPluginUtil.sendError(callbackId,"APZ-CNT-022",resError,activity,webView,true);
		}
	}

	public void stopListener() {
		//Natasha's Changes 30/06/2017 Security Changes
		//activity.unregisterReceiver(broadcastReceiver);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver);
		broadcastReceiver = null;
		JSONObject res = new JSONObject();
		try {
			res.put("event", "stopped");
		}catch (JSONException ex){

		}
		ApzPluginUtil.sendSuccess(callbackId,res,false,activity,webView,true);
		}else{
			JSONObject resError = new JSONObject();
			try {
				resError.put("errorMessage", "Plugin is not supported for this device");
			} catch (JSONException ex) {

			}
			ApzPluginUtil.sendError(callbackId,"APZ-CNT-022",resError,activity,webView,true);
		}
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
