package com.iexceed.plugins.fileoperation

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.webkit.WebView
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.AppzillonUtils.validatePath
import com.iexceed.common.FileUtils
import com.iexceed.common.ServerUtilities
import com.iexceed.common.StringUtils.getString
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder
import com.iexceed.utils.localstorage.FileAccessHelper
import com.iexceed.utils.network.InternetCheck
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLConnection

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class FileDownload(
    private val mContext: Context,
    private val mActivity: Activity,
    private val mWebView: WebView,
    apzPluginUtil: IapzPluginUtil
) {
    private var mServerStatus: String? = null
    private var mServerErrorReason: String? = null
    private val TAG = "DOWNLOAD"
    private var base64: String? = null
    private var mApzPluginUtil = apzPluginUtil
    private var downloadExternalPath: String? = null
    private var callerId: String? = null
    private var downloadReqDetails: String? = null
    lateinit var downloadPayload : JSONObject

    fun downloadFile(jsonObj: JSONObject)
    {
		ApzLogger.d(TAG, "Inside download file : $jsonObj");
        var destinationPath: String
        val isSession: String
        try {
            val downloadReqDetailsObj = jsonObj.getJSONObject("downloadReqDetails")
            downloadReqDetails = downloadReqDetailsObj.toString()
            callerId = jsonObj.getString("id")
            destinationPath = jsonObj.getString("destinationPath")
            base64 = jsonObj.getString("base64")
            isSession = jsonObj.getString("sessionReq")
            downloadExternalPath = jsonObj.optString("downloadExternalPath")
            destinationPath = validateDestinationPath(destinationPath)
        } catch (e1: JSONException) {
            ApzLogger.e(TAG, e1.toString())
            downloadFailure(mContext.resources.getString(R.string.download_json_error))
            return
        }
        try {
            if (InternetCheck.isNetworkAvailable(mContext)) {
                if (!FileUtils.isSDCardPresent()) {
                    ApzLogger.e(TAG, " SDCardPresent NOT PRESENT: ")

                    downloadFailure(mContext.resources.getString(R.string.sdcard_unavailable))
                    return
                }
            } else {
                ApzLogger.e(TAG, " Network NOT PRESENT: ")

                downloadFailure(mContext.resources.getString(R.string.internet_connection_error))
                return
            }
            try {
                downloadPayload = JSONObject(this.downloadReqDetails.toString())
                downloadPayload.getJSONObject("appzillonHeader").also {
                    it.put("serverNonce", AppzillonConstants.SNONCE)
                    it.put("clientNonce", System.currentTimeMillis())
                    it.put("sessionToken", AppzillonConstants.SESSIONTOKEN)
                }
            } catch (e: Exception) {
                //Sonar fix
            }
            val lPayLoadEncryption = getString("payloadEncryption");
            val lReqBody: RequestBody = prepareRequestBody(lPayLoadEncryption)

            // Change this approach after DI integration
            //val apiInterface = RetrofitBuilder.apiService
            val apiInterface = RetrofitBuilder.getRetrofit(mActivity).create(ApiService::class.java)
            val call = apiInterface.downloadFile(lReqBody)
            val output: FileOutputStream? = null

            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>,
                                        response: Response<ResponseBody>)
                {
                    val lRes = response
                    val lApzResponse = lRes.body()?.string()
                    handleDownloadSuccess(lApzResponse, isSession, destinationPath, output)
                }
 
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    t.printStackTrace()
                    ApzLogger.e(TAG, "Download File onFailure ")
                    downloadFailure(t.message)
                }
            })
        }
        catch (e: Exception) {
            ApzLogger.e(TAG, e.toString())
            mServerStatus = "error"
            mServerErrorReason = "Illegal Argument Exception."
        }
    }

    private fun handleDownloadSuccess(
        lApzResponse: String?,
        isSession: String,
        destinationPath: String,
        output: FileOutputStream?
    ) {
        // download Success
        var jsonResult: JSONObject? = null
        try {
            jsonResult = JSONObject(lApzResponse!!)
            val resultBody: JSONObject = jsonResult.getJSONObject("appzillonBody")
            val resultHeader: JSONObject = jsonResult.getJSONObject("appzillonHeader")
            if (resultHeader.getBoolean("status")) {
                val jsonBodyResult: JSONObject = getTheResponseBody(isSession, resultBody)
                if ("" != jsonBodyResult.getString("file")) {
                    // file download success
                    if (base64.equals("Y", ignoreCase = true)) {
                        val resultBase64String = jsonBodyResult.getString("file")
                        downloadSuccess(resultBase64String)
                    } else {
                        handleFileDownload(jsonBodyResult, destinationPath)
                    }
                }
            } else {
                ApzLogger.e(TAG, "Failed to donwload the file")
                val error: JSONArray =
                    jsonResult.getJSONArray(AppzillonConstants.APPZILLON_ERRORS)
                val errObj = error.getJSONObject(0)
                downloadFailure(errObj)
            }
        } catch (ex: Exception) {
            ApzLogger.e(TAG, ex.toString())
            downloadFailure(mContext.resources.getString(R.string.download_error))
        } finally {
            releaseOutput(output)
        }
    }

    private fun handleFileDownload(
        jsonBodyResult: JSONObject,
        destinationPath: String
    ) {
        var fileName = jsonBodyResult.getString("fileName")
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1)
        var outFileName = ""
        val finalPath = ""
        if (!destinationPath.contains(
                AppzillonMainScreen.SANDBOX_LOC + File.separator +
                        AppzillonMainScreen.ASSET_APP_LOC
            )
        ) {
            if (downloadExternalPath.equals(
                    "N",
                    ignoreCase = true
                ) ||
                downloadExternalPath.equals("", ignoreCase = true)
            ) {
                outFileName =
                    AppzillonMainScreen.SANDBOX_LOC + File.separator +
                            AppzillonMainScreen.ASSET_APP_LOC +
                            destinationPath
                val bytePath = Base64.decode(
                    jsonBodyResult.getString("file"),
                    Base64.NO_WRAP
                )
                downloadFile(outFileName, fileName, bytePath)
            }
            if (downloadExternalPath.equals("Y", ignoreCase = true)) {
                handleExternalFileDownloadLocation(
                    fileName,
                    finalPath,
                    jsonBodyResult,
                    outFileName
                )
            }
        }
    }

    private fun releaseOutput(output: FileOutputStream?) {
        if (output != null) {
            try {
                output.close()
            } catch (e: IOException) {
                ApzLogger.e(TAG, e.toString())
            }
        }
    }

    private fun handleExternalFileDownloadLocation(
        fileName: String,
        finalPath: String?,
        jsonBodyResult: JSONObject,
        outFileName: String?
    ) {
        var finalPath1 = finalPath
        var outFileName1 = outFileName
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver =
                mActivity.applicationContext.contentResolver
            val file = File(fileName)
            val mimeType =
                URLConnection.guessContentTypeFromName(file.name)
            val contentValues = ContentValues()
            contentValues.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                fileName
            )
            contentValues.put(
                MediaStore.MediaColumns.MIME_TYPE,
                mimeType
            )
            contentValues.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS
            )
            val uri = resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )
            finalPath1 = getDataColumn(
                mActivity.applicationContext,
                uri,
                null,
                null
            )
            try {
                val fos = resolver.openOutputStream(uri!!)
                val decodedString = Base64.decode(
                    jsonBodyResult.getString("file"),
                    Base64.DEFAULT
                )
                try {
                    fos!!.write(decodedString)
                } catch (e: IOException) {
                    //Sonar fix
                }
                fos!!.flush()
                fos.close()
                downloadSuccess(finalPath1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            try {
                var fileDir =
                    FileAccessHelper.getExternalFileDirFile(mContext)
                fileDir = fileDir.substring(
                    0,
                    fileDir.lastIndexOf("Android")
                ) //gives storage/emulated/0/
                outFileName1 =
                    fileDir + Environment.DIRECTORY_DOWNLOADS
                val file = File(fileName)
                URLConnection.guessContentTypeFromName(file.name)
                downloadFile(
                    outFileName1,
                    fileName,
                    Base64.decode(
                        jsonBodyResult.getString("file"),
                        Base64.NO_WRAP
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getTheResponseBody(
        isSession: String,
        resultBody: JSONObject
    ) = if (isSession.equals(
            "Y",
            ignoreCase = true
        )
    ) {
        resultBody.getJSONObject("appzillonFilePushServiceResponse")
    } else {
        resultBody.getJSONObject(
            "appzillonFilePushServiceWSResponse"
        )
    }

    private fun prepareRequestBody(lPayLoadEncryption: String) = if (lPayLoadEncryption == "Y") {
        val lEncryptedObj =
            ServerUtilities.getEncryptedRequest(mActivity, downloadPayload.toString())
        lEncryptedObj.toString().toRequestBody()
    } else {
        downloadPayload.put("encMode", 1)
        downloadPayload.toString().toRequestBody()
    }

    private fun validateDestinationPath(destinationPath: String): String {
        var destinationPath1 = destinationPath
        if (destinationPath1 == "") {
            destinationPath1 = if (downloadExternalPath.equals(
                    "N",
                    ignoreCase = true
                ) || downloadExternalPath.equals("", ignoreCase = true)
            ) {
                AppzillonMainScreen.SANDBOX_LOC + File.separator +
                        AppzillonMainScreen.ASSET_APP_LOC + "/documents/downloads"
            } else if (downloadExternalPath.equals("Y", ignoreCase = true)) {
                ""
            } else {
                //Sonar fix
                val fullPath = "/documents/downloads"
                AppzillonMainScreen.SANDBOX_LOC + File.separator +
                        AppzillonMainScreen.ASSET_APP_LOC + fullPath
            }
        }
        return destinationPath1
    }

    private fun downloadFile(outFileName: String, fileName: String, base64: ByteArray)
    {
        val fPath: String? = null
        try {
            val file = File("$outFileName/")
            if(!file.exists()){
                file.mkdirs()
            }
            val filePath = File(file.path, fileName)
            filePath.createNewFile()
            val output = FileAccessHelper.getFileOutPutStream(File(validatePath(filePath.path, null)))
            output.write(base64)
        } catch (e: IOException) {
            downloadFailure(e.message)
        }
        downloadSuccess(fPath)
    }


    private fun downloadSuccess(path: String?)
    {
		ApzLogger.d(TAG, "Success : $path");
        val fileDownloadRes = JSONObject()
        try {
            if (base64.equals("Y", ignoreCase = true)) {
                fileDownloadRes.put("filePath", "")
                fileDownloadRes.put("base64", path)
            } else {
                fileDownloadRes.put("filePath", path)
                fileDownloadRes.put("base64", "")
            }
        } catch (ex: JSONException) {
            ApzLogger.e(TAG, ex.toString())
            return
        }
        mApzPluginUtil.sendSuccess(
            callerId, fileDownloadRes, false, mActivity, mWebView,
            true
        )
    }

    private fun downloadFailure(errorMsg: String?) {
        ApzLogger.e(TAG, " downloadFailure errorMsg: ")

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

    private fun downloadFailure(errorBody: JSONObject) {
        ApzLogger.e(TAG, " downloadFailure json obj: ")
        mApzPluginUtil.sendError(
            callerId, "APZ-CNT-079", errorBody, mActivity,
            mWebView, true
        )
    }

    companion object {
        val isFileDownload: Boolean
            get() = true

        fun getDataColumn(
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
}