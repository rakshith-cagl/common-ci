package com.iexceed.plugins.storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.webkit.WebView;

public class StoragePlugin extends ApzPlugin{
	
	private String queryResult;
	
	private static ApzPlugin pluginObj;
	
	private StoragePlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity){
		if(pluginObj == null){
		pluginObj = new StoragePlugin(webView, activity);
		}
		return pluginObj;}
	/**
	 * executes query on given Database
	 * @param jsonObj
	 */
	public void executeSQL(JSONObject jsonObj) {
		String databaseName = "";
		String executeQuery = "";
		try {
			callbackId = jsonObj.getString("id");
			databaseName = jsonObj.getString("databaseName");
			executeQuery = jsonObj.getString("executeQuery");
		} catch (JSONException e) {
			ApzLogger.i("StoragePlugin",e.toString());
		}
		
		SQLiteDatabase myDataBase = null;
		Cursor selectCursor = null;
		final String dataRetrieval = executeQuery.split(" ")[0].toLowerCase();
		try {
			myDataBase = activity.getApplicationContext().openOrCreateDatabase(databaseName,Context.MODE_PRIVATE, null);
			if (dataRetrieval.equalsIgnoreCase("select")) {
				// for DRL query
				selectCursor = myDataBase.rawQuery(executeQuery, null);
				if (selectCursor != null) {
					queryResult = processCursorResults(selectCursor);
					selectCursor.close();
					JSONObject result = null;
					try {
						result = new JSONObject();
						result.put("sqlResult", queryResult);
					} catch (JSONException e) {
						ApzLogger.i("StoragePlugin",e.toString());
					}
					ApzPluginUtil.sendSuccess(this.callbackId, result, false, this.activity , this.webView, true);

			} }else {
				// for ddl commands
				myDataBase.execSQL(executeQuery);
				JSONObject result = null;
				try {
					result = new JSONObject();
					result.put("sqlResult", "success");
				}catch(JSONException ex){
					ApzLogger.i("StoragePlugin",ex.toString());
				}		
				ApzPluginUtil.sendSuccess(this.callbackId, result, false, this.activity , this.webView, true);
			}
		}catch (final SQLException e) {
			ApzLogger.i("StoragePlugin",e.toString());
			JSONObject json = new JSONObject();
			try {
				json.put("errorMessage", e.toString());
			} catch (JSONException e1) {
				ApzLogger.i("StoragePlugin",e.toString());
			}
			ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-056", json, this.activity, this.webView, true);//Incorrect Query
		} finally {
			myDataBase.close();
		}
	}

	/**
	 * Prepares json array from Cursor object
	 * @param cur
	 * @return JSONArray as String
	 */
	public String processCursorResults(Cursor cur) {
		String result = "[]";

		if (cur.moveToFirst()) {
			JSONArray fullresult = new JSONArray();
			String key = "";
			String value = "";
			int colCount = cur.getColumnCount();

			// Build up JSON result object for each row
			do {
				JSONObject row = new JSONObject();
				try {
					for (int i = 0; i < colCount; ++i) {
						key = cur.getColumnName(i);
						value = cur.getString(i);
						row.put(key, value);
					}
					fullresult.put(row);

				} catch (JSONException e) {
					ApzLogger.i("StoragePlugin",e.toString());
				}

			} while (cur.moveToNext());

			result = fullresult.toString();
		}
		return result;
	}

public static boolean isStoragePlugin() {
		return true;
	}

@Override
public void execute(JSONObject params) {
	executeSQL(params);
}
}
