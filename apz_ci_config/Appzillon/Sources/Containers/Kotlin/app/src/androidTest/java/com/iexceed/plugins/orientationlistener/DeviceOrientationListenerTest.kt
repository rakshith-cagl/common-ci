package com.iexceed.plugins.orientationlistener

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
class DeviceOrientationListenerTest {

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
    fun testPluginSupportedStatus() {

        assert(DeviceOrientationListener.isPlugin())
    }

    @Test
    fun whenExecute_withoutParams_thenError() {

        val deviceOrientationListener = webView.let {
            DeviceOrientationListener.createPlugin(it,
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
                }) as DeviceOrientationListener
        }

        deviceOrientationListener.execute(JSONObject())
    }

    @Test
    fun whenExecute_withActionAsStartListener_portraitOrientation_thenSuccess() {

        val activity = activity
        activity.runOnUiThread {

            val deviceOrientationListener = webView.let {
                DeviceOrientationListener.createPlugin(it,
                    activity, object : IapzPluginUtil {

                        override fun sendSuccess(
                            callbackId: String?,
                            aResult: JSONObject?,
                            isKeepAlive: Boolean,
                            activity: Activity,
                            webView: WebView,
                            isInUIThread: Boolean
                        ) {

                            Assert.assertEquals("PORTRAIT", aResult?.getString("orientation"))
                        }
                    }) as DeviceOrientationListener
            }

            val jsonObject = JSONObject()
            jsonObject.put("id", "Orientation")
            jsonObject.put("action", "STARTLISTENER")
            activity.resources.configuration.orientation = 1
            deviceOrientationListener.execute(jsonObject)
        }
    }

    @Test
    fun whenExecute_withActionAsStartListener_LandscapeOrientation_thenSuccess() {

        val activity = activity
        activity.runOnUiThread {

            val deviceOrientationListener = webView.let {
                DeviceOrientationListener.createPlugin(it,
                    activity, object : IapzPluginUtil {

                        override fun sendSuccess(
                            callbackId: String?,
                            aResult: JSONObject?,
                            isKeepAlive: Boolean,
                            activity: Activity,
                            webView: WebView,
                            isInUIThread: Boolean
                        ) {

                            Assert.assertEquals("LANDSCAPE", aResult?.getString("orientation"))
                        }
                    }) as DeviceOrientationListener
            }

            val jsonObject = JSONObject()
            jsonObject.put("id", "Orientation")
            jsonObject.put("action", "STARTLISTENER")
            activity.resources.configuration.orientation = 2
            deviceOrientationListener.execute(jsonObject)
        }
    }

    @Test
    fun whenExecute_withActionAsStopListener_thenSuccess() {

        val activity = activity
        activity.runOnUiThread {

            val deviceOrientationListener = webView.let {
                DeviceOrientationListener.createPlugin(it,
                    activity, object : IapzPluginUtil {

                        override fun sendSuccess(
                            callbackId: String?,
                            aResult: JSONObject?,
                            isKeepAlive: Boolean,
                            activity: Activity,
                            webView: WebView,
                            isInUIThread: Boolean
                        ) {

                            Assert.assertEquals("stopped", aResult?.getString("event"))
                        }
                    }) as DeviceOrientationListener
            }

            val jsonObject = JSONObject()
            jsonObject.put("id", "Orientation")
            jsonObject.put("action", "STOPLISTENER")
            deviceOrientationListener.execute(jsonObject)
        }
    }
}