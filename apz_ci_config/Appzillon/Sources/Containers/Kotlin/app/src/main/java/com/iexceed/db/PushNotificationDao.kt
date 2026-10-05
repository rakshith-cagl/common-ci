package com.iexceed.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.iexceed.db.RoomAppDb.Companion.PUSH_NOTIFICATION_TABLE_NAME

@Dao
interface PushNotificationDao
{
    @Query("SELECT * FROM $PUSH_NOTIFICATION_TABLE_NAME ORDER BY timeStamp DESC")
    fun getAllUserInfo(): List<PushNotificationData>?

    @Query("SELECT * FROM $PUSH_NOTIFICATION_TABLE_NAME WHERE timeStamp =:atime")
    fun findPushNotificationById(atime: String): PushNotificationData?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPushNotification(aData: PushNotificationData?)

    @Delete
    fun deletePushNotification(aData: PushNotificationData?)

    @RawQuery()
    fun updateDataRawQuery(query: SupportSQLiteQuery):Int
}