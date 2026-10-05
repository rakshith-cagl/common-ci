package com.iexceed.plugins.permissiontests

//@LargeTest
//@RunWith(AndroidJUnit4::class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class RunTimePermissionsTest {

//    @get:Rule
//    val activityRule = ActivityTestRule(AppzillonMainScreen::class.java)
//
//    private lateinit var device: UiDevice
//    @Before
//    fun setUp() {
//        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun a_shouldDisplayPermissionRequestDialogAtStartup() {
//        assertViewWithTextIsVisible(device, UiAutomatorUtils.TEXT_ALLOW)
//        assertViewWithTextIsVisible(device, UiAutomatorUtils.TEXT_DENY)
//
//        // cleanup for the next test
//        denyCurrentPermission(device)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun b_shouldDisplayPermissionDeniedCallbackIfPermissionWasDenied() {
//        denyCurrentPermission(device)
//        Espresso.onView(withText(R.string.permission_required))
//            .check(matches(ViewMatchers.isDisplayed()))
//        Espresso.onView(withText(R.string.allow))
//            .check(matches(ViewMatchers.isDisplayed()))
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun c_permissionDeniedCallbackIfPermissionWasDeniedThenAllowed() {
//        if(Build.VERSION.SDK_INT == 32){
//            denyCurrentPermission(device)
//            Espresso.onView(withText(R.string.permission_required))
//                .check(matches(ViewMatchers.isDisplayed()))
//            Espresso.onView(withText(R.string.allow))
//                .check(matches(ViewMatchers.isDisplayed()))
//        }else{
//            denyCurrentPermissionPermanently(device)
//            Espresso.onView(withText(R.string.permission_required))
//                .check(matches(ViewMatchers.isDisplayed()))
//            Espresso.onView(withText(R.string.allow))
//                .check(matches(ViewMatchers.isDisplayed()))
//        }
//
//        // will grant the permission for the next test
//        Espresso.onView(withText(R.string.allow)).perform(click())
//        allowCurrentPermission(device)
//
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun d_permissionDeniedCallbackIfPermissionWasDeniedThenDenied() {
//        if(Build.VERSION.SDK_INT == 32){
//            denyCurrentPermission(device)
//            Espresso.onView(withText(R.string.permission_required))
//                .check(matches(ViewMatchers.isDisplayed()))
//            Espresso.onView(withText(R.string.allow))
//                .check(matches(ViewMatchers.isDisplayed()))
//        }else{
//            denyCurrentPermissionPermanently(device)
//            Espresso.onView(withText(R.string.permission_required))
//                .check(matches(ViewMatchers.isDisplayed()))
//            Espresso.onView(withText(R.string.allow))
//                .check(matches(ViewMatchers.isDisplayed()))
//        }
//
//        // will grant the permission for the next test
//        Espresso.onView(withText(R.string.allow)).perform(click())
//
//        denyCurrentPermission(device)
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun e_permissionDeniedCallbackIfPermissionWasDeniedThenExit() {
//        if(Build.VERSION.SDK_INT == 31){
//            denyCurrentPermission(device)
//            Espresso.onView(withText(R.string.permission_required))
//                .check(matches(ViewMatchers.isDisplayed()))
//            Espresso.onView(withText(R.string.allow))
//                .check(matches(ViewMatchers.isDisplayed()))
//        }else{
//            denyCurrentPermissionPermanently(device)
//            Espresso.onView(withText(R.string.permission_required))
//                .check(matches(ViewMatchers.isDisplayed()))
//            Espresso.onView(withText(R.string.allow))
//                .check(matches(ViewMatchers.isDisplayed()))
//        }
//
//        // will not grant the permission for the next test
//        Espresso.onView(withText(R.string.exit)).perform(click())
//
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun f_shouldDisplayWhenNeverAskAgainDialogIsShown() {
//        denyCurrentPermission(device)
//        Espresso.onView(withText(R.string.change_permission_appsetting))
//            .check(matches(ViewMatchers.isDisplayed()))
//        Espresso.onView(withText(R.string.exit))
//            .check(matches(ViewMatchers.isDisplayed()))
//
//        // click exit
//        Espresso.onView(withText(R.string.exit)).perform(click())
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun g_testContactsPermission() {
//
////        denyCurrentPermission(device)
//        assertViewWithTextIsVisible(device, UiAutomatorUtils.TEXT_ALLOW)
//        assertViewWithTextIsVisible(device, UiAutomatorUtils.TEXT_DENY)
//
//        allowCurrentPermission(device)
//
//        /*val contactsPlugin = Contacts.createPlugin(AppzillonMainScreen.webView!!,
//            AppzillonMainScreen.activity, object : IapzPluginUtil {
//                override fun sendError(
//                    callbackId: String?,
//                    errorCode: String?,
//                    aResult: JSONObject?,
//                    activity: Activity,
//                    webView: WebView,
//                    isInUIThread: Boolean
//                ) {
//                    Assert.assertEquals("APZ-CNT-200", errorCode)
//                }
//            }) as Contacts
//        val lObj = JSONObject()
//        lObj.put("id", "CONTACT_ADD")
//        lObj.put("callBack", "executeCallback")
//        lObj.put("command", "PLGN_ADD_CONT")
//        lObj.put("action", "PLGN_ADD_CONT")
//        contactsPlugin.execute(lObj)*/
//
//        assertViewWithTextIsVisible(device, UiAutomatorUtils.TEXT_ALLOW)
//        assertViewWithTextIsVisible(device, UiAutomatorUtils.TEXT_DENY)
//
//        denyCurrentPermission(device)
//
//        Espresso.onView(withText("To add or edit contacts, allow app to access by granting requested permissions"))
//            .check(matches(ViewMatchers.isDisplayed()))
//    }



}
