package com.iexceed.plugins.notification

import android.app.Activity
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

@RunWith(AndroidJUnit4::class)
class NotificationPluginTest {

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
        assert(NotificationPlugin.isPlugin)
    }

    @Test
    fun whenExecute_WithMissingParams_thenError() {
        val notificationPlugin = NotificationPlugin.createPlugin(
            this.webView,
            this.activity, object : IapzPluginUtil {
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
            }) as NotificationPlugin
        val lObj = JSONObject()
        notificationPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_with_params_action_STARTLISTENER_thenSuccess() {
        val notificationPlugin = NotificationPlugin.createPlugin(
            this.webView,
            this.activity, object : IapzPluginUtil {
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
            }) as NotificationPlugin
        val lObj = JSONObject()
        lObj.put("id", "NOTIFICATION_CLLBACK")
        lObj.put("action","STARTLISTENER")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "APZ_PLUGIN_NOTIF_LSTN_STRT")
        notificationPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_with_params_action_STOPLISTENER_thenSuccess() {
        val notificationPlugin = NotificationPlugin.createPlugin(
            this.webView,
            this.activity, object : IapzPluginUtil {
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
            }) as NotificationPlugin
        val lObj = JSONObject()
        lObj.put("id", "NOTIFICATION_CLLBACK")
        lObj.put("action","STOPLISTENER")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "APZ_PLUGIN_NOTIF_LSTN_STOP")
        notificationPlugin.execute(lObj)
    }
}