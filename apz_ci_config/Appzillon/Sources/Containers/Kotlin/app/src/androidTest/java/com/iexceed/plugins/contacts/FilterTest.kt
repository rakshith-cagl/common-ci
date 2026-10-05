package com.iexceed.plugins.contacts

import android.webkit.WebView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.iexceed.TestComponentRule
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.common.ApzActivity
import org.json.JSONArray
import org.junit.*
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilterTest {

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
    fun whenExecute_withEmptyData_thenSuccess(){

        activity.runOnUiThread {

            val filter = Filter(activity).filterContacts("",
                "", "", "", "")

            Assert.assertNotNull("Filter result not null", filter)
        }
    }

    @Test
    fun whenExecute_withFirstNameAsCriteria_thenSuccess(){

        activity.runOnUiThread {

            //Providing first name of an existing contact
            val filter = Filter(activity).filterContacts("Test",
                "", "", "", "")

//            Assert.assertNotNull(filter)
        }
    }

    @Test
    fun whenExecute_withLastNameAsCriteria_thenSuccess(){

        activity.runOnUiThread {

            //Providing last name of an existing contact
            val filter = Filter(activity).filterContacts("",
                "rag", "", "", "")

            Assert.assertNull(filter)
        }
    }

    @Test
    fun whenExecute_withPhoneNumAsCriteria_thenSuccess(){

        activity.runOnUiThread {

            //Providing phone number of an existing contact
            val filter = Filter(activity).filterContacts("",
                "", "4545", "", "")

//            Assert.assertNotNull(filter)
        }
    }

    @Test
    fun whenExecute_withPhoneHomeAsCriteria_thenSuccess(){

        activity.runOnUiThread {

            //Providing phone home number of an existing contact
            val filter = Filter(activity).filterContacts("",
                "", "", "464", "")

            Assert.assertNull(filter)
        }
    }

    @Test
    fun whenExecute_withPhoneWorkAsCriteria_thenSuccess(){

        activity.runOnUiThread {

            //Providing phone work number of an existing contact
            val filter = Filter(activity).filterContacts("",
                "", "", "", "878")

            Assert.assertNull(filter)
        }
    }

    @Test
    fun whenExecute_withEmptyCriteria_thenError(){

        activity.runOnUiThread {

            val filter = Filter(activity).filterContacts("",
                "", "", "", "")
            val emptyJSONArray = JSONArray()
            Assert.assertEquals(emptyJSONArray, filter)
        }
    }

    @Test
    fun whenExecute_withNonExistingContactFirstNameAsCriteria_thenError(){

        activity.runOnUiThread {

            val filter = Filter(activity).filterContacts("zara",
                "", "", "", "")
            Assert.assertNull(filter)
        }
    }

    @Test
    fun whenExecute_withNonExistingContactLastNameAsCriteria_thenError(){

        activity.runOnUiThread {

            val filter = Filter(activity).filterContacts("",
                "david", "", "", "")
            Assert.assertNull(filter)
        }
    }

    @Test
    fun whenExecute_withNonExistingContactPhoneMobAsCriteria_thenError(){

        activity.runOnUiThread {

            val filter = Filter(activity).filterContacts("",
                "", "46474657", "", "")
            Assert.assertNull(filter)
        }
    }

    @Test
    fun whenExecute_withNonExistingContactPhoneHomeAsCriteria_thenError(){

        activity.runOnUiThread {

            val filter = Filter(activity).filterContacts("",
                "", "", "74636376", "")
            Assert.assertNull(filter)
        }
    }

    @Test
    fun whenExecute_withNonExistingContactPhoneWorkAsCriteria_thenError(){

        activity.runOnUiThread {

            val filter = Filter(activity).filterContacts("",
                "", "", "", "47387376476")
            Assert.assertNull(filter)
        }
    }
}