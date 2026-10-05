package com.iexceed.plugins.calendar

import android.content.ContentValues
import android.net.Uri
import android.provider.CalendarContract.Events
import android.provider.CalendarContract.Reminders
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class CreateEvent(val mCallbackId: String?, val activity: ApzActivity<*>, val webView: WebView) {

    val TAG = "CreateEvent"

    fun create(calValues: Map<String, String>, dateFormat: String): Int {
        ApzLogger.i(TAG, "create")

        var multipleEvents = 0
        val eventsSearchResult = Search(activity).searchEvents(calValues["title"])
        val startDateTime: Date?
        val endDateTime: Date?
        val sdf = SimpleDateFormat(dateFormat + "HH:mm:ss", Locale.US)
        try {
            startDateTime =
                sdf.parse(calValues["startDate"].toString() + " " + calValues["startTime"])
            endDateTime = sdf.parse(calValues["endDate"].toString() + " " + calValues["endTime"])
        } catch (e: ParseException) {
            ApzLogger.e(TAG, "APZ-CNT-014$e")
            return 0
        }
        if (endDateTime < startDateTime) {
            ApzLogger.e(TAG, "Date Check : sDate is after eDate")
            return 0
        }
        multipleEvents = countEvents(eventsSearchResult, startDateTime, endDateTime, multipleEvents)
        if (multipleEvents < 1) {
            val values = ContentValues()
            values.put(Events.DTSTART, startDateTime.time)
            val recurrenceEndDate = calValues["recurrenceEndDate"]
            val recurrence = calValues["recurrence"]
            if (recurrence.equals("", ignoreCase = true) || recurrence.equals(
                    "None",
                    ignoreCase = true
                )
            ) {
                values.put(Events.DTEND, endDateTime.time)
            } else {
                if (recurrenceEndDate.equals("", ignoreCase = true)) {
                    updateRecurrence(recurrence, values)
                } else {
                    try {
                        updateGivenRecurrenceDate(sdf, calValues, recurrence, values)
                    } catch (e: ParseException) {
                        ApzLogger.i(TAG, "APZ-CNT-130")
                        return 0
                    }
                }
                values.put("duration", "P15M")
            }
            val newEventId: Long = updateOptions(values, calValues)
            val uriRem: Uri? =
                activity.contentResolver.insert(Reminders.CONTENT_URI, values)
            val newEventRemId: Long = uriRem!!.lastPathSegment!!.toLong()
            return getStatusOfTheEvent(newEventId, newEventRemId)
        }
        return 0
        //Multiple Events Send Error
    }

    private fun getStatusOfTheEvent(newEventId: Long, newEventRemId: Long) =
        if (newEventId > 0 && newEventRemId > 0) {
            1
        } else {
            0
        }

    private fun updateOptions(
        values: ContentValues,
        calValues: Map<String, String>
    ): Long {
        values.put(Events.TITLE, calValues["title"])
        values.put(Events.EVENT_LOCATION, calValues["location"])

        values.put(Events.CALENDAR_ID, Search(activity).getCalendarId())
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        values.put(Events.DESCRIPTION, calValues["summary"])
        values.put(Events.GUESTS_CAN_MODIFY, 1)
        values.put(Events.HAS_ALARM, 1)
        val uri: Uri? =
            activity.contentResolver.insert(Events.CONTENT_URI, values)
        val newEventId: Long = uri!!.lastPathSegment!!.toLong()
        ApzLogger.i(TAG, "newEventId:$newEventId")
        values.clear()
        values.put(Reminders.METHOD, Reminders.METHOD_ALERT)
        values.put(Reminders.EVENT_ID, newEventId)
        when {
            calValues["alarm"].equals("5M", ignoreCase = true) -> {
                values.put(Reminders.MINUTES, 5)
            }
            calValues["alarm"].equals("15M", ignoreCase = true) -> {
                values.put(Reminders.MINUTES, 15)
            }
            calValues["alarm"].equals("1H", ignoreCase = true) -> {
                values.put(Reminders.MINUTES, 60)
            }
            calValues["alarm"].equals("1D", ignoreCase = true) -> {
                values.put(Reminders.MINUTES, 60 * 24)
            }
        }
        return newEventId
    }

    private fun updateGivenRecurrenceDate(
        sdf: SimpleDateFormat,
        calValues: Map<String, String>,
        recurrence: String?,
        values: ContentValues
    ) {
        val rEndDate: Date? =
            sdf.parse(calValues["recurrenceEndDate"].toString() + " " + calValues["endTime"])
        val sdfR = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        var dateUntil: String = sdfR.format(rEndDate!!.time)
        dateUntil = dateUntil.replace("-", "").replace(":", "")
        ApzLogger.i(TAG, dateUntil)
        when {
            recurrence.equals("Daily", ignoreCase = true) -> {
                values.put(
                    Events.RRULE,
                    "FREQ=DAILY;UNTIL=$dateUntil"
                )
            }
            recurrence.equals("Weekly", ignoreCase = true) -> {
                values.put(
                    Events.RRULE,
                    "FREQ=WEEKLY;UNTIL=$dateUntil"
                )
            }
            recurrence.equals("Monthly", ignoreCase = true) -> {
                values.put(
                    Events.RRULE,
                    "FREQ=MONTHLY;UNTIL=$dateUntil"
                )
            }
            recurrence.equals("Yearly", ignoreCase = true) -> {
                values.put(
                    Events.RRULE,
                    "FREQ=YEARLY;UNTIL=$dateUntil"
                )
            }
        }
    }

    private fun updateRecurrence(recurrence: String?, values: ContentValues) {
        when {
            recurrence.equals("Daily", ignoreCase = true) -> {
                values.put(Events.RRULE, "FREQ=DAILY")
            }
            recurrence.equals("Weekly", ignoreCase = true) -> {
                values.put(Events.RRULE, "FREQ=WEEKLY")
            }
            recurrence.equals("Monthly", ignoreCase = true) -> {
                values.put(Events.RRULE, "FREQ=MONTHLY")
            }
            recurrence.equals("Yearly", ignoreCase = true) -> {
                values.put(Events.RRULE, "FREQ=YEARLY")
            }
        }
    }

    private fun countEvents(
        eventsSearchResult: List<JSONObject>,
        startDateTime: Date,
        endDateTime: Date,
        multipleEvents: Int
    ): Int {
        var multipleEvents1 = multipleEvents
        if (eventsSearchResult.isNotEmpty()) {
            for (i in eventsSearchResult.indices) {
                try {
                    val searchResultStartDateTime =
                        Date(eventsSearchResult[i].getString("startDateTime").toLong())
                    val searchResultEndDateTime =
                        Date(eventsSearchResult[i].getString("endDateTime").toLong())
                    // compare their startDate end endDate
                    if (startDateTime.time == searchResultStartDateTime.time && endDateTime.time == searchResultEndDateTime.time) {
                        // same exist
                        multipleEvents1++
                    }
                } catch (e: JSONException) {
                    return 0
                }
            }
        }
        return multipleEvents1
    }


}