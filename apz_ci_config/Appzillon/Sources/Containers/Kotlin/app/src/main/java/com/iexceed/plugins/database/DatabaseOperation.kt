package com.iexceed.plugins.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.webkit.WebView
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.iexceed.common.ApzActivity
import com.iexceed.db.RoomAppDb
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONObject

class DatabaseOperation private constructor(webView: WebView, activity: ApzActivity<*>,
                                            override val apzPluginUtil: IapzPluginUtil) :
    ApzPlugin()
{
    private val errorMessage = " column and data must have same size"
    private val errorDataMessage = " data cannot be empty "
    private val createDbSuccessMessage = " CreateDB success "
    private val errorTableMessage = " Table name cannot be empty "
    private val errorDbVersionMessage = " db_version cannot be "
    private val errorColumnsEmptyMessage = " columns cannot be empty "
    override fun execute(params: JSONObject) {
        val callbackId = params!!["id"] as String
        this.callbackId = callbackId
        val action = params["action"] as String
        val gson = Gson()
        when (action)
        {
            "CREATE_DB" -> {
                //ErrorCases
                performCreateDB(gson, params, callbackId, action)
            }

            "INSERT_DB" -> {
                //ErrorCases
                performInsertDB(gson, params, callbackId, action)
            }

            "UPDATE_DB" -> {
                //ErrorCases
                performUpdateDB(gson, params, callbackId, action)
            }

            "DELETE_DB" -> {
                val res = JSONObject()
                //ErrorCases
                try {
                    val lData: DeleteData = gson.fromJson(params.toString(), DeleteData::class.java)
                    when {
                       /* lData.columns.isNullOrEmpty() -> {
                            res.put("message", " columns cannot be empty ")
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        lData.data.isNullOrEmpty() -> {
                            res.put("message", " data cannot be empty ")
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        lData.data.size != lData.columns.size -> {
                            Log.d(DatabaseOperation.TAG, "column : ${lData.columns.size}")
                            Log.d(DatabaseOperation.TAG, "columnType ${lData.data.size}")
                            res.put("message", " column and data must have same size")
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }*/

                        lData.dbVersion == 0 -> {
                            res.put("message", errorDbVersionMessage +lData.dbVersion)
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        lData.tableName.isNullOrEmpty() -> {
                            res.put("message", errorTableMessage)
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        lData.condition.isNullOrEmpty() -> {
                            res.put("message", " condition cannot be empty ")
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        //Everything ok..
                        else -> {
                            res.put("message", createDbSuccessMessage)
                            deleteTable(callbackId, action, res, lData)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    res.put("message", e.message)
                    apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                }
            }

            "MIGRATE_DB" -> {
                val res = JSONObject()
                //ErrorCases
                try {
                    val lData: MigrateDbData = gson.fromJson(params.toString(), MigrateDbData::class.java)
                    when {
                        lData.operation.isNullOrEmpty() -> {
                            res.put("message", " operation cannot be empty ")
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        lData.oldDbVersion == 0 -> {
                            res.put("message", errorDbVersionMessage +lData.oldDbVersion)
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        lData.tableName.isNullOrEmpty() -> {
                            res.put("message", errorTableMessage)
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        //Everything ok..
                        else -> {
                            res.put("message", " CREATE_DB Success")
                            migrateDb(callbackId, action, res, lData)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    res.put("message", e.message)
                    apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                }
            }

            "GET_FROM_DB" -> {
                //ErrorCases
                val res = JSONObject()
                try {
                    val lData: GetFromDBData = gson.fromJson(params.toString(), GetFromDBData::class.java)
                    when {
                        lData.columns.isNullOrEmpty() -> {
                            res.put("message", errorColumnsEmptyMessage)
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        lData.dbVersion == 0 -> {
                            res.put("message", errorDbVersionMessage +lData.dbVersion)
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        lData.tableName.isNullOrEmpty() -> {
                            res.put("message", " tableName cannot be empty ")
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        lData.condition.isNullOrEmpty() -> {
                            res.put("message", " condition cannot be empty ")
                            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        }

                        //Everything ok..
                        else -> {
                            res.put("message", " Get records Success")
                            getDataFromTable(callbackId, action, res, lData)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    res.put("message", e.message)
                    apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                }
            }

        }
    }

    private fun performUpdateDB(
        gson: Gson,
        params: JSONObject,
        callbackId: String,
        action: String
    ) {
        val res = JSONObject()
        try {
            val lData: UpdateData = gson.fromJson(params.toString(), UpdateData::class.java)
            Log.d(DatabaseOperation.TAG, "inside : execute lData $lData")
            when {
                lData.columns.isNullOrEmpty() -> {
                    res.put("message", errorColumnsEmptyMessage)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.data.isNullOrEmpty() -> {
                    res.put("message", errorDataMessage)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.data.size != lData.columns.size -> {
                    Log.d(DatabaseOperation.TAG, "column : ${lData.columns.size}")
                    Log.d(DatabaseOperation.TAG, "columnValue ${lData.data.size}")
                    res.put("message", errorMessage)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.dbVersion == 0 -> {
                    res.put("message", errorDbVersionMessage + lData.dbVersion)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.tableName.isNullOrEmpty() -> {
                    res.put("message", errorTableMessage)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                //Everything ok..
                else -> {
                    res.put("message", createDbSuccessMessage)
                    updateTable(callbackId, action, res, lData)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            res.put("message", e.message)
            apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
        }
    }

    private fun performInsertDB(
        gson: Gson,
        params: JSONObject,
        callbackId: String,
        action: String
    ) {
        val res = JSONObject()
        try {
            val lData: InsertData = gson.fromJson(params.toString(), InsertData::class.java)
            Log.d(DatabaseOperation.TAG, "inside : execute lData $lData")
            when {
                lData.columns.isNullOrEmpty() -> {
                    res.put("message", errorColumnsEmptyMessage)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.data.isNullOrEmpty() -> {
                    res.put("message", errorDataMessage)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.data.size != lData.columns.size -> {
                    Log.d(DatabaseOperation.TAG, "column : ${lData.columns.size}")
                    Log.d(DatabaseOperation.TAG, "column value ${lData.data.size}")
                    res.put("message", errorMessage)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.dbVersion == 0 -> {
                    res.put("message", errorDbVersionMessage + lData.dbVersion)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.tableName.isNullOrEmpty() -> {
                    res.put("message", errorTableMessage)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                //Everything ok..
                else -> {
                    res.put("message", createDbSuccessMessage)
                    insertToTable(callbackId, action, res, lData)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            res.put("message", e.message)
            apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
        }
    }

    private fun performCreateDB(
        gson: Gson,
        params: JSONObject,
        callbackId: String,
        action: String
    ) {
        val res = JSONObject()
        try {
            val lData: CreateDbData = gson.fromJson(params.toString(), CreateDbData::class.java)
            Log.d(DatabaseOperation.TAG, "inside : execute lData $lData")
            when {
                lData.columns.isNullOrEmpty() -> {
                    res.put("message", errorColumnsEmptyMessage)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.data.isNullOrEmpty() -> {
                    res.put("message", " columnType cannot be empty ")
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.data.size != lData.columns.size -> {
                    Log.d(DatabaseOperation.TAG, "column : ${lData.columns.size}")
                    Log.d(DatabaseOperation.TAG, "columnType ${lData.data.size}")
                    res.put("message", " column and columnType must have same size")
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.dbVersion == 0 -> {
                    res.put("message", errorDbVersionMessage + lData.dbVersion)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.primaryKey.isNullOrEmpty() -> {
                    res.put("message", " primaryKey cannot be empty ")
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                lData.tableName.isNullOrEmpty() -> {
                    res.put("message", errorTableMessage)
                    apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
                }

                //Everything ok..
                else -> {
                    res.put("message", createDbSuccessMessage)
                    createTable(callbackId, action, res, lData)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            res.put("message", e.message)
            apzPluginUtil.sendError(callbackId, action, res, aActivity, aWebview, true)
        }
    }

    private fun getDataFromTable(
        callbackId: String,
        action: String,
        res: JSONObject,
        lData: GetFromDBData
    ) {
        try {
            val lDb =  RoomAppDb.getDb(aActivity.applicationContext)
            var lColumnsList = String()
            for(item in lData.columns)
            {
                if(!item.isNullOrEmpty()){
                    lColumnsList+= "$item,"
                }
            }

            if(lColumnsList.endsWith(",")){
                lColumnsList = lColumnsList.substring(0, lColumnsList.length - 1)
            }
            val ldbCursor: Cursor = lDb.query(
                "SELECT $lColumnsList FROM ${lData.tableName} Where ${lData.condition}")
            ldbCursor.moveToFirst()
            if (ldbCursor.count > 0){
                res.put("message", "get data success : records found : "+ldbCursor.count)
                apzPluginUtil.sendSuccess(callbackId, res, false, aActivity, aWebview, true)
            }else{
                res.put("message", "No data found ")
                apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
            }
        }catch (e:Exception){
            e.printStackTrace()
            res.put("message", e.message)
            apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
        }

    }

    private fun migrateDb(
        callbackId: String,
        action: String,
        res: JSONObject,
        lData: MigrateDbData)
    {
        try {
            val lDb = RoomAppDb.getDb(aActivity.applicationContext)
            when (lData.operation)
            {
                "add" -> {
                    handleAddOperation(lData, res, action, lDb, callbackId)
                }

                "rename_column" -> {
                    //RENAME COLUMN TO
                    handleColumnRename(lData, res, action, lDb, callbackId)
                }

                "remove_column" -> {
                    //DROP COLUMN
                    if(lData.columns.isNullOrEmpty()) {
                        res.put("message", errorColumnsEmptyMessage)
                        apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        return
                    }

                    lData.columns.forEachIndexed { _, element ->
                        if(element.isNullOrEmpty()){
                            res.put("message", "column cannot be empty success")
                            apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
                            return
                        }
                        val queryString = "ALTER TABLE ${lData.tableName} DROP COLUMN $element"
                        lDb.execSQL(queryString)
                    }

                    res.put("message", "Remove column success")
                    apzPluginUtil.sendSuccess(callbackId, res, false, aActivity, aWebview, true)
                }

                "rename_table" -> {
                    //RENAME TO
                    if(lData.newTableName.isNullOrEmpty()){
                        res.put("message", "New table name cannot be empty")
                        apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        return
                    }

                    if(lData.tableName.isNullOrEmpty()){
                        res.put("message", "Old table name cannot be empty")
                        apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
                        return
                    }

                    val queryString = "ALTER TABLE ${lData.tableName} RENAME TO  ${lData.newTableName}"
                    lDb.execSQL(queryString)
                    res.put("message", "Rename table success")
                    apzPluginUtil.sendSuccess(callbackId, res, false, aActivity, aWebview, true)
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
            res.put("message", e.message)
            apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
        }
    }

    private fun handleColumnRename(
        lData: MigrateDbData,
        res: JSONObject,
        action: String,
        lDb: SupportSQLiteDatabase,
        callbackId: String
    ) {
        if (validateData(lData, res, action)) {
            var lColumnsList = ""
            lData.columns.forEachIndexed { index, element ->

                if (element.isNullOrEmpty() || lData.data[index].isNullOrEmpty()) {
                    res.put("message", "field cannot be empty success")
                    apzPluginUtil.sendError(this.callbackId, action, res, aActivity, aWebview, true)
                    return
                }

                lColumnsList = element + " TO " + lData.data[index]

                val queryString = "ALTER TABLE ${lData.tableName} RENAME COLUMN " + lColumnsList
                lDb.execSQL(queryString)
            }

            res.put("message", "migrate Rename Column success")
            apzPluginUtil.sendSuccess(callbackId, res, false, aActivity, aWebview, true)
        }
    }

    private fun handleAddOperation(
        lData: MigrateDbData,
        res: JSONObject,
        action: String,
        lDb: SupportSQLiteDatabase,
        callbackId: String
    ) {
        if (validateData(lData, res, action)) {
            //ADD COLUMN
            var lColumnsList = ""
            lData.columns.forEachIndexed { index, element ->
                if (element.isNullOrEmpty() || lData.data[index].isNullOrEmpty()) {
                    res.put("message", "fields cannot be empty success")
                    apzPluginUtil.sendError(this.callbackId, action, res, aActivity, aWebview, true)
                    return
                }

                if (lData.data[index].lowercase() == "text") {
                    lColumnsList = element + " " + lData.data[index] + " DEFAULT ''"
                }
                if (lData.data[index].lowercase() == "integer" ||
                    lData.data[index].lowercase() == "numeric"
                ) {
                    lColumnsList = element + " " + lData.data[index] + " DEFAULT 0"
                }

                val queryString = "ALTER TABLE ${lData.tableName} ADD COLUMN " + lColumnsList
                lDb.execSQL(queryString)
            }

            res.put("message", "migrate success")
            apzPluginUtil.sendSuccess(callbackId, res, false, aActivity, aWebview, true)
        }
    }

    private fun validateData(lData: MigrateDbData, res: JSONObject, action: String):Boolean
    {
        if(lData.columns.isNullOrEmpty()) {
            res.put("message", errorColumnsEmptyMessage)
            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
            return false
        }

        if(lData.data.isNullOrEmpty()) {
            res.put("message", errorDataMessage)
            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
            return false
        }

        if(lData.data.size != lData.columns.size) {
            Log.d(DatabaseOperation.TAG, "column : ${lData.columns.size}")
            Log.d(DatabaseOperation.TAG, "columnType ${lData.data.size}")
            res.put("message", errorMessage)
            apzPluginUtil.sendError(callbackId, action, res,  aActivity, aWebview, true)
            return false
        }
        return true
    }

    private fun updateTable(
        callbackId: String,
        action: String,
        res: JSONObject,
        lData: UpdateData)
    {
        try {
            val lDb = RoomAppDb.getDb(aActivity.applicationContext)
            val values = ContentValues()

            lData.columns.forEachIndexed { index, element ->
                Log.d(TAG, "columnType Data ${lData.data[index]}")
                Log.d(TAG, "columnType forEachIndexed $element")
                if(element.isNullOrEmpty()){
                    res.put("message", "column cannot be empty success")
                    apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
                    return
                }
                values.put(element, lData.data[index])
            }
            val lWhereClause = lData.condition
            val id = lDb.update(lData.tableName,
                SQLiteDatabase.CONFLICT_REPLACE,
                values, lWhereClause, null)

            if (id > -1){
                res.put("message", "Update success")
                apzPluginUtil.sendSuccess(callbackId, res, false, aActivity, aWebview, true)
            }else{
                res.put("message", "Update failed ")
                apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
            }
        }catch (e:Exception){
            e.printStackTrace()
            res.put("message", e.message)
            apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
        }
    }


    private fun insertToTable(
        callbackId: String,
        action: String,
        res: JSONObject,
        lData: InsertData)
    {
        try {
            val lDb = RoomAppDb.getDb(aActivity.applicationContext)
            val values = ContentValues()

            lData.columns.forEachIndexed { index, element ->
                if(element.isNullOrEmpty()){
                    res.put("message", "fields cannot be empty success")
                    apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
                    return
                }
                values.put(element, lData.data[index])
            }
            val id = lDb.insert(lData.tableName, SQLiteDatabase.CONFLICT_REPLACE, values)
            if (id > -1){
                res.put("message", "Insert success")
                apzPluginUtil.sendSuccess(callbackId, res, false, aActivity, aWebview, true)
            }else{
                res.put("message", "Insert failed ")
                apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
            }
        }catch (e:Exception){
            e.printStackTrace()
            res.put("message", e.message)
            apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
        }
    }

    private fun deleteTable(
        callbackId: String,
        action: String,
        res: JSONObject,
        lData: DeleteData
    ) {
        try {
            val lDb =  RoomAppDb.getDb(aActivity.applicationContext)
            val id = lDb.delete(lData.tableName,  lData.condition, null)
            if (id > 0){
                res.put("message", "Delete success")
                apzPluginUtil.sendSuccess(callbackId, res, false, aActivity, aWebview, true)
            }else{
                res.put("message", "Delete failed ")
                apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
            }
        }catch (e:Exception){
            e.printStackTrace()
            res.put("message", e.message)
            apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
        }
    }


    private fun createTable(
        callbackId: String,
        action: String,
        res: JSONObject,
        lData: CreateDbData
    ) {
        try {
            val lDb =  RoomAppDb.getDb(aActivity.applicationContext)
            var lColumnsList = String()

            lData.columns.forEachIndexed { index, element ->
                if(element.isNullOrEmpty() || lData.data[index].isNullOrEmpty()){
                    res.put("message", "field cannot be empty success")
                    apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
                    return
                }
                lColumnsList+= element + " "+ lData.data[index] +", "
            }

            if(lColumnsList.endsWith(", ")){
                lColumnsList = lColumnsList.substring(0, lColumnsList.length - 2)
            }

            val queryString = "CREATE TABLE IF NOT EXISTS ${lData.tableName}" + " ("+ lColumnsList + ");"
            lDb.execSQL(queryString)
            res.put("message", "Create DB success")
            apzPluginUtil.sendSuccess(callbackId, res, false, aActivity, aWebview, true)
        } catch (e:Exception){
            e.printStackTrace()
            res.put("message", e.message)
            apzPluginUtil.sendError(this.callbackId, action, res,  aActivity, aWebview, true)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var pluginObj: ApzPlugin? = null
        var TAG = "DbOperationPlugin"
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = DatabaseOperation(webView, activity, apzPluginUtil)
            }
            ApzLogger.d(TAG, "createPlugin : pluginObj $pluginObj")
            return pluginObj
        }
    }

    init {
        super.aActivity = activity
        super.aWebview = webView
    }
}