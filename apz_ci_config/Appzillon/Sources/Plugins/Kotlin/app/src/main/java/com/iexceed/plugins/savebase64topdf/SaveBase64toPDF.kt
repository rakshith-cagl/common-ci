package com.iexceed.plugins.savebase64topdf

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.AppzillonUtils.getApzFile
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.FileAccessHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLConnection

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
object SaveBase64toPDF
{
    lateinit var apzPluginUtil: IapzPluginUtil
    var base64: String? = ""
    var fileName: String = ""
    lateinit var filePath: String
    var callbackId: String? = null
    private var saveInDownloads : Boolean? = false
    fun convertBase64toPDF(webView: WebView, activity: Activity, jsonObj: JSONObject, iApzPluginUtil: IapzPluginUtil)
    {

        try {
            apzPluginUtil = iApzPluginUtil
            base64 = jsonObj.getString("base64")
            fileName = jsonObj.getString("fileName")
            filePath = jsonObj.getString("filePath")
            callbackId = jsonObj.getString("id")
            saveInDownloads = jsonObj.optBoolean("saveInDownloads")
            if (filePath.trim { it <= ' ' }.isEmpty()) {
                filePath = "downloads"
            }

            if (saveInDownloads as Boolean){
                saveInExternalDownloads(webView, activity)
            } else {
                saveInSandBox(webView, activity)
            }
        } catch (e: JSONException) {
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-077", null, activity,
                webView, true
            )
            Base64toPDFPlugin.pluginObj = null
            return
        }

    }

    private fun saveInExternalDownloads(webView: WebView, activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            val resolver =
                activity.applicationContext.contentResolver
            val file = File(fileName)
            val mimeType =
                URLConnection.guessContentTypeFromName(file.name)
            Log.d("MIME_TYPE", mimeType)
            val contentValues = ContentValues()
            contentValues.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                fileName)
            if (fileName.contains(".png")) {
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            }
            if (fileName.contains(".pdf")) {
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            }
            /*contentValues.put(
                MediaStore.MediaColumns.MIME_TYPE,
                mimeType)*/
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS)
            val uri = resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues)
            val finalPath = getDataColumn(
                activity.applicationContext,
                uri,
                null,
                null
            )
            try {
                    val fos = resolver.openOutputStream(uri!!)
                    fos!!.write((Base64.decode(base64, Base64.DEFAULT)))
                    fos.flush()
                    val json = JSONObject()
                    json.put("filePath", finalPath)
                    fos.close()
                    apzPluginUtil.sendSuccess(callbackId, json, false, activity, webView, true)
                    Base64toPDFPlugin.pluginObj = null
                } catch (e: Exception) {
                    e.printStackTrace()
                }
        } else {
            try {
                var fileDir =
                    FileAccessHelper.getExternalFileDirFile(activity)
                fileDir = fileDir.substring(
                    0,
                    fileDir.lastIndexOf("Android")
                ) //gives storage/emulated/0/
                val outFileName1 =
                    fileDir + Environment.DIRECTORY_DOWNLOADS
                val file = File(fileName)
                URLConnection.guessContentTypeFromName(file.name)
                saveFileToExtDownloadFolder(
                    activity,
                    webView,
                    outFileName1,
                    fileName,
                    base64
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveFileToExtDownloadFolder(activity: Activity, webView: WebView,outFileName1: String, fileName: String, base64: String?) {
        try {
            val file = File("$outFileName1/")
            if(!file.exists()){
                file.mkdirs()
            }
            val filePath = File(file.path, fileName)
            filePath.createNewFile()
            val output = FileAccessHelper.getFileOutPutStream(File(
                AppzillonUtils.validatePath(
                    filePath.path,
                    null
                )
            ))
            output.write(Base64.decode(base64, Base64.DEFAULT))
            val json = JSONObject()
            json.put("filePath", filePath.path)
            apzPluginUtil.sendSuccess(callbackId, json, false, activity, webView, true)
            Base64toPDFPlugin.pluginObj = null
        } catch (e: IOException) {
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-002", null, activity,
                webView, true
            )
            Base64toPDFPlugin.pluginObj = null
        }
    }

    private fun saveInSandBox(webView: WebView, activity: Activity){
        var fos: FileOutputStream? = null
        val finalPath : File
        try {
            val fullPath =
                AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + filePath
            //			File file = new File(fullPath);
            val file = getApzFile(fullPath, null)
            if (!file.exists()) {
                file.mkdirs()
            }
            //			finalPath = new File(fullPath+File.separator+fileName);
            finalPath = getApzFile(fullPath + File.separator + fileName, null)
            fos = FileAccessHelper.getFileOutPutStream(finalPath)
            val decodedString = Base64.decode(base64, Base64.DEFAULT)
            try {
                fos.write(decodedString)
            } catch (e: IOException) {
                ApzLogger.e("SaveBase64toPDF", e.toString())
                apzPluginUtil.sendError(
                    callbackId, "APZ-CNT-127", null, activity,
                    webView, true
                )
                Base64toPDFPlugin.pluginObj = null
                return
            }
        } catch (e: FileNotFoundException) {
            ApzLogger.e("SaveBase64toPDF", e.toString())
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-002", null, activity,
                webView, true
            )
            Base64toPDFPlugin.pluginObj = null
            return
        } finally {
            fos?.close()
        }
        try {
            val json = JSONObject()
            json.put("filePath", finalPath.path)
            apzPluginUtil.sendSuccess(callbackId, json, false, activity, webView, true)
            Base64toPDFPlugin.pluginObj = null

        } catch (e: Exception) {
            ApzLogger.e("SaveBase64toPDF", e.toString())
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-127", null, activity,
                webView, true
            )
            Base64toPDFPlugin.pluginObj = null
            return
        }
    }

    private fun getDataColumn(
        context: Context, uri: Uri?,
        selection: String?, selectionArgs: Array<String?>?): String?
    {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor
                    .getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }
}