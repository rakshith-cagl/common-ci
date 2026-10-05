package com.iexceed.plugins.auditlog;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.iexceed.common.JavaScriptInterface;
//import com.iexceed.plugins.errorlog.ServerLog;

public class AuditLog {

	static String startTime = "";
	static String pluginName = "";
	static String auditMessage = "";

	private static String DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss.SSS";

	public static void makeString(String PLUG_IN, String ACTION) {
		auditMessage = "";
		startTime = getCurrentDateTime().toString();
		pluginName = PLUG_IN;

	}

	public static void sendToJSON() {
		String action = "NATIVE";
		JSONObject sendJSON = null;

		try {
			sendJSON = new JSONObject();

			sendJSON.put("action", action);
			sendJSON.put("startTime", startTime);
			sendJSON.put("endTime", getCurrentDateTime().toString());
			sendJSON.put("field1", pluginName);
			sendJSON.put("field2", "");
			sendJSON.put("field3", "");
			sendJSON.put("field4", "");
			sendJSON.put("field5", "");
		} catch (Exception e1) {
			
		}
		auditMessage = sendJSON.toString();
		JavaScriptInterface.activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// AuditLog.makeString("ENCRYPT DATA","Success");
				//JavaScriptInterface.webView.loadUrl("javascript:appzillon.util.auditLog("+ auditMessage + ");");	
			}
		});
	//ServerLog.saveLogInJavascript();	
	}

	private static String getCurrentDateTime() {

		Date cur_date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMATTER);
		String currDate = null;
		try {
			currDate = sdf.format(cur_date);
		} catch (Exception e) {
			
		}

		return currDate;
	}

}
