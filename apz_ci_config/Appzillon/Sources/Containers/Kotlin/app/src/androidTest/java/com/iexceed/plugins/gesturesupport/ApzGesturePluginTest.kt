package com.iexceed.plugins.gesturesupport

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
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.powermock.reflect.Whitebox

@RunWith(AndroidJUnit4::class)
class ApzGesturePluginTest {

    private lateinit var apzGestureMock: ApzGesturePlugin
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
        apzGestureMock = Mockito.mock(ApzGesturePlugin::class.java)
    }

    @After
    fun tearDown() {

        activityScenario.close()
    }

    @Test
    fun testPluginSupportedStatus() {

        assert(ApzGesturePlugin.isPlugin())
    }

    @Test
    fun whenExecute_withoutParams_thenError() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(
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
            }) as ApzGesturePlugin

        apzGesturePlugin.execute(JSONObject())
    }

    @Test
    fun whenExecute_getGestureCallBack_thenSuccess() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(
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

                    Assert.assertEquals("TEST_GESTURE", aResult?.optString("event"))
                }
            }) as ApzGesturePlugin

        val eventDetected = "TEST_GESTURE"
        Whitebox.invokeMethod<Any>(apzGesturePlugin, "getGestureCallBack", eventDetected)
    }

    @Test
    fun whenExecute_setTapCount_forSingleTap_thenSuccess() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(
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

                    Assert.assertEquals("singleTap", aResult?.optString("event"))
                }
            }) as ApzGesturePlugin

        apzGesturePlugin.setTapCount(1)
    }

    @Test
    fun whenExecute_setTapCount_forDouble_thenSuccess() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(
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

                    Assert.assertEquals("doubleTap", aResult?.optString("event"))
                }
            }) as ApzGesturePlugin

        apzGesturePlugin.setTapCount(2)
    }

    @Test
    fun whenExecute_setTapCount_tripleTap_thenSuccess() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(
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

                    Assert.assertEquals("tripleTap", aResult?.optString("event"))
                }
            }) as ApzGesturePlugin

        apzGesturePlugin.setTapCount(3)
    }

    @Test
    fun whenExecute_sendError_thenError() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(
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

                    Assert.assertEquals("TEST_ERROR_CODE", errorCode)
                }
            }) as ApzGesturePlugin

        Whitebox.invokeMethod<Any>(apzGesturePlugin, "sendError", "TEST_ERROR_CODE")
    }

    @Test
    fun whenExecute_fetchTapCounter_thenSuccess() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {

            }) as ApzGesturePlugin

        val delay = 500L

        activity.runOnUiThread {

            val tapCounter =
                Whitebox.invokeMethod<Any>(apzGesturePlugin, "fetchTapCounter", delay, delay)
            Assert.assertNotNull(tapCounter)
        }
    }

    @Test
    fun whenExecute_fetchScaleGestureDetector_thenSuccess() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {

            }) as ApzGesturePlugin

        val context = activity
        val scaleListener = apzGesturePlugin.ScaleListener()

        activity.runOnUiThread {

            val scaleGestureDetector = Whitebox.invokeMethod<Any>(
                apzGesturePlugin,
                "fetchScaleGestureDetector", context, scaleListener
            )
            Assert.assertNotNull(scaleGestureDetector)
        }
    }

    @Test
    fun whenExecute_fetchGestureDetector_thenSuccess() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {

            }) as ApzGesturePlugin

        val context = activity
        val gestureListener = apzGesturePlugin.GestureListener()

        activity.runOnUiThread {

            val gestureDetector = Whitebox.invokeMethod<Any>(
                apzGesturePlugin,
                "fetchGestureDetector", context, gestureListener
            )
            Assert.assertNotNull(gestureDetector)
        }
    }

    @Test
    fun whenExecute_stopListener_thenSuccess() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {

            }) as ApzGesturePlugin

        val jsonObject = JSONObject()
        apzGesturePlugin.stopListener(jsonObject)
        doNothing().`when`(apzGestureMock).stopListener(jsonObject)
        apzGestureMock.stopListener(jsonObject)
        verify(apzGestureMock, times(1)).stopListener(jsonObject)
    }

    @Test
    fun whenExecute_startListener_withNoParams_thenError() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(webView,
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
            }) as ApzGesturePlugin

        activity.runOnUiThread {

            apzGesturePlugin.startListener(JSONObject())
        }
    }

    @Test
    fun whenExecute_startListener_thenSuccess() {

        val apzGesturePlugin = ApzGesturePlugin.createPlugin(webView,
            activity, object : IapzPluginUtil {

            }) as ApzGesturePlugin

        activity.runOnUiThread {

            val jsonObject = JSONObject()
            jsonObject.put("id", "GESTURE_SUPPORT")
            apzGesturePlugin.startListener(jsonObject)
            doNothing().`when`(apzGestureMock).startListener(jsonObject)
            apzGestureMock.startListener(jsonObject)
            verify(apzGestureMock, times(1)).startListener(jsonObject)
        }
    }

    @Test
    fun whenExecute_withParams_startAsAction_thenSuccess() {

        val jsonObject = JSONObject()
        jsonObject.put("id", "GESTURE_PLUGIN")
        jsonObject.put("action", "START")

        activity.runOnUiThread {

            doNothing().`when`(apzGestureMock).execute(jsonObject)
            apzGestureMock.execute(jsonObject)
            verify(apzGestureMock, times(1)).execute(jsonObject)
        }
    }

    @Test
    fun whenExecute_withParams_stopAsAction_thenSuccess() {

        val jsonObject = JSONObject()
        jsonObject.put("id", "GESTURE_PLUGIN")
        jsonObject.put("action", "STOP")

        activity.runOnUiThread {

            doNothing().`when`(apzGestureMock).execute(jsonObject)
            apzGestureMock.execute(jsonObject)
            verify(apzGestureMock, times(1)).execute(jsonObject)
        }
    }
}