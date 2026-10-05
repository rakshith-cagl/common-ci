package com.iexceed.plugins.networkmonitor

import android.app.Activity
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.utils.ApzUtilsPlugin
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NetworkMonitorTest {

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
        val networkMonitor = ApzUtilsPlugin.createPlugin(
            webView!!,
            activity,
            object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) { }
            }) as ApzUtilsPlugin

        val lObj = JSONObject()
        lObj.put("command", "PLGN_NETWORK_MONITOR")
        networkMonitor.execute(lObj)

    }

    @Test
    fun when_excute_with_param_with_action_equal_start_and_monitoring_true_then_success() {
        val networkMonitor = ApzUtilsPlugin.createPlugin(
            webView!!, activity,
            object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("already running.", aResult?.get("event"))
                }
            }) as ApzUtilsPlugin
        ApzUtilsPlugin.monitoring = true
        val lObj = JSONObject()
        lObj.put("id", " NETWORKMONITOR_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NETWORK_MONITOR")
        lObj.put("action", "start")
        networkMonitor.execute(lObj)
    }

    @Test
    fun when_excute_with_param_with_action_equal_stop_and_monitoring_false_then_success() {
        val networkMonitor = ApzUtilsPlugin.createPlugin(
            webView!!,
            activity,
            object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("Not started Yet.",aResult?.getString("event"))
                }
            }) as ApzUtilsPlugin
        val lObj = JSONObject()
        lObj.put("id", " NETWORKMONITOR_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NETWORK_MONITOR")
        lObj.put("action", "stop")
        networkMonitor.execute(lObj)
    }



    /*@Test
    fun when_startNetworkCallback_then_success() {
        val startNetworkCallback = NetworkMonitor(
            activity,
            webView!!,
            callbackId = "abc",
            object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) { }
            })
        Whitebox.invokeMethod<Any>(startNetworkCallback,"startNetworkCallback", null)
    }

    @Test
    fun when_stopNetworkCallback_then_success() {
        val stopNetworkCallback = NetworkMonitor(
            activity,
            webView!!,
            callbackId = "abc",
            object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) { }
            })
        Whitebox.invokeMethod<Any>(stopNetworkCallback,"stopNetworkCallback", null)
    }





    @Test
    fun when_sendCallback_with_true_parameter_then_success() {
        val sendCallback =  NetworkMonitor(
            activity,
            webView!!,
            callbackId = "abc",
            object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("on", aResult?.get("event"))
                }
            })
        AppzillonConstants.isNetworkConnected = true
        Whitebox.invokeMethod<Any>(sendCallback,"sendCallback", true)
    }

    @Test
    fun when_sendCallback_with_false_paramter_then_success() {
        val sendCallback =  NetworkMonitor(
            activity,
            webView!!,
            callbackId = "abcd",
            object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("off",aResult?.getString("event"))
                }
            })
        Whitebox.invokeMethod<Any>(sendCallback,"sendCallback", false)
    }
*/

}



