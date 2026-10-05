package com.iexceed.plugins.compass;

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
import com.iexceed.plugins.auditlog.AuditLog;
import com.iexceed.plugins.errorlog.ApzLogger;

public class CompassPlugin extends ApzPlugin {

	public static boolean isRunning;

	private SensorManager mSensorManager;

	private Sensor gsensor;

	private Sensor msensor;

	private CompassSensorListener mCompassListener;

	private float[] mGravity = new float[3];

	private float[] mGeomagnetic = new float[3];

	private int mAzimuth = 0;

	private String mInterval;

	private String mPeriodicity;

	private String TAG = "COMPASS";
	
	private String callerId;

	public static boolean isTimerRunning = false;

	private static ApzPlugin pluginObj;

	private JSONObject body;

	private int timeInterval;

	private static Timer myTimer;

	public CompassPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new CompassPlugin(webView, activity);
		}
		return pluginObj;
	}

	public void startCompass(JSONObject jsonObj) {
		mPeriodicity = "none";
		String periodicityPresent;
		try {
			//id = jsonObj.getString("id");
			Iterator<String> jsonObjKeys = jsonObj.keys();
			while (jsonObjKeys.hasNext()) {
				periodicityPresent = (String) jsonObjKeys.next();
				if (periodicityPresent.equals("periodicity")) {
					mPeriodicity = jsonObj.getString("periodicity");
				}
				ApzLogger.i(TAG, "Key : " + periodicityPresent);
			}
			if (mPeriodicity.equals("timed")) {
				mInterval = jsonObj.getString("interval");
				if (mInterval != null && !mInterval.isEmpty()
						&& !mInterval.equalsIgnoreCase("")) {
					timeInterval = Integer.parseInt(mInterval);
				}
			}
		} catch (final JSONException e) {
			ApzLogger.e(TAG,e.toString());
			isRunning = false;
			ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity, webView, true);
			return;
		}
		mSensorManager = (SensorManager) activity.getApplication()
				.getSystemService(Context.SENSOR_SERVICE);

		if (mCompassListener == null) {
			mCompassListener = new CompassSensorListener();
		}

		if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
				&& mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
			gsensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			msensor = mSensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			try {
				if (mPeriodicity.equals("timed")) {
					mSensorManager.registerListener(mCompassListener, gsensor,
							Integer.parseInt(mInterval) * 1000);
					mSensorManager.registerListener(mCompassListener, msensor,
							Integer.parseInt(mInterval) * 1000);
				} else {
					mSensorManager.registerListener(mCompassListener, gsensor,
							SensorManager.SENSOR_DELAY_NORMAL);
					mSensorManager.registerListener(mCompassListener, msensor,
							SensorManager.SENSOR_DELAY_NORMAL);
				}
			} catch (final NumberFormatException nfe) {
				ApzLogger.e(TAG,nfe.toString());
				isRunning = false;
				
				pause();// unregisters listener
				ApzPluginUtil.sendError(callerId, "APZ_CNT-129", null, this.activity,
						this.webView, true); // Invalid Number
				return;
			}
			isRunning = true;
			AuditLog.sendToJSON();
		} else {
			ApzLogger.e(TAG,"APZ-CNT-062");
			isRunning = false;
			pause();// unregisters listener
			ApzPluginUtil.sendError(callerId, "APZ-CNT-062", null, this.activity,
					this.webView, true); // Device does not support Compass
			return;

		}

	}

	private void TimerMethod() {
		this.activity.runOnUiThread(Timer_Tick);
	}

	private Runnable Timer_Tick = new Runnable() {
		public void run() {
			callSuccessCallback();
		}
	};

	private void callSuccessCallback() {
		ApzPluginUtil.sendSuccess(callerId, body, true,
				activity, webView, false);
	}

	private class CompassSensorListener implements SensorEventListener {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			final float alpha = 0.97f;

			synchronized (this) {
				if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

					mGravity[0] = alpha * mGravity[0] + (1 - alpha)
							* event.values[0];
					mGravity[1] = alpha * mGravity[1] + (1 - alpha)
							* event.values[1];
					mGravity[2] = alpha * mGravity[2] + (1 - alpha)
							* event.values[2];

					ApzLogger.e(TAG, Float.toString(mGravity[0]));
				}

				if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

					mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha)
							* event.values[0];
					mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha)
							* event.values[1];
					mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha)
							* event.values[2];
					ApzLogger.e(TAG, Float.toString(event.values[0]));

				}

				float R[] = new float[9];
				float I[] = new float[9];
				boolean success = SensorManager.getRotationMatrix(R, I,
						mGravity, mGeomagnetic);
				if (success) {
					float orientation[] = new float[3];
					SensorManager.getOrientation(R, orientation);
					mAzimuth = (int) Math.toDegrees(orientation[0]); // orientation
					mAzimuth = (mAzimuth + 360) % 360;
					body = new JSONObject();

					try {

						body.put("magneticNorth",
								String.valueOf(mAzimuth));
						body.put("trueNorth", String.valueOf(mAzimuth));
					} catch (JSONException e) {
						ApzLogger.e(TAG,e.toString());
						isRunning = false;
						return;
					}
					if (mPeriodicity.equals("none")) {
						pause();
						// mWebView.loadUrl("javascript:"+
						// mSuccessCallback + "(" + compassRes+ ");");
						ApzPluginUtil.sendSuccess(callerId, body, true,
								activity, webView, false);
					} else if (isRunning) {
						// Abhishek, 29 October 2015, for API Level
						// below 19, it will send callback if
						// softkeyboard is not open START
						if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
							HitTestResult testResult = webView
									.getHitTestResult();
							if (testResult == null) {
								ApzLogger.i(TAG, "testResult is NULL");
							}
							if (testResult == null
									|| testResult.getType() != HitTestResult.EDIT_TEXT_TYPE)
								ApzPluginUtil.sendSuccess(callerId, body,
										true, activity, webView, false);
						} else {
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
						// Abhishek, 29 October 2015, for API Level
						// below 19, it will send callback if
						// softkeyboard is not open END
					}
				}
			}
		}
	}

	/**
	 * unregister the listener
	 */
	public void pause() {
		isRunning = false;
		if (mCompassListener != null) {
			mSensorManager.unregisterListener(mCompassListener);
			mCompassListener = null;
		}
	}

	public static boolean isCompassPlugin() {
		return true;
	}

	@Override
	public void execute(JSONObject params) {

		String action = "";
		//String id = "";

		try {
			callerId = params.getString("id");
			action = params.getString("action");
		} catch (Exception ex) {
			ApzLogger.e(TAG,ex.toString());
		}
		if (action.equalsIgnoreCase("START")) {
			if (isRunning) {
				ApzPluginUtil.sendError(callerId, "APZ-CNT-016", null, activity,
						webView, true);// Is Already Running
			} else {
				startCompass(params);
			}
		} else if (action.equalsIgnoreCase("STOP")) {
			if (isRunning) {
				pause();
				pluginObj = null;
				JSONObject body = null;
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
				try {
					body = new JSONObject();
					body.put("successMessage", "Compass Stopped");
				} catch (Exception ex) {
				}
				ApzPluginUtil.sendSuccess(callerId, body, false, activity, webView,
						true);
			} else {
				if (pluginObj != null)
					pluginObj = null;
				ApzPluginUtil.sendError(callerId, "APZ-CNT-063", null, activity,
						webView, true);// Compass already stopped
			}
		}
	}
}
