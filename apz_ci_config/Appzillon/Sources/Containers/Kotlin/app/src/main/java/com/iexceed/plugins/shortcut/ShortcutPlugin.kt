package com.iexceed.plugins.shortcut

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.webkit.WebView
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject

class ShortcutPlugin private constructor(webView: WebView,
                                         activity: ApzActivity<*>,
                                         override val apzPluginUtil: IapzPluginUtil)
    : ApzPlugin() {
    protected var mCallbackId: String? = null
    val observer = androidx.lifecycle.Observer<Intent>{intent ->
        onShortCutReceived(intent)
    }
    private val packageId = "com.iexceed.shortcut"
    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ShortcutPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }

    private fun onShortCutReceived(intent: Intent) {
        handleBroadcastAction(intent)
    }

    private fun handleBroadcastAction(intent: Intent){
        Log.i(TAG, "on create onReceive: shorctcut")
        var shortcutCode = ""
        var actionCode: String? = ""
        if (intent.hasExtra("action")) {
            actionCode = intent.getStringExtra("action")
        }
        shortcutCode = packageId
        (aActivity as AppzillonMainScreen).sendShortcut(shortcutCode, actionCode, mCallbackId)
    }

    private var intentFilter = IntentFilter(packageId)


    private fun startListener(
        osVersion: Int
    ) {
        Log.d("StartShortcutList ", "ShortcutPlugin : startListener : ")

        if (osVersion >= Build.VERSION_CODES.N_MR1) {
            try {
                aActivity.runOnUiThread{
                    aActivity.viewModel.shortcutListenerLiveData.observe(aActivity, observer)
                }
            } catch (ex: JSONException) {
                ex.printStackTrace()
            }
            val res = JSONObject()
            try {
                res.put("event", "started")
            } catch (ex: JSONException) {
                //Sonar fix
            }


            //Sending broadcast when the app is not active
            if (AppzillonMainScreen.SHORTCUT_MSG!= null) {
                Log.i(TAG, "startListener: oncreate" + " " + AppzillonMainScreen.SHORTCUT_MSG)
                val brIntent = Intent()
                brIntent.putExtra("action", AppzillonMainScreen.SHORTCUT_MSG)
                brIntent.action = packageId
                aActivity.viewModel.shortcutListenerLiveData.value = brIntent
                AppzillonMainScreen.SHORTCUT_MSG = null
            } else {
                apzPluginUtil.sendSuccess(mCallbackId, res, true, aActivity, aWebview, true)
            }
        } else {
            val resError = JSONObject()
            try {
                resError.put("errorMessage", "Plugin is not supported for this device")
            } catch (ex: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-022", resError, aActivity , aWebview, true)
        }
    }

    private fun stopListener(currentOSVersion: Int) {

        if (currentOSVersion >= Build.VERSION_CODES.N_MR1) {
            try {
                aActivity.runOnUiThread{
                    aActivity.viewModel.shortcutListenerLiveData.removeObserver(observer)
                }
            } catch (ex: JSONException) {
                ex.printStackTrace()
            }
            val res = JSONObject()
            try {
                res.put("event", "stopped")
            } catch (ex: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendSuccess(callbackId, res, false, aActivity, aWebview, true)
        } else {
            val resError = JSONObject()
            try {
                resError.put("errorMessage", "Plugin is not supported for this device")
            } catch (ex: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-022", resError, aActivity, aWebview, true)
        }
    }


    override fun execute(params: JSONObject) {
        var action = ""
        try {
            action = params!!.getString("action")
            mCallbackId = params!!.getString("id")
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }
        val osVersion = Build.VERSION.SDK_INT
        if (action == "STARTLISTENER") {
            startListener(osVersion)
        } else if (action == "STOPLISTENER") {
            stopListener(osVersion)
        }
    }


    fun isPlugin(): Boolean {
        return true
    }
}
