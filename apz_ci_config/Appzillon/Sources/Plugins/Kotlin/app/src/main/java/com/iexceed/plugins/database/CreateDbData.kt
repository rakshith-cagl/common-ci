package com.iexceed.plugins.database

data class CreateDbData(
    val data: List<String>,
    val columns: List<String>,
    val dbVersion: Int,
    val operation: String,
    val primaryKey: String,
    val tableName: String
)