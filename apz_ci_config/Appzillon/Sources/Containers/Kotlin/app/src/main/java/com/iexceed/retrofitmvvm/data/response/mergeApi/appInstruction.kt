package com.iexceed.retrofitmvvm.data.response.mergeApi

data class appInstruction(
    val appId: String,
    val appVersion: String,
    val containerApp: String,
    val expired: String,
    val expiryDate: String,
    val otaReq: String,
    val parentAppId: String,
    val remoteDebug: String,
    val updateAction: String,
    val wipeout: String
)
