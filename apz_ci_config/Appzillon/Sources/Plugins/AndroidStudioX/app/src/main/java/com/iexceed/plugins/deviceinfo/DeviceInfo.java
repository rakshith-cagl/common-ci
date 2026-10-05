package com.iexceed.plugins.deviceinfo;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ApzActivity;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.webkit.WebView;


public class DeviceInfo extends ApzPlugin{

	private static ApzPlugin pluginObj;

	private ConnectivityManager mConMgr;

	private String TAG = "DEVICE INFO";

	private final int TEMP_NETWORK_TYPE_4G = 19;

	private DeviceInfo(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity){
		if(pluginObj == null){
			pluginObj = new DeviceInfo(webView, activity);
		}
		return pluginObj;
	}


	public void getDeviceDetails(JSONObject jsonObj) {

		JSONObject obj = new JSONObject();
		try {
			callbackId = jsonObj.getString("id");

			obj.put("osName", AppzillonMainScreen.ANDROID_OS);
			obj.put("osVersion", AppzillonUtils.getOsDetails(this.activity));
			obj.put("devType", AppzillonUtils.getDeviceType());
			obj.put("isEmulator", AppzillonUtils.isEmulator());  //3.2 changes
//			obj.put("screenResolution", getScreenSize());
			obj.put("screenResolution", AppzillonUtils.getScreenSize(this.activity));
			obj.put("connectionType", getNetworkType());
			obj.put("batteryStatus", getBatteryLevel()+"%");
			//obj.put("simDetails", simStatus());
//			ApzLogger.i(TAG,"Dev Tet : "+obj.toString());

		} catch (final JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, this.activity, this.webView, true);
		}

		ApzPluginUtil.sendSuccess(this.callbackId, obj, false, this.activity, this.webView, true);
	}
	/**
	 * Returns device model name like HTC Desire C
	 * @return
	 */
	/**
	 * Retrieves Battery Level of the device
	 * @return batterylevel as folat
	 */
	public float getBatteryLevel() {
		Intent batteryIntent = activity.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		// Error checking that probably isn't needed but I added just in case.
		if(level == -1 || scale == -1) {
			return 50.0f;
		}
		return ((float)level / (float)scale) * 100.0f;
	}

	public String getNetworkType() {
//		AuditLog.makeString("DEVICE INFO","Network type");
		String networkTypeStr = "No-Connection";
		// Check each connection type

		/** Check the connection **/

		ConnectivityManager cm = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info==null || !info.isConnected())
			return "-"; //not connected
		if(info.getType() == ConnectivityManager.TYPE_WIFI)
			return "WIFI";
		if(info.getType() == ConnectivityManager.TYPE_MOBILE){
			int networkType = info.getSubtype();
			switch (networkType) {
				case TelephonyManager.NETWORK_TYPE_GPRS:
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_CDMA:
				case TelephonyManager.NETWORK_TYPE_1xRTT:
				case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
				case TelephonyManager.NETWORK_TYPE_GSM:
					return "2G";
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
				case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
				case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
				case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
					return "3G";
				case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
				case TEMP_NETWORK_TYPE_4G:
				case TelephonyManager.NETWORK_TYPE_IWLAN:
					return "4G";
				default:
					return networkTypeStr;
			}
		}
		return networkTypeStr;
	}
	/**
	 * To check whether network connection is available on device or not
	 *
	 * @return boolean(true if network connection available otherwise false)
	 */
	public boolean checkInternetConnection() {
		mConMgr = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		if (mConMgr.getActiveNetworkInfo() != null&& mConMgr.getActiveNetworkInfo().isAvailable()	&& mConMgr.getActiveNetworkInfo().isConnected())
			return true;
		else
			return false;
	}
	
	@TargetApi(22)
	 public JSONArray simStatus(){
		 TelephonyManager tm = (TelephonyManager) activity.getSystemService(activity.TELEPHONY_SERVICE);
		 JSONArray array = new JSONArray();//gets the current TelephonyManager
		 if (tm.getSimState() != TelephonyManager.SIM_STATE_ABSENT){
			 List<SubscriptionInfo> subscriptionInfoList = null;

				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
					SubscriptionManager subscriptionManager = SubscriptionManager
							.from(activity);
					subscriptionInfoList = subscriptionManager
							.getActiveSubscriptionInfoList();
					try {
						if (subscriptionInfoList != null) {
							for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
								JSONObject obj = new JSONObject();
								int subscriptionId = subscriptionInfo.getSubscriptionId();
								obj.put("subscriptionId", subscriptionId);
								String dName = (String) subscriptionInfo.getDisplayName();
								obj.put("displayName", dName);
								String icc_id = subscriptionInfo.getIccId();
								obj.put("icc_id", icc_id);
								int slot = subscriptionInfo.getSimSlotIndex();
								obj.put("slot", slot);
								array.put(obj);
								ApzLogger.d("ApzUtilsPlugin", "subscriptionId:" + subscriptionId);
							}
						}} catch (JSONException e) {
						ApzLogger.e("SMS",e.toString());
					}
				}
		 }
		return array;
	 }
	
	public static boolean isDeviceInfo() {
		return true;
	}

	@Override
	public void execute(JSONObject params) {
		getDeviceDetails(params);
		
	}
}

