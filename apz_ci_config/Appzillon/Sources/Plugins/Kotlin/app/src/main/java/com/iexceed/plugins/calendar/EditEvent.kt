package com.iexceed.plugins.calendar

import android.content.ContentValues
import android.provider.CalendarContract.Events
import android.provider.CalendarContract.Reminders
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class EditEvent(val mCallbackId: String?, val activity: ApzActivity<*>, val webView: WebView, val apzPluginUtil: IapzPluginUtil) {

    val TAG = "EditEvent"

    fun edit(calValues: Map<String, String>, dateFormat: String) {
        ApzLogger.i(TAG, "edit")
        if (getEditCount(calValues, dateFormat) == 1) {
            apzPluginUtil.sendSuccess(mCallbackId, null, false, activity, webView, true)
        } else {
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-039", null, activity, webView, true)
        }
    }

    fun getEditCount(calValues: Map<String, String>, dateFormat: String): Int {
        ApzLogger.i(TAG, "getEditCount")
        val eventsSearchResult = Search(activity).searchEvents(calValues["title"])
        if (eventsSearchResult.isEmpty()) {
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-039", null, activity, webView, true)
            return 0  // not found
        }
        /* check for startNewDate and endNewDate */
        val newStartDateTime: Date?
        val newEndDateTime: Date?
        val sdf = SimpleDateFormat(dateFormat + "HH:mm:ss", Locale.US)
        try {
            newStartDateTime =
                sdf.parse(calValues["newStartDate"].toString() + " " + calValues["newStartTime"])
            newEndDateTime =
                sdf.parse(calValues["newEndDate"].toString() + " " + calValues["newEndTime"])
        } catch (e: ParseException) {
            return 0
        }
        if (newEndDateTime < newStartDateTime) {
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-086", null, activity, webView, true)
            return 0
        }
        /* date check end */
        var eventId = ""
        /* if there is a single events with the given title, then update it */
        val startDateTime: Date?
        val endDateTime: Date?
        try {
            startDateTime =
                sdf.parse(calValues["startDate"].toString() + " " + calValues["startTime"])
            endDateTime = sdf.parse(calValues["endDate"].toString() + " " + calValues["endTime"])
        } catch (e: ParseException) {
            return 0
        }
        if (endDateTime <= startDateTime) {
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-086", null, activity, webView, true)
            return 0
        }
        var multipleEvents = 0
        var idToBeUpdated = ""
        for (i in eventsSearchResult.indices) {
            try {
                val id = eventsSearchResult[i].getString("eventId")
                val searchResultStartDateTime =
                    Date(eventsSearchResult[i].getString("startDateTime").toLong())
                val searchResultEndDateTime =
                    Date(eventsSearchResult[i].getString("endDateTime").toLong())
                // compare their startDate end endDate
                if (startDateTime.time == searchResultStartDateTime.time && endDateTime.time == searchResultEndDateTime.time) {
                    // same exist
                    eventId = eventsSearchResult[i].getString("eventId")
                    multipleEvents++
                    idToBeUpdated = id
                }
            } catch (e: JSONException) {
                return 0
            }
        }
        return checkStatusOfTheEvent(multipleEvents, idToBeUpdated, eventId, calValues, dateFormat)
    }

    private fun checkStatusOfTheEvent(
        multipleEvents: Int,
        idToBeUpdated: String,
        eventId: String,
        calValues: Map<String, String>,
        dateFormat: String
    ) = if (multipleEvents == 1 && idToBeUpdated != "") {
        // update it
        if (update(eventId, calValues, dateFormat)) {
            1
        } else {
            0
        }
    } else {
        // multiple events found with given criteria
        0
    }

    /**
     * to update events
     *
     * @param eventId
     */
    private fun update(
        eventId: String,
        newValues: Map<String, String>,
        dateFormat: String
    ): Boolean {
        ApzLogger.i(TAG, "update : $eventId")
        val sdf = SimpleDateFormat(dateFormat + "HH:mm:ss", Locale.US)
        val sDate: Date? =
            sdf.parse(newValues["newStartDate"].toString() + " " + newValues["newStartTime"])
        val eDate: Date? =
            sdf.parse(newValues["newEndDate"].toString() + " " + newValues["newEndTime"])
        val values = ContentValues()
        values.put(Events.DTSTART, sDate?.time)
        values.put(Events.DTEND, eDate?.time)
        when (newValues["recurrence"]) {
            "Daily" -> {
                values.put(Events.RRULE, "FREQ=DAILY")
            }
            "Weekly" -> {
                values.put(Events.RRULE, "FREQ=WEEKLY")
            }
            "Monthly" -> {
                values.put(Events.RRULE, "FREQ=MONTHLY")
            }
        }
        values.put(Events.TITLE, newValues["title"])
        values.put(Events.EVENT_LOCATION, newValues["location"])
        values.put(Events.CALENDAR_ID, Search(activity).getCalendarId())
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().displayName)
        values.put(Events.DESCRIPTION, newValues["summary"])
        // reasonable defaults exist:
        values.put(Events.GUESTS_CAN_MODIFY, 1)
        values.put(Events.HAS_ALARM, 1)
        val eventUpdated = activity.contentResolver.update(
            Events.CONTENT_URI,
            values,
            Events._ID + " =? ",
            arrayOf(eventId)
        )
        ApzLogger.i(TAG, "eventUpdated:$eventUpdated")
        values.clear()
        values.put(Reminders.EVENT_ID, eventId)
        when {
            newValues["alarm"].equals("5M", ignoreCase = true) -> {
                values.put(Reminders.MINUTES, 5)
            }
            newValues["alarm"].equals("15M", ignoreCase = true) -> {
                values.put(Reminders.MINUTES, 15)
            }
            newValues["alarm"].equals("1H", ignoreCase = true) -> {
                values.put(Reminders.MINUTES, 60)
            }
            newValues["alarm"].equals("1D", ignoreCase = true) -> {
                values.put(Reminders.MINUTES, 60 * 24)
            }
        }
        val reminderUpdated: Int = activity.contentResolver.update(
            Reminders.CONTENT_URI,
            values,
            Reminders.EVENT_ID + " =? ",
            arrayOf(eventId)
        )
        ApzLogger.i(TAG, "Update Events:$eventUpdated reminderUpdated:$reminderUpdated")
        return eventUpdated > 0 && reminderUpdated > 0
    }
}