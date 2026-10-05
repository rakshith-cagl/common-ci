package com.iexceed.retrofitmvvm.data.model.mergeApi

data class DeviceRegisterRequest(
    val appId: String,
    val appVersion: String,
    val deviceId: String,
    val deviceName: String,
    val latitude: String,
    val longitude: String,
    val make: String,
    val mobile1: String,
    val mobile2: String,
    val model: String,
    val os: String,
    val osVersion: String,
    val screenResolution: String
)