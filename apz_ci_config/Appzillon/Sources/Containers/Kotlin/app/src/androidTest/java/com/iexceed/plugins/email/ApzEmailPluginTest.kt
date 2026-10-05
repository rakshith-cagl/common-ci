package com.iexceed.plugins.email

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
import org.json.JSONArray
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.powermock.reflect.Whitebox


@RunWith(AndroidJUnit4::class)
class ApzEmailPluginTest {

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
    fun tearDown(){

        activityScenario.close()
    }

    @Test
    fun testPluginSupportedStatus(){

        assert(ApzEmailPlugin.isPlugin())
    }

    @Test
    fun whenExecute_withoutParams_thenError(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

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
            }) as ApzEmailPlugin

        apzEmailPlugin.execute(JSONObject())
    }

    @Test
    fun whenExecute_InternalAsY_thenError(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("MAIL_ERROR", errorCode)
                }
            }) as ApzEmailPlugin

        val jsonObject = JSONObject()
        val filePathArray = JSONArray()
        jsonObject.put("id", "SEND_EMAIL_PLUGIN")
        jsonObject.put("internal", "Y")
        jsonObject.put("recipientMailId", "akshitha.p@i-exceed.com")
        jsonObject.put("subject", "Testing Email Plugin")
        jsonObject.put("body", "SEND_SMS_PLUGIN")
        jsonObject.put("ccIdList", "")
        jsonObject.put("senderMailId", "")
        jsonObject.put("filePaths", filePathArray)
        jsonObject.put("maxAttachmentSize", "5")

        apzEmailPlugin.execute(jsonObject)
    }

    @Test
    fun whenExecute_sendEMail_withFileSizeIsZero_thenError(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-322", errorCode)
                }

            }) as ApzEmailPlugin

        val jsonObject = JSONObject()
        val filePathArray = JSONArray()
        filePathArray.put("/sdcard/DCIM")
        jsonObject.put("id", "SEND_EMAIL_PLUGIN")
        jsonObject.put("internal", "N")
        jsonObject.put("recipientMailId", "akshitha.p@i-exceed.com")
        jsonObject.put("subject", "Testing Email Plugin")
        jsonObject.put("body", "SEND_SMS_PLUGIN")
        jsonObject.put("ccIdList", "")
        jsonObject.put("senderMailId", "")
        jsonObject.put("filePaths", filePathArray)
        jsonObject.put("maxAttachmentSize", "0")

//        apzEmailPlugin.sendEMail(jsonObject)
        Whitebox.invokeMethod<Any>(apzEmailPlugin, "sendEMail", jsonObject)
    }

    @Test
    fun whenExecute_sendError_thenSuccess(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-322", errorCode)
                }

            }) as ApzEmailPlugin

        Whitebox.invokeMethod<Any>(apzEmailPlugin, "sendError", "APZ-CNT-322")
    }

    @Test
    fun whenExecute_sendSuccess_onSuccessfullySent_thenSuccess(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("sent", aResult?.optString("event"))
                }

            }) as ApzEmailPlugin

        val status = "sent"
        val jsonObject = JSONObject()
        jsonObject.put("event", status)
        Whitebox.invokeMethod<Any>(apzEmailPlugin, "sendSuccess", jsonObject)
    }

    @Test
    fun whenExecute_sendSuccess_onCancelled_thenSuccess(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("cancel", aResult?.optString("event"))
                }

            }) as ApzEmailPlugin

        val status = "cancel"
        val jsonObject = JSONObject()
        jsonObject.put("event", status)
        Whitebox.invokeMethod<Any>(apzEmailPlugin, "sendSuccess", jsonObject)
    }

    @Test
    fun whenExecute_getSuccessJsonData_onSent_thenSuccess(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

            }) as ApzEmailPlugin

        val resultCode = 1
        val resultJson = Whitebox.invokeMethod<Any>(apzEmailPlugin, "getSuccessJsonData", resultCode) as JSONObject
        Assert.assertEquals("sent", resultJson.optString("event"))
    }

    @Test
    fun whenExecute_getSuccessJsonData_onCancel_thenSuccess(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

            }) as ApzEmailPlugin

        val resultCode = 0
        val resultJson = Whitebox.invokeMethod<Any>(apzEmailPlugin, "getSuccessJsonData", resultCode) as JSONObject
        Assert.assertEquals("cancel", resultJson.optString("event"))
    }

    /*@Test
    fun whenExecute_getFileURI_thenSuccess(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

            }) as ApzEmailPlugin

        val file = File("")
        val fileURI = Whitebox.invokeMethod<Any>(apzEmailPlugin, "getFileURI", file) as Uri
        Assert.assertEquals("content://com.iexceed.container/root/", fileURI.toString())

    }*/

    @Test
    fun whenExecute_pickAppAndSendEmail_hasFileSizeIssue_thenError(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-322", errorCode)
                }
            }) as ApzEmailPlugin

        apzEmailPlugin.errorCode = "APZ-CNT-322"
        Whitebox.invokeMethod<Any>(apzEmailPlugin, "pickAppAndSendEmail", "N", false, Intent())
    }

    @Test
    fun whenExecute_pickAppAndSendEmail_fileNotExists_thenError(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

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
            }) as ApzEmailPlugin

        apzEmailPlugin.errorCode = "APZ-CNT-002"
        Whitebox.invokeMethod<Any>(apzEmailPlugin, "pickAppAndSendEmail", "N", false, Intent())
    }

    @Test
    fun whenExecute_pickAppAndSendEmail_thenException(){

        val apzEmailPlugin = ApzEmailPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-012", errorCode)
                }
            }) as ApzEmailPlugin

        Whitebox.invokeMethod<Any>(apzEmailPlugin, "pickAppAndSendEmail", "N", true, null)
    }

}