package com.iexceed.plugins.keyboard

import android.R
import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class KeyboardPlugin private constructor (webView: WebView?, activity: ApzActivity<*>,
                     override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {
    var isOpened = false
    var activityRootView: View? = null
    private val mActivity: Activity
    private val mWebview: WebView?
    private var callerId: String? = null
    private var globalLayoutListener: OnGlobalLayoutListener? = null

    fun stopListener(result: JSONObject?) {
        val json = JSONObject()
        json.put("event", "stopped")
        apzPluginUtil.sendSuccess(callerId, json, false, mActivity, mWebview!!, true)
        if (activityRootView != null) {
            activityRootView!!.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            activityRootView = null
        }
    }

    private fun setListenerToRootView() {
        activityRootView = mActivity.window.decorView
            .findViewById(R.id.content)
        activityRootView?.viewTreeObserver?.addOnGlobalLayoutListener(OnGlobalLayoutListenerTask())
    }

    override fun execute(params: JSONObject) {
        var action = ""
        try {
            callerId = params.getString("id")
            action = params.getString("action")
            if (action == "STARTLISTENER") {
                setListenerToRootView()
                var jsonLstn: JSONObject? = null
                jsonLstn = JSONObject()
                jsonLstn.put("event", "started")
                apzPluginUtil.sendSuccess(callerId, jsonLstn, true, mActivity, mWebview!!, true)
            } else if (action == "STOPLISTENER") {
                stopListener(params)
            }
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(
                callbackId="", "APZ-CNT-077", params, mActivity,
                mWebview!!, true
            )
        }
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView?, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = KeyboardPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }

    init {
        mActivity = activity
        mWebview = webView
    }

    inner class OnGlobalLayoutListenerTask : OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            globalLayoutListener = this
            if (activityRootView != null) {
                /*int heightDiff = activityRootView.getRootView()
                .getHeight() - activityRootView.getHeight();*/
                val r = Rect()
                activityRootView!!.getWindowVisibleDisplayFrame(r)
                val screenHeight = activityRootView!!.rootView.height

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                val keypadHeight = screenHeight - r.bottom
                if (keypadHeight > screenHeight * 0.15) { // 99% of the time the height
                    // diff will be due to a keyboard.
                    val result = JSONObject()
                    try {
                        result.put("event", "show")
                    } catch (e: JSONException) {
                        ApzLogger.e(TAG, e.toString())
                    }
                    apzPluginUtil.sendSuccess(
                        callerId, result, true,
                        mActivity,
                        mWebview!!, true
                    )
                    if (!isOpened) {
                        // Do two things, make the view top visible and
                        // the editText smaller
                    }
                    isOpened = true
                } else if (isOpened) {
                    isOpened = false
                    val result = JSONObject()
                    try {
                        result.put("event", "hide")
                    } catch (e: JSONException) {
                        //Sonar fix
                    }
                    apzPluginUtil.sendSuccess(
                        callerId, result, true,
                        mActivity,
                        mWebview!!, true
                    )
                }
            }
        }
    }

}
