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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.annotation.KeepName
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.iexceed.appzillonapp.R
import com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.Companion.faceFontColor
import com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.Companion.failureCallback
import com.iexceed.plugins.selfiecapture.ApzSelfieCapturePlugin.Companion.fetchOutput
import java.io.ByteArrayOutputStream
import java.io.IOException


/** Live preview demo for ML Kit APIs. */
@KeepName
class LivePreviewActivity :
  AppCompatActivity(),
  ActivityCompat.OnRequestPermissionsResultCallback,
  OnItemSelectedListener,
  CompoundButton.OnCheckedChangeListener {

  private var cameraSource: CameraSource? = null
  private var preview: CameraSourcePreview? = null
  private var graphicOverlay: GraphicOverlay? = null
  private var selectedModel = FACE_DETECTION
  private var frameWidth = 0
  private var frameHeight = 0

  private var ovalGraphicOverlay: OvalGraphicOverlay? = null
  private var activity:Activity?=null

  private val FACE_IMG_PREV = 88


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Log.d(TAG, "onCreate")
    setContentView(R.layout.activity_live_preview)
    preview = findViewById(R.id.firePreview)
    if (preview == null) {
      Log.d(TAG, "Preview is null")
    }
    activity =this
    mActivity=activity
    graphicOverlay = findViewById(R.id.fireFaceOverlay)
    ovalFrame = findViewById(R.id.oval_frame)
    ovalGraphicOverlay = findViewById(R.id.ovalOverlay)

    fPageTitle = findViewById(R.id.face_pagetitle);
    fInstruction1 = findViewById(R.id.face_instruction1);
    fInstruction2 = findViewById(R.id.face_instruction2);
    fInstruction3 = findViewById(R.id.face_instruction3);
    val gd = ovalFrame!!.getBackground() as GradientDrawable
    gd.setStroke(2, Color.parseColor("#000000"))
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null")
    }
   // graphicOverlay = findViewById(R.id.graphic_overlay)
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null")
    }



 //   if (allPermissionsGranted()) {
      createCameraSource(selectedModel)
   /* } else {
      runtimePermissions
    }*/
  }
  fun changeFrameDimension() {
    screenWidth
    screenHeight
    frameWidth = (Companion.screenWidth * 0.8).toInt()
    frameHeight = (frameWidth * 8 / 7)
    val params = FrameLayout.LayoutParams(frameWidth, frameHeight, Gravity.CENTER)
    ovalFrame!!.layoutParams = params
    fPageTitle!!.text = "Capture Face"
    setInstructionTextView("Position face inside the oval frame.")
    fPageTitle!!.setTextColor(Color.parseColor(faceFontColor))
    fInstruction1!!.setTextColor(Color.parseColor(faceFontColor))
    fInstruction2!!.setTextColor(Color.parseColor(faceFontColor))
    fInstruction3!!.setTextColor(Color.parseColor(faceFontColor))
  }

  val screenWidth: Float
    get() {
      Companion.display = DisplayMetrics()
      windowManager.defaultDisplay.getMetrics(Companion.display)
      Companion.screenWidth = Companion.display!!.widthPixels.toFloat()
      return Companion.screenWidth
    }
  val screenHeight: Float
    get() {
      Companion.display = DisplayMetrics()
      windowManager.defaultDisplay.getMetrics(Companion.display)
      Companion.screenHeight = Companion.display!!.heightPixels.toFloat()
      return Companion.screenHeight
    }

  @Synchronized
  override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = parent?.getItemAtPosition(pos).toString()
    Log.d(TAG, "Selected model: $selectedModel")
    preview?.stop()
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel)
      startCameraSource()
    } else {
      runtimePermissions
    }
  }

  override fun onNothingSelected(parent: AdapterView<*>?) {
    // Do nothing.
  }

  override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
    Log.d(TAG, "Set facing")

  }

  private fun createCameraSource(model: String) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = graphicOverlay?.let { CameraSource(this, it) }
    }
    cameraSource!!.setFacing(CameraSource.CAMERA_FACING_FRONT)
  //  startCameraSource()
  //  ovalGraphicOverlay!!.invalidate()
    handler = object : FaceDetectorProcessor.onFaceDetectionHandler {
      override fun onFaceDetected(face: Face?, firebaseImgBmp: Bitmap?) {
        preview!!.stop()
        startAnAct(firebaseImgBmp);
      }
    }
        //  val faceDetectorOptions = PreferenceUtils.getFaceDetectorOptions(this)
    val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .enableTracking()
        .build()
    Log.d(TAG,"Face options")
          cameraSource!!.setMachineLearningFrameProcessor(
            FaceDetectorProcessor(this, faceDetectorOptions)
          )

  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private fun startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null")
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null")
        }
        preview!!.start(cameraSource, graphicOverlay)
      } catch (e: IOException) {
        Log.e(TAG, "Unable to start camera source.", e)
        cameraSource!!.release()
        cameraSource = null
      }
    }
  }

  public override fun onResume() {
    super.onResume()
    Log.d(TAG, "onResume")
//    createCameraSource(selectedModel)
    changeFrameDimension()
    startCameraSource()
  }

  /** Stops the camera. */
  override fun onPause() {
    super.onPause()
    preview?.stop()
  }

  public override fun onDestroy() {
    super.onDestroy()
    //cameraSource?.release()
  }
    override fun onBackPressed() {
        failureCallback("Operation cancelled")
        finish()
        //  super.onBackPressed();
    }

  private val requiredPermissions: Array<String?>
    get() =
      try {
        val info =
          this.packageManager.getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
        val ps = info.requestedPermissions
        if (ps != null && ps.isNotEmpty()) {
          ps
        } else {
          arrayOfNulls(0)
        }
      } catch (e: Exception) {
        arrayOfNulls(0)
      }

  private fun allPermissionsGranted(): Boolean {
    for (permission in requiredPermissions) {
      if (!isPermissionGranted(this, permission)) {
        return false
      }
    }
    return true
  }

  private val runtimePermissions: Unit
    get() {
      val allNeededPermissions: MutableList<String?> = ArrayList()
      for (permission in requiredPermissions) {
        if (!isPermissionGranted(this, permission)) {
          allNeededPermissions.add(permission)
        }
      }
      if (allNeededPermissions.isNotEmpty()) {
        ActivityCompat.requestPermissions(
          this,
          allNeededPermissions.toTypedArray(),
          PERMISSION_REQUESTS
        )
      }
    }
    fun startAnAct(objectThumbnailForBottomSheet: Bitmap?) {
        try {
            if (objectThumbnailForBottomSheet != null) {
                if (ApzSelfieCapturePlugin.nativePreviewScreen.equals("Y", ignoreCase = true)) {

                    val stream = ByteArrayOutputStream()
                    objectThumbnailForBottomSheet.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val bytearr = stream.toByteArray()
                    val intent = Intent(this, ImagePreviewActivity::class.java)
                    //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("bitmap", bytearr)
                    startActivityForResult(intent, FACE_IMG_PREV)
                  cameraSource?.release()
                } else {
                    fetchOutput(objectThumbnailForBottomSheet)
                    finish()
                }
            }
        } catch (e: Exception) {
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intent = Intent()
        try {
            Log.i(TAG, "onActivityResult: liveobj")
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    val bytearr = data.getByteArrayExtra("bitmap")
                    val bmp = BitmapFactory.decodeByteArray(bytearr, 0, bytearr!!.size)
                    fetchOutput(bmp)
                }
            } else if (resultCode == RESULT_CANCELED) {
                setResult(RESULT_CANCELED, intent)
            }
            finish()
            // }
        } catch (e: Exception) {
            setResult(RESULT_CANCELED, intent)
            finish()
        }
    }
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    Log.i(TAG, "Permission granted!")
    if (allPermissionsGranted()) {
      createCameraSource(selectedModel)
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
  }


  companion object {
    private const val FACE_DETECTION = "Face Detection"

    var handler: FaceDetectorProcessor.onFaceDetectionHandler? = null
    var fPageTitle: TextView? = null
    var fInstruction1: TextView? = null
    var fInstruction2: TextView? = null
    var fInstruction3: TextView? = null

    @JvmField
    var ovalFrame: FrameLayout? = null
    var screenHeight = 0f
    var screenWidth = 0f
    var display: DisplayMetrics? = null
    var mActivity:Activity ?=null
    private const val TAG = "LivePreviewActivity"
    private const val PERMISSION_REQUESTS = 1
    private fun isPermissionGranted(context: Context, permission: String?): Boolean {
      if (ContextCompat.checkSelfPermission(context, permission!!) ==
          PackageManager.PERMISSION_GRANTED
      ) {
        Log.i(TAG, "Permission granted: $permission")
        return true
      }
      Log.i(TAG, "Permission NOT granted: $permission")
      return false
    }
    @JvmStatic
    fun setInstructionTextView(message: String?) {
    mActivity!!.runOnUiThread(Runnable {
        try {
          when (1) {
            1 -> LivePreviewActivity.fInstruction1!!.text = message
            2 -> LivePreviewActivity.fInstruction2!!.text = message
            else -> LivePreviewActivity.fInstruction3!!.text = message
          }
        } catch (e: java.lang.Exception) {
        }
      })
    }
  }
}
