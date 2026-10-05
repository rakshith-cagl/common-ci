package com.iexceed.plugins.selfiecapture

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class OvalGraphicOverlay : View {
    var mContext: Context
    var outerFillColor: Paint? = null
    var frameLayout: FrameLayout? = null

    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val rect = RectF(
            LivePreviewActivity.ovalFrame!!.left.toFloat(),
            LivePreviewActivity.ovalFrame!!.top
                .toFloat(),
            LivePreviewActivity.ovalFrame!!.right.toFloat(),
            LivePreviewActivity.ovalFrame!!.bottom
                .toFloat()
        )
        outerFillColor = Paint(Paint.ANTI_ALIAS_FLAG)
        outerFillColor!!.color =
            Color.parseColor(ApzSelfieCapturePlugin.faceOverlayColor)
        outerFillColor!!.style = Paint.Style.FILL
        outerFillColor!!.alpha = 255
        canvas.drawPaint(outerFillColor!!)
        outerFillColor!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawOval(rect, outerFillColor!!)
    }
}
