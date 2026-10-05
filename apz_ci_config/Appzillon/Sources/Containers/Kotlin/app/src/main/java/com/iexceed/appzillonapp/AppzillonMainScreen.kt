//package com.iexceed.consumerBanking
package com.iexceed.appzillonapp
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.KeyEvent
import android.view.WindowManager
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.lifecycle.lifecycleScope
import com.iexceed.common.*
import com.iexceed.common.AppzillonConstants.ASSETS_MAIN_FOLDER
import com.iexceed.common.AppzillonConstants.IS_SERVER
import com.iexceed.common.AppzillonUtils.getOsDetails
import com.iexceed.common.StringUtils.getString
import com.iexceed.common.UserSettings.getAppValue
import com.iexceed.common.UserSettings.getIsMultiFactorRegistered
import com.iexceed.common.UserSettings.getIsNotificationRegistered
import com.iexceed.common.UserSettings.getNotificationToken
import com.iexceed.common.UserSettings.getOSVersion
import com.iexceed.di.component.ActivityComponent
import com.iexceed.plugins.ApzPlugin.Companion.checkPermissionFlag
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.barcode.ApzBarcodePlugin
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.plugins.miscellaneous.Miscellaneous
import com.iexceed.plugins.notification.NotificationPlugin
import com.iexceed.utils.battery.BatteryBroadcastReceiver
import com.iexceed.utils.common.ApzHelperUtils
import com.iexceed.utils.misc.ApzMisc
import com.iexceed.utils.network.InternetCheck.isNetworkAvailable
import com.iexceed.utils.permission.ApzPermission
import com.iexceed.utils.webview.ApzWebChromeClient
import com.iexceed.utils.webview.ApzWebViewClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*
import javax.inject.Inject

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/

class AppzillonMainScreen : ApzActivity<ApzViewModel>(), OnRequestPermissionsResultCallback, PostAppSecToken,GetSSlStatusInterface{
    private val TAG = "AppzillonMainScreen"
    private var languageCode: String? = null
    @Inject
    lateinit var webSettings: WebSettings
    //private var apps: EncryptedPrefHelper? = null
    @Inject
    lateinit var mEncryptedPref: SharedPreferences
    var app_loc: String? = null
    @Inject
    lateinit var mWebView: WebView
    @Inject
    lateinit var apzPluginBridge : ApzPluginBridge
    @Inject
    lateinit var stringUtils: StringUtils
    @Inject
    lateinit var apzWebViewClient: ApzWebViewClient
    @Inject
    lateinit var apzApzWebChromeClient : ApzWebChromeClient

    lateinit var connectivityManager: ConnectivityManager

    private var isAppPaused = false // checking the condition in one place do we need this.
    private val isLandingPage = false //Noweher setting to true and again making to false.

    /* For batteryListener *//*
    private var initialPercent = 0

    private var isInitialPercentSet = false*/
    var activity = this

    private lateinit var receiver: BatteryBroadcastReceiver

    private val lHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if(msg.arg1 == AppzillonConstants.SANDBOX_CREATED){
                proceedToFetchAppSecToken()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        isAppPaused = true
        unregisterReceiver(receiver)
        if (!isLandingPage)
        {
            ApzHelperUtils.handleAppPauseEvent(this, mWebView, mEncryptedPref)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::connectivityManager.isInitialized) {
            connectivityManager.unregisterNetworkCallback(ConnectivityManager.NetworkCallback())
        }

        ApzBarcodePlugin.checkForBarcodeOnDestroy()
        if(alertDialogBuilder!=null && customAlertDialog?.isShowing==true){
            customAlertDialog?.dismiss()
            customAlertDialog=null
            alertDialogBuilder=null
        }
    }

    companion object {
        // removed ApzActivity
       // var webView: WebView? = null
        var REMOTE_DEBUG_VALUE = "N"
        lateinit var isAppFirstTime: String

        var serverErrorDialog: AlertDialog.Builder? = null
        var IS_APP_INITIALIZED = false
        var MAIN_APP_NAME: String = ""
        var APP_NAME = ""
        var PUSH_MESSAGE: String? = null
        var SHORTCUT_MSG: String? = null

        var APP_REMOTE_DEBUG = "N"

        var DECRYPTED_KEY = ""

        var OTAREQUIRED = ""

        var SANDBOX_LOC = ""

        var ASSET_APP_LOC = ""

        var app_loc: String? = null

        var isSecured = false
        var APP_LAUNCHED = false
        var MAIN_APP_PARAMS: JSONObject? = null
        var internalPathCheck = ""

        var serverErrorAlertDialog: AlertDialog? = null

        //public static HashMap<String,JSONObject> notifMap;
        var mapjson: JSONArray? = null

        //NotificationValues
        var NOTIFICATION_CODE: String? = null

        var ACTION_CODE: String? = null

        var NOTIF_CODE = 0

        var NOTIF_PARAMS: String? = null

        var NOTIF_TITLE: String? = null

        var NOTIF_IMG_URL: String? = null

        var globalSSLHandler: SslErrorHandler? = null

        var alertDialogBuilder: AlertDialog.Builder? = null
        var customAlertDialog: AlertDialog? = null

        var IS_BATTERY_CALLBACK_ENABLED = false

        var IS_STATE_CALLBACK_ENABLED = false

        var IS_THRESHOLD_CALLBACK_ENABLED = false

        var IS_REGISTERED = false

        var recordingState = false

        var playingState = false

        var pausedState = false
		
		val app_props = "APP_PREFS"

    }

    private fun onSetupAppzillonView(savedInstanceState: Bundle?) {
        try {
            receiver = BatteryBroadcastReceiver(this, mEncryptedPref)
            //mEncryptedPref = EncryptedPrefHelper.init(applicationContext)
            isAppFirstTime = UserSettings.getIsMainAppFirstTime(applicationContext)
            //activity = this
            IS_APP_INITIALIZED = false

            MAIN_APP_NAME = resources.getString(R.string.MAINAPPID)

            OTAREQUIRED = resources.getString(R.string.OTAREQUIRED)

            var appName: String? = null

            var appDebug: String? = null
            val pushmessage: String
            val shortcutType: String

            if (savedInstanceState != null) {
                appName = savedInstanceState.getString("app_name")
                app_loc = savedInstanceState.getString("app_loc")
                appDebug = savedInstanceState.getString("app_debug")
                pushmessage = savedInstanceState.getString("message")!!
                shortcutType = savedInstanceState.getString("type")!!
            }

            if (appName == null){
                appName = MAIN_APP_NAME
            }

            if (app_loc == null){
                app_loc = "$ASSETS_MAIN_FOLDER/$MAIN_APP_NAME"
            }

            if (appDebug == null){
                appDebug = REMOTE_DEBUG_VALUE
            }

            if (app_loc != null) {
                ASSET_APP_LOC = app_loc+"/"
            }
            if (appName != null) {
                APP_NAME = appName
            }
            if (appDebug != null) {
                APP_REMOTE_DEBUG = appDebug
            }

			// Master sync
            ApzHelperUtils.onContainerLoaded("N")
            // Multifactor Check
            if (isAppFirstTime.equals("YES", ignoreCase = true)) {  
				ApzHelperUtils.generateRandomKey(applicationContext, mEncryptedPref )
            }

            //To print the server logs as native logs START
            AppzillonConstants.IS_APP_DEBUGABLE = 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
            // For Actionable Notification, get the message from intent
            ApzHelperUtils.handleActionNotification(intent)


            if (intent.hasExtra("type")) {
                SHORTCUT_MSG = intent.getStringExtra("action")
            }

            //val titleBarString = "NO"
            //For Actionable Notification when app is in background.
            // Taking title mode from values folder file only, Because for child app it will crash END
            /*if (savedInstanceState == null) {
                val enableNavigation = "YES".equals(titleBarString, ignoreCase = true)
                if (enableNavigation) {
                    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE)
                } else {
                    requestWindowFeature(Window.FEATURE_NO_TITLE)
                }
            }*/
            setTheme(R.style.Removebg)
            if (swipeLayout == null) {
                swipeLayout = findViewById(R.id.swipe_container)
                swipeLayout!!.isEnabled = false
            }

            /*mWebView?.isVerticalScrollBarEnabled = false
            mWebView?.isHorizontalScrollBarEnabled = false
            mWebView?.scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            mWebView?.isScrollbarFadingEnabled = true*/
            /*mWebView?.isFocusableInTouchMode = true
            mWebView?.filterTouchesWhenObscured = true*/
            //apzPluginBridge = ApzPluginBridge(mWebView!!,this,applicationContext, apzPluginUtil)
            mWebView.addJavascriptInterface(apzPluginBridge, "NativeBridge")
            // to disable text selection
            mWebView.webViewClient = apzWebViewClient

            mWebView.webChromeClient = apzApzWebChromeClient

            if (resources.getString(R.string.SERVER) == "Y") {
                IS_SERVER = true
            }
            if (resources.getString(R.string.TRACKLOCATION) == "Y") {
                AppzillonConstants.IS_TRACK_LOCATION = true
            }
            if (ApzPermission.checkApzPermission(this)) {
                // permissions has not been granted.
                ApzPermission.requestDefaultPermissions(this)
            }else{
                FileUtils.scopeStorageMigration(lifecycleScope , this, lHandler)
            }
        } catch (e: Exception) {
            ApzLogger.w(TAG ,"run-time exception onCreate")
        }
    }

    private fun launchAppzillonFirstPage() {
        if (OTAREQUIRED.equals("Y", ignoreCase = true)) {
            mWebView.loadUrl("file:///$SANDBOX_LOC/$APP_NAME.html")
        } else {
            mWebView.loadUrl("file:///android_asset/$MAIN_APP_NAME.html")
        }
    }

    override fun onResume() {
        super.onResume()
        if (!BuildConfig.DEBUG) {
            ApzHelperUtils.checkAppEthics(this)
        }
        registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED), AppzillonConstants.APPZILLON_BROADCAST_PERMISSION, null)
        if (isAppPaused) {
            ApzHelperUtils.handleResumeEvent(this, mWebView, mEncryptedPref)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            viewModel.orientationListenerLiveData.value = "LANDSCAPE"
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewModel.orientationListenerLiveData.value = "PORTRAIT"
        }
    }


   //AppShortcut
    fun sendShortcut(type: String?, action: String?, callbackId: String?) {
        ApzLogger.w(TAG, "SendShortcutMSG "+action.toString())
        val json = JSONObject()
        Handler(Looper.getMainLooper()).postDelayed({
            if (mWebView != null) {
                try {
                    json.put("shortcutID", action)
                    json.put("event", "shortcutItem")
                } catch (e: JSONException) {
                    // handle exception
                }
                apzPluginUtil.sendSuccess(callbackId, json, true, activity, mWebView!!, true)
            }
        }, 500)
    }

    override fun onNewIntent(newIntent: Intent) {
        ApzLogger.w(TAG, "Inside newIntent")
        try {
            ApzHelperUtils.handleOnNewIntent(this, newIntent)
            super.onNewIntent(newIntent)
        } catch (e: java.lang.Exception) {
            ApzLogger.w(TAG, "run-time exception onNewIntent")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var mDenied = false
        var neverAskAgain = false
        if (requestCode == PluginConstants.APZ_REQ_DEFAULT_PERMISSIONS) {
            handleDefaultPermissions(permissions, mDenied, neverAskAgain)
        } else if (mapPermissionResultHandler != null) {
            val resultPermissionHandler = mapPermissionResultHandler!![requestCode]
            if (resultPermissionHandler != null) {
                removeOnPermissionsResultHandler(requestCode)
                resultPermissionHandler.handlePermissionResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun handleDefaultPermissions(
        permissions: Array<String?>,
        mDenied: Boolean,
        neverAskAgain: Boolean
    ) {
        var mDenied1 = mDenied
        var neverAskAgain1 = neverAskAgain
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission!!)) {
                mDenied1 = true
            } else {
                neverAskAgain1 = ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            }
        }
        when {
            neverAskAgain1 -> {
                ApzMisc.showDialog(
                    this,
                    resources.getString(R.string.grant_access),
                    resources.getString(R.string.change_permission_appsetting),
                    null,
                    resources.getString(R.string.exit)
                ) { which ->
                    if (which == Dialog.BUTTON_NEGATIVE) finish()
                }
            }

            mDenied1 -> {
                checkPermissionFlag = false
                permissionDeniedCallback()
            }
            else -> {
                checkPermissionFlag = true
                FileUtils.scopeStorageMigration(lifecycleScope, this, lHandler)
            }
        }
    }

    private fun permissionDeniedCallback() {
        val message = resources.getString(R.string.PERMISSIONDENIEDMESSAGE).ifEmpty {
            ApzHelperUtils.getPermissionDeniedMessage(this)
        }

        ApzMisc.showDialog(
            this,
            resources.getString(R.string.permission_required),
            message,
            resources.getString(R.string.allow),
            resources.getString(R.string.exit)
        ) { which ->
            when(which){
                Dialog.BUTTON_POSITIVE -> ApzPermission.requestDefaultPermissions(this)
                Dialog.BUTTON_NEGATIVE -> finish()
            }
        }
    }

    private fun proceedToFetchAppSecToken() {
        launchAppzillonFirstPage()
        ApzLogger.w(TAG, "Inside proceedToFetchAppSecToken")
        //stringUtils = StringUtils.StringUtils(this, app_loc!!)
        ApzHelperUtils.setupDebugLevel()

        var isMockServerEnabled = false
        //Get MockServer status from App props
        if (stringUtils.getString(StringUtils.ENABLE_MOCK_SERVER).trim().equals("true")) {
            isMockServerEnabled = true
        }

        //Send first request if server is enabled && Mock server is not enabled
        loadWebView()
        if (IS_SERVER && !isMockServerEnabled)
        {
            //serverErrorAlertDialog = serverErrorDialog?.create()
            if (isNetworkAvailable(this)) {
                viewModel.doLaunchMergedInterfaceRequest()
            } else {
                    if (!isFinishing &&
                        (resources.getString(R.string.OFFLINESUPPORT) == "N" &&
                                resources.getString(R.string.APPOFFLINESUPPORT) == "N")) {
                        ApzMisc.showDialog(
                            this,
                            getServerErrorTitle(),
                            getServerErrorMessage(),
                            resources.getString(R.string.ok),
                            null
                        ) { which ->
                            when(which){
                                Dialog.BUTTON_POSITIVE -> {
                                    //Do Nothing
                                }
                            }
                        }
                    }
                if(resources.getString(R.string.APPOFFLINESUPPORT) == "Y"){
                    connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    connectivityManager.registerDefaultNetworkCallback(networkCallback)
                }
            }
        }
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            AppzillonConstants.isNetworkConnected = true
            val isAppTokenSet = UserSettings.getAppValue(
                AppzillonMainScreen.APP_NAME,
                AppzillonConstants.IS_CONTAINER_LOADED,
                "N",
                mEncryptedPref
            )
            if(!isAppTokenSet.equals("Y",ignoreCase = true)) {
                viewModel.doLaunchMergedInterfaceRequest()
            }
        }

        override fun onLost(network: Network) {
            AppzillonConstants.isNetworkConnected = false
        }
    }

    private fun getServerErrorTitle() : String {
        return resources.getString(R.string.SERVERCONNECTFAILURETITLE).ifEmpty {
            resources.getString(R.string.network_error)
        }
    }

    private fun getServerErrorMessage() : String {
        return resources.getString(R.string.SERVERCONNECTFAILUREMESSAGE)
            .ifEmpty { resources.getString(R.string.no_internet) }
    }

    private fun multiFactorRegister() {
        viewModel.doMultiFactorRequest()
    }

    fun showExpiryMsg(context: Context, act: Activity?, appName: String) {

        ApzMisc.showDialog(
            this,
            resources.getString(R.string.app_name),
            resources.getString(R.string.appExpiryMsg),
            resources.getString(R.string.ok),
            null
        ) { which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    UserSettings.setApplicationExpired("true", appName, mEncryptedPref)
                    if (appName.equals(APP_NAME, ignoreCase = true)) finish()
                }
            }
        }
    }

    override fun executeAfterAppSecToken(refreshServerNonce: Boolean) {
        ApzLogger.w(TAG, "Inside executeAfterAppSecToken ")
        loadWebView()
    }

    fun loadWebView() {
        secondLaunch()
        IS_APP_INITIALIZED = true
        // For preventing app to take screen shot if is_screenshot_enabled flag is Y END
        if (getString(StringUtils.IS_SCREENSHOT_ENABLED).equals("Y",ignoreCase = true))
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE)

        if (!"".equals(getString(StringUtils.EXPIRY_DATE), ignoreCase = true) &&
            AppzillonUtils.isAppExpired(this, APP_NAME) ) {
                showExpiryMsg(applicationContext, this, APP_NAME)
        }

        /* To change language setting */
        languageCode = getAppValue(APP_NAME,
            "DEFAULTLANGUAGE",
            getString(StringUtils.DEFAULT_LANG), mEncryptedPref)
        val appLocale = Locale(languageCode!!)
        ApzHelperUtils.setLocale(this, appLocale)
        handleFirstLaunch()

        //Read File notif_details.json
        handleNotificationDetails()
        ApzHelperUtils.initializeEventsDefault(APP_NAME, mEncryptedPref)
    }

    private fun handleFirstLaunch() {
        val isFirstTime: String = UserSettings.getIsAppFirstTime(applicationContext, APP_NAME)
        val lang = getString(StringUtils.DEFAULT_LANG)
        val otpFlag = getString(StringUtils.GENERATE_OTP)
        if (isFirstTime.equals("YES", ignoreCase = true)) {
            UserSettings.setAppValue(APP_NAME, "DEFAULTLANGUAGE", lang, mEncryptedPref)
            UserSettings.setAppValue(APP_NAME, "OTPFLAG", otpFlag, mEncryptedPref)
            UserSettings.setIsAppFirstTime(APP_NAME, "NO", mEncryptedPref)
            val list: Array<String?>? = null
            try {
                //For OTA refresh
                verifyOTAEnabled(list)
            } catch (e: IOException) {
                //handle exception
            }

            FileUtils.copyStaticFiles(applicationContext)
        }
    }

    private fun verifyOTAEnabled(list: Array<String?>?) {
        var list1 = list
        if (OTAREQUIRED.equals("Y", ignoreCase = true)) {
            val loc = SANDBOX_LOC + File.separator + ASSET_APP_LOC + "sqlite"
            val fileList = File(loc).listFiles()
            if (fileList != null) {
                list1 = arrayOfNulls(fileList.size)
                for (a in fileList.indices) {
                    //list = new String [fileList.length];
                    if (fileList[a].isFile) {
                        list1[a] = fileList[a].name
                    }
                }
            }
        } else {
            list1 = applicationContext.assets.list(ASSET_APP_LOC + "sqlite")
        }
    }

    private fun handleNotificationDetails() {
        var isNotif: InputStream? = null
        var notifbr: BufferedReader? = null
        try {
            val sbNotif = StringBuilder()
            var notifline: String?
            var notificationJson: JSONArray? = null
            isNotif = if (OTAREQUIRED.equals("Y", ignoreCase = true)) {
                val settingsJson =
                    File(SANDBOX_LOC + File.separator + ASSET_APP_LOC + "screens/config/notif_details.json")
                FileInputStream(settingsJson)
            } else {
                assets.open(ASSET_APP_LOC + "screens/config/notif_details.json")
            }
            notifbr = BufferedReader(InputStreamReader(isNotif))
            while (notifbr.readLine().also { notifline = it } != null) {
                sbNotif.append(notifline)
            }

            notificationJson = JSONArray(sbNotif.toString())
            mapjson = JSONArray()
            for (j in 0 until notificationJson.length()) {
                val json = notificationJson.getJSONObject(j)
                val arr = json.getJSONArray("actions")
                val notification = json.getString("notification_code")
                val jsonObj = JSONObject()
                jsonObj.put("notification_code", notification)
                jsonObj.put("actions", arr)
                mapjson!!.put(j, jsonObj)
            }
        } catch (io: IOException) {
            io.printStackTrace()
        } finally {
            isNotif?.close()
            notifbr?.close()
        }
    }

    private fun secondLaunch()
    {
        ApzLogger.w(TAG, "Inside secondLaunch : isAppFirstTime : $isAppFirstTime")
        if (!isAppFirstTime.equals("YES", ignoreCase = true)) {
            val multifactorReg = getIsMultiFactorRegistered(
                getString(StringUtils.APP_ID), mEncryptedPref)
            val savedOsVersion = getOSVersion(mEncryptedPref)
            val presentOsVersion = getOsDetails(this)
            val notificationRegistration = getIsNotificationRegistered(
                getString(StringUtils.APP_ID), mEncryptedPref)
            //Check weather device is already registered or not Check weather OS version from last registration is same or updated
            if ((multifactorReg == "false" || savedOsVersion != presentOsVersion) && IS_SERVER) {
                multiFactorRegister()
            } else {
                ApzLogger.w(TAG, "Device Already registered for multifactor")
            }
            if (notificationRegistration.equals("false", ignoreCase = true)) {
                val token = getNotificationToken(getString(StringUtils.APP_ID), mEncryptedPref)
                if (token?.isNotEmpty() == true && NotificationPlugin.isPlugin && IS_SERVER) {
                    NotificationRegistration(this).execute()
                }
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val action = event.action
        val keyCode = event.keyCode
        val result = JSONObject()

        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (action == KeyEvent.ACTION_DOWN) {
                    AppzillonUtils.checkVolumeState(this, mWebView,"U")
                }
                true
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (action == KeyEvent.ACTION_DOWN) {
                    AppzillonUtils.checkVolumeState(this, mWebView,"D")
                }
                true
            }
            KeyEvent.KEYCODE_MENU -> {
                handleKeyCodeMenu(action, result)
                true
            }
            KeyEvent.KEYCODE_CALL -> {
                handleKeyCodeCall(action, result)
                true
            }
            KeyEvent.KEYCODE_SEARCH -> {
                handleKeyCodeSearch(action, result)
                true
            }
            else -> super.dispatchKeyEvent(event)
        }
    }

    private fun handleKeyCodeMenu(action: Int, result: JSONObject) {
        if (action == KeyEvent.ACTION_DOWN) {
            try {
                result.put("event", "menuButton")
            } catch (e: JSONException) {
                //handle exception
            }
            if (getAppValue(
                    APP_NAME,
                    "allEvents", "off", mEncryptedPref
                ) == "on" ||
                getAppValue(
                    APP_NAME,
                    "menuButtonEvent", "off", mEncryptedPref
                ) == "on"
            ) {
                Miscellaneous.mApzPluginUtil?.sendSuccess(
                    Miscellaneous.eventId,
                    result, false,
                    activity, mWebView, true
                )
            }
        }
    }

    private fun handleKeyCodeSearch(action: Int, result: JSONObject) {
        if (action == KeyEvent.ACTION_DOWN) {
            try {
                result.put("event", "searchButton")
            } catch (e: JSONException) {
                //handle exception
            }
            if (getAppValue(
                    APP_NAME,
                    "allEvents", "off", mEncryptedPref
                ) == "on" ||
                getAppValue(
                    APP_NAME,
                    "appSearchButtonEvent", "off", mEncryptedPref
                ) == "on"
            ) {
                Miscellaneous.mApzPluginUtil?.sendSuccess(
                    Miscellaneous.eventId,
                    result, true,
                    activity, mWebView, true
                )
            }
        }
    }

    private fun handleKeyCodeCall(action: Int, result: JSONObject) {
        if (action == KeyEvent.ACTION_DOWN) {
            try {
                result.put("event", "callButton")
            } catch (e: JSONException) {
                //handle exception
            }
            if (getAppValue(
                    APP_NAME,
                    "allEvents", "off", mEncryptedPref
                ) == "on" ||
                getAppValue(
                    APP_NAME,
                    "appCallStartEvent", "off", mEncryptedPref
                ) == "on"
            ) {
                Miscellaneous.mApzPluginUtil?.sendSuccess(
                    Miscellaneous.eventId, result, true,
                    activity, mWebView, true
                )
            }
        }
    }

    override fun onResult(status: Boolean) {
        onSSLStatus(status)
    }

    //GetSSL Status
    fun onSSLStatus(status: Boolean) {
        isSecured = status
        if (globalSSLHandler != null) {
            if (status) {
                globalSSLHandler?.proceed()
            } else {
                globalSSLHandler?.cancel()
            }
            globalSSLHandler = null
        }
    }

    override fun onBackPressed()
    {
        if(ApzBarcodePlugin.onBackPressed()){
            ApzBarcodePlugin.checkForBarcodeOnDestroy()
        }else{
            ApzHelperUtils.handleBackButtonEvent(this, mWebView, mEncryptedPref)
        }
    }

    override fun provideLayoutId(): Int = R.layout.webview

    override fun injectDependencies(activityComponent: ActivityComponent) {
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        onSetupAppzillonView(savedInstanceState)
    }
}
