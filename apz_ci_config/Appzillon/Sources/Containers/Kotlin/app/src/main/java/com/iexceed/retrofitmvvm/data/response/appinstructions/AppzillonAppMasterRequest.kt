package com.iexceed.retrofitmvvm.data.response.appinstructions

data class AppzillonAppMasterRequest(
    val appId: String,
    val appVersion: String,
    val deviceId: String,
    val os: String,
    val updateAppVersion: String
)