package com.iexceed.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.accelerometer.AccelerometerPlugin;
import com.iexceed.plugins.audio.AudioPlugin;
import com.iexceed.plugins.auditlog.AuditLog;
import com.iexceed.plugins.compass.CompassPlugin;
import com.iexceed.plugins.dataencryption.EncryptDecryptUtility;
import com.iexceed.plugins.errorlog.ApzLogger;
import com.iexceed.plugins.openfile.LoadPDFInWebView;
import com.iexceed.plugins.openfile.LoadTxtInWebView;
import com.iexceed.plugins.ota.OTAPlugin;
import com.iexceed.plugins.pushnotification.PushNotification;
import com.iexceed.plugins.runtimelogger.RuntimeLogger;
import com.iexceed.plugins.whitelist.EnableWhiteList;
import com.iexceed.security.HashXor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

//import com.iexceed.plugins.fingerprintscan.MorphoCapture;
//import com.iexceed.plugins.fingerprintscan.MorphoSampleActivity;
//import com.iexceed.plugins.launchwebview.LaunchWebviewActivity;
//import com.iexceed.plugins.printfile.MyPrintDocumentAdapter;
//import com.iexceed.plugins.printfile.PrintDialogActivity;
//import com.iexceed.plugins.camera.CameraActivity;
//import com.iexceed.plugins.camera.NativeCamera;
//import com.iexceed.plugins.fileoperation.FileDownload;
//import com.iexceed.plugins.fileoperation.FileUpload;
//import com.iexceed.plugins.map.DrivingDirection;
//import com.iexceed.plugins.map.Map;
//import com.iexceed.plugins.map.SelectionMap;
//import android.media.Ringtone;

public class JavaScriptInterface {
	
	private Context context;
	
	private Bundle bundle;
	
	private Button button;
	
	private Dialog mSpinView;
	
	public static Activity activity;
	
	private SharedPreferences settings;
	
	final static String properties = "USER_PREFS";
	
	final static String keyIP = "IP";
	
	final static String PAGE = "Page";
	
	public static int RESULT_LOAD_IMAGE = 1;
	
	public static int RESULT_LOAD_VIDEO = 2;
	
	public static int RESULT_LOAD_AUDIO = 3;
	
	public static final int BROWSE_FILE = 102;
	
	public static final int TAKE_PIC = 10;
	
	public static final int BARCODE_SCAN = 100;
	
	public static final int SEND_NFC = 101;
	
	public static final int RECEIVE_NFC = 103;
	
	public static final int DEVICE_RECEIVE_NFC = 104;
	
	public static final int CAPTURE_SIGN = 106;
	
	public static final int PICK_CONTACTS = 108;
	
	public static final int FINGERPRINT = 109;
	
	public static int RESULT_LOAD_PDF_IN_WEBVIEW = 110;
	
	public static int RESULT_LOAD_TXT_IN_WEBVIEW = 111;
	
	public static String isOpenable;
	
	public static Uri imageUri;
	
	private static String TAG = "JavascriptInterface";
	
	public static WebView webView;
	
	private CustomProgressDialog progressDialog;
	
	private String imei;
	
	private String imsi;
	
	private String ip;
	
	public static String SUCCESS_CALLBACK;
	
	public static String ACTION;
	
	public static String FAILURE_CALLBACK;
	
	/* Accelerometer start */
	private AccelerometerPlugin mAccelerometerPlugin;
	
	/* Compass */
	private CompassPlugin mCompassPlugin;
	
	/* gps */

	/* PushNotification */
	private PushNotification mPushNotification;
	
	/* RunTime Logger */
	private RuntimeLogger mRuntimeLogger;
	
	/* Audio Plugin */
	private AudioPlugin mAudioPlugin;
	
	/* for multiview */
	private com.iexceed.plugins.multiview.JavaScriptBridge jb;
	
//	private SharedPreferences apps;
	
	final static String app_props = "APP_PREFS";
	
	private String paddingMask = "$$$$$$$$$$$$$$$$";
	
	private static String AUG_REALITY_TH_CALLBACK;
	
	public static String BEACON_ID = "";
	
	public static String OPEN_FILE_SUCCESS_CALLBACK;
	
	public static String OPEN_FILE_FAILURE_CALLBACK;
	
	/*for SMS listener*/
	public BroadcastReceiver smsReceiver;

//	JavaScriptInterface(Context c, Activity a, WebView w,CustomProgressDialog p, RelativeLayout splashScreen,com.iexceed.demobank.plugins.multiview.JavaScriptBridge jb) {
		//context = c;
		//this.activity = a;
		//this.webView = w;
		//this.progressDialog = p;
		//mSpinView = splashScreen;
		//this.jb = jb;
	//}

public JavaScriptInterface(Context c, Activity a, WebView w, CustomProgressDialog p, Dialog splashScreen, com.iexceed.plugins.multiview.JavaScriptBridge jb) {
		context = c;
		this.activity = a;
		this.webView = w;
		this.progressDialog = p;
		mSpinView = splashScreen;
		this.jb = jb;
	}

//	@JavascriptInterface
//	public void closeApplication() {
//		ServerLog.i(TAG," closeApplication ");
//		//Abhishek,13 August 2015,Bug id 6147,Commented out On Back press all the preferences data is been cleared,Reverted Back to 3.0 START
////		settings = context.getSharedPreferences(properties, 0);
////		Editor editor = settings.edit();
////		editor.clear();
////		editor.commit();
//		//Abhishek,13 August 2015,Bug id 6147,Commented out On Back press all the preferences data is been cleared,Reverted Back to 3.0 END
//		activity.finish();
//	}

	@JavascriptInterface
	public void setScreenMapping(String jsonscr) {
		ApzLogger.i(TAG," setScreenMapping");
		JSONObject scrjson = null;
		try {
			scrjson = new JSONObject(jsonscr);
		} catch (JSONException e1) {

		}
		Iterator<String> scrkeys = scrjson.keys();
		StringBuilder screenMapping = null;
		StringBuilder screenMap = new StringBuilder();
		while (scrkeys.hasNext()) {
			String scrName = scrkeys.next();
			String value = null;
			try {
				value = scrjson.getString(scrName);
			} catch (JSONException e) {

			}
			screenMapping = new StringBuilder(scrName + ":" + value + "%");
			screenMap.append(screenMapping.toString());
		}

		UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,"ScreenMap", screenMap.toString(), settings);
	}

	@JavascriptInterface
	public String getScreenMap() {
		String screenMap = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,"ScreenMap", "", settings);
		ApzLogger.i(TAG, "getScreenMap");
		String screenorientation = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,"ORIENTATION", "", settings);
		if (screenorientation.equalsIgnoreCase("Landscape")) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else if (screenorientation.equalsIgnoreCase("Portrait")) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		} else {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		return screenMap;
	}

	@JavascriptInterface
	public final void setButtonVisible() {
		if (context != null && button != null) {
			activity.runOnUiThread(new Runnable() {
				public void run() {
					button.setVisibility(View.VISIBLE);
				}
			});
		} else {
		}
	}

	@JavascriptInterface
	public final void setButtonGone() {
		if (context != null && button != null) {
			activity.runOnUiThread(new Runnable() {
				public void run() {
					button.setVisibility(View.GONE);
				}
			});
		} else {
		}
	}

	@JavascriptInterface
	public final void startCalendar(String event, String decription, String str_date) {
		ApzLogger.d(TAG, "startCalendar  str_date : "+str_date);
		settings = context.getSharedPreferences(properties, 0);
		final long endTime = 60 * 60 * 1000;
		String dateFormat = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,"DATEFORMAT", "", settings);
		Date date = null;
		try {
			SimpleDateFormat formatter;
			if (dateFormat.contains("MON")) {
				formatter = new SimpleDateFormat(dateFormat.replace("MON", "MMM").replace("Y", "y").replace("D", "d"));
			} else {
				formatter = new SimpleDateFormat(dateFormat.replace("Y", "y").replace("D", "d"));
			}
			date = (Date) formatter.parse(str_date);
		} catch (ParseException e) {
			ApzLogger.e(TAG, "Exception :" + e.getMessage());
		} catch (IllegalArgumentException e) {
			ApzLogger.d(TAG, "IllegalException :" + e.getMessage());
		} finally {
			if (date == null) {
				date = new Date();
				ApzLogger.d(TAG, "EventTime : UnDefined");
			}
			ApzLogger.d(TAG, "Date : " + date.getTime());
			final Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			intent.putExtra("beginTime", date.getTime());
			intent.putExtra("rrule", "FREQ=YEARLY");
			intent.putExtra("endTime", date.getTime() + endTime);
			intent.putExtra("title", event);
			intent.putExtra("description", decription);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.context.startActivity(intent);
			ApzLogger.d(TAG, "Success Calendar Launched");
		}
	}

	/**
	 * Starts audio plugin
	 * 
	 * @param jsonText
	 */
//	@JavascriptInterface
//	public void startAudioPlugin(String jsonText) {
//		if (AudioPlugin.isAudioPlugin()) {
//			ServerLog.d(TAG, "startAudioPlugin");
//			AuditLog.makeString("AUDIO","START");
//			JSONObject audioJson = null;
//			try {
//				audioJson = new JSONObject(jsonText);
//			} catch (JSONException ex) {
//
//				return;
//			}
//			if (mAudioPlugin == null) {
//				mAudioPlugin = new AudioPlugin(context, activity, webView);
//			}
//			mAudioPlugin.audioPlugin(audioJson);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-100','','');");
//				}
//			});
//		}
//	}

	/**
	 * Calls CameraActivity class to take picture
	 * 
	 * @param jsonText
	 */
//	@JavascriptInterface
//	public final void startCameraUsingApi(String jsonText) {
//
//		boolean isCamera = CameraActivity.IsCamera();
//		if (isCamera) {
//			ServerLog.d(TAG, "startCameraUsingApi");
//			AuditLog.makeString("CAMERA", "START");
//			JSONObject jsonCamera = null;
//			String openNativeCamera = null;
//			try {
//				try {
//					jsonCamera = new JSONObject(jsonText);
//							openNativeCamera = jsonCamera
//									.optString("openNativeCamera");
//							SUCCESS_CALLBACK = jsonCamera
//									.getString("successCallback");
//							FAILURE_CALLBACK = jsonCamera
//									.getString("failureCallback");
//							ACTION = jsonCamera.getString("action");
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//
//				}
//					if (openNativeCamera.equalsIgnoreCase("N")
//							|| openNativeCamera.equalsIgnoreCase("")) {
//						Intent camera = new Intent(context, CameraActivity.class);
//						camera.putExtra("jsonStr", jsonText);
//						activity.startActivityForResult(camera, TAKE_PIC);
//					} else {
//						Intent openNativeCamera_intent = new Intent(context,
//								NativeCamera.class);
//						openNativeCamera_intent.putExtra("jsonStr", jsonText);
//						activity.startActivityForResult(
//								openNativeCamera_intent, TAKE_PIC);
//					}
//			} catch (final Exception e) {
//				// AuditLog.makeString("CAMERA","ERROR");
//
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						AuditLog.sendToJSON();
//						// webView.loadUrl("javascript:appzillon.plugin.addauditdata ('"+
//						// AuditLog.getAuditString("CAMERA") + "');");
//						webView.loadUrl("javascript:jsonParseExceptionCallBack"
//								+ "("
//								+ "{\"errorCode\":\"APZ-CNT-035\",\"errorDescription\":\""
//								+ e.getMessage() + "\"}" + ");");
//					}
//				});
//				return;
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-100','','');");
//				}
//			});
//		}
//	}

//	@JavascriptInterface
//	public final void startNotePad(String refNO) {
//		ServerLog.d(TAG, "startNotePad refNo : "+refNO);
//		final Intent intent = new Intent(context, CreateNote.class);
//		bundle = new Bundle();
//		bundle.putString("refNO", refNO);
//		intent.putExtras(bundle);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		this.context.startActivity(intent);
//	}

	@JavascriptInterface
	public final void startDatePicker(final String elementID) {
		ApzLogger.d(TAG, "startDatePicker");
		final LayoutInflater factory = LayoutInflater.from(context);
		final DatePicker picker = (DatePicker) factory.inflate(R.layout.date_picker, null);
		final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setTitle(context.getString(R.string.datepicker_set_title));
		dialog.setView(picker);
		dialog.setPositiveButton(context.getString(R.string.datepicker_set),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("Set Date!!!!!");
						final DatePicker datePicker = (DatePicker) picker.findViewById(R.id.datepicker);
						final int dayOfMonth = datePicker.getDayOfMonth();
						final int monthOfYear = datePicker.getMonth() + 1;
						final int year = datePicker.getYear();
						//ApzLogger.d(TAG,"dayOfMonth : " + dayOfMonth +", monthOfYear : " + monthOfYear + ", year : " + year);
						String month = "";
						if (monthOfYear < 10) {
							month = "0" + String.valueOf(monthOfYear);
						} else {
							month = String.valueOf(monthOfYear);
						}
						final String date = year + "-" + month + "-"+ dayOfMonth;
						webView.loadUrl("javascript:(function() {" + "$('#"
								+ elementID + "').val('');" + "$('#"
								+ elementID + "').val('" + date + "');})()");
						dialog.dismiss();
					}
				});
		dialog.setNegativeButton(context.getString(R.string.datepicker_clear),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						webView.loadUrl("javascript:(function() {" + "$('#"	+ elementID + "').val('');})()");
						dialog.dismiss();
					}
				});
		final Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable() {
			public void run() {
				ApzLogger.i(TAG,"keyboard visible");
				activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				ApzLogger.i(TAG,"keyboard hidden");
				dialog.show();
			}
		});

	}
	
	//Abhishek 09 September 2015, Native loader is not needed, so commented START

	//Abhishek 09 September 2015, Native loader is not needed, so commented END

	/**
	 * For storing name value pairs to preference file.
	 *
	 * @param jsonString
	 *            contains name value pair to be saved to file
	 * @param nodeVal
	 *            contains keys
	 */
	@JavascriptInterface
	public final void setSettingsValue(final String jsonString) {
		settings = activity.getSharedPreferences(properties, 0);
		//ApzLogger.d(TAG,"JSON for setting is" +jsonString);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject jsonResult = new JSONObject(jsonString);
					Iterator<String> settingskey = jsonResult.keys();
					while (settingskey.hasNext()) {
						String name = (String) settingskey.next();
						String keyval = jsonResult.getString(name);
						UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,name, keyval, settings);
					}
				} catch (Exception e) {

				}
			}
		});
	}
	
	/**
	 * For storing name value pairs to preference file.
	 *
	 * @param key
	 *            contains key to be saved
	 * @param value
	 *            contains value related to respective key 
	 */
	@JavascriptInterface
	public final void setSetting(final String key, final String value) {
		settings = activity.getSharedPreferences(properties, 0);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,key, value, settings);  // 3.2 changes
				} catch (Exception e) {

				}
			}
		});
	}

	/**
	 * To retrieve single value from preferences by passing single KEY
	 *
	 * @param keyName
	 * @return String as Key Value
	 */
	@JavascriptInterface
	public final String getSettingsValue(String keyName) {
		String defaultval = "";
		settings = activity.getSharedPreferences(properties, 0);
		String str = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,keyName, defaultval, settings);
		ApzLogger.d(TAG,"getSettingsValue for "+keyName+" : "+str);
		return str;
	}

	/**
	 * To retrieve all values from preferences by passing keys
	 *
	 * @param nodeVal
	 * @return String as Key Value
	 */
	@JavascriptInterface
	public final String getAllSettingsValue(String nodeVal) {
		String defaultval = "";
		StringBuilder sb = new StringBuilder();
		settings = activity.getSharedPreferences(properties, 0);
		sb.append("{");
		int i = 0;
		try {
			JSONObject settingjson = new JSONObject(nodeVal);
			Iterator<String> settingskey = settingjson.keys();
			while (settingskey.hasNext()) {
				String name = settingskey.next();
				String str = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,name, defaultval, settings);
				sb.append("\"" + name + "\"");
				sb.append(":");
				sb.append("\"" + str + "\"");
				i++;
				if (i < settingjson.length()) {
					sb.append(",");
				}
			}
			sb.append("}");
		} catch (JSONException e) {

		}
		return sb.toString();
	}

	@JavascriptInterface
	public void loadFirstPage() {
		ApzLogger.d(TAG, "loadFirstPage");
		String isWiped = UserSettings.getwiped(settings);
		if (isWiped.equals("NO")) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.start();");  // 3.2 changes
				}
			});
		} else {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:(function() { alert('Appzillon has been Disabled , Reinstall the Application');})()");
				}
			});
		}
	}

	/**
	 * To provide OTP to javascript layer
	 *
	 * @param userId
	 * @param pwd
	 */
	@JavascriptInterface
	public String getOTP(String userId, String pin , String timeStamp) {
		ApzLogger.d(TAG, "getOTP");
		settings = context.getSharedPreferences(properties, 0);
		//SimpleDateFormat formatter;
		String hashPin = null;
		//formatter = new SimpleDateFormat("EEE, dd MM yyyy HH:mm:ss");

		//final Calendar cal = Calendar.getInstance();
		//final String l_time = formatter.format(cal.getTime());
		try {
			hashPin = hashSHA256(pin, userId + StringUtils.getString(StringUtils.SERVER_TOKEN));
			//ApzLogger.i(TAG,"userDetails: PIN : " + pin + ",USER ID : " + userId + ",SERVER TOKEN : "+ StringUtils.getString(StringUtils.SERVER_TOKEN) + ", LOGIN HASH : " + hashPin);
		} catch (NoSuchAlgorithmException e) {


		}
		final HashXor hashXor = new HashXor();
		//ApzLogger.d(TAG, "HASHKEY 1 : "+ UserSettings.getIMEI(settings));
		//ApzLogger.d(TAG, "HASHKEY 2 : "+ UserSettings.getIMSI(settings));
		final String OTP = hashXor.hashValue(UserSettings.getIMEI(settings), UserSettings.getIMSI(settings), "", userId, hashPin, timeStamp);
		//ApzLogger.d("OTP", OTP);
		return OTP;
	}
//
//	@JavascriptInterface
//    public void recordVideo(String jsonObj) {
//		   if (VideoCaptureActivity.isDummy) {
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						// webView.loadUrl("javascript:appzillon.util.displayMessage('e','APZ-CNT-100','','');");
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-100','','');");  // change later
//					}
//				});
//			}else {
//				ServerLog.d(TAG, "record Video");
//				String fileName = "";
//				String successCallback = "";
//				String failureCallback = "";
//				String overwrite = "";
//				try {
//					JSONObject json = new JSONObject(jsonObj);
//					fileName = json.getString("fileName");
//					successCallback = json.getString("successCallback");
//					failureCallback = json.getString("failureCallback");
//					overwrite = json.getString("overwrite");
//				} catch (JSONException e) {
//
//					activity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//						}
//					});
//				}
//				if (fileName.equals("")) {
//					Toast.makeText(context,
//							context.getString(R.string.video_filename_empty),
//							Toast.LENGTH_SHORT).show();
//				} else {
//					final Intent intent = new Intent(context,
//							VideoCaptureActivity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//					intent.putExtra("fileName", fileName);
//					intent.putExtra("successCallback", successCallback);
//					intent.putExtra("failureCallback", failureCallback);
//					intent.putExtra("overwrite", overwrite);
//					context.startActivity(intent);
//				}
//			}		
//    }

	/**
	 * To initialize localstorage
	 */
	@JavascriptInterface
	public void initLocalStorage() {   // 3.2 changes
		ApzLogger.d(TAG, "initLocalStorage");
		settings = context.getSharedPreferences(properties, 0);
		String isFirstTime = UserSettings.getIsAppFirstTime(AppzillonMainScreen.APP_NAME,settings);
		if (isFirstTime.equals("YES")) {
			UserSettings.setIsAppFirstTime(AppzillonMainScreen.APP_NAME,"NO", settings);
		}
		// Abhishek storing orientation 
		final String orientation ;
		if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			orientation = "landscapelo";
		}else{
			orientation = "portraitlo";			
		}
		//ApzLogger.i(TAG, "DEBUGREQUIRED : "+AppzillonMainScreen.APP_REMOTE_DEBUG);
		activity.runOnUiThread(new Runnable() {
			public void run() {
//				webView.loadUrl("javascript:(function() { "
//						+ "localStorage.setItem('UPDATEREQUEST','" + AppzillonMainScreen.UPDATE_REQUEST + "'); "
//						+ "localStorage.setItem('DEBUGREQUIRED','" + AppzillonMainScreen.APP_REMOTE_DEBUG + "'); " + "})()");
				//Abhishek bud id 4911 START
				webView.loadUrl("javascript:appzillon.plugin.setDeviceOrientation('"+orientation+"');");
				//Abhishek bud id 4911 END
			}
		});

	}
	/**
	 * Used to return device type
	 * 
	 * @return
	 */
	private final char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8','9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public String toHexString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			int c = ((b[i]) >>> 4) & 0xf;
			sb.append(hex[c]);
			c = (b[i] & 0xf);
			sb.append(hex[c]);
		}
		return sb.toString();
	}

	/* To encrypt ptext with salt using SHA-256 hash Algo */
	public String hashSHA256(String ptext, String psalt) throws NoSuchAlgorithmException {
		String pTextSalt = ptext + psalt;
		String pHashedText = "";
		byte[] ptextSaltbyte = new byte[200];
		byte[] hashbyte = new byte[200];
		MessageDigest msgdigest = MessageDigest.getInstance("SHA-256");
		try {
			ptextSaltbyte = pTextSalt.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			ApzLogger.e(TAG,"Unsupported character set");
		}
		msgdigest.reset();
		msgdigest.update(ptextSaltbyte);
		hashbyte = msgdigest.digest();
		pHashedText = toHexString(hashbyte);
		//ApzLogger.i(TAG,"EncryptHash : "+ pHashedText);
		return pHashedText;
	}

	/* Returns server token */
	/* Initialise Settings Default Value */
	@SuppressWarnings("unchecked")
	@JavascriptInterface
	public void setSettingsDefaultVal(String settingsJson) {
		ApzLogger.i(TAG,"setSettingsDefaultVal");
		settings = context.getSharedPreferences(properties, 0);
		
//Abhishek 24 April 2015, Setting local storage again on login after logout START
		final boolean settingsPresent = UserSettings.getAppDefaultSettingPresent(AppzillonMainScreen.APP_NAME,"defaultSettingPresent","false", settings).equalsIgnoreCase("false");
		
			try {
				JSONObject set = new JSONObject(settingsJson);
				String jsonRoot = set.names().toString().replaceAll("[\\[\\]\"]", ""); // returns root
				UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,"SettingsRoot", jsonRoot, settings);
				final JSONObject setting = set.getJSONObject(jsonRoot);
				final Iterator<String> iterator = setting.keys();

				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						while (iterator.hasNext()) {
							String setval = (String) iterator.next();
							try {
								//Abhishek 24 April 2015, if true then save from Settins.json file, like old way START
								if (settingsPresent) {
									UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,setval,setting.getString(setval), settings);
									webView.loadUrl("javascript:(function() { "	+ "localStorage.setItem('" + setval+ "','" + setting.getString(setval)+ "'); " + "})()");
								}
								//Abhishek 24 April 2015, if true then save from Settins.json file, like old way END
								
								//Abhishek 24 April 2015, if false then save from preferences giving value of settings.json as default value , new approach START
								else{
									String prefValue = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,setval,setting.getString(setval), settings);
//									ApzLogger.d(TAG, "setval : "+setval+" ,prefValue : "+prefValue);
//									webView.loadUrl("javascript:(function() { "	+ "localStorage.setItem('" + setval+ "','" + prefValue+ "'); " + "})()");
								}
								//Abhishek 24 April 2015, if false then save from preferences giving value of settings.json as default value , new approach END
								
							} catch (JSONException e) {

								return;
							} finally {
								UserSettings.setAppDefaultSettingPresent(AppzillonMainScreen.APP_NAME,"defaultSettingPresent", "true",settings);
							}
						}
					}
				});

			} catch (JSONException e) {

			}

//		}
//Abhishek 24 April 2015, Setting local storage again  on login after logout END
	}	
	

	/* Returns current locale of the device */
//	@JavascriptInterface
//	public void getDeviceLocale(String jsonText) {
//		if (Localization.isLocalization()) {
//			AuditLog.makeString("DEVICE Locale","START");
//			ServerLog.d(TAG, "getDeviceLocale");
//			/* Localization */
//			Localization mLocale = null;
//			JSONObject localeJson = null;
//			try {
//				localeJson = new JSONObject(jsonText);
//
//			} catch (final JSONException e) {
//
//
//				return;
//			}
//			mLocale = new Localization(activity, webView);
//			mLocale.getDeviceLocale(localeJson);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-049','','');");
//				}
//			});
//		}
//
//	}

	/**
	 * Starts Accelerometer Sensor(will be called from javascript)
	 */
//	@JavascriptInterface
//	public void startAccelerometer(String json) {
//		if (AccelerometerPlugin.isAccelerometer()) {
////			AuditLog.makeString("ACCELEROMETER","START");
//			ServerLog.d(TAG, "startAccelerometer");
//			JSONObject accJson = null;
//			try {
//				accJson = new JSONObject(json);
//				SUCCESS_CALLBACK = accJson.getString("successCallback");
//				FAILURE_CALLBACK = accJson.getString("failureCallback");
//			} catch (final JSONException e) {
//
//				activity.runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//				return;
//			}
//			if (mAccelerometerPlugin == null) {
//				mAccelerometerPlugin = new AccelerometerPlugin(context,	activity, webView);
//				mAccelerometerPlugin.startAccelerometerLister(accJson);
//			} else if (AccelerometerPlugin.isRunning) {
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
////						AuditLog.makeString("ACCELEROMETER","Is Running");
//						webView.loadUrl("javascript:"
//								+ FAILURE_CALLBACK
//								+ "("
//								+ "{\"errorCode\":\"APZ-CNT-015\",\"errorDescription\":\"Acceleromerter Running.\"}"
//								+ ");");
//					}
//				});
//			} else {
//				mAccelerometerPlugin.startAccelerometerLister(accJson);
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-108','','');");
//				}
//			});
//		}
//	}
//
//	/**
//	 * Starts Accelerometer Sensor(will be called from javascript)
//	 */
//	@JavascriptInterface
//	public void stopAccelerometer(String jsonText) {
//		JSONObject accStop = null;
//		ServerLog.d(TAG, "stopAccelerometer");
////		AuditLog.makeString("ACCELEROMETER","STOP");
//		try {
//			accStop = new JSONObject(jsonText);
//			SUCCESS_CALLBACK = accStop.getString("successCallback");
//			FAILURE_CALLBACK = accStop.getString("failureCallback");
//		} catch (final JSONException ex) {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//				}
//			});
//			return;
//		}
//
//		if (AccelerometerPlugin.isRunning) {
//			mAccelerometerPlugin.pause();
//			mAccelerometerPlugin = null;
//			try {
//				if (AccelerometerPlugin.myTimer != null) {
//					AccelerometerPlugin.myTimer.cancel();
//					AccelerometerPlugin.myTimer = null;
//					AccelerometerPlugin.isTimerRunning = false;
//				}
//			} catch (Exception e) {
//				Log.i("TimerProblem", "exception in closing the timer "+e.toString());
//			}
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					AuditLog.sendToJSON();
////					AuditLog.makeString("ACCELEROMETER","Stopped");
//					webView.loadUrl("javascript:" + SUCCESS_CALLBACK + "("+ "{\"successMessage\":\"Accelerometer Stopped\"}"+ ");");
//
//				}
//			});
//		} else {
//			if (mAccelerometerPlugin != null)
//				mAccelerometerPlugin = null;
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
////					AuditLog.makeString("ACCELEROMETER","already stopped");
//					webView.loadUrl("javascript:"
//							+ FAILURE_CALLBACK
//							+ "("
//							+ "{\"errorCode\":\"APZ-CNT-061\",\"errorDescription\":\"Acceleromerter already stopped\"}"
//							+ ");");
//
//				}
//			});
//		}
//	}

	/**
	 * Will be called from destroy() method of activity to release resources
	 * held by the application like sensor
	 */
	public void releseResources() {
		/* Unregisters the AccelrometerSensor on application destroy */

//		/* For Compass Listener */
//		if (mAccelerometerPlugin != null) {
//			mAccelerometerPlugin.pause();
//			mAccelerometerPlugin = null;
//		}
//		/* For Compass Listener */
//		if (mCompassPlugin != null) {
//			mCompassPlugin.pause();
//			mCompassPlugin = null;
//		}
//		/* Fro GPS Listener */
//		if (mGpsLocator != null) {
//			mGpsLocator.pause();
//			mGpsLocator = null;
//		}
//		/* Fro AudioPlugin */
//		if (mAudioPlugin != null) {
//			mAudioPlugin.releaseAudioPlayer();
//			mAudioPlugin.releaseMediaRecorder();
//			mAudioPlugin = null;
//		}
	}

	/**
	 * Starts Compasslistener
	 *
	 * @param json
	 */
//	@JavascriptInterface
//	public void startCompassListener(String json) {
//		if (CompassPlugin.isCompassPlugin()) {
//			ServerLog.d(TAG, "startCompassListener");
////			AuditLog.makeString("COMPASS","START");
//			JSONObject compJson = null;
//			try {
//				compJson = new JSONObject(json);
//				SUCCESS_CALLBACK = compJson.getString("successCallback");
//				FAILURE_CALLBACK = compJson.getString("failureCallback");
//
//			} catch (final JSONException e) {
//				activity.runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//				return;
//			}
//			if (mCompassPlugin == null) {
//				mCompassPlugin = new CompassPlugin(context, activity, webView);
//				mCompassPlugin.startCompass(compJson);
//			} else if (CompassPlugin.isRunning) {
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
////						AuditLog.makeString("COMPASS","Compass Running");
//						webView.loadUrl("javascript:"
//								+ FAILURE_CALLBACK
//								+ "("
//								+ "{\"errorCode\":\"APZ-CNT-016\",\"errorDescription\":\"Compass Running.\"}"
//								+ ");");
//					}
//				});
//			} else {
//				mCompassPlugin.startCompass(compJson);
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-112','','');");
//				}
//			});
//		}
//
//	}

	/**
	 * Stops CompassListener.
	 *
	 * @param json
	 */
//	@JavascriptInterface
//	public void stopCompassListener(String jsonText) {
//		ServerLog.i(TAG,"Stop Compass :" + jsonText);
////		AuditLog.makeString("COMPASS","STOP");
//		JSONObject compStop = null;
//		try {
//			compStop = new JSONObject(jsonText);
//			SUCCESS_CALLBACK = compStop.getString("successCallback");
//			FAILURE_CALLBACK = compStop.getString("failureCallback");
//		} catch (final Exception ex) {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//				}
//			});
//			return;
//		}
//
//		if (CompassPlugin.isRunning) {
//			mCompassPlugin.pause();
//			mCompassPlugin = null;
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					AuditLog.sendToJSON();
////					AuditLog.makeString("COMPASS","Compass Stopped");
//					webView.loadUrl("javascript:" + SUCCESS_CALLBACK + "("+ "{\"successMessage\":\"Compass Stopped\"}" + ");");
//				}
//			});
//		} else {
//			if (mCompassPlugin != null)
//				mCompassPlugin = null;
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
////					AuditLog.makeString("COMPASS","Compass already Stopped");
//					webView.loadUrl("javascript:"
//							+ FAILURE_CALLBACK
//							+ "("
//							+ "{\"errorCode\":\"APZ-CNT-063\",\"errorDescription\":\"Compass already stopped\"}"
//							+ ");");
//				}
//			});
//		}
//	}
//
//	@JavascriptInterface
//	public void startGpsLocationListener(String json) {
//		if (GpsLocator.isGpsLocator()) {
//			ServerLog.d(TAG, "startGpsLocationListener");
////			AuditLog.makeString("GPS LOCATOR","START");
//			JSONObject locJson = null;
//			try {
//				locJson = new JSONObject(json);
//				SUCCESS_CALLBACK = locJson.getString("successCallback");
//				FAILURE_CALLBACK = locJson.getString("failureCallback");
//			} catch (final JSONException e) {
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//				return;
//			}
//			if (mGpsLocator == null) {
//				mGpsLocator = new GpsLocator(context, activity, webView);
//				mGpsLocator.getCoordinates(locJson);
//			} else if (GpsLocator.isRunning) {
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
////						AuditLog.makeString("GPS LOCATOR","GPS Running");
//						webView.loadUrl("javascript:"
//								+ FAILURE_CALLBACK
//								+ "("
//								+ "{\"errorCode\":\"APZ-CNT-017\",\"errorDescription\":\"GPS Running.\"}"
//								+ ");");
//					}
//				});
//			} else {
//				mGpsLocator.getCoordinates(locJson);
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-105','','');");
//				}
//			});
//		}
//	}
//
//	/**
//	 * Stops Location Update Listener
//	 */
//	@JavascriptInterface
//	public void stoptGpsLocationListener(String jsonText) {
//		JSONObject gpsStop = null;
//		ServerLog.d(TAG, "stoptGpsLocationListener");
////		AuditLog.makeString("GPS LOCATOR","STOP");
//		try {
//			gpsStop = new JSONObject(jsonText);
//			SUCCESS_CALLBACK = gpsStop.getString("successCallback");
//			FAILURE_CALLBACK = gpsStop.getString("failureCallback");
//		} catch (final Exception ex) {
//
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//				}
//			});
//			return;
//		}
//		if (GpsLocator.isRunning) {
//			mGpsLocator.stop();
//			mGpsLocator = null;
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					AuditLog.sendToJSON();
////					AuditLog.makeString("GPS LOCATOR","GPS Stopped");
//					webView.loadUrl("javascript:" + SUCCESS_CALLBACK + "("+ "{\"successMessage\":\"GPS Stopped\"}" + ");");
//				}
//			});
//		} else {
//			if (mGpsLocator != null)
//				mGpsLocator = null;
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
////					AuditLog.makeString("GPS LOCATOR","GPS already stopped");
//					webView.loadUrl("javascript:"
//							+ FAILURE_CALLBACK
//							+ "("
//							+ "{\"errorCode\":\"APZ-CNT-057\",\"errorDescription\":\"GPS already stopped\"}"
//							+ ");");
//				}
//			});
//		}
//	}
//
	/**
	 * Retrives device info. like screenSize,resolution,networkType,Os Version
	 * etc
	 *
	 * @param json
	 */
//	@JavascriptInterface
//	public void getDeviceInformation(String json) {
//		if (DeviceInfo.isDeviceInfo()) {
//			ServerLog.d(TAG, "getDeviceInformation");
//			AuditLog.makeString("DEVICE INFO","START");
//			/* Device Info */
//			DeviceInfo mDevInfo = null;
//			JSONObject deviceJson = null;
//			try {
//				deviceJson = new JSONObject(json);
//			} catch (final JSONException e) {
//
//
//			}
//			mDevInfo = new DeviceInfo(context, activity, webView);
//			mDevInfo.getDeviceDetails(deviceJson);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-103','','');");
//				}
//			});
//		}
//	}

//	/**
//	 * Calls getAllPushNotes()method that retrieves push messages from local
//	 * sqlite database
//	 *
//	 * @param json
//	 */
//
//
//
//	/**
//	 * Calls logRuntimeMessage() method to log messages on logcat console
//	 *
//	 * @param jsonText
//	 */
//	@JavascriptInterface
//	public void logRuntimeDebugMsg(String jsonText) {
//		if (RuntimeLogger.isRuntimeLogger()) {
//			JSONObject logJson = null;
//			try {
//				logJson = new JSONObject(jsonText);
//				if (mRuntimeLogger == null) {
//					mRuntimeLogger = new RuntimeLogger(context, activity,webView);
//					mRuntimeLogger.logRuntimeMessage(logJson);
//				} else {
//					mRuntimeLogger.logRuntimeMessage(logJson);
//				}
//			} catch (JSONException e) {
//
//				return;
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-000','','');");
//				}
//			});
//		}
//	}

	/**
	 * Launches installed Google map present in device
	 */
//	@JavascriptInterface
//	public void launchMap(final String jsonText) {
//		if (Map.IsMap()) {
//			ServerLog.d(TAG, "launchMap");
//			AuditLog.makeString("MAP","START");
//			Intent intent = new Intent(context, Map.class);
//			intent.putExtra("mapval", jsonText);
//			activity.startActivity(intent);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-107','','');");
//				}
//			});
//		}
//	}

//	@JavascriptInterface
//	public void launchEmail(String emailJsonStr) {
//		if (EmailPlugin.IsEmail()) {
//			ServerLog.d(TAG, "launchEmail");
//			AuditLog.makeString("EMAIL","START");
//			EmailPlugin mEmailPlugin = null;
//			JSONObject mailJson = null;
//			try {
//				mailJson = new JSONObject(emailJsonStr);
//			} catch (final JSONException e) {
//
//				return;
//			}
//			mEmailPlugin = new EmailPlugin(context, activity, webView);
//			mEmailPlugin.sendmail(mailJson);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-106','','');");
//				}
//			});
//		}
//	}

	/**
	 * Launches barcode activity
	 *
	 * @param jsonText
	 */
//	@JavascriptInterface
//	public void barcodeScanner(String jsonText) {
//		if (BarcodeActivity.isBarcodeActivity()) {
//			ServerLog.d(TAG, "barcodeScanner");
//			AuditLog.makeString("BARCODE","START");
//			JSONObject barcodeJson = null;
//			try {
//				barcodeJson = new JSONObject(jsonText);
//				SUCCESS_CALLBACK = barcodeJson.getString("successCallback");
//				FAILURE_CALLBACK = barcodeJson.getString("failureCallback");
//			} catch (final JSONException e) {
//
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//				return;
//			}
//			Intent camera = new Intent(context, BarcodeActivity.class);
//			activity.startActivityForResult(camera, BARCODE_SCAN);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-110','','');");
//				}
//			});
//		}
//	}

//	@JavascriptInterface
//	public void contactDeleteOperation(String jsonText) {
//		if (Contacts.isContacts()) {
//			ServerLog.d(TAG, "contactDeleteOperation");
//			AuditLog.makeString("CONTACT","DELETE");
//			Contacts deleteContact = null;
//			JSONObject deleteContactJson = null;
//			try {
//				deleteContactJson = new JSONObject(jsonText);
//				deleteContact = new Contacts(context, activity, webView);
//				deleteContact.deleteContact(deleteContactJson);
//			} catch (JSONException e) {
//
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-113','','');");
//				}
//			});
//		}
//	}
//
//	@JavascriptInterface
//	public void contactSearchOperation(String jsonText) {
//		if (Contacts.isContacts()) {
//			ServerLog.d(TAG, "contactSearchOperation");
//			AuditLog.makeString("CONTACT","SEARCH");
//			Contacts searchContact = null;
//			JSONObject searchContactJson = null;
//			try {
//				searchContactJson = new JSONObject(jsonText);
//				searchContact = new Contacts(context, activity, webView);
//				searchContact.searchContacts(searchContactJson);
//			} catch (JSONException e) {
//
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-113','','');");
//				}
//			});
//		}
//	}
//
//	@JavascriptInterface
//	public void contactAddOperation(String jsonText) {
//		if (Contacts.isContacts()) {
//			ServerLog.d(TAG, "contactAddOperation");
//			AuditLog.makeString("CONTACT","ADD");
//			Contacts addContact = null;
//			JSONObject addContactJson = null;
//			try {
//				addContactJson = new JSONObject(jsonText);
//				addContact = new Contacts(context, activity, webView);
//				addContact.addContacts(addContactJson);
//			} catch (JSONException e) {
//
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-113','','');");
//				}
//			});
//		}
//	}
//	
//	//Abhishek bug 2970 : Added javascriptInterface
//	@JavascriptInterface
//	public void contactEditOperation(String jsonText) {
//		if (Contacts.isContacts()) {
//			ServerLog.d(TAG, "contactEditOperation");
//			AuditLog.makeString("CONTACT","EDIT");
//			System.out.println("contactEditOperation" + jsonText);
//			Contacts editContact = null;
//			JSONObject editContactJson = null;
//			try {
//				editContactJson = new JSONObject(jsonText);
//				editContact = new Contacts(context, activity, webView);
//				editContact.editContacts(editContactJson);
//			} catch (JSONException e) {
//
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-113','','');");
//				}
//			});
//		}
//	}
//
//	@JavascriptInterface
//	public void lockScreenRotation(String jsonText) {
//		ServerLog.d(TAG, "lockScreenRotation");
//		AuditLog.makeString("LOCK SCREEN ROTATION","LOCK");
//		ScreenRotation rotation = null;
//		JSONObject lockJson = null;
//		try {
//			lockJson = new JSONObject(jsonText);
//			rotation = new ScreenRotation(context, activity, webView);
//			rotation.enableLock(lockJson, false);
//		} catch (JSONException e) {
//
//		}
//	}
//
//	@JavascriptInterface
//	public void unLockScreenRotation(String jsonText) {
//		ServerLog.d(TAG, "unLockScreenRotation");
//		AuditLog.makeString("LOCK SCREEN ROTATION","UNLOCK");
//		ScreenRotation rotation = null;
//		JSONObject unLockJson = null;
//		try {
//			unLockJson = new JSONObject(jsonText);
//			rotation = new ScreenRotation(context, activity, webView);
//			rotation.disableLock(unLockJson);
//		} catch (JSONException e) {
//
//		}
//	}
//
//	@JavascriptInterface
//	public void calendarPlugin(String jsonText, String dateFormat) {
//		if (CalendarPlugin.isCalendarPlugin()) {
//			AuditLog.makeString("CALENDER","START");
//			ServerLog.i(TAG,"Calendar : " + jsonText);
//			CalendarPlugin calendar = null;
//			JSONObject calendarJson = null;
//			try {
//				calendarJson = new JSONObject(jsonText);
//				calendar = new CalendarPlugin(context, activity, webView);
//				calendar.calendarOperation(calendarJson, dateFormat);
//			} catch (JSONException e) {
//
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-109','','');");
//				}
//			});
//		}
//	}
//
//	@JavascriptInterface
//	public void calendarEventDelete(String jsonText, String dateFormat) {
//		if (CalendarPlugin.isCalendarPlugin()) {
////			AuditLog.makeString("CALENDER","calendarEventDelete");
//			ServerLog.i(TAG,"Calendar : " + jsonText);
//			CalendarPlugin calendar = null;
//			JSONObject calendarJson = null;
//			try {
//				calendarJson = new JSONObject(jsonText);
//				calendar = new CalendarPlugin(context, activity, webView);
//				calendar.delete(calendarJson, dateFormat);
//			} catch (JSONException e) {
//
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-109','','');");
//				}
//			});
//		}
//	}
//
//	/**
//	 * Used to perform file browse
//	 *
//	 * @param jsonText
//	 */
//	@JavascriptInterface
//	public void browserPlugin(String jsonText) {
//		if (DirectoryBrowser.isFileBrowserPlugIn()) {
//			AuditLog.makeString("FILE BROWSER","START");
//			ServerLog.i(TAG,"browserPlugin : " + jsonText);
//			JSONObject browserJson = null;
//			String fileCategory = null;
//			try {
//				browserJson = new JSONObject(jsonText);
//				fileCategory = browserJson.getString("fileCategory");
//				SUCCESS_CALLBACK = browserJson.getString("successCallback");
//				FAILURE_CALLBACK = browserJson.getString("failureCallback");
//				if (fileCategory.equals("AUDIO")) {
////					AuditLog.makeString("FILE BROWSER","Audio");
//					isOpenable = browserJson.getString("openFile");
//					Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
//					activity.startActivityForResult(i, RESULT_LOAD_AUDIO);
//				} else if (fileCategory.equals("VIDEO")) {
////					AuditLog.makeString("FILE BROWSER","Video");
//					isOpenable = browserJson.getString("openFile");
//					Intent i = new Intent(Intent.ACTION_PICK, null);
//					i.setType("video/*");
//					activity.startActivityForResult(i, RESULT_LOAD_VIDEO);
//				} else if (fileCategory.equals("PHOTO")) {
////					AuditLog.makeString("FILE BROWSER","Photo");
//					isOpenable = browserJson.getString("openFile");
//					Intent i = new Intent(Intent.ACTION_PICK, null);
//					i.setType("image/*");
//					activity.startActivityForResult(i, RESULT_LOAD_IMAGE);
//				} else if (fileCategory.equals("DEFAULT")) {
////					AuditLog.makeString("FILE BROWSER","Default");
//					Intent browserIntent = new Intent(context,DirectoryBrowser.class);
//					browserIntent.putExtra("location", "");
//					browserIntent.putExtra("root", "DEFAULT");
//					browserIntent.putExtra("filter", "");
//					browserIntent.putExtra("openFile",browserJson.getString("openFile"));
//					activity.startActivityForResult(browserIntent, BROWSE_FILE);
//				} else if (fileCategory.equals("EXTERNAL")) {
////              AuditLog.makeString("FILE BROWSER","EXTERNAL");
//					Intent browserIntent = new Intent(context,DirectoryBrowser.class);
//					browserIntent.putExtra("location", "EXTERNAL");
//					browserIntent.putExtra("root", "DEFAULT");
//					browserIntent.putExtra("filter", "");
//					browserIntent.putExtra("openFile",browserJson.getString("openFile"));
//					activity.startActivityForResult(browserIntent, BROWSE_FILE);
//				} else {
////					AuditLog.makeString("FILE BROWSER","No Selection");
//					Intent browserIntent = new Intent(context,DirectoryBrowser.class);
//					browserIntent.putExtra("location",browserJson.getString("location"));
//					browserIntent.putExtra("filter",browserJson.getString("filter"));
//					browserIntent.putExtra("openFile",browserJson.getString("openFile"));
//					activity.startActivityForResult(browserIntent, BROWSE_FILE);
//				}
//			} catch (JSONException e) {
//
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//			} catch (Exception e) {
//
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-104','','');");
//				}
//			});
//		}
//	}
//
	@JavascriptInterface
	public void encryptData(String jsonText) {
		if (EncryptDecryptUtility.isEncryptDecryptUtility()) {
			AuditLog.makeString("ENCRYPT DATA","START");
//			ApzLogger.i(TAG,"encryptData : " + jsonText);
			JSONObject encryptDataJson = null;
			final String id;
			String stringToEncrypt = null;
			String key = null;
			try {
				encryptDataJson = new JSONObject(jsonText);
				id = encryptDataJson.getString("id").trim();
				key = encryptDataJson.getString("key").trim();
				stringToEncrypt = encryptDataJson.getString("stringToEncrypt").trim();
				SUCCESS_CALLBACK = encryptDataJson.getString("successCallback").trim();
				FAILURE_CALLBACK = encryptDataJson.getString("failureCallback").trim();
			} catch (JSONException e) {

				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
					}
				});
				return;
			}
			if (key.length() <= 16) {
				key += paddingMask.substring(0, 16 - key.length());
			}
			//Abhishek 24 April 2015, if key is greater then 16 make it of 16 length, check Nagaraj mail START
			else {
				key = key.substring(0, 16);
			}
//			Log.i(TAG, "Key length : "+key.length());
			//Abhishek 24 April 2015, if key is greater then 16 make it of 16 length, check Nagaraj mail END
			byte[] iv = getIV(key);
			String finalSalt = getSalt(key);
			final String encryptedText = EncryptDecryptUtility.encryptString(key,stringToEncrypt, finalSalt);
//			Log.i("Abhishek", "encryptedText : "+encryptedText);
			if (encryptedText != null) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AuditLog.sendToJSON();
//						AuditLog.makeString("ENCRYPT DATA","Success");
						webView.loadUrl("javascript:" + SUCCESS_CALLBACK + "("
								+ "{\"id\":\"" + id
								+ "\",\"encryptedString\":\"" + encryptedText
								+ "\"}" + ");");
					}
				});
			} else {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AuditLog.sendToJSON();
//						AuditLog.makeString("ENCRYPT DATA","Encryption Fail");
						webView.loadUrl("javascript:"
								+ FAILURE_CALLBACK
								+ "("
								+ "{\"errorCode\":\"APZ-CNT-048\",\"errorDescription\":\"Encryption fail\"}"
								+ ");");
					}
				});
			}
		} else {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-102','','');");
				}
			});
		}
	}

	@JavascriptInterface
	public void decryptData(String jsonText) {
		if (EncryptDecryptUtility.isEncryptDecryptUtility()) {
			AuditLog.makeString("DECRYPT DATA","START");
//			ApzLogger.i(TAG,"decryptData : " + jsonText);
			JSONObject decryptDataJson = null;
			final String id;
			String stringToDecrypt = null;
			String key = null;
			try {
				decryptDataJson = new JSONObject(jsonText);
				id = decryptDataJson.getString("id").trim();
				key = decryptDataJson.getString("key").trim();
				stringToDecrypt = decryptDataJson.getString("stringToDecrypt").trim();
				SUCCESS_CALLBACK = decryptDataJson.getString("successCallback").trim();
				FAILURE_CALLBACK = decryptDataJson.getString("failureCallback").trim();
			} catch (JSONException e) {

				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
					}
				});
				return;
			}
			/**
			 * if key length<16 then make it 16 by appending '$'
			 */
			if (key.length() <= 16) {
				key += paddingMask.substring(0, 16 - key.length());
			}
			//Abhishek 24 April 2015, if key is greater then 16 make it of 16 length, check Nagaraj mail START
			else {
				key = key.substring(0, 16);
			}
//			Log.i(TAG, "Key length : "+key.length());
			//Abhishek 24 April 2015, if key is greater then 16 make it of 16 length, check Nagaraj mail END
			byte[] iv = getIV(key);
			String finalSalt = getSalt(key);
			final String decryptedString = EncryptDecryptUtility.decryptString( key,stringToDecrypt, finalSalt);
//			Log.i("Abhishek", "decryptedString : "+decryptedString);
			if (decryptedString != null) {
				
				//Abhishek 22 April 2015, for sending back decrypted json START
				final JSONObject obj = new JSONObject();
				try {
					obj.put("id", id);
					obj.put("decryptedString", decryptedString);
				} catch (JSONException e) {
					// TODO Auto-generated catch block

				}
				//Abhishek 22 April 2015, for sending back decrypted json END
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AuditLog.sendToJSON();
//						AuditLog.makeString("DECRYPTION DATA","Success");
						//Abhishek 22 April 2015, for sending back decrypted json START
						webView.loadUrl("javascript:" + SUCCESS_CALLBACK + "("+obj+");");
						//Abhishek 22 April 2015, for sending back decrypted json END
					}
				});
			} else {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AuditLog.sendToJSON();
						webView.loadUrl("javascript:"
								+ FAILURE_CALLBACK
								+ "("
								+ "{\"errorCode\":\"APZ-CNT-046\",\"errorDescription\":\"Decryption fail\"}"
								+ ");");
					}
				});
			}
		} else {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-102','','');");
				}
			});
		}
	}

	/**
	 * Prepares the salt based on key
	 *
	 * @param key
	 * @return
	 */
	private String getSalt(String key) {
		String originalString = key;
		char[] c = originalString.toCharArray();
		// Replace with a "swap" function, if desired:
		char temp = c[0];
		c[0] = c[1];
		c[1] = temp;
		temp = c[c.length - 1];
		c[c.length - 1] = c[c.length - 2];
		c[c.length - 2] = temp;
		String swappedString = new String(c);
		return swappedString;
	}

	/**
	 * prepares the IV from key
	 * 
	 * @param key
	 * @return
	 */
	private byte[] getIV(String key) {
		byte[] iv = new byte[16];
		Arrays.fill(iv, (byte) 0);
		StringBuffer or = new StringBuffer(key);
		String nw = or.reverse().toString();
		byte[] keyBytes = null;
		try {
			keyBytes = nw.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {

		}
		byte[] rawIV = new byte[keyBytes.length];
		for (int i = 0; i < keyBytes.length; i++) {
			rawIV[i] = (byte) (keyBytes[i] >> 1);
		}
		for (int i = 0; i < iv.length; i++) {
			iv[i] = rawIV[i];
		}
		return iv;
	}

	/**
	 * To handle json exception
	 *
	 * @param errorCode
	 * @param errMessage
	 */
	@JavascriptInterface
	public void jsonParseException(final String errorCode,final String errMessage) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				webView.loadUrl("javascript:jsonParseExceptionCallBack" + "("
						+ "{\"errorCode\":\"" + errorCode
						+ "\",\"errorDescription\":\"" + errMessage + "\"}"
						+ ");");
			}
		});
	}

	/**
	 * To perform database operation
	 *
	 * @param jsonText
	 */
//	@JavascriptInterface
//	public void storagePlugin(String jsonText) {
//		if (StoragePlugin.isStoragePlugin()) {
//			AuditLog.makeString("STORAGE","START");
//			ServerLog.i(TAG,"storagePlugin : " + jsonText);
//			JSONObject sqlJson = null;
//			try {
//				sqlJson = new JSONObject(jsonText);
//				new StoragePlugin(context, activity, webView).executeSQL(sqlJson);
//			} catch (JSONException e) {
//
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-115','','');");
//				}
//			});
//		}
//
//	}
	/**
	 * creating url whitelist, 3.2 siddu
	 *
	 * @param jsonText
	 * @param header
	 */
	@JavascriptInterface
	public void enableWhiteList(String jsonText) {
		if (EnableWhiteList.isDummyPlugin) {
			AuditLog.makeString("WHITELIST","START");
//			ApzLogger.i(TAG,"storagePlugin : " + jsonText);
			JSONObject sqlJson = null;
			try {
				sqlJson = new JSONObject(jsonText);
				//new EnableWhiteList(context, activity, webView).executeWhitelist();
			} catch (JSONException e) {

				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
					}
				});
			}
		} else {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-115','','');");
				}
			});
		}
	}

	/**
	 * Uploads a file to the internal server
	 *
	 * @param jsonText
	 * @param header
	 */
//	@JavascriptInterface
//	public synchronized void uploadToServer(final String jsonText,final String header) {
//		if (FileUpload.isFileUpload()) {
//			AuditLog.makeString("UPLOAD TO SERVER","START");
//			ServerLog.i(TAG,"uploadToServer : " + jsonText);
//			final JSONObject uploadJson;
//			try {
//				uploadJson = new JSONObject(jsonText);
//			} catch (JSONException je) {
//				return;
//			}
//			final FileUpload up = new FileUpload(context, activity, webView);
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					up.uploadFile(uploadJson, header);
//				}
//			}).start();
//		} else {
//			activity.runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-104','','');");
//				}
//			});
//		}
//
//	}
//	/**
//	 * To override events default
//	 *
//	 * @param jsonText
//	 */
//	@JavascriptInterface
//	public void overrideEventsDefault(String jsonText) {
//		ServerLog.i(TAG,"overrideEventsDefault : " + jsonText);
//		JSONObject eventsJson = null;
//		try {
//			eventsJson = new JSONObject(jsonText);
//			Iterator<String> eventsIterator = eventsJson.keys();
//			settings = context.getSharedPreferences(properties, 0);
//			while (eventsIterator.hasNext()) {
//				String key = (String) eventsIterator.next().trim();
//				String val = eventsJson.getString(key).trim();
//				UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,key, val, settings);
//			}
//			/*
//			 * update the key to true (i.e now events defaults values are
//			 * overridden)
//			 */
//			UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,"eventsInitialized", "true", settings);
//		} catch (JSONException e) {
//
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//				}
//			});
//		}
//	}
	/**
	 * Used to open new webview
	 * */
//	@JavascriptInterface
//	public void multiviewOpen(String jsonText) {
//		AuditLog.makeString("MULTIVIEW","OPEN");
//		ServerLog.i(TAG,"multiviewOpen : " + jsonText);
//		final JSONObject multViewJson;
//		try {
//			multViewJson = new JSONObject(jsonText);
//		} catch (JSONException e) {
//
//			return;
//		}
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				jb.enableMultiView(multViewJson);
//			}
//		});
//	}
	/**
	 * Used to close newly opened webview
	 **/
//	@JavascriptInterface
//	public void multiviewClose(String jsonText) {
////		AuditLog.makeString("MULTIVIEW","CLOSE");
//		ServerLog.i(TAG,"multiviewClose : " + jsonText);
//		final JSONObject closeMultViewJson;
//		try {
//			closeMultViewJson = new JSONObject(jsonText);
//		} catch (JSONException e) {
//
//			return;
//		}
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				jb.killMultiView(closeMultViewJson);
//			}
//		});
//	}
	/**
	 * Used to resize the new webview on deviceorientation change
	 */
//	@JavascriptInterface
//	public void resizeMultiviewOnOrientationChange() {
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				jb.resizeMain();
//			}
//		});
//	}
	/**
	 * used to perform file download
	 *
	 * @param jsonText
	 */
//	@JavascriptInterface
//	public synchronized void download(final String jsonReqText, String jsonText) {
//		if (FileDownload.isFileDownload()) {
//			AuditLog.makeString("DOWNLOAD","START");
//			ServerLog.i(TAG,"download : ");
//			final JSONObject downloadJson;
//			try {
//				downloadJson = new JSONObject(jsonText);
//			} catch (JSONException e) {
//
//				return;
//			}
//			final FileDownload fileDownload = new FileDownload(context,	activity, webView);
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					fileDownload.downloadFile(jsonReqText, downloadJson);
//				}
//			}).start();
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-104','','');");
//				}
//			});
//		}
//	}
	/**
	 * Deletes log file from sandbox after successful logfileUpload
	 */
	@JavascriptInterface
	public void deleteLogFile() {
		ApzLogger.i(TAG,"deleteLogFile");
		if (MediaUtils.isSDCardPresent()) {
			String outFileName = AppzillonMainScreen.SANDBOX_LOC + "/Log/log.txt";
			try {
				File f = new File(outFileName);
				f.delete();
			} catch (Exception e) {

			}
		}
	}
	/**
	 * used to open a file from sandbox with given directory and filename
	 */
//	@JavascriptInterface
//	public void fileOpen(String jsonString) {
//		if (FileOperation.isFileOperation()) {
//			AuditLog.makeString("FILE OPERATION","OPEN START");
//			ServerLog.i(TAG,"fileOpen : " + jsonString);
//			FileOperation fileOp = new FileOperation(context, activity, webView);
//			fileOp.openFile(jsonString);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-104','','');");
//				}
//			});
//		}
//	}
	/**
	 * used to create a file in sandbox
	 */
//	@JavascriptInterface
//	public void fileCreate(String jsonString) {
//		if (FileOperation.isFileOperation()) {
//			AuditLog.makeString("FILE OPERATION","CREATE START");
//			ServerLog.i(TAG,"fileCreate : " + jsonString);
//			FileOperation fileOp = new FileOperation(context, activity, webView);
//			fileOp.createFile(jsonString);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-104','','');");
//				}
//			});
//		}
//	}
//	/**
//	 * used to read file content from sandbox
//	 */
//	@JavascriptInterface
//	public void fileContent(String jsonString) {
//		if (FileOperation.isFileOperation()) {
//			AuditLog.makeString("FILE OPERATION","CONTENT START");
//			ServerLog.i(TAG,"fileContent : " + jsonString);
//			FileOperation fileOp = new FileOperation(context, activity, webView);
//			fileOp.getFileContent(jsonString);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-104','','');");
//				}
//			});
//		}
//	}
//	/**
//	 * used to delete a file from sandbox
//	 */
//	@JavascriptInterface
//	public void fileDelete(String jsonString) {
//		if (FileOperation.isFileOperation()) {
//			AuditLog.makeString("FILE OPERATION","FILE DELETE");
//			ServerLog.i(TAG,"fileDelete : " + jsonString);
//			FileOperation fileOp = new FileOperation(context, activity, webView);
//			fileOp.deleteFile(jsonString);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-104','','');");
//				}
//			});
//		}
//	}

//	@JavascriptInterface
//	public final void sendSms(String jsonobj) {
//		ServerLog.i(TAG,"sendSMS : " + jsonobj);
//		JSONObject smsJson = null;
//		try {
//			smsJson = new JSONObject(jsonobj);
//		} catch (JSONException ex) {
//
//			return;
//		}
//		Sms smsop = new Sms(activity, webView);
//		smsop.sendSms(smsJson);
//	}

//	@JavascriptInterface
//	public void makePhoneCall(String phNo) {
//		ServerLog.i(TAG,"makePhoneCall : " + phNo);
//		Intent phoneCallIntent = new Intent(activity, CallActivity.class);
//		phoneCallIntent.putExtra("Contact", phNo);
//		activity.startActivity(phoneCallIntent);
//	}

//	@JavascriptInterface
//	public void setOrientation(String mode) {
//		setWebview webop = new setWebview(context, activity, webView);
//		webop.webviewMode(mode);
//	}

	@JavascriptInterface
	public final String getOtpflag() {
		final String otp_flag = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,"OTPFLAG", StringUtils.getString(StringUtils.GENERATE_OTP), settings);
		//ApzLogger.i(TAG, "getOtpflag : "+otp_flag);
		return otp_flag;
	}

	@JavascriptInterface
	public void showSplash() {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				mSpinView.setVisibility(View.VISIBLE);
			}
		});
	}

	/**
	 * Hides splashScreen
	 */
	@JavascriptInterface
	public void hideSplash() {
		//hideSpin();
		//Abhishek Fix for Bug id 6760 START
		final Handler refresh = new Handler(Looper.getMainLooper());
		refresh.postDelayed(new Runnable() {
			public void run() {
				mSpinView.dismiss();
			}
		}, 500);
		//Abhishek Fix for Bug id 6760 END
	}

//	@JavascriptInterface
//	public void savereport(String jsonobj) {
//		if (Report.isReport()) {
//			ServerLog.i(TAG,"savereport : " + jsonobj);
//			JSONObject reportJson = null;
//			try {
//				reportJson = new JSONObject(jsonobj);
//			} catch (JSONException ex) {
//
//				return;
//			}
//			Report savereport = new Report(context, activity, webView);
//			savereport.savereport(reportJson);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-000','','');");
//				}
//			});
//		}
//	}

 @ JavascriptInterface
public void lockRotationByDefault(){
	 final String orientation = getDeviceValue("orientation");
	webView.post(new Runnable() {
		 @ Override
		public void run() {
			webView.loadUrl("javascript:(function() { " + "localStorage.setItem('ORIENTATION','" + orientation + "'); " + "})()");
		}
	});
	UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,"ORIENTATION", orientation, settings);
	if (orientation.equalsIgnoreCase("LANDSCAPE")) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
	} else if (orientation.equalsIgnoreCase("PORTRAIT")) {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
	} else {
		activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
	System.out.println("orientation" + orientation);
}

 private String getDeviceValue(String val){
	 DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		//Natasha changes for orientation,16-10-2015
		float dpi= metrics.densityDpi;
		int n = Math.round(dpi/160);
		width = width/n;
		height = height/n;
		AssetManager manager = context.getAssets();
		JSONArray devicegrp;
		String orientation = null;
		String devname = null;
		int count = 0;
		int temp = 0;
		try {
			//Abhishek For OTA
			InputStream is;
			if((AppzillonMainScreen.OTAREQUIRED).equalsIgnoreCase("Y")){
				File jsonFile = new File(AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC+"screens/config/DeviceGroups.json");
				is = new FileInputStream(jsonFile);
			}else{
				is = manager.open(AppzillonMainScreen.ASSET_APP_LOC+"screens/config/DeviceGroups.json");
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			try {
				devicegrp = new JSONObject(out.toString()).getJSONArray("devices");
				for (int i = 0; i < devicegrp.length(); i++) {
					JSONObject devval = devicegrp.getJSONObject(i);
					int devwidth = Integer.parseInt(devval.getString("width"));
					int devheight = Integer.parseInt(devval.getString("height"));
					int deltawidth = width - devwidth;
					int deltaheight = height - devheight;
					if (deltawidth == 0 && deltaheight == 0) {
						orientation = devval.getString("orientation");
						devname = devval.getString("name");
						break;
					}

					if (deltawidth < 0) {
						deltawidth = 0 - deltawidth;
					}

					if (deltaheight < 0) {
						deltaheight = 0 - deltaheight;
					}

					int deltaWidthHeight = deltawidth + deltaheight;

					if (deltaWidthHeight < 0) {
						deltaWidthHeight = 0 - deltaWidthHeight;
					}
					if (count == 0) {
						temp = deltaWidthHeight;
						count++;
						orientation = devval.getString("orientation");
						devname = devval.getString("name");
					}

					if (temp > deltaWidthHeight) {
						temp = deltaWidthHeight;
						orientation = devval.getString("orientation");
						devname = devval.getString("name");
					}
				}

			} catch (JSONException e) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
					}
				});

			}
		} catch (IOException e1) {

		}

		if(val.equalsIgnoreCase("name")){
			return devname;
		}else if(val.equalsIgnoreCase("orientation")){
			return orientation;
		}else{
			return "";
		}
 }


//Abhishek, 06 July 2015 , Updated getDevice Logic, Now get device and lock Rotation are independent on each other END
	@JavascriptInterface
	public String getBuildversion() {
		String version = Build.VERSION.RELEASE;
		//ApzLogger.i(TAG,"version : " + version);
		return version;
	}

//	@JavascriptInterface
//	public void sendNFC(String jsonobj) {
//		if (NFCDeviceActivity.isNFCDeviceActivity()) {
//			//Abhishek, 28 May 2015, Bug id 5269, Checking device supports NFC START
//			if(AppzillonUtils.isNFCSupported(context)){
//				AuditLog.makeString("NFC","START SEND");
//				ServerLog.i(TAG,"sendNFC : " + jsonobj);
//				JSONObject sendjson = null;
//				String type = null;
//				String content = null;
//				String action = null;
//				try {
//					sendjson = new JSONObject(jsonobj);
//					type = sendjson.getString("type");
//					content = sendjson.getString("content");
//					SUCCESS_CALLBACK = sendjson.getString("successCallback");
//					FAILURE_CALLBACK = sendjson.getString("failureCallback");
//					action = sendjson.getString("action");
//				} catch (JSONException e1) {
//					activity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//						}
//					});
//
//				}
//				Intent sendnfc = null;
//				if (action.equalsIgnoreCase("tag")) {
//					sendnfc = new Intent(context, WriteNFCActivity.class);
//				} else if (action.equalsIgnoreCase("device")) {
//					sendnfc = new Intent(context, NFCDeviceActivity.class);
//					//Abhishek 29 May 2015, adding value to set send/receive START
//					sendnfc.putExtra("device", "SEND");
//					//Abhishek 29 May 2015, adding value to set send/receive END
//				}
//				sendnfc.putExtra("Type", type);
//				sendnfc.putExtra("Content", content);
//				activity.startActivityForResult(sendnfc, SEND_NFC);
//			}else{
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-214','','');");
//					}
//				});
//			}
//			//Abhishek, 28 May 2015, Bug id 5269, Checking device supports NFC END
//
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-116','','');");
//				}
//			});
//		}
//	}
//
//	@JavascriptInterface
//	public void receiveNFC(String jsonobj) {
//		if (ReadNFCActivity.isReadNFCActivity()) {
//			//Abhishek, 28 May 2015, Bug id 5269, Checking device supports NFC START
//			if(AppzillonUtils.isNFCSupported(context)){
//				AuditLog.makeString("NFC","START RECEIVE");
//				ServerLog.i(TAG,"receiveNFC : " + jsonobj);
//				JSONObject recjson = null;
//				String type = null;
//				String action = null;
//				try {
//					recjson = new JSONObject(jsonobj);
//					type = recjson.getString("type");
//					SUCCESS_CALLBACK = recjson.getString("successCallback");
//					FAILURE_CALLBACK = recjson.getString("failureCallback");
//					action = recjson.getString("action");
//				} catch (JSONException e1) {
//					activity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//						}
//					});
//
//				}
//				Intent recnfc = null;
//				if (action.equalsIgnoreCase("tag")) {
//					recnfc = new Intent(context, ReadNFCActivity.class);
//					recnfc.putExtra("Type", type);
//					activity.startActivityForResult(recnfc, RECEIVE_NFC);
//				} else if (action.equalsIgnoreCase("device")) {
//					recnfc = new Intent(context, NFCDeviceActivity.class);
//					//Abhishek 29 May 2015, removing "type" from receive, adding value to set send/receive START
//					recnfc.putExtra("device", "RECEIVE");
//					//Abhishek 29 May 2015, removing "type" from receive, adding value to set send/receive END
//					activity.startActivityForResult(recnfc, DEVICE_RECEIVE_NFC);
//				}
//			}else{
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-214','','');");
//					}
//				});
//			}
//			//Abhishek, 28 May 2015, Bug id 5269, Checking device supports NFC END
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-116','','');");
//				}
//			});
//		}
//	}
//
//	@JavascriptInterface
//	public void stopNFC(String jsonobj) {
//		//Abhishek, 28 May 2015, Bug id 5269, Checking device supports NFC START
//		if(AppzillonUtils.isNFCSupported(context)){
//			AuditLog.makeString("NFC","STOP");
//			Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
//			activity.startActivity(intent);
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-214','','');");
//				}
//			});
//		}
//		//Abhishek, 28 May 2015, Bug id 5269, Checking device supports NFC END
//	}

//	@JavascriptInterface
//	public void captureSignature(String jsonobj) {
//		if (CaptureSignature.isSignauturePlugin()) {
//			AuditLog.makeString("SIGNATURE","START");
//			ServerLog.i(TAG, "captureSignature : " + jsonobj);
//			JSONObject signjson = null;
//			try {
//				signjson = new JSONObject(jsonobj);
//				SUCCESS_CALLBACK = signjson.getString("successCallback");
//				FAILURE_CALLBACK = signjson.getString("failureCallback");
//			} catch (JSONException e) {
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//
//			}
//			Intent intent = new Intent(context, CaptureSignature.class);
//			activity.startActivityForResult(intent, CAPTURE_SIGN);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-143','','');");
//				}
//			});
//		}
//	}

//	@JavascriptInterface
//	public void Vibrate(String jsonobj) {
//		if(Vibration.isVibratePlugin()){
//			AuditLog.makeString("VIBRATE","START");
//			ServerLog.i(TAG,"Vibrate : " + jsonobj);
//			Vibration vib = new Vibration(context,activity,webView);
//			vib.vibrate(jsonobj);
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-141','','');");
//				}
//			});
//		}
//	}

//	@JavascriptInterface
//	public void wipeOut(String jsonobj) {
//		ServerLog.i(TAG,"WIPEOUT");
//		settings = activity.getSharedPreferences(properties, 0);
//		SharedPreferences.Editor editor = settings.edit();
//		editor.clear();
//		editor.commit();
//		WipeOut wipe = new WipeOut(context,activity,webView);
//		wipe.unInstallApp(jsonobj);
//	}

	@JavascriptInterface
	public String getUUID() {
		TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		final String device_id = mTelephonyMgr.getDeviceId();
		return device_id;
	}

//	@JavascriptInterface
//	public void appIdleTimeout(String jsonobj) {
//		//Abhishek 03 March 2015 Made it as plug-in Start
//		if (AppIdleTimeOut.isPlugin()) {
//			ServerLog.i(TAG, "appIdleTimeout : " + jsonobj);
//			AppIdleTimeOut appIdleTimeOut = new AppIdleTimeOut(activity, webView);
//			appIdleTimeOut.startTimeClocking(jsonobj);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-153','','');");
//				}
//			});
//		}
//		//Abhishek 03 March 2015 Made it as plug-in END
//	}

//	@JavascriptInterface
//	public void routeMap(final String jsonText) {
//		if (Map.IsMap()) {
//			AuditLog.makeString("ROUTE MAP","START");
//			ServerLog.i(TAG,"routeMap : " + jsonText);
//			Intent intent = new Intent(context, DrivingDirection.class);
//			//Abhishek 19 Feb 2015 Reference of Main Web View to show Error Message START
//			DrivingDirection.setWebView(webView);
//			//Abhishek 19 Feb 2015 Reference of Main Web View to show Error Message END
//			intent.putExtra("mapval", jsonText);
//			activity.startActivity(intent);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-149','','');");
//				}
//			});
//		}
//	}

//	@JavascriptInterface
//	public void selectArea(final String jsonText) {
//		if (Map.IsMap()) {
//			AuditLog.makeString("LOCATION SELECTOR","START");
//			ServerLog.i(TAG,"selectArea : " + jsonText);
//			Intent intent = new Intent(context, SelectionMap.class);
//			//Abhishek 19 Feb 2015 Reference of Main Web View to show Error Message START
//			SelectionMap.setWebView(webView);
//			//Abhishek 19 Feb 2015 Reference of Main Web View to show Error Message END
//			intent.putExtra("mapval", jsonText);
//			activity.startActivity(intent);
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-144','','');");
//				}
//			});
//		}
//	}
	//For conversion of speech to text

//	@JavascriptInterface
//	public void speechToText(final String voiceobj) {
//		if(SpeechToText.isSpeechToTextPlugin()){
//			AuditLog.makeString("VOICE","START");
//			ServerLog.i(TAG,"speechToText : " + voiceobj);
//			final SpeechToText speech = new SpeechToText(context, webView, progressDialog);
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					speech.startVoiceConversion(voiceobj);
//				}
//			});
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-145','','');");
//				}
//			});
//		}
//	}
//
//	//For encryption of File
//	@JavascriptInterface
//	public void fileEncrypt(final String json){
//		if(FileCrypto.isFileCryptoPlugin()){
//			ServerLog.d(TAG, "fileEncrypt");
//			AuditLog.makeString("FILECRYPTO","ENCRYPTION START");
//			String key = "";
//			try {
//				JSONObject jsonObj = new JSONObject(json);
//				key = jsonObj.getString("key");
//			} catch (JSONException e) {
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//
//			}
//			FileCrypto fileEnc = new FileCrypto(context,activity,webView);
//			if (key.length() <= 16) {
//				key += paddingMask.substring(0, 16 - key.length());
//			}
//			//Abhishek 24 April 2015, if key is greater then 16 make it of 16 length, check Nagaraj mail START
//			else {
//				key = key.substring(0, 16);
//			}
//			Log.i(TAG, "Key length : "+key.length());
//			//Abhishek 24 April 2015, if key is greater then 16 make it of 16 length, check Nagaraj mail END
//
//			byte[] iv = getIV(key);
//			String finalSalt = getSalt(key);
//			fileEnc.encryptFile(key,finalSalt, iv,json);
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-155','','');");
//				}
//			});
//		}
//	}
//
//	//For decryption of file
//	@JavascriptInterface
//	public void fileDecrypt(final String json){
//		AuditLog.makeString("FILECRYPTO","DECRYPTION START");
//		if(FileCrypto.isFileCryptoPlugin()){
//			ServerLog.d(TAG, "fileDecrypt");
//			String key = "";
//			try {
//				JSONObject jsonObj = new JSONObject(json);
//				key = jsonObj.getString("key");
//			} catch (JSONException e) {
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//
//			}
//			FileCrypto fileEnc = new FileCrypto(context,activity,webView);
//			if (key.length() <= 16) {
//				key += paddingMask.substring(0, 16 - key.length());
//			}
//			//Abhishek 24 April 2015, if key is greater then 16 make it of 16 length, check Nagaraj mail START
//			else{
//				key = key.substring(0, 16);
//			}
//			Log.i(TAG, "Key length : "+key.length());
//			//Abhishek 24 April 2015, if key is greater then 16 make it of 16 length, check Nagaraj mail START
//
//			byte[] iv = getIV(key);
//			String finalSalt = getSalt(key);
//			fileEnc.decryptFile(key, finalSalt, iv,json);
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-156','','');");
//				}
//			});
//		}
//	}

	//For Gesture support start
//	@JavascriptInterface
//	public void gestureSupportStart(String json){
//		if(GesturePlugin.isGesturePlugin()){
//			AuditLog.makeString("GESTURE","START");
//			ServerLog.i(TAG,"gestureSupportStart : " );
//			GesturePlugin gp = new GesturePlugin(context,activity,webView);
//			gp.startListener(json);
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-146','','');");
//				}
//			});
//		}
//	}
//
//	//For Gesture support stop
//	@JavascriptInterface
//	public void gestureSupportStop(String json){
//		if(GesturePlugin.isGesturePlugin()){
////			AuditLog.makeString("GESTURE","STOP");
//			ServerLog.i(TAG,"gestureSupportStop : ");
//			GesturePlugin gp = new GesturePlugin(context,activity,webView);
//			gp.stopListener(json);
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-146','','');");
//				}
//			});
//		}
//	}
//
//	//For geo fencing of file
//	@JavascriptInterface
//	public void geoFencing(String json){
//		if(GeoFencing.isGeoFencing()){
//			AuditLog.makeString("GEO FENCING","START");
//			ServerLog.i(TAG,"geoFencing : " + json);
//			GeoFencing geoFencing = new GeoFencing(context,activity,webView);
//			geoFencing.checkGeoValidity(json);
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-147','','');");
//				}
//			});
//		}
//	}
//
	@JavascriptInterface
	public void loadPage(String pageName){
		//ApzLogger.i(TAG,"loadPage : " + pageName);
		//TODO Abhishek closing the activity running
		//Abhishek, bug id 5527,checking app expired before launching START
		if(AppzillonUtils.isAppExpired(context, pageName)){
			AppzillonMainScreen.showExpiryMsg(context,activity, pageName);
		}else{
		//Abhishek 12 March 2015 check weather the app to be launched is in assets folder or not START
		if(checkIfFolderExists(pageName)){
//			//Abhishek , Bug id 5239, clear when app exists START
//			//Abhishek , Bug id 5238, Commented out START
				String appLoc = AppzillonMainScreen.ASSETS_MAIN_FOLDER+"/"+pageName;
				Intent intent = new Intent(activity,AppzillonMainScreen.class);
				intent.putExtra("app_name", pageName);
				intent.putExtra("app_loc", appLoc);
				intent.putExtra("app_remoteDebug", "Y");
				activity.startActivity(intent);
		}else{
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-221','','');");
				}
			});
		}
		//Abhishek 12 March 2015 check weather the app to be launched is in assets folder or not END
		}
		//Abhishek, bug id 5527,checking app expired before launching END
	}

	public boolean checkIfFolderExists(String assetName) {

		if((AppzillonMainScreen.OTAREQUIRED).equalsIgnoreCase("Y")){
			//Abhishek 25 March 2015 added method to check directory of launched folder exists START
			System.out.println("OTA_ENABLED");
			String appLoc = AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSETS_MAIN_FOLDER+File.separator+assetName;
			System.out.println("appLodc : "+appLoc);
			File appFile = new File(appLoc);
			if(appFile.exists()){
				return true;
			}else{
				return false;
			}
			//Abhishek 25 March 2015 added method to check directory of launched folder exists END
		}else{
			List<String> mapList = null;
		    if (mapList == null) {
		    	AssetManager am = context.getAssets();
		        try {
		            mapList = Arrays.asList(am.list("apps"));
		        } catch (IOException e) {
		        }
		    }
		    return mapList.contains(assetName) ? true : false;
		}
	}

	//Call for upgrade app by downloading/deleting files
	@JavascriptInterface
	public void upgradeApp(String appName){
		//ApzLogger.i(TAG,"upgradeApp : " + appName);
		boolean subAppOTARefreshStatus = false;
		SharedPreferences apps = context.getSharedPreferences(AppzillonMainScreen.app_props, 0);
		String appVersion = UserSettings.getAppVersion(appName, apps);
		//Abhishek Bug id 4439 START
		if(appVersion.equalsIgnoreCase("0.0.0")){
			StringUtils stringUtils = new StringUtils(context, AppzillonMainScreen.ASSETS_MAIN_FOLDER+File.separator+appName);
			appVersion = StringUtils.getString(StringUtils.APP_VERSION);
		}
		//Abhishek Bug id 4439 START
		OTAPlugin otaPlugin = new OTAPlugin(context);
		subAppOTARefreshStatus = otaPlugin.getDataForOTA(appName,appVersion);
		if(subAppOTARefreshStatus){
			//Abhishek 09 April updated the version as received in responce START
			UserSettings.setAppVersion(appName,otaPlugin.getUpdatedAppversion(), apps);
			//Abhishek 09 April updated the version as received in responce END
		}

	}

	//Get the instruction/details of the app
	@JavascriptInterface
	public void appInstructions(String jsonObj){
		ApzLogger.i(TAG,"appInstructions : " + jsonObj);
		SharedPreferences apps = context.getSharedPreferences(AppzillonMainScreen.app_props, 0);
		try {
			AuditLog.makeString("APP INSTRUCTIONS","START");
			JSONObject json = new JSONObject(jsonObj);
			//Abhishek 27 March 2015 appname changed to appId START
			String appName = json.getString("appId");
			//Abhishek 27 March 2015 appname changed to appId END
			SUCCESS_CALLBACK = json.getString("successCallback");
			FAILURE_CALLBACK = json.getString("failureCallback");
			//Abhishek, bug id 5333 , sending present app id for header app id START
			String presentAppid = StringUtils.getString(StringUtils.APP_ID);
			//Abhishek, bug id 5529, passing child app server URL as argument START
			String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
			final JSONObject response = AppzillonMainScreen.getAppInstructions(appName,presentAppid,serverUrl);
			//Abhishek, bug id 5529, passing child app server URL as argument END
			//Abhishek, bug id 5333 , sending present app id for header app id END
			if(response != null){
//				Log.i(TAG, "App instructions Response : "+response);
				JSONObject body = response.getJSONObject(AppzillonMainScreen.APPZILLON_BODY);
				JSONObject header = response.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
				//String resStatus = header.getString("status");   3.2 changes
				boolean resStatus = header.getBoolean("status");
				if(resStatus){   // 3.2 changes resStatus.equalsIgnoreCase("success")
				//Abhishek 23 April 2015, Bug id 5007, getting response body from appname passed in request START
				final JSONObject responseBody = body.getJSONObject(appName);
				//Abhishek 23 April 2015, Bug id 5007, getting response body from appname passed in request END
				String lVersion = UserSettings.getAppVersion(appName, apps);
				//Abhishek Bug id 4439 START
				if(lVersion.equalsIgnoreCase("0.0.0")){
					StringUtils stringUtils = new StringUtils(context, AppzillonMainScreen.ASSETS_MAIN_FOLDER+File.separator+appName);
					lVersion = StringUtils.getString(StringUtils.APP_VERSION);
				}
				//Abhishek Bug id 4439 END
				if(!lVersion.equalsIgnoreCase(responseBody.getString(AppzillonMainScreen.APP_VERSION))){
					responseBody.put("upgradeRequired", "Y");
				}
				//Abhishek Fix for bug 4840 Added field "upgradeRequired" with value "N" START
				else{
					responseBody.put("upgradeRequired", "N");
				}
				//Abhishek Fix for bug 4840 Added field "upgradeRequired" with value "N" END
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						AuditLog.makeString("APP INSTRUCTIONS","Success");
						AuditLog.sendToJSON();
						//Abhishek Bug id 5088,sending json back to successcallback START
//						webView.loadUrl("javascript:" + SUCCESS_CALLBACK + "(" + responseBody + ");");
						//Abhishek Bug id 5088,sending json back to successcallback END
					}
				});
			}
				//Abhishek 23 April 2015, for error START
				else{
					activity.runOnUiThread(new Runnable() {
						JSONArray error = response.getJSONArray(AppzillonMainScreen.APPZILLON_ERRORS);
						JSONObject errObj = error.getJSONObject(0);
						@Override
						public void run() {
							AuditLog.sendToJSON();
							webView.loadUrl("javascript:" + FAILURE_CALLBACK + "('" + errObj + "');");
						}
					});
				}
				//Abhishek 23 April 2015, for error END
			}else{
				//Log.e(TAG, "App instructions : Server Error");
				final String errorMsg;
//				if(response != null){
//					errorMsg = response.toString();
//				}else{
					errorMsg = "Server Error";
//				}
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						AuditLog.sendToJSON();
//						AuditLog.makeString("APP INSTRUCTIONS","Failure");
						webView.loadUrl("javascript:" + FAILURE_CALLBACK + "('" + errorMsg + "');");
					}
				});
			}
		} catch (JSONException e) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
				}
			});

		} catch (IllegalStateException e) {

		}
	}

	@JavascriptInterface
	public String currentVersion(final String appName){
//		ApzLogger.i(TAG,"currentVersion : " + appName);
		String appVersion = null;
		SharedPreferences apps = context.getSharedPreferences(AppzillonMainScreen.app_props, 0);
		appVersion = UserSettings.getAppVersion(appName,apps);
//			ApzLogger.i(TAG,"currentVersion : " + appVersion);
			//Abhishek Bug id 4439 START
			if(appVersion.equalsIgnoreCase("0.0.0")){
			//Abhishek Bug id 4439 END
				StringUtils stringUtils = new StringUtils(context, AppzillonMainScreen.ASSETS_MAIN_FOLDER+File.separator+appName);
				appVersion = StringUtils.getString(StringUtils.APP_VERSION);
			}
			return appVersion;
	}

//	@JavascriptInterface
//	public void deleteSubApp(String appName){
//		ServerLog.i(TAG,"deleteSubApp : " + appName);
//		WipeOut wp = new WipeOut(context,activity,webView);
//		wp.wipeOutSubApp(appName);
//	}

	public static void activityDestroyed(String PLUGIN) {
		AuditLog.sendToJSON();
	}

	//Abhishek bug id 4549 did device supports animation START
	@JavascriptInterface
	public boolean isAnimationSupported(){
		//Abhishek devices lower then API 14 supports only partial animation
		if (Build.VERSION.SDK_INT > VERSION_CODES.HONEYCOMB_MR2){
			return true;
		}else{
			return false;
		}
	}
	//Abhishek bug id 4549 did device supports animation END

//Abhishek 24 April 2015, added to print login request START
	@JavascriptInterface
	public void printLoginReq(String req){
//		ApzLogger.i(TAG,"Login request : " + req);
	}
//Abhishek 24 April 2015, added to print login request END

//Abhishek , Bug id 5324, START
		@JavascriptInterface
		public void setRemoteDebug(String jsonobj){
			JSONObject json = null;
			final String successCallBack;
			String faliureCallBack = null;
			try {
				json = new JSONObject(jsonobj);
				successCallBack = json.getString("successCallback");
				faliureCallBack = json.getString("failureCallback");
				setAppPersistanceValue("SENDLOG", json.getString("debug"));
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:" + successCallBack + "(" + "{\"message\":\"Success\"}" + ");");
					}
				});
			} catch (Exception e) {

				final String callBack = faliureCallBack;
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:" + callBack + "(" + "{\"failure\":\"Failure\"}" + ");");
					}
				});
			}
		}

//Abhishek , Bug id 5324, END

	//Abhishek TODO
	@JavascriptInterface
	public void setAppPersistanceValue(String key,String value){
		settings = context.getSharedPreferences(properties, 0);
		UserSettings.setAppValue(AppzillonMainScreen.APP_NAME,key, value, settings);
	}

//	@JavascriptInterface
//	public String getAppVersion(){
//		SharedPreferences apps = context.getSharedPreferences(AppzillonMainScreen.app_props, 0);
//		String lVersion = UserSettings.getAppVersion(AppzillonMainScreen.APP_NAME, apps);
//		if(lVersion.equalsIgnoreCase("0.0.0")){
//			StringUtils stringUtils = new StringUtils(context, AppzillonMainScreen.ASSETS_MAIN_FOLDER+File.separator+AppzillonMainScreen.APP_NAME);
//			lVersion = StringUtils.getString(StringUtils.APP_VERSION);
//		}
//		return lVersion;
//	}
	//Nagaraj changes 16-10-2015
//	@JavascriptInterface
//	public String getPPI(){
//		String ppi="";
//		ppi =AppzillonUtils.getPPI(activity);
//		return ppi;
//	}
//
//	@JavascriptInterface
//	public String getScreenSize(){
//		String scrsize="";
//		scrsize =AppzillonUtils.getScreenSize(activity);
//		return scrsize;
//	}

	@JavascriptInterface
	public void getTheSpeed() {
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				// getting the speed of the device
//				//Intent speedInt = new Intent(activity,SpeedAlarmActivity.class);
//				//activity.startActivity(speedInt);
//				try {
//					 Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//					 Ringtone r = RingtoneManager.getRingtone(context, notification);
//					 r.play();
//
//
//
//
//					 } catch (Exception e) {
//
//					 Log.i(TAG, "exception playing ringtone");
//					 }
//			}
//		});

//		NotificationCompat.Builder mBuilder =
//			    new NotificationCompat.Builder(context)
//			    .setSmallIcon(R.drawable.notification)
//			    .setContentTitle("My notification")
//			    .setSound(Uri.parse("android.resource://"
//			            + context.getPackageName() + "/" + R.raw.baarish))
//			    .setContentText("Hello World!");

		// 3.2 changes for notification

//		int mNotificationId = 001;
//		// Gets an instance of the NotificationManager service
//		NotificationManager mNotifyMgr =
//		        (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//		// Builds the notification and issues it.
//		mNotifyMgr.notify(mNotificationId, mBuilder.build());

	}


//@JavascriptInterface
//	public void beaconMonitering(String value,String jsonobj){
//	ServerLog.i(TAG, "beaconMonitering : "+value);
//	if(BeaconMonitoring.isBeaconPlugin()){
//		JSONObject json;
//		String successCallback = null;
//		String failureCallBack = null;
//		String uuid = null;
//		try {
//			json = new JSONObject(jsonobj);
//			successCallback = json.getString("successCallback");
//			failureCallBack = json.getString("failureCallback");
//			if(value.equalsIgnoreCase("START")){
//				uuid = json.getString("uuid");
//				if(uuid != null){
//					BeaconMonitoring.setWebView(webView);
//					Intent in = new Intent(activity,BeaconMonitoring.class);
//					in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//					in.putExtra("BEACON_ID", uuid);
//					in.putExtra("failureCallback", failureCallBack);
//					in.putExtra("successCallback", successCallback);
//					BEACON_ID = uuid;
//					activity.startActivity(in);
//					//Bug No 6392 start
//					}else{
//					final String fCallBack = failureCallBack;
//					activity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							JSONObject json = new JSONObject();
//							try {
//								json.put("errorCode", "APZ-CNT-256");
//								json.put("errorDescription", "Invalid UUID");
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//
//							}
//							final JSONObject jsonError = json;
//							webView.loadUrl("javascript:"+fCallBack+"("+jsonError+");");
//						}
//					});
//				}
//				//Bug No 6392 end
//			}else{
//				BEACON_ID = "";
//				if(BeaconMonitoring.activity == null || BeaconMonitoring.activity.isFinishing()){
//					final String fCallBack = failureCallBack;
//					activity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							JSONObject json = new JSONObject();
//							try {
//								json.put("errorCode", "APZ-CNT-254");
//								json.put("errorDescription", "Beacon is not running");
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//
//							}
//							final JSONObject jsonError = json;
//							webView.loadUrl("javascript:"+fCallBack+"("+jsonError+");");
//						}
//					});
//				}else{
//					BeaconMonitoring.activity.finish();
//					final String sCallBack = successCallback;
//					activity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							webView.loadUrl("javascript:"+sCallBack+"();");
//						}
//					});
//				}
//			}
//		} catch (JSONException e) {
//
//			ServerLog.i(TAG, "beaconMonitering : "+e.getMessage());
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//				}
//			});
//		}
//	}else{
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-233','','');");
//			}
//		});
//	}
//}

//	@JavascriptInterface
//	public void startAugReality(String jsonobj){
//		ServerLog.i(TAG, "startAugReality");
//		if(ARActivity.isPlugin()){
//			JSONObject json = null;
//			String successCallBack = null;
//			String faliureCallBack = null;
//			String thresholdDist = null;
//			JSONArray places = null;
//			try {
//				json = new JSONObject(jsonobj);
//				successCallBack = json.getString("successCallback");
//				faliureCallBack = json.getString("failureCallback");
//				thresholdDist = json.getString("thresholdDistance");
//				double thDist = Double.parseDouble(thresholdDist);
//				CurrentLocation.setThreshold(thDist);
//				AUG_REALITY_TH_CALLBACK = json.getString("thresholdCallback");
//				places = json.getJSONArray("Places");
//			}catch (JSONException e){
//
//				ServerLog.i(TAG, "startAugReality : "+e.getMessage());
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//			}
//			Location presentLoc = CurrentLocation.getLocation(context,activity);
//			CurrentLocation.setPresentLocation(presentLoc);
//			if(presentLoc != null){
//				ARActivity.setJsonArray(places);
//				ARActivity.setCurrentLocation(presentLoc);
//				Intent intent  = new Intent(activity, ARActivity.class);
//				activity.startActivity(intent);
//				final String sCallBack = successCallBack;
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:"+sCallBack+"();");
//					}
//				});
//			}else{
//				final String fCallBack = faliureCallBack;
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-092','','');");
//						webView.loadUrl("javascript:"+fCallBack+"();");
//					}
//				});
//			}
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-235','','');");
//				}
//			});
//		}
//	}
//
//	public static void augRealityThresholdReached(double latitude,double longitude) {
//		final JSONObject json = new JSONObject();
//		try {
//			json.put("longitude", longitude+"");
//			json.put("latitude", latitude+"");
//		} catch (JSONException e) {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//				}
//			});
//
//		}
//		if(ARActivity.isPlugin()){
//			ServerLog.i(TAG, "augRealityThresholdReached");
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:"+AUG_REALITY_TH_CALLBACK+"("+json+");");
//				}
//			});
//		}
//	}
//
//	@JavascriptInterface
//	public void reloadAugReality(String json) {
//		JSONObject jsonObj = null;
//		JSONArray jsonarr = null;
//		try {
//			jsonObj = new JSONObject(json);
//			jsonarr = jsonObj.getJSONArray("Places");
//		} catch (JSONException e) {
//
//		}
//		OverlayView.refreshPlace(jsonarr);
//	}

//	@JavascriptInterface
//	public void  authGooglePlus (String jsonObj){
//		ServerLog.i(TAG, "authGooglePlus");
//		if(GoogleAuth.isPlugin()){
//			GoogleAuth gpAuth = new GoogleAuth(context, activity, webView);
//			gpAuth.googlePlusAuth(jsonObj);
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-022','','');");
//				}
//			});
//		}
//	}
//
//	@JavascriptInterface
//	public void  authFacebook (String jsonObj){
//		ServerLog.i(TAG, "authFacebook");
//		if(FacebookAuth.isPlugin()){
//			try {
//				JSONObject json = new JSONObject(jsonObj);
//				String successCallBack = json.getString("successCallback");
//				String failureCallBack = json.getString("failureCallback");
//				FacebookAuth.setWebView(webView);
//				Intent fbIntent = new Intent(activity, FacebookAuth.class);
//				fbIntent.putExtra("successCallBack", successCallBack);
//				fbIntent.putExtra("failureCallBack", failureCallBack);
//				activity.startActivity(fbIntent);
//			} catch (JSONException e) {
//
//				ServerLog.i(TAG, "authFacebook : "+e.getMessage());
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//			}
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-022','','');");
//				}
//			});
//		}
//	}
//
//	@JavascriptInterface
//	public void  authLinkedin (String jsonObj){
//		ServerLog.i(TAG, "authLinkedin");
//		if(LinkedinAuth.isPlugin()){
//			JSONObject json;
//			try {
//				json = new JSONObject(jsonObj);
//				String successCallBack = json.getString("successCallback");
//				String failureCallBack = json.getString("failureCallback");
//				LinkedinAuth.setWebView(webView);
//				Intent linkedinIntent = new Intent(activity, LinkedinAuth.class);
//				linkedinIntent.putExtra("successCallBack", successCallBack);
//				linkedinIntent.putExtra("failureCallBack", failureCallBack);
//				activity.startActivity(linkedinIntent);
//			} catch (JSONException e) {
//
//				ServerLog.i(TAG, "authLinkedin : "+e.getMessage());
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//					}
//				});
//			}
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-022','','');");
//				}
//			});
//		}
//	}

	/*@JavascriptInterface
	public void getPresentLatLong(String jsonObj){
		JSONObject json;
		try {
			json = new JSONObject(jsonObj);
			final String successCallBack = json.getString("successCallback");
			final String failureCallBack = json.getString("failureCallback");
			String latitude;
			String longitude;
			Location latlng = CurrentLocation.getLocation(this);
			double latd = 0.0;
			double lngd = 0.0;
			if(latlng!= null){
				final JSONObject successJson = new JSONObject();
				latd = latlng.getLatitude();
				lngd = latlng.getLongitude();
				latitude = Double.toString(latd);
				longitude = Double.toString(lngd);
				successJson.put("latitude", latitude);
				successJson.put("longitude", longitude);
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:"+successCallBack+"("+successJson+");");
					}
				});
			}else{
				if(!(failureCallBack.equals(""))){
				latitude = "";
				longitude = "";
				final JSONObject eJson = new JSONObject();
				eJson.put("errorCode", "");
				eJson.put("errorMessage", "Unable to Fetch Location.");
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:"+failureCallBack+"('"+eJson+"');");
					}
				});
			}}
		} catch (JSONException e) {

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
				}
			});
		}
	}*/

	@JavascriptInterface
	public void setRingtone(String ringtoneJson){
		String path = "";
		String title = "";
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/AppzillonRingtone";

		String json =  ringtoneJson;
		JSONObject obj = null;
		try {
			obj = new JSONObject(json);
			path = obj.getString("filePath");
			title = obj.getString("title");

		} catch (JSONException e) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
				}
			});

		}
		if(!path.contains(dir)){
			path = dir+File.separator+path;
		}
		File fpath = new File(path);
		if(fpath.exists()){
			ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, path);
            values.put(MediaStore.MediaColumns.TITLE, title);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            values.put(MediaStore.Audio.Media.ARTIST, "None");
            values.put(MediaStore.MediaColumns.SIZE, fpath.length());
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
            values.put(MediaStore.Audio.Media.IS_ALARM, false);
            values.put(MediaStore.Audio.Media.IS_MUSIC, false);
            Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);
            activity.getContentResolver().delete(
                    uri,
                    MediaStore.MediaColumns.DATA + "=\""
                            + path + "\"", null);
            Uri newUri = activity.getContentResolver().insert(uri, values);

            RingtoneManager.setActualDefaultRingtoneUri(
            		activity.getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
                    newUri);
		}else{
			System.out.println("File not found");
		}
	}

//	@JavascriptInterface
//	public void startSMSReceiveListener(){
//		smsReceiver = new ReceiveSMS();
//		if(((ReceiveSMS) smsReceiver).isPlugin()){
//			// Set When broadcast event will fire.
//	        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
//			activity.registerReceiver(smsReceiver, filter);
//		}
//	}
//
//	@JavascriptInterface
//	public void stopSMSListener(){
//		if(smsReceiver != null){
//			activity.unregisterReceiver(smsReceiver);
//		}
//	}

//	@JavascriptInterface
//	public void getInboxSms(String jsonObj) {
//		String successCallBack = "";
//		String failureCallBack = "";
//		JSONObject obj = null;
//		try {
//			obj = new JSONObject(jsonObj);
//			successCallBack = obj.getString("successCallback");
//			failureCallBack = obj.getString("failureCallback");
//		} catch (JSONException e) {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//				}
//			});
//
//		}
//		Uri uri = Uri.parse("content://sms/inbox");
//		Cursor c = activity.getContentResolver().query(uri, null, null, null,null);
//		// Read the sms data and store it in the list
//		JSONObject smsList = null;
//		try {
//			smsList = new JSONObject();
//			JSONArray smsArray = new JSONArray();
//			if (c.moveToFirst()) {
//				for (int i = 0; i < c.getCount(); i++) {
//					JSONObject sms = new JSONObject();
//					String smsBody = c.getString(c.getColumnIndexOrThrow("body")).toString();
//					String smsAddress = c.getString(c.getColumnIndexOrThrow("address")).toString();
//					String smsDate = c.getString(c.getColumnIndexOrThrow("date")).toString();
//					sms.put("body", smsBody);
//					sms.put("address", smsAddress);
//					sms.put("timestamp", smsDate);
//					smsArray.put(sms);
//					c.moveToNext();
//				}
//			}
//			smsList.put("smsList", smsArray);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//
//		}
//		c.close();
//		final String sCallback = successCallBack;
//		final JSONObject resultObj = smsList;
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				webView.loadUrl("javascript:"+ sCallback + "(" + resultObj + ");");
//			}
//		});
//	}

//	@JavascriptInterface
//	public void getMissedCalls(String jsonObj) {
//		String successCallBack = "";
//   		String failureCallBack = "";
//   		JSONObject obj = null;
//   		try {
//   			obj = new JSONObject(jsonObj);
//   			successCallBack = obj.getString("successCallback");
//   			failureCallBack = obj.getString("failureCallback");
//   		} catch (JSONException e) {
//   			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//				}
//			});
//
//   		}
//		TelephonyManager tm= (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//	       if(tm.getPhoneType()==TelephonyManager.PHONE_TYPE_NONE){
//	        //No calling functionality
//	    	   activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-022','','');");
//					}
//				});
//	       }
//	       else
//	       {
//	   		//String[] projection = { CallLog.Calls.CACHED_NAME, CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.TYPE };
//	        String where = CallLog.Calls.TYPE+"="+CallLog.Calls.MISSED_TYPE;
//	   		Cursor cursor = activity.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,where, null, null);
//
//	   		// Read the sms data and store it in the list
//	   		JSONObject missedCallList = null;
//	   		 try {
//	   			missedCallList = new JSONObject();
//	   			JSONArray missedCallArray = new JSONArray();
//	   			if (cursor.moveToFirst()) {
//	   				for (int i = 0; i < cursor.getCount(); i++) {
//	   					// SMSData sms = new SMSData();
//	   					JSONObject missedCall = new JSONObject();
//	   			        String callNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
//	   			        String callDate = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
//	   			        missedCall.put("number", callNumber);
//	   			        missedCall.put("timestamp", callDate);
//	   			        missedCallArray.put(missedCall);
//	   					cursor.moveToNext();
//	   				}
//	   			}
//	   			missedCallList.put("missedCallList", missedCallArray);
//	   		 } catch (JSONException e) {
//	   			// TODO Auto-generated catch block
//
//	   		}
//	   		cursor.close();
//	   		final String sCallback = successCallBack;
//	   		final JSONObject resultObj = missedCallList;
//	   		activity.runOnUiThread(new Runnable() {
//	   			@Override
//	   			public void run() {
//	   				webView.loadUrl("javascript:"+ sCallback + "(" + resultObj + ");");
//	   			}
//	   		});
//	       }
//	}

//	@JavascriptInterface
//	public void launchWebview(String json){
//		if(LaunchWebviewActivity.isPlugin()){
//		JSONObject jsonObj = null;
//		String URL = null;
//		try {
//			jsonObj = new JSONObject(json);
//			SUCCESS_CALLBACK = jsonObj.getString("successCallback");
//			FAILURE_CALLBACK = jsonObj.getString("failureCallback");
//			URL = jsonObj.getString("URL");
//		} catch (JSONException e) {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
//				}
//			});
//
//		}
//		LaunchWebviewActivity.setWebView(webView);
//		Intent in = new Intent(context, LaunchWebviewActivity.class);
//		in.putExtra("URL", URL);
//		try{
//		activity.startActivity(in);
//		}catch(Exception e){
//			System.out.println(e.getMessage());
//		}
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-022','','');");
//				}
//			});
//		}
//	}
//
//	@JavascriptInterface
//	public void closeWebview(){
//		if(LaunchWebviewActivity.isPlugin()){
//			LaunchWebviewActivity.webviewActivity.finish();
//		}
//	}

	@JavascriptInterface
	public void fileToBase64(String json){
	     byte[] bytes;
	     String filePath = null;
		try {
			 JSONObject jsonObj = new JSONObject(json);
			 SUCCESS_CALLBACK = jsonObj.getString("successCallback");
	    	 FAILURE_CALLBACK = jsonObj.getString("failureCallback");
	    	 filePath = jsonObj.getString("filePath");
		} catch (JSONException e1) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
				}
			});

		}
	     try {
	         ByteArrayOutputStream baos = new ByteArrayOutputStream();
	         FileInputStream fis = new FileInputStream(new File(filePath));
	         byte[] buf = new byte[1024];
	         int n;
	         while (-1 != (n = fis.read(buf)))
	             baos.write(buf, 0, n);
	         fis.close();
	         bytes = baos.toByteArray();
	         final String str = Base64.encodeToString(bytes, Base64.DEFAULT);
	         final JSONObject returnJson = new JSONObject();
				try {
					returnJson.put("baseString", str);
				} catch (final JSONException ex) {
					return;
				}
	         activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
//					webView.loadUrl("javascript:" + SUCCESS_CALLBACK + "("	+ returnJson + ");");
				}
			});
	    } catch(final IOException io){

	    	activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
//					webView.loadUrl("javascript:" + FAILURE_CALLBACK + "('"	+ io.getMessage() + "');");
				}
			});
	    } catch (final OutOfMemoryError oom){

	    	activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
//					webView.loadUrl("javascript:" + FAILURE_CALLBACK + "('"	+ oom.getMessage() + "');");
				}
			});
	    }catch (final Exception e){

	    	activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
//					webView.loadUrl("javascript:" + FAILURE_CALLBACK + "('"	+ e.getMessage() + "');");
				}
			});
	    }
	}

	@JavascriptInterface
	public void loadFile(String jsonobj){
		if(LoadPDFInWebView.isPlugin()){
			JSONObject json = null;
			String fLocation = null;
			try {
				json = new JSONObject(jsonobj);
				OPEN_FILE_SUCCESS_CALLBACK = json.getString("successCallback");
				OPEN_FILE_FAILURE_CALLBACK = json.getString("failureCallback");
				fLocation = json.getString("filePath");
			}catch (JSONException e){
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
					}
				});
			}
			String filePath;
			if(fLocation.contains(AppzillonMainScreen.SANDBOX_LOC)){
				filePath = fLocation;
			}else{
				filePath = AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC+File.separator+fLocation;
			}
			String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(filePath)).toString());
			//Log.i("Abhishek", "extension : "+extension);
			if(extension.equalsIgnoreCase("pdf")){
				Intent newIntent = new Intent(activity, LoadPDFInWebView.class);
				newIntent.putExtra("PATH", filePath);
				activity.startActivityForResult(newIntent, RESULT_LOAD_PDF_IN_WEBVIEW);
			}else if(extension.equalsIgnoreCase("txt") || extension.equalsIgnoreCase("xml")){
				Intent newIntent = new Intent(activity, LoadTxtInWebView.class);
				newIntent.putExtra("PATH", filePath);
				activity.startActivityForResult(newIntent, RESULT_LOAD_TXT_IN_WEBVIEW);
			}else{
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-022','','');");
					}
				});
			}
		}else{
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-236','','');");
				}
			});
		}
	}

//	@TargetApi(Build.VERSION_CODES.KITKAT)
//	@JavascriptInterface
//	public void printDoc(String jsonobj){
//		if(PrintDialogActivity.isPlugin()){
//		JSONObject json = null;
//		String successCallBack = null;
//		String faliureCallBack = null;
//		String fLocation = null;
//		try {
//			json = new JSONObject(jsonobj);
//			successCallBack = json.getString("successCallback");
//			faliureCallBack = json.getString("failureCallback");
//			fLocation = json.getString("filePath");
//		}catch (JSONException e){
//
//		}
//
//		String filePath;
//			if(fLocation.contains(AppzillonMainScreen.SANDBOX_LOC)){
//				filePath = fLocation;
//			}else{
//				filePath = AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC+File.separator+fLocation;
//			}
//		final Uri docUri = Uri.fromFile(new File(filePath));
//		String docType = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(filePath)).toString());
//		String docMimeType = null;
//		if(docType.equalsIgnoreCase("pdf")){
//			docMimeType = "application/pdf";
//		}else if (docType.equalsIgnoreCase("txt")){
//			docMimeType =  "text/plain";
//		}else if (docType.equalsIgnoreCase("doc")){
//			docMimeType =  "application/msword";
//		}else if (docType.equalsIgnoreCase("xls")){
//			docMimeType =  "application/vnd.ms-excel";
//		}else if (docType.equalsIgnoreCase("xlsx")){
//			docMimeType =  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//		}else if (docType.equalsIgnoreCase("xml")){
//			docMimeType =  "application/xml";
//		}
//		String docTitle = fLocation.replace("."+docType, "");
//		final String fcallBack = faliureCallBack;
//		final String scallBack = successCallBack;
//
//		if (AppzillonUtils.isNetworkAvailable(activity) == false) {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:" + fcallBack + "();");
//				}
//			});
//		} else {
//			if (android.os.Build.VERSION.SDK_INT < 19) {
//				Intent printIntent = new Intent(activity,PrintDialogActivity.class);
//				printIntent.setDataAndType(docUri, docMimeType);
//				printIntent.putExtra("title", docTitle);
//				activity.startActivity(printIntent);
//			} else {
//				if (docMimeType.contains("pdf")) {
//					PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
//					String jobName = StringUtils.getString(StringUtils.APP_NAME)+ " Document";
//					MyPrintDocumentAdapter pda = new MyPrintDocumentAdapter(filePath);
//					PrintJob val = printManager.print(jobName, pda, null);
//					if(val.isCompleted()){
//						activity.runOnUiThread(new Runnable() {
//							@TargetApi(Build.VERSION_CODES.KITKAT)
//							@Override
//							public void run() {
//								webView.loadUrl("javascript:" + scallBack + "();");
//							}
//						});
//					}else if(val.isFailed()){
//						activity.runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								webView.loadUrl("javascript:" + fcallBack + "();");
//							}
//						});
//					}
//				} else {
//					activity.runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							webView.loadUrl("javascript:" + fcallBack + "();");
//						}
//					});
//				}
//			}
//		}
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-239','','');");
//				}
//			});
//		}
//	}

	//Print WebView
	@JavascriptInterface
	public void createWebPrintJob() {
		if (Build.VERSION.SDK_INT > 19){
		try{
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					PrintManager printManager = (PrintManager) activity
				            .getSystemService(Context.PRINT_SERVICE);
				    // Get a print adapter instance
				    PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();
				    // Create a print job with name and adapter instance
				    String jobName = "Document";
				    printManager.print(jobName, printAdapter,
				            new PrintAttributes.Builder().build());
				}
			});
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}} if (Build.VERSION.SDK_INT < 19){
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-022','','');");
				}
			});
		}
	}
	
//	@JavascriptInterface
//	public void registorBatteryListener(String jsonobj, String val){
//		if(BatteryPlugin.isPlugin()){
//			BatteryPlugin bp = new BatteryPlugin(context,activity,webView);
//			
//			if (val.equalsIgnoreCase("Yes"))
//				bp.registerBatteryReceiver(jsonobj);
//			else if (val.equalsIgnoreCase("No"))
//				bp.unregisterBatteryReceiver(jsonobj);
//			else{
//				activity.runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-022','','');");
//					}
//				});
//			}
//		}else{
//			activity.runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-239','','');");
//				}
//			});
//		}
//	}
//	
//	@JavascriptInterface
//	public void zip(String jsonobj) {
//		if (ZipPlugin.isZipPlugin()) {
//			ServerLog.i(TAG, "zip : " + jsonobj);
//			ZipPlugin zipobj = new ZipPlugin(activity, webView);
//			try {
//				zipobj.zip(jsonobj);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-234','','');");
//				}
//			});
//		}
//	} 
//	
//	@JavascriptInterface
//	public void unzip(String jsonobj) {
//		if (ZipPlugin.isZipPlugin()) {
//			ServerLog.i(TAG, "Unzip : " + jsonobj);
//			ZipPlugin zipobj = new ZipPlugin(activity, webView);
//			try {
//				zipobj.unzip(jsonobj);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//
//			}
//		} else {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-234','','');");
//				}
//			});
//		}
//	}
	
//	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//	@SuppressWarnings("deprecation")
//	@JavascriptInterface
//	public void enableSwipeLayout(String jsonobj){
//		if (PullDown.isDummy) {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-100','','');");  // change later
//				}
//			});
//		}else {
//			ServerLog.i(TAG, "PullDown : " + jsonobj);
//			try {
//				PullDown.enablePullDown(activity, webView,jsonobj);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//
//			}
//		}
//	}

//@JavascriptInterface
//	public void hideRefreshIcon(){
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				if(AppzillonMainScreen.swipeLayout.isRefreshing())
//				AppzillonMainScreen.swipeLayout.setRefreshing(false);
//			}
//		});
//	}
	
//	@JavascriptInterface
//	public void disableSwipeLayout(String jsonobj){
//		if (PullDown.isDummy) {
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-100','','');");  // change later
//				}
//			});
//		}else {
//			ServerLog.i(TAG, "PullDown : " + jsonobj);
//			try {
//				PullDown.disablePullDown(activity, webView,jsonobj);
//			} catch (Exception e) {
//
//			}
//		}
//	}

//@JavascriptInterface	
//  public void getIP(String json){
//	 try{		 
//	  WifiManager wm = (WifiManager) activity.getSystemService(context.WIFI_SERVICE);
//	  String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
//	  JSONObject jsonobj = new JSONObject(json);
//	  SUCCESS_CALLBACK = jsonobj.getString("successCallback");
//	  FAILURE_CALLBACK = jsonobj.getString("failureCallback");	  
//	  final JSONObject ipjson = new JSONObject();
//	  ipjson.put("ip", ip);
//	  activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				webView.loadUrl("javascript:" + SUCCESS_CALLBACK + "("	+ipjson + ");");
//			}
//		});
//	 }catch(Exception ex){		 
//	 }
//  }
//	@JavascriptInterface
//	public void fetchContact(String json){
//		if(Contacts.isContacts())
//		{
//		JSONObject jsonObj;
//		try {
//			jsonObj = new JSONObject(json);
//			SUCCESS_CALLBACK = jsonObj.getString("successCallback");
//			FAILURE_CALLBACK = jsonObj.getString("failureCallback");
//		} catch (JSONException e) {
//		}
//		Contacts contact = new Contacts(context, activity, webView);
//		contact.fectchcontacts(PICK_CONTACTS);
//		}else{
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-022','','');");
//				}
//			});
//		}		
//	}
	
//	@JavascriptInterface
//	public void twitterAuth(String json){
//		if(TwitterAuth.isPlugin()){
//		JSONObject jsonObj;
//		String succ = "";
//		String fail = "";
//		try {
//			jsonObj = new JSONObject(json);
//			succ=jsonObj.getString("successCallback");
//			fail = jsonObj.getString("failureCallback");
//		} catch (JSONException e) {
//
//		}
//	Intent intent = new Intent(context, TwitterAuth.class);
//	TwitterAuth.mActivity(webView);
//	intent.putExtra("successCallback", succ);
//	intent.putExtra("failureCallback", fail);
//	activity.startActivity(intent);
//	}else{
//		activity.runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-022','','');");
//			}
//		});
//		}
//	}
//	
//	@JavascriptInterface
//	public void openYoutubeApp(String json) {
//		if(youtube.isDummyPlugin){
//			activity.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-022','','');");
//				}
//			});
//		}else{
//			new youtube(context, activity, webView).openYouTubeApp(json);
//		}
//	}
//	
	@JavascriptInterface
	public void openBrowser(String json){
		JSONObject jsonObj = null;
		String URL = null;
		try {
			jsonObj = new JSONObject(json);
			URL = jsonObj.getString("url");
			SUCCESS_CALLBACK = jsonObj.getString("successCallback");
			FAILURE_CALLBACK = jsonObj.getString("failureCallback");
		} catch (JSONException e) {

		}
		try{
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
		activity.startActivity(intent);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				webView.loadUrl("javascript:" + SUCCESS_CALLBACK + "();");
			}
		});
		} catch (Exception ex) {
			final JSONObject errorJson = new JSONObject();
				try {
					errorJson.put("errorCode","");
					errorJson.put("errorMessage","Unable to open the Browser");
				} catch (JSONException e) {

				}
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:" + FAILURE_CALLBACK + "("+errorJson+");");
				}
			});
		}
	}
	
	@JavascriptInterface
	public void enableListener(){
		//MissedCall.enableListener();    3.2 changes
	}
	
//	@JavascriptInterface
//	public void testConnection(){
//		Intent intent = new Intent(context,MorphoSampleActivity.class);
//		activity.startActivity(intent);
//	}

//	@JavascriptInterface
//	public void scanFinger(String jsonStr){
//		JSONObject json = null;
//		try {
//			json = new JSONObject(jsonStr);
//			SUCCESS_CALLBACK = json.getString("successCallback");
//			FAILURE_CALLBACK = json.getString("failureCallback");
//		} catch (JSONException e) {
//
//		}
//		Intent intent = new Intent(context,MorphoCapture.class);
//		activity.startActivityForResult(intent, FINGERPRINT);
//	}
	@JavascriptInterface
	public void getfileSize(String json){
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(json);
			String path = jsonObj.getString("filePath");
			SUCCESS_CALLBACK = jsonObj.getString("successCallback");
			FAILURE_CALLBACK = jsonObj.getString("failureCallback");
			File filenew = new File(path);
			long len = filenew.length();
			final int file_size = Integer.parseInt(String.valueOf(len/1024));
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					JSONObject result = new JSONObject();
					try {
						result.put("fileSize", file_size);
					} catch (JSONException e) {

					}
					webView.loadUrl("javascript:" + SUCCESS_CALLBACK + "("+result+");");
				}
			});
		} catch (JSONException e) {
			final JSONObject errorJson = new JSONObject();
			try {
				errorJson.put("errorCode","");
				errorJson.put("errorMessage","Unable to get file size");
			} catch (JSONException err) {
				// TODO Auto-generated catch block

			}
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				webView.loadUrl("javascript:" + FAILURE_CALLBACK + "("+errorJson+");");
			}
		});
		}
	}
	
	@JavascriptInterface
	public void saveBase64ToPdf(String json) {
//		if (Report.isReport()) {
		String base64;
		String ext ;
		final String mSuccessCallback ;
		final String mFailureCallback;
		String fileName;
		String filePath;
		try {
			JSONObject jsonObj = new JSONObject(json);
			base64 = jsonObj.getString("base64");
			ext = jsonObj.getString("extension");
			mSuccessCallback = jsonObj.getString("successCallback");
			mFailureCallback = jsonObj.getString("failureCallback");
			fileName = jsonObj.getString("fileName");
			filePath = jsonObj.getString("filePath");
			if(filePath.length()>0){
				// Do nothing
			}else{
				filePath = "downloads";
			}
		} catch (final JSONException e) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:appzillon.util.displayMessage('APZ-CNT-077','','');");
				}
			});

			return;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(AppzillonMainScreen.SANDBOX_LOC +File.separator+AppzillonMainScreen.ASSET_APP_LOC+filePath+File.separator+fileName+"."+ext);
			BufferedOutputStream buf = new BufferedOutputStream(fos);	
			 byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
			try {
				fos.write(decodedString);
			} catch (IOException e) {

				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						System.out.println("report12");
						webView.loadUrl("javascript:"
								+ mFailureCallback
								+ "("
								+ "{\"errorCode\":\"APZ_AD01\",\"errorDescription\":\""
								+ activity.getResources().getString(R.string.sdcard_unavailable) + "\"}" + ");");
					}
				});
			}
		} catch (FileNotFoundException e) {

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					System.out.println("report12");
					webView.loadUrl("javascript:"
							+ mFailureCallback
							+ "("
							+ "{\"errorCode\":\"APZ_AD01\",\"errorDescription\":\""
							+ activity.getResources().getString(R.string.file_notfound) + "\"}" + ");");
				}
			});
		}			
		try {
			fos.close();
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					webView.loadUrl("javascript:" + mSuccessCallback + "("
							+ "{\"successMessage\":\"" + activity.getResources().getString(R.string.report_save_success) + "\"}" + ");");
				}
			});
		} catch (IOException e) {

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					System.out.println("report12");
					webView.loadUrl("javascript:"
							+ mFailureCallback
							+ "("
							+ "{\"errorCode\":\"APZ_AD01\",\"errorDescription\":\""
							+ activity.getResources().getString(R.string.sdcard_unavailable) + "\"}" + ");");
				}
			});
		}
	}
	
	@JavascriptInterface
	public final String getDeviceRunTimeInfo() {   // 3.2 changes
		final JSONObject obj=new JSONObject();
		try {
			obj.put("DEVICEID", UserSettings.getIMEI(settings));
			obj.put("DEVICETYPE", "ANDROID");
			obj.put("SCREENSIZE",AppzillonUtils.getScreenSize(activity));
			obj.put("SCREENPPI", AppzillonUtils.getPPI(activity));  //3.2 changes 
			obj.put("OTAREQUIRED", AppzillonMainScreen.OTAREQUIRED);
			obj.put("HASHKEY2", UserSettings.getIMSI(settings));
//			Log.i(TAG,"JSONObject : "+obj.toString());
		} catch (final JSONException e) {
			//Log.i(TAG,"Exception JSONObject : "+e.toString());
		}
		return obj.toString();
	}

	@JavascriptInterface
	public final String callServer(String jsonObj) throws JSONException {   // 3.2 changes
		JSONObject obj=new JSONObject(jsonObj);
		String serverResp = null;
		String serverRequest = obj.getString("request");
		String serverUrl = obj.getString("serverurl");
		JSONObject response = ServerUtilities.sendRequestToServer(serverUrl,serverRequest);
		if(response != null){
//			Log.e(TAG, "deviceLoginResponse : "+response);
			serverResp = response.toString();
		}else{
			//Log.e(TAG, "deviceLoginResponse is null ");
		}
		return serverResp;
	}
	
}

