package com.iexceed.plugins.createnote;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzCreateNote extends ApzPlugin{

	private static ApzPlugin pluginObj;
	
	private ApzCreateNote(WebView webView, ApzActivity activity) {
		super(webView, activity);
		// TODO Auto-generated constructor stub
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){ 
		pluginObj = new ApzCreateNote(webView, activity); 
	}  
	return pluginObj; 
	}

	@Override
	public void execute(JSONObject params) {
		try {
			this.callbackId = (String) params.getString("id");
			String refNo = params.getString("refNo");
//			ApzLogger.d(TAG, "startNotePad refNo : "+refNo);
			final Intent intent = new Intent(activity, CreateNote.class);
			Bundle bundle = new Bundle();
			bundle.putString("refNO", refNo);
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.activity.startActivity(intent);
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}
		
	}

}
