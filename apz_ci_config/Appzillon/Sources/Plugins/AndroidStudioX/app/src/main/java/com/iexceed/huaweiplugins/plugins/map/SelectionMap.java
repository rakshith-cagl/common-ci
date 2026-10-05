package com.iexceed.plugins.map;

import org.json.JSONException;
import org.json.JSONObject;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationServices;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.common.StringUtils;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ConsoleMessage.MessageLevel;

@SuppressLint("SetJavaScriptEnabled")
public class SelectionMap extends Activity {

	private String jsontext;

	private String currjson;

	public JSONObject jobj;

	private String callbackId;
	
	private final String TAG = "LOCATION SELECTOR";
	
	private static WebView mainWebview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ApzLogger.i(TAG,"onCreate");
		setContentView(R.layout.activity_map);
		Intent i = getIntent();
		jsontext = i.getStringExtra("mapval");
		FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

		try {
			Task<Location> lastLocation = mFusedLocationProviderClient.getLastLocation();
			lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
				@Override
				public void onSuccess(Location presentLocation) {
					loadMap(presentLocation);
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(Exception e) {
					loadMap(null);
				}
			});
		} catch (Exception e) {
			loadMap(null);
		}
	}


	private void loadMap(Location location) {
		if (location != null) {
			JSONObject currLoc = new JSONObject();
			try {
				jobj = new JSONObject(jsontext);
				currLoc.put("locationlatitude",location.getLatitude());
				currLoc.put("locationlongitude", location.getLongitude());
				currjson = currLoc.toString();
				callbackId = jobj.getString("id");
			} catch (JSONException e) {
				ApzLogger.e(TAG,e.toString());
			}
		}

		final WebView mwebview = findViewById(R.id.webView1);

		WebSettings webSettings = mwebview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setGeolocationDatabasePath("");
		String URL = "";
		String GOOGLE_MAP_KEY = StringUtils.getString(StringUtils.GOOGLE_MAPS_KEY);
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
		mwebview.loadUrl(URL);
		mwebview.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedSslError(WebView view,SslErrorHandler handler, SslError error) {}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) { }

			@Override
			public void onPageFinished(WebView view, String url) {
				mwebview.loadUrl("javascript:(function() { initializmap.select('" + jsontext+ "','"+ currjson+ "')})()");
			}

		});

		// Abhishek 19 Feb 2015 To handle the error thrown by JavaScript for No Network connectivity START
		mwebview.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				MessageLevel level = consoleMessage.messageLevel();
				if (level.ordinal() == 3) { // Error ordinal
					if (consoleMessage.message().contains("google is not defined")) {
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
				} catch (JSONException e) { }
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
