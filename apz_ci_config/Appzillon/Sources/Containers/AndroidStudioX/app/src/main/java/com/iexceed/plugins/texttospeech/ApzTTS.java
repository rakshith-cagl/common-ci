package com.iexceed.plugins.texttospeech;

import android.webkit.WebView;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import org.json.JSONObject;

/**
 * Created by mishra.abhishek on 20/11/17.
 */

public class ApzTTS extends ApzPlugin {

    public ApzTTS(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        return null;
    }

    @Override
    public void execute(JSONObject params) {}

    

    public static boolean isPlugin() {
        return false;
    }
}
