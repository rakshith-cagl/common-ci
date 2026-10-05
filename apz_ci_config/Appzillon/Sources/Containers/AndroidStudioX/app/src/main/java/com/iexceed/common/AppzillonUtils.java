package com.iexceed.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.encryption.ApzEncryptionPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.apache.http.conn.util.InetAddressUtils;
import com.scottyab.rootbeer.RootBeer;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Paths;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.content.pm.Signature;
import java.security.MessageDigest;

import static com.iexceed.common.StringUtils.CRYPTO_ALGORITHM;

public class AppzillonUtils {

	private static SharedPreferences apps;

	final static String app_props = "APP_PREFS";

	static String TAG = "AppzillonUtils";

	private static boolean IS_DEVICE_LOG_ENABLED = true;

	private final static char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private final static String encryptionKeyForServerValue = "APPZILLONDECRYPT";
	private static final int REQUEST_DEVICEINFO = 0;

	public static String getDeviceId(Context mContext) {
		apps = mContext.getSharedPreferences(app_props, 0);
		String deviceId = UserSettings.getDeviceId(apps);

		if (deviceId == "") {
			deviceId = UUID.randomUUID().toString();
			UserSettings.setDeviceId(deviceId, apps);
		}

		return deviceId;
	}

	public static String getOsDetails(Activity mActivity) {
		DisplayMetrics display = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(display);
		String versionNumber = Build.VERSION.RELEASE; // Build.VERSION.SDK_INT;
		return versionNumber;
	}

	public static String getCurrentAppStoreVersion(Activity mActivity) {
		String versionNumber = ""; // Build.VERSION.SDK_INT;
		try {
			versionNumber = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionNumber;
	}

	public static String getPhoneNumber(Context mContext) {
		String phoneNum = "";

		TelephonyManager telMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		int simState = telMgr.getSimState();
		switch (simState) {
			case TelephonyManager.SIM_STATE_ABSENT:
				phoneNum = "ABSENT";
				break;
			case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
				phoneNum = "LOCKED";
				break;
			case TelephonyManager.SIM_STATE_PIN_REQUIRED:
				phoneNum = "PIN_REQ";
				break;
			case TelephonyManager.SIM_STATE_PUK_REQUIRED:
				phoneNum = "PUK_REQ";
				break;
			case TelephonyManager.SIM_STATE_READY:
				phoneNum = telMgr.getLine1Number();
				break;
			case TelephonyManager.SIM_STATE_UNKNOWN:
				phoneNum = "UNKNOWN";
				break;
		}

		if (phoneNum == null) {
			phoneNum = "UNSHARED";
		}
		return phoneNum;
	}

	/**
	 * Returns device model name like HTC Desire C
	 *
	 * @return
	 */
	public static String getDeviceType() {
		String devType = Build.MODEL;
		return devType;
	}

	public static String getDeviceMake() {
		return Build.MANUFACTURER;
	}

	public static boolean isTablet(Activity mActivity) {
		DisplayMetrics display = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(display);

		if (display.widthPixels > 1023 || display.heightPixels > 1023) {
			return true;
		} else {
			return false;
		}
	}

	// Abhishek, 21 May 2015 Moved method from AppzillonMainScreen to here START
	public static boolean isAppExpired(Context context, String appName) {
		boolean appExpired = false;
		String properties = "USER_PREFS";
		SharedPreferences settings = context.getSharedPreferences(properties, 0);
		if ("false".equalsIgnoreCase(UserSettings.isApplicationExpired(appName,
				settings))) {
			//StringUtils stringUtils = new StringUtils(context,AppzillonMainScreen.ASSETS_MAIN_FOLDER + File.separator+ appName);
			try {

				Date cur_date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat(StringUtils.getString(StringUtils.EXPIRY_DATE_FORMAT));
				Date currDate = sdf.parse(sdf.format(cur_date));
				String expdate = StringUtils.getString(StringUtils.EXPIRY_DATE);

				Date expirydate = sdf.parse(expdate);
				if (currDate.compareTo(expirydate) > 0) {
					appExpired = true;
				}

			} catch (ParseException ex) {

			} catch (NullPointerException e) {

			} catch (IllegalArgumentException ex) {

			}
		} else {
			appExpired = true;
		}
		return appExpired;
	}

	// Abhishek, 21 May 2015 Moved method from AppzillonMainScreen to here END

	public static boolean isNFCSupported(Context context) {
		return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC);
	}

	public static String getScreenSize(Activity mActivity) {
		DisplayMetrics display = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(display);
		int width = display.widthPixels;
		int height = display.heightPixels;
		return String.valueOf(width) + "X" + String.valueOf(height);

	}

	public static String getPPI(Activity mActivity) {
		DisplayMetrics display = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(display);
		int density = display.densityDpi;
		return Integer.toString(density);
	}

	// Used to save the message in the form of log in the device
	public static void writeLogsInDevice(String type, String data) {
		if (IS_DEVICE_LOG_ENABLED) {
			try {

				File myFile = new File(AppzillonMainScreen.SANDBOX_LOC+ File.separator + ".abhi");

				Date d = new Date();
				String time = d.getHours() + "::" + d.getMinutes() + "::"+ d.getSeconds();

				Writer out = new BufferedWriter(new FileWriter(myFile, true),1024);
				out.write(type + " " + time + " ~~~ " + data + "\n");
				out.close();
			} catch (IOException e) {
				//Log.e("Exception", "File write failed: " + e.toString());
			}
		}

	}

	private static boolean checkRootMethod1() {
		String buildTags = Build.TAGS;
		return buildTags != null && buildTags.contains("test-keys");
	}

	private static boolean checkRootMethod2() {
		String[] paths = { "/system/app/Superuser.apk", "/sbin/su",
				"/system/bin/su", "/system/xbin/su", "/data/local/xbin/su",
				"/data/local/bin/su", "/system/sd/xbin/su",
				"/system/bin/failsafe/su", "/data/local/su" };
		for (String path : paths) {
			if (new File(path).exists())
				return true;
		}
		return false;
	}

	private static boolean checkRootMethod3() {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			if (in.readLine() != null)
				return true;
			return false;
		} catch (Throwable t) {
			return false;
		} finally {
			if (process != null)
				process.destroy();
		}
	}

	public static boolean isDeviceRooted(Context context) {

		RootBeer rootBeer = new RootBeer(context);
		boolean rooted = false;
		if (rootBeer.isRooted() || rootBeer.detectRootCloakingApps()) {
			//if (rootBeer.isRootedWithoutBusyBoxCheck() || rootBeer.detectRootCloakingApps()) {
				rooted = true;
				Toast.makeText(context,"Rooted device, cant Launch App.", Toast.LENGTH_SHORT).show();
			//}
		}
		return rooted;
	}

	public static String ipAddress(Context mContext) {
		boolean WIFI = false;
		boolean MOBILE = false;

		ConnectivityManager CM = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] networkInfo = CM.getAllNetworkInfo();
		for (NetworkInfo netInfo : networkInfo) {
			if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
				if (netInfo.isConnected())
					WIFI = true;

			if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))

				if (netInfo.isConnected())

					MOBILE = true;
		}
		if(WIFI){
			WifiManager wm = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
			return ip;
		} if(MOBILE){
			try {
				List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
				for (NetworkInterface networkInterface : networkInterfaces) {
					List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
					for (InetAddress inetAddress : inetAddresses) {
						if (!inetAddress.isLoopbackAddress()) {
							String sAddr = inetAddress.getHostAddress().toUpperCase();
							boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
							// if (useIPv4) {
							if (isIPv4)
								return sAddr;
			                   /* } else {
			                        if (!isIPv4) {
			                            // drop ip6 port suffix
			                            int delim = sAddr.indexOf('%');
			                            return delim < 0 ? sAddr : sAddr.substring(0, delim);
			                        }
			                    }*/
						}
					}
				}
			} catch (Exception ex) {

			}

		}
		return null;
	}

	public static boolean isEmulator() { // 3.2 changes
		return Build.FINGERPRINT.contains("generic")|| Build.PRODUCT.contains("sdk");
	}

	public static boolean isNetworkAvailable(Activity activity) {

		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		// if no network is available networkInfo will be null otherwise check
		// if we are connected
		if (networkInfo != null && networkInfo.isConnected()) {
			//Log.e("Network Testing", "***Available***");
			return true;
		}
		//Log.e("Network Testing", "***Not Available***");
		return false;
	}

	// 3.2 changes
	public static JSONObject getDeviceRunTimeInfo(SharedPreferences settings,Activity activity, WebView webview) {
		// 3.2 changes
		final JSONObject obj = new JSONObject();
		String deviceGroup = getDeviceValue(activity);
		String[] deviceInfo = deviceGroup.split(",");
		try {
			// obj.put("DEVICEID", UserSettings.getIMEI(settings));
			// obj.put("DEVICETYPE", "ANDROID");
			// obj.put("SCREENSIZE", AppzillonUtils.getScreenSize(activity));
			// obj.put("SCREENPPI", AppzillonUtils.getPPI(activity)); // 3.2
			// // changes
			// obj.put("OTAREQUIRED", AppzillonMainScreen.OTAREQUIRED);
			// obj.put("HASHKEY2", UserSettings.getIMSI(settings));

			// prabudas 3.2 changes
			if(AppzillonMainScreen.IS_SERVER){
				obj.put("deviceId", AppzillonUtils.getDeviceId(activity));
			}
//			obj.put("deviceId", AppzillonUtils.getDeviceId(activity));
			obj.put("deviceOs", "ANDROID");
			obj.put("deviceType", "ANDROID");
			obj.put("screenSize", AppzillonUtils.getScreenSize(activity));
			obj.put("screenPpi", AppzillonUtils.getPPI(activity)); // 3.2 changes
			obj.put("deviceGroup", deviceInfo[0]);
			obj.put("orientation", deviceInfo[1]);
			obj.put("lockRotation", deviceInfo[2]);
		} catch (final JSONException e) {
			//Log.e(TAG, "Exception JSONObject : " + e.toString());
		}
		return obj;
	}

	/* To encrypt ptext with salt using SHA-256 hash Algo */
	public static String hashSHA256(String ptext, String psalt)	throws NoSuchAlgorithmException {
		String pTextSalt = ptext + psalt;
		String pHashedText = "";
		byte[] ptextSaltbyte = new byte[200];
		byte[] hashbyte = new byte[200];
		MessageDigest msgdigest = MessageDigest.getInstance("SHA-256");
		try {
			ptextSaltbyte = pTextSalt.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			ApzLogger.e(TAG, "Unsupported character set");
		}
		msgdigest.reset();
		msgdigest.update(ptextSaltbyte);
		hashbyte = msgdigest.digest();
		pHashedText = toHexString(hashbyte);
		//ApzLogger.i(TAG, "EncryptHash : " + pHashedText);
		return pHashedText;
	}

	/**
	 * Used to return device type
	 *
	 * @return
	 */

	public static String toHexString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			int c = ((b[i]) >>> 4) & 0xf;
			sb.append(hex[c]);
			c = (b[i] & 0xf);
			sb.append(hex[c]);
		}
		return sb.toString();
	}

	public final static JSONObject getAllSettingsValue(String jsonNodeVal,SharedPreferences settings) {
		String defaultval = "";
		JSONObject json = null;
		StringBuilder sb = new StringBuilder();
		// settings = activity.getSharedPreferences(properties, 0);
		sb.append("{");
		int i = 0;
		try {
			JSONObject settingjson = new JSONObject(jsonNodeVal);
			Iterator<String> settingskey = settingjson.keys();
			while (settingskey.hasNext()) {
				String name = settingskey.next();
				String str = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME, name, defaultval,settings);
				sb.append("\"" + name + "\"");
				sb.append(":");
				sb.append("\"" + str + "\"");
				i++;
				if (i < settingjson.length()) {
					sb.append(",");
				}
			}
			sb.append("}");
			json = new JSONObject(sb.toString());
		} catch (Exception e) {

		}
		return json;
	}

	public static File getApzFile(String path, String exten)
	{
		return new File(validatePath(path, exten));
	}

	/**
	 * <pre>
	 * Paths.get(path) Checks if a string is a valid path.
	 * Null safe.
	 *
	 * Calling examples:
	 *    validatePath("c:/test");      //returns true
	 *    validatePath("c:/te:t");      //returns false
	 *    validatePath("c:/te?t");      //returns false
	 *    validatePath("c/te*t");       //returns false
	 *    validatePath("good.txt");     //returns true
	 *    validatePath("not|good.txt"); //returns false
	 *    validatePath("not:good.txt"); //returns false
	 * </pre>
	 */
	public static String validatePath(String path, String ext) {
		String extension = "";
		String fullpth = path;
		
		String FILE_DIR = AppzillonMainScreen.activity.getExternalFilesDir(null).getAbsolutePath();

		FILE_DIR= FILE_DIR.substring(0, FILE_DIR.lastIndexOf("/Android"));

		if(ext == null){
			if(path.startsWith(AppzillonMainScreen.SANDBOX_LOC)||path.startsWith(AppzillonMainScreen.ASSET_APP_LOC)||path.startsWith(FILE_DIR)){
			//Matches criteria
			}else{
				if(path.endsWith("AppzillonRingtone")){
				//Matches for Ringtone
				}else{
					return "";
				}
			}
		}

		if(ext != null) {
			String[] allowedExtensions = new String[]{"jpg","gif","png","json","jpeg","mp4"};
			for (String allowedExtension: allowedExtensions) {
				if (allowedExtension.equals(ext)) {
					extension = ext;
					break;
				}
			}
		}

		if(extension != "") {
			fullpth = fullpth + "." + extension;
		}

		try {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
				Paths.get(path);
			}
		} catch (Exception ex) {
			fullpth = "";
		}

		if(fullpth.contains("../")){
			return "";
		}
		return fullpth;
	}


	@SuppressWarnings("resource")
	private static String getDeviceValue(Activity activity) {

		DisplayMetrics metrics = activity.getBaseContext().getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		// Natasha changes for orientation,16-10-2015
		float dpi = metrics.densityDpi;
		int n = Math.round(dpi / 160);
		width = width / n;
		height = height / n;
		AssetManager manager = activity.getBaseContext().getAssets();
		JSONArray devicegrp;
		String orientation = null;
		String devname = null;
		int count = 0;
		int temp = 0;
		try {
			// Abhishek For OTA
			InputStream is;
			if ((AppzillonMainScreen.OTAREQUIRED).equalsIgnoreCase("Y")) {
				//File jsonFile = new File(buildValidAvatarPath(12314,"json"));
//				File jsonFile = new File(AppzillonMainScreen.SANDBOX_LOC+ File.separator + AppzillonMainScreen.ASSET_APP_LOC+ "screens/config/devicegroups.json");
				File jsonFile = getApzFile(AppzillonMainScreen.SANDBOX_LOC+ File.separator + AppzillonMainScreen.ASSET_APP_LOC+ "screens/config/devicegroups","json");
				//create file()
				is = new FileInputStream(jsonFile);
			} else {
//				is = manager.open(AppzillonMainScreen.ASSET_APP_LOC+ "screens/config/devicegroups.json");
				is = manager.open(validatePath(AppzillonMainScreen.ASSET_APP_LOC+ "screens/config/devicegroups","json"));
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			try {
				devicegrp = new JSONObject(out.toString()).getJSONArray("deviceGroups");
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
				return "";
			} finally {
				is.close();
			}
		} catch (IOException e1) {

		}

		if (!devname.equalsIgnoreCase("") && !orientation.equalsIgnoreCase("")) {
			boolean lockRotation = true;
			if (orientation.equalsIgnoreCase("ANY")) {
				lockRotation = false;
				int rotationInt = activity.getResources().getConfiguration().orientation;
				if (rotationInt == 1) {
					orientation = "PORTRAIT";
				} else {
					orientation = "LANDSCAPE";
				}
			}
//Natasha's changes 6-07-2017 for 18130 orientation issue
			if(lockRotation == true) {
				if (orientation.equalsIgnoreCase("LANDSCAPE")) {
					activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
				} else if (orientation.equalsIgnoreCase("PORTRAIT")) {
					activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
				} else {
					activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
				}
			}
			return devname + "," + orientation + "," + lockRotation;
		} else
			return "";

	}


	public static byte[] getIV(String key) {
		byte[] iv = new byte[16];
		java.util.Arrays.fill(iv, (byte) 0);
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

	public static String getSalt(String key) {
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

	public static String getAppVersion(Activity activity, JSONObject params) {
		String appName = null;
		try {
			appName = params.getString("appId");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		String appVersion = null;
		SharedPreferences apps = activity.getApplicationContext().getSharedPreferences(AppzillonMainScreen.app_props, 0);
		appVersion = UserSettings.getAppVersion(appName, apps);
		//ApzLogger.i(TAG, "currentVersion : " + appVersion);
		// Abhishek Bug id 4439 START
		if (appVersion.equalsIgnoreCase("0.0.0")) {
			// Abhishek Bug id 4439 END
			StringUtils stringUtils = new StringUtils(activity.getApplicationContext(),	AppzillonMainScreen.ASSETS_MAIN_FOLDER + File.separator	+ appName);
			appVersion = StringUtils.getString(StringUtils.APP_VERSION);
		}
		return appVersion;
	}

	public static void closeApplication(Activity activity) {
		ApzLogger.i(TAG, " closeApplication ");
		// Abhishek,13 August 2015,Bug id 6147,Commented out On Back press all
		// the preferences data is been cleared,Reverted Back to 3.0 START
		// settings = context.getSharedPreferences(properties, 0);
		// Editor editor = settings.edit();
		// editor.clear();
		// editor.commit();
		// Abhishek,13 August 2015,Bug id 6147,Commented out On Back press all
		// the preferences data is been cleared,Reverted Back to 3.0 END
		activity.finish();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	public static boolean setSetting(Activity activity, JSONObject params) {
		String properties = "USER_PREFS";
		String key = null;
		String value = null;
		boolean flag = false;

		SharedPreferences settings = activity.getSharedPreferences(properties,
				0);
		try {
			key = params.getString("key");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block

		}
		try {
			value = params.getString("value");
		} catch (JSONException e1) {

		}
		try{
			UserSettings.setAppValue(AppzillonMainScreen.APP_NAME, key, value, settings);
			flag = true;
		}catch(Exception e1){
			flag = false;
		}
		//    Iterator<String> iterator = params.keys();
        /*while (iterator.hasNext()) {

            try {
                key = (String) iterator.next();
                value = params.getString(key);
            UserSettings.setAppValue(AppzillonMainScreen.APP_NAME, key, value,
                    settings); // 3.2 changes
            flag = true;
            } catch (JSONException e) {

        }
        }*/

		return flag;
	}

	public static String getSetting(ApzActivity activity, JSONObject params) {
		String defaultval = "";
		String properties = "USER_PREFS";
		String str = null;
		String key = null;
		SharedPreferences settings = activity.getSharedPreferences(properties,0);
		try {
			key = params.getString("key");
		} catch (JSONException e) {
			// TODO Auto-generated catch block

		}
		String value = null;
		str = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,key, value , settings);
		//    Iterator<String> iterator = params.keys();
		//while (iterator.hasNext()) {
		//key = (String) iterator.next();
		//str = UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,key, defaultval, settings);
		//}

		return str;
	}

	public static boolean isRunningOnEmulator() {
		boolean result=
				Build.FINGERPRINT.startsWith("generic")
						||Build.FINGERPRINT.startsWith("unknown")//
						||Build.MODEL.contains("google_sdk")//
						||Build.MODEL.contains("Emulator")//
						||Build.MANUFACTURER.contains("Genymotion")
						||Build.MANUFACTURER.equals("unknown")
						||Build.MODEL.contains("Android SDK built for x86");
		if(result)
			return true;
		result|=Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic");
		if(result)
			return true;
		result|="google_sdk".equals(Build.PRODUCT);
		return result;
	}

	public static String getDecryptedValue(String serverValue){
		String decryptedServerValue = ApzEncryptionPlugin.decryptPassword(encryptionKeyForServerValue,serverValue);
		return decryptedServerValue;
	}

	// encryption method
	public  static String encyyptString(String key,String clearText) {
		byte[] iv = getIV(key);
		String salt = getSalt(key);
		SecretKeySpec skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");

		try {
			SecureRandom random = new SecureRandom();
			Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, parameterSpec);

			byte[] encryptedData = cipher.doFinal(clearText.getBytes("UTF-8"));
			if (encryptedData == null)
				return null;
			return Base64.encodeToString(encryptedData,Base64.NO_WRAP );
		} catch (Exception e) {

		}
		return null;
	}


	public static String decyyptString( String key, String textToDecrypt) {
		byte[] iv = getIV(key);
		String salt = getSalt(key);
		return decryptString(key,textToDecrypt,salt,iv);
	}

/*	// decrypt
	private static String decryptString(String cypher, String key, String textToDecrypt, String salt, byte[] iv) {
		SecretKeySpec skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");
		try {
			Cipher cipher = Cipher.getInstance(cypher);
			IvParameterSpec ivParams = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParams);
			byte[] plaintext = cipher.doFinal(Base64.decode(textToDecrypt, Base64.NO_WRAP | Base64.NO_PADDING));
			String plainrStr = new String(plaintext, "UTF-8");
			if (plainrStr == null)
				return null;

			return new String(plainrStr);
		} catch (Exception e) {
		}
		return null;
	}*/

	//decrypt
	private static String decryptString( String key, String textToDecrypt, String salt, byte[] iv) {

		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M && textToDecrypt.length()<23){
			return "plain";
		}

		SecretKeySpec skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");
		try {
			final Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, parameterSpec);
			byte[] plaintext = cipher.doFinal(Base64.decode(textToDecrypt, Base64.NO_WRAP | Base64.NO_PADDING));

			String plainrStr = new String(plaintext, "UTF-8");
			if (plainrStr.equals("")) {
				return "";
			}

			return new String(plainrStr);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return "plain";
		}catch (javax.crypto.IllegalBlockSizeException e){
			e.printStackTrace();
			return "plain";
		}catch (Exception e){
			e.printStackTrace();
		}
		return "";
	}

	public static byte[] hmacSha1(String salt, String key) {
		SecretKeyFactory factory = null;
		Key keyByte = null;
			int keyLength=0;
		try {
			if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
				factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				keyLength =128;

			}else{
				factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
				keyLength =256;
			}
			KeySpec keyspec = new PBEKeySpec(key.toCharArray(),salt.getBytes("UTF-8"), 2, keyLength);
			keyByte = factory.generateSecret(keyspec);
		} catch (NoSuchAlgorithmException e) {

		} catch (InvalidKeySpecException e) {

		} catch (UnsupportedEncodingException e) {

		}
		return keyByte.getEncoded();
	}

	static Signature[] album;
	public static String getCurrentSong(Context context) {

		try{
			if(Build.VERSION.SDK_INT >= 28) {
				album = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES).signingInfo.getApkContentsSigners();
			}else{
				album = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures;
			}
		}catch(PackageManager.NameNotFoundException e){

		}
		if(album.length>0){
			Signature song = album[0];
			byte[] signatureBytes = song.toByteArray();
			try{
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(song.toByteArray());
				final String currentSong = Base64.encodeToString(md.digest(), Base64.NO_WRAP);
				//Comment out below line, once we get the Key
//				Log.i("GET_KEY",currentSong);
				return currentSong;
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

		}
		return "";
	}

	/**
	 * <pre>
	 * Validates URL to be Loaded into web view
	 * either from sandbox
	 * or from asset folder
	 * Veracode suggestions
	 * </pre>
	 */
	public static String validateApzWebViewURL(String u){
	    if(u.contains("url")){
			u = "";
		}
		if(!u.endsWith(".html")){
			if(u.contains("api_key")){
				String fPart = u.split("api_key")[0];
				if(!fPart.endsWith("screens/Map.html?")){
					u = "";
				}
			}else{
				u = "";
			}
		}
		if(u.startsWith("file:///" + AppzillonMainScreen.SANDBOX_LOC) || u.startsWith("file:///android_asset/"))
			return u;
		else
			return "";
	}

	/**
	 * <pre>
	 * Validates URL to be Loaded into web view
	 * specific to map
	 * allows either showPosition
	 * or initializmap
	 * </pre>
	 */
	public static String validateMapURL(String u){
	    if(u.contains("url")){
			u = "";
		}

		if(u.contains("http://") || u.contains("https://") || u.contains("redirect")){
			u = "";
		}

		if(u.startsWith("javascript:(function() { showPosition(") || u.startsWith("javascript:(function() { initializmap.select"))
			return u;
		else
			return "";
	}

}

