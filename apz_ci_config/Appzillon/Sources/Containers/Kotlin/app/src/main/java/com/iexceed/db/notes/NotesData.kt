package com.iexceed.db.notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iexceed.db.RoomAppDb

@Entity(tableName = RoomAppDb.TABLE_NAME_NOTES)
data class NotesData (
    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "txn_no") val txnNo: String,
    @ColumnInfo(name = "note") val note: String

)
