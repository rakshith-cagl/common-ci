package com.iexceed.plugins.contacts

import android.content.ContentProviderOperation
import android.content.ContentProviderResult
import android.content.ContentResolver
import android.provider.ContactsContract
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
class EditContact(val mCallbackId: String?,
                  val activity: ApzActivity<*>,
                  val webView: WebView,
                  val apzPluginUtil: IapzPluginUtil
) {

    val TAG = "EditContact"
    private val filler = "=? AND "


    fun editContacts(jsonObj: JSONObject?) {
        ApzLogger.i(TAG, "editContact")
        val updateJson: JSONObject?
        val firstNameNew: String
        val lastNameNew: String?
        val phoneMobileNew: String?
        val phoneHomeNew: String?
        val phoneWorkNew: String?
        val websiteNew: String?
        val mailNew: String?
        val addressNew: String?
        val searchCriteria: JSONObject?
        val firstName: String?
        val lastName: String?
        val phoneMobile: String?
        val phoneHome: String?
        val phoneWork: String?
        try {
            searchCriteria = JSONObject(jsonObj!!.getString("searchCriteria"))
            firstName = searchCriteria.getString("firstName")
            lastName = searchCriteria.getString("lastName")
            phoneMobile = searchCriteria.getString("phoneMobile")
            phoneHome = searchCriteria.getString("phoneHome")
            phoneWork = searchCriteria.getString("phoneWork")
            updateJson = JSONObject(jsonObj.getString("details"))
            firstNameNew = updateJson.getString("firstName")
            lastNameNew = updateJson.getString("lastName")
            phoneMobileNew = updateJson.getString("phoneMobile")
            phoneHomeNew = updateJson.getString("phoneHome")
            phoneWorkNew = updateJson.getString("phoneWork")
            websiteNew = updateJson.getString("website")
            mailNew = updateJson.getString("mail")
            addressNew = updateJson.getString("address")
        } catch (e: Exception) {
            ApzLogger.e(TAG, e.toString())
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-077", null, activity, webView, true)
            //Multiple Contact Exist
            return
        }
        val editSearchResult =
            Filter(activity).filterContacts(firstName, lastName, phoneMobile, phoneHome, phoneWork)
        if (editSearchResult == null || editSearchResult.length() <= 0) {
            apzPluginUtil.sendError(
                mCallbackId,
                "APZ-CNT-044",
                null,
                activity,
                webView,
                true
            ) //Contact Not Found
            return
        }
        // if single record exist ,then perform edit else Multiple contact exist
        if (editSearchResult.length() == 1) {
            try {
                // if single contact is present
                val contactID = (editSearchResult[0] as JSONObject).getString("contactID")
                var isUpdated = false
                val cr = activity.applicationContext.contentResolver
                val ops = ArrayList<ContentProviderOperation>()
                ops.add(
                    ContentProviderOperation
                        .newUpdate(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build()
                )
                handleFirstAndLastName(firstNameNew, contactID, ops, lastNameNew)
                /* get_ email row id */
                val (phoneRowId, homePhoneRowId, workPhoneRowId) = getEmailRowId(cr, contactID)

                handlePhoneDetails(
                    phoneMobileNew,
                    ops,
                    phoneRowId,
                    phoneWorkNew,
                    workPhoneRowId,
                    phoneHomeNew,
                    homePhoneRowId
                )

                /* get_ email row id */
                getEmailRowId(cr, contactID, mailNew, ops)
                /* get_ address row id */
                getAddressRowId(cr, contactID, addressNew, ops)
                /* get_ webSite row id */
                getWebsiteRowId(cr, contactID, websiteNew, ops)
                try {
                    val res: Array<ContentProviderResult> =
                        cr.applyBatch(ContactsContract.AUTHORITY, ops)
                    if (res != null) isUpdated = true
                } catch (e: Exception) {
                    return
                }
                if (isUpdated) {
                    val result = JSONObject()
                    result.put("successMessage", "Updated")
                    apzPluginUtil.sendSuccess(mCallbackId, result, false, activity, webView, true)
                } else {
                    apzPluginUtil.sendError(
                        mCallbackId,
                        "APZ-CNT-042",
                        null,
                        activity,
                        webView,
                        true
                    )
                    //Update Failed
                }
            } catch (e: JSONException) {
                ApzLogger.e(TAG, e.toString())
            }
        } else {
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-045", null, activity, webView, true)
            //Multiple Contact Exist
        }
    }

    private fun handleFirstAndLastName(
        firstNameNew: String,
        contactID: String?,
        ops: ArrayList<ContentProviderOperation>,
        lastNameNew: String?
    ) {
        if (firstNameNew != "") {
            val contactBuilder = ContentProviderOperation
                .newUpdate(Data.CONTENT_URI)
                .withSelection(
                    Data.RAW_CONTACT_ID + filler + Data.MIMETYPE + "=?",
                    arrayOf(contactID, StructuredName.CONTENT_ITEM_TYPE)
                )
                .withValue(StructuredName.GIVEN_NAME, firstNameNew)
            ops.add(contactBuilder.build())
        }
        if (lastNameNew != "") {
            val contactBuilder = ContentProviderOperation
                .newUpdate(Data.CONTENT_URI)
                .withSelection(
                    (Data.RAW_CONTACT_ID + filler + Data.MIMETYPE + "=?"),
                    arrayOf(contactID, StructuredName.CONTENT_ITEM_TYPE)
                )
                .withValue(StructuredName.FAMILY_NAME, lastNameNew)
            ops.add(contactBuilder.build())
        }
    }

    private fun getWebsiteRowId(
        cr: ContentResolver,
        contactID: String?,
        websiteNew: String?,
        ops: ArrayList<ContentProviderOperation>
    ) {
        var webSiteRowId = ""
        val websiteCursor = cr.query(
            Data.CONTENT_URI,
            null,
            Data.CONTACT_ID + " = ? AND " + Data.MIMETYPE + "= ?",
            arrayOf(contactID, Website.CONTENT_ITEM_TYPE),
            null
        )
        while (websiteCursor!!.moveToNext()) {
            val websiteId = websiteCursor.getColumnIndex(Website._ID)
            webSiteRowId = websiteCursor.getString(websiteId)
        }
        websiteCursor.close()

        if (websiteNew != "" && webSiteRowId != "") {
            ops.add(
                ContentProviderOperation
                    .newUpdate(Data.CONTENT_URI)
                    .withSelection(
                        Website._ID + filler + Data.MIMETYPE + "=?",
                        arrayOf(webSiteRowId, Website.CONTENT_ITEM_TYPE)
                    )
                    .withValue(Website.DATA, websiteNew)
                    .withValue(Website.TYPE, Website.TYPE_WORK)
                    .build()
            )
        }
    }

    private fun getAddressRowId(
        cr: ContentResolver,
        contactID: String?,
        addressNew: String?,
        ops: ArrayList<ContentProviderOperation>
    ) {
        var addressRowId = ""
        val addressCursor = cr.query(
            StructuredPostal.CONTENT_URI,
            null,
            StructuredPostal.CONTACT_ID + " = ? ",
            arrayOf(contactID),
            null
        )
        while (addressCursor!!.moveToNext()) {
            val postalId: Int = addressCursor.getColumnIndex(StructuredPostal._ID)
            addressRowId = addressCursor.getString(postalId)
        }
        addressCursor.close()

        if (addressNew != "" && addressRowId != "") {
            ops.add(
                ContentProviderOperation
                    .newUpdate(Data.CONTENT_URI)
                    .withSelection(
                        StructuredPostal._ID + filler + Data.MIMETYPE + "=?",
                        arrayOf(addressRowId, StructuredPostal.CONTENT_ITEM_TYPE)
                    )
                    .withValue(StructuredPostal.TYPE, StructuredPostal.TYPE_HOME)
                    .withValue(StructuredPostal.STREET, addressNew)
                    .build()
            )
        }
    }

    private fun getEmailRowId(
        cr: ContentResolver,
        contactID: String?,
        mailNew: String?,
        ops: ArrayList<ContentProviderOperation>
    ) {
        var eMailRowId = ""
        val emailCursor = cr.query(
            Email.CONTENT_URI,
            null,
            Email.CONTACT_ID + " = ? ",
            arrayOf(contactID),
            null
        )
        while (emailCursor!!.moveToNext()) {
            val emailId: Int = emailCursor.getColumnIndex(Email._ID)
            eMailRowId = emailCursor.getString(emailId)
        }
        emailCursor.close()

        if (mailNew != "" && eMailRowId != "") {
            ops.add(
                ContentProviderOperation
                    .newUpdate(Data.CONTENT_URI)
                    .withSelection(
                        Email._ID + filler + Data.MIMETYPE + "=?",
                        arrayOf(eMailRowId, Email.CONTENT_ITEM_TYPE)
                    )
                    .withValue(Email.DATA, mailNew)
                    .withValue(Email.TYPE, Email.TYPE_HOME)
                    .build()
            )
        }
    }

    private fun handlePhoneDetails(
        phoneMobileNew: String?,
        ops: ArrayList<ContentProviderOperation>,
        phoneRowId: String,
        phoneWorkNew: String?,
        workPhoneRowId: String,
        phoneHomeNew: String?,
        homePhoneRowId: String
    ) {
        if (phoneMobileNew != "") {
            ops.add(
                ContentProviderOperation
                    .newUpdate(Data.CONTENT_URI)
                    .withSelection(
                        (Phone._ID + filler + Data.MIMETYPE + "=?"),
                        arrayOf(
                            phoneRowId,
                            Phone.CONTENT_ITEM_TYPE
                        )
                    )
                    .withValue(Phone.DATA, phoneMobileNew)
                    .withValue(
                        Phone.TYPE,
                        Phone.TYPE_MOBILE
                    )
                    .build()
            )
        }
        if (phoneWorkNew != "") {
            ops.add(
                ContentProviderOperation
                    .newUpdate(Data.CONTENT_URI)
                    .withSelection(
                        (Phone._ID + filler + Data.MIMETYPE + "=?"),
                        arrayOf(workPhoneRowId, Phone.CONTENT_ITEM_TYPE)
                    )
                    .withValue(Phone.DATA, phoneWorkNew)
                    .withValue(Phone.TYPE, Phone.TYPE_WORK)
                    .build()
            )
        }
        if (phoneHomeNew != "") {
            ops.add(
                ContentProviderOperation
                    .newUpdate(Data.CONTENT_URI)
                    .withSelection(
                        (Phone._ID + filler + Data.MIMETYPE + "=?"),
                        arrayOf(homePhoneRowId, Phone.CONTENT_ITEM_TYPE)
                    )
                    .withValue(Phone.DATA, phoneHomeNew)
                    .withValue(Phone.TYPE, Phone.TYPE_HOME)
                    .build()
            )
        }
    }

    private fun getEmailRowId(
        cr: ContentResolver,
        contactID: String?
    ): Triple<String, String, String> {
        var phoneRowId = ""
        var homePhoneRowId = ""
        var workPhoneRowId = ""
        val phoneCursor = cr.query(
            Phone.CONTENT_URI,
            null,
            (Phone.CONTACT_ID + " = ? "),
            arrayOf(contactID),
            null
        )
        while (phoneCursor!!.moveToNext()) {
            val pType: Int =
                phoneCursor.getColumnIndex(Phone.TYPE)
            val type = phoneCursor.getInt(pType)
            val pId: Int =
                phoneCursor.getColumnIndex(Phone._ID)
            if (Phone.TYPE_MOBILE == type) {
                phoneRowId = phoneCursor.getString(pId)
            }
            if (Phone.TYPE_HOME == type) {
                homePhoneRowId = phoneCursor.getString(pId)
            }
            if (Phone.TYPE_WORK == type) {
                workPhoneRowId = phoneCursor.getString(pId)
            }
        }
        phoneCursor.close()
        return Triple(phoneRowId, homePhoneRowId, workPhoneRowId)
    }

}
