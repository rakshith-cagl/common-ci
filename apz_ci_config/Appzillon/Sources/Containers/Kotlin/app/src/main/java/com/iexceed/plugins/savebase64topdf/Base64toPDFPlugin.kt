package com.iexceed.plugins.savebase64topdf

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil

class Base64toPDFPlugin
{
    companion object {
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            return null
        }
		fun isPlugin(): Boolean {
            return false
        }
    }
}
