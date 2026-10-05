package com.iexceed.plugins.processimage

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.*
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.ApzActivity
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.FileAccessHelper
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
class ApzProcessImgPlugin private constructor(webView: WebView,
                                      activity: ApzActivity<*>,
                                      override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin() {
    //override val TAG: String = "ApzProcessImgPlugin"
    private var mJsonObj: JSONObject? = null
    private val mAction: String? = null
    private var mInputFileType: String = ""
    private var mInputData: String = ""
    private var mEncodingType: String = ""
    var mCompressFormat: Bitmap.CompressFormat? = null
    private var mEncodingFormat: String? = null
    private var FILENAME: String? = null
    var pictureFile: File? = null
    private var cmpLevel: Int = 0
    private lateinit var permissions: Array<String>
    public override fun execute(params: JSONObject) {
        try {
            // mAction = mJsonObj.getString("action");
            mJsonObj = params
            callbackId = params.getString("id")

            var isSourceTypeExternal: Boolean = false
            mInputFileType = mJsonObj!!.optJSONObject("inputFile").optString("type")
            FILENAME = mJsonObj!!.optJSONObject("outputFile").optString("fileName")
            val threshold: String = mJsonObj!!.optJSONObject("outputFile").optString("threshold")
            mInputData = mJsonObj!!.optJSONObject("inputFile").optString("data")
            mEncodingType = mJsonObj!!.optString("encodingType")
            val compressionLevel: String = mJsonObj!!.optString("quality")
            if (compressionLevel != "") {
                cmpLevel = compressionLevel.toInt()
            } else {
                cmpLevel = 100
            }
            if (!threshold.equals("", ignoreCase = true)) {
                mThreshold = threshold.toInt()
            } else {
                mThreshold = 128
            }
            setCompressFormat(mEncodingType)
            if (mInputFileType.equals("file", ignoreCase = true)) {
                isSourceTypeExternal = true
            }

            if ("N".equals(
                    aActivity.getResources().getString(R.string.INTERNALSANDBOX),
                    ignoreCase = true
                )
                || isSourceTypeExternal
            ) {
                handlePermissionRequest()
            } else {
                callProccessor()
            }
        } catch (e: Exception) {
            failureCallback("Exception occured")
        }
    }

    private fun handlePermissionRequest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    aActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestForPermission()
            } else {
                callProccessor()
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    aActivity,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                requestForPermission()
            } else {
                callProccessor()
            }
        }
    }

    private fun requestForPermission() {
        ApzLogger.d(TAG, "Inside requestForPermission ")
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                aActivity.startOnPermissionForResult(
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
                                handleGivenPemrissions(permissions)
                            } else {
                                PermissionDeniedCallback()
                            }
                        }
                    }
                )
            }

    }

    private fun handleGivenPemrissions(permissions: Array<String?>) {
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
            PermissionDeniedCallback()
        } else if (denied) {
            displayReconfirmationMessage()
        } else {
            callProccessor()
        }
    }

    private fun displayReconfirmationMessage() {
        val message: String =
            "To access image from gallery, allow app to access by requested permissions"
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(aActivity)
        alertDialogBuilder.setTitle("Permission Denied")
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow", object : DialogInterface.OnClickListener {
                public override fun onClick(dialog: DialogInterface, id: Int) {
                    dialog.cancel()
                    requestForPermission()
                }
            }).setNegativeButton("Deny", object : DialogInterface.OnClickListener {
                public override fun onClick(dialog: DialogInterface, id: Int) {
                    dialog.cancel()
                    PermissionDeniedCallback()
                }
            })
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun PermissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied("Image Storage", callbackId, aActivity, aWebview)
    }

    fun callProccessor() {
        try {
            val mOutputFileType: String =
                mJsonObj!!.optJSONObject("outputFile").optString("type", "file")
            var mAction: String = mJsonObj!!.optString("imageAction", "grayscale")
            var outputData: String? = ""
            if (mAction.equals("", ignoreCase = true)) {
                mAction = "grayscale"
            }
            val resultBody: JSONObject = JSONObject()
            val internalres: JSONObject = JSONObject()
            if (!mInputData.equals("", ignoreCase = true)) {
                var inputBitmap: Bitmap? = getBitmap(mInputFileType, mInputData)
                if (inputBitmap != null) {
                    inputBitmap = convertToBlackWhite(inputBitmap, mAction)
                    if (mOutputFileType.equals("base64", ignoreCase = true)) {
                        outputData = getBase64Image(inputBitmap)
                    } else {
                        outputData = saveImage(inputBitmap)
                    }
                    inputBitmap = null
                    internalres.put("type", mOutputFileType)
                    internalres.put("data", outputData)
                    resultBody.put("outputFile", internalres)
                    resultBody.put("imageAction", mAction)
                    apzPluginUtil.sendSuccess(
                        callbackId,
                        resultBody, false,
                        aActivity,
                        aWebview, true
                    )
                    pluginObj = null
                }
            } else {
                failureCallback("Image data is invalid")
            }
        } catch (e: Exception) {
            failureCallback("Exception occured")
        }
    }

    private fun setCompressFormat(mEncodingType2: String) {
        if ((mEncodingType2.equals("JPG", ignoreCase = true)
                    || mEncodingType2.equals("JPEG", ignoreCase = true))
        ) {
            mCompressFormat = Bitmap.CompressFormat.JPEG
            mEncodingFormat = ".jpg"
        } else if (mEncodingType2.equals("PNG", ignoreCase = true)) {
            mCompressFormat = Bitmap.CompressFormat.PNG
            mEncodingFormat = ".png"
        }
    }

    fun getBitmap(fileType: String, fileData: String?): Bitmap? {
        var finalBitmap: Bitmap? = null
        val bmOptions: BitmapFactory.Options = BitmapFactory.Options()
        if (fileType.equals("file", ignoreCase = true)) {
            finalBitmap = BitmapFactory.decodeFile(fileData, bmOptions)
        } else if (fileType.equals("base64", ignoreCase = true)) {
            val decodedString: ByteArray = Base64.decode(
                fileData, Base64.NO_WRAP
            )
            finalBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
        return finalBitmap
    }

    private fun getBase64Image(bitmap: Bitmap?): String? {
        var bitmap: Bitmap? = bitmap
        var returnBase64: String? = null
        try {
            var baos: ByteArrayOutputStream? = ByteArrayOutputStream()
            bitmap!!.compress(mCompressFormat, cmpLevel, baos) // mBitmap is the
            // bitmap object
            val b: ByteArray = baos!!.toByteArray()
            val base64Image: String = Base64.encodeToString(b, Base64.NO_WRAP)
            returnBase64 = base64Image
            baos.close()
        } catch (e: OutOfMemoryError) {
            failureCallback("Exception in base64 image")
        } catch (e: IOException) {
            failureCallback("Exception in base64 image")
        }
        return returnBase64
    }

    fun failureCallback(mMessage: String?) {
        val json = JSONObject()
        try {
            json.put("text", "Operation Cancelled")
        } catch (e: JSONException) {
            //Sonar fix
        }
        apzPluginUtil.sendError(callbackId, "APZ-CNT-065", json, aActivity, aWebview, true)
    }

    private fun saveImage(bitmp: Bitmap?): String? {
        val photoFile: String
        var photoFilepath: String? = null
        val dateFormat: SimpleDateFormat = SimpleDateFormat("ddmmyyhhmmss")
        val date: String = dateFormat.format(Date())
        photoFile = FILENAME + date + mEncodingFormat
        try {
            val pictureFileDir: File? = getDir("photo")
            if (pictureFileDir != null) {
                val filenameWithPath: String = (pictureFileDir.getPath()
                        + File.separator + photoFile)
                //                pictureFile = new File("" + filenameWithPath );
                pictureFile = AppzillonUtils.getApzFile(
                    pictureFileDir.getPath() + File.separator + photoFile,
                    null
                )
                if (pictureFile!!.exists() && pictureFile!!.delete()) {
                    val contentUri: Uri = Uri
                        .parse(
                            ("file://"
                                    + FileAccessHelper.getExternalFileDirFile(aActivity))
                        )
                    MediaScannerConnection.scanFile(aActivity, arrayOf(File(contentUri.toString()).absolutePath), null) { _, _ ->
                        // Scanning is complete, and the file is now available in the media database
                        Log.d("TAG","SUCCESS")
                    }
                    // Abhishek 18 March 2015 to handle the crash while
                    // sending broadcast in KITKAT and above END
                    try {
                        val isCreated: Boolean = pictureFile!!.createNewFile()
                        if (!isCreated){
                            //Sonar fix
                        }
                    } catch (e: OutOfMemoryError) {
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
                MediaScannerConnection.scanFile(aActivity.applicationContext,
                    arrayOf(pictureFile.toString()),
                    null
                ) { path, uri ->
                    ApzLogger.i(
                        TAG, "ExternalStorage Scanned "
                                + path + ":"
                    )
                    ApzLogger.i(
                        TAG, "ExternalStorage -> uri="
                                + uri
                    )
                }
            }
        } catch(ex:Exception){
            failureCallback("exception")
        }
        return photoFilepath
    }

    private fun getDir(loc: String): File? {
        if (com.iexceed.common.FileUtils.isSDCardPresent()) {
//            File sdDir = new File(SANDBOX_LOC
//                    + File.separator + ASSET_APP_LOC + loc);
            val sdDir: File = AppzillonUtils.getApzFile(
                AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + loc,
                null
            )
            try {
                if (sdDir.exists()) {
                    return sdDir
                } else {
                    if (sdDir.mkdirs()) {
                        return sdDir
                    } else {
                        return null
                    }
                }
            } catch (e: Exception) {
                ApzLogger.e(TAG, "Create directory failed : " + e.message)
            }
        } else {
            failureCallback("Operation cancelled")
        }
        return null
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        private var mThreshold: Int = 0
        @JvmStatic
        fun createPlugin(webView: WebView?, activity: ApzActivity<*>?, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = webView?.let { activity?.let { it1 -> ApzProcessImgPlugin(it, it1, apzPluginUtil) } }
            }
            return pluginObj
        }

        fun convertToBlackWhite(colorBmp: Bitmap, mAction: String): Bitmap {
            val bmpMonochrome: Bitmap = Bitmap.createBitmap(
                colorBmp.getWidth(),
                colorBmp.getHeight(),
                Bitmap.Config.ARGB_8888
            )
            val canvas: Canvas = Canvas(bmpMonochrome)
            //set contrast
            val contrastMatrix: ColorMatrix = ColorMatrix()
            //change contrast
            if (mAction.equals("grayscale", ignoreCase = true)) {
                contrastMatrix.setSaturation(0f)
            } else if (mAction.equals("BW", ignoreCase = true)) {
                contrastMatrix.setSaturation(0f)
                contrastMatrix.set(
                    floatArrayOf(
                        128f,
                        128f,
                        128f,
                        0f,
                        (-(mThreshold) * 255).toFloat(),
                        128f,
                        128f,
                        128f,
                        0f,
                        (-(mThreshold) * 255).toFloat(),
                        128f,
                        128f,
                        128f,
                        0f,
                        (-(mThreshold) * 255).toFloat(),
                        0f,
                        0f,
                        0f,
                        1f,
                        0f
                    )
                )
            }
            //apply contrast
            val contrastPaint: Paint = Paint()
            contrastPaint.setColorFilter(ColorMatrixColorFilter(contrastMatrix))
            canvas.drawBitmap(colorBmp, 0f, 0f, contrastPaint)
            return bmpMonochrome
        }

        @JvmStatic
        fun isPlugin(): Boolean {
            return true
        }
    }

    init {
        aWebview = webView
        aActivity = activity
    }
}
