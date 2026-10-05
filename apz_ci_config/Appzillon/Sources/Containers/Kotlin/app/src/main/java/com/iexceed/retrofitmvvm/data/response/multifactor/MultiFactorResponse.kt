package com.iexceed.retrofitmvvm.data.response.multifactor

data class MultiFactorResponse(
    val appzillonBody: AppzillonBody,
    val appzillonHeader: AppzillonHeader
    //val appzillonErrors: MultiFactorErrorResponse?=null
)