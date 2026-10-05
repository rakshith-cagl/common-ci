package com.iexceed.plugins.augmentedreality;

import org.json.JSONObject;

import android.location.Location;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class ApzARPlugin extends ApzPlugin{

	public ApzARPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(JSONObject params) {
		// TODO Auto-generated method stub
		
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		// TODO Auto-generated method stub
		return null;
	}

	public static boolean isPlugin() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void augRealityThresholdReached(double latitude,
			double longitude) {
		// TODO Auto-generated method stub
		
	}

	public static void setCurrentLocation(Location location) {
		// TODO Auto-generated method stub
		
	}}
