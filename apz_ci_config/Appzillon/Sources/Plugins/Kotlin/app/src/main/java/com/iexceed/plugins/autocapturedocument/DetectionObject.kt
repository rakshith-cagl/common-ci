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
package com.iexceed.plugins.autocapturedocument

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Holds the detected object and its related image info.
 */
class DetectionObject constructor(
    private val `object`: DetectedObject,
    val objectIndex: Int,
    private val image: InputImage
) {
    private val objectThumbnailCornerRadius: Int = 0
    private var bitmap: Bitmap? = null
    private var jpegBytes: ByteArray? = null
    private var objectThumbnail: Bitmap? = null
    val objectId: Int?
        get() {
            return `object`.getTrackingId()
        }
    val boundingBox: Rect
        get() {
            return `object`.getBoundingBox()
        }

    @Synchronized
    fun getBitmap(): Bitmap? {
        if (bitmap == null) {
            val boundingBox: Rect = `object`.getBoundingBox()
            bitmap = Bitmap.createBitmap(
                (image.getBitmapInternal())!!,
                boundingBox.left,
                boundingBox.top,
                boundingBox.width(),
                boundingBox.height()
            )
            if (bitmap!!.getWidth() > MAX_IMAGE_WIDTH) {
                val dstHeight: Int =
                    (MAX_IMAGE_WIDTH.toFloat() / bitmap!!.getWidth() * bitmap!!.getHeight()).toInt()
                bitmap = Bitmap.createScaledBitmap(
                    bitmap!!,
                    MAX_IMAGE_WIDTH,
                    dstHeight,  /* filter= */
                    false
                )
            }
        }
        return bitmap
    }

    /* quality= */
    @get:Synchronized
    val imageData: ByteArray?
        get() {
            if (jpegBytes == null) {
                try {
                    ByteArrayOutputStream().use({ stream ->
                        getBitmap()!!.compress(
                            Bitmap.CompressFormat.JPEG,  /* quality= */
                            100,
                            stream
                        )
                        jpegBytes = stream.toByteArray()
                    })
                } catch (e: IOException) {
                    Log.e(TAG, "Error getting object image data!")
                }
            }
            return jpegBytes
        }

    @Synchronized
    fun getObjectThumbnail(): Bitmap? {
        if (objectThumbnail == null) {
            objectThumbnail = Utils.getCornerRoundedBitmap(getBitmap()!!, objectThumbnailCornerRadius)
        }
        return objectThumbnail
    }

    @get:Synchronized
    val objectBmpThumbnail: Bitmap?
        get() {
            if (objectThumbnail == null) {
                objectThumbnail =
                    Utils.getCornerRoundedBitmap(getBitmap()!!, objectThumbnailCornerRadius)
            }
            return objectThumbnail
        }

    companion object {
        private val TAG: String = "DetectedObject"
        private val MAX_IMAGE_WIDTH: Int = 640
    }
}