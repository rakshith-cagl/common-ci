package com.iexceed.plugins.fileoperation;

//import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.MediaUtils;
import com.iexceed.common.ServerUtilities;
import com.iexceed.common.StringUtils;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPluginUtil;
import android.content.ContentValues;
import android.net.Uri;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.UnsupportedEncodingException;
//import java.net.SocketTimeoutException;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.conn.ConnectTimeoutException;
//import org.apache.http.conn.HttpHostConnectException;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;
//import android.os.Environment;
import com.iexceed.plugins.errorlog.ApzLogger;

public class FileDownload {
	
	private Context mContext;
	
	private Activity mActivity;
	
	private WebView mWebView;
	
	private String mServerStatus;
	
	private String mServerErrorReason = null;

	private String TAG = "DOWNLOAD";
	
	private String base64;
	
	private String downloadExternalPath;
	
	private String callerId;
	
	private String downloadReqDetails;
	public FileDownload(Context context, Activity activity, WebView webview) {
		mContext = context;
		mActivity = activity;
		mWebView = webview;
	}

	public void downloadFile(JSONObject jsonObj) {
//		AuditLog.makeString("DOWNLOAD","dowload file");
		String destinationPath;
		String isSession;

		try {
			JSONObject downloadReqDetailsObj = jsonObj.getJSONObject("downloadReqDetails");
			downloadReqDetails = downloadReqDetailsObj.toString();
			callerId = jsonObj.getString("id");
			destinationPath = jsonObj.getString("destinationPath");
			base64 = jsonObj.getString("base64");
			isSession = jsonObj.getString("sessionReq");
			downloadExternalPath = jsonObj.optString("downloadExternalPath");
			
			if (destinationPath.equals("")) {
				//Abhishek 20 April 2015 Updated path to specific app sandbox START
				if(downloadExternalPath.equalsIgnoreCase("N") || downloadExternalPath.equalsIgnoreCase("") ){
				    destinationPath = AppzillonMainScreen.SANDBOX_LOC +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"/documents/downloads";
				}else if(downloadExternalPath.equalsIgnoreCase("Y")){
					destinationPath = "";
				}else{
					destinationPath = AppzillonMainScreen.SANDBOX_LOC +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"/documents/downloads";
				}//Abhishek 20 April 2015 Updated path to specific app sandbox END
//				destinationPath = AppzillonMainScreen.SANDBOX_LOC + "/documents/downloads";
			}
		} catch (JSONException e1) {
			ApzLogger.e(TAG,e1.toString());
			downloadFailure(mContext.getResources().getString(R.string.download_json_error));
			return;
		}
		try {
			if (MediaUtils.checkInternetConnection(mContext)) {
				if (MediaUtils.isSDCardPresent()) {}
				else {
					downloadFailure(mContext.getResources().getString(R.string.sdcard_unavailable));
					return;
				}
			} else {
				downloadFailure(mContext.getResources().getString(R.string.internet_connection_error));
				return;
			}
			JSONObject response = ServerUtilities.sendRequestToServer(StringUtils.getString(StringUtils.SERVER_URL), downloadReqDetails);
			if(response != null){
				mServerStatus = response.toString();
			}else{
				mServerStatus = "error";
				mServerErrorReason = "Server Error.";
			}
//			final int timeOut = 20000;
//			final HttpParams httpParameters = new BasicHttpParams();
//			HttpConnectionParams.setConnectionTimeout(httpParameters, timeOut);
//			HttpConnectionParams.setSoTimeout(httpParameters, timeOut);
//			final HttpClient httpclient = new DefaultHttpClient(httpParameters);
//			final HttpPost httppost = new HttpPost(AppzillonMainScreen.stringUtils.getString(StringUtils.SERVER_URL));
//			httppost.setHeader("Accept", "application/json");
//			StringEntity reqentity;
//			reqentity = new StringEntity(downloadReqDetails);
//			httppost.setEntity(reqentity);
//			HttpResponse response;
//			response = httpclient.execute(httppost);
//			final HttpEntity entity = response.getEntity();
//			if (entity != null) {
//				final InputStream instream = entity.getContent();
//				final String result = convertStreamToString(instream);
//				Log.i(TAG,"Response:::" + result);
//				mServerStatus = result;
//			} else {
//				mServerStatus = "error";
//			}
		} 
//		catch (UnsupportedEncodingException e) {
//			
//			mServerStatus = "error";
//		}
//		catch (ClientProtocolException e) {
//			
//			mServerStatus = "error";
//		}
//		catch (SocketTimeoutException e) {
//			
//			mServerStatus = "error";
//		}
//		catch (ConnectTimeoutException e) {
//			
//			mServerStatus = "error";
//		}
//		catch (HttpHostConnectException e) {
//			
//			mServerStatus = "error";
//		}
//		catch (IOException e) {
//			
//			mServerStatus = "error";
//		}
		catch (IllegalArgumentException e){
			ApzLogger.e(TAG,e.toString());
			mServerStatus = "error";
			mServerErrorReason = "Illegal Argument Exception.";
		}catch (IllegalStateException e){
			ApzLogger.e(TAG,e.toString());
			mServerStatus = "error";
			mServerErrorReason = "Illegal State Exception.";
		}
		 finally {
         if ("error".equals(mServerStatus) || mServerStatus == null) {
            if(mServerErrorReason != null)
               downloadFailure(mContext.getResources().getString(R.string.download_error));
            else
               downloadFailure(mServerErrorReason);
         } else {
            // download Success
            JSONObject jsonResult = null;
            FileOutputStream output = null;
            try {
               jsonResult = new JSONObject(mServerStatus);
               final JSONObject resultBody = jsonResult.getJSONObject("appzillonBody");
               final JSONObject resultHeader = jsonResult.getJSONObject("appzillonHeader");
               if (resultHeader.getBoolean("status")) {
                  JSONObject jsonBodyResult;
                  if (isSession.equalsIgnoreCase("Y"))
                     jsonBodyResult = resultBody.getJSONObject("appzillonFilePushServiceResponse");
                  else
                     jsonBodyResult = resultBody.getJSONObject("appzillonFilePushServiceWSResponse");
                  if (!"".equals(jsonBodyResult.getString("file"))) {
                     // file download success
                     if (base64.equalsIgnoreCase("Y")) {
                        String resultBase64String = jsonBodyResult.getString("file");
                        downloadSuccess(resultBase64String);
                     } else {
                        String fileName = jsonBodyResult.getString("fileName");
                        //Abhishek 07 Sept 2015, Updated as per received path START
                        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                        //Abhishek 07 Sept 2015, Updated as per received path END
                        String outFileName = null;
						 String finalPath = null;
//Abhishek 16 September 2015, Change save location in sandbox as per API manual START
//                   if(destinationPath.contains(Environment.getExternalStorageDirectory().getAbsolutePath())){
//                      outFileName = destinationPath;
//                   }else{
//                      outFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + destinationPath;
//                   }
                        if (destinationPath.contains(AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC)) {
                           outFileName = destinationPath;
                        } else {
							if (downloadExternalPath.equalsIgnoreCase("N") || downloadExternalPath.equalsIgnoreCase("")) {
								outFileName = AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + File.separator + destinationPath;
							byte [] bytePath=Base64.decode(jsonBodyResult.getString("file"),Base64.NO_WRAP);
								downloadFile(outFileName,fileName,bytePath);
							}
							if (downloadExternalPath.equalsIgnoreCase("Y")) {
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
									ContentResolver resolver = mActivity.getApplicationContext().getContentResolver();
									File file = new File(fileName);
									String mimeType = URLConnection.guessContentTypeFromName(file.getName());
									Log.d("MIME_TYPE",mimeType);
									ContentValues contentValues = new ContentValues();
									contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
									contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
									contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
									Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
									 finalPath =getDataColumn(mActivity.getApplicationContext(),uri,null,null);
									try {

										OutputStream fos = resolver.openOutputStream(uri);
										BufferedOutputStream buf = new BufferedOutputStream(fos);
										byte[] decodedString = Base64.decode(jsonBodyResult.getString("file"), Base64.DEFAULT);
										try {
											fos.write(decodedString);
										} catch (IOException e) {

										}
										fos.flush();
										fos.close();
										downloadSuccess(finalPath);

									} catch (Exception e) {

									}

								}else{
									try {
										String FILE_DIR = mActivity.getExternalFilesDir(null).getAbsolutePath();
										FILE_DIR = FILE_DIR.substring(0, FILE_DIR.lastIndexOf("Android"));      //gives storage/emulated/0/
										outFileName = FILE_DIR + Environment.DIRECTORY_DOWNLOADS;
										File file = new File(fileName);
										String mimeType = URLConnection.guessContentTypeFromName(file.getName());
										downloadFile(outFileName,fileName,Base64.decode(jsonBodyResult.getString("file"),Base64.NO_WRAP));

									} catch (Exception e) {
									}

								}
						}


                        }



                     }
                  }
               } else{
                  ApzLogger.e(TAG, "Failed to donwload the file");
                  JSONArray error = jsonResult.getJSONArray(AppzillonMainScreen.APPZILLON_ERRORS);
                  JSONObject errObj = error.getJSONObject(0);
                  downloadFailure(errObj);
               }
            } catch (JSONException jex) {
               ApzLogger.e(TAG,jex.toString());
               downloadFailure(mContext.getResources().getString(R.string.download_json_error));
            } catch (Exception ex) {
               ApzLogger.e(TAG,ex.toString());
               downloadFailure(mContext.getResources().getString(R.string.download_error));
            }
            finally {
               if (output != null) {
                  try {
                     output.close();
                  } catch (IOException e) {
                     ApzLogger.e(TAG,e.toString());
                  }
               }
            }
         }
      }

	}
	private void downloadFile(String outFileName, String fileName, byte[] base64){
		String fPath=null;
		try {
	//	File fileLoc = AppzillonUtils.getApzFile(outFileName,null);
	//	if(!fileLoc.exists())
	//		fileLoc.mkdirs();
		 fPath = outFileName+ "/" + fileName;
		FileOutputStream output = new FileOutputStream(AppzillonUtils.validatePath(fPath,null));
			output.write(base64);
		} catch (IOException e) {
			e.printStackTrace();
		}
		downloadSuccess(fPath);

	}


//	private static String convertStreamToString(InputStream is) {
////		AuditLog.makeString("DOWNLOAD","convert stream to String");
//		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//		final StringBuilder sb = new StringBuilder();
//		String line = null;
//		try {
//			while ((line = reader.readLine()) != null) {
//				sb.append(line + "\n");
//			}
//		} catch (IOException e) {
//			
//		}
//		finally {
//			try {
//				is.close();
//			} catch (IOException e) {
//				
//			}
//		}
//		return sb.toString();
//	}

	

	private void downloadSuccess(final String path) {
//		AuditLog.makeString("DOWNLOAD","Success");
//		Log.i(TAG, "Success : "+path);
		final JSONObject fileDownloadRes = new JSONObject();
		try {
			if(base64.equalsIgnoreCase("Y")){
				fileDownloadRes.put("filePath", "");
				fileDownloadRes.put("base64", path);
			}else{
				fileDownloadRes.put("filePath", path);
				fileDownloadRes.put("base64", "");
			}
			
		} catch (final JSONException ex) {
			ApzLogger.e(TAG,ex.toString());
			return;
		}
		
		ApzPluginUtil.sendSuccess(callerId, fileDownloadRes, false, mActivity, mWebView,
				true);
	}

	private void downloadFailure(final String errorMsg) {
		JSONObject json = new JSONObject();
		try {
			json.put("errorMessage", errorMsg);
		} catch (JSONException e) {
		}
		ApzPluginUtil.sendError(callerId, "APZ-CNT-079", json, mActivity,
				mWebView, true);
	}
	
	private void downloadFailure(final JSONObject errorBody) {

		ApzPluginUtil.sendError(callerId, "APZ-CNT-079", errorBody, mActivity,
				mWebView, true);
	
		
	}

	public static boolean isFileDownload() {
		return true;
	}

	public static String getDataColumn(Context context, Uri uri,
                           String selection, String[] selectionArgs) {
   Cursor cursor = null;
   final String column = "_data";
   final String[] projection = { column };
   try {
      cursor = context.getContentResolver().query(uri, projection,
            selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
         final int column_index = cursor
               .getColumnIndexOrThrow(column);
         return cursor.getString(column_index);
      }
   } finally {
      if (cursor != null)
         cursor.close();
   }
   return null;
}
}
