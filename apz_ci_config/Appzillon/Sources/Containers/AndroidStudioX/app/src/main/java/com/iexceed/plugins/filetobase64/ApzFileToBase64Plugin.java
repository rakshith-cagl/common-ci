package com.iexceed.plugins.filetobase64;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzFileToBase64Plugin extends ApzPlugin {

	private static ApzPlugin pluginObj;
	private static JSONObject params;
	private String[] permissions;
	private ApzFileToBase64Plugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){
		pluginObj = new ApzFileToBase64Plugin(webView, activity);
	}
		return pluginObj;
	}

	@Override
	public void execute(JSONObject params) {
		try {
			this.callbackId = params.getString("id");
			this.params = params;
			boolean conditionCheck = false;
			if(params.has("filePath") && !params.getString("filePath").startsWith(AppzillonMainScreen.internalPathCheck)){
				conditionCheck = true;
			}
			if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
					&& "N".equalsIgnoreCase(activity.getResources().getString(R.string.INTERNALSANDBOX)))
					|| conditionCheck) {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ) {
					if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
							!= PackageManager.PERMISSION_GRANTED) {
						permissions = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE};
						requestForPermission();
					} else {
						convertFiletoBase64();
					}
				} else {
					if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
							!= PackageManager.PERMISSION_GRANTED
							|| ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
							!= PackageManager.PERMISSION_GRANTED
							|| ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO)
							!= PackageManager.PERMISSION_GRANTED) {
						permissions = new String[]{Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_MEDIA_IMAGES};
						requestForPermission();
					} else {
						convertFiletoBase64();
					}
				}

			}
			else {
				convertFiletoBase64();
			}
		} catch (Exception e) {
			ApzLogger.e(TAG,e.toString());
		}

	}

	private void requestForPermission() {
		this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_WRITE_STORAGE, new OnPermissionsResultHandler() {
					@Override
					public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
						if (requestCode == ApzPlugin.APZ_REQ_WRITE_STORAGE) {
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
								convertFiletoBase64();
							}
						} else {
							PermissionDeniedCallback();
						}

					}
				}
		);
	}
	private void displayReconfirmationMessage() {
		String message = "To access files ,allow app to access by granting requested permissions";
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
		ApzPluginUtil.sendPermissionDenied("File access",this.callbackId, this.activity,this.webView);
	}

	private void convertFiletoBase64(){
		FileToBase64.convertFileToBase64(this.callbackId, this.webView, this.activity,params);
	}

}
