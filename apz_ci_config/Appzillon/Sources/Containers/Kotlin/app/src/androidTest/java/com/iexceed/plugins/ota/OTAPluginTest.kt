package com.iexceed.plugins.ota

import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OTAPluginTest {

    lateinit var activityScenario: ActivityScenario<AppzillonMainScreen>
    lateinit var activity: ApzActivity<*>
    lateinit var webView: WebView

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

    /*@Test(expected = MockitoException::class)
    fun test_getDataForOTA() {
            val otaPlugin = Mockito.mock(OTAPlugin(
                this.activity)::class.java)
            Mockito.doThrow(Exception())
                .`when`(otaPlugin)
                .getDataForOTA("com.iexceed.container","1.0")
            Assert.assertNotNull(otaPlugin.presentApp)
    }

    @Test
    fun test_actionOnOTAFiles_withMissingJSONObject() {
        val otaPlugin = OTAPlugin(
            this.activity)
        val lObj = JSONObject()
        Whitebox.invokeMethod<Any>(otaPlugin, "actionOnOTAFiles", lObj,"1.0")
        Assert.assertNotNull(otaPlugin.updatedAppversion)
    }

    @Test(expected = NullPointerException::class)
    fun test_actionOnOTAFiles() {
            val otaPlugin = Mockito.mock(OTAPlugin(
                this.activity)::class.java)
            val lObj = JSONObject()
            val lArr = JSONArray()
            OTAPlugin.downloadArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("filepath","storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx")
            jsonObject.put("action", "OPEN")
            jsonObject.put("filename", "Appzillon_3.1.1_UserManual.docx")
            jsonObject.put("appVersion", "1.0")
            jsonObject.put("os", "ANDROID")
            OTAPlugin.downloadArray!!.put(jsonObject)
            lObj.put("id", "OTAPLUGIN_CLBACK")
            lObj.put("appId", "ota")
            lObj.put("ota",lArr)
            Mockito.doThrow(Exception()).`when`(Whitebox.invokeMethod<Any>(otaPlugin, "actionOnOTAFiles", lObj,"1.0"))
            Assert.assertNotNull(otaPlugin.updatedAppversion)
    }

    @Test
    fun test_deleteOtaFiles() {
            val otaPlugin = OTAPlugin(
                this.activity)
            val lObj = JSONObject()
            val lArr = JSONArray()
            OTAPlugin.downloadArray = JSONArray()
            val jsonObject = JSONObject()
            jsonObject.put("filepath","storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/apps/IEXC/docs/word/Appzillon_3.1.1_UserManual.docx")
            jsonObject.put("action", "OPEN")
            jsonObject.put("filename", "Appzillon_3.1.1_UserManual.docx")
            jsonObject.put("appVersion", "1.0")
            jsonObject.put("os", "ANDROID")
            OTAPlugin.downloadArray!!.put(jsonObject)
            lObj.put("id", "OTAPLUGIN_CLBACK")
            lObj.put("appId", "ota")
            lObj.put("ota",lArr)
            otaPlugin.file = File(jsonObject.getString("filepath"))
            Whitebox.invokeMethod<Any>(otaPlugin, "deleteOtaFiles", OTAPlugin.downloadArray)
            Assert.assertNotNull(otaPlugin.file)
    }

    @Test
    fun test_getDir() {
        val otaPlugin = OTAPlugin(
            this.activity)
        otaPlugin.sdDir = File(AppzillonMainScreen.SANDBOX_LOC + File.separator + "appzillonOTATemp")
        val file = Whitebox.invokeMethod<Any>(otaPlugin, "getDir", "appzillonOTATemp")
        Assert.assertEquals(File("/storage/emulated/0/Android/data/com.iexceed.container/files/IEXC/appzillonOTATemp"),otaPlugin.sdDir)
        Assert.assertNotNull(file)
    }

    @Test
    fun test_deleteOtaTempFolder() {
        val fileDelete = Whitebox.invokeMethod<Boolean>(OTAPlugin.Companion, "deleteOtaTempFolder", File("appzillonOTATemp"))
        Assert.assertFalse(fileDelete)
    }*/
}