package com.iexceed.plugins.map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.StringUtils;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.currentlocation.CurrentLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("SetJavaScriptEnabled")
public class Map extends Activity {

	public WebView mwebview;

	public String jsontext;
	public JSONObject jobj;
	String headerText;
	String headerColor;

	private WebSettings webSettings;

	private static String successCallback;

	private static String failureCallback;
	private static String mapkey = "";

	private String TAG = "MAP";

	private String callbackId;

	// private static JavaScriptInterface mapjsi;
	public static boolean IsMap() {
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		AuditLog.makeString("MAP","onstart");
		/*if(getResources().getString(R.string.is_screenshot_enabled).equalsIgnoreCase("Y"))
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);*/
		setContentView(R.layout.activity_map);
		Intent i = getIntent();
		//titleText = i.getStringExtra("titleText");
		jsontext = i.getStringExtra("mapval");
		mwebview = (WebView) findViewById(R.id.webView1);


		try {
			jobj = new JSONObject(jsontext);

			headerText = jobj.optString("headerText");
			headerColor=jobj.optString("headerColor");
			mapkey=jobj.optString("mapKey");
			callbackId = jobj.getString("id");
		} catch (Exception e) {

		}
		if(!(headerColor.trim().isEmpty())) {
			LinearLayout textLayout = (LinearLayout) findViewById(R.id.logo);
			textLayout.setVisibility(View.VISIBLE);
			textLayout.setBackgroundColor(Color.parseColor(headerColor));
			TextView title = (TextView) findViewById(R.id.headerText);
			title.setText(headerText);
		}



		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			Location location = CurrentLocation.getLocation(AppzillonMainScreen.activity);
			if (location != null) {
				try {

					JSONObject currLoc = new JSONObject();
					currLoc.put("locationName", "I am here");
					currLoc.put("locationDescription", "");
					currLoc.put("locationLatitude", location.getLatitude());
					currLoc.put("locationLongitude", location.getLongitude());
					JSONArray jArr = jobj.getJSONArray("markerInfo");
					jArr.put(currLoc);
					jsontext = jobj.toString();
				} catch (JSONException e) {

					//mwebview.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");

				}

				mwebview.addJavascriptInterface(this, "markerLocation");
				webSettings = mwebview.getSettings();
				//webSettings.setAllowUniversalAccessFromFileURLs(true);
				webSettings.setJavaScriptEnabled(true);
				webSettings.setBuiltInZoomControls(false);
				webSettings.setDomStorageEnabled(true);
				webSettings.setLoadsImagesAutomatically(true);
				webSettings.setGeolocationEnabled(true);
				webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
				webSettings.setGeolocationDatabasePath("");

				//Abhishek For OTA
				String URL = "";
				//String GOOGLE_MAP_KEY = StringUtils.getString(StringUtils.GOOGLE_MAPS_KEY);
				String GOOGLE_MAP_KEY =mapkey;
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

//				mwebview.loadUrl(URL);
                mwebview.loadUrl(AppzillonUtils.validateApzWebViewURL(URL));
				mwebview.setWebChromeClient(new WebChromeClient() {
				});
				mwebview.setWebViewClient(new WebViewClient() {

					@Override
					public void onReceivedSslError(WebView view,
												   SslErrorHandler handler, SslError error) {

					}

					@Override
					public void onPageStarted(WebView view, String url, Bitmap favicon) {
					}

					@Override
					public void onPageFinished(WebView view, String url) {
						//mwebview.loadUrl("javascript:(function() { showPosition('" + jsontext + "')})()");
						mwebview.loadUrl(AppzillonUtils.validateMapURL("javascript:(function() { showPosition('" + jsontext + "')})()"));
					}

				});
			} else {
				failureCallback("Could not retrieve current location.");
			}
		} else {
			failureCallback("No network available.");
		}


	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public final void onBackPressed() {
		failureCallback("User cancelled.");
		super.onBackPressed();
	}

	@JavascriptInterface
	public void fetchloc(final String jsonText) {
		AppzillonMainScreen.activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				JSONObject json = new JSONObject();
				try {
					JSONObject location = new JSONObject(jsonText);
					json.put("latitude", location.getString("latitude"));
					json.put("longitude", location.getString("longitude"));
				} catch (JSONException e) {
					//e.printStackTrace();
				}
				/*AppzillonMainScreen.mwebView.loadUrl("javascript:" + successCallback
						+ "(" + json + ");");*/
				ApzPluginUtil.sendSuccess(callbackId, json, false, AppzillonMainScreen.activity, AppzillonMainScreen.webView, true);
				finish();
			}
		});
	}

	@JavascriptInterface
	public void getCurrLoc() {

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			AppzillonMainScreen.activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					try {
						Location location = CurrentLocation.getLocation(AppzillonMainScreen.activity);
						if (location != null) {
							JSONObject currLoc = new JSONObject();
							currLoc.put("locationLatitude", location.getLatitude());
							currLoc.put("locationLongitude", location.getLongitude());
							mwebview.loadUrl("javascript:getCurrentPosition(" + currLoc + ");");
						}
					} catch (JSONException e) {
					}
				}
			});
		}
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
}
