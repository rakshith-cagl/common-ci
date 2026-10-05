package com.iexceed.plugins.nativeextensibility

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
class ApzExtensibilityPluginTest {

    lateinit var activityScenario: ActivityScenario<AppzillonMainScreen>
    lateinit var activity: ApzActivity<*>
    lateinit var webView: WebView

    @Before
    fun setUp() {
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
    fun testPlugin_without_ID() {
        println("testPlugin_without_ID")
        val apzExtensibilityPlugin = ApzExtensibilityPlugin.createPlugin(this.webView,this.activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-171", errorCode)
                }
            })
        val lObj = JSONObject()
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NTV_EXT")
        apzExtensibilityPlugin?.execute(lObj)
    }

    @Test
    fun whenExecute_actionStart_with_params_thenSuccess() {
        val apzExtensibilityPlugin = ApzExtensibilityPlugin.createPlugin(
            this.webView,this.activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("NATIVE_EXT", callbackId)
                }
            }) as ApzExtensibilityPlugin
        val lObj = JSONObject()
        lObj.put("id", "NATIVE_EXT")
        lObj.put("action","START")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NTV_EXT")
        apzExtensibilityPlugin.execute(lObj)
    }

}
