package com.iexceed.plugins.map;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.StringUtils;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.currentlocation.CurrentLocation;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ConsoleMessage.MessageLevel;
import android.widget.Toast;

import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;

@SuppressLint("SetJavaScriptEnabled")
public class SelectionMap extends Activity {

	private String jsontext;
	
	private String currjson;
	public JSONObject jobj;
	private String callbackId;
	
	private final String TAG = "LOCATION SELECTOR";
	
	private static WebView mainWebview;
	private static String mapkey = "";	
	
	private boolean safeBrowsingIsInitialized;

	public static boolean IsMap(){
			return true;
		}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApzLogger.i(TAG,"onCreate");
		setContentView(R.layout.activity_map);
		Intent i = getIntent();
		jsontext = i.getStringExtra("mapval");
//		ApzLogger.i(TAG,"jsontext : "+jsontext);
		Location location = CurrentLocation.getLocation(this);
		if (location != null) {
			JSONObject currLoc = new JSONObject();
			try {
				jobj = new JSONObject(jsontext);
				currLoc.put("locationlatitude",location.getLatitude());
				currLoc.put("locationlongitude", location.getLongitude());
				mapkey=jobj.optString("mapKey");				
				currjson = currLoc.toString();
				callbackId = jobj.getString("id");
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
				//mainWebview.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
			}			
		}
		
		final WebView mwebview = (WebView) findViewById(R.id.webView1);

		WebSettings webSettings = mwebview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setGeolocationDatabasePath("");
		//Abhishek For OTA
		String URL = "";
		//String GOOGLE_MAP_KEY = StringUtils.getString(StringUtils.GOOGLE_MAPS_KEY);
		String GOOGLE_MAP_KEY = mapkey;
		if(!(GOOGLE_MAP_KEY.equalsIgnoreCase(""))) {

			if ((AppzillonMainScreen.OTAREQUIRED).equalsIgnoreCase("Y")) {
				//googleMapsKey

				URL = "file:///" + AppzillonMainScreen.SANDBOX_LOC + "/" + AppzillonMainScreen.ASSET_APP_LOC + "screens/Map.html?api_key="+GOOGLE_MAP_KEY;
			} else {
				URL = "file:///android_asset/" + AppzillonMainScreen.ASSET_APP_LOC + "screens/Map.html?api_key="+GOOGLE_MAP_KEY;
			}
		}else{
			failureCallback("Google Map API key not found.");
		}
		ApzLogger.i(TAG,"Launch google map");
//		mwebview.loadUrl(URL);
		mwebview.loadUrl(AppzillonUtils.validateApzWebViewURL(URL));
		safeBrowsingIsInitialized = false;
		if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
			WebViewCompat.startSafeBrowsing(this, new ValueCallback<Boolean>() {
				@Override
				public void onReceiveValue(Boolean success) {
					safeBrowsingIsInitialized = true;
					if (!success) {
						ApzLogger.e(TAG, "Unable to initialize Safe Browsing!");
					}
				}
			});
		}
		mwebview.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedSslError(WebView view,SslErrorHandler handler, SslError error) {}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) { }

			@Override
			public void onPageFinished(WebView view, String url) {
//				mwebview.loadUrl("javascript:(function() { initializmap.select('" + jsontext+ "','"+ currjson+ "')})()");
                mwebview.loadUrl(AppzillonUtils.validateMapURL("javascript:(function() { initializmap.select('" + jsontext+ "','"+ currjson+ "')})()"));
			}
			
			@Override
			public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
				// The "true" argument indicates that your app reports incidents like this one to Safe Browsing.
				if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
						callback.backToSafety(true);
					}
					Toast.makeText(view.getContext(), "Unsafe web page blocked.",Toast.LENGTH_LONG).show();
				}
			}

		});
		
		// Abhishek 19 Feb 2015 To handle the error thrown by JavaScript for No Network connectivity START
		mwebview.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				MessageLevel level = consoleMessage.messageLevel();
				if (level.ordinal() == 3) { // Error ordinal
					if (consoleMessage.message().contains("google is not defined")) {
						//mainWebview.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-059','','');");
						finish();
					}

				}
				return false;
			}

		});
		// Abhishek 19 Feb 2015 To handle the error thrown by JavaScript for No Network connectivity END

	}
	
	@Override
	protected void onDestroy() {
		ApzLogger.i(TAG, "onDestroy");
		super.onDestroy();
	}
	public void failureCallback(final String jsonText) {
		AppzillonMainScreen.activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				JSONObject json = new JSONObject();
				try {
					json.put("error", jsonText);
				} catch (JSONException e) {
					//e.printStackTrace();
				}
				/*AppzillonMainScreen.mwebView.loadUrl("javascript:" + failureCallback
						+ "(" + json + ");");*/
				ApzPluginUtil.sendError(callbackId, "", json, AppzillonMainScreen.activity, AppzillonMainScreen.webView, true);
				finish();
			}
		});
	}
	
	//Abhishek 19 Feb 2015 Reference of Main Web View to show Error Message START
		public static void setWebView(WebView webView) {
			mainWebview = webView;		
		}
	//Abhishek 19 Feb 2015 Reference of Main Web View to show Error Message END
}
