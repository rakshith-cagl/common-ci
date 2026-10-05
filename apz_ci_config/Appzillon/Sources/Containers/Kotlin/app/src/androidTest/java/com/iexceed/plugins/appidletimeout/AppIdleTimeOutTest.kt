package com.iexceed.plugins.appidletimeout

import android.app.Activity
import android.util.Log
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.common.StringUtils
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.powermock.reflect.Whitebox
import java.util.*

@RunWith(AndroidJUnit4::class)
class AppIdleTimeOutTest {
    private val TAG = "AppIdleTimeOutTest"
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
    fun testPluginNotSupportedStatus() {
        assert(AppIdleTimeOut.isPlugin())
    }

    @Test
    fun testPlugin_without_ID() {
        val appIdleTimeOut = AppIdleTimeOut.createPlugin(this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-200", errorCode)
                }
            })
        val lObj = JSONObject()
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_IDLE_TMR_START")
        appIdleTimeOut?.execute(lObj)
    }

    @Test
    fun testPlugin_timeOut_zero() {
        val appIdleTimeOut = AppIdleTimeOut.createPlugin(this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-200", errorCode)
                }
            }) as AppIdleTimeOut
        val lObj = JSONObject()
        lObj.put("id", "APP_IDLE_TIMEOUT_ZERO")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_IDLE_TMR_START")
//        var stringUtils = StringUtils.StringUtils(AppzillonMainScreen.activity.applicationContext, "ABC" )
//        stringUtils.appInfo[StringUtils.APP_IDLE_TIME_OUT] = "0"
        StringUtils.appInfo[StringUtils.APP_IDLE_TIME_OUT] = "0"
        appIdleTimeOut.execute(lObj)
    }

    @Test
    fun whenTimeoutGiven_thenTimerExceeds() {
        val appIdleTimeOut = AppIdleTimeOut.createPlugin(this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    if (isKeepAlive) {
                        Log.d("AppIdleTimeOut", "started")
                        Assert.assertEquals(aResult?.get("event"), "started")
                    } else {
                        Log.d("AppIdleTimeOut", "timerExceeds")
                        Assert.assertEquals(aResult?.get("event"), "timerExceeds1")
                    }
                }
            })
        val lObj = JSONObject()
        lObj.put("id", "APP_IDLE_TIMEOUT")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_IDLE_TMR_START")
        StringUtils.appInfo[StringUtils.APP_IDLE_TIME_OUT] = "11"
        appIdleTimeOut?.execute(lObj)
    }

    /*@Test
    fun whenPluginCalledTwice_thenCancelAndRerunTimerExceeds() {
        val appIdleTimeOut = AppIdleTimeOut.createPlugin(AppzillonMainScreen.webView!!,
            AppzillonMainScreen.activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    if (isKeepAlive) {
                        Log.d("AppIdleTimeOut", "started")
                        Assert.assertEquals(aResult?.get("event"), "started")

                    } else {
                        Log.d("AppIdleTimeOut", "timerExceeds")
                        Assert.assertEquals(aResult?.get("event"), "timerExceeds")
                    }
                }
            })
        val lObj = JSONObject()
        lObj.put("id", "APP_IDLE_TIMEOUT")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_IDLE_TMR_START")
        StringUtils.appInfo[StringUtils.APP_IDLE_TIME_OUT] = "10"
        appIdleTimeOut?.execute(lObj)
    }*/

    @Test
    fun test_startTimerForAppIdleTimeout_for_appTimer_notNull(){
        val appIdleTimeOut = AppIdleTimeOut.createPlugin(this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-200", errorCode)
                }
            }) as AppIdleTimeOut
        appIdleTimeOut.APPIDLE_TIMEOUT= 10
        Whitebox.invokeMethod<Any>(appIdleTimeOut,"startTimerForAppIdleTimeout", Timer(true))
    }

    @Test
    fun test_TimerTask_run_method(){
        val appIdleTimeOut = AppIdleTimeOut.createPlugin(this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertNotNull(aResult)
                }
            }) as AppIdleTimeOut
        appIdleTimeOut.result = JSONObject()
        val tt = appIdleTimeOut.AppIdleTimerTask()
        tt.run()
    }
}
