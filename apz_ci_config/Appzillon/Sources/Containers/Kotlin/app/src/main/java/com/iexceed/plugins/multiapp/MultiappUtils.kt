package com.iexceed.plugins.multiapp

import android.webkit.WebView
import com.iexceed.common.*
import com.iexceed.common.AppzillonConstants.ASSETS_MAIN_FOLDER
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.plugins.ota.OTAPlugin
import com.iexceed.plugins.wipeout.WipeOut
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class MultiappUtils private constructor(var webView: WebView, var activity: ApzActivity<*>,
                    override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    override var TAG : String = "MultiappUtils"

    companion object{
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if(pluginObj == null){
                pluginObj = MultiappUtils(webView,activity, apzPluginUtil)
            }
            return pluginObj
        }
    }

    override fun execute(params: JSONObject) {
        ApzLogger.i(TAG, "INSIDE EXECUTE")
        var action: String? = null
        var appId: String? = null
        try{
            action = params.getString("action")
            appId = params.getString("appId")
            callbackId = params.getString("id")
        }catch (je: JSONException){
            ApzLogger.i(TAG, je.toString())
            apzPluginUtil.sendError(
                callbackId,
                "APZ-CNT-077",
                null,
                activity,
                webView,
                true
            )
        }

        /*if(action.equals("APPLAUNCH")){
            loadPage(appId)
        }else */
        if(action.equals("APPDELETE")){
            val wp = WipeOut.createPlugin(this.webView, this.activity, apzPluginUtil)
                params.put("action", "subAppDelete")
                wp!!.execute(params)
        }else if(action.equals("INSTRUCTIONS")){
            appInstructions(params)
        }else if(action.equals("UPGRADEREQ")){
            val result = JSONObject()
            result.put("text",AppzillonConstants.UPDATE_REQUEST)
            apzPluginUtil.sendSuccess(callbackId,result,false,activity,webView,true)
        }else if(action.equals("UPDATEACTION")){
            val result = JSONObject()
            result.put("updateAction",AppzillonConstants.UPDATE_ACTION)
            apzPluginUtil.sendSuccess(callbackId,result,false,activity,webView,true)
        }else if(action.equals("UPGRADEAPP",ignoreCase = true)){
            val appName = params.getString("appId")
            var appVersion: String = UserSettings.getAppVersion(appName, EncryptedPrefHelper.getPrefs())
            if(appVersion.equals("0.0.0")){
                val sUtils: StringUtils = StringUtils.StringUtils(activity,
                    "$ASSETS_MAIN_FOLDER/$appName"
                )
                appVersion = sUtils.getString(sUtils.APP_VERSION)
            }
            val otaPlugin = OTAPlugin(activity)
            val subAppOTARefreshStatus = otaPlugin.getDataForOTA(appName,appVersion)
            if(subAppOTARefreshStatus){
                otaPlugin.getUpdatedAppVersion()?.let { UserSettings.setAppVersion(appName, it,EncryptedPrefHelper.getPrefs()) }
                val result = JSONObject()
                result.put("text","")
                apzPluginUtil.sendSuccess(callbackId,result,false,activity,webView,true)
            }else{
                apzPluginUtil.sendError(callbackId, "", null, activity, webView, true)
            }
        }

    }

    private fun appInstructions(params: JSONObject?) {
        val appId = params!!.getString("appId")
        val presentAppId = StringUtils.getString(StringUtils.APP_ID)
        AppInstructions(activity,appId,presentAppId,apzPluginUtil).executeForResp(callbackId,webView)
    }
}
