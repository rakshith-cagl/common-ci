package com.iexceed.plugins.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.webkit.WebView;

import com.iexceed.common.StringUtils;
import com.iexceed.common.AppzillonUtils;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.GetAppSecToken;
import com.iexceed.common.PostAppSecToken;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.ServerUtilities;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mishra.abhishek on 28/11/17.
 */

public class NativeServerCall extends ApzPlugin {

    private static ApzPlugin pluginObj;

    private WebView mWebView;

    private Activity mActivity;

    private String serverURL;

    private String reqID;

    private JSONObject reqJson;

    public NativeServerCall(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }


    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {

        if (pluginObj == null) {
            pluginObj = new NativeServerCall(webView, activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {
        this.mWebView = webView;
        this.mActivity = activity;
        String command;
        try {
            this.callbackId = params.getString("id");
            command = params.getString("command");
            if ("PLGN_REFSVRNONCE".equalsIgnoreCase(command)) {
                refreshServerNonce(params);
            } else {
                reqID = params.getString("reqId");
                serverURL = StringUtils.getString(StringUtils.SERVER_URL);
                reqJson = params.getJSONObject("params").getJSONObject("reqFull");
                if (AppzillonUtils.isNetworkAvailable(this.mActivity)) {
                    //new ServerCall(callbackId, reqID).execute(reqJson.toString());
					new ServerCall(callbackId,reqID).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,reqJson.toString());
                } else {
                    final JSONObject json = new JSONObject();
                    json.put("id", this.callbackId);
                    json.put("keepAlive", false);
                    json.put("reqId", reqID);

                    JSONObject resFull = new JSONObject();
                    resFull.put("status", false);
                    resFull.put("resFull", reqJson);
                    json.put("status", false);

                    ApzPluginUtil.sendError(callbackId, "APZ-CNT-233", json, mActivity, mWebView, true);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block

        }
    }

    private class ServerCall extends AsyncTask<String, Void, JSONObject> {

        String callbackId = "";
        String requestId = "";

        public ServerCall(String callbackId, String requestId) {
            super();
            this.callbackId = callbackId;
            this.requestId = requestId;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject response = null;
            try {
                JSONObject json = new JSONObject(params[0]);
                response = ServerUtilities.sendRequestToServer(serverURL, json.toString());
            } catch (JSONException e) {
                return response;
            }
            return response;
        }

        @Override
        protected void onPostExecute(final JSONObject response) {
            super.onPostExecute(response);
            final JSONObject json = new JSONObject();
            try {
                json.put("id", this.callbackId);
                json.put("keepAlive", false);
                json.put("reqId", this.requestId);
                if (response != null) {

                    if (response.has("errorCode")) {
                        JSONObject resFull = new JSONObject();
                        resFull.put("status", false);
                        resFull.put("resFull", response);
                        json.put("params", resFull);
                        json.put("status", false);
                        json.put("errorCode", response.getString("errorCode"));
                    }

                    JSONObject resFull = new JSONObject();
                    resFull.put("status", true);
                    resFull.put("resFull", response);
                    json.put("params", resFull);
                    json.put("status", true);
                } else {
                    JSONObject resFull = new JSONObject();
                    resFull.put("status", false);
                    resFull.put("resFull", reqJson);
                    json.put("status", false);
                }
                ApzPluginUtil.sendSuccess(this.callbackId, json, false, mActivity, mWebView, true);
            } catch (Exception e) {

            }

        }
    }

    public void refreshServerNonce(JSONObject json) {
        new GetAppSecToken(new PostAppSecToken() {
            @Override
            public void executeAfterAppSecToken() {
                if (!TextUtils.isEmpty(AppzillonMainScreen.SNONCE)) {
                    ApzPluginUtil.sendSuccess(callbackId, null, false, activity, webView, true);
                } else {
                    ApzPluginUtil.sendError(callbackId, "APZ-CNT-330", null, activity, webView, true);
                }
            }
        },true).execute();
    }
}

