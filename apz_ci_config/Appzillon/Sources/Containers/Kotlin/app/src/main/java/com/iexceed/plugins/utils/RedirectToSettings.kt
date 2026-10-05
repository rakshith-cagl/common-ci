package com.iexceed.plugins.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.common.ExternalActivityResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject


class RedirectToSettings private constructor(var webView: WebView,
                                             var activity: ApzActivity<*>,
                                             override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {
  private val requestPermissionForSettings = 1044

    companion object {
        private var pluginObj: ApzPlugin? = null
       
        fun createPlugin(
            webView: WebView,
            activity: ApzActivity<*>,
            apzPluginUtil: IapzPluginUtil
        ): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = RedirectToSettings(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }



    //  Redirects to app setting page.
    private fun openSettingsPage() {
        try {
            val lHandler: ExternalActivityResultHandler =
                object : ExternalActivityResultHandler() {
                    override fun handleActivityResult(
                        resultCode: Int, data: Intent?
                    ) {
                        successMessage()
                    }
                }
            val lSettingsIntent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", aActivity.packageName, null)
            )
            lSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            aActivity.startActivityForResult(
                lSettingsIntent,
               requestPermissionForSettings,
                lHandler
            )
        } catch (e: Exception) {
            failureMessage()
        }
    }

    private fun successMessage() {
        try {
            val lJson = JSONObject()
            lJson.put("text", "Redirected to settings")
            apzPluginUtil.sendSuccess(callbackId, lJson, false, aActivity, aWebview, true)
        } catch (e: JSONException) {
            failureMessage()
        }
    }

    private fun failureMessage() {
        try {
            val lJson = JSONObject()
            lJson.put("errorMessage", "Failed to redirect to settings")
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", lJson,
                aActivity, aWebview, true
            )
        } catch (e: JSONException) {
            //
        }
    }


    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            openSettingsPage()
        } catch (ex: JSONException) {
             ex.localizedMessage?.let { ApzLogger.d("", it) }
        }
    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }
}
