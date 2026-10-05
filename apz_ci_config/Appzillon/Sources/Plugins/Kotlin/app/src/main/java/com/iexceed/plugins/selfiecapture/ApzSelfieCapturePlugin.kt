package com.iexceed.plugins.selfiecapture

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.ApzActivity
import com.iexceed.common.FileUtils
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.utils.localstorage.FileAccessHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzSelfieCapturePlugin private constructor(webView: WebView,
                                                 activity: ApzActivity<*>,
                                                 override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin() {
    private var mJsonObj: JSONObject? = null
    private lateinit var permissions: Array<String>
    override fun execute(params: JSONObject) {
        try {
            this.callbackId = params.getString("id")
            val callbackId1 : String = params.getString("callBack")
            mCallbackId = this.callbackId
            mActivity =aActivity
            mWebview =aWebview
            mJsonObj = params
            fileName = mJsonObj!!.optString("fileName")
            mEncodingType = mJsonObj!!.optString("encodingType").trim { it <= ' ' }
            blinkEyeDetection = mJsonObj!!.optString("blinkEyeDetection").trim { it <= ' ' }
            facePageTitle = mJsonObj!!.optString("pageTitle")
            faceInstruction1 = mJsonObj!!.optString("faceInstruction1")
            faceInstruction2 = mJsonObj!!.optString("faceInstruction2")
            faceInstruction3 = mJsonObj!!.optString("faceInstruction3")
            blinkInstruction = mJsonObj!!.optString("blinkInstruction")
            val captureTime: String = mJsonObj!!.optString("holdTimeForCapture").trim { it <= ' ' }
            holdTimeForCapture =
                if (captureTime.equals("", ignoreCase = true)) 2 else captureTime.toInt()
            holdTimeInstruction = mJsonObj!!.optString("holdTimeInstruction")
            nativePreviewScreen = mJsonObj!!.optString("nativePreviewScreen").trim { it <= ' ' }
            val position: String = mJsonObj!!.optString("instructionPosition").trim { it <= ' ' }
            instructionPosition =
                if (position.equals("", ignoreCase = true)) 3 else position.toInt()
            faceScanningMsg = mJsonObj!!.optString("scanStatus")
            faceFontColor = mJsonObj!!.optString("fontColor")
            faceOverlayColor = mJsonObj!!.optString("overlayColor")
            // color code validation
            val colorRegex = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
            if (!(faceFontColor.matches(colorRegex.toRegex()) && faceOverlayColor.matches(colorRegex.toRegex()))) {
                faceFontColor = "#000000"
                faceOverlayColor = "#ffffff"
            }
            val compressionLevel: String = mJsonObj!!.optString("quality").trim { it <= ' ' }
            if (compressionLevel != "") {
                cmpLevel = compressionLevel.toInt()
            } else {
                cmpLevel = 100
            }
            setCompressFormat(mEncodingType)
            val targetWidth: String = mJsonObj!!.optString("targetWidth").trim { it <= ' ' }
            if (!targetWidth.isEmpty() && targetWidth != null) {
                HTMLWIDTH = targetWidth.toInt()
            }
            val targetHeight: String = mJsonObj!!.optString("targetHeight").trim { it <= ' ' }
            if (!targetHeight.isEmpty() && targetHeight != null) {
                HTMLHEIGHT = targetHeight.toInt()
            }

            var isSourceTypeExternal = false
            if ("N".equals(
                    aActivity.getResources().getString(R.string.INTERNALSANDBOX),
                    ignoreCase = true
                )
            ) {
                isSourceTypeExternal = true
            }

            // Android 13 storage change
            if (ActivityCompat.checkSelfPermission(aActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions = arrayOf(
                    Manifest.permission.CAMERA
                )
                requestForPermission(permissions)
            } else {
                callSelfieCapture()
            }
        } catch (e: Exception) {
            apzPluginUtil.sendError(callbackId, "APZ-CNT-211", null, mActivity, mWebview, true)
        }
    }

    private fun requestForPermission(permissions:Array<String>) {
        this.aActivity.startOnPermissionForResult(
            aActivity,
            permissions,
            PluginConstants.APZ_REQ_CAMERA,
            object : OnPermissionsResultHandler() {
                override fun handlePermissionResult(
                    requestCode: Int,
                    permissions: Array<String?>,
                    grantResults: IntArray
                ) {
                    if (requestCode == PluginConstants.APZ_REQ_CAMERA) {
                        handleCameraPermissions(permissions)
                    } else {
                        permissionDeniedCallback()
                    }
                }
            }
        )
    }

    private fun handleCameraPermissions(permissions: Array<String?>) {
        var denied = false
        var neverAskAgain = false
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity,
                    permission!!
                )
            ) {
                denied = true
            } else {
                if (ActivityCompat.checkSelfPermission(
                        mActivity,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    callSelfieCapture();
                } else {
                    neverAskAgain = true
                }
            }
        }
        if (neverAskAgain) {
            permissionDeniedCallback()
        } else if (denied) {
            displayReconfirmationMessage()
        } else {
            callSelfieCapture()
        }
    }

    private fun displayReconfirmationMessage() {
        val message =
            "To capture image/select image from gallery, allow app to access by requested permissions"
        val alertDialogBuilder = AlertDialog.Builder(aActivity)
        alertDialogBuilder.setTitle("Permission Denied")
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id: Int) {
                    dialog.cancel()
                    requestForPermission(permissions)
                }
            }).setNegativeButton("Deny", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, id: Int) {
                    dialog.cancel()
                    permissionDeniedCallback()
                }
            })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun permissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied(
            "Camera or Storage ",
            callbackId,
            this.aActivity,
            this.aWebview
        )
    }

    private fun callSelfieCapture() {
        try {
            mOutputFileType = mJsonObj!!.optString("type", "file")
            val capture = Intent(aActivity, LivePreviewActivity::class.java)
            capture.putExtra("jsonStr", mJsonObj.toString())
            this.aActivity.startActivity(capture)
        } catch (e: Exception) {
            failureCallback("Operation failed")
        }
    }

    companion object {
        lateinit var apzPluginUtil : IapzPluginUtil
        private const val TAG = "ApzSelfieCapture"
        private var pluginObj: ApzPlugin? = null
        const val AUTO_CAPTURE_PIC = 16
         lateinit var mWebview: WebView
         lateinit var mActivity: Activity
        var mCallbackId: String? = null
        private var mEncodingType = ""
        var mCompressFormat: Bitmap.CompressFormat? = null
        private var mEncodingFormat: String? = null
        private var fileName: String? = null
        @JvmField
        var blinkEyeDetection = "N"
        @JvmField
        var facePageTitle = ""
        @JvmField
        var faceInstruction1 = ""
        @JvmField
        var faceInstruction2 = ""
        @JvmField
        var faceInstruction3 = ""
        @JvmField
        var blinkInstruction = ""
        @JvmField
        var faceScanningMsg = ""
        var mOutputFileType = ""
        @JvmField
        var faceFontColor = ""
        @JvmField
        var faceOverlayColor = ""
        @JvmField
        var nativePreviewScreen = ""
        var instructionPosition = 0
        @JvmField
        var holdTimeForCapture = 0
        @JvmField
        var holdTimeInstruction = ""
        var pictureFile: File? = null
        private var cmpLevel = 0
        private var HTMLWIDTH = 0
        private var HTMLHEIGHT = 0
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                this.apzPluginUtil = apzPluginUtil
                pluginObj = ApzSelfieCapturePlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        @JvmStatic
        fun fetchOutput(bitmap: Bitmap?) {
            var outputData: String? = ""
            try {
                val resultBody = JSONObject()
                val internalres = JSONObject()
                if (bitmap != null) {
                    //Bitmap inputBitmap = bitmap;
                    var inputBitmap: Bitmap?
                    inputBitmap=bitmap
                    inputBitmap = if (HTMLHEIGHT > 0 && HTMLWIDTH > 0) {
                        Utils.compressSelfieBmpByHeightWeigth(
                            bitmap,
                            HTMLWIDTH,
                            HTMLHEIGHT,
                            mActivity
                        )
                    } else {
                        bitmap
                    }
                    outputData = if (mOutputFileType.equals("base64", ignoreCase = true)) {
                        getBase64Image(inputBitmap)
                    } else {
                        saveImage(inputBitmap)
                    }
                    inputBitmap = null
                    internalres.put("type", mOutputFileType)
                    internalres.put("data", outputData)
                    resultBody.put("outputFile", internalres)
                    this.apzPluginUtil.sendSuccess(
                        mCallbackId,
                        resultBody, false,
                        mActivity,
                        mWebview, true
                    )
                    pluginObj = null
                }
            } catch (e: Exception) {
            }
        }

        private fun getBase64Image(bitmap: Bitmap?): String? {
            var bitmap: Bitmap? = bitmap
            var returnBase64: String? = null
            try {
                var baos: ByteArrayOutputStream? = ByteArrayOutputStream()
                bitmap!!.compress(mCompressFormat, cmpLevel, baos) // mBitmap is the
                // bitmap object
                val b = baos!!.toByteArray()
                val base64Image = Base64.encodeToString(b, Base64.NO_WRAP)
                returnBase64 = base64Image
                baos.close()
                baos = null
                bitmap = null
            } catch (e: Exception) {
                failureCallback("Exception in base64 image")
            }
            return returnBase64
        }

        @JvmStatic
        fun failureCallback(mMessage: String?) {
            val json = JSONObject()
            try {
                json.put("text", "" + mMessage)
            } catch (e: JSONException) {
            }
            this.apzPluginUtil.sendError(mCallbackId, "APZ-CNT-211", json, mActivity, mWebview, true)
            pluginObj = null
        }

        private fun saveImage(bitmp: Bitmap?): String? {
            val photoFile: String
            var photoFilepath: String? = null
            val dateFormat = SimpleDateFormat("ddMMyyhhmmss")
            val date = dateFormat.format(Date())
            photoFile = fileName + date + mEncodingFormat
            try {
                val pictureFileDir = getDir("Selfies")
                if (pictureFileDir != null) {
                    val filenameWithPath = (pictureFileDir.path
                            + File.separator + photoFile)
                    //                pictureFile = new File(filenameWithPath);
                    pictureFile = File(filenameWithPath)
                    if (pictureFile!!.exists() && pictureFile!!.delete()) {
                        val contentUri: Uri = Uri
                            .parse(
                                ("file://"
                                        + FileAccessHelper.getExternalFileDirFile(mActivity))
                            )
                        MediaScannerConnection.scanFile(mActivity, arrayOf(File(contentUri.toString()).absolutePath), null) { _, _ ->
                            // Scanning is complete, and the file is now available in the media database
                            Log.d("TAG","SUCCESS")
                        }
                        // Abhishek 18 March 2015 to handle the crash while
                        // sending broadcast in KITKAT and above END
                        try {
                            val isCreated = pictureFile!!.createNewFile()
                        } catch (e: Exception) {
                            failureCallback(e.message)
                        }
                    }
                    val fos = FileAccessHelper.getFileOutPutStream(pictureFile!!)
                    val bos = BufferedOutputStream(
                        fos,
                        1024 * 8
                    )
                    bitmp!!.compress(mCompressFormat, cmpLevel, bos)
                    bos.flush()
                    bos.close()
                    fos.close()
                    photoFilepath = filenameWithPath

                }
            } catch (e: Exception) {
                failureCallback("Exception in saving image")
            }
            return photoFilepath
        }

        private fun getDir(loc: String): File? {
            if (FileUtils.isSDCardPresent()) {
//            File sdDir = new File(SANDBOX_LOC
//                    + File.separator + ASSET_APP_LOC + loc);
                val sdDir: File = File(
                    AppzillonMainScreen.SANDBOX_LOC
                        .toString() + File.separator + AppzillonMainScreen.ASSET_APP_LOC + loc
                )
                try {
                    return if (sdDir.exists()) {
                        sdDir
                    } else {
                        if (sdDir.mkdirs()) {
                            sdDir
                        } else {
                            null
                        }
                    }
                } catch (e: Exception) {
                    //Sonar fix
                }
            } else {
                failureCallback("Operation cancelled")
            }
            return null
        }

        private fun setCompressFormat(mEncodingType2: String) {
            if (mEncodingType2.equals("JPG", ignoreCase = true)
                || mEncodingType2.equals("JPEG", ignoreCase = true)
            ) {
                mCompressFormat = Bitmap.CompressFormat.JPEG
                mEncodingFormat = ".jpg"
            } else if (mEncodingType2.equals("PNG", ignoreCase = true)) {
                mCompressFormat = Bitmap.CompressFormat.PNG
                mEncodingFormat = ".png"
            }
        }

        @JvmStatic
        fun setInstructionTextView(message: String?) {
            mActivity.runOnUiThread(Runnable {
                try {
                    when (instructionPosition) {
                        1 -> LivePreviewActivity.fInstruction1!!.text = message
                        2 -> LivePreviewActivity.fInstruction2!!.text = message
                        else -> LivePreviewActivity.fInstruction3!!.text = message
                    }
                } catch (e: Exception) {
                }
            })
        }

        val isSelfieCapture: Boolean
            get() = true
    }

    init {

        aWebview = webView
        aActivity = activity

    }
}
