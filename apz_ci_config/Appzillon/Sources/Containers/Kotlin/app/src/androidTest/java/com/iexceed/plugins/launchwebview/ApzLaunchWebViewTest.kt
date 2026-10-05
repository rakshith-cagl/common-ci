package com.iexceed.plugins.launchwebview

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
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class ApzLaunchWebViewTest {

    private lateinit var apzLaunchWebViewMock: ApzLaunchWebView
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
        apzLaunchWebViewMock = mock(ApzLaunchWebView::class.java)
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

        assert(ApzLaunchWebView.isPlugin())
    }

    @Test
    fun whenExecute_withNoParams_thenError(){

        val apzLaunchWebView = webView.let {

            ApzLaunchWebView.createPlugin(it,
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
            })
        } as ApzLaunchWebView
        apzLaunchWebView.execute(JSONObject())
    }

    @Test
    fun whenExecute_withActionAsClose_thenSuccess(){

        val apzLaunchWebView = webView.let {

            ApzLaunchWebView.createPlugin(it,
                activity, object : IapzPluginUtil{

                    override fun sendSuccess(
                        callbackId: String?,
                        aResult: JSONObject?,
                        isKeepAlive: Boolean,
                        activity: Activity,
                        webView: WebView,
                        isInUIThread: Boolean
                    ) {

                        Assert.assertEquals("webView Closed.", aResult?.optString("text"))
                    }
                })
        } as ApzLaunchWebView

        val jsonObject = JSONObject()
        jsonObject.put("id","LAUNCH_WEB_VIEW")
        jsonObject.put("action","Close")
        activity.runOnUiThread {
            apzLaunchWebView.execute(jsonObject)
        }
    }

    @Test
    fun whenExecute_openWebView_thenSuccess(){

        val callbackID = "LAUNCH_WEB_VIEW"
        val jsonObject = JSONObject()
        jsonObject.put("id",callbackID)
        jsonObject.put("URL","https://developer.android.com/")
        jsonObject.put("trackURL","https://developer.android.com/about")
        jsonObject.put("cancelButton","Y")

        doNothing().`when`(apzLaunchWebViewMock).openWebView(jsonObject, activity, callbackID,
            webView
        )
        apzLaunchWebViewMock.openWebView(jsonObject, activity, callbackID, webView)
        verify(apzLaunchWebViewMock, times(1))
                .openWebView(jsonObject, activity, callbackID, webView)
    }

    @Test
    fun whenExecute_withActionAsOpen_thenSuccess(){

        val apzLaunchWebView = ApzLaunchWebView.createPlugin(webView,
        activity, object : IapzPluginUtil{}) as ApzLaunchWebView

        val callbackID = "LAUNCH_WEB_VIEW"
        val jsonObject = JSONObject()
        jsonObject.put("id",callbackID)
        jsonObject.put("action","Open")
        jsonObject.put("URL","https://developer.android.com/")
        jsonObject.put("trackURL","https://developer.android.com/about")
        jsonObject.put("cancelButton","Y")

        apzLaunchWebView.execute(jsonObject)
        Assert.assertEquals("https://developer.android.com/", apzLaunchWebView.url)
    }
}