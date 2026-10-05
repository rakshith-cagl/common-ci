package com.iexceed.plugins.fileoperation;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import androidx.core.content.FileProvider;
import android.webkit.WebView;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.MediaUtils;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.BuildConfig;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileOperation {

	private Activity mActivity;

	private WebView mWebView;

	private String TAG = "FILE OPERATION";

	public String fullContent;

	private String callbackId;

	public FileOperation(Activity activity, WebView webview) {
		mActivity = activity;
		mWebView = webview;
	}
	/**
	 * used to open file present in sandbox
	 */
	public void  openFile(JSONObject fileJson) {
		String directory=null;
		String fileLoc = "";
		try {
			callbackId = fileJson.getString("id");
			directory=fileJson.getString("filePath").trim();
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, mActivity, mWebView, false);
			return;
		}
		if(!MediaUtils.isSDCardPresent()){
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-127", null, mActivity, mWebView, true);//SD Card Unavailable
			return;
		}

		if("".equals(directory))
		{
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-002", null, mActivity, mWebView, true);
		}else{
			if(directory.contains(AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC))
			{
				fileLoc = directory;
			}else{
				fileLoc =  directory;
				//No relative path allowed. AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC+directory;
			}
			File file=new File(fileLoc);
			if(file.exists()){

				try {
					Intent i = new Intent(Intent.ACTION_VIEW,
							FileProvider.getUriForFile(mActivity, BuildConfig.APPLICATION_ID, file));

					i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

					mActivity.startActivity(i);
				}catch (ActivityNotFoundException act){
					ApzLogger.e(TAG,"File not found");
					ApzPluginUtil.sendError(callbackId, "APZ-CNT-275", null, mActivity, mWebView, true);
				}

			}
			else{
				ApzLogger.e(TAG,"File not found");
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-002", null, mActivity, mWebView, true);//File Not found
			}
		}
	}

	/**
	 * creates a file in the sandbox with given details
	 * @param fileJson
	 */
	public void  createFile(JSONObject fileJson) {
		final String words;
		final String fileName;
		final String filePath;

		try {
			callbackId = fileJson.getString("id");
			words=fileJson.getString("fileContent").trim();
			fileName=fileJson.getString("fileName").trim();
			filePath = fileJson.getString("filePath").trim();
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, mActivity, mWebView, false);
			return;
		}
		if(!MediaUtils.isSDCardPresent()){
			ApzLogger.i(TAG,"No SD Card");
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-127", null, mActivity, mWebView, true);
			return;
		}
		if("".equals(words) || "".equals(fileName)){
			//fileOpFailure(mContext.getResources().getString(R.string.failure_msg));
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-169", null, mActivity, mWebView, true);//Failure
			return;
		}
		new Thread(new Runnable(){
			@Override
			public void run() {
				writeToFile();
			}

			private void writeToFile() {
				BufferedWriter writer = null;
				try {
					String fileLoc ;
					if(filePath.length()>1){
						fileLoc = AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC + filePath;
					}else{
						fileLoc = AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC;
					}

//					File file=new File(fileLoc);
					File file = AppzillonUtils.getApzFile(fileLoc,null);
					file.mkdirs();

					final File directory = new File(file, fileName);
					//Abhishek 05 May 2015, File Name will come with extension END

					//This will output the full path where the file will be written to...
//					ApzLogger.i(TAG,directory.getCanonicalPath());
					writer = new BufferedWriter(new FileWriter(directory,false));
					writer.write(words);
					// fileOpSuccess(mContext.getResources().getString(R.string.success_msg));
					JSONObject result = new JSONObject();
					result.put("filePath", directory.getAbsolutePath());
					ApzPluginUtil.sendSuccess(callbackId, result, false, mActivity, mWebView, true);
				} catch (Exception e) {
					ApzLogger.e(TAG,e.toString());

				} finally {
					try {
						// Close the writer regardless of what happens...
						writer.close();
					} catch (Exception e) {
						ApzLogger.e(TAG,e.toString());
					}
				}
			}
		}).start();
	}
	/**
	 * Retrieves the file contents 
	 * @param fileJson
	 */
	public void  getFileContent(JSONObject fileJson) {

		final String fileName;
		final String filePath;
		try {
			callbackId = fileJson.getString("id");
			//fileName=fileJson.getString("fileName").trim();
			filePath=fileJson.getString("filePath").trim();
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, mActivity, mWebView, false);
			return;
		}
		if(!MediaUtils.isSDCardPresent()){
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-127", null, mActivity, mWebView, true);//SD Card unavailable
			//fileOpFailure(mContext.getResources().getString(R.string.sdcard_unavailable));
			return;
		}
			/*if("".equals(fileName)){
				ApzPluginUtil.sendError(callbackId, "", null, mActivity, mWebView, true);
				//fileOpFailure(mContext.getResources().getString(R.string.failure_msg));
				return;
			}*/
		new Thread(new Runnable(){
			@Override
			public void run() {

				readFromFile();

			}
			private void readFromFile() {
				BufferedReader reader = null;

				try {
					String fileLoc = null;
					if(!("".equals(filePath))){
						if(filePath.contains(AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC )){
							fileLoc = filePath;
						}else{
							fileLoc=AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC +filePath;
						}}
//					File file=new File(fileLoc);
					File file = AppzillonUtils.getApzFile(fileLoc,null);
					//final File directory = new File(file, fileName);
					if(!file.exists()){
						//fileOpFailure(mContext.getResources().getString(R.string.file_notfound));
						ApzPluginUtil.sendError(callbackId, "APZ-CNT-002", null, mActivity, mWebView, true);//File not found
						return;
					}
					//This will output the full path where the file will be written to...
//					ApzLogger.i(TAG,file.getCanonicalPath());
					reader = new BufferedReader(new FileReader(file));
					final StringBuilder fileContent=new StringBuilder();
					try {
						fullContent = getStringFromFile(fileLoc);
//						ApzLogger.i("filecontent", " file content is "+fullContent);
					} catch (Exception e) {
						ApzLogger.i("FileContent", "exception is in here in appending"+e.toString());
					}
					JSONObject result = null;
					try{
						result = new JSONObject();
						result.put("content", fullContent.trim());
					}catch(JSONException ex){
						ApzLogger.e(TAG,ex.toString());
					}
					ApzPluginUtil.sendSuccess(callbackId, result, false, mActivity, mWebView, true);

				} catch (Exception e) {
					ApzLogger.i("fileRead", "Exception in reading the file" +e.toString());
				} finally {
					try {
						// Close the writer regardless of what happens...
						reader.close();
					} catch (Exception e) {
						ApzLogger.e(TAG,e.toString());
					}
				}
			}
		}).start();
	}
	/**/
	public void  deleteFile(JSONObject fileJson) {
		//	final String fileName;
		final String filePath;
		try {
			callbackId = fileJson.getString("id");
			//fileName=fileJson.getString("fileName").trim();
			filePath=fileJson.getString("filePath").trim();

		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, mActivity, mWebView, true);
			return;
		}
		if(!MediaUtils.isSDCardPresent()){
			//fileOpFailure(mContext.getResources().getString(R.string.sdcard_unavailable));
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-127", null, mActivity, mWebView, true);//SD Card not available
			return;
		}
			/*if("".equals(fileName)){
				//fileOpFailure(mContext.getResources().getString(R.string.failure_msg));
				ApzPluginUtil.sendError(callbackId, "", null, mActivity, mWebView, true);//Failed

				return;
			}*/
		new Thread(new Runnable(){
			@Override
			public void run() {

				deleteFile();

			}
			private void deleteFile() {
				try {
					String fileLoc = "";
					if(!("".equals(filePath)))
					{
						if(filePath.contains(AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC)){
							fileLoc = filePath;
						}else{
							fileLoc = AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC + filePath;
						}}

//					File directory=new File(fileLoc);
					File directory = AppzillonUtils.getApzFile(fileLoc,null);
					// final File directory = new File(file, fileName);
					if(!directory.exists()){
						//fileOpFailure(mContext.getResources().getString(R.string.file_notfound));
						ApzPluginUtil.sendError(callbackId, "APZ-CNT-002", null, mActivity, mWebView, true);//SD Card not available

						return;
					}
					else{
//						new File(directory.getAbsolutePath()).mkdir();
						AppzillonUtils.getApzFile(directory.getAbsolutePath(),null).mkdir();
						if(directory.delete()){
							JSONObject result;
							result = new JSONObject();
							result.put("successMessage", "Success");
							ApzPluginUtil.sendSuccess(callbackId, result, false, mActivity, mWebView, true);
						}
						else{
							ApzPluginUtil.sendError(callbackId, "APZ-CNT-075", null, mActivity, mWebView, true);
							//fileOpFailure(mContext.getResources().getString(R.string.failure_msg));
						}
					}

				} catch (Exception e) {
					ApzLogger.e(TAG,e.toString());
					ApzPluginUtil.sendError(callbackId, "APZ-CNT-075", null, mActivity, mWebView, true);
					//	fileOpFailure(mContext.getResources().getString(R.string.failure_msg));
				}
			}
		}).start();
	}

	public static boolean isFileOperation() {
		return true;
	}


	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

	public static String getStringFromFile (String filePath) throws Exception {
//		File fl = new File(filePath);
		File fl = AppzillonUtils.getApzFile(filePath,null);
		FileInputStream fin = new FileInputStream(fl);
		String ret = convertStreamToString(fin);
		//Make sure you close all streams.
		fin.close();
		return ret;
	}
}
