package com.iexceed.plugins.filetobase64

import android.app.Activity
import android.webkit.WebView
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
object FileToBase64 {

    fun convertFileToBase64(callbackId: String?, webView: WebView?, activity: Activity?, jsonObj: JSONObject, apzPluginUtil: IapzPluginUtil) {
        if(jsonObj.has("filePath")) {
            val filePath = jsonObj.getString("filePath")
            try {
                apzPluginUtil.sendSuccess(callbackId, getReturnJson(filePath), false, activity!!, webView!!, true)
            } catch (oom: OutOfMemoryError) {
                sendCBError(apzPluginUtil,oom.message,callbackId,activity,webView)
            } catch (e: Exception) {
                sendCBError(apzPluginUtil,e.message,callbackId,activity,webView)
            }
        } else {
            apzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity!!, webView!!, true)
        }
    }

    private fun getReturnJson(filePath: String): JSONObject {
        val fileBytes = Files.readAllBytes(Paths.get(filePath))
        val base64Encoded = Base64.getEncoder().encodeToString(fileBytes)
        val str = base64Encoded.toString()
        val returnJson = JSONObject()
        returnJson.put("text", str)
        return returnJson
    }

    private fun sendCBError(apzPluginUtil: IapzPluginUtil, s: String?, callbackId: String?, activity: Activity?, webView: WebView?) {
        val json = JSONObject()
        json.put("errorDescription", s)
        apzPluginUtil.sendError(callbackId, "APZ-CNT-270", json, activity!!, webView!!, true)
    }
}
