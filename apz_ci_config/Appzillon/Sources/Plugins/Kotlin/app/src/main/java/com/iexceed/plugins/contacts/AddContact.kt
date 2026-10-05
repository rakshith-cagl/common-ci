package com.iexceed.plugins.contacts

import android.content.ContentProviderOperation
import android.provider.ContactsContract
import android.provider.ContactsContract.AUTHORITY
import android.provider.ContactsContract.CommonDataKinds.*
import android.provider.ContactsContract.Data
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class AddContact(val mCallbackId: String?,
                 val activity: ApzActivity<*>,
                 val webView: WebView,
                 val apzPluginUtil: IapzPluginUtil) {

    val TAG = "AddContact"

    fun addContacts(jsonObj: JSONObject?) {
        ApzLogger.i(TAG, "addContacts")
        val details: JSONObject?
        val firstName: String?
        val lastName: String?
        val phoneMobile: String?
        val phoneHome: String?
        val phoneWork: String?
        val mail: String?
        val address: String?
        val website: String?
        try {
            details = JSONObject(jsonObj!!.getString("details"))
            firstName = details.getString("firstName")
            lastName = details.getString("lastName")
            phoneMobile = details.getString("phoneMobile")
            phoneHome = details.getString("phoneHome")
            phoneWork = details.getString("phoneWork")
            mail = details.getString("mail")
            address = details.getString("address")
            website = details.getString("website")
        } catch (e: Exception) {
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-077", null, activity, webView, true)
            return
        }
        if (firstName == null || firstName == "") {
            apzPluginUtil.sendError(
                mCallbackId,
                "APZ-CNT-041",
                null,
                activity,
                webView,
                true
            ) //First Name cannot be empty
            return
        }
        val ops = ArrayList<ContentProviderOperation>()
        ops.add(
            ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build()
        )

        // ------------------------------------------------------Names(firstName and lastName)
        ops.add(
            ContentProviderOperation
                .newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.GIVEN_NAME, firstName)
                .withValue(StructuredName.FAMILY_NAME, lastName)
                .build()
        )

        // ------------------------------------------------------ Mobile Number
        if (phoneMobile != null) {
            ops.add(getPhoneContentProvider(phoneMobile, Phone.TYPE_MOBILE))
        }

        // ------------------------------------------------------ Home Number
        if (phoneHome != null) {
            ops.add(getPhoneContentProvider(phoneHome, Phone.TYPE_HOME))
        }

        // ------------------------------------------------------ Work Numbers
        if (phoneWork != null) {
            ops.add(getPhoneContentProvider(phoneWork, Phone.TYPE_WORK))
        }

        // ------------------------------------------------------ Email
        if (mail != null) {
            ops.add(
                ContentProviderOperation
                    .newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                    .withValue(Email.DATA, mail)
                    .withValue(Email.TYPE, Email.TYPE_HOME)
                    .build()
            )
        }

        // ------------------------------------------------------ address
        if (address != null) {
            ops.add(
                ContentProviderOperation
                    .newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(StructuredPostal.STREET, address)
                    .withValue(StructuredPostal.TYPE, StructuredPostal.TYPE_HOME)
                    .build()
            )
        }
        // ------------------------------------------------------ website
        if (website != null) {
            ops.add(
                ContentProviderOperation
                    .newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                    .withValue(Data.MIMETYPE, Website.CONTENT_ITEM_TYPE)
                    .withValue(Website.DATA, website)
                    .withValue(Website.TYPE, Website.TYPE_WORK)
                    .build()
            )
        }

        // Asking the Contact provider to create a new contact
        var newId: String? = null
        try {
            val cpResults = activity.applicationContext.contentResolver.applyBatch(
                AUTHORITY,
                ops
            )
            if (cpResults.isNotEmpty()) {
                // to know te newly added contact id
                newId = cpResults[0].uri!!.lastPathSegment
            }
        } catch (e: Exception) {
            ApzLogger.e(TAG, e.toString())
        }
        if (newId != null) {
            val resJson = JSONObject()
            try {
                resJson.put("successMessage", "Success")
            } catch (e: JSONException) {
                ApzLogger.e(TAG, e.toString())
            }
            apzPluginUtil.sendSuccess(mCallbackId, resJson, false, activity, webView, true)
        } else {
            apzPluginUtil.sendError(
                mCallbackId,
                "APZ-CNT-041",
                null,
                activity,
                webView,
                true
            ) //Can not be created
        }
    }

    private fun getPhoneContentProvider(number: String, type: Int): ContentProviderOperation {
        return ContentProviderOperation
            .newInsert(Data.CONTENT_URI)
            .withValueBackReference(Data.RAW_CONTACT_ID, 0)
            .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
            .withValue(Phone.NUMBER, number)
            .withValue(Phone.TYPE, type)
            .build()
    }

}
