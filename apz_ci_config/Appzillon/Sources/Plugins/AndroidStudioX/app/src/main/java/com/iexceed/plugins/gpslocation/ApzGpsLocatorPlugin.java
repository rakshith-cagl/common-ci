package com.iexceed.plugins.gpslocation;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.camera.ApzCameraPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzGpsLocatorPlugin extends ApzPlugin {

    private GpsLocator mGpsLocator;

    private String action;

    private static ApzPlugin pluginObj;
    private JSONObject mJsonObj;

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
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED ) {
                    requestForPermission();
                } else {
                    enableGPS();
                }
            } else {
                enableGPS();
            }
        } catch (JSONException ex) {
        }
    }

    private void enableGPS(){
        if (action.equals("START")) {
            ApzLogger.d(TAG, "startGpsLocationListener");
            if (mGpsLocator == null) {
                mGpsLocator = new GpsLocator(activity, webView);
                mGpsLocator.getCoordinates(mJsonObj);
            } else if (GpsLocator.isRunning) {
                ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-017", null, this.activity, this.webView, true);//GPS Running
            } else {
                mGpsLocator.getCoordinates(mJsonObj);
            }
        } else if (action.equals("STOP")) {
            ApzLogger.d(TAG, "stoptGpsLocationListener");
            if (GpsLocator.isRunning) {
                mGpsLocator.stop();
                mGpsLocator = null;
                JSONObject json = new JSONObject();
                try {
                    json.put("text", "GPS stopped");
                } catch (JSONException e) {
                    
                }
                ApzPluginUtil.sendSuccess(callbackId, json, false, activity, webView, true);
            } else {
                if (mGpsLocator != null)
                    mGpsLocator = null;
                ApzPluginUtil.sendError(callbackId, "APZ-CNT-057", null, this.activity, this.webView, true);//GPS already stopped
            }
        }
    }

    public static boolean isGpsLocator() {
        return true;
    }

    private void requestForPermission() {
        this.activity.startOnPermissionForResult(activity, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, ApzPlugin.APZ_REQ_FINE_LOCATION, new OnPermissionsResultHandler() {

                    @Override
                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == ApzPlugin.APZ_REQ_FINE_LOCATION) {
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
