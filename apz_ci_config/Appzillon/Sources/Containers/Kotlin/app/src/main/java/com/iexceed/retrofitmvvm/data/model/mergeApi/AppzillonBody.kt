package com.iexceed.retrofitmvvm.data.model.mergeApi

data class AppzillonBody(
    val appzillonAppMasterRequest: AppzillonAppMasterRequest,
    val appzillonGetAppSecTokensRequest: AppzillonGetAppSecTokensRequest,
    val appzillonNotificationRegistrationRequest: AppzillonNotificationRegistrationRequest,
    val deviceRegisterRequest: DeviceRegisterRequest
)

data class AppzillonEncryptedBody(
    val appzillonAppMasterRequest: String?,
    val appzillonGetAppSecTokensRequest: String?,
    val appzillonNotificationRegistrationRequest: String?,
    val deviceRegisterRequest: String?
)