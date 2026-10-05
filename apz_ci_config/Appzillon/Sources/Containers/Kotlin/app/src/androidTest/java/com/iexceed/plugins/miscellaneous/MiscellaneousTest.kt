package com.iexceed.plugins.miscellaneous

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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MiscellaneousTest {

    /*@Test
    fun getProperties() {
    }

    @Test
    fun execute() {
    }

    @Test
    fun eventControl() {
    }

    @Test
    fun getApzPluginUtil() {
    }*/

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
        // Made to wait because initializeEventsDefault is called which reset the events to default
        Thread.sleep(1000)
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
    fun givenWithoutId_thenReturnErrorCode() {
        val miscellaneous = Miscellaneous.createPlugin(webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    assertEquals("APZ-CNT-077", errorCode)
                }
            })
        val lObj = JSONObject()
        lObj.put("callBack", "executeCallback")
        miscellaneous?.execute(lObj)
    }

    @Test
    fun givenAllEvents_thenReturnSuccess(){
        val miscellaneous = Miscellaneous.createPlugin(webView,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    //Couldn't test other lifecycle events
                    when(aResult?.get("event")){
                        "backButton" -> assert(true)
                        "appPaused" -> assert(true)
                        else -> assert(false)
                    }
                }
            })
        val lObj = JSONObject()
        lObj.put("id", "EVENTS")
        lObj.put("allEvents", "on")
        lObj.put("callBack", "executeCallback")
        miscellaneous?.execute(lObj)
        /*activityScenario.moveToState(Lifecycle.State.INITIALIZED)
        activityScenario.moveToState(Lifecycle.State.CREATED)
        activityScenario.moveToState(Lifecycle.State.STARTED)
        activityScenario.moveToState(Lifecycle.State.RESUMED)
        activityScenario.moveToState(Lifecycle.State.DESTROYED)*/
        activityScenario.onActivity {
            it.onBackPressed()
        }

    }
}