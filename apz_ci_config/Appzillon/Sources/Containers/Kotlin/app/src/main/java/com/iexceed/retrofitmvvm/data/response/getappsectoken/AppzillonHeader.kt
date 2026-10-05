package com.iexceed.retrofitmvvm.data.response.getappsectoken

data class AppzillonHeader(
    val appId: String,
    val clientNonce: String,
    val deviceId: String,
    val interfaceId: String,
    val reqRefId: String,
    val requestId: String,
    val requestKey: String,
    val screenId: String,
    val sessionId: String,
    val source: String,
    val status: Boolean,
    val userId: String
)