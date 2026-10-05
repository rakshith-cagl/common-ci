package com.iexceed.plugins.dataencryption

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
class EncryptDecryptUtilityTest {

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
        assert(EncryptDecryptUtility.Companion.isEncryptDecryptUtility)
    }

    @Test
    fun whenExecute_WithMissingParams_thenError() {
        val encryptDecryptUtility = EncryptDecryptUtility.createPlugin(
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
            }) as EncryptDecryptUtility
        val lObj = JSONObject()
        encryptDecryptUtility.execute(lObj)
    }

    @Test
    fun whenExecute_withParams_actionEncrypt_thenSuccess() {
        val encryptDecryptUtility = EncryptDecryptUtility.createPlugin(
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
                    Assert.assertEquals("ENCRYPTDECRYPT_CLBACK", callbackId)
                }
            }) as EncryptDecryptUtility
        val lObj = JSONObject()
        lObj.put("id", "ENCRYPTDECRYPT_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GET_ENCRYPT")
        lObj.put("action", "ENCRYPT")
        lObj.put("key", "10")
        lObj.put("stringToEncrypt", "abc")
        encryptDecryptUtility.execute(lObj)
    }

    @Test
    fun whenExecute_withParams_actionDecrypt_decryptStringNull_thenError() {
        val encryptDecryptUtility = EncryptDecryptUtility.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-046", errorCode)
                }
            }) as EncryptDecryptUtility
        val lObj = JSONObject()
        lObj.put("id", "ENCRYPTDECRYPT_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GET_ENCRYPT")
        lObj.put("action", "DECRYPT")
        lObj.put("key", "10")
        lObj.put("stringToDecrypt", "abcdgjahdsgjagh")
        encryptDecryptUtility.execute(lObj)
    }

    @Test
    fun whenExecute_withParams_actionDecrypt_thenSuccess() {
        val encryptDecryptUtility = EncryptDecryptUtility.createPlugin(
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
                    Assert.assertEquals("abc", aResult?.get("decryptedString"))
                }
            }) as EncryptDecryptUtility
        val lObj = JSONObject()
        lObj.put("id", "ENCRYPTDECRYPT_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_GET_ENCRYPT")
        lObj.put("action", "DECRYPT")
        lObj.put("key", "10")
        lObj.put("stringToDecrypt", "Jtnol5i6A8GQvo+0H4HDy0Zv47EbacXf68+YmVjd2A==")
        encryptDecryptUtility.execute(lObj)
    }
}