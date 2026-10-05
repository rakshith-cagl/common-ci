package com.iexceed.plugins.battery

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
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
import java.util.*

@RunWith(AndroidJUnit4::class)
class BatteryPluginTest {

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
        assert(BatteryPlugin.isBatteryPlugin())
    }

    @Test
    fun whenExecute_WithMissingParams_thenError() {
        val batteryPlugin = BatteryPlugin.createPlugin(
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
            }) as BatteryPlugin
        val lObj = JSONObject()
        batteryPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_actionStart_missing_params_thenError() {
        val batteryPlugin = BatteryPlugin.createPlugin(
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
            }) as BatteryPlugin
        val lObj = JSONObject()
        lObj.put("id", "BATTERY_CLLBACK")
        lObj.put("action","START")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BT_MTR_START")
        batteryPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_actionStart_with_params_thenSuccess() {
        val batteryPlugin = BatteryPlugin.createPlugin(
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
                    Assert.assertEquals("BATTERY_CLLBACK", callbackId)
                }
            }) as BatteryPlugin
        val lObj = JSONObject()
        lObj.put("id", "BATTERY_CLLBACK")
        lObj.put("action","START")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BT_MTR_START")
        lObj.put("state", "Y")
        lObj.put("level", "Y")
        lObj.put("threshold", "")
        lObj.put("time", "")
        batteryPlugin.execute(lObj)
    }

	@Test
    fun whenExecute_actionStop_receiverNull_thenError() {
        val batteryPlugin = BatteryPlugin.createPlugin(
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
            }) as BatteryPlugin
        val lObj = JSONObject()
        lObj.put("id", "BATTERY_CLLBACK")
        lObj.put("action","STOP")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BT_MTR_START")
        batteryPlugin.batteryTimer = Timer()
        batteryPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_actionStop_receiverNotNull_thenSuccess() {
        val batteryPlugin = BatteryPlugin.createPlugin(
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
                    Assert.assertEquals("RECEIVER_REGISTER_CLLBACK", callbackId)
                }
            }) as BatteryPlugin
        val lObj = JSONObject()
        lObj.put("id", "RECEIVER_REGISTER_CLLBACK")
        lObj.put("action","STOP")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BT_MTR_START")
        batteryPlugin.batteryTimer = Timer()
        activity.registerReceiver(batteryPlugin.powerConnectionReceiver, IntentFilter())
        batteryPlugin.execute(lObj)
    }

    @Test
    fun test_PowerConnectionReceiver_in_handler() {
        val batteryPlugin = BatteryPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {}
            }) as BatteryPlugin
        Whitebox.invokeMethod<Any>(batteryPlugin,"regPowerConnectionReceiver", null)
        Assert.assertNotNull(batteryPlugin.ifilter)
    }

    @Test
    fun test_startTimer() {
        val batteryPlugin = BatteryPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {}
            }) as BatteryPlugin
        Whitebox.invokeMethod<Any>(batteryPlugin,"startTimer", "2")
        Assert.assertNotNull(batteryPlugin.batteryTimer)
    }

    @Test
    fun test_BatteryTimerTask() {
        val batteryPlugin = BatteryPlugin.createPlugin(
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
                    Assert.assertEquals("time",aResult?.get("event"))
                }
            }) as BatteryPlugin
        batteryPlugin.BatteryTimerTask().run()
    }

    @Test
    fun when_isEnabled_wrongValue_thenError() {
        val batteryPlugin = BatteryPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-240", errorCode)
                }
            }) as BatteryPlugin
        Whitebox.invokeMethod<Any>(batteryPlugin,"isEnabled", "abc")
    }

    @Test
    fun test_getBatteryStatus() {
        val batteryPlugin = BatteryPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-240", errorCode)
                }
            }) as BatteryPlugin
        val intent = Intent()
        intent.putExtra(BatteryManager.EXTRA_STATUS,BatteryManager.BATTERY_STATUS_FULL)
        val status = Whitebox.invokeMethod<Any>(batteryPlugin,"getBatteryStatus", intent)
        Assert.assertEquals(2,status)
    }

    @Test
    fun test_getBatteryLevel() {
        val batteryPlugin = BatteryPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-240", errorCode)
                }
            }) as BatteryPlugin
        val intent = Intent()
        intent.putExtra(BatteryManager.EXTRA_SCALE,BatteryManager.BATTERY_STATUS_FULL)
        intent.putExtra(BatteryManager.EXTRA_LEVEL,BatteryManager.BATTERY_STATUS_FULL)
        val bLevel = Whitebox.invokeMethod<Any>(batteryPlugin,"getBatteryLevel", intent)
        Assert.assertNotNull(bLevel)
        Assert.assertEquals(100,bLevel)
    }
}