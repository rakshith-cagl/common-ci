package com.iexceed.common

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.webkit.WebView
import androidx.lifecycle.LifecycleCoroutineScope
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.BuildConfig
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonConstants.ASSETS_APZ_FOLDER
import com.iexceed.common.AppzillonConstants.ASSETS_MAIN_FOLDER
import com.iexceed.plugins.ApzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import com.iexceed.utils.localstorage.FileAccessHelper
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.database.Cursor
import android.content.ContentUris
import android.provider.OpenableColumns
import java.io.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
object FileUtils {
	
    private const val TAG = "FileUtils"
    private val apzPluginUtil = ApzPluginUtil()
    var oldAppVersion = "0"
    fun isSDCardPresent(): Boolean {
        var lb = false
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED == state) {
            lb = true
        }
        return lb
    }

    fun createFolder(loc: String) {
        val file = File(loc)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    fun copyAssets(aisAppFirstTime: String?, mgr: AssetManager, path: String, level: Int) {
        if (!path.contains("staticfiles")) {
            try {
                initiateAssetsCopy(mgr, path, aisAppFirstTime, level)
            } catch (e: IOException) {
                //Sonar fix
            }
        }
        // Do Not copy static files and sqilte into sandbox
    }

    private fun initiateAssetsCopy(
        mgr: AssetManager,
        path: String,
        aisAppFirstTime: String?,
        level: Int
    ) {
        val list = mgr.list(path)
        if (list?.size == 0) {
            //Copy files
            val inputStream: InputStream?
            var copyFile = true
            if (path.contains("APPSDB.sqlite") && !aisAppFirstTime.equals(
                    "YES",
                    ignoreCase = true
                )
            ) {
                copyFile = false
            }
            if (copyFile) {
                inputStream = mgr.open(path)
                val outFile = File(AppzillonMainScreen.SANDBOX_LOC + "/" + path)
                FileAccessHelper.copyFileInto(inputStream, outFile)
            }
        } else {
            File(AppzillonMainScreen.SANDBOX_LOC + "/" + path).mkdirs()
        }
        if (list != null) for (i in list.indices) {
            copyAssets(aisAppFirstTime, mgr, path + "/" + list[i], level + 1)
        }
    }

    @Throws(IOException::class)
    fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }

    @Throws(IOException::class)
    fun copyStaticFiles(mContext: Context, aJson: JSONObject) {
        var filename: String
        var filepath: String
        val assetManager = mContext.assets
        try {
            val staticobj = aJson.getJSONObject("Files")
            val iterator = staticobj.keys()
            while (iterator.hasNext()) {
                filename = iterator.next() as String
                filepath = staticobj.getString(filename)
                var basepath = ""
                basepath = if (filepath.equals("AppzillonRingtone", ignoreCase = true)) {
                    FileAccessHelper.getExternalFileDirFilePath(mContext, filepath)
                } else {
                    AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + filepath
                }
                val lDir = File(basepath)
                if (!lDir.exists()) {
                    lDir.mkdirs()
                }
                val pathOut = "$basepath/$filename"
                try {
                    val pathIn = AppzillonMainScreen.ASSET_APP_LOC + "staticfiles" + "/" + filename
                    val lInputStream = assetManager.open(pathIn)
                    FileAccessHelper.copyFile(lInputStream, File(pathOut))
                } catch (e: IOException) {
                    //handle exception
                }
            }
        } catch (e: JSONException) {
            //handle exception
        }
    }


    @Throws(IOException::class)
    fun copy(aInputStream: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (aInputStream.read(buffer).also { length = it } > 0) {
            out.write(buffer, 0, length)
        }
        out.flush()
        out.close()
        aInputStream.close()
    }

    fun copyStaticFiles(aContext: Context) {

        var br: BufferedReader? = null
        var inputStream: InputStream? = null
        val sb = java.lang.StringBuilder()
        var line: String?
        try {
            //For OTA refresh
            inputStream = if (AppzillonMainScreen.OTAREQUIRED.equals("Y", ignoreCase = true)) {
                val filesJson =
                    File(AppzillonMainScreen.SANDBOX_LOC + "/" + AppzillonMainScreen.ASSET_APP_LOC + "screens/config/Files.json")
                FileInputStream(filesJson)
            } else {
                aContext.assets.open(AppzillonMainScreen.ASSET_APP_LOC + "screens/config/Files.json")
            }
            br = BufferedReader(InputStreamReader(inputStream))
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }

            val lJson = JSONObject(sb.toString())
            copyStaticFiles(aContext, lJson)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
            br?.close()
        }
    }

    /*fun copyDirectoryOneLocationToAnotherLocation(sourceLocation: File, targetLocation: File)
    {
        if (sourceLocation.isDirectory)
        {
            if (!targetLocation.exists()) {
                targetLocation.mkdir()
            }
            val lFiles = sourceLocation.listFiles()
            if(!lFiles.isNullOrEmpty())
            {
                val children = sourceLocation.list()
                if(!children.isNullOrEmpty())
                {
                    for (i in lFiles.indices) {
                        copyDirectoryOneLocationToAnotherLocation(
                            File(sourceLocation, children[i]),
                            File(targetLocation, children[i])
                        )
                    }
                }
            }
        } else {
            val `in`: InputStream = FileInputStream(sourceLocation)
            val out: OutputStream = FileOutputStream(targetLocation)

            // Copy the bits from instream to outstream
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            `in`.close()
            out.close()
        }
    }*/

    fun initSandBoxRelatedFn(
        lifecycleScope: LifecycleCoroutineScope,
        aContext: Context,
        aHandler: Handler
    ) {
        createFolder(AppzillonMainScreen.SANDBOX_LOC)
        val defaultFolders = arrayOf("photo", "video", "audio", "docs")
        for (folderName in defaultFolders) {
            createFolder(AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + folderName)
        }
        // refresh entire asset folder after app update
        var refreshAssetFolder = false
        try {
            val appVersionCodeStr: String = UserSettings.getAppVersionCode(
                AppzillonMainScreen.APP_NAME, EncryptedPrefHelper.getPrefs()
            )
            if (!appVersionCodeStr.equals("0", ignoreCase = true) ||
                !AppzillonMainScreen.isAppFirstTime.equals("YES", ignoreCase = true)
            ) {
                oldAppVersion = appVersionCodeStr
                val currentAppVersionCode = appVersionCodeStr.toInt()
                if (BuildConfig.VERSION_CODE > currentAppVersionCode) {
                    refreshAssetFolder = true
                }
            }
        } catch (nfe: java.lang.Exception) {
            nfe.printStackTrace()
        }
        if (AppzillonMainScreen.OTAREQUIRED.equals("Y", ignoreCase = true) &&
            (AppzillonMainScreen.isAppFirstTime.equals(
                "YES",
                ignoreCase = true
            ) || refreshAssetFolder)
        ) {
            lifecycleScope.launch {
                copyToSandbox(aContext, aHandler, refreshAssetFolder)
            }
        } else {
            val msg: Message = aHandler.obtainMessage()
            msg.arg1 = AppzillonConstants.SANDBOX_CREATED
            aHandler.sendMessage(msg)
        }
    }


    private fun copyToSandbox(
        aContext: Context,
        aHandler: Handler,
        aRefreshAssetFolder: Boolean
    ) {
        val assetManager = aContext.assets
        copyAssets(
            AppzillonMainScreen.isAppFirstTime, assetManager,
            ASSETS_MAIN_FOLDER, 0
        )
        copyAssets(
            AppzillonMainScreen.isAppFirstTime, assetManager,
            ASSETS_APZ_FOLDER, 0
        )
        try {
            val folders = assetManager.list("")
            for (i in folders?.indices!!) {
                if (folders[i].contains(".html")) {
                    copyAssets(AppzillonMainScreen.isAppFirstTime, assetManager, folders[i], 0)
                }
            }
            UserSettings.setAppVersionCode(
                AppzillonMainScreen.APP_NAME,
                BuildConfig.VERSION_CODE.toString(),
                EncryptedPrefHelper.getPrefs()
            )

            if (aRefreshAssetFolder) {
                // update' static files in case of version upgrade
                copyStaticFiles(aContext)
            }
            val msg: Message = aHandler.obtainMessage()
            msg.arg1 = AppzillonConstants.SANDBOX_CREATED
            aHandler.sendMessage(msg)
        } catch (e: IOException) {
            ApzLogger.e("File", "Exception in copying asset folder $e")
        }
    }

    fun getFileSize(webView: WebView?, activity: Activity?, jsonObj: JSONObject) {
        var callbackId: String? = null
        try {
            val path = jsonObj.getString("filePath")
            callbackId = jsonObj.getString("id")
            val filenew = File(path)
            val len = filenew.length()
            val fileSize = (len / 1024).toString().toInt()
            try {
                val json = JSONObject()
                json.put("fileSize", fileSize)
                apzPluginUtil.sendSuccess(callbackId, json, false, activity!!, webView!!, true)
            } catch (e: Exception) {
                ApzLogger.e(TAG, e.toString())
            }
        } catch (e: JSONException) {
            val json = JSONObject()
            try {
                json.put("errorCode", "")
            } catch (e1: JSONException) {
                ApzLogger.d(TAG, e1.toString())
            }
            apzPluginUtil.sendError(
                callbackId, "APZ-CNT-002", json, activity!!,
                webView!!, true
            )
        }
    }


    fun scopeStorageMigration(
        lifecycleScope: LifecycleCoroutineScope,
        context: Context,
        lHandler: Handler
    ) {
        ApzLogger.w(TAG, "Inside scopeStorageMigration")
        try {
            val lExtStorageState = Environment.getExternalStorageState()
            if (lExtStorageState.equals(Environment.MEDIA_MOUNTED, ignoreCase = true)
                && "N".equals(
                    context.resources.getString(R.string.INTERNALSANDBOX),
                    ignoreCase = true
                )
            ) {
                ApzLogger.w(TAG, "Inside scopeStorageMigration ext_storage_state = MEDIA_MOUNTED")
                AppzillonMainScreen.SANDBOX_LOC =
                    FileAccessHelper.getExternalFileDirFilePath(context,AppzillonMainScreen.MAIN_APP_NAME)
            } else {
                AppzillonMainScreen.SANDBOX_LOC = context.applicationContext.filesDir.absolutePath
                //check the string
                AppzillonMainScreen.internalPathCheck =
                    AppzillonMainScreen.SANDBOX_LOC.substring(0, 6)
                ApzLogger.w(
                    TAG,
                    "Inside internalPathCheck : = ${AppzillonMainScreen.internalPathCheck}"
                )
            }
            initSandBoxRelatedFn(lifecycleScope, context.applicationContext, lHandler)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    
     fun GetRealPathFromURI(context: Context, uri: Uri): String? {
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (FileUtils.isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }

                // TODO handle non-primary volumes
            } else if (FileUtils.isDownloadsDocument(uri)) {
                return FileUtils.getDownloadsPath(context, uri)
            } else if (FileUtils.isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.getContentUri("external")
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.getContentUri("external")
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.getContentUri("external")
                } else if ("document" == type) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentUri = MediaStore.getMediaUri(context, uri)
                    }
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return FileUtils.getDataColumn(context, contentUri, selection, selectionArgs)
            }

            // MediaStore (and general)
            else if ("content" == uri.scheme) {
                return FileUtils.getDataColumn(context, uri, null, null)
            } else if ("file" == uri.scheme) {
                return uri.path
            }



        }
        return ""
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }


    private fun getDownloadsPath(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            cursor = context.contentResolver.query(uri,
                arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                null,
                null
            )
            val nameindex = cursor!!.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
            cursor!!.moveToFirst()
            String.format(
                "%s/Download/%s",
                Environment.getExternalStorageDirectory().toString(),
                cursor!!.getString(nameindex)
            )
        } else {
            val docId = DocumentsContract.getDocumentId(uri)
            val contentUri =
                ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), 0L)
            FileUtils.getDataColumn(context, contentUri, null, null)
        }

    }

    private fun getDataColumn(context: Context, contentUri: Uri?, selection:String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {

            contentUri?.let {
                cursor = context.contentResolver.query(
                    it, projection, selection, selectionArgs,
                    null
                )
            }

            cursor?.let {
                if (it.moveToFirst()) {
                    val column_index = it.getColumnIndexOrThrow(column)
                    return it.getString(column_index)
                }
            }

        } finally {
            cursor?.close()
        }
        return ""

    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority

    }

    @Throws(IOException::class)
    fun getFile(context: Context, uri: Uri?): File? {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar + uri?.let {
                FileUtils.queryName(context,
                    it
                )
            })

        try {
            context.contentResolver.openInputStream(uri!!).use { ins ->
                if (ins != null) {
                    FileUtils.createFileFromStream(
                        ins,
                        destinationFilename
                    )
                }
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return destinationFilename
    }

    fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }
}
