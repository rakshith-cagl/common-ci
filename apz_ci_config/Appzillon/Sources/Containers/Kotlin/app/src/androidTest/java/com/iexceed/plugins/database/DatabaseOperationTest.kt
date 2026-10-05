package com.iexceed.plugins.database

import android.app.Activity
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseOperationTest {

    lateinit var activityScenario: ActivityScenario<AppzillonMainScreen>
    lateinit var activity: ApzActivity<*>
    lateinit var webView: WebView

    private val component =
        TestComponentRule(InstrumentationRegistry.getInstrumentation().targetContext)

    @get:Rule
    val chain = RuleChain.outerRule(component)

    @Before
    fun setUp() {
        // anything to instatiate before tests are run
        activityScenario = ActivityScenario.launch(AppzillonMainScreen::class.java)
        activityScenario.onActivity {
            this.activity = it.activity
            this.webView = it.mWebView
        }
    }

    @After
    fun tearDown() {
        activityScenario.close()
    }

    @Test
    fun whenExecute_WithMissingParams_columnsNull_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj = JSONObject()
        lObj.put("id", "DB_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_DB_OPERATION")
        lObj.put("action", "CREATE_DB")
        databaseOperation.execute(lObj)
    }

    @Test
    fun whenExecute_WithParams_missingData_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'CREATE_DB', 'columns': ['rollno','name']}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_mismatchDataAndColumnSize_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'CREATE_DB', 'columns': ['rollno','name'], " +
                "'data': ['1','abc','xyz']}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_missingDbVersion_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'CREATE_DB', 'columns': ['rollno','name'], " +
                "'data': ['1','abc']}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_missingPrimaryKey_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'CREATE_DB', 'columns': ['rollno','name'], " +
                "'data': ['1','abc'], 'db_version':'2'}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_missingTableName_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'CREATE_DB', 'columns': ['rollno','name'], " +
                "'data': ['1','abc'], 'db_version':'2', 'primaryKey':'rollno'}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_missingsqlquery_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'CREATE_DB', 'columns': ['rollno','name'], " +
                "'data': ['1','abc'], 'db_version':'2', 'primaryKey':'rollno', 'tableName':'studentdata'}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithMissingParams_actionInsert_columnsNull_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj = JSONObject()
        lObj.put("id", "DB_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_DB_OPERATION")
        lObj.put("action", "INSERT_DB")
        databaseOperation.execute(lObj)
    }

    @Test
    fun whenExecute_WithParams_actionInsert_missingData_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'INSERT_DB', " +
                "'columns': ['rollno','name']}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_actionInsert_mismatchDataAndColumnSize_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'INSERT_DB', 'columns': ['rollno','name'], " +
                "'data': ['1','abc','xyz']}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_actionInsert_missingDbVersion_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        var lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'INSERT_DB', " +
                "'columns': ['rollno','name'], 'data': ['1','abc']}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_actionInsert_missingPrimaryKey_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'INSERT_DB', 'columns': ['rollno','name'], " +
                "'data': ['1','abc'], 'db_version':'2'}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_actionInsert_missingTableName_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'INSERT_DB', " +
                "'columns': ['rollno','name'], 'data': ['1','abc'], 'db_version':'2', " +
                "'primaryKey':'rollno'}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_actionInsert_missingsqlquery_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'INSERT_DB', " +
                "'columns': ['rollno','name'], 'data': ['1','abc'], 'db_version':'2', " +
                "'primaryKey':'rollno', 'tableName':'studentdata'}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_actionUpdate_missingsqlquery_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String = "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                "'command': 'PLGN_DB_OPERATION', 'action': 'UPDATE_DB', " +
                "'columns': ['rollno','name'], 'data': ['1','abc'], 'db_version':'2', " +
                "'primaryKey':'rollno', 'tableName':'studentdata'}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_actionDelete_missingsqlquery_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String =
            "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                    "'command': 'PLGN_DB_OPERATION', 'action': 'DELETE_DB', " +
                    "'columns': ['rollno','name'], 'data': ['1','abc'], 'db_version':'2', " +
                    "'primaryKey':'rollno', 'tableName':'studentdata', 'condition':'working'}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_actionMigrate_thenSuccess() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String =
            "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                    "'command': 'PLGN_DB_OPERATION', 'action': 'MIGRATE_DB', " +
                    "'columns': ['rollno','name'], 'data': ['1','abc'], 'old_db_version':'2', " +
                    "'primaryKey':'rollno', 'tableName':'studentdata', 'condition':'working', " +
                    "'operation':'migrate'}"
        databaseOperation.execute(JSONObject(lObj))
    }

    @Test
    fun whenExecute_WithParams_actionGetFromDb_missingsqlquery_thenError() {
        val databaseOperation = DatabaseOperation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("DB_CALLBACK", callbackId)
                }
            }) as DatabaseOperation
        val lObj: String =
            "{'id': 'DB_CALLBACK', 'callBack': 'executeCallback', " +
                    "'command': 'PLGN_DB_OPERATION', 'action': 'GET_FROM_DB', " +
                    "'columns': ['rollno','name'], 'data': ['1','abc'], 'db_version':'2', " +
                    "'primaryKey':'rollno', 'tableName':'studentdata', 'condition':'working'}"
        databaseOperation.execute(JSONObject(lObj))
    }
}