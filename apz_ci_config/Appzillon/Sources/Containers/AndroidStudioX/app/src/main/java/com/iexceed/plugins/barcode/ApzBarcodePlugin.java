package com.iexceed.plugins.barcode;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;

import org.json.JSONObject;



public class ApzBarcodePlugin extends ApzPlugin{

	public ApzBarcodePlugin(WebView webView, ApzActivity activity) {
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

	public static boolean isBarcodeActivity() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void checkForBarcodeOnPause(){

	}

	public static void checkForBarcodeOnResume(){

	}
}

