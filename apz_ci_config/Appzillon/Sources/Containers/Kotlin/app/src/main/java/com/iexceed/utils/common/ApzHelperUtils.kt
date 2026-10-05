package com.iexceed.utils.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.text.TextUtils
import android.webkit.WebView
import android.widget.Toast
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.StringUtils
import com.iexceed.common.UserSettings
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.miscellaneous.Miscellaneous
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import org.json.JSONException
import org.json.JSONObject
import java.security.SecureRandom
import java.util.*

object ApzHelperUtils {
    private const val TAG = "ApzHelperUtils"
    var SHORTCUT_MSG: String? = null
    var PUSH_MESSAGE: String? = null
    var NOTIFICATION_CODE: String? = null
    var ACTION_CODE: String? = null
    var NOTIF_CODE = 0
    var NOTIF_PARAMS: String? = null
    var NOTIF_TITLE: String? = null
    var NOTIF_IMG_URL: String? = null
    fun generateRandomKey(applicationContext: Context, mEncryptedPref: SharedPreferences) {
        val generator = SecureRandom()
        val randomStringBuilder = StringBuilder()
        val randomLength = 36
        var tempChar: Char
        for (i in 0 until randomLength) {
            tempChar = (generator.nextInt(96) + 32).toChar()
            randomStringBuilder.append(tempChar)
        }
        val randomKey = randomStringBuilder.toString()
        UserSettings.storeRandomKey(applicationContext, randomKey)
        UserSettings.setIsMainAppFirstTime("NO", mEncryptedPref)
    }

    fun checkAppEthics(appzillonMainScreen: Activity) {
        if (AppzillonUtils.isRunningOnEmulator()) {
            Toast.makeText(
                appzillonMainScreen,
                appzillonMainScreen.resources.getString(R.string.app_not_support_emulator),
                Toast.LENGTH_SHORT
            ).show()
            appzillonMainScreen.finish()
        } else if (AppzillonUtils.isDeviceRooted(appzillonMainScreen)) {
            Toast.makeText(
                appzillonMainScreen,
                appzillonMainScreen.resources.getString(R.string.rooted_device_no_applunch),
                Toast.LENGTH_SHORT
            ).show()
            appzillonMainScreen.finish()
        }
    }

    fun handleBackButtonEvent(
        activity: Activity,
        webView: WebView,
        mEncryptedPref: SharedPreferences
    ) {
        val allEvents = UserSettings.getAppValue(
            AppzillonMainScreen.APP_NAME,
            "allEvents",
            "off",
            mEncryptedPref
        )
        val backButtonEvent = UserSettings.getAppValue(
            AppzillonMainScreen.APP_NAME,
            "backButtonEvent",
            "off",
            mEncryptedPref
        )
        if (allEvents == "on" || backButtonEvent == "on") {
            val result = JSONObject()
            try {
                result.put("event", "backButton")
                Miscellaneous.mApzPluginUtil?.sendSuccess(
                    Miscellaneous.eventId,
                    result,
                    true,
                    activity,
                    webView,
                    true
                )
            } catch (e: JSONException) {
                //Sonar fix
            }
        } else {
            webView.loadUrl(
                "javascript:(function() {" + " if(apz.currScr == '" + StringUtils.getString(
                    StringUtils.FIRST_PAGE
                ) + "'){apz.ns.closeApplication({});}})()"
            )
        }
    }

    fun handleAppPauseEvent(
        activity: Activity,
        webView: WebView,
        mEncryptedPref: SharedPreferences
    ) {
        if (UserSettings.getAppValue(
                AppzillonMainScreen.APP_NAME,
                "allEvents",
                "off", mEncryptedPref
            ) == "on"
            || UserSettings.getAppValue(
                AppzillonMainScreen.APP_NAME,
                "appPausedEvent",
                "off",
                mEncryptedPref
            ) == "on"
        ) {
            val result = JSONObject()
            try {
                result.put("event", "appPaused")
                Miscellaneous.mApzPluginUtil?.sendSuccess(
                    Miscellaneous.eventId,
                    result,
                    true,
                    activity,
                    webView,
                    true
                )
            } catch (e: JSONException) {
                //Sonar fix
            }
        }
    }

    fun handleResumeEvent(
        activity: Activity,
        webView: WebView,
        mEncryptedPref: SharedPreferences
    ) {
        if (UserSettings.getAppValue(
                AppzillonMainScreen.APP_NAME,
                "allEvents",
                "off",
                mEncryptedPref
            ) == "on" || UserSettings.getAppValue(
                AppzillonMainScreen.APP_NAME,
                "appResumedEvent",
                "off",
                mEncryptedPref
            ) == "on"
        ) {
            val result = JSONObject()
            result.put("event", "appResumed")
            Miscellaneous.mApzPluginUtil?.sendSuccess(
                Miscellaneous.eventId,
                result,
                true,
                activity,
                webView,
                true
            )
        }
    }

    fun handleActionNotification(intent: Intent) {
        if (intent.hasExtra("type")) {
            SHORTCUT_MSG = intent.getStringExtra("action")
        }
        PUSH_MESSAGE = intent.getStringExtra("message")
        NOTIF_PARAMS = intent.getStringExtra("msgParameters")
        if (intent.hasExtra("notification_code")) {
            NOTIFICATION_CODE = intent.getStringExtra("notification_code")
            ACTION_CODE = intent.getStringExtra("action_code")
            NOTIF_CODE = intent.getIntExtra("notif_id", -1)
        }
        NOTIF_TITLE = intent.getStringExtra("title")
        NOTIF_IMG_URL = intent.getStringExtra("image_url")
    }

    fun initializeEventsDefault(appName: String, mEncryptedPref: SharedPreferences) {
        if ("false".equals(
                UserSettings.isDefaultEventsInitialized(mEncryptedPref),
                ignoreCase = true
            )
        ) {
            val eventsKey = arrayOf(
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
            )
            for (i in eventsKey.indices) {
                UserSettings.setAppValue(appName, eventsKey[i], "off", mEncryptedPref)
            }
        }
    }

    fun handleOnNewIntent(context: Context, newIntent: Intent) {
        if (!TextUtils.isEmpty(newIntent.getStringExtra("message"))) {
            val msg = newIntent.getStringExtra("message")
            val msgPrams = newIntent.getStringExtra("msgParameters")
            val imageUrl = newIntent.getStringExtra("image_url")
            val appStatus = true

            //Notification handle
            val title = newIntent.getStringExtra("title")
            val brIntent = Intent()
            brIntent.putExtra("message", msg)
            brIntent.putExtra("appstatus", appStatus)
            brIntent.putExtra("msgParameters", msgPrams)
            brIntent.putExtra("title", title)
            brIntent.putExtra("image_url", imageUrl)
            (context as AppzillonMainScreen).viewModel.notificationListenerLiveData.value = brIntent
        } else if (!TextUtils.isEmpty(newIntent.getStringExtra("type"))) {
            val brIntent = Intent()
            brIntent.action = "com.iexceed.shortcut"
            brIntent.putExtra("action", newIntent.getStringExtra("action"))
            (context as AppzillonMainScreen).viewModel.shortcutListenerLiveData.value = brIntent
        }
    }

    fun setupDebugLevel() {
        when (StringUtils.getString("logLevel")) {
            "F" -> ApzPlugin.debugLevel = 0
            "E" -> ApzPlugin.debugLevel = 1
            "W" -> ApzPlugin.debugLevel = 2
            "I" -> ApzPlugin.debugLevel = 3
            "D" -> ApzPlugin.debugLevel = 4
            "T" -> ApzPlugin.debugLevel = 5
        }
    }

    fun getPermissionDeniedMessage(activity: Activity): String {
        var message = ""
        if (AppzillonConstants.IS_TRACK_LOCATION && "N".equals(
                activity.resources.getString(R.string.INTERNALSANDBOX),
                ignoreCase = true
            )
        ) {
            message = activity.resources.getString(R.string.grantaccess_location_storage)
        } else if (AppzillonConstants.IS_TRACK_LOCATION) {
            message = activity.resources.getString(R.string.grantaccess_location)
        } else if ("N".equals(
                activity.resources.getString(R.string.INTERNALSANDBOX),
                ignoreCase = true
            )
        ) {
            message = activity.resources.getString(R.string.grantaccess_storage)
        }
        return message
    }

    fun onContainerLoaded(value: String) {
        UserSettings.setAppValue(
            AppzillonMainScreen.APP_NAME,
            AppzillonConstants.IS_CONTAINER_LOADED,
            value,
            EncryptedPrefHelper.getPrefs()
        )
    }

    fun setLocale(context: Context, locale: Locale) {
        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        configuration.setLocale(locale)

        context.createConfigurationContext(configuration)
    }


}