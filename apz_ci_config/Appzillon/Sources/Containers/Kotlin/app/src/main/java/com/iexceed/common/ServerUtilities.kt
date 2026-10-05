package com.iexceed.common

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Base64
import com.iexceed.common.AppzillonConstants.ANDROID_OS
import com.iexceed.common.AppzillonUtils.getDeviceId
import com.iexceed.common.AppzillonUtils.ipAddress
import com.iexceed.common.PayloadEncryptionDecryption.decryptDataWithRSAPublicKey
import com.iexceed.common.PayloadEncryptionDecryption.getDecryptedPayloadWithAES
import com.iexceed.common.PayloadEncryptionDecryption.getKey
import com.iexceed.common.StringUtils.getString
import com.iexceed.common.UserSettings.setIsNotificationRegistered
import com.iexceed.appzillonapp.R
import com.iexceed.retrofitmvvm.data.api.ApiService
import com.iexceed.retrofitmvvm.data.api.RetrofitBuilder
import com.iexceed.utils.localstorage.EncryptedPrefHelper
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.apache.commons.text.StringEscapeUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.security.*
import java.security.cert.CertificateFactory
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

object ServerUtilities {
    var TIME_OUT = 300 * 1000
    private var sc: SSLContext? = null

    private var mPayLoadEncryption = ""
    private val hex = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    private val TAG = "ServerUtilities"
    private var safeBit: String = "0"
    private var decryptedResponseJson: JSONObject? = null
    private var decryptedResponseString = ""

    // Decrypt the Payload
    private val finalDecryptedResponse = JSONObject()
    private var payloadTobeEncrypted = ""

    /**
     * Register this account/device pair within the server.
     *
     * @return whether the registration succeeded or not.
     */
    fun register(context: Context, regId: String?) {
        AppzillonUtils.displayMessage(context, context.getString(R.string.server_registering))
        postNotificationRegistration(context, regId)
    }

    // Broadcast the message of notification.
    private fun registerNotification(aContext: Context, aBoolean: Boolean) {
        if (aBoolean) {
            val message = aContext.getString(R.string.server_registered)
            AppzillonUtils.displayMessage(aContext, message)
            val apps = EncryptedPrefHelper.getPrefs()
            setIsNotificationRegistered(getString(StringUtils.APP_ID), "true", apps)
        } else {
            val message = aContext.getString(R.string.server_register_error)
            AppzillonUtils.displayMessage(aContext, message)
        }
    }

    // sending request for registering the notification
    private fun postNotificationRegistration(aContext: Context, aRegisterId: String?) {
        val bluetoothName: String = AppzillonUtils.getBluetoothName(aContext as Activity)
        val osVersion = Build.VERSION.RELEASE
        val ip = ipAddress()
        val jsonObject = JSONObject()
        try {
            val header = JSONObject()
            header.put(AppzillonConstants.APP_ID, getString(StringUtils.APP_ID))
            header.put(AppzillonConstants.SESSION_ID, "sessionId")
            header.put(AppzillonConstants.INTERFACE_ID, "appzillonNotificationRegistration")
            header.put(AppzillonConstants.SCREEN_ID, "login")
            header.put(AppzillonConstants.DEVICE_ID, getDeviceId())
            header.put(AppzillonConstants.REQUEST_KEY, "000NEW")
            header.put(AppzillonConstants.REQUEST_ID, "")
            header.put(AppzillonConstants.ASYNC, "false")
            header.put(AppzillonConstants.USER_ID, AppzillonConstants.USER_ID_FOR_OTA)
            header.put(AppzillonConstants.REQ_STATUS, true) // sid, 3.2 server changes
            header.put("origination", ip)
            header.put("source", "APPZILLON")

            val appFileReq = JSONObject()
            appFileReq.put(AppzillonConstants.DEVICE_ID, getDeviceId())
            appFileReq.put(AppzillonConstants.DEVICE_NAME, bluetoothName)
            appFileReq.put(AppzillonConstants.OS_ID, ANDROID_OS)
            appFileReq.put("regId", aRegisterId)
            appFileReq.put(AppzillonConstants.OS_VERSION, osVersion)
            appFileReq.put(AppzillonConstants.APP_ID, getString(StringUtils.APP_ID))
            jsonObject.put(AppzillonConstants.APPZILLON_HEADER, header)
            jsonObject.put(AppzillonConstants.APPZILLON_BODY, appFileReq)
        } catch (e: Exception) {
            return
        }

        val lPayLoadEncryption = getString("payloadEncryption")
        val lReq: RequestBody = requestBody(lPayLoadEncryption, aContext, jsonObject)

        val clientNonce = System.currentTimeMillis().toString()
        val interfaceId = jsonObject.getJSONObject(AppzillonConstants.APPZILLON_HEADER).getString(AppzillonConstants.INTERFACE_ID)
        // Change this approach after DI integration
        // val apiInterface = RetrofitBuilder.apiService
        val apiInterface = RetrofitBuilder.getRetrofit(aContext).create(ApiService::class.java)
        val call = apiInterface.notificationRegistration(lReq)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                try {
                    val lRes = response
                    val lApzResponse = lRes.body()?.string()
                    var statusbody = false
                    var statusheader = false
                    if (lRes.isSuccessful && lApzResponse != null) {
                        val lResponseObj: JSONObject? = if (lPayLoadEncryption == "Y") {
                            getDecryptedResponseObj(
                                aContext,
                                lApzResponse,
                                clientNonce,
                                interfaceId
                            )
                        } else {
                            JSONObject(lApzResponse)
                        }
                        if (lResponseObj != null) {
                            val body = lResponseObj.getJSONObject(AppzillonConstants.APPZILLON_BODY)
                            statusbody =
                                body.getString("status").equals("success", ignoreCase = true)
                            val header = lResponseObj.getJSONObject(AppzillonConstants.APPZILLON_HEADER)
                            statusheader = header.getBoolean("status")
                        }
                        registerNotification(aContext, statusbody && statusheader)
                    }
                } catch (e: Exception) {
                    registerNotification(aContext, false)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                registerNotification(aContext, false)
                t.printStackTrace()
            }
        })
    }

    private fun requestBody(
        lPayLoadEncryption: String,
        aContext: Context,
        jsonObject: JSONObject
    ) = if ("Y".equals(lPayLoadEncryption, ignoreCase = true)) {
        val lJSONObject = getEncryptedRequest(aContext, jsonObject.toString())
        lJSONObject.toString().toRequestBody()
    } else {
        jsonObject.toString().toRequestBody()
    }

    // To get the Encrypted Request JSON OBJECT
    fun getEncryptedRequest(aContext: Context, aReqString: String, isCNonceRequired: Boolean = false): JSONObject {
        payloadTobeEncrypted = aReqString
        mPayLoadEncryption = getString("payloadEncryption")
        val requestJson: JSONObject
        val appzillonheader: JSONObject
        val appzillonbody: JSONObject
        var finalResponseObject: JSONObject = JSONObject()

        val isDownload: Boolean
        val currentInterfaceId: String
        val lKey = UUID.randomUUID().toString()

        try {
            safeBit = "1"
            // Request without dataIntegrity and encryption
            requestJson = JSONObject(payloadTobeEncrypted)
            appzillonheader = requestJson.getJSONObject(AppzillonConstants.APPZILLON_HEADER)
            appzillonbody = requestJson.getJSONObject(AppzillonConstants.APPZILLON_BODY)

            currentInterfaceId = appzillonheader.getString(AppzillonConstants.INTERFACE_ID)
            var headerStr = appzillonheader.toString()
            var bodyStr = appzillonbody.toString()
            val clientNonce = System.currentTimeMillis().toString()

            // Checking if the request is for Download
            isDownload = "appzillonFilePushService" == currentInterfaceId ||
                "appzillonFilePushServiceWS" == currentInterfaceId

            if ("appzillonOnAppLaunch" != currentInterfaceId &&
                "appzillonGetAppSecTokens" != currentInterfaceId
            ) {
                appzillonheader.put("serverNonce", AppzillonConstants.SNONCE)
                appzillonheader.put("clientNonce", clientNonce)
                appzillonheader.put("sessionToken", AppzillonConstants.SESSIONTOKEN)
                if (currentInterfaceId.equals("appzillonChangePassword") && "N".equals(mPayLoadEncryption)) {
                    appzillonheader.put("appzillonSafeBit", safeBit)
                }

                payloadTobeEncrypted = requestJson.toString()

                // Converting JsonObject to string for encryption and QOP value
                headerStr =
                    requestJson.getJSONObject(AppzillonConstants.APPZILLON_HEADER).toString()
                bodyStr = requestJson.getJSONObject(AppzillonConstants.APPZILLON_BODY).toString()

                // String paylod
                val payloadStr = (
                    (
                        "{\"" +
                            AppzillonConstants.APPZILLON_HEADER
                        ) + "\":" + headerStr + "," + "\"" +
                        AppzillonConstants.APPZILLON_BODY + "\":" + bodyStr + "}"
                    )

                if (!isDownload) {
                    payloadTobeEncrypted = prepareDIAndEncryptionRequest(
                        aContext,
                        payloadStr,
                        headerStr,
                        bodyStr
                    )
                }
            } else {
                // Encryption Payload for first Request
                if ("Y".equals(mPayLoadEncryption, ignoreCase = true)) {
                    payloadTobeEncrypted = completeEncryptedPayload(
                        aContext,
                        headerStr,
                        bodyStr,
                        false,
                        lKey
                    ).toString()
                }
            }
            finalResponseObject = JSONObject(payloadTobeEncrypted)
            if (isCNonceRequired) {
                finalResponseObject = finalResponseObject.put("localCNonce", clientNonce)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return finalResponseObject
    }

    private fun prepareDIAndEncryptionRequest(
        aContext: Context,
        payloadStr: String,
        headerStr: String,
        bodyStr: String
    ): String {
        var cNonce = ""
        if ("Y".equals(getString("dataIntegrity"), ignoreCase = true)) {
            val json: JSONObject?
            val header: JSONObject?
            try {
                json = JSONObject(payloadTobeEncrypted)
                header = json.getJSONObject(AppzillonConstants.APPZILLON_HEADER)
                cNonce = header.getString("clientNonce")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val rString: String = StringEscapeUtils.escapeJava(payloadStr)
            val qop = finalPayloadAfterHashing(rString, cNonce)

            // Data Integrity with encrytion
            payloadTobeEncrypted = if ("Y".equals(mPayLoadEncryption, ignoreCase = true)) {
                val safeResponse = completeEncryptedPayload(
                    aContext,
                    headerStr,
                    bodyStr,
                    false,
                    AppzillonConstants.APPZILLONSAFE
                )
                val strSafeReq = safeResponse.toString()
                "{\"appzillonQop\":\"" + qop + "\"," + strSafeReq.substring(1)
            } else {
                // Data Integrity without encryption
                "{\"appzillonQop\":\"" + qop + "\"," + payloadStr.substring(1)
            }
        } else {
            // DataIntegrity not enabled
            // Encryption without DataIntegrity
            if ("Y".equals(mPayLoadEncryption, ignoreCase = true)) {
                val safeResponse = completeEncryptedPayload(
                    aContext,
                    headerStr,
                    bodyStr,
                    false,
                    AppzillonConstants.APPZILLONSAFE
                )
                payloadTobeEncrypted = safeResponse.toString()
            }
        }
        return payloadTobeEncrypted
    }

    // To get the decrypted Response JSON OBJECT
    fun getDecryptedResponseObj(context: Context, aString: String, cNonce: String, currentInterfaceId: String): JSONObject? {
        var isFirstResponse = false
        // ResponseStr for QOP

        try {
            // Finding whether first response
            isFirstResponse = checkIfFirstResponse(currentInterfaceId)

            decryptedResponseJson = verifyPayloadAndFirstResponse(aString, isFirstResponse)

            if ("Y".equals(mPayLoadEncryption, ignoreCase = true)) {
                decryptedResponseJson = JSONObject(aString)

                // Copy qop if present
                if (decryptedResponseJson!!.has("appzillonQop")) {
                    val qop = decryptedResponseJson!!.getString("appzillonQop")
                    finalDecryptedResponse.put("appzillonQop", qop)
                    decryptedResponseString = "{\"appzillonQop\":\"$qop\","
                }

                var appzillonSafe = ""
                if (decryptedResponseJson!!.has("appzillonSafe")) {
                    appzillonSafe = decryptedResponseJson!!.optString("appzillonSafe")
                }

                val decryptedAppzillonSafe = decryptDataWithRSAPublicKey(
                    appzillonSafe,
                    getKey(context)
                )
                AppzillonConstants.APPZILLONSAFE = decryptedAppzillonSafe.toString()

                // AppzillonErrors
                checkForAppzillonError(
                    decryptedAppzillonSafe
                )

                // AppzillonHeader
                val decryptedHeader = getDecryptedPayloadWithAES(
                    decryptedResponseJson!!.getString(AppzillonConstants.APPZILLON_HEADER),
                    decryptedAppzillonSafe!!
                )
                decryptedResponseString += "\"" + AppzillonConstants.APPZILLON_HEADER + "\":" + decryptedHeader.toString() + ","
                finalDecryptedResponse.put(
                    AppzillonConstants.APPZILLON_HEADER,
                    JSONObject(decryptedHeader!!)
                )

                // AppzillonBody
                val decryptedBody = getDecryptedPayloadWithAES(
                    decryptedResponseJson!!.getString(AppzillonConstants.APPZILLON_BODY),
                    decryptedAppzillonSafe
                )

                // Checking whether the body is json array or json object
                checkIfTheBodyIsAnArray(decryptedBody)

                // Response JSON
                decryptedResponseJson = finalDecryptedResponse
            }

            // Data Integrity check
            verifyDataIntegrityOfResponse(cNonce, isFirstResponse, aString)

            if ("N".equals(mPayLoadEncryption, ignoreCase = true) &&
                "N".equals(getString("dataIntegrity"), ignoreCase = true) && !isFirstResponse
            ) {
                decryptedResponseJson = JSONObject(aString)
            }

            // }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return decryptedResponseJson
    }

    private fun verifyDataIntegrityOfResponse(
        cNonce: String,
        isFirstResponse: Boolean,
        aString: String
    ) {
        if ("Y".equals(getString("dataIntegrity"), ignoreCase = true) && !isFirstResponse) {
            // if(validateHash(responseJson.toString(0),cNonce)){
            if ("Y".equals(mPayLoadEncryption, ignoreCase = true)) {
                if (validateHash(decryptedResponseString, cNonce)) {
                    decryptedResponseJson?.remove("appzillonQop")
                } else {
                    decryptedResponseJson?.put("errorCode", "APZ-CNT-230")
                }
            } else {
                if (validateHash(aString, cNonce)) {
                    decryptedResponseJson = JSONObject(aString)
                } else {
                    decryptedResponseJson = JSONObject(aString)
                    decryptedResponseJson!!.put("errorCode", "APZ-CNT-230")
                }
            }
        }
    }

    private fun checkIfTheBodyIsAnArray(decryptedBody: String?) {
        if (decryptedBody?.startsWith("[") == true && decryptedBody.endsWith("]")) {
            decryptedResponseString += "\"" + AppzillonConstants.APPZILLON_BODY + "\":" + decryptedBody.toString() + "}"
            finalDecryptedResponse.put(
                AppzillonConstants.APPZILLON_BODY,
                JSONArray(decryptedBody)
            )
        } else {
            decryptedResponseString += "\"" + AppzillonConstants.APPZILLON_BODY + "\":" + decryptedBody.toString() + "}"
            finalDecryptedResponse.put(
                AppzillonConstants.APPZILLON_BODY,
                JSONObject(decryptedBody)
            )
        }
    }

    private fun checkForAppzillonError(
        decryptedAppzillonSafe: String?
    ) {
        if (decryptedResponseJson!!.has(AppzillonConstants.APPZILLON_ERRORS)) {
            val lErrorString = decryptedResponseJson!!.getString(AppzillonConstants.APPZILLON_ERRORS)
            if (!lErrorString.isNullOrEmpty()) {
                val decryptedError = getDecryptedPayloadWithAES(
                    lErrorString,
                    decryptedAppzillonSafe!!
                )
                decryptedResponseString += "\"" + AppzillonConstants.APPZILLON_ERRORS + "\":" + decryptedError.toString() + ","
                finalDecryptedResponse.put(
                    AppzillonConstants.APPZILLON_ERRORS,
                    JSONArray(decryptedError)
                )
            }
        }
    }

    private fun checkIfFirstResponse(
        currentInterfaceId: String
    ): Boolean {
        if ("appzillonOnAppLaunch" == currentInterfaceId ||
            "appzillonGetAppSecTokens" == currentInterfaceId
        ) {
            return true
        }
        return false
    }

    private fun verifyPayloadAndFirstResponse(aString: String, firstResponse: Boolean): JSONObject? {
        if ("N".equals(mPayLoadEncryption, ignoreCase = true) && firstResponse) {
            return JSONObject(aString)
        }
        return null
    }

    private fun validateHash(request: String, cNonce: String): Boolean {
        val reqwithoutQop = "{" + request.substring(request.indexOf(',') + 1)
        val hashedCNONCE: String = hashPayload(cNonce, AppzillonConstants.SNONCE + getString(StringUtils.SERVER_TOKEN))
        // Base64 conversion of the payload
        val addEscape = StringEscapeUtils.escapeJava(reqwithoutQop)

        val base64Req = Base64.encodeToString(addEscape.toByteArray(), Base64.NO_WRAP)
        val hashPayload: String = hashPayload(base64Req, hashedCNONCE)

        var appzillonQop = ""
        try {
            val json = JSONObject(request)
            appzillonQop = json.optString("appzillonQop", "")
        } catch (exp: Exception) {
            exp.printStackTrace()
        }
        return appzillonQop == hashPayload
    }

    private fun hashPayload(textToHash: String, salt: String): String {
        var hashPin: String? = null
        try {
            hashPin = hashSHA256(textToHash, salt)
        } catch (e: NoSuchAlgorithmException) {
            // Sonar fix
        }
        return hashPin!!
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun hashSHA256(ptext: String, psalt: String): String {
        val pTextSalt = ptext + psalt
        var pHashedText = ""
        var ptextSaltbyte = ByteArray(200)
        var hashbyte = ByteArray(200)
        val msgdigest = MessageDigest.getInstance("SHA-256")
        try {
            ptextSaltbyte = pTextSalt.toByteArray(charset("UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            // Sonar fix
        }
        msgdigest.reset()
        msgdigest.update(ptextSaltbyte)
        hashbyte = msgdigest.digest()
        pHashedText = toHexString(hashbyte)
        return pHashedText
    }

    private fun toHexString(b: ByteArray): String {
        val sb = StringBuffer()
        for (i in b.indices) {
            var c: Int = b[i].toInt() ushr 4 and 0xf
            sb.append(hex[c])
            c = b[i].toInt() and 0xf
            sb.append(hex[c])
        }
        return sb.toString()
    }

    private fun completeEncryptedPayload(
        context: Context,
        header: String,
        body: String,
        isRSA: Boolean,
        key: String
    ): JSONObject? {
        var encryptedResponce: JSONObject? = null
        try {
            encryptedResponce = JSONObject()
            // Master sync
            encryptedResponce.put("appzillonSafeBit", safeBit)
            encryptedResponce.put("encMode", 1)

            if (isRSA) {
                encryptedResponce.put(
                    AppzillonConstants.APPZILLON_HEADER,
                    PayloadEncryptionDecryption.getEncryptePayloadwithRSA(
                        context,
                        header
                    )
                )
                encryptedResponce.put(
                    AppzillonConstants.APPZILLON_BODY,
                    PayloadEncryptionDecryption
                        .getEncryptePayloadwithRSA(context, body)
                )
            } else {
                encryptedResponce.put(
                    AppzillonConstants.APPZILLON_HEADER,
                    PayloadEncryptionDecryption.getEncryptePayloadwithAES(header, key)
                )
                encryptedResponce.put(
                    AppzillonConstants.APPZILLON_BODY,
                    PayloadEncryptionDecryption.getEncryptePayloadwithAES(body, key)
                )
            }
            if (!isRSA) {
                encryptedResponce.put(
                    "appzillonSafe",
                    PayloadEncryptionDecryption.encryptDataWithRSAPublicKey(
                        key,
                        getKey(context)
                    )
                )
            }
            return encryptedResponce
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return encryptedResponce
    }

    private fun finalPayloadAfterHashing(
        req: String,
        cNonce: String
    ): String {
        val hashedCNONCE = hashPayload(
            cNonce,
            AppzillonConstants.SNONCE + getString(StringUtils.SERVER_TOKEN)
        )

        // Converting the payload to base64
        val base64req =
            Base64.encodeToString(req.toByteArray(), Base64.NO_WRAP)

        // Hashing the base64 payload
        return hashPayload(base64req, hashedCNONCE)
    }

    fun pinning(context: Context): SSLContext? {
        var caInput: InputStream? = null
        var keyStore: KeyStore?
        try {
            val man = context.assets
            val files = man.list("apps/" + getString(StringUtils.APP_ID) + "/sslCertificates")
            return if (files!!.isNotEmpty()) {
                val sc = SSLContext.getInstance("TLSv1.2")
                val keyStoreType = KeyStore.getDefaultType()
                keyStore = KeyStore.getInstance(keyStoreType)
                keyStore.load(null, null)
                // randomCA.crt should be in the Assets directory
                for (j in files.indices) {
                    try {
                        caInput = null
                        // Load CAs from an InputStream
                        caInput =
                            BufferedInputStream(
                                man.open(
                                    "apps/" +
                                        getString(StringUtils.APP_ID) + "/sslCertificates/" + files[j]
                                )
                            )
                        val cf = CertificateFactory.getInstance("X.509")
                        val ca = cf.generateCertificate(caInput)

                        // Create a KeyStore containing our trusted CAs
                        keyStore.setCertificateEntry("cert$j", ca)
                    } catch (ex: java.lang.Exception) {
                        // Sonar fix
                    }
                }
                // Create a TrustManager that trusts the CAs in our KeyStore
                val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
                val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
                tmf.init(keyStore)
                // Create an SSLContext that uses our TrustManager
                sc.init(null, tmf.trustManagers, null)
                sc
            } else {
                null
            }
        } catch (e: Exception) {
            // Sonar fix
        } finally {
            try {
                caInput?.close()
            } catch (e: IOException) {
                // Sonar fix
            }
        }
        return null
    }

    // changes for javax.net.ssl.SSLHandshakeException (< 5.0) START P1
//    fun updateAndroidSecurityProvider(callingActivity: Activity?) {
//        try {
//            ProviderInstaller.installIfNeeded(AppzillonMainScreen.activity)
//        } catch (e: GooglePlayServicesRepairableException) {
//            // Thrown when Google Play Services is not installed, up-to-date, or enabled
//            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
//            GooglePlayServicesUtil.getErrorDialog(e.connectionStatusCode, callingActivity!!, 0)
//        } catch (e: GooglePlayServicesNotAvailableException) {
//            //ApzLogger.e("SecurityException", "Google Play Services not available.");
//        }
//    }
}
