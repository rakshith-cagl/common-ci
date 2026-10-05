package com.iexceed.plugins.sms

import android.content.*
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class Sms private constructor(var webView: WebView,
          var activity: ApzActivity<*>,
          override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {
    private var mAction: String? = null
    private var mParams: JSONObject? = null
    private var mCallbackId: String? = null


    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = Sms(webView, activity,apzPluginUtil)
            }
            return pluginObj
        }

        fun IsSMS(): Boolean {
            return true
        }
    }

    override fun execute(params: JSONObject) {
        try {
            mParams = params
            callbackId = params.getString("id")
            mCallbackId = callbackId
            mAction = params.getString("action")
            proceedSMS()
        } catch (e: Exception) {
            apzPluginUtil.sendError(
                mCallbackId, "APZ-CNT-329", null, aActivity,
                aWebview, true
            )
        }
    }

    private fun proceedSMS() {
        try {
            if (mAction == "SEND") {
                sendSms(mParams)
            } else if (mAction == "STARTLISTENER") {
                startListener()
            } else if (mAction == "STOPLISTENER") {
                stopListener()
            }
        } catch (e: Exception) {
            apzPluginUtil.sendError(
                mCallbackId, "APZ-CNT-329", null, aActivity,
                aWebview, true
            )
        }
    }

    private fun sendSms(smsJson: JSONObject?) {
        var mphoneno = ""
        var message = ""
        val errorMsg = "SERVICE NOT AVAILABLE"
        try {
            mphoneno = smsJson!!.getString("phoneNo")
            message = smsJson.getString("message")
            //marg = smsJson.getString("type").toUpperCase();
            if (mphoneno.equals("", ignoreCase = true) || message.equals("", ignoreCase = true)) {
                apzPluginUtil.sendError(
                    callbackId, "APZ-CNT-171", null,
                    aActivity, aWebview, true
                )
                return
            }
            // callbackId = smsJson.getString("id");
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", null,
                aActivity, aWebview, true
            )
        }
        try {

            val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("sms", mphoneno, null))
            smsIntent.putExtra("sms_body", message)
            aActivity.startActivity(smsIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                aActivity.applicationContext, errorMsg,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    // New code may require READ_SMS Permission
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        var senderNum = ""

        override fun onReceive(context: Context, intent: Intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                if (extras != null) {
                    when (extras.getInt(SmsRetriever.EXTRA_STATUS)) {
                        CommonStatusCodes.SUCCESS -> {
                            val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                            // Get SMS message contents
                            // Extract one-time code from the message and complete verification
                            // by sending the code back to your server.
                            val jsonObj = JSONObject()
                            jsonObj.put("number", "")
                            jsonObj.put("message", message)
                            jsonObj.put("event", "messageReceived")
                            senderNum = ""
                            apzPluginUtil.sendSuccess(
                                callbackId, jsonObj,
                                true, aActivity, aWebview, true
                            )
                        }
                        CommonStatusCodes.TIMEOUT -> {
                            val jsonObj = JSONObject()
                            jsonObj.put("errorMessage", "timeout occurred")
                            apzPluginUtil.sendError(callbackId, "APZ-CNT-077", jsonObj, aActivity, aWebview, true)
                        }
                    }
                }
            }
        }

    }

    private fun startListener() {
        Log.d("SMS_STRTLISTIN", "Inside SMSstart Listener")
        try {

            val client = SmsRetriever.getClient(aActivity)

            if (broadcastReceiver == null) {
                Log.e("SMS", "broadcastReceiver is null")
            }
            val intentFilter = IntentFilter()
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
            val appContext: Context = aActivity.getApplicationContext()
            appContext.registerReceiver(broadcastReceiver, intentFilter ,
                AppzillonConstants.APPZILLON_BROADCAST_PERMISSION, null)

            // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
            // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
            // action SmsRetriever#SMS_RETRIEVED_ACTION.

            val task = client.startSmsRetriever()


            // Listen for success/failure of the start Task. If in a background thread, this
            // can be made blocking using Tasks.await(task, [timeout]);
            task.addOnSuccessListener {
                var json: JSONObject? = null
                try {
                    json = JSONObject()
                    json.put("event", "started")
                } catch (e: JSONException) {
                    ApzLogger.e("SMS", e.toString())
                }
                apzPluginUtil.sendSuccess(
                    callbackId, json, true,
                    aActivity, aWebview, true
                )
            }
            task.addOnFailureListener {

                var json: JSONObject? = null
                try {
                    json = JSONObject()
                    json?.put("event", "started")
                } catch (ex: JSONException) {
                    ApzLogger.e("SMS", ex.toString())
                }
                apzPluginUtil.sendSuccess(
                    callbackId, json, false,
                    aActivity, aWebview, true
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace();
        }
    }

    private fun stopListener() {
        val json = JSONObject()
        try {
            aActivity.unregisterReceiver(broadcastReceiver)
            json.put("event", "stopped")
        } catch (e: JSONException) {
            ApzLogger.e("SMS", e.toString())
        }
        apzPluginUtil.sendSuccess(
            callbackId, json, false,
            aActivity, aWebview, true
        )
    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }


}
