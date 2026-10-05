package com.iexceed.plugins.dataencryption

import android.util.Base64
import android.webkit.WebView
import com.iexceed.common.AppzillonConstants.CRYPTO_ALGORITHM
import com.iexceed.common.AppzillonUtils.getSalt
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.errorlog.ApzLogger
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.security.Key
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class EncryptDecryptUtility private constructor(webView: WebView, activity: ApzActivity<*>,
                                                override val apzPluginUtil: IapzPluginUtil) : ApzPlugin()
{
    private val paddingMask = "$$$$$$$$$$$$$$$$"

    init {
        super.aWebview = webView
        super.aActivity = activity
    }

    override fun execute(params: JSONObject)
    {
        callbackId = ""
        var key = ""
        var stringToEncrypt = ""
        var stringToDecrypt: String? = ""
        var action = ""
        val result: JSONObject
        try {
            result = JSONObject()
            callbackId = params.getString("id")
            key = params.getString("key").trim { it <= ' ' }
            action = params.getString("action")
            if (action.equals("ENCRYPT", ignoreCase = true)) {
                stringToEncrypt = params.getString("stringToEncrypt")
            } else if (action.equals("DECRYPT", ignoreCase = true)) {
                stringToDecrypt = params.getString("stringToDecrypt")
            }
            if (key.length <= 16) {
                key += paddingMask.substring(0, 16 - key.length)
            } else {
                key = key.substring(0, 16)
            }

            val finalSalt = getSalt(key)
            if (action == "ENCRYPT") {
                val encryptedText = encryptString(key, stringToEncrypt, finalSalt)
                if (encryptedText != null) {
                    result.put("encryptedString", encryptedText)
                    apzPluginUtil.sendSuccess(callbackId, result, false, aActivity, aWebview, true)
                } else {
                    apzPluginUtil.sendError(
                        callbackId,
                        "APZ-CNT-048",
                        null,
                        aActivity,
                        aWebview,
                        true
                    ) //Encryption Failed
                }
            } else if (action.equals("DECRYPT", ignoreCase = true)) {
                val decryptedString = decryptString(key, stringToDecrypt, finalSalt)
                if (decryptedString != null) {
                    result.put("decryptedString", decryptedString)
                    apzPluginUtil.sendSuccess(callbackId, result, false, aActivity, aWebview, true)
                } else {
                    apzPluginUtil.sendError(
                        callbackId,
                        "APZ-CNT-046",
                        null,
                        aActivity,
                        aWebview,
                        true
                    ) //Encryption Failed
                }
            }
        } catch (e: JSONException) {
            ApzLogger.e(Companion.TAG, e.toString())
            apzPluginUtil.sendError(callbackId, "APZ-CNT-077", null, aActivity, aWebview, true)
        }
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        var TAG = "EncryptDecryptUtility"
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = EncryptDecryptUtility(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun hmacSha1(salt: String, key: String): ByteArray {
            var factory: SecretKeyFactory? = null
            var keyByte: Key? = null
            val keyLength: Int
            try {
                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                keyLength = 256
                val keyspec: KeySpec =
                    PBEKeySpec(key.toCharArray(),
                        salt.toByteArray(charset("UTF-8")),
                        2, keyLength)

                keyByte = factory.generateSecret(keyspec)
            } catch (e: NoSuchAlgorithmException) {
                ApzLogger.e(TAG, e.toString())
            } catch (e: InvalidKeySpecException) {
                ApzLogger.e(TAG, e.toString())
            } catch (e: UnsupportedEncodingException) {
                ApzLogger.e(TAG, e.toString())
            }
            return keyByte!!.encoded
        }

        // encrypt
        fun encryptString(key: String, clearText: String, salt: String): String? {
            var skeySpec: SecretKeySpec? = null
            skeySpec = SecretKeySpec(hmacSha1(salt, key), "AES")
            var cipher: Cipher? = null
            try {
                cipher = Cipher.getInstance(CRYPTO_ALGORITHM)
                val iv = ByteArray(12)
                val secureRandom = SecureRandom()
                secureRandom.nextBytes(iv)
                val parameterSpec = GCMParameterSpec(128, iv) //128 bit auth tag length
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, parameterSpec)
                val encryptedData = cipher.doFinal(clearText.toByteArray(charset("UTF-8")))
                val encryptedDataWithIv = Base64.encode(
                    ByteBuffer.allocate(iv.size + encryptedData.size)
                        .put(iv)
                        .put(encryptedData)
                        .array(), Base64.NO_WRAP
                ) ?: return null
                return String(encryptedDataWithIv)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: NoSuchPaddingException) {
                e.printStackTrace()
            } catch (e: Exception) {
                //Sonar fix
            }
            return null
        }

        // decrypt
        fun decryptString(key: String, textToDecrypt: String?, salt: String): String? {
            var skeySpec: SecretKeySpec? = null
            skeySpec = SecretKeySpec(hmacSha1(salt, key), "AES")
            val plainText: ByteArray
            var plainrStr: String? = null
            try {
                val ciphertext = Base64.decode(textToDecrypt, Base64.NO_WRAP or Base64.NO_PADDING)
                val cipher = Cipher.getInstance(CRYPTO_ALGORITHM)
                //use first 12 bytes for iv
                val gcmIv: AlgorithmParameterSpec = GCMParameterSpec(128, ciphertext, 0, 12)
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, gcmIv)
                plainText = cipher.doFinal(ciphertext, 12, ciphertext.size - 12)
                plainrStr = String(plainText, Charset.forName("UTF-8"))
                if (plainrStr.isNullOrEmpty()) return null
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return plainrStr
        }

        val isEncryptDecryptUtility: Boolean
            get() = true
    }
}