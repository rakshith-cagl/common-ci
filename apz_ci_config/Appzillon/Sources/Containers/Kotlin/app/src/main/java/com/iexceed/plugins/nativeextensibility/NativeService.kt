package com.iexceed.plugins.nativeextensibility

import android.app.Activity
import android.webkit.WebView
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject

class NativeService {

    companion object{
        

        fun nativeServiceEntry( webView: WebView,  activity: Activity,params: JSONObject?, apzPluginUtil: IapzPluginUtil): JSONObject? {
            var json: JSONObject? = null

            //your code goes here

            return json
        }
    }


}



