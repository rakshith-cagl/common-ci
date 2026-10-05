/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iexceed.plugins.selfiecapture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import com.iexceed.plugins.selfiecapture.LivePreviewActivity.Companion.handler
import com.iexceed.plugins.selfiecapture.LivePreviewActivity.Companion.ovalFrame
import com.iexceed.plugins.selfiecapture.LivePreviewActivity.Companion.setInstructionTextView
import java.util.*


/** Face Detector Demo.  */
class FaceDetectorProcessor(context: Context, detectorOptions: FaceDetectorOptions?) :
  VisionProcessorBase<List<Face>>(context) {

  private val detector: FaceDetector


  private val overlayBitmap: Bitmap? = null
  private var isDetected = false
  private val faceHandler: onFaceDetectionHandler? = null
  private var openRightEye = false
  private var openLeftEye = false
  private var closeRightEye = false
  private var closeLeftEye = false
  private var startTime: Int = 0

  init {
    val options = detectorOptions
      ?: FaceDetectorOptions.Builder()
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .enableTracking()
        .build()
    detector = FaceDetection.getClient(options)

    Log.v(MANUAL_TESTING_LOG, "Face detector options: $options")
  }

  override fun stop() {
    super.stop()
    detector.close()
  }

  override fun detectInImage(image: InputImage): Task<List<Face>> {
    return detector.process(image)
  }

  override fun onSuccess(faces: List<Face>, graphicOverlay: GraphicOverlay,originalCameraImage: Bitmap?) {

    graphicOverlay.clear()
    if (originalCameraImage != null) {
      val imageGraphic = CameraImageGraphic(graphicOverlay, originalCameraImage)
      graphicOverlay.add(imageGraphic)
    }
    if (faces.isEmpty()) {
      setFalse()
      //   fInstruction.setText("Position face within the oval frame.");
//            fInstruction.setText("Position face inside the oval frame.");
      setInstructionTextView(ApzSelfieCapturePlugin.faceInstruction1)
    }
    else {

      val face: Face = faces[0]/*
      val cameraFacing: Int =
        if (frameMetadata != null) frameMetadata.getCameraFacing() else Camera.CameraInfo.CAMERA_FACING_BACK*/
      val faceGraphic = FaceGraphic(graphicOverlay, face)
      graphicOverlay.add(faceGraphic)
      if (!isDetected) {
        isDetected = false
        if (ovalContainsFace(face, graphicOverlay)) {
          if (compareArea(face, graphicOverlay, "greaterThan0.5")) {
            Log.i(
              TAG,
              "Blink: Eye detection AFTER check right eye open +$openRightEye\nleft eye open +$openLeftEye\nleft eye close +$closeLeftEye\nright eye close +$closeRightEye"
            )
            if (ApzSelfieCapturePlugin.blinkEyeDetection.equals(
                "Y",
                ignoreCase = true
              )
            ) {
              if (!(openLeftEye && openRightEye && closeLeftEye && closeRightEye)) {
                startTime = 0
                //                                    fInstruction.setText(blinkInstruction);
                setInstructionTextView(ApzSelfieCapturePlugin.blinkInstruction)   //blinkinstruction
                checkEyeProbability(face)
              } else {
                if (startTime === 0) {
                  startTime = System.nanoTime().toInt()
                } else {
//                                        fInstruction.setText("Hold steady");
                  setInstructionTextView(ApzSelfieCapturePlugin.holdTimeInstruction)
                  val finishTime = System.nanoTime()
                  val timeElapsed: Long = finishTime - startTime
                  Log.i(
                    TAG,
                    "onSuccess: $timeElapsed"
                  )
                  if (timeElapsed / 1000000 >= ApzSelfieCapturePlugin.holdTimeForCapture * 1000) {
//                                            fInstruction.setText(faceScanningMsg);
                    setInstructionTextView(ApzSelfieCapturePlugin.faceScanningMsg)
                    //   if (face.getRightEyeOpenProbability() > 0.7 && face.getLeftEyeOpenProbability() > 0.7) {
                    isDetected = true
                    handler!!.onFaceDetected(face, originalCameraImage)
                  }
                }
                // }
              }
            } else {
              if (startTime === 0) {
                startTime = System.nanoTime().toInt()
              } else {
//                                        fInstruction.setText("Hold steady");
                setInstructionTextView(ApzSelfieCapturePlugin.holdTimeInstruction)
                val finishTime = System.nanoTime()
                val timeElapsed: Long = finishTime - startTime
                Log.i(
                  TAG,
                  "onSuccess: $timeElapsed"
                )
                if (timeElapsed / 1000000 >= ApzSelfieCapturePlugin.holdTimeForCapture * 1000) {   //holdTimeForCapture
//                                            fInstruction.setText(faceScanningMsg);
                  setInstructionTextView(ApzSelfieCapturePlugin.faceScanningMsg)   //SCANSTATUS
                  if (face.getRightEyeOpenProbability()!! > 0.7 && face.getLeftEyeOpenProbability()!! > 0.7) {
                    isDetected = true
                    handler!!.onFaceDetected(face, originalCameraImage)
                  }
                }
                //                                fInstruction.setText(faceScanningMsg);
                /*  setInstructionTextView("Capturing.....");
                  isDetected = true;
                  handler!!.onFaceDetected(face, originalCameraImage);*/
              }
            }
          }else {
            startTime = 0
            setFalse()
            //  fInstruction.setText("Face is too far.Move closer.");
//                fInstruction.setText(faceInstruction2);
            setInstructionTextView(ApzSelfieCapturePlugin.faceInstruction2)
          }
        } else {
          startTime = 0
          setFalse()
          if (compareArea(face, graphicOverlay, "greater")) {
            //  fInstruction.setText("Face is too close.Move far.");
            //   fInstruction.setText("Face is too close.Move far.");
            setInstructionTextView(ApzSelfieCapturePlugin.faceInstruction3)
          } else {
            // fInstruction.setText("Position face within the oval frame.");
            // fInstruction.setText("Position face inside the oval frame.");
            setInstructionTextView(ApzSelfieCapturePlugin.faceInstruction1)
          }
        }
      }
    }
    graphicOverlay.postInvalidate()


    /*
      for (face in faces) {
        graphicOverlay.add(FaceGraphic(graphicOverlay, face))
        logExtrasForTesting(face)
      }*/


  }

/*  override fun onFailure(e: Exception) {
    Log.e(TAG, "Face detection failed $e")
  }*/


  fun setFalse() {
    openRightEye = false
    openLeftEye = false
    closeRightEye = false
    closeLeftEye = false
  }

  fun checkEyeProbability(face: Face) {
    if (face.leftEyeOpenProbability!! > 0.7) {
      openLeftEye = true
    }
    if (face.leftEyeOpenProbability!! < 0.3) {
      closeLeftEye = true
    }
    if (face.rightEyeOpenProbability!! > 0.7) {
      openRightEye = true
    }
    if (face.rightEyeOpenProbability!! < 0.3) {
      closeRightEye = true
    }
  }

  override fun onFailure(e: Exception) {
    Log.e(TAG, "Face detection failed $e")
  }

  interface onFaceDetectionHandler {
    fun onFaceDetected(face: Face?, firebaseImgBmp: Bitmap?)
  }

  fun ovalContainsFace(face: Face, graphicOverlay: GraphicOverlay): Boolean {
    val x = graphicOverlay.translateX(face.getBoundingBox().centerX().toFloat())
    val y = graphicOverlay.translateY(face.getBoundingBox().centerY().toFloat())
    val ovalRect =
      RectF(
        ovalFrame!!.getLeft().toFloat(), ovalFrame!!.getTop().toFloat(),
        ovalFrame!!.getRight().toFloat(),
        ovalFrame!!.getBottom().toFloat()
      )
    val xOffset = graphicOverlay.scaleX(face.getBoundingBox().width() / 2.0f)
    val yOffset = graphicOverlay.scaleY(face.getBoundingBox().height() / 2.0f)
    val left = x - xOffset
    val top = y - yOffset
    val right = x + xOffset
    val bottom = y + yOffset
    val rect = RectF(left, top, right, bottom)
    return ovalRect.contains(rect)
  }

  fun compareArea(
    face: Face,
    graphicOverlay: GraphicOverlay,
    checker: String
  ): Boolean {
    var bool = false
    val x = graphicOverlay.translateX(face.getBoundingBox().centerX().toFloat())
    val y = graphicOverlay.translateY(face.getBoundingBox().centerY().toFloat())
    val ovalRect =
      RectF(ovalFrame!!.getLeft().toFloat(),
        ovalFrame!!.getTop().toFloat(), ovalFrame!!.getRight().toFloat(), ovalFrame!!.getBottom().toFloat()
      )
    val xOffset = graphicOverlay.scaleX(face.getBoundingBox().width() / 2.0f)
    val yOffset = graphicOverlay.scaleY(face.getBoundingBox().height() / 2.0f)
    val left = x - xOffset
    val top = y - yOffset
    val right = x + xOffset
    val bottom = y + yOffset
    val rect = RectF(left, top, right, bottom)
    val ovalArea = ovalRect.width() * ovalRect.height()
    val boxArea = rect.width() * rect.height()
    if (checker.equals("greater", ignoreCase = true)) {
      bool = boxArea > ovalArea
    } else if (checker.equals("greaterThan0.5", ignoreCase = true)) {
      bool = boxArea >= ovalArea * 0.5
    }
    return bool
  }

  companion object {
    private const val TAG = "FaceDetectorProcessor"
    private fun logExtrasForTesting(face: Face?) {
      if (face != null) {
        Log.v(
          MANUAL_TESTING_LOG,
          "face bounding box: " + face.boundingBox.flattenToString()
        )
        Log.v(
          MANUAL_TESTING_LOG,
          "face Euler Angle X: " + face.headEulerAngleX
        )
        Log.v(
          MANUAL_TESTING_LOG,
          "face Euler Angle Y: " + face.headEulerAngleY
        )
        Log.v(
          MANUAL_TESTING_LOG,
          "face Euler Angle Z: " + face.headEulerAngleZ
        )
        // All landmarks
        val landMarkTypes = intArrayOf(
          FaceLandmark.MOUTH_BOTTOM,
          FaceLandmark.MOUTH_RIGHT,
          FaceLandmark.MOUTH_LEFT,
          FaceLandmark.RIGHT_EYE,
          FaceLandmark.LEFT_EYE,
          FaceLandmark.RIGHT_EAR,
          FaceLandmark.LEFT_EAR,
          FaceLandmark.RIGHT_CHEEK,
          FaceLandmark.LEFT_CHEEK,
          FaceLandmark.NOSE_BASE
        )
        val landMarkTypesStrings = arrayOf(
          "MOUTH_BOTTOM",
          "MOUTH_RIGHT",
          "MOUTH_LEFT",
          "RIGHT_EYE",
          "LEFT_EYE",
          "RIGHT_EAR",
          "LEFT_EAR",
          "RIGHT_CHEEK",
          "LEFT_CHEEK",
          "NOSE_BASE"
        )
        for (i in landMarkTypes.indices) {
          val landmark = face.getLandmark(landMarkTypes[i])
          if (landmark == null) {
            Log.v(
              MANUAL_TESTING_LOG,
              "No landmark of type: " + landMarkTypesStrings[i] + " has been detected"
            )
          } else {
            val landmarkPosition = landmark.position
            val landmarkPositionStr =
              String.format(Locale.US, "x: %f , y: %f", landmarkPosition.x, landmarkPosition.y)
            Log.v(
              MANUAL_TESTING_LOG,
              "Position for face landmark: " +
                      landMarkTypesStrings[i] +
                      " is :" +
                      landmarkPositionStr
            )
          }
        }
        Log.v(
          MANUAL_TESTING_LOG,
          "face left eye open probability: " + face.leftEyeOpenProbability
        )
        Log.v(
          MANUAL_TESTING_LOG,
          "face right eye open probability: " + face.rightEyeOpenProbability
        )
        Log.v(
          MANUAL_TESTING_LOG,
          "face smiling probability: " + face.smilingProbability
        )
        Log.v(
          MANUAL_TESTING_LOG,
          "face tracking id: " + face.trackingId
        )
      }
    }
  }
}
