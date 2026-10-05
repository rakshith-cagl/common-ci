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

import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.iexceed.appzillonapp.R

/**
 * A camera reticle that locates at the center of canvas to indicate the system is active but has
 * not recognized an object yet.
 */
internal class ObjectReticleGraphic(
    overlay: GraphicOverlay,
    private val animator: CameraReticleAnimator
) : GraphicOverlay.Graphic(overlay) {
    private val outerRingFillPaint: Paint
    private val outerRingStrokePaint: Paint
    private val innerRingStrokePaint: Paint
    private val ripplePaint: Paint
    private val outerRingFillRadius: Int
    private val outerRingStrokeRadius: Int
    private val innerRingStrokeRadius: Int
    private val rippleSizeOffset: Int
    private val rippleStrokeWidth: Int
    private val rippleAlpha: Int
    override fun draw(canvas: Canvas?) {
        val cx: Float = LiveObjectDetectionActivity.cardFrame!!.getWidth() / 2f
        val cy: Float = LiveObjectDetectionActivity.cardFrame!!.getHeight() / 2f
        canvas!!.drawCircle(cx, cy, outerRingFillRadius.toFloat(), outerRingFillPaint)
        canvas.drawCircle(cx, cy, outerRingStrokeRadius.toFloat(), outerRingStrokePaint)
        canvas.drawCircle(cx, cy, innerRingStrokeRadius.toFloat(), innerRingStrokePaint)

        // Draws the ripple to simulate the breathing animation effect.
        ripplePaint.alpha = (rippleAlpha * animator.rippleAlphaScale).toInt()
        ripplePaint.strokeWidth = rippleStrokeWidth * animator.rippleStrokeWidthScale
        val radius = outerRingStrokeRadius + rippleSizeOffset * animator.rippleSizeScale
        canvas.drawCircle(cx, cy, radius, ripplePaint)
    }

    init {
        val resources = overlay.resources
        outerRingFillPaint = Paint()
        outerRingFillPaint.style = Paint.Style.FILL
        outerRingFillPaint.color =
            ContextCompat.getColor(context, R.color.object_reticle_outer_ring_fill)
        outerRingStrokePaint = Paint()
        outerRingStrokePaint.style = Paint.Style.STROKE
        outerRingStrokePaint.strokeWidth =
            resources.getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_stroke_width)
                .toFloat()
        outerRingStrokePaint.strokeCap = Paint.Cap.ROUND
        outerRingStrokePaint.color =
            ContextCompat.getColor(context, R.color.object_reticle_outer_ring_stroke)
        innerRingStrokePaint = Paint()
        innerRingStrokePaint.style = Paint.Style.STROKE
        innerRingStrokePaint.strokeWidth =
            resources.getDimensionPixelOffset(R.dimen.object_reticle_inner_ring_stroke_width)
                .toFloat()
        innerRingStrokePaint.strokeCap = Paint.Cap.ROUND
        innerRingStrokePaint.color = ContextCompat.getColor(context, R.color.white)
        ripplePaint = Paint()
        ripplePaint.style = Paint.Style.STROKE
        ripplePaint.color = ContextCompat.getColor(context, R.color.reticle_ripple)
        outerRingFillRadius =
            resources.getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_fill_radius)
        outerRingStrokeRadius =
            resources.getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_stroke_radius)
        innerRingStrokeRadius =
            resources.getDimensionPixelOffset(R.dimen.object_reticle_inner_ring_stroke_radius)
        rippleSizeOffset =
            resources.getDimensionPixelOffset(R.dimen.object_reticle_ripple_size_offset)
        rippleStrokeWidth =
            resources.getDimensionPixelOffset(R.dimen.object_reticle_ripple_stroke_width)
        rippleAlpha = ripplePaint.alpha
    }
}