package com.iexceed.retrofitmvvm.data.model.login

import androidx.annotation.Keep

data class LoginResponse(
    val status: Boolean,
    val userDet: UserDet)

@Keep
data class UserDet(
    val otpRequired: String,
    val extId: String,
    val id: String,
    val lastLogin: String,
    val name: String,
    val profilePic: String)