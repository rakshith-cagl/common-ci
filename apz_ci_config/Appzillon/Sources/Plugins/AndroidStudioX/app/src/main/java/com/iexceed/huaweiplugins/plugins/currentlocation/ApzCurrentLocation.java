package com.iexceed.plugins.currentlocation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.webkit.WebView;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class ApzCurrentLocation extends ApzPlugin {

	private static ApzPlugin pluginObj;
	private JSONObject mJsonObj;
	public static boolean hasLocationPermission;
	private String[] permissions;

	FusedLocationProviderClient fusedLocationProviderClient;

	private ApzCurrentLocation(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){
		pluginObj = new ApzCurrentLocation(webView, activity);
	}
		return pluginObj;
	}

	private void fetchCurrentLocation(){

		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
		SettingsClient settingsClient = LocationServices.getSettingsClient(activity);

		LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
		LocationRequest mLocationRequest = new LocationRequest();
		builder.addLocationRequest(mLocationRequest);
		LocationSettingsRequest locationSettingsRequest = builder.build();

		//check Location Settings
		settingsClient.checkLocationSettings(locationSettingsRequest)
				.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
					@Override
					public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
						//Have permissions， send requests
						Task<Location> task = fusedLocationProviderClient.getLastLocation()
								.addOnSuccessListener(new OnSuccessListener<Location>() {
									@Override
									public void onSuccess(Location location) {
										if (location == null) {
											try{
												JSONObject eJson = new JSONObject();
												eJson.put("errorCode", "");
												eJson.put("errorMessage", "No providers available to fetch location");
												ApzPluginUtil.sendError(callbackId, "APZ-CNT-274", eJson, activity, webView, true);
											}catch(JSONException je){ }
										}else{

											try {
												JSONObject successJson = new JSONObject();
												String latitude = Double.toString(location.getLatitude());
												String longitude = Double.toString(location.getLongitude());
												successJson.put("latitude", latitude);
												successJson.put("longitude", longitude);
												successJson.put("accuracy",Double.toString(location.getAccuracy()));

												ApzPluginUtil.sendSuccess(callbackId, successJson, false, activity, webView, true);
											} catch (Exception e) { }

										}
									}
								})
								.addOnFailureListener(new OnFailureListener() {
									@Override
									public void onFailure(Exception e) {
										//Exception handling logic.
										try{
											JSONObject eJson = new JSONObject();
											eJson.put("errorCode", "");
											eJson.put("errorMessage", e.getLocalizedMessage());
											ApzPluginUtil.sendError(callbackId, "APZ-CNT-274", eJson, activity, webView, true);
										}catch(JSONException je){ }
									}
								});
					}
				})
				.addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(Exception e) {
						//Settings do not meet targeting criteria
						int statusCode = ((ApiException) e).getStatusCode();
						switch (statusCode) {
							case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
								try {
									ResolvableApiException rae = (ResolvableApiException) e;
									//Calling startResolutionForResult can pop up a window to prompt the user to open the corresponding permissions
									rae.startResolutionForResult(activity, 0);
								} catch (IntentSender.SendIntentException sie) {
									try{
										JSONObject eJson = new JSONObject();
										eJson.put("errorCode", "");
										eJson.put("errorMessage", sie.getLocalizedMessage());
										ApzPluginUtil.sendError(callbackId, "APZ-CNT-274", eJson, activity, webView, true);
									}catch(JSONException je){ }
								}
								break;
						}
					}
				});
	}


	@Override
	public void execute(JSONObject params) {
		try {
			this.callbackId = params.getString("id");
			this.mJsonObj = params;
			if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)) {
				if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
						|| ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
					permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
					requestForPermission();
				} else {
					fetchCurrentLocation();
				}
			} else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P){

				if (ActivityCompat.checkSelfPermission(activity,
						Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
						|| ActivityCompat.checkSelfPermission(activity,
						Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
						|| ActivityCompat.checkSelfPermission(activity,
						"android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED){
					permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
							Manifest.permission.ACCESS_COARSE_LOCATION,
							"android.permission.ACCESS_BACKGROUND_LOCATION"};
					requestForPermission();
				}else{
					fetchCurrentLocation();
				}
			}
			else {
				fetchCurrentLocation();
			}

		} catch (Exception e) {
			//ApzLogger.e(TAG,e.toString());
		}

	}

	private void requestForPermission() {
		this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_LOCATION, new OnPermissionsResultHandler() {
					@Override
					public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
						if (requestCode == ApzPlugin.APZ_REQ_LOCATION) {
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
								fetchCurrentLocation();
								hasLocationPermission = true;
							}
						} else {
							PermissionDeniedCallback();
						}

					}
				}
		);
	}

	private void displayReconfirmationMessage() {
		String message = "To access location ,allow app to access by granting requested permissions";
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
		ApzPluginUtil.sendPermissionDenied("Location",callbackId, this.activity,this.webView);
	}
}
