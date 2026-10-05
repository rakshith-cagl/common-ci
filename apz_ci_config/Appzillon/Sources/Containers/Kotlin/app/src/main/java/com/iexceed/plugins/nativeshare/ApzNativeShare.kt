package com.iexceed.plugins.nativeshare

import android.app.Activity
import android.content.Intent
import android.webkit.WebView
import androidx.core.content.FileProvider
import com.iexceed.appzillonapp.BuildConfig
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class ApzNativeShare private constructor (val webView: WebView,
                     val activity: Activity,
                     override val apzPluginUtil: IapzPluginUtil): ApzPlugin() {

    companion object{
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin{
            if(pluginObj == null){
                pluginObj = ApzNativeShare(webView,activity, apzPluginUtil)
            }
            return pluginObj as ApzPlugin
        }
    }

    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
        } catch (e: JSONException) {
            sendFailure("")
        }
        shareText(params)
    }

    private fun shareText(json: JSONObject?) {
        if(json != null){
            try {
                val action: String = json.getString("action")
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                if ("text".equals(action, ignoreCase = true)) {
                    val text: String = json.getString("textToShare")
                    sendIntent.putExtra(Intent.EXTRA_TEXT, text)
                    sendIntent.type = "text/plain"
                    activity.startActivity(sendIntent)
                } else if ("file".equals(action, ignoreCase = true)) {
                    val path: String = json.getString("filePath")
                    if (path.isEmpty()) {
                        sendFailure("Native Share Failed - FilePath missing")
                    }
                    val uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID, File(path))
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "application/*"
                    share.putExtra(Intent.EXTRA_STREAM, uri)
                    activity.startActivity(Intent.createChooser(share, "Share File"))
                } else {
                    sendFailure("Native Share Failed - Action missing")
                }

                val resultObj = JSONObject()
                try {
                    resultObj.put("text", "Native share success")
                } catch (ex: JSONException) {
                    //Sonar fix
                }
                apzPluginUtil.sendSuccess(callbackId, resultObj, false, activity,
                    webView, true)
            } catch (e: Exception) {
                sendFailure("Native Share Failed")
            }
        }else{
            sendFailure("Parameters are empty")
        }

    }

    fun sendFailure(msg: String?) {
        val resultObj = JSONObject()
        try {
            resultObj.put("error", msg)
        } catch (ex: JSONException) {
            //Sonar fix
        }
        apzPluginUtil.sendError(callbackId, "", resultObj, activity, webView, true)
    }

}
