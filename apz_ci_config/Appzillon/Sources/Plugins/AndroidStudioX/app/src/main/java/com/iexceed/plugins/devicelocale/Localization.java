package com.iexceed.plugins.devicelocale;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.webkit.WebView;

public class Localization extends ApzPlugin {

	private String mCurrLocale;
	private static ApzPlugin pluginObj;
	private String mCurrCountry;

	private Localization(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new Localization(webView, activity);
		}
		return pluginObj;
	}

	/* Returns current locale of the device */
	public void getDeviceLocale(JSONObject jsonObj) {
		int ver = Integer.valueOf(android.os.Build.VERSION.SDK);
		if (ver >= 23) {
			try {
				callbackId = jsonObj.getString("id");
				Process execLang = Runtime.getRuntime().exec(
						new String[] { "getprop", "persist.sys.locale" });

				mCurrLocale = new BufferedReader(new InputStreamReader(
						execLang.getInputStream())).readLine();
				execLang.destroy();
				/*
				 * }else{ Process execLang = Runtime.getRuntime().exec( new
				 * String[] { "getprop", "persist.sys.language" });
				 *
				 *
				 * mCurrLocale = new BufferedReader(new InputStreamReader(
				 * execLang.getInputStream())).readLine(); execLang.destroy();
				 *
				 * Log.e("", "Device locale: "+execLang); Process execCountry =
				 * Runtime.getRuntime().exec( new String[] { "getprop",
				 * "persist.sys.country" }); mCurrCountry = new
				 * BufferedReader(new InputStreamReader(
				 * execCountry.getInputStream())).readLine();
				 * execLang.destroy(); }
				 */
			} catch (IOException e) {
				ApzLogger.e(TAG, e.toString());
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-049", null, this.activity,
						this.webView, true);
				return;
			} catch (SecurityException se) {
				ApzLogger.e(TAG, se.toString());
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-049", null, this.activity,
						this.webView, true);
				return;
			} catch (JSONException e) {
				ApzLogger.e(TAG, e.toString());
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null,
						this.activity, this.webView, true);

			}
			final JSONObject localeRes = new JSONObject();
			try {
				localeRes.put("locale", mCurrLocale);

			} catch (final JSONException e) {
				ApzLogger.e(TAG, e.toString());

				return;
			}
			ApzPluginUtil.sendSuccess(callbackId, localeRes, false,
					this.activity, this.webView, true);
		} else {
			try {
				callbackId = jsonObj.getString("id");
				Process execLang = Runtime.getRuntime().exec(
						new String[] { "getprop", "persist.sys.language" });

				mCurrLocale = new BufferedReader(new InputStreamReader(
						execLang.getInputStream())).readLine();
				execLang.destroy();
				Process execCountry = Runtime.getRuntime().exec(
						new String[] { "getprop", "persist.sys.country" });
				mCurrCountry = new BufferedReader(new InputStreamReader(
						execCountry.getInputStream())).readLine();
				execLang.destroy();
			} catch (IOException e) {
				ApzLogger.e(TAG, e.toString());
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-049", null, this.activity,
						this.webView, true);
				return;
			} catch (SecurityException se) {
				ApzLogger.e(TAG, se.toString());
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-049", null, this.activity,
						this.webView, true);
				return;
			} catch (JSONException e) {
				ApzLogger.e(TAG, e.toString());
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null,
						this.activity, this.webView, true);

			}
			final JSONObject localeRes = new JSONObject();
			try {
				localeRes.put("locale", mCurrLocale + "_" + mCurrCountry);

			} catch (final JSONException e) {
				ApzLogger.e(TAG, e.toString());

				return;
			}
			ApzPluginUtil.sendSuccess(callbackId, localeRes, false,
					this.activity, this.webView, true);
		}
	}

	public static boolean isLocalization() {
		return true;
	}

	@Override
	public void execute(JSONObject params) {
		getDeviceLocale(params);
	}
}
