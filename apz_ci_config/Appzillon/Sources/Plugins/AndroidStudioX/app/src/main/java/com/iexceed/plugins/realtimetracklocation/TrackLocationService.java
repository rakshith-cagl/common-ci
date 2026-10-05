package com.iexceed.plugins.realtimetracklocation;

//import android.app.AlertDialog;
import android.app.Service;
//import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ServerUtilities;
import com.iexceed.common.StringUtils;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.Writer;
//import java.text.DateFormat;
//import java.util.Date;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//import javax.net.ssl.HttpsURLConnection;

import static com.iexceed.appzillonapp.AppzillonMainScreen.activity;

/**
 * Created by mishra.abhishek on 13/11/17.
 */

public class TrackLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static int UPDATE_INTERVAL = 5* 60 * 1000; // 5 minutes

    private static int FATEST_INTERVAL = 2* 60 * 1000; // 2 minutes

    private static int DISPLACEMENT = 100; // meters

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1011;

    private Location mLastLocation;

    private String isIntervalBased = "Y";

    private double LONGITUDE;

    private double LATITUDE;

    private String TAG = "APZ_LOC_SERVICE";

//    int TIME_OUT = 10000; // 10 seconds

    public TrackLocationService(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ApzLogger.i(TAG,"Service onStartCommand");
        if(intent != null){
            isIntervalBased = intent.getStringExtra("isIntervalBased");
//            appId = intent.getStringExtra("appid");
//            deviceId = intent.getStringExtra("deviceid");
//            serverUrl = intent.getStringExtra("serverUrl");
            UPDATE_INTERVAL = intent.getIntExtra("updateInterval",0);
            FATEST_INTERVAL = intent.getIntExtra("fastestInterval",0);
            DISPLACEMENT = intent.getIntExtra("displacement",0);
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
        if(checkPlayServices()){
            buildGoogleApiClient();
        }else{
            ApzLogger.e(TAG,"Play services Not Available");
            this.stopSelf();
        }

    }

    synchronized void buildGoogleApiClient() {
        ApzLogger.i(TAG,"buildGoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(TrackLocationService.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),"This device is not supported.", Toast.LENGTH_LONG) .show();
                this.stopSelf();
            }
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        sendUpdatedLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        ApzLogger.i(TAG,"onConnected");
        sendUpdatedLocation();

        startLocationUpdates();
    }

    /**
     * Method to display the location on UI
     * */
    private void sendUpdatedLocation() {
        ApzLogger.i(TAG,"sendUpdate");
        try{

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                LATITUDE = mLastLocation.getLatitude();
                LONGITUDE = mLastLocation.getLongitude();
                new UpdateLocToServer().execute();
//                writeInfile("latitude : "+LATITUDE+" longitude : "+LONGITUDE);

            } else {
                ApzLogger.e(TAG,"Couldn't get the location. Make sure location is enabled on the device");
            }
        } catch (SecurityException ex){
            ApzLogger.e(TAG,"SecurityException : "+ex.getMessage());
        }


    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {
        try{
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if(isIntervalBased.equalsIgnoreCase("Y")){
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        }else{
	    mLocationRequest.setInterval(1000);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
        }

        enableGPSSettings();

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }catch(SecurityException ex){
            ApzLogger.e(TAG,"SecurityException : "+ex.getMessage());
        } catch (Exception e){
            ApzLogger.e(TAG,"Exception : "+e.getMessage());
        }


    }

    private void enableGPSSettings(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        ApzLogger.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        ApzLogger.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                            status.startResolutionForResult(activity, ApzTrackLocation.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            ApzLogger.e(TAG,"IntentSender.SendIntentException : PendingIntent unable to execute request. "+e.getMessage());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        ApzLogger.e(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        ApzLogger.i(TAG,"onConnectionSuspended");
//        writeInfile("Service onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        ApzLogger.i(TAG,"onConnectionFailed");
//        writeInfile("Service onConnectionFailed");
    }

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

//    public JSONObject sendRequestToServer(String endpoint, String string) {
//        JSONObject obj ;
//        if(endpoint.contains("https:")){
//            obj = sendRequestToHTTPSServer(endpoint,string );
//        }else {
//            obj = sendRequestToHTTPServer(endpoint,string );
//        }
//        return obj;
//
//    }

//    private JSONObject sendRequestToHTTPSServer(String URL, String request) {
//        JSONObject responseJson = null;
//        HttpsURLConnection urlConnection = null;
//        try{
//            boolean pinning_success = false;
////            if(StringUtils.getString(StringUtils.CERTIFICATE_PINNING).equals("Y")){
////                pinning_success = pinning();
////            }
//            java.net.URL url = new URL(URL);
//            urlConnection = (HttpsURLConnection) url.openConnection();
////            if(pinning_success){
////                urlConnection.setSSLSocketFactory(sc.getSocketFactory());
////            }
//            urlConnection.setDoOutput(true);
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setUseCaches(false);
//            urlConnection.setConnectTimeout(TIME_OUT);
//            urlConnection.setReadTimeout(TIME_OUT);
//            urlConnection.setRequestProperty("Content-Type","application/json");
//
//            urlConnection.connect();
//
//            OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
//            out.write(request.toString());
//            out.close();
//
//            int HttpResult =urlConnection.getResponseCode();
//            if(HttpResult == HttpURLConnection.HTTP_OK){
//                StringBuilder sb = new StringBuilder();
//                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
//                String line = null;
//                while ((line = br.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//                br.close();
//
//                responseJson = new JSONObject(sb.toString());
//            }else{
////			    	Log.e(TAG, ""+urlConnection.getResponseMessage());
//            }
//        }catch(MalformedURLException e){
//            // TODO Auto-generated catch block
//            
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//
//            if(e.getMessage().equals("java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.") || e.getMessage().equals("Handshake failed")){
//                AppzillonMainScreen.activity.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(AppzillonMainScreen.activity);
//                        builder.setMessage("Untrusted Connection,request cannot be processed");
//                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //finish();
//                            }
//                        });
//                        AlertDialog dialog = builder.create();
//                        dialog.show();
//
//                    }
//                });
//            }
//            
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//           
//        }
//        return responseJson;
//
//    }
//
//    private JSONObject sendRequestToHTTPServer(String URL, String request) {
//        HttpURLConnection urlConnection = null;
//        JSONObject responseJson = null;
//        try{
//            URL url = new URL(URL);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setDoOutput(true);
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setUseCaches(false);
//            urlConnection.setConnectTimeout(TIME_OUT);
//            urlConnection.setReadTimeout(TIME_OUT);
//            urlConnection.setRequestProperty("Content-Type","application/json");
//
//            urlConnection.connect();
//
//            OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
//            out.write(request.toString());
//            out.close();
//
//            int HttpResult =urlConnection.getResponseCode();
//            if(HttpResult == HttpURLConnection.HTTP_OK){
//                StringBuilder sb = new StringBuilder();
//                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
//                String line = null;
//                while ((line = br.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//                br.close();
//
//                responseJson = new JSONObject(sb.toString());
//            }else{
////			    	Log.e(TAG, ""+urlConnection.getResponseMessage());
//            }
//        }catch(MalformedURLException e){
//            // TODO Auto-generated catch block
//            
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//           
//        }
//        return responseJson;
//
//    }
}

