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

import android.os.SystemClock
import android.util.Log
import androidx.annotation.GuardedBy
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import java.nio.ByteBuffer

/** Abstract base class of [FrameProcessor].  */
abstract class FrameProcessorBase<T> constructor() : FrameProcessor {
    // To keep the latest frame and its metadata.
    @GuardedBy("this")
    private var latestFrame: ByteBuffer? = null

    @GuardedBy("this")
    private var latestFrameMetaData: FrameMetadata? = null

    // To keep the frame and metadata in process.
    @GuardedBy("this")
    private var processingFrame: ByteBuffer? = null

    @GuardedBy("this")
    private var processingFrameMetaData: FrameMetadata? = null
    @Synchronized
    public override fun process(
        data: ByteBuffer?,
        frameMetadata: FrameMetadata?,
        graphicOverlay: GraphicOverlay?
    ) {
        latestFrame = data
        latestFrameMetaData = frameMetadata
        if (processingFrame == null && processingFrameMetaData == null) {
            processLatestFrame(graphicOverlay!!)
        }
    }

    @Synchronized
    private fun processLatestFrame(graphicOverlay: GraphicOverlay) {
        processingFrame = latestFrame
        processingFrameMetaData = latestFrameMetaData
        latestFrame = null
        latestFrameMetaData = null
        if (processingFrame != null && processingFrameMetaData != null) {
            /*    FirebaseVisionImageMetadata metadata =
          new FirebaseVisionImageMetadata.Builder()
              .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
              .setWidth(processingFrameMetaData.width)
              .setHeight(processingFrameMetaData.height)
              .setRotation(processingFrameMetaData.rotation)
              .build();
      preWidth=processingFrameMetaData.width;
      preHeight=processingFrameMetaData.height;*/
            val image: InputImage = InputImage.fromByteBuffer(
                processingFrame!!,
                processingFrameMetaData!!.width,
                processingFrameMetaData!!.height,
                processingFrameMetaData!!.rotation,
                InputImage.IMAGE_FORMAT_NV21
            )
            /*    final FirebaseVisionImage image = FirebaseVisionImage.fromByteBuffer(processingFrame, metadata);
*/
            val startMs: Long = SystemClock.elapsedRealtime()
            detectInImage(image)
                .addOnSuccessListener(
                    object : OnSuccessListener<T> {
                        public override fun onSuccess(results: T) {
                            Log.d(TAG, "Latency is: " + (SystemClock.elapsedRealtime() - startMs))
                            this@FrameProcessorBase.onSuccess(image, results, graphicOverlay)
                            processLatestFrame(graphicOverlay)
                        }
                    })
                .addOnFailureListener(object : OnFailureListener {
                    public override fun onFailure(e: Exception) {
                        Log.e("exc", e.getLocalizedMessage())
                    }
                })
        }
    }

    protected abstract fun detectInImage(image: InputImage?): Task<T>

    /** Be called when the detection succeeds.  */
    protected abstract fun onSuccess(
        image: InputImage?, results: T, graphicOverlay: GraphicOverlay?
    )

    protected abstract fun onFailure(e: Exception?)

    companion object {
        private val TAG: String = "FrameProcessorBase"
        var preWidth: Int = 0
        var preHeight: Int = 0
    }
}