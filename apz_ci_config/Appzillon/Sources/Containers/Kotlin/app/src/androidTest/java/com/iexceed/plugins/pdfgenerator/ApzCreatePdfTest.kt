package com.iexceed.plugins.pdfgenerator

import android.app.Activity
import android.graphics.pdf.PdfDocument
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.reflect.Whitebox

@RunWith(AndroidJUnit4::class)
class ApzCreatePdfTest {

    lateinit var activityScenario: ActivityScenario<AppzillonMainScreen>
    lateinit var activity: ApzActivity<*>
    lateinit var webView: WebView


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
    fun testPluginSupportedStatus(){
        assert(ApzCreatePDF.isPlugin())
    }

    @Test
    fun whenExecute_WithMissingParams_thenError() {
        val apzCreatePDF = ApzCreatePDF.createPlugin(
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
            }) as ApzCreatePDF
        val lObj = JSONObject()
        apzCreatePDF.execute(lObj)
    }

    @Test
    fun whenExecute_with_params_action_append_thenSuccess() {
        val apzCreatePDF = ApzCreatePDF.createPlugin(
            this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("contentAdded", aResult?.get("event"))
                }
            }) as ApzCreatePDF
        val lObj = JSONObject()
        lObj.put("id", "PDF_GENERATE_CLLBACK")
        lObj.put("action","append")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GEN_PDF")
        lObj.put("contentType", "Text")
        lObj.put("text", "abc")
        lObj.put("fontSize", "0")
        lObj.put("padding", "0")
        apzCreatePDF.execute(lObj)
    }

    @Test
    fun whenExecute_with_action_append_contentType_Image_thenSuccess() {
        val apzCreatePDF = ApzCreatePDF.createPlugin(
            this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("contentAdded", aResult?.get("event"))
                }
            }) as ApzCreatePDF
        val lObj = JSONObject()
        lObj.put("id", "PDF_GENERATE_CLLBACK")
        lObj.put("action","append")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GEN_PDF")
        lObj.put("contentType", "Image")
        lObj.put("imagePath", "")
        lObj.put("imageWidth", "0")
        lObj.put("base64", "RGV2ZWxvcGVyIHByb2R1Y3Rpdml0eSBpcyBt")
        apzCreatePDF.execute(lObj)
    }

    @Test
    fun whenExecute_with_action_append_contentType_Image_PathNotEmpty_thenSuccess() {
        val apzCreatePDF = ApzCreatePDF.createPlugin(
            this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("contentAdded", aResult?.get("event"))
                }
            }) as ApzCreatePDF
        val lObj = JSONObject()
        lObj.put("id", "PDF_GENERATE_CLLBACK")
        lObj.put("action","append")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GEN_PDF")
        lObj.put("contentType", "Image")
        lObj.put("imagePath", "storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/sample.jpg")
        lObj.put("imageWidth", "0")
        lObj.put("imageHeight", "0")
        apzCreatePDF.execute(lObj)
    }

    @Test
    fun whenExecute_with_action_append_contentType_Empty_thenError() {
        val apzCreatePDF = ApzCreatePDF.createPlugin(
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
                    Assert.assertEquals("APZ-FM-EX-025", errorCode)
                }
            }) as ApzCreatePDF
        val lObj = JSONObject()
        lObj.put("id", "PDF_GENERATE_CLLBACK")
        lObj.put("action","append")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GEN_PDF")
        lObj.put("contentType", "")
        lObj.put("text", "abc")
        lObj.put("fontSize", "0")
        apzCreatePDF.execute(lObj)
    }

    @Test
    fun whenExecute_with_action_generate_pdfDocNull_thenError() {
        val apzCreatePDF = ApzCreatePDF.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-082", errorCode)
                }
            }) as ApzCreatePDF
        val lObj = JSONObject()
        lObj.put("id", "PDF_GENERATE_CLLBACK")
        lObj.put("action","generate")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GEN_PDF")
        apzCreatePDF.execute(lObj)
    }

    @Test
    fun whenExecute_with_action_generate_pdfDocNotNull_thenSuccess() {
        val apzCreatePDF = ApzCreatePDF.createPlugin(
            this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("contentAdded", aResult?.get("event"))
                }
            }) as ApzCreatePDF
        val lObj = JSONObject()
        apzCreatePDF.pdfDoc = Whitebox.invokeMethod<PdfDocument>(apzCreatePDF,"initialise", null)
        lObj.put("id", "PDF_GENERATE_CLLBACK")
        lObj.put("action","generate")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GEN_PDF")
        lObj.put("base64", "Y")
        lObj.put("filePath", "storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/java/XmlTag.java")
        apzCreatePDF.execute(lObj)
    }
}