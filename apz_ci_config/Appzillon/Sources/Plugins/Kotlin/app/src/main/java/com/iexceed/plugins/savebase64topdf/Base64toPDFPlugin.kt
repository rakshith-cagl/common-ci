package com.iexceed.plugins.savebase64topdf

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject
/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class Base64toPDFPlugin private constructor(webView: WebView, activity: ApzActivity<*>,
                                            override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin()
{

    init {
        super.aActivity = activity
        super.aWebview = webView
    }

    override fun execute(params: JSONObject) {
        SaveBase64toPDF.convertBase64toPDF (aWebview,aActivity, params, apzPluginUtil)
    }

    companion object {
        @JvmField
		var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            run {
                if (pluginObj == null) {
                    pluginObj = Base64toPDFPlugin(webView, activity, apzPluginUtil)
                }
                return pluginObj
            }
        }
		fun isPlugin(): Boolean {
            return true
        }
    }
}