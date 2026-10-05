package com.iexceed.plugins.gpslocation;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.ApzLocationManager;
import com.iexceed.plugins.ApzPluginUtil;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import androidx.core.app.ActivityCompat;
import android.webkit.WebView;

public class GpsLocator extends Service implements LocationListener {

    private LocationManager locationManager;

    private WebView mWebView;

    private Context mContext;

    private Activity mActivity;

    private String mPeriodicity = "none";

    private String mDistanceInterval;

    private String mtimeInterval;

    public static boolean isRunning;

    private Location location;

    double latitude;

    double longitude;

    boolean canGetLocation = false;

    private String TAG = "ApzLocationManager LOCATOR";

    String callbackId;

    private JSONObject gpsRes;

    private static Timer myTimer;

    private int timeInterval;

    public static boolean isTimerRunning = false;

    public GpsLocator(Activity act, WebView wv) {
        mWebView = wv;
        mActivity = act;
        mContext = mActivity.getApplicationContext();
    }

    public void getCoordinates(JSONObject jsonData) {
        boolean isGPSEnabled = false;
        boolean isNetworkEnabled = false;
        String periodicityPresent;
        ApzLocationManager gps=new ApzLocationManager(mActivity);
        try {
            gpsRes = new JSONObject();
			callbackId = jsonData.getString("id");
            Iterator<String> jsonObjKeys = jsonData.keys();
            while (jsonObjKeys.hasNext()) {
                periodicityPresent = (String) jsonObjKeys.next();
                if (periodicityPresent.equals("periodicity")) {
                    mPeriodicity = jsonData.getString("periodicity");
                }
            }

            if (mPeriodicity.equalsIgnoreCase("intervalBased") || mPeriodicity.equalsIgnoreCase("timed")) {
                mDistanceInterval = jsonData.getString("distanceInterval");
                mtimeInterval = jsonData.getString("timeInterval");
                if (mtimeInterval != null && !mtimeInterval.isEmpty()
                        && !mtimeInterval.equalsIgnoreCase("")) {
                    timeInterval = Integer.parseInt(mtimeInterval);
                }
            }

        } catch (final JSONException e) {
            //ApzLogger.e(TAG, e.toString());
            ApzPluginUtil.sendError(this.callbackId, "APZ-CNT_077", null,
                    mActivity, mWebView, true);
            return;
        }
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                enableProvider();
                return;
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (mPeriodicity.equals("intervalBased")) {
                        try {
                            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {
                                if (mtimeInterval.equals("")) {
                                    locationManager.requestLocationUpdates(
                                            LocationManager.NETWORK_PROVIDER, 0,
                                            Float.parseFloat(mDistanceInterval),
                                            this);
                                } else if (mDistanceInterval.equals("")) {
                                    locationManager.requestLocationUpdates(
                                            LocationManager.NETWORK_PROVIDER,
                                            Long.parseLong(mtimeInterval), 0, this);
                                } else {
                                    locationManager.requestLocationUpdates(
                                            LocationManager.NETWORK_PROVIDER,
                                            Long.parseLong(mtimeInterval),
                                            Float.parseFloat(mDistanceInterval),
                                            this);
                                }
                                if (locationManager != null) {
                                    /*location = locationManager
                                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);*/
                                    location = gps.getLocation();
                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }
                            }
                        } catch (final NumberFormatException nfe) {
                            //ApzLogger.e(TAG, nfe.toString());
                            ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, mActivity, mWebView, false);
                            return;
                        }

                    } else {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER, 0, 0, this);
                        //ApzLogger.d("Network", "Network");
                        if (locationManager != null) {
                            /*location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);*/
                            location = gps.getLocation();
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                } else if (isGPSEnabled) {
                    if (mPeriodicity.equals("intervalBased")) {
                        try {
                            if (mtimeInterval.equals("")) {
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER, 0,
                                        Float.parseFloat(mDistanceInterval),
                                        this);
                            } else if (mDistanceInterval.equals("")) {
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        Long.parseLong(mtimeInterval), 0, this);
                            } else {
                                locationManager.requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        Long.parseLong(mtimeInterval),
                                        Float.parseFloat(mDistanceInterval),
                                        this);
                            }
                            if (locationManager != null) {
                                /*location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);*/
                                location = gps.getLocation();
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        } catch (final NumberFormatException nfe) {
                            //ApzLogger.i(TAG, nfe.toString());
                            ApzPluginUtil.sendError(this.callbackId, "APZ-CNT059", null,
                                    mActivity, mWebView, true);
                            return;
                        }
                    } else {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER, 0, 0, this);

                            if (locationManager != null) {
                                /*location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);*/
                                location = gps.getLocation();
                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            //ApzLogger.e(TAG, e.toString());
        }
        isRunning = true;
        //AuditLog.sendToJSON();
        
        try {
            gpsRes.put("latitude",
                    String.valueOf(String.valueOf(location.getLatitude())));
            gpsRes.put("longitude",
                    String.valueOf(String.valueOf(location.getLongitude())));
            gpsRes.put("altitude", String.valueOf(String
                    .valueOf(location.getAltitude())));
            gpsRes.put("accuracy", String.valueOf(String
                    .valueOf(location.getAccuracy())));
            gpsRes.put("altitudeaccuracy", "");
            gpsRes.put("heading", "");
            gpsRes.put("speed", String.valueOf(String
                    .valueOf(location.getSpeed())));

        } catch (JSONException e) {
            //ApzLogger.e(TAG, e.toString());
            return;
        }


        if (mPeriodicity.equals("none")) {
            pause();
            callSuccessCallback();
        } else if (isRunning) {
            callSuccessCallback();

        }

    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    private void enableProvider() {
        //ApzLogger.i(TAG, "Please enable Location ");
        ApzPluginUtil.sendError(callbackId, "APZ-CNT-057", null, mActivity, mWebView, true);//GPS_ERROR Enable Provider
    }

    private void TimerMethod() {
        mActivity.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            callSuccessCallback();
        }
    };

    private void callSuccessCallback() {
        ApzPluginUtil.sendSuccess(callbackId, gpsRes, true,
                mActivity, mWebView, true);
    }

    @Override
    public void onLocationChanged(final Location location) {
        synchronized (this) {
            if (location != null) {
//				ApzLogger.i(TAG,
//						"lat:" + location.getLatitude() + "long:"
//								+ location.getLongitude());
                
                try {
                    gpsRes.put("latitude", String.valueOf(String
                            .valueOf(location.getLatitude())));
                    gpsRes.put("longitude", String.valueOf(String
                            .valueOf(location.getLongitude())));
                    gpsRes.put("altitude", String.valueOf(String
                            .valueOf(location.getAltitude())));
                    gpsRes.put("accuracy", String.valueOf(String
                            .valueOf(location.getAccuracy())));
                    gpsRes.put("altitudeaccuracy", "");
                    gpsRes.put("heading", "");
                    gpsRes.put("speed", String.valueOf(String
                            .valueOf(location.getSpeed())));

                } catch (JSONException e) {
                    //ApzLogger.e(TAG, e.toString());
                    return;
                }

                if (mPeriodicity.equals("none")) {
                    pause();
                    callSuccessCallback();
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

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void pause() {
        if (isRunning) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(this);
                isRunning = false;
            }
        }
    }

    public void stop() {
        if (isRunning) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.removeUpdates(this);
                isRunning = false;
                try {
                    if (myTimer != null) {
                        myTimer.cancel();
                        myTimer = null;
                        isTimerRunning = false;
                    }
                } catch (Exception e) {
                    //ApzLogger.i(TAG,
                           // "exception in closing the timer " + e.toString());
                }
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
