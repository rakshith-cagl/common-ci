package com.iexceed.plugins.gpslocation;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;

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
import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

public class ApzGpsLocatorPlugin extends ApzPlugin {

    private String action;

    private static ApzPlugin pluginObj;

    private JSONObject mJsonObj;

    private String[] permissions;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private ApzGpsLocatorPlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzGpsLocatorPlugin(webView, activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {
        try {
            action = params.getString("action");
            callbackId = params.getString("id");
            this.mJsonObj = params;
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                    requestForPermission();
                } else {
                    enableGPS();
                }
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P){

                if (ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(activity,
                        "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED){
                    permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            "android.permission.ACCESS_BACKGROUND_LOCATION"};
                    requestForPermission();
                }else{
                    enableGPS();
                }
            }
            else {
                enableGPS();
            }
        } catch (JSONException ex) {
        }
    }

    private void enableGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        SettingsClient settingsClient = LocationServices.getSettingsClient(activity);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        LocationRequest mLocationRequest = new LocationRequest();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        //check Location Settings
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //Have permissions， send requests
                        if(action.equals("START")){
                            startLocationRegularUpdates();
                        }else if(action.equals("STOP")){
                            stopLocationUpdates();
                        }else{
                            try{
                                JSONObject eJson = new JSONObject();
                                eJson.put("errorCode", "");
                                eJson.put("errorMessage", "Unknown Operation");
                                ApzPluginUtil.sendError(callbackId, "APZ-CNT-274", eJson, activity, webView, true);
                            }catch(JSONException je){ }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //Settings do not meet targeting criteria
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    //Calling startResolutionForResult can pop up a window to prompt the user to open the corresponding permissions
                                    rae.startResolutionForResult(activity, 0);
                                } catch (IntentSender.SendIntentException sie) {
                                    try{
                                        JSONObject eJson = new JSONObject();
                                        eJson.put("errorCode", "");
                                        eJson.put("errorMessage", sie.getLocalizedMessage());
                                        ApzPluginUtil.sendError(callbackId, "APZ-CNT-274", eJson, activity, webView, true);
                                    }catch(JSONException je){ }
                                }
                                break;
                        }
                    }
                });
    }

    private void startLocationRegularUpdates(){
        long interval = 5000;
        LocationRequest mLocationRequest = new LocationRequest();
        //Set the location update interval (int milliseconds).
        try{
            String s = mJsonObj.getString("timeInterval");
            interval =  Long.parseLong(s);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLocationRequest.setInterval(interval);
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

    private void stopLocationUpdates(){
        //Note: When location updates are stopped, the mLocationCallback object must be the same as that of requestLocationUpdates().
        fusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        JSONObject json = new JSONObject();
                        try {
                            json.put("text", "Location Updates are stopped.");
                        } catch (JSONException e) {

                        }
                        ApzPluginUtil.sendSuccess(callbackId, json, false, activity, webView, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        ApzPluginUtil.sendError(callbackId, "APZ-CNT-057", null, activity, webView, true);
                    }
                });
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            try{
                if (locationResult != null) {
                    List<Location> locations = locationResult.getLocations();
                    JSONObject gpsRes = new JSONObject();
                    if(!locations.isEmpty()){
                        gpsRes.put("latitude", String.valueOf(locations.get(0).getLatitude()));
                        gpsRes.put("longitude", String.valueOf(locations.get(0).getLongitude()));
                        gpsRes.put("altitude", String.valueOf(locations.get(0).getAltitude()));
                        gpsRes.put("accuracy", String.valueOf(locations.get(0).getAccuracy()));
                        gpsRes.put("altitudeaccuracy", "");
                        gpsRes.put("heading", "");
                        gpsRes.put("speed", String.valueOf(locations.get(0).getSpeed()));
                    }
                    ApzPluginUtil.sendSuccess(callbackId, gpsRes, true, activity, webView, true);
                }else{
                    Log.i("ABHISHEK","null");
                }
            }catch(Exception e){

            }
        }
    };

    public static boolean isGpsLocator() {
        return true;
    }

    private void requestForPermission() {
        this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_LOCATION, new OnPermissionsResultHandler() {

                    @Override
                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == ApzPlugin.APZ_REQ_LOCATION) {
                            boolean denied = false;
                            boolean never_ask_again = false;
                            for (String permission : permissions) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                                    denied = true;
                                } else {
                                    if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                                        never_ask_again = true;
                                    }
                                }
                            }
                            if (never_ask_again) {
                                PermissionDeniedCallback();
                            } else if (denied) {
                                displayReconfirmationMessage();
                            } else {
                                enableGPS();
                            }
                        } else {
                            PermissionDeniedCallback();
                        }
                    }
                }
        );
    }

    private void displayReconfirmationMessage() {
        String message = "To get location, grant permission for app to access gps";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Permission Denied");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        requestForPermission();
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                PermissionDeniedCallback();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void PermissionDeniedCallback(){
        ApzPluginUtil.sendPermissionDenied("GPS location ",callbackId, this.activity,this.webView);
    }
}
