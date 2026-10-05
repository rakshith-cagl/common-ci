package com.iexceed.retrofitmvvm.data.model.getappsectoken

data class AppzillonHeader(
    val appId: String,
    val async: Boolean,
    val clientNonce: String,
    val deviceId: String,
    val interfaceId: String,
    val origination: String,
    val os: String,
    val screenId: String,
    val sessionId: String,
    val signature: String,
    val source: String,
    val status: Boolean,
    val userId: String
)
