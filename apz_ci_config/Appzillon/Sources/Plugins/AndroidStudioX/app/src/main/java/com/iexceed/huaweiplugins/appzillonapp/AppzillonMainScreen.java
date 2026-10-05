package com.iexceed.appzillonapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.webkit.ConsoleMessage;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.ApzPluginBridge;
import com.iexceed.common.CustomProgressDialog;
import com.iexceed.common.DatabaseHandler;
import com.iexceed.common.ExternalActivityResultHandler;
import com.iexceed.common.GetAppSecToken;
import com.iexceed.common.JavaScriptInterface;
import com.iexceed.common.MultifactorRegistor;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.common.PostAppSecToken;
import com.iexceed.common.ServerUtilities;
import com.iexceed.common.StringUtils;
import com.iexceed.common.UserSettings;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.appidletimeout.AppIdleTimeOut;
import com.iexceed.plugins.barcode.ApzBarcodePlugin;
import com.iexceed.plugins.copydatabase.CopyDatabase;
import com.iexceed.plugins.copystaticfiles.CopyStaticFiles;
import com.iexceed.plugins.errorlog.ApzLogger;
import com.iexceed.plugins.miscellaneous.Miscellaneous;
import com.iexceed.plugins.multiview.ApzPluginMultiView;
import com.iexceed.plugins.multiview.JavaScriptBridge;
import com.iexceed.plugins.notification.NotificationPlugin;
import com.iexceed.plugins.whitelist.EnableWhiteList;
import com.iexceed.plugins.wipeout.WipeOut;

import com.iexceed.common.HMSLocationCallback;
import com.iexceed.common.HMSLocationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Locale;

import static com.iexceed.common.ApzPluginBridge.jsMsgIntentReceiver;
import static com.iexceed.common.MultifactorRegistor.LATITUDE;
import static com.iexceed.common.MultifactorRegistor.LONGITUDE;
import static com.iexceed.plugins.ApzPlugin.APZ_REQ_DEFAULT_PERMISSIONS;
import static com.iexceed.plugins.ApzPlugin.checkPermissionFlag;
import static com.iexceed.plugins.dataSecurity.StoreCredentialsSecurely.createNewKeys;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;

public class AppzillonMainScreen extends ApzActivity implements ActivityCompat.OnRequestPermissionsResultCallback, PostAppSecToken {
    // Prabudas, new plugin Interface class
    private static ApzPluginBridge apzPluginBridge;

    public static WebView webView;

    public static WebView mwebView;

    private WebSettings webSettings;

    public static  AlertDialog.Builder serverErrorDialog;
    // Prabudas
    // Moved to ApzActivity
    // private static SharedPreferences settings;
    final static String properties = "USER_PREFS";

    private String languageCode;

    public static boolean recordingState;

    public static boolean playingState;

    public static boolean pausedState;

//	protected static String goToPage;

    private JavaScriptInterface jsi;


    //public static CustomProgressDialog mProgreesDialog;

//    private String imei;

//    private String imsi;

//	private RelativeLayout mSplashContainer;

    private RelativeLayout mainWebViewContainer;

    private Handler mHandler;

    private boolean isLandingPage;

    private boolean isAppPaused;

    /* For batteryListener */
    private int initialPercent;

    private boolean isInitialPercentSet;

    private DatabaseHandler dbHelp;

//	/*For multiview*/
//	private JavaScriptBridge jsBridge;

	/*For Native Extensibility*/
//	private NativeService nsService;

    public static String ASSET_APP_LOC = "";

    public static String APP_NAME = "";

    public static String APP_REMOTE_DEBUG = "N";

    private final static String TAG = "AppzillonMainScreen";

    public static StringUtils stringUtils;

    public static boolean APP_LAUNCHED = false;

    public static Activity activity;

    public final static String app_props = "APP_PREFS";

    private SharedPreferences apps;

    public static String SANDBOX_LOC = "";

    public static final String ANDROID_OS = "ANDROID";

    public final static String ASSETS_MAIN_FOLDER = "apps";

    public final static String ASSETS_APZ_FOLDER = "appzillon";

    public static String USER_ID_FOR_OTA = "Android";

    public static final String PRE_LOGIN = "preLogin";

    public static final String APP_ID = "appId";

    public static final String SESSION_ID = "sessionId";

    public static final String INTERFACE_ID = "interfaceId";

    public static final String SCREEN_ID = "screenId";

    public static final String DEVICE_ID = "deviceId";

    public static final String APP_STORE_VERSION = "appVersion";   //playstore version

    public static final String UPDATE_APP_STORE_VERSION = "updateAppVersion";   //update playstore version -Y/N

    public static final String DEVICE_NAME = "deviceName";

    public static final String REQUEST_KEY = "requestKey";

    public static final String USER_ID = "userId";

    public static final String REQ_STATUS = "status";

    public static final String APP_VERSION = "appVersion";

    public static final String OS = "os";

    public static final String ASYNC = "async";

    public static final String FILENAME = "fileName";

    public static final String FILEPATH = "filepath";

    public static final String OS_ID = "osId";

    public static final String REQUEST_ID = "requestID";

    public static final String OS_VERSION = "osVersion";

    public static final String MOBILE_ONE = "mobile1";

    public static final String MOBILE_TWO = "mobile2";

    public static final String DEVICE_MODEL = "model";

    public static final String DEVICE_MAKE = "make";

    public static final String SCREEN_RESOLUTION = "screenResolution";

    public static String OTAREQUIRED = "";

    public static final String REMOTE_DEBUG = "remoteDebug";

    public static final String APP_TYPE = "appType";

    public static final String PARENT_APP_ID = "parentAppId";

    public static final String EXPIRY_DATE = "expiryDate";

    public static final String APP_EXPIRED = "expired";

    public static final String APPZILLON_HEADER = "appzillonHeader";

    public static final String APPZILLON_BODY = "appzillonBody";

    public static final String APPZILLON_ERRORS = "appzillonErrors";

    public static final String CONTAINER_APP = "containerApp";

    public static final String WIPE_OUT = "wipeout";

    public static String UPDATE_REQUEST = "N";

    public static String UPDATE_ACTION = "";

    public static String MAIN_APP_NAME;

    private String REMOTE_DEBUG_VALUE = "N";

    private static JSONObject appInstructionsResponseBody;

    public static boolean IS_APP_DEBUGABLE;

    public static String PUSH_MESSAGE;
    public static String SHORTCUT_MSG;

    public static String NOTIFICATION_CODE;

    public static String ACTION_CODE;

    public static int NOTIF_CODE;

    public static String NOTIF_PARAMS;

    public static String NOTIF_TITLE;

    public static String NOTIF_IMG_URL;

    public static boolean IS_BATTERY_CALLBACK_ENABLED = false;

    public static boolean IS_STATE_CALLBACK_ENABLED = false;

    public static boolean IS_THRESHOLD_CALLBACK_ENABLED = false;

    public static boolean IS_REGISTERED = false;

    //public static SwipeRefreshLayout swipeLayout;


//    boolean isOpened = false;

//    public static TelephonyManager mTelephonyMgr = null;

    String isAppFirstTime;

    //public static HashMap<String,JSONObject> notifMap;

    public static JSONArray mapjson;

    String app_loc = null;

    public static boolean isSecured = false;

    public SslErrorHandler globalSSLHandler = null;

    public static boolean IS_SERVER = false;

    private static boolean IS_TRACK_LOCATION = false;

    public String PREVENTREPLAYATTACK = "N";

    public static String SNONCE = "";

    public static String SESSIONTOKEN = "";

    public static String APPZILLONSAFE = "";

    public static String DECRYPTED_KEY = "";

    public static String internalPathCheck = "";

    public static boolean IS_APP_INITIALIZED;

    public static  AlertDialog serverErrorAlertDialog = null;

    public static JSONObject MAIN_APP_PARAMS;

    private boolean safeBrowsingIsInitialized;

    /*APP LAUNCH*/
    public static final String APZ_SIGNATURE = "signature";
    private String onLaunchErrorCode = "";
    private AlertDialog.Builder  allIntfBuilder;
    AlertDialog intfCustomAlertDialog;
    public static final String IS_CONTAINER_LOADED = "isContainerLoaded";
    /*APP LAUNCH*/
    
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            showSplashScreen(); // Prabu, 3.2 changes
            apps = getSharedPreferences(app_props, 0);
            activity = this;
            IS_APP_INITIALIZED = false;
            isAppFirstTime = UserSettings.getIsMainAppFirstTime(apps);

            MAIN_APP_NAME = getResources().getString(R.string.MAINAPPID);

            OTAREQUIRED = getResources().getString(R.string.OTAREQUIRED);

            String app_name = null;

            String app_debug = null;
            final String pushmessage;
            final String shortcutType;

            if (savedInstanceState != null) {
                app_name = savedInstanceState.getString("app_name");
                app_loc = savedInstanceState.getString("app_loc");
                app_debug = savedInstanceState.getString("app_debug");
                pushmessage = savedInstanceState.getString("message");
                shortcutType = savedInstanceState.getString("type");
            } else {
                pushmessage = PUSH_MESSAGE;
                shortcutType = SHORTCUT_MSG;

            }

            if (app_name == null)
                app_name = MAIN_APP_NAME;

            if (app_loc == null)
                app_loc = ASSETS_MAIN_FOLDER + "/" + MAIN_APP_NAME;

            if (app_debug == null)
                app_debug = REMOTE_DEBUG_VALUE;

            if (app_loc != null) {
                ASSET_APP_LOC = app_loc + "/";
            }
            if (app_name != null) {
                APP_NAME = app_name;
            }
            if (app_debug != null) {

                APP_REMOTE_DEBUG = app_debug;
            }
            settings = getSharedPreferences(properties, 0);

            //String ext_storage_state = Environment.getExternalStorageState();

            //Changes for OTA for internal memory Natasha 10-7-2018
            /*if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED) &&  "N".equalsIgnoreCase(getResources().getString(R.string.INTERNALSANDBOX))) {
                SANDBOX_LOC = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + MAIN_APP_NAME;
            } else {
                SANDBOX_LOC = getFilesDir().getAbsolutePath();
                internalPathCheck = SANDBOX_LOC.substring(0,6);
            }*/
            //activity = this;

//        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Camera permission has not been granted.
//
//            requestDefaultPermissions();
//            //return true;
//
//        }else{
//            initSandBoxRelatedFn();
//        }


            // Abhishek For OTA


            //splashDialog = new Dialog(this, R.style.SplashTheme);
            //splashDialog.show();

            //showSplashScreen(); // Prabu, 3.2 changes
            //activity = this;

            if (Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
                    WebView.setWebContentsDebuggingEnabled(true);
                }
            }

            if (!(Build.VERSION.SDK_INT >= VERSION_CODES.M)) {
                checkPermissionFlag = true;
            }

            //apps = getSharedPreferences(app_props, 0);


            // Abhishek For Multifactor


            if (isAppFirstTime.equalsIgnoreCase("YES")) {
                if (Build.VERSION.SDK_INT < VERSION_CODES.M) {
                    createNewKeys("credentials");
                }else{
                    SecureRandom generator = new SecureRandom();
                    StringBuilder randomStringBuilder = new StringBuilder();
                    int randomLength = generator.nextInt(16);
                    char tempChar;
                    for (int i = 0; i < randomLength; i++){
                        tempChar = (char) (generator.nextInt(96) + 32);
                        randomStringBuilder.append(tempChar);
                    }
                    String randomKey = randomStringBuilder.toString();
                    UserSettings.storeRandomKey(randomKey);
                }
                UserSettings.setIsMainAppFirstTime("NO", apps);
            }

            // Abhishek 21 April 2015 to print the server logs as native logs START
            IS_APP_DEBUGABLE = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
            // Abhishek 21 April 2015 to print the server logs as native logs END

            // Abhishek 27 April 2015,Bug id 5184, For Actionable Notification, get the message from intent START
            Intent i = getIntent();
            if(i.hasExtra("type")){
                SHORTCUT_MSG=i.getExtras().getString("action");
            }
            PUSH_MESSAGE = i.getStringExtra("message");
            NOTIF_PARAMS = i.getStringExtra("msgParameters");
            if (i.hasExtra("notification_code")) {
                //Log.d("Intent for notif", "Noti_Code");
                NOTIFICATION_CODE = i.getStringExtra("notification_code");
                ACTION_CODE = i.getStringExtra("action_code");
                NOTIF_CODE = i.getIntExtra("notif_id", -1);
            }
            NOTIF_TITLE = i.getStringExtra("title");
            NOTIF_IMG_URL = i.getStringExtra("image_url");

            // Abhishek 27 April 2015,Bug id 5184, For Actionable Notification, get the message from intent END

            //Abhishek 13 May 2015, New approach introduced for Actionable Notification, when app is in background then bring back the running instance START
//		String app_name = null;
//		String app_loc = null;
//		String app_debug = null;
//		final String pushmessage;
//
//		if(savedInstanceState != null){
//			app_name = savedInstanceState.getString("app_name");
//			app_loc = savedInstanceState.getString("app_loc");
//			app_debug = savedInstanceState.getString("app_debug");
//			pushmessage = savedInstanceState.getString("message");
//		}else{
//			pushmessage = PUSH_MESSAGE;
//
//		}
//
//		if(app_name == null)
//			app_name = MAIN_APP_NAME;
//
//		if(app_loc == null)
//			app_loc = ASSETS_MAIN_FOLDER+"/"+MAIN_APP_NAME;
//
//		if(app_debug == null)
//			app_debug = REMOTE_DEBUG_VALUE;

            //Abhishek 13 May 2015, New approach introduced for Actionable Notification, when app is in background then bring back the running instance END

            //stringUtils = new StringUtils(this,app_loc);

            //Abhishek 12 March 2015 if app crashes and tries to restart then to handle the exception START


            //Abhishek 12 March 2015 if app crashes and tries to restart then to handle the exception END

            //Abhishek 13 May 2015, for Actionable Notification when app is in background.Taking title mode from values folder file only, Because for child app it will crash START
            String titleBarString = "NO"; //StringUtils.getString(StringUtils.ENABLE_NAVIGATION_MODE); for 3.2 changes
            //String titleBarString = stringUtils.getString(StringUtils.ENABLE_NAVIGATION_MODE);
            //Abhishek 13 May 2015, for Actionable Notification when app is in background.Taking title mode from values folder file only, Because for child app it will crash END
            if (savedInstanceState == null) {
                boolean enableNavigation = "YES".equalsIgnoreCase(titleBarString);
                if (enableNavigation) {
                    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
                } else {
                    requestWindowFeature(Window.FEATURE_NO_TITLE);
                }
            }

//		String lang = getResources().getString(R.string.default_lang);
//		String serverurl = getResources().getString(R.string.server_url);

//        String lang = StringUtils.getString(StringUtils.DEFAULT_LANG);
//        String serverurl = StringUtils.getString(StringUtils.SERVER_URL);
////		String isFirstTime = UserSettings.getIsFirstTime(settings);
////		String otpFlag = getResources().getString(R.string.generate_otp);
//        String otpFlag = StringUtils.getString(StringUtils.GENERATE_OTP);


            //Abhishek, bug id 5527, checking app expired START

            //Abhishek, bug id 5527, checking app expired END

            //String isFirstTime = UserSettings.getIsAppFirstTime(APP_NAME, settings);
            // AppzillonUtils.hasUserPermission();


            activity.setTheme(R.style.Removebg);
            if (webView == null) {
                this.setContentView(R.layout.webview);
            }
            //3.2 PullDown

            if (swipeLayout == null) {
                swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
                swipeLayout.setEnabled(false);
            }

            if (webView == null) {
                webView = (WebView) findViewById(R.id.main);
                mwebView = webView;
            }
//		mSplashContainer = (RelativeLayout)findViewById(R.id.spinContainer);
            //Abhishek ,28 Oct 2015 ,Splash screen is shown at landing page activity START
//		if (!stringUtils.getString(StringUtils.SPLACH_ICON).isEmpty()){
//			if(mSplashContainer.getVisibility() == View.GONE){
//				mSplashContainer.setVisibility(View.VISIBLE);
//			}
//			int splash_id = getResources().getIdentifier(AppzillonMainScreen.stringUtils.getString(StringUtils.SPLACH_ICON), "drawable", getPackageName());
//			mSplashContainer.setBackgroundResource(splash_id);
//		}
            //Abhishek ,28 Oct 2015 ,Splash screen is shown at landing page activity END
            mainWebViewContainer = (RelativeLayout) findViewById(R.id.mainWebViewLayout);
            mHandler = new Handler();
            mProgreesDialog = new CustomProgressDialog(AppzillonMainScreen.this);
            mProgreesDialog.setMessage(this.getResources().getString(R.string.progress_loading_msg));
            mProgreesDialog.setCancelable(false);
            webView.setVerticalScrollBarEnabled(false);
            webView.setHorizontalScrollBarEnabled(false);
            ApzPlugin.jsBridge = new JavaScriptBridge(getApplicationContext(), this, webView, mainWebViewContainer);
//		nsService = new NativeService(getApplicationContext(), this, webView);
//		jsi = new JavaScriptInterface(getApplicationContext(), this, webView, mProgreesDialog, mSplashContainer, jsBridge);
            jsi = new JavaScriptInterface(getApplicationContext(), this, webView, mProgreesDialog, splashDialog, ApzPlugin.jsBridge);

            webView.setFocusableInTouchMode(true);
            webView.setFilterTouchesWhenObscured(true);
            //webView.addJavascriptInterface(ApzPlugin.jsBridge, "MultiView");
            webView.addJavascriptInterface(jsi, "Android");
//		webView.addJavascriptInterface(nsService, "NSService");
            // Prabudas, renamed javascriptInterface
            apzPluginBridge = new ApzPluginBridge(webView, this, getApplicationContext());
            webView.addJavascriptInterface(apzPluginBridge, "NativeBridge");
            webSettings = webView.getSettings();
            if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN)
                webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadsImagesAutomatically(true);
            webSettings.setGeolocationEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            webSettings.setGeolocationDatabasePath("");
            webSettings.setSaveFormData(false);
            // to disable text selection
            webView.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            webView.setLongClickable(false);
            final Context appContext = this;
            // String pageToLoad = UserSettings.getPageToLoad(settings);
            
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
            
            webView.setWebViewClient(new WebViewClient() {

                /*  @ Override
                public void onReceivedSslError(WebView view,
                    SslErrorHandler handler, SslError error) {
                    ServerLog.d(TAG, "SSL Error : "+error.toString());
                    handler.proceed();
                } */

                  /*  @Override
                    public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(appContext);
                        builder.setMessage("Untrusted Connection,request cannot be processed ");
    //			   builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
    //				   @Override
    //				   public void onClick(DialogInterface dialog, int which) {
    //					   handler.proceed();
    //				   }
    //			   });
                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handler.cancel();
                                //webView.loadUrl("javascript:appzillon.app.sslErrorCallback()");
                            }
                        });
                        final AlertDialog dialog = builder.create();
                        dialog.show();

                    }*/

                @Override
                public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                    if (isSecured) {
                        handler.proceed();
                    } else {
                        globalSSLHandler = handler;
                        new GetSSLStatus().execute();
                    }

                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {

    //                    InputStream istream = null;
    //                    BufferedReader ibffr;
    //                    StringBuilder strb = new StringBuilder();
    //                    String ibfrline;
    //                    try {
    //                        //For OTA refresh
    //                        if (OTAREQUIRED.equalsIgnoreCase("Y")) {
    //                            File settingsJson = new File(SANDBOX_LOC + File.separator + ASSET_APP_LOC + "screens/config/settings.json");
    //                            istream = new FileInputStream(settingsJson);
    //                        } else {
    //                            istream = getAssets().open(ASSET_APP_LOC + "screens/config/settings.json");
    //                        }
    //
    //                        if (istream != null) {
    //                            ibffr = new BufferedReader(new InputStreamReader(istream));
    //                            while ((ibfrline = ibffr.readLine()) != null) {
    //                                strb.append(ibfrline);
    //                            }
    //                            ibffr.close();
    //                        }
    //                    } catch (IOException e1) {
    //
    //                    }
    //                    String settingsJson = strb.toString();
    //                    settings = getSharedPreferences(properties, 0);
    //                    if (UserSettings.getAppDefaultSettingPresent(APP_NAME, "defaultSettingPresent", "false", settings).equalsIgnoreCase("false")) {
    //                        try {
    //                            JSONObject set = new JSONObject(settingsJson);
    //                            String jsonRoot = set.names().toString().replaceAll("[\\[\\]\"]", ""); // returns root
    //                            String names[] = jsonRoot.split(",");
    //                            UserSettings.setAppValue(APP_NAME, "SettingsRoot", "Settings", settings);
    //                            final JSONObject setting = set;//.getJSONObject(jsonRoot);  3.2 properties changes
    //                            final Iterator<String> iterator = setting.keys();
    //
    //                            new Handler(Looper.getMainLooper()).post(new Runnable() {
    //
    //                                @Override
    //                                public void run() {
    //                                    while (iterator.hasNext()) {
    //                                        String setval = (String) iterator.next();
    //                                        try {
    //                                            UserSettings.setAppValue(APP_NAME, setval, setting.getString(setval), settings);
    //                                            webView.loadUrl("javascript:(function() { "
    //                                                    + "localStorage.setItem('" + setval
    //                                                    + "','" + setting.getString(setval)
    //                                                    + "'); " + "})()");
    //
    //                                        } catch (JSONException e) {
    //
    //                                            return;
    //                                        } finally {
    //                                            UserSettings.setAppDefaultSettingPresent(APP_NAME, "defaultSettingPresent", "true", settings);
    //                                        }
    //                                    }
    //                                    //Abhishek bug id 4549 saving device type START
    //                                    //webView.loadUrl("javascript:(function() { localStorage.setItem('DEVICETYPE','"+"ANDROID"+"'); " + "})()");
    //                                    //Abhishek bug id 4549 saving device type END
    //                                    //Abhishek 27 October 2015 Setting trackLocation START
    //                                    //webView.loadUrl("javascript:(function() { localStorage.setItem('TRACKLOCATION','"+StringUtils.getString(StringUtils.TRACK_LOCATION)+"'); " + "})()");
    //                                    //Abhishek 27 October 2015 Setting trackLocation END
    //                                    //Siddu 01 July 2016 loadWhiteList START
    //
    //                                }
    //                            });
    //
    //                        } catch (JSONException e) {
    //
    //                        }
    //
    //                    }

                    if (!EnableWhiteList.isDummyPlugin) {
                        final SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                        String value = (mSharedPreference.getString("URLWHITELIST", "Default_Value"));
                        if (!value.equalsIgnoreCase("Loaded"))
                            new EnableWhiteList(getApplicationContext(), activity, webView).loadWhitelist();
                        //Siddu 01 July 2016 loadWhiteList END
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {

                    //Abhishek commented out for Bug id 4284
    //				 if(mSplashContainer.getVisibility() == View.VISIBLE){
    //						mSplashContainer.setVisibility(View.GONE);
    //					}

                    //Abhishek 15 April 2015 wait for page to launch then call app.instructions START
                    //Other wise it may not launch function app.instructions
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        public void run() {

                            //Abhishek 17 March 2015 Added for remote wipe out START
                            JSONObject appInstructionsRes = getAppInstructionsResponceBody();
                            if (appInstructionsRes != null) {
                                //Abhishek 24 April 2015, changed to give control to infra side,Ashutosh Mail, START
    //								webView.loadUrl("javascript:appzillon.app.instructions ("+appInstructionsRes +");");
                                //.loadUrl("javascript:appzillon.util.instructions (" + appInstructionsRes + ");");
                                //Abhishek 24 April 2015, changed to give control to infra side,Ashutosh Mail, END
                            }
                            //Abhishek 17 March 2015 Added for remote wipe out END
                        }
                    };
                    handler.postDelayed(r, 1000);
                    //Abhishek 15 April 2015 wait for page to launch then call app.instructions END

                    //Abhishek,bug id 5332, 08 May 2015, launch push notification when pageloding ends START
                    //Abhishek, 13 May 2015 , already retrived, so commented out START
    //				 String pushmessage = i.getStringExtra("message");
                    //Abhishek, 13 May 2015 , already retrived, so commented out END
                    //Abhishek, Bug id 5283, When Notification came before this point, perform remaining tasks START
                    //  if (pushmessage != null || GCMIntentService.IS_BEFORE_LAUNCH) {
                    //pushmessage(pushmessage);


                    //  }
                    APP_LAUNCHED = true;
                    //Abhishek, Bug id 5283, When Notification came before this point, perform remaining tasks END

                    //Abhishek,bug id 5332, 08 May 2015, launch push notification when pageloding ends END

                    // set SNONCE value to infra changes start
                    if(!TextUtils.isEmpty(SNONCE)){
                        webView.loadUrl("javascript:apz.server.setAppSecToken('Y');");
                    }else{
                        webView.loadUrl("javascript:apz.server.setAppSecToken('N');");
                    }
                    // set SNONCE value to infra changes end
					// internal Apz SDK call
                    if(MAIN_APP_PARAMS != null){
                        String JS_CALLBACK = "apz.ns.setMainAppParams";
                        final String msg = JS_CALLBACK + "(" + MAIN_APP_PARAMS + ");";
                        webView.evaluateJavascript(msg, null);
                    }

                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
                
                @Override
                public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
                    // The "true" argument indicates that your app reports incidents like this one to Safe Browsing.
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
                        if (Build.VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
                            callback.backToSafety(true);
                        }
                        Toast.makeText(view.getContext(), "Unsafe web page blocked.",Toast.LENGTH_LONG).show();
                    }
                }
            });

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onGeolocationPermissionsShowPrompt(final String origin, final Callback callback) {
                    final boolean remember = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(AppzillonMainScreen.this);
                    builder.setTitle(R.string.geolocation_title);
                    builder.setMessage(R.string.share_geoloc_msg)
                            .setCancelable(true)
                            .setPositiveButton(R.string.allow_geolocation, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // origin, allow, remember
                                    callback.invoke(origin, true, remember);
                                }
                            })
                            .setNegativeButton(R.string.not_allow_geolocation, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // origin,not allow, remember
                                    callback.invoke(origin, false, remember);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
             // Log fix start
                         @Override
                         public boolean onConsoleMessage(ConsoleMessage cm) {
                             return true;
                         }
                         // Log fix end
            });

		/* To set imei and imsi */

//		WifiManager wifimanager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            //mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//		DeviceInfo di = new DeviceInfo(getApplicationContext(), this);
            ////// deviceidchanges starts
//		imei = "ANDROID";

            //imei = AppzillonUtils.getDeviceId(getApplicationContext());
            //UserSettings.setIMEI(imei, settings);

//		imsi = "ANDROID";
            //imsi = mTelephonyMgr.getSubscriberId();
            //if(imsi == null){
            //imsi = imei.substring(1);
            //}

            //UserSettings.setIMSI(imsi, settings);
            ////// deviceidchanges ends

            webView.setVerticalScrollBarEnabled(true);
            webView.setHorizontalScrollBarEnabled(false);
            webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            webView.setScrollbarFadingEnabled(true);

//        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
//                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Camera permission has not been granted.
//
//            requestDefaultPermissions();
//            //return true;
//
//        }else{
//            initSandBoxRelatedFn();
//        }
            if(getResources().getString(R.string.SERVER).equalsIgnoreCase("Y")){
                IS_SERVER = true;
            }
            if(getResources().getString(R.string.TRACKLOCATION).equalsIgnoreCase("Y")){
                IS_TRACK_LOCATION = true;
            }

        /*if (IS_SERVER) {*/

            boolean granted = false;
            if(IS_TRACK_LOCATION && "N".equalsIgnoreCase(getResources().getString(R.string.INTERNALSANDBOX))){
                granted = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
            }else if(IS_TRACK_LOCATION){
                granted = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
            }else if("N".equalsIgnoreCase(getResources().getString(R.string.INTERNALSANDBOX))){
                granted = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
            }
            if (granted) {
                // permissions has not been granted.
                requestDefaultPermissions();

            } else {
                //initSandBoxRelatedFn();
                scopeStorageMigration();
            }

        /*} else {
            if(IS_TRACK_LOCATION){
                if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestDefaultPermissions();
                }else {
                    initSandBoxRelatedFn();
                }
            }else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestDefaultPermission();
            } else {
                initSandBoxRelatedFn();
            }
        }*/
        } catch (Resources.NotFoundException e) {
            Log.d("Exception" ,"run-time exception onCreate");
        }

    }

    private void launchAppzillonFirstPage() {
        //Abhishek START For OTA refresh
        if (OTAREQUIRED.equalsIgnoreCase("Y")) {
            //webView.loadUrl("file:///"+SANDBOX_LOC+"/"+ASSET_APP_LOC+"screens/appzillon.html");
//            webView.loadUrl("file:///" + SANDBOX_LOC + "/" + APP_NAME + ".html");
            webView.loadUrl(AppzillonUtils.validateApzWebViewURL("file:///" + SANDBOX_LOC + "/" + APP_NAME + ".html"));
        } else {
            //webView.loadUrl("file:///android_asset/"+ASSET_APP_LOC+"screens/appzillon.html");
//            webView.loadUrl("file:///android_asset/" + MAIN_APP_NAME + ".html");
            webView.loadUrl(AppzillonUtils.validateApzWebViewURL("file:///android_asset/" + MAIN_APP_NAME + ".html"));
        }
    }


    private void multiFactorRegister() {
        if(IS_SERVER){
            MultifactorRegistor mf = new MultifactorRegistor(getApplicationContext(), this);
            mf.startMultifactorRegistor();
        }

    }

    private void postCopyAssetFolder() {

        if (isNetworkAvailable() && IS_SERVER) {
            new AppInstructions().execute();  // sid changes here
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Abhishek 17 March 2015 for sending appMaster response body to app.instructions START
    public static JSONObject getAppInstructionsResponceBody() {
        //Abhishek 27 April 2015, Bug id 5139, send response only for master app START
//			return appInstructionsResponseBody;
        JSONObject resp = appInstructionsResponseBody;
        //  appInstructionsResponseBody = null;
        return resp;
        //Abhishek 27 April 2015, Bug id 5139, send response only for master app END

    }

    //Abhishek,bug id 5529, passing server URL also as argument START
//		public static HttpResponse getAppInstructions(String appName,String presentAppID) {
    public static JSONObject getAppInstructions(String appName, String presentAppID, String serverUrl) {
        //Abhishek,bug id 5529, passing server URL also as argument END
//			HttpResponse response = null;
        JSONObject jsonObject = new JSONObject();
        String ip = AppzillonUtils.ipAddress(AppzillonMainScreen.activity);
        try {

            JSONObject header = new JSONObject();
            header.put(PRE_LOGIN, "true");

            //Abhishek, bug id 5333 , sending present app id in header app id START
//				header.put(APP_ID, activity.getResources().getString(R.string.app_id));
            header.put(APP_ID, presentAppID);
            //Abhishek, bug id 5333 , sending present app id in header app id END

            header.put(SESSION_ID, "");
            header.put(INTERFACE_ID, "appzillonGetAppMasterDetails");
            header.put(SCREEN_ID, "login");
            header.put(DEVICE_ID, ANDROID_OS);
            header.put(REQUEST_KEY, "");
            header.put(USER_ID, USER_ID_FOR_OTA);
            header.put(REQ_STATUS, true); // "success" 3.2 changes
            header.put("origination", ip);
            header.put("source", "APPZILLON");

            JSONObject reqBody = new JSONObject();
            JSONObject appFileReq = new JSONObject();
            appFileReq.put(APP_ID, appName);
//				DeviceInfo di = new DeviceInfo(activity.getApplicationContext(), activity);
            String phoneDeviceId = AppzillonUtils.getDeviceId(activity.getApplicationContext());
            String currAppStoreVersion = AppzillonUtils.getCurrentAppStoreVersion(activity);
            String updateAppVersion ="N";
            try {
                String appVersionCodeStr = UserSettings.getAppVersionCode(APP_NAME,settings);
                if (!appVersionCodeStr.equalsIgnoreCase("0")) {
                    int currentAppVersionCode = Integer.parseInt(appVersionCodeStr);
                    if(BuildConfig.VERSION_CODE > currentAppVersionCode){
                        updateAppVersion ="Y";
                    }else{
                        updateAppVersion ="N";
                    }
                }
            } catch(Exception nfe) {

            }
            appFileReq.put(DEVICE_ID, phoneDeviceId);
            appFileReq.put(APP_STORE_VERSION, currAppStoreVersion);
            appFileReq.put(UPDATE_APP_STORE_VERSION, updateAppVersion);
            appFileReq.put(OS, ANDROID_OS);
            reqBody.put("appzillonAppMasterRequest", appFileReq);
            jsonObject.put(APPZILLON_HEADER, header);
            jsonObject.put(APPZILLON_BODY, reqBody);
        } catch (Exception jsonException) {
            
        }
//        Log.i(TAG, "appzillonAppMasterRequest : " + jsonObject.toString());
//			String serverUrl = activity.getResources().getString(R.string.server_url);
        JSONObject response = ServerUtilities.sendRequestToServer(serverUrl, jsonObject.toString());


        return response;
    }

    class AppInstructions extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String mainAppId = StringUtils.getString(StringUtils.APP_ID);

            //Abhishek, bug id 5529, passing server URL as argument START
            String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
//				HttpResponse response = getAppInstructions(mainAppId,mainAppId);
            JSONObject response = getAppInstructions(mainAppId, mainAppId, serverUrl);
            //Abhishek, bug id 5529, passing server URL as argument END

            //Abhishek, bug id 5333, sending mainAppId as both appname and appid END

            if (response != null) {
                try {
                    JSONObject body = response.getJSONObject(APPZILLON_BODY);
                    JSONObject header = response.getJSONObject(APPZILLON_HEADER);

                    String appId = header.getString("appId");
                    if (appInstructionsResponseBody != null) {
                        appInstructionsResponseBody = null;
                    }
                    appInstructionsResponseBody = body.getJSONObject(appId);
                    MAIN_APP_NAME = appInstructionsResponseBody.getString(APP_ID);
//
                    if (appInstructionsResponseBody.getString(APP_EXPIRED).equalsIgnoreCase("Y")) {
//							//TODO Abhishek when app is expired
                    }

                    String lVersion = UserSettings.getAppVersion(MAIN_APP_NAME, apps);

                    //Abhishek Bug id 4439 START
                    if (lVersion.equalsIgnoreCase("0.0.0")) {
                        lVersion = StringUtils.getString(StringUtils.APP_VERSION);
                    }
                    //Abhishek Bug id 4439 END
                    if ((OTAREQUIRED).equalsIgnoreCase("Y")) {
                        if (!lVersion.equalsIgnoreCase(appInstructionsResponseBody.getString(APP_VERSION))) {
                            UPDATE_REQUEST = "Y";

                            //Abhishek 15 April 2015 adding to responce START
                            appInstructionsResponseBody.put("upgradeRequired", "Y");
                            //Abhishek 15 April 2015 adding to responce END

                            //Abhishek bug id 4798 Commented out START
//							UserSettings.setAppVersion(MAIN_APP_NAME, appInstructionsResponseBody.getString(APP_VERSION), apps);
                            //Abhishek bug id 4798 Commented out END
                        } else {
                            UPDATE_REQUEST = "N";

                            //Abhishek 15 April 2015 adding to responce START
                            appInstructionsResponseBody.put("upgradeRequired", "N");
                            //Abhishek 15 April 2015 adding to responce END
                        }
                    }else{
                        UPDATE_REQUEST = "N";

                        //Abhishek 15 April 2015 adding to responce START
                        appInstructionsResponseBody.put("upgradeRequired", "N");
                        //Abhishek 15 April 2015 adding to responce END
                    }

                    //check for force update Action
                    if(appInstructionsResponseBody.has("updateAction")){
                        UPDATE_ACTION = appInstructionsResponseBody.getString("updateAction");
                    }
                    //check for force update Action
                    REMOTE_DEBUG_VALUE = appInstructionsResponseBody.getString(REMOTE_DEBUG);

                    if (REMOTE_DEBUG_VALUE.equalsIgnoreCase("Y")) {
                        ApzPlugin.debugLevel = 4;
                    }

                    //Abhishek bug id 4405, for single app it was throwing error START
//						appInstructionsResponseBody.getString(PARENT_APP_ID);
                    //Abhishek bug id 4405, for single app it was throwing error END

                    String expiryDate = appInstructionsResponseBody.getString(EXPIRY_DATE);
                    //TODO Abhishek do some thing with expiry date

                    if (appInstructionsResponseBody.getString(CONTAINER_APP).equalsIgnoreCase("Y")) {
                        //TODO Abhishek when it is a appzillonapp app
                    }
                    //Abhishek 27 March 2015 wipe out app if it is appzillonapp app START
                    if (appInstructionsResponseBody.getString(WIPE_OUT).equalsIgnoreCase("Y")) { // && getResources().getString(R.string.container_app).equalsIgnoreCase("Y") // containerApp Removed
                        //Abhishek 27 March 2015 wipe out app if it is appzillonapp app END
                        //WipeOut wipe = new WipeOut(null,activity,getApplicationContext());
                        WipeOut.wipeOutMainApp();
                        activity.finish();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    
                }

            } else {
//					ServerLog.e(TAG, "AppInstructions : Server Error");
            }
//				String lResponse = null;
//
//
//				if (response != null && response.getStatusLine().getStatusCode() == 200) {
//
//					HttpEntity entity = response.getEntity();
//					InputStream inpStream;
//					try {
//						inpStream = entity.getContent();
//						lResponse = OTAPlugin.getStringFromInputStream(inpStream);
//						ServerLog.i(TAG, ""+lResponse);
//						JSONObject obj = new JSONObject(lResponse);
//						final JSONObject body = obj.getJSONObject(APPZILLON_BODY);
//						final JSONObject header = obj.getJSONObject(APPZILLON_HEADER);
//
//						String appId = header.getString("appId");
//						appInstructionsResponseBody = body.getJSONObject(appId);
//						MAIN_APP_NAME = appInstructionsResponseBody.getString(APP_ID);
//
//						if(appInstructionsResponseBody.getString(APP_EXPIRED).equalsIgnoreCase("Y")){
//							//TODO Abhishek when app is expired
//						}
//
//						String lVersion = UserSettings.getAppVersion(MAIN_APP_NAME, apps);
//
//						//Abhishek Bug id 4439 START
//						if(lVersion.equalsIgnoreCase("0.0.0")){
//							lVersion = getResources().getString(R.string.app_version);
//						}
//						//Abhishek Bug id 4439 END
//						if(!lVersion.equalsIgnoreCase(appInstructionsResponseBody.getString(APP_VERSION))){
//							UPDATE_REQUEST = "Y";
//
//							//Abhishek 15 April 2015 adding to responce START
//							appInstructionsResponseBody.put("upgradeRequired", "Y");
//							//Abhishek 15 April 2015 adding to responce END
//
//							//Abhishek bug id 4798 Commented out START
////							UserSettings.setAppVersion(MAIN_APP_NAME, appInstructionsResponseBody.getString(APP_VERSION), apps);
//							//Abhishek bug id 4798 Commented out END
//						}else{
//							UPDATE_REQUEST = "N";
//
//							//Abhishek 15 April 2015 adding to responce START
//							appInstructionsResponseBody.put("upgradeRequired", "N");
//							//Abhishek 15 April 2015 adding to responce END
//						}
//
//
//						REMOTE_DEBUG_VALUE = appInstructionsResponseBody.getString(REMOTE_DEBUG);
//
//						//Abhishek bug id 4405, for single app it was throwing error START
////						appInstructionsResponseBody.getString(PARENT_APP_ID);
//						//Abhishek bug id 4405, for single app it was throwing error END
//
//						String expiryDate = appInstructionsResponseBody.getString(EXPIRY_DATE);
//						//TODO Abhishek do some thing with expiry date
//
//						if(appInstructionsResponseBody.getString(CONTAINER_APP).equalsIgnoreCase("Y")){
//							//TODO Abhishek when it is a appzillonapp app
//						}
//						//Abhishek 27 March 2015 wipe out app if it is appzillonapp app START
//						if(appInstructionsResponseBody.getString(WIPE_OUT).equalsIgnoreCase("Y") && getResources().getString(R.string.container_app).equalsIgnoreCase("Y") ){
//						//Abhishek 27 March 2015 wipe out app if it is appzillonapp app END
//							WipeOut wipe = new WipeOut(getApplicationContext(),activity,null);
//							wipe.wipeOutMainApp();
//							activity.finish();
//						}
//
//
//
//					} catch (IllegalStateException e) {
//						
//					} catch (IOException e) {
//						
//					} catch (JSONException e) {
//						
//					}
//
//				}


            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    //	webView.loadUrl("javascript:appzillon.plugin.setContainerReady('Y');");
                    onContainerLoaded("Y");
                }
            });


        }
    }

    private void copyAssets(AssetManager mgr, String path, int level) {

        ApzLogger.i(TAG, "going to write logs");
        //Abhishek 10 April 2015 START
        if (path.contains("staticfiles")) {
            // Do Not copy static files and sqilte into sandbox
        } else {
            //Abhishek 10 April 2015 END
            try {
                String list[] = mgr.list(path);
                if (list.length == 0) {
                    // Abhishek : Copy files
                    InputStream in = null;
                    OutputStream out = null;
                    boolean copyFile = true;

                    if(path.contains("APPSDB.sqlite") && !isAppFirstTime.equalsIgnoreCase("YES")){
                        copyFile = false;
                    }
                    if (copyFile) {
                        in = mgr.open(path);
//                        File outFile = new File(SANDBOX_LOC + "/" + path);
                        File outFile = AppzillonUtils.getApzFile(SANDBOX_LOC + "/" + path,null);
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    }
                } else {
                    // Abhishek : create Folder
                    // new File(getFilesDir()+"/"+path).mkdirs(); // Path to sandbox
//                    new File(SANDBOX_LOC + "/" + path).mkdirs();
                    AppzillonUtils.getApzFile(SANDBOX_LOC + "/" + path,null).mkdirs();

                }

                if (list != null)
                    for (int i = 0; i < list.length; ++i) {
                        copyAssets(mgr, path + "/" + list[i], level + 1);
                    }
            } catch (IOException e) {
               
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    private void createFolder(String loc) {
//        File file = new File(loc);
        File file = AppzillonUtils.getApzFile(loc,null);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    /**
     * initialize events default settings
     */
    private void initializeEventsDefault() {
        // if isDefaultEventsInitialized() returns false then initialized to defaults
        if ("false".equalsIgnoreCase(UserSettings.isDefaultEventsInitialized(settings))) {
            String[] eventsKey = {
                    "allEvents",
                    "batteryEvent",
                    "backButtonEvent",
                    "menuButtonEvent",
                    "volUpButtonEvent",
                    "volDownButtonEvent",
                    "appPausedEvent",
                    "appResumedEvent",
                    "appSearchButtonEvent",
                    "appCallStartEvent",
                    "appCallEndEvent"
            };
            for (int i = 0; i < eventsKey.length; i++) {
                UserSettings.setAppValue(APP_NAME, eventsKey[i], "off", settings);
            }
        }
    }

    public static void showExpiryMsg(Context context, Activity act, final String appName) {
        AlertDialog.Builder alert = new AlertDialog.Builder(act);
        alert.setMessage(context.getResources().getString(R.string.appExpiryMsg));
        alert.setCancelable(false);
        alert.setPositiveButton(context.getResources().getString(R.string.appExpiryMsgOK), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserSettings.setApplicationExpired("true", appName, settings);
                if (appName.equalsIgnoreCase(APP_NAME))
                    activity.finish();
            }
        });
        alert.create().show();
    }

    @Override
    protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Prabudas , on activity results are redirected to calling plugins START
//        if (requestCode == ApzPlugin.GOOGLE_PLUS_SIGN_IN) {
//            if (resultCode == RESULT_OK) {
//            	ApzGoogleAuthPlugin.connect(requestCode,resultCode);
//            }
//        }
        if (this.mapActivityResultHandler != null) {
            ExternalActivityResultHandler resultHandler = this.mapActivityResultHandler
                    .get(requestCode);
            if (resultHandler != null) {
                this.removeExternalActivityResultHandler(requestCode);
                resultHandler.handleActivityResult(resultCode, data);
            }
        }


    }

    @Override
    public void onBackPressed() {

        String allEvents = UserSettings.getAppValue(APP_NAME, "allEvents", "off", settings);
        String backButtonEvent = UserSettings.getAppValue(APP_NAME, "backButtonEvent", "off", settings);
        if (allEvents.equals("on") || backButtonEvent.equals("on")) {
            JSONObject result = new JSONObject();
            try {
                result.put("event", "backButton");
                ApzPluginUtil.sendSuccess(Miscellaneous.eventId, result, true, activity, mwebView, true);
            } catch (JSONException e) {

            }
        }else{
		webView.loadUrl("javascript:(function() {" + " if(apz.currScr == '" + StringUtils.getString(StringUtils.FIRST_PAGE) + "'){apz.ns.closeApplication({});}})()");
	}
    }


    @Override
    public final void onPause() {
        super.onPause();
		ApzBarcodePlugin.checkForBarcodeOnPause();
        isAppPaused = true;
        unregisterReceiver(receiver);
        if (!isLandingPage) {
            if (UserSettings.getAppValue(APP_NAME, "allEvents", "off", settings).equals("on") || UserSettings.getAppValue(APP_NAME, "appPausedEvent", "off", settings).equals("on")) {
                JSONObject result = new JSONObject();
                try {
                    result.put("event", "appPaused");
                    ApzPluginUtil.sendSuccess(Miscellaneous.eventId, result, true, activity, mwebView, true);
                } catch (JSONException e) {
                   
                }
            }
        }
    }

    /*public static BroadcastReceiver Notification_button_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action_code = intent.getStringExtra("action_code");
            String notification_code = intent.getStringExtra("notification_code");


        }
    };*/

    @Override
    protected void onStart() {
        super.onStart();
        try {
//            if (StringUtils.getString(StringUtils.IS_NOTIFICATION_SUPPORTED).equalsIgnoreCase("Y")) {
//               /* IntentFilter intentFilter = new IntentFilter(
//                        "com.broadcast.notification");
//                registerReceiver(Notification_button_receiver, intentFilter);*/
//            }
        } catch (Exception e) {
           
        }
    }


    @Override
    public final void onResume() {
        super.onResume();
		ApzBarcodePlugin.checkForBarcodeOnResume();
        if (BuildConfig.DEBUG) {
        } else {
            if (AppzillonUtils.isRunningOnEmulator()) {
                //TODO Show Alert Dialog
                Toast.makeText(this, "App is not supported on Emulator.", Toast.LENGTH_SHORT).show();
                finish();
            } else if (AppzillonUtils.isDeviceRooted(this)) {
                //TODO Show Alert Dialog
                Toast.makeText(this, getRToastMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (isAppPaused) {
            if (UserSettings.getAppValue(APP_NAME, "allEvents", "off", settings).equals("on") || UserSettings.getAppValue(APP_NAME, "appResumedEvent", "off", settings).equals("on")) {
                JSONObject result = new JSONObject();
                try {
                    result.put("event", "appResumed");
                    ApzPluginUtil.sendSuccess(Miscellaneous.eventId, result, true, activity, mwebView, true);
                } catch (JSONException e) {

                }
            }
        }
    }

    @Override
    protected final void onDestroy() {

 
            if (swipeLayout != null) {
                swipeLayout.destroyDrawingCache();
            }
        if (this.splashDialog.isShowing()) {
            this.splashDialog.dismiss();
        }
            if (jsi != null) {
                jsi.releseResources(); // to release resource
            }

            try {
                //  RegisterToGCM.unregisterDeviceToGCM(getApplicationContext());
                //  unregisterReceiver(Notification_button_receiver);
            } catch (Error e) {
            }
            webView.clearCache(true);
            //MediaUtils.deleteExternalStoragePublicPicture("Tmp", getApplicationContext());

            if (dbHelp != null) {
                dbHelp.close();
            }
            if (jsi != null) {
                jsi = null;
            }
            if (ApzPlugin.jsBridge != null) {
                ApzPlugin.jsBridge = null;
            }
//		if (nsService != null) {
//			nsService = null;
//		}
            if (webView != null) {
                swipeLayout.removeView(webView);
                webView.removeAllViews();
                webView.destroy();
                webView = null;
            }



        this.activity.unregisterReceiver(jsMsgIntentReceiver);
        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        JSONObject result = new JSONObject();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    checkVolumeState("U");
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    checkVolumeState("D");
                }
                return true;
            case KeyEvent.KEYCODE_MENU:
                if (action == KeyEvent.ACTION_DOWN) {
                    try {
                        result.put("event", "menuButton");
                    } catch (JSONException e) {
                       
                    }
                    if (UserSettings.getAppValue(APP_NAME, "allEvents", "off", settings).equals("on") || UserSettings.getAppValue(APP_NAME, "menuButtonEvent", "off", settings).equals("on")) {
                        ApzPluginUtil.sendSuccess(Miscellaneous.eventId, result, false, activity, mwebView, true);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_CALL:
                if (action == KeyEvent.ACTION_DOWN) {
                    try {
                        result.put("event", "callButton");
                    } catch (JSONException e) {
                      
                    }
                    if (UserSettings.getAppValue(APP_NAME, "allEvents", "off", settings).equals("on") || UserSettings.getAppValue(APP_NAME, "appCallStartEvent", "off", settings).equals("on")) {
                        ApzPluginUtil.sendSuccess(Miscellaneous.eventId, result, true, activity, mwebView, true);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_SEARCH:
                if (action == KeyEvent.ACTION_DOWN) {
                    try {
                        result.put("event", "searchButton");
                    } catch (JSONException e) {
                       
                    }
                    if (UserSettings.getAppValue(APP_NAME, "allEvents", "off", settings).equals("on") || UserSettings.getAppValue(APP_NAME, "appSearchButtonEvent", "off", settings).equals("on")) {
                        ApzPluginUtil.sendSuccess(Miscellaneous.eventId, result, true, activity, mwebView, true);
                    }
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }


    /**
     * To check current volume level of the device
     */
    private void checkVolumeState(String volUpDown) {
     JSONObject result = new JSONObject();
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        final int currVolLevel = audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currVolLevel, AudioManager.FLAG_SHOW_UI);


        if (volUpDown.equals("U")) {
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
            try {
                result.put("event", "volumeUp");
                result.put("volumeLevel", currVolLevel);
            } catch (JSONException e) {
                
            }
            if (UserSettings.getAppValue(APP_NAME, "allEvents", "off", settings).equals("on") || UserSettings.getAppValue(APP_NAME, "volUpButtonEvent", "off", settings).equals("on")) {
                ApzPluginUtil.sendSuccess(Miscellaneous.eventId, result, true, activity, mwebView, true);
            }
        } else if (volUpDown.equals("D")) {
            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
            try {
                result.put("event", "volumeDown");
                result.put("volumeLevel", currVolLevel);
            } catch (JSONException e) {
               
            }
            if (UserSettings.getAppValue(APP_NAME, "allEvents", "off", settings).equals("on") || UserSettings.getAppValue(APP_NAME, "volDownButtonEvent", "off", settings).equals("on")) {
                ApzPluginUtil.sendSuccess(Miscellaneous.eventId, result, true, activity, mwebView, true);
            }
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent batteryIntent) {
            if (UserSettings.getAppValue(APP_NAME, "allEvents", "off", settings).equals("on") || UserSettings.getAppValue(APP_NAME, "batteryEvent", "off", settings).equals("on")) {
                final int batteryLevel = batteryIntent.getIntExtra("level", 0);

                if (!isInitialPercentSet) {
                    initialPercent = batteryLevel;
                    isInitialPercentSet = true;
                }
                if ((batteryLevel - initialPercent > 0) && (batteryLevel - initialPercent) >= 5) {
                    // update battery level
                    initialPercent = batteryLevel;
                    // batteryPercentage.append(batteryLevel);
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            //webView.loadUrl("javascript:batteryStateChange('" + batteryLevel + "');");
                        }
                    });

                } else if ((initialPercent - batteryLevel) >= 5) {
                    // update battery level
                    initialPercent = batteryLevel;
                    // batteryPercentage.append(batteryLevel);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            //webView.loadUrl("javascript:batteryStateChange('" + batteryLevel + "');");
                        }

                    });
                }
            }
        }

    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
           
//			mHandler.post(new Runnable() {
//				 @ Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.onOrientationChange('landscapelo');");
//				}
//			});

            final Intent intent = new Intent("com.iexceed.orientation");
            intent.putExtra("orientation", "LANDSCAPE");
            //Natasha's changes 30/6/2017 Security Changes
            // activity.sendBroadcast(intent);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            //Abhishek bug id 5362 START
//			jsi.resizeMultiviewOnOrientationChange();
            if (jsi != null)
                ApzPluginMultiView.resizeMultiviewOnOrientationChange(activity);
            //Abhishek bug id 5362 END

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
          
//			mHandler.post(new Runnable() {
//				 @ Override
//				public void run() {
//					webView.loadUrl("javascript:appzillon.util.onOrientationChange('portraitlo');");
//				}
//			});

            final Intent intent = new Intent("com.iexceed.orientation");
            intent.putExtra("orientation", "PORTRAIT");
            //Natasha's changes 30/6/2017 Security Changes
            //activity.sendBroadcast(intent);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            //Abhishek bug id 5362 START
//			jsi.resizeMultiviewOnOrientationChange();
            if (jsi != null)
                ApzPluginMultiView.resizeMultiviewOnOrientationChange(activity);
            //Abhishek bug id 5362 END
        }
    }

    @Override
    public void onUserInteraction() {
        if (AppIdleTimeOut.APPIDLE_TIMEOUT > 0) {
            AppIdleTimeOut.startTimerForAppIdleTimeout();
        }
    }

    public static void pushmessage(final String pushmessage, final boolean appStatus, final String msgParam, final String callbackId, final String notification_code, final String action_code, final String title, final String image_url) {
        final JSONObject json = new JSONObject();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mwebView != null) {

                    try {
                        json.put("message", pushmessage);
                        json.put("params", msgParam);
                        json.put("event", "notification");
                        json.put("notification_code", notification_code);
                        json.put("action_code", action_code);
                        json.put("image_url", image_url);
                        json.put("title", title);
                        json.put("subtitle", "");

                    } catch (JSONException e) {
                        
                    }
                    ApzPluginUtil.sendSuccess(callbackId, json, true, activity, mwebView, true);
                    // mwebView.loadUrl("javascript:appzillon.plugin.notify('" +
                    // pushmessage + "'," + appStatus + ",'" + msgParam +
                    // "');");
                } else {
                }
            }
        }, 2000);
    }
  public static void sendShortcut(final String type, final String action, final String callbackId){
      final JSONObject json = new JSONObject();
      new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
          @Override
          public void run() {
              if (mwebView != null) {

                  try {
//							json.put("message", pushmessage);
                      json.put("shortcutID", action);
                      json.put("event", "shortcutItem");

                  } catch (JSONException e) {

                  }
                  ApzPluginUtil.sendSuccess(callbackId, json, true, activity, mwebView, true);
                  // mwebView.loadUrl("javascript:appzillon.plugin.notify('" +
                  // pushmessage + "'," + appStatus + ",'" + msgParam +
                  // "');");
              } else {
              }
          }
      }, 500);
  }
    @Override
    protected void onNewIntent(Intent i) {
        //Abhishek 13 May 2015, When child app is been launched START
        try {

            if (i.getStringExtra("app_name") != null) {
                Bundle savedInstanceState = new Bundle();

                savedInstanceState.putString("app_name", i.getStringExtra("app_name"));
                savedInstanceState.putString("app_loc", i.getStringExtra("app_loc"));
                savedInstanceState.putString("app_debug", i.getStringExtra("app_remoteDebug"));
                savedInstanceState.putString("message", i.getStringExtra("message"));
                savedInstanceState.putString("type", i.getStringExtra("type"));
                onCreate(savedInstanceState);

            } else if(!TextUtils.isEmpty(i.getStringExtra("message"))){
                String msg = i.getStringExtra("message");
                String msg_params = i.getStringExtra("msgParameters");
                String image_url = i.getStringExtra("image_url");
                boolean appstatus = true;
                String title = i.getStringExtra("title");
                Intent brIntent = new Intent();
                brIntent.setAction("com.iexceed.notification");
                brIntent.putExtra("message", msg);
                brIntent.putExtra("appstatus", appstatus);
                brIntent.putExtra("msgParameters", msg_params);
                brIntent.putExtra("title", title);
                brIntent.putExtra("image_url", image_url);
                //Natasha's Changes 30/06/2017 Security Changes
                // AppzillonMainScreen.activity.sendBroadcast(brIntent);
                LocalBroadcastManager.getInstance(this).sendBroadcast(brIntent);
            }
            else if(!TextUtils.isEmpty(i.getStringExtra("type"))){

                    Intent brIntent = new Intent();
                    brIntent.setAction("com.iexceed.shortcut");
                    brIntent.putExtra("action", i.getStringExtra("action"));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(brIntent);

            }
            //Abhishek 13 May 2015, When child app is been launched END
            super.onNewIntent(i);
        } catch (Exception e) {
            Log.d("Exception" ,"run-time exception onNewIntent");
        }
    }

    /*	 public void setListenerToRootView(){
        final View activityRootView = getWindow().getDecorView().findViewById(android.R.id.content);
	    activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	        @Override
	        public void onGlobalLayout() {

	            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
	            if (heightDiff > 100 ) { // 99% of the time the height diff will be due to a keyboard.
	                mHandler.post(new Runnable() {

	    				@Override
	    				public void run() {
	    					webView.loadUrl("javascript:appzillon.app.showKeyBoard();");

	    				}
	    			});


	                if(isOpened == false){
	                    //Do two things, make the view top visible and the editText smaller
	                }
	                isOpened = true;
	            }else if(isOpened == true){
	                isOpened = false;
	                mHandler.post(new Runnable() {

	    				@Override
	    				public void run() {
	    					webView.loadUrl("javascript:appzillon.app.hideKeyBoard();");

	    				}
	    			});
	            }
	         }
	    });
	}*/


    private void scopeStorageMigration() {
        // sid changes for sandbox migration changes to prepare for Android 11
        try {
            String ext_storage_state = Environment.getExternalStorageState();
            if (ext_storage_state.equalsIgnoreCase(Environment.MEDIA_MOUNTED)
                    &&  "N".equalsIgnoreCase(getResources().getString(R.string.INTERNALSANDBOX))) {
                int targetSdkVersion = getApplicationContext().getApplicationInfo().targetSdkVersion;
                String isScopeStorageMigrated = UserSettings.getScopeMigrationStatus(APP_NAME, settings);
                if(targetSdkVersion <= VERSION_CODES.Q
                        && !isScopeStorageMigrated.equalsIgnoreCase("YES")
                        && !isAppFirstTime.equalsIgnoreCase("YES")){
                    // startTime = System.nanoTime();
//                    File fromLoc = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + MAIN_APP_NAME);
                    File fromLoc = AppzillonUtils.getApzFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + MAIN_APP_NAME,null);
//                    File toLoc = new File(getApplicationContext().getExternalFilesDir(null) + "/" + MAIN_APP_NAME);
                    File toLoc = AppzillonUtils.getApzFile(getApplicationContext().getExternalFilesDir(null) + "/" + MAIN_APP_NAME,null);
                    new scopeStorageAsychTask(fromLoc, toLoc).execute();
                }else{
                    SANDBOX_LOC = getApplicationContext().getExternalFilesDir(null) + "/" + MAIN_APP_NAME;
                    initSandBoxRelatedFn();
                }
            } else {
                SANDBOX_LOC = getFilesDir().getAbsolutePath();
                internalPathCheck = SANDBOX_LOC.substring(0,6);
                initSandBoxRelatedFn();
            }

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void initSandBoxRelatedFn() {

        createFolder(SANDBOX_LOC);
        String[] defaultFolders = {"photo", "video", "audio", "docs"};
        for (String folderName : defaultFolders) {
            createFolder(SANDBOX_LOC + File.separator + ASSET_APP_LOC + folderName);
        }
       /* if (isNetworkAvailable()) {
            postCopyAssetFolder();
        }*/
       // refresh entire asset folder after app update
        boolean refreshAssetFolder = false;
        try {
            String appVersionCodeStr = UserSettings.getAppVersionCode(APP_NAME,settings);
            if (!appVersionCodeStr.equalsIgnoreCase("0") || !isAppFirstTime.equalsIgnoreCase("YES")) {
                int currentAppVersionCode = Integer.parseInt(appVersionCodeStr);
                if(BuildConfig.VERSION_CODE > currentAppVersionCode){
                    refreshAssetFolder = true;
                }
            }
        } catch(Exception nfe) {

        }

        if ((OTAREQUIRED).equalsIgnoreCase("Y") && (isAppFirstTime.equalsIgnoreCase("YES") || refreshAssetFolder)) {

            new copyOTAFilesAsychTask(refreshAssetFolder).execute();


        }else{
            proceedToFetchAppSecToken();
        }





       /* new GetAppSecToken(new PostAppSecToken() {
            @Override
            public void executeAfterAppSecToken() {

            }
        }).execute();*/
    }

    private void proceedToFetchAppSecToken() {

        launchAppzillonFirstPage();
        stringUtils = new StringUtils(this, app_loc);

        //Changes for 3.5.3 Natasha Dawra
      /*  if (isNetworkAvailable()) {
            postCopyAssetFolder();
        }*/

        /*if (SANDBOX_LOC.length() <= 1) {

            SANDBOX_LOC = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + StringUtils.getString(StringUtils.APP_ID);
        }*/
        String strDebugLevel = StringUtils.getString("logLevel");
        switch (strDebugLevel) {
            case "F":
                ApzPlugin.debugLevel = 0;
                break;
            case "E":
                ApzPlugin.debugLevel = 1;
                break;
            case "W":
                ApzPlugin.debugLevel = 2;
                break;
            case "I":
                ApzPlugin.debugLevel = 3;
                break;
            case "D":
                ApzPlugin.debugLevel = 4;
                break;

            default:
                break;
        }

        //new GetAppSecToken(AppzillonMainScreen.this).execute();

	boolean isMockServerEnabled = false;

	//Get MockServer status from App props
	if(StringUtils.getString(StringUtils.ENABLE_MOCK_SERVER).trim().equalsIgnoreCase("true")){
	    isMockServerEnabled = true;
	}

	//Send first request if server is enabled && Mock server is not enabled
        loadWebView();
	if(IS_SERVER && !isMockServerEnabled){
        serverErrorDialog = new AlertDialog.Builder(AppzillonMainScreen.this);
        AppzillonMainScreen.serverErrorDialog.setTitle(getServerErrorTitle());
        AppzillonMainScreen.serverErrorDialog
                .setMessage(getServerErrorMessage())
                .setCancelable(false)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //finish();
//							android.os.Process.killProcess(android.os.Process.myPid());
//							System.exit(0);
                    }
                });
        serverErrorAlertDialog = serverErrorDialog.create();

 //get location for device registration
       String TRACKLOCATION = StringUtils.getString(StringUtils.TRACK_LOCATION);
	if(TRACKLOCATION.equalsIgnoreCase("Y")){
			HMSLocationManager lManager = new HMSLocationManager(activity);
			lManager.getLocation(new HMSLocationCallback() {
				@Override
				public void onLocationSuccess(JSONObject successJson) {
					LATITUDE = successJson.optString("latitude");
					LONGITUDE = successJson.optString("longitude");
					
				}

				@Override
				public void onLocationFailure(String error) {
					
				}
			});
		}
     /*   if(TRACKLOCATION.equalsIgnoreCase("Y")){
            Location latlng = CurrentLocation.getLocation(AppzillonMainScreen.activity);
            double latd = 0.0;
            double lngd = 0.0;
            if(latlng!= null){
                latd = latlng.getLatitude();
                lngd = latlng.getLongitude();
                LATITUDE = Double.toString(latd);
                LONGITUDE = Double.toString(lngd);
            }
        }*/
	    if(isNetworkAvailable()) {
            new LaunchMergedInterface().execute();
            // new GetAppSecToken(AppzillonMainScreen.this,false).execute();
        }else {
            if(!isFinishing()) {
                if (getResources().getString(R.string.OFFLINESUPPORT).equalsIgnoreCase("N")) {
                final AlertDialog.Builder networkErrorDialog = new AlertDialog.Builder(AppzillonMainScreen.this);
                networkErrorDialog.setTitle("Network Error");
                networkErrorDialog
                        .setMessage("No internet connection found.")
                        .setCancelable(false)
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (isNetworkAvailable()) {
                                    new LaunchMergedInterface().execute();
                                    //    new GetAppSecToken(AppzillonMainScreen.this,false).execute();
                        } else {
                                    AlertDialog alertDialog = networkErrorDialog.create();
                                    alertDialog.show();
                        }
                              //  AppzillonMainScreen.this.finish();
                            }
                        });
              /*  AlertDialog alertDialog = networkErrorDialog.create();
                alertDialog.show();*/
                }/*else{

                    loadWebView();
                }*/
            }
        }
	}/*else{
	    //Load UI without sending first request
	    loadWebView();
	}*/



       /* new GetAppSecToken(new PostAppSecToken() {
            @Override
            public void executeAfterAppSecToken() {

            }
        }).execute();*/
    }

       /* if (!isAppFirstTime.equalsIgnoreCase("YES")) {

            String multifactorReg = UserSettings.getIsMultiFactorRegistered(StringUtils.getString(StringUtils.APP_ID), apps);
            String savedOsVersion = UserSettings.getOSVersion(apps);
            String presentOsVersion = AppzillonUtils.getOsDetails(this);
            String notificationRegistration = UserSettings.getIsNotificationRegistered(StringUtils.getString(StringUtils.APP_ID), apps);

            //Check weather device is already registered or not Check weather OS version from last registration is same or updated

            if (multifactorReg.equalsIgnoreCase("false") || (!savedOsVersion.equalsIgnoreCase(presentOsVersion))) {
                multiFactorRegister();
            } else {
                //ServerLog.i(TAG, "Device Already registered for multifactor");
            }
            *//*Natasha Dawra 5/6/2017
            Notification Registration in case the registration
            fails
             *//*
            if (notificationRegistration.equalsIgnoreCase("false")) {
                String token = UserSettings.getNotificationToken(StringUtils.getString(StringUtils.APP_ID), apps);
                if (!("".equals(token)) && NotificationPlugin.isPlugin()) {
                    new NotificationRegistration().execute();
                }
            }
        }

        //Abhishek 16 July 2015 for preventing app to take screen shot if is_screenshot_enabled flag is Y START
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
//			if(!LandingPageActivity.IS_APP_DEBUGABLE)
            if (StringUtils.getString(StringUtils.IS_SCREENSHOT_ENABLED).equalsIgnoreCase("Y"))
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
//Abhishek 16 July 2015 for preventing app to take screen shot if is_screenshot_enabled flag is Y END


        if ("".equalsIgnoreCase(StringUtils.getString(StringUtils.EXPIRY_DATE))) {
        } else {
//			checkAppExpiry();
            if (AppzillonUtils.isAppExpired(this, APP_NAME)) {
                showExpiryMsg(getApplicationContext(), this, APP_NAME);
            }
        }


		*//* To change language setting *//*
//		languageCode = UserSettings.getValue("LANGUAGE", getResources().getString(R.string.default_lang), settings);
        languageCode = UserSettings.getAppValue(APP_NAME, "DEFAULTLANGUAGE", StringUtils.getString(StringUtils.DEFAULT_LANG), settings);
        final Locale appLocale = new Locale(languageCode);
        Locale.setDefault(appLocale);
        final Configuration config2 = new Configuration();
        config2.locale = appLocale;
        getApplicationContext().getResources().updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());

        if (isAppFirstTime.equalsIgnoreCase("YES")) {
            multiFactorRegister();

            //Natasha Dawra 5/6/2017 getting token from shared preferences
            String token = UserSettings.getNotificationToken(StringUtils.getString(StringUtils.APP_ID), apps);
            if (!("".equals(token)) && NotificationPlugin.isPlugin()) {
                new NotificationRegistration().execute();
            }
        }


        String isFirstTime = UserSettings.getIsAppFirstTime(APP_NAME, settings);
        String lang = StringUtils.getString(StringUtils.DEFAULT_LANG);
        String serverurl = StringUtils.getString(StringUtils.SERVER_URL);
//		String isFirstTime = UserSettings.getIsFirstTime(settings);
//		String otpFlag = getResources().getString(R.string.generate_otp);
        String otpFlag = StringUtils.getString(StringUtils.GENERATE_OTP);
        if (isFirstTime.equals("YES")) {
            UserSettings.setAppValue(APP_NAME, "DEFAULTLANGUAGE", lang, settings);
            UserSettings.setAppValue(APP_NAME, "OTPFLAG", otpFlag, settings);
            UserSettings.setAppValue(APP_NAME, "SERVERURL", serverurl, settings);
            UserSettings.setIsAppFirstTime(APP_NAME, "NO", settings);
            dbHelp = new DatabaseHandler(getApplicationContext());
            String list[] = null;
            String dbName = null;
            try {
                //For OTA refresh
                //Abhishek ,13 August 2015, bug id 6147, Reverted back changes of 10 April 2015 START
                if (OTAREQUIRED.equalsIgnoreCase("Y")) {
                    String loc = SANDBOX_LOC + File.separator + ASSET_APP_LOC + "sqlite";
                    File[] fileList = new File(loc).listFiles();
                    if (fileList != null) {
                        list = new String[fileList.length];
                        for (int a = 0; a < fileList.length; a++) {
                            //list = new String [fileList.length];
                            if (fileList[a].isFile()) {
                                list[a] = fileList[a].getName();
                            }
                        }
                    }

                } else
                //Abhishek ,13 August 2015, bug id 6147, Reverted back changes of 10 April 2015 END
                {
                    list = getApplicationContext().getAssets().list(ASSET_APP_LOC + "sqlite");
                }

            } catch (IOException e) {
                
            }
            for (String file : list) {
                dbName = file;
                CopyDatabase dbCopy = new CopyDatabase(getApplicationContext(), dbName);
                try {
                    dbCopy.copyDataBase(dbName);
                    dbCopy.close();
                } catch (IOException e) {
                    
                }
            }
            dbHelp.createDataBase();
            InputStream is = null;
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                //For OTA refresh
                if (OTAREQUIRED.equalsIgnoreCase("Y")) {
                    File filesJson = new File(SANDBOX_LOC + "/" + ASSET_APP_LOC + "screens/config/Files.json");
                    is = new FileInputStream(filesJson);
                } else {
                    is = getAssets().open(ASSET_APP_LOC + "screens/config/Files.json");
                }

                if (is != null) {
                    br = new BufferedReader(new InputStreamReader(is));
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                }
            } catch (IOException e) {
                
            }
            JSONObject StaticJson = null;
            if (is != null) {
                try {
                    StaticJson = new JSONObject(sb.toString());
                } catch (JSONException ex) {
                    
                }
                CopyStaticFiles staticfiles = new CopyStaticFiles(getApplicationContext());
                staticfiles.copystatic(StaticJson);
            }

            //Abhishek : Check for folders and if not present then create in sandbox
//            createFolder(SANDBOX_LOC);
//            String[] defaultFolders = {"photo", "video", "audio", "docs"};
//            for (String folderName : defaultFolders) {
//                createFolder(SANDBOX_LOC + File.separator + ASSET_APP_LOC + File.separator + folderName);
//            }


        }

        //Read File notif_details.json
        try {
            InputStream isNotif = null;
            StringBuilder sbNotif = new StringBuilder();
            BufferedReader notifbr;
            String notifline;
            JSONArray notificationJson = null;


            if (OTAREQUIRED.equalsIgnoreCase("Y")) {

                File settingsJson = new File(SANDBOX_LOC + File.separator + ASSET_APP_LOC + "screens/config/notif_details.json");
                isNotif = new FileInputStream(settingsJson);
            } else {
                isNotif = getAssets().open(ASSET_APP_LOC + "screens/config/notif_details.json");
            }

            if (isNotif != null) {
                notifbr = new BufferedReader(new InputStreamReader(isNotif));
                while ((notifline = notifbr.readLine()) != null) {
                    sbNotif.append(notifline);
                }
                notifbr.close();
            }

            if (isNotif != null) {
                // notifMap = new HashMap<String, JSONObject>();
                try {
                    notificationJson = new JSONArray(sbNotif.toString());

                    mapjson = new JSONArray();
                    for (int j = 0; j < notificationJson.length(); j++) {
                        JSONObject json = notificationJson.getJSONObject(j);
                        JSONArray arr = json.getJSONArray("actions");
                        String notification = json.getString("notification_code");
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("notification_code", notification);
                        jsonObj.put("actions", arr);
                        mapjson.put(j, jsonObj);

                    }


                } catch (JSONException ex) {
                   
                }
            }
        } catch (IOException io) {

        }
        initializeEventsDefault();
        launchAppzillonFirstPage();
    }*/

   /* private void requestDefaultPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AppzillonMainScreen.activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PermissionDeniedCallback();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, APZ_REQ_WRITE_STORAGE);
        }
    }*/

    private void requestDefaultPermissions() {
       /* if (ActivityCompat.shouldShowRequestPermissionRationale(AppzillonMainScreen.activity, Manifest.permission.READ_PHONE_STATE)) {
            PermissionDeniedCallback();
        } else */

                if (IS_TRACK_LOCATION && "N".equalsIgnoreCase(getResources().getString(R.string.INTERNALSANDBOX))) {
                    ActivityCompat.requestPermissions(this, new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                            ApzPlugin.APZ_REQ_DEFAULT_PERMISSIONS);
                }else if(IS_TRACK_LOCATION){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            ApzPlugin.APZ_REQ_DEFAULT_PERMISSIONS);
                }else if("N".equalsIgnoreCase(getResources().getString(R.string.INTERNALSANDBOX))){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE},
                        ApzPlugin.APZ_REQ_DEFAULT_PERMISSIONS);
            }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean mDenied = false;
        boolean never_ask_again = false;
        if (requestCode == APZ_REQ_DEFAULT_PERMISSIONS) {
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    //Log.e("denied", permission);
                    mDenied = true;
                } else {
                    if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                        //Log.e("allowed", permission);
                    } else {
                        never_ask_again = true;
                    }
                }
            }
            if (never_ask_again) {
                String message = "";
                if(IS_SERVER)
                    message = "Change permissions in your device's app settings. Grant access to Location,Storage.";
                else
                    message = "Change permissions in your device's app settings. Grant access to Storage to try again.";
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AppzillonMainScreen.this);
                alertDialogBuilder.setTitle("Grant Access");
                alertDialogBuilder
                        .setMessage("Change permissions in your device's app settings for application to run smoothly")
                        .setCancelable(false)
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                                AppzillonMainScreen.this.finish();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                if (this.splashDialog.isShowing()) {
                    this.splashDialog.dismiss();
                }
            } else if (mDenied) {
                checkPermissionFlag = false;
                PermissionDeniedCallback();
            } else {
                checkPermissionFlag = true;
                //initSandBoxRelatedFn();
                scopeStorageMigration();
            }
        } else if (this.mapPermissionResultHandler != null) {
            OnPermissionsResultHandler resultHandler1 = this.mapPermissionResultHandler.get(requestCode);
            if (resultHandler1 != null) {
                this.removeOnPermissionsResultHandler(requestCode);
                resultHandler1.handlePermissionResult(requestCode, permissions, grantResults);
            }
        }

    }

//    private void PhonePermissionDeniedCallback(){
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AppzillonMainScreen.this);
//
//        // set title
//        alertDialogBuilder.setTitle("Permission Required");
//
//        alertDialogBuilder
//                .setMessage("Grant access to Telephone for Application to run normally")
//                .setCancelable(false)
//                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        ActivityCompat.requestPermissions(AppzillonMainScreen.this, new String[]{Manifest.permission.READ_PHONE_STATE}, ApzPlugin.APZ_REQ_DEFAULT_PERMISSIONS);
//
//                    }
//                })
//                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                        AppzillonMainScreen.this.finish();
//                    }
//                });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//
//        alertDialog.show();
//    }

    private void PermissionDeniedCallback(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AppzillonMainScreen.this);

        String message = getPermissionDeniedMessage();            
       //     final_permissions = permissions;
        /*else{
            message = "Grant access to Storage for Application to run normally.";
            permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            callbackValue = APZ_REQ_WRITE_STORAGE;
        }*/
        // set title
        alertDialogBuilder.setTitle("Permission Required");

        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //ActivityCompat.requestPermissions(AppzillonMainScreen.this, final_permissions,callbackValue);
                        requestDefaultPermissions();

                    }
                })
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        AppzillonMainScreen.this.finish();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    class NotificationRegistration extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            ServerUtilities.register(activity, UserSettings.getNotificationToken(StringUtils.getString(StringUtils.APP_ID), apps));
            return null;
        }
    }

    class GetSSLStatus extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;
            String appName = StringUtils.getString(StringUtils.APP_ID);
            String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
            JSONObject response = null;
            response = getAppInstructions(appName, appName, serverUrl);
            try {
                if (response != null) {
                    result = true;
                }
            } catch (Exception ex) {
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            onSSLStatus(result);
        }
    }

    public void onSSLStatus(boolean status) {
        isSecured = status;
        if (globalSSLHandler != null) {
            if (status) {
                globalSSLHandler.proceed();
            } else {
                globalSSLHandler.cancel();
            }
            globalSSLHandler = null;
        }

    }
    /*class GetAppSecToken extends AsyncTask<Void, Void, Void>{

        PostAppSecToken postObj;

        GetAppSecToken(PostAppSecToken postAppSecToken ){
            super();
            postObj = postAppSecToken;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject jsonObject = new JSONObject();
            try {

                JSONObject header = new JSONObject();

                header.put(AppzillonMainScreen.APP_ID, StringUtils.getString(StringUtils.APP_ID));
                header.put(AppzillonMainScreen.SESSION_ID, "");
                header.put(AppzillonMainScreen.DEVICE_ID, AppzillonUtils.getDeviceId(activity));
                header.put("requestId","CSNONCE");
                header.put("async", false);
                header.put(AppzillonMainScreen.USER_ID, "");
                header.put("screenId","Login");
                header.put(AppzillonMainScreen.REQ_STATUS, true);
                header.put("source" , "APPZILLON");
                header.put("clientNonce",System.currentTimeMillis()+"");
                header.put(AppzillonMainScreen.INTERFACE_ID, "appzillonGetAppSecTokens");
                header.put(AppzillonMainScreen.OS, AppzillonMainScreen.ANDROID_OS);
                header.put("origination", AppzillonUtils.ipAddress(activity));



                JSONObject reqBody = new JSONObject();
                JSONObject appFileReq = new JSONObject();
                appFileReq.put(AppzillonMainScreen.APP_ID, StringUtils.getString(StringUtils.APP_ID));
                appFileReq.put(AppzillonMainScreen.DEVICE_ID, AppzillonUtils.getDeviceId(activity));
                appFileReq.put("requestId","CSNONCE");

                reqBody.put("appzillonGetAppSecTokensRequest", appFileReq);

                jsonObject.put(AppzillonMainScreen.APPZILLON_HEADER, header);
                jsonObject.put(AppzillonMainScreen.APPZILLON_BODY, reqBody);
            } catch (JSONException jsonException) {
                
            }
//
            String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
//
            JSONObject response = ServerUtilities.sendRequestToServer(serverUrl,jsonObject.toString());
            if(response != null){
//
                try {
                    JSONObject header = response.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
                    boolean status  = header.getBoolean("status");

                    if(status){
                        JSONObject apzBody = response.getJSONObject(AppzillonMainScreen.APPZILLON_BODY);
                        JSONObject apzResponse = apzBody.getJSONObject("appzillonGetAppSecTokensResponse");
                        SNONCE = apzResponse.getString("serverNonce");
                        SESSIONTOKEN = apzResponse.getString("sessionToken");
                    }else{
                        JSONArray errArray = response.getJSONArray(AppzillonMainScreen.APPZILLON_ERRORS);
                        JSONObject error = errArray.getJSONObject(0);
                        String errorMessage = error.getString("errorMessage");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    
                }

            }else{
                Log.e(TAG, "GetAppSecureToken is null ");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!TextUtils.isEmpty(SNONCE)){
                postObj.executeAfterAppSecToken();
                //loadWebView();
            }
        }

    }*/





    @Override
    public void executeAfterAppSecToken(){
        loadWebView();

    }

    public void loadWebView(){
        IS_APP_INITIALIZED = true;

        secondLaunch();
        //Abhishek 16 July 2015 for preventing app to take screen shot if is_screenshot_enabled flag is Y START
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
//			if(!LandingPageActivity.IS_APP_DEBUGABLE)
            if (StringUtils.getString(StringUtils.IS_SCREENSHOT_ENABLED).equalsIgnoreCase("Y"))
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
//Abhishek 16 July 2015 for preventing app to take screen shot if is_screenshot_enabled flag is Y END


        if ("".equalsIgnoreCase(StringUtils.getString(StringUtils.EXPIRY_DATE))) {
        } else {
//			checkAppExpiry();
            if (AppzillonUtils.isAppExpired(this, APP_NAME)) {
                showExpiryMsg(getApplicationContext(), this, APP_NAME);
            }
        }


		/* To change language setting */
//		languageCode = UserSettings.getValue("LANGUAGE", getResources().getString(R.string.default_lang), settings);
        languageCode = UserSettings.getAppValue(APP_NAME, "DEFAULTLANGUAGE", StringUtils.getString(StringUtils.DEFAULT_LANG), settings);
        final Locale appLocale = new Locale(languageCode);
        Locale.setDefault(appLocale);
        final Configuration config2 = new Configuration();
        config2.locale = appLocale;
        getApplicationContext().getResources().updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());
/*
        if (isAppFirstTime.equalsIgnoreCase("YES")) {
            multiFactorRegister();

            //Natasha Dawra 5/6/2017 getting token from shared preferences
            String token = UserSettings.getNotificationToken(StringUtils.getString(StringUtils.APP_ID), apps);
            if (!("".equals(token)) && NotificationPlugin.isPlugin() && IS_SERVER) {
                new NotificationRegistration().execute();
            }else{
                getToken();
            }
        }*/


        String isFirstTime = UserSettings.getIsAppFirstTime(APP_NAME, settings);
        String lang = StringUtils.getString(StringUtils.DEFAULT_LANG);
        String serverurl = StringUtils.getString(StringUtils.SERVER_URL);
//		String isFirstTime = UserSettings.getIsFirstTime(settings);
//		String otpFlag = getResources().getString(R.string.generate_otp);
        String otpFlag = StringUtils.getString(StringUtils.GENERATE_OTP);
        if (isFirstTime.equals("YES")) {
            UserSettings.setAppValue(APP_NAME, "DEFAULTLANGUAGE", lang, settings);
            UserSettings.setAppValue(APP_NAME, "OTPFLAG", otpFlag, settings);
            UserSettings.setAppValue(APP_NAME, "SERVERURL", serverurl, settings);
            UserSettings.setIsAppFirstTime(APP_NAME, "NO", settings);
            dbHelp = new DatabaseHandler(getApplicationContext());
            String list[] = null;
            String dbName = null;
            try {
                //For OTA refresh
                //Abhishek ,13 August 2015, bug id 6147, Reverted back changes of 10 April 2015 START
                if (OTAREQUIRED.equalsIgnoreCase("Y")) {
                    String loc = SANDBOX_LOC + File.separator + ASSET_APP_LOC + "sqlite";
//                    File[] fileList = new File(loc).listFiles();
                    File[] fileList = AppzillonUtils.getApzFile(loc,null).listFiles();
                    if (fileList != null) {
                        list = new String[fileList.length];
                        for (int a = 0; a < fileList.length; a++) {
                            //list = new String [fileList.length];
                            if (fileList[a].isFile()) {
                                list[a] = fileList[a].getName();
                            }
                        }
                    }

                } else
                //Abhishek ,13 August 2015, bug id 6147, Reverted back changes of 10 April 2015 END
                {
//                    list = getApplicationContext().getAssets().list(ASSET_APP_LOC + "sqlite");
                    list = getApplicationContext().getAssets().list(AppzillonUtils.validatePath(ASSET_APP_LOC + "sqlite",null));
                }

            } catch (IOException e) {
                
            }
            if (list != null) {
                for (String file : list) {
                    dbName = file;
                    CopyDatabase dbCopy = new CopyDatabase(getApplicationContext(), dbName);
                    try {
                        dbCopy.copyDataBase(dbName);
                        dbCopy.close();
                    } catch (IOException e) {

                    }
                }
            }
            dbHelp.createDataBase();
            // App update changes
            copyStaticFiles();

            //Abhishek : Check for folders and if not present then create in sandbox
//            createFolder(SANDBOX_LOC);
//            String[] defaultFolders = {"photo", "video", "audio", "docs"};
//            for (String folderName : defaultFolders) {
//                createFolder(SANDBOX_LOC + File.separator + ASSET_APP_LOC + File.separator + folderName);
//            }


        }

        //Read File notif_details.json
        try {
            InputStream isNotif = null;
            StringBuilder sbNotif = new StringBuilder();
            BufferedReader notifbr;
            String notifline;
            JSONArray notificationJson = null;


            if (OTAREQUIRED.equalsIgnoreCase("Y")) {

//                File settingsJson = new File(SANDBOX_LOC + File.separator + ASSET_APP_LOC + "screens/config/notif_details.json");
                File settingsJson = AppzillonUtils.getApzFile(SANDBOX_LOC + File.separator + ASSET_APP_LOC + "screens/config/notif_details","json");
                isNotif = new FileInputStream(settingsJson);
            } else {
//                isNotif = getAssets().open(ASSET_APP_LOC + "screens/config/notif_details.json");
                isNotif = getAssets().open(AppzillonUtils.validatePath(ASSET_APP_LOC + "screens/config/notif_details","json"));
            }

            if (isNotif != null) {
                notifbr = new BufferedReader(new InputStreamReader(isNotif));
                while ((notifline = notifbr.readLine()) != null) {
                    sbNotif.append(notifline);
                }
                notifbr.close();
            }

            if (isNotif != null) {
                // notifMap = new HashMap<String, JSONObject>();
                try {
                    notificationJson = new JSONArray(sbNotif.toString());

                    mapjson = new JSONArray();
                    for (int j = 0; j < notificationJson.length(); j++) {
                        JSONObject json = notificationJson.getJSONObject(j);
                        JSONArray arr = json.getJSONArray("actions");
                        String notification = json.getString("notification_code");
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("notification_code", notification);
                        jsonObj.put("actions", arr);
                        mapjson.put(j, jsonObj);

                    }


                } catch (JSONException ex) {
                   
                }
            }
        } catch (IOException io) {

        }
        initializeEventsDefault();

    }
    private void secondLaunch(){
        if (!isAppFirstTime.equalsIgnoreCase("YES")) {

            String multifactorReg = UserSettings.getIsMultiFactorRegistered(StringUtils.getString(StringUtils.APP_ID), apps);
            String savedOsVersion = UserSettings.getOSVersion(apps);
            String presentOsVersion = AppzillonUtils.getOsDetails(this);
            String notificationRegistration = UserSettings.getIsNotificationRegistered(StringUtils.getString(StringUtils.APP_ID), apps);

            //Check weather device is already registered or not Check weather OS version from last registration is same or updated

            if (multifactorReg.equalsIgnoreCase("false") || (!savedOsVersion.equalsIgnoreCase(presentOsVersion))) {
                multiFactorRegister();
            } else {
                //ServerLog.i(TAG, "Device Already registered for multifactor");
            }
            /*Natasha Dawra 5/6/2017
            Notification Registration in case the registration
            fails
             */
/*
            //Changes for 3.5.3 Natasha
            if (isNetworkAvailable()) {
                postCopyAssetFolder();
            }*/

            if (notificationRegistration.equalsIgnoreCase("false")) {
                String token = UserSettings.getNotificationToken(StringUtils.getString(StringUtils.APP_ID), apps);
                if (!("".equals(token)) && NotificationPlugin.isPlugin() && IS_SERVER) {
                    new NotificationRegistration().execute();
                }else{
                getToken();
            }
            }
        }
    }
    private void copyStaticFiles() {
        InputStream is = null;
        BufferedReader br;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            //For OTA refresh
            if (OTAREQUIRED.equalsIgnoreCase("Y")) {
//                File filesJson = new File(SANDBOX_LOC + "/" + ASSET_APP_LOC + "screens/config/Files.json");
                File filesJson = AppzillonUtils.getApzFile(SANDBOX_LOC + "/" + ASSET_APP_LOC + "screens/config/Files","json");
                is = new FileInputStream(filesJson);
            } else {
//                is = getAssets().open(ASSET_APP_LOC + "screens/config/Files.json");
                is = getAssets().open(AppzillonUtils.validatePath(ASSET_APP_LOC + "screens/config/Files","json"));
            }

            if (is != null) {
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
            }
        } catch (IOException e) {

        }
        JSONObject StaticJson = null;
        if (is != null) {
            try {
                StaticJson = new JSONObject(sb.toString());
            } catch (JSONException ex) {

            }
            CopyStaticFiles staticfiles = new CopyStaticFiles(getApplicationContext());
            staticfiles.copystatic(StaticJson);
        }
    }


    private class copyOTAFilesAsychTask extends AsyncTask<Void, Void, Void>{
        boolean mRefreshStaticFiles;
        public copyOTAFilesAsychTask(boolean refreshAssetFolder) {
            this.mRefreshStaticFiles = refreshAssetFolder;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            AssetManager assetManager = getAssets();
            copyAssets(assetManager, ASSETS_MAIN_FOLDER, 0);
            copyAssets(assetManager, ASSETS_APZ_FOLDER, 0);
            try {
                String folders[] = assetManager.list("");
                for (int i = 0; i < folders.length; i++) {
                    if (folders[i].contains(".html")) {
                        copyAssets(assetManager, folders[i], 0);
                    }
                }
                UserSettings.setAppVersionCode(APP_NAME, String.valueOf(BuildConfig.VERSION_CODE), settings);
                if (this.mRefreshStaticFiles) {
                    // update static files in case of version upgrade
                    copyStaticFiles();
                }
            } catch (IOException e) {
                ApzLogger.e(TAG, "Exception in copying asset folder " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            proceedToFetchAppSecToken();
        }
    }

    private class scopeStorageAsychTask extends AsyncTask<Void, Void, Void>{
        File fromLocation;
        File toLocation;
        public scopeStorageAsychTask(File fromLoc, File toLoc) {
            this.fromLocation = fromLoc;
            this.toLocation = toLoc;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                copyDirectoryOneLocationToAnotherLocation(this.fromLocation, this.toLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            UserSettings.setScopeMigrationStatus(APP_NAME, "YES", settings);
            SANDBOX_LOC = getApplicationContext().getExternalFilesDir(null) + "/" + MAIN_APP_NAME;
            deleteDir(this.fromLocation);
            initSandBoxRelatedFn();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    // return false;
                }
            }
        }
        // The directory is now empty so delete it
        boolean deleted = dir.delete();
        return deleted;
    }

    public static void copyDirectoryOneLocationToAnotherLocation(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                copyDirectoryOneLocationToAnotherLocation(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);

            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }
    /*APP LAUNCH*/
    class LaunchMergedInterface extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            allIntfBuilder =new AlertDialog.Builder(activity);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean respStatus =false;
            try{
                JSONObject onLaunchRequest =new JSONObject();
                JSONObject appzillonheader = new JSONObject();
                String app_id =AppzillonMainScreen.stringUtils.getString(StringUtils.APP_ID);


                appzillonheader.put("serverNonce", AppzillonMainScreen.SNONCE);
                appzillonheader.put("requestKey", "");
                appzillonheader.put("clientNonce", System.currentTimeMillis() + "");
                appzillonheader.put("sessionToken", AppzillonMainScreen.SESSIONTOKEN);
                appzillonheader.put(AppzillonMainScreen.APP_ID, app_id);
                appzillonheader.put(AppzillonMainScreen.DEVICE_ID, AppzillonUtils.getDeviceId(AppzillonMainScreen.activity));
                appzillonheader.put("source", "APPZILLON");
                appzillonheader.put(AppzillonMainScreen.USER_ID, AppzillonMainScreen.USER_ID_FOR_OTA);
                appzillonheader.put("screenId", "Login");
                appzillonheader.put(AppzillonMainScreen.SESSION_ID, "");
                appzillonheader.put("origination", AppzillonUtils.ipAddress(AppzillonMainScreen.activity));
                appzillonheader.put(AppzillonMainScreen.INTERFACE_ID, "appzillonOnAppLaunch");
                appzillonheader.put(AppzillonMainScreen.REQ_STATUS, true);
                appzillonheader.put("longitude", LONGITUDE);
                appzillonheader.put("latitude", LATITUDE);
                appzillonheader.put(AppzillonMainScreen.REQUEST_KEY, "000NEW");
                appzillonheader.put(AppzillonMainScreen.SCREEN_ID, "launchapp");

                //check for notification token

                String token = UserSettings.getNotificationToken(StringUtils.getString(StringUtils.APP_ID), apps);

                if(isAppFirstTime.equalsIgnoreCase("YES")) {
                    if(token.equalsIgnoreCase("")) {
                        appzillonheader.put("appLaunch", "FIRST");
                    }else{
                        appzillonheader.put("appLaunch","NOTIFY");
                    }
                }else{
                    appzillonheader.put("appLaunch", "SECOND");
                }

                appzillonheader.put(APZ_SIGNATURE, AppzillonUtils.getCurrentSong(AppzillonMainScreen.activity.getApplicationContext()));
                appzillonheader.put(PRE_LOGIN,"true" );
                JSONObject appzillonBody = new JSONObject();
                JSONObject appReqDetails1 = new JSONObject();
                JSONObject appReqDetails2 = new JSONObject();
                JSONObject appReqDetails3 = new JSONObject();
                JSONObject appReqDetails4 = new JSONObject();
                String bluetoothName = ServerUtilities.getBluetoothName();

                appReqDetails1.put(APP_ID, app_id);
                String phoneDeviceId = AppzillonUtils.getDeviceId(activity.getApplicationContext());
                String currAppStoreVersion = AppzillonUtils.getCurrentAppStoreVersion(activity);
                String updateAppVersion ="N";
                try {
                    String appVersionCodeStr = UserSettings.getAppVersionCode(APP_NAME,settings);
                    if (!appVersionCodeStr.equalsIgnoreCase("0")) {
                        int currentAppVersionCode = Integer.parseInt(appVersionCodeStr);
                        if(BuildConfig.VERSION_CODE > currentAppVersionCode){
                            updateAppVersion ="Y";
                        }else{
                            updateAppVersion ="N";
                        }
                    }
                } catch(Exception nfe) {

                }
                appReqDetails1.put(DEVICE_ID, phoneDeviceId);
                appReqDetails1.put(APP_STORE_VERSION, currAppStoreVersion);
                appReqDetails1.put(UPDATE_APP_STORE_VERSION, updateAppVersion);
                appReqDetails1.put(OS, ANDROID_OS);
                appzillonBody.put("appzillonAppMasterRequest", appReqDetails1);

                if(isAppFirstTime.equalsIgnoreCase("YES")) {
                    appReqDetails2.put(AppzillonMainScreen.APP_ID, app_id);
                    appReqDetails2.put(AppzillonMainScreen.OS, AppzillonMainScreen.ANDROID_OS);
                    appReqDetails2.put(AppzillonMainScreen.OS_VERSION, AppzillonUtils.getOsDetails(activity));
                    appReqDetails2.put(AppzillonMainScreen.DEVICE_ID, AppzillonUtils.getDeviceId(activity.getApplicationContext()));
                    appReqDetails2.put(AppzillonMainScreen.MOBILE_ONE, "UNKNOWN");
                    appReqDetails2.put(AppzillonMainScreen.MOBILE_TWO, "UNKNOWN");
                    appReqDetails2.put(AppzillonMainScreen.DEVICE_MODEL, AppzillonUtils.getDeviceType());
                    appReqDetails2.put(AppzillonMainScreen.DEVICE_MAKE, AppzillonUtils.getDeviceMake());
                    appReqDetails2.put(AppzillonMainScreen.SCREEN_RESOLUTION, AppzillonUtils.getScreenSize(activity));
                    appReqDetails2.put(AppzillonMainScreen.APP_STORE_VERSION, AppzillonUtils.getCurrentAppStoreVersion(activity));
                    appReqDetails2.put(AppzillonMainScreen.DEVICE_NAME, bluetoothName);
                    appReqDetails2.put("longitude", LONGITUDE);
                    appReqDetails2.put("latitude", LATITUDE);
                    appzillonBody.put("deviceRegisterRequest", appReqDetails2);
                    if(!token.equalsIgnoreCase("")) {
                        appReqDetails3.put(AppzillonMainScreen.DEVICE_ID, AppzillonUtils.getDeviceId(activity.getApplicationContext()));
                        appReqDetails3.put(AppzillonMainScreen.DEVICE_NAME, bluetoothName);
                        appReqDetails3.put(AppzillonMainScreen.OS_ID, AppzillonMainScreen.ANDROID_OS);
                        appReqDetails3.put("regId", token);
                        appReqDetails3.put(AppzillonMainScreen.OS_VERSION, AppzillonUtils.getOsDetails(activity));
                        appReqDetails3.put(AppzillonMainScreen.APP_ID, app_id);
                        appReqDetails3.put("notifMsgServer", "HUAWEI");
/*
                        appReqDetails3.put("origination", AppzillonUtils.ipAddress(AppzillonMainScreen.activity));    //repeated -available in header
                        appReqDetails3.put("source", "APPZILLON");*/
                        appzillonBody.put("appzillonNotificationRegistrationRequest", appReqDetails3);
                    }
                }

                appReqDetails4.put(AppzillonMainScreen.APP_ID, app_id);
                appReqDetails4.put(AppzillonMainScreen.DEVICE_ID, AppzillonUtils.getDeviceId(AppzillonMainScreen.activity));
                appzillonBody.put("appzillonGetAppSecTokensRequest", appReqDetails4);

                onLaunchRequest.put(APPZILLON_HEADER,appzillonheader);
                onLaunchRequest.put(APPZILLON_BODY, appzillonBody);


                JSONObject response = ServerUtilities.sendRequestToServer(StringUtils.getString(StringUtils.SERVER_URL), onLaunchRequest.toString());
                Log.d("TRACK","Merged request all interface");

                if (response != null) {
               //     Log.d("TRACK","Merged response"+response.toString());
                    JSONObject header = response.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
                    try {

                        //boolean status = header.getBoolean("status");
                        String status = header.getString("status");

                        //if (status) {
                        if (status.equalsIgnoreCase("true")) {
                            JSONObject apzBody = response.getJSONObject(AppzillonMainScreen.APPZILLON_BODY);
                            if(apzBody.has("appzillonGetAppSecTokensResponse")) {
                                try {
                                    JSONObject apzResponse = apzBody.getJSONObject("appzillonGetAppSecTokensResponse");
                                    AppzillonMainScreen.SNONCE = apzResponse.getString("serverNonce");
                                    AppzillonMainScreen.SESSIONTOKEN = apzResponse.getString("sessionToken");
                                    if("Y".equalsIgnoreCase(StringUtils.getString("payloadEncryption"))){
                                        AppzillonMainScreen.APPZILLONSAFE = apzResponse.getString("safeToken");
                                    }
                                }catch(Exception e){

                                }
                                //loadWebView();


                            }
                            if(apzBody.has("appzillonAppMasterResponse")){
                                try{
                                JSONObject body = response.getJSONObject(APPZILLON_BODY);

                                String appId = header.getString("appId");
                                if (appInstructionsResponseBody != null) {
                                    appInstructionsResponseBody = null;
                                }
                              //  appInstructionsResponseBody = body.getJSONObject(appId);
                                appInstructionsResponseBody = body.getJSONObject("appzillonAppMasterResponse").getJSONObject(appId);
                                MAIN_APP_NAME = appInstructionsResponseBody.getString(APP_ID);
//
                                if (appInstructionsResponseBody.getString(APP_EXPIRED).equalsIgnoreCase("Y")) {
//							//TODO Abhishek when app is expired
                                }

                                String lVersion = UserSettings.getAppVersion(MAIN_APP_NAME, apps);

                                //Abhishek Bug id 4439 START
                                if (lVersion.equalsIgnoreCase("0.0.0")) {
                                    lVersion = StringUtils.getString(StringUtils.APP_VERSION);
                                }
                                //Abhishek Bug id 4439 END
                                if ((OTAREQUIRED).equalsIgnoreCase("Y")) {
                                    if (!lVersion.equalsIgnoreCase(appInstructionsResponseBody.getString(APP_VERSION))) {
                                        UPDATE_REQUEST = "Y";

                                        //Abhishek 15 April 2015 adding to responce START
                                        appInstructionsResponseBody.put("upgradeRequired", "Y");
                                        //Abhishek 15 April 2015 adding to responce END

                                        //Abhishek bug id 4798 Commented out START
//							UserSettings.setAppVersion(MAIN_APP_NAME, appInstructionsResponseBody.getString(APP_VERSION), apps);
                                        //Abhishek bug id 4798 Commented out END
                                    } else {
                                        UPDATE_REQUEST = "N";

                                        //Abhishek 15 April 2015 adding to responce START
                                        appInstructionsResponseBody.put("upgradeRequired", "N");
                                        //Abhishek 15 April 2015 adding to responce END
                                    }
                                }else{
                                    UPDATE_REQUEST = "N";

                                    //Abhishek 15 April 2015 adding to responce START
                                    appInstructionsResponseBody.put("upgradeRequired", "N");
                                    //Abhishek 15 April 2015 adding to responce END
                                }

                                //check for force update Action
                                if(appInstructionsResponseBody.has("updateAction")){
                                    UPDATE_ACTION = appInstructionsResponseBody.getString("updateAction");
                                }
                                //check for force update Action
                                REMOTE_DEBUG_VALUE = appInstructionsResponseBody.getString(REMOTE_DEBUG);

                                if (REMOTE_DEBUG_VALUE.equalsIgnoreCase("Y")) {
                                    ApzPlugin.debugLevel = 4;
                                }

                                //Abhishek bug id 4405, for single app it was throwing error START
//						appInstructionsResponseBody.getString(PARENT_APP_ID);
                                //Abhishek bug id 4405, for single app it was throwing error END

                                String expiryDate = appInstructionsResponseBody.getString(EXPIRY_DATE);
                                //TODO Abhishek do some thing with expiry date

                                if (appInstructionsResponseBody.getString(CONTAINER_APP).equalsIgnoreCase("Y")) {
                                    //TODO Abhishek when it is a appzillonapp app
                                }
                                //Abhishek 27 March 2015 wipe out app if it is appzillonapp app START
                                if (appInstructionsResponseBody.getString(WIPE_OUT).equalsIgnoreCase("Y")) { // && getResources().getString(R.string.container_app).equalsIgnoreCase("Y") // containerApp Removed
                                    //Abhishek 27 March 2015 wipe out app if it is appzillonapp app END
                                    //WipeOut wipe = new WipeOut(null,activity,getApplicationContext());
                                    WipeOut.wipeOutMainApp();
                                    activity.finish();
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block

                            }


                        }
                            if(apzBody.has("deviceRegisterResponse")){
                                try {
                                    String deviceRegisterStatus = apzBody.getJSONObject("deviceRegisterResponse").getString("status");
                                    /*if Device registered then update then save os version*/
                                    if(deviceRegisterStatus.equalsIgnoreCase("success")){
                                       updateUserSettings();
                                    }else{
                                        JSONArray errArray = response.getJSONArray(AppzillonMainScreen.APPZILLON_ERRORS);
                                        JSONObject error = errArray.getJSONObject(0);
                                        String errorMessage  = getResources().getString(R.string.already_registered);
                                        if(error.getString("errorMessage").contains(errorMessage) || error.getString("errorMessage").equalsIgnoreCase(errorMessage)){
                                            updateUserSettings();
                                        }
                                    }
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block

                                }
                            }
                            respStatus =true;
                         /*   if(!isAppFirstTime.equalsIgnoreCase("YES")){
                                proceedLaunch();
                            }*/
                         //   loadWebView();
                        } else {
                            try {
                                JSONArray errArray = response.getJSONArray(AppzillonMainScreen.APPZILLON_ERRORS);
                                JSONObject error = errArray.getJSONObject(0);
                                String errorMessage = error.getString("errorMessage");
                                onLaunchErrorCode = error.getString("errorCode");
                                respStatus = false;
                            }catch(Exception e){

                            }
                        }
                  //      Log.d("all interface","Log All interface response"+response.toString());

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        //e.printStackTrace();
                    }

                }else{
                    //response null
                    respStatus =false;
                }

            }catch(Exception e){
                respStatus =false;
            }
            return respStatus;
        }

        @Override
        protected void onPostExecute(final Boolean status) {
            super.onPostExecute(status);
            final boolean[] nonceStatus = {false};
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    if (TextUtils.isEmpty(AppzillonMainScreen.SNONCE)) {
                        if (AppzillonMainScreen.activity.getResources().getString(R.string.OFFLINESUPPORT).equalsIgnoreCase("Y")) {
                            nonceStatus[0] =true;
                        } else nonceStatus[0] = false;
                    }else{
                        webView.loadUrl("javascript:appzillon.server.setAppSecToken('Y');");
                        nonceStatus[0]=true;
                    }
                    //check for proper response
                    if(status && nonceStatus[0]) {
                        //webView.loadUrl("javascript:appzillon.plugin.setContainerReady('Y');");
                        onContainerLoaded("Y");
                       /* if (!TextUtils.isEmpty(SNONCE)) {
                            webView.loadUrl("javascript:appzillon.server.setAppSecToken('Y');");
                        }*/
                    }else{
                        if (onLaunchErrorCode.length() > 1 && onLaunchErrorCode.contains("APZ-APP-SIGN-FAULT")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            AlertDialog customAlertDialog;
                            builder.setMessage("Application tempered, press ok to exit").setTitle("Error");
                            builder.setCancelable(false);
                            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    AppzillonMainScreen.activity.finish();
                                }
                            });

                            customAlertDialog = builder.create();
                            customAlertDialog.show();

                        }else{
                            if (!(AppzillonMainScreen.activity.isFinishing())) {
                                showCustomDialog(getServerErrorTitle(), getServerErrorMessage());
                            }
                        }
                        //	webView.loadUrl("javascript:appzillon.plugin.setContainerReady('N');");
                        onContainerLoaded("N");
                    }
                }
            });


        }
    }
		private void getToken() {
        ApzLogger.i(TAG, "getToken");
        new Thread() {
            @Override
            public void run() {
                try {
                    // read from agconnect-services.json
                    String appId = AGConnectServicesConfig.fromContext(activity).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(activity).getToken(appId, "HCM");
                    ApzLogger.i(TAG, "get token:" + token);
                    if(!TextUtils.isEmpty(token)) {
                        sendRegTokenToServer(token);
                    }
                } catch (ApiException e) {
                    ApzLogger.e(TAG, "get token failed, " + e);
                }
            }
        }.start();
    }
	public static void sendRegTokenToServer(String refreshedToken) {
        ApzLogger.i(TAG, "sending token to server. token:" + refreshedToken);
        SharedPreferences apps = activity.getSharedPreferences(app_props, 0);
        UserSettings.setNotificationToken(activity.getResources().getString(R.string.MAINAPPID),refreshedToken,apps);
        try {
            if(IS_SERVER && IS_APP_INITIALIZED){
                ServerUtilities.register(activity.getApplicationContext(),  refreshedToken);
            }
        } catch (Exception e) {
            ApzLogger.d("Exception" ,"run-time exception onTokenRefresh");
        }
    }
    private void showCustomDialog(String title, String msg) {
        allIntfBuilder.setMessage(msg).setTitle(title);
        allIntfBuilder.setCancelable(false);
        allIntfBuilder.setPositiveButton("Try Again",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                new LaunchMergedInterface().execute();
            }
        });
        allIntfBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                AppzillonMainScreen.activity.finish();
            }
        });

        intfCustomAlertDialog = allIntfBuilder.create();
        intfCustomAlertDialog.show();
    }
    public void updateUserSettings() {
        UserSettings.setMultiFactorRegistered(StringUtils.getString(StringUtils.APP_ID),"true",apps);
        UserSettings.setOSVersion(AppzillonUtils.getOsDetails(activity),apps);
    }
    private void onContainerLoaded(String value) {
        UserSettings.setAppValue(AppzillonMainScreen.APP_NAME, AppzillonMainScreen.IS_CONTAINER_LOADED, value, settings);
    }
    /*APP LAUNCH*/
	
	private String getServerErrorTitle() {
        String title = getString(R.string.SERVERCONNECTFAILURETITLE);
        if (title.equals("")) {
            title = "Network Error";
        }
        return title;
    }

    private String getServerErrorMessage() {
        String message = getString(R.string.SERVERCONNECTFAILUREMESSAGE);
        if (message.equals("")) {
            message = "Unable to connect to the server. Retrying in 5 seconds.";
        }
        return message;
    }

    private String getPermissionDeniedMessage() {
        String message = getString(R.string.PERMISSIONDENIEDMESSAGE);
        if (message.equals("")) {
			if(IS_TRACK_LOCATION && "N".equalsIgnoreCase(getResources().getString(R.string.INTERNALSANDBOX))){
                message = "Grant access to Location,Storage for Application to run normally.";
            }else if(IS_TRACK_LOCATION){
                message = "Grant access to Location for Application to run normally.";
            }else if("N".equalsIgnoreCase(getResources().getString(R.string.INTERNALSANDBOX))){
                message = "Grant access to Storage for Application to run normally.";
            }
            
        }
        return message;
    }
	
	private String getRToastMessage() {
        String message = getString(R.string.ROOTEDDEVICETOASTMESSAGE);
        if (message.equals("")) {
            message = "Rooted device, cant Launch App.";
        }
        return message;
    }
}


