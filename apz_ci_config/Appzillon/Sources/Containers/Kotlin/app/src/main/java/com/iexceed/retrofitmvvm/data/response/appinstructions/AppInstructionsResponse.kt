package com.iexceed.retrofitmvvm.data.response.appinstructions

data class AppInstructionsResponse(
    val appzillonBody: AppzillonBody,
    val appzillonErrors: List<AppzillonError>,
    val appzillonHeader: AppzillonHeader
)