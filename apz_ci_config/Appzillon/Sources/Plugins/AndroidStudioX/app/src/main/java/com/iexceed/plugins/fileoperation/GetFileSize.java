package com.iexceed.plugins.fileoperation;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.webkit.WebView;

import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class GetFileSize {

	private static final String TAG = "GetFileSize";

	static void getFileSize(WebView webView , Activity activity , JSONObject jsonObj){
		String callbackId = null;
		try {
			String path = jsonObj.getString("filePath");
			callbackId = jsonObj.getString("id");
			File filenew = new File(path);
			long len = filenew.length();
			final int file_size = Integer.parseInt(String.valueOf(len/1024));
			try {
				JSONObject json = new JSONObject();
				json.put("fileSize", file_size);
				ApzPluginUtil.sendSuccess(callbackId, json, false, activity, webView, true);
			} catch (Exception e) {
				ApzLogger.e(TAG,e.toString());
			}
		} catch (JSONException e) {
			JSONObject json = new JSONObject();
			try {
				json.put("errorCode", "");
			} catch (JSONException e1) {
				ApzLogger.d(TAG,e1.toString());
			}
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-002", json, activity,
					webView, true);
		}
	}

}
