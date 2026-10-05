package com.iexceed.plugins.nfcsupport

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
import org.powermock.reflect.Whitebox


@RunWith(AndroidJUnit4::class)
class NfcSupportTest {


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
    fun when_execute_without_param_then_error() {

        val nfcSupport = ApzIsNfcSupported.createPlugin(
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
            }) as ApzIsNfcSupported
        val lObj = JSONObject()
        nfcSupport.execute(lObj)
    }

    @Test
    fun when_execute_with_param_success() {
        val nfcSupport = ApzIsNfcSupported.createPlugin(
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

                    aResult?.has("text")?.let { Assert.assertTrue(it) }
                }
            }) as ApzIsNfcSupported
        val lObj = JSONObject()
        lObj.put("id", "NFC_SUPPORT_CALLBACK")
        nfcSupport.execute(lObj)
    }


    @Test
    fun when_isNfcSupported_notSupported_thenError() {
        val isNfcDeviceSupported = ApzIsNfcSupported.createPlugin(
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
                    aResult?.has("text")?.let { Assert.assertTrue(it) }
                }
            }) as ApzIsNfcSupported
        val lObj = JSONObject()
        Whitebox.invokeMethod<Any>(isNfcDeviceSupported, "isNfcDeviceSupported", lObj, false)

    }

    @Test
    fun when_isNfcSupported_isSupported_thenSuccess() {
        val isNfcDeviceSupported = ApzIsNfcSupported.createPlugin(
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

                    aResult?.has("text")?.let { Assert.assertTrue(it) }
                }
            }) as ApzIsNfcSupported
        val lObj = JSONObject()
        Whitebox.invokeMethod<Any>(isNfcDeviceSupported, "isNfcDeviceSupported", lObj, true)
    }

}
