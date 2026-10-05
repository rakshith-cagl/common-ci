package com.iexceed.plugins.auditlog

import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
object AuditLog {
    private var startTime = ""
    var pluginName = ""
    var auditMessage = ""

    private val DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss.SSS"

    fun makeString(plugin: String, action: String?) {
        auditMessage = ""
        startTime = getCurrentDateTime().toString()
        pluginName = plugin
    }

    suspend fun sendToJSON() {
        val action = "NATIVE"
        var sendJSON: JSONObject? = null
        try {
            sendJSON = JSONObject()
            sendJSON.put("action", action)
            sendJSON.put("startTime", startTime)
            sendJSON.put("endTime", getCurrentDateTime().toString())
            sendJSON.put("field1", pluginName)
            sendJSON.put("field2", "")
            sendJSON.put("field3", "")
            sendJSON.put("field4", "")
            sendJSON.put("field5", "")

            auditMessage = sendJSON.toString()
            withContext(Main) {
                // AuditLog.makeString("ENCRYPT DATA","Success");
                // commented to verify if this plugin is used
                //AppzillonMainScreen.webView?.loadUrl("javascript:apz.audit.auditLog("+ auditMessage + ");")
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
    }

    private fun getCurrentDateTime(): String? {
        val curDate = Date()
        val sdf = SimpleDateFormat(DATE_FORMATTER)
        var currDate: String? = null
        try {
            currDate = sdf.format(curDate)
        } catch (e: Exception) {
            //Sonar fix
        }
        return currDate
    }
}