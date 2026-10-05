package com.iexceed.retrofitmvvm.data.model.multifactor

import android.content.Context
import com.iexceed.common.AppzillonConstants.ANDROID_OS
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.StringUtils

data class DeviceRegisterRequest(
    val appId: String,
    var appVersion: String,
    val deviceId: String,
    var deviceName: String,
    var latitude: String,
    var longitude: String,
    val make: String,
    val mobile1: String,
    val mobile2: String,
    val model: String,
    val os: String,
    var osVersion: String,
    var screenResolution: String
) {
    constructor(aContext: Context) : this(

        appId = StringUtils.getString(StringUtils.APP_ID),
        os = ANDROID_OS,
        osVersion = "",
        deviceId = AppzillonUtils.getDeviceId(),
        mobile1 = "UNKNOWN",
        mobile2 = "UNKNOWN",
        model =  AppzillonUtils.getDeviceType(),
        make = AppzillonUtils.getDeviceMake(),
        screenResolution = "",
        appVersion = "",
        deviceName = "",
        latitude = "",
        longitude = ""
    )

    constructor() : this(

        appId = StringUtils.getString(StringUtils.APP_ID),
        os = ANDROID_OS,
        osVersion = "",
        deviceId = "",
        mobile1 = "UNKNOWN",
        mobile2 = "UNKNOWN",
        model =  AppzillonUtils.getDeviceType(),
        make = AppzillonUtils.getDeviceMake(),
        screenResolution = "",
        appVersion = "",
        deviceName = "",
        latitude = "",
        longitude = ""
    )
}
