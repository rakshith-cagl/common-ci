package com.iexceed.plugins.fileoperationTest

import android.app.Activity
import android.content.Intent
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.fileoperation.ApzFileOperationPlugin
import com.iexceed.plugins.fileoperation.DirectoryBrowser
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DirectoryBrowserTest {

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
    fun when_fileBrowser_category_default_success() {
        val default = ApzFileOperationPlugin.createPlugin(
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
                    Assert.assertEquals("FILEOPERATION_CALLBACK", callbackId)
                }
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        lObj.put("fileCategory", "DEFAULT")
        lObj.put("openFile", "Y")
        default.execute(lObj)
    }

    @Test
    fun test_onActivityResult_forResultCodeOK() {
        val default = ApzFileOperationPlugin.createPlugin(
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
                    Assert.assertEquals("abc/photo.jpg", aResult?.get("filePath"))
                }
            }) as ApzFileOperationPlugin
        val data = Intent(activity, DirectoryBrowser::class.java)
        data.putExtra("filePath", "abc/photo.jpg")
        default.ExternalActivityResultHandlerTask().handleActivityResult(-1, data)
    }

    @Test
    fun test_onActivityResult_forErrorResult_dataNotNull() {
        val default = ApzFileOperationPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView, isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-275", errorCode)
                }
            }) as ApzFileOperationPlugin
        val data = Intent(activity, DirectoryBrowser::class.java)
        data.putExtra("error", "ERROR")
        default.ExternalActivityResultHandlerTask().handleActivityResult(0, data)
    }

    @Test
    fun test_onActivityResult_forErrorResult_dataNull() {
        val default = ApzFileOperationPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView, isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-328", errorCode)
                }
            }) as ApzFileOperationPlugin
        val data = Intent(activity, DirectoryBrowser::class.java)
        default.ExternalActivityResultHandlerTask().handleActivityResult(0, data)
    }
}