package com.iexceed.plugins.deeplinking

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONException
import org.json.JSONObject
/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzDeeplinkingPlugin private constructor(webview: WebView,
                                               aActivity: ApzActivity<*>,
                                               override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin() 
{
    override var TAG = "DeeplinkPlG"
    override fun execute(params: JSONObject) {
        Log.d(TAG,"INSIDE EXECUTE")
        try {
            callbackId = params.getString("id")
            val packageName = params.getString("packageName")
            startNewActivity(packageName)
            apzPluginUtil.sendSuccess(callbackId, null, false, aActivity, aWebview, true)
        } catch (e: JSONException) {
            //Sonar fix
        }
    }

    private fun startNewActivity(packageName: String) {
        Log.d(TAG,"INSIDE startNewActivity")

        try {
            var intent: Intent? = aActivity.packageManager.getLaunchIntentForPackage(packageName)
            if (intent == null) {
                // Bring user to the market or let them choose an app?
                intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("market://details?id=$packageName")
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            aActivity.startActivity(intent)
        } catch (e: Exception) {
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", null, aActivity, aWebview,
                true
            )
        }
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(aWebview: WebView, aActivity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzDeeplinkingPlugin(aWebview, aActivity, apzPluginUtil)
            }
            return pluginObj
        }
    }
    
    init {
        super.aWebview = webview
        super.aActivity = aActivity
    }
}
