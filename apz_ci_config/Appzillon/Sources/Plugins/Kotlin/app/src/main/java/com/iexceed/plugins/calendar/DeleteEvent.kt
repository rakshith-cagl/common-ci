package com.iexceed.plugins.calendar

import android.provider.CalendarContract.Events
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class DeleteEvent(val mCallbackId: String?, val activity: ApzActivity<*>, val webView: WebView, val apzPluginUtil: IapzPluginUtil) {

    val TAG = "DeleteEvent"

    /**
     * Deletes events
     *
     * @param mJsonObj
     */
    fun delete(mJsonObj: JSONObject) {
        ApzLogger.i(TAG, "Delete")


        if (getDeletedCount(mJsonObj) == 1) {
            apzPluginUtil.sendSuccess(mCallbackId, null, false, activity, webView, true)
        } else {
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-038", null, activity, webView, true)
        }
    }

    fun getDeletedCount(mJsonObj: JSONObject): Int {
        ApzLogger.i(TAG, "Delete")
        val dateFormat: String
        val title: String
        val startDate: String
        val endDate: String
        val startTime: String
        val endTime: String
        try {
            dateFormat = mJsonObj.getString("dateFormat")
            title = mJsonObj.getString("title").trim { it <= ' ' }
            startDate = mJsonObj.getString("startDate").trim { it <= ' ' }
            endDate = mJsonObj.getString("endDate").trim { it <= ' ' }
            startTime = mJsonObj.getString("startTime").trim { it <= ' ' }
            endTime = mJsonObj.getString("endTime").trim { it <= ' ' }
        } catch (e: JSONException) {
            ApzLogger.i(TAG, "APZ-CNT-077$e")
            return 0
        } catch (e: java.lang.Exception) {
            return 0
        }
        var deleted = 0
        val eventsSearchResult = Search(activity).searchEvents(title)
        if (eventsSearchResult.isEmpty()) {
            return 0
        }
        val sdf = SimpleDateFormat(dateFormat + "HH:mm:ss", Locale.US)
        var startDateTime: Date?
        var endDateTime: Date?
        var searchResultStartDateTime: Date
        var searchResultEndDateTime: Date
        var searchResulteventId: String
        for (i in eventsSearchResult.indices) {
            try {
                searchResulteventId = eventsSearchResult[i].getString("eventId")
                searchResultStartDateTime =
                    Date(eventsSearchResult[i].getString("startDateTime").toLong())
                searchResultEndDateTime =
                    Date(eventsSearchResult[i].getString("endDateTime").toLong())
                startDateTime = sdf.parse("$startDate $startTime")
                endDateTime = sdf.parse("$endDate $endTime")
                ApzLogger.i(
                    TAG,
                    "sDateTime:$searchResultStartDateTime   eDateTime:$searchResultEndDateTime"
                )
                // compare their startDate end endDate
                if (startDateTime.time == searchResultStartDateTime.time && endDateTime.time == searchResultEndDateTime.time) {

                    deleted = activity.contentResolver.delete(
                        Events.CONTENT_URI,
                        Events._ID + " =? ",
                        arrayOf(searchResulteventId)
                    )
                    ApzLogger.i(TAG, "Events deleted : $deleted")
                }
            } catch (e: JSONException) {
                ApzLogger.i(TAG, "APZ-CNT$e")
                return 0
            } catch (e: ParseException) {
                ApzLogger.i(TAG, "APZ-CNT$e")
                return 0
            }
        }
        return if (deleted > 0) {
            1
        } else {
            0
        }
    }
}