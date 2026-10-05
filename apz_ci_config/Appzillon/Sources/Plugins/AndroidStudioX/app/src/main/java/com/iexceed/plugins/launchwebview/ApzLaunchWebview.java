package com.iexceed.plugins.launchwebview;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzLaunchWebview extends ApzPlugin {

	String TAG = "ApzLaunchWebview";
	private static ApzPlugin pluginObj;
	static String[] trackURLStringArray;
	static String trackURLString = "";
	private ApzLaunchWebview(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){ 
		pluginObj = new ApzLaunchWebview(webView, activity); 
	}  
	return pluginObj; 
	}

	@Override
	public void execute(JSONObject params) {
		try {
			this.callbackId = params.getString("id");
			String action = params.getString("action");

			if (action.equalsIgnoreCase("Open")) {
				String URL = params.getString("URL");
				JSONObject postData=params.optJSONObject("postData");
				Object trackURL = params.opt("trackURL");
				trackURLStringArray = null;
				trackURLString = "";
				String closeEnabled = params.optString("cancelButton");
				if (trackURL != null) {
					if (trackURL instanceof JSONArray) {
                        JSONArray jsonArray = params.getJSONArray("trackURL");
						trackURLStringArray = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            trackURLStringArray[i] = jsonArray.getString(i);
                        }
                    }else if (trackURL instanceof JSONObject) {
                        trackURLString = params.getString("trackURL");
                    }
				}
				LaunchWebviewActivity.setWebView(ApzLaunchWebview.this.webView);
				Intent in = new Intent(ApzLaunchWebview.this.activity,
						LaunchWebviewActivity.class);
				in.putExtra("callbackId", ApzLaunchWebview.this.callbackId);
				in.putExtra("URL", URL);
				in.putExtra("cancel_btn", closeEnabled);
				if(postData==null)
					in.putExtra("postData","");
				else
				in.putExtra("postData",postData.toString());
				this.activity.startActivity(in);
			}else if (action.equalsIgnoreCase("Close")){
					LaunchWebviewActivity.webviewActivity.finish();
					ApzPluginUtil.sendSuccess(callbackId, null, false, activity, webView, true);

			}

		} catch (Exception e) {
			ApzLogger.e(TAG,e.toString());
		}

	}
	
	public static boolean isPlugin() {
		return true;
	}

}


