package com.iexceed.retrofitmvvm.data.model.mergeApi

data class AppzillonAppMasterRequest(
    val appId: String,
    val appVersion: String,
    val deviceId: String,
    val os: String,
    val updateAppVersion: String
)