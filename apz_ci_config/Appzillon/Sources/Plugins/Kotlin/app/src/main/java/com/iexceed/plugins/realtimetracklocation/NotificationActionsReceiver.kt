package com.iexceed.plugins.realtimetracklocation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
class NotificationActionsReceiver: BroadcastReceiver()
{
    private fun stopService(context: Context?)
    {
        val intent = Intent(context, TrackLocationService::class.java)
        context?.stopService(intent)
    }

    override fun onReceive(context: Context?, intent: Intent?)
    {
        val action = intent?.getStringExtra ("action")
        if (action.equals("close")) {
            stopService(context)
        }
    }
}
