package com.iexceed.plugins.barcode

import android.content.Context
import android.util.Size
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.common.util.concurrent.ListenableFuture
import com.iexceed.appzillonapp.R
import com.iexceed.appzillonapp.databinding.ActivityBarcodeScanningBinding
import com.iexceed.plugins.IapzPluginUtil
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/
class BarcodeScan(c: Context, a: AppCompatActivity, wv: WebView, json: String, apzPluginUtil: IapzPluginUtil)
{
    private var mCallbackId: String? = null
    var activity: AppCompatActivity = a
    var context: Context = c
    var webView: WebView = wv
    var mApzPluginUtil = apzPluginUtil
    var screenWidth = 0
    var mJson = ""

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var binding: ActivityBarcodeScanningBinding
    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false
    private var isQrText = "Y"
    var mCallBackId :String = "BARCODE_ID"
    var mFrameLayout :FrameLayout
    var barcodeScanned = false

    @JvmName("getScreenWidth1")
    private fun getScreenWidth(): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun createLayout()
    {
        activity.lifecycleScope.launch(Main) {
            try {
                val linear = activity.findViewById(R.id.mainWebViewLayout) as RelativeLayout
                binding = ActivityBarcodeScanningBinding.inflate(
                    LayoutInflater.from(activity.applicationContext))
                screenWidth = getScreenWidth()
                mFrameLayout = FrameLayout(context)
                val pParams: RelativeLayout.LayoutParams =
                    RelativeLayout.LayoutParams(screenWidth, screenWidth)
                pParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                mFrameLayout.layoutParams = pParams
                mFrameLayout.addView(binding.root)
                linear.addView(mFrameLayout)

                cameraProviderFuture = ProcessCameraProvider.getInstance(activity)
                // Initialize our background executor
                cameraExecutor = Executors.newSingleThreadExecutor()

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    bindPreview(cameraProvider)
                }, ContextCompat.getMainExecutor(activity))

                binding.overlay.post {
                    binding.overlay.setViewFinder()
                }

                binding.scanningView.post {
                    binding.scanningView.startAnimation()
                }
            } catch (e: Exception) {
                val error = JSONObject()
                error.put("errorMessage", "Exception occurred")
                mApzPluginUtil.sendError(
                    mCallbackId, "APZ-CNT-070", error, activity,
                    webView, true)
            }
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?)
    {
        if (activity.isDestroyed || activity.isFinishing) {
            //This check is to avoid an exception when trying to re-bind use cases but user closes the activity.
            //java.lang.IllegalArgumentException: Trying to create use case mediator with destroyed lifecycle.
            return
        }
        barcodeScanned = false
        cameraProvider?.unbindAll()
        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(binding.cameraPreview.width, binding.cameraPreview.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val orientationEventListener = object : OrientationEventListener(activity as Context) {
            override fun onOrientationChanged(orientation : Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation : Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis.targetRotation = rotation
            }
        }
        orientationEventListener.enable()

        class ScanningListener : ScanningResultListener
        {
            override fun onScanned(result: String) {
                activity.runOnUiThread {
                    imageAnalysis.clearAnalyzer()
                    cameraProvider?.unbindAll()
                    binding.scanningView.stopAnimation()
                    binding.scanningView.visibility = View.GONE
                }
                if(!barcodeScanned){
                    releaseCamera()
                    barcodeScanned = true
                    val decodedData = JSONObject()
                    decodedData.put("text", result)
                    sendSuccess(decodedData)
                }
            }

            override fun onFailure(error: String) {
                barcodeScanned = false
                failureCallBack(error)
            }
        }

        val analyzer: ImageAnalysis.Analyzer = MLKitBarcodeAnalyzer(activity, ScanningListener())
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

        val camera = cameraProvider?.bindToLifecycle(activity, cameraSelector, imageAnalysis, preview)

        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            binding.ivFlashControl.visibility = View.VISIBLE

            binding.ivFlashControl.setOnClickListener {
                camera.cameraControl.enableTorch(!flashEnabled)
            }

            camera.cameraInfo.torchState.observe(activity) {
                it?.let { torchState ->
                    if (torchState == TorchState.ON) {
                        flashEnabled = true
                        binding.ivFlashControl.setImageResource(R.drawable.ic_round_flash_on)
                    } else {
                        flashEnabled = false
                        binding.ivFlashControl.setImageResource(R.drawable.ic_round_flash_off)
                    }
                }
            }
        }
        if(isQrText == "Y"){
            binding.tvScanningWith.visibility = View.VISIBLE

        }else {
            binding.tvScanningWith.visibility = View.GONE

        }

    }


    fun closeLayout()
    {
        activity.lifecycleScope.launch(Main) {
            try {
                releaseCamera()
                val relative1: RelativeLayout =
                    activity.findViewById<View>(R.id.mainWebViewLayout) as RelativeLayout
                relative1.removeView(mFrameLayout)

                var data: JSONObject? = null
                try {
                    data = JSONObject()
                    data.put("text", "Camera closed")
                } catch (e: Exception) {
                    //Sonar fix
                }
                ApzBarcodePlugin.barcodeScan = null
                mApzPluginUtil.sendSuccess(
                    mCallbackId, data, false, activity, webView, true)

            } catch (e: Exception) {
                failureCallBack("Failed to close the camera.")
            }
        }
    }

    private fun releaseCamera() {
        cameraExecutor.shutdown()
    }

    private fun failureCallBack(errMessage: String?) {
        val error = JSONObject()
        error.put("errorMessage", errMessage)
        mApzPluginUtil.sendError(
            mCallbackId, errMessage, error, activity,
            webView, true)
    }

    private fun sendSuccess(jsonObject: JSONObject?) {
        mApzPluginUtil.sendSuccess(mCallbackId,
            jsonObject, true,
            activity, webView, true)
    }

    init {
        mJson = json
        mFrameLayout = FrameLayout(activity)
        try {
            val mJson = JSONObject(json)
            mCallbackId = mJson.getString("id")
            isQrText = mJson.optString("showScanBarcodeText")

        } catch (e: JSONException) {
            //handle exception
        }
    }
}
