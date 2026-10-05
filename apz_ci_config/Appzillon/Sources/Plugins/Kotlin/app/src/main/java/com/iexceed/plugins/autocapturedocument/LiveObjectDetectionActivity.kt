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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.OrientationEventListener
import android.view.View
import android.view.View.OnSystemUiVisibilityChangeListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.mlkit.vision.text.Text
import com.iexceed.appzillonapp.R
import com.iexceed.plugins.autocapturedocument.TextValidator.modifiedPassportLine
import com.iexceed.plugins.autocapturedocument.TextValidator.modifiedTD1Line
import org.json.JSONArray
import org.json.JSONObject

//import com.google.common.collect.ImmutableList;
/**
 * Demonstrates the object detection and visual search workflow using camera preview.
 */
class LiveObjectDetectionActivity : AppCompatActivity(), View.OnClickListener {
    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var frameGraphicOverlay: FrameGraphicOverlay? = null
    private val searchProgressBar: ProgressBar? = null
    var constraint: ConstraintLayout? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowModel.WorkflowState? = null

    private lateinit var permissions: Array<String>

    // private SearchEngine searchEngine;
    private var objectThumbnailForBottomSheet: Bitmap? = null
    private val IMG_PREVIEW = 18
    var captureMode: ToggleButton? = null
    private var frameWidth = 0
    private var frameHeight = 0
    private var prevOrientation = 0
    private var horizonG: Guideline? = null
    private var landHorizonG: Guideline? = null
    private var layerDrawable: LayerDrawable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.e_activity_live_object)
            preview = findViewById(R.id.camera_preview)
            graphicOverlay = findViewById(R.id.camera_preview_graphic_overlay)
            cardFrame = findViewById(R.id.edge_frame)
            frameGraphicOverlay = findViewById(R.id.frame_preview_graphic_overlay)
            graphicOverlay!!.setOnClickListener(this)
            cameraSource = CameraSource(this,graphicOverlay!!)
            cScanningTextMsg = findViewById(R.id.scanningTextMsg)
            cPageTitle = findViewById(R.id.pagetitle)
            cMsgTitle = findViewById(R.id.msgTitle)
            cMessage = findViewById(R.id.messages)
            clickCapture = findViewById(R.id.captureButton)
            constraint = findViewById(R.id.constraintP)
            captureMode = findViewById(R.id.captureMode)
            captureMode!!.setTextColor(Color.parseColor(ApzAutoCapturePlugin.mFontColor))
            captureMode!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    ApzAutoCapturePlugin.isManualCapture = true
                    setToggleVisibility(View.VISIBLE)
                } else {
                    ApzAutoCapturePlugin.isManualCapture = false
                    setToggleVisibility(View.INVISIBLE)
                }
            })
            horizonG = findViewById(R.id.guide)
            landHorizonG = findViewById(R.id.landguide)
            layerDrawable = resources
                .getDrawable(R.drawable.lens) as LayerDrawable
            /* GradientDrawable gd = (GradientDrawable) clickCapture.getBackground();
        gd.setColor(Color.parseColor(mFontColor));*/

            /*  GradientDrawable gd1 = (GradientDrawable) captureMode.getBackground();
        gd1.setStroke(2, Color.parseColor(mFontColor));*/clickCapture!!.setOnClickListener(
                View.OnClickListener {
                    manualClick = true
                    clickCapture!!.setVisibility(View.INVISIBLE)
                })
            setButtonLenscolor()
            // searchProgressBar = findViewById(R.id.search_progress_bar);
            lContext = this.applicationContext
            lActivity = this
            orientation
            ApzAutoCapturePlugin.mActivity.runOnUiThread(
                Runnable {
                    try {
                        /*  if (!deviceTextRecognition) {
                        captureMode.setChecked(true);
                        captureMode.setVisibility(View.INVISIBLE);
                    } else {*/
                        if (ApzAutoCapturePlugin.istoggleButtonReq.equals("Y", ignoreCase = true)) {
                            captureMode!!.setVisibility(View.VISIBLE)
                            if (ApzAutoCapturePlugin.defaultCaptureMode.equals(
                                    "Manual",
                                    ignoreCase = true
                                )
                            ) {
                                captureMode!!.setChecked(true)
                            } else {
                                captureMode!!.setChecked(false)
                            }
                        } else {
                            captureMode!!.setVisibility(View.INVISIBLE)
                        }
                        // }
                    } catch (e: Exception) {
                        //Sonar fix
                    }
                })

            // outputRotate = getWindowManager().getDefaultDisplay().getRotation();
            val orientationEventListener: OrientationEventListener =
                object : OrientationEventListener(this) {
                    override fun onOrientationChanged(orientation: Int) {
                        orientation
                    }
                }
            if (orientationEventListener.canDetectOrientation()) {
                orientationEventListener.enable()
            }
            setUpWorkflowModel()
        } catch (e: Exception) {
            ApzAutoCapturePlugin.failureCallback("Invalid json parameters", "")
            finish()
        }
    }//  failureCallback("Operation failed","");// failureCallback("Operation failed","");//  params.bottomToTop = landHorizonG.getId();


    val orientation: Unit
        get() {
            try {
                outputOrien = resources.configuration.orientation
                if (prevOrientation != 0 && prevOrientation == outputOrien) {
                    return
                }
                prevOrientation = outputOrien
                screenWidth
                screenHeight
                val width =
                    ApzAutoCapturePlugin.documentAspectRatio.split(":").toTypedArray()[0].toDouble()
                val height =
                    ApzAutoCapturePlugin.documentAspectRatio.split(":").toTypedArray()[1].toDouble()
                ApzAutoCapturePlugin.mActivity.runOnUiThread(object : Runnable {
                    override fun run() {
                        try {
                            var params: ConstraintLayout.LayoutParams? = null
                            if (outputOrien == 1) {
                                val topMargin =
                                    (Companion.screenHeight * ApzAutoCapturePlugin.topMarginPercent).toInt() / 100
                                frameWidth =
                                    (Companion.screenWidth * (100 - ApzAutoCapturePlugin.tPortMarginPercent) / 100).toInt()
                                frameHeight = (frameWidth * height / width).toInt()
                                frameHeight =
                                    if (((frameHeight + topMargin) >= Companion.screenHeight)) (Companion.screenHeight.toInt() - topMargin) else frameHeight
                                params = ConstraintLayout.LayoutParams(frameWidth, frameHeight)
                                params.topMargin =
                                    (Companion.screenHeight * ApzAutoCapturePlugin.topMarginPercent).toInt() / 100
                                params.topToTop = constraint!!.id
                                //  params.bottomToTop=horizonG.getId();
                                params.rightToRight = constraint!!.id
                                params.leftToLeft = constraint!!.id
                            } else if (outputOrien == 2) {
                                frameHeight = (Companion.screenHeight * (100 - 43) / 100).toInt()
                                frameWidth = (frameHeight * width / height).toInt()
                                frameWidth =
                                    if (frameWidth > Companion.screenWidth) Companion.screenWidth.toInt() else frameWidth
                                params = ConstraintLayout.LayoutParams(frameWidth, frameHeight)
                                params.topToBottom = cPageTitle!!.id
                                //  params.bottomToTop = landHorizonG.getId();
                                params.rightToRight = constraint!!.id
                                params.leftToLeft = constraint!!.id
                            }
                            cardFrame!!.layoutParams = params

                            frameGraphicOverlay!!.invalidate()
                            setButtonLenscolor()
                            cScanningTextMsg!!.text = ApzAutoCapturePlugin.mScanStatus1
                            cScanningTextMsg!!.setTextColor(Color.parseColor(ApzAutoCapturePlugin.mFontColor))
                            cPageTitle!!.text = ApzAutoCapturePlugin.mPageTitle
                            cPageTitle!!.setTextColor(Color.parseColor(ApzAutoCapturePlugin.mFontColor))
                            cMsgTitle!!.text = ApzAutoCapturePlugin.mMessageTitle
                            cMsgTitle!!.setTextColor(Color.parseColor(ApzAutoCapturePlugin.mFontColor))
                            cMessage!!.text = ApzAutoCapturePlugin.mMessage
                            cMessage!!.setTextColor(Color.parseColor(ApzAutoCapturePlugin.mFontColor))
                        } catch (e: Exception) {
                            //handle exception
                        }
                    }
                })
            } catch (e: Exception) {
                //handle exception
            }
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

    @SuppressLint("NewApi")
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    override fun onResume() {
        super.onResume()
        ///Full screen mode check NBF
        val flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        window.decorView.systemUiVisibility = flags

        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        val decorView = window.decorView
        decorView
            .setOnSystemUiVisibilityChangeListener(object : OnSystemUiVisibilityChangeListener {
                override fun onSystemUiVisibilityChange(visibility: Int) {
                    if ((visibility and View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.systemUiVisibility = flags
                    }
                }
            })
        /////Full screen mode NBF
        workflowModel!!.markCameraFrozen()
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        //  cameraSource.setFrameProcessor(new ProminentObjectProcessor(graphicOverlay, workflowModel));
        cameraSource!!.setFrameProcessor(ProminentObjectProcessor(graphicOverlay!!, workflowModel!!,this))
        // cameraSource.setFrameProcessor(new TextRecognitionProcessor());
        workflowModel!!.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
        orientation
        if (captureMode!!.visibility == View.VISIBLE && captureMode!!.isChecked && ApzAutoCapturePlugin.isManualCapture) {
            setToggleVisibility(View.VISIBLE)
        }
    }

    override fun onPause() {
        super.onPause()
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource!!.release()
            objectThumbnailForBottomSheet = null
            workflowModel = null
            cameraSource = null
        }
    }

    override fun onBackPressed() {
        ApzAutoCapturePlugin.failureCallback("Operation cancelled", "")
        finish()
    }

    override fun onClick(view: View) {
        view.id
    }

    fun setButtonLenscolor() {
        ApzAutoCapturePlugin.mActivity.runOnUiThread(object : Runnable {
            override fun run() {
                try {
                    layerDrawable = resources
                        .getDrawable(R.drawable.lens) as LayerDrawable
                    val gradientDrawable0 = layerDrawable!!
                        .findDrawableByLayerId(R.id.ring0) as GradientDrawable
                    gradientDrawable0.setColor(Color.parseColor(ApzAutoCapturePlugin.mFontColor))
                    val gradientDrawable1 = layerDrawable!!
                        .findDrawableByLayerId(R.id.ring1) as GradientDrawable
                    gradientDrawable1.setColor(Color.parseColor(ApzAutoCapturePlugin.mOverlayColor))
                    val gradientDrawable2 = layerDrawable!!
                        .findDrawableByLayerId(R.id.ring2) as GradientDrawable
                    gradientDrawable2.setColor(Color.parseColor(ApzAutoCapturePlugin.mFontColor))
                    clickCapture!!.background = layerDrawable
                } catch (e: Exception) {
                }
            }
        })
    }

    private fun startCameraPreview() {
        if (!workflowModel!!.isCameraLive() && cameraSource != null) {
            try {
                workflowModel!!.markCameraLive()
                preview!!.start(cameraSource)
            } catch (e: Exception) {
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        if (workflowModel!!.isCameraLive()) {
            workflowModel!!.markCameraFrozen()
            preview!!.stop()
        }
    }

    private fun setUpWorkflowModel() {
        workflowModel = ViewModelProviders.of(this).get(
            WorkflowModel::class.java
        )

        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        workflowModel!!.workflowState.observe(
            this,
            object : Observer<WorkflowModel.WorkflowState?> {
                override fun onChanged(workflowState: WorkflowModel.WorkflowState?) {
                    if (workflowState == null) {
                        return
                    }
                    if (workflowState === WorkflowModel.WorkflowState.CONFIRMED) {
                        stopCameraPreview()
                    }
                    currentWorkflowState = workflowState
                    stateChangeInManualSearchMode(workflowState)
                }
            })

        // Observes changes on the object to search, if happens, fire product search request.
        workflowModel!!.objectToSearch.observe(
            this, object : Observer<DetectionObject> {
                override fun onChanged(`object`: DetectionObject) {
                    try {
                        val image = workflowModel!!.getFireImage()
                        workflowModel!!.setFrameImage(null)
                        if (image != null) {
                            objectThumbnailForBottomSheet = image.bitmapInternal
                        } else {
                            objectThumbnailForBottomSheet = `object`.getObjectThumbnail()
                        }
                        stopCameraPreview()
                        startAnAct(objectThumbnailForBottomSheet)
                    } catch (e: Exception) {
                        ApzAutoCapturePlugin.failureCallback("Error occured", "")
                    }
                }
            })
    }

    fun startAnAct(objectThumbnailForBottomSheet: Bitmap?) {
        try {
            if (objectThumbnailForBottomSheet != null) {
                /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
                objectThumbnailForBottomSheet.compress(Bitmap.CompressFormat.JPEG, 100, stream);*/
                //  byte[] bytearr = stream.toByteArray();
                var fbtext: String = ""
                var fbfulltext = ""
                if (ApzAutoCapturePlugin.deviceTextRecognition) {
                    var oldLine: String = ""
                    var newLine = ""
                    val fireText = workflowModel!!.getFireText()
                    fbfulltext = fireText!!.text
                    val triple = validateFbFullText(fbfulltext, oldLine, newLine)
                    fbfulltext = triple.first
                    newLine = triple.second
                    oldLine = triple.third
                    fbtext = ocrText(fireText, oldLine, newLine).toString()
                    //    Log.i(TAG, " checkVisionText liveMainaActivity: " + fireText.getText() + " FirebaseVisionText " + fireText);
                } /*else{
                    String fireText = workflowModel.getTesseractText();
                    fbtext = "";
                    fbfulltext = fireText;
                }*/

                if (ApzAutoCapturePlugin.nativePreviewScreen.equals("Y", ignoreCase = true)) {
                    //Write file
                    val filename = "bitmap.png"
                    val stream = openFileOutput(filename, MODE_PRIVATE)
                    objectThumbnailForBottomSheet.compress(Bitmap.CompressFormat.PNG, 100, stream)

                    //Cleanup
                    stream.close()
                    val intent = Intent(this, ImagePreviewActivity::class.java)
                    intent.putExtra("bitmap", filename)
                    intent.putExtra("firebaseText", fbtext)
                    intent.putExtra("firebaseWholeText", fbfulltext)
                    startActivityForResult(intent, IMG_PREVIEW)
                } else {
                    ApzAutoCapturePlugin.fetchOutput(
                        objectThumbnailForBottomSheet,
                        fbtext,
                        fbfulltext
                    )
                    finish()
                }
            }
        } catch (e: Exception) {
            ApzAutoCapturePlugin.failureCallback("Error occured", "")
        }
    }

    private fun validateFbFullText(
        fbfulltext: String,
        oldLine: String,
        newLine: String
    ): Triple<String, String, String> {
        var fbfulltext1 = fbfulltext
        var oldLine1 = oldLine
        var newLine1 = newLine
        if (fbfulltext1.isNotEmpty()) {
            if (!modifiedPassportLine!!.isEmpty()) {
                oldLine1 = fbfulltext1.substring(fbfulltext1.lastIndexOf("\n"))
                newLine1 = "\n" + modifiedPassportLine
                fbfulltext1 = fbfulltext1.replace(oldLine1, newLine1)
            }
            if (!modifiedTD1Line!!.isEmpty()) {
                val splitTexts = fbfulltext1.split("\n").toTypedArray()
                oldLine1 = splitTexts[splitTexts.size - 3]
                newLine1 = modifiedTD1Line + "\n"
                fbfulltext1 = fbfulltext1.replace(oldLine1, newLine1)
            }
        }
        return Triple(fbfulltext1, newLine1, oldLine1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intent = Intent()
        try {

            if (resultCode == RESULT_OK) {
                if (data != null) {
                    val filename = data.getStringExtra("bitmap")
                    val array = data.extras!!.getString("ocrText")
                    val textStr = data.extras!!.getString("ocrFullText")
                    val `is` = openFileInput(filename)
                    val bmp = BitmapFactory.decodeStream(`is`)
                    `is`.close()
                    ApzAutoCapturePlugin.fetchOutput(bmp, array, textStr)
                }
                finish()
            } /* else if (resultCode == RESULT_CANCELED) {
                setResult(RESULT_CANCELED, intent);
            }*/


            // }
        } catch (e: Exception) {
            setResult(RESULT_CANCELED, intent)
            finish()
        }
    }

    private fun stateChangeInManualSearchMode(workflowState: WorkflowModel.WorkflowState) {
        //   boolean wasPromptChipGone = (promptChip.getVisibility() == View.GONE);
        // boolean wasSearchButtonGone = (searchButton.getVisibility() == View.GONE);

        when (workflowState) {
            WorkflowModel.WorkflowState.DETECTING, WorkflowModel.WorkflowState.DETECTED, WorkflowModel.WorkflowState.CONFIRMING ->
                startCameraPreview()
            WorkflowModel.WorkflowState.CONFIRMED ->
                stopCameraPreview()
            WorkflowModel.WorkflowState.SEARCHING ->
                stopCameraPreview()
            WorkflowModel.WorkflowState.SEARCHED ->
                stopCameraPreview()
            else -> {}
        }
    }

    fun ocrText(firebaseVisionText: Text?, oldLine: String?, newLine: String): JSONArray {
        val array = JSONArray()
        try {
            val blockText = firebaseVisionText!!.textBlocks
            for (i in blockText.indices) {
                var text = blockText[i].text
                if ((!newLine.isEmpty()) && (text.contains((oldLine)!!))) {
                    text = text.replace((oldLine), newLine)
                }
                val wholeObj = JSONObject()
                val boundsArr = JSONArray()
                val cornerPts = blockText[i].cornerPoints
                for (j in cornerPts!!.indices) {
                    val boundsObj = JSONObject()
                    boundsObj.put("x", cornerPts[j].x)
                    boundsObj.put("y", cornerPts[j].y)
                    boundsArr.put(j, boundsObj)
                }
                wholeObj.put("text", text)
                wholeObj.put("bounds", boundsArr)
                array.put(i, wholeObj)
            }
        } catch (e: Exception) {
            //handle exception
        }
        return array
    }

    companion object {
        private val TAG = "LiveObjectActivity"
        @JvmField
        var cardFrame: FrameLayout? = null
        var outputOrien = 8
        var screenHeight = 0f
        var screenWidth = 0f
        var display: DisplayMetrics? = null
        var cScanningTextMsg: TextView? = null
        var cPageTitle: TextView? = null
        var cMsgTitle: TextView? = null
        var cMessage: TextView? = null
        var clickCapture: ImageButton? = null
        @JvmField
        var frameRect: RectF? = null
        var lContext: Context? = null
        @JvmField
        var lActivity: Activity? = null
        @JvmField
        var manualClick = false
        @JvmStatic
        fun setTextViewMessage(message: String?) {
            ApzAutoCapturePlugin.mActivity.runOnUiThread(object : Runnable {
                override fun run() {
                    try {
                        cScanningTextMsg!!.text = message
                    } catch (e: Exception) {
                    }
                }
            })
        }

        @JvmStatic
        fun setToggleVisibility(visibility: Int) {
            ApzAutoCapturePlugin.mActivity.runOnUiThread(object : Runnable {
                override fun run() {
                    try {
                        when (visibility) {
                            View.VISIBLE -> clickCapture!!.visibility = View.VISIBLE
                            else -> clickCapture!!.visibility = View.INVISIBLE
                        }
                    } catch (e: Exception) {
                    }
                }
            })
        }
    }
}