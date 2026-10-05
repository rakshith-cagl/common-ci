package com.iexceed.retrofitmvvm.data.model.getappsectoken

data class AppzillonBody(
    val appzillonGetAppSecTokensRequest: AppzillonGetAppSecTokensRequest
)

data class AppzillonEncryptedBody(
    val appzillonGetAppSecTokensRequest: String?
)