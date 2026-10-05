package com.iexceed.plugins.whitelist;

import android.app.Activity;
import android.content.Context;
import android.webkit.WebView;

public class EnableWhiteList {
	public static boolean isDummyPlugin = true;

	private static WebView mWebView;

	private static Context mContext;

	private static Activity mActivity;	

	public EnableWhiteList(Context context, Activity activity, WebView webView) {
		mWebView = webView;
		mContext = context;
		mActivity = activity;
	}

	public void loadWhitelist() {

	}
	
	
}
