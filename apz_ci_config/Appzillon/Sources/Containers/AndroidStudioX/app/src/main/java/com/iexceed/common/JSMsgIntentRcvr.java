package com.iexceed.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;

import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This broadcast receiver receive Intent and send the message to JS layer
 * Created by prabudas.s on 2/9/2017.
 */

public class JSMsgIntentRcvr extends BroadcastReceiver {

    protected Activity activity;
    protected WebView webView;

    public JSMsgIntentRcvr(Activity activity, WebView webView){
        super();
        this.activity = activity;
        this.webView = webView;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            String type = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_TYPE);
            String callbackId = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_CBID);
            boolean isInUIThread = intent.getBooleanExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_ISINUITHREAD, true);

            //If success message with JSON Object result
            if(type.equalsIgnoreCase(JSNotifier.APZ_INTENT_JSNOTIFY_SUCCESS_OBJECT)){
                String strResult = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_OBJ);
                JSONObject resutObj = new JSONObject(strResult);
                boolean keepAlive = intent.getBooleanExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_KALIVE, false);

                ApzPluginUtil.sendSuccess(callbackId, resutObj, keepAlive, activity, webView, isInUIThread);

            }
            //If success message with JSON array result
            else if(type.equalsIgnoreCase(JSNotifier.APZ_INTENT_JSNOTIFY_SUCCESS_ARRAY)){
                String strResult = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_ARRAY);
                JSONArray resutObj = new JSONArray(strResult);
                boolean keepAlive = intent.getBooleanExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_KALIVE, false);
                String resultKey = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_RESULTKEY);

                ApzPluginUtil.sendSuccess(callbackId, resutObj, resultKey, keepAlive, activity, webView, isInUIThread);

            }
            //If error message
            else if(type.equalsIgnoreCase(JSNotifier.APZ_INTENT_JSNOTIFY_ERROR)){
                String strResult = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_OBJ);
                JSONObject resutObj = new JSONObject(strResult);
                boolean keepAlive = intent.getBooleanExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_KALIVE, false);
                String errorCode = intent.getStringExtra(JSNotifier.APZ_INTENT_JSNOTIFY_KEY_ERRORCODE);

                ApzPluginUtil.sendError(callbackId, errorCode, resutObj, activity, webView, isInUIThread);

            }else{
            //If Plugin not supported error
                ApzPluginUtil.sendPluginNotSupported(callbackId, activity, webView, isInUIThread);
            }
        } catch (JSONException e) {
            
        }
    }
}
