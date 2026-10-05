package com.iexceed.retrofitmvvm.data.model.logout

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.iexceed.retrofitmvvm.data.response.appinstructions.AppzillonError

data class ApzServerCallRes(

    val appzillonBody: List<ApzServerCallArrayBody>,
    val appzillonHeader: ApzServerCallHeader,
    val appzillonErrors: List<AppzillonError>,
    val appzillonQop: String?
)

@Keep
data class ApzServerCallArrayBody(
    @SerializedName("COUNT(*)" ) var count : Int? = null
)