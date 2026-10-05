package com.iexceed.plugins.camera

import android.app.Activity
import android.content.Intent
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
class ApzCameraPluginTest {

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
    fun testPluginNotSupportedStatus() {
        assert(ApzCameraPlugin.IsCamera())
    }

    @Test
    fun whenExecute_WithMissingParams_thenError() {
        val cameraPlugin = ApzCameraPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-211", errorCode)
                }
            }) as ApzCameraPlugin
        val lObj = JSONObject()
        cameraPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_withParams_sourceTypePhoto_thenSuccess() {
        val cameraPlugin = ApzCameraPlugin.createPlugin(
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
                    Assert.assertNotNull(aResult?.get("filePath"))
                }
            }) as ApzCameraPlugin
        val lObj = JSONObject()
        lObj.put("id", "CAMERA_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_OPN_CAMERA")
        lObj.put("sourceType", "Photo")
        cameraPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_withParams_sourceTypeEmpty_encodingTypeJpg_thenSuccess() {
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("adb shell pm grant ${androidx.test.InstrumentationRegistry.getTargetContext().packageName} android.permission.CAMERA")

        val cameraPlugin = ApzCameraPlugin.createPlugin(
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
                    Assert.assertNull(aResult?.get("filePath"))
                }
            }) as ApzCameraPlugin
        val lObj = JSONObject()
        lObj.put("id", "CAMERA_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_OPN_CAMERA")
        lObj.put("sourceType", "")
        lObj.put("frontCamera", "Y")
        lObj.put("encodingType", "JPG")
        lObj.put("crop","Y")
        lObj.put("targetWidth", "200")
        lObj.put("targetHeight", "200")
        cameraPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_withParams_sourceTypeEmpty_encodingTypePng_thenSuccess() {
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("adb shell pm grant ${androidx.test.InstrumentationRegistry.getTargetContext().packageName} android.permission.CAMERA")

        val cameraPlugin = ApzCameraPlugin.createPlugin(
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
                    Assert.assertNull(aResult?.get("filePath"))
                }
            }) as ApzCameraPlugin
        val lObj = JSONObject()
        lObj.put("id", "CAMERA_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_OPN_CAMERA")
        lObj.put("sourceType", "")
        lObj.put("frontCamera", "Y")
        lObj.put("encodingType", "PNG")
        lObj.put("crop","Y")
        lObj.put("targetWidth", "200")
        lObj.put("targetHeight", "200")
        cameraPlugin.execute(lObj)
    }

    @Test
    fun whenExecute_withParams_sourceTypeEmpty_encodingTypeOther_thenSuccess() {
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("adb shell pm grant ${androidx.test.InstrumentationRegistry.getTargetContext().packageName} android.permission.CAMERA")

        val cameraPlugin = ApzCameraPlugin.createPlugin(
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
                    Assert.assertNull(aResult?.get("filePath"))
                }
            }) as ApzCameraPlugin
        val lObj = JSONObject()
        lObj.put("id", "CAMERA_CLBACK")
        lObj.put("callBack", "executeCallback")
        lObj.put("command", "PLGN_OPN_CAMERA")
        lObj.put("sourceType", "")
        lObj.put("frontCamera", "Y")
        lObj.put("encodingType", "MVA")
        lObj.put("crop","Y")
        lObj.put("targetWidth", "200")
        lObj.put("targetHeight", "200")
        cameraPlugin.execute(lObj)
    }

    @Test
    fun testSendPermissionDenied() {
        val cameraPlugin = ApzCameraPlugin.createPlugin(
            webView,
            activity, object : IapzPluginUtil {
                override fun sendPermissionDenied(
                    pluginName: String, callbackId: String?,
                    activity: Activity?, webView: WebView?
                ){
                    Assert.assertEquals("Camera or Storage ",pluginName)
                }
            }) as ApzCameraPlugin
        Whitebox.invokeMethod<Any>(cameraPlugin, "PermissionDeniedCallback", null)
    }

    @Test
    fun test_ExternalActivityResultHandlerTask_actionBase64_thenSuccess() {
        val cameraPlugin = ApzCameraPlugin.createPlugin(
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
                    Assert.assertEquals("",aResult?.get("encodedImage"))
                }
            }) as ApzCameraPlugin

        val lObj  = JSONObject()
        lObj.put("action", "base64")
        cameraPlugin.mJsonObj = lObj
        val data = Intent(activity, NativeCamera::class.java)
        cameraPlugin.ExternalActivityResultHandlerTask().handleActivityResult(-1, data)
    }

    @Test
    fun test_ExternalActivityResultHandlerTask_actionSave_thenSuccess() {
        val cameraPlugin = ApzCameraPlugin.createPlugin(
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
                    Assert.assertEquals("abc/photo.jpg",aResult?.get("path"))
                }
            }) as ApzCameraPlugin

        val lObj  = JSONObject()
        lObj.put("action", "save")
        cameraPlugin.mJsonObj = lObj
        val data = Intent(activity, NativeCamera::class.java)
        data.putExtra("url","abc/photo.jpg")
        cameraPlugin.ExternalActivityResultHandlerTask().handleActivityResult(-1, data)
    }

    @Test
    fun test_ExternalActivityResultHandlerTask_actionBase64Save_thenSuccess() {
        val cameraPlugin = ApzCameraPlugin.createPlugin(
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
                    Assert.assertEquals("abc/photo.jpg",aResult?.get("path"))
                }
            }) as ApzCameraPlugin

        val lObj  = JSONObject()
        lObj.put("action", "base64_Save")
        cameraPlugin.mJsonObj = lObj
        val data = Intent(activity, NativeCamera::class.java)
        data.putExtra("url","abc/photo.jpg")
        cameraPlugin.ExternalActivityResultHandlerTask().handleActivityResult(-1, data)
    }

    @Test
    fun test_ExternalActivityResultHandlerTask_actionSrcBase64_thenSuccess() {
        val cameraPlugin = ApzCameraPlugin.createPlugin(
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
                    Assert.assertEquals("Success",aResult?.get("successMessage"))
                }
            }) as ApzCameraPlugin

        val lObj  = JSONObject()
        lObj.put("action", "srcBase64")
        cameraPlugin.mJsonObj = lObj
        val data = Intent(activity, NativeCamera::class.java)
        data.putExtra("url","abc/photo.jpg")
        data.putExtra("idName","executeCallback")
        cameraPlugin.ExternalActivityResultHandlerTask().handleActivityResult(-1, data)
    }

    @Test
    fun test_ExternalActivityResultHandlerTask_actionSrcUrl_thenSuccess() {
        val cameraPlugin = ApzCameraPlugin.createPlugin(
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
                    Assert.assertEquals("Success",aResult?.get("successMessage"))
                }
            }) as ApzCameraPlugin

        val lObj  = JSONObject()
        lObj.put("action", "srcUrl")
        cameraPlugin.mJsonObj = lObj
        val data = Intent(activity, NativeCamera::class.java)
        data.putExtra("url","abc/photo.jpg")
        data.putExtra("idName","executeCallback")
        cameraPlugin.ExternalActivityResultHandlerTask().handleActivityResult(-1, data)
    }

    @Test
    fun test_ExternalActivityResultHandlerTask_actionSrcUrl_Result_NotOk_thenError() {
        val cameraPlugin = ApzCameraPlugin.createPlugin(
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
                    Assert.assertEquals("APZ-CNT-211", errorCode)
                }
            }) as ApzCameraPlugin
        val lObj  = JSONObject()
        lObj.put("action", "srcUrl")
        cameraPlugin.mJsonObj = lObj
        val data = Intent(activity, NativeCamera::class.java)
        data.putExtra("url","abc/photo.jpg")
        data.putExtra("idName","executeCallback")
        cameraPlugin.ExternalActivityResultHandlerTask().handleActivityResult(0, data)
    }

    @Test
    fun test_compressImage_uncompressedTrue() {
        val arr = byteArrayOfInts(0xA1, 0x2E, 0x38, 0xD4, 0x89, 0xC3)
        val triple = Triple(200,200, true)
        val bitmap = CameraUtils.compressImage(activity, arr, "JPG", true,
            "/Internal storage/DCIM/Screenshots",triple)
        Assert.assertNull(bitmap)
    }

    @Test
    fun test_compressImage_uncompressedFalse() {
        val arr = byteArrayOfInts(0xA1, 0x2E, 0x38, 0xD4, 0x89, 0xC3)
        val triple = Triple(200,200, true)
        val bitmap = CameraUtils.compressImage(activity, arr, "JPG", false,
            "/Internal storage/DCIM/Screenshots",triple)
        Assert.assertNull(bitmap)
    }

    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }
}