package com.iexceed.plugins.keyboard;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN) public class KeyboardPlugin extends ApzPlugin{
	
	boolean isOpened = false;
	View activityRootView = null;
	private static ApzPlugin pluginObj;
	
	private Activity mActivity;
	private WebView mWebview;
	private String callerId;
	private OnGlobalLayoutListener globalLayoutListener;
	
	public KeyboardPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
		this.mActivity = activity;
		this.mWebview = webView;
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  
	if(pluginObj == null){ 
		pluginObj = new KeyboardPlugin(webView, activity); 
	}  
	return pluginObj; 
	}
	
	public void stopListener(JSONObject result){
		JSONObject json = new JSONObject();
		try {
			json.put("event", "stopped");
		} catch (JSONException e) {
		}
		
		ApzPluginUtil.sendSuccess(this.callerId, json, false, this.mActivity, this.mWebview, true);
		if(activityRootView != null){
			activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
			activityRootView = null;
		}

	}
	
	public void setListenerToRootView() {
   activityRootView = mActivity.getWindow().getDecorView()
         .findViewById(android.R.id.content);
   
   activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
         new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
               globalLayoutListener = this;
               if (activityRootView != null) {
                  /*int heightDiff = activityRootView.getRootView()
                        .getHeight() - activityRootView.getHeight();*/
				   Rect r = new Rect();
				   activityRootView.getWindowVisibleDisplayFrame(r);
				   int screenHeight = activityRootView.getRootView().getHeight();

				   // r.bottom is the position above soft keypad or device button.
				   // if keypad is shown, the r.bottom is smaller than that before.
				   int keypadHeight = screenHeight - r.bottom;
                  if (keypadHeight > screenHeight * 0.15) { // 99% of the time the height
                     // diff will be due to a
                     // keyboard.

                     JSONObject result = new JSONObject();
                     try {
                        result.put("event", "show");
                     } catch (JSONException e) {
                        ApzLogger.e(TAG, e.toString());
                     }
                     ApzPluginUtil.sendSuccess(
                           KeyboardPlugin.this.callerId, result, true,
                           KeyboardPlugin.this.mActivity,
                           KeyboardPlugin.this.mWebview, true);
                     if (isOpened == false) {
                        // Do two things, make the view top visible and
                        // the editText smaller
                     }
                     isOpened = true;
                  } else if (isOpened == true) {
                     isOpened = false;
                     JSONObject result = new JSONObject();
                     try {
                        result.put("event", "hide");
                     } catch (JSONException e) {
                     }
                     ApzPluginUtil.sendSuccess(
                           KeyboardPlugin.this.callerId, result, true,
                           KeyboardPlugin.this.mActivity,
                           KeyboardPlugin.this.mWebview, true);
                  }
               }
            }
         });
}

	

	@Override
	public void execute(JSONObject params) {
		
		String action = "";
		try {
			this.callerId = params.getString("id");
			action = params.getString("action");
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
		}
		if(action.equals("STARTLISTENER")){
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			setListenerToRootView();
			JSONObject jsonLstn = null;
			try {
				jsonLstn = new JSONObject();
				jsonLstn.put("event", "started");
			} catch (JSONException e) {
			}
			ApzPluginUtil.sendSuccess(this.callerId, jsonLstn, true, this.mActivity, this.mWebview, true);
			}else{
				ApzPluginUtil.sendError(callerId, "APZ-CNT-323", null, mActivity, mWebview, true);//Not supported below kitkat
			}
			}else if(action.equals("STOPLISTENER")){
			stopListener(params);
		}
		
	}

}

