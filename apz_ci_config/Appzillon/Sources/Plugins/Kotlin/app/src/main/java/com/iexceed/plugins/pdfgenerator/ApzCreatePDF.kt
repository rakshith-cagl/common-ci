package com.iexceed.plugins.pdfgenerator

import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Base64
import android.webkit.WebView
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
import java.io.FileInputStream
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class ApzCreatePDF private constructor(val webView: WebView,
                   val activity: ApzActivity<*>,
                   override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    var pdfDoc: PdfDocument? = null

    private var pageHeight = 0

    private var pageWidth = 0

    private var scale = 0f

    override var TAG = "ApzCreatePDF"

    private val errorCode006 = "APZ-DM-006"

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin {
            if (pluginObj == null) {
                pluginObj = ApzCreatePDF(webView, activity, apzPluginUtil)
            }
            return pluginObj as ApzPlugin
        }

        fun isPlugin(): Boolean {
            return true
        }
    }

    override fun execute(params: JSONObject) {
        val action: String
        try {
            callbackId = params.getString("id")
            action = params.getString("action")

            if (action.equals("append", true)) {
                if (pdfDoc == null) {
                    pdfDoc = initialise()
                }

                val type = params.getString("contentType")
                if (type.equals("Text", true)) {
                    handleTypeText(params)
                } else if (type.equals("Image", true)) {
                    handleTypeImage(params)
                } else {
                    apzPluginUtil.sendError(
                        callbackId,
                        "APZ-FM-EX-025",
                        null,
                        activity,
                        webView,
                        true
                    )
                }
            } else if (action.equals("generate", true)) {
                handleTypeGenerate(params)
            }
        } catch (e: Exception) {
            ApzLogger.e(TAG, "Error : " + e.message)
            apzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, activity, webView, true)
        }

    }

    private fun handleTypeGenerate(params: JSONObject) {
        if (pdfDoc == null) {
            ApzLogger.e(TAG, "Generating PDF without Content.");
            apzPluginUtil.sendError(callbackId, "APZ-CNT-082", null, activity, webView, true);
        } else {
            val base64 = params.optString("base64")
            var filePath = params.optString("filePath")
            filePath = if (filePath.isEmpty()) {
                val date = Date()
                val dateFormatter: DateFormat = SimpleDateFormat("yyyyMMdd_hhmmss")
                val fileName = dateFormatter.format(date)
                AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + File.separator + fileName + ".pdf"
            } else {
                AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + File.separator + filePath
            }
            if (base64.equals("Y", true)) {
                savePdf(filePath, true)
            } else {
                savePdf(filePath, false)
            }
        }
    }

    private fun handleTypeImage(params: JSONObject) {
        val path = params.optString("imagePath")
        var imgWidht = params.optInt("imageWidth")
        if (imgWidht == 0) imgWidht = 200  //Default Width
        var imgHeight = params.optInt("imageHeight")
        if (imgHeight == 0) imgHeight = 200    //Default Height
        if (path.isEmpty()) {
            val base64 = params.optString("base64")
            val decodedString = Base64.decode(base64, Base64.DEFAULT)
            val bitmap: Bitmap =
                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            appendImage(path, bitmap, imgWidht, imgHeight)
        } else {
            appendImage(path, null, imgWidht, imgHeight)
        }
    }

    private fun handleTypeText(params: JSONObject) {
        val text = params.getString("text")
        var textColor = params.optString("fontColor")
        if (textColor.isEmpty()) textColor = "#000000" // Default color as Black
        var textSize = params.optInt("fontSize")
        if (textSize == 0) textSize = 13 // Default text Size
        var font = params.optString("fontType")
        if (font.isEmpty()) font = "Ariel"
        var padding = params.optInt("padding")
        if (padding == 0) padding = 10 //Default Padding
        appendText(text, textColor, textSize, font, padding)
    }

    private fun initialise(): PdfDocument {
        pageWidth = activity.resources.displayMetrics.widthPixels
        pageHeight = activity.resources.displayMetrics.heightPixels
        scale = activity.resources.displayMetrics.density
        return PdfDocument()
    }

    private fun appendText(
        value: String,
        txtColor: String,
        txtSize: Int,
        font: String,
        padding: Int
    ) {
        val pageInfo = PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDoc!!.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        try {
            val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)
            paint.color = Color.parseColor(txtColor)
            paint.textSize = txtSize.toFloat()
            val typeface = Typeface.create(font, Typeface.NORMAL)
            paint.typeface = typeface

            // set text width to canvas width minus 16dp padding
            val textWidth: Int = canvas.width - (16 * scale).toInt()

            val sb = StaticLayout.Builder.obtain(value, 0, value.length, paint, textWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(1.0f, 1.0f)
                .setIncludePad(false)

            val textLayout = sb.build()

            canvas.save()
            // Set x and y axis padding
            canvas.translate(padding.toFloat(), padding.toFloat())
            textLayout.draw(canvas)
            canvas.restore()
            pdfDoc!!.finishPage(page)
            val resultObj = JSONObject()
            try {
                resultObj.put("event", "contentAdded")
            } catch (e: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendSuccess(callbackId, resultObj, false, activity, webView, true)
        } catch (iae: IllegalArgumentException) {
            pdfDoc!!.finishPage(page)
            apzPluginUtil.sendError(callbackId, errorCode006, null, activity, webView, true)
        } catch (sobe: StringIndexOutOfBoundsException) {
            pdfDoc!!.finishPage(page)
            apzPluginUtil.sendError(callbackId, errorCode006, null, activity, webView, true)
        } catch (ise: IllegalStateException) {
            pdfDoc!!.finishPage(page)
            apzPluginUtil.sendError(callbackId, errorCode006, null, activity, webView, true)
        }
    }

    private fun appendImage(path: String, bmp: Bitmap?, imgWidth: Int, imgHeight: Int) {
        val pageInfo = PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDoc!!.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()
        try{
            paint.color = Color.WHITE
            canvas.drawPaint(paint)
            val bitmap: Bitmap? = if (bmp == null) {
                getBitmapFromPath(path, imgWidth, imgHeight)
            } else {
                Bitmap.createScaledBitmap(bmp, imgWidth, imgHeight, true)
            }
            if (bitmap != null) {
                val left: Int = (pageWidth - bitmap.width) / 2
                val top: Int = (pageHeight - bitmap.height) / 2
                canvas.drawBitmap(bitmap, left.toFloat(), top.toFloat(), paint)
                val resultObj = JSONObject()
                try {
                    resultObj.put("event", "contentAdded")
                } catch (e: JSONException) {
                    //Sonar fix
                }
                apzPluginUtil.sendSuccess(callbackId, resultObj, false, activity, webView, true)
            } else {
                ApzLogger.e(TAG, "Bitmap Is Null")
                apzPluginUtil.sendError(callbackId, "APZ-FM-EX-030", null, activity, webView, true)
            }

        }catch(e: Exception){
            ApzLogger.e(TAG, "Exception : "+e.message)
            apzPluginUtil.sendError(callbackId, "APZ-DM-006", null, activity, webView, true)
        }
        pdfDoc!!.finishPage(page)

    }

    private fun getBitmapFromPath(path: String, width: Int, height: Int): Bitmap? {
        var bmp: Bitmap? = null
        try {
            val file = File(path)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                bmp = Bitmap.createScaledBitmap(bitmap, width, height, true)
            }
        } catch (e: java.lang.Exception) {
            ApzLogger.e(TAG, e.message!!)
        }
        return bmp
    }

    private fun savePdf(filePath: String, isBase64: Boolean) {
        try {
            val fileName = filePath.substringAfterLast("/")
            val directory: File = AppzillonUtils.getApzFile(filePath.substringBeforeLast("/"), null)
            directory.mkdirs()
            val file = File(directory, fileName)
            val fileOutputStream = FileAccessHelper.getFileOutPutStream(file)
            pdfDoc!!.writeTo(fileOutputStream)
            val resultObj = JSONObject()
            // If return type is base 64
            if (isBase64) {
                try {
                    val baos = ByteArrayOutputStream()
                    val fis = FileInputStream(File(filePath))
                    val buf = ByteArray(1024)
                    var n: Int
                    while (-1 != fis.read(buf).also { n = it }) baos.write(buf, 0, n)
                    fis.close()
                    val bytes: ByteArray = baos.toByteArray()
                    val str = Base64.encodeToString(bytes, Base64.DEFAULT)
                    resultObj.put("text", str)
                } catch (e: Exception) {
                    //Sonar fix
                }
            } else {
                try {
                    resultObj.put("text", filePath)
                } catch (e: JSONException) {
                    //Sonar fix
                }
            }
            //Send Success
            apzPluginUtil.sendSuccess(callbackId, resultObj, false, activity, webView, true)
        } catch (e: IOException) {
            e.printStackTrace()
            ApzLogger.e(TAG, e.message.toString())
            apzPluginUtil.sendError(callbackId, "APZ-FM-EX-030", null, activity, webView, true)
        }
        finally {
            // close the document
            pdfDoc!!.close()
            pdfDoc = null
        }

    }

}
