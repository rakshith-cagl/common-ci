package com.iexceed.plugins.contacts

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
class ContactsPluginTest {

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
    fun testPluginNotSupportedStatus(){

        assert(Contacts.isPlugin())
    }

    @Test
    fun whenExecute_WithMissingParams_thenError(){

        val contactsPlugin = Contacts.createPlugin(webView,
        activity, object : IapzPluginUtil{

                override fun sendError(
                    callbackId: String?,
                    errorCode: String?,
                    aResult: JSONObject?,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {

                    Assert.assertEquals("APZ-CNT-2362", errorCode)
                }
        }) as Contacts

        val emptyJson = JSONObject()
        contactsPlugin.execute(emptyJson)
    }

//    @Test
//    fun testAddContactWithParamsSuccess(){
//
//        val contactsPlugin = Contacts.createPlugin(AppzillonMainScreen.webView!!,
//        AppzillonMainScreen.activity, object : IapzPluginUtil{
//
//                override fun sendSuccess(
//                    callbackId: String?,
//                    aResult: JSONObject?,
//                    isKeepAlive: Boolean,
//                    activity: Activity,
//                    webView: WebView,
//                    isInUIThread: Boolean
//                ) {
//
//                    Assert.assertEquals("ADD_CONTACT_SUCCESS", callbackId)
//                }
//        })
//
//        val jsonObject = JSONObject()
//        val detailsObj = JSONObject()
//        jsonObject.put("id", "ADD_CONTACT_SUCCESS")
//        jsonObject.put("action", "ADDCONTACT")
//        detailsObj.put("firstName", "abc")
//        detailsObj.put("lastName", "abc")
//        detailsObj.put("phoneMobile", "9999999900")
//        detailsObj.put("phoneHome", "9999999901")
//        detailsObj.put("phoneWork", "9999999900")
//        detailsObj.put("mail", "edhvdj@jhgdj.com")
//        detailsObj.put("address", "dkjhk dkjhdk")
//        detailsObj.put("website", "google.com")
//
//        jsonObject.put("details", detailsObj)
//
//        contactsPlugin?.execute(jsonObject)
//    }

//    @Test
//    fun whenActionIsNull_proceedContacts_thenError(){
//
//        val contactsPlugin = Contacts(
//            AppzillonMainScreen.webView!!,
//            AppzillonMainScreen.activity, object : IapzPluginUtil{
//
//                override fun sendError(
//                    callbackId: String?,
//                    errorCode: String?,
//                    aResult: JSONObject?,
//                    activity: Activity,
//                    webView: WebView,
//                    isInUIThread: Boolean) {
//
//                    Assert.assertEquals("APZ-CNT-2362", errorCode)
//                }
//        })
//
//        contactsPlugin.mAction = null
//        Whitebox.invokeMethod<Any>(contactsPlugin, "proceedContacts", null)
//
//    }

    @Test
    fun whenExecute_requestForPermission_thenSuccess(){

        val contactsPlugin = Contacts.createPlugin(webView,
            activity, object : IapzPluginUtil{

                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                }
            }) as Contacts
            contactsPlugin.permissions = arrayOf<String>(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS
            )
            Whitebox.invokeMethod<Any>(contactsPlugin, "requestForPermission", null)
    }

    @Test
    fun whenExecute_permissionDeniedCallback(){

        val contactsPlugin = Contacts.createPlugin(webView,
            activity, object : IapzPluginUtil{

                override fun sendPermissionDenied(
                    pluginName: String,
                    callbackId: String?,
                    activity: Activity?,
                    webView: WebView?
                ) {

                    Assert.assertEquals("", callbackId)
                }
            }) as Contacts
        contactsPlugin.permissions = arrayOf<String>(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
        )
        Whitebox.invokeMethod<Any>(contactsPlugin, "requestForPermission", null)
    }

}