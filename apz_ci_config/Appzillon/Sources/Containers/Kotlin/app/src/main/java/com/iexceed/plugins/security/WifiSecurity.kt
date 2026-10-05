package com.iexceed.plugins.security

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.iexceed.common.ApzActivity
import com.iexceed.common.OnPermissionsResultHandler
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject

class WifiSecurity(
    var mActivity: ApzActivity<*>,
    var mWebView: WebView,
    mJsonObject: JSONObject,
    val apzPluginUtil: IapzPluginUtil
) {
    var mCallbackId: String? = null
    private var isGpsEnabled = false
    var wifi: WifiManager? = null
    private val TAG = "WifiSecurity"

    fun requestGPSpermission()
    {
        val lm = mActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            isGpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            //Sonar fix
        }
        wifi = mActivity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (ActivityCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestForPermission()
        } else {
            checkGPSStatus()
        }
    }

    private fun checkGPSStatus()
    {
        if (wifi?.isWifiEnabled == true) {
            if (isGpsEnabled) {
                checkWifiStatus()
            } else {
                sendFailure("GPS is not enabled in this device.")
            }
        } else {
            sendFailure("Wifi is not enabled in this device.")
        }
    }

    private fun checkWifiStatus()
    {
        val result = JSONObject()
        try {
            val networkList = wifi!!.scanResults
            val wi = wifi!!.connectionInfo
            val currentBSSID = wi.bssid
            if (networkList != null && !networkList.isEmpty()) {
                for (network in networkList) {
                    //check if current connected SSID
                    if (currentBSSID == network.BSSID) {
                        //get capabilities of current connection
                        val capabilities = network.capabilities
                        result.put("WifiStatus", capabilities)
                        ApzLogger.d(TAG, "WifiStatus :  $capabilities")
                        sendSuccess(result)
                    }
                }
            } else {
                sendFailure("Unable to fetch the details.")
            }
        } catch (e: Exception) {
            sendFailure("Unable to fetch the details.")
        }
    }

    private fun requestForPermission()
    {
        mActivity.startOnPermissionForResult(mActivity, arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION
        ), PluginConstants.APZ_REQ_FINE_LOCATION, object : OnPermissionsResultHandler() {
            override fun handlePermissionResult(
                requestCode: Int,
                permissions: Array<String?>,
                grantResults: IntArray
            ) {
                if (requestCode == PluginConstants.APZ_REQ_FINE_LOCATION) {
                    handlePermissionsResults(permissions)
                } else {
                    permissionDeniedCallback()
                }
            }
        })
    }

    private fun handlePermissionsResults(permissions: Array<String?>) {
        var denied = false
        var neverAskAgain = false
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity,
                    permission!!
                )
            ) {
                denied = true
            } else {
                if (ActivityCompat.checkSelfPermission(
                        mActivity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    neverAskAgain = true
                } else {
                    checkGPSStatus()
                }
            }
        }
        if (neverAskAgain) {
            permissionDeniedCallback()
        } else if (denied) {
            displayReconfirmationMessage()
        }
    }

    private fun displayReconfirmationMessage() {
        val message = "To get location, grant permission for app to access gps"
        val alertDialogBuilder = AlertDialog.Builder(mActivity)
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
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun permissionDeniedCallback() {
        apzPluginUtil.sendPermissionDenied("GPS location ", mCallbackId, mActivity, mWebView)
    }

    private fun sendFailure(msg: String) {
        val resultObj = JSONObject()
        try {
            resultObj.put("error", msg)
        } catch (ex: JSONException) {
            //Sonar fix
        }
        apzPluginUtil.sendError(mCallbackId, "", resultObj, mActivity, mWebView, true)
    }

    private fun sendSuccess(networkJson: JSONObject) {
        apzPluginUtil.sendSuccess(
            mCallbackId,
            networkJson, true, mActivity,
            mWebView, true
        )
    }

    init {
        try {
            mCallbackId = mJsonObject.getString("id")
        } catch (e: JSONException) {
            //Sonar fix
        }
    }
}
