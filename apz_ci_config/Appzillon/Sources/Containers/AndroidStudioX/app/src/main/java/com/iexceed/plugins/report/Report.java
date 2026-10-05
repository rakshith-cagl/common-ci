package com.iexceed.plugins.report;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Base64;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class Report extends ApzPlugin {

	private static ApzPlugin pluginObj;
	private String base64;
	private String ext;

	private Report(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new Report(webView, activity);
		}
		return pluginObj;
	}

	public void savereport(JSONObject reportJson) {
		try {
			callbackId = reportJson.getString("id");
			base64 = reportJson.getString("base64");
			ext = reportJson.getString("extension");
		} catch (final JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null,
					this.activity, this.webView, true);
			return;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(
					this.activity.getExternalFilesDir(null) + File.separator
							+ "download." + ext);
			BufferedOutputStream buf = new BufferedOutputStream(fos);
			byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
			try {
				fos.write(decodedString);
			} catch (IOException e) {
				ApzLogger.e(TAG,e.toString());
				ApzPluginUtil.sendError(callbackId, "", null, this.activity,
						this.webView, true);// sdcard unavailable
			}
		} catch (FileNotFoundException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callbackId, "", null, this.activity,
					this.webView, true);// File not found
		}
		try {
			fos.close();
			JSONObject result = new JSONObject();
			result.put("successMessage", "Report Saved Successfully");
		} catch (IOException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callbackId, "", null, this.activity,
					this.webView, true);// SD Card Unavailable
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}
	}

	public static boolean isReport() {
		return true;
	}

	@Override
	public void execute(JSONObject params) {

		savereport(params);

	}

}
