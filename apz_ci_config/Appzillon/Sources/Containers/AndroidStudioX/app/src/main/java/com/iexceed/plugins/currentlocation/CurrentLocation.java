package com.iexceed.plugins.currentlocation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;

import com.iexceed.common.ApzLocationManager;
import com.iexceed.plugins.augmentedreality.ApzARPlugin;

public class CurrentLocation {

	private static LocationManager locationManager;

    static boolean isNetworkEnabled = false;
	
	// The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    
	private static final String TAG = "";
    
  // flag for ApzLocationManager status
    private static boolean canGetLocation = false;
    
    private static Location location = null; // location
    
    private static double thresholdDistance = 0.0;
    
    private static Location presentLoc;

    private static Activity mActivity;

	public static Location getLocation(Activity activity) {

        ApzLocationManager gps=new ApzLocationManager(activity);
        Location loc  = gps.getLocation();

//		Location loc = null;
		/*
		Criteria crit=new Criteria();

        mActivity = activity;
		
		try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            crit.setAccuracy(Criteria.ACCURACY_FINE);
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
            }

            // getting ApzLocationManager status
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            	showSettingsAlert(mActivity);
            } else {
                canGetLocation = true;
                if (isNetworkEnabled) {
                    location=null;
//Abhishek 01 Oct 2015, Using critiria START
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                      locationManager.requestLocationUpdates(0L, 0.0f, crit, mLocationListener, null);
//Abhishek 01 Oct 2015, Using critiria END
                    //ApzLogger.d(TAG, "Network");
                    if (locationManager != null) {
                        //location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        location = gps.getLocation();
                        if (location != null) {
                        	loc = location;
                        }
                    }
                }
                // if ApzLocationManager Enabled get location using ApzLocationManager Services
                if (isGPSEnabled) {
//Abhishek 01 Oct 2015, if we got Loc from Netwrok then use it START
//                    location=null;
//Abhishek 01 Oct 2015, if we got Loc from Netwrok then use it END
                    if (location == null) {
//Abhishek 01 Oct 2015, Using critiria START
//                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                    	  locationManager.requestLocationUpdates(0L, 0.0f, crit, mLocationListener, null);
//Abhishek 01 Oct 2015, Using critiria END
                    	  //ApzLogger.d(TAG, "ApzLocationManager Enabled");
                        if (locationManager != null) {
                            //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            location = gps.getLocation();
                            if (location != null) {
                            	loc = location;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
        	//ApzLogger.e(TAG,e.toString());
        }*/
//		stopLocationListener();
		if(thresholdDistance <= 0.0)
			stopLocationListener();
		return loc;
	
	}
	
	/**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     * */
	private static void showSettingsAlert(final Activity mActivity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mActivity.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        //Abhishek, 27 October 2015,If the call is from LandingPageActivity Don't show Alert START 
		if(mActivity.toString().contains("LandingPageActivity")){
			//ApzLogger.d(TAG, "LandingPageActivty Request Do not show Alert Dialog.");
		}
		//Abhishek, 27 October 2015,If the call is from LandingPageActivity Don't show Alert END 
		else{
			 alertDialog.show();
		}
       
    }
	
	/**
     * Stop using Location listener Calling this function will stop using ApzLocationManager in app
     * */
  public static void stopLocationListener() {
  if (locationManager != null) {
      if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
              == PackageManager.PERMISSION_GRANTED ) {
          locationManager.removeUpdates(mLocationListener);
      }
  }
}
	
	private final static LocationListener mLocationListener = new LocationListener() {
	    @Override
	    public void onLocationChanged(final Location location) {
	    	//For Augmented Reality
			if(presentLoc != null){
	    	double dist =  location.distanceTo(presentLoc);
	    	if(dist > thresholdDistance)
	    		ApzARPlugin.augRealityThresholdReached(location.getLatitude(),location.getLongitude());
	    	else if(dist > 3 && ApzARPlugin.isPlugin())
	    		ApzARPlugin.setCurrentLocation(location);
	    		setPresentLocation(location);
			}else{
				//ApzLogger.e("Abhishek", "Present Location is null");
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
	};

	public static void setThreshold(double th){
		thresholdDistance = th;
	}
	
	public static void setPresentLocation(Location loc){
		presentLoc = loc;
	}

}
