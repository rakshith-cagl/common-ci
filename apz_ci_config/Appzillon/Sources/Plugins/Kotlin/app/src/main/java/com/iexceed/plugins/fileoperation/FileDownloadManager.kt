package com.iexceed.plugins.fileoperation

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.webkit.WebView
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonConstants
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class FileDownloadManager(
    private val mContext: Context,
    private val mActivity: Activity,
    private val mWebView: WebView,
    apzpluginUtil: IapzPluginUtil
) {
    private val TAG = "DOWNLOADMANAGER "
    private val base64: String? = null
    private val downloadExternalPath: String? = null
    private var callerId: String? = null
    private var downloadManager: DownloadManager? = null
    private var fileName: String? = null
    private var downLoadId: Long = 0
    private var fileURL: String? = null
    private var mApzPluginUtil = apzpluginUtil

    fun downloadFile(jsonObj: JSONObject)
    {
        try {
            callerId = jsonObj.getString("id")
            fileURL = jsonObj.optString("fileURL")
            fileName = jsonObj.optString("fileName")

            // DownloadManager Changes
            downloadManager = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            mContext.registerReceiver(
                onComplete,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                AppzillonConstants.APPZILLON_BROADCAST_PERMISSION, null
            )
            val request = DownloadManager.Request(Uri.parse(fileURL))
            request.setTitle("Download")
                .setDescription("File is downloading...")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            downLoadId = downloadManager!!.enqueue(request)
        } catch (e1: Exception) {
            ApzLogger.e(TAG, e1.toString())
            downloadFailure(mContext.resources.getString(R.string.download_json_error))
        }
    }

    private fun downloadSuccess(path: String) {
        val fileDownloadRes = JSONObject()
        try {
            fileDownloadRes.put("filePath", path)
        } catch (ex: JSONException) {
            ApzLogger.e(TAG, ex.toString())
            return
        }
        mApzPluginUtil.sendSuccess(
            callerId, fileDownloadRes, false, mActivity, mWebView,
            true
        )
    }

    private fun downloadFailure(errorMsg: String) {
        val json = JSONObject()
        try {
            json.put("errorMessage", errorMsg)
        } catch (e: JSONException) {
            //Sonar fix
        }
        mApzPluginUtil.sendError(
            callerId, "APZ-CNT-079", json, mActivity,
            mWebView, true
        )
    }

    var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //check if the broadcast message is for our Enqueued download
            val action = intent.action
            if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (referenceId == downLoadId) {
                    handleFileDownloadFromManager()
                }
            } else {
                downloadFailure("Download unsuccessful")
            }
        }
    }

    private fun handleFileDownloadFromManager() {
        try {
            val query = DownloadManager.Query()
            query.setFilterById(downLoadId)
            val c: Cursor? = downloadManager?.query(query)
            if (c != null && c.moveToFirst()) {
                val columnIndex = c
                    .getColumnIndex(DownloadManager.COLUMN_STATUS)
                if (DownloadManager.STATUS_SUCCESSFUL == c
                        .getInt(columnIndex)
                ) {
                    val lIndex = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                    val uriString = c.getString(lIndex)
                    val subString = uriString.substring(7)
                    downloadSuccess(File(subString).absolutePath)
                } else {
                    downloadFailure("Download unsuccessful")
                }
            } else {
                downloadFailure("Download cancelled")
            }
        } catch (e: Exception) {
            downloadFailure("Download unsuccessful " + e.message)
        }
    }

    companion object {
        val isFileDownload: Boolean
            get() = true
    }
}