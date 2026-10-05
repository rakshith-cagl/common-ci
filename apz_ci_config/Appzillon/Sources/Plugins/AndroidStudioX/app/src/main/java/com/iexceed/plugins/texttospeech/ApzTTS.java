package com.iexceed.plugins.texttospeech;

import android.speech.tts.TextToSpeech;
import android.webkit.WebView;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by mishra.abhishek on 20/11/17.
 */

public class ApzTTS extends ApzPlugin {

    private static ApzPlugin plugin;

    private String lang;

    private String text;

    private TextToSpeech speech;

    public ApzTTS(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if(plugin == null){
            plugin = new ApzTTS(webView,activity);
        }
        return plugin;
    }

    @Override
    public void execute(JSONObject params) {

        try {
            this.callbackId = params.getString("id");
            lang = params.getString("language");
            text = params.getString("actualText");
            initSpeech();
        } catch (JSONException e) {
          
        }catch (Exception ex){

        }
    }

    public void initSpeech() throws Exception
    {
        final Locale lan = getLocale(lang);
        speech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status != TextToSpeech.ERROR) {
                    speech.setLanguage(lan);
                    speech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }else{
                    JSONObject json = new JSONObject();
                    try {
                        json.put("text", "Initialization Failed.");
                    } catch (JSONException e) {
                    }
                    ApzPluginUtil.sendError(callbackId, "APZ-CNT-211", json, activity, webView, true);
                }
            }
        });
    }

    public Locale getLocale(String l)
    {
        Locale lan = Locale.ENGLISH;
        if(l.contentEquals("CHINESE"))
        { lan = Locale.CHINESE;}
        if(l.contentEquals("US"))
        {lan = Locale.ENGLISH;}
        if(l.contentEquals("JAPAN"))
        {lan = Locale.JAPAN;}
        if(l.contentEquals("ITALY"))
        {lan = Locale.ITALY;}
        if(l.contentEquals("GERMANY"))
        {lan = Locale.GERMANY;}
        if (l.contentEquals("CANADA_FRENCH"))
            lan = Locale.CANADA_FRENCH;
        if(l.contentEquals("HINDI"))
        {lan = new Locale("hi");}
        if(l.contentEquals("BENGALI"))
        {lan = new Locale("bn");}
        if(l.contentEquals("TELUGU"))
        {lan = new Locale("te");}
        return lan;
    }

    public static boolean isPlugin() {
        return true;
    }
}
