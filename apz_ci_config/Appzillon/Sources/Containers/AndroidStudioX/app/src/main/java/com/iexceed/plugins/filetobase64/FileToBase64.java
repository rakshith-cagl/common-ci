package com.iexceed.plugins.filetobase64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.webkit.WebView;

import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class FileToBase64 {
	private static final String TAG = "FileToBase64";

	protected static void convertFileToBase64(String callbackId,WebView webView,Activity activity, JSONObject jsonObj){
		//byte[] bytes;
	     String filePath = null;
		try {
	    	 filePath = jsonObj.getString("filePath");
		} catch (JSONException e1) {
			/*try {
				JSONObject json = new JSONObject();
				json.put("errorCode", "APZ-CNT-077");
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", json,activity,
						webView, true);
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
			}*/
		ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null,activity,
						webView, true);
		}
	     try {
	        /* ByteArrayOutputStream baos = new ByteArrayOutputStream();
	         FileInputStream fis = new FileInputStream(new File(filePath));
	         byte[] buf = new byte[1024];
	         int n;
	         while (-1 != (n = fis.read(buf)))
	             baos.write(buf, 0, n);
	         fis.close();
	         bytes = baos.toByteArray();
	         final String str = Base64.encodeToString(bytes, Base64.DEFAULT);*/
	         
	         InputStream inputStream = null;
	         inputStream = new FileInputStream(new File(filePath).getAbsolutePath());
	         byte[] buffer = new byte[8192];
	         int bytesRead;
	         ByteArrayOutputStream output = new ByteArrayOutputStream();
	         Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
	         try {
	             while ((bytesRead = inputStream.read(buffer)) != -1) {
	                 output64.write(buffer, 0, bytesRead);
	             }
	         } catch (IOException e) {
	             
	         }
	         output64.close();
	         String str = output.toString("UTF-8");
	          
	         final JSONObject returnJson = new JSONObject();
				try {
					returnJson.put("text", str);
				} catch (final JSONException ex) {
					ApzLogger.e(TAG,ex.toString());
					return;
				}
				if(output != null)
				output.close();
				if(inputStream != null)
				inputStream.close();
				
	         ApzPluginUtil.sendSuccess(callbackId, returnJson, false, activity, webView,true);
	    } catch(final IOException io){
	    	try {
				JSONObject json = new JSONObject();
				json.put("errorDescription", io.toString());
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-270", json,activity,
						webView, true);
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
			}
	    } catch (final OutOfMemoryError oom){
	    	try {
				JSONObject json = new JSONObject();
				json.put("errorDescription", oom.toString());
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-270", json,activity,
						webView, true);
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
			}
	    }catch (final Exception e){
	    	try {
				JSONObject json = new JSONObject();
				json.put("errorDescription", e.toString());
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-270", json,activity,
						webView, true);
			} catch (JSONException e1) {
				ApzLogger.e(TAG,e1.toString());
			}
	    }
	}

}
