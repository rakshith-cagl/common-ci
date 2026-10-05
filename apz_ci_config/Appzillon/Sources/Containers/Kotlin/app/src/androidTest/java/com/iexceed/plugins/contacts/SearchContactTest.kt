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
class SearchContactTest {


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

        val searchContact = SearchContact("SearchContact", activity,
        webView, object : IapzPluginUtil{

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

        searchContact.searchContacts(JSONObject())
    }

    @Test
    fun whenExecute_withExistingContactsDetails_thenSuccess(){

        val searchContact = SearchContact("SearchContact", activity,
            webView, object : IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("SearchContact", callbackId)
                }
            })

        val mainJSONObject = JSONObject()
        val searchCriteria = JSONObject()

        searchCriteria.put("firstName", "Test")
        searchCriteria.put("lastName", "")
        searchCriteria.put("phoneMobile", "232")
        searchCriteria.put("phoneHome", "")
        searchCriteria.put("phoneWork", "")

        mainJSONObject.put("searchCriteria", searchCriteria)

        activity.runOnUiThread {

            searchContact.searchContacts(mainJSONObject)
        }
    }

    @Test
    fun whenExecute_provideNonExistingContact_thenError(){

        val searchContact = SearchContact("SearchContact", activity,
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

        val mainJSONObject = JSONObject()
        val searchCriteria = JSONObject()

        searchCriteria.put("firstName", "kjfkw")
        searchCriteria.put("lastName", "ragfewfwf")
        searchCriteria.put("phoneMobile", "432545")
        searchCriteria.put("phoneHome", "")
        searchCriteria.put("phoneWork", "")

        mainJSONObject.put("searchCriteria", searchCriteria)

        activity.runOnUiThread {

            searchContact.searchContacts(mainJSONObject)
        }
    }

    @Test
    fun whenExecute_fetchAllContacts_withSortOrderFirstName_thenSuccess(){

        val searchContact = SearchContact("SearchContact", activity,
        webView, object : IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertTrue(true)
                }
        })

        val jsonObject = JSONObject()
        val detailsObj = JSONObject()

        detailsObj.put("sortOrder", "firstName")
        jsonObject.put("details", detailsObj)

        activity.runOnUiThread {

            searchContact.fetchAllContacts(jsonObject)
        }
    }

    @Test
    fun whenExecute_fetchAllContacts_withSortOrderLastName_thenSuccess(){

        val searchContact = SearchContact("SearchContact", activity,
            webView, object : IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertTrue(true)
                }
            })

        val jsonObject = JSONObject()
        val detailsObj = JSONObject()

        detailsObj.put("sortOrder", "lastName")
        jsonObject.put("details", detailsObj)

        activity.runOnUiThread {

            searchContact.fetchAllContacts(jsonObject)
        }
    }

    @Test
    fun whenExecute_fetchAllContacts_withSortOrderUserDefault_thenSuccess(){

        val searchContact = SearchContact("SearchContact", activity,
            webView, object : IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertTrue(true)
                }
            })

        val jsonObject = JSONObject()
        val detailsObj = JSONObject()

        detailsObj.put("sortOrder", "userDefault")
        jsonObject.put("details", detailsObj)

        activity.runOnUiThread {

            searchContact.fetchAllContacts(jsonObject)
        }
    }

    @Test
    fun whenExecute_fetchAllContacts_withSortOrderIsEmpty_thenSuccess(){

        val searchContact = SearchContact("SearchContact", activity,
            webView, object : IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertTrue(true)
                }
            })

        val jsonObject = JSONObject()
        val detailsObj = JSONObject()

        detailsObj.put("sortOrder", "")
        jsonObject.put("details", detailsObj)

        activity.runOnUiThread {

            searchContact.fetchAllContacts(jsonObject)
        }
    }

}
