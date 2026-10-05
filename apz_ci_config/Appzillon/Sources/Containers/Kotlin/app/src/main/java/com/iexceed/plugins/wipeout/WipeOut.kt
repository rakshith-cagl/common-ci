package com.iexceed.plugins.wipeout

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.AppzillonConstants.ASSETS_MAIN_FOLDER
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
import java.io.File


class WipeOut private constructor(webView: WebView, activity: ApzActivity<*>,
                          override val apzPluginUtil: IapzPluginUtil): ApzPlugin() {

    override var TAG = "WipeOut"


//    private fun unInstallApp(jsonobj: String?) {
    private fun unInstallApp() {
        try {
            val uninstall = Intent(Intent.ACTION_DELETE)
            uninstall.data = Uri.parse("package:" + aActivity.applicationContext.packageName)
            uninstall.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            aActivity.startActivity(uninstall)

            val successMsg = "Wipeout Success"
            val json = JSONObject()
            json.put("text", successMsg)
            apzPluginUtil.sendSuccess(callbackId, json, false, aActivity, aWebview, true)
        } catch (e: Exception) {
            ApzLogger.i(TAG, e.toString())
            try {
                val errorMsg = e.message
                val json = JSONObject()
                json.put("text", errorMsg)
                apzPluginUtil.sendError(callbackId, "", json, aActivity, aWebview, true)
            } catch (e1: JSONException) {
                ApzLogger.i(TAG, e.toString())
            }
        }
    }

    // To wipe out sub app means to delete it from sandbox
    fun wipeOutSubApp(appName: String) {
        val appLoc = (AppzillonMainScreen.SANDBOX_LOC + File.separator
                + ASSETS_MAIN_FOLDER + File.separator
                + appName)
        val htmlFileLoc = AppzillonMainScreen.SANDBOX_LOC + File.separator + appName + ".html"
        val htmlFile = File(htmlFileLoc)
        if (htmlFile.exists()) {
            val isDeleted = htmlFile.delete()
            if (!isDeleted){
                //Sonar fix
            }
        }
        val subAppFolder = File(appLoc)
        if (subAppFolder != null) {
            deleteRecursive(subAppFolder)
        }
        apzPluginUtil.sendSuccess(callbackId, null, false, aActivity, aWebview, true)
    }



    private fun deleteRecursive(subAppFolder: File) {

        if (subAppFolder.isDirectory) {
            val children = subAppFolder.list()
            for (i in children.indices) {
                val temp = File(subAppFolder, children[i])
                handleFile(temp)
            }
        }
        val isDeleted = subAppFolder.delete()
        if (!isDeleted){
            //Sonar fix
        }
    }

    private fun handleFile(temp: File) {
        if (temp.isDirectory) {
            deleteRecursive(temp)
        } else {
            val b = temp.delete()
            if (!b) {
                ApzLogger.e(TAG, "DELETE FAIL : ")
            }
        }
    }

    override fun execute(params: JSONObject) {

        try {
            callbackId = params!!.getString("id")
            val action = params.getString("action")
            if (action.equals("unInstallApp", ignoreCase = true)) {
                unInstallApp()
            } else if (action.equals("subAppDelete", ignoreCase = true)) {
                wipeOutSubApp(params.getString("appId"))
            }
        } catch (e: Exception) {
            ApzLogger.i(TAG, e.toString())
        }

    }

    companion object {
        private var pluginObj: ApzPlugin? = null

        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = WipeOut(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }

}
