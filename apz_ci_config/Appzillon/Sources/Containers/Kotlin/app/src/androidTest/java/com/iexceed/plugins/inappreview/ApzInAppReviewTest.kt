package com.iexceed.plugins.inappreview

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
class ApzInAppReviewTest {

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
    fun whenExecute_WithMissingParams_thenError() {
        val apzInAppReview = ApzInAppReview.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-077", errorCode)
                }
            }) as ApzInAppReview
        val lObj = JSONObject()
        apzInAppReview.execute(lObj)
    }

    @Test
    fun whenExecute_actionStart_with_params_thenSuccess() {
        val apzInAppReview = ApzInAppReview.createPlugin(
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
                    Assert.assertEquals("Review Complete", aResult?.get("status"))
                }
            }) as ApzInAppReview
        val lObj = JSONObject()
        lObj.put("id", "INAPP_CLLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_INAPP_REVIEW")
        apzInAppReview.execute(lObj)
    }

    @Test
    fun whenSendCallback_withReviewManagerNull_thenError() {
        val apzInAppReview = ApzInAppReview.createPlugin(
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
                    Assert.assertEquals("Unavailable", aResult?.get("status"))
                }
            }) as ApzInAppReview
        val lObj = JSONObject()
        lObj.put("id", "INAPP_CLLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_INAPP_REVIEW")
        Whitebox.invokeMethod<Any>(apzInAppReview,"sendCallBack", null)
    }
}