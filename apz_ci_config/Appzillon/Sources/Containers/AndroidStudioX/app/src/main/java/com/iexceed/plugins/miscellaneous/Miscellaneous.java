package com.iexceed.plugins.miscellaneous;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.UserSettings;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class Miscellaneous extends ApzPlugin {
	
	private SharedPreferences settings;
	final static String properties = "USER_PREFS";
	public static String eventId;
	private static ApzPlugin pluginObj;
	
	private String TAG = "Miscellaneous";
	
	private Miscellaneous(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new Miscellaneous(webView, activity);
		}
		return pluginObj;
	}

	@Override
	public void execute(JSONObject params) {
		eventControl(params);
		
	}
	
	public void eventControl(JSONObject params){
		try{
			eventId = params.getString("id");
			Iterator<String> eventsIterator = params.keys();
			settings = activity.getApplicationContext().getSharedPreferences(properties, 0);
			while (eventsIterator.hasNext()) {
				String key = (String) eventsIterator.next().trim();
				String val = params.getString(key).trim();
				UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,key, val, settings);
			}
			UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,"eventsInitialized", "true", settings);
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, this.activity, this.webView, true);
		}
	}
}
