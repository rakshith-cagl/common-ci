package com.iexceed.plugins.contacts

import android.content.ContentProviderOperation
import android.database.Cursor
import android.provider.ContactsContract
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class DeleteContact(val mCallbackId: String?,
                    val activity: ApzActivity<*>,
                    val webView: WebView,
                    val apzPluginUtil: IapzPluginUtil
) {

    val TAG = "DeleteContact"

    fun deleteContact(jsonObj: JSONObject?) {
        ApzLogger.i(TAG, "deleteContact")
        val deleteCriteria: JSONObject?
        val firstName: String?
        val lastName: String?
        val phoneMobile: String?
        val phoneHome: String?
        val phoneWork: String?
        try {
            deleteCriteria = JSONObject(jsonObj!!.getString("deleteCriteria"))
            firstName = deleteCriteria.getString("firstName")
            lastName = deleteCriteria.getString("lastName")
            phoneMobile = deleteCriteria.getString("phoneMobile")
            phoneHome = deleteCriteria.getString("phoneHome")
            phoneWork = deleteCriteria.getString("phoneWork")
        } catch (e: Exception) {
            apzPluginUtil.sendError(
                mCallbackId,
                "APZ-CNT-044",
                null,
                activity,
                webView,
                true
            ) //No Contact Found.
            return
        }
        val deleteResult: JSONArray? =
            Filter(activity).filterContacts(firstName, lastName, phoneMobile, phoneHome, phoneWork)
        if (deleteResult == null || deleteResult.length() <= 0) {
            apzPluginUtil.sendError(
                mCallbackId,
                "APZ-CNT-044",
                null,
                activity,
                webView,
                true
            ) //No Contact Found.
            return
        }
        if (deleteResult.length() == 1) {
            handleIfContactsDeleted(deleteResult)
        } else {
            apzPluginUtil.sendError(
                mCallbackId,
                "APZ-CNT-045",
                null,
                activity,
                webView,
                true
            ) //Multiple Contact Exist
        }
    }

    private fun handleIfContactsDeleted(deleteResult: JSONArray) {
        var isDeleted = false
        /* finding raw_contact_id for contacts to be deleted */
        val rawContCur: Cursor? = try {
            activity.contentResolver.query(
                ContactsContract.RawContacts.CONTENT_URI,
                arrayOf(ContactsContract.RawContacts._ID),
                ContactsContract.RawContacts.CONTACT_ID + "=?",
                arrayOf((deleteResult[0] as JSONObject).getString("contactID")),
                null
            )
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
            return
        }
        val rawIdList: MutableList<String> = ArrayList()
        if (rawContCur!!.moveToFirst()) {
            do {
                val cIndex: Int = rawContCur.getColumnIndex(ContactsContract.RawContacts._ID)
                val rawId: String = rawContCur.getString(cIndex)
                rawIdList.add(rawId)
            } while (rawContCur.moveToNext())
            rawContCur.close()
        }


        /* delete all rowcontact_id related to contact_id */
        val ops = ArrayList<ContentProviderOperation>()
        for (j in rawIdList.indices) {
            ops.add(
                ContentProviderOperation
                    .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(
                        ContactsContract.RawContacts._ID + " = ?",
                        arrayOf(rawIdList[j])
                    ).build()
            )
        }
        try {
            val res = activity.contentResolver.applyBatch(
                ContactsContract.AUTHORITY,
                ops
            )
            if (res.isNotEmpty()) {
                isDeleted = true
            }
        } catch (e: Exception) {
            ApzLogger.e(TAG, e.toString())
            return
        }
        if (isDeleted) {
            val result = JSONObject()
            try {
                result.put("successMessage", "Deleted")
            } catch (e: JSONException) {
                //Sonar fix
            }
            apzPluginUtil.sendSuccess(mCallbackId, result, false, activity, webView, true)
        } else {
            apzPluginUtil.sendError(
                mCallbackId,
                "APZ-CNT-043",
                null,
                activity,
                webView,
                true
            ) //Deletion Failed.
        }
    }

}
