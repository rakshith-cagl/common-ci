package com.iexceed.plugins.wipeOut

import android.app.Activity
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.wipeout.WipeOut
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.powermock.reflect.Whitebox
import java.io.File

@RunWith(AndroidJUnit4::class)
class WipeOutTest {

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
    fun when_excute_without_param_then_error(){
        val wipeOut = WipeOut.createPlugin(
            webView!!,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) { }
            }) as WipeOut
        val lObj = JSONObject()
        lObj.put("command", "PLGN_WIPEOUT")
        wipeOut.execute(lObj)

    }

    @Test
    fun when_excute_with_param_actionEqualTo_unInstallApp_then_success(){
        val wipeOut = WipeOut.createPlugin (
            webView!!,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {}
            }) as WipeOut
        val lObj = JSONObject()
        lObj.put("id", "    WIPEOUT_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("action", "unInstallApp")
        lObj.put("command", "PLGN_WIPEOUT")
        wipeOut.execute(lObj)
    }

    @Test
    fun when_excute_with_param_actionEqualTo_subAppDelete_then_success(){
        val wipeOut = WipeOut.createPlugin (
            webView!!,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {}
            }) as WipeOut
        val lObj = JSONObject()
        lObj.put("id", "    WIPEOUT_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("action", "subAppDelete")
        lObj.put("command", "PLGN_WIPEOUT")
        lObj.put("appId", "IEXC")
        wipeOut.execute(lObj)
    }


    @Test
    fun when_unInstallApp_then_error(){
        val unInstallApp = WipeOut.createPlugin(
            webView!!,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("",errorCode)
                }
            }) as WipeOut
        Whitebox.invokeMethod<Any>(unInstallApp,"unInstallApp", null)
    }

    @Test
    fun when_unInstallApp_then_success(){
        val unInstallApp = WipeOut.createPlugin(
            webView!!,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("text",aResult?.get("Wipeout Success"))
                }
            }) as WipeOut

        Whitebox.invokeMethod<Any>(unInstallApp,"unInstallApp", null)
    }




    @Test
    fun when_wipeOutSubApp_then_success(){
        val wipeOutSubApp = WipeOut.createPlugin(
            webView!!,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {}
            }) as WipeOut

        val appName ="ContainerApp"
        Whitebox.invokeMethod<Any>(wipeOutSubApp,"wipeOutSubApp", appName)
    }


    @Test
    fun when_deleteRecursive_then_success(){
        val deleteRecursive = WipeOut.createPlugin(
            webView!!,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {}
            }) as WipeOut



        val fileName = "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps"
        val subAppFolder = File(fileName)
        Whitebox.invokeMethod<Any>(deleteRecursive ,"deleteRecursive", subAppFolder)
    }


}