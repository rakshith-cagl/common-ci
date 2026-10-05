package com.iexceed.common

import android.content.Context
import com.iexceed.common.ServerUtilities.register
import com.iexceed.common.StringUtils.getString
import com.iexceed.common.UserSettings.getNotificationToken
import com.iexceed.utils.localstorage.EncryptedPrefHelper

class NotificationRegistration(val aContext:Context)
{
    fun execute()
    {
        register(
            aContext,
            getNotificationToken(getString(StringUtils.APP_ID),
                EncryptedPrefHelper.init(aContext)))
    }
}
