package com.iexceed.plugins.contacts

import android.database.Cursor
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.webkit.WebView
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class SearchContact(val mCallbackId: String?,
                    val activity: ApzActivity<*>,
                    val webView: WebView,
                    val apzPluginUtil: IapzPluginUtil
) {

    val TAG = "SearchContact"

    fun searchContacts(searchCriteriaJsonObj: JSONObject?) {
        ApzLogger.i(TAG, "searchContacts")
        val searchCriteria: JSONObject?
        val firstName: String
        val lastName: String
        val phoneMobile: String
        val phoneHome: String
        val phoneWork: String
        try {
            searchCriteria = JSONObject(searchCriteriaJsonObj!!.getString("searchCriteria"))
            firstName = searchCriteria.getString("firstName").trim { it <= ' ' }
            lastName = searchCriteria.getString("lastName").trim { it <= ' ' }
            phoneMobile = searchCriteria.getString("phoneMobile").trim { it <= ' ' }
            phoneHome = searchCriteria.getString("phoneHome").trim { it <= ' ' }
            phoneWork = searchCriteria.getString("phoneWork").trim { it <= ' ' }
        } catch (e: JSONException) {
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-077", null, activity, webView, true)
            return
        } catch (e: Exception) {
            return
        }
        val searchResultTotal: JSONArray? =
            Filter(activity).filterContacts(firstName, lastName, phoneMobile, phoneHome, phoneWork)
        sendResult(searchResultTotal)

    }

    fun fetchAllContacts(mJsonObject: JSONObject?) {
        if (mJsonObject != null) {
            val dJson = JSONObject(mJsonObject.getString("details"))
            when(dJson.optString("sortOrder")){
                "firstName" ->
                    fetchContacts("firstName")
                "lastName" ->
                    fetchContacts("lastName")
                "userDefault" ->
                    fetchContacts("name")
                "" ->
                    fetchContacts("name")
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchContacts(sortOrder: String) {
        GlobalScope.launch {
            val contactsListAsync = async { getPhoneContacts(sortOrder) }
            val contactNumbersAsync = async { getContactNumbers() }
            val contactEmailAsync = async { getContactEmails() }

            val contacts = contactsListAsync.await()
            val contactNumbers = contactNumbersAsync.await()
            val contactEmails = contactEmailAsync.await()

            contacts.forEach {
                it.getString("contactID")
                contactNumbers[it.getString("contactID")]?.let { numbers ->
                    for(o in numbers){
                        try{
                            val type = o.getString("type")
                            val num = o.getString("number")
                            it.put(type,num)
                        }catch(e:JSONException){
                            ApzLogger.i(TAG,"$e.message")
                        }

                    }
                }
                contactEmails[it.getString("contactID")]?.let { emails ->
                    it.put("emails",JSONArray(emails))
                }
            }
            sendResult(JSONArray(contacts))
        }
    }

    private fun getPhoneContacts(sortOrder: String): ArrayList<JSONObject> {
        val contactsList = ArrayList<JSONObject>()
        val contactsCursor = activity.contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null)
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                val contact = JSONObject()
                contact.put("contactID",id)
                contact.put("name",name)
                val result = Filter(activity).getContactName(id,contact)
                contactsList.add(result)
            }
            contactsCursor.close()
        }
        contactsList.sortWith { p0, p1 ->
            // Here you could parse string id to integer and then compare.
            p0.getString(sortOrder).compareTo(p1.getString(sortOrder))
        }
        return contactsList
    }

    private suspend fun getContactNumbers(): HashMap<String, ArrayList<JSONObject>> {
        val contactsNumberMap = HashMap<String, ArrayList<JSONObject>>()
        val phoneCursor: Cursor? = activity.contentResolver.query(
            Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (phoneCursor != null && phoneCursor.count > 0) {
            val contactIdIndex = phoneCursor.getColumnIndex(Phone.CONTACT_ID)
            val numberIndex = phoneCursor.getColumnIndex(Phone.NUMBER)
            while (phoneCursor.moveToNext()) {
                val numbers = JSONObject()
                val contactId = phoneCursor.getString(contactIdIndex)
                val number: String = phoneCursor.getString(numberIndex)
                val cPTypeIndex = phoneCursor.getColumnIndex(Phone.TYPE)
                val type = phoneCursor.getInt(cPTypeIndex)
                when {
                    Phone.TYPE_HOME == type -> {
                        numbers.put("type", "phoneHome")
                        numbers.put("number", number)
                    }
                    Phone.TYPE_WORK == type -> {
                        numbers.put("type", "phoneWork")
                        numbers.put("number", number)
                    }
                    Phone.TYPE_MOBILE == type -> {
                        numbers.put("type", "phoneMobile")
                        numbers.put("number", number)
                    }
                }
                //check if the map contains key or not, if not then create a new array list with number
                if (contactsNumberMap.containsKey(contactId)) {
                    contactsNumberMap[contactId]?.add(numbers)
                } else {
                    contactsNumberMap[contactId] = arrayListOf(numbers)
                }

            }
            //contact contains all the number of a particular contact
            phoneCursor.close()
        }
        return contactsNumberMap
    }

    private suspend fun getContactEmails(): HashMap<String, ArrayList<String>> {
        val contactsEmailMap = HashMap<String, ArrayList<String>>()
        val emailCursor = activity.contentResolver.query(Email.CONTENT_URI,
            null,
            null,
            null,
            null)
        if (emailCursor != null && emailCursor.count > 0) {
            val contactIdIndex = emailCursor.getColumnIndex(Email.CONTACT_ID)
            val emailIndex = emailCursor.getColumnIndex(Email.ADDRESS)
            while (emailCursor.moveToNext()) {
                val contactId = emailCursor.getString(contactIdIndex)
                val email = emailCursor.getString(emailIndex)
                //check if the map contains key or not, if not then create a new array list with email
                if (contactsEmailMap.containsKey(contactId)) {
                    contactsEmailMap[contactId]?.add(email)
                } else {
                    contactsEmailMap[contactId] = arrayListOf(email)
                }
            }
            //contact contains all the emails of a particular contact
            emailCursor.close()
        }
        return contactsEmailMap
    }


    private fun sendResult(searchResultTotal: JSONArray?) {
        if (searchResultTotal == null || searchResultTotal.length() <= 0) {
            apzPluginUtil.sendError(
                mCallbackId,
                "APZ-CNT-044",
                null,
                activity,
                webView,
                true
            ) //Contact not found
            return
        }
        ApzLogger.i(TAG, "Contact Search Result")
        if (searchResultTotal.length() > 0) {
            val result = JSONObject()
            try {
                result.put("contacts", searchResultTotal)
            } catch (ex: Exception) {
                //Sonar fix
            }
            apzPluginUtil.sendSuccess(mCallbackId, result, false, activity, webView, true)
        } else {
            apzPluginUtil.sendError(
                mCallbackId,
                "APZ-CNT-044",
                null,
                activity,
                webView,
                true
            ) //No Contact Found.
        }
    }


}
