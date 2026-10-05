package com.iexceed.plugins.map;

import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class ApzMapPlugin extends ApzPlugin{

	public ApzMapPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(JSONObject params) {
		// TODO Auto-generated method stub
		
	}

	public static boolean IsMap() {
		// TODO Auto-generated method stub
		return false;
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		// TODO Auto-generated method stub
		return null;
	}}
