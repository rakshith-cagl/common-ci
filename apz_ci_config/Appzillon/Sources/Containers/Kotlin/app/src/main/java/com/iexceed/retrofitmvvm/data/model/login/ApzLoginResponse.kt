package com.iexceed.retrofitmvvm.data.model.login

import androidx.annotation.Keep
import com.iexceed.retrofitmvvm.data.response.appinstructions.AppzillonError

data class ApzLoginResponse(
    val appzillonHeader: AppzillonHeader,
    val appzillonBody: AppzillonBody,
    val appzillonErrors: List<AppzillonError>)

@Keep
data class AppzillonBody(val loginResponse: LoginResponse)


@Keep
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