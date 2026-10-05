package com.iexceed.plugins.copystaticfiles

import android.content.Context
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.utils.localstorage.FileAccessHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
 class CopyStaticFiles(applicationContext: Context) {
    private var TAG = "CopyStaticFiles"
    var mContext: Context? = applicationContext

    fun CopyStaticFiles(context: Context) {
        mContext = context
    }

    fun copystatic(staticJson: JSONObject) {
        var filename: String
        var filepath: String
        val assetManager = mContext!!.assets
        var inputStrm: InputStream? = null
        var outStrm: OutputStream? = null
        try {
            val staticobj = staticJson.getJSONObject("Files")
            val iterator = staticobj.keys()
            while (iterator.hasNext()) {
                filename = iterator.next() as String
                filepath = staticobj.getString(filename)
                var basepath = ""
                basepath = if (filepath.equals("AppzillonRingtone", ignoreCase = true)) {
                    FileAccessHelper.getExternalFileDirFilePath(mContext!!, filepath)
                } else {
                    AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC + filepath
                }
                val clipartdir = File(basepath)
                if (!clipartdir.exists()) {
                    clipartdir.mkdirs()
                }
                val pathOut = "$basepath/$filename"
                try {
                    // Abhishek OTA
                    var pathIn: String? = null
                    //Abhishek 10 April 2015 Static files are not been copied into sandbox START

                    //Abhishek 10 April 2015 Static files are not been copied into sandbox END
                    run {
                        pathIn = AppzillonMainScreen.ASSET_APP_LOC + "staticfiles" + "/" + filename
                        inputStrm = assetManager.open(pathIn!!)
                    }
                    outStrm = FileAccessHelper.getFileOutPutStream(File(pathOut))
                    copy(inputStrm!!, outStrm)
                    inputStrm!!.close()
                    inputStrm = null
                    outStrm.flush()
                    outStrm.close()
                } catch (e: Exception) {
                   //handle exception
                }
            }
        } catch (e: JSONException) {
            //handle exception
        }
    }

    @Throws(IOException::class)
    private fun copy(inputStrm: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStrm.read(buffer).also { length = it } > 0) {
            out.write(buffer, 0, length)
        }
        out.flush()
        out.close()
        inputStrm.close()
    }

}