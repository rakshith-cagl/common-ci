package com.iexceed.plugins.zip

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONObject
import java.io.IOException
/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzZipPlugin private constructor(webView: WebView, activity: ApzActivity<*>,
                                       override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin() {

    init {
        super.aActivity = activity
        super.aWebview = webView
    }
    
    override fun execute(params: JSONObject) {

        ApzLogger.w(TAG,"Inside ZIPPlugin Execute")
        try {
            callbackId = params.getString("id")
            val action = params.getString("action")
            if (action.equals("zip", ignoreCase = true)) {
                val zipobj = ZipPlugin(callbackId!!, super.aActivity, super.aWebview, apzPluginUtil)
                try {
                    zipobj.zip(params.toString())
                } catch (e: IOException) {
                    ApzLogger.w(TAG, e.toString())
                }
            } else if (action.equals("unzip", ignoreCase = true)) {
                val zipobj = ZipPlugin(callbackId!!, super.aActivity, super.aWebview, apzPluginUtil)
                try {
                    zipobj.unzip(params.toString())
                } catch (e: IOException) {
                    ApzLogger.w(TAG, e.toString())
                }
            }
        } catch (e: Exception) {
            ApzLogger.w(TAG, e.toString())
        }
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzZipPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        val isZipPlugin: Boolean
            get() = true
    }
}
