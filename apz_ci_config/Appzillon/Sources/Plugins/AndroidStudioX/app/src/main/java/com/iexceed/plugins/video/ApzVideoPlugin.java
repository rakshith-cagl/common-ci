package com.iexceed.plugins.video;

import org.json.JSONException;
import org.json.JSONObject;

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

import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.appzillonapp.R;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzVideoPlugin extends ApzPlugin{
	
	public static Activity mActivity;
	public static WebView mWebview;
	private JSONObject jsonObject;
	private String mCallbackId;
	private String[] permisisons;
	
	private static ApzPlugin pluginObj;
	public ApzVideoPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
		mActivity = activity;
		mWebview = webView;
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){ 
		pluginObj = new ApzVideoPlugin(webView, activity); 
	}  
	return pluginObj; 
	}

	@Override
	public void execute(JSONObject params) {

		try {
			mCallbackId = params.getString("id");
			jsonObject = params;

			if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )) {
				// Android 13 storage change
				if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
						!= PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
						!= PackageManager.PERMISSION_GRANTED) {
					permisisons = new String[]{
							Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};
					requestForPermission();
				} else {
					startVideoRecording();
				}

			}else {
				startVideoRecording();
			}
		} catch (JSONException e) {
			ApzLogger.i("ApzVideoPlugin",e.toString());
			ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-077", null, mActivity, mWebview, true);
		}

	}

	private void startVideoRecording(){
		String fileName = null;
		String overwrite = null;
		try{
			fileName = jsonObject.getString("fileName");
			overwrite = jsonObject.getString("overwrite");
			if (fileName.equals("")) {
				ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-169", null, mActivity, mWebview, true);
			}
		}catch (Exception e){

		}

		final Intent intent = new Intent(mActivity.getApplicationContext(),
				VideoCaptureActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("fileName", fileName);
		intent.putExtra("overwrite", overwrite);
		intent.putExtra("id", mCallbackId);
		activity.startActivity(intent);
	}

	private void requestForPermission() {
		this.activity.startOnPermissionForResult(activity, permisisons, ApzPlugin.APZ_REQ_CAMERA, new OnPermissionsResultHandler() {
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
								startVideoRecording();
							}
						} else {
							PermissionDeniedCallback();
						}

					}
				}
		);
	}

	private void displayReconfirmationMessage() {
		String message = "To capture image/select image from gallery, allow app to access by requested permissions";

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
		ApzPluginUtil.sendPermissionDenied("Camera or Storage ",mCallbackId, this.activity,this.webView);
	}
	
	public static boolean isPlugin(){
		return true;
	}

}

