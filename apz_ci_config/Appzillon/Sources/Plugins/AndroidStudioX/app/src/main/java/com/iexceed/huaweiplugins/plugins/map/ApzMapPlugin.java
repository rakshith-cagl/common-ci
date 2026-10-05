package com.iexceed.plugins.map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzMapPlugin extends ApzPlugin{
	
	protected String TAG = "ApzMapPlugin";
	private static ApzPlugin pluginObj;

	private ApzMapPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{
		if(pluginObj == null){
		pluginObj = new ApzMapPlugin(webView, activity);
		}
		return pluginObj;
	}

	@Override
	public void execute(JSONObject params) {
		String action = null;;
		try{
			action = params.getString("action");
		}catch(JSONException ex){
			ApzLogger.e(TAG,ex.toString());
		}
		if(action.equals("DRIVINGDIRECTIONMAP")){
			Intent intent = new Intent(activity.getApplicationContext(), DrivingDirection.class);
			DrivingDirection.setWebView(webView);
			intent.putExtra("mapval", params.toString());
			activity.startActivity(intent);
		}else if(action.equals("LOADMAP")){
			Intent intent = new Intent(activity.getApplicationContext(), Map.class);
			intent.putExtra("mapval", params.toString());
			activity.startActivity(intent);
		}else if(action.equals("LOCATIONSELECTORMAP")){
			Intent intent = new Intent(activity.getApplicationContext(), SelectionMap.class);
			SelectionMap.setWebView(webView);
			intent.putExtra("mapval", params.toString());
			activity.startActivity(intent);
		}
		
	}
	
	public static boolean IsMap(){
		return true;
	}

}
