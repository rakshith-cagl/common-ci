package com.iexceed.plugins.miscellaneous

import android.content.SharedPreferences
import android.webkit.WebView
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.common.UserSettings.setAppValue
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class Miscellaneous private constructor(webView: WebView, activity: ApzActivity<*>,
                                        override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {
    private var settings: SharedPreferences? = null
    val properties = "USER_PREFS"



    companion object {
        private var pluginObj: ApzPlugin? = null
        var eventId: String? = null
        var mApzPluginUtil: IapzPluginUtil? = null

        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                mApzPluginUtil = apzPluginUtil
                pluginObj = Miscellaneous(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

    }

    override fun execute(params: JSONObject) {
        eventControl(params)
    }

    fun eventControl(params: JSONObject) {
        try {
            eventId = params.getString("id")
            val eventsIterator = params.keys()
            settings = EncryptedPrefHelper.getPrefs(aActivity.applicationContext)
            while (eventsIterator.hasNext()) {
                val key = eventsIterator.next().trim { it <= ' ' }
                val `val` = params.getString(key).trim { it <= ' ' }
                setAppValue(AppzillonMainScreen.APP_NAME, key, `val`, settings)
            }
            setAppValue(AppzillonMainScreen.APP_NAME, "eventsInitialized", "true", settings)
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            mApzPluginUtil?.sendError(
                eventId,
                "APZ-CNT-077",
                null,
                this.aActivity,
                this.aWebview,
                true
            )
        }
    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }

   /*

activateEvents = function() {
    var json = {
        allEvents: "on"
    };
    json.id = "EVENTS_ID";
    json.callBack = EventsCallback;
    apz.ns.detectEvents(json)
};

    */

}
