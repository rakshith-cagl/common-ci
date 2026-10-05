package com.iexceed.plugins.email;

import org.json.JSONObject;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

public class EmailPlugin extends ApzPlugin {

	public EmailPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(JSONObject params) {
		// TODO Auto-generated method stub
		
	}

	public static boolean IsEmail() {
		// TODO Auto-generated method stub
		return false;
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		// TODO Auto-generated method stub
		return null;
	}}
