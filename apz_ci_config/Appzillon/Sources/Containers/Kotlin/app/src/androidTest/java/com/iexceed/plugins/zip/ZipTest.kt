package com.iexceed.plugins.zip

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
class ZipTest {

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
        val zip = ApzZipPlugin.createPlugin(
            webView, activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-077",errorCode)
                }
            }) as ApzZipPlugin

        val lObj = JSONObject()
        lObj.put("command", "PLGN_ZIP")
        zip.execute(lObj)
    }


    @Test
    fun when_excute_with_param_action_zip_then_success() {
        val zip = ApzZipPlugin.createPlugin(
            webView, activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("ZIP_CALLBACK", callbackId)
                }
            }) as ApzZipPlugin

        val lObj = JSONObject()
        lObj.put("id", "ZIP_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_ZIP")

        lObj.put("action", "zip")
        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word")
        lObj.put("destFilePath", "")
        zip.execute(lObj)
    }

    @Test
    fun when_excute_with_param_action_unzip_then_success() {
        val zip = ApzZipPlugin.createPlugin(
            webView, activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("ZIP_CALLBACK", callbackId)
                }
            }) as ApzZipPlugin

        val lObj = JSONObject()
        lObj.put("id", "ZIP_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_ZIP")

        lObj.put("action", "unzip")
        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word")
        lObj.put("destFilePath", "")
        zip.execute(lObj)
    }





    @Test
    fun when_zip_with_param_action_zip_then_success() {
        val zipobj = ZipPlugin("callbackId",
            activity,webView, object : IapzPluginUtil{
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC///word.zip", aResult?.getString("filePath"))
                }
        } )

        val lObj = JSONObject()

        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word")
        lObj.put("destFilePath", "")

        zipobj.zip(lObj.toString())

    }

    @Test
    fun when_zip_without_param_action_zip_then_error() {
        val zipobj = ZipPlugin("callbackId",
            activity,webView, object : IapzPluginUtil{
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("File not found",aResult?.get("text"))
                }
            } )

        val lObj = JSONObject()

//        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word")
        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/abc")
        lObj.put("destFilePath", "")

//        zipobj.zip(lObj.toString())
        zipobj.zip(lObj.toString())

    }

    @Test
    fun when_zip_with_param_action_zip_with_destFilePath_then_success() {
        val zipobj = ZipPlugin("callbackId",
            activity,webView, object : IapzPluginUtil{
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC///word.zip", aResult?.getString("filePath"))
                }
            } )

        val lObj = JSONObject()
        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word")
        lObj.put("destFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs")

        zipobj.zip(lObj.toString())

    }








    @Test
    fun when_unzip_with_param_action_unzip_then_success() {
        val zipobj = ZipPlugin("callbackId",
            activity,webView, object : IapzPluginUtil{
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/", aResult?.getString("filePath"))
                }
            } )

        val lObj = JSONObject()
        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word")
        lObj.put("destFilePath", "")
        zipobj.unzip(lObj.toString())

    }

    @Test
    fun when_unzip_without_param_action_unzip_then_error() {
        val zipobj = ZipPlugin("callbackId",
            activity,webView, object : IapzPluginUtil{
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("File not found",aResult?.get("text"))
                }
            } )

        val lObj = JSONObject()
        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/abc")
        lObj.put("destFilePath", "")

        zipobj.unzip(lObj.toString())

    }

    @Test
    fun when_unzip_with_param_action_unzip_with_destFilePath_then_success() {
        val zipobj = ZipPlugin("callbackId",
            activity,webView, object : IapzPluginUtil{
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC//", aResult?.getString("filePath"))
                }
            } )

        val lObj = JSONObject()
        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word")
        lObj.put("destFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs")
        zipobj.unzip(lObj.toString())

    }




}