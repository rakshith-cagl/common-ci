package com.iexceed.plugins.filetobase64

import android.Manifest
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.powermock.reflect.Whitebox

@RunWith(AndroidJUnit4::class)
class ApzFileToBase64PluginTest {

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
        println("testPluginNotSupportedStatus")
        assert(ApzFileToBase64Plugin.isPlugin())
    }

    @Test
    fun testPlugin_without_ID() {
        println("testPlugin_without_ID")
        val apzFileToBase64 = ApzFileToBase64Plugin.createPlugin(webView,
            activity, object : IapzPluginUtil {
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
        lObj.put("command", "PLGN_FILE_TO_B64")
        apzFileToBase64?.execute(lObj)
    }

    @Test
    fun testPlugin_whenFilePathMissing_thenError() {
        println("testPlugin_whenFilePathMissing_thenError")
        val apzFileToBase64 = ApzFileToBase64Plugin.createPlugin(webView,
            activity, object : IapzPluginUtil {
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
            })
        val lObj = JSONObject()
        lObj.put("id", "FILETOBASE64")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_FILE_TO_B64")
        apzFileToBase64?.execute(lObj)
    }

    @Test
    fun testPlugin_whenFilePathWrong_thenError() {
        println("testPlugin_whenFilePathWrong_thenError")
        val apzFileToBase64 = ApzFileToBase64Plugin.createPlugin(webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-270", errorCode)
                }
            })
        val lObj = JSONObject()
        lObj.put("id", "FILETOBASE64")
        lObj.put("filePath", "WRONG_FILE_PATH")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_FILE_TO_B64")
        apzFileToBase64?.execute(lObj)
    }

    @Test
    fun testPlugin_whenCorrectFilePathAdPermissionByCommandPrompt_thenSuccessOrFail() {
        println("testPlugin_whenCorrectFilePathAdPermissionByCommandPrompt_thenSuccess")
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("pm grant ${androidx.test.InstrumentationRegistry.getTargetContext().packageName} android.permission.WRITE_EXTERNAL_STORAGE")

        val apzFileToBase64 = ApzFileToBase64Plugin.createPlugin(webView,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("FILETOBASE64", callbackId)
                    Assert.assertTrue(aResult!!.has("text"))
                    println("Result : " + aResult.getString("text"))
                }

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    println("Result : $errorCode")
                    Assert.assertEquals("APZ-CNT-270", errorCode)
                }

            })
        val lObj = JSONObject()
        lObj.put("id", "FILETOBASE64")
        lObj.put(
            "filePath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/java/XmlTag.java"
        )
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_FILE_TO_B64")
        apzFileToBase64?.execute(lObj)
    }

    @Test
    fun whenExecute_withPermission_convertFile_thenSuccess(){

        val apzFileToBase64Plugin = ApzFileToBase64Plugin.createPlugin(webView, activity, object : IapzPluginUtil{

            override fun sendSuccess(
                callbackId: String?,
                aResult: JSONObject?,
                isKeepAlive: Boolean,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

                Assert.assertNotNull(aResult)
            }
        }) as ApzFileToBase64Plugin

        val jsonObject = JSONObject()
        jsonObject.put("id", "FILE_TO_BASE64")
        jsonObject.put("filePath", "/storage/emulated/0/Download/000107506.jpg")

        apzFileToBase64Plugin.execute(jsonObject)
    }

    @Test
    fun testPrivateFunction_displayReconfirmationMessage() {
        println("testPrivateFunction_displayReconfirmationMessage")
        val apzFileToBase64 = ApzFileToBase64Plugin.createPlugin(webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                }
            })
        val h = Handler(Looper.getMainLooper())
        h.post {
            Whitebox.invokeMethod<Any>(apzFileToBase64, "displayReconfirmationMessage", null)
        }
    }

    @Test
    fun testPrivateFunction_permissionDeniedCallback() {
        println("testPrivateFunction_permissionDeniedCallback")
        val apzFileToBase64 = ApzFileToBase64Plugin.createPlugin(webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                }
            })

        Whitebox.invokeMethod<Any>(apzFileToBase64, "permissionDeniedCallback", null)
    }

    @Test
    fun testPrivateFunction_requestForPermission() {
        println("testPrivateFunction_requestForPermission")
        val apzFileToBase64 = ApzFileToBase64Plugin.createPlugin(webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                }
            })
        Whitebox.invokeMethod<Any>(apzFileToBase64, "requestForPermission", null)
    }

    @Test
    fun testPrivateFunction_handlePermissionsResult() {
        println("testPrivateFunction_handlePermissionsResult")
        val apzFileToBase64 = ApzFileToBase64Plugin.createPlugin(webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                }
            })
        Whitebox.invokeMethod<Any>(
            apzFileToBase64,
            "handlePermissionsResult",
            PluginConstants.APZ_REQ_WRITE_STORAGE,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        )
    }

}
