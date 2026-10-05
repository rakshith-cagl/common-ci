package com.iexceed.retrofitmvvm.data.model.multifactor

import android.content.Context
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.StringUtils

data class MultiFactorHeader(
    var appId: String,
    var deviceId: String,
    var interfaceId: String,
    var latitude: String,
    var longitude: String,
    var origination: String,
    var preLogin: String,
    var requestKey: String,
    var screenId: String,
    var sessionId: String,
    var source: String,
    var status: Boolean,
    var userId: String
) {
    constructor(aContext:Context) : this(
         appId= StringUtils.getString(StringUtils.APP_ID),
         deviceId= AppzillonUtils.getDeviceId(),
         interfaceId= "appzillonDeviceRegistration",
         latitude= "",
         longitude= "",
         origination = AppzillonUtils.ipAddress(),
         preLogin= "true",
         requestKey= "000NEW",
         screenId= "lauchApp",
         sessionId= "",
         source= "APPZILLON",
         status= true,
         userId = AppzillonConstants.USER_ID_FOR_OTA
    )
}