package com.iexceed.plugins.openbrowser;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

public class OpenBrowser {
	protected static void openBrowser(String callbackId,WebView webView,Activity activity, JSONObject jsonObj){
		String URL = null;
		try{
			URL = jsonObj.getString("url");
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
		activity.startActivity(intent);
		
		ApzPluginUtil.sendSuccess(callbackId, null, false, activity, webView, true);
		} catch (Exception ex) {
//			ApzLogger.e("OpenBrowser",ex.toString());
			JSONObject json =  new JSONObject();
			try {
				json.put("errorDescription", ex.toString());
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", json, activity, webView, true);
			} catch (JSONException e) {
				ApzLogger.e("OpenBrowser",e.toString());
			}
			
		}
	}

}
