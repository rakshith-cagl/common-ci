package com.iexceed.plugins.notifydownload

import android.Manifest
import android.app.Activity
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.audio.AudioPlugin
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.powermock.reflect.Whitebox

@RunWith(AndroidJUnit4::class)
class NotifyDownloadTest {

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
        assert(AudioPlugin.isAudioPlugin())
    }




    @Test
    fun when_excute_without_param_then_error(){
        val notifyDownload = NotifyDownload.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) { }
            }) as NotifyDownload
        val lObj = JSONObject()
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        notifyDownload.execute(lObj)

    }

    @Test
    fun when_excute_with_param_then_success(){
        val notifyDownload = NotifyDownload.createPlugin (
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
            }) as NotifyDownload
        val lObj = JSONObject()
        lObj.put("id", "NOTIFYDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        lObj.put("filePath", "")
        notifyDownload.execute(lObj)
    }

    @Test
    fun when_excute_with_param_then__success(){
        val notifyDownload = NotifyDownload.createPlugin (
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
            }) as NotifyDownload
        val lObj = JSONObject()
        lObj.put("id", "NOTIFYDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        lObj.put("filePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word")
        notifyDownload.execute(lObj)
    }

    @Test
    fun when_notifyDownload_without_param_then_error(){
        val notifyDownload = NotifyDownload.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) { }
            }) as NotifyDownload

        val lObj = JSONObject()
        lObj.put("id", "NOTIFYDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        lObj.put("filePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx")
        Whitebox.invokeMethod<Any>(notifyDownload,"notifyDownload", lObj)
    }

    @Test
    fun when_notifyDownload_with_START_then_success(){
        val notifyDownload = NotifyDownload.createPlugin (
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
            }) as NotifyDownload

        val lObj = JSONObject()
        lObj.put("id", "NOTIFYDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        lObj.put("action", "START")
        lObj.put("filePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx")
        Whitebox.invokeMethod<Any>(notifyDownload,"notifyDownload", lObj)
    }

    @Test
    fun when_notifyDownload_with_STOP_then_success(){
        val notifyDownload = NotifyDownload.createPlugin (
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
            }) as NotifyDownload

        val lObj = JSONObject()
        lObj.put("id", "NOTIFYDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        lObj.put("action", "STOP")
        lObj.put("filePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx")
        Whitebox.invokeMethod<Any>(notifyDownload,"notifyDownload", lObj)
    }


    @Test
    fun when_startNotify_then_error(){
        val startNotify = NotifyDownload.createPlugin(
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
                    Assert.assertEquals("", errorCode)
                }
            }) as NotifyDownload

        val lObj = JSONObject()
        lObj.put("id", "NOTIFYDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        lObj.put("filePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx")
        Whitebox.invokeMethod<Any>(startNotify,"startNotify", lObj)
    }

    @Test
    fun when_startNotify_then_success(){
        val notifyDownload = NotifyDownload.createPlugin (
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
            }) as NotifyDownload

        val lObj = JSONObject()
        lObj.put("id", "NOTIFYDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        lObj.put("action", "START")
        lObj.put("filePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx")
        lObj.put("title", "title")
        lObj.put("content", "content")
        Whitebox.invokeMethod<Any>(notifyDownload,"startNotify", lObj)
    }



    @Test
    fun when_stopNotify_then_error(){
        val stopNotify = NotifyDownload.createPlugin(
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
                    Assert.assertEquals("", errorCode)
                }
            }) as NotifyDownload

        val lObj = JSONObject()
        lObj.put("id", "NOTIFYDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        Whitebox.invokeMethod<Any>(stopNotify,"stopNotify", lObj)
    }

    @Test
    fun when_stopNotify_then__error(){
        val stopNotify = NotifyDownload.createPlugin (
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
                    Assert.assertEquals("APZ-CNT-002", errorCode)
                }
            }) as NotifyDownload

        val lObj = JSONObject()
        lObj.put("id", "NOTIFYDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        lObj.put("filePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx")

        lObj.put("title", "title")
        lObj.put("content", "content")
        lObj.put("mimeType", "mimeType")
        Whitebox.invokeMethod<Any>(stopNotify,"stopNotify", lObj)
    }

    @Test
    fun when_stopNotify_with_file_then_success(){
        val stopNotify = NotifyDownload.createPlugin (
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
            }) as NotifyDownload

        val lObj = JSONObject()
        lObj.put("id", "NOTIFYDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        lObj.put("title", "title")
        lObj.put("content", "content")
        lObj.put("mimeType", "mimeType")
        lObj.put("filePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word")
        Whitebox.invokeMethod<Any>(stopNotify,"stopNotify", lObj)
    }

    @Test
    fun when_requestForPermission_thenNoError() {
        val notifyDownload= NotifyDownload.createPlugin(
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
            }) as NotifyDownload
        val lObj = JSONObject()
        lObj.put("id", "NOTIFYDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_NOTIFY_DOWNLOAD")
        notifyDownload.permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        Whitebox.invokeMethod<Any>(notifyDownload,"requestForPermission", lObj)
    }

    @Test
    fun test_PermissionDeniedCallback() {
        val notifyDownload = NotifyDownload.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendPermissionDenied(
                    pluginName: String,
                    callbackId: String?,
                    activity: Activity?,
                    webView: WebView?
                ) {
                }
            }) as NotifyDownload
        Whitebox.invokeMethod<Any>(notifyDownload,"PermissionDeniedCallback", null)
    }
}
