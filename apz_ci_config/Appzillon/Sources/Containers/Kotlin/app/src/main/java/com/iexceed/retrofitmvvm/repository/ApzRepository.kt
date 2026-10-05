package com.iexceed.retrofitmvvm.repository

import com.iexceed.common.LaunchMergedInterface
import com.iexceed.common.MultifactorRegister
import com.iexceed.common.StringUtils.getString
import com.iexceed.retrofitmvvm.data.api.ApiService
import javax.inject.Inject

class ApzRepository @Inject constructor(
    private val apiService: ApiService,
    private val multifactorRegister: MultifactorRegister,
    private val launchMergedInterface: LaunchMergedInterface
) {
    fun doMultiFactorRegistration() {
        val multiFactorResponseBody =
            apiService.getMultiFactorReq(multifactorRegister.prepareRequestBody())
        multifactorRegister.processMultiFactorResponseBody(multiFactorResponseBody)
    }

    fun doAppzillonOnAppLaunch() {
        val lPayLoadEncryption = getString("payloadEncryption")
        if ("Y".equals(lPayLoadEncryption, ignoreCase = true)) {
            val mergeApiEncryptedResponse =
                apiService.getMergeApiReqEncrypted(launchMergedInterface.getFinalEncryptedRequest())
            launchMergedInterface.processEncryptedResponse(mergeApiEncryptedResponse)
        } else {
            val mergeApiResponse =
                apiService.getMergeApiReq(launchMergedInterface.getFinalRequest())
            launchMergedInterface.processResponse(mergeApiResponse)
        }
    }
}