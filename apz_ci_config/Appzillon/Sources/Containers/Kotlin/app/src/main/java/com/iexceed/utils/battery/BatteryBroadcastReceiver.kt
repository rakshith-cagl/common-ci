package com.iexceed.utils.battery

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.UserSettings

class BatteryBroadcastReceiver(val activity: Activity, private val mEncryptedPref: SharedPreferences) :
    BroadcastReceiver() {
    /* For batteryListener */
    private var initialPercent = 0
    private var mHandler = Handler(Looper.getMainLooper())
    private var isInitialPercentSet = false
    override fun onReceive(context: Context, batteryIntent: Intent) {
        if (UserSettings.getAppValue(
                AppzillonMainScreen.APP_NAME,
                "allEvents",
                "off",
                mEncryptedPref
            ) == "on" || UserSettings.getAppValue(
                AppzillonMainScreen.APP_NAME,
                "batteryEvent",
                "off",
                mEncryptedPref
            ) == "on"
        ) {
            val batteryLevel = batteryIntent.getIntExtra("level", 0)
            if (!isInitialPercentSet) {
                initialPercent = batteryLevel
                isInitialPercentSet = true
            }
            if (batteryLevel - initialPercent > 0 && batteryLevel - initialPercent >= 5) {
                // update battery level
                initialPercent = batteryLevel
                mHandler.post {
                    //webView.loadUrl("javascript:batteryStateChange('" + batteryLevel + "');");
                }
            }
        }
    }
}