package com.iexceed.plugins.autocapturedocument

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.iexceed.appzillonapp.AppzillonMainScreen.Companion.ASSET_APP_LOC
import com.iexceed.appzillonapp.AppzillonMainScreen.Companion.SANDBOX_LOC
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.ApzActivity
import com.iexceed.common.FileUtils
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.utils.localstorage.FileAccessHelper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzAutoCapturePlugin private constructor (webView: WebView, activity: ApzActivity<*>,
                                               override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin() {
    private var mJsonObj: JSONObject? = null
    private lateinit var permissions: Array<String>
    private var mportraitMarginPercent = ""
    private var mLandscapeMarginPercent = ""
    private var mtopMarginPercent = ""
    override fun execute(params: JSONObject) {
        try {
            this.callbackId = params.getString("id")
            mCallbackId = this.callbackId
            mActivity=aActivity
            mWebview=aWebview
            mJsonObj = params
            fileName = mJsonObj!!.optString("fileName")
            mEncodingType = mJsonObj!!.optString("encodingType").trim { it <= ' ' }
            documentAspectRatio = mJsonObj!!.optString("documentWHRatio").trim { it <= ' ' }
            documentType = mJsonObj!!.optString("documentType").trim { it <= ' ' }
            defaultCaptureMode = mJsonObj!!.optString("defaultCaptureMode").trim { it <= ' ' }
            validateDocumentAspectRatio()
            val compressionLevel: String = mJsonObj!!.optString("quality").trim { it <= ' ' }
            cmpLevel = validateCompressionLevel(compressionLevel)
            setCompressFormat(mEncodingType)
            val captureTime: String = mJsonObj!!.optString("holdTimeForCapture").trim { it <= ' ' }
            val timeout: String = mJsonObj!!.optString("timeOutForCapture").trim { it <= ' ' }
            holdTimeForAutoCapture =
                if (captureTime.equals("", ignoreCase = true)) 2 else captureTime.toInt()
            timeOutForAutoCapture =
                if (timeout.equals("", ignoreCase = true)) 0 else timeout.toInt()
            val objTextToDetect: JSONObject = mJsonObj!!.optJSONObject("UIParams")!!
            if (objTextToDetect != null) {
                mportraitMarginPercent =
                    mJsonObj!!.optJSONObject("UIParams")!!.optString("portraitMarginPercent")
                        .trim { it <= ' ' }
                mLandscapeMarginPercent =
                    mJsonObj!!.optJSONObject("UIParams")!!.optString("landscapeMarginPercent")
                        .trim { it <= ' ' }
                mtopMarginPercent = mJsonObj!!.optJSONObject("UIParams")!!.optString("topMarginPercent")
                    .trim { it <= ' ' }
                istoggleButtonReq =
                    mJsonObj!!.optJSONObject("UIParams")!!.optString("toggleButton").trim { it <= ' ' }
                mPageTitle = mJsonObj!!.optJSONObject("UIParams")!!.optString("pageTitle")
                mMessageTitle = mJsonObj!!.optJSONObject("UIParams")!!.optString("messageTitle")
                mMessage = mJsonObj!!.optJSONObject("UIParams")!!.optString("message")
                mFontColor =
                    mJsonObj!!.optJSONObject("UIParams")!!.optString("fontColor").trim { it <= ' ' }
                mOverlayColor =
                    mJsonObj!!.optJSONObject("UIParams")!!.optString("overlayColor").trim { it <= ' ' }
                mScanStatus1 = mJsonObj!!.optJSONObject("UIParams")!!.optString("scanStatus1")
                mScanStatus2 = mJsonObj!!.optJSONObject("UIParams")!!.optString("scanStatus2")
                mScanStatus3 = mJsonObj!!.optJSONObject("UIParams")!!.optString("scanStatus3")
            }
            // color code validation
            val colorRegex = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
            if (!(mFontColor.matches(colorRegex.toRegex()) && mOverlayColor.matches(colorRegex.toRegex()))) {
                mFontColor = "#000000"
                mOverlayColor = "#ffffff"
            }
            mScanStatus1 =
                if (mScanStatus1.equals("", ignoreCase = true)) "Scanning..." else mScanStatus1
            mScanStatus2 =
                if (mScanStatus2.equals("", ignoreCase = true)) "Verifying..." else mScanStatus2
            mScanStatus3 =
                if (mScanStatus3.equals("", ignoreCase = true)) "Hold Steady..." else mScanStatus3
            nativePreviewScreen = mJsonObj!!.optString("nativePreviewScreen").trim { it <= ' ' }
            tPortMarginPercent = validatePortraitMarginPercent()
            topMarginPercent = validateTopMarginPercent()
            tLandMarginPercent = validateLandspaceMarginPercent()

            // Android 13 storage change
            if (ActivityCompat.checkSelfPermission(aActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions = arrayOf(
                    Manifest.permission.CAMERA
                )
                requestForPermission(permissions)
            } else {
                callAutoCapture()
            }
        } catch (e: Exception) {
            failureCallback("Error occurred", "")
        }
    }

    private fun validateLandspaceMarginPercent() =
        if (!mLandscapeMarginPercent.equals("", ignoreCase = true)) {
            if (mLandscapeMarginPercent.toInt() == 100) 30 else mLandscapeMarginPercent.toInt()
        } else {
            30
        }

    private fun validateTopMarginPercent() = if (!mtopMarginPercent.equals("", ignoreCase = true)) {
        if (mtopMarginPercent.toInt() == 0) 20 else mtopMarginPercent.toInt()
    } else {
        20
    }

    private fun validatePortraitMarginPercent() =
        if (!mportraitMarginPercent.equals("", ignoreCase = true)) {
            if (mportraitMarginPercent.toInt() == 100) 10 else mportraitMarginPercent.toInt()
        } else {
            10
        }

    private fun validateCompressionLevel(compressionLevel: String) =
        if (compressionLevel != "") {
            compressionLevel.toInt()
        } else {
            100
        }

    private fun validateDocumentAspectRatio() {
        if (documentAspectRatio.equals(
                "",
                ignoreCase = true
            ) || !documentAspectRatio.contains(":") || documentAspectRatio.split(":")
                .toTypedArray().isEmpty()
        ) {
            documentAspectRatio = "3:2"
        }
    }

    private fun requestForPermission(permissions: Array<String>) {
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
                        verifyPermissions(permissions)
                    } else {
                        permissionDeniedCallback()
                    }
                }
            }
        )
    }

    private fun verifyPermissions(permissions: Array<String?>) {
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
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    callAutoCapture()
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
            callAutoCapture()
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

    private fun callAutoCapture() {
        try {
            mOutputFileType = mJsonObj!!.optString("type", "file")
            val objTextToDetect: JSONObject = mJsonObj!!.optJSONObject("textToDetect")
            TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )
            if (objTextToDetect != null && objTextToDetect.has("type") && objTextToDetect.has("text")) {
                if (objTextToDetect.isNull("text")) {
                    isTextDetection = "Y"
                    //used if textTo detect is null put dummy value to scan card with "" string and not all objects
                    mTextTypeToDetect = "any"
                    mTextToDetect = JSONArray()
                    mTextToDetect!!.put(0, "")
                } else {
                    isTextDetection = "Y"
                    mTextTypeToDetect =
                        mJsonObj!!.optJSONObject("textToDetect").optString("type", "Any")
                    mTextToDetect = mJsonObj!!.optJSONObject("textToDetect").optJSONArray("text")
                }
            } else {
                isTextDetection = "Y"
                mTextTypeToDetect = "any"
                mTextToDetect = JSONArray()
                mTextToDetect!!.put(0, "")
            }
            val capture = Intent(aActivity, LiveObjectDetectionActivity::class.java)
            capture.putExtra("jsonStr", mJsonObj.toString())
       /*     if {
                failureCallback("Text detection is not supported in this device.", "")
            } else {*/
                if (isTextDetection.equals("Y", ignoreCase = true)) {
                    deviceTextRecognition = true
                } else {
                    failureCallback("Detection parameters not passed", "")
                    return
                }
           // }
            this.aActivity.startActivity(capture)
        } catch (e: Exception) {
            failureCallback("Operation failed", "")
            return
        }
    }

    companion object {
        private var mApzPluginUtil: IapzPluginUtil? = null
        private const val TAG = "ApzAutoCapture"
        private var pluginObj: ApzPlugin? = null
        const val AUTO_CAPTURE_PIC = 16
        lateinit var mWebview: WebView
        lateinit var mActivity: Activity
        var mCallbackId: String? = null
        private var mEncodingType = ""
        var mCompressFormat: Bitmap.CompressFormat? = null
        private var mEncodingFormat: String? = null
        private var fileName: String? = null
        var gMatchingThreshold = 0f
        var sTemplateFileName = ""
        var sTesseractCheckRequired = ""
        @JvmField
        var mTextTypeToDetect = ""
        var isTextDetection = ""
        var isTemplateDetection = ""
        @JvmField
        var isManualCapture = false
        @JvmField
        var deviceTextRecognition = true
        private var mOutputFileType = ""
        @JvmField
        var documentAspectRatio = ""
        @JvmField
        var mTextToDetect: JSONArray? = null
        @JvmField
        var tPortMarginPercent = 0
        var tLandMarginPercent = 0
        @JvmField
        var topMarginPercent = 0
        @JvmField
        var mPageTitle = ""
        @JvmField
        var mMessageTitle = ""
        @JvmField
        var mMessage = ""
        @JvmField
        var mFontColor = ""
        @JvmField
        var mOverlayColor = ""
        @JvmField
        var mScanStatus1 = "Scanning..."
        @JvmField
        var mScanStatus2 = "Verifying..."
        @JvmField
        var mScanStatus3 = ""
        @JvmField
        var defaultCaptureMode = ""
        @JvmField
        var istoggleButtonReq = ""
        @JvmField
        var holdTimeForAutoCapture = 0
        @JvmField
        var timeOutForAutoCapture = 0
        @JvmField
        var nativePreviewScreen = ""
        @JvmField
        var documentType = ""
        var pictureFile: File? = null
        private var cmpLevel = 0
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                this.mApzPluginUtil = apzPluginUtil
                pluginObj = ApzAutoCapturePlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        @JvmStatic
        fun fetchOutput(bitmap: Bitmap?, textArr: String?, fullText: String?) {
            var outputData: String? = ""
            try {
                val resultBody = JSONObject()
                val internalres = JSONObject()
                if (bitmap != null) {
                    var inputBitmap: Bitmap? = bitmap
                    outputData = if (mOutputFileType.equals("base64", ignoreCase = true)) {
                        getBase64Image(inputBitmap!!)
                    } else {
                        saveImage(inputBitmap!!)
                    }
                    inputBitmap = null
                    internalres.put("type", mOutputFileType)
                    internalres.put("data", outputData)
                    if (deviceTextRecognition) {
                        val jsonArray = JSONArray(textArr)
                        internalres.put("ocrText", jsonArray)
                        internalres.put("ocrWholeText", fullText)
                    }
                    resultBody.put("outputFile", internalres)
                    this.mApzPluginUtil?.sendSuccess(
                        mCallbackId,
                        resultBody, false,
                        mActivity,
                        mWebview, true
                    )
                    pluginObj = null
                }
            } catch (e: Exception) {
                //Sonar fix
            }
        }

        private fun getBase64Image(bitmap: Bitmap): String? {
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
            } catch (e: OutOfMemoryError) {
                failureCallback("Exception in base64 image", "")
            } catch (e: IOException) {
                failureCallback("Exception in base64 image", "")
            }
            return returnBase64
        }

        @JvmStatic
        fun failureCallback(mMessage: String, errorCode: String) {
            var errorCode = errorCode
            val json = JSONObject()
            try {
                json.put("text", "" + mMessage)
                if (errorCode.equals("", ignoreCase = true)) {
                    errorCode = "APZ-CNT-341"
                }
            } catch (e: JSONException) {
                //Sonar fix
            }
            this.mApzPluginUtil?.sendError(mCallbackId, errorCode, json, mActivity, mWebview, true)
            pluginObj = null
        }

        private fun saveImage(bitmp: Bitmap): String? {
            val photoFile: String
            var photoFilepath: String? = null
            val dateFormat = SimpleDateFormat("ddMMyyhhmmss")
            val date = dateFormat.format(Date())
            photoFile = fileName + date + mEncodingFormat
            try {
                val pictureFileDir = getDir("ScannedDocuments")
                if (pictureFileDir != null) {
                    val filenameWithPath = (pictureFileDir.path
                            + File.separator + photoFile)
                    pictureFile = File(filenameWithPath)
                    if (pictureFile!!.exists() && pictureFile!!.delete()) {
                        val mediaScanIntent = Intent(
                            Intent.ACTION_MEDIA_SCANNER_STARTED
                        )
                        val contentUri = Uri
                            .parse(
                                "file://"
                                        + FileAccessHelper.getExternalFileDirFile(mActivity)
                            ) // out
                        mediaScanIntent.setData(contentUri)
                        mActivity.sendBroadcast(mediaScanIntent, AppzillonConstants.APPZILLON_BROADCAST_PERMISSION)
                        // Abhishek 18 March 2015 to handle the crash while
                        // sending broadcast in KITKAT and above END
                        try {
                            pictureFile!!.createNewFile()
                        } catch (e: IOException) {
                            //Sonar fix
                        } catch (e: OutOfMemoryError) {
                            failureCallback("", "")
                        }
                    }
                    val fos = FileAccessHelper.getFileOutPutStream(pictureFile!!)
                    val bos = BufferedOutputStream(
                        fos,
                        1024 * 8
                    )
                    bitmp.compress(mCompressFormat, cmpLevel, bos)
                    bos.flush()
                    bos.close()
                    fos.close()
                    photoFilepath = filenameWithPath
                }
            } catch (e: Exception) {
                failureCallback("Exception in saving image", "")
            }
            return photoFilepath
        }

        private fun getDir(loc: String): File? {
            if (FileUtils.isSDCardPresent()) {
                val sdDir: File = File(
                    SANDBOX_LOC
                        .toString() + File.separator + ASSET_APP_LOC + loc
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
                    //handle exception
                }
            } else {
                failureCallback("Operation cancelled", "")
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

        val isAutoCapture: Boolean
            get() = true
    }

    init {
        aWebview = webView
        aActivity = activity
    }
}
