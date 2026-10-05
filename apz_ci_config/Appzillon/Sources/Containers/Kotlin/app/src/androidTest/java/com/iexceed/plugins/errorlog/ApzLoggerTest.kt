package com.iexceed.plugins.errorlog

import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import com.iexceed.common.StringUtils
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.utils.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.*
import org.junit.rules.RuleChain

class ApzLoggerTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fun_e() = this.testCoroutineRule.runBlockingTest {
        ApzPlugin.debugLevel = 1
        StringUtils.appInfo[StringUtils.IS_SENDLOG] = "Y"
        ApzLogger.Companion.e("ApzLogger","error")
        Assert.assertEquals("Y", StringUtils.getString(StringUtils.IS_SENDLOG))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fun_i() = this.testCoroutineRule.runBlockingTest {
        ApzPlugin.debugLevel = 3
        StringUtils.appInfo[StringUtils.IS_SENDLOG] = "Y"
        ApzLogger.Companion.i("ApzLogger","info")
        Assert.assertEquals("Y", StringUtils.getString(StringUtils.IS_SENDLOG))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fun_d() = this.testCoroutineRule.runBlockingTest {
        ApzPlugin.debugLevel = 4
        StringUtils.appInfo[StringUtils.IS_SENDLOG] = "Y"
        ApzLogger.Companion.d("ApzLogger","debug")
        Assert.assertEquals("Y", StringUtils.getString(StringUtils.IS_SENDLOG))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fun_w() = this.testCoroutineRule.runBlockingTest {
        ApzPlugin.debugLevel = 2
        StringUtils.appInfo[StringUtils.IS_SENDLOG] = "Y"
        ApzLogger.Companion.w("ApzLogger","warn")
        Assert.assertEquals("Y", StringUtils.getString(StringUtils.IS_SENDLOG))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fun_v() = this.testCoroutineRule.runBlockingTest {
        ApzPlugin.debugLevel = 0
        StringUtils.appInfo[StringUtils.IS_SENDLOG] = "Y"
        ApzLogger.Companion.v("ApzLogger","fatal")
        Assert.assertEquals("Y", StringUtils.getString(StringUtils.IS_SENDLOG))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fun_initialTime() = this.testCoroutineRule.runBlockingTest {
        ApzPlugin.debugLevel = 5
        StringUtils.appInfo[StringUtils.IS_SENDLOG] = "Y"
        ApzLogger.Companion.initialTime("ApzLogger","reqtime")
        Assert.assertEquals("Y", StringUtils.getString(StringUtils.IS_SENDLOG))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_fun_endTime() = this.testCoroutineRule.runBlockingTest {
        ApzPlugin.debugLevel = 5
        StringUtils.appInfo[StringUtils.IS_SENDLOG] = "Y"
        ApzLogger.Companion.endTime("ApzLogger","reqtime")
        Assert.assertEquals("Y", StringUtils.getString(StringUtils.IS_SENDLOG))
    }
}