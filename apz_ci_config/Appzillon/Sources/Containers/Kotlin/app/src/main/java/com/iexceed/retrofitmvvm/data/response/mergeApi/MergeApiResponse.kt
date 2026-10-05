package com.iexceed.retrofitmvvm.data.response.mergeApi

data class MergeApiResponse(
    val appzillonBody: AppzillonBody,
    val appzillonHeader: AppzillonHeader,
    val appzillonErrors: List<MergeApiErrorResponse>?
)

data class MergeApiEncryptedResponse(
    val appzillonBody: String,
    val appzillonHeader: String,
    val appzillonErrors: List<MergeApiErrorResponse>?,
    val appzillonSafe: String?,
    val appzillonQop :String?
)

/*
@Keep
data class AppzillonBody(
    val appzillonBody: AppzillonBody,
    val appzillonHeader: AppzillonHeader
)

@Keep
data class AppzillonHeader(
    val appzillonBody: AppzillonBody,
    val appzillonHeader: AppzillonHeader
)
*/
