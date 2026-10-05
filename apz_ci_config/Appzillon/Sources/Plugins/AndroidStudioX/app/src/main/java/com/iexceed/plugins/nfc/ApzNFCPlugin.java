package com.iexceed.plugins.nfc;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.ExternalActivityResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.auditlog.AuditLog;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzNFCPlugin extends ApzPlugin {
	public static final int SEND_NFC = 101;
	public static final int RECEIVE_NFC = 103;
	public static final int DEVICE_RECEIVE_NFC = 104;
	private static ApzPlugin pluginObj;
	public static int mRequestCode = 0;
	
	String TAG = "ApzNFCPlugin";
	
	private ApzNFCPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){ 
		pluginObj = new ApzNFCPlugin(webView, activity); 
	}  
	return pluginObj; 
	}

	@Override
	public void execute(JSONObject params) {
		try {
			String execute = params.getString("executeAction");
			this.callbackId = params.getString("id");
			String type = null;
			String content = null;
			String action = null;
			if (execute.equalsIgnoreCase("SEND")) {
				try {
					type = params.getString("type");
					content = params.getString("content");
					action = params.getString("action");
				} catch (JSONException e1) {
					ApzLogger.e(TAG,e1.toString());
					ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-077",
							null, ApzNFCPlugin.this.activity,
							ApzNFCPlugin.this.webView, true);
				}
				Intent sendnfc = null;
				if (action.equalsIgnoreCase("tag")) {
					sendnfc = new Intent(
							ApzNFCPlugin.this.activity.getBaseContext(),
							WriteNFCActivity.class);
				} else if (action.equalsIgnoreCase("device")) {
					sendnfc = new Intent(
							ApzNFCPlugin.this.activity.getBaseContext(),
							NFCDeviceActivity.class);
					// Abhishek 29 May 2015, adding value to set send/receive
					// START
					sendnfc.putExtra("device", "SEND");
					// Abhishek 29 May 2015, adding value to set send/receive
					// END
				}
				sendnfc.putExtra("Type", type);
				sendnfc.putExtra("Content", content);

				this.activity.startActivityForResult(sendnfc, SEND_NFC,
						new ExternalActivityResultHandler() {

							@Override
							public void handleActivityResult(int resultCode,
									Intent data) {
								if (resultCode == Activity.RESULT_OK) {
									// Abhishek, 27 May 2015, updated callback
									// result,sending back JSON START
									final JSONObject sJson = new JSONObject();
									try {
										sJson.put("successMessage",
												"Send Successfully.");
									} catch (final JSONException ex) {
										ApzLogger.e(TAG,ex.toString());
										return;
									}
									// Abhishek, 27 May 2015, updated callback
									// result,sending back JSON END
									ApzPluginUtil.sendSuccess(
											ApzNFCPlugin.this.callbackId,
											sJson, false,
											ApzNFCPlugin.this.activity,
											ApzNFCPlugin.this.webView, true);
								} else {
									AuditLog.sendToJSON();
									final JSONObject eJson = new JSONObject();
									try {
										eJson.put("errorMessage",
												"Failed to write.");
									} catch (final JSONException ex) {
										ApzLogger.e(TAG,ex.toString());
										return;
									}
									ApzPluginUtil.sendError(
											ApzNFCPlugin.this.callbackId, "APZ-CNT-310",
											eJson, ApzNFCPlugin.this.activity,
											ApzNFCPlugin.this.webView, true);
								}
							}
						});

			} else if (execute.equalsIgnoreCase("RECEIVE")) {
				try {
					type = params.getString("type");
					action = params.getString("action");
				} catch (JSONException e1) {
					ApzLogger.e(TAG,e1.toString());
					ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-077",
							null, ApzNFCPlugin.this.activity,
							ApzNFCPlugin.this.webView, true);
				}
				Intent recnfc = null;
				// sid changes to make just one onresult start

				if (action.equalsIgnoreCase("tag")) {
					recnfc = new Intent(
							ApzNFCPlugin.this.activity.getBaseContext(),
							ReadNFCActivity.class);
					recnfc.putExtra("Type", type);
					mRequestCode = RECEIVE_NFC;
					// activity.startActivityForResult(recnfc, RECEIVE_NFC);
				} else if (action.equalsIgnoreCase("device")) {
					recnfc = new Intent(
							ApzNFCPlugin.this.activity.getBaseContext(),
							NFCDeviceActivity.class);
					// Abhishek 29 May 2015, removing "type" from receive,
					// adding value to set send/receive START
					recnfc.putExtra("device", "RECEIVE");
					mRequestCode = DEVICE_RECEIVE_NFC;
					// Abhishek 29 May 2015, removing "type" from receive,
					// adding value to set send/receive END
					// activity.startActivityForResult(recnfc,
					// DEVICE_RECEIVE_NFC);
				}
				// sid changes to make one onresult ends
				this.activity.startActivityForResult(recnfc, mRequestCode,
						new ExternalActivityResultHandler() {

							@Override
							public void handleActivityResult(int resultCode,
									Intent data) {
								if (resultCode == Activity.RESULT_OK) {
									if (mRequestCode == RECEIVE_NFC) {
										// Abhishek, 27 May 2015, updated
										// callback result,sending back JSON in
										// place of string START
										final JSONObject sJson = new JSONObject();
										try {
											sJson.put("successMessage",
													ReadNFCActivity.NFC_RESULT);
										} catch (final JSONException ex) {
											ApzLogger.e(TAG,ex.toString());
											return;
										}
										// Abhishek, 27 May 2015, updated
										// callback result,sending back JSON in
										// place of string END
										ApzPluginUtil
												.sendSuccess(
														ApzNFCPlugin.this.callbackId,
														sJson,
														false,
														ApzNFCPlugin.this.activity,
														ApzNFCPlugin.this.webView,
														true);
									} else if (mRequestCode == DEVICE_RECEIVE_NFC) {
										final JSONObject sJson = new JSONObject();
										try {
											sJson.put(
													"successMessage",
													NFCDeviceActivity.NFC_RESULT);
										} catch (final JSONException ex) {
											ApzLogger.e(TAG,ex.toString());
											return;
										}
										// Abhishek, 27 May 2015, updated
										// callback result,sending back JSON in
										// place of string END
										ApzPluginUtil
												.sendSuccess(
														ApzNFCPlugin.this.callbackId,
														sJson,
														false,
														ApzNFCPlugin.this.activity,
														ApzNFCPlugin.this.webView,
														true);
									} else {
										final JSONObject eJson = new JSONObject();
										try {
											eJson.put("errorDescription",
													"Failed to read from TAG.");
										} catch (final JSONException ex) {
											return;
										}
										ApzPluginUtil
												.sendError(
														ApzNFCPlugin.this.callbackId,
														"APZ-CNT-311",
														eJson,
														ApzNFCPlugin.this.activity,
														ApzNFCPlugin.this.webView,
														true);
									}

								} else {
									final JSONObject eJson = new JSONObject();
									try {
										eJson.put("errorDescription",
												"Failed to read from TAG.");
									} catch (final JSONException ex) {
										return;
									}
									ApzPluginUtil
											.sendError(
													ApzNFCPlugin.this.callbackId,
													"APZ-CNT-311",
													eJson,
													ApzNFCPlugin.this.activity,
													ApzNFCPlugin.this.webView,
													true);
								}
							}
						});

				// sid changes to make just one onresult end
			} else if(execute.equalsIgnoreCase("STOP")){
					Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
					activity.startActivity(intent);
			}
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}

	}
	
	public static boolean isNFCDeviceActivity() {
		return true;
	}

}
