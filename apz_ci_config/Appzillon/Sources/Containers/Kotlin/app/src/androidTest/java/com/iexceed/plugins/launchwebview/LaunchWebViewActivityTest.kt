package com.iexceed.plugins.launchwebview

import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class LaunchWebViewActivityTest {

    private lateinit var launchWebViewActivity: LaunchWebViewActivity
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
        launchWebViewActivity = mock(LaunchWebViewActivity::class.java)
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

    /*@Test
    fun whenExecute_getAlertDialog_thenSuccess(){

        activity.runOnUiThread {

            val launchWebViewActivity = LaunchWebViewActivity(object : IapzPluginUtil{
            })
            val alert = Whitebox.invokeMethod<Any>(launchWebViewActivity, "getAlertDialog", 1, null, activity)
            Assert.assertNotNull(alert)
            }
        }

    @Test
    fun whenExecute_setWebViewProps_thenSuccess(){

        activity.runOnUiThread {

            val webView = webView
            val launchWebViewActivity = LaunchWebViewActivity(object : IapzPluginUtil{})
            Whitebox.invokeMethod<Any>(launchWebViewActivity, "setWebViewProps", webView)
            Assert.assertTrue(webView.settings.javaScriptCanOpenWindowsAutomatically)
            }
        }

    @Test
    fun whenExecute_sendSuccessCallback_thenSuccess(){

        activity.runOnUiThread {

            val launchWebViewActivity = LaunchWebViewActivity(object : IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("https://www.google.com/", aResult?.optString("URL"))
                }
            })

            val callbackID = "webViewActivity"
            Whitebox.invokeMethod<Any>(launchWebViewActivity, "sendSuccessCallback",
                callbackID, "https://www.google.com/", activity, webView)

        }

    }

    @Test
    fun whenExecute_isCallback_whenTrackURLStringArrayIsNull_thenSuccess(){

        activity.runOnUiThread {

            val launchWebViewActivity = LaunchWebViewActivity(object : IapzPluginUtil{
            })

            val trackURLArray: Array<String>? = null
            val finalURL = "https://www.google.com/"
            val trackURL = "https://www.google.com/"
            val isCallback = Whitebox.invokeMethod<Any>(launchWebViewActivity,
                "isCallback", trackURLArray, finalURL, trackURL) as Boolean

            Assert.assertTrue(isCallback)
        }
    }

    @Test
    fun whenExecute_isCallback_whenArrayHasURL_thenSuccess(){

        activity.runOnUiThread {

            val launchWebViewActivity = LaunchWebViewActivity(object : IapzPluginUtil{
            })

            val trackURLArray: Array<String> = arrayOf("https://www.google.com/")
            val finalURL = "https://www.google.com/"
            val trackURL = ""
            val isCallback = Whitebox.invokeMethod<Any>(launchWebViewActivity,
                "isCallback", trackURLArray, finalURL, trackURL) as Boolean

            Assert.assertTrue(isCallback)
        }
    }

    @Test
    fun whenExecute_isCallback_whenURLNotMatchingFromArray_thenSuccess() {

        activity.runOnUiThread {

            val launchWebViewActivity = LaunchWebViewActivity(object : IapzPluginUtil {
            })

            val trackURLArray: Array<String> = arrayOf("https://developer.android.com/")
            val finalURL = "https://www.google.com/"
            val trackURL = ""
            val isCallback = Whitebox.invokeMethod<Any>(
                launchWebViewActivity,
                "isCallback", trackURLArray, finalURL, trackURL
            ) as Boolean

            Assert.assertFalse(isCallback)
        }
    }

    @Test
    fun whenExecute_isCallback_whenURLNotMatchingTrackURL_thenSuccess() {

        activity.runOnUiThread {

            val launchWebViewActivity = LaunchWebViewActivity(object : IapzPluginUtil {
            })

            val trackURLArray: Array<String>? = null
            val finalURL = "https://www.google.com/"
            val trackURL = "https://developer.android.com/"
            val isCallback = Whitebox.invokeMethod<Any>(
                launchWebViewActivity,
                "isCallback", trackURLArray, finalURL, trackURL
            ) as Boolean

            Assert.assertFalse(isCallback)
        }
    }

    @Test
    fun whenExecute_redirectToSafetyBrowsing_thenSuccess(){

        activity.runOnUiThread {

            val launchWeb = LaunchWebViewActivity(object : IapzPluginUtil{})

            val activity = activity
            val safeBrowsingResponse = object : SafeBrowsingResponse(){
                override fun showInterstitial(allowReporting: Boolean) {

                }

                override fun proceed(report: Boolean) {

                }

                override fun backToSafety(report: Boolean) {

                }
            }

            launchWeb.redirectToSafetyBrowsing(activity, safeBrowsingResponse)

            doNothing().`when`(launchWebViewActivity).redirectToSafetyBrowsing(activity, safeBrowsingResponse)

            launchWebViewActivity.redirectToSafetyBrowsing(activity, safeBrowsingResponse)

            verify(launchWebViewActivity, times(1)).redirectToSafetyBrowsing(activity, safeBrowsingResponse)
        }
    }

    @Test
    fun whenExecute_getWebClient_thenSuccess(){

        activity.runOnUiThread {

            val launchWebViewActivity = LaunchWebViewActivity(object : IapzPluginUtil {
            })

            val callbackID = "webViewActivity"
            val activity = activity
            val webView = WebView(activity)
            val trackURLArray: Array<String> = arrayOf("https://developer.android.com/")
            val trackURL = "https://developer.android.com/"
            Whitebox.invokeMethod<Any>(launchWebViewActivity, "getWebClient",
            webView, activity, trackURLArray, trackURL, callbackID)

            Assert.assertNotNull(webView.webViewClient)
        }
    }

    @Test
    fun whenExecute_sendError_thenError(){

        activity.runOnUiThread {

            val launchWebViewActivity = LaunchWebViewActivity(object : IapzPluginUtil {

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("Error Message", aResult?.optString("Error"))
                }
            })

            val jsonObject = JSONObject()
            jsonObject.put("Error", "Error Message")
            val callbackID = "webViewActivity"
            val activity = activity
            val webView = webView
            Whitebox.invokeMethod<Any>(launchWebViewActivity, "sendError",
                callbackID, activity, webView, jsonObject)
        }
    }

    @Test
    fun whenExecute_getPostString_thenSuccess(){

        activity.runOnUiThread {

            val launchWebViewActivity = LaunchWebViewActivity(object : IapzPluginUtil {

            })

            val jsonObject = JSONObject()
            val postString = Whitebox.invokeMethod<Any>(launchWebViewActivity, "getPostString",
                jsonObject)

            Assert.assertNotNull(postString)
        }
    }

    @Test
    fun whenExecute_getDownloadListener_thenSuccess(){

        activity.runOnUiThread {

            val launchWebViewActivity = LaunchWebViewActivity(object : IapzPluginUtil {

            })

            val listener = Whitebox.invokeMethod<Any>(launchWebViewActivity, "getDownloadListener",
            null)

            Assert.assertNotNull(listener)
        }
    }*/
}