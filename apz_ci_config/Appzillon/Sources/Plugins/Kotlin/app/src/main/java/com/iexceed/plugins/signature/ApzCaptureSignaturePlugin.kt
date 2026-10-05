package com.iexceed.plugins.signature

import android.app.Activity
import android.content.Intent
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.common.ExternalActivityResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class ApzCaptureSignaturePlugin private constructor(val webView: WebView,
                                val activity: ApzActivity<*>,
                                override val apzPluginUtil: IapzPluginUtil) : ApzPlugin()  {

    private val CAPTURE_SIGN = 106

    override fun execute(params: JSONObject) {
        try {
            callbackId = params.getString("id")

            val intent = Intent(activity, CaptureSignature::class.java)
            activity.startActivityForResult(
                intent,
                CAPTURE_SIGN,
                object : ExternalActivityResultHandler() {
                    override fun handleActivityResult(resultCode: Int, data: Intent?) {
                        if (resultCode == Activity.RESULT_OK) {
                            val sCallBack = JSONObject()
                            val signString = data!!.getStringExtra("signvalue")
                            try {
                                sCallBack.put("successMessage", "")
                                sCallBack.put("path", "")
                                sCallBack.put("encodedImage", signString)
                            } catch (ex: JSONException) {
                                return
                            }
                            apzPluginUtil.sendSuccess(
                                callbackId,
                                sCallBack,
                                false,
                                activity,
                                webView,
                                true
                            )
                        }else if(resultCode == Activity.RESULT_CANCELED){
                            val sCallBack = JSONObject()
                            try {
                                sCallBack.put("successMessage", "Cancel")
                            } catch (ex: JSONException) {
                                return
                            }
                            apzPluginUtil.sendSuccess(callbackId, sCallBack, false, activity, webView, true)
                        }
                    }
                })
        } catch (e: Exception) {
            //Sonar fix
        }

    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzCaptureSignaturePlugin(webView, activity,apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }
    }
}
