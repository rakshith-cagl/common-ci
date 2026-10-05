package com.iexceed.plugins.networkmonitor

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.webkit.WebView
import androidx.annotation.RequiresPermission
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject

class MonitorNetwork
@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
constructor(val aActivity: ApzActivity<*>,
            val aWebView: WebView,
            val callbackId: String,
            val apzPluginUtil: IapzPluginUtil)
{

    fun startNetworkCallback() {
        val cm: ConnectivityManager = aActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerDefaultNetworkCallback(networkCallback)
        }

    fun stopNetworkCallback() {
        val cm: ConnectivityManager = aActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.unregisterNetworkCallback(ConnectivityManager.NetworkCallback())
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            AppzillonConstants.isNetworkConnected = true
            sendCallback(true);
        }

        override fun onLost(network: Network) {
            AppzillonConstants.isNetworkConnected = false
            sendCallback(false)
        }
    }

    private fun sendCallback(b: Boolean) {
        val networkJson = JSONObject()
        if(b){
            networkJson.put("event", "on")
        }else{
            networkJson.put("event", "off")
        }
        apzPluginUtil.sendSuccess(callbackId, networkJson, true, aActivity, aWebView, true)
    }
}
