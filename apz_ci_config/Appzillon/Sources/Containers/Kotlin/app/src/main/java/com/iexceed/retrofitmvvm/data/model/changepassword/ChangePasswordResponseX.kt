package com.iexceed.retrofitmvvm.data.model.changepassword

data class ChangePasswordResponseX(
    val communication: Communication,
    val channel: String,
    val message: String,
    val status: String
)