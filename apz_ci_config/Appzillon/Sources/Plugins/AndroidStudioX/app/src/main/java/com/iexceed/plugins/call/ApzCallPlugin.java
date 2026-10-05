package com.iexceed.plugins.call;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebView;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

public class ApzCallPlugin extends ApzPlugin {

    private static ApzPlugin pluginObj;

    private static String phoneNumber;

    private ApzCallPlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzCallPlugin(webView, activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {
        try {
            this.callbackId = params.getString("id");
            this.phoneNumber = params.getString("phoneNo");

            if(isNetworkAvailable()) {
                MakeCall();
            }else{
                Log.i(TAG, "No Network available.");
                JSONObject json = new JSONObject();
                try {
                    json.put("text", "Cellular network not available");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-059", json, this.activity,
                        this.webView, true);
            }
        } catch (JSONException e) {
            ApzLogger.e("ApzCallPlugin", e.toString());
        }
    }

    void MakeCall() {
        try{
            final String contact = phoneNumber;

            final Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + contact));
            this.activity.startActivity(intent);
        }catch(ActivityNotFoundException e){
            ApzLogger.e(TAG,"Making Call, Call failed"+ e.toString());
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            //Log.e("Network Testing", "***Available***");
            return true;
        }
        //Log.e("Network Testing", "***Not Available***");
        return false;
    }


    public static boolean IsCall() {
        // TODO Auto-generated method stub
        return true;
    }
}
