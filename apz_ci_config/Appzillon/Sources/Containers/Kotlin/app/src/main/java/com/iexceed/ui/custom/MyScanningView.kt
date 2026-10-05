package com.iexceed.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 */
class MyScanningView : View {
    private val paint = Paint()
    private var mPosY = 0
    private var runAnimation = true
    private var showLine = true
    private var mHandler: Handler? = null
    private var refreshRunnable: Runnable? = null
    private var isGoingDown = true
    private var mHeight = 0

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        paint.color = Color.RED
        paint.strokeWidth = 5.0f
        //Add anything else you want to customize your line, like the stroke width
        mHandler = Handler(Looper.getMainLooper())
        refreshRunnable = Runnable { refreshView() }
    }

    public override fun onDraw(canvas: Canvas) {
        mHeight = canvas.height
        if (showLine) {
            canvas.drawLine(0f, mPosY.toFloat(), canvas.width.toFloat(), mPosY.toFloat(), paint)
        }
        if (runAnimation) {
            mHandler?.postDelayed(refreshRunnable!!, 0)
        }
    }

    fun startAnimation() {
        runAnimation = true
        showLine = true
        this.invalidate()
    }

    fun stopAnimation() {
        runAnimation = false
        showLine = false
        reset()
        this.invalidate()
    }

    private fun reset() {
        mPosY = 0
        isGoingDown = true
    }

    private fun refreshView() {
        //Update new position of the line
        if (isGoingDown) {
            mPosY += 5
            if (mPosY > mHeight) {
                mPosY = mHeight
                isGoingDown = false
            }
        } else {
            //We invert the direction of the animation
            mPosY -= 5
            if (mPosY < 0) {
                mPosY = 0
                isGoingDown = true
            }
        }
        this.invalidate()
    }
}