package com.iexceed.plugins.filecrypto

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
import org.junit.runners.MethodSorters
import org.powermock.reflect.Whitebox

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class FileCryptoTest {

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
        assert(FileCrypto.Companion.isFileCryptoPlugin)
    }

    @Test
    fun a_whenExecute_WithMissingParams_thenError() {
        val fileCrypto = FileCrypto.createPlugin(
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
            }) as FileCrypto
        val lObj = JSONObject()
        fileCrypto.execute(lObj)
    }

    @Test
    fun b_whenExecute_withParams_actionEncrypt_thenSuccess() {
        val fileCrypto = FileCrypto.createPlugin(
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
                    Assert.assertNotNull(aResult?.get("filePath"))
                }
            }) as FileCrypto
        val lObj = JSONObject()
        lObj.put("id", "FILECRYPTO_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_ENCRPT_FILE")
        lObj.put("action", "ENCRYPT")
        lObj.put("key", "10")
        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/java/XmlTag.java")
        lObj.put("destFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/java")
        fileCrypto.execute(lObj)
    }

    @Test
    fun c_whenExecute_withParams_actionEncrypt_keyLengthGreaterThan16_thenSuccess() {
        val fileCrypto = FileCrypto.createPlugin(
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
                    Assert.assertNotNull(aResult?.get("filePath"))
                }
            }) as FileCrypto
        val lObj = JSONObject()
        lObj.put("id", "FILECRYPTO_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_ENCRPT_FILE")
        lObj.put("action", "ENCRYPT")
        lObj.put("key", "1789948249247293479")
        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/java/XmlTag.java")
        lObj.put("destFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/java")
        fileCrypto.execute(lObj)
    }

    /*@Test
    fun d_whenExecute_withParams_actionDecrypt_DecodeException_thenError() {
        val fileCrypto = FileCrypto.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-210", errorCode)
                }
            }) as FileCrypto
        val lObj = JSONObject()
        lObj.put("id", "FILECRYPTO_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_ENCRPT_FILE")
        lObj.put("action", "DECRYPT")
        lObj.put("key", "10")
        lObj.put("srcFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/java/XmlTag.java")
        lObj.put("destFilePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/java")
        fileCrypto.execute(lObj)
    }*/

    @Test
    fun e_whenExecute_withParams_actionDecrypt_thenSuccess() {
        val fileCrypto = FileCrypto.createPlugin(
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
                    Assert.assertNotNull(aResult?.get("filePath"))
                }
            }) as FileCrypto
        val lObj = JSONObject()
        lObj.put("id", "FILECRYPTO_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_ENCRPT_FILE")
        lObj.put("action", "DECRYPT")
        lObj.put("key", "10")
        lObj.put("srcFilePath", "storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/java")
        lObj.put("destFilePath", "storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC")
        fileCrypto.execute(lObj)
    }

    @Test
    fun whenExecute_sendEMail_withFileSizeIsZero_thenError(){

        val fileCrypto = FileCrypto.createPlugin(
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
                    Assert.assertNotNull(aResult?.get("filePath"))
                }
            }) as FileCrypto

        val arr = byteArrayOfInts(0xA1, 0x2E, 0x38, 0xD4, 0x89, 0xC3)
        Whitebox.invokeMethod<Any>(fileCrypto, "writeToDecyFile",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/java/XmlTag.java",
        arr)
    }

    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }
}