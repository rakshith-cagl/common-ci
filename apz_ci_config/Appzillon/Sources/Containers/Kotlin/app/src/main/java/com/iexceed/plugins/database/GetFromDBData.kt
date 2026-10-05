package com.iexceed.plugins.database

data class GetFromDBData(
    val dbVersion: Int,
    val tableName: String,
    val columns: List<String>,
    val condition: String,
)