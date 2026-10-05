package com.iexceed.plugins.battery;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class BatteryPlugin extends ApzPlugin {

	private static ApzPlugin pluginObj;

	private String batteryState;

	private String batteryLevel;

	private String threshold;

	private String time;

	private int oldChargingLevel;

	private int oldChargingStatus;

	private static Timer batteryTimer;

	private String TAG = "BatteryPlugin";

	public BatteryPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new BatteryPlugin(webView, activity);
		}
		return pluginObj;
	}

	public void registerBatteryReceiver(JSONObject jsonObj) {
		JSONObject result = null;
		try {
			result = new JSONObject();
			batteryState = jsonObj.getString("state");
			batteryLevel = jsonObj.getString("level");
			threshold = jsonObj.getString("threshold");
            if("".equalsIgnoreCase(threshold)){
                threshold = "0";
            }
			time = jsonObj.getString("time");
            if("".equalsIgnoreCase(time)){
                time = "0";
            }

			AppzillonMainScreen.IS_BATTERY_CALLBACK_ENABLED = batteryLevel
					.equalsIgnoreCase("Y");
			AppzillonMainScreen.IS_STATE_CALLBACK_ENABLED = batteryState
					.equalsIgnoreCase("Y");
			AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED = isEnabled(threshold);

			// For Time
			if (isEnabled(time)) {
				startTimer(time);
			}

			if (!AppzillonMainScreen.IS_REGISTERED) {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						IntentFilter ifilter = new IntentFilter();
						ifilter.addAction(Intent.ACTION_BATTERY_CHANGED);
						activity.registerReceiver(powerConnectionReceiver, ifilter);
						
					}
				}, 1000);
				oldChargingLevel = getPresentChargingLevel();
				oldChargingStatus = getPresentState();
				AppzillonMainScreen.IS_REGISTERED = true;
				try {
					result.put("event", "started");
				} catch (Exception ex) {
					ex.getStackTrace();
				}
				ApzPluginUtil.sendSuccess(this.callbackId, result, true,
						activity, webView, true);
			} else {
				ApzLogger.i(TAG,"Already Registered, Please Stop and Start again.");
				ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-241", null,
						this.activity, this.webView, true);
			}
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-077", null,
					this.activity, this.webView, true);// Invalid JSON
		}

	}

	// For checking if there is any value present or not
	private boolean isEnabled(String value) {
		try {
			if(!"".equalsIgnoreCase(value)){
				int tHold = Integer.parseInt(value);
				if (tHold > 0) {
					return true;
				}
			}
			return false;
		} catch (NumberFormatException e) {
			ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-240", null,
					this.activity, this.webView, true);// Threshold and time
														// values should be an
														// Integer

			return false;
		}

	}

	BroadcastReceiver powerConnectionReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ApzLogger.i(TAG, "IS_REGISTERED : "
					+ AppzillonMainScreen.IS_REGISTERED);
			final JSONObject result = new JSONObject();
			if (AppzillonMainScreen.IS_REGISTERED) {

				int status = intent
						.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
				if (status == BatteryManager.BATTERY_STATUS_CHARGING
						|| status == BatteryManager.BATTERY_STATUS_FULL)
					status = BatteryManager.BATTERY_STATUS_CHARGING;
				else
					status = BatteryManager.BATTERY_STATUS_NOT_CHARGING;

				final int scale = intent.getIntExtra(
						BatteryManager.EXTRA_SCALE, -1);
				final int level = intent.getIntExtra(
						BatteryManager.EXTRA_LEVEL, -1);

				boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
				final String bState = isCharging ? "plugged" : "unplugged";

				final int bLevel = (int) (((float) level / (float) scale) * 100.0f);

				ApzLogger.i(TAG, "oldChargingStatus : " + oldChargingStatus
						+ " , status : " + status + " , oldChargingLevel : "
						+ oldChargingLevel + " , bLevel : " + bLevel);

				final int tStatus = status;
				boolean isChargingValueChanged = oldChargingLevel != bLevel;
				boolean isChargingStatusChanged = oldChargingStatus != status;

				if (AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED) {

					ApzLogger.i(TAG, "Threshold callback enabled");

					if (bLevel < Integer.parseInt(threshold)) {

						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								oldChargingLevel = bLevel;
								oldChargingStatus = tStatus;
								try {
									result.put("event", "threshold");
									result.put("level", bLevel);
									result.put("state", bState);
									result.put("command", "BATTERY_VALUE");
								} catch (JSONException e) {
									
								}
								ApzPluginUtil.sendSuccess(callbackId, result,
										true, activity, webView, false);
								AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED = false;
							}
						});
					} else {
						ApzLogger.i(TAG,
								"bLevel : " + bLevel + " , Threshold : "
										+ Integer.parseInt(threshold));
					}

				} else {

					ApzLogger.i(TAG, "bLevel : " + bLevel + " , Threshold : "
							+ Integer.parseInt(threshold));
					if (bLevel > Integer.parseInt(threshold)) {
						ApzLogger.i(TAG, "BLevel is greater then Threshold.");
						AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED = isEnabled(threshold);
					}
				}

				if (AppzillonMainScreen.IS_BATTERY_CALLBACK_ENABLED
						&& isChargingValueChanged) {
					ApzLogger
							.i(TAG,
									"Battery Callback is enabled and charging value changed");
					if (bLevel == Integer.parseInt(threshold)
							&& AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED) {
						ApzLogger.i(TAG, "SHOW Threshold");
					} else {
						oldChargingLevel = bLevel;
						oldChargingStatus = tStatus;
						try {
							result.put("event", "battery");
							result.put("level", bLevel);
							result.put("state", bState);
							result.put("command", "BATTERY_VALUE");
						} catch (JSONException e) {
							
						}
						ApzPluginUtil.sendSuccess(callbackId, result, true,
								activity, webView, false);
					}

				} else if (AppzillonMainScreen.IS_STATE_CALLBACK_ENABLED
						&& isChargingStatusChanged) {
					oldChargingLevel = bLevel;
					oldChargingStatus = tStatus;
					try {
						result.put("event", "state");
						result.put("level", bLevel);
						result.put("state", bState);
						result.put("command", "BATTERY_VALUE");
					} catch (JSONException e) {
						
					}

					ApzPluginUtil.sendSuccess(callbackId, result, true,
							activity, webView, false);
				}

			} else {
				ApzLogger.i(TAG, "Battery is not registered");
				context.unregisterReceiver(powerConnectionReceiver);
			}
		}
	};

	public void unregisterBatteryReceiver(JSONObject jsonObj) {
		JSONObject resultStopped = new JSONObject();
		try {
			/*callbackId = jsonObj.getString("id");
			batteryState = jsonObj.getString("state");
			batteryLevel = jsonObj.getString("level");
			threshold = jsonObj.getString("threshold");
			time = jsonObj.getString("time");

			if (batteryState.equalsIgnoreCase("Y")) {
				AppzillonMainScreen.IS_STATE_CALLBACK_ENABLED = false;
			}
			if (batteryLevel.equalsIgnoreCase("Y")) {
				AppzillonMainScreen.IS_BATTERY_CALLBACK_ENABLED = false;
			}
			if (threshold.equalsIgnoreCase("Y")) {
				AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED = false;
			}
			if (time.equalsIgnoreCase("Y")) {
				stopTimer();
			}

			if (batteryState.equalsIgnoreCase("Y")
					&& batteryLevel.equalsIgnoreCase("Y")
					&& threshold.equalsIgnoreCase("Y")
					&& time.equalsIgnoreCase("Y")) {*/
			AppzillonMainScreen.IS_STATE_CALLBACK_ENABLED = false;
			AppzillonMainScreen.IS_BATTERY_CALLBACK_ENABLED = false;
			AppzillonMainScreen.IS_THRESHOLD_CALLBACK_ENABLED = false;
			stopTimer();
				AppzillonMainScreen.IS_REGISTERED = false;
				if (powerConnectionReceiver != null) {
					activity.unregisterReceiver(powerConnectionReceiver);
					powerConnectionReceiver = null;
					pluginObj = null;
				}
			//}

			resultStopped.put("event", "stopped");
			ApzPluginUtil.sendSuccess(this.callbackId, resultStopped, false,
					this.activity, this.webView, true);

		} catch (JSONException e) {
			ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-077", null,
					this.activity, this.webView, true);
		} catch (IllegalArgumentException e) {
				powerConnectionReceiver = null;
			try {
				resultStopped.put("text", e.getMessage());
				ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-260", null,
						this.activity, this.webView, true);

			} catch (JSONException e1) {
				
			}
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-260", null,
					this.activity, this.webView, true);
			
		}
	}

	private void startTimer(String callBackTime) {

		int period = Integer.parseInt(callBackTime) * 1000; // Converting into
		batteryTimer = new Timer(true);													// milliseconds		
		batteryTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				
				int status = getPresentState();

				boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
				final String bState = isCharging ? "plugged" : "unplugged";
				final int bLevel = getPresentChargingLevel();
				

				JSONObject resObj = new JSONObject();
				try {
					resObj.put("event", "time");
					resObj.put("level", bLevel);
					resObj.put("state", bState);
				} catch (JSONException e) {
					ApzLogger.e(TAG,"JSONException"+e);
				}
				ApzPluginUtil.sendSuccess(callbackId, resObj, true, activity,
						webView, true);

			}
		}, period, period);
	}

	private void stopTimer() {
		if (batteryTimer != null)
			batteryTimer.cancel();
		ApzLogger.e(TAG,"Battery stopped");

	}

	private int getPresentChargingLevel() {
		Intent batteryIntent = activity.registerReceiver(null,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

		int batteryLevel = (int) (((float) level / (float) scale) * 100.0f);

		return batteryLevel;
	}

	private int getPresentState() {
		Intent batteryIntent = activity.registerReceiver(null,
				new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

		int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		if (status == BatteryManager.BATTERY_STATUS_CHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL)
			status = BatteryManager.BATTERY_STATUS_CHARGING;
		else
			status = BatteryManager.BATTERY_STATUS_NOT_CHARGING;
		return status;
	}

	public static boolean isBatteryPlugin() {
		return true;
	}

	@Override
	public void execute(JSONObject params) {
		String action = "";
		
		try {
			callbackId = params.getString("id");
			action = params.getString("action");
			
		} catch (Exception ex) {
			
		}
		if (action.equalsIgnoreCase("START")) {
			registerBatteryReceiver(params);
		} else if (action.equalsIgnoreCase("STOP")) {
			unregisterBatteryReceiver(params);
		}
	}
}
