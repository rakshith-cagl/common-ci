package com.iexceed.retrofitmvvm.data.response.multifactor

data class AppzillonHeader(
    val appId: String?=null,
    val clientNonce: String?=null,
    val deviceId: String?=null,
    val interfaceId: String?=null,
    val reqRefId: String,
    val requestId: String?=null,
    val requestKey: String?=null,
    val screenId: String?=null,
    val serverNonce: String?=null,
    val sessionId: String?=null,
    val sessionToken: String?=null,
    val source: String?=null,
    val status: Boolean,
    val userId: String?=null
)