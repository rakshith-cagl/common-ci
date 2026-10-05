package com.iexceed.plugins.checkgpsstatus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.ExternalActivityResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/


public class CheckGpsStatus extends ApzPlugin
{
    private static ApzPlugin pluginObj;
    private LocationManager mLocationManager;
    private static final int REQUEST_GPS_DIALOG = 1031;
    private static final int REQUEST_GPS_SETTINGS = 1032;
    private static String TAG = "ApzEnableGpsPlugin";


    private CheckGpsStatus(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    //  mAction = "GET_GPS_STATUS"
    //  checks for GPS provider Enabled or Disabled.

    //  mAction = "GET_LOCATION"
    //  checks if device supports PLAY SERVICES and opens GPS enable dialog.
    //  if device not support PLAY SERVICES , redirects to GPS enable setting page.
    @Override
    public void execute(JSONObject params)
    {
        Log.d(TAG, " INSIDE EXECUTE");

        mLocationManager = (LocationManager) activity
                .getSystemService(Context.LOCATION_SERVICE);
        try {
            String lAction = params.getString("action");
            callbackId = params.getString("id");
            if(lAction.equals("CHECK_LOCATION_AVAILABILITY"))
            {
                sendResponse();
            }else if(lAction.equals("REDIRECT_TO_SETTINGS")){
                moveToSettings();
            }
        } catch (JSONException ex) {
            invalidJson();
        }
    }

    private void sendResponse()
    {
        try {
            JSONObject lJson = new JSONObject();
            if(getGpsStatus())
            {
                lJson.put("text", "GPS enabled");
                ApzPluginUtil.sendSuccess(callbackId, lJson, false, activity, webView, true);
            }else{
                lJson.put("text", "GPS disabled");
                ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-057", null,
                        activity, webView, true);
            }
        } catch (JSONException e){
            invalidJson();
        }
    }

    //   checks if device supports PLAY SERVICES and opens GPS enable dialog.
    //   if device not support PLAY SERVICES , redirects to GPS enable setting page.
    private void moveToSettings()
    {
        boolean lIsGpsEnable = getGpsStatus();
        boolean lIsPlayServicesSupported = !checkPlayServices();
        if(!lIsGpsEnable)
        {
            if(lIsPlayServicesSupported) {
                openGpsDialog();
            }else{
                openGpsSetting();
            }
        }else{
            sendResponse();
        }
    }

    //  Redirects to GPS enable setting page.
    private void openGpsSetting()
    {
        ExternalActivityResultHandler lHandler =
        new ExternalActivityResultHandler()
        {
            @Override
            public void handleActivityResult(
                    int resultCode, Intent data) {
                sendResponse();
            }
        };

        Intent lSettingsIntent = new
                Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(lSettingsIntent, REQUEST_GPS_SETTINGS, lHandler);
    }

    // // Invalid JSON...
    private void invalidJson() {
        ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null,
                activity, webView, true);
    }

    // Opens GPS enable dialog...
    private void openGpsDialog()
    {
        ExternalActivityResultHandler lHandler =
                new ExternalActivityResultHandler()
                {
                    @Override
                    public void handleActivityResult(
                            int resultCode, Intent data) {
                        JSONObject lJson = new JSONObject();
                        if (resultCode == Activity.RESULT_OK) {
                            try {
                                lJson.put("text", "GPS enabled");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApzPluginUtil.sendSuccess(callbackId, lJson, false, activity, webView, true);
                        }else{
                            try {
                                lJson.put("errorMessage", "GPS disabled");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ApzPluginUtil.sendError(callbackId, "APZ-CNT-057", lJson,
                                    activity, webView, true);
                        }
                    }
                };

        activity.addExternalActivityResultHandler(REQUEST_GPS_DIALOG, lHandler);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are granted.
                // The client can initialize location requests here.
                try {
                    JSONObject lJson = new JSONObject();
                    lJson.put("text", "Gps already enabled");
                    ApzPluginUtil.sendSuccess(callbackId, lJson, false, activity, webView, true);
                } catch (JSONException je){
                    invalidJson();
                }
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied,
                    // show the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_GPS_DIALOG);
                        try {
                            JSONObject lJson = new JSONObject();
                            lJson.put("text", "Redirected to Gps dialog");
                            ApzPluginUtil.sendSuccess(callbackId, lJson, false, activity, webView, true);
                        } catch (JSONException je){
                            invalidJson();
                        }

                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                        try {
                            JSONObject lJson = new JSONObject();
                            lJson.put("errorMessage ", "Failed to open Gps dialog");
                            ApzPluginUtil.sendError(callbackId,null , lJson, activity, webView, true);
                        } catch (JSONException je){
                            invalidJson();
                        }
                    }
                }
            }
        });
    }

    // Checks GPS service status...
    private boolean getGpsStatus() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    // Checks PlayServices availability...
    private boolean checkPlayServices()
    {
        int resultCode = GoogleApiAvailability.
                getInstance().isGooglePlayServicesAvailable(activity.getApplicationContext());
        return resultCode == ConnectionResult.SUCCESS;
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new CheckGpsStatus(webView, activity);
        }
        return pluginObj;
    }
}
