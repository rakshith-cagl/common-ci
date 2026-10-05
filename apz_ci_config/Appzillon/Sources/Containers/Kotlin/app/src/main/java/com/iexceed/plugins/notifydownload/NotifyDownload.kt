/*
 *  Copyright (c) 2022 Appzillon . All rights reserved.
 *  
 */
package com.iexceed.plugins.notifydownload

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.iexceed.appzillonapp.BuildConfig
import com.iexceed.appzillonapp.R
import com.iexceed.common.ApzActivity
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class NotifyDownload private constructor(webView: WebView, activity: ApzActivity<*>,
                                         override val apzPluginUtil: IapzPluginUtil) : ApzPlugin()
{
    private var mParams: JSONObject? = null
    private var mCallbackId: String? = null
    private var mBuilder: NotificationCompat.Builder? = null
    private var mNotifyManager: NotificationManager? = null
    private val id = 1
     var permissions: Array<String> = arrayOf()


    init {
        super.aActivity = activity
        super.aWebview = webView
    }

    override fun execute(params: JSONObject) {
        try {
            mParams = params
            callbackId = params.getString("id")
            mCallbackId = callbackId

            if ("N".equals(
                    aActivity.resources.getString(R.string.INTERNALSANDBOX),
                    ignoreCase = true
                )
            ) {
                handleRequestPermissions(params)
            } else {
                notifyDownload(params)
            }
        } catch (e1: Exception) {
            //Sonar fix
        }
    }

    private fun handleRequestPermissions(params: JSONObject) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    aActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            if ((ActivityCompat.checkSelfPermission(
                    aActivity,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
                        != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                    aActivity,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
                        != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                    aActivity,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                permissions = arrayOf(
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            }
            if ((ActivityCompat.checkSelfPermission(
                    aActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                )
                        != PackageManager.PERMISSION_GRANTED)
            ) {
                permissions = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (permissions.isNotEmpty()) {
            requestForPermission(params)
        } else {
            notifyDownload(params)
        }
    }


    private fun notifyDownload(jsonObj: JSONObject) {
        try {

            mParams=jsonObj
            val action = mParams!!.getString("action")
            if (action.equals("START", ignoreCase = true)) {
                startNotify(jsonObj)
            } else {
                stopNotify(jsonObj)
            }
        } catch (e: JSONException) {
            //Sonar fix
        }
    }

    private fun startNotify(jsonObj: JSONObject) {
        var title: String? = ""
        var content: String? = ""
        mParams=jsonObj
        try {
            title = mParams!!.getString("title")
            content = mParams!!.getString("content")
            mNotifyManager = aActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotifyManager?.createNotificationChannel(channel)
            mBuilder = NotificationCompat.Builder(aActivity, CHANNEL_ID)
            if (mBuilder != null && mNotifyManager != null)
            {
                mBuilder!!.setContentTitle(title).setContentText(content)
                    .setSmallIcon(R.drawable.notification)
                // Start a lengthy operation in a background thread
                mBuilder!!.setChannelId(CHANNEL_ID)
                mBuilder!!.setProgress(0, 0, true)
                mNotifyManager!!.notify(id, mBuilder!!.build())
                mBuilder!!.setAutoCancel(true)
            }
        } catch (e: JSONException) {
            val json = JSONObject()
            try {
                json.put("text", "Error in starting notifier")
            } catch (e1: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendError(callbackId, "", json, aActivity, aWebview, true)
        }
    }

    private fun stopNotify(jsonObj: JSONObject) {
        var title: String? = ""
        var content: String? = ""
        var filePath: String? = ""
        var mime: String? = ""
        mParams=jsonObj
        try {
            title = mParams!!.getString("title")
            content = mParams!!.getString("content")
            filePath = mParams!!.getString("filePath")
            mime = mParams!!.getString("mimeType")
            val file = File(filePath)
            val intent = Intent()
            if (file.exists()) {
                val uri = FileProvider.getUriForFile(aActivity, BuildConfig.APPLICATION_ID, file)
                intent.action = Intent.ACTION_VIEW
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                intent.setDataAndType(uri, mime)

                var pIntent: PendingIntent? = null
                pIntent = PendingIntent.getActivity(aActivity, 0, intent, FLAG_MUTABLE)


                mNotifyManager = aActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mBuilder = NotificationCompat.Builder(aActivity, CHANNEL_ID)

                if (mBuilder != null && mNotifyManager != null)
                {
                    mBuilder!!.setContentTitle(title)
                    mBuilder!!.setSmallIcon(R.drawable.notification)
                    mBuilder!!.setContentIntent(pIntent)
                    mBuilder!!.setContentText(content)
                        .setProgress(0, 0, false)
                    mBuilder!!.setChannelId(CHANNEL_ID)
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                    mNotifyManager!!.createNotificationChannel(channel)
                    mNotifyManager!!.notify(id, mBuilder!!.build())
                } else {
                    //Sonar fix
                }
            } else {
                apzPluginUtil.sendError(callbackId, "APZ-CNT-002", null, aActivity, aWebview, true)
                return
            }
        } catch (e: JSONException) {
            val json = JSONObject()
            try {
                json.put("text", "Error in stopping notifier")
            } catch (e1: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendError(callbackId, "", json, aActivity, aWebview, true)
        }
    }

    private fun requestForPermission(jsonObj: JSONObject) {
        this.aActivity.startOnPermissionForResult(
            aActivity,
            permissions,
            PluginConstants.APZ_REQ_WRITE_STORAGE,
            object : OnPermissionsResultHandler() {
                override fun handlePermissionResult(
                    requestCode: Int,
                    permissions: Array<String?>,
                    grantResults: IntArray
                ) {
                    if (requestCode == PluginConstants.APZ_REQ_WRITE_STORAGE) {
                        handleNotifyPermissions(permissions, jsonObj)
                    } else {
                        PermissionDeniedCallback()
                    }
                }
            }
        )
    }

    private fun handleNotifyPermissions(
        permissions: Array<String?>,
        jsonObj: JSONObject
    ) {
        var denied = false
        var neverAskAgain = false
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(aActivity, permission!!)) {
                denied = true
            } else {
                if (ActivityCompat.checkSelfPermission(
                        aActivity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    neverAskAgain = true
                }
            }
        }
        if (neverAskAgain) {
            PermissionDeniedCallback()
        } else if (denied) {
            displayReconfirmationMessage(jsonObj)
        } else {
            notifyDownload(jsonObj)
        }
    }

    private fun displayReconfirmationMessage(jsonObj: JSONObject) {
        val message = "To read files, grant permission for app to access storage"
        val alertDialogBuilder = AlertDialog.Builder(aActivity)
        alertDialogBuilder.setTitle("Permission Denied")
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.cancel()
                requestForPermission(jsonObj)
            }.setNegativeButton("Deny") { dialog, _ ->
                dialog.cancel()
                PermissionDeniedCallback()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun PermissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied("File read", callbackId, aActivity, aWebview)
    }



    companion object {
        private var pluginObj: ApzPlugin? = null
        private const val CHANNEL_ID = "123"
        private const val CHANNEL_NAME = "Notification Channel"
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = NotifyDownload(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }
}
