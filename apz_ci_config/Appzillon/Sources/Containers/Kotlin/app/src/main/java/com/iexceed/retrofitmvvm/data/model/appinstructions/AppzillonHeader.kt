package com.iexceed.retrofitmvvm.data.model.appinstructions

data class AppzillonHeader(
    val appId: String,
    val deviceId: String,
    val interfaceId: String,
    val origination: String,
    val preLogin: String,
    val requestKey: String,
    val screenId: String,
    val sessionId: String,
    val source: String,
    val status: Boolean,
    val userId: String
)