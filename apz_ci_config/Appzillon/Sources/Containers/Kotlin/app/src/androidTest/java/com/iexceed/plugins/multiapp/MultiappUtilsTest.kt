package com.iexceed.plugins.multiapp

import android.app.Activity
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.AppzillonConstants
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MultiappUtilsTest {
    private val TAG = "MultiappUtilsTest"
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
    fun givenWithoutId_thenReturnErrorCode() {
        val multiAppUtils = MultiappUtils.createPlugin(
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
                    assertEquals("APZ-CNT-077", errorCode)

                }
            })
        val lObj = JSONObject()
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_MULTI_APP_UTILS")
        multiAppUtils?.execute(lObj)
    }

    @Test
    fun givenDeleteSubApp_thenSuccess() {
        val multiAppUtils = MultiappUtils.createPlugin(
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
                    assert(true)
                }
            })
        val lObj = JSONObject()
        lObj.put("callBack", "executeCallback")
        lObj.put("id", "APP_SUB")
        lObj.put("command", "PLGN_SUBAPP_DEL")
        lObj.put("action", "APPDELETE")
        lObj.put("appId", "IEXC")
        multiAppUtils?.execute(lObj)
    }

    // INSTRUCTIONS is skipped since it is a sever call
   /* @Test
    fun givenGetInstructions_thenSuccess() {
        val multiAppUtils = MultiappUtils.createPlugin(
            AppzillonMainScreen.webView!!,
            AppzillonMainScreen.activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    assert(true)
                }
            })
        val lObj = JSONObject()
        lObj.put("callBack", "executeCallback")
        lObj.put("id", "APP_SUB")
        lObj.put("command", "PLGN_GET_INSTRUCTION")
        lObj.put("action", "INSTRUCTIONS")
        lObj.put("appId", "IEXC")
        multiAppUtils?.execute(lObj)
    }*/

    @Test
    fun givenIsUpgradeRequiredN_thenNo() {
        val multiAppUtils = MultiappUtils.createPlugin(
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
                    assertEquals("N",aResult?.get("text"))               }
            })
        val lObj = JSONObject()
        lObj.put("id", "APP_SUB")
        lObj.put("action", "UPGRADEREQ")
        lObj.put("appId", "IEXC")
        multiAppUtils?.execute(lObj)
    }

    @Test
    fun givenIsUpgradeRequiredY_thenYes() {
        val multiAppUtils = MultiappUtils.createPlugin(
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
                    assertEquals("Y",aResult?.get("text"))               }
            })
        val lObj = JSONObject()
        lObj.put("id", "APP_SUB")
        lObj.put("action", "UPGRADEREQ")
        lObj.put("appId", "IEXC")
        AppzillonConstants.UPDATE_REQUEST = "Y"
        multiAppUtils?.execute(lObj)
    }
    @Test
    fun givenUpdateActionN_thenNo() {
        val multiAppUtils = MultiappUtils.createPlugin(
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
                    assertEquals("N",aResult?.get("updateAction"))               }
            })
        val lObj = JSONObject()
        lObj.put("id", "APP_SUB")
        lObj.put("action", "UPDATEACTION")
        lObj.put("appId", "IEXC")
        AppzillonConstants.UPDATE_ACTION = "N"
        multiAppUtils?.execute(lObj)
    }

    @Test
    fun givenUpdateActionY_thenYes() {
        val multiAppUtils = MultiappUtils.createPlugin(
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
                    assertEquals("Y",aResult?.get("updateAction"))               }
            })
        val lObj = JSONObject()
        lObj.put("id", "APP_SUB")
        lObj.put("action", "UPDATEACTION")
        lObj.put("appId", "IEXC")
        AppzillonConstants.UPDATE_ACTION = "Y"
        multiAppUtils?.execute(lObj)
    }
}