package com.iexceed.plugins.nativeshare;

import android.content.Intent;
import android.net.Uri;
import com.iexceed.appzillonapp.BuildConfig;
import androidx.core.content.FileProvider;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by natasha.dawra on 7/5/18.
 */

public class ApzNativeShare extends ApzPlugin {

    private static ApzPlugin pluginObj;

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzNativeShare(webView, activity);
        }
        return pluginObj;
    }


    private ApzNativeShare(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public void shareText(JSONObject json) {
        {
            try {
                String action = json.getString("action");
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                if ("text".equalsIgnoreCase(action)) {
                    String text = json.getString("textToShare");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                    sendIntent.setType("text/plain");
                    activity.startActivity(sendIntent);
                }else if("file".equalsIgnoreCase(action)){
                    String path = json.getString("filePath");
                    if(path.isEmpty()){
                        sendFailure("Native Share Failed - FilePath missing");
                    }
                    Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID, new File(path));
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("application/*");
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    activity.startActivity(Intent.createChooser(share, "Share File"));
                }else{
                    sendFailure("Native Share Failed - Action missing");
                }
                JSONObject resultObj = new JSONObject();
                try {
                    resultObj.put("text", "Native share success");
                } catch (JSONException ex) {
                }
                ApzPluginUtil.sendSuccess(callbackId, resultObj, false, activity, webView, true);
            } catch (final Exception e) {
                sendFailure("Native Share Failed");
            }
        }
    }

    void sendFailure(String msg){
        JSONObject resultObj = new JSONObject();
        try {
            resultObj.put("error", msg);
        } catch (JSONException ex) {
        }
        ApzPluginUtil.sendError(callbackId, "", resultObj, activity, webView, true);
    }

    @Override
    public void execute(JSONObject params) {
        try {
            callbackId = params.getString("id");
        } catch (JSONException e) {

        }
        shareText(params);
    }
}
