package com.iexceed.plugins.currentlocation

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
class ApzCurrentLocationTest {

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
        val currentLocation = ApzCurrentLocation.createPlugin(
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
            }) as ApzCurrentLocation
        val lObj = JSONObject()
        currentLocation.execute(lObj)
    }

    @Test
    fun whenExecute_with_permissionRevoke_thenSendPermissionDeniedCallback() {
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("adb shell pm revoke ${androidx.test.InstrumentationRegistry.getTargetContext().packageName} android.permission.ACCESS_FINE_LOCATION")

        val currentLocation = ApzCurrentLocation.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendPermissionDenied(
                    pluginName: String,
                    callbackId: String?,
                    activity: Activity?,
                    webView: WebView?
                ) {
                    Assert.assertEquals("CURRENTLOCATION_CLBACK", callbackId)
                }
            }) as ApzCurrentLocation
        val lObj = JSONObject()
        lObj.put("id", "CURRENTLOCATION_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GET_LOCATION")
        currentLocation.execute(lObj)
    }

    @Test
    fun whenExecute_with_params_permissionGranted_thenSendSuccess() {
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("pm grant ${androidx.test.InstrumentationRegistry.getTargetContext().packageName} android.permission.ACCESS_FINE_LOCATION")

        val currentLocation = ApzCurrentLocation.createPlugin(
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
                    Assert.assertEquals("CURRENTLOCATION_CLBACK", callbackId)
                }
            }) as ApzCurrentLocation
        val lObj = JSONObject()
        lObj.put("id", "CURRENTLOCATION_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GET_LOCATION")
        currentLocation.execute(lObj)
    }

    @Test
    fun whenExecute_with_params_permissionGranted_gpsFalse_thenError() {
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("pm grant ${androidx.test.InstrumentationRegistry.getTargetContext().packageName} android.permission.ACCESS_FINE_LOCATION")

        val currentLocation = ApzCurrentLocation.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-274", errorCode)
                }
            }) as ApzCurrentLocation
        val lObj = JSONObject()
        lObj.put("id", "CURRENTLOCATION_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GET_LOCATION")
        currentLocation.isGPS=false
        Whitebox.invokeMethod<Any>(currentLocation,"fetchCurrentLocation", null)
    }
}