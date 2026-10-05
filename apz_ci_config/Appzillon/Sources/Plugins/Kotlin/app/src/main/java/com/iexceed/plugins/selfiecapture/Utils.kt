/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iexceed.plugins.selfiecapture

//import com.iexceed.plugins.autocapturedocument.CameraSizePair
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.iexceed.utils.localstorage.FileAccessHelper
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/** Utility class to provide helper methods.  */
object Utils {
    /**
     * If the absolute difference between aspect ratios is less than this tolerance, they are
     * considered to be the same aspect ratio.
     */
    const val ASPECT_RATIO_TOLERANCE = 0.01f
    private const val TAG = "Utils"
    fun requestRuntimePermissions(activity: Activity) {
        val allNeededPermissions: MutableList<String?> = ArrayList()
        for (permission in getRequiredPermissions(activity)) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                allNeededPermissions.add(permission)
            }
        }
        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                activity, allNeededPermissions.toTypedArray(),  /* requestCode= */0
            )
        }
    }

    fun allPermissionsGranted(context: Context): Boolean {
        for (permission in getRequiredPermissions(context)) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission!!
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private fun getRequiredPermissions(context: Context): Array<String?> {
        return try {
            val info = context
                .packageManager
                .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            val ps = info.requestedPermissions
            if (ps != null && ps.size > 0) ps else arrayOfNulls(0)
        } catch (e: Exception) {
            arrayOfNulls(0)
        }
    }

    fun isPortraitMode(context: Context): Boolean {
        return (context.resources.configuration.orientation
                == Configuration.ORIENTATION_PORTRAIT)
    }

    /**
     * Generates a list of acceptable preview sizes. Preview sizes are not acceptable if there is not
     * a corresponding picture size of the same aspect ratio. If there is a corresponding picture size
     * of the same aspect ratio, the picture size is paired up with the preview size.
     *
     *
     * This is necessary because even if we don't use still pictures, the still picture size must
     * be set to a size that is the same aspect ratio as the preview size we choose. Otherwise, the
     * preview images may be distorted on some devices.
     * @return
     */
//    fun generateValidPreviewSizeList(camera: Camera): List<CameraSizePair> {
//        val parameters = camera.parameters
//        val supportedPreviewSizes = parameters.supportedPreviewSizes
//        val supportedPictureSizes = parameters.supportedPictureSizes
//        val validPreviewSizes: MutableList<CameraSizePair> = ArrayList()
//        for (previewSize in supportedPreviewSizes) {
//            val previewAspectRatio = previewSize.width.toFloat() / previewSize.height.toFloat()
//
//            // By looping through the picture sizes in order, we favor the higher resolutions.
//            // We choose the highest resolution in order to support taking the full resolution
//            // picture later.
//            for (pictureSize in supportedPictureSizes) {
//                val pictureAspectRatio = pictureSize.width.toFloat() / pictureSize.height.toFloat()
//                if (Math.abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
//                    validPreviewSizes.add(CameraSizePair(previewSize, pictureSize))
//                    break
//                }
//            }
//        }
//
//        // If there are no picture sizes with the same aspect ratio as any preview sizes, allow all of
//        // the preview sizes and hope that the camera can handle it.  Probably unlikely, but we still
//        // account for it.
//        if (validPreviewSizes.size == 0) {
//            //  ApzLogger.w(TAG, "No preview sizes have a corresponding same-aspect-ratio picture size.");
//            for (previewSize in supportedPreviewSizes) {
//                // The null picture size will let us know that we shouldn't set a picture size.
//                validPreviewSizes.add(CameraSizePair(previewSize!!, null))
//            }
//        }
//        return validPreviewSizes
//    }

//    fun getCornerRoundedBitmap(srcBitmap: Bitmap, cornerRadius: Int): Bitmap {
//        val dstBitmap =
//            Bitmap.createBitmap(srcBitmap.width, srcBitmap.height, Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(dstBitmap)
//        val paint = Paint()
//        paint.isAntiAlias = true
//        val rectF = RectF(
//            0.0f, 0.0f, srcBitmap.width.toFloat(), srcBitmap.height
//                .toFloat()
//        )
//        canvas.drawRoundRect(rectF, cornerRadius.toFloat(), cornerRadius.toFloat(), paint)
//        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        canvas.drawBitmap(srcBitmap, 0f, 0f, paint)
//        return dstBitmap
//    }
    fun compressSelfieBmpByHeightWeigth(
        bitmap: Bitmap,
        HTMLWIDTH: Int,
        HTMLHEIGHT: Int,
        mActivity: Activity?
    ): Bitmap? {
        var HTMLWIDTH = HTMLWIDTH
        var HTMLHEIGHT = HTMLHEIGHT
        return try {
            var scaledBitmap: Bitmap? = null
            val mTimeStamp = SimpleDateFormat("ddMMyyyy_HHmm").format(Date())
            val mImageName = "snap_$mTimeStamp.jpg"
            val wrapper = ContextWrapper(mActivity)
            var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
            file = File(file, "snap_$mImageName.jpg")
            val actualH = bitmap.height
            val actualW = bitmap.width
            if (HTMLWIDTH <= HTMLHEIGHT) {
                HTMLHEIGHT = HTMLWIDTH * actualH / actualW
            } else {
                HTMLWIDTH = HTMLHEIGHT * actualW / actualH
            }
            try {
                var stream: OutputStream? = null
                stream = FileAccessHelper.getFileOutPutStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            scaledBitmap = imageScaling(
                bitmap,
                file.absolutePath,
                HTMLWIDTH,
                HTMLHEIGHT
            )
            val exif: android.media.ExifInterface
            exif = android.media.ExifInterface(file.absolutePath)
        //    scaledBitmap = maybeTransformBitmap(exif, scaledBitmap)
            scaledBitmap
        } catch (e: Exception) {
            bitmap
        }
    }
//    fun openImagePicker(activity: Activity) {
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.addCategory(Intent.CATEGORY_OPENABLE)
//        intent.type = "image/*"
//        activity.startActivityForResult(intent, PluginConstants.REQUEST_CODE_PHOTO_LIBRARY)
//    }

    @Throws(IOException::class)
//    fun loadImage(context: Context, imageUri: Uri, maxImageDimension: Int): Bitmap? {
//        var inputStreamForSize: InputStream? = null
//        var inputStreamForImage: InputStream? = null
//        return try {
//            inputStreamForSize = context.contentResolver.openInputStream(imageUri)
//            var opts = BitmapFactory.Options()
//            opts.inJustDecodeBounds = true
//            BitmapFactory.decodeStream(inputStreamForSize,  /* outPadding= */null, opts)
//            val inSampleSize = Math.max(
//                opts.outWidth / maxImageDimension,
//                opts.outHeight / maxImageDimension
//            )
//            opts = BitmapFactory.Options()
//            opts.inSampleSize = inSampleSize
//            inputStreamForImage = context.contentResolver.openInputStream(imageUri)
//            val decodedBitmap =
//                BitmapFactory.decodeStream(inputStreamForImage,  /* outPadding= */null, opts)
//            maybeTransformBitmap(
//                context.contentResolver,
//                imageUri,
//                decodedBitmap
//            )
//        } finally {
//            inputStreamForSize?.close()
//            inputStreamForImage?.close()
//        }
//    }

//    private fun maybeTransformBitmap(
//        resolver: ContentResolver,
//        uri: Uri,
//        bitmap: Bitmap?
//    ): Bitmap? {
//        val orientation = getExifOrientationTag(resolver, uri)
//        var matrix: Matrix? = Matrix()
//        when (orientation) {
//            ExifInterface.ORIENTATION_UNDEFINED, ExifInterface.ORIENTATION_NORMAL ->         // Set the matrix to be null to skip the image transform.
//                matrix = null
//            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> {
//                matrix = Matrix()
//                matrix.postScale(-1.0f, 1.0f)
//            }
//            ExifInterface.ORIENTATION_ROTATE_90 -> matrix!!.postRotate(90f)
//            ExifInterface.ORIENTATION_TRANSPOSE -> {
//                matrix!!.postRotate(90.0f)
//                matrix.postScale(-1.0f, 1.0f)
//            }
//            ExifInterface.ORIENTATION_ROTATE_180 -> matrix!!.postRotate(180.0f)
//            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix!!.postScale(1.0f, -1.0f)
//            ExifInterface.ORIENTATION_ROTATE_270 -> matrix!!.postRotate(-90.0f)
//            ExifInterface.ORIENTATION_TRANSVERSE -> {
//                matrix!!.postRotate(-90.0f)
//                matrix.postScale(-1.0f, 1.0f)
//            }
//            else ->         // Set the matrix to be null to skip the image transform.
//                matrix = null
//        }
//        return if (matrix != null) {
//            Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width, bitmap.height, matrix, true)
//        } else {
//            bitmap
//        }
//    }

//    private fun getExifOrientationTag(resolver: ContentResolver, imageUri: Uri): Int {
//        if (ContentResolver.SCHEME_CONTENT != imageUri.scheme
//            && ContentResolver.SCHEME_FILE != imageUri.scheme
//        ) {
//            return 0
//        }
//        var exif: ExifInterface? = null
//        try {
//            resolver.openInputStream(imageUri).use { inputStream ->
//                if (inputStream != null) {
//                    exif = ExifInterface(inputStream)
//                }
//            }
//        } catch (e: IOException) {
//            // ApzLogger.e(TAG, "Failed to open file to read rotation meta data: " + imageUri, e);
//        }
//        return if (exif != null) exif!!.getAttributeInt(
//            ExifInterface.TAG_ORIENTATION,
//            ExifInterface.ORIENTATION_NORMAL
//        ) else ExifInterface.ORIENTATION_UNDEFINED
//    }

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
    fun imageScaling(bitmap: Bitmap?, filePath: String?, HTMLWIDTH: Int, HTMLHEIGHT: Int): Bitmap? {
        return try {
            //            Uri mImageUri = Uri.parse(file.getAbsolutePath());
            var scaledBitmap: Bitmap? = null
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            var bmp = BitmapFactory.decodeFile(filePath, options)

            //      setting inSampleSize value allows to load a scaled down version of the original image
            options.inSampleSize =
                calculateInSampleSize(options, HTMLWIDTH, HTMLHEIGHT)

            //      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false

            //      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true
            options.inInputShareable = true
            options.inTempStorage = ByteArray(16 * 1024)
            try {
                //          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            try {
                scaledBitmap = Bitmap.createBitmap(HTMLWIDTH, HTMLHEIGHT, Bitmap.Config.ARGB_8888)
            } catch (exception: OutOfMemoryError) {
                exception.printStackTrace()
            }
            val ratioX = HTMLWIDTH / options.outWidth.toFloat()
            val ratioY = HTMLHEIGHT / options.outHeight.toFloat()
            val middleX = HTMLWIDTH / 2.0f
            val middleY = HTMLHEIGHT / 2.0f
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
}
