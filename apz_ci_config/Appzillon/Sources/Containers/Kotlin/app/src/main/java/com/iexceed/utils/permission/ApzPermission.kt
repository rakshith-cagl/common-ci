package com.iexceed.utils.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.iexceed.common.AppzillonConstants
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.map.ApzMapPlugin
import com.iexceed.plugins.notification.NotificationPlugin

object ApzPermission {
    private var permissions = mutableListOf<String>()

    fun checkApzPermission(context: Context): Boolean {
        var granted = false

        if (AppzillonConstants.IS_TRACK_LOCATION || ApzMapPlugin.isPlugin()) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (NotificationPlugin.isPlugin
            && AppzillonConstants.IS_SERVER
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissions.isNotEmpty()) {
            for (permission in permissions) {
                granted = granted or (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED)
            }
        }
        return granted
    }

    fun requestDefaultPermissions(context: Context) {
        ActivityCompat.requestPermissions(context as Activity, permissions.toTypedArray(), PluginConstants.APZ_REQ_DEFAULT_PERMISSIONS)
    }
}
