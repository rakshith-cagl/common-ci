package com.scanlibrary;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
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

public class ApzDocScannerPlugin extends ApzPlugin {

	private final String TAG = "ApzDocScannerPlugin";
	private static ApzPlugin pluginObj;
	public static final int SCAN_DOCUMENT = 23;
	String ocr;
	String callBackId;
	JSONObject jsonObject;
	public static boolean OCR = false;
	private String[] permisisons;

	private ApzDocScannerPlugin(WebView wv, ApzActivity act) {
		super(wv, act);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new ApzDocScannerPlugin(webView, activity);
		}
		return pluginObj;
	}

	@Override
	public void execute(JSONObject params) {

		try {
			ocr = params.getString("OCR");
			callBackId = params.getString("id");
			jsonObject = params;
			if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )) {
				/*if ((ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
						!= PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED)) {
					permisisons = new String[]{
							Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE};
					requestForPermission();
				} else */

				// Android 13 storage change
				if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
						!= PackageManager.PERMISSION_GRANTED ) {
					permisisons = new String[]{
							Manifest.permission.CAMERA};
					requestForPermission();
				}
				else {
					callDocScanner();
				}

			} else {
				callDocScanner();
			}
//			ApzLogger.e(TAG, "Plugin Payload "+ params);
		} catch (Exception e) {
			ApzLogger.e(TAG, e.getMessage());
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-211", null, activity, webView, true);
		}

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
								callDocScanner();
							}
						} else {
							PermissionDeniedCallback();
						}

					}
				}
		);
	}

	private void displayReconfirmationMessage() {
		String message = "To scan image from gallery, allow app to access by requested permissions";

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
		ApzPluginUtil.sendPermissionDenied("Camera or Storage ",callBackId, this.activity,this.webView);
	}

	private void callDocScanner(){
		if("Y".equalsIgnoreCase(ocr)){
			OCR = true;
		}else{
			OCR = false;
		}
	//	if("N".equalsIgnoreCase(ocr)){
			ScanActivity.setWebView(webView,activity,callBackId);
			Intent intent = new Intent(activity, ScanActivity.class);
			intent.putExtra("json", jsonObject.toString());
			//Natasha's changes 24-7-2017 for App crashing
			/*activity.startActivityForResult(intent, SCAN_DOCUMENT, new ExternalActivityResultHandler() {

				@Override
				public void handleActivityResult(int resultCode, Intent data) {
					// TODO Auto-generated method stub
					JSONObject resultBody = new JSONObject();
					if(resultCode == Activity.RESULT_OK){
						try {
							String encodedImage = data.getStringExtra("encodedImage");
//						Uri uri = Uri.parse(path);
//						Bitmap bmp = ScanActivity.getBitmap(uri);
//						ByteArrayOutputStream outSream = new ByteArrayOutputStream();
//				    	bmp.compress(Bitmap.CompressFormat.PNG, 100, outSream);
//				    	byte[] byteArray = outSream.toByteArray();
//				    	String img = Base64.encodeToString(byteArray, Base64.DEFAULT);
							resultBody.put("encodedImage", encodedImage);
							//to delete files and folders
//						activity.getContentResolver().delete(uri, null, null);
//						deleteRecursive(new File(ScanActivity.IMAGE_PATH));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							
						}
						ApzPluginUtil.sendSuccess(callBackId, resultBody, false, activity, webView, true);
					}else{
						try {
							resultBody.put("text", "Could not scann the document");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							
						}
						ApzPluginUtil.sendError(callBackId, "APZ-CNT-313", resultBody, activity, webView, true);
					}


				}
			});*/
			activity.startActivity(intent);
	/*	} else {
			ApzPluginUtil.sendError(callBackId, "APZ-CNT-022", null, activity, webView, true);
		}*/
	}
	void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            deleteRecursive(child);

	    fileOrDirectory.delete();
	}
	
	public static boolean isDocScannerPlugin() {
		return true;
	}
}

