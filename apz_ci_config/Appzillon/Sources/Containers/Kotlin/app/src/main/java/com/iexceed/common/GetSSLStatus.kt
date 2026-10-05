package com.iexceed.common

import androidx.appcompat.app.AppCompatActivity
import com.iexceed.plugins.ApzPluginUtil

class GetSSLStatus(val aActivity: AppCompatActivity) {
    private val TAG = "GetSSLStatus"
    private val apzPluginUtil = ApzPluginUtil()

    fun execute( statusInterface:GetSSlStatusInterface) {
        try {
            val appId =  StringUtils.getString(StringUtils.APP_ID)
            AppInstructions(aActivity,appId,appId, apzPluginUtil).execute(statusInterface)
        }catch (jsonException: Exception){
            jsonException.printStackTrace()
        }

    }


}