package com.iexceed.common;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.iexceed.appzillonapp.AppzillonMainScreen.IS_APP_INITIALIZED;
import static com.iexceed.appzillonapp.AppzillonMainScreen.serverErrorAlertDialog;
import static com.iexceed.appzillonapp.AppzillonMainScreen.webView;

/**
 * Created by natasha.dawra on 3/2/18.
 */

public class GetAppSecToken extends AsyncTask<Void, Void, Void> {

    private PostAppSecToken postObj;
    private static boolean isWebViewLoaded = false;
    private boolean isRefreshServerNonce = false;
	
	private String APZ_SIGNATURE = "signature";

    private String errorCode = "";

    private String dErrorMessage = "Application tempered, press ok to exit";

    private String dErrorTitle = "Error";

    private final String eCode = "APZ-APP-SIGN-FAULT";

    public GetAppSecToken(PostAppSecToken postAppSecToken, boolean refreshServerNonce) {
        super();
        postObj = postAppSecToken;
        isRefreshServerNonce = refreshServerNonce;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        AppzillonMainScreen.SNONCE = "";
        JSONObject jsonObject = new JSONObject();
        try {

            JSONObject header = new JSONObject();

            header.put(AppzillonMainScreen.APP_ID, StringUtils.getString(StringUtils.APP_ID));
            header.put(AppzillonMainScreen.SESSION_ID, "");
            header.put(AppzillonMainScreen.DEVICE_ID, AppzillonUtils.getDeviceId(AppzillonMainScreen.activity));
           // header.put("requestId", "CSNONCE");
            header.put("async", false);
            header.put(AppzillonMainScreen.USER_ID, "");
            header.put("screenId", "Login");
            header.put(AppzillonMainScreen.REQ_STATUS, true);
            header.put("source", "APPZILLON");
            header.put("clientNonce", System.currentTimeMillis() + "");
            header.put(AppzillonMainScreen.INTERFACE_ID, "appzillonGetAppSecTokens");
            header.put(AppzillonMainScreen.OS, AppzillonMainScreen.ANDROID_OS);
            header.put("origination", AppzillonUtils.ipAddress(AppzillonMainScreen.activity));
			header.put(APZ_SIGNATURE, AppzillonUtils.getCurrentSong(AppzillonMainScreen.activity.getApplicationContext()));


            JSONObject reqBody = new JSONObject();
            JSONObject appFileReq = new JSONObject();
            appFileReq.put(AppzillonMainScreen.APP_ID, StringUtils.getString(StringUtils.APP_ID));
            appFileReq.put(AppzillonMainScreen.DEVICE_ID, AppzillonUtils.getDeviceId(AppzillonMainScreen.activity));
            appFileReq.put("requestId", "CSNONCE");

            reqBody.put("appzillonGetAppSecTokensRequest", appFileReq);

            jsonObject.put(AppzillonMainScreen.APPZILLON_HEADER, header);
            jsonObject.put(AppzillonMainScreen.APPZILLON_BODY, reqBody);
        } catch (Exception jsonException) {
            
        }
//
        String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
//
        JSONObject response = ServerUtilities.sendRequestToServer(serverUrl, jsonObject.toString());
        if (response != null) {
//
            try {
                JSONObject header = response.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
                boolean status = header.getBoolean("status");

                if (status) {
                    JSONObject apzBody = response.getJSONObject(AppzillonMainScreen.APPZILLON_BODY);
                    JSONObject apzResponse = apzBody.getJSONObject("appzillonGetAppSecTokensResponse");
                    AppzillonMainScreen.SNONCE = apzResponse.getString("serverNonce");
                    AppzillonMainScreen.SESSIONTOKEN = apzResponse.getString("sessionToken");
                    if("Y".equalsIgnoreCase(StringUtils.getString("payloadEncryption"))){
                        AppzillonMainScreen.APPZILLONSAFE = apzResponse.getString("safeToken");
                    }
                } else {
                    JSONArray errArray = response.getJSONArray(AppzillonMainScreen.APPZILLON_ERRORS);
                    JSONObject error = errArray.getJSONObject(0);
                    String errorMessage = error.getString("errorMessage");
					errorCode = error.getString("errorCode");
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
               
            }

        } else {
            //Log.e("GetAppSecureToken", "GetAppSecureToken is null ");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!TextUtils.isEmpty(AppzillonMainScreen.SNONCE)) {
            if(IS_APP_INITIALIZED && !isRefreshServerNonce){
                webView.loadUrl("javascript:apz.server.setAppSecToken('Y');");
            }
            if (!isWebViewLoaded) {
                postObj.executeAfterAppSecToken();
            }
            //loadWebView();
        }   else {

                if(AppzillonMainScreen.activity.getResources().getString(R.string.OFFLINESUPPORT).equalsIgnoreCase("Y") && !isWebViewLoaded){
                    isWebViewLoaded = true;
                    postObj.executeAfterAppSecToken();
                }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new GetAppSecToken(postObj, false).execute();
                }
            }, 5000);

        }
    }
}


