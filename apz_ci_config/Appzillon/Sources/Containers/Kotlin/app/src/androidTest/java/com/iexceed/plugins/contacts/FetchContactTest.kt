package com.iexceed.plugins.contacts

import android.app.Activity
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FetchContactTest {

    lateinit var activityScenario: ActivityScenario<AppzillonMainScreen>

    //Rule that temporarily grants necessary run time permission
    // for running the test.
    @get:Rule
    val contactsPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.WRITE_CONTACTS,
    )
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
    fun whenExecute_fetchContact_thenError(){

        val fetchContact = FetchContact("FetchContact", activity,
         webView, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-328", errorCode)
                }
         })

        activity.runOnUiThread {

            fetchContact.fetchContacts()
        }
    }

    @Test
    fun whenExecute_fetchContact_thenSuccess(){

        val fetchContact = FetchContact("FetchContact", activity,
            webView, object : IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                    Assert.assertEquals("FetchContact", callbackId)
                }
            })

        activity.runOnUiThread {

            fetchContact.fetchContacts()
        }
    }
}