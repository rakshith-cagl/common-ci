package com.iexceed.plugins.zip

import android.app.Activity
import android.webkit.WebView
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.AppzillonUtils.getApzFile
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.FileAccessHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ZipPlugin(
    var mCallerId: String,
    private var mActivity: Activity,
    private var mWebView: WebView,
    apzPluginUtil: IapzPluginUtil
) {
    private val TAG = "ZipPlugin"
    lateinit var files: Array<String>
    var filename: String? = null
    private var mApzPluginUtil = apzPluginUtil
    var folderStructure: String? = null
    var origin: BufferedInputStream? = null
    var out: ZipOutputStream? = null
    var appSandboxLoc = (AppzillonMainScreen.SANDBOX_LOC + File.separator
            + AppzillonMainScreen.ASSET_APP_LOC)

    @Throws(IOException::class)
    fun zip(jsonObject: String?) {
        var zipFile: String? = null
        var file: String? = null
        val pair = getFilePath(jsonObject, file, zipFile)
        file = pair.first
        zipFile = pair.second
        if (!zipFile!!.contains(AppzillonMainScreen.SANDBOX_LOC)) {
            zipFile = appSandboxLoc + zipFile
        }
        val mZipFile = File(zipFile)
        if (!mZipFile.exists()) {
            mZipFile.mkdirs()
        }
        // zipFile = AppzillonMainScreen.SANDBOX_LOC
        // +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"app"+zipFile;
        // file = AppzillonMainScreen.SANDBOX_LOC
        // +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"app"+file;
        val check = File(file!!)
        if (check.exists()) {
            zipFile = if (check.isDirectory) {
                (zipFile
                        + file.substring(file.lastIndexOf("/"), file.length)
                        + ".zip")
            } else {
                (zipFile
                        + file.substring(
                    file.lastIndexOf("/"),
                    file.lastIndexOf(".")
                ) + ".zip")
            }
            zipFileAtPath(file, zipFile)
            zipSuccess(zipFile)
        } else {
            zipFailure("File not found")
        }
    }

    private fun getFilePath(
        jsonObject: String?,
        file: String?,
        zipFile: String?
    ): Pair<String?, String?> {
        var file1 = file
        var zipFile1 = zipFile
        try {
            obj = JSONObject(jsonObject!!)
            file1 = obj!!.getString("srcFilePath") //  extra / is removed
            zipFile1 = if (obj!!.getString("destFilePath") == "") {
                file1.substring(0, file1.lastIndexOf("/"))
            } else {
                obj!!.getString("destFilePath")
                obj!!.getString("destFilePath") // extra / is removed
            }
            if (zipFile1!!.startsWith("/")) zipFile1 = zipFile1.substring(0, 1)
        } catch (e: JSONException) {
            ApzLogger.w(TAG, e.toString())
        }
        if (file1 != null && !file1.contains(AppzillonMainScreen.SANDBOX_LOC)) {
            if (file1.startsWith("/")) {
                file1 = file1.substring(1, file1.length)
            }
            val fileCheck = File(file1)
            if (!fileCheck.exists()) {
                file1 = appSandboxLoc + file1
            }
        } else {
            if (file1!!.startsWith("/")) {
                file1 = file1.substring(1, file1.length)
            }
        }
        return Pair(file1, zipFile1)
    }

    fun zipFileAtPath(sourcePath: String?, toLocation: String?): Boolean {
        val buffer = 2048
        val sourceFile = File(sourcePath!!)
        try {
            var origin: BufferedInputStream? = null
            val dest = FileAccessHelper.getFileOutPutStream(File(toLocation))
            val out = ZipOutputStream(
                BufferedOutputStream(
                    dest
                )
            )
            if (sourceFile.isDirectory) {
                zipSubFolder(out, sourceFile, sourceFile.parent!!.length)
            } else {
                val data = ByteArray(buffer)
                val fi = FileInputStream(sourcePath)
                origin = BufferedInputStream(fi, buffer)
                val entry = ZipEntry(getLastPathComponent(sourcePath))
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, buffer).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
            }
            out.close()
        } catch (e: Exception) {
            ApzLogger.w(TAG, e.toString())
            zipFailure(e.message)
            return false
        }
        return true
    }

    @Throws(IOException::class)
    private fun zipSubFolder(
        out: ZipOutputStream, folder: File,
        basePathLength: Int
    ) {
        val buffer = 2048
        val fileList = folder.listFiles()
        var origin: BufferedInputStream? = null
        for (file in fileList!!) {
            if (file.isDirectory) {
                zipSubFolder(out, file, basePathLength)
            } else {
                val data = ByteArray(buffer)
                val unmodifiedFilePath = file.path
                val relativePath = unmodifiedFilePath
                    .substring(basePathLength)
                val fi = FileInputStream(unmodifiedFilePath)
                origin = BufferedInputStream(fi, buffer)
                val entry = ZipEntry(relativePath)
                out.putNextEntry(entry)
                var count: Int
                while (origin.read(data, 0, buffer).also { count = it } != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }
        }
    }

    fun getLastPathComponent(filePath: String?): String {
        val segments = filePath!!.split("/").toTypedArray()
        return segments[segments.size - 1]
    }

    fun unzip(jsonObject: String) {
        try {
            obj = JSONObject(jsonObject)
            var zipFile: String? = null
            zipFile = obj!!.getString("srcFilePath") // extra path is removed
            var location: String? = null
            location =
                if (!obj!!.getString("destFilePath").isNullOrEmpty()) {
                    zipFile.substring(0, zipFile.lastIndexOf("/"))
                } else {
                    obj!!.getString("destFilePath")
                }
            if (location!!.startsWith("/")) location = location.substring(0, 1)
            if (!zipFile.contains(AppzillonMainScreen.SANDBOX_LOC)) {
                if (zipFile.startsWith("/")) {
                    zipFile = zipFile.substring(1, zipFile.length)
                }
                zipFile = appSandboxLoc + zipFile
            } else {
                if (zipFile.startsWith("/")) {
                    zipFile = zipFile.substring(1, zipFile.length)
                }
            }
            if (!location.contains(AppzillonMainScreen.SANDBOX_LOC)) {
                location = appSandboxLoc + location
            }
            // zipFile = AppzillonMainScreen.SANDBOX_LOC
            // +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"app"+zipFile;
            // location = AppzillonMainScreen.SANDBOX_LOC
            // +File.separator+AppzillonMainScreen.ASSET_APP_LOC+"app"+location;
            val check = File(zipFile!!)
            if (check.exists()) {
                unzipFile(zipFile, location)
                unzipSuccess(location)
            } else {
                unzipFailure("File not found")
            }
        } catch (e: JSONException) {
            ApzLogger.w(TAG, e.toString())
            status = e.message
            unzipFailure(status)
        }
    }

    fun unzipFile(source: String?, destination: String?) {
        val zipFile = File(source!!)
        var directory: String? = null
        if (destination == "" || destination == null) {
            directory = zipFile.parent
            directory = "$directory/"
        } else {
            directory = "$destination/"
        }
        val workthread = Thread(UnZip(zipFile, directory))
        workthread.start()
    }

    inner class UnZip(var archive: File, var outputDir: String) : Runnable {
        override fun run() {
            try {
                val zipfile = ZipFile(archive)
                val e: Enumeration<*> = zipfile.entries()
                while (e.hasMoreElements()) {
                    val entry = e.nextElement() as ZipEntry
                    unzipEntry(zipfile, entry, outputDir)
                }
            } catch (e: Exception) {
                ApzLogger.w(TAG, e.toString())
            }
        }

        fun unzipArchive(archive: File?, outputDir: String) {
            try {
                val zipfile = ZipFile(archive)
                val e: Enumeration<*> = zipfile.entries()
                while (e.hasMoreElements()) {
                    val entry = e.nextElement() as ZipEntry
                    unzipEntry(zipfile, entry, outputDir)
                }
            } catch (e: Exception) {
                ApzLogger.w(TAG, e.toString())
            }
        }

        @Throws(IOException::class)
        private fun unzipEntry(
            zipfile: ZipFile, entry: ZipEntry,
            outputDir: String
        ) {
            if (entry.isDirectory) {
//				createDir(new File(outputDir, entry.getName()));
                createDir(getApzFile(outputDir + "/" + entry.name, null))
                return
            }

//			File outputFile = new File(outputDir, entry.getName());
            val outputFile = getApzFile(outputDir + "/" + entry.name, null)
            if (!outputFile.parentFile!!.exists()) {
                createDir(outputFile.parentFile!!)
            }
            BufferedInputStream(
                zipfile.getInputStream(entry)
            ).use { inputStream ->
                BufferedOutputStream(
                    FileAccessHelper.getFileOutPutStream(outputFile)
                ).use { outputStream ->
                    inputStream.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }
                }

            }
        }

        private fun createDir(dir: File) {
            if (!dir.mkdirs()) throw RuntimeException("Can not create dir $dir")
        }
    }

    fun zipSuccess(path: String?) {
        try {
            val json = JSONObject()
            json.put("filePath", path)
            mApzPluginUtil.sendSuccess(
                mCallerId, json, false, mActivity,
                mWebView, true
            )
        } catch (e: JSONException) {
            ApzLogger.w(TAG, e.toString())
        }
    }

    private fun unzipFailure(status: String?) {
        ApzLogger.w(TAG, status !!)

        try {
            val json = JSONObject()
            json.put("text", status)
            mApzPluginUtil.sendError(
                mCallerId, "APZ-CNT-315", json, mActivity, mWebView,
                true
            )
        } catch (e: JSONException) {
            ApzLogger.w(TAG, e.toString())
        }
    }

    private fun unzipSuccess(path: String?) {
        try {
            val json = JSONObject()
            json.put("filePath", path)
            mApzPluginUtil.sendSuccess(
                mCallerId, json, false, mActivity,
                mWebView, true
            )
        } catch (e: JSONException) {
            ApzLogger.w(TAG, e.toString())
        }
    }

    fun zipFailure(status: String?) {
        try {
            val json = JSONObject()
            json.put("text", status)
            mApzPluginUtil.sendError(
                mCallerId, "APZ-CNT-314", json, mActivity, mWebView,
                true
            )
        } catch (e: JSONException) {
            ApzLogger.w(TAG, e.toString())
        }
    }

    companion object {
        private var obj: JSONObject? = null
        private const val TAGZIP = "ZIP"
        private const val TAGUNZIP = "UNZIP"
        private var status: String? = null
        @Throws(IOException::class)
        fun copyStream(input: InputStream, output: OutputStream) {
            val buffer = ByteArray(1024) // Adjust if you want
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
        }
    }
}