package com.iexceed.plugins.appidletimeout;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.StringUtils;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class AppIdleTimeOut extends ApzPlugin {

	public static int APPIDLE_TIMEOUT = 0;
	private static ApzPlugin pluginObj;
	private static Timer appTimer;
	private static String callbackId;
	private static Activity mActivity;
	private static WebView mWebView;
	private static String TAG = "AppIdleTimeOut";

	private AppIdleTimeOut(WebView webView, ApzActivity activity) {
		super(webView, activity);
		mActivity = activity;
		mWebView = webView;
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new AppIdleTimeOut(webView, activity);
		}
		return pluginObj;
	}

	public void startTimeClocking(JSONObject jsonobj) {
		try {
			APPIDLE_TIMEOUT = Integer.parseInt(StringUtils.getString(StringUtils.APP_IDLE_TIME_OUT))*1000;
			startTimerForAppIdleTimeout();
			final String successMsg = "started";
			JSONObject result = new JSONObject();
			result.put("event", successMsg);
			ApzLogger.d(TAG,"Idle timer started");
			ApzPluginUtil.sendSuccess(callbackId, result, true, activity, webView, true);

		} catch (JSONException e) {
			ApzLogger.e(TAG,"Idle timer exception"+e);
		}
	}

	public static void startTimerForAppIdleTimeout() {

		if (appTimer != null) {
			appTimer.cancel();
			appTimer = null;
		}
		if (appTimer == null && APPIDLE_TIMEOUT > 0) {
			appTimer = new Timer(true);
			final JSONObject result = new JSONObject();
			try {
				result.put("event", "timerExceeds");
			} catch (JSONException e) {
				ApzLogger.e(TAG,"Idle timer exception"+e);
			}
			appTimer.schedule(new TimerTask() {
				
				public void run() {
					APPIDLE_TIMEOUT = 0;
					ApzLogger.d(TAG,"Idle timer executed");
					ApzPluginUtil.sendSuccess(callbackId, result, false, mActivity, mWebView, true);
				}
			}, APPIDLE_TIMEOUT);

		} else {
			ApzLogger.e(TAG,"APZ-CNT-200");
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-200", null, mActivity, mWebView, true);//Unable to start timer
		}

	}

	public static boolean isPlugin() {
		return true;
	}

	@Override
	public void execute(JSONObject params) {
		try {
			callbackId = params.getString("id");
		} catch (JSONException e) {
		}
			startTimeClocking(params);
	}

}
