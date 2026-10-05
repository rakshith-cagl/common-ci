package com.iexceed.plugins.devicelocale

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
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.powermock.reflect.Whitebox
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LocalizationTest {

    lateinit var localizationPlugin: Localization
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
        localizationPlugin = Mockito.mock(Localization::class.java)
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
    fun testPluginNotSupportedStatus(){

        assert(Localization.isLocalization)
    }

    @Test
    fun whenExecute_withEmptyParams_thenError(){

        val localization = Localization.createPlugin(
            webView,
            activity, object: IapzPluginUtil{

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
            }) as Localization
        localization.execute(JSONObject())
    }

    @Test
    fun whenExecute_withParams_thenSuccess(){

        val localization = Localization.createPlugin(
            webView,
            activity, object: IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    aResult?.let {
                        Assert.assertTrue(it.has("locale"))
                    }
                }
            }) as Localization

        val jsonObject = JSONObject()
        jsonObject.put("id", "LocalePlugin")
        localization.execute(jsonObject)
    }

    @Test
    fun whenExecute_fetchDeviceLocale_thenSuccess(){

        val localization = Localization.createPlugin(
            webView,
            activity, object: IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                }
            }) as Localization

        val locale = Whitebox.invokeMethod<Any>(localization, "fetchDeviceLocale", null)
        Assert.assertNotNull(locale)
    }

    @Test(expected = IOException::class)
    fun whenExecute_withParams_thenIOException(){

            val jsonObject = JSONObject()
            jsonObject.put("id", "LocalePlugin")

            doThrow(IOException())
                .`when`(localizationPlugin)
                .getDeviceLocale(jsonObject)

            localizationPlugin.getDeviceLocale(jsonObject)

    }

    @Test(expected = SecurityException::class)
    fun whenExecute_withParams_thenSecurityException(){

        val jsonObject = JSONObject()
        jsonObject.put("id", "LocalePlugin")

        doThrow(SecurityException())
            .`when`(localizationPlugin)
            .getDeviceLocale(jsonObject)

        localizationPlugin.getDeviceLocale(jsonObject)

    }
}
