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
class FileOperationTest {

    private val TAG = "FileOperationTest"
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


    //for File open:
    @Test
    fun when__filepath__without_param_then_error() {
        val openFIle = FileOperation(
            activity,
            webView, object : IapzPluginUtil {
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
        lObj.put("command", "PLGN_OPN_FILE")
        openFIle.openFile(lObj)
    }

    @Test
    fun when_filepath__with_param_error() {
        val openFIle = FileOperation(activity,
            webView, object : IapzPluginUtil {
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
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_OPN_FILE")
        lObj.put("action", "OPENFILE")
        lObj.put(
            "filePath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx"
        )
        openFIle.openFile(lObj)

    }


    @Test
    fun when_directory_isEmpty_then_error() {
        val directory = FileOperation(
            activity,
            webView, object : IapzPluginUtil {
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
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_OPN_FILE")
        lObj.put("action", "OPENFILE")
        lObj.put("filePath", "")
        directory.openFile(lObj)

    }

    @Test
    fun when_directory_isPresent_then_error() {
        val directory = FileOperation(activity,
            webView, object : IapzPluginUtil {
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
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_OPN_FILE")
        lObj.put("action", "OPENFILE")
        lObj.put(
            "filePath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx"
        )
        directory.openFile(lObj)

    }


    //for file create:
    @Test
    fun when__createFile__without_param_then_error() {
        val createFile = FileOperation(
            activity,
            webView, object : IapzPluginUtil {
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
        lObj.put("command", "PLGN_CRT_FILE")
        createFile.createFile(lObj)
    }

    @Test
    fun when_createFile__with_param_then_success() {
        val createFile = FileOperation(
            activity,
            webView, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                }
            })
        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_CRT_FILE")
        lObj.put("action", "FILECREATE")

        lObj.put("fileContent", "xyz")
        lObj.put("fileName", "Appzillon_3.1.1_UserManual.docx")
        lObj.put(
            "filePath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx"
        )
        createFile.createFile(lObj)

    }

    @Test
    fun when_words_and_FileName_is_empty__then_error() {
        val fileNameIsEmpty = FileOperation(
            activity,
            webView, object : IapzPluginUtil {
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
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("command", "PLGN_CRT_FILE")
        lObj.put("action", "FILECREATE")
        lObj.put(
            "filePath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx"
        )
        lObj.put("filecontent", "")
        lObj.put("filename", "")
        fileNameIsEmpty.createFile(lObj)
    }


    //for delete file:
    @Test
    fun when_deleteFile_without_param_then_error() {
        val deleteFile = FileOperation(
            activity,
            webView, object : IapzPluginUtil {
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
        lObj.put("command", "PLGN_DEL_FILE")
        deleteFile.deleteFile(lObj)

    }

    @Test
    fun when_deleteFile_with_param_then_success() {
        val deleteFile = FileOperation(
            activity,
            webView, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                }
            })
        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_DEL_FILE")
        lObj.put("action", "FILEDELETE")
        lObj.put(
            "filePath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx"
        )
        deleteFile.deleteFile(lObj)

    }

    @Test
    fun when_deleteFile_then_error() {
        val deleteFile = FileOperation(
            activity,
            webView, object : IapzPluginUtil {
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
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_DEL_FILE")
        lObj.put("action", "FILEDELETE")
        lObj.put(
            "filePath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx"
        )
        deleteFile.deleteFile(lObj)
    }


    //for file content :
    @Test
    fun when_getfileContent_without_param_then_error() {
        val getfileContent = FileOperation(
            activity,
            webView, object : IapzPluginUtil {
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
        lObj.put("command", "PLGN_FILE_CONTENT")
        getfileContent.getFileContent(lObj)

    }

    @Test
    fun when_getfileContent_with_param_then_error() {
        val getfileContent = FileOperation(
            activity,
            webView, object : IapzPluginUtil {
                override fun sendSuccess(
                    callbackId: String?,
                    aResult: JSONObject?,
                    isKeepAlive: Boolean,
                    activity: Activity,
                    webView: WebView,
                    isInUIThread: Boolean
                ) {
                }
            })
        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_FILE_CONTENT")
        lObj.put("action", "FILECONTENT")
        lObj.put(
            "filePath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx"
        )
        getfileContent.getFileContent(lObj)

    }

    @Test
    fun when_readFromFile_then_error() {
        val readFromFile = FileOperation(
            activity,
            webView, object : IapzPluginUtil {
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
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_FILE_CONTENT")
        lObj.put("action", "FILECONTENT")
        lObj.put(
            "filePath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx"
        )
//        val obj_res=JSONObject()
//        obj_res.put("content", "content")
//        lObj.put("result", obj_res)
        readFromFile.getFileContent(lObj)
    }


    //    //for getting FileSize:
    @Test
    fun when_getFileSize_without_param_then_error() {
        val filesize = ApzFileOperationPlugin.createPlugin(
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
        lObj.put("command", "PLGN_FILE_SIZE")
        filesize?.execute(lObj)

    }

    @Test
    fun when_getFileSize_with_param_then_success() {

        val deleteFile = ApzFileOperationPlugin.createPlugin(
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
                    Assert.assertEquals("fileSize", aResult)
                }
            }) as ApzFileOperationPlugin
        val lObj = JSONObject()
        lObj.put("id", "FILEOPERATION_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_FILE_SIZE")
        lObj.put("action", "GETFILESIZE")
        lObj.put(
            "filePath",
            "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx"
        )
        deleteFile.execute(lObj)

    }
}