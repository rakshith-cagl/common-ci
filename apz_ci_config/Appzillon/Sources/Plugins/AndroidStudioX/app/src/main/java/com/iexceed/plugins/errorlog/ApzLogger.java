package com.iexceed.plugins.errorlog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.util.Log;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.ApzPlugin;

public class ApzLogger {

	public static int LOG_LEVEL_FATAL = 0;
	public static int LOG_LEVEL_ERROR = 1;
	public static int LOG_LEVEL_WARN = 2;
	public static int LOG_LEVEL_INFO = 3;
	public static int LOG_LEVEL_DEBUG = 4;

	public static StringBuilder stringBuilder = new StringBuilder();
	private static int appLogLevel = ApzPlugin.debugLevel;

	public static void writelog() {
		try {
			Process process = Runtime.getRuntime().exec("logcat -d");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			StringBuilder log = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				log.append(line);
				log.append("\r\n");
			}

		} catch (IOException e) {
		}
	}

	public static void e(String plugIn, String action) {
		if (appLogLevel >= LOG_LEVEL_ERROR) {
			//Log.e(plugIn, action);
			appendLogger("E", plugIn, action);
		}
	}
	

	public static void i(String plugIn, String action) {
		if (appLogLevel >= LOG_LEVEL_INFO) {
			//Log.i(plugIn, action);
			appendLogger("I", plugIn, action);
		}
	}

	public static void d(String plugIn, String action) {
		if (appLogLevel >= LOG_LEVEL_DEBUG) {
			//Log.d(plugIn, action);
			appendLogger("D", plugIn, action);
		}
	}

	public static void w(String plugIn, String action) {
		if (appLogLevel >= LOG_LEVEL_WARN) {
			//Log.w(plugIn, action);
			appendLogger("W", plugIn, action);
		}
	}

	public static void v(String plugIn, String action) {
		if (appLogLevel >= LOG_LEVEL_FATAL) {
			//Log.v(plugIn, action);
			appendLogger("F", plugIn, action);
		}

	}

	private static void appendLogger(final String type,
			final String PLUG_IN, final String message) {
		if (AppzillonMainScreen.activity != null && AppzillonMainScreen.webView != null ) {
			AppzillonMainScreen.activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						AppzillonMainScreen.webView
								.loadUrl("javascript:Apz.appendLog('" + type
										+ "','" + message + "');");
					} catch (Exception e) {
						
					}
				}
			});
		}
	}

}
