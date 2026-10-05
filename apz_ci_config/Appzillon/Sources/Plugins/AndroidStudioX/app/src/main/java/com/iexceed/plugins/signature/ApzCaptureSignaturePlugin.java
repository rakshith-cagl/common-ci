package com.iexceed.plugins.signature;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.ExternalActivityResultHandler;
import com.iexceed.common.UserSettings;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

public class ApzCaptureSignaturePlugin extends ApzPlugin{

	private static ApzPlugin pluginObj;
	private final int CAPTURE_SIGN = 106;
	
	private ApzCaptureSignaturePlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity){
		if(pluginObj == null)
		{
			pluginObj = new ApzCaptureSignaturePlugin(webView, activity);
		}
		return pluginObj;
	}
	
	@Override
	public void execute(JSONObject params) {
			try {
				callbackId = params.getString("id");
			} catch (JSONException e) {
				
			}
			Intent intent = new Intent(this.activity.getApplicationContext(), CaptureSignature.class);
			this.activity.startActivityForResult(intent, CAPTURE_SIGN, new ExternalActivityResultHandler() {
				
				@Override
				public void handleActivityResult(int resultCode, Intent data) {
					if (resultCode == activity.RESULT_OK) {
						
						final JSONObject signSuccessCallbackRes = new JSONObject();
						String signString= data.getStringExtra("signvalue");
						//String signString = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,"base64sign", "",	AppzillonMainScreen.settings);

						try {
							signSuccessCallbackRes.put("successMessage", "");
							signSuccessCallbackRes.put("path", "");
							signSuccessCallbackRes.put("encodedImage", signString);
						} catch (final JSONException ex) {
							return;
						}
						ApzPluginUtil.sendSuccess(callbackId, signSuccessCallbackRes, false, activity, webView, true);
					}
					
				}
			});
		} 
	
	public static boolean isSignauturePlugin() {
		return true;
	}
}
