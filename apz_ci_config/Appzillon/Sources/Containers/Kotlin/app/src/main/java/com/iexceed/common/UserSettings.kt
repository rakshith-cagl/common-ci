package com.iexceed.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

object UserSettings {

    private val apzPrefs = "SecureData"

    private val ALIAS = "spAlias"

    private lateinit var iv: ByteArray

    private const val RANDOM_KEY_ENCRYPTED = "randonKeyEncrypted"

   fun setBase64Image(base64Image: String?, settings: SharedPreferences) {
        val editor = settings.edit()
        editor.putString("base64Image", base64Image)
        editor.apply()
    }

    fun getBase64Image(settings: SharedPreferences): String? {
        return settings.getString("base64Image", "")
    }

    fun deleteBase64Image(settings: SharedPreferences) {
        val editor = settings.edit()
        editor.remove("base64Image")
        editor.apply()
    }

    //Get Device ID
    fun getDeviceId(settings: SharedPreferences): String {
        var deviceId = settings.getString(AppzillonConstants.sDeviceId, null)
        if (deviceId.isNullOrEmpty()) {
            deviceId = UUID.randomUUID().toString()
            setDeviceId(deviceId, settings)
        }
        return deviceId
    }

    fun setDeviceId(deviceId: String?, settings: SharedPreferences) {
        val editor = settings.edit()
        editor.putString(AppzillonConstants.sDeviceId, deviceId)
        editor.apply()
    }

    fun decStr(aContext: Context, textToDecrypt: String): String {
        return try {
            AppzillonUtils.decyyptString(getKey(aContext), textToDecrypt).toString()
        }catch (e:Exception){
            ""
        }
    }

    fun getIsMainAppFirstTime(aContext: Context): String {
        val lIsAppFromEncryptedPref = EncryptedPrefHelper.read("isFirstTime", "")
        if(lIsAppFromEncryptedPref.isNullOrEmpty())
        {
            //If not present it will create the pref
            val lOldSettings = aContext.getSharedPreferences("APP_PREFS",0)
            val isFirstTime = lOldSettings.getString("isFirstTime", "")
            var lDecryptString: String
            if(!isFirstTime.isNullOrEmpty())
            {
                lDecryptString = decStr(aContext, isFirstTime)
                if(lDecryptString.isNotEmpty() && lDecryptString != "plain"){
                    //Here we move all OldPrefs to newEncryptedPrefs
                    movePrefToEncryptPref(aContext)
                }
            }else{
                lDecryptString = "YES"
            }
            if(lDecryptString.isEmpty() && isFirstTime != null) {
                lDecryptString = isFirstTime.toString()
                EncryptedPrefHelper.save("isFirstTime", lDecryptString)
            }
            return lDecryptString
        }else{
            return lIsAppFromEncryptedPref
        }
    }

    @SuppressLint("SdCardPath")
    fun movePrefToEncryptPref(aContext: Context) {
        val properties = arrayOf("USER_PREFS", "APP_PREFS")
        try {
            for (item in properties)
            {
                val lSettings = aContext.getSharedPreferences(item,0)
                for ((key) in lSettings.all)
                {
                    try {
                        val lString = lSettings.getString(key,"")
                        if(lString?.isNotEmpty() == true){
                            val lDecryptString = decStr(aContext,lString)
                            //Here we move from old pref to new EncryptedPref
                            if(lDecryptString.isNotEmpty() && lDecryptString != "plain")
                            {
                                EncryptedPrefHelper.save(key, lDecryptString)
                            }
                        }
                    } catch (e: Exception) {
                        //Sonar fix
                    }
                }
                //Removing all the keys..
                lSettings.edit().clear().apply()
            }
        } catch (e: Exception) {
            //Sonar fix
        }

    }

    fun getKey(aContext: Context): String {
        val paddingMask = "$$$$$$$$$$$$$$$$"
        var key: String = retrieveRandomKey(aContext)
        if (key.length <= 16) {
            key += paddingMask.substring(0, 16 - key.length)
        } else {
            key = key.substring(0, 16)
        }
        return key
    }


    /*public static String retrieveRandomKey(){
		Activity activity = AppzillonMainScreen.activity;
		String dKey = AppzillonMainScreen.DECRYPTED_KEY;
		if(dKey.equalsIgnoreCase("")){
			SharedPreferences sharedPreferences = activity.getSharedPreferences(apzPrefs, activity.MODE_PRIVATE);
			String eKey = sharedPreferences.getString(RANDOM_KEY_ENCRYPTED,"");
			dKey = decryptKey(eKey);
			AppzillonMainScreen.DECRYPTED_KEY = dKey;
		}
		return dKey;
	}*/
    fun retrieveRandomKey(aContext: Context): String {
        var dKey = AppzillonMainScreen.DECRYPTED_KEY
        if (dKey.isEmpty()) {
            val sharedPreferences = aContext.getSharedPreferences(apzPrefs, Context.MODE_PRIVATE)
            val eKey = sharedPreferences.getString(RANDOM_KEY_ENCRYPTED, "")
            if (eKey.isNullOrEmpty()) {
                val generator = SecureRandom()
                val randomStringBuilder = StringBuilder()
                val randomLength = generator.nextInt(16)
                var tempChar: Char
                for (i in 0 until randomLength) {
                    tempChar = (generator.nextInt(96) + 32).toChar()
                    randomStringBuilder.append(tempChar)
                }
                dKey = randomStringBuilder.toString()
                storeRandomKey(aContext,dKey)
            } else {
                dKey = decryptKey(eKey).toString()
            }
            AppzillonMainScreen.DECRYPTED_KEY = dKey
        }
        return dKey
    }

    private fun decryptKey(textToDecrypt: String): String? {
        var dStr: String? = null
        val bytes = Base64.decode(textToDecrypt, Base64.NO_WRAP or Base64.NO_PADDING)
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val secretKeyEntry = keyStore.getEntry(ALIAS, null) as? KeyStore.SecretKeyEntry
            val secretKey = secretKeyEntry!!.secretKey
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val ivStr = EncryptedPrefHelper.read("iv_sp_key", "")
            iv = Base64.decode(ivStr, Base64.NO_WRAP)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val decodedData = cipher.doFinal(bytes)
            dStr = String(decodedData, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dStr
    }

    fun setIsMainAppFirstTime(isFirstTime: String?, settings: SharedPreferences) {
        val editor = settings.edit()
        editor.putString("isFirstTime", isFirstTime)
        editor.apply()
    }

    fun storeRandomKey(aContext: Context, dKey: String) {
        val sharedPreferences = aContext.getSharedPreferences(apzPrefs, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(RANDOM_KEY_ENCRYPTED, dKey)
        editor.apply()
    }


    fun getScopeMigrationStatus(aContext: Context, appName: String): String {
        val lSettings = EncryptedPrefHelper.getPrefs(aContext)
        val migrationStatus = lSettings.getString(appName + "_scopeStorage", "")
        return if (!migrationStatus.isNullOrEmpty()) {
            migrationStatus
        } else {
            ""
        }
    }

    fun setScopeMigrationStatus(appName: String, migrationStatus: String?, settings: SharedPreferences) {
        val encryptedString: String = migrationStatus!!
        val editor = settings.edit()
        editor.putString(appName + "_scopeStorage", encryptedString)
        editor.apply()
    }

    fun getAppVersionCode(appName: String, settings: SharedPreferences): String {
        val appVersionCode = settings.getString(appName + "_versionCode", "")
        return if (!appVersionCode.isNullOrEmpty()) {
            appVersionCode
        } else {
            "0"
        }
    }

    fun setAppVersionCode(appName: String, versionCode: String, settings: SharedPreferences) {
        val encryptedString: String = versionCode
        val editor = settings.edit()
        editor.putString(appName + "_versionCode", encryptedString)
        editor.apply()
    }

    fun getIsMultiFactorRegistered(appName: String?, settings: SharedPreferences): String {

        val multifactorStatus = settings.getString(appName + "_multifactor", "")
        return if (!"".equals(multifactorStatus, ignoreCase = true)) {
            multifactorStatus!!
        } else {
            "false"
        }
    }

    fun setMultiFactorRegistered(appName: String, version: String?, settings: SharedPreferences) {
        val encryptedString: String = version!!
        val editor = settings.edit()
        editor.putString(appName + "_multifactor", encryptedString)
        editor.apply()
    }

    fun getIsNotificationRegistered(appName: String?, settings: SharedPreferences): String {
        val notificationStatus = settings.getString(appName + "_notification", "")
        return if (!"".equals(notificationStatus, ignoreCase = true)) {
            notificationStatus!!
        } else {
            "false"
        }
    }

    fun setIsNotificationRegistered(appName: String, status: String?, settings: SharedPreferences) {
        val encryptedString: String = status!!
        val editor = settings.edit()
        editor.putString(appName + "_notification", encryptedString)
        editor.apply()
    }


    fun getNotificationToken(appName: String?, settings: SharedPreferences): String? {
        return  settings.getString(appName + "_notificationtoken", "")
    }

    fun setNotificationToken(appName: String, refreshedToken: String?, settings: SharedPreferences) {
        val encryptedString: String = refreshedToken!!
        val editor = settings.edit()
        editor.putString(appName + "_notificationtoken", encryptedString)
        editor.apply()
    }

    fun getOSVersion(settings: SharedPreferences): String {
        val osVersion = settings.getString("osVersion", "")
        return if (!"".equals(osVersion, ignoreCase = true)) {
            osVersion!!
        } else {
            "4.4"
        }
    }

    fun setOSVersion(osVersion: String?, settings: SharedPreferences) {
        val encryptedString: String = osVersion!!
        val editor = settings.edit()
        editor.putString("osVersion", encryptedString)
        editor.apply()
    }


    fun setAppValue(appName: String, name: String, value: String?, settings: SharedPreferences?) {
        val encryptedString: String = value!!
        val editor = settings!!.edit()
        editor.putString(appName + "_" + name, encryptedString)
        editor.apply()
    }

    fun getAppValue(
        appName: String,
        valName: String,
        defaultVal: String,
        settings: SharedPreferences, ): String {

        val strValue = settings.getString(appName + "_" + valName, "")
        return if (!"".equals(strValue, ignoreCase = true)) {
            strValue!!
        } else {
            defaultVal
        }
    }

    // method to set main app launch END
    fun getIsAppFirstTime(aContext: Context, appName: String): String {
        val settings = EncryptedPrefHelper.getPrefs(aContext)
        var isFirstTime = settings.getString(appName + "_" + "isFirstTime", "")
        if (isFirstTime.isNullOrEmpty()) {
            isFirstTime = "YES"
            setIsAppFirstTime(appName, isFirstTime, settings)
        }
        return isFirstTime
    }

    fun setIsAppFirstTime(appName: String, isFirstTime: String, settings: SharedPreferences) {
        val editor = settings.edit()
        editor.putString(appName + "_" + "isFirstTime", isFirstTime)
        editor.apply()
    }

    fun isApplicationExpired(appName: String, settings: SharedPreferences): String {

        // encryption changes
        val expire = settings.getString(appName + "_expire", "")
        return if (!"".equals(expire, ignoreCase = true)) {
            expire!!
        } else {
            "false"
        }
    }

    fun setApplicationExpired(isExpire: String?, appName: String, settings: SharedPreferences) {
        val encryptedString: String = isExpire!!

        val editor = settings.edit()
        editor.putString(appName + "_expire", encryptedString)
        editor.apply()
    }


    /**
     * flag to check events default values are overridden or not
     * @param settings
     * @return
     */
    fun isDefaultEventsInitialized(settings: SharedPreferences): String {
        val events = settings.getString("eventsInitialized", "")
        return if (!"".equals(events, ignoreCase = true)) {
            events!!
        } else {
            "false"
        }
    }

    fun getAppVersion(appName: String, settings: SharedPreferences): String{
        return  settings.getString(appName + "_version", "0.0.0")!!
    }

    fun setAppVersion(appName: String,appVersion: String,settings: SharedPreferences){
        val editor = settings.edit()
        editor.putString(appName+"_version",appVersion)
        editor.apply()
    }
 }
