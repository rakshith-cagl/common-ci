package com.iexceed.retrofitmvvm.data.model.changepassword

data class ApzChangePasswordEncryptionRequest(
    val appzillonQop :String?,
    val appzillonBody: String,
    val appzillonHeader: String,
    val appzillonSafe: String,
    val encMode: Int,
    val safeBit: Int)