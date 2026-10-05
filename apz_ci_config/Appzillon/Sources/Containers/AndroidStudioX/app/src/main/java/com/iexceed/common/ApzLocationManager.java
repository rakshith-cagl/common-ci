package com.iexceed.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class ApzLocationManager implements LocationListener {

    boolean isGPSEnabled;
    boolean canGetLocation;
    boolean isPassiveEnabled;
    boolean isNetworkEnabled;
    Location location = null;
    Location gpsLocation = null;
    Location passiveLocation = null;
    Location networkLocation = null;
    LocationManager locationManager;

    private Activity mActivity;
    private int retryCount = 0;

    public ApzLocationManager(Activity act) {
        this.mActivity = act;
    }

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        int MIN_TIME_BW_UPDATES = 0;
        int MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

        try {
            locationManager = (LocationManager) mActivity
                    .getSystemService(Service.LOCATION_SERVICE);

            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            isPassiveEnabled = locationManager
                    .isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled || isPassiveEnabled) {
                this.canGetLocation = true;
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        gpsLocation = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
                if (isPassiveEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.PASSIVE_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        passiveLocation = locationManager
                                .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    }
                }

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (locationManager != null) {
                        networkLocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }

                float gpsAccuracy = 0;
                float passiveAccuracy = 0;
                float networkAccuracy = 0;
                float mostAccurate = 0;
                if (gpsLocation != null && gpsLocation.hasAccuracy()) {
                    gpsAccuracy = gpsLocation.getAccuracy();
                    location = gpsLocation;
                    mostAccurate = gpsAccuracy;
                }
                if (passiveLocation != null && passiveLocation.hasAccuracy()) {
                    passiveAccuracy = passiveLocation.getAccuracy();
                    if (passiveAccuracy < mostAccurate || mostAccurate == 0) {
                        location = passiveLocation;
                        mostAccurate = passiveAccuracy;
                    }
                }
                if (networkLocation != null && networkLocation.hasAccuracy()) {
                    networkAccuracy = networkLocation.getAccuracy();
                    if (networkAccuracy < mostAccurate || mostAccurate == 0) {
                        location = networkLocation;
                    }
                }

            } else {

                return null;
            }

        } catch (Exception e) {
            //e.printStackTrace();
        }
        if (location == null) {
            if(retryCount < 5){
                retryCount ++;
                getLocation();
            }else{
                retryCount = 0;
                return null;
            }
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
