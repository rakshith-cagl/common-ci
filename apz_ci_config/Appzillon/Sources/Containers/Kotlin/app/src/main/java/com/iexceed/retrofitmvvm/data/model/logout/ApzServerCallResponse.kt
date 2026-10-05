package com.iexceed.retrofitmvvm.data.model.logout

import androidx.annotation.Keep
import com.iexceed.retrofitmvvm.data.response.appinstructions.AppzillonError

data class ApzServerCallResponse(
    val appzillonBody: ApzServerCallBody,
    val appzillonHeader: ApzServerCallHeader,
    val appzillonErrors: List<AppzillonError>,
    val appzillonQop: String?
)

@Keep
data class ApzServerCallBody(
    val status: Boolean
)


@Keep
data class ApzServerCallHeader(
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