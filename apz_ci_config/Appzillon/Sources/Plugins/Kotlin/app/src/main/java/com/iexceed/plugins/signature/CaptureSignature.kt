package com.iexceed.plugins.signature

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.util.Base64
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.iexceed.appzillonapp.R
import java.io.ByteArrayOutputStream
import java.lang.Float.max
import java.lang.Float.min

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class CaptureSignature : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(SignatureLayout(this))
    }

    class SignatureLayout(context: Context?) : LinearLayout(context) {
        var buttonsLayout: LinearLayout
        private var signatureView: SignatureView
        var signatureBitmap: Bitmap? = null
        var isCaptured = false
        private val strokeWidth = 5f

        private fun buttonsLayout(): LinearLayout {
            val linearLayout = LinearLayout(this.context)
            linearLayout.orientation = HORIZONTAL
            linearLayout.gravity = Gravity.CENTER_HORIZONTAL
            linearLayout.setBackgroundColor(Color.GRAY)
            val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(10, 0, 10, 0)
            val saveButton = Button(this.context)
            saveButton.setPadding(20, 0, 20, 0)
            val clearButton = Button(this.context)
            clearButton.setPadding(20, 0, 20, 0)
            val cancelButton = Button(this.context)
            cancelButton.setPadding(20, 0, 20, 0)
            saveButton.tag = "Save"
            saveButton.text = resources.getString(R.string.signature_save)
            saveButton.setOnClickListener {
                saveImage(signatureView.signature)
            }
            clearButton.tag ="Clear"
            clearButton.text = resources.getString(R.string.signature_clear)
            clearButton.setOnClickListener {
                signatureView.clearSignature()
            }
            cancelButton.tag = "Cancel"
            cancelButton.text = resources.getString(R.string.signature_cancel)
            cancelButton.setOnClickListener {
                (context as CaptureSignature).setResult(RESULT_CANCELED)
                (context as CaptureSignature).finish()
            }

            linearLayout.addView(cancelButton, layoutParams)
            linearLayout.addView(saveButton, layoutParams)
            linearLayout.addView(clearButton, layoutParams)
            return linearLayout
        }

        fun saveImage(signature: Bitmap?) {
            if (isCaptured) {
                val baos = ByteArrayOutputStream()
                signature!!.compress(Bitmap.CompressFormat.PNG, 80, baos)
                val b: ByteArray = baos.toByteArray()
                val base64sign: String = Base64.encodeToString(b, Base64.DEFAULT)
                val intent = Intent()
                intent.putExtra("signvalue", base64sign)
                (context as CaptureSignature).setResult(RESULT_OK, intent)
                (context as CaptureSignature).finish()
            } else {
                Toast.makeText(context, resources.getString(R.string.no_signature), Toast.LENGTH_SHORT).show()
            }
        }

        inner class SignatureView(context: Context?) : View(context) {
            private val paint: Paint = Paint()
            private val path: Path = Path()
            private var lastTouchX = 0f
            private var lastTouchY = 0f
            private val dirtyRect = RectF()

            val signature: Bitmap?
                get() {
                    if (signatureBitmap == null) {
                        signatureBitmap =
                            Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    }
                    val canvas = Canvas(signatureBitmap!!)
                    draw(canvas)
                    return signatureBitmap
                }

            fun clearSignature() {
                isCaptured = false
                path.reset()
                this.invalidate()
            }

            // all touch events during the drawing
            override fun onDraw(canvas: Canvas) {
                canvas.drawPath(path, paint)
            }

            override fun onTouchEvent(event: MotionEvent): Boolean {
                val eventX = event.x
                val eventY = event.y
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        path.moveTo(eventX, eventY)
                        lastTouchX = eventX
                        lastTouchY = eventY
                        isCaptured = true
                        return isCaptured
                    }
                    MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                        resetDirtyRect(eventX, eventY)
                        val historySize = event.historySize
                        var i = 0
                        while (i < historySize) {
                            val historicalX = event.getHistoricalX(i)
                            val historicalY = event.getHistoricalY(i)
                            expandDirtyRect(historicalX, historicalY)
                            path.lineTo(historicalX, historicalY)
                            i++
                        }
                        path.lineTo(eventX, eventY)
                    }
                    else -> {
                        isCaptured = false
                        return isCaptured
                    }
                }

                invalidate()
                lastTouchX = eventX
                lastTouchY = eventY
                isCaptured = true
                return isCaptured
            }

            private fun expandDirtyRect(historicalX: Float, historicalY: Float) {
                if (historicalX < dirtyRect.left) {
                    dirtyRect.left = historicalX
                } else if (historicalX > dirtyRect.right) {
                    dirtyRect.right = historicalX
                }
                if (historicalY < dirtyRect.top) {
                    dirtyRect.top = historicalY
                } else if (historicalY > dirtyRect.bottom) {
                    dirtyRect.bottom = historicalY
                }
            }

            private fun resetDirtyRect(eventX: Float, eventY: Float) {
                dirtyRect.left = min(lastTouchX, eventX)
                dirtyRect.right = max(lastTouchX, eventX)
                dirtyRect.top = min(lastTouchY, eventY)
                dirtyRect.bottom = max(lastTouchY, eventY)
            }

            init {
                paint.isAntiAlias = true
                paint.color = Color.BLACK
                paint.style = Paint.Style.STROKE
                paint.strokeJoin = Paint.Join.ROUND
                paint.strokeWidth = strokeWidth
                setBackgroundColor(Color.WHITE)
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            }
        }

        init {
            this.orientation = VERTICAL
            buttonsLayout = buttonsLayout()
            signatureView = SignatureView(context)
            this.addView(buttonsLayout)
            this.addView(signatureView)
        }
    }
}
