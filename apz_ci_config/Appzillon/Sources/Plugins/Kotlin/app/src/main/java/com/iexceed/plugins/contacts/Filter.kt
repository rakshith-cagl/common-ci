package com.iexceed.plugins.contacts

import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.*
import android.util.Base64
import androidx.loader.content.CursorLoader
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class Filter(val activity: ApzActivity<*>) {

    val TAG = "FilterContact"

    private var searchValues: Map<String, String>? = null

    var sortOrder = StructuredName.DISPLAY_NAME

    private val like = " LIKE ?"

    /**
     * Returns list of displayNames present in contact
     * @return
     */
    fun filterContacts(
        firstName: String,
        lastName: String,
        phoneMobile: String,
        phoneHome: String,
        phoneWork: String
    ): JSONArray? {

        try {
            ApzLogger.i(TAG, "filterContacts")
            searchValues = HashMap<String, String>()
            (searchValues as HashMap<String, String>)["firstName"] = firstName
            (searchValues as HashMap<String, String>)["lastName"] = lastName
            (searchValues as HashMap<String, String>)["phoneMobile"] = phoneMobile
            (searchValues as HashMap<String, String>)["phoneWork"] = phoneWork
            (searchValues as HashMap<String, String>)["phoneHome"] = phoneHome
        } catch (e: java.lang.Exception) {
            ApzLogger.e(TAG, e.toString())
            return null
        }

        /* Search by Number */
        var genericSearchRes: JSONArray? = JSONArray()
        val searchString: String
        //		if(firstName.equals("") && lastName.equals("") && phoneMobile.equals("") && phoneHome.equals("")|| phoneWork.equals("")){
        if (firstName == "" && lastName == "" && phoneMobile == "" && phoneHome == "" && phoneWork == "") {
            searchString = firstName
            genericSearchRes = genericSearch(searchString, "")
        } else if (firstName != "") {
            searchString = firstName
            genericSearchRes = genericSearch(searchString, "firstName")
        } else if (lastName != "") {
            searchString = lastName
            genericSearchRes = genericSearch(searchString, "lastName")
        } else if (phoneMobile != "") {
            searchString = phoneMobile
            genericSearchRes = genericSearch(searchString, "phNo")
        } else if (phoneHome != "") {
            searchString = phoneHome
            genericSearchRes = genericSearch(searchString, "phNo")
        } else if (phoneWork != "") {
            searchString = phoneWork
            genericSearchRes = genericSearch(searchString, "phNo")
        }
        return genericSearchRes
    }

    /**
     * Returns list of names matching the search critera to
     * filterContacts(JSONObject) function
     *
     * @param name
     * @return JSONArray with search results
     */

    private fun genericSearch(name: String, criteria: String): JSONArray? {
        ApzLogger.i(TAG, "genericSearch")
        /* List will store all the displayName details */
        val localContentResolver = activity.contentResolver
        val displayNameList: MutableSet<String> = HashSet()
        if (criteria == "phNo") {
            try {
                name.toDouble()

                val contactLookupCursor = localContentResolver.query(
                    Uri.withAppendedPath(
                        ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                        Uri.encode(name)
                    ),
                    arrayOf(
                        ContactsContract.PhoneLookup.DISPLAY_NAME,
                        ContactsContract.PhoneLookup._ID
                    ),
                    null,
                    null,
                    null
                )
                try {
                    if (contactLookupCursor!!.moveToNext()) {
                        do {
                            val contactName = contactLookupCursor.getString(
                                contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME)
                            )
                            displayNameList.add(contactName)
                        } while (contactLookupCursor.moveToNext())
                        contactLookupCursor.close()
                    } else {
                        contactLookupCursor.close()
                        return null
                    }
                } catch (e: IllegalStateException) {
                    ApzLogger.e(TAG, e.toString())
                }
            } catch (e: NumberFormatException) {
                ApzLogger.e(TAG, e.toString())
                return null
            }
        } else if (displayNameList.isEmpty()) {
            val projection1: Array<String?>?
            val selection1: String?
            val selectionArgs1: Array<String>?

            when (criteria) {
                "" -> {
                    projection1 = arrayOf(StructuredName.DISPLAY_NAME)
                    selection1 = StructuredName.DISPLAY_NAME + like
                    selectionArgs1 = arrayOf(name.trim { it <= ' ' } + "%")
                }
                "firstName" -> {
                    projection1 = arrayOf(StructuredName.GIVEN_NAME, StructuredName.DISPLAY_NAME)
                    selection1 = StructuredName.GIVEN_NAME + like
                    selectionArgs1 = arrayOf(name.trim { it <= ' ' } + "%")
                }
                "lastName" -> {
                    projection1 = arrayOf(StructuredName.FAMILY_NAME, StructuredName.DISPLAY_NAME)
                    selection1 = StructuredName.FAMILY_NAME + like
                    selectionArgs1 = arrayOf(name.trim { it <= ' ' } + "%")
                }
                else -> return null
            }
            val nameCur = localContentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                projection1,
                selection1,
                selectionArgs1,
                sortOrder
            )
            if (nameCur!!.moveToNext()) {
                do {
                    val cIndex =
                        nameCur.getColumnIndex(StructuredName.DISPLAY_NAME)
                    val display = nameCur.getString(cIndex).trim { it <= ' ' }
                    displayNameList.add(display)
                } while (nameCur.moveToNext())
                nameCur.close()
            } else {
                nameCur.close()
                return null
            }
        }
        if (displayNameList.isEmpty()) {
            return null
        }

        return returnSearchResult(displayNameList, criteria)
    }

    private fun returnSearchResult(
        displayNameList: MutableSet<String>,
        criteria: String
    ): JSONArray? {
        val searchResultTotal = JSONArray()
        for (str in displayNameList) {
            val searchContacts: Uri = ContactsContract.Contacts.CONTENT_URI
            val projection =
                arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME)
            val selection = ContactsContract.Contacts.DISPLAY_NAME + like
            val selectionArgs = arrayOf(str.trim { it <= ' ' })
            val orderBy = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
            val cursorLoader = CursorLoader(
                activity,
                searchContacts,
                projection,
                selection,
                selectionArgs,
                orderBy
            )

            val c: Cursor? = cursorLoader.loadInBackground()

            if (c != null) {
                if (c.moveToFirst()) {
                    do {
                        var result = JSONObject()
                        val cContactsIndex = c.getColumnIndex(ContactsContract.Contacts._ID)
                        val contactID = c.getString(cContactsIndex)
                        val imageBase64: String = retrieveContactPhoto(contactID)
                        try {
                            result.put("contactID", contactID)

                            result = getEmails(contactID, result)
                            result = getAddress(contactID, result)
                            result = getWebSite(contactID, result)
                            result = getContactName(contactID, result)
                            /* Phone numbers */
                            result = getPhoneDetails(contactID, result)


                            /* before adding to list check other search criteria */
                            verifyMatchCriteria(result, criteria, imageBase64, searchResultTotal)
                            /* phones, Email,Address,Website end */
                        } catch (e: JSONException) {
                            ApzLogger.e(TAG, e.toString())
                            return null
                        }
                    } while (c.moveToNext())
                }
                c.close()
            }
        }
        return searchResultTotal
    }

    private fun verifyMatchCriteria(
        result: JSONObject,
        criteria: String,
        imageBase64: String,
        searchResultTotal: JSONArray
    ) {
        if (matchCriteria(result, criteria)) {
            result.put("encodedImage", imageBase64)
            searchResultTotal.put(result)
        }
    }

    private fun getEmails(contactID: String?, result: JSONObject): JSONObject {
        val emailCursor = activity.contentResolver
            .query(Email.CONTENT_URI, null, Email.CONTACT_ID + " = ? ", arrayOf(contactID), null)
        while (emailCursor!!.moveToNext()) {
            val cIndex = emailCursor.getColumnIndex(Email.DATA)
            val value = emailCursor.getString(cIndex)
            val cEmailIndex = emailCursor.getColumnIndex(Email.TYPE)
            val type = emailCursor.getInt(cEmailIndex)
            if (Email.TYPE_HOME == type) {
                result.put("email", value)
            }
        }
        emailCursor.close()
        if (!result.has("email")) {
            result.put("email", "")
        }
        return result
    }

    private fun getAddress(contactID: String?, result: JSONObject): JSONObject {
        val addressCursor = activity.contentResolver
            .query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + "= ?",
                arrayOf(contactID, StructuredPostal.CONTENT_ITEM_TYPE), null
            )
        while (addressCursor!!.moveToNext()) {
            val cPostalIndex = addressCursor.getColumnIndex(StructuredPostal.TYPE)
            val type = addressCursor.getInt(cPostalIndex)
            if (StructuredPostal.TYPE_HOME == type || StructuredPostal.TYPE_WORK == type || StructuredPostal.TYPE_OTHER == type) {
                val cStreetIndex = addressCursor.getColumnIndex(StructuredPostal.STREET)
                val street = addressCursor.getString(cStreetIndex)
                result.put("street", street)
            }
        }
        addressCursor.close()
        if (!result.has("street")) {
            result.put("street", "")
        }
        return result
    }

    private fun getWebSite(contactID: String?, result: JSONObject): JSONObject {
        val websiteCursor = activity.contentResolver
            .query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + "= ?",
                arrayOf(contactID, Website.CONTENT_ITEM_TYPE),
                null
            )
        while (websiteCursor!!.moveToNext()) {
            val cIndex: Int = websiteCursor.getColumnIndex(Website.TYPE)
            val type = websiteCursor.getInt(cIndex)
            if (Website.TYPE_HOME == type || Website.TYPE_WORK == type || Website.TYPE_OTHER == type) {
                val cURLIndex = websiteCursor.getColumnIndex(Website.URL)
                val websiteUrl = websiteCursor.getString(cURLIndex)
                result.put("website", websiteUrl)
            }
        }
        websiteCursor.close()
        if (!result.has("website")) {
            result.put("website", "")
        }
        return result
    }

    private fun getPhoneDetails(contactID: String?, json: JSONObject): JSONObject {
        val phoneCursor = activity.contentResolver
            .query(
                Phone.CONTENT_URI,
                null,
                Phone.CONTACT_ID + "= ?",
                arrayOf(contactID),
                null
            )
        while (phoneCursor!!.moveToNext()) {
            val cPnIndex =
                phoneCursor.getColumnIndex(Phone.NUMBER)
            val no = phoneCursor.getString(cPnIndex)
            val cPTypeIndex =
                phoneCursor.getColumnIndex(Phone.TYPE)
            val type = phoneCursor.getInt(cPTypeIndex)
            when {
                Phone.TYPE_HOME == type -> {
                    json.put("phoneHome", no)
                }
                Phone.TYPE_WORK == type -> {
                    json.put("phoneWork", no)
                }
                Phone.TYPE_MOBILE == type -> {
                    json.put("phoneMobile", no)
                }
            }
        }
        if (!json.has("phoneHome")) {
            json.put("phoneHome", "")
        }
        if (!json.has("phoneWork")) {
            json.put("phoneWork", "")
        }
        if (!json.has("phoneMobile")) {
            json.put("phoneMobile", "")
        }
        phoneCursor.close()
        return json
    }

    fun getContactName(contactID: String, sResult: JSONObject): JSONObject {
        val nameCursor = activity.contentResolver
            .query(
                ContactsContract.Data.CONTENT_URI,
                null,
                ContactsContract.Data.MIMETYPE + "= ? AND " + ContactsContract.RawContactsEntity.CONTACT_ID + "= ?",
                arrayOf(
                    StructuredName.CONTENT_ITEM_TYPE,
                    contactID
                ),
                null
            )
        while (nameCursor!!.moveToNext()) {
            val cNameIndex =
                nameCursor.getColumnIndex(StructuredName.GIVEN_NAME)
            val given = nameCursor.getString(cNameIndex)
            val cFamilyIndex =
                nameCursor.getColumnIndex(StructuredName.FAMILY_NAME)
            val family = nameCursor.getString(cFamilyIndex)
            if (given != null) {
                sResult.put("firstName", given)
            }
            if (family != null) {
                sResult.put("lastName", family)
            }
        }
        nameCursor.close()
        if (!sResult.has("firstName")) {
            sResult.put("firstName", "")
        }
        if (!sResult.has("lastName")) {
            sResult.put("lastName", "")
        }
        return sResult
    }

    /* Used to narrow down the search criteria */
    private fun matchCriteria(searchResult: JSONObject, criteria: String): Boolean {
        ApzLogger.i(TAG, "matchCriteria")
        try {
            if (criteria == "firstName") {
                if (handleFirstNameCriteria(searchResult)) return true
            } else if (criteria == "lastName") {
                if (handleLastNameCriteria(searchResult)) return true
            } else if (criteria == "phNo" && ((searchResult.getString("phoneHome").equals(
                    searchValues!!["phoneHome"], ignoreCase = true
                ) || searchValues!!["phoneHome"].equals(""))
                        && (searchResult.getString("phoneWork").equals(
                    searchValues!!["phoneWork"], ignoreCase = true
                ) || searchValues!!["phoneWork"].equals("")))
            ) {
                return true
            }
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }
        return false
    }

    private fun handleLastNameCriteria(searchResult: JSONObject): Boolean {
        if ((searchResult.getString("phoneMobile").equals(
                searchValues!!["phoneMobile"], ignoreCase = true
            ) || searchValues!!["phoneMobile"].equals(""))
            && (searchResult.getString("phoneHome").equals(
                searchValues!!["phoneHome"], ignoreCase = true
            ) || searchValues!!["phoneHome"].equals(""))
            && (searchResult.getString("phoneWork").equals(
                searchValues!!["phoneWork"], ignoreCase = true
            ) || searchValues!!["phoneWork"].equals(""))
        ) {
            return true
        }
        return false
    }

    private fun handleFirstNameCriteria(searchResult: JSONObject): Boolean {
        if ((searchResult.getString("lastName")
                .contains(searchValues!!["lastName"].toString()) || searchValues!!["lastName"]
                .equals(""))
            && (searchResult.getString("phoneMobile").equals(
                searchValues!!["phoneMobile"], ignoreCase = true
            ) || searchValues!!["phoneMobile"].equals(""))
            && (searchResult.getString("phoneHome").equals(
                searchValues!!["phoneHome"], ignoreCase = true
            ) || searchValues!!["phoneHome"].equals(""))
            && (searchResult.getString("phoneWork").equals(
                searchValues!!["phoneWork"], ignoreCase = true
            ) || searchValues!!["phoneWork"].equals(""))
        ) {
            return true
        }
        return false
    }

    fun retrieveContactPhoto(contactID: String): String {
        val photo: Bitmap?
        var encoded = ""
        try {
            val inputStream: InputStream? = ContactsContract.Contacts.openContactPhotoInputStream(
                activity.contentResolver,
                ContentUris.withAppendedId(
                    ContactsContract.Contacts.CONTENT_URI,
                    contactID.toLong()
                )
            )
            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream)
                val byteArrayOutputStream = ByteArrayOutputStream()
                photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
                inputStream.close()
            } else {
                encoded = ""
            }
        } catch (e: IOException) {
            //Sonar fix
        }
        return encoded
    }
}