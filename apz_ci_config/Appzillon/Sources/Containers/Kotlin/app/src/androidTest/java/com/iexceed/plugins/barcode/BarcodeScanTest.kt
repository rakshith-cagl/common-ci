package com.iexceed.plugins.barcode

import android.app.Activity
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.utils.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BarcodeScanTest {

    lateinit var activityScenario: ActivityScenario<AppzillonMainScreen>

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()
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
        assert(ApzBarcodePlugin.isBarcodeActivity)
    }

    @Test
    fun whenExecute_WithMissingParams_thenError() {
        val barcodeScan = ApzBarcodePlugin.createPlugin(
            this.webView,
            this.activity, object : IapzPluginUtil {
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
            }) as ApzBarcodePlugin
        val lObj = JSONObject()
        barcodeScan.execute(lObj)
    }

//    @Test
//    fun whenExecute_WithParams_thenSuccess() {
//        val barcodeScan = ApzBarcodePlugin.createPlugin(
//            this.webView,
//            this.activity, object : IapzPluginUtil {
//                override fun sendSuccess(
//                    callbackId: String?,
//                    aResult: JSONArray?,
//                    aResultKey: String?,
//                    isKeepAlive: Boolean,
//                    activity: Activity, webView: WebView, isInUIThread: Boolean
//                ) {
//                    Assert.assertEquals("BS_CLBACK", callbackId)
//                }
//            }) as ApzBarcodePlugin
//        val lObj = JSONObject()
//        lObj.put("id", "BS_CLBACK")
//        lObj.put("action", "START")
//        lObj.put("callBack", "executeCallback")
//        lObj.put("command", "PLGN_SCN_BARCODE")
//        barcodeScan.execute(lObj)
//    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun whenExecute_WithParams_action_Stop_thenSuccess() {
//        testCoroutineRule.runBlockingTest {
//            val barcodeScan = ApzBarcodePlugin.createPlugin(
//                webView,
//                activity, object : IapzPluginUtil {
//                    override fun sendSuccess(
//                        callbackId: String?,
//                        aResult: JSONArray?,
//                        aResultKey: String?,
//                        isKeepAlive: Boolean,
//                        activity: Activity, webView: WebView, isInUIThread: Boolean
//                    ) {
//                        Assert.assertEquals("BS_CLBACK", callbackId)
//                    }
//                }) as ApzBarcodePlugin
//            val lObj = JSONObject()
//            lObj.put("id", "BS_CLBACK")
//            lObj.put("action", "STOP")
//            lObj.put("callBack", "executeCallback")
//            lObj.put("command", "PLGN_SCN_BARCODE")
//            ApzBarcodePlugin.Companion.barcodeScan = BarcodeScan(
//                activity,
//                activity,
//                webView,
//                lObj.toString(),
//                barcodeScan.apzPluginUtil
//            )
//            ApzBarcodePlugin.Companion.barcodeScan!!.closeLayout()
//        }
//    }

    @Test
    fun whenExecute_WithParams_action_ScanFromGallery_filePath_random_thenError() {
        testCoroutineRule.runBlockingTest {
            val barcodeScan = ApzBarcodePlugin.createPlugin(
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
                        Assert.assertEquals("APZ-CNT-070", errorCode)
                    }
                }) as ApzBarcodePlugin
            val lObj = JSONObject()
            lObj.put("id", "BS_CLBACK")
            lObj.put("filePath", "abc")
            lObj.put("action", "SCAN_FROM_GALLERY")
            lObj.put("callBack", "executeCallback")
            lObj.put("command", "PLGN_SCN_BARCODE")
            barcodeScan.execute(lObj)
        }
    }
}