package com.iexceed.plugins.launchwebview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.DownloadListener;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;

import static com.iexceed.plugins.launchwebview.ApzLaunchWebview.trackURLString;
import static com.iexceed.plugins.launchwebview.ApzLaunchWebview.trackURLStringArray;

public class LaunchWebviewActivity extends AppCompatActivity {

	protected static final String TAG = "LaunchWebviewActivity";
	String URL;
	String finalURL;
	String callbackId;
	public static WebView mWebView;
	public static Activity webviewActivity;
	JSONObject postData = null;
	String postStr = "";
	
	private boolean safeBrowsingIsInitialized;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launchwebview);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().hide();
		webviewActivity = this;
		Intent intent = getIntent();
		URL = intent.getStringExtra("URL");
		callbackId = intent.getStringExtra("callbackId");
		try {
			if(intent.getStringExtra("cancel_btn").equalsIgnoreCase("Y")){
				getSupportActionBar().show();
			}

			String result = intent.getStringExtra("postData");
			if (!(result.equalsIgnoreCase(""))) {
				postData = new JSONObject(result);
			}
			if (!(postData == null)) {
				Iterator itr = postData.keys();
				int i = 0;
				String key;
				Object value;
				while (itr.hasNext()) {
					if (i == 0) {
						key = (String) itr.next();
						value = postData.get(key);
						postStr = key + "=" + URLEncoder.encode(value.toString(), "UTF-8");
					} else {
						key = (String) itr.next();
						value = postData.get(key);
						postStr = postStr + "&" + key + "=" + URLEncoder.encode(value.toString(), "UTF-8");
					}
					i++;
				}
			}
		} catch (Exception e) {
			Log.d(TAG, "Exception " + e);
			final JSONObject failureCallbackRes = new JSONObject();
			try {
				failureCallbackRes.put("Error", "Failed" + e);
			} catch (JSONException ex) {
				ApzLogger.e(TAG, ex.toString());
			}
			ApzPluginUtil.sendError(callbackId, "", failureCallbackRes, webviewActivity, mWebView, true);
			webviewActivity.finish();
		}
		WebView webview = new WebView(this);
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setDomStorageEnabled(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setGeolocationEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setGeolocationDatabasePath("");
		/*check for posting a URL or Launch a URL*/
		if (postData != null)
			webview.postUrl(URL, EncodingUtils.getBytes(postStr, "BASE64"));
		else
			webview.loadUrl(URL);
		setContentView(webview);

		webview.setDownloadListener(new DownloadListener() {
			public void onDownloadStart(String url, String userAgent,
										String contentDisposition, String mimetype,
										long contentLength) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		
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

		webview.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
				AlertDialog.Builder builder = new AlertDialog.Builder(webviewActivity);
				String message = "";
				switch (error.getPrimaryError()) {
					case SslError.SSL_UNTRUSTED:
						message = "Certificate is untrusted.";
						break;
					case SslError.SSL_EXPIRED:
						message = "Certificate has expired.";
						break;
					case SslError.SSL_IDMISMATCH:
						message = "Certificate ID is mismatched.";
						break;
					case SslError.SSL_NOTYETVALID:
						message = "Certificate is not yet valid.";
						break;
				}
				message += " Do you want to continue anyway?";
				builder.setTitle("SSL Certificate Error");
				builder.setMessage(message);
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.proceed();
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.cancel();
						webviewActivity.finish();
					}
				});
				AlertDialog ad = builder.create();
				ad.show();//handler.proceed();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				/*final JSONObject SuccessCallbackRes = new JSONObject();
				finalURL = url;
				try {
					SuccessCallbackRes.put("URL", finalURL);
				} catch (JSONException e) {
					ApzLogger.e(TAG,e.toString());
				}*/
				super.onPageFinished(view, url);
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				super.onLoadResource(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				finalURL = url;
				boolean callback = false;
				view.loadUrl(url);
				if (trackURLStringArray != null || !trackURLString.equals("")) {
					if (trackURLStringArray != null) {
						for (int i = 0; i < trackURLStringArray.length; i++) {
							if(trackURLStringArray[i].equalsIgnoreCase(finalURL)){
								callback = true;
							}
						}
					}else if(trackURLString.equalsIgnoreCase(finalURL)){
						callback = true;
					}
					if(callback){
						final JSONObject SuccessCallbackRes = new JSONObject();
						try {
							SuccessCallbackRes.put("URL", finalURL);
						} catch (JSONException e) {
							ApzLogger.e(TAG,e.toString());
						}
						ApzPluginUtil.sendSuccess(callbackId, SuccessCallbackRes, true, webviewActivity, mWebView, true);
					}
				}
				return super.shouldOverrideUrlLoading(view, url);

			}
			
			@Override
			public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
				// The "true" argument indicates that your app reports incidents like
				// this one to Safe Browsing.
				if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
						callback.backToSafety(true);
					}
					Toast.makeText(view.getContext(), "Unsafe web page blocked.",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	public static void setWebView(WebView wb){
		mWebView = wb;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ApzPluginUtil.sendSuccess(callbackId, null, false, webviewActivity, mWebView, true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		if(item.getItemId() == R.id.action_close){
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.web_activity_close, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
	}
}

