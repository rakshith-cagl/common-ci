package com.iexceed.plugins.networkmonitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.webkit.WebView;

import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class MonitorNetwork extends BroadcastReceiver {
    Activity mActivity;
    JSONObject mJsonObject;
    WebView mWebView;
    String networkStatus = "";
    String mCallbackId;
    public MonitorNetwork(Activity activity, WebView webView, JSONObject jsonObject) {
        mActivity = activity;
        mWebView = webView;
        mJsonObject = jsonObject;
        try {
            mCallbackId = (String) jsonObject.get("id");
        } catch (JSONException e) {
            //e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            JSONObject networkJson = new JSONObject();
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ConnectivityManager.CONNECTIVITY_ACTION.equalsIgnoreCase(intent.getAction())) {
                if (networkInfo.isConnected()) {
                    if (networkStatus.equalsIgnoreCase("NotConnected") || networkStatus.equalsIgnoreCase("")) {
                        networkStatus = "Connected";
                        networkJson.put("event","on");
                        sendSuccess(networkJson);
                    }
                } else {
                    if (networkStatus.equalsIgnoreCase("Connected") || networkStatus.equalsIgnoreCase("")) {
                        networkStatus = "NotConnected";
                        networkJson.put("event","off");
                        sendSuccess(networkJson);
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void sendSuccess(JSONObject networkJson){
        ApzPluginUtil.sendSuccess(mCallbackId,
                networkJson, true, mActivity,
                mWebView, true);
    }
}

