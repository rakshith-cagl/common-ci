package com.iexceed.plugins.utils

import android.util.Log
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector


object UiAutomatorUtils {
    const val TEXT_ALLOW = "Allow"
//    const val TEXT_DENY = "Deny"
    const val TEXT_DENY = "Don't allow"
    const val TEXT_NEVER_ASK_AGAIN = "Never ask again"
    const val TEXT_PERMISSIONS = "Permissions"

    // Navigation
    @Throws(UiObjectNotFoundException::class)
    fun openPermissions(device: UiDevice) {
        val permissions = device.findObject(UiSelector().text(TEXT_PERMISSIONS))
        permissions.click()
    }

    // Assertions
    fun assertViewWithTextIsVisible(device: UiDevice, text: String) {
        val allowButton = device.findObject(UiSelector().text(text))
        if (!allowButton.exists()) {
            throw AssertionError("View with text <$text> not found!")
        }else{
            Log.i("ABHISHEK","Found : $text");
        }
    }

    // Actions
    @Throws(UiObjectNotFoundException::class)
    fun allowCurrentPermission(device: UiDevice) {
        val allowButton = device.findObject(UiSelector().text(TEXT_ALLOW))
        allowButton.click()
    }

    @Throws(UiObjectNotFoundException::class)
    fun denyCurrentPermission(device: UiDevice) {
        val denyButton = device.findObject(UiSelector().text(TEXT_DENY))
        if (denyButton.exists()) {
            denyButton.click()
        }else{
            println("$TEXT_DENY not Found.")
        }

    }

    @Throws(UiObjectNotFoundException::class)
    fun denyCurrentPermissionPermanently(device: UiDevice) {
        val neverAskAgainCheckbox = device.findObject(UiSelector().text(TEXT_NEVER_ASK_AGAIN))
        if (!neverAskAgainCheckbox.exists()) {
//            throw AssertionError("View with text <$TEXT_NEVER_ASK_AGAIN> not found!")
            println("$TEXT_NEVER_ASK_AGAIN not Found.")
        }else{
            Log.i("ABHISHEK","Found : $TEXT_NEVER_ASK_AGAIN")
            neverAskAgainCheckbox.click()
        }
        denyCurrentPermission(device)
    }

    @Throws(UiObjectNotFoundException::class)
    fun grantPermission(device: UiDevice, permissionTitle: String?) {
        val permissionEntry = device.findObject(UiSelector().text(permissionTitle))
        permissionEntry.click()
    }
}