package com.iexceed.plugins.barcodegenerator

import android.graphics.*
import android.util.Base64
import android.webkit.WebView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.FileAccessHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class BarcodeGenerator private constructor(webView: WebView, activity: ApzActivity<*>,
                       override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {
    override var TAG = "BarcodeGenerator"
    var isLogo = "N"
    var base64: String? = null


    private var targetHeight = 200
    private var targetWidth = 200

    init {
        aActivity = activity
        aWebview = webView
    }

    /*
    * GeneratingFile using input string
    * */
    private fun generateFile(inputString: String, filePath: String, fileName: String?)
    {
        val dest = AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + File.separator + filePath
        try {
            val imageBitmap: Bitmap? = encodeAsBitmap(inputString)
            if (imageBitmap != null)
            {
                if (!File(dest).exists()) File(dest).mkdirs()
                val imageFile = File(dest, "$fileName.jpg")
                val os: OutputStream
                try {
                    os = FileAccessHelper.getFileOutPutStream(imageFile)
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    os.flush()
                    os.close()
                    val lJson = JSONObject()
                    lJson.put("text", imageFile.absolutePath)
                    //Send Success
                    apzPluginUtil.sendSuccess(callbackId, lJson, false, aActivity, aWebview, true)
                } catch (e: Exception) {
                    ApzLogger.e(TAG, e.message.toString())
                    handleFailure("Error writing bitmap.")
                }
            } else {
                handleFailure("Unable to encode data.")
            }
        } catch (e: WriterException) {
            handleFailure("WriterException")
        }
    }

    /*
    * Converting string to Base64 bitmap
    * */
    private fun generateBase64(data: String)
    {
        try {
            val imageBitmap: Bitmap? = encodeAsBitmap(data)
            if (imageBitmap != null)
            {
                val baos = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val byteArray = baos.toByteArray()
                val str = Base64.encodeToString(byteArray, Base64.DEFAULT)
                val rJson = JSONObject()
                try {
                    rJson.put("text", str)
                } catch (e: JSONException) {
                    //Sonar fix
                }
                //Send Success
                apzPluginUtil.sendSuccess(callbackId, rJson, false, aActivity, aWebview, true)
            } else {
                handleFailure("Unable to encode data.")
            }
        } catch (e: Exception) {
            handleFailure("WriterException")
        }
    }

	private fun encodeAsBitmap(str: String): Bitmap? {
        val result: BitMatrix
        result = try {
            MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, targetWidth, targetHeight, null)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            handleFailure("Unsupported format")
            return null
        }
        val w: Int = result.getWidth()
        val h: Int = result.getHeight()
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            val offset: Int = y * w
            for (x in 0 until w) {
                pixels[offset + x] = if (result.get(x, y)) Color.BLACK else Color.WHITE
            }
        }
        val bitmap: Bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, targetWidth, 0, 0, w, h)
        return if (isLogo.equals("Y", ignoreCase = true)) {
            val resID: Int = aActivity.getResources()
                .getIdentifier("qr_code_logo", "drawable", aActivity.getPackageName())
            if (resID == 0) {
                bitmap
            } else {
                val icon: Bitmap = BitmapFactory.decodeResource(aActivity.getResources(), resID)
                mergeBitmaps(icon, bitmap)
            }
        } else {
            bitmap
        }
    }

    private fun mergeBitmaps(logo: Bitmap, qrcode: Bitmap): Bitmap {
        val combined: Bitmap =
            Bitmap.createBitmap(qrcode.getWidth(), qrcode.getHeight(), qrcode.getConfig())
        val canvas = Canvas(combined)
        val canvasWidth: Int = canvas.getWidth()
        val canvasHeight: Int = canvas.getHeight()
        canvas.drawBitmap(qrcode, Matrix(), null)
        val resizeLogo: Bitmap =
            Bitmap.createScaledBitmap(logo, canvasWidth / 5, canvasHeight / 5, true)
        val centreX = ((canvasWidth - resizeLogo.getWidth()) / 2).toFloat()
        val centreY = ((canvasHeight - resizeLogo.getHeight()) / 2).toFloat()
        canvas.drawBitmap(resizeLogo, centreX, centreY, null)
        return combined
    }

    private fun handleFailure(str: String)
    {
        val fJson = JSONObject()
        try {
            fJson.put("error", str)
        } catch (e: JSONException) {
            //Sonar fix
        }
        apzPluginUtil.sendError(callbackId, "APZ-DM-006", fJson, aActivity, aWebview, true)
    }

    override fun execute(params: JSONObject)
    {
        try {
            callbackId = params.getString("id")
            val inputString: String = params.getString("inputString")
            base64 = params.getString("base64")
            isLogo = params.optString("isLogoImagePresent")

            if (!params.optString("targetWidth").equals("") &&
                !params.optString("targetHeight").equals("")
            ) {
                try {
                    targetWidth = params.optString("targetWidth").toInt()
                    targetHeight = params.optString("targetHeight").toInt()
                } catch (e: java.lang.Exception) {
                    setDefaultQRDimension()
                    e.printStackTrace()
                }
            } else {
                setDefaultQRDimension()
            }

            if (base64.equals("N", ignoreCase = true))
            {
                var filePath: String = params.optString("destinationPath")
                if (filePath.isEmpty()) filePath = "TempFolderQRCode"
                var fileName: String = params.optString("fileName")
                if (fileName.isEmpty()) {
                    val date = Date()
                    val dateFormatter: DateFormat = SimpleDateFormat("yyyyMMdd_hhmmss", Locale.getDefault())
                    fileName = dateFormatter.format(date)
                }
                generateFile(inputString, filePath, fileName)
            } else {
                generateBase64(inputString)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private  fun setDefaultQRDimension(){
        if (AppzillonUtils.isTablet(aActivity)){
            targetWidth = 300
            targetHeight = 300
        }else {
            targetWidth = 200
            targetHeight = 200
        }
    }


    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = BarcodeGenerator(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        val isPlugin: Boolean
            get() = true
    }
}
