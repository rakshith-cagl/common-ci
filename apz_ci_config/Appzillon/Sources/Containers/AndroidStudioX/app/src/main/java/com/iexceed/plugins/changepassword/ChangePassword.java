package com.iexceed.plugins.changepassword;

import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.ServerUtilities;
import com.iexceed.common.StringUtils;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.encryption.ApzEncryptionPlugin;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by natasha.dawra on 14/2/18.
 */

public class ChangePassword extends ApzPlugin{

    private static ApzPlugin pluginObj;

    public ChangePassword(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if(pluginObj == null){
            pluginObj = new ChangePassword(webView, activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {
        String userName = "";
        String password = "";
        String sysDate = "";
        JSONObject appzillonBody = null;
        JSONObject changePasswordRequest = null;
        JSONObject reqfull;
        JSONObject requestJson = null;
        String reqId = "";
        String mCallBackId = "";

        try {
            mCallBackId = params.getString("id");
            reqfull = params.getJSONObject("params").getJSONObject("reqFull");
            appzillonBody = reqfull.getJSONObject(AppzillonMainScreen.APPZILLON_BODY);
            changePasswordRequest = appzillonBody.getJSONObject("changePasswordRequest");
            userName = changePasswordRequest.getString("userId");
            String oldPassword = changePasswordRequest.getString("pwd");
            String newPassword = changePasswordRequest.getString("newPassword");
            sysDate = changePasswordRequest.getString("sysDate");
            reqId = params.getString("reqId");

            if ("#DeviceId".equalsIgnoreCase(StringUtils.getString("authenticationType"))) {
                JSONObject json = new JSONObject();
                try {
                    json.put("userId", userName);
                    json.put("pwd", oldPassword);
                    json.put("date", sysDate);//new Date().

                } catch (JSONException ex) {

                }
                oldPassword = ApzEncryptionPlugin.hashPwdforNativeLogin(json);
            }

            String key = StringUtils.getString(StringUtils.SERVER_TOKEN);

            String paddingMask = "$$$$$$$$$$$$$$$$";
            if (key.length() <= 16) {
                key += paddingMask.substring(0, 16 - key.length());
            }
            if (key.length() > 16) {
                key = key.substring(0, 16);
            }
            newPassword = ApzEncryptionPlugin.encryptPassword(key,newPassword);
            changePasswordRequest.put("pwd",oldPassword);
            changePasswordRequest.put("newPassword",newPassword);
            requestJson = new JSONObject();
            requestJson.put(AppzillonMainScreen.APPZILLON_HEADER, reqfull.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER));
            requestJson.put(AppzillonMainScreen.APPZILLON_BODY,reqfull.getJSONObject(AppzillonMainScreen.APPZILLON_BODY));


        } catch (JSONException e) {
            
        }

        new SendChangePasswordRequest(reqId,mCallBackId).execute(requestJson.toString());
    }

    class SendChangePasswordRequest extends AsyncTask<String,Void,JSONObject>{


        String requestId = "";
        String callBackId = "";

        public SendChangePasswordRequest(String requestId,String callbackId){
            super();
            this.requestId = requestId;
            this.callBackId = callbackId;
        }


        @Override
        protected JSONObject doInBackground(String... strings) {
            String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
            int length = strings[0].length();
            //Log.d(TAG, "doInBackground: length"+length);
            //Log.d(TAG, "doInBackground: reqest"+strings[0]);
            JSONObject response = ServerUtilities.sendRequestToServer(serverUrl,strings[0]);
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            final JSONObject json = new JSONObject();
            try {
                json.put("id", this.callBackId);
                json.put("keepAlive", false);
                json.put("reqId", this.requestId);
                if (response != null) {

                    //If hashing fails
                    if(response.has("errorCode")){
                        JSONObject params = new JSONObject();
                        params.put("status", false);
                        params.put("resFull", response);
                        json.put("params", params);
                        json.put("status", false);
                        json.put("errorCode",response.getString("errorCode"));
                    }else {
                        //Proper response from server
                        JSONObject params = new JSONObject();
                        params.put("status", true);
                        params.put("resFull", response);
                        json.put("params", params);
                        json.put("status", true);
                    }
                } else {
                    //If server response is null
                    JSONObject params = new JSONObject();
                    params.put("status", false);
                    params.put("resFull", new JSONObject());
                    json.put("params", params);
                    json.put("status", false);
                }
                ApzPluginUtil.sendSuccess(this.callBackId, json, false, activity, webView, true);
            } catch (Exception e) {

            }
        }
    }
}


