package com.iexceed.plugins.calendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class CalendarPlugin extends ApzPlugin {

	private final String TAG = "CALENDER";

	private String id = "";

	private String dateFormat;

	private static ApzPlugin pluginObj;

	private JSONObject mJsonObj;

	private Activity mActivity;

	private WebView mWebview;

	private String mCallbackId;

	private String[] permissions;

	private int eventsCounts = 0;

	private CalendarPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
		mActivity = activity;
		mWebview = webView;
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity){
		if(pluginObj == null){
			pluginObj = new CalendarPlugin(webView, activity);
		}
		return pluginObj;
	}

	private void calendarOperation(JSONObject jsonObj) {
		ApzLogger.i(TAG,"calendarOperation");
		try {
			id = mJsonObj.getString("id");
			dateFormat = mJsonObj.getString("dateFormat");

		} catch (JSONException e1) {

		}
		/*To check api level for Calendar support*/
		if(Build.VERSION.SDK_INT < 14){
			//failureCallBack(mContext.getResources().getString(R.string.unsupported_operation));
			//ApzPluginUtil.sendError(id, "APZ-CNT-037", null, mActivity, mWebview, true);
			return;
		}
		Map<String, String> calendarVal = new HashMap<String, String>();
		String action = "";
		try {
			action = mJsonObj.getString("action").trim();
			calendarVal.put("title", jsonObj.getString("title").trim());
			calendarVal.put("alarm", jsonObj.getString("alarm").trim());
			calendarVal.put("startDate", jsonObj.getString("startDate").trim());
			calendarVal.put("endDate", jsonObj.getString("endDate").trim());
			calendarVal.put("startTime", jsonObj.getString("startTime").trim());
			calendarVal.put("endTime", jsonObj.getString("endTime").trim());
			if (action.equals("edit")) {
				calendarVal.put("newStartDate", jsonObj.getString("newStartDate").trim());
				calendarVal.put("newEndDate", jsonObj.getString("newEndDate").trim());
				calendarVal.put("newStartTime", jsonObj.getString("newStartTime").trim());
				calendarVal.put("newEndTime", jsonObj.getString("newEndTime").trim());
			}
			calendarVal.put("summary", jsonObj.getString("summary").trim());
			calendarVal.put("recurrence", jsonObj.getString("recurrence").trim());
			calendarVal.put("recurrenceEndDate", jsonObj.getString("recurrenceEndDate").trim());
			calendarVal.put("location", jsonObj.getString("location").trim());

		} catch (JSONException e) {
			//ApzPluginUtil.sendError(id, "APZ-CNT-077", null, mActivity,mWebview, true);
			return;
		} catch (Exception e) {
			return;
		}
		if (action.equals("create")) {
			create(calendarVal,dateFormat);
		} else if (action.equals("edit")) {
			edit(calendarVal,dateFormat);
		} else {
			ApzLogger.i(TAG,"APZ-CNT-082");
			//ApzPluginUtil.sendError(id, "APZ-CNT-082", null, mActivity,mWebview, true);
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void create(Map<String, String> calValues,String dateFormat) {
		int multipleEvents = 0;
		ApzLogger.i(TAG,"create");
		List<JSONObject> eventsSearchResult = searchEvents(calValues.get("title"));
		Date startDateTime = null;
		Date endDateTime = null;

		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat + "HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			startDateTime = sdf.parse(calValues.get("startDate") + " " + calValues.get("startTime"));
			endDateTime = sdf.parse(calValues.get("endDate") + " " + calValues.get("endTime"));

		} catch (ParseException e) {
			ApzLogger.e(TAG, "APZ-CNT-014" + e);
			//ApzPluginUtil.sendError(id, "APZ-CNT-014", null, mActivity, mWebview, true);
			return;
		}
		if (endDateTime.compareTo(startDateTime) >= 0) {
			ApzLogger.i(TAG, "Date Check : eDate is after sDate");
		} else {
			ApzLogger.e(TAG, "Date Check : sDate is after eDate");
			//failureCallBack(mContext.getResources().getString(R.string.check_date_time));
			//ApzPluginUtil.sendError(id, "APZ-CNT-086", null, mActivity, mWebview, true);
			return;
		}

		if(eventsSearchResult.size() > 0){

			for (int i = 0; i < eventsSearchResult.size(); i++) {
				try {

					String id = eventsSearchResult.get(i).getString("eventId");

					Date searchResultStartDateTime = new Date(Long.valueOf(eventsSearchResult.get(i).getString("startDateTime")));
					Date searchResultEndDateTime = new Date(Long.valueOf(eventsSearchResult.get(i).getString("endDateTime")));
					//Log.i(TAG, "searchResultStartDateTime:" + searchResultStartDateTime + "   searchResultEndDateTime:" + searchResultEndDateTime);
					//Log.i(TAG, "startDateTime:" + startDateTime + "   endDateTime:" + endDateTime);
					// compare their startDate end endDate
					if ((startDateTime.getTime() == searchResultStartDateTime.getTime()) && (endDateTime.getTime() == searchResultEndDateTime.getTime())) {
						// same exist
						multipleEvents++;

					}

				} catch (JSONException e) {

					return;
				}
			}

		}if (multipleEvents >= 1 ) {
			//Send Error
			//ApzPluginUtil.sendError(id, "APZ-CNT-085", null, mActivity, mWebview, true);

		}else {

			/*SimpleDateFormat sdf = new SimpleDateFormat(dateFormat + "HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			try {
				startDateTime = sdf.parse(calValues.get("startDate") + " " + calValues.get("startTime"));
				endDateTime = sdf.parse(calValues.get("endDate") + " " + calValues.get("endTime"));

			} catch (ParseException e) {
				ApzLogger.e(TAG, "APZ-CNT-014" + e);
				ApzPluginUtil.sendError(id, "APZ-CNT-014", null, mActivity, mWebview, true);
				return;
			}
			if (endDateTime.compareTo(startDateTime) >= 0) {
				ApzLogger.i(TAG, "Date Check : eDate is after sDate");
			} else {
				ApzLogger.e(TAG, "Date Check : sDate is after eDate");
				//failureCallBack(mContext.getResources().getString(R.string.check_date_time));
				ApzPluginUtil.sendError(id, "APZ-CNT-130", null, mActivity, mWebview, true);
				return;
			}*/


			//if (eventsSearchResult.isEmpty()) {

			ContentValues values = new ContentValues();

			values.put(Events.DTSTART, startDateTime.getTime());

			String recurrenceEndDate = calValues.get("recurrenceEndDate");
			String recurrence = calValues.get("recurrence");
			if (recurrence.equalsIgnoreCase("") || recurrence.equalsIgnoreCase("None")) {
				values.put(Events.DTEND, endDateTime.getTime());

			} else {

				if (recurrenceEndDate.equalsIgnoreCase("")) {
					if (recurrence.equalsIgnoreCase("Daily")) {
						values.put(Events.RRULE, "FREQ=DAILY");
					} else if (recurrence.equalsIgnoreCase("Weekly")) {
						values.put(Events.RRULE, "FREQ=WEEKLY");
					} else if (recurrence.equalsIgnoreCase("Monthly")) {
						values.put(Events.RRULE, "FREQ=MONTHLY");
					} else if (recurrence.equalsIgnoreCase("Yearly")) {
						values.put(Events.RRULE, "FREQ=YEARLY");
					}

				} else {
					try {
						Date rEndDate = sdf.parse(calValues.get("recurrenceEndDate") + " " + calValues.get("endTime"));
						SimpleDateFormat sdfR = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
						String dateUntil = sdfR.format(rEndDate.getTime());
						dateUntil = dateUntil.replace("-", "").replace(":", "");
						ApzLogger.i(TAG, dateUntil);

						if (recurrence.equalsIgnoreCase("Daily")) {
							values.put(Events.RRULE, "FREQ=DAILY;UNTIL=" + dateUntil);
						} else if (recurrence.equalsIgnoreCase("Weekly")) {
							values.put(Events.RRULE, "FREQ=WEEKLY;UNTIL=" + dateUntil);
						} else if (recurrence.equalsIgnoreCase("Monthly")) {
							values.put(Events.RRULE, "FREQ=MONTHLY;UNTIL=" + dateUntil);
						} else if (recurrence.equalsIgnoreCase("Yearly")) {
							values.put(Events.RRULE, "FREQ=YEARLY;UNTIL=" + dateUntil);
						}
//						values.put("rrule", "FREQ=DAILY;COUNT=10");
					} catch (ParseException e) {
						ApzLogger.i(TAG, "APZ-CNT-130");
						//ApzPluginUtil.sendError(id, "APZ-CNT-130", null, mActivity, mWebview, true);
						return;
					}

				}

				values.put("duration", "P15M");

			}

			values.put(Events.TITLE, calValues.get("title"));
			values.put(Events.EVENT_LOCATION, calValues.get("location"));
			//Bug  14924 Natasha
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				values.put(Events.CALENDAR_ID, 3);
			} else {
				values.put(Events.CALENDAR_ID, 1);
			}
			values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
			values.put(Events.DESCRIPTION, calValues.get("summary"));
			values.put(Events.GUESTS_CAN_MODIFY, 1);
			//values.put(Events.AVAILABILITY, Events.AVAILABILITY_FREE);
			values.put(Events.HAS_ALARM, 1);

			Uri uri = null;

			if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CALENDAR)
					== PackageManager.PERMISSION_GRANTED) {
				uri = mActivity.getApplicationContext().getContentResolver().insert(Events.CONTENT_URI, values);
			} else {
				PermissionDeniedCallback();
			}

			long newEventId = Long.valueOf(uri.getLastPathSegment());
			ApzLogger.i(TAG, "newEventId:" + newEventId);
			values.clear();
//Fix for 14277
			values.put(Reminders.METHOD, 1);
			values.put(Reminders.EVENT_ID, newEventId);
			if (calValues.get("alarm").equalsIgnoreCase("5M")) {
				values.put(Reminders.MINUTES, 5);
			} else if (calValues.get("alarm").equalsIgnoreCase("15M")) {
				values.put(Reminders.MINUTES, 15);
			} else if (calValues.get("alarm").equalsIgnoreCase("1H")) {
				values.put(Reminders.MINUTES, 60);
			} else if (calValues.get("alarm").equalsIgnoreCase("1D")) {
				values.put(Reminders.MINUTES, 60 * 24);
			}

			Uri uriRem = mActivity.getApplicationContext().getContentResolver().insert(Reminders.CONTENT_URI, values);
			long newEventRemId = Long.valueOf(uriRem.getLastPathSegment());
			if (newEventId > 0 && newEventRemId > 0) {
				//	successCallBack(mContext.getResources().getString(R.string.add_success));
				eventsCounts++;
				//ApzPluginUtil.sendSuccess(id, null, false, mActivity, mWebview, true);
			} else {
				//failureCallBack(mContext.getResources().getString(R.string.add_failed));
				//ApzPluginUtil.sendError(id, "APZ-CNT-084", null, mActivity, mWebview, true);
			}
			return;
			//	}
		}
	}

	@SuppressLint("NewApi")
	public void edit(Map<String, String> calValues,String dateFormat) {
		ApzLogger.i(TAG,"edit");
		List<JSONObject> eventsSearchResult = searchEvents(calValues.get("title"));
		/*
		 * if multiple events exists with the given title, then check events
		 * date
		 */

		if (eventsSearchResult == null || eventsSearchResult.isEmpty()) {
			ApzPluginUtil.sendError(id, "APZ-CNT-039", null, mActivity,mWebview, true);
			return;// not found
		}
		/* check for startNewDate and endNewDate */
		Date newStartDateTime = null;
		Date newEndDateTime = null;
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat+"HH:mm:ss");
		try {
			newStartDateTime = sdf.parse(calValues.get("newStartDate") + " "+ calValues.get("newStartTime"));
			newEndDateTime = sdf.parse(calValues.get("newEndDate") + " "+ calValues.get("newEndTime"));
		} catch (ParseException e) {

			return;
		}
		if (newEndDateTime.compareTo(newStartDateTime) >= 0) {
			ApzLogger.i(TAG,"Date Check:newEndDateTime is after StartDateTime");
		} else {
			ApzLogger.i(TAG,"Date Check:sDate is after eDate");
			//	failureCallBack(mContext.getResources().getString(R.string.check_date_time));
			ApzPluginUtil.sendError(id, "APZ-CNT-086", null, mActivity,mWebview, true);
			return;
		}
		/* date check end */
		String eventId = "";
		/* if there is a single events with the given title, then update it */
		//	if (eventsSearchResult.size() == 1)
			/*{
			Date startDateTime = null;
			Date endDateTime = null;
			try {
				eventId = eventsSearchResult.get(0).getString("eventId");
				startDateTime = sdf.parse(calValues.get("newStartDate") + " "	+ calValues.get("newStartTime"));
				endDateTime = sdf.parse(calValues.get("newEndDate") + " "+ calValues.get("newEndTime"));
				Date searchResultStartDateTime = new Date(Long.valueOf(eventsSearchResult.get(0).getString("startDateTime")));
				Date searchResultEndDateTime = new Date(Long.valueOf(eventsSearchResult.get(0).getString("endDateTime")));
				// compare their startDate end endDate
				if ((startDateTime.getTime() == searchResultStartDateTime.getTime()) && (endDateTime.getTime() == searchResultEndDateTime.getTime())) {
					// same exists
					//failureCallBack(mContext.getResources().getString(R.string.event_present));
					ApzPluginUtil.sendError(id, "APZ-CNT-020", null, mActivity,mWebview, true);
				}else{
					if (update(eventId, calValues, "edit",dateFormat)) {
						//successCallBack(mContext.getResources().getString(R.string.edit_success));
						ApzPluginUtil.sendSuccess(id, null, false, mActivity,mWebview, true);
						return;
					} else {
						//failureCallBack(mContext.getResources().getString(R.string.edit_failed));
						ApzPluginUtil.sendError(id, "APZ-CNT-020", null, mActivity,mWebview, true);
					}
				}

			} catch (JSONException e) {
				ApzPluginUtil.sendError(id, "APZ-CNT-020", null, mActivity,mWebview, true);
				//failureCallBack(mContext.getResources().getString(R.string.edit_failed));

				return;
			} catch (ParseException e) {
				ApzPluginUtil.sendError(id, "APZ-CNT-020", null, mActivity,mWebview, true);
				//failureCallBack(mContext.getResources().getString(R.string.edit_failed));

				return;
			}

		}*/
		/*
		 * if multiple events are there , then compare their schedule time(start
		 * and end time)
		 */

		//	else if (eventsSearchResult.size() > 1) {
		//ApzPluginUtil.sendError(id, "APZ-CNT-085", null, mActivity, mWebview, true);
		//failureCallBack(mContext.getResources().getString(R.string.multiple_entry));

		Date startDateTime = null;
		Date endDateTime = null;
		try {
			//Abhishek, Bug id 5972, edit calendar START
			startDateTime = sdf.parse(calValues.get("startDate") + " " + calValues.get("startTime"));
			endDateTime = sdf.parse(calValues.get("endDate") + " " + calValues.get("endTime"));
			//startDateTime = sdf.parse(calValues.get("newStartDate") + " " + calValues.get("newStartTime"));
			//endDateTime = sdf.parse(calValues.get("newEndDate") + " " + calValues.get("newEndTime"));
			//Abhishek, Bug id 5972, edit calendar END
		} catch (ParseException e) {

			return;
		}
		if (endDateTime.compareTo(startDateTime) > 0) {
			//Log.i(TAG, "Date Check:sDate is after eDate");
		} else {
			//Log.i(TAG, "Date Check:sDate is after eDate");
			//failureCallBack("Check Schedule Date and Time");
			ApzPluginUtil.sendError(id, "APZ-CNT-086", null, mActivity, mWebview, true);
			return;
		}
		int multipleEvents = 0;
		String idToBeUpdated = "";
		for (int i = 0; i < eventsSearchResult.size(); i++) {
			try {

				String id = eventsSearchResult.get(i).getString("eventId");

				Date searchResultStartDateTime = new Date(Long.valueOf(eventsSearchResult.get(i).getString("startDateTime")));
				Date searchResultEndDateTime = new Date(Long.valueOf(eventsSearchResult.get(i).getString("endDateTime")));
				//Log.i(TAG, "searchResultStartDateTime:" + searchResultStartDateTime + "   searchResultEndDateTime:" + searchResultEndDateTime);
				//Log.i(TAG, "startDateTime:" + startDateTime + "   endDateTime:" + endDateTime);
				// compare their startDate end endDate
				if ((startDateTime.getTime() == searchResultStartDateTime.getTime()) && (endDateTime.getTime() == searchResultEndDateTime.getTime())) {
					// same exist
					eventId = eventsSearchResult.get(i).getString("eventId");
					multipleEvents++;
					idToBeUpdated = id;
				}

			} catch (JSONException e) {

				return;
			}
		}
		if (multipleEvents == 1 && !idToBeUpdated.equals("")) {
			// update it
			if (update(eventId, calValues, "edit", dateFormat)) {
				ApzPluginUtil.sendSuccess(id, null, false, activity, webView, true);

			}
		} else {
			// multiple events found with given criteria
			ApzPluginUtil.sendError(id, "APZ-CNT-039", null, mActivity, mWebview, true);
			return;
		}
		//}
	}

	/**
	 * to update events
	 *
	 * @param eventId
	 */
	@SuppressLint("NewApi")
	private boolean update(String eventId, Map<String, String> newValues,String action,String dateFormat) {
		ApzLogger.i(TAG,"update : " + eventId);
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat+"HH:mm:ss");
		Date sDate, eDate = null;
		if (action.equals("create")) {
			try {
				sDate = sdf.parse(newValues.get("startDate") + " "	+ newValues.get("startTime"));
				eDate = sdf.parse(newValues.get("endDate") + " "+ newValues.get("endTime"));
			} catch (ParseException e) {

				return false;
			}
		} else {
			try {
				//Abhishek, Bug id 5972, edit calendar START
				sDate = sdf.parse(newValues.get("newStartDate") + " "+ newValues.get("newStartTime"));
				eDate = sdf.parse(newValues.get("newEndDate") + " "+ newValues.get("newEndTime"));
				//Abhishek, Bug id 5972, edit calendar START
			} catch (ParseException e) {

				return false;
			}
		}

		ContentValues values = new ContentValues();
		values.put(Events.DTSTART, sDate.getTime());

		values.put(Events.DTEND, eDate.getTime());
		String recurrence = newValues.get("recurrence");
		if (recurrence.equals("Daily")) {
			values.put(Events.RRULE, "FREQ=DAILY");
		} else if (recurrence.equals("Weekly")) {
			values.put(Events.RRULE, "FREQ=WEEKLY");
		} else if (recurrence.equals("Monthly")) {
			values.put(Events.RRULE, "FREQ=MONTHLY");
		}
		values.put(Events.TITLE, newValues.get("title"));
		values.put(Events.EVENT_LOCATION, newValues.get("location"));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
			values.put(Events.CALENDAR_ID, 3);
		}else{
			values.put(Events.CALENDAR_ID, 1);
		}
		values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
		values.put(Events.DESCRIPTION, newValues.get("summary"));
		// reasonable defaults exist:
		values.put(Events.GUESTS_CAN_MODIFY, 1);
		values.put(Events.HAS_ALARM, 1);
		int eventUpdated = 0;
		if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CALENDAR)
				== PackageManager.PERMISSION_GRANTED ) {
			eventUpdated = mActivity.getApplicationContext().getContentResolver().update(Events.CONTENT_URI, values, Events._ID + " =? ",new String[] { eventId });
		}else{
			PermissionDeniedCallback();
		}

		ApzLogger.i(TAG,"eventUpdated:" + eventUpdated);
		values.clear();
		values.put(Reminders.EVENT_ID, eventId);
		if (newValues.get("alarm").equalsIgnoreCase("5M")) {
			values.put(Reminders.MINUTES, 5);
		} else if (newValues.get("alarm").equalsIgnoreCase("15M")) {
			values.put(Reminders.MINUTES, 15);
		} else if (newValues.get("alarm").equalsIgnoreCase("1H")) {
			values.put(Reminders.MINUTES, 60);
		} else if (newValues.get("alarm").equalsIgnoreCase("1D")) {
			values.put(Reminders.MINUTES, 60 * 24);
		}
		int reminderUpdated = mActivity.getApplicationContext().getContentResolver().update(Reminders.CONTENT_URI, values, Reminders.EVENT_ID + " =? ",new String[] { eventId });
		ApzLogger.i(TAG,"Update Events:"+eventUpdated+" reminderUpdated:"+reminderUpdated);
		if (eventUpdated > 0 && reminderUpdated > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Deletes events
	 *
	 * @param jsonObject
	 */
	@SuppressLint("NewApi")
	public void delete(JSONObject jsonObject) {
		String dateFormat = "";
		ApzLogger.i(TAG,"Delete");
		if(Build.VERSION.SDK_INT < 14){
			//failureCallBack(mContext.getResources().getString(R.string.unsupported_operation));
			ApzPluginUtil.sendError(id, "APZ-CNT-323", null, mActivity,mWebview, true);
			return;
		}
		String title = "";

		String startDate = "";
		String endDate = "";
		String startTime = "";
		String endTime = "";

		try {
			id = jsonObject.getString("id");
			dateFormat = jsonObject.getString("dateFormat");
			//JSONObject jsonObj = new JSONObject(jsonObject.getString("body"));
			title = jsonObject.getString("title").trim();
			startDate = jsonObject.getString("startDate").trim();
			endDate = jsonObject.getString("endDate").trim();
			startTime = jsonObject.getString("startTime").trim();
			endTime = jsonObject.getString("endTime").trim();

		} catch (JSONException e) {
			ApzLogger.i(TAG,"APZ-CNT-077"+e);
			ApzPluginUtil.sendError(id, "APZ-CNT-077", null, mActivity,mWebview, true);
			return;
		} catch (Exception e) {
			return;
		}

		int deleted = 0;
		List<JSONObject> eventsSearchResult = searchEvents(title);
		if (eventsSearchResult == null || !(eventsSearchResult.size() > 0)) {
			//failureCallBack(mContext.getResources().getString(R.string.event_not_present));
			ApzPluginUtil.sendError(id, "APZ-CNT-039", null, mActivity,mWebview, true);
			return;
		}
		/* if single events exists with the given title, then delete it */
		/*if (eventsSearchResult.size() == 1) {
			try {
				String[] selArgs = new String[] { eventsSearchResult.get(0).getString("eventId") };

				if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CALENDAR)
						== PackageManager.PERMISSION_GRANTED ) {
					deleted = mActivity.getApplicationContext().getContentResolver().delete(Events.CONTENT_URI, Events._ID + " =? ", selArgs);
				}else{
					PermissionDeniedCallback();
				}

				if (deleted > 0) {
					//successCallBack(mContext.getResources().getString(R.string.delete_success));
					ApzPluginUtil.sendSuccess(id, null, false, mActivity,mWebview, true);

					return;
				} else {
					//failureCallBack(mContext.getResources().getString(R.string.delete_failed));
					ApzPluginUtil.sendError(id, "APZ-CNT-038", null, mActivity,mWebview, true);
					return;
				}
			}

			catch (JSONException e) {
				ApzLogger.i(TAG,"APZ-CNT-038"+e);
				return;
			}

		}*/
		/*
		 * if multiple events exists with the given title, then delete entire
		 * events
		 */
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat+"HH:mm:ss");
		Date startDateTime, endDateTime = null;
		Date searchResultStartDateTime, searchResultEndDateTime = null;
		//	if (eventsSearchResult.size() > 1) {
		String searchResulteventId = "";
		for (int i = 0; i < eventsSearchResult.size(); i++) {
			try {
				searchResulteventId = eventsSearchResult.get(i).getString("eventId");
				searchResultStartDateTime = new Date(Long.valueOf(eventsSearchResult.get(i).getString("startDateTime")));
				searchResultEndDateTime = new Date(Long.valueOf(eventsSearchResult.get(i).getString("endDateTime")));

				startDateTime = sdf.parse(startDate + " " + startTime);
				endDateTime = sdf.parse(endDate + " " + endTime);

				ApzLogger.i(TAG,"searchResultStartDateTime:"	+ searchResultStartDateTime	+ "   searchResultEndDateTime:"	+ searchResultEndDateTime);
				// compare their startDate end endDate
				if ((startDateTime.getTime() == searchResultStartDateTime.getTime())&& (endDateTime.getTime() == searchResultEndDateTime.getTime())) {
					if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CALENDAR)
							== PackageManager.PERMISSION_GRANTED ){
						deleted = mActivity.getApplicationContext().getContentResolver().delete(Events.CONTENT_URI, Events._ID + " =? ",	new String[] { searchResulteventId });
						ApzLogger.i(TAG,"Events deleted : "	+ deleted);
					}else{
						PermissionDeniedCallback();
					}
				}
			} catch (JSONException e) {
				ApzLogger.i(TAG,"APZ-CNT"+e);
				return;
			} catch (ParseException e) {
				ApzLogger.i(TAG,"APZ-CNT"+e);
				return;
			}

		}
		//}
		if (deleted > 0) {
			//successCallBack(mContext.getResources().getString(R.string.delete_success));
			ApzPluginUtil.sendSuccess(id, null, false, mActivity,mWebview, true);
		} else {
			//failureCallBack(mContext.getResources().getString(R.string.delete_failed));
			ApzPluginUtil.sendError(id, "APZ-CNT-038", null, mActivity,mWebview, true);
		}
	}

	@SuppressLint("NewApi")
	private List<JSONObject> searchEvents(String searchTitle) {
		ApzLogger.i(TAG,"searchEvents");
		List<JSONObject> searchedEvents = new ArrayList<JSONObject>();
		String[] projection = new String[] { Events._ID, Events.TITLE,Events.DTSTART, Events.DTEND, Events.DESCRIPTION };
// Abhishek , Bug id 5972 START
		String selection = "( ("+ Events.TITLE + " LIKE ?) AND ( deleted != 1 ) )";
//		Cursor calCursor = mContext.getContentResolver().query(Events.CONTENT_URI, projection, Events.TITLE + " LIKE ?",new String[] { searchTitle }, Events._ID + " ASC");
		Cursor calCursor = null;

		if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CALENDAR)
				== PackageManager.PERMISSION_GRANTED ) {
			calCursor = mActivity.getApplicationContext().getContentResolver().query(Events.CONTENT_URI, projection, selection,new String[] { searchTitle }, Events._ID + " ASC");
		}else{
			PermissionDeniedCallback();
		}

// Abhishek , Bug id 5972 END
		try {
			if (calCursor.moveToFirst()) {
				do {
					JSONObject eventsJson = new JSONObject();
					String eventId = calCursor.getString(calCursor.getColumnIndex(Events._ID));
					String startDateTime = calCursor.getString(calCursor.getColumnIndex(Events.DTSTART));
					String endDateTime = calCursor.getString(calCursor.getColumnIndex(Events.DTEND));
					eventsJson.put("eventId", eventId);// require to delete
					// require to edit
					eventsJson.put("startDateTime", startDateTime);
					eventsJson.put("endDateTime", endDateTime);
					searchedEvents.add(eventsJson);
				} while (calCursor.moveToNext());
			}
		} catch (JSONException e) {

		} finally {
			calCursor.close();
		}
		ApzLogger.i(TAG,"SEARCH : " + searchedEvents);
		return searchedEvents;
	}

	public static boolean isCalendarPlugin() {
		return true;
	}

	@Override
	public void execute(JSONObject params) {
		try {
			this.mJsonObj = params;
			mCallbackId = params.getString("id");
			if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
				if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR)
						!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR)
						!= PackageManager.PERMISSION_GRANTED ) {
					permissions = new String[]{
							Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR};
					requestForPermission();
				} else {
					callCalendar();
				}
			} else {
				callCalendar();
			}
		}catch (Exception e){
			ApzLogger.i(TAG,"Problem "+e.toString());
		}
	}

	private void requestForPermission() {
		this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_CALENDAR, new OnPermissionsResultHandler() {
					@Override
					public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
						if (requestCode == ApzPlugin.APZ_REQ_CALENDAR) {
							boolean denied = false;
							boolean never_ask_again = false;
							for (String permission : permissions) {
								if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
									denied = true;
								} else {
									if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
										//callCalendar();
									} else {
										never_ask_again = true;
									}
								}
							}
							if (never_ask_again) {
								PermissionDeniedCallback();
							} else if (denied) {
								displayReconfirmationMessage();
							} else {
								callCalendar();
							}
						} else {
							PermissionDeniedCallback();
						}

					}
				}
		);
	}

	private void displayReconfirmationMessage() {
		String message = "To handle calendar events,allow app to access by granting requested permissions";

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		alertDialogBuilder.setTitle("Permission Denied");
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						requestForPermission();
					}
				}).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				PermissionDeniedCallback();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	private void PermissionDeniedCallback(){
		ApzPluginUtil.sendPermissionDenied("Calendar",mCallbackId, this.activity,this.webView);
	}

	private void callCalendar(){

		String action = "";
		try{
			action = mJsonObj.getString("action");
		}catch(JSONException ex){
			ex.getStackTrace();
		}
		if(action.equalsIgnoreCase("create")){
			try{
				JSONArray calendarArray = mJsonObj.getJSONArray("events");
				int myJsonArraySize = calendarArray.length();
				if(myJsonArraySize != 0) {
					for (int i = 0; i < myJsonArraySize; i++) {
						JSONObject calendarObject = (JSONObject) calendarArray.get(i);
						calendarOperation(calendarObject);
					}
					if(eventsCounts != 0){
						JSONObject jsonObject = new JSONObject();
						jsonObject.put("success", ""+eventsCounts+" events created");
						eventsCounts = 0;
						ApzPluginUtil.sendSuccess(id, jsonObject, false, mActivity, mWebview, true);
					}else{
						eventsCounts = 0;
						ApzPluginUtil.sendError(id, "APZ-CNT-084", null, mActivity, mWebview, true);
					}
				}else{
					ApzPluginUtil.sendError(id, "APZ-CNT-082", null, mActivity,mWebview, true);
				}

			}catch (Exception e){

			}

		}else if(action.equalsIgnoreCase("edit")){
			calendarOperation(mJsonObj);
		}else if(action.equalsIgnoreCase("delete")){
			delete(mJsonObj);
		}
	}

}


