package com.iexceed.plugins.camera;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.webkit.WebView;

import com.iexceed.appzillonapp.R;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.ExternalActivityResultHandler;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.common.UserSettings;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONException;
import org.json.JSONObject;

public class ApzCameraPlugin extends ApzPlugin {

    private final String TAG = "ApzCameraPlugin";
    private static ApzPlugin pluginObj;
    public static final int TAKE_PIC = 10;
    private JSONObject mJsonObj;
    private String[] permissions;

    private ApzCameraPlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzCameraPlugin(webView, activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(final JSONObject params) {
        try {
            this.callbackId = params.getString("id");
            this.mJsonObj = params;
            boolean isSourceTypeExternal = false;
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                if (params.optString("sourceType").equalsIgnoreCase("Photo")) {
                    //isSourceTypeExternal = true;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ) {
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
                            requestForPermission();
                        } else {
                            callCamera();
                        }
                    } else {
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
                            requestForPermission();
                        } else {
                            callCamera();
                        }

                    }
                } else if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions = new String[]{Manifest.permission.CAMERA};
                    requestForPermission();
                } else {
                    callCamera();
                }
                /*if ("N".equalsIgnoreCase(activity.getResources().getString(R.string.INTERNALSANDBOX)) && (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
                    permissions = new String[]{
                            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestForPermission();
                } else if (isSourceTypeExternal && (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
                    permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestForPermission();
                } else if (!isSourceTypeExternal && ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions = new String[]{
                            Manifest.permission.CAMERA};
                    requestForPermission();
                } else {
                    callCamera();
                }*/

            } else {
                callCamera();
            }
//			ApzLogger.e(TAG, "Plugin Payload "+ params);
        } catch (Exception e) {
            ApzLogger.e(TAG, e.getMessage());
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-211", null, activity, webView, true);
        }

    }

    private void requestForPermission() {
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
                                callCamera();
                            }
                        } else {
                            PermissionDeniedCallback();
                        }

                    }
                }
        );
    }

    private void displayReconfirmationMessage() {
        String message = "To capture image/select image from gallery, allow app to access by requested permissions";

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
        ApzPluginUtil.sendPermissionDenied("Camera or Storage ",callbackId, this.activity,this.webView);
    }

    private void callCamera() {
        Intent camera = new Intent(activity, NativeCamera.class);
        camera.putExtra("jsonStr", mJsonObj.toString());

//		Intent camera = new Intent(activity, MyNativeCamera.class);
//		activity.startActivity(camera);

        this.activity.startActivityForResult(camera, TAKE_PIC, new ExternalActivityResultHandler() {

            @Override
            public void handleActivityResult(int resultCode, Intent data) {
                // TODO Auto-generated method stub

                try {
                    if (resultCode == Activity.RESULT_OK) {
                        String imgString = UserSettings
                                .getBase64Image(ApzActivity.settings);
                        try {
                            JSONObject resultBody = new JSONObject();
                            if (mJsonObj.getString("action").equalsIgnoreCase(
                                    "base64")) {
                                resultBody.put("encodedImage", imgString);
                                resultBody.put("path", "");
                                resultBody.put("successMessage", "");
                            } else if (mJsonObj.getString("action")
                                    .equalsIgnoreCase("save")) {
                                String url = data.getStringExtra("url");
                                resultBody.put("encodedImage", "");
                                resultBody.put("path", url);
                                resultBody.put("successMessage", "");
                            } else if (mJsonObj.getString("action")
                                    .equalsIgnoreCase("base64_Save")) {
                                String url = data.getStringExtra("url");
                                resultBody.put("successMessage", "");
                                resultBody.put("path", url);
                                resultBody.put("encodedImage", imgString);
                            } else if (mJsonObj.getString("action")
                                    .equalsIgnoreCase("srcBase64")) {
                                String idName = data.getStringExtra("idName");
                                resultBody.put("successMessage", "Success");
                                resultBody.put("path", "");
                                resultBody.put("encodedImage", "");
                                webView.loadUrl("javascript:(function() { "
                                        + "$('#"
                                        + idName
                                        + "').attr('src','data:image/jpg;base64,"
                                        + imgString + "');}))");
                            } else if (mJsonObj.getString("action")
                                    .equalsIgnoreCase("srcUrl")) {
                                String idName = data.getStringExtra("idName");
                                String url = data.getStringExtra("url");
                                resultBody.put("successMessage", "Success");
                                resultBody.put("path", "");
                                resultBody.put("encodedImage", "");
                                webView.loadUrl("javascript:(function() { "
                                        + "$('#" + idName + "').attr('src','"
                                        + url + "');})");
                            }
                            ApzPluginUtil.sendSuccess(
                                    callbackId,
                                    resultBody, false,
                                    activity,
                                    webView, true);
                            pluginObj = null;
                        } catch (final Exception ex) {
                            return;
                        }
                        UserSettings.deleteBase64Image(ApzActivity.settings);
                        String str = UserSettings.getBase64Image(ApzActivity.settings);

                    } else {
                        JSONObject json = new JSONObject();
                        try {
                            json.put("text", "Operation Cancelled");
                        } catch (JSONException e) {
                        }
                        ApzPluginUtil.sendError(callbackId, "APZ-CNT-211", json, activity, webView, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public static boolean IsCamera() {
        return true;
    }

}
