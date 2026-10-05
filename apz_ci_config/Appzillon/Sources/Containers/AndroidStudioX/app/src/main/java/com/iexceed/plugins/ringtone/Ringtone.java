package com.iexceed.plugins.ringtone;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.ExternalActivityResultHandler;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class Ringtone extends ApzPlugin {
	
	private static ApzPlugin pluginObj;
	private JSONObject mParams;
	private String mCallbackId;
	private int CODE_WRITE_SETTINGS_PERMISSION = 1;
	private Ringtone(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){ 
		pluginObj = new Ringtone(webView, activity); 
	}  
	return pluginObj; 
	}

	@Override
	public void execute(JSONObject params) {
		try {
			mParams = params;
			callbackId = params.getString("id");
			mCallbackId = callbackId;
			if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)){
			if(checkWritePermission()){
				//proceedPlugin();
				proceedSetRingtone();
			}else{
				Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
				intent.setData(Uri.parse("package:" + activity.getPackageName()));
				activity.startActivityForResult(intent,
						CODE_WRITE_SETTINGS_PERMISSION, new ExternalActivityResultHandler() {

							@Override
							public void handleActivityResult(
									int resultCode, Intent data) {

								//if (resultCode == activity.RESULT_OK) {
								if(checkWritePermission()) {
									proceedSetRingtone();
								}else{
									PermissionDeniedCallback();
								}

							/*}else{
								PermissionDeniedCallback();
							}*/

							}

						});

			}}else{
				proceedSetRingtone();
			}

		} catch (JSONException e) {
			
		}

	}



	/*private void proceedPlugin(){
		if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {

			if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_SETTINGS)
					!= PackageManager.PERMISSION_GRANTED ) {
				requestForPermission();
			} else {
				proceedSetRingtone();
			}
		} else {
			proceedSetRingtone();
		}
	}*/

	@TargetApi(23)
	private boolean checkWritePermission(){
		return Settings.System.canWrite(activity);
	}

	private void requestForPermission() {
		activity.startOnPermissionForResult(activity, new String[]{
						Manifest.permission.WRITE_SETTINGS}, ApzPlugin.APZ_REQ_WRITE_SETTINGS, new OnPermissionsResultHandler() {

					@Override
					public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
						if (requestCode == ApzPlugin.APZ_REQ_WRITE_SETTINGS) {
							if (grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
								proceedSetRingtone();
							} else {
								PermissionDeniedCallback();
							}
						}
					}
				}
		);
	}

	private void PermissionDeniedCallback(){
		//Log.i(TAG, "Setting access permissions Denied.");
		JSONObject json = new JSONObject();
		try {
			json.put("text","Setting access permissions Denied.");
		} catch (JSONException e) {
			
		}
		ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-329", json, this.activity,
				this.webView, true);
	}
	private void proceedSetRingtone(){

			String path = "";
			String title = "";
			String dir = this.activity.getExternalFilesDir(null)
					.getAbsolutePath() + "/AppzillonRingtone";

			try {
				path = mParams.getString("filePath");
				title = mParams.getString("title");

			} catch (JSONException e) {
				ApzLogger.e(TAG, e.toString());
				ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-077", null,
						Ringtone.this.activity, Ringtone.this.webView, true);
			}
			if (!path.contains(dir)) {
				path = dir + File.separator + path;
			}
			File fpath = new File(path);
			if (fpath.exists()) {
				ContentValues values = new ContentValues();
				values.put(MediaStore.MediaColumns.DATA, path);
				values.put(MediaStore.MediaColumns.TITLE, title);
				values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
				values.put(MediaStore.Audio.Media.ARTIST, "None");
				values.put(MediaStore.MediaColumns.SIZE, fpath.length());
				values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
				values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
				values.put(MediaStore.Audio.Media.IS_ALARM, false);
				values.put(MediaStore.Audio.Media.IS_MUSIC, false);
				Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
				activity.getContentResolver().delete(uri,
						MediaStore.MediaColumns.DATA + "=\"" + path + "\"", null);
				Uri newUri = activity.getContentResolver().insert(uri, values);

				RingtoneManager.setActualDefaultRingtoneUri(
						activity.getApplicationContext(),
						RingtoneManager.TYPE_RINGTONE, newUri);

				JSONObject sJson = new JSONObject();
				try {
					sJson.put("status", "RingTone Set");
				} catch (JSONException e) {
				}
				ApzPluginUtil.sendSuccess(mCallbackId, sJson, false,
						activity, webView, true);

			} else {
				ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-002", null,
						Ringtone.this.activity, Ringtone.this.webView, true);
			}
		}

}
