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

import android.graphics.*
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.objects.DetectedObject
import com.iexceed.appzillonapp.R

/**
 * Draws the detected object info over the camera preview for prominent object detection mode.
 */
internal class ObjectGraphicInProminentMode(
    overlay: com.iexceed.plugins.autocapturedocument.GraphicOverlay,
    `object`: DetectedObject,
    confirmationController: ObjectConfirmationController
) : GraphicOverlay.Graphic(overlay) {
    private val `object`: DetectedObject
    private val confirmationController: ObjectConfirmationController
    private val scrimPaint: Paint
    private val eraserPaint: Paint
    private val boxPaint: Paint

    @ColorInt
    private val boxGradientStartColor: Int

    @ColorInt
    private val boxGradientEndColor: Int
    private val boxCornerRadius: Int
    override fun draw(canvas: Canvas?) {
        val rect: RectF = overlay.translateRect(`object`.boundingBox)

        // Draws the dark background scrim and leaves the object area clear.
        canvas!!.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), scrimPaint)

        // Draws the bounding box with a gradient border color at vertical.
        /* boxPaint.setShader(
        confirmationController.isConfirmed()
            ? null
            : new LinearGradient(
                rect.left,
                rect.top,
                rect.left,
                rect.bottom,
                boxGradientStartColor,
                boxGradientEndColor,
                TileMode.CLAMP));*/canvas.drawRoundRect(
            rect,
            boxCornerRadius.toFloat(),
            boxCornerRadius.toFloat(),
            boxPaint
        )
    }

    init {
        this.`object` = `object`
        this.confirmationController = confirmationController
        scrimPaint = Paint()
        // Sets up a gradient background color at vertical.
        scrimPaint.shader = LinearGradient(
                0.0f,
                0.0f,
                overlay.width.toFloat(),
                overlay.height.toFloat(),
                ContextCompat.getColor(context, R.color.object_detected_bg_gradient_start),
                ContextCompat.getColor(context, R.color.object_detected_bg_gradient_start),
                Shader.TileMode.CLAMP
            )
        eraserPaint = Paint()
        boxPaint = Paint()
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = context
            .getResources()
            .getDimensionPixelOffset(
                if (confirmationController.isConfirmed) R.dimen.bounding_box_confirmed_stroke_width else R.dimen.bounding_box_stroke_width
            ).toFloat()
        boxPaint.color = ContextCompat.getColor(context, R.color.object_detected_bg_gradient_start)
        boxGradientStartColor =
            ContextCompat.getColor(context, R.color.object_detected_bg_gradient_start)
        boxGradientEndColor =
            ContextCompat.getColor(context, R.color.object_detected_bg_gradient_start)
        boxCornerRadius =
            context.getResources().getDimensionPixelOffset(R.dimen.bounding_box_corner_radius)
    }
}