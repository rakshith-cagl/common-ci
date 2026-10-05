package com.iexceed.plugins.fileoperation

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


@RunWith(AndroidJUnit4::class)
class FileDownloadTest {

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
    fun when__downloadFile__without_param_then_error() {
        val downloadFile = FileDownload(
            activity,
            activity,
            webView, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-079", errorCode)
                }
            })

        val lObj = JSONObject()
        downloadFile.downloadFile(lObj)

    }

    @Test
    fun when_downloadFile__with_param_success() {
        val downloadFile = FileDownload(
            activity,
            activity,
            webView,
            object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-079", errorCode)
                }
            })

        val lObj = JSONObject()
        val lObj1 = JSONObject()
        lObj.put("downloadReqDetails", lObj1)

        lObj.put("id", "FILEDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_DWLD_FILE")
        lObj.put("action", "FILEDOWNLOAD")

        lObj.put(
            "destinationPath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word"
        )
        lObj.put("base64", "base64")
        lObj.put("sessionReq", "sessionReq")
        lObj.put("downloadExternalPath", "downloadExternalPath")
        downloadFile.downloadFile(lObj)


    }


    @Test
    fun when_base64_is_N_error() {
        val base64 = FileDownload(
            activity,
            activity,
            webView,
            object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-079", errorCode)
                }
            })

        val lObj = JSONObject()
        val lObj1 = JSONObject()
        lObj.put("downloadReqDetails", lObj1)


        lObj.put("id", "FILEDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_DWLD_FILE")
        lObj.put("action", "FILEDOWNLOAD")

        lObj.put(
            "destinationPath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word"
        )
        lObj.put("base64", "base64")
        lObj.put("sessionReq", "sessionReq")
        lObj.put("downloadExternalPath", "downloadExternalPath")
        lObj.put("appzillonBody", "appzillonBody")
        lObj.put("appzillonHeader", "appzillonHeader")

        base64.downloadFile(lObj)

    }

    @Test
    fun when_base64_is_Y_success() {
        val base64 = FileDownload(
            activity,
            activity,
            webView,
            object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("FILEDOWNLOAD_CALLBACK", callbackId)
                }
            })
        val lObj = JSONObject()
        lObj.put("id", "FILEDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_DWLD_FILE")
        lObj.put("action", "FILEDOWNLOAD")

        lObj.put("downloadReqDetails", "downloadReqDetails")
        lObj.put("destinationPath", "destinationPath")
        lObj.put("base64", "base64")
        lObj.put("sessionReq", "sessionReq")
        lObj.put("downloadExternalPath", "downloadExternalPath")
        base64.downloadFile(lObj)

    }


    @Test
    fun when_downloadExternalPath_is_N_orEmpty_error() {
        val downloadFile = ApzFileOperationPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-079", errorCode)
                }
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_DWLD_FILE")
        lObj.put("action", "FILEDOWNLOAD")
        downloadFile.execute(lObj)

    }

    @Test
    fun when_downloadExternalPath_is_Y_success() {
        val downloadFile = ApzFileOperationPlugin.createPlugin(
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
                    Assert.assertEquals("FILEDOWNLOAD_CALLBACK", callbackId)
                }
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEDOWNLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_DWLD_FILE")
        lObj.put("action", "FILEDOWNLOAD")
        downloadFile.execute(lObj)

    }
}