package com.iexceed.plugins.storage

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import org.json.JSONObject
import org.json.JSONException
import com.iexceed.plugins.errorlog.ApzLogger
import android.database.sqlite.SQLiteDatabase
import android.webkit.WebView
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONArray

class StoragePlugin private constructor(webView: WebView, activity: ApzActivity<*>,
                                        override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin()
{
    private var queryResult: String? = null

    init {
        super.aActivity = activity
        super.aWebview = webView
    }


    /**
     * executes query on given Database
     * @param jsonObj
     */
    fun executeSQL(jsonObj: JSONObject) {
        var databaseName: String? = ""
        var executeQuery = ""
        try {
            callbackId = jsonObj.getString("id")
            databaseName = jsonObj.getString("databaseName")
            executeQuery = jsonObj.getString("executeQuery")
        } catch (e: JSONException) {
            ApzLogger.i("StoragePlugin", e.toString())
        }
        var myDataBase: SQLiteDatabase? = null
        var selectCursor: Cursor? = null
        val dataRetrieval = executeQuery.split(" ".toRegex()).toTypedArray()[0].toLowerCase()
        try {
            myDataBase = aActivity.applicationContext
                .openOrCreateDatabase(databaseName, Context.MODE_PRIVATE, null)
            if (dataRetrieval.equals("select", ignoreCase = true)) {
                // for DRL query
                selectCursor = myDataBase.rawQuery(executeQuery, null)
                if (selectCursor != null) {
                    queryResult = processCursorResults(selectCursor)
                    selectCursor.close()
                    var result: JSONObject? = null
                    try {
                        result = JSONObject()
                        result.put("sqlResult", queryResult)
                    } catch (e: JSONException) {
                        ApzLogger.i("StoragePlugin", e.toString())
                    }
                    apzPluginUtil.sendSuccess(
                        callbackId,
                        result,
                        false,
                        this.aActivity,
                        this.aWebview,
                        true
                    )
                }
            } else {
                // for ddl commands
                myDataBase.execSQL(executeQuery)
                var result: JSONObject? = null
                try {
                    result = JSONObject()
                    result.put("sqlResult", "success")
                } catch (ex: JSONException) {
                    ApzLogger.i("StoragePlugin", ex.toString())
                }
                apzPluginUtil.sendSuccess(
                    callbackId,
                    result,
                    false,
                    this.aActivity,
                    this.aWebview,
                    true
                )
            }
        } catch (e: SQLException) {
            ApzLogger.i("StoragePlugin", e.toString())
            val json = JSONObject()
            try {
                json.put("errorMessage", e.toString())
            } catch (e1: JSONException) {
                ApzLogger.i("StoragePlugin", e.toString())
            }
            apzPluginUtil.sendError(
                callbackId,
                "APZ-CNT-056",
                json,
                this.aActivity,
                this.aWebview,
                true
            ) //Incorrect Query
        } finally {
            myDataBase?.close()
        }
    }

    /**
     * Prepares json array from Cursor object
     * @param cur
     * @return JSONArray as String
     */
    fun processCursorResults(cur: Cursor): String {
        var result = "[]"
        if (cur.moveToFirst()) {
            val fullresult = JSONArray()
            var key: String? = ""
            var value: String? = ""
            val colCount = cur.columnCount

            // Build up JSON result object for each row
            do {
                val row = JSONObject()
                try {
                    for (i in 0 until colCount) {
                        key = cur.getColumnName(i)
                        value = cur.getString(i)
                        row.put(key, value)
                    }
                    fullresult.put(row)
                } catch (e: JSONException) {
                    ApzLogger.i("StoragePlugin", e.toString())
                }
            } while (cur.moveToNext())
            result = fullresult.toString()
        }
        return result
    }

    override fun execute(params: JSONObject) {
        executeSQL(params)
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = StoragePlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun isStoragePlugin(): Boolean {
            return true
        }
    }
}