package com.iexceed.plugins.speechtotext

import android.app.Activity
import android.speech.SpeechRecognizer
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
class SpeechToTextTest {


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
        val stt = SpeechToText.createPlugin(
            webView,
            activity,
            object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-203",errorCode)
                }
            }) as SpeechToText

        val lObj = JSONObject()
        lObj.put("command", "PLGN_TEXT_TO_SPEECH")
        stt.execute(lObj)
    }



    @Test
    fun when_startVoiceConversion_without_param_then_error() {
        val startVoiceConversion = SpeechToText.createPlugin(
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
            }) as SpeechToText

        val lObj = JSONObject()
        lObj.put("id", "SPEECHTOTEXT_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_TEXT_TO_SPEECH")

        Whitebox.invokeMethod<Any>(startVoiceConversion,"startVoiceConversion", lObj)
    }

    @Test
    fun when_startVoiceConversion_with_param_supportedLanguages_void_then_error() {
        val startVoiceConversion = SpeechToText.createPlugin(
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
            }) as SpeechToText

        val lObj = JSONObject()
        lObj.put("id", "SPEECHTOTEXT_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_TEXT_TO_SPEECH")

        lObj.put("action", "stop")
        lObj.put("languageCode", "")
        lObj.put("pauseRecognize", "Y")
        Whitebox.invokeMethod<Any>(startVoiceConversion,"startVoiceConversion", lObj)
    }

    @Test
    fun when_startVoiceConversion_with_param_supportedLanguages_null_then_error() {
        val startVoiceConversion = SpeechToText.createPlugin(
            webView, activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-331",errorCode)
                }
            }) as SpeechToText

        val lObj = JSONObject()
        lObj.put("id", "SPEECHTOTEXT_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_TEXT_TO_SPEECH")

        lObj.put("action", "stop")
        lObj.put("languageCode", "")
        lObj.put("pauseRecognize", "Y")
        startVoiceConversion.supportedLanguages= listOf("x" , "y" , "z")
        Whitebox.invokeMethod<Any>(startVoiceConversion,"startVoiceConversion", lObj)
    }

    @Test
    fun when_startVoiceConversion_with_param_speech_not_null_then_error() {
        val startVoiceConversion = SpeechToText.createPlugin(
            webView, activity, object : IapzPluginUtil {
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
            }) as SpeechToText

        val lObj = JSONObject()
        lObj.put("id", "SPEECHTOTEXT_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_TEXT_TO_SPEECH")

        lObj.put("action", "stop")
        lObj.put("languageCode", "")
        lObj.put("pauseRecognize", "Y")

        activity.runCatching {
            startVoiceConversion.speech= SpeechRecognizer.createSpeechRecognizer(this.applicationContext)

        }


        Whitebox.invokeMethod<Any>(startVoiceConversion,"startVoiceConversion", lObj)
    }





    @Test
     fun when_startVoiceConversion_action_stop_supportedLanguages_not_null_then_success() {
        val startVoiceConversion = SpeechToText.createPlugin(
            webView, activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("STOPPED", aResult?.get("action"))
                }
            }) as SpeechToText

        val lObj = JSONObject()
        lObj.put("id", "SPEECHTOTEXT_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_TEXT_TO_SPEECH")

        lObj.put("action", "stop")
        lObj.put("languageCode", "")
        lObj.put("pauseRecognize", "Y")

        startVoiceConversion.supportedLanguages= listOf("x" , "y" , "z" , "")

        activity.runCatching {
            startVoiceConversion.speech= SpeechRecognizer.createSpeechRecognizer(this.applicationContext)
        }

        startVoiceConversion.successCallback("STOPPED","")
        Whitebox.invokeMethod<Any>(startVoiceConversion,"startVoiceConversion", lObj)
    }




    @Test
    fun when_startVoiceConversion_action_start_supportedLanguages_not_null_then_error() {
        val startVoiceConversion = SpeechToText.createPlugin(
            webView, activity, object : IapzPluginUtil {
                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("APZ-CNT-203", errorCode)
                }
            }) as SpeechToText

        val lObj = JSONObject()
        lObj.put("id", "SPEECHTOTEXT_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_TEXT_TO_SPEECH")

        lObj.put("action", "start")
        lObj.put("languageCode", "")
        lObj.put("pauseRecognize", "Y")

        startVoiceConversion.supportedLanguages = listOf("x", "y", "z", "")

        activity.runCatching {
            startVoiceConversion.speech =
                SpeechRecognizer.createSpeechRecognizer(this.applicationContext)
        }

        startVoiceConversion.successCallback("STARTED", "")
        Whitebox.invokeMethod<Any>(startVoiceConversion, "startVoiceConversion", lObj)
    }

        @Test
        fun when_onError_then_success() {
            val onError = SpeechToText.createPlugin(
                webView, activity, object : IapzPluginUtil {
                    override fun sendSuccess(
                        callbackId: String?,
                        aResult: JSONObject?,
                        isKeepAlive: Boolean,
                        activity: Activity,
                        webView: WebView,
                        isInUIThread: Boolean
                    ) {

//                    Assert.assertEquals("SPEECHTOTEXT_CALLBACK", callbackId)
                    }
                }) as SpeechToText

            val lObj = JSONObject()
            lObj.put("id", "SPEECHTOTEXT_CALLBACK")
            lObj.put("callBack", "executeCallback")
            lObj.put("command", "PLGN_TEXT_TO_SPEECH")

            val err = SpeechRecognizer.ERROR_NO_MATCH
            Whitebox.invokeMethod<Any>(onError, "onError", err)
        }


}