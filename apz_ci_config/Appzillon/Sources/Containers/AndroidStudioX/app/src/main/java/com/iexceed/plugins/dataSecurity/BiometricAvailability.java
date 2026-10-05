package com.iexceed.plugins.dataSecurity;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.webkit.WebView;

import androidx.biometric.BiometricManager;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by natasha.dawra on 8/2/18.
 */

public class BiometricAvailability extends ApzPlugin{

    private static ApzPlugin pluginObj;

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if(pluginObj == null){
            pluginObj = new BiometricAvailability(webView, activity);
        }
        return pluginObj;
    }

    public BiometricAvailability(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    @Override
    public void execute(JSONObject params) {
        try {
            callbackId = params.getString("id");
        } catch (JSONException e) {
           
        }
        checkBiometric();
    }

    public void checkBiometric() {
        BiometricManager biometricManager = BiometricManager.from(activity.getApplicationContext());
        int biometricStatus =biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        if (biometricStatus == BiometricManager.BIOMETRIC_SUCCESS){

        }
        KeyguardManager keyguardManager = (KeyguardManager) activity.getApplicationContext()
                .getSystemService(Context.KEYGUARD_SERVICE);

        if (Build.VERSION.SDK_INT > 22) {


            if (biometricManager != null) {
                if (biometricStatus ==BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE || biometricStatus==BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
                    callback("NOTSUPPORTED");
                } else if (!keyguardManager.isKeyguardSecure()) {
                    callback("NOTSUPPORTED");
                }/*else if (biometricStatus ==BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ) {
                    callback("NOTSUPPORTED");
                } */else if (biometricStatus ==BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                    callback("NOTCONFIGURED");
                } else {
                    callback("TOUCHID");
                }
            } else {
                callback("NOTSUPPORTED");
            }
        }else{
            callback("NOTSUPPORTED");
        }
    }

    public void callback(String message){
        JSONObject json = null;
        try{
            json = new JSONObject();
            json.put("biometricStatus",message);
        }catch(JSONException ex){

        }
        ApzPluginUtil.sendSuccess(callbackId,json,false,activity,webView,true);
    }
}
