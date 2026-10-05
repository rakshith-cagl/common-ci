package com.iexceed.retrofitmvvm.data.response.mergeApi

data class AppzillonBody(
    val appzillonAppMasterResponse: AppzillonAppMasterResponse,
    val appzillonGetAppSecTokensResponse: AppzillonGetAppSecTokensResponse,
    val appzillonNotificationRegistrationResponse: AppzillonNotificationRegistrationResponse?,
    val deviceRegisterResponse: DeviceRegisterResponse?
)