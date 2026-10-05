package com.iexceed.plugins.vibration;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.os.Build.VERSION_CODES;
import android.webkit.WebView;

public class Vibration extends ApzPlugin {

	private static ApzPlugin pluginObj;

	private Vibration(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new Vibration(webView, activity);
		}
		return pluginObj;
	}

	@SuppressLint("NewApi")
	public void vibrate(JSONObject jsonobj) {

		int vibtime = 0;
		try {
			callbackId = jsonobj.getString("id");
			vibtime = jsonobj.getInt("time");
		} catch (JSONException e) {
			ApzLogger.i("Vibration",e.toString());
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null,
					this.activity, this.webView, true);
		}
		Vibrator vib = (Vibrator) activity.getApplicationContext()
				.getSystemService(Context.VIBRATOR_SERVICE);

		Boolean checkvibrator = true;
		if (Build.VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1) {
			checkvibrator = vib.hasVibrator();
		}

		if (checkvibrator) {
			vib.vibrate(vibtime);
			JSONObject result = new JSONObject();
			try {
				result.put("successMessage", "Success");
			} catch (JSONException e) {
			}
			ApzPluginUtil.sendSuccess(callbackId, result, false, activity,
					webView, true);

		} else {
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-134", null,
					this.activity, this.webView, true);

		}
	}

	public void stop() {

		Vibrator vib = (Vibrator) activity.getApplicationContext()
				.getSystemService(Context.VIBRATOR_SERVICE);
		vib.cancel();
	}

	public static boolean isVibratePlugin() {
		return true;
	}

	@Override
	public void execute(JSONObject params) {
		vibrate(params);

	}

}
