package com.iexceed.common;

import android.content.Context;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import android.os.Build;

public class StringUtils {	
	
	static HashMap<String,String>  appInfo;
	
	public static String APP_NAME = "PRODUCTNAME";
	
	public static String TITLE_ACTIVITY_MAIN = "PRODUCTTITLE";
	
	public static String APP_DESCRIPTION = "PRODUCTDESC";
	
	public static String APP_VERSION = "appVersion";
	
//	public static String APP_VENDOR = "PRODUCTVENDOR";
	
	//public static String APP_ICON = "APPICON"; // missing
	
	public static String FIRST_PAGE = "firstPage";
	
	public static String SERVER_URL = "serverUrl";

	public static String IFACE_ID_IN_SEVER_URL = "IFACEIDINURI";
	
	public static String GCM_SERVER_URL = "GCMNOTIFAPPLICATIONKEY";

	public static String GOOGLE_WEB_CLIENTID = "GOOGLEWEBCLIENTID";
	
	//public static String GCM_SENDER_ID = "GCMSENDERID";
	
	//public static String ENABLE_NAVIGATION_MODE = "ENABLENAVIGATIONMODE";  // missing
	
	public static String GENERATE_OTP = "otpReqd";
	
	public static String SERVER_TOKEN = "serverToken";
	
//	public static String SPLACH_ICON = "splash_icon";
	
	public static String DEFAULT_LANG = "DEFAULTLANGUAGE";
	
	public static String APP_ID = "appId";
	
//	public static String IS_OTA_ENABLED = "OTAREQUIRED";
	
//	public static String CONTAINER_APP = "CONTAINERAPP";
	
//	public static String CONTAINER_MENU = "CONTAINERMENU";
	
	public static String EXPIRY_DATE = "expiryDate";
	
	public static String EXPIRY_DATE_FORMAT = "DATEFORMAT";
	
	public static String APP_IDLE_TIME_OUT = "idleTimeOut";
	
	public static String TRACK_LOCATION = "trackLocation";
	
	public static String CERTIFICATE_PINNING  = "CERTIFICATEPINNING"; 
	
	public static String CERTIFICATE_NAME  = "CERTIFICATENAME";
	
	public static String TWITTER_CONSUMER_KEY = "twitterClientId";
	
	public static String TWITTER_CONSUMER_SECRET = "twitterSecretKey";

        public static String GOOGLE_MAPS_KEY = "googleMapsKey";

	public static String LINKEDIN_API_KEY = "linkedinClientId";

	public static String LINKEDIN_API_SECRET = "linkedinSecretKey";

	public static String LINKEDIN_REDIRECT_URL = "linkedinRedirectUrl";
	
	public static String IS_SCREENSHOT_ENABLED = "PREVENTSCREENSHOT";
	
	public static String IS_NOTIFICATION_SUPPORTED = "NOTIFICATIONREQUIRED"; 
	
	public static String IS_SENDLOG = "sendLog";
	
	public static String LOG_LEVEL = "logLevel";	

	public static String SSL_PINNING = "sslPinning";

	public static String TRUST_ALL_CERTIFICATES = "trustAllCertificates";

	public static String ENABLE_MOCK_SERVER = "enableMockServer";


	public static String CRYPTO_ALGORITHM ="AES/GCM/NoPadding";

	public static String RSA_CRYPTO_ALGORITHM ="RSA/NONE/OAEPPadding";


	//public static String OTPREQUIRED = "OTPREQUIRED";
	
	public StringUtils(Context context,String app_loc) {		
		
//		AssetManager assetManager = context.getAssets();
//		
//		try {
//			//Abhishek, Fix for bug id 6147 START
//			InputStream is = null;
////			InputStream is = assetManager.open(app_loc+File.separator+"AppProperties.xml");
//			if(context.getResources().getString(R.string.is_OTA_enabled).equalsIgnoreCase("Y")){
//				File filesJson = new File(AppzillonMainScreen.SANDBOX_LOC+"/"+app_loc+File.separator+"AppProperties.xml");
//				is = new FileInputStream(filesJson);
//			}else{
//				is = assetManager.open(app_loc+File.separator+"AppProperties.xml");
//			}
//			//Abhishek, Fix for bug id 6147 END
//			SAXParserFactory spf = SAXParserFactory.newInstance();
//	        SAXParser sp = spf.newSAXParser();
//	        XMLReader xr = sp.getXMLReader();
//	        OrderXMLHandler myXMLHandler = new OrderXMLHandler();
//	        xr.setContentHandler(myXMLHandler);
//	        InputSource inStream = new InputSource(is);        
//	        xr.parse(inStream);
//	        
//	        appInfo = myXMLHandler.getAppInfoMap();
//	        
//		} catch (IOException e) {
//		
//		} catch (ParserConfigurationException e) {
//			
//		} catch (SAXException e) {
//			
//		}
		
		////////////////////////////////////
		appInfo = new HashMap<String,String>();
		
		appInfo.put(APP_NAME, context.getResources().getString(R.string.PRODUCTNAME));
		appInfo.put(TITLE_ACTIVITY_MAIN, context.getResources().getString(R.string.PRODUCTTITLE));
		appInfo.put(APP_DESCRIPTION, context.getResources().getString(R.string.PRODUCTDESC));
		//appInfo.put(GCM_SERVER_URL, context.getResources().getString(R.string.GCMNOTIFAPPLICATIONKEY));
		appInfo.put(GOOGLE_WEB_CLIENTID, context.getResources().getString(R.string.GOOGLEWEBCLIENTID));
		//appInfo.put(GCM_SENDER_ID, context.getResources().getString(R.string.GCMSENDERID));
		//appInfo.put(TRACK_LOCATION, context.getResources().getString(R.string.TRACKLOCATION));
		appInfo.put(CERTIFICATE_PINNING, context.getResources().getString(R.string.CERTIFICATEPINNING));
		appInfo.put(CERTIFICATE_NAME, context.getResources().getString(R.string.CERTIFICATENAME));
		appInfo.put(IS_SCREENSHOT_ENABLED, context.getResources().getString(R.string.PREVENTSCREENSHOT));
		appInfo.put(IS_NOTIFICATION_SUPPORTED, context.getResources().getString(R.string.NOTIFICATION));
		appInfo.put(EXPIRY_DATE_FORMAT, context.getResources().getString(R.string.expiryDateFormat));
		appInfo.put(DEFAULT_LANG, "en");
		
		InputStream is = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		
		try {
			//For OTA refresh
			if((AppzillonMainScreen.OTAREQUIRED).equalsIgnoreCase("Y")){
//				File filesJson = new File(AppzillonMainScreen.SANDBOX_LOC+"/"+AppzillonMainScreen.ASSET_APP_LOC+"screens/config/appprops.json");
				File filesJson = AppzillonUtils.getApzFile(AppzillonMainScreen.SANDBOX_LOC+"/"+AppzillonMainScreen.ASSET_APP_LOC+"screens/config/appprops","json");
				is = new FileInputStream(filesJson);
			}else {
//				is = context.getAssets().open(app_loc+"/screens/config/appprops.json");
				is = context.getAssets().open(AppzillonUtils.validatePath(app_loc+"/screens/config/appprops","json"));
			}
			
			if (is != null) {
				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
			}
		} catch (Exception e) {
		
		}
		JSONObject SettingsJson = null;
		if (is != null) {
			try {
				SettingsJson = new JSONObject(sb.toString());
				//JSONObject staticobj = SettingsJson.getJSONObject("ContainerProperties");
				Iterator<String> iterator = SettingsJson.keys();
				while (iterator.hasNext()) {
				    String key = (String) iterator.next();
					String value = SettingsJson.getString(key);
						if(key.startsWith("serverTokenEnhanced")||key.startsWith("serverUrlEnhanced")) {
						if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
							if ("serverTokenEnhanced".equalsIgnoreCase(key)) {
								key = "serverToken";
								value = AppzillonUtils.getDecryptedValue(value);
							}
							if ("serverUrlEnhanced".equalsIgnoreCase(key)) {
								key = "serverUrl";
								if (!value.startsWith("http") || SettingsJson.getString("serverUrlEncReq").equalsIgnoreCase("Y")) {
									//decrypt the value only if either the URL does not starts with http or the serverUrlEncReq is Y
									value = AppzillonUtils.getDecryptedValue(value);
								}
							}
						} else {
							if ("serverTokenEnhanced2".equalsIgnoreCase(key)) {
								key = "serverToken";
								value = AppzillonUtils.getDecryptedValue(value);
							}
							if ("serverUrlEnhanced2".equalsIgnoreCase(key)) {
								key = "serverUrl";
								if (!value.startsWith("http") || SettingsJson.getString("serverUrlEncReq").equalsIgnoreCase("Y")) {
									//decrypt the value only if either the URL does not starts with http or the serverUrlEncReq is Y
									value = AppzillonUtils.getDecryptedValue(value);
								}
							}
						}
					}
					appInfo.put(key, value);
				}
			} catch (Exception ex) {
				
			}
			
		}
        
	}	
	
	public static String getString(String string_id){
		String appProp = null;
		try {
			appProp = appInfo.get(string_id);
			if(appProp == null){
                appProp = "";
            }
		} catch (Exception e) {

		}
		//return appInfo.get(string_id);
		return appProp;
	}

}
