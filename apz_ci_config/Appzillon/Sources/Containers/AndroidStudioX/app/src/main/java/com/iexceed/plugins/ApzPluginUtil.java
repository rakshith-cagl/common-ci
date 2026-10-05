package com.iexceed.plugins;

import android.app.Activity;
import android.webkit.WebView;

import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class ApzPluginUtil {

	static String TAG = "ApzPluginUtil";
	static String JS_CALLBACK = "Apz.nativeServiceCB";

	public static void sendSuccess(String callbackId, JSONObject result,
			boolean isKeepAlive, Activity activity, WebView webView,
			boolean isInUIThread) {
		try {
			ApzLogger.d(TAG, "Send Success");
			
			if(result == null)
				result = new JSONObject();
			result.put("id", callbackId);
			result.put("status", true);
			result.put("keepAlive", isKeepAlive);

			/*sendMsgToJS("javascript:" + JS_CALLBACK + "(" + result + ");",
					activity, webView, isInUIThread);*/
			sendMsgToJS(result,activity, webView, isInUIThread);

		} catch (JSONException e) {
			ApzLogger.e(TAG, e.getMessage());
		}
	}
	
	public static void sendSuccess(String callbackId, JSONArray result, String resutKey,
			boolean isKeepAlive, Activity activity, WebView webView,
			boolean isInUIThread) {
		try {
			ApzLogger.d(TAG, "Send Success");
			JSONObject resultObj = new JSONObject();
			
			resultObj.put("id", callbackId);
			resultObj.put("status", true);
			resultObj.put("keepAlive", isKeepAlive);
			resultObj.put(resutKey, result);

			/*sendMsgToJS("javascript:" + JS_CALLBACK + "(" + resultObj + ");",
					activity, webView, isInUIThread);*/
			sendMsgToJS(resultObj,activity, webView, isInUIThread);

		} catch (JSONException e) {
			ApzLogger.e(TAG, e.getMessage());
		}
	}

	public static void sendError(String callbackId, String errorCode,
			JSONObject result, Activity activity, WebView webView,
			boolean isInUIThread) {
		try {
			ApzLogger.d(TAG, "Send Error");
			if(result == null)
				result = new JSONObject();
			result.put("id", callbackId);
			result.put("status", false);
			result.put("errorCode", errorCode);
			result.put("keepAlive", false);

			/*sendMsgToJS("javascript:" + JS_CALLBACK + "(" + result + ");",
					activity, webView, isInUIThread);*/
			sendMsgToJS(result,activity, webView, isInUIThread);

		} catch (JSONException e) {
			ApzLogger.e(TAG, e.getMessage());
		}
	}

	public static void sendPluginNotSupported(String callbackId,
			Activity activity, WebView webView, boolean isInUIThread) {
		try {
			ApzLogger.d(TAG, "Send Error");

			JSONObject result = new JSONObject();
			
			result.put("id", callbackId);
			result.put("status", false);
			result.put("errorCode", "APZ-CNT-022");
			result.put("keepAlive", false);
			result.put("body", "");

			/*sendMsgToJS("javascript:" + JS_CALLBACK + "(" + result + ");",
					activity, webView, isInUIThread);*/
			sendMsgToJS(result,activity, webView, isInUIThread);

		} catch (JSONException e) {
			ApzLogger.e(TAG, e.getMessage());
		}
	}

	/*public static void sendMsgToJS(final String msg, Activity activity,
			final WebView webView, boolean isInUIThread) {
		if (isInUIThread) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl(msg);
				}
			});
		} else {
			webView.loadUrl(msg);
		}

	}*/
	public static void sendMsgToJS(final JSONObject result, Activity activity,
			final WebView webView, boolean isInUIThread) {
//		final String msg = "javascript:" + JS_CALLBACK + "(" + result + ");";
		final String msg = JS_CALLBACK + "(" + result + ");";
		if (isInUIThread) {
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					/**
					 * Prabudas S, 16Feb17
					 * Call Javascript function using webView.evaluateJavascript 
					 * Instead of webView.loadUrl
					 */
//					webView.loadUrl(msg);
					webView.evaluateJavascript(msg, null);
				}
			});
		} else {
//			webView.loadUrl(msg);
			webView.evaluateJavascript(msg, null);
		}

	}

	public static void sendPermissionDenied(String pluginName, String callbackId,
											Activity activity, WebView webView){
		JSONObject result = new JSONObject();
		try {
			result.put("text",""+ pluginName + "access permissions Denied.");
			sendError(callbackId, "APZ-CNT-329", result, activity,
					webView, true);
		} catch (JSONException e) {
		}
	}

}

