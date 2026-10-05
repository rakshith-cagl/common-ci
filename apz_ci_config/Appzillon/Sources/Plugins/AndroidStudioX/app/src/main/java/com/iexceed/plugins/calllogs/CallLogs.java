package com.iexceed.plugins.calllogs;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.CallLog;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.webkit.WebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;


import static android.Manifest.permission.READ_CALL_LOG;



public class CallLogs extends ApzPlugin {
//test
    private static ApzPlugin pluginObj;
    public static TelephonyManager mTelephonyMgr = null;

    private String[] permissions;

    public static String TAG = "CallLogPlugin";
    private static String callType=null;

    private CallLogs(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
    {  if(pluginObj == null){
        pluginObj = new CallLogs(webView, activity);
    }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {
        try {
            this.callbackId = params.getString("id");
            this.callType=params.getString("callType");
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                if (ActivityCompat.checkSelfPermission(activity, READ_CALL_LOG)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions = new String[]{
                            Manifest.permission.READ_CALL_LOG};
                    requestForPermission();
                } else {
                    getCallLog();
                }
            } else {
                getCallLog();
            }

        } catch (JSONException e1) {
        }
    }

    private void requestForPermission() {
        this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_CALL_PHONE, new OnPermissionsResultHandler() {

                    @Override
                    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                        if (requestCode == ApzPlugin.APZ_REQ_CALL_PHONE) {
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
                                getCallLog();
                            }
                        } else {
                            PermissionDeniedCallback();
                        }
                    }
                }
        );
    }

    private void displayReconfirmationMessage() {
        String message = "To get call logs, grant permission for app to access call logs on device";
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
        ApzPluginUtil.sendPermissionDenied("Call Logs",this.callbackId, this.activity,this.webView);
    }
    public static boolean IsCallLog() {
        return true;
    }

    private void getCallLog() {
        getCallLogs(this.callbackId,
                this.webView,
                this.activity);
    }


    public static void getCallLogs(String callbackId, WebView webview , Activity activity){

        TelephonyManager tm= (TelephonyManager)activity.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if(tm.getPhoneType()==TelephonyManager.PHONE_TYPE_NONE){
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-022", null, activity, webview, true);
        }else {
            String where;

            if(callType.equalsIgnoreCase("MISSED")){
                where = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE;
            }else if(callType.equalsIgnoreCase("OUTGOING")){
                where = CallLog.Calls.TYPE + "=" + CallLog.Calls.OUTGOING_TYPE;
            }else if(callType.equalsIgnoreCase("INCOMING")){
                where = CallLog.Calls.TYPE + "=" + CallLog.Calls.INCOMING_TYPE;
            }else{
                where = null;
            }
            Cursor cursor = activity.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, where, null, null);

            // Read the sms data and store it in the list
            JSONObject logs = null;
            try {
                logs = new JSONObject();
                JSONArray callArray = new JSONArray();
                if (cursor.moveToFirst()) {
                    for (int i = 0; i < cursor.getCount(); i++) {
                        // SMSData sms = new SMSData();
                        JSONObject call = new JSONObject();
                        String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
                        String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));

                        call.put("number", callNumber);
                        call.put("timestamp", callDate);

                        callArray.put(call);

                        cursor.moveToNext();
                    }
                }

                logs.put("logs", callArray);
                logs.put("type", callType);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
            }
            cursor.close();
            final JSONObject resultObj = logs;
            ApzPluginUtil.sendSuccess(callbackId, resultObj, false, activity, webview, true);
        }
    }
}
