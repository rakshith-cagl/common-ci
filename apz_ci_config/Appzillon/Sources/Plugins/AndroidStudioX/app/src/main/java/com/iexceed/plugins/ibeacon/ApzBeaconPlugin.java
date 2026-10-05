package com.iexceed.plugins.ibeacon;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.altbeacon.beacon.Beacon;
import org.json.JSONException;
import org.json.JSONObject;

public class ApzBeaconPlugin extends ApzPlugin {

	private static ApzPlugin pluginObj;
	private String action;
	private JSONObject jsonObject;
	private String[] permissions;

	private ApzBeaconPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new ApzBeaconPlugin(webView, activity);
		}
		return pluginObj;
	}


	@Override
	public void execute(JSONObject params) {
		try {
			action = params.getString("action");
			callbackId = params.getString("id");
			jsonObject = params;

			if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
				if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH)
						!= PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN)
						!= PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
						!= PackageManager.PERMISSION_GRANTED) {
					permissions = new String[]{Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_FINE_LOCATION};
					requestForPermission();
				} else {
					detectBeacon();
				}
			} else {
				detectBeacon();
			}


		} catch (JSONException e) {
			ApzLogger.e(TAG, e.toString());
		}

	}

	private void detectBeacon() {
		try {
			if (action.equalsIgnoreCase("Start")) {
				String uuid = jsonObject.getString("uuid");
				if (!uuid.isEmpty()) {
					if(!BeaconMonitoring.isBeaconRunning) {
						BeaconMonitoring.setWebView(webView, activity, callbackId);
//					Intent in = new Intent(mActivity, BeaconMonitoring.class);
////					in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//					in.putExtra("BEACON_ID", uuid);
//					in.putExtra("callbackId", mCallbackId);
//					mActivity.startActivity(in);

						Intent serviceIntent = new Intent(activity, BeaconMonitoring.class);
						serviceIntent.putExtra("BEACON_ID", uuid);
						BEACON_ID = uuid;
						activity.startService(serviceIntent);
					}else{
						ApzPluginUtil.sendError(callbackId, "APZ-CNT-254", null, activity, webView, true);
					}
				} else {
					ApzPluginUtil.sendError(callbackId, "APZ-CNT-256", null, activity, webView, true);
					pluginObj = null;
				}
			} else if (action.equalsIgnoreCase("Stop")) {
				BEACON_ID = "";
				/*if (BeaconMonitoring.activity == null
						|| BeaconMonitoring.activity.isFinishing()) {*/
					/*JSONObject json = new JSONObject();
					ApzLogger.e(TAG, "APZ-CNT-254");
					try {
						json.put("errorCode", "APZ-CNT-254");
					} catch (JSONException e) {
					}
					ApzPluginUtil.sendError(callbackId, "APZ-CNT-254", json, mActivity,
							webView, true);
					pluginObj = null;*/

				//} else {
					/*BeaconMonitoring.activity.finish();
					ApzPluginUtil.sendSuccess(callbackId, null, false,
							mActivity, webView, true);
					pluginObj = null;*/
				//}
				if(BeaconMonitoring.isBeaconRunning){
					JSONObject json = new JSONObject();
					try {
						json.put("event", "stopped");
					} catch (JSONException e) {
					}
					activity.stopService(new Intent(activity,BeaconMonitoring.class));
					ApzPluginUtil.sendSuccess(callbackId,json,false,activity,webView, true);
					pluginObj = null;
				}else{
					ApzPluginUtil.sendError(callbackId, "APZ-CNT-254", null, activity,
							webView, true);
				}}
		} catch (Exception e) {
			//Log.d("ApzBeaconPlugin",e.getMessage());
		}

	}

	private void requestForPermission() {
		this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_BLUETOOTH, new OnPermissionsResultHandler() {
					@Override
					public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
						if (requestCode == ApzPlugin.APZ_REQ_BLUETOOTH) {
							boolean denied = false;
							boolean never_ask_again = false;
							for (String permission : permissions) {
								if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
									denied = true;
								} else {
									if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
										//callCamera();
									} else {
										never_ask_again = true;
									}
								}
							}
							if (never_ask_again) {
								PermissionDeniedCallback();
							} else if (denied) {
								displayReconfirmationMessage();
							} else {
								detectBeacon();
							}
						} else {
							PermissionDeniedCallback();
						}

					}
				}
		);
	}
	private void displayReconfirmationMessage() {
		String message = "To search beacons,allow app to access by granting requested permissions";
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		alertDialogBuilder.setTitle("Permission Denied");
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						requestForPermission();
					}
				}).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				PermissionDeniedCallback();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	private void PermissionDeniedCallback(){
		ApzPluginUtil.sendPermissionDenied("Bluetooth",callbackId, this.activity,this.webView);
	}

	public static boolean isBeaconPlugin() {
		return true;
	}


}

