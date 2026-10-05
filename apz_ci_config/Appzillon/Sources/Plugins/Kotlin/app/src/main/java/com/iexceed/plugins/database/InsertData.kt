package com.iexceed.plugins.database

data class InsertData(
    val dbVersion: Int,
    val tableName: String,
    val columns: List<String>,
    val data: List<String>
)