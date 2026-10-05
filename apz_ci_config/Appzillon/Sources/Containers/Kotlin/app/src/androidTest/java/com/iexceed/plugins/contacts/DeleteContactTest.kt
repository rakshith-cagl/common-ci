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
class DeleteContactTest {

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
    fun whenExecute_withNoParams_thenError(){

        val deleteContact = DeleteContact("DeleteContact",
        activity,
        webView, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                   Assert.assertEquals("APZ-CNT-044", errorCode)
                }
        })

        deleteContact.deleteContact(JSONObject())
    }

    @Test
    fun whenExecute_withEmptyParams_thenError(){

        val deleteContact = DeleteContact("DeleteContact",
            activity,
            webView, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-044", errorCode)
                }
            })

         val jsonObject = JSONObject()
         val deleteCriteria = JSONObject()
         deleteCriteria.put("firstName", "")
         deleteCriteria.put("lastName", "")
         deleteCriteria.put("phoneMobile", "")
         deleteCriteria.put("phoneHome", "")
         deleteCriteria.put("phoneWork", "")

        jsonObject.put("deleteCriteria", deleteCriteria)
        activity.runOnUiThread {

            deleteContact.deleteContact(jsonObject)
        }
    }

    @Test
    fun whenExecute_withExistingContactDetails_thenSuccess(){

        val deleteContact = DeleteContact("DeleteContact",
            activity,
            webView, object : IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("DeleteContact", callbackId)
                }
            })


        //Providing an existing contact details
        val jsonObject = JSONObject()
        val deleteCriteria = JSONObject()
        deleteCriteria.put("firstName", "Anu")
        deleteCriteria.put("lastName", "")
        deleteCriteria.put("phoneMobile", "344")
        deleteCriteria.put("phoneHome", "")
        deleteCriteria.put("phoneWork", "")

        jsonObject.put("deleteCriteria", deleteCriteria)
        activity.runOnUiThread {

            deleteContact.deleteContact(jsonObject)
        }
    }

    @Test
    fun whenExecute_withExistingDuplicateContactDetails_thenError(){

        val deleteContact = DeleteContact("DeleteContact",
            activity,
            webView, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-044", errorCode)
                }
            })


        //Providing an existing duplicate contact details
        val jsonObject = JSONObject()
        val deleteCriteria = JSONObject()
        deleteCriteria.put("firstName", "Anu")
        deleteCriteria.put("lastName", "")
        deleteCriteria.put("phoneMobile", "333")
        deleteCriteria.put("phoneHome", "")
        deleteCriteria.put("phoneWork", "")

        jsonObject.put("deleteCriteria", deleteCriteria)
        activity.runOnUiThread {

            deleteContact.deleteContact(jsonObject)
        }
    }

    @Test
    fun whenExecute_withNonExistingContactDetails_thenError(){


        val deleteContact = DeleteContact("DeleteContact",
            activity,
            webView, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-044", errorCode)
                }
            })

        //Providing the details of a non-existing contact details
        val jsonObject = JSONObject()
        val deleteCriteria = JSONObject()
        deleteCriteria.put("firstName", "first")
        deleteCriteria.put("lastName", "last")
        deleteCriteria.put("phoneMobile", "34242")
        deleteCriteria.put("phoneHome", "")
        deleteCriteria.put("phoneWork", "")

        jsonObject.put("deleteCriteria", deleteCriteria)
        activity.runOnUiThread {

            deleteContact.deleteContact(jsonObject)
        }
    }

    @Test
    fun whenExecute_withExistingContactDetails_thenError(){

        val deleteContact = DeleteContact("DeleteContact",
            activity,
            webView, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-044", errorCode)
                }
            })

        //Providing an existing contact details
        val jsonObject = JSONObject()
        val deleteCriteria = JSONObject()
        deleteCriteria.put("firstName", "Anu")
        deleteCriteria.put("lastName", "")
        deleteCriteria.put("phoneMobile", "344")
        deleteCriteria.put("phoneHome", "")
        deleteCriteria.put("phoneWork", "")

        jsonObject.put("deleteCriteria", deleteCriteria)
        activity.runOnUiThread {

            deleteContact.deleteContact(jsonObject)
        }
    }
}