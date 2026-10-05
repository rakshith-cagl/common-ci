package com.iexceed.plugins.accelerometer;

import org.json.JSONObject;

import android.content.Context;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;


public class AccelerometerPlugin extends ApzPlugin{

	public AccelerometerPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(JSONObject params) {
		// TODO Auto-generated method stub
		
	}

	public static boolean isAccelerometer() {
		// TODO Auto-generated method stub
		return false;
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity,
			Context context) {
		// TODO Auto-generated method stub
		return null;
	}
	public void pause() {}
	
	
}



