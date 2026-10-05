package com.iexceed.retrofitmvvm.data.model.login

import androidx.annotation.Keep

data class ApzLoginRequest(
    val appzillonBody: AppzillonReqBody,
    val appzillonHeader: AppzillonReqHeader)

@Keep
data class AppzillonReqBody(
    val loginRequest: LoginReqRequest)


@Keep
data class AppzillonReqHeader(
    val appId: String,
    val async: Boolean,
    val deviceId: String,
    val interfaceId: String,
    val os: String,
    val requestId: String,
    val sessionId: Any,
    val source: String,
    val status: Boolean,
    val userId: String)


@Keep
data class LoginReqRequest(
    val appId: String,
    val controlsAccessType: String,
    val deviceId: String,
    val ifacesAccessType: String,
    val pwd: String,
    val scrsAccessType: String,
    val sysDate: String,
    val userId: String)

