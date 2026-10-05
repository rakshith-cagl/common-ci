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

import android.util.Log
import android.view.SurfaceHolder
import java.io.IOException

/** Preview the camera image in the screen.  */
class CameraSourcePreview constructor(
    context: android.content.Context,
    attrs: android.util.AttributeSet?
) : android.widget.FrameLayout(context, attrs) {
    private val surfaceView: android.view.SurfaceView?
    private var graphicOverlay: GraphicOverlay? = null
    private var startRequested: kotlin.Boolean = false
    private var surfaceAvailable: kotlin.Boolean = false
    private var cameraSource: com.iexceed.plugins.autocapturedocument.CameraSource? = null
    private var cameraPreviewSize: com.google.android.gms.common.images.Size? = null
    override fun onFinishInflate() {
        super.onFinishInflate()
        graphicOverlay = findViewById(com.iexceed.appzillonapp.R.id.camera_preview_graphic_overlay)
        CameraSourcePreview.Companion.frameGraphicOverlay =
            findViewById(com.iexceed.appzillonapp.R.id.frame_preview_graphic_overlay)
    }

    @Throws(java.io.IOException::class)
    fun start(cameraSource: com.iexceed.plugins.autocapturedocument.CameraSource?) {
        this.cameraSource = cameraSource
        startRequested = true
        startIfReady()
    }

    fun stop() {
        if (cameraSource != null) {
            cameraSource!!.stop()
            cameraSource = null
            startRequested = false
        }
    }

    @Throws(java.io.IOException::class)
    private fun startIfReady() {
        if (startRequested && surfaceAvailable) {
            cameraSource!!.start(surfaceView!!.getHolder())
            requestLayout()
            if (graphicOverlay != null) {
                graphicOverlay!!.setCameraInfo(cameraSource!!)
                graphicOverlay!!.clear()
            }
            startRequested = false
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val layoutWidth = right - left
        val layoutHeight = bottom - top
        if (cameraSource != null && cameraSource!!.previewSize != null) {
            cameraPreviewSize = cameraSource!!.previewSize
        }
        var previewSizeRatio = layoutWidth.toFloat() / layoutHeight
        if (cameraPreviewSize != null) {
            val isPortraitMode:Boolean =true
            previewSizeRatio = if (isPortraitMode) {
                // Camera's natural orientation is landscape, so need to swap width and height.
                cameraPreviewSize!!.height.toFloat() / cameraPreviewSize!!.width
            } else {
                cameraPreviewSize!!.width.toFloat() / cameraPreviewSize!!.height
            }
        }

        // Match the width of the child view to its parent.
        val childHeight = (layoutWidth / previewSizeRatio).toInt()
        /*if (childHeight <= layoutHeight) {
      for (int i = 0; i < getChildCount(); ++i) {
        getChildAt(i).layout(0, 0, childWidth, childHeight);
      }
    } else {*/
        // When the child view is too tall to be fitted in its parent: If the child view is static
        // overlay view container (contains views such as bottom prompt chip), we apply the size of
        // the parent view to it. Otherwise, we offset the top/bottom position equally to position it
        // in the center of the parent.
        (childHeight - layoutHeight) / 2
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            childView.layout(0, 0, layoutWidth, layoutHeight)
        }
        try {
            startIfReady()
        } catch (e: IOException) {
            Log.e("tag", "Could not start camera source.", e)
        }
    }

    private inner class SurfaceCallback constructor() : android.view.SurfaceHolder.Callback {


        public override fun surfaceDestroyed(holder: SurfaceHolder) {
            surfaceAvailable = false
        }



        override fun surfaceCreated(holder: SurfaceHolder) {
            surfaceAvailable = true
            try {
                startIfReady()
            } catch (e: Exception) {
                //handle exception
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            //handle surfaceChanged
        }
    }

    companion object {
        private val TAG: kotlin.String? = "CameraSourcePreview"
        var frameGraphicOverlay: FrameGraphicOverlay? = null
    }

    init {
        surfaceView = android.view.SurfaceView(context)
        surfaceView.getHolder().addCallback(SurfaceCallback())
        addView(surfaceView)
    }
}