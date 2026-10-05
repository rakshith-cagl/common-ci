package com.iexceed.plugins.sms;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

public class Sms extends ApzPlugin {

    private String mAction;
    private JSONObject mParams;
    private String mCallbackId;

    private static ApzPlugin pluginObj;


    private Sms(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new Sms(webView, activity);
        }
        return pluginObj;
    }


    @Override
    public void execute(JSONObject params) {
        String action = "";
        try {
            mParams = params;
            callbackId = params.getString("id");
            mCallbackId = callbackId;
            mAction = params.getString("action");

            proceedSMS();
        } catch (Exception e) {
            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-329", null, this.activity,
                    this.webView, true);
        }
    }

    private void proceedSMS(){
        try{
            if (mAction.equals("SEND")) {
                sendSms(mParams);
            } else if (mAction.equals("STARTLISTENER")) {
                startListener(mParams);
            } else if (mAction.equals("STOPLISTENER")) {
                stopListener(mParams);
            }
        }catch (Exception e){
            ApzPluginUtil.sendError(mCallbackId, "APZ-CNT-329", null, this.activity,
                    this.webView, true);
        }

    }

    public void sendSms(JSONObject smsJson) {
        String mphoneno = "";
        String message = "";
        final String error_msg = "SERVICE NOT AVAILABLE";
        try {
            mphoneno = smsJson.getString("phoneNo");
            message = smsJson.getString("message");
            //marg = smsJson.getString("type").toUpperCase();
            if(mphoneno.equalsIgnoreCase("") || message.equalsIgnoreCase("")){
                ApzPluginUtil.sendError(callbackId, "APZ-CNT-171", null,
                        this.activity, this.webView, true);
                return;
            }
            // callbackId = smsJson.getString("id");
        } catch (JSONException e) {
            ApzLogger.e(TAG,e.toString());
            ApzPluginUtil.sendError(callbackId, "APZ-CNT-077", null,
                    this.activity, this.webView, true);
        }
        try {
            /*// new code
            Uri uri = Uri.parse("smsto:" + mphoneno);
            Intent it = new Intent(Intent.ACTION_SENDTO, uri);
            it.putExtra("sms_body", message);
            activity.startActivity(it);*/
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("sms", mphoneno, null));
            smsIntent.putExtra("sms_body", message);
            activity.startActivity(smsIntent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity.getApplicationContext(), error_msg,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        String senderNum = "";
        String message = "";

        @Override
        public void onReceive(Context context, Intent intent) {

            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                        Status status = (Status) bundle.get(SmsRetriever.EXTRA_STATUS);
                        switch(status.getStatusCode()) {
                            case CommonStatusCodes.SUCCESS:
                                // Get SMS message contents
                                String message = (String) bundle.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                                // Extract one-time code from the message and complete verification
                                // by sending the code back to your server.

                                JSONObject jsonObj = new JSONObject();
                                jsonObj.put("number", "");
                                jsonObj.put("message", message);
                                jsonObj.put("event", "messageReceived");

                                message = "";
                                senderNum = "";
                                ApzPluginUtil.sendSuccess(Sms.this.callbackId, jsonObj,
                                        true, Sms.this.activity, Sms.this.webView, true);
                                break;

                            case CommonStatusCodes.TIMEOUT:
                                // Waiting for SMS timed out (5 minutes) Handle the error ...
//								Log.e(TAG,"TIMEOUT");
                                break;
                        }
                    }


                }

            } catch (Exception e) {
                ApzLogger.e("SmsReceiver", "Exception smsReceiver" + e);

            }

        }

    };

    public void startListener(JSONObject params) {
        SmsRetrieverClient client = SmsRetriever.getClient(activity);

//		activity.registerReceiver(broadcastReceiver, intentFilter);

        if(broadcastReceiver == null){
//            broadcastReceiver = new broadcastReceiver();
            Log.e(TAG,"broadcastReceiver is null");
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        Context appContext = activity.getApplicationContext();
        appContext.registerReceiver(broadcastReceiver, intentFilter);

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                JSONObject json = null;
                try {
                    json = new JSONObject();
                    json.put("event", "started");
                } catch (JSONException e) {
                    ApzLogger.e("SMS",e.toString());
                }
                ApzPluginUtil.sendSuccess(Sms.this.callbackId, json, true,
                        Sms.this.activity, Sms.this.webView, true);
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                JSONObject json = null;
                try {
                    json = new JSONObject();
                    json.put("event", "started");
                } catch (JSONException ex) {
                    ApzLogger.e("SMS",ex.toString());
                }
                ApzPluginUtil.sendSuccess(Sms.this.callbackId, json, false,
                        Sms.this.activity, Sms.this.webView, true);
            }
        });

    }

    public void stopListener(JSONObject result) {
        JSONObject json = new JSONObject();
        try {
            activity.unregisterReceiver(broadcastReceiver);
            json.put("event", "stopped");
        } catch (JSONException e) {
            ApzLogger.e("SMS",e.toString());
        }
        ApzPluginUtil.sendSuccess(Sms.this.callbackId, json, false,
                Sms.this.activity, Sms.this.webView, true);

    }

    private void handleSuccess() {
        JSONObject sJson = new JSONObject();
        try {
            sJson.put("status", "Success");
        } catch (JSONException e) {
        }
        ApzPluginUtil.sendSuccess(Sms.this.callbackId, sJson, false,
                Sms.this.activity, Sms.this.webView, true);
    }
    public static boolean IsSMS() {
        // TODO Auto-generated method stub
        return true;
    }

}
