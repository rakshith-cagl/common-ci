package com.iexceed.plugins.email

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import androidx.core.content.FileProvider
import com.iexceed.appzillonapp.BuildConfig
import com.iexceed.common.ApzActivity
import com.iexceed.common.ExternalActivityResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.math.pow

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class ApzEmailPlugin private constructor(val webView: WebView,
                     val activity: ApzActivity<*>,
                     override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    private var key: String? = null

    private val resultLoadMail = 201

    override var TAG = "ApzEmailPlugin"

    var errorCode = ""

    override fun execute(params: JSONObject) {
        sendEMail(params)
    }

    /**
     *  Sends email using native application
     * @param obj
     */
    private fun sendEMail(obj: JSONObject) {
        val recipientId: String?
        val emailSub: String?
        val emailBody: String?
        var ccIdList: String? = null
        val internal: String?
        var filePathArr: JSONArray? = JSONArray()
        var maxFileSize = 0
        var fileStatus = true
        try {
            val jsonObjKeys = obj.keys()
            while (jsonObjKeys.hasNext()) {
                key = jsonObjKeys.next() as String
                when (key) {
                    "ccIdList" ->
                        ccIdList = obj.getString("ccIdList")
                    "senderMailId" -> {
                        ApzLogger.i(TAG, "Key : senderMailId Present")
                    }
                    "filePaths" -> filePathArr = obj.getJSONArray("filePaths")
                    "maxAttachmentSize" -> maxFileSize = obj.getString("maxAttachmentSize").toInt()
                }
            }
            callbackId = obj.getString("id")
            internal = obj.getString("internal")
            recipientId = obj.getString("recipientMailId")
            emailSub = obj.getString("subject")
            emailBody = obj.getString("body")
        } catch (ex: JSONException) {

            sendError("APZ-CNT-077")
            return
        }
        val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        emailIntent.type = "plain/text"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientId))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, emailSub)
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody)
        if (filePathArr != null) {
            fileStatus = handleFilePath(filePathArr, maxFileSize, fileStatus, emailIntent)
        }
        if (ccIdList != null) {
            emailIntent.putExtra(Intent.EXTRA_CC, arrayOf(ccIdList))
        }

        pickAppAndSendEmail(internal, fileStatus, emailIntent)

    }

    private fun handleFilePath(
        filePathArr: JSONArray,
        maxFileSize: Int,
        fileStatus: Boolean,
        emailIntent: Intent
    ): Boolean {
        var fileStatus1 = fileStatus
        var size = 0.0
        val uris: ArrayList<Uri> = ArrayList()
        for (i in 0 until filePathArr.length()) {
            var file: File? = null
            try {
                file = File(filePathArr.getString(i))
            } catch (e: JSONException) {
                //Sonar fix
            }
            if (file!!.exists()) {
                if (maxFileSize != 0) {
                    size += (file.length() / 1024.0.pow(2.0))
                    if (size <= maxFileSize) {

                        val fileURI = getFileURI(file)
                        uris.add(fileURI)

                    } else {
                        fileStatus1 = false
                        errorCode = "APZ-CNT-322"
                    }
                    emailIntent.putExtra(Intent.EXTRA_STREAM, uris)
                } else {
                    fileStatus1 = false
                    errorCode = "APZ-CNT-322"
                }
            } else {
                fileStatus1 = false
                errorCode = "APZ-CNT-002"
            }
        }
        return fileStatus1
    }

    private fun pickAppAndSendEmail(internal: String?, status: Boolean, intent: Intent){

        try {

            if (internal == "N") {

                if (status) {

                    activity.startActivityForResult(
                        Intent.createChooser(intent, "Send mail"),
                        resultLoadMail,
                        object : ExternalActivityResultHandler() {
                            override fun handleActivityResult(resultCode: Int, data: Intent?) {

                                val resultJson = getSuccessJsonData(resultCode)
                                sendSuccess(resultJson)

                            }
                        })
                } else {

                    sendError(errorCode)
                }
            } else if (internal == "Y") {

                //Cannot be launched.
                sendError("MAIL_ERROR")
                return
            }
        } catch (anf: Exception) {
            ApzLogger.e(TAG, anf.toString())

            //Mail client not present
            sendError("APZ-CNT-012")
            return
        }
    }

    private fun getSuccessJsonData(resultCode: Int): JSONObject{

        var status = "cancel"
        if (resultCode != Activity.RESULT_CANCELED) {
            status = "sent"
        }
        val result = JSONObject()
        try {
            result.put("event", status)
        } catch (ex: Exception) {
            //Sonar fix
        }

        return result
    }

    private fun sendError(errorCode: String){

        apzPluginUtil.sendError(
            callbackId,
            errorCode,
            null,
            activity,
            webView,
            true)
    }

    private fun sendSuccess(jsonObject: JSONObject){

        apzPluginUtil.sendSuccess(
            callbackId,
            jsonObject,
            false,
            activity,
            webView,
            true
        )
    }

    private fun getFileURI(file: File): Uri {

        return FileProvider.getUriForFile(
            activity,
            BuildConfig.APPLICATION_ID,
            file
        )
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzEmailPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}
