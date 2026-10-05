package com.iexceed.plugins.fileoperation

import android.Manifest.permission
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.provider.MediaStore
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.iexceed.appzillonapp.BuildConfig
import com.iexceed.appzillonapp.R
import com.iexceed.common.ApzActivity
import com.iexceed.common.ExternalActivityResultHandler
import com.iexceed.common.FileUtils
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.auditlog.AuditLog
import com.iexceed.plugins.errorlog.ApzLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.CoroutineContext
import android.util.Log


/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class ApzFileOperationPlugin private constructor(webView: WebView, activity: ApzActivity<*>,
                                                 override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin(), CoroutineScope {
    var isOpenable: String? = null
    var result: JSONObject? = null
    override var TAG = "ApzFileOperationPlugin"
    private var mAction: String? = null
    private var mParams = JSONObject()
    private var mCallbackId: String? = null
    private lateinit var permissions: Array<String>
    private val errorCode328 = "APZ-CNT-328"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    init {
        super.aActivity = activity
        super.aWebview = webView
    }

    override fun execute(params: JSONObject)
    {
        ApzLogger.d(TAG, "Inside execute : $params")
        try {
            mParams = params
            callbackId = params.getString("id")
            mCallbackId = callbackId
            mAction = params.getString("action")
            var conditionCheck = false
            if (!mAction.equals("FILECREATE", ignoreCase = true)
                && params.has("filePath")
                && !params.getString("filePath").equals("", ignoreCase = true)
                && !params.getString("filePath").contains(BuildConfig.APPLICATION_ID)
            ) {
                val file = File(params.getString("filePath"))
                if (file.isDirectory || file.isFile) {
                    conditionCheck = true
                }
            } else if (params.has("fileCategory") && params.optString("fileCategory") == "EXTERNAL") {
                conditionCheck = true
            }
            if ("N".equals(
                    aActivity.resources.getString(R.string.INTERNALSANDBOX),
                    ignoreCase = true
                )
                || conditionCheck
            ) {
                verifyPermissions(conditionCheck)
            } else {
                proceedFileOperation()
            }
        } catch (e1: JSONException) {
            ApzLogger.i(TAG, e1.toString())
        }
    }

    private fun verifyPermissions(conditionCheck: Boolean) {
        if (SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(aActivity, READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions = arrayOf(READ_EXTERNAL_STORAGE)
                requestForPermission()
            } else {
                proceedFileOperation()
            }
        } else {
            if (conditionCheck) {
                getPermissionsToAsk()
                if (permissions.isNotEmpty()) {
                    requestForPermission()
                } else {
                    proceedFileOperation()
                }
            } else {
                proceedFileOperation()
            }
        }
    }

    private fun getPermissionsToAsk() {
        when (mParams.optString("fileCategory", "EXTERNAL")) {
            "PHOTO" -> {
                if (ActivityCompat.checkSelfPermission(
                        aActivity,
                        permission.READ_MEDIA_IMAGES
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissions = arrayOf(permission.READ_MEDIA_IMAGES)
                }
            }
            "AUDIO" -> {
                if (ActivityCompat.checkSelfPermission(
                        aActivity,
                        permission.READ_MEDIA_AUDIO
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissions = arrayOf(permission.READ_MEDIA_AUDIO)
                }
            }
            "VIDEO" -> {
                if (ActivityCompat.checkSelfPermission(
                        aActivity,
                        permission.READ_MEDIA_VIDEO
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissions = arrayOf(permission.READ_MEDIA_VIDEO)
                }
            }
            "EXTERNAL" -> {
                if ((ActivityCompat.checkSelfPermission(
                        aActivity,
                        permission.READ_MEDIA_VIDEO
                    )
                            != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                        aActivity,
                        permission.READ_MEDIA_AUDIO
                    )
                            != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                        aActivity,
                        permission.READ_MEDIA_IMAGES
                    )
                            != PackageManager.PERMISSION_GRANTED)
                ) {
                    permissions = arrayOf(
                        permission.READ_MEDIA_VIDEO,
                        permission.READ_MEDIA_IMAGES,
                        permission.READ_MEDIA_AUDIO
                    )
                }
            }
        }
    }

    private fun requestForPermission() {
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
                        handlePermissionsResults(permissions)
                    } else {
                        permissionDeniedCallback()
                    }
                }
            }
        )
    }

    private fun handlePermissionsResults(permissions: Array<String?>) {
        var denied = false
        var neverAskAgain = false
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    aActivity,
                    permission!!
                )
            ) {
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
            permissionDeniedCallback()
        } else if (denied) {
            displayReconfirmationMessage()
        } else {
            proceedFileOperation()
        }
    }

    private fun displayReconfirmationMessage() {
        val message = "To access files,allow app to access by granting requested permissions"
        val alertDialogBuilder = AlertDialog.Builder(aActivity)
        alertDialogBuilder.setTitle("Permission Denied")
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.cancel()
                requestForPermission()
            }.setNegativeButton("Deny") { dialog, _ ->
                dialog.cancel()
                permissionDeniedCallback()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun permissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied("File access", callbackId, aActivity, this.aWebview)
    }

    private fun proceedFileOperation()
    {
        ApzLogger.d(TAG, "Inside proceedFileOperation ")
        if (mAction.equals("BROWSER", ignoreCase = true)) {
            val fileCategory: String?
            try {
                fileCategory = mParams.getString("fileCategory")
                ApzLogger.d(TAG, "Inside fileCategory : $fileCategory")

                when (fileCategory) {
                    "AUDIO" -> {
                        handleFileCategoryAudio()
                    }
                    "VIDEO" -> {
                        handleFileCategoryVideo()
                    }
                    "PHOTO" -> {
                        handleFileCategoryPhoto()
                    }
                    "DEFAULT" -> {
                        val browserIntent = Intent(
                            aActivity.applicationContext,
                            DirectoryBrowser::class.java)
                        browserIntent.putExtra("location", "")
                        browserIntent.putExtra("root", "DEFAULT")
                        browserIntent.putExtra("filter", "")
                        browserIntent.putExtra(
                            "openFile",
                            mParams.getString("openFile")
                        )
                        aActivity.startActivityForResult(browserIntent,
                            BROWSE_FILE, ExternalActivityResultHandlerTask())
                    }
                    "EXTERNAL" -> {
                        handleFileCategoryExternal("file chooser")
                    }
                    else -> {
                        handleFileCategoryDefault()
                    }
                }
            } catch (e: Exception) {
                ApzLogger.i(TAG, e.toString())
                val json = JSONObject()
                try {
                    json.put("errorCode", e.toString())
                } catch (e1: JSONException) {
                    ApzLogger.i(TAG, e1.toString())
                }
                apzPluginUtil.sendError(mCallbackId, "APZ-CNT-227", json, aActivity, aWebview, true)
            }
        }
        else {
            handleFileRelatedPlugins()
        }
    }

    private fun handleFileRelatedPlugins() {
        if (mAction.equals("FILECONTENT", ignoreCase = true)) {
            val fileOp = FileOperation(aActivity, aWebview, apzPluginUtil)
            fileOp.getFileContent(mParams)
        } else if (mAction.equals("FILECREATE", ignoreCase = true)) {
            val fileOp = FileOperation(aActivity, aWebview, apzPluginUtil)
            fileOp.createFile(mParams)
        } else if (mAction.equals("FILEDELETE", ignoreCase = true)) {
            val fileOp = FileOperation(aActivity, aWebview, apzPluginUtil)
            fileOp.deleteFile(mParams)
        } else if (mAction.equals("OPENFILE", ignoreCase = true)) {
            val fileOp = FileOperation(aActivity, aWebview, apzPluginUtil)
            fileOp.openFile(mParams)
        } else if (mAction.equals("GETFILESIZE", ignoreCase = true)) {
            FileUtils.getFileSize(aWebview, aActivity, mParams)
        } else if (mAction.equals("FILEUPLOAD", ignoreCase = true)) {
            val mFileUpload = FileUpload(aWebview, aActivity, apzPluginUtil)
            mFileUpload.uploadFile(mParams)
        } else if (mAction.equals("FILEDOWNLOAD", ignoreCase = true)) {
            val mFileUpload =
                FileDownload(aActivity.applicationContext, aActivity, aWebview, apzPluginUtil)
            mFileUpload.downloadFile(mParams)
        } else if (mAction.equals("FILEDOWNLOAD_MANAGER", ignoreCase = true)) {
            val mFileDownload =
                FileDownloadManager(
                    aActivity.applicationContext,
                    aActivity,
                    aWebview,
                    apzPluginUtil
                )
            mFileDownload.downloadFile(mParams)
        }
    }

    private fun handleFileCategoryDefault() {
        val browserIntent = Intent(
            aActivity.applicationContext,
            DirectoryBrowser::class.java
        )
        browserIntent.putExtra(
            "location",
            mParams?.getString("location")
        )
        browserIntent
            .putExtra("filter", mParams?.getString("filter"))
        browserIntent.putExtra(
            "openFile",
            mParams.getString("openFile")
        )
        browserIntent.putExtra("root", "DEFAULT")
        this.aActivity.startActivityForResult(browserIntent,
            BROWSE_FILE, object : ExternalActivityResultHandler() {
                override fun handleActivityResult(
                    resultCode: Int, data: Intent?
                ) {
                    if (resultCode == Activity.RESULT_OK) {
                        handleDefaultResults(data)
                    } else {
                        apzPluginUtil.sendError(
                            mCallbackId,
                            errorCode328,
                            null,
                            aActivity,
                            aWebview,
                            true
                        )
                    }
                }
            })
    }

    private fun handleDefaultResults(data: Intent?) {
        val filePath = data?.getStringExtra("filePath")
        launch {
            AuditLog.sendToJSON()
        }
        try {
            result = JSONObject()
            result?.put("filePath", filePath)
        } catch (e: JSONException) {
            ApzLogger.i(TAG, e.toString())
        }
        apzPluginUtil.sendSuccess(
            mCallbackId,
            result, false, aActivity,
            aWebview, false
        )
    }

    private fun handleFileCategoryExternal(messageTitle:String) {
    
       val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        intent.type = "*/*"
        if (intent.resolveActivity(aActivity.packageManager) != null) {
            this.aActivity.startActivityForResult(
                Intent.createChooser(intent, messageTitle),
                PluginConstants.OPEN_DIRECTORY_REQUEST_CODE,
                object : ExternalActivityResultHandler() {
                    override  fun handleActivityResult(resultCode: Int, data: Intent?) {
                        if (resultCode == Activity.RESULT_OK) {
                            if (isOpenable.equals("Y", ignoreCase = true)) {
                                try {
                                    val i = Intent(Intent.ACTION_VIEW, data!!.data)
                                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    aActivity.startActivity(i)
                                } catch (act: ActivityNotFoundException) {
                                    // ApzLogger.e(TAG, "Activity not found");
                                    //ApzPluginUtil.sendError(t, "APZ-CNT-275", null, activity, webView, true);
                                }
                            } else {
                                val filePath: String? = FileUtils.GetRealPathFromURI(aActivity, data!!.data!!)
//                                sendToJSON()
                                try {
                                    result = JSONObject()
                                    result!!.put("filePath", filePath)
                                    result!!.put("fileURi",data!!.data!!)
                                } catch (e: JSONException) {
                                }
                                apzPluginUtil.sendSuccess(
                                    mCallbackId,
                                    result, false, aActivity,
                                    aWebview, false
                                )
                            }
                        } else {
                            apzPluginUtil.sendError(
                                mCallbackId,
                                "APZ-CNT-328",
                                null,
                                aActivity,
                                aWebview,
                                true
                            )
                        }
                    }
                })
        } else {
            Log.d("", "Unable to resolve Intent.ACTION_OPEN_DOCUMENT {}")
        }
   
    }

    private fun handleFileCategoryPhoto() {
        isOpenable = mParams.getString("openFile")
        val photo = Intent(Intent.ACTION_PICK, null)

        photo.type = "image/*"
        this.aActivity.startActivityForResult(photo,
            RESULT_LOAD_IMAGE,
            object : ExternalActivityResultHandler() {
                override fun handleActivityResult(
                    resultCode: Int, data: Intent?
                ) {
                    if (resultCode == Activity.RESULT_OK) {
                        handlePhotoResult(data)
                    } else {
                        apzPluginUtil.sendError(
                            mCallbackId,
                            errorCode328,
                            null,
                            aActivity,
                            aWebview,
                            true
                        )
                    }
                }
            })
    }

    private fun handleFileCategoryVideo() {
        isOpenable = mParams.getString("openFile")
        val video = Intent(Intent.ACTION_PICK, null)

        video.type = "video/*"
        this.aActivity.startActivityForResult(video,
            RESULT_LOAD_VIDEO,
            object : ExternalActivityResultHandler() {
                override fun handleActivityResult(
                    resultCode: Int, data: Intent?
                ) {
                    if (resultCode == Activity.RESULT_OK) {
                        handleVideoResult(data)
                    } else {
                        apzPluginUtil.sendError(
                            mCallbackId,
                            errorCode328,
                            null,
                            aActivity,
                            aWebview,
                            true
                        )
                    }
                }
            })
    }

    private fun handleVideoResult(data: Intent?) {
        val selectedImage = data?.data
        val filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = aActivity
            .applicationContext
            .contentResolver
            .query(
                selectedImage!!,
                filePathColumn, null,
                null, null
            )
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor
                .getColumnIndex(filePathColumn[0])
            val videoPath = cursor
                .getString(columnIndex)
            cursor.close()
            try {
                if (isOpenable == "Y") {
                    openFile(videoPath)
                } else {
                    launch {
                        AuditLog.sendToJSON()
                    }
                    result = JSONObject()
                    result!!.put(
                        "filePath",
                        videoPath
                    )
                    apzPluginUtil.sendSuccess(
                        mCallbackId, result,
                        false, aActivity,
                        aWebview, false
                    )
                }
            } catch (anf: ActivityNotFoundException) {
                ApzLogger.i(TAG, anf.toString())
            } catch (e: Exception) {
                ApzLogger.i(TAG, e.toString())
            }
        }
        cursor?.close()
    }

    private fun handleFileCategoryAudio() {
        isOpenable = mParams.getString("openFile")
        val audio = Intent(
            Intent.ACTION_PICK,
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )

        this.aActivity.startActivityForResult(audio,
            RESULT_LOAD_AUDIO,
            object : ExternalActivityResultHandler() {
                override fun handleActivityResult(
                    resultCode: Int, data: Intent?
                ) {
                    if (resultCode == Activity.RESULT_OK) {
                        handleAudioResult(data)
                    } else {
                        apzPluginUtil.sendError(
                            mCallbackId,
                            errorCode328,
                            null,
                            aActivity,
                            aWebview,
                            true
                        )
                    }
                }
            })
    }

    private fun handleAudioResult(data: Intent?) {
        val selectedImage = data?.data
        val filePathColumn = arrayOf(MediaStore.Audio.Media.DATA)
        val cursor: Cursor? = aActivity
            .contentResolver.query(
                selectedImage!!,
                filePathColumn, null,
                null, null
            )
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor
                .getColumnIndex(filePathColumn[0])
            val audioPath = cursor
                .getString(columnIndex)
            cursor.close()
            try {
                if (isOpenable == "Y") {
                    openFile(audioPath)
                } else {
                    result = JSONObject()
                    result!!.put(
                        "filePath",
                        audioPath
                    )
                    apzPluginUtil.sendSuccess(
                        mCallbackId, result,
                        false, aActivity,
                        aWebview, false
                    )
                }
            } catch (anf: ActivityNotFoundException) {
                ApzLogger.i(TAG, anf.toString())
            } catch (e: Exception) {
                ApzLogger.i(TAG, e.toString())
            }
        }
        cursor?.close()
    }

    private fun handlePhotoResult(data: Intent?) {
        try {
            var picturePath: String? = null
            var cursor: Cursor? = null
            val selectedImage = data!!.data
            if (selectedImage.toString()
                    .contains("file:///")
            ) {
                picturePath = selectedImage
                    .toString()
                    .substring(7)
            } else {
                val filePathColumn =
                    arrayOf(MediaStore.Images.Media.DATA)
                cursor = aActivity
                    .applicationContext
                    .contentResolver
                    .query(
                        selectedImage!!,
                        filePathColumn,
                        null, null,
                        null
                    )
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor
                        .getColumnIndex(filePathColumn[0])
                    picturePath = cursor
                        .getString(columnIndex)
                    cursor.close()
                }
            }
            if (isOpenable == "Y") {
                openFile(picturePath)
            } else {
                launch {
                    AuditLog.sendToJSON()
                }
                result = JSONObject()
                result?.put(
                    "filePath",
                    picturePath
                )
                apzPluginUtil.sendSuccess(
                    mCallbackId, result,
                    false, aActivity,
                    aWebview, false
                )
            }
            cursor?.close()
        } catch (anf: ActivityNotFoundException) {
            ApzLogger.e(TAG, anf.toString())
        } catch (e: Exception) {
            ApzLogger.e(TAG, e.toString())
        }
    }

    fun openFile(path: String?) {
        val file = File(path!!)
        if (file.exists()) {
            try {
                val i = Intent(
                    Intent.ACTION_VIEW,
                    FileProvider.getUriForFile(aActivity, BuildConfig.APPLICATION_ID, file)
                )
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                aActivity.startActivity(i)
            } catch (act: ActivityNotFoundException) {
                ApzLogger.e(TAG, "Activity not found")
                apzPluginUtil.sendError(callbackId, "APZ-CNT-275", null, aActivity, aWebview, true)
            }
        } else {
            ApzLogger.e(TAG, "File not found")
            apzPluginUtil.sendError(
                callbackId,
                "APZ-CNT-002",
                null,
                aActivity,
                aWebview,
                true
            ) //File Not found
        }
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        var RESULT_LOAD_IMAGE = 1
        var RESULT_LOAD_VIDEO = 2
        var RESULT_LOAD_AUDIO = 3
        const val BROWSE_FILE = 102
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzFileOperationPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        val isFileOperation: Boolean
            get() = true
    }

    inner class ExternalActivityResultHandlerTask : ExternalActivityResultHandler() {
        override fun handleActivityResult(resultCode: Int, data: Intent?) {
            if (resultCode == Activity.RESULT_OK) {
                val filePath = data?.getStringExtra("filePath")
                launch {
                    AuditLog.sendToJSON()
                }
                try {
                    result = JSONObject()
                    result?.put("filePath", filePath)
                } catch (e: JSONException) {
                    ApzLogger.i(TAG, e.toString())
                }
                apzPluginUtil.sendSuccess(
                    mCallbackId,
                    result, false, aActivity,
                    aWebview, false
                )
            } else {
                if (data != null) {
                    val error = data
                        .getStringExtra("error")
                    if (error.equals("ANF", ignoreCase = true)) {
                        apzPluginUtil.sendError(
                            mCallbackId,
                            "APZ-CNT-275",
                            null,
                            aActivity,
                            aWebview,
                            true
                        )
                    }
                } else apzPluginUtil.sendError(
                    mCallbackId,
                    errorCode328,
                    null,
                    aActivity,
                    aWebview,
                    true
                )
            }
        }
    }
}
