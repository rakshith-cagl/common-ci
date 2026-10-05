package com.iexceed.plugins.email;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.ExternalActivityResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class EmailPlugin extends ApzPlugin {
	
	private String key;
	
	private int RESULT_LOAD_MAIL = 201;
	
	//private String EMAIL_ID;
	
	private String TAG = "EMAIL";
	
	private static ApzPlugin pluginObj;

	String errorCode = "";
	
	private EmailPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	/**
	 *  Sends email using native application
	 * @param obj
	 */
	  public static boolean IsEmail(){
			return true;
		}
	  
	  public static ApzPlugin createPlugin(WebView webView, ApzActivity activity){
			if(pluginObj == null){
			pluginObj = new EmailPlugin(webView, activity);
			}
		return pluginObj;
		}
	  
	public final void sendmail( JSONObject obj){
		String recipientId=null, emailSub=null, emailBody=null, id=null, senderMailId=null, ccIdList=null, internal=null;
		final String emailId;
		JSONArray filePathArr = new JSONArray();
		int maxFileSize = 0;
		boolean fileStatus = true;
		
		try{
			/**/
			Iterator<String> jsonObjKeys=obj.keys();
	     	while(jsonObjKeys.hasNext()){
	     		key=(String)jsonObjKeys.next();
	     		switch(key){
	     			case "ccIdList":
//	     				ApzLogger.i(TAG,"Key : ccIdList Present");
						ccIdList=obj.getString("ccIdList");
						break;
						
	     			case "senderMailId":
	     				ApzLogger.i(TAG,"Key : senderMailId Present");
						senderMailId=obj.getString("senderMailId");
						break;
						
	     			case "filePaths":
	     				filePathArr = obj.getJSONArray("filePaths");
	     				break;
	     				
	     			case "maxAttachmentSize":
	     				maxFileSize = Integer.parseInt(obj.getString("maxAttachmentSize"));
	     				break;
	     				
	     		}
				/*if(key.equals("ccIdList")){
					ApzLogger.i(TAG,"Key : ccIdList Present");
					ccIdList=obj.getString("ccIdList");
				}*/
				/*elseif(key.equals("senderMailId")){
					ApzLogger.i(TAG,"Key : senderMailId Present");
					senderMailId=obj.getString("senderMailId");
				}*/
			}
		 callbackId=obj.getString("id");
		// EMAIL_ID = id;
		 internal=obj.getString("internal");
		 recipientId=obj.getString("recipientMailId");
		 emailSub=obj.getString("subject");
		 emailBody=obj.getString("body");
		 emailId = obj.getString("mailId");
		}
		catch (Exception ex) {
			return;
		}
		final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientId});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSub);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
        if(filePathArr != null){
        	float size = 0;
        	ArrayList<Uri> uris = new ArrayList<Uri>();
        	for(int i = 0; i<filePathArr.length(); i++){
        	File file = null;
			try {
				file = new File(filePathArr.getString(i));
			} catch (JSONException e) {
				
			}
			
        	if (file.exists()) {
        		if(maxFileSize != 0){ 
        		size += file.length()/(Math.pow(1024, 2));
        		if(size <= maxFileSize){
        		uris.add(Uri.fromFile(file));	
        		}else{
        			fileStatus = false;
        			errorCode = "APZ-CNT-322";
        		}
        		emailIntent.putExtra(Intent.EXTRA_STREAM, uris);
        		}else{
        			fileStatus = false;
        			errorCode = "APZ-CNT-322";
        		}
        	}else{
        		fileStatus = false;
        		errorCode = "APZ-CNT-002";
    		}}}
        if(ccIdList !=null ){
        	emailIntent.putExtra(android.content.Intent.EXTRA_CC,new String[]{ ccIdList});
        }
        try{
        	if(internal.equals("N")){
        		if(fileStatus){
 			this.activity.startActivityForResult(Intent.createChooser(emailIntent, "Send mail"), RESULT_LOAD_MAIL, new ExternalActivityResultHandler() {
 				
 				@Override
 				public void handleActivityResult(int resultCode, Intent data) {
 					String status  = "cancel";
 					if(resultCode != Activity.RESULT_CANCELED){
 						status = "sent";
 					}
 					JSONObject result = new JSONObject();
 					try{
 						result.put("event", status);
 					}catch(Exception ex){
 					}
 					ApzPluginUtil.sendSuccess(callbackId, result, false, activity, webView, true);
 				}
 				});
        		}else{
        			ApzPluginUtil.sendError(callbackId, errorCode, null, this.activity, this.webView, true);
        			}
        	}
        	else if(internal.equals("Y")){
        		ApzPluginUtil.sendError(callbackId, "MAIL_ERROR", null, this.activity, this.webView, true);//Cannot be launched.
 				return;
        	}
        }
        catch(final ActivityNotFoundException anf){
        	ApzLogger.e(TAG,anf.toString());
        	ApzPluginUtil.sendError(callbackId, "APZ-CNT-012", null, this.activity, this.webView, true);//Mail client not present
				return;
        }
	}
	@Override
	public void execute(JSONObject params) {
		sendmail(params);
	}
}

