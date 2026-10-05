package com.iexceed.plugins.calendar

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract.Events
import android.util.Log
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Search(val activity: ApzActivity<*>) {

    val TAG = "SearchEvents"

    fun searchEvents(searchTitle: String?): List<JSONObject> {
        ApzLogger.i(TAG, "search")
        val searchedEvents: MutableList<JSONObject> = ArrayList()
        val projection =
            arrayOf(Events._ID, Events.TITLE, Events.DTSTART, Events.DTEND, Events.DESCRIPTION)
        val selection = "( (" + Events.TITLE + " LIKE ?) AND ( deleted != 1 ) )"
        val calCursor: Cursor? = activity.contentResolver.query(
            Events.CONTENT_URI,
            projection,
            selection,
            arrayOf(searchTitle),
            Events._ID + " ASC"
        )

        try {
            if (calCursor!!.moveToFirst()) {
                do {
                    val eventsJson = JSONObject()
                    val eventIdStr = calCursor.getColumnIndex(Events._ID)
                    val eventId: String = calCursor.getString(eventIdStr)
                    val sDateIdx = calCursor.getColumnIndex(Events.DTSTART)
                    val startDateTime: String = calCursor.getString(sDateIdx)
                    val eDateIdx = calCursor.getColumnIndex(Events.DTEND)
                    val endDateTime: String = calCursor.getString(eDateIdx)
                    eventsJson.put("eventId", eventId) // require to delete
                    // require to edit
                    eventsJson.put("startDateTime", startDateTime)
                    eventsJson.put("endDateTime", endDateTime)
                    searchedEvents.add(eventsJson)
                } while (calCursor.moveToNext())
            }
        } catch (e: JSONException) {
            //Sonar fix
        } finally {
            calCursor!!.close()
        }
        ApzLogger.i(TAG, "SEARCH : $searchedEvents")
        return searchedEvents
    }

    fun getCalendarId(): Int {
        val calendarIdTable: Hashtable<String, String> = Hashtable()
        try {
//            if (haveCalendarReadWritePermissions(context as Activity?)) {
            val projection = arrayOf("_id", "calendar_displayName")
            val calendars: Uri = Uri.parse("content://com.android.calendar/calendars")
            val contentResolver: ContentResolver = activity!!.contentResolver
            val managedCursor: Cursor? =
                contentResolver.query(calendars, projection, null, null, null)
            if (managedCursor!!.moveToFirst()) {
                var calName: String
                var calID: String
                var cont = 0
                val nameCol: Int = managedCursor.getColumnIndex(projection[1])
                val idCol: Int = managedCursor.getColumnIndex(projection[0])

                do {
                    calName = managedCursor.getString(nameCol)
                    calID = managedCursor.getString(idCol)
                    Log.v(TAG, "CalendarName:$calName ,id:$calID")
                    calendarIdTable[calName] = calID
                    cont++
                } while (managedCursor.moveToNext())
                managedCursor.close()
//                return calendarIdTable
            }
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var calendarId = 0
        try{
            val keys = calendarIdTable!!.keys
            val key = keys.elementAt(0)
            calendarId = calendarIdTable[key]!!.toInt()
        }catch(e : Exception){
            //Sonar fix
        }
        if(calendarId == 0)
            calendarId = 3
        return calendarId
    }
}