package com.iexceed.plugins.camera

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.iexceed.common.ApzActivity
import com.iexceed.common.ExternalActivityResultHandler
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.common.UserSettings.deleteBase64Image
import com.iexceed.common.UserSettings.getBase64Image
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class ApzCameraPlugin private constructor(webView: WebView,
                                          activity: ApzActivity<*>,
                                          override val apzPluginUtil: IapzPluginUtil
) :
    ApzPlugin() {
    override var TAG:String = "ApzCameraPlugin"
    var mJsonObj: JSONObject? = null
    private lateinit var permissions: Array<String>
    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            mJsonObj = params
            if (params.optString("sourceType").equals("Photo", ignoreCase = true)) {
                //isSourceTypeExternal = true;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(
                            aActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        requestForPermission()
                    } else {
                        callCamera()
                    }
                } else {
                    if (ActivityCompat.checkSelfPermission(
                            aActivity,
                            Manifest.permission.READ_MEDIA_IMAGES
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissions = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                        requestForPermission()
                    } else {
                        callCamera()
                    }
                }
            } else if (ActivityCompat.checkSelfPermission(aActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions = arrayOf(Manifest.permission.CAMERA)
                requestForPermission()
            } else {
                callCamera()
            }

        } catch (e: Exception) {
            ApzLogger.e(TAG, e.message!!)
            apzPluginUtil.sendError(callbackId, "APZ-CNT-211", null, aActivity, aWebview, true)
        }
    }

    private fun requestForPermission() {
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
                        handleCameraPermissions(permissions)
                    } else {
                        PermissionDeniedCallback()
                    }
                }
            }
        )
    }

    private fun handleCameraPermissions(permissions: Array<String?>) {
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
            PermissionDeniedCallback()
        } else if (denied) {
            displayReconfirmationMessage()
        } else {
            callCamera()
        }
    }

    private fun displayReconfirmationMessage() {
        val message =
            "To capture image/select image from gallery, allow app to access by requested permissions"
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
                PermissionDeniedCallback()
            }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun PermissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied(
            "Camera or Storage ",
            callbackId,
            this.aActivity,
            this.aWebview
        )
    }

    private fun callCamera() {
        val camera = Intent(aActivity, NativeCamera::class.java)
        camera.putExtra("jsonStr", mJsonObj.toString())

        this.aActivity.startActivityForResult(
            camera,
            TAKE_PIC,
            ExternalActivityResultHandlerTask())
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        const val TAKE_PIC = 10
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzCameraPlugin(webView, activity,apzPluginUtil )
            }
            return pluginObj
        }

        fun IsCamera(): Boolean {
            return true
        }
    }
    init {
        aWebview = webView
        aActivity = activity
    }

    inner class ExternalActivityResultHandlerTask : ExternalActivityResultHandler() {
        override fun handleActivityResult(resultCode: Int, data: Intent?) {
            try {
                if (resultCode == Activity.RESULT_OK) {
                    val imgString = getBase64Image(EncryptedPrefHelper.getPrefs())
                    try {
                        val resultBody = JSONObject()
                        if (mJsonObj!!.getString("action").equals(
                                "base64", ignoreCase = true
                            )
                        ) {
                            resultBody.put("encodedImage", imgString)
                            resultBody.put("path", "")
                            resultBody.put("successMessage", "")
                        } else if (mJsonObj!!.getString("action")
                                .equals("save", ignoreCase = true)
                        ) {
                            val url = data!!.getStringExtra("url")
                            resultBody.put("encodedImage", "")
                            resultBody.put("path", url)
                            resultBody.put("successMessage", "")
                        } else if (mJsonObj!!.getString("action")
                                .equals("base64_Save", ignoreCase = true)
                        ) {
                            val url = data!!.getStringExtra("url")
                            resultBody.put("successMessage", "")
                            resultBody.put("path", url)
                            resultBody.put("encodedImage", imgString)
                        } else if (mJsonObj!!.getString("action")
                                .equals("srcBase64", ignoreCase = true)
                        ) {
                            val idName = data!!.getStringExtra("idName")
                            resultBody.put("successMessage", "Success")
                            resultBody.put("path", "")
                            resultBody.put("encodedImage", "")
                            aWebview.loadUrl(
                                "javascript:(function() { "
                                        + "$('#"
                                        + idName
                                        + "').attr('src','data:image/jpg;base64,"
                                        + imgString + "');}))"
                            )
                        } else if (mJsonObj!!.getString("action")
                                .equals("srcUrl", ignoreCase = true)
                        ) {
                            val idName = data!!.getStringExtra("idName")
                            val url = data.getStringExtra("url")
                            resultBody.put("successMessage", "Success")
                            resultBody.put("path", "")
                            resultBody.put("encodedImage", "")
                            aWebview.loadUrl(
                                "javascript:(function() { "
                                        + "$('#" + idName + "').attr('src','"
                                        + url + "');})"
                            )
                        }
                        apzPluginUtil.sendSuccess(
                            callbackId,
                            resultBody, false,
                            aActivity,
                            aWebview, true
                        )
                        pluginObj = null
                    } catch (ex: Exception) {
                        return
                    }
                    deleteBase64Image(EncryptedPrefHelper.getPrefs())
                    getBase64Image(EncryptedPrefHelper.getPrefs())
                } else {
                    val json = JSONObject()
                    try {
                        json.put("text", "Operation Cancelled")
                    } catch (e: JSONException) {
                        //Sonar fix
                    }
                    apzPluginUtil.sendError(
                        callbackId,
                        "APZ-CNT-211",
                        json,
                        aActivity,
                        aWebview,
                        true
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}
