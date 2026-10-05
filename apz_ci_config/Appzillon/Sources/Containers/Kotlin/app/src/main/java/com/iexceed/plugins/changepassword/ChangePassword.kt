package com.iexceed.plugins.changepassword

import android.webkit.WebView
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.ApzActivity
import com.iexceed.common.StringUtils
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.encryption.ApzEncryptionPlugin
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class ChangePassword private constructor(webView: WebView,
                     activity: ApzActivity<*>,
                     override val apzPluginUtil: IapzPluginUtil
) : ApzPlugin()
{
    init {
        super.aWebview = webView
        super.aActivity = activity
    }

    override var  TAG = "ChangePassword"
    override fun execute(params: JSONObject)
    {
        var userName = ""
        var sysDate = ""
        var appzillonBody: JSONObject? = null
        var changePasswordRequest: JSONObject? = null
        val reqfull: JSONObject
        var requestJson: JSONObject? = null
        var reqId = ""
        var mCallBackId = ""
        try {
            mCallBackId = params.getString("id")
            reqfull = params.getJSONObject("params").getJSONObject("reqFull")
            appzillonBody = reqfull.getJSONObject(AppzillonConstants.APPZILLON_BODY)
            changePasswordRequest = appzillonBody.getJSONObject("changePasswordRequest")
            userName = changePasswordRequest.getString("userId")
            var oldPassword: String = changePasswordRequest.getString("pwd")
            var newPassword: String = changePasswordRequest.getString("newPassword")
            changePasswordRequest.getString("confirmPassword")
            sysDate = changePasswordRequest.getString("sysDate")
            reqId = params.getString("reqId")
            if ("#DeviceId".equals(
                    StringUtils.getString("authenticationType"), ignoreCase = true)) {
                val json = JSONObject()
                try {
                    json.put("userId", userName)
                    json.put("pwd", oldPassword)
                    json.put("date", sysDate) //new Date().
                } catch (ex: JSONException) {
                    //Sonar fix
                }
                oldPassword = ApzEncryptionPlugin.hashPwdforNativeLogin(json)
            }
            var key = StringUtils.getString(StringUtils.SERVER_TOKEN)
            val paddingMask = "$$$$$$$$$$$$$$$$"
            if (key.length <= 16) {
                key += paddingMask.substring(0, 16 - key.length)
            }
            if (key.length > 16) {
                key = key.substring(0, 16)
            }
            val lNewPassword = ApzEncryptionPlugin.encryptPassword(key, newPassword)
            if(!lNewPassword.isNullOrEmpty()){
                newPassword = lNewPassword
            }
            changePasswordRequest.put("pwd", oldPassword)
            changePasswordRequest.put("newPassword", newPassword)

            requestJson = JSONObject()
            requestJson.put(
                AppzillonConstants.APPZILLON_HEADER,
                reqfull.getJSONObject(AppzillonConstants.APPZILLON_HEADER))
            requestJson.put(
                AppzillonConstants.APPZILLON_BODY,
                reqfull.getJSONObject(AppzillonConstants.APPZILLON_BODY))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        SendChangePasswordRequest(reqId, mCallBackId, aActivity, aWebview, apzPluginUtil).execute(aActivity, requestJson)
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ChangePassword(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }
    }
}
