package com.iexceed.plugins.errorlog

import android.util.Log
import com.iexceed.plugins.ApzPlugin
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class ApzLogger {

    companion object{
        var LOG_LEVEL_FATAL = 0
        var LOG_LEVEL_ERROR = 1
        var LOG_LEVEL_WARN = 2
        var LOG_LEVEL_INFO = 3
        var LOG_LEVEL_DEBUG = 4
        var LOG_LEVEL_REQ_TIME = 5


        fun e(plugIn: String, action: String) {
            if (ApzPlugin.debugLevel >= LOG_LEVEL_ERROR) {
                Log.e(plugIn, action);
                appendLogger("E", plugIn, action)
            }
        }


        fun i(plugIn: String, action: String) {
            if (ApzPlugin.debugLevel >= LOG_LEVEL_INFO) {
                appendLogger("I", plugIn, action)
            }
        }

        fun d(plugIn: String, action: String) {
            if (ApzPlugin.debugLevel >= LOG_LEVEL_DEBUG) {
                Log.d(plugIn, action);
                appendLogger("D", plugIn, action)
            }
        }

        fun w(plugIn: String, action: String) {
            if (ApzPlugin.debugLevel >= LOG_LEVEL_WARN) {
                Log.w(plugIn, action);
                appendLogger("W", plugIn, action)
            }
        }

        fun v(plugIn: String, action: String) {
            if (ApzPlugin.debugLevel >= LOG_LEVEL_FATAL) {
                Log.v(plugIn, action);
                appendLogger("F", plugIn, action)
            }
        }

        fun initialTime(plugIn: String, action: String) {
            if (ApzPlugin.debugLevel >= LOG_LEVEL_REQ_TIME) {
                val currentTime: String =
                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                val lAction = "$action : $currentTime"
                Log.d(plugIn, lAction);
                appendLogger("T", plugIn, lAction)
            }
        }
        fun endTime(plugIn: String, action: String) {
            if (ApzPlugin.debugLevel >= LOG_LEVEL_REQ_TIME) {
                val currentTime: String =
                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                val lAction = "$action : $currentTime"
                Log.d(plugIn, lAction);
                appendLogger("T", plugIn, lAction)
            }
        }

        private fun appendLogger(type: String, plugin: String, message: String, ) {
            //Sonar fix
            Log.d("ApzLogger","$type$plugin$message")
        }
    }


}