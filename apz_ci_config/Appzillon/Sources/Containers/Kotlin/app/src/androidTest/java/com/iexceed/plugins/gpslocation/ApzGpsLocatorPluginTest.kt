package com.iexceed.plugins.gpslocation

import android.app.Activity
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.powermock.reflect.Whitebox

@RunWith(AndroidJUnit4::class)
class ApzGpsLocatorPluginTest {

    private lateinit var gpsLocatorPluginMock: ApzGpsLocatorPlugin
    private lateinit var activityScenario: ActivityScenario<AppzillonMainScreen>

    @get:Rule
    val locationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

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
        gpsLocatorPluginMock = mock(ApzGpsLocatorPlugin::class.java)
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
    fun testPluginSupportedStatus() {

        assert(ApzGpsLocatorPlugin.isPlugin())
    }

    @Test
    fun whenExecute_withNoParams_thenError() {

        val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
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
            }) as ApzGpsLocatorPlugin

        apzGpsLocatorPlugin.execute(JSONObject())
    }

    @Test
    fun whenExecute_sendError_thenError() {

        val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
            activity, object : IapzPluginUtil {

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("TEST_ERROR_CODE", errorCode)
                }
            }) as ApzGpsLocatorPlugin

        Whitebox.invokeMethod<Any>(apzGpsLocatorPlugin, "sendError", "TEST_ERROR_CODE")
    }

    @Test
    fun whenExecute_withActionAsStop_whenLocatorIsNotRunning_thenSuccess() {

        val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
            activity, object : IapzPluginUtil {

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-057", errorCode)
                }
            }) as ApzGpsLocatorPlugin

        val jsonObject = JSONObject()
        jsonObject.put("id", "GPS_LOCATOR")
        jsonObject.put("action", "STOP")

        apzGpsLocatorPlugin.execute(jsonObject)
    }

    @Test
    fun whenExecute_permissionDeniedCallback_thenError() {

        val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
            activity, object : IapzPluginUtil {

                override fun sendPermissionDenied(
                    pluginName: String,
                    callbackId: String?,
                    activity: Activity?,
                    webView: WebView?
                ) {

                    Assert.assertEquals("GPS location ", pluginName)
                }
            }) as ApzGpsLocatorPlugin

        Whitebox.invokeMethod<Any>(apzGpsLocatorPlugin, "permissionDeniedCallback", null)
    }

    @Test
    fun whenExecute_displayReconfirmationMessageAlert_thenSuccess() {

        val activity = activity

        activity.runOnUiThread {

            val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
                activity, object : IapzPluginUtil {
                }) as ApzGpsLocatorPlugin

            val alert = Whitebox.invokeMethod<Any>(
                apzGpsLocatorPlugin,
                "displayReconfirmationMessageAlert",
                null
            )
            Assert.assertNotNull(alert)
        }
    }

    @Test
    fun whenExecute_stopGPSLocator_thenSuccess() {

        val activity = activity
        val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
            activity, object : IapzPluginUtil {

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("GPS stopped", aResult?.optString("text"))
                }
            }) as ApzGpsLocatorPlugin

        val gpsLocator = GpsLocator(activity, webView, object : IapzPluginUtil {})
        Whitebox.invokeMethod<Any>(apzGpsLocatorPlugin, "stopGPSLocator", gpsLocator)
    }

    @Test
    fun whenExecute_enableGPS_withActionAsStart_whenLocatorIsAlreadyRunning_thenError() {

        val activity = activity
        val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
            activity, object : IapzPluginUtil {

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-017", errorCode)
                }
            }) as ApzGpsLocatorPlugin

        GpsLocator.isRunning = true
        val action = "START"

        val jsonObject = JSONObject()
        jsonObject.put("id", "GPS_LOCATOR")
        jsonObject.put("action", "START")

        val gpsLocator = GpsLocator(activity, webView, object : IapzPluginUtil {})
        apzGpsLocatorPlugin.mGpsLocator = gpsLocator
        Whitebox.invokeMethod<Any>(apzGpsLocatorPlugin, "enableGPS", action, jsonObject)
    }

    @Test
    fun whenExecute_withActionAsStop_thenSuccess() {

        val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
            activity, object : IapzPluginUtil {

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("GPS stopped", aResult?.optString("text"))
                }
            }) as ApzGpsLocatorPlugin

        val jsonObject = JSONObject()
        jsonObject.put("id", "GPS_LOCATOR")
        jsonObject.put("action", "STOP")
        GpsLocator.isRunning = true
        apzGpsLocatorPlugin.execute(jsonObject)
    }

    /*@Test
    fun whenExecute_requestForPermission_thenSuccess() {

        val activity = activity

        val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
            activity, object : IapzPluginUtil {

            }) as ApzGpsLocatorPlugin
        apzGpsLocatorPlugin.action = "START"

        doNothing().`when`(gpsLocatorPluginMock).requestForPermission(activity)
        gpsLocatorPluginMock.requestForPermission(activity)
        verify(gpsLocatorPluginMock, times(1)).requestForPermission(activity)
    }*/

    @Test
    fun whenExecute_enableGPS_thenSuccess() {

        val activity = activity

        activity.runOnUiThread {

            val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
                activity, object : IapzPluginUtil {

                    override fun sendSuccess(
                        callbackId: String?,
                        aResult: JSONObject?,
                        isKeepAlive: Boolean,
                        activity: Activity,
                        webView: WebView,
                        isInUIThread: Boolean
                    ) {

                        aResult?.has("latitude")?.let { Assert.assertTrue(it) }
                    }
                }) as ApzGpsLocatorPlugin

            val jsonObject = JSONObject()
            jsonObject.put("id", "GPS_LOCATOR")
            jsonObject.put("action", "START")

            apzGpsLocatorPlugin.execute(jsonObject)
        }
    }

    @Test
    fun whenExecute_enableGPS_whenPeriodicityAsIntervalBased_thenSuccess() {

        val activity = activity

        activity.runOnUiThread {

            val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
                activity, object : IapzPluginUtil {

                    override fun sendSuccess(
                        callbackId: String?,
                        aResult: JSONObject?,
                        isKeepAlive: Boolean,
                        activity: Activity,
                        webView: WebView,
                        isInUIThread: Boolean
                    ) {

                        aResult?.has("latitude")?.let { Assert.assertTrue(it) }
                    }
                }) as ApzGpsLocatorPlugin

            val jsonObject = JSONObject()
            jsonObject.put("id", "GPS_LOCATOR")
            jsonObject.put("action", "START")
            jsonObject.put("periodicity", "intervalBased")
            jsonObject.put("distanceInterval", "10.0")
            jsonObject.put("timeInterval", "1000")

            apzGpsLocatorPlugin.execute(jsonObject)
        }
    }

    @Test
    fun whenExecute_enableGPS_whenPeriodicityAsTimed_thenSuccess() {

        val activity = activity

        activity.runOnUiThread {

            val apzGpsLocatorPlugin = ApzGpsLocatorPlugin.createPlugin(webView,
                activity, object : IapzPluginUtil {

                    override fun sendSuccess(
                        callbackId: String?,
                        aResult: JSONObject?,
                        isKeepAlive: Boolean,
                        activity: Activity,
                        webView: WebView,
                        isInUIThread: Boolean
                    ) {

                        aResult?.has("latitude")?.let { Assert.assertTrue(it) }
                    }
                }) as ApzGpsLocatorPlugin

            val jsonObject = JSONObject()
            jsonObject.put("id", "GPS_LOCATOR")
            jsonObject.put("action", "START")
            jsonObject.put("periodicity", "timed")
            jsonObject.put("distanceInterval", "10.0")
            jsonObject.put("timeInterval", "1000")

            apzGpsLocatorPlugin.execute(jsonObject)
        }
    }
}