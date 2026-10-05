package com.iexceed.plugins.calendar

import android.app.Activity
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONArray
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.powermock.reflect.Whitebox

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class CalendarPluginTest {

    lateinit var activityScenario: ActivityScenario<AppzillonMainScreen>
    lateinit var lObj: JSONObject
    lateinit var jObj1: JSONObject
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
    fun a_testPluginNotSupportedStatus() {
        assert(CalendarPlugin.isPlugin())
    }

    @Test
    fun b_whenExecute_WithMissingParams_thenError() {
        val calendarPlugin = CalendarPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-077", errorCode)
                }
            }) as CalendarPlugin
        val lObj = JSONObject()
        calendarPlugin.execute(lObj)
    }

    @Test
    fun c_whenExecute_actionCreate_with_params_thenSuccess() {
        val calendarPlugin = CalendarPlugin.createPlugin(
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
                    Assert.assertEquals("0 events created", aResult?.get("success"))
                }
            }) as CalendarPlugin
        lObj = getDummyJSONobject()
        lObj.put("action", "create")
        val arr = JSONArray()
        arr.put(jObj1)

        lObj.put("events",arr)
        calendarPlugin.execute(lObj)
    }

    @Test
    fun d_whenExecute_actionEdit_with_params_thenSuccess() {
        val calendarPlugin = CalendarPlugin.createPlugin(
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
                    Assert.assertEquals("0 events edited", aResult?.get("success"))
                }
            }) as CalendarPlugin
        lObj = getDummyJSONobject()
        lObj.put("action", "edit")
        jObj1.put("newStartDate", "24-SEP-2022")
        jObj1.put("newEndDate", "24-NOV-2022")
        jObj1.put("newStartTime", "03:37:10")
        jObj1.put( "newEndTime", "04:37:10")

        val arr = JSONArray()
        arr.put(jObj1)

        lObj.put("events",arr)
        calendarPlugin.execute(lObj)
    }

    @Test
    fun e_whenExecute_actionDelete_with_params_thenSuccess() {
        val calendarPlugin = CalendarPlugin.createPlugin(
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
                    Assert.assertEquals("0 events deleted", aResult?.get("success"))
                }
            }) as CalendarPlugin
        lObj = getDummyJSONobject()
        lObj.put("action", "delete")

        val arr = JSONArray()
        arr.put(jObj1)

        lObj.put("events",arr)
        calendarPlugin.execute(lObj)
    }

    @Test
    fun f_testSendPermissionDenied() {
        val calendarPlugin = CalendarPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendPermissionDenied(
                    pluginName: String, callbackId: String?,
                    activity: Activity?, webView: WebView?
                ){
                    Assert.assertEquals("Calendar",pluginName)
                }
            }) as CalendarPlugin
        Whitebox.invokeMethod<Any>(calendarPlugin, "permissionDeniedCallback", null)
    }

    private fun getDummyJSONobject() : JSONObject {
        lObj = JSONObject()

        jObj1 = JSONObject()
        jObj1.put("title", "event27")
        jObj1.put("alarm", "on")
        jObj1.put("startDate", "23-SEP-2022")
        jObj1.put("endDate", "23-NOV-2022")
        jObj1.put("startTime", "02:37:10")
        jObj1.put( "endTime", "03:37:10")
        jObj1.put("priority", "N")
        jObj1.put("recurrence", "Monthly")
        jObj1.put("recurrenceEndDate", "23-NOV-2022")
        jObj1.put("location", "Bangalore")
        jObj1.put("summary","ABC")

        lObj.put("id", "CALENDAR_CLLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_CRT_CAL_EVT")
        lObj.put("dateFormat","DD-MMM-YYYY")
        return lObj
    }
}