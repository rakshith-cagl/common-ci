package com.iexceed.plugins.camera

//import com.iexceed.common.JavaScriptInterface
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.graphics.Bitmap.CompressFormat
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.AppzillonUtils.validatePath
import com.iexceed.utils.localstorage.FileAccessHelper
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
object CameraUtils {
    fun compressImage(
        context: Context,
        realData: ByteArray, fileFormat: String,
        uncompressed: Boolean,
        originalFilePath: String?,
        triple: Triple<Int, Int, Boolean>
    ): Bitmap? {
        val htmlWidth: Int = triple.first
        val htmlHeight: Int = triple.second
        val isCrop: Boolean = triple.third
        var processingBitmap: Bitmap?
        val originalimagepath = getFilename("preCompression", fileFormat) // creating

        try {
            // Write to SD Card
//            FileOutputStream outStream = new FileOutputStream(""+originalimagepath);
            val outStream = FileAccessHelper.getFileOutPutStream(File(validatePath(originalimagepath, null)))
            outStream.write(realData)
            outStream.close()
        } catch (e: Exception) {
            //handle exception
        }
        val filePath = getRealPathFromURI(context, originalimagepath)
        val options = BitmapFactory.Options()

        // by setting this field as true, the actual bitmap pixels are not
        // loaded in the memory. Just the bounds are loaded. If
        // you try the use the bitmap here, you will get null.
        if (!uncompressed) {
            options.inJustDecodeBounds = true
            val bmp = BitmapFactory.decodeFile(filePath, options)
            var actualHeight = options.outHeight
            var actualWidth = options.outWidth

            // max Height and width values of the compressed image is taken as
            // 816x612
            if (htmlWidth == 0 && htmlHeight == 0) {
                val pair = withoutHW(actualWidth, actualHeight)
                actualHeight = pair.first
                actualWidth = pair.second
            } else {
                val pair = whenHWGiven(actualWidth, actualHeight, htmlWidth, htmlHeight)
                actualHeight = pair.first
                actualWidth = pair.second
            }
            processingBitmap = imageScaling(bmp, filePath, actualWidth, actualHeight)
        } else {
            processingBitmap = BitmapFactory.decodeByteArray(realData, 0, realData.size)
        }

        // check the rotation of the image and display it properly
        val exif: ExifInterface
        try {
            // exif = new ExifInterface(filePath);
            // Bug #9476 Start
            exif = if (isCrop) {
                ExifInterface(originalimagepath)
            } else {
                ExifInterface(originalFilePath!!)
            }
            processingBitmap = exifOrientation(exif, processingBitmap)
        } catch (e: IOException) {
            //Sonar fix
        }
        deleteDir(
            File(
                AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC,
                "appzillonTemp"
            )
        ) // deleting appzillonTemp folder
        return processingBitmap
    }

    private fun whenHWGiven(
        actualWidth: Int,
        actualHeight: Int,
        htmlWidth: Int,
        htmlHeight: Int
    ): Pair<Int, Int> {
        // Abhishek : In place of taking hardcoded values now size is
        // dependent upon compression level
        var actualWidth1 = actualWidth
        var actualHeight1 = actualHeight
        val sizeRatio = actualWidth1.toDouble() / actualHeight1.toDouble()
        if (htmlWidth > 0 && htmlHeight == 0) {
            actualWidth1 = htmlWidth
            actualHeight1 = (actualWidth1 / sizeRatio).toInt()
        } else if (htmlWidth == 0 && htmlHeight > 0) {
            actualHeight1 = htmlHeight
            actualWidth1 = (actualHeight1 * sizeRatio).toInt()
        } else {
            actualHeight1 = htmlHeight
            actualWidth1 = htmlWidth
        }
        return Pair(actualHeight1, actualWidth1)
    }

    private fun withoutHW(
        actualWidth: Int,
        actualHeight: Int
    ): Pair<Int, Int> {
        // max Height and width values of the compressed image is taken as
        // 816x612
        // Abhishek Fix for 9826 START
        var actualWidth1 = actualWidth
        var actualHeight1 = actualHeight
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = (actualWidth1 / actualHeight1).toFloat()
        val maxRatio = maxWidth / maxHeight
        if (actualHeight1 > maxHeight || actualWidth1 > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight1
                actualWidth1 = (imgRatio * actualWidth1).toInt()
                actualHeight1 = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth1
                actualHeight1 = (imgRatio * actualHeight1).toInt()
                actualWidth1 = maxWidth.toInt()
            } else {
                actualHeight1 = maxHeight.toInt()
                actualWidth1 = maxWidth.toInt()
            }
        }
        return Pair(actualHeight1, actualWidth1)
    }

    fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    // return false;
                }
            }
        }

        // The directory is now empty so delete it
        val deleted = dir.delete()
        val create = dir.mkdir()
        if (create){
            //handle create
        }
        return deleted
    }

    fun getFilename(type: String, mFileFormat: String): String {
        var file: File? = null
        file = if (type.equals("preCompression", ignoreCase = true)) {
            File(
                AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC,
                "appzillonTemp/originalImages"
            )
        } else {
            File(
                AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC,
                "appzillonTemp/compressedImages"
            )
        }
        if (!file.exists()) {
            file.mkdirs()
        }
        return (file.absolutePath + "/"
                + System.currentTimeMillis() + mFileFormat)
    }

    fun getRealPathFromURI(context: Context, contentURI: String?): String? {
        val contentUri = Uri.parse(contentURI)
        val cursor = context.contentResolver
            .query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor
                .getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(index)
        }
    }

    fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int, reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(
                height.toFloat()
                        / reqHeight.toFloat()
            )
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }

    fun getBytesFromBitmap(
        croppedImageToconvert: Bitmap,
        cmpLevel: Int, mCompressFormat: CompressFormat?
    ): ByteArray? {
        var tempbyte: ByteArray? = null
        try {
            var baos: ByteArrayOutputStream? = ByteArrayOutputStream()
            croppedImageToconvert.compress(mCompressFormat, cmpLevel, baos) // mBitmap
            // is
            // the
            // bitmap
            // object
            val b = baos!!.toByteArray()
            baos.close()
            baos = null
            tempbyte = b
        } catch (e: IOException) {
            //Sonar fix
        }
        return tempbyte
    }

    fun compressSelfieBmpByHeightWeigth(
        bitmap: Bitmap,
        lhtmlWidth: Int,
        lhtmlHeight: Int,
        mActivity: Activity?
    ): Bitmap? {
        var htmlWidth = lhtmlWidth
        var htmlHeight = lhtmlHeight
        return try {
            var scaledBitmap: Bitmap? = null
            val mTimeStamp = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
            val mImageName = "snap_$mTimeStamp.jpg"
            val wrapper = ContextWrapper(mActivity)
            var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
            file = File(file, "snap_$mImageName.jpg")
            val actualH = bitmap.height
            val actualW = bitmap.width
            if (htmlWidth <= htmlHeight) {
                htmlHeight = htmlWidth * actualH / actualW
            } else {
                htmlWidth = htmlHeight * actualW / actualH
            }
            try {
                var stream: OutputStream? = null
                stream = FileAccessHelper.getFileOutPutStream(file)
                bitmap.compress(CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            scaledBitmap = imageScaling(bitmap, file.absolutePath, htmlWidth, htmlHeight)
            val exif: ExifInterface
            exif = ExifInterface(file.absolutePath)
            scaledBitmap = exifOrientation(exif, scaledBitmap)
            scaledBitmap
        } catch (e: Exception) {
            bitmap
        }
    }

    fun imageScaling(bitmap: Bitmap?, filePath: String?, htmlWidth: Int, htmlHeight: Int): Bitmap? {
        return try {
            //            Uri mImageUri = Uri.parse(file.getAbsolutePath());
            var scaledBitmap: Bitmap? = null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(filePath, options)

//      setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize = calculateInSampleSize(options, htmlWidth, htmlHeight)

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inTempStorage = ByteArray(16 * 1024)
            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            try {
                scaledBitmap = Bitmap.createBitmap(htmlWidth, htmlHeight, Bitmap.Config.ARGB_8888)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            val ratioX = htmlWidth / options.outWidth.toFloat()
            val ratioY = htmlHeight / options.outHeight.toFloat()
            val middleX = htmlWidth / 2.0f
            val middleY = htmlHeight / 2.0f
            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            val canvas = Canvas(scaledBitmap!!)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(
                bmp,
                middleX - bmp.width / 2,
                middleY - bmp.height / 2,
                Paint(Paint.FILTER_BITMAP_FLAG)
            )
            scaledBitmap
        } catch (e: Exception) {
            bitmap
        }
    }

    fun exifOrientation(exif: ExifInterface, scaledBitmap: Bitmap?): Bitmap? {
        try{
            var scaledBitmap = scaledBitmap
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            if (orientation == 6) {
                matrix.postRotate(90f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 3) {
                matrix.postRotate(180f)
                Log.d("EXIF", "Exif: $orientation")
            } else if (orientation == 8) {
                matrix.postRotate(270f)
                Log.d("EXIF", "Exif: $orientation")
            }
            scaledBitmap = Bitmap.createBitmap(
                scaledBitmap!!,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
            )
        } catch(e: Exception) {
            e.printStackTrace()
        }
        return scaledBitmap
    }
}
