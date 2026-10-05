package com.iexceed.plugins.nativeshare

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
class NativeShareTest {


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
        val nativeShare = ApzNativeShare.createPlugin(
            webView!!, activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {Assert.assertEquals("", errorCode) }
            }) as ApzNativeShare
        val lObj = JSONObject()
        lObj.put("command", "PLGN_NATVE_SHARE")
        nativeShare.execute(lObj)
    }



    @Test
    fun when_excute_with_param_then_success() {
        val nativeShare = ApzNativeShare.createPlugin(
            webView!!, activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) { Assert.assertEquals("NATIVESHARE_CALLBACK", callbackId)}
            }) as ApzNativeShare
        val lObj = JSONObject()
        lObj.put("id", "NATIVESHARE_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NATVE_SHARE")
        nativeShare.execute(lObj)
    }


    @Test
    fun when_shareText_action_missing_then_error() {
        val shareText = ApzNativeShare.createPlugin(
            webView, activity, object : IapzPluginUtil {
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
            }) as ApzNativeShare
        val lObj = JSONObject()
        lObj.put("id", "NATIVESHARE_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NATVE_SHARE")
        Whitebox.invokeMethod<Any>(shareText,"shareText", lObj)
    }


    @Test
    fun when_shareText_action_equal_text_then_error() {
        val shareText = ApzNativeShare.createPlugin(
            webView!!, activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {Assert.assertEquals("",errorCode)}
            }) as ApzNativeShare
        val lObj = JSONObject()
        lObj.put("id", "NATIVESHARE_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NATVE_SHARE")
        lObj.put("action", "text")
        Whitebox.invokeMethod<Any>(shareText,"shareText", lObj)
    }

    @Test
    fun when_shareText_action_equal_text_then_success() {
        val shareText = ApzNativeShare.createPlugin(
            webView!!, activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("Native share success",aResult?.get("text"))
                }
            }) as ApzNativeShare
        val lObj = JSONObject()
        lObj.put("id", "NATIVESHARE_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NATVE_SHARE")
        lObj.put("action", "text")
        Whitebox.invokeMethod<Any>(shareText,"shareText", lObj)
    }


    @Test
    fun when_shareText_action_equal_file_then_error() {
        val shareText = ApzNativeShare.createPlugin(
            webView!!, activity, object : IapzPluginUtil {
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
            }) as ApzNativeShare
        val lObj = JSONObject()
        lObj.put("id", "NATIVESHARE_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NATVE_SHARE")
        lObj.put("action", "file")
        Whitebox.invokeMethod<Any>(shareText,"shareText", lObj)
    }

    @Test
    fun when_shareText_action_equal_file_and_filepath_missing_then_error() {
        val shareText = ApzNativeShare.createPlugin(
            webView!!, activity, object : IapzPluginUtil {
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
            }) as ApzNativeShare
        val lObj = JSONObject()
        lObj.put("id", "NATIVESHARE_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NATVE_SHARE")
        lObj.put("action", "file")
        lObj.put("filePath", "")
        Whitebox.invokeMethod<Any>(shareText,"shareText", lObj)
    }


    @Test
    fun when_shareText_action_equal_file_with_filepath_then_success() {
        val shareText = ApzNativeShare.createPlugin(
            webView!!, activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("Native share success",aResult?.get("text"))
                }
            }) as ApzNativeShare
        val lObj = JSONObject()
        lObj.put("id", "NATIVESHARE_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NATVE_SHARE")
        lObj.put("action", "file")
        lObj.put("filePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx")
        Whitebox.invokeMethod<Any>(shareText,"shareText", lObj)
    }


}