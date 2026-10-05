package com.iexceed.plugins.keyboard

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
class KeyboardPluginTest {

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
    fun whenExecute_WithMissingParams_thenError() {
        val keyboardPlugin = KeyboardPlugin.createPlugin(
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
            }) as KeyboardPlugin
        val lObj = JSONObject()
        keyboardPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_actionStartListener_with_params_thenSuccess() {
        val keyboardPlugin = KeyboardPlugin.createPlugin(
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
                    Assert.assertEquals("KEYBOARD_CLLBACK", callbackId)
                }
            }) as KeyboardPlugin
        val lObj = JSONObject()
        lObj.put("id", "KEYBOARD_CLLBACK")
        lObj.put("action","STARTLISTENER")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_KEYBD_LISTR_STRT")
        keyboardPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_actionStopListener_with_params_thenSuccess() {
        val keyboardPlugin = KeyboardPlugin.createPlugin(
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
                    Assert.assertEquals("stopped", aResult?.get("event"))
                }
            }) as KeyboardPlugin
        val lObj = JSONObject()
        lObj.put("id", "KEYBOARD_CLLBACK")
        lObj.put("action","STOPLISTENER")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_KEYBD_LISTR_STRT")
        keyboardPlugin.execute(lObj)
    }

    @Test
    fun testInnerClass_with_params_thenSuccess() {
        val keyboardPlugin = KeyboardPlugin.createPlugin(
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
                    Assert.assertEquals("started", aResult?.get("event"))
                }
            }) as KeyboardPlugin
        val lObj = JSONObject()
        lObj.put("id", "KEYBOARD_CLLBACK")
        lObj.put("action","STARTLISTENER")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_KEYBD_LISTR_STRT")
        keyboardPlugin.execute(lObj)
        keyboardPlugin.OnGlobalLayoutListenerTask().onGlobalLayout()
    }
}