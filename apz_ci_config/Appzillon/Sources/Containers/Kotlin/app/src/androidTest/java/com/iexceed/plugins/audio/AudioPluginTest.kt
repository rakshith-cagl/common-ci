package com.iexceed.plugins.audio

import android.Manifest
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
class AudioPluginTest {

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
        assert(AudioPlugin.isAudioPlugin())
    }

    @Test
    fun whenExecute_WithMissingParams_thenError() {
        val audioPlugin = AudioPlugin.createPlugin(
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
            }) as AudioPlugin
        val lObj = JSONObject()
        audioPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_WithParams_thenNoError() {
        val audioPlugin = AudioPlugin.createPlugin(
            this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {}
            }) as AudioPlugin
        val lObj = JSONObject()
        lObj.put("id", "AUDIO_CLBACK")
        lObj.put("action","START")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_AUDIO")
        lObj.put("base64", "abc")
        lObj.put("samplingRate", "")
        lObj.put("bitRate", "")
        lObj.put("channel", "")
        lObj.put("timeDuration", "1")
        lObj.put("fileName", "abc")
        lObj.put("wavFileFormat", "N")
        lObj.put("location", "storage")
        audioPlugin.execute(lObj)
    }

    /*@Test
    fun when_confirmAudioPermissionToProceed_actionRandom_thenError() {
        val audioPlugin = AudioPlugin.createPlugin(
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
//                    when (callbackId) {
//                        "ErrorCallback082" ->
                    Assert.assertEquals("APZ-CNT-082", errorCode)
//                    }
                }
            }) as AudioPlugin
//        audioPlugin.mCallbackId="ErrorCallback082"
        Whitebox.invokeMethod<Any>(audioPlugin,"confirmAudioPermissionToProceed", null)
    }*/

    /*@Test
    fun when_actionRecord_audioFileLocationNull_thenError() {
        val audioPlugin = AudioPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-031", errorCode)
                }
            }) as AudioPlugin
        audioPlugin.mAction="record"
        audioPlugin.wavFileFormat="N"
        Whitebox.invokeMethod<Any>(audioPlugin,"confirmAudioPermissionToProceed", null)
    }*/

    /*@Test
    fun when_actionRecord_audioFileLocationEmpty_thenError() {
        val audioPlugin = AudioPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-031", errorCode)
                }
            }) as AudioPlugin
        audioPlugin.mAction="record"
        audioPlugin.wavFileFormat="N"
        audioPlugin.mJsonObject= JSONObject()
        audioPlugin.mJsonObject?.put("location","")
        Whitebox.invokeMethod<Any>(audioPlugin,"confirmAudioPermissionToProceed", null)
    }*/

    /*@Test
    fun when_actionRecord_audioFileNameEmpty_thenError() {
        val audioPlugin = AudioPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-095", errorCode)
                }
            }) as AudioPlugin
        audioPlugin.mAction="record"
        audioPlugin.wavFileFormat="N"
        audioPlugin.mJsonObject= JSONObject()
        audioPlugin.mJsonObject?.put("location","external")
        Whitebox.invokeMethod<Any>(audioPlugin,"confirmAudioPermissionToProceed", null)
    }*/

    /*@Test
    fun when_actionSave_FileFormatY_BASE64N_thenSuccess() {
        val audioPlugin = AudioPlugin.createPlugin(
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
                    Assert.assertEquals("BASE64_YN_SUCCESS", callbackId)
                }
            }) as AudioPlugin
        audioPlugin.recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            0, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT, 2300)
        audioPlugin.mBase64="N"
        audioPlugin.mCallbackId="BASE64_YN_SUCCESS"
        audioPlugin.mAction="save"
        audioPlugin.wavFileFormat="Y"
        audioPlugin.mFileName="abc"
        audioPlugin.mSampleRate="16000"
        audioPlugin.mBitRate="16"
        audioPlugin.mChannel="mono"
        audioPlugin.mAudioFileLocation="external"
        audioPlugin.mJsonObject= JSONObject()
        audioPlugin.mJsonObject?.put("location","external")
        Whitebox.invokeMethod<Any>(audioPlugin,"confirmAudioPermissionToProceed", null)
    }*/

    /*@Test
    fun when_actionSave_FileFormatN_BASE64N_recorderNotRunning_thenError() {
        val audioPlugin = AudioPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-095", errorCode)
                }
            }) as AudioPlugin
        audioPlugin.mRecorder = MediaRecorder()
        audioPlugin.mBase64="N"
        audioPlugin.mCallbackId="BASE64_N_SUCCESS"
        audioPlugin.mAction="save"
        audioPlugin.wavFileFormat="N"
        audioPlugin.mFileName="abc"
        audioPlugin.mSampleRate="16000"
        audioPlugin.mBitRate="16"
        audioPlugin.mChannel="mono"
        audioPlugin.mAudioFileLocation="external"
        audioPlugin.mJsonObject= JSONObject()
        audioPlugin.mJsonObject?.put("location","external")
        Whitebox.invokeMethod<Any>(audioPlugin,"confirmAudioPermissionToProceed", null)
    }*/

    /*@Test
    fun when_actionSave_FileFormatY_BASE64Y_thenSuccess() {
        val audioPlugin = AudioPlugin.createPlugin(
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
                    Assert.assertEquals("BASE64_YY_SUCCESS", callbackId)
                }
            }) as AudioPlugin
        audioPlugin.recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            0, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT, 2300)
        audioPlugin.mBase64="Y"
        audioPlugin.mCallbackId="BASE64_YY_SUCCESS"
        audioPlugin.mAction="save"
        audioPlugin.wavFileFormat="Y"
        audioPlugin.mFileName="abc"
        audioPlugin.mSampleRate="16000"
        audioPlugin.mBitRate="16"
        audioPlugin.mChannel="mono"
        audioPlugin.mAudioFileLocation="external"
        audioPlugin.mJsonObject= JSONObject()
        audioPlugin.mJsonObject?.put("location","external")
        Whitebox.invokeMethod<Any>(audioPlugin,"confirmAudioPermissionToProceed", null)
    }*/

    /*@Test
    fun when_actionPlay_FileFormatY_thenError() {
        val audioPlugin = AudioPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-002", errorCode)
                }
            }) as AudioPlugin
        audioPlugin.mCallbackId="AUDIO_SUCCESS"
        audioPlugin.mAction="play"
        audioPlugin.wavFileFormat="Y"
        audioPlugin.mFileName="abc"
        audioPlugin.mSampleRate="16000"
        audioPlugin.mBitRate="16"
        audioPlugin.mChannel="mono"
        audioPlugin.mJsonObject= JSONObject()
        audioPlugin.mJsonObject?.put("location","external")
        Whitebox.invokeMethod<Any>(audioPlugin,"confirmAudioPermissionToProceed", null)
    }*/

    /*@Test
    fun when_actionPlay_FileFormatN_thenError() {
        val audioPlugin = AudioPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-002", errorCode)
                }
            }) as AudioPlugin
        audioPlugin.mCallbackId="AUDIO_SUCCESS"
        audioPlugin.mAction="play"
        audioPlugin.wavFileFormat="N"
        audioPlugin.mFileName="abc"
        audioPlugin.mSampleRate="16000"
        audioPlugin.mBitRate="16"
        audioPlugin.mChannel="mono"
        audioPlugin.mJsonObject= JSONObject()
        audioPlugin.mJsonObject?.put("location","external")
        Whitebox.invokeMethod<Any>(audioPlugin,"confirmAudioPermissionToProceed", null)
    }*/

    /*@Test
    fun when_actionPlay_FileFormatN_isPausedTrue_thenSuccess() {
        val audioPlugin = AudioPlugin.createPlugin(
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
                    Assert.assertEquals("AUDIO_PLAY_SUCCESS", callbackId)
                }
            }) as AudioPlugin
        audioPlugin.mPlayer = MediaPlayer()
        audioPlugin.isPaused=true
        audioPlugin.mCallbackId="AUDIO_PLAY_SUCCESS"
        audioPlugin.mAction="play"
        audioPlugin.wavFileFormat="N"
        audioPlugin.mFileName="abc"
        audioPlugin.mSampleRate="16000"
        audioPlugin.mBitRate="16"
        audioPlugin.mChannel="mono"
        audioPlugin.mJsonObject= JSONObject()
        audioPlugin.mJsonObject?.put("location","external")
        Whitebox.invokeMethod<Any>(audioPlugin,"confirmAudioPermissionToProceed", null)
    }*/

    /*@Test
    fun when_actionPlay_FileFormatN_isPausedTrue_PlayerNull_thenError() {
        val audioPlugin = AudioPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-093", errorCode)
                }
            }) as AudioPlugin
        audioPlugin.isPaused=true
        audioPlugin.mCallbackId="AUDIO_PLAY_SUCCESS"
        audioPlugin.mAction="play"
        audioPlugin.wavFileFormat="N"
        audioPlugin.mFileName="abc"
        audioPlugin.mSampleRate="16000"
        audioPlugin.mBitRate="16"
        audioPlugin.mChannel="mono"
        audioPlugin.mJsonObject= JSONObject()
        audioPlugin.mJsonObject?.put("location","external")
        Whitebox.invokeMethod<Any>(audioPlugin,"confirmAudioPermissionToProceed", null)
    }*/

    @Test
    fun when_requestForPermission_thenNoError() {
        val audioPlugin = AudioPlugin.createPlugin(
            this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {}
            }) as AudioPlugin
        audioPlugin.permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
        Whitebox.invokeMethod<Any>(audioPlugin,"requestForPermission", null)
    }

    @Test
    fun test_permissionDeniedCallback() {
        val audioPlugin = AudioPlugin.createPlugin(
            this.webView,
            this.activity, object : IapzPluginUtil {
                override fun sendPermissionDenied(
                    pluginName: String,
                    callbackId: String?,
                    activity: Activity?,
                    webView: WebView?
                ) {
                    Assert.assertEquals("AUDIO_PERMISSION_DENIED_CALLBACK",callbackId)
                }
            }) as AudioPlugin
        audioPlugin.mCallbackId="AUDIO_PERMISSION_DENIED_CALLBACK"
        Whitebox.invokeMethod<Any>(audioPlugin,"permissionDeniedCallback", null)
    }
}
