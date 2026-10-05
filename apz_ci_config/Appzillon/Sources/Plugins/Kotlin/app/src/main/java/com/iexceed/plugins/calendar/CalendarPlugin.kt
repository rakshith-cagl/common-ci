package com.iexceed.plugins.calendar

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.webkit.WebView
import androidx.core.app.ActivityCompat
import com.iexceed.common.ApzActivity
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class CalendarPlugin private constructor(val webView: WebView, val activity: ApzActivity<*>, override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    private lateinit var mJsonObj: JSONObject

    private var mCallbackId: String? = null

    private lateinit var permissions: Array<String>

    private var eventsCounts = 0

    private lateinit var dateFormat: String

    private val errorCode = "APZ-CNT-084"

    override fun execute(params: JSONObject) {
        try {
            this.mJsonObj = params
            mCallbackId = params.getString("id")
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_CALENDAR
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissions = arrayOf(
                    Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR
                )
                requestForPermission()
            } else {
                callCalendar()
            }
        } catch (e: Exception) {
            ApzLogger.i(TAG, "Problem $e")
            apzPluginUtil.sendError(
                callbackId="", "APZ-CNT-077", null,
                activity, webView, true
            )
        }
    }

    private fun requestForPermission() {
        activity.startOnPermissionForResult(
            activity,
            permissions,
            PluginConstants.APZ_REQ_CALENDAR,
            object : OnPermissionsResultHandler() {
                override fun handlePermissionResult(
                    requestCode: Int,
                    permissions: Array<String?>,
                    grantResults: IntArray
                ) {
                    if (requestCode == PluginConstants.APZ_REQ_CALENDAR) {
                        processCalendarPermission(permissions)
                    } else {
                        permissionDeniedCallback()
                    }
                }
            }
        )
    }

    private fun processCalendarPermission(permissions: Array<String?>) {
        var denied = false
        var neverAskAgain = false
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permission!!
                )
            ) {
                denied = true
            } else {
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    neverAskAgain = true
                }
            }
        }
        when {
            neverAskAgain -> {
                permissionDeniedCallback()
            }
            denied -> {
                displayReconfirmationMessage()
            }
            else -> {
                callCalendar()
            }
        }
    }

    private fun displayReconfirmationMessage() {
        val message =
            "To handle calendar events,allow app to access by granting requested permissions"
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle("Permission Denied")
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.cancel()
                requestForPermission()
            }.setNegativeButton("Deny") { dialog, _ ->
                dialog.cancel()
                permissionDeniedCallback()
            }
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun callCalendar() {
        var action = ""
        try {
            action = mJsonObj.getString("action")
        } catch (ex: JSONException) {
            ex.stackTrace
        }
        when {
            action.equals("create", ignoreCase = true) -> {
                onActionCreate()
            }
            action.equals("edit", ignoreCase = true) -> {
                onActionEdit()
            }
            action.equals("delete", ignoreCase = true) -> {
                onActionDelete()
            }
        }
    }

    private fun onActionDelete() {
        val calendarArray = mJsonObj.optJSONArray("events")
        if (calendarArray != null) {
            val myJsonArraySize = calendarArray.length()
            if (myJsonArraySize != 0) {
                try {
                    for (i in 0 until myJsonArraySize) {
                        val calendarObject = calendarArray[i] as JSONObject
                        eventsCounts += DeleteEvent(
                            mCallbackId,
                            activity,
                            webView,
                            apzPluginUtil
                        ).getDeletedCount(calendarObject)
                    }
                } catch (e: Exception) {
                    ApzLogger.e(TAG, "Exception : ${e.message}")
                }

                if (eventsCounts != 0) {
                    eventsCounts = 0
                    sendSuccessResponse("$eventsCounts events deleted")

                } else {
                    eventsCounts = 0
                    sendErrorResponse(errorCode)
                }

            } else {
                DeleteEvent(mCallbackId, activity, webView, apzPluginUtil).delete(mJsonObj)
            }
        } else {
            DeleteEvent(mCallbackId, activity, webView, apzPluginUtil).delete(mJsonObj)
        }
    }

    private fun onActionEdit() {
        val calendarArray = mJsonObj.optJSONArray("events")
        if (calendarArray != null) {
            val myJsonArraySize = calendarArray.length()

            if (myJsonArraySize != 0) {
                try {
                    for (i in 0 until myJsonArraySize) {
                        val calendarObject = calendarArray[i] as JSONObject
                        calendarCreateOrEit(calendarObject, false)
                    }
                } catch (e: Exception) {
                    ApzLogger.e(TAG, "Exception : ${e.message}")
                }
                if (eventsCounts != 0) {
                    eventsCounts = 0
                    sendSuccessResponse("$eventsCounts events edited")
                } else {
                    eventsCounts = 0
                    sendErrorResponse(errorCode)
                }

            } else {
                calendarCreateOrEit(mJsonObj, true)
            }
        } else {
            calendarCreateOrEit(mJsonObj, true)
        }
    }

    private fun onActionCreate() {
        try {
            val calendarArray = mJsonObj.getJSONArray("events")
            val myJsonArraySize = calendarArray.length()

            if (myJsonArraySize != 0) {
                for (i in 0 until myJsonArraySize) {
                    val calendarObject = calendarArray[i] as JSONObject
                    calendarCreateOrEit(calendarObject, false)
                }
            } else {
                sendErrorResponse("APZ-CNT-082")
            }
        } catch (e: Exception) {
            ApzLogger.e(TAG, "Exception : ${e.message}")
        }
        if (eventsCounts != 0) {
            eventsCounts = 0
            sendSuccessResponse("$eventsCounts events created")
        } else {
            eventsCounts = 0
            sendErrorResponse(errorCode)
        }
    }

    fun sendErrorResponse(eCode: String) {
        apzPluginUtil.sendError(
            mCallbackId,
            eCode,
            null,
            activity,
            webView,
            true
        )
    }

    fun sendSuccessResponse(message : String){
        val jsonObject = JSONObject()
        jsonObject.put("success", message)
        apzPluginUtil.sendSuccess(
            mCallbackId,
            jsonObject,
            false,
            activity,
            webView,
            true
        )
    }


    private fun permissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied("Calendar", mCallbackId, activity, webView)
    }

    private fun calendarCreateOrEit(jsonObj: JSONObject?, isSingleOperation: Boolean) {
        ApzLogger.i(TAG, "calendarOperation")
        try {
            dateFormat = mJsonObj.getString("dateFormat")
        } catch (e1: JSONException) {
            //Sonar fix
        }
        val calendarVal: MutableMap<String, String> = HashMap()
        val action: String
        try {
            action = mJsonObj.getString("action").trim { it <= ' ' }
            calendarVal["title"] = jsonObj!!.getString("title").trim { it <= ' ' }
            calendarVal["alarm"] = jsonObj.getString("alarm").trim { it <= ' ' }
            calendarVal["startDate"] = jsonObj.getString("startDate").trim { it <= ' ' }
            calendarVal["endDate"] = jsonObj.getString("endDate").trim { it <= ' ' }
            calendarVal["startTime"] = jsonObj.getString("startTime").trim { it <= ' ' }
            calendarVal["endTime"] = jsonObj.getString("endTime").trim { it <= ' ' }
            if (action == "edit") {
                calendarVal["newStartDate"] = jsonObj.getString("newStartDate").trim { it <= ' ' }
                calendarVal["newEndDate"] = jsonObj.getString("newEndDate").trim { it <= ' ' }
                calendarVal["newStartTime"] = jsonObj.getString("newStartTime").trim { it <= ' ' }
                calendarVal["newEndTime"] = jsonObj.getString("newEndTime").trim { it <= ' ' }
            }
            calendarVal["summary"] = jsonObj.getString("summary").trim { it <= ' ' }
            calendarVal["recurrence"] = jsonObj.getString("recurrence").trim { it <= ' ' }
            calendarVal["recurrenceEndDate"] =
                jsonObj.getString("recurrenceEndDate").trim { it <= ' ' }
            calendarVal["location"] = jsonObj.getString("location").trim { it <= ' ' }
        } catch (e: JSONException) {
            return
        } catch (e: java.lang.Exception) {
            return
        }
        when (action) {
            "create" -> {
                eventsCounts += CreateEvent(mCallbackId, activity, webView).create(
                    calendarVal,
                    dateFormat
                )
            }
            "edit" -> {
                if (isSingleOperation) {
                    EditEvent(mCallbackId, activity, webView, apzPluginUtil).edit(calendarVal, dateFormat)
                } else {
                    eventsCounts += EditEvent(mCallbackId, activity, webView, apzPluginUtil).getEditCount(
                        calendarVal,
                        dateFormat
                    )
                }

            }
            else -> {
                ApzLogger.i(TAG, "APZ-CNT-082")
            }
        }
    }


    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil : IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = CalendarPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }

    }

}
