package com.iexceed.plugins.barcodegenerate

import android.app.Activity
import android.graphics.Bitmap
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.barcodegenerator.BarcodeGenerator
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.powermock.reflect.Whitebox

@RunWith(AndroidJUnit4::class)
class BarcodeGenerateTest {

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
        assert(BarcodeGenerator.isPlugin)
    }

    @Test
    fun whenExecute_WithMissingParams_thenError() {
        val barcodeGenerator = BarcodeGenerator.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {}
            }) as BarcodeGenerator
        val lObj = JSONObject()
        barcodeGenerator.execute(lObj)
        Assert.assertEquals("N",barcodeGenerator.isLogo)
    }

    @Test
    fun whenExecute_WithParams_base64N_thenSuccess() {
        val barcodeGenerator = BarcodeGenerator.createPlugin(
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
                    Assert.assertEquals("BG_B64N_CLBACK", callbackId)
                }
            }) as BarcodeGenerator
        val lObj = JSONObject()
        lObj.put("id", "BG_B64N_CLBACK")
        lObj.put("inputString","START")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_AUDIO")
        lObj.put("base64", "N")
        lObj.put("isLogoImagePresent", "Y")
        barcodeGenerator.execute(lObj)
    }

    @Test
    fun whenExecute_WithParams_base64Y_thenSuccess() {
        val barcodeGenerator = BarcodeGenerator.createPlugin(
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
                    Assert.assertEquals("BG_B64Y_CLBACK", callbackId)
                }
            }) as BarcodeGenerator
        val lObj = JSONObject()
        lObj.put("id", "BG_B64Y_CLBACK")
        lObj.put("inputString","START")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_AUDIO")
        lObj.put("base64", "Y")
        lObj.put("isLogoImagePresent", "Y")
        barcodeGenerator.execute(lObj)
    }

    @Test
    fun whenExecute_WithParams_base64Y_isLogoN_thenSuccess() {
        val barcodeGenerator = BarcodeGenerator.createPlugin(
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
                    Assert.assertEquals("BG_B64Y_ISLOGO_N_CLBACK", callbackId)
                }
            }) as BarcodeGenerator
        val lObj = JSONObject()
        lObj.put("id", "BG_B64Y_ISLOGO_N_CLBACK")
        lObj.put("inputString","START")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_AUDIO")
        lObj.put("base64", "Y")
        lObj.put("isLogoImagePresent", "N")
        barcodeGenerator.execute(lObj)
    }

    @Test
    fun when_handleFailure_thenError() {
        val barcodeGenerator = BarcodeGenerator.createPlugin(
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
                    Assert.assertEquals("APZ-DM-006", errorCode)
                }
            }) as BarcodeGenerator
        Whitebox.invokeMethod<Any>(barcodeGenerator,"handleFailure", "Error")
    }

	@Test
    fun test_mergeBitmaps() {
        val barcodeGenerator = BarcodeGenerator.createPlugin(
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
                }
            }) as BarcodeGenerator
        val b1: Bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888)
        val b2: Bitmap = Bitmap.createBitmap(150, 150, Bitmap.Config.ARGB_8888)
        val resultBitmap = Whitebox.invokeMethod<Any>(barcodeGenerator, "mergeBitmaps", b1, b2)
        Assert.assertNotNull(resultBitmap)
    }
}
