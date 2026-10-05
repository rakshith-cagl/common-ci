package com.iexceed.plugins.multiapp;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.webkit.WebView;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.StringUtils;
import com.iexceed.common.UserSettings;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.auditlog.AuditLog;
import com.iexceed.plugins.errorlog.ApzLogger;
import com.iexceed.plugins.ota.OTAPlugin;
import com.iexceed.plugins.wipeout.WipeOut;

public class MultiappUtils extends ApzPlugin {

	private static ApzPlugin pluginObj;

	private String TAG = "MultiappUtils";

	private MultiappUtils(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new MultiappUtils(webView, activity);
		}
		return pluginObj;
	}

	public void loadPage(String pageName) {
		if (AppzillonUtils.isAppExpired(activity, pageName)) {
			AppzillonMainScreen.showExpiryMsg(activity.getApplicationContext(),
					activity, pageName);
		} else {
			if (checkIfFolderExists(pageName)) {
				String appLoc = AppzillonMainScreen.ASSETS_MAIN_FOLDER + "/"
						+ pageName;
				Intent intent = new Intent(activity, AppzillonMainScreen.class);
				intent.putExtra("app_name", pageName);
				intent.putExtra("app_loc", appLoc);
				intent.putExtra("app_remoteDebug", "Y");
				activity.startActivity(intent);
			} else {
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-226", null, activity, webView, true);
			}
		}

	}

	public boolean checkIfFolderExists(String assetName) {
		if ((AppzillonMainScreen.OTAREQUIRED).equalsIgnoreCase("Y")) {
			System.out.println("OTA_ENABLED");
			String appLoc = AppzillonMainScreen.SANDBOX_LOC + File.separator
					+ AppzillonMainScreen.ASSETS_MAIN_FOLDER + File.separator
					+ assetName;
			System.out.println("appLodc : " + appLoc);
			File appFile = new File(appLoc);
			if (appFile.exists()) {
				return true;
			} else {
				return false;
			}
		} else {
			List<String> mapList = null;
			if (mapList == null) {
				AssetManager am = activity.getApplicationContext().getAssets();
				try {
					mapList = Arrays.asList(am.list("apps"));
				} catch (IOException e) {
				}
			}
			return mapList.contains(assetName) ? true : false;
		}
	}


	public void upgradeApp(String appName) {
//		ApzLogger.i(TAG, "upgradeApp : " + appName);
		boolean subAppOTARefreshStatus = false;
		SharedPreferences apps = activity.getSharedPreferences(
				AppzillonMainScreen.app_props, 0);
		String appVersion = UserSettings.getAppVersion(appName, apps);
		// Abhishek Bug id 4439 START
		if (appVersion.equalsIgnoreCase("0.0.0")) {
			StringUtils stringUtils = new StringUtils(activity,
					AppzillonMainScreen.ASSETS_MAIN_FOLDER + File.separator
							+ appName);
			appVersion = StringUtils.getString(StringUtils.APP_VERSION);
		}
		// Abhishek Bug id 4439 START
		OTAPlugin otaPlugin = new OTAPlugin(activity);
		subAppOTARefreshStatus = otaPlugin.getDataForOTA(appName, appVersion);
		if (subAppOTARefreshStatus) {
			UserSettings.setAppVersion(appName,
					otaPlugin.getUpdatedAppversion(), apps);
		}
	}

	public void appInstructions(JSONObject jsonObj) {

		SharedPreferences apps = activity.getSharedPreferences(
				AppzillonMainScreen.app_props, 0);
		try {
			AuditLog.makeString("APP INSTRUCTIONS", "START");
			String appName = jsonObj.getString("appId");
			String presentAppid = StringUtils.getString(StringUtils.APP_ID);
			String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
			final JSONObject response = AppzillonMainScreen.getAppInstructions(
					appName, presentAppid, serverUrl);
			if (response != null) {
//				ApzLogger.i(TAG, "App instructions Response : " + response);
				JSONObject body = response
						.getJSONObject(AppzillonMainScreen.APPZILLON_BODY);
				JSONObject header = response
						.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
				boolean resStatus = header.getBoolean("status");
				if (resStatus) {
					final JSONObject responseBody = body.getJSONObject(appName);
					String lVersion = UserSettings.getAppVersion(appName, apps);
					if (lVersion.equalsIgnoreCase("0.0.0")) {
						StringUtils stringUtils = new StringUtils(
								activity.getApplicationContext(),
								AppzillonMainScreen.ASSETS_MAIN_FOLDER
										+ File.separator + appName);
						lVersion = StringUtils
								.getString(StringUtils.APP_VERSION);
					}
                    if ((AppzillonMainScreen.OTAREQUIRED).equalsIgnoreCase("Y")) {
					if (!lVersion.equalsIgnoreCase(responseBody
							.getString(AppzillonMainScreen.APP_VERSION))) {
						responseBody.put("upgradeRequired", "Y");
						AppzillonMainScreen.UPDATE_REQUEST = "Y";
					} else {
                                               AppzillonMainScreen.UPDATE_REQUEST = "N";
						responseBody.put("upgradeRequired", "N");
					}
                    }else{
                        AppzillonMainScreen.UPDATE_REQUEST = "N";
                         responseBody.put("upgradeRequired", "N");
                    }
                   //check for force update Action
                    if(responseBody.has("updateAction")){
                        AppzillonMainScreen.UPDATE_ACTION = responseBody.getString("updateAction");
                    }
                    //check for force update Action
					ApzPluginUtil.sendSuccess(callbackId, responseBody, false,
							this.activity, this.webView, true);
				} else {
					ApzPluginUtil.sendError(callbackId, "", null, activity,
							webView, true);// AppzillonMainScreen.APPZILLON_ERRORS
				}
			} else {
				String errorMsg = "Server Error";
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-304", null, activity,
						webView, true);// Server Error

			}
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity,
					webView, true);// AppzillonMainScreen.APPZILLON_ERRORS
		} catch (IllegalStateException e) {
			ApzLogger.e(TAG,e.toString());
		}
	}

	@Override
	public void execute(JSONObject params) {
		String action = null;
		String appId = null;
		try {
			action = params.getString("action");
			appId = params.optString("appId");
			callbackId = params.getString("id");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}
		if (action.equals("APPLAUNCH")) {
			loadPage(appId);
		} else if (action.equals("APPDELETE")) {
			ApzPlugin wp = WipeOut.createPlugin(this.webView, this.activity);

			try {
				params.put("action", "subAppDelete");
			} catch (JSONException e) {
			}
			wp.execute(params);
		} else if (action.equals("INSTRUCTIONS")) {
			appInstructions(params);
		} else if (action.equals("UPGRADEREQ")) {
			JSONObject result = new JSONObject();
			try {
				result.put("text", AppzillonMainScreen.UPDATE_REQUEST);
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
			}
			ApzPluginUtil.sendSuccess(callbackId, result, false, activity,
					webView, true);

		}
		else if (action.equals("UPDATEACTION")) {
			JSONObject result = new JSONObject();
			try {
				result.put("updateAction", AppzillonMainScreen.UPDATE_ACTION);
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
			}
			ApzPluginUtil.sendSuccess(callbackId, result, false, activity,
					webView, true);

		} else if (action.equalsIgnoreCase("UPGRADEAPP")) {
			boolean subAppOTARefreshStatus = false;
			String appName = null;
			try {
				appName = params.getString("appId");
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
			}
			SharedPreferences apps = activity.getApplicationContext()
					.getSharedPreferences(AppzillonMainScreen.app_props, 0);
			String appVersion = UserSettings.getAppVersion(appName, apps);
			// Abhishek Bug id 4439 START
			if (appVersion.equalsIgnoreCase("0.0.0")) {
				StringUtils stringUtils = new StringUtils(
						activity.getApplicationContext(),
						AppzillonMainScreen.ASSETS_MAIN_FOLDER + File.separator
								+ appName);
				appVersion = StringUtils.getString(StringUtils.APP_VERSION);
			}
			// Abhishek Bug id 4439 START
			OTAPlugin otaPlugin = new OTAPlugin(
					activity.getApplicationContext());
			subAppOTARefreshStatus = otaPlugin.getDataForOTA(appName,
					appVersion);
			if (subAppOTARefreshStatus) {
				// Abhishek 09 April updated the version as received in responce
				// START
				UserSettings.setAppVersion(appName,
						otaPlugin.getUpdatedAppversion(), apps);
				// Abhishek 09 April updated the version as received in responce
				// END
				JSONObject result = new JSONObject();

				try {

					result.put("text", "");

				} catch (JSONException e) {

				}

				// OTA Succsscallback changes start

				ApzPluginUtil.sendSuccess(callbackId, null, false, activity,

						webView, true);
			}else{
				ApzPluginUtil.sendError(callbackId, "", null, activity, webView, true);
			}
		}

	}
}
