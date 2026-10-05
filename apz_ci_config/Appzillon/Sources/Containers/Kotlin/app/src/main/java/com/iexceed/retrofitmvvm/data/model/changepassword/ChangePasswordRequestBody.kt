package com.iexceed.retrofitmvvm.data.model.changepassword

import androidx.annotation.Keep

data class ChangePasswordRequestBody(
    val appzillonBody: AppzillonBody,
    val appzillonHeader: AppzillonHeader,
    var encMode: Int
)

@Keep
data class AppzillonBody(
    val changePasswordRequest: ChangePasswordRequest
)


@Keep
data class AppzillonHeader(
    val appId: String,
    val async: Boolean,
    val deviceId: String,
    val interfaceId: String,
    val os: String,
    val requestId: String,
    val sessionId: String,
    val source: String,
    val status: Boolean,
    val userId: String
)

@Keep
data class ChangePasswordRequest(
    val appId: String,
    val confirmPassword: String,
    val deviceId: String,
    val newPassword: String,
    val pwd: String,
    val sysDate: String,
    val userId: String
)