package com.iexceed.plugins.notifydownload;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.BuildConfig;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by siddaiahswamy.patil on 20-Sep-18.
 */

public class NotifyDownload extends ApzPlugin {
    private static ApzPlugin pluginObj;
    private JSONObject mParams;
    private String mCallbackId;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    private int id = 1;
    private static String CHANNEL_ID="123";
    private static String CHANNEL_NAME="Notification Channel";
  //  private String[] permissions;
    private List<String> permissions=new ArrayList<String>();

    private NotifyDownload(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new NotifyDownload(webView, activity);
        }
        return pluginObj;
    }


    @Override
    public void execute(JSONObject params) {
        try {
            mParams = params;
            callbackId = params.getString("id");
            mCallbackId = callbackId;
            boolean conditionCheck = false;

            if ( (("N".equalsIgnoreCase(activity.getResources().getString(R.string.INTERNALSANDBOX)))
                    )) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                     //   requestForPermission();
                    }
                } else {
                        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO)
                                != PackageManager.PERMISSION_GRANTED ||
                                ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
                                        != PackageManager.PERMISSION_GRANTED ||
                                ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                                        != PackageManager.PERMISSION_GRANTED) {
                            permissions.add(Manifest.permission.READ_MEDIA_VIDEO);
                            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
                            permissions.add(Manifest.permission.READ_MEDIA_AUDIO);

                        }

                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                            != PackageManager.PERMISSION_GRANTED) {

                        permissions.add(Manifest.permission.POST_NOTIFICATIONS);
                    }
                }
                if (!permissions.isEmpty()) {
                    requestForPermission();
                }else{
                    notifyDownload();
                }
            }else{
            notifyDownload();
        }

        // Android 13 storage change
        // notifyDownload();
    } catch (Exception e1) {
         //   Log.i("Tag","exception"+e1);
        }
    }

    private void notifyDownload() {
        try {
            String action = mParams.getString("action");
            if (action.equalsIgnoreCase("START")) {
                startNotify();
            } else {
                stopNotify();
            }
        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

    public void startNotify() {
        String title = "";
        String content = "";
        try {
            title = mParams.getString("title");
            content = mParams.getString("content");
            mNotifyManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                mNotifyManager.createNotificationChannel(channel);
            }
            mBuilder = new NotificationCompat.Builder(activity);
            mBuilder.setContentTitle(title).setContentText(content).setSmallIcon(R.drawable.appicon);
            // Start a lengthy operation in a background thread
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mBuilder.setChannelId(CHANNEL_ID);
            }
            mBuilder.setProgress(0, 0, true);
            mNotifyManager.notify(id, mBuilder.build());
            mBuilder.setAutoCancel(true);
        } catch (Exception e) {
            JSONObject json = new JSONObject();
            try {
                json.put("text", "Error in starting notifier");
            } catch (JSONException e1) {

            }
            ApzPluginUtil.sendError(callbackId, "", json, activity, webView, true);
        }
    }

    public void stopNotify() {
        String title = "";
        String content = "";
        String filePath = "";
        String mime = "";
        try {
            title = mParams.getString("title");
            content = mParams.getString("content");
            filePath = mParams.getString("filePath");
            mime = mParams.getString("mimeType");

            File file = new File(filePath);
            Intent intent = new Intent();
            if (file.exists()) {
                Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID, file);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, mime);

                PendingIntent pIntent = null;
                pIntent = PendingIntent.getActivity(activity, 0, intent, 0|PendingIntent.FLAG_IMMUTABLE);

                if (mBuilder != null && mNotifyManager != null) {
                    mBuilder.setContentTitle(title);
                    mBuilder.setSmallIcon(R.drawable.appicon);
                    mBuilder.setContentIntent(pIntent);
                    mBuilder.setContentText(content)
                            .setProgress(0, 0, false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mBuilder.setChannelId(CHANNEL_ID);
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                CHANNEL_NAME,
                                NotificationManager.IMPORTANCE_DEFAULT);
                        mNotifyManager.createNotificationChannel(channel);
                    }
                    mNotifyManager.notify(id, mBuilder.build());
                } else {
                    //Log.d("TAG","Notification inactive");
                }
            } else {
                ApzPluginUtil.sendError(callbackId, "APZ-CNT-002", null, activity, webView, true);
                return;
            }
        } catch (JSONException e) {
            JSONObject json = new JSONObject();
            try {
                json.put("text", "Error in stopping notifier");
            } catch (JSONException e1) {

            }
            ApzPluginUtil.sendError(callbackId, "", json, activity, webView, true);
        }
    }

    private void requestForPermission() {
        String[] permission = (String[]) permissions.toArray(new String[0]);

        this.activity.startOnPermissionForResult(activity, permission, ApzPlugin.APZ_REQ_WRITE_STORAGE, new OnPermissionsResultHandler() {

                    @Override
                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == ApzPlugin.APZ_REQ_WRITE_STORAGE) {
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
                                notifyDownload();
                            }
                        } else {
                            PermissionDeniedCallback();
                        }
                    }
                }
        );
    }

    private void displayReconfirmationMessage() {
        String message = "To read files, grant permission for app to send Notifications.";
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
        ApzPluginUtil.sendPermissionDenied("Notify download",callbackId, this.activity,this.webView);
    }
}
