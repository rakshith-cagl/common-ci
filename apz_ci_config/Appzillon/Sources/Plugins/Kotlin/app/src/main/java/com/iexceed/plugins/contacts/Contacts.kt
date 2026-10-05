package com.iexceed.plugins.contacts

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
class Contacts private constructor(val webView: WebView,
               val activity: ApzActivity<*>,
               override val apzPluginUtil: IapzPluginUtil
) : ApzPlugin() {

    private var mJsonObject: JSONObject? = null

    private var mCallbackId: String? = null

    private var mAction: String? = null

    lateinit var permissions: Array<String>

    override var TAG = "Contacts"

    init {
        super.aActivity = activity
        super.aWebview = webView
    }

    override fun execute(params: JSONObject) {
        try {
            mJsonObject = params
            mAction = params.getString("action")
            mCallbackId = params.getString("id")
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.WRITE_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissions = arrayOf<String>(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
                )
                requestForPermission()
            } else {
                proceedContacts()
            }
        } catch (e: JSONException) {
            ApzLogger.e(TAG, e.toString())
        }
    }

    private fun requestForPermission() {
        activity.startOnPermissionForResult(
            activity,
            permissions,
            PluginConstants.APZ_REQ_CONTACTS,
            object : OnPermissionsResultHandler() {
                override fun handlePermissionResult(
                    requestCode: Int,
                    permissions: Array<String?>,
                    grantResults: IntArray
                ) {
                    if (requestCode == PluginConstants.APZ_REQ_CONTACTS) {
                        handleContactPermissionResult(permissions)
                    } else {
                        permissionDeniedCallback()
                    }
                }
            }
        )
    }

    private fun handleContactPermissionResult(permissions: Array<String?>) {
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
                proceedContacts()
            }
        }
    }

    private fun displayReconfirmationMessage() {
        val message =
            "To add or edit contacts, allow app to access by granting requested permissions"
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


    private fun permissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied(TAG, mCallbackId, activity, webView)
    }


    private fun proceedContacts() {
        try{
            ApzLogger.i(TAG, "proceedContacts")
            when {
                mAction.equals("ADDCONTACT", ignoreCase = true) -> {
                    AddContact(mCallbackId, activity, webView,apzPluginUtil).addContacts(mJsonObject)
                }
                mAction.equals("DELETECONTACT", ignoreCase = true) -> {
                    DeleteContact(mCallbackId, activity, webView,apzPluginUtil).deleteContact(mJsonObject)
                }
                mAction.equals("EDITCONTACT", ignoreCase = true) -> {
                    EditContact(mCallbackId, activity, webView,apzPluginUtil).editContacts(mJsonObject)
                }
                mAction.equals("SEARCHCONTACT", ignoreCase = true) -> {
                    SearchContact(mCallbackId, activity, webView,apzPluginUtil).searchContacts(mJsonObject)
                }
                mAction.equals("FETCHCONTACT", ignoreCase = true) -> {
                    FetchContact(mCallbackId, activity, webView,apzPluginUtil).fetchContacts()
                }
                mAction.equals("FETCHALLCONTACTS", ignoreCase = true) -> {
                    SearchContact(mCallbackId, activity, webView,apzPluginUtil).fetchAllContacts(mJsonObject)
                }
            }
        }catch (e: Exception){
            apzPluginUtil.sendError(mCallbackId, "APZ-CNT-2362", null, activity, webView, true)
        }

    }


    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = Contacts(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isPlugin(): Boolean {
            return true
        }

    }


}
