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

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.google.mlkit.vision.text.Text
import com.iexceed.appzillonapp.R
import com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.Companion.setTextViewMessage
import com.iexceed.plugins.autocapturedocument.LiveObjectDetectionActivity.Companion.setToggleVisibility
import com.iexceed.plugins.autocapturedocument.TextValidator.validateDetectedText

/**
 * A processor to run object detector in prominent object only mode.
 */
class ProminentObjectProcessor(
    graphicOverlay: GraphicOverlay,
    private val workflowModel: WorkflowModel, context: Context
) : VisionProcessorBase<List<DetectedObject>>(context) {
    private val detector: ObjectDetector
    private val confirmationController: ObjectConfirmationController
    private val initOpencv = false
    private val reticleOuterRingRadius: Int
    private var captureHoldTimeStart: Long = 0
    private var captureTimeOutStart: Long = 0



    private fun processFrame(image: InputImage?, `object`: DetectedObject?) {
        try {
            val bitmap = getCroppedBitmap(image, "temp")
            val scaleBmpCard = convertToBlackWhite(bitmap, "grayscale")
            val objectImage = InputImage.fromBitmap(scaleBmpCard,90 )
            if (ApzAutoCapturePlugin.deviceTextRecognition && !confirmationController.isConfirmed && !isTextDetectionInproress) {
                setTextViewMessage(ApzAutoCapturePlugin.mScanStatus1)
                workflowModel.setFireText(null)
                isTextDetectionInproress = true
                val tt = TextDetector(
                    image!!,
                    objectImage,
                    `object`!!,
                    object : TextDetector.TextDetectorHandler {
                        override fun onTextDetected(
                            text: Text?,
                            fullImage: InputImage?,
                            objectImage: InputImage?,
                            fireObject: DetectedObject?
                        ) {
                            isTextDetectionInproress = false
                            try {

                                if (text != null) {
                                    setTextViewMessage("text  --" + text.text)
                                    if (validateDetectedText(
                                            text.text,
                                            ApzAutoCapturePlugin.documentType
                                        )
                                    ) {
                                        workflowModel.setFireText(text)
                                        val bitmap = getCroppedBitmap(image, "final")
                                        val finalObjectImage = InputImage.fromBitmap(
                                            bitmap!!, 90
                                        )
                                        confirmObject(image, `object`, finalObjectImage)
                                    } else {
                                        resetObject()
                                    }
                                } else {
                                    resetObject()
                                }
                            } catch (e: Exception) {
                                ApzAutoCapturePlugin.failureCallback(
                                    "Exception occured",
                                    ""
                                )
                                LiveObjectDetectionActivity.lActivity!!.finish()
                            }
                        }
                    })
                tt.findText()
            }
        } catch (e: Exception) {
            captureHoldTimeStart = 0
            resetObject()
        }
    }

    private fun resetObject() {
        if (ApzAutoCapturePlugin.isManualCapture) {
            setToggleVisibility(View.VISIBLE)
        }
        // captureHoldTimeStart = 0;
        confirmationController.reset()
        workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTED)
    }

    private fun confirmObject(
        image: InputImage?,
        `object`: DetectedObject?,
        objectImage: InputImage
    ) {
        setTextViewMessage(ApzAutoCapturePlugin.mScanStatus2)
        confirmationController.reset()
        confirmationController.setConfirmed()
        workflowModel.confirmingObject(
            DetectionObject(`object`!!, 0, image!!), confirmationController.progress, objectImage
        )
    }

    private fun getCroppedBitmap(image: InputImage?, type: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val bmpWidth = image!!.bitmapInternal!!.width.toFloat()
            val bmpHeight = image.bitmapInternal!!.height.toFloat()

            var frameL = 0
            var frameT = 0
            var frameW = 0
            var frameH = 0
            val frame = Rect(
                LiveObjectDetectionActivity.cardFrame!!.left,
                LiveObjectDetectionActivity.cardFrame!!.top,
                LiveObjectDetectionActivity.cardFrame!!.right,
                LiveObjectDetectionActivity.cardFrame!!.bottom
            )
            //   RectF frame = overlayFrame.translateRect(new Rect(cardFrame.getLeft(), cardFrame.getTop(), cardFrame.getRight(), cardFrame.getBottom()));
            bmpWidth * bmpHeight
            LiveObjectDetectionActivity.screenWidth * LiveObjectDetectionActivity.screenHeight
            val conRatioW = bmpWidth / LiveObjectDetectionActivity.screenWidth
            val conRatioH = bmpHeight / LiveObjectDetectionActivity.screenHeight
            frameL = Math.round(frame.left * conRatioW)
            frameT = Math.round(frame.top * conRatioH)
            frameW = Math.round(frame.width() * conRatioW)
            frameH = Math.round(frame.height() * conRatioH)
            val addWidth = (frameW * 0.2).toInt()
            val addHeight = (frameH * 0.2).toInt()
            if (type.equals("final", ignoreCase = true)) {
                frameL = if (frameL - addWidth / 2 < 0) 0 else frameL - addWidth / 2
                frameT = if (frameT - addHeight / 2 < 0) 0 else frameT - addHeight / 2
                frameW =
                    if (frameW + addWidth + frameL > bmpWidth) (bmpWidth - frameL).toInt() else frameW + addWidth
                frameH =
                    if (frameH + addHeight + frameT > bmpHeight) (bmpHeight - frameT).toInt() else frameH + addHeight
            }

            bitmap = Bitmap.createBitmap(
                image.bitmapInternal!!,
                frameL,
                frameT,
                frameW,
                frameH
            )
        } catch (ex: Exception) {
        }
        return bitmap
    }

    private fun objectBoxOverlapsConfirmationReticle(
        graphicOverlay: GraphicOverlay, `object`: DetectedObject
    ): Boolean {
        val boxRect = graphicOverlay!!.translateRect(`object`.boundingBox)
        LiveObjectDetectionActivity.frameRect = RectF(
            LiveObjectDetectionActivity.cardFrame!!.left
                .toFloat(), LiveObjectDetectionActivity.cardFrame!!.top
                .toFloat(), LiveObjectDetectionActivity.cardFrame!!.right
                .toFloat(), LiveObjectDetectionActivity.cardFrame!!.bottom
                .toFloat()
        )
        graphicOverlay.width / 2f
        graphicOverlay.height / 2f
        return LiveObjectDetectionActivity.frameRect!!.intersect(boxRect) || LiveObjectDetectionActivity.frameRect!!.contains(
            boxRect
        )
    }



    protected override fun detectInImage(image: InputImage): Task<List<DetectedObject>> {
        //   boolean isChecked=checkVisionText(image);
        return detector.process(image)
    }

    override fun stop() {
        //handle onStop
    }

    companion object {
        private const val TAG = "ProminentObjProcessor"
        var outputTextRecognised = arrayOf("")
        private var isTextDetectionInproress = false
        fun convertToBlackWhite(colorBmp: Bitmap?, mAction: String): Bitmap {
            val bmpMonochrome =
                Bitmap.createBitmap(colorBmp!!.width, colorBmp.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmpMonochrome)
            //set contrast
            val contrastMatrix = ColorMatrix()
            //change contrast
            if (mAction.equals("grayscale", ignoreCase = true)) {
                contrastMatrix.setSaturation(0f)
            } else if (mAction.equals("BW", ignoreCase = true)) {
                contrastMatrix.setSaturation(0f)
                contrastMatrix.set(
                    floatArrayOf(
                        128f,
                        128f,
                        128f,
                        0f,
                        (-128 * 255).toFloat(),
                        128f,
                        128f,
                        128f,
                        0f,
                        (-128 * 255).toFloat(),
                        128f,
                        128f,
                        128f,
                        0f,
                        (-128 * 255).toFloat(),
                        0f,
                        0f,
                        0f,
                        1f,
                        0f
                    )
                )
            }
            //apply contrast
            val contrastPaint = Paint()
            contrastPaint.colorFilter = ColorMatrixColorFilter(contrastMatrix)
            canvas.drawBitmap(colorBmp, 0f, 0f, contrastPaint)
            return bmpMonochrome
        }
    }

    init {
        confirmationController = ObjectConfirmationController(graphicOverlay)
        //  cameraReticleAnimator = new CameraReticleAnimator(graphicOverlay);
        reticleOuterRingRadius = graphicOverlay
            .resources
            .getDimensionPixelOffset(R.dimen.object_reticle_outer_ring_stroke_radius)

        val builder = ObjectDetectorOptions.Builder().setDetectorMode(ObjectDetectorOptions.STREAM_MODE)

        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.DEFAULT_OPTIONS.detectorMode)
        detector = ObjectDetection.getClient(builder.build())
        /*
            this.detector = FirebaseVision.getInstance().getOnDeviceObjectDetector(optionsBuilder.build());
*/
    }
    override fun onFailure(e: Exception) {
        Log.e("tag",e.localizedMessage)
    }

    override fun onSuccess(
        results: List<DetectedObject>,
        graphicOverlay: GraphicOverlay,
        originalCameraImage: Bitmap?
    ) {
        try {
            if (!workflowModel.isCameraLive()) {
                return
            }

            //add    graphicOverlay.add(new ObjectReticleGraphic(graphicOverlay, cameraReticleAnimator));
            //add   cameraReticleAnimator.start();
            setTextViewMessage(ApzAutoCapturePlugin.mScanStatus1)
            setTextViewMessage("objects is empty"+results.toString())
            val fireImageManual = InputImage.fromBitmap(originalCameraImage!!,90)
            //manual click changes
            if (ApzAutoCapturePlugin.isManualCapture) {
                setupManualCapture(results, fireImageManual)
            } else {
                //timeout check
                if (ApzAutoCapturePlugin.timeOutForAutoCapture != 0) {
                    setupAutomaticCapture()
                }
                //auto capture
                handleAutoCaptureResult(results, graphicOverlay, originalCameraImage)
            }
        } catch (e: Exception) {
            captureHoldTimeStart = 0
            resetObject()
        }
        graphicOverlay!!.clear()
        if (results!!.isEmpty()) {
            //add graphicOverlay.add(new ObjectReticleGraphic(graphicOverlay, cameraReticleAnimator));
            //add  cameraReticleAnimator.start();
        } else {
            if (objectBoxOverlapsConfirmationReticle(graphicOverlay, results[0])) {
                // User is confirming the object selection.
                //add   cameraReticleAnimator.cancel();
                graphicOverlay.add(
                    ObjectGraphicInProminentMode(
                        graphicOverlay, results[0], confirmationController
                    )
                )
                if (!confirmationController.isConfirmed) {
                    // Shows a loading indicator to visualize the confirming progress if in auto search mode.
                    //  graphicOverlay.add(new ObjectConfirmationGraphic(graphicOverlay, confirmationController));
                }
            } else {
                // Object is detected but the confirmation reticle is moved off the object box, which
                // indicates user is not trying to pick this object.
                graphicOverlay.add(
                    ObjectGraphicInProminentMode(
                        graphicOverlay, results[0], confirmationController
                    )
                )
                //add   graphicOverlay.add(new ObjectReticleGraphic(graphicOverlay, cameraReticleAnimator));
                //add  cameraReticleAnimator.start();
            }
        }
        graphicOverlay.invalidate()
    }

    private fun handleAutoCaptureResult(
        results: List<DetectedObject>,
        graphicOverlay: GraphicOverlay,
        originalCameraImage: Bitmap?
    ) {
        if (results.isEmpty()) {
            confirmationController.reset()
            workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
        } else {
            val objectIndex = 0
            val `object` = results[objectIndex]
            if (objectBoxOverlapsConfirmationReticle(graphicOverlay, `object`!!)) {
                if (captureHoldTimeStart == 0L) {
                    captureHoldTimeStart = System.nanoTime()
                } else {
                    setTextViewMessage(ApzAutoCapturePlugin.mScanStatus3)
                    val finishTime = System.nanoTime()
                    val timeElapsed = finishTime - captureHoldTimeStart
                    if (timeElapsed / 1000000 >= ApzAutoCapturePlugin.holdTimeForAutoCapture * 1000) {
                        setTextViewMessage(ApzAutoCapturePlugin.mScanStatus1)
                        processFrame(InputImage.fromBitmap(originalCameraImage!!, 90), `object`)
                    }
                }
            } else {
                // Object detected but user doesn't want to pick this one.
                resetObject()
                //  captureHoldTimeStart = 0;
            }
        }
    }

    private fun setupAutomaticCapture() {
        if (captureTimeOutStart == 0L) {
            captureTimeOutStart = System.nanoTime()
        } else {
            val finishTime = System.nanoTime()
            val timeElapsed = finishTime - captureTimeOutStart
            if (timeElapsed / 1000000 >= ApzAutoCapturePlugin.timeOutForAutoCapture * 1000) {
                ApzAutoCapturePlugin.failureCallback(
                    "Auto capture timed out",
                    "APZ-CNT-340"
                )
                LiveObjectDetectionActivity.lActivity!!.finish()
            }
        }
    }

    private fun setupManualCapture(
        results: List<DetectedObject>,
        fireImageManual: InputImage
    ) {
        if (LiveObjectDetectionActivity.manualClick) {
            val manualObject: DetectedObject? = getManualObject(results)
            processFrame(fireImageManual, manualObject)
            LiveObjectDetectionActivity.manualClick = false
        } else {
            setTextViewMessage(ApzAutoCapturePlugin.mScanStatus1)
            confirmationController.reset()
            workflowModel.setWorkflowState(WorkflowModel.WorkflowState.DETECTED)
        }
    }

    private fun getManualObject(results: List<DetectedObject>) =
        if (results.isEmpty()) {
            null
        } else {
            results[0]
        }

}