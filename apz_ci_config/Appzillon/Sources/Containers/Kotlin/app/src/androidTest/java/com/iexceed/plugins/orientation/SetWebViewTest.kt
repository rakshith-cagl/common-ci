package com.iexceed.plugins.orientation

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
class SetWebViewTest {

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
    fun tearDown(){

        activityScenario.close()
    }

    @Test
    fun testPluginSupportedStatus(){

        assert(SetWebView.isPlugin())
    }

    @Test
    fun whenExecute_withoutParams_thenError(){

        val setWebView = SetWebView.createPlugin(webView,
        activity, object : IapzPluginUtil{

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
        }) as SetWebView

        setWebView.execute(JSONObject())
    }

    @Test
    fun whenExecute_withModeAsLandscape_thenSuccess(){

        val activity = activity
        val setWebView = SetWebView.createPlugin(webView,
            activity, object : IapzPluginUtil{

            }) as SetWebView

        val jsonObject = JSONObject()
        jsonObject.put("orientation", "LANDSCAPE")
        setWebView.execute(jsonObject)

        Assert.assertEquals(6, activity.requestedOrientation)
    }

    @Test
    fun whenExecute_withModeAsPortrait_thenSuccess(){

        val activity = activity
        val setWebView = SetWebView.createPlugin(webView,
            activity, object : IapzPluginUtil{

            }) as SetWebView

        val jsonObject = JSONObject()
        jsonObject.put("orientation", "PORTRAIT")
        setWebView.execute(jsonObject)

        Assert.assertEquals(7, activity.requestedOrientation)
    }

    @Test
    fun whenExecute_withModeAsUnspecified_thenSuccess(){

        val activity = activity
        val setWebView = SetWebView.createPlugin(webView,
            activity, object : IapzPluginUtil{

            }) as SetWebView

        val jsonObject = JSONObject()
        jsonObject.put("orientation", "NONE")
        setWebView.execute(jsonObject)

        Assert.assertEquals(-1, activity.requestedOrientation)
    }
}