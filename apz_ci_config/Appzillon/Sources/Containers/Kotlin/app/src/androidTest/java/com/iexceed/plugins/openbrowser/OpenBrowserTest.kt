package com.iexceed.plugins.openbrowser


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

class OpenBrowserTest {

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
    fun when_excute_without_param_then_error() {
        val openBrowser = ApzOpenBrowserPlugin.createPlugin(
            webView!!,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {  Assert.assertEquals("APZ-CNT-077", errorCode)

                }
            }) as ApzOpenBrowserPlugin
        val lObj = JSONObject()
        lObj.put("command", "PLGN_OPEN_URL")
        openBrowser.execute(lObj)

    }

    @Test
    fun when_excute_with_param_then_success() {
        val openBrowser = ApzOpenBrowserPlugin.createPlugin(
            webView!!,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals(null,aResult)
                }
            }) as ApzOpenBrowserPlugin
        val lObj = JSONObject()
        lObj.put("id", "OPENBROWSER_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_OPEN_URL")
        lObj.put("url", "https://www.google.co.in/")
        openBrowser.execute(lObj)
    }
}