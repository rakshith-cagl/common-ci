package com.iexceed.plugins.fileoperation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.webkit.WebView
import androidx.core.content.FileProvider
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.BuildConfig
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.FileUtils.isSDCardPresent
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.FileAccessHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class FileOperation(private val mActivity: Activity, private val mWebView: WebView, apzPluginUtil: IapzPluginUtil) {
    private val TAG = "FILE OPERATION"
    var fullContent: String? = null
    private var callbackId: String? = null
    private var mApzPluginUtil = apzPluginUtil
    private val errorCode077 = "APZ-CNT-077"
    private val errorCode127 = "APZ-CNT-127"
    private val errorCode002 = "APZ-CNT-002"
    /**
     * used to open file present in sandbox
     */
    fun openFile(fileJson: JSONObject) {
        var directory: String? = null
        var fileLoc: String? = ""
        try {
            callbackId = fileJson.getString("id")
            directory = fileJson.getString("filePath").trim { it <= ' ' }
            ApzLogger.d(TAG, "directory : "+directory)
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            mApzPluginUtil.sendError(callbackId, errorCode077, null, mActivity, mWebView, false)
            return
        }
        if (!isSDCardPresent()) {
            mApzPluginUtil.sendError(
                callbackId,
                errorCode127,
                null,
                mActivity,
                mWebView,
                true
            ) //SD Card Unavailable
            return
        }
        if ("" == directory) {
            mApzPluginUtil.sendError(callbackId, errorCode002, null, mActivity, mWebView, true)
        } else {
            fileLoc = directory
            val file = File(fileLoc)
            if (file.exists()) {
                try {
                    val i = Intent(
                        Intent.ACTION_VIEW,
                        FileProvider.getUriForFile(
                            Objects.requireNonNull(mActivity.applicationContext),
                            BuildConfig.APPLICATION_ID , file))
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    mActivity.startActivity(i)
                } catch (act: ActivityNotFoundException) {
                    ApzLogger.e(TAG, "File not found")
                    mApzPluginUtil.sendError(
                        callbackId,
                        "APZ-CNT-275",
                        null,
                        mActivity,
                        mWebView,
                        true
                    )
                }
            } else {
                ApzLogger.e(TAG, "File not found")
                mApzPluginUtil.sendError(
                    callbackId,
                    errorCode002,
                    null,
                    mActivity,
                    mWebView,
                    true
                ) //File Not found
            }
        }
    }

    /**
     * creates a file in the sandbox with given details
     * @param fileJson
     */
    fun createFile(fileJson: JSONObject) {
        val words: String
        val fileName: String
        val filePath: String
        try {
            callbackId = fileJson.getString("id")
            words = fileJson.getString("fileContent").trim { it <= ' ' }
            fileName = fileJson.getString("fileName").trim { it <= ' ' }
            filePath = fileJson.getString("filePath").trim { it <= ' ' }
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            mApzPluginUtil.sendError(callbackId, errorCode077, null, mActivity, mWebView, false)
            return
        }
        if (!isSDCardPresent()) {
            ApzLogger.i(TAG, "No SD Card")
            mApzPluginUtil.sendError(callbackId, errorCode127, null, mActivity, mWebView, true)
            return
        }
        if ("" == words || "" == fileName) {
            mApzPluginUtil.sendError(
                callbackId,
                "APZ-CNT-169",
                null,
                mActivity,
                mWebView,
                true
            ) //Failure
            return
        }
        Thread(object : Runnable {
            override fun run() {
                writeToFile()
            }

            private fun writeToFile() {
                var writer: BufferedWriter? = null
                try {
                    val fileLoc: String
                    fileLoc = if (filePath.length > 1) {
                        AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + filePath
                    } else {
                        AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC
                    }

//					File file=new File(fileLoc);
                    val file: File = AppzillonUtils.getApzFile(fileLoc, null)
                    file.mkdirs()
                    val directory = File(file, fileName)
                    //Abhishek 05 May 2015, File Name will come with extension END

                    //This will output the full path where the file will be written to...
//					ApzLogger.i(TAG,directory.getCanonicalPath());
                    writer = BufferedWriter(FileAccessHelper.getWriter(directory))
                    writer.write(words)
                    val result = JSONObject()
                    result.put("filePath", directory.absolutePath)
                    mApzPluginUtil.sendSuccess(callbackId, result, false, mActivity, mWebView, true)
                } catch (e: Exception) {
                    ApzLogger.e(TAG, e.toString())
                } finally {
                        // Close the writer regardless of what happens...
                        writer!!.close()
                }
            }
        }).start()
    }

    /**
     * Retrieves the file contents
     * @param fileJson
     */
    fun getFileContent(fileJson: JSONObject) {
        val filePath: String
        try {
            callbackId = fileJson.getString("id")
            //fileName=fileJson.getString("fileName").trim();
            filePath = fileJson.getString("filePath").trim { it <= ' ' }
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            mApzPluginUtil.sendError(callbackId, errorCode077, null, mActivity, mWebView, false)
            return
        }
        if (!isSDCardPresent()) {
            mApzPluginUtil.sendError(
                callbackId,
                errorCode127,
                null,
                mActivity,
                mWebView,
                true
            )
            return
        }
        Thread(object : Runnable {
            override fun run() {
                readFromFile()
            }

            private fun readFromFile() {
                var reader: BufferedReader? = null
                try {
                    var fileLoc: String? = null
                    if ("" != filePath) {
                        fileLoc =
                            fetchFullPath(filePath)
                    }
                    //					File file=new File(fileLoc);
                    val file: File = AppzillonUtils.getApzFile(fileLoc, null)
                    //final File directory = new File(file, fileName);
                    if (!file.exists()) {
                        mApzPluginUtil.sendError(
                            callbackId,
                            errorCode002,
                            null,
                            mActivity,
                            mWebView,
                            true
                        ) //File not found
                        return
                    }
                    //This will output the full path where the file will be written to...
//					ApzLogger.i(TAG,file.getCanonicalPath());
                    reader = BufferedReader(FileReader(file))
                    try {
                        fullContent = getStringFromFile(fileLoc)
                    } catch (e: Exception) {
                        ApzLogger.i("FileContent", "exception is in here in appending$e")
                    }
                    var result: JSONObject? = null
                    try {
                        result = JSONObject()
                        result.put("content", fullContent!!.trim { it <= ' ' })
                    } catch (ex: JSONException) {
                        ApzLogger.e(TAG, ex.toString())
                    }
                    mApzPluginUtil.sendSuccess(callbackId, result, false, mActivity, mWebView, true)
                } catch (e: Exception) {
                    ApzLogger.i("fileRead", "Exception in reading the file$e")
                } finally {
                        reader!!.close()
                }
            }
        }).start()
    }

    fun deleteFile(fileJson: JSONObject) {
        //	final String fileName;
        val filePath: String
        try {
            callbackId = fileJson.getString("id")
            //fileName=fileJson.getString("fileName").trim();
            filePath = fileJson.getString("filePath").trim { it <= ' ' }
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            mApzPluginUtil.sendError(callbackId, errorCode077, null, mActivity, mWebView, true)
            return
        }
        if (!isSDCardPresent()) {
            mApzPluginUtil.sendError(
                callbackId,
                errorCode127,
                null,
                mActivity,
                mWebView,
                true
            ) //SD Card not available
            return
        }
        proceedToDeleteFile(filePath)
    }

    private fun proceedToDeleteFile(filePath: String) {
        Thread(object : Runnable {
            override fun run() {
                deleteFile()
            }

            private fun deleteFile() {
                try {
                    var fileLoc = ""
                    if ("" != filePath) {
                        fileLoc =
                            fetchFullPath(filePath)
                    }

    //					File directory=new File(fileLoc);
                    val directory: File = AppzillonUtils.getApzFile(fileLoc, null)
                    // final File directory = new File(file, fileName);
                    if (!directory.exists()) {
                        mApzPluginUtil.sendError(
                            callbackId,
                            errorCode002,
                            null,
                            mActivity,
                            mWebView,
                            true
                        ) //SD Card not available
                        return
                    } else {
    //						new File(directory.getAbsolutePath()).mkdir();
                        val isPathCreated =
                            AppzillonUtils.getApzFile(directory.absolutePath, null).mkdir()
                        if (!isPathCreated) {
                            //handle file creation failure
                        }
                        if (directory.delete()) {
                            val result = JSONObject()
                            result.put("successMessage", "Success")
                            mApzPluginUtil.sendSuccess(
                                callbackId,
                                result,
                                false,
                                mActivity,
                                mWebView,
                                true
                            )
                        } else {
                            mApzPluginUtil.sendError(
                                callbackId,
                                "APZ-CNT-075",
                                null,
                                mActivity,
                                mWebView,
                                true
                            )
                        }
                    }
                } catch (e: Exception) {
                    ApzLogger.e(TAG, e.toString())
                    mApzPluginUtil.sendError(
                        callbackId,
                        "APZ-CNT-075",
                        null,
                        mActivity,
                        mWebView,
                        true
                    )
                }
            }
        }).start()
    }

    private fun fetchFullPath(filePath: String) =
        if (filePath.contains(AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC)) {
            filePath
        } else {
            AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + filePath
        }

    companion object {
        val isFileOperation: Boolean
            get() = true

        @Throws(Exception::class)
        fun convertStreamToString(`is`: InputStream?): String {
            val reader = BufferedReader(InputStreamReader(`is`))
            val sb = StringBuilder()
            var line: String? = null
            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }
            reader.close()
            return sb.toString()
        }

        @Throws(Exception::class)
        fun getStringFromFile(filePath: String?): String {
//		File fl = new File(filePath);
            val fl: File = AppzillonUtils.getApzFile(filePath, null)
            val fin = FileInputStream(fl)
            val ret = convertStreamToString(fin)
            //Make sure you close all streams.
            fin.close()
            return ret
        }
    }
}