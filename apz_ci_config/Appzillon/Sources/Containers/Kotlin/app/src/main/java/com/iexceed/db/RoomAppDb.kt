package com.iexceed.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.iexceed.db.notes.NotesDao
import com.iexceed.db.notes.NotesData

@Database(entities = [PushNotificationData::class, NotesData::class], version = 1, exportSchema = false)
abstract class RoomAppDb: RoomDatabase()
{
    abstract fun pushMsgDao(): PushNotificationDao
    abstract fun notesDao(): NotesDao

    fun addPushMsg(aNotes: PushNotificationData) {
        pushMsgDao().insertPushNotification(aNotes)
    }

    fun delete(aTableName: String, aWhere: String): Boolean {
        return try {
            val lDb = INSTANCE?.openHelper?.writableDatabase
            val int = lDb?.delete(aTableName, aWhere, null)
            !(int != null && int < 0)
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }

    companion object
    {
        private var INSTANCE: RoomAppDb? = null
        const val PUSH_NOTIFICATION_TABLE_NAME = "tb_notifications"
        const val TABLE_NAME_NOTES = "Notes"
		private const val ROOM_DB_NAME = "APZ_ROOM_DB"


        fun getAppDatabase(context: Context): RoomAppDb
        {
            if (INSTANCE == null)
            {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    RoomAppDb::class.java, ROOM_DB_NAME)
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }

        fun getDb(context: Context): SupportSQLiteDatabase
        {
            val instance = getAppDatabase(context)
            return instance.openHelper.writableDatabase
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}