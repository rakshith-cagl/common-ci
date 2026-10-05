package com.iexceed.plugins.pushnotification;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.webkit.WebView;

import com.iexceed.common.DatabaseHandler;
import com.iexceed.common.Notification;
import com.iexceed.plugins.auditlog.AuditLog;
import com.iexceed.plugins.errorlog.ApzLogger;
public class PushNotification {
	
	private DatabaseHandler mDbHelper;
	
	private ArrayList<Notification> arrList;
	
	private WebView mWebView;
	
	private Context mContext;
	
	private Activity mActivity;
	
	private String mSuccessCallback;
	
	private String mFailureCallback;
	
	public String mRowID;
	
	private String TAG = "PushNotification";
	
	public PushNotification(Context context,Activity act, WebView wv) {
		mWebView = wv;
		mContext = context;
		mActivity=act;
	}
	/**
	 * Retrieves push messages from local sqlite database and process them
	 * @param jsonObj
	 */
	public void getAllPushNotes(JSONObject jsonObj) {
//		AuditLog.makeString("PUSHNOTIFICATION","getAllPushNotes");
		try {
			mSuccessCallback=jsonObj.getString("successCallback");
			mFailureCallback=jsonObj.getString("failureCallback");
			ApzLogger.i(TAG,"mSuccessCallback : "+mSuccessCallback);
		} catch (final JSONException e) {
			ApzLogger.e(TAG,e.toString());
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
//					AuditLog.makeString("PUSHNOTIFICATION","JSON Exception");
					AuditLog.sendToJSON();
					//mWebView.loadUrl("javascript:appzillon.plugin.addauditdata ('"+ AuditLog.getAuditString(TAG) + "');");
					//mWebView.loadUrl("javascript:jsonParseExceptionCallBack('"	+ e.getMessage() + "');");
					return;
				}
			});
		}
		
		
		mDbHelper = new DatabaseHandler(mContext);
		final JSONArray ja = new JSONArray();
		arrList = mDbHelper.getAllPushMsg();
		if (arrList.size() > 0) {
			 try{
			   for (int i = 0; i < arrList.size(); i++) {
             	ja.put(arrList.get(i).getJsonobject());
                  }
			   }
              catch (final Exception e) {
            	  ApzLogger.e(TAG,e.toString());
            	  mActivity.runOnUiThread(new Runnable() {
      				@Override
      				public void run() {
//      					AuditLog.makeString("PUSHNOTIFICATION","Failure");
      					AuditLog.sendToJSON();
      					//mWebView.loadUrl("javascript:appzillon.plugin.addauditdata ('"+ AuditLog.getAuditString(TAG) + "');");
      					//mWebView.loadUrl("javascript:"+mFailureCallback+"('"+ e.getMessage() + "');");
      					return;
      				}
      			});
              
              }
              finally{
            	  mDbHelper.close();
              }
			
			 mActivity.runOnUiThread(new Runnable() {
   				@Override
   				public void run() {
//   					AuditLog.makeString("PUSHNOTIFICATION","Success");
   					AuditLog.sendToJSON();
   					//mWebView.loadUrl("javascript:appzillon.plugin.addauditdata ('"+ AuditLog.getAuditString(TAG) + "');");
   					mWebView.loadUrl("javascript:"+mSuccessCallback+"('"+ ja.toString() + "');");
   				}
   			});
			ApzLogger.i(TAG,"Json2:" + ja.toString());	
		}
		else{
			final String emptyMsg="[{\"ID\": \" \",\"MSG\": \" \",\"Time\": \" \"}]";
            mActivity.runOnUiThread(new Runnable() {
   				@Override
   				public void run() {
//   					AuditLog.makeString("PUSHNOTIFICATION","Success");
   					AuditLog.sendToJSON();
   					//mWebView.loadUrl("javascript:appzillon.plugin.addauditdata ('"+ AuditLog.getAuditString(TAG) + "');");
   					mWebView.loadUrl("javascript:"+mSuccessCallback+"('"+ emptyMsg + "');");
   				}
   			});
		}
		
	}
	/**
	 * Deletes stored pushMessages from Sqlite DB based on RowID
	 * @param jsonObj
	 */
	public void deletePushNotes(JSONObject jsonObj) {
//		AuditLog.makeString("PUSHNOTIFICATION","deletePushNotes");
		boolean isDeleted = false;
		try {
			mRowID=jsonObj.getString("notificationID");
			mSuccessCallback=jsonObj.getString("successCallback");
			mFailureCallback=jsonObj.getString("failureCallback");
			ApzLogger.i(TAG,"mSuccessCallback : "+mSuccessCallback);
		} catch (final JSONException e) {
			ApzLogger.e(TAG,e.toString());
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
//					AuditLog.makeString("PUSHNOTIFICATION","JSON Exception");
					AuditLog.sendToJSON();
					//mWebView.loadUrl("javascript:appzillon.plugin.addauditdata ('"+ AuditLog.getAuditString(TAG) + "');");
					//mWebView.loadUrl("javascript:jsonParseExceptionCallBack('"+ e.getMessage() + "');");
					return;
				}
			});
		}
		
		mDbHelper = new DatabaseHandler(mContext);
		try{
			if(mRowID==null){
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						AuditLog.makeString("PUSHNOTIFICATION","Success");
						AuditLog.sendToJSON();
						//mWebView.loadUrl("javascript:appzillon.plugin.addauditdata ('"+ AuditLog.getAuditString(TAG) + "');");
						mWebView.loadUrl("javascript:"+mSuccessCallback+"('"+ mRowID+ "');");
						return;
					}
				});
			}
			else{
		      isDeleted = mDbHelper.deletePushMsg(mRowID);
		      
			}
		}
		catch(final SQLiteException e){
			ApzLogger.e(TAG,e.toString());
			mDbHelper.close();
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
//					AuditLog.makeString("PUSHNOTIFICATION","SQLite exception");
					AuditLog.sendToJSON();
					//mWebView.loadUrl("javascript:appzillon.plugin.addauditdata ('"+ AuditLog.getAuditString(TAG) + "');");
					//mWebView.loadUrl("javascript:"+mFailureCallback+"('"+ e.getMessage() + "');");
					return;
				}
			});
		}
		
		if(isDeleted){
			mDbHelper.close();
			ApzLogger.i(TAG,"Push Row with _id "+mRowID+" deleted successfully");
		mActivity.runOnUiThread(new Runnable() {
			@Override
			
			public void run() {
//				AuditLog.makeString("PUSHNOTIFICATION","Success");
				AuditLog.sendToJSON();
				//mWebView.loadUrl("javascript:appzillon.plugin.addauditdata ('"+ AuditLog.getAuditString(TAG) + "');");
				mWebView.loadUrl("javascript:"+mSuccessCallback+"('"+ mRowID+ "');");
				
			}
		});
		}
		else{
			mDbHelper.close();
			ApzLogger.i(TAG,"Push Row with _id "+mRowID+" failed deletion");
			mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					AuditLog.sendToJSON();
					//mWebView.loadUrl("javascript:appzillon.plugin.addauditdata ('"+ AuditLog.getAuditString(TAG) + "');");
					mWebView.loadUrl("javascript:"+mSuccessCallback+"('"+ mRowID+ "');");
					
				}
			});
		}

	}
public static boolean isPushNotification() {
		return true;
	}
}
