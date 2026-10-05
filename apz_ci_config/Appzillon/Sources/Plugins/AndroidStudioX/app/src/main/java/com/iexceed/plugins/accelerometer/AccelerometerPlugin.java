package com.iexceed.plugins.accelerometer;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;


public class AccelerometerPlugin extends ApzPlugin {

	private static ApzPlugin pluginObj;

	private AccelerometerPlugin(WebView webView, ApzActivity activity,
								Context context) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity,
			Context context) {
		if (pluginObj == null) {
			pluginObj = new AccelerometerPlugin(webView, activity, context);
		}
		return pluginObj;
	}

	private SensorManager mSensorManager;

	private Sensor mAccelerometer;

	private AccelerometerSensorListener mAccSensorListener;

	private String mPeriodicity;

	private String mInterval;

	public static boolean isRunning;

	private String TAG = "ACCELEROMETER";

	public static boolean isTimerRunning = false;

	private static Timer myTimer;

	private JSONObject accRes = null;

	private int timeInterval;

	private String id;

	/**
	 * Starts Accelerometer Sensor(will be called from javascript)
	 */
	private void startAccelerometerLister(JSONObject jsonObj) {
		mPeriodicity = "none";
		String periodicityPresent;
		try {
			id = jsonObj.getString("id");
			Iterator<String> jsonObjKeys = jsonObj.keys();
			while (jsonObjKeys.hasNext()) {
				periodicityPresent = (String) jsonObjKeys.next();
				if (periodicityPresent.equals("periodicity")) {
					mPeriodicity = jsonObj.getString("periodicity");
					if (mPeriodicity.isEmpty())
						mPeriodicity = "none";
				}
			}
			if (mPeriodicity.equalsIgnoreCase("timed")) {
				mInterval = jsonObj.getString("interval");
			}
			ApzLogger.i(TAG, "Key : " + mPeriodicity);

		} catch (final JSONException e) {
			ApzLogger.e(TAG, "Exception : " + e);
			ApzPluginUtil.sendError(id, "APZ-CNT-077", null, this.activity,
					this.webView, true);
		}
		mSensorManager = (SensorManager) activity.getApplicationContext()
				.getSystemService(Context.SENSOR_SERVICE);

		if (mAccSensorListener == null) {
			mAccSensorListener = new AccelerometerSensorListener();
		}

		if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
			mAccelerometer = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			try {
				if (mPeriodicity.equalsIgnoreCase("timed")) {
					if (Integer.parseInt(mInterval) > 0) {
						mSensorManager.registerListener(mAccSensorListener,
								mAccelerometer,
								Integer.parseInt(mInterval) * 1000);
					} else {
						mSensorManager.registerListener(mAccSensorListener,
								mAccelerometer,
								SensorManager.SENSOR_DELAY_NORMAL);
					}
				} else
					mSensorManager.registerListener(mAccSensorListener,
							mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

			} catch (final NumberFormatException nfe) {
				isRunning = false;
				ApzLogger.e(TAG, "Exception : " + nfe);
				pause(); // unregisters listener
				ApzPluginUtil.sendError(id, "APZ-CNT-078", null, this.activity,
						this.webView, true);
			}
			isRunning = true;
		} else {
			isRunning = false;
			pause();
			ApzLogger.i(TAG, "is Accelerometer running ? : " + isRunning);
			ApzPluginUtil.sendError(id, "APZ-CNT-080", null, this.activity,
					this.webView, true);
			return;

		}
	}

	private class AccelerometerSensorListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				final float x = event.values[0];
				final float y = event.values[1];
				final float z = event.values[2];
				accRes = new JSONObject();
				if (mInterval != null && !mInterval.isEmpty()
						&& !mInterval.equalsIgnoreCase("")) {
					timeInterval = Integer.parseInt(mInterval);
				}
				try {
					accRes.put("xCord", String.valueOf(x));
					accRes.put("yCord", String.valueOf(y));
					accRes.put("zCord", String.valueOf(z));

				} catch (JSONException e) {
					ApzLogger.e(TAG, "Exception : " + e);
					return;
				}
				if (mPeriodicity.equalsIgnoreCase("none")) {
					pause();
					ApzPluginUtil.sendSuccess(id, accRes, true, activity,
							webView, true);
				} else if (isRunning) {
					if (timeInterval > 0 && !mPeriodicity.equalsIgnoreCase("onChange")) { 
						if (!isTimerRunning) {
							isTimerRunning = true;
							myTimer = new Timer();
							myTimer.schedule(new TimerTask() {
								@Override
								public void run() {
									TimerMethod();
								}
							}, 0, timeInterval);
						}
					} else {
						callSuccessCallback();
					}
				}
			}
		}
	}

	/**
	 * Unregisters the listener on Activity pause
	 */

	public void pause() {
		if (mAccSensorListener != null) {
			mSensorManager.unregisterListener(mAccSensorListener);
			mAccSensorListener = null;
			isRunning = false;
		}

	}

	public static boolean isAccelerometer() {
		return true;
	}

	private void TimerMethod() {
		this.activity.runOnUiThread(Timer_Tick);
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			callSuccessCallback();
		}
	};

	public void callSuccessCallback() {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
			HitTestResult testResult = this.webView.getHitTestResult();
			if (testResult == null) {
				ApzLogger.i(TAG, "testResult is NULL");
			}
			if (testResult == null
					|| testResult.getType() != HitTestResult.EDIT_TEXT_TYPE)
				ApzPluginUtil.sendSuccess(id, accRes, true, this.activity,
						this.webView, false);
			ApzLogger.i(TAG, "Updating the coordinates from the timer");
		} else {
			ApzLogger.d(TAG, "Accelerometer Success");
			ApzPluginUtil.sendSuccess(id, accRes, true, this.activity,
					this.webView, false);

		}
	}

	@Override
	public void execute(JSONObject params) {

		String action = "";
		try {
			action = params.getString("action");
			this.callbackId = params.getString("id");
		} catch (JSONException e1) {
			
		}
		if (action.equalsIgnoreCase("START")) {
			if (isRunning) {
				ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-015", null,
						activity, webView, true);

			} else {
				startAccelerometerLister(params);
			}
		} else if (action.equalsIgnoreCase("STOP")) {
			if (isRunning) {
				pause();
				pluginObj = null;
				try {
					if (myTimer != null) {
						myTimer.cancel();
						myTimer = null;
						isTimerRunning = false;
					}
				} catch (Exception e) {
					ApzLogger.i(TAG,
							"exception in closing the timer " + e.toString());
				}

				JSONObject body = new JSONObject();
				try {
					body.put("successMessage", "Accelerometer Stopped");
				} catch (JSONException e) {
				}

				ApzPluginUtil.sendSuccess(this.callbackId, body, false,
						this.activity, this.webView, true);

			} else {
				if (pluginObj != null)
					pluginObj = null;
				ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-061", null,
						this.activity, this.webView, true);
			}
		}
	}
}
