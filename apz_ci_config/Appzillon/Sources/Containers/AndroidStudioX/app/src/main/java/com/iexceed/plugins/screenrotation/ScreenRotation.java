package com.iexceed.plugins.screenrotation;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.Surface;
import android.webkit.WebView;

public class ScreenRotation extends ApzPlugin {

	private String TAG = "LOCK_SCREEN_ROTATION";
	private static ApzPlugin pluginObj;

	private ScreenRotation(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new ScreenRotation(webView, activity);
		}
		return pluginObj;
	}

	public void enableLock(JSONObject lockJson, boolean device) {

		try {
			callbackId = lockJson.getString("id");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}
		final int orientation = activity.getResources().getConfiguration().orientation;
		final int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		String appOrientation = "";

		// Copied from Android docs, since we don't have these values in Froyo
		// 2.2
		int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
		int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;

		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
			if (orientation == Configuration.ORIENTATION_PORTRAIT) {
				if (device) {
					activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				} else {
					activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
				appOrientation = "PORTRAIT";
			} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				appOrientation = "LANDSCAPE";
			}
		}

		else if (rotation == Surface.ROTATION_180
				|| rotation == Surface.ROTATION_270) {
			if (orientation == Configuration.ORIENTATION_PORTRAIT) {
				if (device) {
					activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else {
					activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_PORTRAIT);
				}

				appOrientation = "REVERSE_PORTRAIT";
			} else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
				appOrientation = "REVERSE_LANDSCAPE";
			}
		}
		ApzLogger.i(TAG, "Orientation : " + appOrientation);
		showCurrentOrientation(appOrientation);
	}

	public void showCurrentOrientation(final String orientation) {
		JSONObject result = new JSONObject();
		try {
			result.put("successMessage", orientation);
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}
		ApzPluginUtil.sendSuccess(callbackId, result, false, activity, webView,
				true);
	}

	public void disableLock(JSONObject unLockJson) {
		try {
			callbackId = unLockJson.getString("id");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity,
					webView, true);
			return;
		}
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		showCurrentOrientation("Successfully unlocked the rotation");
	}

	@Override
	public void execute(JSONObject params) {
		String action = "";
		try {
			action = params.getString("action");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}

		if (action.equals("LOCKROTATION")) {
			enableLock(params, false);
		} else if (action.equals("UNLOCKROTATION")) {
			disableLock(params);
		}
	}
}
