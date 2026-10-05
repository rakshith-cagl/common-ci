package com.iexceed.plugins.realtimetracklocation;

import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Looper;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ServerUtilities;
import com.iexceed.common.StringUtils;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.annotation.Nullable;

import static com.iexceed.appzillonapp.AppzillonMainScreen.activity;

/**
 * Created by mishra.abhishek on 13/11/17.
 * Modified for Huawei Location on 20/08/20
 */

public class TrackLocationService extends Service //implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{

    private static int UPDATE_INTERVAL = 5* 60 * 1000; // 5 minutes

    private double LONGITUDE;

    private double LATITUDE;

    private String TAG = "APZ_LOC_SERVICE";

    private FusedLocationProviderClient fusedLocationProviderClient;

    public TrackLocationService(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ApzLogger.i(TAG,"Service onStartCommand");
        if(intent != null){
//            isIntervalBased = intent.getStringExtra("isIntervalBased");
            UPDATE_INTERVAL = intent.getIntExtra("updateInterval",0);
//            FATEST_INTERVAL = intent.getIntExtra("fastestInterval",0);
//            DISPLACEMENT = intent.getIntExtra("displacement",0);
        }else{
            ApzLogger.e(TAG,"No Values from Intent");
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApzLogger.i(TAG,"Service onCreate");
//        writeInfile("Service onCreate");
//        if(checkPlayServices()){
//            buildGoogleApiClient();
//        }else{
//            ApzLogger.e(TAG,"Play services Not Available");
//            this.stopSelf();
//        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        LocationRequest mLocationRequest = new LocationRequest();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //Have permissions， send requests
                        ApzLogger.i(TAG,"Have permissions， send requests.");
                        startLocationRegularUpdates();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //Settings do not meet targeting criteria
                        ApzLogger.i(TAG,"Settings do not meet targeting criteria.");
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    //Calling startResolutionForResult can pop up a window to prompt the user to open the corresponding permissions
                                    rae.startResolutionForResult(activity, 0);
                                } catch (IntentSender.SendIntentException sie) {
                                    ApzLogger.i(TAG,"IntentSender.SendIntentException : "+sie.getLocalizedMessage());
                                }
                                break;
                        }
                        //Stop the service
                        stopSelf();
                    }
                });


    }

    private void startLocationRegularUpdates(){
        LocationRequest mLocationRequest = new LocationRequest();
        //Set the location update interval (int milliseconds).
       mLocationRequest.setInterval(UPDATE_INTERVAL);
        //Set the weight.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Processing when the API call is successful.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //Processing when the API call fails.
                    }
                });
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            try{
                if (locationResult != null) {
                    List<Location> locations = locationResult.getLocations();
                    if(!locations.isEmpty()){
                        LATITUDE = locations.get(0).getLatitude();
                        LONGITUDE = locations.get(0).getLongitude();
                        new UpdateLocToServer().execute();
                    }
                }else{
                    ApzLogger.e(TAG,"Couldn't get the location. Make sure location is enabled on the device");
                }
            }catch(Exception e){
                ApzLogger.e(TAG,"Exception in mLocationCallback : "+e.getMessage());
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        mLastLocation = location;
//        sendUpdatedLocation();
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        ApzLogger.i(TAG,"onConnected");
//        sendUpdatedLocation();
//
//        startLocationUpdates();
//    }

    /**
     * Method to display the location on UI
     * */
//    private void sendUpdatedLocation() {
//        ApzLogger.i(TAG,"sendUpdate");
//        try{
//
//            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//            if (mLastLocation != null) {
//                LATITUDE = mLastLocation.getLatitude();
//                LONGITUDE = mLastLocation.getLongitude();
//                new UpdateLocToServer().execute();
////                writeInfile("latitude : "+LATITUDE+" longitude : "+LONGITUDE);
//
//            } else {
//                ApzLogger.e(TAG,"Couldn't get the location. Make sure location is enabled on the device");
//            }
//        } catch (SecurityException ex){
//            ApzLogger.e(TAG,"SecurityException : "+ex.getMessage());
//        }
//
//
//    }

    /**
     * Starting the location updates
     * */
//    protected void startLocationUpdates() {
//        try{
//        mLocationRequest = LocationRequest.create();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        if(isIntervalBased.equalsIgnoreCase("Y")){
//            mLocationRequest.setInterval(UPDATE_INTERVAL);
//            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
//        }else{
//	    mLocationRequest.setInterval(1000);
//            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
//        }
//
//        enableGPSSettings();
//
//        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        }catch(SecurityException ex){
//            ApzLogger.e(TAG,"SecurityException : "+ex.getMessage());
//        } catch (Exception e){
//            ApzLogger.e(TAG,"Exception : "+e.getMessage());
//        }
//
//
//    }

//    private void enableGPSSettings(){
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
//        builder.setAlwaysShow(true);
//
//        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final Status status = result.getStatus();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        ApzLogger.i(TAG, "All location settings are satisfied.");
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        ApzLogger.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
//
//                        try {
//                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
//                            status.startResolutionForResult(activity, ApzTrackLocation.REQUEST_CHECK_SETTINGS);
//                        } catch (IntentSender.SendIntentException e) {
//                            ApzLogger.e(TAG,"IntentSender.SendIntentException : PendingIntent unable to execute request. "+e.getMessage());
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        ApzLogger.e(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
//                        break;
//                }
//            }
//        });
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//        ApzLogger.i(TAG,"onConnectionSuspended");
////        writeInfile("Service onConnectionSuspended");
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        ApzLogger.i(TAG,"onConnectionFailed");
////        writeInfile("Service onConnectionFailed");
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    private void writeInfile( String string){
//        try{
//            String filename = "SampleFile.txt";
//            String filepath = "MyFileStorage";
//            File myExternalFile = new File(getExternalFilesDir(filepath), filename);
//            Writer out = new BufferedWriter(new FileWriter(myExternalFile, true),1024);
//            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//            String str = currentDateTimeString+string+"\n";
//            out.write(str);
//            out.close();
//        } catch(IOException ioe){
//            
//        }
//
//    }

    private class UpdateLocToServer extends AsyncTask<Void, Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject jsonObject = new JSONObject();
            try{
                JSONObject header = new JSONObject();
                header.put("appId", StringUtils.getString(StringUtils.APP_ID));
                header.put("sessionId","");
                header.put("deviceId", AppzillonUtils.getDeviceId(activity));
                header.put("requestID", "");
                header.put("async", true);
                header.put("userId", "");
                header.put("screenId", "trackLocationService");
                header.put("status", true);
                header.put("source", "Appzillon");
                header.put("interfaceId", "appzillonTrackLocation");
                header.put("os", "Android");
                header.put("longitude", ""+LONGITUDE);
                header.put("latitude", ""+LATITUDE);

                JSONObject reqBody = new JSONObject();
                JSONObject appFileReq = new JSONObject();
                appFileReq.put("appId", StringUtils.getString(StringUtils.APP_ID));
                appFileReq.put("deviceId", AppzillonUtils.getDeviceId(activity));
                appFileReq.put("longitude", ""+LONGITUDE);
                appFileReq.put("latitude", ""+LATITUDE);

                reqBody.put("appzillonTrackLocationRequest", appFileReq);
                jsonObject.put(AppzillonMainScreen.APPZILLON_HEADER, header);
                jsonObject.put(AppzillonMainScreen.APPZILLON_BODY, reqBody);

            }catch(JSONException e){
                
            }
            String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
            JSONObject response = ServerUtilities.sendRequestToServer(serverUrl,jsonObject.toString());
            ApzLogger.i(TAG,"Resp : "+response);
            return null;
        }
    }

}

