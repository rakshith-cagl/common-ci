package com.iexceed.retrofitmvvm.data.model.mergeApi

data class AppzillonHeader(
    val appId: String,
    val appLaunch: String,
    val clientNonce: String,
    val deviceId: String,
    val interfaceId: String,
    val latitude: String,
    val longitude: String,
    val origination: String,
    val preLogin: String,
    val requestKey: String,
    val screenId: String,
    val serverNonce: String,
    val sessionId: String,
    val sessionToken: String,
    val signature: String,
    val source: String,
    val status: Boolean,
    val userId: String
)