package com.iexceed.common;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.webkit.WebView;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.security.auth.x500.X500Principal;

import static com.iexceed.common.StringUtils.CRYPTO_ALGORITHM;
import static com.iexceed.common.StringUtils.RSA_CRYPTO_ALGORITHM;
import static com.iexceed.plugins.dataSecurity.StoreCredentialsSecurely.encryptString;

public class UserSettings extends ApzPlugin{

	private static String apzPrefs = "SecureData";
	private static String ALIAS = "spAlias";
	private static byte[] iv;

	private static String RANDOM_KEY_ENCRYPTED = "randonKeyEncrypted";


	private SharedPreferences settings;
	private static ApzPlugin pluginObj;
	
	public UserSettings(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  
		if(pluginObj == null){ 
		pluginObj = new UserSettings(webView, activity);
	}  
	return pluginObj; 
	}
	
	public static void setBase64Image(String base64Image, SharedPreferences settings){
		final SharedPreferences.Editor editor = settings.edit();
    	editor.putString("base64Image", base64Image);
    	editor.commit();
	}
	public static String getBase64Image(SharedPreferences settings){
		final String base64Image = settings.getString("base64Image", "");
		return base64Image;
	}

	public static void deleteBase64Image(SharedPreferences settings){
		final SharedPreferences.Editor editor = settings.edit();
		editor.remove("base64Image");
        editor.commit();
	}
	/**
	 * For checking application has expire or not
	 * @param appName
	 * @param settings
	 * @return
	 */
	/*starts*/
	public static String isApplicationExpired(String appName, SharedPreferences settings){
		/*final String expire = settings.getString(appName+"_expire", "false");
		return expire;*/  // encryption changes
		final String expire = settings.getString(appName+"_expire", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(expire))) {
			decryptedString = decStr(expire);
		} else{
			decryptedString = "false";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setApplicationExpired("true", appName, settings);
			decryptedString = expire;
		}
		return decryptedString;
	}
	
	public static void setApplicationExpired(String isExpire, String appName, SharedPreferences settings){
		/*final SharedPreferences.Editor editor = settings.edit();
    	editor.putString(appName+"_expire", isExpire);
    	editor.commit();*/
		String encryptedString = encStr(isExpire);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(appName+"_expire", encryptedString);
		editor.apply();
	}
	//Abhishek, bug id 5527, setting app expired on the basis of app name END
	/**
	 * flag to check events default values are overridden or not
	 * @param settings
	 * @return
	 */
	public static String isDefaultEventsInitialized(SharedPreferences settings){
		/*final String events = settings.getString("eventsInitialized", "false");
		return events;*/
		final String events = settings.getString("eventsInitialized", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(events))) {
			decryptedString = decStr(events);
		} else{
			decryptedString = "false";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setAppValue(AppzillonMainScreen.APP_NAME, "eventsInitialized", events, settings);
			decryptedString = events;
		}
		return decryptedString;
	}
    /*Ends*/


	public static String getIMEI(SharedPreferences settings){
		/*final String page = settings.getString("IMEI", "1111111111111111");
		return page;*/
		final String imei = settings.getString("IMEI", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(imei))) {
			decryptedString = decStr(imei);
		} else{
			decryptedString = "1111111111111111";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setIMEI(imei, settings);
			decryptedString = "";
		}
		return decryptedString;
	}

	public static void setIMEI(String IMEI, SharedPreferences settings){
		/*final SharedPreferences.Editor editor = settings.edit();
    	editor.putString("IMEI", IMEI);
    	editor.commit();*/
		String encryptedString = encStr(IMEI);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString("IMEI", encryptedString);
		editor.apply();
	}

	public static String getIMSI(SharedPreferences settings){
		/*final String page = settings.getString("IMSI", "111111111111111");
		return page;*/
		final String imsi = settings.getString("IMSI", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(imsi))) {
			decryptedString = decStr(imsi);
		} else{
			decryptedString = "1111111111111111";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setIMSI(imsi, settings);
			decryptedString = "";
		}
		return decryptedString;
	}

	public static void setIMSI(String IMSI, SharedPreferences settings){
		/*final SharedPreferences.Editor editor = settings.edit();
    	editor.putString("IMSI", IMSI);
    	editor.commit();*/
		String encryptedString = encStr(IMSI);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString("IMSI", encryptedString);
		editor.apply();
	}
	
	//Abhishek , bug id 5238 , Commented out START
//	public static String getIsFirstTime(SharedPreferences settings){
//		final String isFirstTime = settings.getString("isFirstTime", "YES");
//		return isFirstTime;
//	}
//
//	public static void setIsFirstTime(String isFirstTime, SharedPreferences settings){
//		final SharedPreferences.Editor editor = settings.edit();
//    	editor.putString("isFirstTime", isFirstTime);
//    	editor.commit();
//	}
	//Abhishek , bug id 5238 , Commented out START
	
	//Abhishek , 04 May 2015 , method to check main app launch START	
	public static String getIsMainAppFirstTime(SharedPreferences settings){
		/*final String isFirstTime = settings.getString("isFirstTime", "YES");
		return isFirstTime;*/
		final String isFirstTime = settings.getString("isFirstTime", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(isFirstTime))) {
			decryptedString = decStr(isFirstTime);
		} else{
			decryptedString = "YES";
		}
		if (decryptedString.equals("plain")) {
			//UserSettings.setIsMainAppFirstTime(isFirstTime, settings);
			encAllSharedPrefs();
			decryptedString = isFirstTime;
		}else if (decryptedString.equals("")){
			removeAllSharedPrefsData();
			decryptedString = "YES";
		}
		return decryptedString;
	}

	//Abhishek , 04 May 2015 , method to check main app launch END
	private static void encAllSharedPrefs() {
		//Log.e(TAG, "encAllSharedPrefs");
		String[] properties = {"USER_PREFS", "APP_PREFS"};
		try {
			for (String table : properties) {
				SharedPreferences settings = AppzillonMainScreen.activity.getSharedPreferences(table, 0);
				Map<String, ?> keys = settings.getAll();
				for (Map.Entry<String, ?> entry : keys.entrySet()) {
					try {

						String encryptedString = encStr(entry.getValue().toString());
						//Log.e(TAG, "encAllSharedPrefs  Key : " + entry.getKey() + " , PlainText : " + entry.getValue().toString() + " , encryptedString : " + encryptedString);
						final SharedPreferences.Editor editor = settings.edit();
						editor.putString(entry.getKey(), encryptedString);
						editor.apply();
					} catch (Exception e) {

					}
				}
			}

		} catch (Exception e) {

		}
	}

	private static void removeAllSharedPrefsData() {
		//Log.e(TAG, "removeAllSharedPrefsData");
		String[] properties = {"USER_PREFS", "APP_PREFS"};
		try {
			for (String table : properties) {
				SharedPreferences settings = AppzillonMainScreen.activity.getSharedPreferences(table, 0);
				Map<String, ?> keys = settings.getAll();
				for (Map.Entry<String, ?> entry : keys.entrySet()) {
					try {
						//Log.e(TAG, "removeAllSharedPrefsData  Key : " + entry.getKey());
						final SharedPreferences.Editor editor = settings.edit();
						editor.putString(entry.getKey(), "");
						editor.apply();
					} catch (Exception e) {
						//Log.e(TAG, "removeAllSharedPrefsData ERROR in saving Shared Prefs");
					}
				}
			}

		} catch (Exception e) {
			//Log.e(TAG, "removeAllSharedPrefsData ERROR "+e.getMessage());
		}
	}
	//Abhishek , 04 May 2015 , method to check main app launch END
	
	//Abhishek , 04 May 2015 , method to set main app launch START
	public static void setIsMainAppFirstTime(String isFirstTime, SharedPreferences settings){
		/*final SharedPreferences.Editor editor = settings.edit();
    	editor.putString("isFirstTime", isFirstTime);
    	editor.commit();*/
		String encryptedString = encStr(isFirstTime);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString("isFirstTime", encryptedString);
		editor.apply();
	}
	//Abhishek , 04 May 2015 , method to set main app launch END
	
	//Abhishek , 04 May 2015 , method to check child app launch START
	public static String getIsAppFirstTime(String appName,SharedPreferences settings){
		/*final String isFirstTime = settings.getString(appName+"_"+"isFirstTime", "YES");
		return isFirstTime;*/
		final String isFirstTime = settings.getString(appName+"_"+"isFirstTime", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(isFirstTime))) {
			decryptedString = decStr(isFirstTime);
		} else{
			decryptedString = "YES";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setIsAppFirstTime(appName, isFirstTime, settings);
			decryptedString = isFirstTime;
		}
		return decryptedString;
	}
	//Abhishek , 04 May 2015 , method to check child app launch END
	
	//Abhishek , 04 May 2015 , method to set child app launch START
	public static void setIsAppFirstTime(String appName,String isFirstTime, SharedPreferences settings){
		/*final SharedPreferences.Editor editor = settings.edit();
    	editor.putString(appName+"_"+"isFirstTime", isFirstTime);
    	editor.commit();*/
		String encryptedString = encStr(isFirstTime);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(appName+"_"+"isFirstTime", encryptedString);
		editor.apply();
	}
	//Abhishek , 04 May 2015 , method to set child app launch END
	
	public static String getAppVersion(String appName,SharedPreferences settings){
		/*final String appVersion = settings.getString(appName+"_version", "0.0.0");
		return appVersion;*/
		final String appVersion = settings.getString(appName+"_version", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(appVersion))) {
			decryptedString = decStr(appVersion);
		} else{
			decryptedString = "0.0.0";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setAppVersion(appName, appVersion, settings);
			decryptedString = appVersion;
		}
		return decryptedString;
	}

	public static void setAppVersion(String appName,String version, SharedPreferences settings){
		/*final SharedPreferences.Editor editor = settings.edit();
    	editor.putString(appName+"_version", version);
    	editor.commit();*/
		String encryptedString = encStr(version);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(appName+"_version", encryptedString);
		editor.apply();
	}
	
	public static String getIsMultiFactorRegistered(String appName,SharedPreferences settings){
		/*final String multifactorStatus = settings.getString(appName+"_multifactor", "false");
		return multifactorStatus;*/
		final String multifactorStatus = settings.getString(appName+"_multifactor", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(multifactorStatus))) {
			decryptedString = decStr(multifactorStatus);
		} else{
			decryptedString = "false";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setMultiFactorRegistered(appName, "true", settings);
			decryptedString = multifactorStatus;
		}
		return decryptedString;
	}

	public static void setMultiFactorRegistered(String appName,String version, SharedPreferences settings){
		/*final SharedPreferences.Editor editor = settings.edit();
    	editor.putString(appName+"_multifactor", version);
    	editor.commit();*/
		String encryptedString = encStr(version);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(appName+"_multifactor", encryptedString);
		editor.apply();
	}

	/*Natasha Dawra 5/6/2017
	Added getIsNotificationRegistered,setIsNotificationRegistered and
	getNotificationToken
	 */

	public static String getIsNotificationRegistered(String appName,SharedPreferences settings){
		/*final String notificationStatus = settings.getString(appName+"_notification", "false");
		return notificationStatus;*/
		final String notificationStatus = settings.getString(appName+"_notification", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(notificationStatus))) {
			decryptedString = decStr(notificationStatus);
		} else{
			decryptedString = "false";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setIsNotificationRegistered(appName, notificationStatus, settings);
			decryptedString = notificationStatus;
		}
		return decryptedString;
	}

	public static void setIsNotificationRegistered(String appName,String status, SharedPreferences settings){
		/*final SharedPreferences.Editor editor = settings.edit();
		editor.putString(appName+"_notification", status);
		editor.commit();*/
		String encryptedString = encStr(status);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(appName+"_notification", encryptedString);
		editor.apply();
	}


	public static String getNotificationToken(String appName,SharedPreferences settings){
		/*final String token = settings.getString(appName+"_notificationtoken", "");
		return token;*/
		final String token = settings.getString(appName+"_notificationtoken", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(token))) {
			decryptedString = decStr(token);
		} else{
			decryptedString = "";
		}
		if (decryptedString.equals("plain")) {
			decryptedString = token;
		}
		return decryptedString;
	}

	public static void setNotificationToken(String appName,String refreshedToken, SharedPreferences settings){
		/*final SharedPreferences.Editor editor = settings.edit();
    	editor.putString(appName+"_multifactor", version);
    	editor.commit();*/
		String encryptedString = encStr(refreshedToken);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(appName+"_notificationtoken", encryptedString);
		editor.apply();
	}

	//Abhishek , bug id 5238 , commented out ,for saving values of respective apps  START
	
//	public static void setValue(String name , String value , SharedPreferences settings) {
//    	final SharedPreferences.Editor editor = settings.edit();
//    	editor.putString(name, value);
//    	editor.commit();
//	}
//	
//	public static String getValue(String valname , String defaultval , SharedPreferences settings )	{
//		final String strvalue = settings.getString(valname, defaultval);		
//		return strvalue;
//	}
	
	//Abhishek , bug id 5238 , commented out ,for saving values of respective apps  END
	
	//Abhishek , bug id 5238 , Commented out  START
//	public static void setCommanValue(String name , String value , SharedPreferences settings) {
//	final SharedPreferences.Editor editor = settings.edit();
//	editor.putString(name, value);
//	editor.commit();
//}
	//Abhishek , bug id 5238 , Commented out  END
	
	//Abhishek , bug id 5238 , Commented out  START
//	public static String getCommanValue(String valname , String defaultval , SharedPreferences settings )	{
//	final String strvalue = settings.getString(valname, defaultval);		
//	return strvalue;
//}
	//Abhishek , bug id 5238 , Commented out  END
	
	//Abhishek , bug id 5238 , set values of respective apps  START
	public static void setAppValue(String appName,String name , String value , SharedPreferences settings) {
	/*final SharedPreferences.Editor editor = settings.edit();
	editor.putString(appName+"_"+name, value);
	editor.commit();*/
		String encryptedString = encStr(value);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(appName+"_"+name, encryptedString);
		editor.apply();
}
	//Abhishek , bug id 5238 , set values of respective apps  END
	
	//Abhishek , bug id 5238 , get values of respective apps  START
	public static String getAppValue(String appName,String valname , String defaultval , SharedPreferences settings )	{
	/*final String strvalue = settings.getString(appName+"_"+valname, defaultval);
	return strvalue;*/
		final String strvalue = settings.getString(appName+"_"+valname, "");
		String decryptedString;
		if(!("".equalsIgnoreCase(strvalue))) {
			decryptedString = decStr(strvalue);
		} else{
			if (null == defaultval)
				defaultval = "";
				decryptedString = defaultval;

		}
		if (decryptedString.equals("plain")) {
			UserSettings.setAppValue(appName, valname, strvalue, settings);
			decryptedString = strvalue;
		}
		return decryptedString;
}
	//Abhishek , bug id 5238 , get values of respective apps  END
	
	//Abhishek , bug id 5238 , set default settings of respective apps  START
	public static void setAppDefaultSettingPresent(String appName, String name , String value , SharedPreferences settings) {
    	/*final SharedPreferences.Editor editor = settings.edit();
    	editor.putString(appName+"_"+name, value);
    	editor.commit();*/
		String encryptedString = encStr(value);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(appName+"_"+name, encryptedString);
		editor.apply();
	}
	//Abhishek , bug id 5238 , set default settings of respective apps  END
	
	//Abhishek , bug id 5238 , get default settings of respective apps  START
	public static String getAppDefaultSettingPresent(String appName, String valname , String defaultval , SharedPreferences settings ){
		/*final String strvalue = settings.getString(appName+"_"+valname, defaultval);
		
		return strvalue;*/
		final String strvalue = settings.getString(appName+"_"+valname, "");
		String decryptedString;
		if(!("".equalsIgnoreCase(strvalue))) {
			decryptedString = decStr(strvalue);
		} else{
			if (null == defaultval)
				defaultval = "";
			decryptedString = defaultval;
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setAppDefaultSettingPresent(appName, valname, strvalue, settings);
			decryptedString = strvalue;
		}
		return decryptedString;
	}
	//Abhishek , bug id 5238 , get default settings of respective apps  END
	
	public static String getwiped(SharedPreferences settings) {
		/*final String isFirstTime = settings.getString("isWiped", "NO");
		return isFirstTime;*/
		final String wiped = settings.getString("isWiped", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(wiped))) {
			decryptedString = decStr(wiped);
		} else{
			decryptedString = "NO";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setwiped(wiped, settings);
			decryptedString = wiped;
		}
		return decryptedString;
	}

	public static void setwiped(String isWiped, SharedPreferences settings) {
		/*final SharedPreferences.Editor editor = settings.edit();
		editor.putString("isWiped", isWiped);
		editor.commit();*/
		String encryptedString = encStr(isWiped);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString("isWiped", encryptedString);
		editor.apply();
	}
	
	public static String getOSVersion(SharedPreferences settings) {
		/*final String osVersion = settings.getString("OSVersion", "4.4");
		return osVersion;*/
		final String osVersion = settings.getString("osVersion", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(osVersion))) {
			decryptedString = decStr(osVersion);
		} else{
			decryptedString = "4.4";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setOSVersion(osVersion, settings);
			decryptedString = osVersion;
		}
		return decryptedString;
	}
	
	public static void setOSVersion(String osVersion,SharedPreferences settings) {
		/*final SharedPreferences.Editor editor = settings.edit();
		editor.putString("OSVersion", osVersion);
		editor.commit();*/
		String encryptedString = encStr(osVersion);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString("osVersion", encryptedString);
		editor.apply();
	}
	
	public static String getDeviceId(SharedPreferences settings) {
		/*final String osVersion = settings.getString("deviceId", null);
		return osVersion;*/
		final String deviceId = settings.getString("deviceId", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(deviceId))) {
			decryptedString = decStr(deviceId);
		} else{
			decryptedString = "";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setDeviceId(deviceId, settings);
			decryptedString = deviceId;
		}
		return decryptedString;
	}
	
	public static void setDeviceId(String deviceId,SharedPreferences settings) {
		/*final SharedPreferences.Editor editor = settings.edit();
		editor.putString("deviceId", deviceId);
		editor.commit();*/
		String encryptedString = encStr(deviceId);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString("deviceId", encryptedString);
		editor.apply();
	}
	
	public void setSettingsValue(final JSONObject jsonResult) {
		settings = activity.getSharedPreferences(properties, 0);
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
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
		ApzPluginUtil.sendSuccess(callbackId,null,false, activity, webView,true);
	}

	private static String decryptKey(String textToDecrypt){
		String dStr = null;
		String alias = ALIAS;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			KeyStore keyStore = null;
			byte[] bytes = Base64.decode(textToDecrypt, Base64.NO_WRAP);
			try {
				keyStore = KeyStore.getInstance("AndroidKeyStore");
				keyStore.load(null);
				final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null);

				final SecretKey secretKey = secretKeyEntry.getSecretKey();

				final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
				SharedPreferences sharedPreferences = AppzillonMainScreen.activity.getSharedPreferences(apzPrefs, AppzillonMainScreen.activity.MODE_PRIVATE);
				String ivStr = sharedPreferences.getString("iv_sp_key", "");
				iv = Base64.decode(ivStr, Base64.NO_WRAP);

				final GCMParameterSpec spec = new GCMParameterSpec(128, iv);
				cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

				final byte[] decodedData = cipher.doFinal(bytes);
				dStr = new String(decodedData, "UTF-8");
			} catch (UnrecoverableEntryException | NoSuchPaddingException | CertificateException | InvalidKeyException | IllegalBlockSizeException | KeyStoreException | NoSuchAlgorithmException | IOException | InvalidAlgorithmParameterException e) {
//				e.printStackTrace();
			} catch (BadPaddingException e) {
//				e.printStackTrace();
			}
		} else {
			//dStr = decryptString(textToDecrypt, "credentials");
			LollipopEncDec encDec = new LollipopEncDec();
			encDec.createNewKeys();
			dStr=encDec.decryptString(textToDecrypt);
		}
		return dStr;
	}

	private static String encryptKey(String textToEncrypt) {
		String alias = ALIAS;
		String eStr = null;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

			byte[] encryptionbytes = null;
			try {
				final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(alias,
							KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
							.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
							.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
							.build();

					keyGenerator.init(keyGenParameterSpec);
					final SecretKey secretKey = keyGenerator.generateKey();

					final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

					cipher.init(Cipher.ENCRYPT_MODE, secretKey);
					iv = cipher.getIV();
					SharedPreferences sharedPreferences = AppzillonMainScreen.activity.getSharedPreferences(apzPrefs, AppzillonMainScreen.activity.MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("iv_sp_key", Base64.encodeToString(iv, Base64.NO_WRAP));
					editor.apply();

					encryptionbytes = cipher.doFinal(textToEncrypt.getBytes("UTF-8"));
					eStr = Base64.encodeToString(encryptionbytes, Base64.NO_WRAP);

				}


			} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				//Log.e("StoreSecurely", e.getMessage());
			} catch (UnsupportedEncodingException e) {
				//e.printStackTrace();
			}


		} else {
//			createNewKeys("credentials");
			eStr = encryptString(textToEncrypt, alias);
		}
		return eStr;
	}

	public static void storeRandomKey(String randomKey) {
		Activity activity = AppzillonMainScreen.activity;
		String eKey = encryptKey(randomKey);
		SharedPreferences sharedPreferences = activity.getSharedPreferences(apzPrefs, activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(RANDOM_KEY_ENCRYPTED, eKey);
		editor.apply();
	}

	// Abhishek changes for update issue
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

	public static String retrieveRandomKey() {
		//Log.e(TAG, "retrieveRandomKey");
		Activity activity = AppzillonMainScreen.activity;
		String dKey = AppzillonMainScreen.DECRYPTED_KEY;
		//Log.e(TAG, "retrieveRandomKey dKey : " + dKey);
		if (dKey.equalsIgnoreCase("")) {
			SharedPreferences sharedPreferences = activity.getSharedPreferences(apzPrefs, activity.MODE_PRIVATE);
			String eKey = sharedPreferences.getString(RANDOM_KEY_ENCRYPTED, "");
			//Log.e(TAG, "retrieveRandomKey eKey : " + eKey);
			if (eKey.equalsIgnoreCase("")) {
				//Log.e(TAG, "Create New Key");
				SecureRandom generator = new SecureRandom();
				StringBuilder randomStringBuilder = new StringBuilder();
				int randomLength = generator.nextInt(16);
				char tempChar;
				for (int i = 0; i < randomLength; i++) {
					tempChar = (char) (generator.nextInt(96) + 32);
					randomStringBuilder.append(tempChar);
				}

				dKey = randomStringBuilder.toString();
				//Log.e(TAG, "Newly Created key : " + dKey);
				storeRandomKey(dKey);

			} else {
				//Log.e(TAG, "encrypt exisitng key " + eKey);
				dKey = decryptKey(eKey);
				//Log.e(TAG, "decrypted Key : " + dKey);
			}
			AppzillonMainScreen.DECRYPTED_KEY = dKey;
		}
		return dKey;

	}

	// encryption method
	public  static String encStr(String clearText) {
String encrypted = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String key = getKey();
            encrypted = AppzillonUtils.encyyptString( key, clearText);
        }else{
            LollipopEncDec encDec = new LollipopEncDec();
            encDec.createNewKeys();
            encrypted=encDec.encryptString(clearText);
        }
        //Log.e(TAG, "Encrypted : " + clearText + " , to : " + encrypted);
        return encrypted;
	}

	// decryption method
	public  static String decStr(String textToDecrypt) {
		String decText = "";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			String key = getKey();
			decText = AppzillonUtils.decyyptString( key, textToDecrypt);
		}else{
			LollipopEncDec encDec = new LollipopEncDec();
			encDec.createNewKeys();
			decText = encDec.decryptString(textToDecrypt);
		}
		//Log.e(TAG, "textToDecrypt : " + textToDecrypt + ", Decrypted Text : " + decText);
		return decText;
	}

	public static String getKey() {
		String paddingMask = "$$$$$$$$$$$$$$$$";
		String key = retrieveRandomKey();
		if (key.length() <= 16) {
			key += paddingMask.substring(0, 16 - key.length());
		}
		else {
			key = key.substring(0, 16);
		}
		return key;
	}
	@Override
	public void execute(JSONObject params) {
		String action = "";
		try {
			action = params.getString("action");
			this.callbackId = params.getString("id");
			if(action.equals("SETSETTINGS")){
				setSettingsValue(params);
			}
		} catch (Exception e) {
			
		}

		
	}

	private static class LollipopEncDec{

		private KeyStore keyStore;
		private String alias = ALIAS;
		private Activity activity = AppzillonMainScreen.activity;

		public LollipopEncDec(){
			try {
				keyStore = KeyStore.getInstance("AndroidKeyStore");
				keyStore.load(null);
			}
			catch(Exception e) {}
		}

		public void createNewKeys() {

			try {
				// Create new key if needed
				if (!keyStore.containsAlias(alias)) {
					Calendar start = Calendar.getInstance();
					Calendar end = Calendar.getInstance();
					end.add(Calendar.YEAR, 1);
					KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(activity)
							.setAlias(alias)
							.setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
							.setSerialNumber(BigInteger.ONE)
							.setStartDate(start.getTime())
							.setEndDate(end.getTime())
							.build();
					KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
					generator.initialize(spec);

					KeyPair keyPair = generator.generateKeyPair();
				}
			} catch (Exception e) {
				//Log.e(TAG, Log.getStackTraceString(e));
			}
		}

		public String encryptString(String plainText) {
			//Log.e(TAG, "LollipopEncDec encryptString : "+plainText);
			String finalString = "";
			try {
				KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
				RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

				String initialText = plainText;
				if(initialText.isEmpty()) {
					return finalString;
				}

				Cipher inCipher = Cipher.getInstance(RSA_CRYPTO_ALGORITHM, "AndroidOpenSSL");
				inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inCipher);
				cipherOutputStream.write(initialText.getBytes("UTF-8"));
				cipherOutputStream.close();

				byte [] vals = outputStream.toByteArray();
				finalString = Base64.encodeToString(vals, Base64.DEFAULT);
			} catch (Exception e) {
				//Log.e(TAG, Log.getStackTraceString(e));
			}
			return finalString;
		}

		public String decryptString(String encryptedText) {
			//Log.e(TAG, "LollipopEncDec decryptDtring");
			String finalText = "";
			try {
				KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
				RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
				Cipher output = Cipher.getInstance(RSA_CRYPTO_ALGORITHM, "AndroidOpenSSL");
				output.init(Cipher.DECRYPT_MODE, privateKey);

				String cipherText = encryptedText;
				CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
				ArrayList<Byte> values = new ArrayList<>();
				int nextByte;
				while ((nextByte = cipherInputStream.read()) != -1) {
					values.add((byte)nextByte);
				}
				byte[] bytes = new byte[values.size()];
				for(int i = 0; i < bytes.length; i++) {
					bytes[i] = values.get(i).byteValue();
				}
				finalText = new String(bytes, 0, bytes.length, "UTF-8");
				if(finalText.length()==0){
					if(encryptedText.length()< 23){
						return "plain";
					}else{
						return "";
					}
				}
				//Log.e(TAG, "LollipopEncDec decryptDtring finalText "+finalText+" , length : "+finalText.length());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				//Log.e(TAG, "LollipopEncDec decryptDtring IllegalArgumentException");
			}catch (Exception e) {
				//Log.e(TAG, Log.getStackTraceString(e));
			}
			//Log.e(TAG, "LollipopEncDec decryptDtring encryptedText : "+encryptedText+", decrypted : "+finalText);
			return finalText;
		}
	}

	public static String getAppVersionCode(String appName,SharedPreferences settings){
		final String appVersionCode = settings.getString(appName+"_versionCode", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(appVersionCode))) {
			decryptedString = decStr(appVersionCode);
		} else{
			decryptedString = "0";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setAppVersionCode(appName, appVersionCode, settings);
			decryptedString = appVersionCode;
		}
		return decryptedString;
	}

	public static void setAppVersionCode(String appName,String versionCode, SharedPreferences settings){
		String encryptedString = encStr(versionCode);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(appName+"_versionCode", encryptedString);
		editor.apply();
	}

	public static String getScopeMigrationStatus(String appName,SharedPreferences settings){
		final String migrationStatus = settings.getString(appName+"_scopeStorage", "");
		String decryptedString;
		if(!("".equalsIgnoreCase(migrationStatus))) {
			decryptedString = decStr(migrationStatus);
		} else{
			decryptedString = "";
		}
		if (decryptedString.equals("plain")) {
			UserSettings.setScopeMigrationStatus(appName, migrationStatus, settings);
			decryptedString = migrationStatus;
		}
		return decryptedString;
	}

	public static void setScopeMigrationStatus(String appName,String migrationStatus, SharedPreferences settings){
		String encryptedString = encStr(migrationStatus);
		final SharedPreferences.Editor editor = settings.edit();
		editor.putString(appName+"_scopeStorage", encryptedString);
		editor.apply();
	}
	
}
