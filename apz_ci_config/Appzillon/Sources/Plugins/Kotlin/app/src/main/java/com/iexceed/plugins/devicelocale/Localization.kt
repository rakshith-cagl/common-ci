package com.iexceed.plugins.devicelocale

import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class Localization private constructor(webView: WebView,
                                       activity: ApzActivity<*>,
                                       override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin() {
    private var mCurrLocale: String? = null
    private var mCurrCountry: String? = null

    /* Returns current locale of the device */
    @Throws(IOException::class, SecurityException::class)
    fun getDeviceLocale(jsonObj: JSONObject)
    {
        try {
            callbackId = jsonObj.getString("id")
            mCurrLocale = fetchDeviceLocale()

        } catch (e: IOException) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-049", null, aActivity,
                aWebview, true
            )
            return
        } catch (se: SecurityException) {
            ApzLogger.e(TAG, se.toString())
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-049", null, aActivity,
                aWebview, true
            )
            return
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", null,
                aActivity, aWebview, true
            )
        }
        val localeRes = JSONObject()
        try {
            localeRes.put("locale", mCurrLocale)
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            return
        }
        apzPluginUtil.sendSuccess(
            callbackId, localeRes, false,
            aActivity, aWebview, true
        )
    }

    private fun fetchDeviceLocale(): String{

        val execLang = Runtime.getRuntime().exec(arrayOf("getprop", "persist.sys.locale"))
        val locale = BufferedReader(
            InputStreamReader(
                execLang.inputStream
            )
        ).readLine()
        execLang.destroy()

        return locale
    }

    override fun execute(params: JSONObject) {
        getDeviceLocale(params)
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = Localization(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        val isLocalization: Boolean
            get() = true
    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }
}
