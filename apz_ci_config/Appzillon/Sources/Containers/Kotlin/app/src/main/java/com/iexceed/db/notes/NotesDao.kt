package com.iexceed.db.notes

import androidx.room.*
import com.iexceed.db.RoomAppDb.Companion.TABLE_NAME_NOTES

@Dao
interface NotesDao
{
    @Query("SELECT * FROM $TABLE_NAME_NOTES ORDER BY txn_no DESC")
    fun getAllNotes(): List<NotesData>?

    @Query("SELECT * FROM $TABLE_NAME_NOTES WHERE txn_no =:aTaxNumber")
    fun getNoteByTxn(aTaxNumber: String): NotesData?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNotes(aNotes: NotesData)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateNotes(aNotes: NotesData)

    @Query("DELETE FROM $TABLE_NAME_NOTES")
    fun deleteNotes():Int
}