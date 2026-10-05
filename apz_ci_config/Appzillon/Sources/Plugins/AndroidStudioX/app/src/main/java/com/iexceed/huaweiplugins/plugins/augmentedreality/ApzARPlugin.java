package com.iexceed.plugins.augmentedreality;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.HMSLocationCallback;
import com.iexceed.common.HMSLocationManager;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.camera.ApzCameraPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import static android.R.attr.action;

public class ApzARPlugin extends ApzPlugin{

	public static ApzPlugin pluginObj;

	public static Activity mActivity;
	public static WebView mWebview;
	public static String callerId;
	private JSONObject mParams;
	private String action;
	private String[] permissions;
	private static String TAG = "ApzARPlugin";

	JSONArray places = null;
	String theme = "";

	private ApzARPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
		mActivity = activity;
		mWebview = webView;
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){
		pluginObj = new ApzARPlugin(webView, activity);
	}
		return pluginObj;
	}

	public void startAugReality(JSONObject jsonobj){

		String thresholdDist = null;

		try {
			//callbackId = jsonobj.getString("id");
			thresholdDist = jsonobj.getString("thresholdDistance");
			double thDist = Double.parseDouble(thresholdDist);
			//CurrentLocation.setThreshold(thDist);
			theme = jsonobj.getString("theme");
			places = jsonobj.getJSONArray("Places");
		}catch (JSONException e){
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callerId, "APZ-CNT-077", null, activity, webView, true);
		}
//		Location presentLoc = CurrentLocation.getLocation(mActivity);
		HMSLocationManager lManager = new HMSLocationManager(mActivity);
		lManager.getLocation(new HMSLocationCallback() {
			@Override
			public void onLocationSuccess(JSONObject successJson) {
				try{
					Location presentLoc = new Location("");
					presentLoc.setLatitude(Double.parseDouble(successJson.getString("latitude")));
					presentLoc.setLongitude(Double.parseDouble(successJson.getString("longitude")));
					presentLoc.setAccuracy(Float.parseFloat(successJson.getString("accuracy")));
					if(presentLoc != null){
						ARActivity.setJsonArray(places);
						ARActivity.setCurrentLocation(presentLoc);
						Intent intent  = new Intent(activity, ARActivity.class);
						intent.putExtra("theme", theme);
						activity.startActivity(intent);
					}else{
						ApzLogger.e(TAG,"APZ-CNT-092");
						ApzPluginUtil.sendError(callerId, "APZ-CNT-092", null, mActivity, mWebview, true);
					}
				}catch(Exception e){
					ApzPluginUtil.sendError(callerId, "APZ-CNT-092", null, mActivity, mWebview, true);
				}

			}

			@Override
			public void onLocationFailure(String error) {
				ApzPluginUtil.sendError(callerId, "APZ-CNT-092", null, mActivity, mWebview, true);
			}
		});

	}

	public static void augRealityThresholdReached(double latitude,double longitude) {
		final JSONObject json = new JSONObject();
		try {
			json.put("event", "AUGREALITY");
			json.put("longitude", longitude+"");
			json.put("latitude", latitude+"");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callerId, "APZ-CNT-077", null, mActivity, mWebview, true);
		}
		ApzPluginUtil.sendSuccess(callerId, json, true, mActivity, mWebview, true);
	}

	public void reloadAugReality(JSONObject jsonObj) {
		JSONArray jsonarr = null;
		try {
			jsonarr = jsonObj.getJSONArray("Places");
		} catch (JSONException e) {

		}
		OverlayView.refreshPlace(jsonarr);
	}

	@Override
	public void execute(JSONObject params) {
		try {
			callerId = params.getString("id");
			action = params.getString("action");
			mParams = params;
			if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
				if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
						!= PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
						!= PackageManager.PERMISSION_GRANTED) {
					permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION };
					requestForPermission();
				} else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED) {
					permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
					requestForPermission();
				} else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
						!= PackageManager.PERMISSION_GRANTED) {
					permissions = new String[]{
							Manifest.permission.ACCESS_FINE_LOCATION};
					requestForPermission();
				} else {
					callARP();
				}
			} else {
				callARP();
			}
		} catch (JSONException e) {
		}

	}

	private void callARP(){
		if(action.equals("START")){
			startAugReality(mParams);
		}
	}

	private void requestForPermission() {
		this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_CAMERA, new OnPermissionsResultHandler() {
					@Override
					public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
						if (requestCode == ApzPlugin.APZ_REQ_CAMERA) {
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
								callARP();
							}
						} else {
							PermissionDeniedCallback();
						}

					}
				}
		);
	}

	private void displayReconfirmationMessage() {
		String message = "To open augmented reality,allow app to access by granting requested permissions";

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
		ApzPluginUtil.sendPermissionDenied("Augmented Reality",callerId, this.activity,this.webView);
	}

	public static boolean isPlugin() {
		return true;
	}

	public static void setCurrentLocation(Location location) {
		ARActivity.setCurrentLocation(location);

	}


}

