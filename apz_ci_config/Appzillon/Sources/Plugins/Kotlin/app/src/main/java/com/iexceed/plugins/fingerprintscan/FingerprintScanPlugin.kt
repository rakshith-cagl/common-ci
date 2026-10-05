package com.iexceed.plugins.fingerprintscan

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class FingerprintScanPlugin private constructor(webView: WebView, activity: ApzActivity<*>) : ApzPlugin() {

    init {
        super.aActivity = activity
        super.aWebview = webView
    }
    override fun execute(params: JSONObject) {
        //handle execution
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = FingerprintScanPlugin(webView, activity)
            }
            return pluginObj
        }
    }
}
