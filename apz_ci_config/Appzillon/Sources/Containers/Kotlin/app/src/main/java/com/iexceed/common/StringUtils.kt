package com.iexceed.common

import android.content.Context
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonUtils.getDecryptedValue
import org.json.JSONObject
import java.io.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

object StringUtils {
    var appInfo = HashMap<String, String>()
    var APP_NAME = "PRODUCTNAME"

    var TITLE_ACTIVITY_MAIN = "PRODUCTTITLE"

    var APP_DESCRIPTION = "PRODUCTDESC"

    var APP_VERSION = "appVersion"

    var FIRST_PAGE = "firstPage"

    var SERVER_URL = "serverUrl"

    var IFACE_ID_IN_SEVER_URL = "IFACEIDINURI"

    var GCM_SERVER_URL = "GCMNOTIFAPPLICATIONKEY"

    var GOOGLE_WEB_CLIENTID = "GOOGLEWEBCLIENTID"

    var GENERATE_OTP = "otpReqd"

    var SERVER_TOKEN = "serverToken"

    var DEFAULT_LANG = "DEFAULTLANGUAGE"

    var APP_ID = "appId"

    var EXPIRY_DATE = "expiryDate"

    var EXPIRY_DATE_FORMAT = "DATEFORMAT"

    var APP_IDLE_TIME_OUT = "idleTimeOut"

    var TRACK_LOCATION = "trackLocation"

    var CERTIFICATE_PINNING = "CERTIFICATEPINNING"

    var CERTIFICATE_NAME = "CERTIFICATENAME"

    var TWITTER_CONSUMER_KEY = "twitterClientId"

    var TWITTER_CONSUMER_SECRET = "twitterSecretKey"

    var GOOGLE_MAPS_KEY = "googleMapsKey"

    var LINKEDIN_API_KEY = "linkedinClientId"

    var LINKEDIN_API_SECRET = "linkedinSecretKey"

    var LINKEDIN_REDIRECT_URL = "linkedinRedirectUrl"

    var IS_SCREENSHOT_ENABLED = "PREVENTSCREENSHOT"

    var IS_NOTIFICATION_SUPPORTED = "NOTIFICATIONREQUIRED"

    var IS_SENDLOG = "sendLog"

    var LOG_LEVEL = "logLevel"

    var SSL_PINNING = "sslPinning"

    var TRUST_ALL_CERTIFICATES = "trustAllCertificates"

    var ENABLE_MOCK_SERVER = "enableMockServer"



    fun StringUtils(context: Context, appLoc: String): StringUtils
    {
       //appInfo = HashMap<String, String>()
       appInfo.put(APP_NAME, context.resources.getString(R.string.PRODUCTNAME))
        appInfo.put(TITLE_ACTIVITY_MAIN, context.resources.getString(R.string.PRODUCTTITLE))
       appInfo.put(APP_DESCRIPTION, context.resources.getString(R.string.PRODUCTDESC))
       appInfo.put(CERTIFICATE_PINNING, context.resources.getString(R.string.CERTIFICATEPINNING))
        appInfo.put(CERTIFICATE_NAME, context.resources.getString(R.string.CERTIFICATENAME))
        appInfo.put(IS_SCREENSHOT_ENABLED, context.resources.getString(R.string.PREVENTSCREENSHOT))
       appInfo.put(IS_NOTIFICATION_SUPPORTED, context.resources.getString(R.string.NOTIFICATION))
       appInfo.put(EXPIRY_DATE_FORMAT, context.resources.getString(R.string.expiryDateFormat))
       appInfo.put(DEFAULT_LANG, "en")
        var inputStream: InputStream? = null
        var br: BufferedReader? = null
        val sb = StringBuilder()
        var line: String?
        try {
            //For OTA refresh
            inputStream = getInputStream(context, appLoc)
            if (inputStream != null) {
                br = BufferedReader(InputStreamReader(inputStream))
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                }
                br.close()
            }
        } catch (e: java.lang.Exception) {
            //Sonar fix
        }
        val settingsJson: JSONObject? = null
        if (inputStream != null) {
            try {
                fetchPropertyDetails(settingsJson, sb)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }
        return this
    }

    private fun fetchPropertyDetails(
        settingsJson: JSONObject?,
        sb: StringBuilder
    ) {
        var settingsJson1 = settingsJson
        settingsJson1 = JSONObject(sb.toString())
        val iterator = settingsJson1.keys()
        while (iterator.hasNext()) {
            var key = iterator.next().toString()
            var value = settingsJson1.getString(key)

            if (key.startsWith("serverTokenEnhanced") || key.startsWith("serverUrlEnhanced")) {
                val pair = handleDeviceOSAboveO(key, value, settingsJson1)
                key = pair.first
                value = pair.second
            }

            appInfo[key] = value
        }
    }

    private fun handleDeviceOSAboveO(
        key: String,
        value: String,
        settingsJson1: JSONObject
    ): Pair<String, String> {
        var key1 = key
        var value1 = value
        if ("serverTokenEnhanced2" == key1) {
            key1 = "serverToken";
            value1 = getDecryptedValue(value1)!!
        }
        if ("serverUrlEnhanced2" == key1) {
            key1 = "serverUrl";
            if (!value1.startsWith("http") || settingsJson1.getString("serverUrlEncReq") == "Y") {
                //decrypt the value only if either the URL does not starts with http or the serverUrlEncReq is Y
                value1 = getDecryptedValue(value1)!!
            }
        }
        return Pair(key1, value1)
    }

    private fun handleDeviceOSBelowO(
        key: String,
        value: String,
        settingsJson1: JSONObject
    ): Pair<String, String> {
        var key1 = key
        var value1 = value
        if ("serverTokenEnhanced" == key1) {
            key1 = "serverToken"
            value1 = getDecryptedValue(value1)!!
        }
        if ("serverUrlEnhanced" == key1) {
            key1 = "serverUrl"
            if (!value1.startsWith("http") || settingsJson1.getString("serverUrlEncReq") == "Y") {
                //decrypt the value only if either the URL does not starts with http or the serverUrlEncReq is Y
                value1 = getDecryptedValue(value1)!!
            }
        }
        return Pair(key1, value1)
    }

    private fun getInputStream(
        context: Context,
        appLoc: String
    ) = if (AppzillonMainScreen.OTAREQUIRED.equals("Y")) {
        val filesJson =
            File(AppzillonMainScreen.SANDBOX_LOC + "/" + AppzillonMainScreen.ASSET_APP_LOC + "screens/config/appprops.json")
        FileInputStream(filesJson)
    } else {
        context.assets.open("$appLoc/screens/config/appprops.json")
    }

    fun getString(stringId: String): String {
        var appProp = ""
        try {
            appProp = appInfo[stringId].toString()
            if (appProp.isEmpty()) {
                appProp = ""
            }
        } catch (e: Exception) {
            //Sonar fix
        }
        return appProp
    }


}
