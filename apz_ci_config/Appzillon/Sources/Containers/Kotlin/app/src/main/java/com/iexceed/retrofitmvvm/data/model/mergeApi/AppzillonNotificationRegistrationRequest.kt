package com.iexceed.retrofitmvvm.data.model.mergeApi

data class AppzillonNotificationRegistrationRequest(
    val appId: String,
    val deviceId: String,
    val deviceName: String,
    val osId: String,
    val osVersion: String,
    val regId: String
)