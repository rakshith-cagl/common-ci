package com.iexceed.plugins.wipeout;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.webkit.WebView;

public class WipeOut extends ApzPlugin {


	private static ApzPlugin pluginObj;
	
	private static String TAG = "WipeOut";

	private static SharedPreferences settings;

	final static String properties = "USER_PREFS";

	private static SharedPreferences apps;

	final static String app_props = "APP_PREFS";
	
	public static Activity stat_activity = null;

	public WipeOut(WebView webView, ApzActivity activity) {
		super(webView, activity);
		stat_activity = activity;
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new WipeOut(webView, activity);
		}
		return pluginObj;
	}

	public void unInstallApp(String jsonobj) {
		try {
			//JSONObject json = new JSONObject(jsonobj);
			Intent uninstall = new Intent(Intent.ACTION_DELETE);
			uninstall.setData(Uri.parse("package:" + activity.getApplicationContext().getPackageName()));
			uninstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(uninstall);

			final String successMsg = "Wipeout Success";
			JSONObject json = new JSONObject();
			json.put("text", successMsg);
			
			ApzPluginUtil.sendSuccess(this.callbackId, json, false, this.activity, this.webView, true);
		} catch (Exception e) {
			ApzLogger.i(TAG,e.toString());
			try {
				final String errorMsg = e.getMessage();
				JSONObject json = new JSONObject();
				json.put("text", errorMsg);
				
				ApzPluginUtil.sendError(callbackId, "", json, this.activity, this.webView, true);
			} catch (JSONException e1) {
				ApzLogger.i(TAG,e.toString());
			}
		}

	}

	// To wipe out sub app means to delete it from sandbox
	public void wipeOutSubApp(String appName) {
		String appLoc = AppzillonMainScreen.SANDBOX_LOC + File.separator
				+ AppzillonMainScreen.ASSETS_MAIN_FOLDER + File.separator
				+ appName;
		String htmlFileLoc = AppzillonMainScreen.SANDBOX_LOC + File.separator + appName + ".html";
		File htmlFile = new File(htmlFileLoc);
		if(htmlFile.exists()){
			try {
				new java.io.FileWriter(htmlFile.getAbsolutePath(), false).close();
			} catch (IOException e) {

			}
			htmlFile.delete();
		}
		File subAppFolder = new File(appLoc);
		if (subAppFolder != null) {
			// subAppFolder.delete();
			deleteRecursive(subAppFolder);
		}
		ApzPluginUtil.sendSuccess(callbackId, null, false, activity, webView, true);
	}

	public static void wipeOutMainApp() {
		ApzLogger.i(TAG, "wipeOutMainApp");

		settings = stat_activity.getSharedPreferences(properties, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.clear();
		editor.commit();

		apps = stat_activity.getSharedPreferences(app_props, 0);
		SharedPreferences.Editor app_editor = apps.edit();
		app_editor.clear();
		app_editor.commit();

		Intent uninstall = new Intent(Intent.ACTION_DELETE);
		uninstall.setData(Uri.parse("package:" + stat_activity.getPackageName()));
		stat_activity.startActivity(uninstall);

	}

	private void deleteRecursive(File subAppFolder) {

//		ApzLogger.d("Wipeout",
//				"DELETEPREVIOUS TOP" + subAppFolder.getPath());
		if (subAppFolder.isDirectory()) {
			String[] children = subAppFolder.list();
			for (int i = 0; i < children.length; i++) {
				File temp = new File(subAppFolder, children[i]);
				if (temp.isDirectory()) {
//					ApzLogger.d(TAG, "Recursive Call" + temp.getPath());
					deleteRecursive(temp);
				} else {
//					ApzLogger.i(TAG, "Delete File" + temp.getPath());
					try {
						new java.io.FileWriter(temp.getAbsolutePath(), false).close();
					} catch (IOException e) {

					}
					boolean b = temp.delete();
					if (b == false) {
						ApzLogger.e(TAG, "DELETE FAIL : ");
					}
				}
			}

		}
		try {
			new java.io.FileWriter(subAppFolder.getAbsolutePath(), false).close();
		} catch (IOException e) {

		}
		subAppFolder.delete();

	}

	@Override
	public void execute(JSONObject params) {
		try {
			
			this.callbackId = params.getString("id");
			String action = params.getString("action");

			if (action.equalsIgnoreCase("unInstallApp")) {
				unInstallApp(params.toString());
			}else if (action.equalsIgnoreCase("subAppDelete")){
				wipeOutSubApp(params.getString("appId"));
			}

		} catch (Exception e) {
			ApzLogger.i(TAG,e.toString());
		}

	}

}
