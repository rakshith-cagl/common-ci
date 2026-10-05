package com.iexceed.common

import android.content.Context
import android.os.Environment
import com.iexceed.appzillonapp.AppzillonMainScreen
import java.io.File
import java.io.IOException

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
object MediaUtils {
    private const val TAG = "MediaUtils"

    /**
     * To check externalStorage(SD Card) availability
     * @return
     */
    val isSDCardPresent: Boolean get() {
        var mExternalStorageAvailable = false
        try {

            val state = Environment.getExternalStorageState()
            mExternalStorageAvailable = if (Environment.MEDIA_MOUNTED == state) {
                true
            } else if (Environment.MEDIA_MOUNTED_READ_ONLY == state) {
                false
            } else {
                false
            }
        }catch (e:Exception){
            //Sonar fix
        }
            return mExternalStorageAvailable

        }

    fun createDirIfNotExist(_path: String?):Boolean {
        val lf = File(_path)
        try {
            if (!lf.exists()) {
                return lf.mkdirs()
            }
        } catch (e: Exception) {
            //handle exception
        }
        return lf.exists()
    }

    fun deleteDirectory(path: File): Boolean {
        try {
            if (path.exists()) {
                val files = path.listFiles() ?: return true
                for (i in files.indices) {
                    if (files[i].isDirectory) {
                        files[i].deleteRecursively()
                    } else {
                        if (!files[i].delete()) {
                            //handle delete failure
                        }
                    }
                }
            }
        } catch (e: IOException) {
            //handle exeption
        }
        return path.delete()
    }

    fun hasExternalStoragePublicPicture(dirName: String, ctx: Context?): Boolean {
        // Create a path where we will place our picture in the user's
        // public pictures directory and check if the file exists.  If
        // external storage is not currently mounted this will think the
        // picture doesn't exist.
        val sdDir = File(AppzillonMainScreen.SANDBOX_LOC + "/" + dirName)
        return sdDir.exists()
    }
}
