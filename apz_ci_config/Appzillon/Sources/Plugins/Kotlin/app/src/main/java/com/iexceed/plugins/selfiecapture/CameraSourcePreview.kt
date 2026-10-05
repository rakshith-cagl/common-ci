// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.iexceed.plugins.selfiecapture

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import com.google.android.gms.common.images.Size
import com.iexceed.appzillonapp.R
import java.io.IOException

/** Preview the camera image in the screen.  */
class CameraSourcePreview(context: Context, attrs: AttributeSet?) : FrameLayout(
    context, attrs
) {
    private val surfaceView: SurfaceView
    private var startRequested = false
    private var surfaceAvailable = false
    private var graphicOverlay: GraphicOverlay? = null
    private var cameraSource: CameraSource? = null
    private var cameraPreviewSize: Size? = null
    private var overlay: GraphicOverlay? = null
    @Throws(IOException::class)
    private fun start(cameraSource: CameraSource?) {
        if (cameraSource == null) {
            stop()
        }
        this.cameraSource = cameraSource
        if (this.cameraSource != null) {
            startRequested = true
            startIfReady()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        graphicOverlay = findViewById(R.id.fireFaceOverlay)
        ovalGraphicOverlay = findViewById(R.id.ovalOverlay)
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource?, overlay: GraphicOverlay?) {
        this.overlay = overlay
        start(cameraSource)
    }

    fun stop() {
        if (cameraSource != null) {
            cameraSource!!.stop()
        }
    }

    fun release() {
        if (cameraSource != null) {
            cameraSource!!.release()
            cameraSource = null
        }
        surfaceView.holder.surface.release()
    }

    @SuppressLint("MissingPermission")
    @Throws(IOException::class)
    private fun startIfReady() {
        if (startRequested && surfaceAvailable) {
            /*  if (PreferenceUtils.isCameraLiveViewportEnabled(context)) {
        cameraSource.start(surfaceView.getHolder());
      } else {
        cameraSource.start();
      }*/
            //  cameraSource.start();
            cameraSource!!.start(surfaceView.holder)
            requestLayout()
            if (overlay != null) {
                val size = cameraSource!!.previewSize
                val min = Math.min(size!!.width, size.height)
                val max = Math.max(size!!.width, size.height)
                if (isPortraitMode) {
                    // Swap width and height sizes when in portrait, since it will be rotated by
                    // 90 degrees
                    overlay!!.setCameraInfo(min, max, cameraSource!!.cameraFacing)
                } else {
                    overlay!!.setCameraInfo(max, min, cameraSource!!.cameraFacing)
                }
                overlay!!.clear()
            }
            startRequested = false
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            surfaceAvailable = true
            try {
                startIfReady()
            } catch (e: IOException) {
                Log.e(TAG, "Could not start camera source.", e)
            }
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            surfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val layoutWidth = right - left
        val layoutHeight = bottom - top
        if (cameraSource != null && cameraSource!!.previewSize != null) {
            cameraPreviewSize = cameraSource!!.previewSize
        }
        var previewSizeRatio = layoutWidth.toFloat() / layoutHeight
        if (cameraPreviewSize != null) {
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
        val excessLenInHalf = (childHeight - layoutHeight) / 2
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            childView.layout(0, 0, layoutWidth, layoutHeight)
            /*if (childView.getId() == R.id.static_overlay_container) {
          childView.layout(0, 0, layoutWidth, layoutHeight);
        }*/
            /* else {
          childView.layout(0, -excessLenInHalf, layoutWidth, layoutHeight + excessLenInHalf);
        }*/
            //  }
        }
        try {
            startIfReady()
        } catch (e: IOException) {
            Log.e(TAG, "Could not start camera source.", e)
        }
    }

    private val isPortraitMode: Boolean
        private get() {
            val orientation = context.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return false
            }
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                return true
            }
            Log.d(TAG, "isPortraitMode returning false by default")
            return false
        }

    companion object {
        private const val TAG = "MIDemoApp:Preview"
        var ovalGraphicOverlay: OvalGraphicOverlay? = null
    }

    init {
        surfaceView = SurfaceView(context)
        surfaceView.holder.addCallback(SurfaceCallback())
        addView(surfaceView)
    }
}
