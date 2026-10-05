package com.iexceed.plugins.shortcut

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.reflect.Whitebox

@RunWith(AndroidJUnit4::class)
class ShortcutPluginTest {

    lateinit var activityScenario: ActivityScenario<AppzillonMainScreen>
    lateinit var activity: ApzActivity<*>
    lateinit var webView: WebView

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
    fun testPluginSupportedStatus(){

        val shortcutPlugin = ShortcutPlugin.createPlugin(webView, activity, object : IapzPluginUtil{

        }) as ShortcutPlugin

        assert(shortcutPlugin.isPlugin())
    }

    @Test
    fun whenExecute_handleBroadcastAction_thenSuccess(){

        val shortcutPlugin = ShortcutPlugin.createPlugin(webView, activity, object : IapzPluginUtil{

            override fun sendSuccess(
                callbackId: String?,
                aResult: JSONObject?,
                isKeepAlive: Boolean,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

                Assert.assertNotNull(aResult?.getString("shortcutID"))
            }
        }) as ShortcutPlugin

        val intent = Intent()
        intent.action = "SHORTCUT_TEST"
        val callbackID = "SHORTCUT_PLUGIN"
        Whitebox.invokeMethod<Any>(shortcutPlugin, "handleBroadcastAction", intent, activity, callbackID)
    }

    @Test
    fun whenExecute_stopListener_thenSuccess(){

        val shortcutPlugin = ShortcutPlugin.createPlugin(webView, activity, object : IapzPluginUtil{

            override fun sendSuccess(
                callbackId: String?,
                aResult: JSONObject?,
                isKeepAlive: Boolean,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

                Assert.assertEquals("stopped", aResult?.getString("event"))
            }
        }) as ShortcutPlugin

        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

            }
        }
        val osVersion = Build.VERSION.SDK_INT
        Whitebox.invokeMethod<Any>(shortcutPlugin, "stopListener", broadcastReceiver, osVersion)
    }

    @Test
    fun whenExecute_stopListener_withUnSupportedOSVersion_thenError(){

        val shortcutPlugin = ShortcutPlugin.createPlugin(webView, activity, object : IapzPluginUtil{

            override fun sendError(
                callbackId: String?,
                errorCode: String?,
                aResult: JSONObject?,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

                Assert.assertNotNull(aResult?.getString("errorMessage"))
            }
        }) as ShortcutPlugin

        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

            }
        }
        val osVersion = 20
        Whitebox.invokeMethod<Any>(shortcutPlugin, "stopListener", broadcastReceiver, osVersion)
    }

    @Test
    fun whenExecute_startListener_thenSuccess(){

        val shortcutPlugin = ShortcutPlugin.createPlugin(webView, activity, object :IapzPluginUtil{

            override fun sendSuccess(
                callbackId: String?,
                aResult: JSONObject?,
                isKeepAlive: Boolean,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

               Assert.assertNotNull(aResult?.getString("event"))
            }
        }) as ShortcutPlugin

        val intentFilter = IntentFilter("com.iexceed.shortcut")
        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

            }
        }
        val osVersion = Build.VERSION.SDK_INT
        Whitebox.invokeMethod<Any>(shortcutPlugin, "startListener", activity, broadcastReceiver, osVersion, intentFilter)
    }

    @Test
    fun whenExecute_startListener_withUnSupportedOS_thenError(){

        val shortcutPlugin = ShortcutPlugin.createPlugin(webView, activity, object :IapzPluginUtil{

            override fun sendError(
                callbackId: String?,
                errorCode: String?,
                aResult: JSONObject?,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

                Assert.assertNotNull(aResult?.getString("errorMessage"))
            }
        }) as ShortcutPlugin

        val intentFilter = IntentFilter("com.iexceed.shortcut")
        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

            }
        }
        val osVersion = 20
        Whitebox.invokeMethod<Any>(shortcutPlugin, "startListener", activity, broadcastReceiver, osVersion, intentFilter)
    }

    @Test
    fun whenExecute_startListener_whenShortcutMsgNotNull_thenSuccess(){

        val shortcutPlugin = ShortcutPlugin.createPlugin(webView, activity, object :IapzPluginUtil{

        }) as ShortcutPlugin

        val intentFilter = IntentFilter("com.iexceed.shortcut")
        val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

            }
        }
        val osVersion = Build.VERSION.SDK_INT
        AppzillonMainScreen.SHORTCUT_MSG = "SHORTCUT_MSG"
        Whitebox.invokeMethod<Any>(shortcutPlugin, "startListener", activity, broadcastReceiver, osVersion, intentFilter)
        Assert.assertNull(AppzillonMainScreen.SHORTCUT_MSG)
    }

    @Test
    fun whenExecute_withActionAsStart_thenSuccess(){

        val shortcutPlugin = ShortcutPlugin.createPlugin(webView, activity, object :IapzPluginUtil{

            override fun sendSuccess(
                callbackId: String?,
                aResult: JSONObject?,
                isKeepAlive: Boolean,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

               Assert.assertNotNull(aResult?.getString("event"))
            }
        }) as ShortcutPlugin

        val jsonObject = JSONObject()
        jsonObject.put("id", "SHORTCUT")
        jsonObject.put("action", "STARTLISTENER")
        shortcutPlugin.execute(jsonObject)
    }

    @Test
    fun whenExecute_withActionAsStop_thenSuccess(){

        val shortcutPlugin = ShortcutPlugin.createPlugin(webView, activity, object :IapzPluginUtil{

            override fun sendSuccess(
                callbackId: String?,
                aResult: JSONObject?,
                isKeepAlive: Boolean,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

                Assert.assertNotNull(aResult?.getString("event"))
            }
        }) as ShortcutPlugin

        val jsonObject = JSONObject()
        jsonObject.put("id", "SHORTCUT")
        jsonObject.put("action", "STOPLISTENER")
        shortcutPlugin.execute(jsonObject)
    }
}