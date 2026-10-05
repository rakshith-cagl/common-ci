package com.iexceed.plugins.gpslocation

import android.app.Activity
import android.app.Service
import android.location.Location
import android.location.LocationManager
import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.common.ApzLocationManager
import com.iexceed.plugins.IapzPluginUtil
import org.json.JSONObject
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.powermock.reflect.Whitebox

@RunWith(AndroidJUnit4::class)
class GpsLocatorTest {

    lateinit var activityScenario: ActivityScenario<AppzillonMainScreen>

    @get:Rule
    val locationPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
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
    fun tearDown() {

        activityScenario.close()
    }

    @Test
    fun whenExecute_enableProvider_thenError() {

        val gpsLocator = webView.let {
            GpsLocator(activity,
                it, object : IapzPluginUtil {

                    override fun sendError(
                        callbackId: String?,
                        errorCode: String?,
                        aResult: JSONObject?,
                        activity: Activity,
                        webView: WebView,
                        isInUIThread: Boolean
                    ) {

                        Assert.assertEquals("APZ-CNT-057", errorCode)
                    }
                })
        }

        Whitebox.invokeMethod<Any>(gpsLocator, "enableProvider", null)
    }

    @Test
    fun whenExecute_callSuccessCallback_thenSuccess() {

        val gpsLocator = webView.let {
            GpsLocator(activity,
                it, object : IapzPluginUtil {

                    override fun sendSuccess(
                        callbackId: String?,
                        aResult: JSONObject?,
                        isKeepAlive: Boolean,
                        activity: Activity,
                        webView: WebView,
                        isInUIThread: Boolean
                    ) {

                        Assert.assertEquals("12.9716", aResult?.optString("latitude"))
                    }
                })
        }

        val jsonObject = JSONObject()
        jsonObject.put("latitude", "12.9716")
        Whitebox.invokeMethod<Any>(gpsLocator, "callSuccessCallback", jsonObject)
    }

    @Test
    fun whenExecute_timerMethod_thenSuccess() {

        val gpsLocator = webView.let {
            GpsLocator(activity,
                it, object : IapzPluginUtil {

                    override fun sendSuccess(
                        callbackId: String?,
                        aResult: JSONObject?,
                        isKeepAlive: Boolean,
                        activity: Activity,
                        webView: WebView,
                        isInUIThread: Boolean
                    ) {

                        Assert.assertEquals("12.9716", aResult?.optString("latitude"))
                    }
                })
        }

        val jsonObject = JSONObject()
        jsonObject.put("latitude", "12.9716")
        Whitebox.invokeMethod<Any>(gpsLocator, "timerMethod", jsonObject)

    }

    @Test
    fun whenExecute_pause_thenSuccess() {

        val activity = activity
        val gpsLocator = webView.let {
            GpsLocator(activity,
                it, object : IapzPluginUtil {
                })
        }

        GpsLocator.isRunning = true
        val locationManager = activity.getSystemService(Service.LOCATION_SERVICE) as LocationManager
        gpsLocator.pause(locationManager)

        Assert.assertFalse(GpsLocator.isRunning)
    }

    @Test
    fun whenExecute_stop_thenSuccess() {

        val gpsLocator = GpsLocator(activity,
            webView, object : IapzPluginUtil {
            })

        GpsLocator.isRunning = true
        gpsLocator.stop()

        Assert.assertFalse(GpsLocator.isRunning)
    }

    @Test
    fun whenExecute_getIsGPSEnabled_thenSuccess() {

        val activity = activity
        val gpsLocator = GpsLocator(activity,
            webView, object : IapzPluginUtil {
            })

        val locationManager = activity.getSystemService(Service.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled =
            Whitebox.invokeMethod<Any>(gpsLocator, "getIsGPSEnabled", locationManager) as Boolean
        Assert.assertTrue(isGPSEnabled)
    }

    @Test
    fun whenExecute_getIsGPSEnabled_thenError() {

        val activity = activity
        val gpsLocator = GpsLocator(activity,
            webView, object : IapzPluginUtil {
            })

        val locationManager: LocationManager? = null
        val isGPSEnabled =
            Whitebox.invokeMethod<Any>(gpsLocator, "getIsGPSEnabled", locationManager) as Boolean
        Assert.assertFalse(isGPSEnabled)
    }

    @Test
    fun whenExecute_getIsNetworkEnabled_thenSuccess() {

        val activity = activity
        val gpsLocator = GpsLocator(activity,
            webView, object : IapzPluginUtil {
            })

        val locationManager = activity.getSystemService(Service.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = Whitebox.invokeMethod<Any>(
            gpsLocator,
            "getIsNetworkEnabled",
            locationManager
        ) as Boolean
        Assert.assertTrue(isGPSEnabled)
    }

    @Test
    fun whenExecute_getIsNetworkEnabled_thenError() {

        val activity = activity
        val gpsLocator = GpsLocator(activity,
            webView, object : IapzPluginUtil {
            })

        val locationManager: LocationManager? = null
        val isGPSEnabled = Whitebox.invokeMethod<Any>(
            gpsLocator,
            "getIsNetworkEnabled",
            locationManager
        ) as Boolean
        Assert.assertFalse(isGPSEnabled)
    }

    @Test
    fun whenExecute_getCoordinates_withNoGPSNetworkEnabled_thenError() {

        val activity = activity

        activity.runOnUiThread {

            val gpsLocator = webView.let {
                GpsLocator(activity,
                    it, object : IapzPluginUtil {

                        override fun sendSuccess(
                            callbackId: String?,
                            aResult: JSONObject?,
                            isKeepAlive: Boolean,
                            activity: Activity,
                            webView: WebView,
                            isInUIThread: Boolean
                        ) {

                            aResult?.has("latitude")?.let { it1 -> Assert.assertTrue(it1) }
                        }
                    })
            }

            val jsonObject = JSONObject()
            jsonObject.put("id", "GPS")

            gpsLocator.getCoordinates(jsonObject)
        }
    }

    @Test
    fun whenExecute_getCoordinates_periodicityAsIntervalBased_thenSuccess() {

        val activity = activity

        activity.runOnUiThread {

            val gpsLocator = webView.let {
                GpsLocator(activity,
                    it, object : IapzPluginUtil {

                        override fun sendSuccess(
                            callbackId: String?,
                            aResult: JSONObject?,
                            isKeepAlive: Boolean,
                            activity: Activity,
                            webView: WebView,
                            isInUIThread: Boolean
                        ) {

                            aResult?.has("latitude")?.let { it1 -> Assert.assertTrue(it1) }
                        }
                    })
            }

            val jsonObject = JSONObject()
            jsonObject.put("id", "GPS")
            jsonObject.put("periodicity", "intervalBased")
            jsonObject.put("distanceInterval", "10.0")
            jsonObject.put("timeInterval", "1000")

            gpsLocator.getCoordinates(jsonObject)
        }
    }

    @Test
    fun whenExecute_getCoordinates_periodicityAsTimed_thenSuccess() {

        val activity = activity

        activity.runOnUiThread {

            val gpsLocator = webView.let {
                GpsLocator(activity,
                    it, object : IapzPluginUtil {

                        override fun sendSuccess(
                            callbackId: String?,
                            aResult: JSONObject?,
                            isKeepAlive: Boolean,
                            activity: Activity,
                            webView: WebView,
                            isInUIThread: Boolean
                        ) {

                            aResult?.has("latitude")?.let { it1 -> Assert.assertTrue(it1) }
                        }
                    })
            }

            val jsonObject = JSONObject()
            jsonObject.put("id", "GPS")
            jsonObject.put("periodicity", "timed")
            jsonObject.put("distanceInterval", "10.0")
            jsonObject.put("timeInterval", "1000")

            gpsLocator.getCoordinates(jsonObject)
        }
    }

    @Test
    fun whenExecute_setLocationData_thenSuccess() {

        val gpsLocator = GpsLocator(activity,
            webView, object : IapzPluginUtil {
            })

        val jsonObject = JSONObject()
        jsonObject.put("id", "GPS")
        val location = Location("")
        location.latitude = 12.972442
        location.longitude = 77.580643
        location.altitude = 920.0
        location.speed = 25.0F
        Whitebox.invokeMethod<Any>(gpsLocator, "setLocationData", jsonObject, location)
        Assert.assertNotNull(jsonObject)
    }

    @Test
    fun whenExecute_getLocationFromNetwork_thenSuccess() {

        val activity = activity

        activity.runOnUiThread {

            val gpsLocator = GpsLocator(activity,
                webView, object : IapzPluginUtil {
                })

            val timeInterval = 0L
            val distanceInterval = 0F
            val apzLocationManager = ApzLocationManager(activity)
            val locationManager =
                activity.getSystemService(Service.LOCATION_SERVICE) as LocationManager
            val location = Whitebox.invokeMethod<Any>(
                gpsLocator,
                "getLocationFromNetwork",
                locationManager,
                timeInterval,
                distanceInterval,
                apzLocationManager
            )
            
            Assert.assertNotNull(location)
        }
    }

    @Test
    fun whenExecute_getLocationFromNetwork_thenError() {

        val activity = activity

        activity.runOnUiThread {

            val gpsLocator = webView.let {
                GpsLocator(activity,
                    it, object : IapzPluginUtil {

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
            }

            val timeInterval = 0L
            val distanceInterval = 0F
            val apzLocationManager = ApzLocationManager(activity)
            val locationManager: LocationManager? = null
            Whitebox.invokeMethod<Any>(
                gpsLocator,
                "getLocationFromNetwork",
                locationManager,
                timeInterval,
                distanceInterval,
                apzLocationManager
            )
        }
    }

    @Test
    fun whenExecute_getLocationFromGPS_thenSuccess() {

        val activity = activity

        activity.runOnUiThread {

            val gpsLocator = GpsLocator(activity,
                webView, object : IapzPluginUtil {
                })

            val timeInterval = 0L
            val distanceInterval = 0F
            val apzLocationManager = ApzLocationManager(activity)
            val locationManager =
                activity.getSystemService(Service.LOCATION_SERVICE) as LocationManager
            val location = Whitebox.invokeMethod<Any>(
                gpsLocator,
                "getLocationFromGPS",
                locationManager,
                timeInterval,
                distanceInterval,
                apzLocationManager
            )

            Assert.assertNotNull(location)
        }
    }

    @Test
    fun whenExecute_getLocationFromGPS_thenError() {

        val activity = activity

        activity.runOnUiThread {

            val gpsLocator = webView.let {
                GpsLocator(activity,
                    it, object : IapzPluginUtil {

                        override fun sendError(
                            callbackId: String?,
                            errorCode: String?,
                            aResult: JSONObject?,
                            activity: Activity,
                            webView: WebView,
                            isInUIThread: Boolean
                        ) {

                            Assert.assertEquals("APZ-CNT-059", errorCode)
                        }
                    })
            }

            val timeInterval = 0L
            val distanceInterval = 0F
            val apzLocationManager = ApzLocationManager(activity)
            val locationManager: LocationManager? = null
            Whitebox.invokeMethod<Any>(
                gpsLocator,
                "getLocationFromGPS",
                locationManager,
                timeInterval,
                distanceInterval,
                apzLocationManager
            )
        }
    }

    @Test
    fun whenExecute_getLocationManager_thenSuccess() {

        val activity = activity
        activity.runOnUiThread {

            val gpsLocator = GpsLocator(activity,
                webView, object : IapzPluginUtil {
                })

            val locationManager =
                Whitebox.invokeMethod<Any>(gpsLocator, "getLocationManager", activity)
            Assert.assertNotNull(locationManager)
        }
    }
}