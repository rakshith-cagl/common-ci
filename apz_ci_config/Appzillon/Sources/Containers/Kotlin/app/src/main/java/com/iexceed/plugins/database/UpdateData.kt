package com.iexceed.plugins.database

data class UpdateData(
    val dbVersion: Int,
    val tableName: String,
    val condition: String,
    val columns: List<String>,
    val data: List<String>
)