package com.iexceed.plugins.gesturesupport

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.RectF
import android.os.CountDownTimer
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.abs
/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
 
class ApzGesturePlugin private constructor (val webView: WebView,
                       val activity: ApzActivity<*>,
                       override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    private var lastTapArea: RectF? = null

    private var lastTapCount = 0

    private val TAP_MAX_DELAY = 500L

    private val RADIUS = 30

    private var tapCounter: TapCounter? = null

    private var scaleGestureDetector: ScaleGestureDetector? = null

    private var IS_ACTION_POINTER_DOWN = false

    override var TAG = "GESTURE"

    override fun execute(params: JSONObject) {
        try {
            val action = params.getString("action")
            if (action == "START") {
                startListener(params)
            } else if (action == "STOP") {
                stopListener(params)
            }
        } catch (e: JSONException) {
            ApzLogger.i(TAG, e.toString())
            sendError("APZ-CNT-077")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun startListener(obj: JSONObject) {
        try {
            callbackId = obj.getString("id")
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            sendError("APZ-CNT-077")
            return
        }
        //initialize gesture detector and scale detector
        val gestureDetector = fetchGestureDetector(activity.applicationContext, GestureListener())
        scaleGestureDetector = fetchScaleGestureDetector(activity.applicationContext, ScaleListener())
        // The ?active pointer? is the one currently moving our object.
        tapCounter = fetchTapCounter(TAP_MAX_DELAY, TAP_MAX_DELAY)
        webView.setOnTouchListener{ _, event ->

            IS_ACTION_POINTER_DOWN = event.actionMasked == MotionEvent.ACTION_POINTER_DOWN
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector!!.onTouchEvent(event)
            false
        }
    }

    private fun fetchTapCounter(millisInFuture: Long, countDownInterval: Long): TapCounter{

        return TapCounter(millisInFuture, countDownInterval)
    }

    private fun fetchScaleGestureDetector(context: Context,
                                          scaleGestureListener: ScaleGestureDetector.OnScaleGestureListener)
                                          : ScaleGestureDetector{

        return ScaleGestureDetector(context, scaleGestureListener)
    }

    private fun fetchGestureDetector(context: Context,
                gestureListener: GestureDetector.OnGestureListener): GestureDetector{

        return GestureDetector(context, gestureListener)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun stopListener(obj: JSONObject) {
        webView.setOnTouchListener(null)
    }

    inner class GestureListener : SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            tapCounter!!.resetCounter()
            val x = e.x
            val y = e.y
            lastTapCount = 2
            lastTapArea = RectF(x - RADIUS, y - RADIUS, x + RADIUS, y + RADIUS)
            return super.onDoubleTap(e)
        }

        override fun onFling(
            event1: MotionEvent,
            event2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val flingMin = 100f
            val velocityMin = 20f
            // If we are using two fingers then it will not take this in consideration
            if (!IS_ACTION_POINTER_DOWN) {
                var leftward = false
                var rightward = false
                var upward = false
                var downward = false

                // calculate the change in X position within the fling gesture
                val horizontalDiff = event2.x - event1.x
                // calculate the change in Y position within the fling gesture
                val verticalDiff = event2.y - event1.y
                val absHDiff = abs(horizontalDiff)
                val absVDiff = abs(verticalDiff)
                val absVelocityX = abs(velocityX)
                val absVelocityY = abs(velocityY)
                if (absHDiff > absVDiff && absHDiff > flingMin && absVelocityX > velocityMin) {
                    if (horizontalDiff > 0) rightward = true else leftward = true
                } else if (absVDiff > flingMin && absVelocityY > velocityMin) {
                    if (verticalDiff > 0) downward = true else upward = true
                }
                when {
                    leftward -> {
                        getGestureCallBack("swipeLeft")
                        ApzLogger.i(TAG, "On swipeLeft")
                    }
                    rightward -> {
                        getGestureCallBack("swipeRight")
                        ApzLogger.i(TAG, "On swipeRight")
                    }
                    upward -> {
                        getGestureCallBack("swipeUp")
                        ApzLogger.i(TAG, "On swipeUp")
                    }
                    downward -> {
                        getGestureCallBack("swipeDown")
                        ApzLogger.i(TAG, "On swipeDown")
                    }
                }
            }
            return super.onFling(event1, event2, velocityX, velocityY)
        }

        override fun onLongPress(e: MotionEvent) {
            getGestureCallBack("longPress")
            ApzLogger.i(TAG, "On Long Click")
            super.onLongPress(e)
        }

        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
            if (lastTapCount == 2 && event.action == MotionEvent.ACTION_DOWN) {
                if (lastTapArea != null && lastTapArea!!.contains(event.x, event.y)) {
                    lastTapCount++
                }
            } else {
                lastTapCount = 1
            }
            setTapCount(lastTapCount)
            return true
        }
    }

    inner class ScaleListener : SimpleOnScaleGestureListener() {
        var startScale = 0f
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            startScale = detector.scaleFactor
            return super.onScaleBegin(detector)
        }

        override fun onScaleEnd(detector: ScaleGestureDetector) {
            val endScale = detector.scaleFactor
            if (startScale > endScale) {
                ApzLogger.i(TAG, "Zoom out Detection")
                getGestureCallBack("zoomOut")
            } else if (startScale < endScale) {
                ApzLogger.i(TAG, "Zoom in Detection")
                getGestureCallBack("zoomIn")
            }
            super.onScaleEnd(detector)
        }
    }

    private fun getGestureCallBack(e: String) {
        var result: JSONObject? = null
        try {
            result = JSONObject()
            result.put("event", e)
        } catch (ex: JSONException) {
            ApzLogger.e(TAG, ex.toString())
        }
        apzPluginUtil.sendSuccess(callbackId, result, true, activity, webView, true)
    }

    fun setTapCount(lastTapCount: Int) {
        var tapValue = ""
        when (lastTapCount) {
            1 -> tapValue = "singleTap"
            2 -> tapValue = "doubleTap"
            3 -> tapValue = "tripleTap"
        }
        getGestureCallBack(tapValue)
        ApzLogger.i(TAG, "Tap Count : $tapValue")
    }

    inner class TapCounter internal constructor(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            if (lastTapArea != null) {
                if (lastTapCount == 2) setTapCount(lastTapCount)
                lastTapCount = 0
                lastTapArea = null
            }
        }

        override fun onTick(millisUntilFinished: Long) {
            //Sonar fix
        }
        fun resetCounter() {
            start()
        }
    }

    private fun sendError(errorCode: String){

        apzPluginUtil.sendError(callbackId, errorCode, null, activity, webView, true)

    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzGesturePlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}
