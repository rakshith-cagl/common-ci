package com.iexceed.retrofitmvvm.data.model.multifactor

data class MultipartReqBody(
    val appzillonBody: MultiFactorHeader,
    val appzillonHeader: DeviceRegisterRequest
)