package com.iexceed.common;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.currentlocation.CurrentLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MultifactorRegistor {
	
	private Context context;
	
	private Activity activity;
	
	private SharedPreferences apps;
	
	final static String app_props = "APP_PREFS";
	
	private String TAG = "";
	
	public static String TRACKLOCATION = "";
	public static String LATITUDE = "";
	public static String LONGITUDE = "";
	
	private static boolean STATUS = true;

	public MultifactorRegistor(Context context,Activity activity){
		this.context = context;
		this.activity = activity;
	}
	
	public void startMultifactorRegistor(){
		apps = context.getSharedPreferences(app_props, 0);
		
		TRACKLOCATION = StringUtils.getString(StringUtils.TRACK_LOCATION);
		if(TRACKLOCATION.equalsIgnoreCase("Y")){
			Location latlng = CurrentLocation.getLocation(AppzillonMainScreen.activity);
			double latd = 0.0;
			double lngd = 0.0;
			if(latlng!= null){
				latd = latlng.getLatitude();
				lngd = latlng.getLongitude();
				LATITUDE = Double.toString(latd);
				LONGITUDE = Double.toString(lngd);
			}
		}else{
//			Log.d(TAG, "TRACKLOCATION is : "+TRACKLOCATION);
		}

		new StartMultifactorRegistor().execute();		
		
		
	}
	
	class StartMultifactorRegistor extends AsyncTask<Void, Void, Void>{
		

		@Override
		protected Void doInBackground(Void... params) {
			
			JSONObject jsonObject = new JSONObject();
			String bluetoothName = ServerUtilities.getBluetoothName();
			try {
				
				JSONObject header = new JSONObject();
				header.put(AppzillonMainScreen.PRE_LOGIN, "true");
				header.put(AppzillonMainScreen.APP_ID, StringUtils.getString(StringUtils.APP_ID));
				header.put(AppzillonMainScreen.SCREEN_ID, "lauchApp");
				header.put(AppzillonMainScreen.REQUEST_KEY, "000NEW");			
//				header.put(AppzillonMainScreen.INTERFACE_ID, "appzillonMultifactorDeviceRegistration");
				header.put(AppzillonMainScreen.INTERFACE_ID, "appzillonDeviceRegistration");
				header.put(AppzillonMainScreen.REQ_STATUS, true); // sid 3.2 server changes
				header.put(AppzillonMainScreen.SESSION_ID, "");			
				header.put(AppzillonMainScreen.DEVICE_ID, AppzillonUtils.getDeviceId(context));			
				header.put(AppzillonMainScreen.USER_ID, AppzillonMainScreen.USER_ID_FOR_OTA);
				header.put("longitude", LONGITUDE);
				header.put("latitude", LATITUDE);
				header.put("origination", AppzillonUtils.ipAddress(context));
				header.put("source" , "APPZILLON");
				
				JSONObject reqBody = new JSONObject();
				JSONObject appFileReq = new JSONObject();
				appFileReq.put(AppzillonMainScreen.APP_ID, StringUtils.getString(StringUtils.APP_ID));
				appFileReq.put(AppzillonMainScreen.OS, AppzillonMainScreen.ANDROID_OS);
				appFileReq.put(AppzillonMainScreen.OS_VERSION, AppzillonUtils.getOsDetails(activity));
				appFileReq.put(AppzillonMainScreen.DEVICE_ID, AppzillonUtils.getDeviceId(context));
				//appFileReq.put(AppzillonMainScreen.MOBILE_ONE, AppzillonUtils.getPhoneNumber(context));
				appFileReq.put(AppzillonMainScreen.MOBILE_ONE, "UNKNOWN");
				appFileReq.put(AppzillonMainScreen.MOBILE_TWO, "UNKNOWN");
				appFileReq.put(AppzillonMainScreen.DEVICE_MODEL, AppzillonUtils.getDeviceType());
				appFileReq.put(AppzillonMainScreen.DEVICE_MAKE, AppzillonUtils.getDeviceMake());
//				appFileReq.put(AppzillonMainScreen.SCREEN_RESOLUTION, AppzillonUtils.getScreenResolution(activity));
				appFileReq.put(AppzillonMainScreen.SCREEN_RESOLUTION, AppzillonUtils.getScreenSize(activity));
				appFileReq.put(AppzillonMainScreen.APP_STORE_VERSION, AppzillonUtils.getCurrentAppStoreVersion(activity));
				appFileReq.put(AppzillonMainScreen.DEVICE_NAME, bluetoothName);
				appFileReq.put("longitude", LONGITUDE);
				appFileReq.put("latitude", LATITUDE);
				
				reqBody.put("deviceRegisterRequest", appFileReq);
				jsonObject.put(AppzillonMainScreen.APPZILLON_HEADER, header);
				jsonObject.put(AppzillonMainScreen.APPZILLON_BODY, reqBody);
			} catch (JSONException jsonException) {
				
			}	
//			Log.i(TAG, "deviceRegisterRequest : "+jsonObject.toString());
			String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
//			HttpResponse response = OTAPlugin.sendRequestToServer(jsonObject.toString(),serverUrl);
			JSONObject response = ServerUtilities.sendRequestToServer(serverUrl,jsonObject.toString());
			if(response != null){
//				Log.e(TAG, "deviceRegisterResponse : "+response);
				try {
					JSONObject header = response.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
					//String status = header.getString("status");  Sid , 3.2 server changes
					boolean status  = header.getBoolean("status");
					/*if Device registered then update then save os version*/
					if(status){     // status.equalsIgnoreCase("success")  Sid , 3.2 server changes
						updateUserSettings();
					}else{
						JSONArray errArray = response.getJSONArray(AppzillonMainScreen.APPZILLON_ERRORS);
						JSONObject error = errArray.getJSONObject(0);
						String errorMessage  = context.getResources().getString(R.string.already_registered);
						if(error.getString("errorMessage").contains(errorMessage) || error.getString("errorMessage").equalsIgnoreCase(errorMessage)){
							updateUserSettings();
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					
				}
				
			}else{
				//Log.e(TAG, "deviceRegisterResponse is null ");
			}
//			String lResponse = null;
//			if (response != null && response.getStatusLine().getStatusCode() == 200) {
//				
//				HttpEntity entity = response.getEntity();
//				InputStream inpStream;
//				try {
//					inpStream = entity.getContent();
//					lResponse = OTAPlugin.getStringFromInputStream(inpStream);
//					Log.i(TAG, ""+lResponse);
//					
//					JSONObject obj = new JSONObject(lResponse);
//					JSONObject header = obj.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
//					String status = header.getString("status");
//					/*if Device registered then update then save os version*/
//					if(status.equalsIgnoreCase("success")){
//						updateUserSettings();
//					}else{
//						JSONArray errArray = obj.getJSONArray(AppzillonMainScreen.APPZILLON_ERRORS);
//						JSONObject error = errArray.getJSONObject(0);
//						String errorMessage  = context.getResources().getString(R.string.already_registered);
//						if(error.getString("errorMessage").contains(errorMessage) || error.getString("errorMessage").equalsIgnoreCase(errorMessage)){
//							updateUserSettings();
//						}
//					}
//					
//				}catch (Exception e){
//					
//				}
//			}
			return null;
		}

		private void updateUserSettings() {
			UserSettings.setMultiFactorRegistered(StringUtils.getString(StringUtils.APP_ID),"true",apps);
			UserSettings.setOSVersion(AppzillonUtils.getOsDetails(activity),apps);
			UserSettings.setAppVersion(AppzillonMainScreen.MAIN_APP_NAME,AppzillonUtils.getCurrentAppStoreVersion(activity),apps);
		}

	}

}

