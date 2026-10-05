package com.iexceed.plugins.notification

import android.content.Intent
import android.util.Log
import android.webkit.WebView
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.ApzFirebaseMessagingService
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class NotificationPlugin private constructor(webView: WebView, activity: ApzActivity<*>,
                                             override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin()
{
    override var TAG = "NotificationPlugin"

    val observer = androidx.lifecycle.Observer<Intent>{intent ->
        onMessageReceived(intent)
    }

   private fun onMessageReceived(intent: Intent)
   {
       val msg = intent.getStringExtra("message")
       val param = intent.getStringExtra("msgParameters")
       var notificationCode: String? = ""
       var actionCode: String? = ""
       val notifId = intent.getIntExtra("notif_id", -1)
       if (intent.hasExtra("notification_code")) {
           notificationCode = intent.getStringExtra("notification_code")
           actionCode = intent.getStringExtra("action_code")
       }
       val title = intent.getStringExtra("title")
       val imageUrl = intent.getStringExtra("image_url")
       if (ApzFirebaseMessagingService.mNotificationManager != null && notifId != -1) {
           ApzFirebaseMessagingService.mNotificationManager!!.cancel(notifId)
       }
       onReceiveNotification(
           msg,
           param,
           callbackId,
           notificationCode,
           actionCode,
           title,
           imageUrl
       )
   }

    private fun startListener()
    {
        Log.d(TAG, "NotificationPlugin : startListener : ")

        val res = JSONObject()
        res.put("event", "started")
        try {
            aActivity.runOnUiThread{
                aActivity.viewModel.notificationListenerLiveData.observe(aActivity,observer)
            }
        } catch (ex: JSONException) {
            ex.printStackTrace()
        }

        //Sending broadcast when the app is not active
        if (!AppzillonMainScreen.PUSH_MESSAGE.isNullOrEmpty())
        {
            Log.d(TAG, "PUSH_MESSAGE : NOT NULL ")
            val brIntent = Intent()
            if ("" != AppzillonMainScreen.NOTIFICATION_CODE) {
                brIntent.putExtra("notification_code", AppzillonMainScreen.NOTIFICATION_CODE)
                brIntent.putExtra("action_code", AppzillonMainScreen.ACTION_CODE)
            }
            brIntent.putExtra("appstatus", "")
            brIntent.putExtra("msgParameters", AppzillonMainScreen.NOTIF_PARAMS)
            brIntent.putExtra("message", AppzillonMainScreen.PUSH_MESSAGE)
            brIntent.putExtra("notif_id", AppzillonMainScreen.NOTIF_CODE)
            brIntent.putExtra("action_code", AppzillonMainScreen.ACTION_CODE)
            brIntent.putExtra("title", AppzillonMainScreen.NOTIF_TITLE)
            brIntent.putExtra("image_url", AppzillonMainScreen.NOTIF_IMG_URL)
            aActivity.viewModel.notificationListenerLiveData.value = brIntent
            AppzillonMainScreen.PUSH_MESSAGE = null
            AppzillonMainScreen.ACTION_CODE = null
            AppzillonMainScreen.NOTIFICATION_CODE = null
            AppzillonMainScreen.NOTIF_CODE = -1
            AppzillonMainScreen.NOTIF_TITLE = null
            AppzillonMainScreen.NOTIF_IMG_URL = null
        } else {
            Log.d(TAG, "PUSH_MESSAGE NULL ")
            apzPluginUtil.sendSuccess(callbackId, res, true, aActivity, aWebview, true)
        }
    }

    private fun stopListener() {
        aActivity.runOnUiThread{
            aActivity.viewModel.notificationListenerLiveData.removeObserver(observer)
        }
        val res = JSONObject()
        res.put("event", "stopped")
        apzPluginUtil.sendSuccess(callbackId, res, false, aActivity, aWebview, true)
    }

    override fun execute(params: JSONObject) {
        var action = ""
        try {
            action = params.getString("action")
            callbackId = params.getString("id")
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }
        if (action == "STARTLISTENER") {
            startListener()
        } else if (action == "STOPLISTENER") {
            stopListener()
        }
    }

    fun onReceiveNotification(
        aPushMsg: String?,
        aMsgParam: String?,
        aCallbackId: String?,
        aNotificationCode: String?,
        aActionCode: String?,
        aTitle: String?,
        aImageUrl: String?
    )
    {
        Log.d(TAG, "Inside onReceiveNotification ")

        Timer().schedule(2000)
        {
            // do something after 2 second to load webview in inital case
            if (aWebview != null)
            {
                val json = JSONObject()
                try {
                    json.put("message", aPushMsg)
                    json.put("params", aMsgParam)
                    json.put("event", "notification")
                    json.put("notification_code", aNotificationCode)
                    json.put("action_code", aActionCode)
                    json.put("image_url", aImageUrl)
                    json.put("title", aTitle)
                    json.put("subtitle", "")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                Log.d(TAG, "Inside sendSuccess ")

                apzPluginUtil.sendSuccess(
                    aCallbackId,
                    json,
                    true,
                    aActivity, aWebview,
                    true)
            }
        }
    }
    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = NotificationPlugin(webView,activity,apzPluginUtil)
            }
            return pluginObj
        }

        val isPlugin: Boolean
            get() = true
    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }
}