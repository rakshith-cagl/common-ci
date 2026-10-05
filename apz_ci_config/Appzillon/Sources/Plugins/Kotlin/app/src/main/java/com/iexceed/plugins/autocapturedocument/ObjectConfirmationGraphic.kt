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

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.iexceed.appzillonapp.R
import com.iexceed.plugins.autocapturedocument.GraphicOverlay.Graphic

/**
 * Similar to the camera reticle but with additional progress ring to indicate an object is getting
 * confirmed for a follow up processing, e.g. product search.
 */
class ObjectConfirmationGraphic internal constructor(
    overlay: GraphicOverlay, private val confirmationController: ObjectConfirmationController
) : Graphic(overlay) {
    private val outerRingFillPaint: Paint
    private val outerRingStrokePaint: Paint
    private val innerRingPaint: Paint
    private val progressRingStrokePaint: Paint
    private val outerRingFillRadius: Int
    private val outerRingStrokeRadius: Int
    private val innerRingStrokeRadius: Int


    init {
        val resources: Resources = overlay.getResources()
        outerRingFillPaint = Paint()
        outerRingFillPaint.setStyle(Paint.Style.FILL)
        outerRingFillPaint.setColor(
            ContextCompat.getColor(context, R.color.object_reticle_outer_ring_fill)
        )
        outerRingStrokePaint = Paint()
        outerRingStrokePaint.setStyle(Paint.Style.STROKE)
        outerRingStrokePaint.setStrokeWidth(
            resources.getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_stroke_width)
                .toFloat()
        )
        outerRingStrokePaint.setStrokeCap(Paint.Cap.ROUND)
        outerRingStrokePaint.setColor(
            ContextCompat.getColor(context, R.color.object_reticle_outer_ring_stroke)
        )
        progressRingStrokePaint = Paint()
        progressRingStrokePaint.setStyle(Paint.Style.STROKE)
        progressRingStrokePaint.setStrokeWidth(
            resources.getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_stroke_width)
                .toFloat()
        )
        progressRingStrokePaint.setStrokeCap(Paint.Cap.ROUND)
        progressRingStrokePaint.setColor(ContextCompat.getColor(context, R.color.white))

        /*if (PreferenceUtils.isMultipleObjectsMode(overlay.getContext())) {
      innerRingPaint = new Paint();
      innerRingPaint.setStyle(Style.FILL);
      innerRingPaint.setColor(ContextCompat.getColor(context, R.color.object_reticle_inner_ring));
    } else {*/innerRingPaint = Paint()
        innerRingPaint.setStyle(Paint.Style.STROKE)
        innerRingPaint.setStrokeWidth(
            resources.getDimensionPixelOffset(R.dimen.object_reticle_inner_ring_stroke_width)
                .toFloat()
        )
        innerRingPaint.setStrokeCap(Paint.Cap.ROUND)
        innerRingPaint.setColor(ContextCompat.getColor(context, R.color.white))
        // }
        outerRingFillRadius =
            resources.getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_fill_radius)
        outerRingStrokeRadius =
            resources.getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_stroke_radius)
        innerRingStrokeRadius =
            resources.getDimensionPixelOffset(R.dimen.object_reticle_inner_ring_stroke_radius)
    }

    override fun draw(canvas: Canvas?) {
        val cx: Float = canvas!!.getWidth() / 2f
        val cy: Float = canvas.getHeight() / 2f
        canvas!!.drawCircle(cx, cy, outerRingFillRadius.toFloat(), outerRingFillPaint)
        canvas.drawCircle(cx, cy, outerRingStrokeRadius.toFloat(), outerRingStrokePaint)
        canvas.drawCircle(cx, cy, innerRingStrokeRadius.toFloat(), innerRingPaint)
        val progressRect: RectF = RectF(
            cx - outerRingStrokeRadius,
            cy - outerRingStrokeRadius,
            cx + outerRingStrokeRadius,
            cy + outerRingStrokeRadius
        )
        val sweepAngle: Float = confirmationController.progress * 360
        canvas.drawArc(
            progressRect, 0f,
            sweepAngle,  /* useCenter= */
            false,
            progressRingStrokePaint
        )
    }
}