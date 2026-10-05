package com.iexceed.plugins.inappreview

import android.webkit.WebView
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class ApzInAppReview private constructor(webView: WebView,
                                         activity: ApzActivity<*>,
                                         override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {
    private var reviewManager: ReviewManager? = null

    init {
        super.aWebview = webView
        super.aActivity = activity
    }


    override fun execute(params: JSONObject) {
        try {
            this.callbackId = params.getString("id")
            showRateApp()
        } catch (e: Exception) {
            apzPluginUtil.sendError(
                callbackId="", "APZ-CNT-077", params, aActivity,
                aWebview, true
            )
        }
    }

    private fun showRateApp() {
        reviewManager = ReviewManagerFactory.create(aActivity)
        if (reviewManager != null) {
            val request: Task<ReviewInfo> = reviewManager!!.requestReviewFlow()
            if (request != null) {
                request.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        handleTaskSuccess(task)
                    } else {
                        // There was some problem, continue regardless of the result.
                        sendCallBack()
                    }
                }
            } else {
                sendCallBack()
            }
        } else {
            sendCallBack()
        }
    }

    private fun handleTaskSuccess(task: Task<ReviewInfo>) {
        val reviewInfo: ReviewInfo = task.result
        val flow: Task<Void> =
            reviewManager!!.launchReviewFlow(aActivity, reviewInfo)

        flow.addOnCompleteListener {
            // The flow has finished. The API does not indicate whether the user
            // reviewed or not, or even whether the review dialog was shown. Thus, no
            // matter the result, we continue our app flow.
            ApzLogger.d("ApzInAppReview", "OnComplete.")
            try {
                val result = JSONObject()
                result.put("status", "Review Complete")
                apzPluginUtil.sendSuccess(
                    this.callbackId,
                    result, false, aActivity, aWebview, true
                )
            } catch (e: JSONException) {
                //Sonar fix
            }
        }
    }

    private fun sendCallBack() {
        try {
            val result = JSONObject()
            result.put("status", "Unavailable")
            apzPluginUtil.sendError(this.callbackId, "", result, aActivity, aWebview, true)
        } catch (e: JSONException) {
            //Sonar fix
        }
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzInAppReview(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }
}
