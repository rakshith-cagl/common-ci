package com.iexceed.retrofitmvvm.data.model.mergeApi

data class MergeApiReqBody(
    val appzillonBody: AppzillonBody,
    val appzillonHeader: AppzillonHeader
)

data class MergeApiEncryptedReqBody(
    val appzillonQop :String?,
    val appzillonBody: String?,
    val appzillonHeader: String?,
    val appzillonSafe: String?,
    val appzillonSafeBit: Int,
    val encMode: Int
)