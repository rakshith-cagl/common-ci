package com.iexceed.retrofitmvvm.data.model.changepassword

import androidx.annotation.Keep
import com.iexceed.retrofitmvvm.data.response.appinstructions.AppzillonError

data class ChangePasswordResponse(
    val appzillonBody: ApzResponseBody,
    val appzillonHeader: ApzResponseHeader,
    val appzillonErrors: List<AppzillonError>
)

@Keep
data class ApzResponseBody(
    val changePasswordResponse: ChangePasswordResponseX
)

@Keep
data class ApzResponseHeader(
    val appId: String,
    val clientNonce: String,
    val deviceId: String,
    val interfaceId: String,
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