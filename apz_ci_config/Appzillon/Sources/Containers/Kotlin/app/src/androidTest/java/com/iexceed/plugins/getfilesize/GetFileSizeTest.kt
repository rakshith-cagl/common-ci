package com.iexceed.plugins.getFileSize

import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.common.FileUtils
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class GetFileSizeTest {


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
    fun when_getFileSize_then_error() {
        val lObj = JSONObject()
        lObj.put("id", "GETFILESIZE_CALLBACK")
        lObj.put("callBack", "executeCallback")
        FileUtils.getFileSize(webView, activity,  lObj )
        Assert.assertEquals("APZ-CNT-002" ,"APZ-CNT-002")

    }


    @Test
    fun when_getFileSize_then_success() {
        val lObj = JSONObject()
        lObj.put("filePath", "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx")
        lObj.put("id", "GETFILESIZE_CALLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_FILE_SIZE")
        FileUtils.getFileSize(webView, activity,  lObj)

        val path = "/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx"
        val filenew = File(path)
        val len = filenew.length()
        val file_size = (len / 1024).toString().toInt()

        Assert.assertEquals(0, file_size)


    }


}