package com.iexceed.plugins.security;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class WifiSecurity {
    ApzActivity mActivity;
    JSONObject mJsonObject;
    WebView mWebView;
    String networkStatus = "";
    String mCallbackId;
    boolean gps_enabled = false;
    WifiManager wifi;

    public WifiSecurity(ApzActivity activity, WebView webView, JSONObject jsonObject) {
        mActivity = activity;
        mWebView = webView;
        mJsonObject = jsonObject;
        try {
            mCallbackId = (String) jsonObject.get("id");
        } catch (JSONException e) {
        }
    }
    public  void requestGPSpermission() {
        LocationManager lm = (LocationManager)
                mActivity.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
        }
         wifi = (WifiManager) mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestForPermission();

            }else{
                checkGPSStatus();
            }
        }else {
            checkGPSStatus();
        }
    }
    private void checkGPSStatus(){
        if(wifi.isWifiEnabled()) {
            if (gps_enabled) {
                checkWifiStatus();
            } else {
                sendFailure("GPS is not enabled in this device.");
            }
        }else{
            sendFailure("Wifi is not enabled in this device.");
        }
    }
    public void checkWifiStatus() {
        final JSONObject result = new JSONObject();
            try {

                List<ScanResult> networkList = wifi.getScanResults();
                WifiInfo wi = wifi.getConnectionInfo();
                String currentBSSID = wi.getBSSID();
                if (networkList != null && !networkList.isEmpty()) {
                    for (ScanResult network : networkList) {
                        //check if current connected SSID
                        if (currentBSSID.equals(network.BSSID)) {
                            //get capabilities of current connection
                            final String capabilities = network.capabilities;/*
                            Log.d("WifiManager", network.SSID + " capabilities : " + capabilities);*/
                            result.put("WifiStatus", capabilities);
                            sendSuccess(result);
                        }
                    }

                } else {
                    sendFailure("Unable to fetch the details.");
                }
            } catch (Exception e) {
                sendFailure("Unable to fetch the details.");
            }
        }

    private void requestForPermission() {
        mActivity.startOnPermissionForResult(mActivity, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, ApzPlugin.APZ_REQ_FINE_LOCATION, new OnPermissionsResultHandler() {

                    @Override
                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == ApzPlugin.APZ_REQ_FINE_LOCATION) {
                            boolean denied = false;
                            boolean never_ask_again = false;
                            for (String permission : permissions) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
                                    denied = true;
                                } else {
                                    if (ActivityCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                                        never_ask_again = true;
                                    }else{
                                            checkGPSStatus();
                                    }
                                }
                            }
                            if (never_ask_again) {
                                PermissionDeniedCallback();
                            } else if (denied) {
                                displayReconfirmationMessage();
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
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
        ApzPluginUtil.sendPermissionDenied("GPS location ",mCallbackId, this.mActivity,this.mWebView);
    }
  private void sendFailure(String msg){
      JSONObject resultObj = new JSONObject();
      try {
          resultObj.put("error", msg);
      } catch (JSONException ex) {
      }
      ApzPluginUtil.sendError(mCallbackId, "", resultObj, mActivity, mWebView, true);
  }
    private void sendSuccess(JSONObject networkJson){
        ApzPluginUtil.sendSuccess(mCallbackId,
                networkJson, true, mActivity,
                mWebView, true);
    }
}
