package com.iexceed.plugins.notification

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.iexceed.appzillonapp.AppzillonMainScreen

class NotificationReceiver : BroadcastReceiver() {
    var appstatus = false
    var appNotRunning = 10
    var appInBackground = 1
    override fun onReceive(context: Context, intent: Intent) {
        appstatus = checkAppStatus(context)

        var actionCode: String? = null
        var notificationCode: String? = null
        var message: String? = ""
        var params: String? = ""
        var notifId = -1
        if (intent.hasExtra("action_code")) actionCode = intent.getStringExtra("action_code")
        notificationCode = intent.getStringExtra("notification_code")
        if (intent.hasExtra("message")) message = intent.getStringExtra("message")
        if (intent.hasExtra("notif_id")) notifId = intent.getIntExtra("notif_id", -1)
        if (intent.hasExtra("msgParameters")) params = intent.getStringExtra("msgParameters")
        if (appstatus) {
            if (AppzillonMainScreen.APP_LAUNCHED) {
                val brIntent = Intent()
                brIntent.putExtra("notification_code", notificationCode)
                brIntent.putExtra("action_code", actionCode)
                brIntent.putExtra("appstatus", appstatus)
                brIntent.putExtra("msgParameters", params)
                brIntent.putExtra("message", message)
                brIntent.putExtra("notif_id", notifId)
                brIntent.putExtra("title", intent.getStringExtra("title"))
                brIntent.putExtra("image_url", intent.getStringExtra("image_url"))
                (context as AppzillonMainScreen).viewModel.notificationListenerLiveData.value = brIntent
            }
        } else {
            val i = Intent(context, AppzillonMainScreen::class.java)
            //Natasha's changes 14-3-2018 for Bug 24249 started
            //Natasha's changes 14-3-2018 for Bug 24249 ends
            i.putExtra("action_code", actionCode)
            i.putExtra("notification_code", notificationCode)
            i.putExtra("message", message)
            i.putExtra("notif_id", notifId)
            i.putExtra("msgParameters", params)
            i.putExtra("title", intent.getStringExtra("title"))
            i.putExtra("image_url", intent.getStringExtra("image_url"))
            context.startActivity(i)
        }
    }

    private fun checkAppStatus(context: Context): Boolean {
        val flag = false
        val packageName = context.packageName
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = activityManager.runningAppProcesses

        for (processInfo in runningProcesses) {
            if (packageName == processInfo.processName) {
                return true
            }
        }
        return flag
    }
}