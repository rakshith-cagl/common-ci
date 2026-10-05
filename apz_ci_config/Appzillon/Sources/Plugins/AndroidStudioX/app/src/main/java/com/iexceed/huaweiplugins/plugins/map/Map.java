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

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationServices;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.common.StringUtils;

import com.iexceed.plugins.ApzPluginUtil;

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

	private String TAG = "MAP";

	private String callbackId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		Intent i = getIntent();
		jsontext = i.getStringExtra("mapval");
		mwebview = findViewById(R.id.webView1);


		try {
			jobj = new JSONObject(jsontext);

			headerText = jobj.optString("headerText");
			headerColor=jobj.optString("headerColor");
			callbackId = jobj.getString("id");
		} catch (Exception e) {

		}
		if(!(headerColor.trim().isEmpty())) {
			LinearLayout textLayout = findViewById(R.id.logo);
			textLayout.setVisibility(View.VISIBLE);
			textLayout.setBackgroundColor(Color.parseColor(headerColor));
			TextView title = findViewById(R.id.headerText);
			title.setText(headerText);
		}

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

			try {
				Task<Location> lastLocation = mFusedLocationProviderClient.getLastLocation();
				lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
					@Override
					public void onSuccess(Location presentLocation) {
						if (presentLocation != null) {
							loadWebView(presentLocation);
						}else{
							failureCallback("Could not retrieve current location.");
						}
						return;
					}
				}).addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(Exception e) {
						failureCallback("Unable to fetch current Location : "+e.getLocalizedMessage());
					}
				});
			} catch (Exception e) {
				failureCallback("Unable to fetch current Location : "+e.getLocalizedMessage());
			}
		} else {
			failureCallback("No network available.");
		}
	}

	private void loadWebView(Location location) {
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
			} catch (JSONException e) { }

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

			mwebview.loadUrl(URL);
			mwebview.setWebChromeClient(new WebChromeClient() {
			});
			mwebview.setWebViewClient(new WebViewClient() {

				@Override
				public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				}

				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					mwebview.loadUrl("javascript:(function() { showPosition('" + jsontext + "')})()");
				}

			});
		} else {
			failureCallback("Could not retrieve current location.");
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
				} catch (JSONException e) { }
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
			FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

			try {
				Task<Location> lastLocation = mFusedLocationProviderClient.getLastLocation();
				lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
					@Override
					public void onSuccess(Location presentLocation) {
						sendLocation(presentLocation);
					}
				}).addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(Exception e) {
						failureCallback("Unable to fetch current Location : "+e.getLocalizedMessage());
					}
				});
			} catch (Exception e) {
				failureCallback("Unable to fetch current Location : "+e.getLocalizedMessage());
			}

		}
	}

	private void sendLocation(final Location location) {
		AppzillonMainScreen.activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if (location != null) {
						JSONObject currLoc = new JSONObject();
						currLoc.put("locationLatitude", location.getLatitude());
						currLoc.put("locationLongitude", location.getLongitude());
						mwebview.loadUrl("javascript:getCurrentPosition(" + currLoc + ");");
					}
				} catch (JSONException e) { }
			}
		});
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
}
