package com.iexceed.plugins.util;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.UserSettings;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;
import com.iexceed.plugins.networkmonitor.MonitorNetwork;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApzUtilsPlugin extends ApzPlugin {
    private static ApzPlugin pluginObj;
    private MonitorNetwork monitorNetwork;
    private static boolean monitoring = false;

    private ApzUtilsPlugin(WebView webView, ApzActivity activity) {
        super(webView, activity);
    }

    public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
        if (pluginObj == null) {
            pluginObj = new ApzUtilsPlugin(webView, activity);
        }
        return pluginObj;
    }

    @Override
    public void execute(JSONObject params) {
        String command = null;

        try {
            this.callbackId = (String) params.get("id");
            command = (String) params.get("command");
            if (command == null || command.isEmpty()) {
                if (this.callbackId == null || this.callbackId.isEmpty()) {
                    ApzPluginUtil.sendPluginNotSupported(this.callbackId,
                            this.activity, this.webView, true);
                }

            } else {
                switch (command) {
                    case ApzPlugin.PLGN_DEV_INFO:
                        JSONObject deviceInfo = AppzillonUtils.getDeviceRunTimeInfo(ApzActivity.settings, ApzUtilsPlugin.this.activity, this.webView);
                        if (deviceInfo != null) {
                            ApzPluginUtil.sendSuccess(this.callbackId, deviceInfo, false, this.activity, ApzUtilsPlugin.this.webView, true);
                        }
                        break;
                    case ApzPlugin.PLGN_GET_USER_PREF:
                        JSONObject userPreJson = AppzillonUtils.getAllSettingsValue(params.toString(), ApzActivity.settings);
                        if (userPreJson != null) {
                            ApzPluginUtil.sendSuccess(this.callbackId, userPreJson, false, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.PLGN_SPLASH_SHOW:
                        final Handler refreshShow = new Handler(Looper.getMainLooper());
                        refreshShow.postDelayed(new Runnable() {
                            public void run() {
                                ApzUtilsPlugin.this.activity.showSplashScreen();
                            }
                        }, 500);
                        ApzPluginUtil.sendSuccess(this.callbackId, new JSONObject(), false, this.activity, this.webView, true);
                        break;
                    case ApzPlugin.PLGN_SPLASH_HIDE:
                        final Handler refreshHide = new Handler(Looper.getMainLooper());
                        refreshHide.postDelayed(new Runnable() {
                            public void run() {
                                ApzUtilsPlugin.this.activity.hideSplashScreen();
                            }
                        }, 0);
                        ApzPluginUtil.sendSuccess(this.callbackId, new JSONObject(), false, this.activity, this.webView, true);
                        break;
                    case ApzPlugin.APZ_PLUGIN_GET_IP:
                        String deviceIpAddress = AppzillonUtils.ipAddress(activity.getApplicationContext());
                        if (deviceIpAddress != null) {
                            JSONObject json = new JSONObject();
                            json.put("ip", deviceIpAddress);
                            ApzPluginUtil.sendSuccess(this.callbackId, json, false, this.activity, ApzUtilsPlugin.this.webView, true);
                        } else {
                            ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-059", null, this.activity, this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_APP_VERSION:
                        String appVersion = AppzillonUtils.getAppVersion(activity, params);
                        if (appVersion != null) {
                            JSONObject json = new JSONObject();
                            json.put("appVersion", appVersion);
                            ApzPluginUtil.sendSuccess(this.callbackId, json, false, this.activity, ApzUtilsPlugin.this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_CLS_APPCTN:
                        AppzillonUtils.closeApplication(activity);
                        ApzPluginUtil.sendSuccess(this.callbackId, new JSONObject(), false, this.activity, ApzUtilsPlugin.this.webView, true);
                        break;
                    case ApzPlugin.PLGN_IS_APPTOKENSET:
                        String defaultval = "N";
                      SharedPreferences settings = activity.getSharedPreferences(properties, 0);
                        JSONObject response = new JSONObject();
                        response.put("appTokenStatus", UserSettings.getAppValue(AppzillonMainScreen.APP_NAME,AppzillonMainScreen.IS_CONTAINER_LOADED, defaultval, settings));
                        ApzPluginUtil.sendSuccess(this.callbackId,
                                response, false, this.activity,
                                ApzUtilsPlugin.this.webView, true);
                        break;
                    case ApzPlugin.APZ_PLUGIN_SET_PREF:
                        boolean result = AppzillonUtils.setSetting(activity, params);
                        if (result) {
                            ApzPluginUtil.sendSuccess(this.callbackId,
                                    null, false, this.activity,
                                    ApzUtilsPlugin.this.webView, true);
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_GET_PREF:
                        String value = AppzillonUtils.getSetting(activity, params);
                        JSONObject json = new JSONObject();
                        if (!"".equalsIgnoreCase(value) || !(value == null)) {
                            json.put("value", value);
                        } else {
                            json.put("value", "");
                        }
                        ApzPluginUtil.sendSuccess(this.callbackId,
                                json, false, this.activity,
                                ApzUtilsPlugin.this.webView, true);
                        break;
                    case ApzPlugin.APZ_APP_AVAILABILITY:
                        JSONObject packageInfo=new JSONObject();
                        JSONArray uri= params.optJSONArray("package");
                        PackageManager pm = activity.getPackageManager();
                        try {
                            JSONArray packageArr=new JSONArray();
                            for( int i =0;i < uri.length();i++){
                                JSONObject packageList=new JSONObject();
                                try {

                                    PackageInfo pmI = pm.getPackageInfo(uri.getString(i),
                                            0);
                                    packageList.put(uri.getString(i),"Installed");

                                }catch(PackageManager.NameNotFoundException e ){
                                    packageList.put(uri.getString(i),"Not Installed");
                                }
                                packageArr.put(i,packageList);
                            }

                            packageInfo.put("result",packageArr);
                            //
                        } catch ( JSONException e) {
                            JSONObject resultObj = new JSONObject();
                            try {
                                resultObj.put("errorMessage", "Error" + e.toString());
                            } catch (JSONException ex) { }
                            ApzPluginUtil.sendError(callbackId, "", resultObj, activity, webView, true);
                            
                        }
                        ApzPluginUtil.sendSuccess(this.callbackId,
                                packageInfo, false, this.activity,
                                ApzUtilsPlugin.this.webView, true);
                        break;
                    case ApzPlugin.APZ_NETWORK_MONITOR:
                        String action = (String) params.get("action");
                        if (action.equalsIgnoreCase("start")) {
                            JSONObject networkJson = new JSONObject();
                            if (!monitoring) {
                                IntentFilter networkListener = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                                monitorNetwork = new MonitorNetwork(activity, webView, params);
                                activity.registerReceiver(monitorNetwork, networkListener);
                                monitoring = true;
                                networkJson.put("event", "started");
                                ApzPluginUtil.sendSuccess(this.callbackId,
                                        networkJson, true, this.activity,
                                        ApzUtilsPlugin.this.webView, true);
                            }
                        } else {
                            JSONObject networkJson = new JSONObject();
                            if (monitoring) {
                                activity.unregisterReceiver(monitorNetwork);
                                monitoring = false;
                                networkJson.put("event", "stopped");
                                ApzPluginUtil.sendSuccess(this.callbackId,
                                        networkJson, true, this.activity,
                                        ApzUtilsPlugin.this.webView, true);
                            }
                        }
                        break;
                    case ApzPlugin.APZ_PLUGIN_STATUS_BAR_COLOR:
                        final String color = params.optString("color");

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    final int c = Color.parseColor(color);

                                    Window window = activity.getWindow();

                                    // clear FLAG_TRANSLUCENT_STATUS flag:
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                                    // finally change the color
                                    window.setStatusBarColor(c);

                                    JSONObject resultObj = new JSONObject();
                                    resultObj.put("text", "StatusBar color change success.");

                                    ApzPluginUtil.sendSuccess(callbackId, resultObj, false, activity, webView, true);
                                } catch (Exception e) {
                                    JSONObject resultObj = new JSONObject();
                                    try {
                                        resultObj.put("errorMessage", "StatusBar color change failed " + e.toString());
                                    } catch (JSONException ex) { }
                                    ApzPluginUtil.sendError(callbackId, "", resultObj, activity, webView, true);

                                }

                            }
                        });
                        break;    
                    default:
                        break;
                }
            }

        } catch (Exception e) {
            ApzLogger.i("ApzUtilsPlugin", e.toString());
        }

    }
}


