package com.iexceed.retrofitmvvm.data.model.changepassword

import androidx.annotation.Keep
import com.iexceed.retrofitmvvm.data.response.appinstructions.AppzillonError

data class ApzChangePasswordDecryptedResponse(
    val appzillonBody: ApzChangePasswordBody,
    val appzillonHeader: ApzChangePasswordHeader,
    val appzillonErrors: List<AppzillonError>,
    val appzillonQop: String
)

@Keep
data class ApzChangePasswordBody(
    val changePasswordResponse: ChangePasswordResponseX
)

@Keep
data class ApzChangePasswordHeader(
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