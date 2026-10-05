package com.iexceed.retrofitmvvm.data.response.getappsectoken

data class AppzillonGetAppSecTokensResponse(
    val safeToken: String,
    val serverNonce: String,
    val sessionToken: String,
    val status: String
)