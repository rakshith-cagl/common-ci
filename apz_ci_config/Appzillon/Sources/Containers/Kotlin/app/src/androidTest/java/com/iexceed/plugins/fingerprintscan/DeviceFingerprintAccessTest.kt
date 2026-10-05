package com.iexceed.plugins.fingerprintscan

//@RunWith(AndroidJUnit4::class)
class DeviceFingerprintAccessTest {

//    lateinit var activityScenario: ActivityScenario<AppzillonMainScreen>
//
//    @ExperimentalCoroutinesApi
//    @get:Rule
//    val testCoroutineRule = TestCoroutineRule()
//
//    lateinit var activity: ApzActivity<*>
//    lateinit var webView: WebView
//
//    private val component =
//        TestComponentRule(InstrumentationRegistry.getInstrumentation().targetContext)
//
//    @get:Rule
//    val chain = RuleChain.outerRule(component)
//
//    @Before
//    fun setUp() {
//        // anything to instatiate before tests are run
//        activityScenario = ActivityScenario.launch(AppzillonMainScreen::class.java)
//        activityScenario.onActivity {
//            this.activity = it.activity
//            this.webView = it.mWebView
//        }
//    }
//
//    @After
//    fun tearDown() {
//
//        activityScenario.close()
//    }
//
//    @Test
//    fun testPluginSupportedStatus() {
//
//        assert(DeviceFingerprintAccess.isPlugin)
//    }
//
//    @Test
//    fun whenExecute_withoutParams_thenError() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                    override fun sendError(
//                        callbackId: String?,
//                        errorCode: String?,
//                        aResult: JSONObject?,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
//                        Assert.assertEquals("APZ-CNT-077", errorCode)
//                    }
//                })
//        } as DeviceFingerprintAccess
//
//        deviceFingerprintAccess.execute(JSONObject())
//    }
//
//    @Test
//    fun whenExecute_sendError_withErrorCode_thenError() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                    override fun sendError(
//                        callbackId: String?,
//                        errorCode: String?,
//                        aResult: JSONObject?,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
//                        Assert.assertEquals("ERROR_CODE", errorCode)
//                    }
//                })
//        } as DeviceFingerprintAccess
//
//        Whitebox.invokeMethod<Any>(deviceFingerprintAccess, "sendError", "ERROR_CODE", null)
//    }
//
//    @Test
//    fun whenExecute_sendError_withErrorMsg_thenError() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                    override fun sendError(
//                        callbackId: String?,
//                        errorCode: String?,
//                        aResult: JSONObject?,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
//                        Assert.assertEquals("ERROR_MSG", aResult?.optString("error"))
//                    }
//                })
//        } as DeviceFingerprintAccess
//
//        Whitebox.invokeMethod<Any>(deviceFingerprintAccess, "sendError", "ERROR_CODE", "ERROR_MSG")
//    }
//
//    @Test
//    fun whenExecute_fetchKeyStore_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val keystore = Whitebox.invokeMethod<Any>(deviceFingerprintAccess, "fetchKeyStore", null)
//        Assert.assertTrue(keystore is KeyStore)
//    }
//
//    @Test
//    fun whenExecute_fetchKeyGenerator_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val keyGenerator = Whitebox.invokeMethod<Any>(
//            deviceFingerprintAccess,
//            "fetchKeyGenerator", null
//        )
//        Assert.assertTrue(keyGenerator is KeyGenerator)
//    }
//
//    @Test
//    fun whenExecute_proceedKeyGeneration_invokeException_thenError() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                    override fun sendError(
//                        callbackId: String?,
//                        errorCode: String?,
//                        aResult: JSONObject?,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
//                        aResult?.has("error")?.let { it1 -> Assert.assertTrue(it1) }
//                    }
//                })
//        } as DeviceFingerprintAccess
//
//        val keyStore: KeyStore? = null
//        val keyGenerator = Whitebox.invokeMethod<Any>(
//            deviceFingerprintAccess,
//            "fetchKeyGenerator", null
//        ) as KeyGenerator
//
//        Whitebox.invokeMethod<Any>(
//            deviceFingerprintAccess,
//            "proceedKeyGeneration", keyStore, keyGenerator
//        )
//    }
//
//    @Test
//    fun whenExecute_enableFingerScanner_withActionAsStoreSecurely_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val jsonObject = JSONObject()
//        jsonObject.put("id", "FINGERPRINT_ACCESS")
//        jsonObject.put("biometricType", "strong")
//
//        val callbackID = "FINGERPRINT_SCAN"
//
//        activity.runOnUiThread {
//
//            Whitebox.invokeMethod<Any>(
//                deviceFingerprintAccess, "enableFingerScanner", activity,
//                webView, callbackID, jsonObject
//            )
//        }
//
//        if (ApzDataSecurity.isPlugin) {
//
//            val apzDataSecurity = ApzDataSecurity.createPlugin(webView,
//                activity, object : IapzPluginUtil {
//
//                    override fun sendSuccess(
//                        callbackId: String?,
//                        aResult: JSONObject?,
//                        isKeepAlive: Boolean,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
//                        val decryptedValue = EncryptedPrefHelper.read("testKey", "")
//                            ?.let { JSONObject(it) }
//                        Assert.assertEquals("testValue", decryptedValue?.optString("value"))
//                    }
//                }) as ApzDataSecurity
//
//            val jsonData = JSONObject()
//            jsonData.put("id", "FINGERPRINT_STORE_SECURELY")
//            jsonData.put("promptBiometric", "N")
//            jsonData.put("action", "STORESECURELY")
//            jsonData.put("reqId", "1")
//            jsonData.put("key", "testKey")
//            jsonData.put("value", "testValue")
//
//            apzDataSecurity.execute(jsonData)
//        }
//    }
//
//    @Test
//    fun whenExecute_enableFingerScanner_withActionAsStoreCredentialsSecurely_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val jsonObject = JSONObject()
//        jsonObject.put("id", "FINGERPRINT_ACCESS")
//        jsonObject.put("biometricType", "strong")
//
//        val callbackID = "FINGERPRINT_SCAN"
//
//        activity.runOnUiThread {
//
//            Whitebox.invokeMethod<Any>(
//                deviceFingerprintAccess, "enableFingerScanner", activity,
//                webView, callbackID, jsonObject
//            )
//        }
//
//        if (ApzDataSecurity.isPlugin) {
//
//            val apzDataSecurity = ApzDataSecurity.createPlugin(webView,
//                activity, object : IapzPluginUtil {
//
//                    override fun sendSuccess(
//                        callbackId: String?,
//                        aResult: JSONObject?,
//                        isKeepAlive: Boolean,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
////                        val decryptedValue = EncryptedPrefHelper.read("testKey", "")
////                            ?.let { JSONObject(it) }
//                        Assert.assertEquals(
//                            "testUserID###@@@###testPwd",
//                            EncryptedPrefHelper.read("credentials", "")
//                        )
//                    }
//                }) as ApzDataSecurity
//
//            val jsonData = JSONObject()
//            jsonData.put("id", "FINGERPRINT_STORE_SECURELY")
//            jsonData.put("promptBiometric", "N")
//            jsonData.put("action", "STORECREDENTIALSSECURELY")
//            jsonData.put("reqId", "1")
//            jsonData.put("isBiometric", "N")
//            jsonData.put("userId", "testUserID")
//            jsonData.put("password", "testPwd")
//
//            apzDataSecurity.execute(jsonData)
//        }
//    }
//
//    @Test
//    fun whenExecute_enableFingerScanner_withActionAsStoreCredentialsSecurely_emptyCredentials_thenError() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val jsonObject = JSONObject()
//        jsonObject.put("id", "FINGERPRINT_ACCESS")
//        jsonObject.put("biometricType", "strong")
//
//        val callbackID = "FINGERPRINT_SCAN"
//
//        activity.runOnUiThread {
//
//            Whitebox.invokeMethod<Any>(
//                deviceFingerprintAccess, "enableFingerScanner", activity,
//                webView, callbackID, jsonObject
//            )
//        }
//
//        if (ApzDataSecurity.isPlugin) {
//
//            val apzDataSecurity = ApzDataSecurity.createPlugin(webView,
//                activity, object : IapzPluginUtil {
//
//                    override fun sendError(
//                        callbackId: String?,
//                        errorCode: String?,
//                        aResult: JSONObject?,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
//                        Assert.assertEquals("APZ-CNT-231", errorCode)
//                    }
//                }) as ApzDataSecurity
//
//            val jsonData = JSONObject()
//            jsonData.put("id", "FINGERPRINT_STORE_SECURELY")
//            jsonData.put("promptBiometric", "N")
//            jsonData.put("action", "STORECREDENTIALSSECURELY")
//            jsonData.put("reqId", "1")
//            jsonData.put("isBiometric", "N")
//            jsonData.put("userId", "")
//            jsonData.put("password", "")
//
//            apzDataSecurity.execute(jsonData)
//        }
//    }
//
//    @Test
//    fun whenExecute_enableFingerScanner_withActionAsRetrieveSecurely_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val jsonObject = JSONObject()
//        jsonObject.put("id", "FINGERPRINT_ACCESS")
//        jsonObject.put("biometricType", "strong")
//
//        val callbackID = "FINGERPRINT_SCAN"
//
//        activity.runOnUiThread {
//
//            Whitebox.invokeMethod<Any>(
//                deviceFingerprintAccess, "enableFingerScanner", activity,
//                webView, callbackID, jsonObject
//            )
//        }
//
//        if (ApzDataSecurity.isPlugin) {
//
//            val apzDataSecurity = ApzDataSecurity.createPlugin(webView,
//                activity, object : IapzPluginUtil {
//
//                    override fun sendSuccess(
//                        callbackId: String?,
//                        aResult: JSONObject?,
//                        isKeepAlive: Boolean,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
//                        Assert.assertNotNull(aResult?.getString("text"))
//                    }
//                }) as ApzDataSecurity
//
//            val jsonData = JSONObject()
//            jsonData.put("id", "FINGERPRINT_STORE_SECURELY")
//            jsonData.put("promptBiometric", "N")
//            jsonData.put("action", "RETRIEVESECURELY")
//            jsonData.put("reqId", "1")
//            jsonData.put("isBiometric", "N")
//            jsonData.put("key", "testKey")
//
//            apzDataSecurity.execute(jsonData)
//        }
//    }
//
//    @Test
//    fun whenExecute_enableFingerScanner_withActionAsRetrieveCredentialsSecurely_encryptedSuccess_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val jsonObject = JSONObject()
//        jsonObject.put("id", "FINGERPRINT_ACCESS")
//        jsonObject.put("biometricType", "strong")
//
//        val callbackID = "FINGERPRINT_SCAN"
//
//        activity.runOnUiThread {
//
//            Whitebox.invokeMethod<Any>(
//                deviceFingerprintAccess, "enableFingerScanner", activity,
//                webView, callbackID, jsonObject
//            )
//        }
//
//        if (ApzDataSecurity.isPlugin) {
//
//            val loginFromContainer =
//                LoginfromContainer(webView, activity, "LOGIN", callbackID, object : IapzPluginUtil {
//
//                    override fun sendSuccess(
//                        callbackId: String?,
//                        aResult: JSONObject?,
//                        isKeepAlive: Boolean,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
//                        val paramsJson = aResult?.getJSONObject("params")
//                        Assert.assertEquals(true, paramsJson?.optBoolean("status"))
//                    }
//                })
//
//            val responseObject = JSONObject()
//            val callbackResObject = JSONObject()
//            Whitebox.invokeMethod<Any>(
//                loginFromContainer,
//                "sendEncryptedSuccess",
//                responseObject,
//                callbackResObject
//            )
//
//        }
//    }
//
//    @Test
//    fun whenExecute_enableFingerScanner_withActionAsRetrieveCredentialsSecurely_sendSuccess_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val jsonObject = JSONObject()
//        jsonObject.put("id", "FINGERPRINT_ACCESS")
//        jsonObject.put("biometricType", "strong")
//
//        val callbackID = "FINGERPRINT_SCAN"
//
//        activity.runOnUiThread {
//
//            Whitebox.invokeMethod<Any>(
//                deviceFingerprintAccess, "enableFingerScanner", activity,
//                webView, callbackID, jsonObject
//            )
//        }
//
//        if (ApzDataSecurity.isPlugin) {
//
//            val loginFromContainer =
//                LoginfromContainer(webView, activity, "LOGIN", callbackID, object : IapzPluginUtil {
//
//                    override fun sendSuccess(
//                        callbackId: String?,
//                        aResult: JSONObject?,
//                        isKeepAlive: Boolean,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
//                        val paramsJson = aResult?.getJSONObject("params")
//                        Assert.assertEquals(true, paramsJson?.optBoolean("status"))
//                    }
//                })
//
//            val appzillonHeader = AppzillonHeader(
//                "", "", "",
//                "", "", "", "", "",
//                "", "", "", "", true, ""
//            )
//            val userDet = UserDet("", "", "", "", "", "")
//            val loginResponse = LoginResponse(true, userDet)
//            val appzillonBody = AppzillonBody(loginResponse)
//            val appzillonError = AppzillonError("", "")
//            val appzillonErrorList = ArrayList<AppzillonError>()
//            appzillonErrorList.add(appzillonError)
//            val callbackResObject = JSONObject()
//            val apzLoginResponse =
//                ApzLoginResponse(appzillonHeader, appzillonBody, appzillonErrorList)
//
//            Whitebox.invokeMethod<Any>(
//                loginFromContainer,
//                "sendSuccess",
//                apzLoginResponse,
//                callbackResObject
//            )
//
//        }
//    }
//
//    @Test
//    fun whenExecute_enableFingerScanner_withActionAsRetrieveCredentialsSecurely_sendError_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val jsonObject = JSONObject()
//        jsonObject.put("id", "FINGERPRINT_ACCESS")
//        jsonObject.put("biometricType", "strong")
//
//        val callbackID = "FINGERPRINT_SCAN"
//
//        activity.runOnUiThread {
//
//            Whitebox.invokeMethod<Any>(
//                deviceFingerprintAccess, "enableFingerScanner", activity,
//                webView, callbackID, jsonObject
//            )
//        }
//
//        if (ApzDataSecurity.isPlugin) {
//
//            val loginFromContainer =
//                LoginfromContainer(webView, activity, "LOGIN", callbackID, object : IapzPluginUtil {
//
//                    override fun sendError(
//                        callbackId: String?,
//                        errorCode: String?,
//                        aResult: JSONObject?,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
//                        aResult?.getJSONObject("params")?.optBoolean("status")
//                            ?.let { Assert.assertFalse(it) }
//                    }
//                })
//
//            val callbackResObject = JSONObject()
//            Whitebox.invokeMethod<Any>(loginFromContainer, "sendError", callbackResObject)
//
//        }
//    }
//
//    @Test
//    fun whenExecute_enableFingerScanner_withActionAsRetrieveCredentialsSecurely_sendErrorWithErrorCode_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val jsonObject = JSONObject()
//        jsonObject.put("id", "FINGERPRINT_ACCESS")
//        jsonObject.put("biometricType", "strong")
//
//        val callbackID = "FINGERPRINT_SCAN"
//
//        activity.runOnUiThread {
//
//            Whitebox.invokeMethod<Any>(
//                deviceFingerprintAccess, "enableFingerScanner", activity,
//                webView, callbackID, jsonObject
//            )
//        }
//
//        if (ApzDataSecurity.isPlugin) {
//
//            val loginFromContainer =
//                LoginfromContainer(webView, activity, "LOGIN", callbackID, object : IapzPluginUtil {
//
//                    override fun sendError(
//                        callbackId: String?,
//                        errorCode: String?,
//                        aResult: JSONObject?,
//                        activity: Activity,
//                        webView: WebView,
//                        isInUIThread: Boolean
//                    ) {
//
//                        aResult?.getJSONObject("params")?.optBoolean("status")
//                            ?.let { Assert.assertFalse(it) }
//                    }
//                })
//
//            val errorCode = ""
//            val response = ""
//            val callbackResObject = JSONObject()
//            Whitebox.invokeMethod<Any>(
//                loginFromContainer,
//                "sendError",
//                errorCode,
//                response,
//                callbackResObject
//            )
//
//        }
//    }
//
//    @Test
//    fun whenExecute_getKeyguardManager_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val keyguardManager = Whitebox.invokeMethod<Any>(
//            deviceFingerprintAccess,
//            "getKeyguardManager", activity
//        )
//
//        Assert.assertTrue(keyguardManager is KeyguardManager)
//    }
//
//    @Test
//    fun whenExecute_getBiometricManager_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val biometricManager = Whitebox.invokeMethod<Any>(
//            deviceFingerprintAccess,
//            "getBiometricManager", activity
//        )
//
//        Assert.assertTrue(biometricManager is BiometricManager)
//    }
//
//    @Test
//    fun whenExecute_getBiometricHandler_thenSuccess() {
//
//        val deviceFingerprintAccess = webView.let {
//            DeviceFingerprintAccess.createPlugin(it,
//                activity, object : IapzPluginUtil {
//
//                })
//        } as DeviceFingerprintAccess
//
//        val callbackID = "FINGERPRINT_SCAN"
//        val biometricHandler = Whitebox.invokeMethod<Any>(
//            deviceFingerprintAccess,
//            "getBiometricHandler", webView,
//            activity, callbackID
//        )
//
//        Assert.assertTrue(biometricHandler is BiometricHandler)
//    }
//
//    @Test
//    fun whenExecute_proceedKeyGeneration_thenSuccess() {
//
//        val deviceFingerprintAccess = DeviceFingerprintAccess.createPlugin(webView,
//            activity, object : IapzPluginUtil {
//
//            })
//
//        val keystore = Whitebox.invokeMethod<Any>(
//            deviceFingerprintAccess,
//            "fetchKeyStore", null
//        ) as KeyStore
//
//        val keyGenerator = Whitebox.invokeMethod<Any>(
//            deviceFingerprintAccess,
//            "fetchKeyGenerator", null
//        ) as KeyGenerator
//
//        Whitebox.invokeMethod<Any>(
//            deviceFingerprintAccess,
//            "proceedKeyGeneration", keystore, keyGenerator
//        )
//
//        Assert.assertEquals("AndroidKeyStore", keyGenerator.provider.name)
//    }
}
