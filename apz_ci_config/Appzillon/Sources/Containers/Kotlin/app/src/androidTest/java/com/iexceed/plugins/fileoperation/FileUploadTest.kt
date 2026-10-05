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
import org.powermock.reflect.Whitebox


@RunWith(AndroidJUnit4::class)
class FileUploadTest {

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
    fun when__uploadFile__without_param_then_error() {
        val uploadFile = FileUpload(
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
            })

        val lObj = JSONObject()
        lObj.put("command", "PLGN_UPLD_FILE")
        uploadFile.uploadFile(lObj)

    }

    /*@Test
    fun when_uploadFile__with_param_whenUploadSuccess_then_error() {

        val uploadFile = FileUpload(
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
                    Assert.assertEquals("APZ-CNT-002", errorCode)
                }
            })
        val lObj = JSONObject()
        val appzillonHeader = JSONObject()
        lObj.put("id", "FILEUPLOAD_CALLBACK")
        lObj.put("destination", "")
        lObj.put("sessionReq", "y")
        lObj.put("fieldID", "/sdcard/Download/CurrentAccount_History_25012022182654.pdf")
        lObj.put("appzillonHeader", appzillonHeader)


        uploadFile.uploadFile(lObj)

    }*/


//    @Test
//    fun when_doFileUpload__without_param_error() {
//        val doFileUpload = FileUpload(
//            webView,
//            activity, object : IapzPluginUtil {
//                override fun sendError(
//                    callbackId: String?,
//                    errorCode: String?,
//                    aResult: JSONObject?,
//                    activity: Activity,
//                    webView: WebView,
//                    isInUIThread: Boolean
//                ) {
//                    Assert.assertEquals("APZ-CNT-002", errorCode)
//                }
//            })
//
//        val lObj = JSONObject()
//        lObj.put("id", "FILEUPLOAD_CALLBACK")
//        lObj.put("callBack", "executeCallback")
//        lObj.put("command", "PLGN_UPLD_FILE")
//        lObj.put("action", "FILEUPLOAD")
//
//        lObj.put("destination", "destination")
//        lObj.put("sessionReq", "sessionReq")
//        lObj.put("fieldID", "fieldID")
//        lObj.put("fileOverride", "fileOverride")
//
//
//        Whitebox.invokeMethod<Any>(doFileUpload, "doFileUpload", null)
//
//
//    }

    /*@Test
    fun whenExecute_doFileUpload_thenSuccess(){

        val fileUpload = FileUpload(webView, activity, object : IapzPluginUtil{
        })

        val jsonObject = JSONObject()
        jsonObject.put("id", "FILE_UPLOAD")

        val file = File("/storage/emulated/0/Download/000107506.jpg")

        fileUpload.doFileUpload(jsonObject, file)
        doNothing().`when`(fileUploadMock).doFileUpload(jsonObject, file)
        fileUploadMock.doFileUpload(jsonObject, file)
        verify(fileUploadMock, times(1)).doFileUpload(jsonObject, file)
    }*/

    @Test
    fun whenExecute_handleDoFileUploadResponse_uploadStatusAsTrue_sessionAsY_thenSuccess(){

        val fileUpload = FileUpload(webView, activity, object : IapzPluginUtil{

            override fun sendSuccess(
                callbackId: String?,
                aResult: JSONObject?,
                isKeepAlive: Boolean,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

                Assert.assertEquals("Upload Success", aResult?.optString("successMessage"))
            }
        })

        val isSession = "Y"
        val responseJson = JSONObject()
        val headerJson = JSONObject()
        val bodyJson = JSONObject()
        val uploadResJson = JSONObject()
        uploadResJson.put("status", "success")
        bodyJson.put("appzillonUploadFileResponse", uploadResJson)
        headerJson.put("status", true)
        responseJson.put("appzillonHeader", headerJson)
        responseJson.put("appzillonBody", bodyJson)

        val status = "status"
        Whitebox.invokeMethod<Any>(fileUpload, "handleDoFileUploadResponse", responseJson.toString(), isSession, status)
    }

    @Test
    fun whenExecute_handleDoFileUploadResponse_uploadStatusAsFailed_sessionAsY_thenSuccess(){

        val fileUpload = FileUpload(webView, activity, object : IapzPluginUtil{

            override fun sendSuccess(
                callbackId: String?,
                aResult: JSONObject?,
                isKeepAlive: Boolean,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

                Assert.assertEquals("File Already Exists", aResult?.optString("successMessage"))
            }
        })

        val isSession = "Y"
        val responseJson = JSONObject()
        val headerJson = JSONObject()
        val bodyJson = JSONObject()
        val uploadResJson = JSONObject()
        uploadResJson.put("status", "failed")
        bodyJson.put("appzillonUploadFileResponse", uploadResJson)
        headerJson.put("status", true)
        responseJson.put("appzillonHeader", headerJson)
        responseJson.put("appzillonBody", bodyJson)

        val status = "status"
        Whitebox.invokeMethod<Any>(fileUpload, "handleDoFileUploadResponse", responseJson.toString(), isSession, status)
    }

    @Test
    fun whenExecute_handleDoFileUploadResponse_uploadStatusAsFailed_sessionAsN_thenSuccess(){

        val fileUpload = FileUpload(webView, activity, object : IapzPluginUtil{

            override fun sendSuccess(
                callbackId: String?,
                aResult: JSONObject?,
                isKeepAlive: Boolean,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

                Assert.assertEquals("File Already Exists", aResult?.optString("successMessage"))
            }
        })

        val isSession = "N"
        val responseJson = JSONObject()
        val headerJson = JSONObject()
        val bodyJson = JSONObject()
        val uploadResJson = JSONObject()
        uploadResJson.put("status", "failed")
        bodyJson.put("appzillonUploadFileWSResponse", uploadResJson)
        headerJson.put("status", true)
        responseJson.put("appzillonHeader", headerJson)
        responseJson.put("appzillonBody", bodyJson)

        val status = "status"
        Whitebox.invokeMethod<Any>(fileUpload, "handleDoFileUploadResponse", responseJson.toString(), isSession, status)
    }

    @Test
    fun whenExecute_handleDoFileUploadResponse_uploadStatusAsSuccess_sessionAsN_thenSuccess(){

        val fileUpload = FileUpload(webView, activity, object : IapzPluginUtil{

            override fun sendSuccess(
                callbackId: String?,
                aResult: JSONObject?,
                isKeepAlive: Boolean,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

                Assert.assertEquals("Upload Success", aResult?.optString("successMessage"))
            }
        })

        val isSession = "N"
        val responseJson = JSONObject()
        val headerJson = JSONObject()
        val bodyJson = JSONObject()
        val uploadResJson = JSONObject()
        uploadResJson.put("status", "success")
        bodyJson.put("appzillonUploadFileWSResponse", uploadResJson)
        headerJson.put("status", true)
        responseJson.put("appzillonHeader", headerJson)
        responseJson.put("appzillonBody", bodyJson)

        val status = "status"
        Whitebox.invokeMethod<Any>(fileUpload, "handleDoFileUploadResponse", responseJson.toString(), isSession, status)
    }

    @Test
    fun whenExecute_handleDoFileUploadResponse_statusAsFailed_sessionAsY_thenSuccess(){

        val fileUpload = FileUpload(webView, activity, object : IapzPluginUtil{

            override fun sendError(
                callbackId: String?,
                errorCode: String?,
                aResult: JSONObject?,
                activity: Activity,
                webView: WebView,
                isInUIThread: Boolean
            ) {

                Assert.assertEquals("APZ-CNT-007", errorCode)
            }
        })

        val isSession = "Y"
        val responseJson = JSONObject()
        val headerJson = JSONObject()
        val bodyJson = JSONObject()
        val uploadResJson = JSONObject()
        uploadResJson.put("status", "failed")
        bodyJson.put("appzillonUploadFileResponse", uploadResJson)
        headerJson.put("status", false)
        responseJson.put("appzillonHeader", headerJson)
        responseJson.put("appzillonBody", bodyJson)

        val status = "status"
        Whitebox.invokeMethod<Any>(fileUpload, "handleDoFileUploadResponse", responseJson.toString(), isSession, status)
    }

    @Test
    fun when_uploadFile_Status_equal_success_then_success() {

        val uploadFile_success = ApzFileOperationPlugin.createPlugin(
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
                    Assert.assertEquals("FILEUPLOAD_CALLBACK", callbackId)
                }
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEUPLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_UPLD_FILE")
        lObj.put("action", "FILEUPLOAD")
        uploadFile_success.execute(lObj)

    }

    /*@Test
    fun when_uploadFile_status_failed_then_error() {

        val uploadFile_failed = ApzFileOperationPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-007", errorCode)
                }
            }) as ApzFileOperationPlugin

        val lObj = JSONObject()
        lObj.put("id", "FILEUPLOAD_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_UPLD_FILE")
        lObj.put("action", "FILEUPLOAD")
        uploadFile_failed.execute(lObj)

    }*/


}