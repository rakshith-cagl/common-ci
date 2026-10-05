package com.iexceed.retrofitmvvm.data.api

import android.content.Context
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import org.json.JSONObject
import java.io.*

object SSLCertificates {

    fun getCertificates(aContext: Context): List<String> {
        val br: BufferedReader
        val sb = java.lang.StringBuilder()
        var line: String?
        try {
            //For OTA, SANDBOX_LOC not initialized yet.Doesn't work for OTA.
            val inputStream =
                //aContext.assets.open(AppzillonMainScreen.ASSET_APP_LOC + "screens/config/certificates.json")
                aContext.assets.open("apps/"+ aContext.resources.getString(R.string.MAINAPPID) + "/" + "screens/config/certificates.json")

            br = BufferedReader(InputStreamReader(inputStream))
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            br.close()
            val certificateJson = JSONObject(sb.toString())

            val certificateList = certificateJson.getJSONArray("certificates")
            val certificatesArray = mutableListOf<String>()
            for (i in  0 until certificateList.length()){
                val certificateObject = certificateList.getJSONObject(i)
                val certificateName = certificateObject.getString("fileName")
                    .substringBeforeLast(".")
                certificatesArray.add(certificateName)
            }
            return certificatesArray.toList()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return listOf()
    }
}