package com.iexceed.plugins.contacts

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.telephony.PhoneNumberUtils
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.common.ExternalActivityResultHandler
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class FetchContact(val mCallbackId: String?,
                   val activity: ApzActivity<*>,
                   val webView: WebView,
                   val apzPluginUtil: IapzPluginUtil
) {

    val pickContactsPermission = 108

    val TAG = "FetchContact"

    fun fetchContacts() {
        var intent: Intent? = null
        try {
            intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        } catch (ex: Exception) {
            ApzLogger.i(TAG,"${ex.message}")
        }
        activity.startActivityForResult(
            intent,
            pickContactsPermission,
            object : ExternalActivityResultHandler() {
                override fun handleActivityResult(resultCode: Int, data: Intent?) {
                    if (resultCode == Activity.RESULT_OK) {
                        handleFetchResult(data)
                    } else {
                        apzPluginUtil.sendError(
                            mCallbackId,
                            "APZ-CNT-328",
                            null,
                            activity,
                            webView,
                            true
                        )
                    }
                }
            })
    }

    private fun handleFetchResult(data: Intent?) {
        var mime: String // MIME type
        val dataIdx: Int // Index of DATA1 column
        val mimeIdx: Int // Index of MIMETYPE column
        val nameIdx: Int // Index of DISPLAY_NAME column
        var name: String? = ""
        val contactData: Uri? = data!!.data
        var email = ""
        var phone = ""
        var base64: String? = ""
        val allNumbers: MutableList<String> = ArrayList()
        val allEmails: MutableList<String> = ArrayList()
        // Cursor object
        val cursor: Cursor? =
            activity.contentResolver.query(contactData!!, null, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                nameIdx =
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                name = cursor.getString(nameIdx)
                val cId = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                base64 =
                    Filter(activity).retrieveContactPhoto(cursor.getString(cId))
                val projection = arrayOf(
                    ContactsContract.Data.DISPLAY_NAME,
                    ContactsContract.Contacts.Data.DATA1,
                    ContactsContract.Contacts.Data.MIMETYPE
                )

                // Query ContactsContract.Data
                val cursorN: Cursor? = activity.contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    projection,
                    ContactsContract.Data.DISPLAY_NAME + " = ?",
                    arrayOf<String?>(name),
                    null
                )
                if (cursorN != null) {
                    if (cursorN.moveToFirst()) {
                        // Get the indexes of the MIME type and data
                        mimeIdx =
                            cursorN.getColumnIndex(ContactsContract.Contacts.Data.MIMETYPE)
                        dataIdx =
                            cursorN.getColumnIndex(ContactsContract.Contacts.Data.DATA1)

                        // Match the data to the MIME type, store in variables
                        do {
                            mime = cursorN.getString(mimeIdx)
                            handleEmailAndPhone(
                                mime,
                                email,
                                cursorN,
                                dataIdx,
                                allEmails,
                                phone,
                                allNumbers
                            )
                        } while (cursorN.moveToNext())
                    }
                    cursorN.close()
                }
            }
            cursor.close()
        }

        processSuccessResponse(allNumbers, allEmails, name, base64)
    }

    private fun handleEmailAndPhone(
        mime: String,
        email: String,
        cursorN: Cursor,
        dataIdx: Int,
        allEmails: MutableList<String>,
        phone: String,
        allNumbers: MutableList<String>
    ) {
        var email1 = email
        var phone1 = phone
        if (Email.CONTENT_ITEM_TYPE.equals(
                mime,
                ignoreCase = true
            )
        ) {
            email1 = cursorN.getString(dataIdx)
            allEmails.add(email1)
        }
        if (Phone.CONTENT_ITEM_TYPE.equals(
                mime,
                ignoreCase = true
            )
        ) {
            phone1 = cursorN.getString(dataIdx)
            phone1 = PhoneNumberUtils.formatNumber(
                phone1, Locale.getDefault().country
            )
            allNumbers.add(phone1)
        }
    }

    private fun processSuccessResponse(
        allNumbers: MutableList<String>,
        allEmails: MutableList<String>,
        name: String?,
        base64: String?
    ) {
        val items = allNumbers.toTypedArray()
        val emailArr = allEmails.toTypedArray()
        var jsonArr: JSONArray? = null
        var emailJson: JSONArray? = null
        try {
            jsonArr = JSONArray(items)
            emailJson = JSONArray(emailArr)
        } catch (e1: JSONException) {
            //Sonar fix
        }
        val jsonObj = JSONObject()
        try {
            jsonObj.put("name", name)
            jsonObj.put("phoneno", jsonArr)
            jsonObj.put("email", emailJson)
            jsonObj.put("encodedImage", base64)
        } catch (e: Exception) {
            //Sonar fix
        }
        apzPluginUtil.sendSuccess(
            mCallbackId,
            jsonObj,
            false,
            activity,
            webView,
            true
        )
    }


}
