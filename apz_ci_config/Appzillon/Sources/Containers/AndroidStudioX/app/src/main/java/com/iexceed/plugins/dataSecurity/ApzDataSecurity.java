package com.iexceed.plugins.dataSecurity;
import android.content.SharedPreferences;
import android.os.Build;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by natasha.dawra on 29/1/18.
 */

public class ApzDataSecurity extends ApzPlugin {

    private static JSONObject json;
    private static ApzPlugin pluginObj;
    private static String promptBiometric = "";
    private static ApzActivity sActivtiy;
    private static String sCallbackId;
    private static WebView sWebView;
    private static String action;
    public static String reqId;


    public ApzDataSecurity(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if(pluginObj == null){
            pluginObj = new ApzDataSecurity(webView, activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {
        sActivtiy= activity;
        sWebView = webView;
        try{
            json = params;
            callbackId = params.getString("id");
            sCallbackId = callbackId;
            promptBiometric = params.optString("promptBiometric");
            action = params.getString("action");
            reqId = params.getString("reqId");

        }catch (JSONException ex){
            //Log.e(TAG,ex.getMessage() );
        }
        StoreCredentialsSecurely.init(webView,activity,callbackId,reqId);

        //store key value Securely
        if("STORESECURELY".equalsIgnoreCase(action)){
        if("Y".equalsIgnoreCase(promptBiometric)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                callBiometric(params);
            } else {
                ApzPluginUtil.sendError(callbackId, "APZ-CNT-000", null, activity, webView, true);
            }
        }else{
            secureData();
        }
        }else if("RETRIEVECREDENTIALSSECURELY".equalsIgnoreCase(action)){
            try {
                String isBiometric = params.getJSONObject("params").optString("isBiometric");
                if("Y".equalsIgnoreCase(isBiometric)){
                    callBiometric(params);
                }else {
                    secureData();
                }

            }catch (JSONException ex){

            }
        }else if("STORECREDENTIALSSECURELY".equalsIgnoreCase(action)){
            callBiometric(params);
        }else{
            try{
            if("RETRIEVESECURELY".equalsIgnoreCase(json.getString("action"))){
                SharedPreferences sharedPreferences = activity.getSharedPreferences(StoreCredentialsSecurely.apzPrefs, activity.MODE_PRIVATE);
                String credentials = sharedPreferences.getString(json.getString("key"),"");
                JSONObject jsonObject = new JSONObject(credentials);
                String prompBipmetricretrieve = jsonObject.getString("promptBiometric");
                if("Y".equalsIgnoreCase(prompBipmetricretrieve)){
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        callBiometric(params);
                    }else{
                        ApzPluginUtil.sendError(callbackId,"APZ-CNT-000",null,activity,webView,true);
                    }
                }else{
                    secureData();
                }

            }else{
                secureData();
            }
            }catch (JSONException ex){

            }
        }
    }

    public static void secureData(){
        String action = "";
        String key = "";
        String value = "";
        try{
            action = json.getString("action");

            if("STORESECURELY".equalsIgnoreCase(action)){
                key = json.getString("key");
                value = json.getString("value");
                StoreCredentialsSecurely.storeSecurely(key,value,promptBiometric);

            }else if("STORECREDENTIALSSECURELY".equalsIgnoreCase(action)){
                key = json.getString("userId");
                value = json.getString("password");
                StoreCredentialsSecurely.storeCredentialsSecurely(key,value);

            }else if("RETRIEVESECURELY".equalsIgnoreCase(action)){
                String encryptedString = "";
                key = json.getString("key");
                StoreCredentialsSecurely.retrieveSecurely(key);

            }else if("RETRIEVECREDENTIALSSECURELY".equalsIgnoreCase(action)){
                JSONObject reqFull;
                    reqFull = json.getJSONObject("params").getJSONObject("reqFull");
                    String isBiometric = json.getJSONObject("params").optString("isBiometric");
                    Boolean isBiometricbool ;
                    if("Y".equalsIgnoreCase(isBiometric)){
                    isBiometricbool =  true;
                    }else{
                    isBiometricbool = false;
                }
                    StoreCredentialsSecurely.loginSecurely(reqFull,isBiometricbool);
            }
        }catch (Exception ex){
            //Log.e("ApzDataSecurity", ex.getMessage());
        }
    }

    public static void callBiometric(JSONObject params){
        BiometricAuth biometricAuth = new BiometricAuth(sWebView, sActivtiy, sCallbackId);
        biometricAuth.biometricAuthentication(params);
    }

    public static boolean isPlugin(){
        return true;
    }
}

