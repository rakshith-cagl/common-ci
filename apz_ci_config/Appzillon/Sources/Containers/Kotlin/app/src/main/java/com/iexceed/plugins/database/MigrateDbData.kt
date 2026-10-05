package com.iexceed.plugins.database

data class MigrateDbData (
    val operation: String,
    val oldDbVersion: Int,
    val newDbVersion: Int,
    val tableName: String,
    val newTableName: String,
    val columns: List<String>,
    val data: List<String>
)