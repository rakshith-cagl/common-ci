package com.iexceed.plugins.runtimelogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.WebView;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.MediaUtils;
import com.iexceed.common.UserSettings;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.errorlog.ApzLogger;

public class RuntimeLogger {

	private Context mContext;
	
	private Activity mActivity;
	
	private WebView mWebView;
	
	private StringBuffer buffer;
	
	private SharedPreferences settings;
	
	final static String properties = "USER_PREFS";
	
	public RuntimeLogger(Context context, Activity activity, WebView webview) {
		mContext = context;
		mActivity = activity;
		mWebView = webview;
		buffer = new StringBuffer();
		settings = mActivity.getSharedPreferences(properties, 0);
	}

	/**
	 * Logs error and it's source to the logcat console
	 * 
	 * @param jsonObj
	 */
	public void logRuntimeMessage(JSONObject jsonObj) {
		String errorType = "";
		String errorSource = "";
		String errorMsg = "";
		String errorSeverity = "";
		try {
			errorType = jsonObj.getString("errorType");
			errorSource = jsonObj.getString("errorSource");
			errorMsg = jsonObj.getString("errorMessage");
			errorSeverity = jsonObj.getString("errorSeverity");

			System.out.println("ERROR_TYPE:" + jsonObj.getString("errorType")
					+ "  ERROR_SOURCE:" + jsonObj.getString("errorSource")
					+ "  ERROR_MESSAGE:" + jsonObj.getString("errorMessage")
					+ "  ERROR_SEVERITY:" + jsonObj.getString("errorSeverity"));
		} catch (JSONException e) {
			ApzLogger.e("RunTimeLogger",e.toString());
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					mWebView.loadUrl("javascript:jsonParseExceptionCallBack"
							+ "("
							+ "{\"errorCode\":\"null\",\"errorDescription\":\"Bad JSON\"}"
							+ ");");
				}
			});
			return;
		}
		if (errorType.equals("E")) {
			buffer.append(new Date(System.currentTimeMillis()).getTime() + " "+ errorType + " " + errorSource + " " + errorMsg + " "+ errorSeverity + "\n");
			new Thread(new Runnable() {
				@Override
				public void run() {

					writeToFile(buffer);

				}			

			}).start();

		} else {
			int length = buffer.toString().split("\n").length;
			if (length > 100) {
				String temp[] = buffer.toString().split("\n");
				// clear the buffer
				buffer.delete(0, buffer.toString().length() - 1);
				// restores the buffer
				for (int i = temp.length / 2; i < temp.length; i++) {
					buffer.append(temp[i]);
				}
			}
			buffer.append(new Date(System.currentTimeMillis()).getTime() + " "
					+ errorType + " " + errorSource + " " + errorMsg + " "+ errorSeverity + "\n");
		}
	}
	
	private void writeToFile(StringBuffer buffer) {

		// TODO Auto-generated method stub
		if (MediaUtils.isSDCardPresent()) {
			OutputStream myOutput = null;
			OutputStreamWriter osw = null;
			//Abhishek 20 April 2015 Updated path to specific app sandbox START
			String outFileName = AppzillonMainScreen.SANDBOX_LOC +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"app"+  "/Log";
			//Abhishek 20 April 2015 Updated path to specific app sandbox END
//			File f = new File(outFileName);
			File f = AppzillonUtils.getApzFile(outFileName,null);
			f.mkdirs();
			final File directory = new File(f, "log.txt");

			try {
				myOutput = new FileOutputStream(directory, true);
				osw = new OutputStreamWriter(myOutput);
				// write the string to file
				osw.write(buffer.toString());
				osw.flush();
			} catch (Exception e) {
				ApzLogger.e("RunTimeLogger",e.toString());
			} finally {
				if (myOutput != null) {

					try {
						osw.close();
						buffer.delete(0, buffer.length() - 1);
					} catch (IOException e) {
						ApzLogger.e("RunTimeLogger",e.toString());
					}
				}
			}
			/* Upload logfile to server */
			String str = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,"UPLOADLOGFILE", "", settings);
//			ApzLogger.d("UPLOADLOGFILE : ",str);
			if ("Y".equals(str)) {
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						//Abhishek 20 April 2015 Updated path to specific app sandbox START
						String logFilePath = AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC+"app"+  "/Log/log.txt";
						//Abhishek 20 April 2015 Updated path to specific app sandbox END
//						mWebView.loadUrl("javascript:appzillon.plugin.uploadLog('"+ logFilePath + "');");
					}
				});
			}
		}

	}

	public static boolean isRuntimeLogger() {
		return true;
	}
}
