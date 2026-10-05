package com.iexceed.plugins.filetobase64

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.ApzActivity
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.plugins.filetobase64.FileToBase64.convertFileToBase64
import org.json.JSONObject


/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzFileToBase64Plugin private constructor(
    webView: WebView, activity: ApzActivity<*>,
    override val apzPluginUtil: IapzPluginUtil
) : ApzPlugin() {
    private var params: JSONObject = JSONObject()
    private lateinit var permissions: Array<String>

    init {
        aActivity = activity
        aWebview = webView
    }

    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            this.params = params
            var conditionCheck = false
            if (params.has("filePath") && !params.getString("filePath")
                    .startsWith(AppzillonMainScreen.internalPathCheck)
            ) {
                conditionCheck = true
            }

            if ("N".equals(
                    this.aActivity.resources.getString(R.string.INTERNALSANDBOX),
                    ignoreCase = true
                )
                || conditionCheck
            ) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(
                            this.aActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissions = arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        requestForPermission()
                    } else {
                        proceedFileConversion()
                    }
                } else {
                    verifyPermissions()
                }
            } else {
                proceedFileConversion()
            }
        } catch (e: Exception) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(
                callbackId,
                "APZ-CNT-171",
                null,
                aActivity,
                aWebview,
                true
            )
        }
    }

    private fun verifyPermissions() {
        if ((ActivityCompat.checkSelfPermission(
                this.aActivity,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED)
            || (ActivityCompat.checkSelfPermission(
                this.aActivity,
                Manifest.permission.READ_MEDIA_AUDIO
            )
                    != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(
                this.aActivity,
                Manifest.permission.READ_MEDIA_VIDEO
            )
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            permissions = arrayOf(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_IMAGES
            )
            requestForPermission()
        } else {
            proceedFileConversion()
        }
    }

    private fun proceedFileConversion() {
        convertFileToBase64(
            callbackId,
            this.aWebview,
            this.aActivity,
            params,
            apzPluginUtil
        )
    }


    private fun requestForPermission() {
        this.aActivity.startOnPermissionForResult(
            aActivity,
            permissions,
            PluginConstants.APZ_REQ_WRITE_STORAGE,
            object : OnPermissionsResultHandler() {
                override fun handlePermissionResult(
                    requestCode: Int,
                    permissions: Array<String?>,
                    grantResults: IntArray
                ) {
                    handlePermissionsResult(requestCode, permissions)
                }
            }
        )
    }

    private fun handlePermissionsResult(requestCode: Int, permissions: Array<String?>) {
        if (requestCode == PluginConstants.APZ_REQ_WRITE_STORAGE) {
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
            verifyPluginPermissions(neverAskAgain, denied)
        } else {
            permissionDeniedCallback()
        }
    }

    private fun verifyPluginPermissions(neverAskAgain: Boolean, denied: Boolean) {
        if (neverAskAgain) {
            permissionDeniedCallback()
        } else if (denied) {
            displayReconfirmationMessage()
        } else {
            convertFileToBase64(
                callbackId,
                aWebview,
                aActivity,
                params,
                apzPluginUtil
            )
        }
    }

    private fun displayReconfirmationMessage() {
        val message = "To access files ,allow app to access by granting requested permissions"
        val alertDialogBuilder = AlertDialog.Builder(aActivity)
        alertDialogBuilder.setTitle("Permission Denied")
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.cancel()
                requestForPermission()
            }.setNegativeButton("Deny") { dialog, _ ->
                dialog.cancel()
                permissionDeniedCallback()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun permissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied("File access", callbackId, this.aActivity, this.aWebview)
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(
            webView: WebView,
            aActivity: ApzActivity<*>,
            apzPluginUtil: IapzPluginUtil
        ): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzFileToBase64Plugin(webView, aActivity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}
