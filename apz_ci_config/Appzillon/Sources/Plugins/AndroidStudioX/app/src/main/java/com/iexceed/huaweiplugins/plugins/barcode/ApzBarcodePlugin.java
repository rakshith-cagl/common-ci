package com.iexceed.plugins.barcode;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.webkit.WebView;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;


public class ApzBarcodePlugin extends ApzPlugin {

    private static ApzPlugin pluginObj;
    private String TAG = "ApzBarcodePlugin";
    private JSONObject mJson;
    public static BarcodeScan barcodeScan;
    protected static boolean restartBarcodeScan = false;
    private String[] permissions;
    public String action;

    private ApzBarcodePlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzBarcodePlugin(webView, activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {
        try {
            this.callbackId = params.getString("id");
            this.mJson = params;
            action = params.getString("action");
            if ("START".equalsIgnoreCase(action)) {
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        permissions = new String[]{
                                Manifest.permission.CAMERA};
                        requestForPermission("CAMERA");
                    } else {
                        openBarcode();
                    }
                } else {
                    openBarcode();
                }
            } else if ("STOP".equalsIgnoreCase(action)) {
                if (barcodeScan != null) {
                    barcodeScan.closeLayout();
                }
            } else if ("SCAN_FROM_GALLERY".equalsIgnoreCase(action)) {
                String filePath = mJson.getString("filePath");
                if(!filePath.startsWith(AppzillonMainScreen.internalPathCheck)) {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        permissions = new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestForPermission("SCAN");
                    } else {
                        new ScanImageFromGallery(this.activity, webView, mJson.toString()).scanImage();
                    }
                }else{
                    new ScanImageFromGallery(this.activity, webView, mJson.toString()).scanImage();
                }
            }
        } catch (JSONException e1) {

        }

    }

    private void openBarcode() {
        barcodeScan = new BarcodeScan(this.activity, this.activity, webView, mJson.toString());
        barcodeScan.createLayout();
    }

    public static boolean isBarcodeActivity() {
        return true;
    }

    private void requestForPermission(final String plugin) {
        this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_CAMERA, new OnPermissionsResultHandler() {
            @Override
            public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                if (requestCode == ApzPlugin.APZ_REQ_CAMERA) {
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
                        if (plugin.equalsIgnoreCase("CAMERA")) {
                            openBarcode();
                        }else{
                            new ScanImageFromGallery(activity, webView, mJson.toString()).scanImage();
                        }
                    }
                }else {
                    PermissionDeniedCallback();
                }

            }
        });
    }
    private void displayReconfirmationMessage() {
        String message = "To scan barcode,allow app to access by granting requested permissions";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Permission Denied");
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if("SCAN_FROM_GALLERY".equalsIgnoreCase(action)){
                            requestForPermission("SCAN");
                        }else{
                            requestForPermission("CAMERA");
                        }
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
        ApzPluginUtil.sendPermissionDenied("Barcode",this.callbackId, this.activity,this.webView);
    }

    public static void checkForBarcodeOnPause() {
        if (BarcodeScan.mCamera != null) {
            if (barcodeScan != null) {
                restartBarcodeScan = true;
                barcodeScan.closeLayout();
            }
        }
    }

    public static void checkForBarcodeOnResume() {
        if (restartBarcodeScan) {
            if(barcodeScan != null){
                restartBarcodeScan = false;
                barcodeScan.createLayout();
            }
        }
    }
}

