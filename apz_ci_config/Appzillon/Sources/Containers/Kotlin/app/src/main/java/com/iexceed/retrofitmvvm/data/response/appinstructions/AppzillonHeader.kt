package com.iexceed.retrofitmvvm.data.response.appinstructions

data class AppzillonHeader(
    val appId: String,
    val clientNonce: String,
    val deviceId: String,
    val interfaceId: String,
    val reqRefId: String,
    val requestId: String,
    val requestKey: String,
    val screenId: String,
    val serverNonce: String,
    val sessionId: String,
    val sessionToken: String,
    val source: String,
    val status: Boolean,
    val userId: String
)