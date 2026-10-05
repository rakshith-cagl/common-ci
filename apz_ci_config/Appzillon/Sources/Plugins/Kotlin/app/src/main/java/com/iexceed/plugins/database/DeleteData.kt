package com.iexceed.plugins.database

data class DeleteData(
    val dbVersion: Int,
    val tableName: String,
    val columns: List<String>,
    val condition: String,
    val data: List<String>
)