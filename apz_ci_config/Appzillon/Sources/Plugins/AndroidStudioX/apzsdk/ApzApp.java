package com.iexceed.app;

import android.content.Context;
import android.content.Intent;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.AppzillonUtils;

import org.json.JSONObject;

public class ApzApp {

    private static ApzApp apzApp;

    private JSONObject apzConfig;
    private  JSONObject appConfig;
    private Context appContext;
    private ApzAppHandler appHandler;

    private ApzApp(Context appContext, JSONObject apzConfig, JSONObject appConfig,  ApzAppHandler appHandler){
        this.appContext = appContext;
        this.apzConfig = apzConfig;
        this.appConfig = appConfig;
        this.appHandler = appHandler;
    }

    public static ApzApp init(Context appContext,JSONObject apzConfig, JSONObject appConfig,  ApzAppHandler appHandler){
        if(ApzApp.apzApp == null){
            ApzApp.apzApp = new ApzApp(appContext, apzConfig, appConfig , appHandler);
        }

        return ApzApp.apzApp;
    }

    public static ApzApp get(){
        if(ApzApp.apzApp == null){
            throw new NullPointerException();
        }else{
            return ApzApp.apzApp;
        }
    }

    public void launch(){
        AppzillonMainScreen.MAIN_APP_PARAMS = this.appConfig;
        Intent intent = new Intent(this.appContext, AppzillonMainScreen.class);
        this.appContext.startActivity(intent);
    }
    public void close(){
        AppzillonUtils.closeApplication(AppzillonMainScreen.activity);
    }
    public void SendMessageToParent(JSONObject msg){
        this.appHandler.onApzAppMessage(msg);
    }

}
