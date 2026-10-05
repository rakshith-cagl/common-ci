package com.iexceed.plugins.realtimetracklocation;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.ExternalActivityResultHandler;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

/**
 * Created by mishra.abhishek on 14/11/17.
 */

public class ApzTrackLocation extends ApzPlugin {

    private static ApzPlugin plugin;

    private JSONObject mJsonObj;

    public static int REQUEST_CHECK_SETTINGS = 1010;

    private String TAG = "ApzTrackLocation";

    public ApzTrackLocation(WebView webView, ApzActivity activity){
        super(webView,activity);
    }

    @Override
    public void execute(JSONObject params) {
        String action = "";
        try{
            this.callbackId = params.getString("id");
            action = params.getString("action");
            this.mJsonObj = params;
        } catch (JSONException e) {
            
        }

        ApzLogger.i(TAG,"Service Running : "+isMyServiceRunning(TrackLocationService.class));

        if(action.equalsIgnoreCase("START")){
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                if ((ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) ) {
                    requestForPermission();
                } else {
                    startTrackingLocation();
                }
            } else {
                startTrackingLocation();
            }
        }else{
            stopService("Stopped.");
        }
    }

    private ExternalActivityResultHandler handler = new ExternalActivityResultHandler() {
        @Override
        public void handleActivityResult(int resultCode, Intent data) {
            if (resultCode == Activity.RESULT_OK) {
                ApzLogger.i(TAG, "Location Settings Enabled.");
            }else{
                stopService("Location Request Cancelled");

            }
        }
    };

    private void stopService(String messsage){
        Intent intent = new Intent(activity, TrackLocationService.class);
        activity.stopService(intent);

        JSONObject json = new JSONObject();
        try {
            json.put("text", messsage);
        } catch (JSONException e) {
        }
        ApzPluginUtil.sendError(callbackId, "APZ-CNT-211", json, activity, webView, true);
    }

    private void startTrackingLocation(){
        int interval;
        int disp;
        int fastestInterval;
        try{

            this.activity.addExternalActivityResultHandler(REQUEST_CHECK_SETTINGS,handler);

            String intervalBased = mJsonObj.getString("isIntervalBased");
            if(intervalBased.equalsIgnoreCase("Y")){
                interval  = mJsonObj.getInt("interval");
                disp = 0;
                fastestInterval = interval/2;
            }else{
                interval = 0;
                disp = mJsonObj.getInt("displacement");
                fastestInterval = 0;
            }
            Intent intent = new Intent(activity, TrackLocationService.class);
            intent.putExtra("updateInterval",interval);
            intent.putExtra("fastestInterval", fastestInterval);
            intent.putExtra("displacement",disp);
            intent.putExtra("isIntervalBased",intervalBased);            
            activity.startService(intent);

        } catch (JSONException e) {
            
        }
    }

//    private void requestForPermission() {
//        this.activity.startOnPermissionForResult(activity, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, ApzPlugin.APZ_REQ_LOCATION, new OnPermissionsResultHandler() {
//
//                    @Override
//                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//                        if (requestCode == ApzPlugin.APZ_REQ_LOCATION) {
//                            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                                if(grantResults[1]==PackageManager.PERMISSION_GRANTED){
//                                    startTrackingLocation();
//                                }else{
//                                    PermissionDeniedCallback();
//                                }
//                            } else {
//                                PermissionDeniedCallback();
//                            }
//                        }
//                    }
//                }
//        );
//    }

    private void requestForPermission() {
        this.activity.startOnPermissionForResult(activity, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION}, ApzPlugin.APZ_REQ_LOCATION, new OnPermissionsResultHandler() {

                    @Override
                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == ApzPlugin.APZ_REQ_LOCATION) {
                            boolean denied = false;
                            boolean never_ask_again = false;
                            for (String permission : permissions) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                                    denied = true;
                                } else {
                                    if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                                        //callCamera();
                                    } else {
                                        never_ask_again = true;
                                    }
                                }
                            }
                            if (never_ask_again) {
                                PermissionDeniedCallback();
                            } else if (denied) {
                                displayReconfirmationMessage();
                            } else {
                                startTrackingLocation();
                            }
                        } else {
                            PermissionDeniedCallback();
                        }
                    }
                }
        );
    }

    private void displayReconfirmationMessage() {
        String message = "To access location ,allow app to access by requested permissions";
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
        ApzPluginUtil.sendPermissionDenied("Location",callbackId, this.activity,this.webView);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if(plugin == null){
            plugin = new ApzTrackLocation(webView, activity);
        }
        return plugin;
    }

    public static boolean isPlugin() {
        return true;
    }
}
