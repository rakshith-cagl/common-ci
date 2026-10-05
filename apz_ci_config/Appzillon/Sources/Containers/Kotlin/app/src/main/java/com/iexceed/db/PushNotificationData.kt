package com.iexceed.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_notifications")
data class PushNotificationData (
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "timeStamp")val pushTimestamp: String,
    @ColumnInfo(name = "message") val pushMessage: String,
    @ColumnInfo(name = "readFlag") val pushMsgReadflag: String?
)
