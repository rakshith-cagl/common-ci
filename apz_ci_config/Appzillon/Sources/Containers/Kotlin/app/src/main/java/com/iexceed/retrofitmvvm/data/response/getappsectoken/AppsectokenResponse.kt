package com.iexceed.retrofitmvvm.data.response.getappsectoken

import com.iexceed.retrofitmvvm.data.model.getappsectoken.GetAppSceErrorResponse

data class AppsectokenResponse(
    val appzillonBody: AppzillonBody,
    val appzillonHeader: AppzillonHeader,
    val appzillonErrors: List<GetAppSceErrorResponse>?
)

data class EncryptedAppsectokenResponse(
    val appzillonBody: String,
    val appzillonHeader: String,
    val appzillonErrors: String?,
    val appzillonSafe: String?
)