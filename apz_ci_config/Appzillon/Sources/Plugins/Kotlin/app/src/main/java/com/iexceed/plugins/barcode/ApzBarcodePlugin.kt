package com.iexceed.plugins.barcode

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.iexceed.appzillonapp.AppzillonMainScreen.Companion.internalPathCheck
import com.iexceed.appzillonapp.R
import com.iexceed.common.ApzActivity
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONObject


/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class ApzBarcodePlugin private constructor(webView: WebView, activity: ApzActivity<*>,
                                           override val apzPluginUtil: IapzPluginUtil) :ApzPlugin()
{
    private lateinit var permissions: Array<String>
    override var TAG = "ApzBarcodePlugin"
    private var mJson = JSONObject()
    var action: String? = null

    init
    {
        super.aActivity = activity
        super.aWebview = webView
    }

    override fun execute(params: JSONObject)
    {
        ApzLogger.d(TAG, " Inside Execute $params")
        try {
            mJson = params
            action = params.getString("action")
            if ("START".equals(action, ignoreCase = true)) {
                //reqPermission()
                if (ActivityCompat.checkSelfPermission(aActivity, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissions = arrayOf(
                        Manifest.permission.CAMERA
                    )
                    requestForPermission("CAMERA")
                } else {
                    openCameraWithScanner()
                }
            } else if ("STOP".equals(action, ignoreCase = true)) {
                if (barcodeScan != null) {
                    barcodeScan?.closeLayout();
                }
            } else if ("SCAN_FROM_GALLERY".equals(action, ignoreCase = true)) {
                initiateScanFromGallery()
            }
        } catch (e1: Exception) {
            apzPluginUtil.sendError(
                callbackId = "", "APZ-CNT-077", null, aActivity,
                aWebview, true
            )
        }
    }

    private fun initiateScanFromGallery() {
        val filePath = mJson.getString("filePath")
        if (!filePath.startsWith(internalPathCheck)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(
                        aActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestForPermission("SCAN")
                } else {
                    ScanImageFromGallery(
                        aActivity,
                        aWebview,
                        mJson.toString(),
                        apzPluginUtil
                    ).scanImage()
                }
            } else {
                if (ActivityCompat.checkSelfPermission(
                        aActivity,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissions = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                    requestForPermission("SCAN")
                } else {
                    ScanImageFromGallery(
                        aActivity,
                        aWebview,
                        mJson.toString(),
                        apzPluginUtil
                    ).scanImage()
                }
            }
        } else {
            ScanImageFromGallery(aActivity, aWebview, mJson.toString(), apzPluginUtil).scanImage()
        }
    }

    private fun requestForPermission(plugin: String) {
        this.aActivity.startOnPermissionForResult(
            aActivity,
            permissions,
            PluginConstants.APZ_REQ_CAMERA,
            object : OnPermissionsResultHandler() {
                override fun handlePermissionResult(
                    requestCode: Int,
                    permissions: Array<String?>,
                    grantResults: IntArray
                ) {
                    if (requestCode == PluginConstants.APZ_REQ_CAMERA) {
                        processPermissionResult(permissions, plugin)
                    } else {
                        permissionDeniedCallback()
                    }
                }
            })
    }

    private fun processPermissionResult(
        permissions: Array<String?>,
        plugin: String
    ) {
        var denied = false
        var neverAskAgain = false
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    aActivity,
                    permission!!
                )
            ) {
                denied = true
            } else {
                if (ActivityCompat.checkSelfPermission(
                        aActivity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    neverAskAgain = true
                }
            }
        }
        if (neverAskAgain) {
            permissionDeniedCallback()
        } else if (denied) {
            displayReconfirmationMessage()
        } else {
            if (plugin.equals("CAMERA", ignoreCase = true)) {
                openCameraWithScanner()
            } else {
                ScanImageFromGallery(
                    aActivity,
                    aWebview,
                    mJson.toString(),
                    apzPluginUtil
                ).scanImage()
            }
        }
    }

    private fun openCameraWithScanner()
    {
        barcodeScan = BarcodeScan(aActivity, aActivity, aWebview, mJson.toString(),apzPluginUtil)
        barcodeScan?.createLayout()
    }

    private fun permissionDeniedCallback()
    {
         apzPluginUtil.sendPermissionDenied(
             "Barcode",this.callbackId, this.aActivity, this.aWebview);
    }

    private fun displayReconfirmationMessage()
    {
        val message = aActivity.applicationContext.getString(R.string.barcode_scan_permission_message)
        val alertDialogBuilder = AlertDialog.Builder(aActivity)
        alertDialogBuilder.setTitle(aActivity.applicationContext.getString(R.string.Permission_denied))
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.cancel()
                if ("SCAN_FROM_GALLERY".equals(action, ignoreCase = true)) {
                    requestForPermission("SCAN")
                } else {
                    requestForPermission("CAMERA")
                }
            }.setNegativeButton("Deny") { dialog, _ ->
                dialog.cancel()
                permissionDeniedCallback()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    companion object
    {
        private var pluginObj: ApzPlugin? = null
        var barcodeScan: BarcodeScan? = null
        //public static BarcodeScan barcodeScan;
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzBarcodePlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun checkForBarcodeOnDestroy() {
            if(barcodeScan != null){
                barcodeScan?.closeLayout()
                barcodeScan = null
            }
        }

        fun onBackPressed(): Boolean {
            if(barcodeScan != null){
                return true
            }
            return false
        }

        val isBarcodeActivity: Boolean
            get() = true
    }
}
