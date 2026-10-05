package com.iexceed.plugins.fileoperation

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
class FileOperationBrowserTest {

    private val TAG = "ApzFileOperationPluginTest"
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

    //for FileCreate
    @Test
    fun when_mAction_is_null_error(){
        val fileOperation = ApzFileOperationPlugin.createPlugin(
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
            }) as ApzFileOperationPlugin
        val lObj = JSONObject()
        lObj.put("command", "PLGN_CRT_FILE")
        fileOperation.execute(lObj)

    }

    @Test
    fun when_mAction_equal_filecreate_success(){
        val fileOperation = ApzFileOperationPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {  }
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_CRT_FILE")
        lObj.put("action", "FILECREATE")
        fileOperation.execute(lObj)

    }

    //for Browser:
    @Test
    fun when_mAction_equal_browser_error(){
        val fileOperation = ApzFileOperationPlugin.createPlugin(
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
            }) as ApzFileOperationPlugin
        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        fileOperation.execute(lObj)

    }
    @Test
    fun when_mAction_equal_browser_success(){
        val browser = ApzFileOperationPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {  }
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        browser.execute(lObj)

    }


    //for fileCategory : AUDIO
    /*@Test
    fun when_fileBrowser_category_audio_error(){
        val audio = ApzFileOperationPlugin.createPlugin(
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
//                    Assert.assertEquals("APZ-CNT-328", errorCode)
                }
            }) as ApzFileOperationPlugin
        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        audio.isOpenable=="Y"
        Whitebox.invokeMethod<Any>(ApzFileOperationPlugin,"proceedFileOperation", null)

    }*/
    @Test
    fun when_fileBrowser_category_audio_success(){
        val audio = ApzFileOperationPlugin.createPlugin(
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
                    Assert.assertEquals("BASE64_YN_SUCCESS", aResult)
                }
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        lObj.put("fileCategory", "AUDIO")
        lObj.put("result", "filePath")

        audio.isOpenable="Y"
        audio.execute(lObj)

    }


    //for fileCategory : VIDEO
    /*@Test
    fun when_fileBrowser_category_video_error(){
        val video = ApzFileOperationPlugin.createPlugin(
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
            }) as ApzFileOperationPlugin
        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        video.execute(lObj)

    }*/
    /*@Test
    fun when_fileBrowser_category_video_success(){
        val video = ApzFileOperationPlugin.createPlugin(
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
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        lObj.put("fileCategory", "VIDEO")
        video.execute(lObj)

    }*/


    //for fileCategory : PHOTO
    /*@Test
    fun when_fileBrowser_category_photo_error(){
        val photo = ApzFileOperationPlugin.createPlugin(
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
            }) as ApzFileOperationPlugin
        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        photo.execute(lObj)

    }*/
    /*@Test
    fun when_fileBrowser_category_photo_success(){
        val photo = ApzFileOperationPlugin.createPlugin(
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
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        lObj.put("fileCategory", "PHOTO")
        photo.execute(lObj)

    }*/


    //for fileCategory : DEFAULT
    /*@Test
    fun when_fileBrowser_category_default_error(){
        val default = ApzFileOperationPlugin.createPlugin(
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
            }) as ApzFileOperationPlugin
        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        default.execute(lObj)

    }*/
    /*@Test
    fun when_fileBrowser_category_default_success(){
        val default = ApzFileOperationPlugin.createPlugin(
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
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        lObj.put("fileCategory", "DEFAULT")
        default.execute(lObj)

    }*/



    //for fileCategory : EXTERNAL
    /*@Test
    fun when_fileBrowser_category_external_error(){
        val external = ApzFileOperationPlugin.createPlugin(
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
            }) as ApzFileOperationPlugin
        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        external.execute(lObj)

    }*/
    /*@Test
    fun when_fileBrowser_category_external_success(){
        val external = ApzFileOperationPlugin.createPlugin(
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
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_BRWS_FILE")
        lObj.put("action", "BROWSER")
        lObj.put("fileCategory", "EXTERNAL")
        external.execute(lObj)

    }*/


    //For GETFILESIZE:

//    @Test
//    fun when_GetFileSize_error(){
//        val getFileSize = ApzFileOperationPlugin.createPlugin(
//            AppzillonMainScreen.webView!!,
//            AppzillonMainScreen.activity, object : IapzPluginUtil {
//                override fun sendError(
//                    callbackId: String?,
//                    errorCode: String?,
//                    aResult: JSONObject?,
//                    activity: Activity,
//                    webView: WebView,
//                    isInUIThread: Boolean
//                ) {
//                    Assert.assertEquals("APZ-CNT-077", errorCode)
//                }
//            }) as ApzFileOperationPlugin
//
////        val lObj = JSONObject()
////        lObj.put("id", "FILEOPERATION_CALLBACK")
////        lObj.put("callBack", "executeCallback")
////        lObj.put("command", "PLGN_FILE_SIZE")
////        lObj.put("action", "GETFILESIZE")
////        lObj.put("fileCategory", "AUDIO")
////        lObj.put("result", "filePath")
//
//
//        Whitebox.invokeMethod<Any>(getFileSize,"proceedFileOperation", null)
//    }
}

