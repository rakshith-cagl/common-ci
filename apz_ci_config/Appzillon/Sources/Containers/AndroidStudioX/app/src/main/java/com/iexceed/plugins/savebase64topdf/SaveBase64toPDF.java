package com.iexceed.plugins.savebase64topdf;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.WebView;

public class SaveBase64toPDF {
	
	public static void convertBase64toPDF(WebView webView , Activity activity , JSONObject jsonObj){
		String base64 = null;
		String fileName;
		String filePath;
		File finalPath;
		String callbackId = null;
		boolean saveInDownloads = false;
		try {
			base64 = jsonObj.getString("base64");
			fileName = jsonObj.getString("fileName");
			filePath = jsonObj.getString("filePath");
			callbackId = jsonObj.getString("id");
			saveInDownloads = jsonObj.optBoolean("saveInDownloads");
			if(!(filePath.trim().length()>0)){
				filePath = "downloads";
			}
		} catch (final JSONException e) {
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity,
					webView, true);
			Base64toPDFPlugin.pluginObj = null;
			return;
		}
		
		if(saveInDownloads){
			try{
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
					ContentResolver resolver = activity.getApplicationContext().getContentResolver();

					ContentValues contentValues = new ContentValues();
					contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
					if(fileName.contains(".png"))
					{
						contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
					}
					if(fileName.contains(".pdf"))
					{
						contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
					}
					
					contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
					Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
					String externalFile =getDataColumn(activity.getApplicationContext(),uri,null,null);
					try {

						OutputStream fos = resolver.openOutputStream(uri);
						BufferedOutputStream buf = new BufferedOutputStream(fos);
						byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
						try {
							fos.write(decodedString);
						} catch (IOException e) {
							ApzPluginUtil.sendError(callbackId, e.getLocalizedMessage(), null, activity, webView, true);
							Base64toPDFPlugin.pluginObj = null;
							return;
						}
						fos.flush();
						fos.close();

					} catch (Exception e) {
						ApzPluginUtil.sendError(callbackId, e.getLocalizedMessage(), null, activity, webView, true);
					}
					try {
						JSONObject json = new JSONObject();
						json.put("filePath", externalFile);
						ApzPluginUtil.sendSuccess(callbackId, json, false, activity, webView, true);
						Base64toPDFPlugin.pluginObj = null;
					} catch (Exception e) { }
				}else{
					FileOutputStream fos = null;
					try {
						String FILE_DIR = activity.getExternalFilesDir(null).getAbsolutePath();
						FILE_DIR = FILE_DIR.substring(0, FILE_DIR.lastIndexOf("Android"));      //gives storage/emulated/0/
						String fullPath = FILE_DIR+Environment.DIRECTORY_DOWNLOADS;
						
						File file = new File(fullPath);
						if (!file.exists()) {
							file.mkdirs();
						}
						finalPath = new File(fullPath + File.separator + fileName);

						fos = new FileOutputStream(finalPath);
						BufferedOutputStream buf = new BufferedOutputStream(fos);
						byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);

						try {
							fos.write(decodedString);
						} catch (IOException e) {
							ApzPluginUtil.sendError(callbackId, "APZ-CNT-127", null, activity, webView, true);
							Base64toPDFPlugin.pluginObj = null;
							return;
						}
						fos.close();
						try {
							JSONObject json = new JSONObject();
							json.put("filePath", finalPath.getPath());
							ApzPluginUtil.sendSuccess(callbackId, json, false, activity, webView, true);
							Base64toPDFPlugin.pluginObj = null;
						} catch (Exception e) { }
					} catch (FileNotFoundException e) {
						ApzPluginUtil.sendError(callbackId, "APZ-CNT-002", null, activity, webView, true);
						Base64toPDFPlugin.pluginObj = null;
						return;
					}
				}
			}catch(Exception e){
				ApzLogger.e("SaveBase64toPDF", e.toString());
				ApzPluginUtil.sendError(callbackId, e.getLocalizedMessage(), null, activity, webView, true);
				Base64toPDFPlugin.pluginObj = null;
				return;
			}
		}else{
			FileOutputStream fos = null;
			try {
				String fullPath = AppzillonMainScreen.SANDBOX_LOC +File.separator+AppzillonMainScreen.ASSET_APP_LOC+filePath;
				File file = AppzillonUtils.getApzFile(fullPath,null);
				if (!file.exists()) {
					file.mkdirs();
				}
				finalPath = AppzillonUtils.getApzFile(fullPath+File.separator+fileName,null);

				fos = new FileOutputStream(finalPath);
				BufferedOutputStream buf = new BufferedOutputStream(fos);
				byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
				try {
					fos.write(decodedString);
				} catch (IOException e) {
					ApzPluginUtil.sendError(callbackId, "APZ-CNT-127", null, activity, webView, true);
					Base64toPDFPlugin.pluginObj = null;
					return;
				}
			} catch (FileNotFoundException e) {
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-002", null, activity, webView, true);
				Base64toPDFPlugin.pluginObj = null;
				return;
			}
			try {
				fos.close();
				try {
					JSONObject json = new JSONObject();
					json.put("filePath", finalPath.getPath());
					ApzPluginUtil.sendSuccess(callbackId, json, false, activity, webView, true);
					Base64toPDFPlugin.pluginObj = null;
				} catch (Exception e) { }
			} catch (IOException e) {
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-127", null, activity,webView, true);
				Base64toPDFPlugin.pluginObj = null;
				return;
			}
		}
		
	}
	
	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection,selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
}