package com.iexceed.plugins.autocapturedocument

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.iexceed.appzillonapp.R

class FrameGraphicOverlay : View {
    var mContext: Context? = null
    var outerFillColor: Paint? = null
    var frameLayout: FrameLayout? = null

    // Xfermode mode=new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        mContext = context
        frameLayout = LiveObjectDetectionActivity.cardFrame
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        LiveObjectDetectionActivity.screenWidth = width.toFloat()
        LiveObjectDetectionActivity.screenHeight = height.toFloat()
        //  RectF rect = new RectF(cardFrame.getLeft()*widthScaleFactor, cardFrame.getTop()*heightScaleFactor, cardFrame.getRight()*widthScaleFactor, cardFrame.getBottom()*heightScaleFactor);
        val rect = android.graphics.RectF(
            LiveObjectDetectionActivity!!.cardFrame!!.getLeft().toFloat(),
            LiveObjectDetectionActivity!!.cardFrame!!.getTop().toFloat(),
            LiveObjectDetectionActivity!!.cardFrame!!.getRight().toFloat(),
            LiveObjectDetectionActivity!!.cardFrame!!.getBottom().toFloat()
        )
        outerFillColor = Paint(Paint.ANTI_ALIAS_FLAG)
        if (ApzAutoCapturePlugin.mOverlayColor.equals("", ignoreCase = true)) {
            outerFillColor!!.color = ContextCompat.getColor(
                mContext!!,
                R.color.object_detected_bg_gradient_end
            )
        } else {
            outerFillColor!!.color = Color.parseColor(ApzAutoCapturePlugin.mOverlayColor)
        }
        outerFillColor!!.style = Paint.Style.FILL
        canvas.drawPaint(outerFillColor!!)
        outerFillColor!!.xfermode =
            PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawRect(rect, outerFillColor!!)
    }
}