package com.iexceed.plugins.barcode

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.WebView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.iexceed.plugins.IapzPluginUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import kotlin.coroutines.CoroutineContext


/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/

class ScanImageFromGallery(
    private val mActivity: Activity,
    private val webView: WebView,
    mJson: String, apzPluginUtil: IapzPluginUtil): CoroutineScope
{
    private val context: Context = mActivity.applicationContext
    private var mApzPluginUtil = apzPluginUtil
    lateinit var mCallbackId: String
    lateinit var filePath: String
    fun scanImage() {
        launch{
            try {
                var imageStream: InputStream? = null
                imageStream = try {
                    val imageUri = Uri.fromFile(File(filePath))
                    mActivity.contentResolver.openInputStream(imageUri)
                } catch (e: FileNotFoundException) {
                    mApzPluginUtil.sendError(
                        mCallbackId, "APZ-CNT-070", null, mActivity,
                        webView, true)
                    return@launch
                }
                val bitmap = BitmapFactory.decodeStream(imageStream)
                val detector = BarcodeDetector.Builder(context)
                    .setBarcodeFormats(Barcode.DATA_MATRIX or Barcode.QR_CODE).build()
                if (!detector.isOperational) {
                    failureCallBack("Failed to scan the image.")
                    return@launch
                }
                val frame = Frame.Builder().setBitmap(bitmap).build()
                val barcodes = detector.detect(frame)
                val thisCode = barcodes.valueAt(0)
                var decodedData: JSONObject? = null
                try {
                    decodedData = JSONObject()
                    decodedData.put("text", thisCode.rawValue)
                } catch (e: Exception) {
                    //Sonar fix
                }
                sendSuccess(decodedData)
            } catch (e: Exception) {
                failureCallBack("Failed to scan the image.")
            }
        }
    }

    private fun failureCallBack(errMessage: String?) {
        var error: JSONObject? = null
        try {
            error = JSONObject()
            error.put("errorMessage", errMessage)
        } catch (e: Exception) {
            //Sonar fix
        }
        mApzPluginUtil.sendError(
            mCallbackId, "", error, mActivity,
            webView, true
        )
    }

    private fun sendSuccess(jsonObject: JSONObject?) {
        mApzPluginUtil.sendSuccess(mCallbackId, jsonObject,
            false, mActivity, webView, true)
    }

    init {
        try {
            val mJson = JSONObject(mJson)
            mCallbackId = mJson.getString("id")
            filePath = mJson.getString("filePath")
        } catch (e: JSONException) {
            //handle exception
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}
