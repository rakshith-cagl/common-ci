package com.iexceed.plugins.report

import android.util.Base64
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.FileAccessHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class Report private constructor(webView: WebView, activity: ApzActivity<*>,
                                 override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin() {
    private var base64: String? = null
    private var ext: String? = null

    fun savereport(reportJson: JSONObject)
    {
        try {
            callbackId = reportJson.getString("id")
            base64 = reportJson.getString("base64")
            ext = reportJson.getString("extension")
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", null,
                aActivity, aWebview, true)
            return
        }
        var fos: FileOutputStream? = null
        try {
            fos = FileAccessHelper.getFileOutPutStream(
                File(
                    FileAccessHelper.getExternalFileDirFile(aActivity) + File.separator
                            + "download." + ext
            ))
            val decodedString = Base64.decode(base64, Base64.DEFAULT)
            try {
                fos.write(decodedString)
            } catch (e: IOException) {
                ApzLogger.e(TAG, e.toString())
                apzPluginUtil.sendError(
                    callbackId, "", null, aActivity,
                    aWebview, true
                ) // sdcard unavailable
            }
        } catch (e: FileNotFoundException) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(
                callbackId, "", null, aActivity,
                aWebview, true
            ) // File not found
        }
        try {
            fos!!.close()
            val result = JSONObject()
            result.put("successMessage", "Report Saved Successfully")
        } catch (e: IOException) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(
                callbackId, "", null, aActivity,
                aWebview, true
            ) // SD Card Unavailable
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }
    }

    override fun execute(params: JSONObject) {
        savereport(params)
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = Report(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        val isReport: Boolean
            get() = true
    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }
}