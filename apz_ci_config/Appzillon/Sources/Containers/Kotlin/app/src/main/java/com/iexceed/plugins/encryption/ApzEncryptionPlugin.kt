package com.iexceed.plugins.encryption

import android.util.Base64
import android.webkit.WebView
import com.iexceed.common.AppzillonConstants.CRYPTO_ALGORITHM
import com.iexceed.common.ApzActivity
import com.iexceed.common.StringUtils
import com.iexceed.common.StringUtils.getString
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.PluginConstants
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.security.HashXor
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.security.Key
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.InvalidKeySpecException
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class ApzEncryptionPlugin private constructor(val webView: WebView, val activity: ApzActivity<*>,
                                              override val apzPluginUtil: IapzPluginUtil) : ApzPlugin() {

    override fun execute(params: JSONObject) {
        try {
            when (params["command"] as String) {
                PluginConstants.PLGN_ENCRPT_DATA -> encript(params)
                PluginConstants.PLGN_DECRPT_DATA -> decript(params)
                PluginConstants.PLGN_HASH_PWD -> hashPwd(params)
            }
        } catch (e: Exception) {
            ApzLogger.e(Companion.TAG, e.toString())
            sendErrorCallback(e.toString(),"")
        }
    }

    private fun encript(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            var key = params.getString("key").trim { it <= ' ' }
            val stringToEncrypt = params.getString("stringToEncrypt").trim { it <= ' ' }
            if (key.length <= 16) {
                key += paddingMask.substring(0, 16 - key.length)
            } else {
                key = key.substring(0, 16)
            }
            val encryptedText = encryptString(key, stringToEncrypt, getSalt(key), getIV(key))
            if(encryptedText.isNullOrEmpty()){
                sendErrorCallback("Encrypted value is null","APZ-CNT-048")
            }else{
                val successJson = JSONObject()
                successJson.put("text", encryptedText)
                apzPluginUtil.sendSuccess(callbackId, successJson, false, activity, webView, true)
            }
        } catch (e: Exception) {
            ApzLogger.e(Companion.TAG, e.toString())
            sendErrorCallback("Unable to Encrypt.","APZ-CNT-048")
        }
    }

    private fun decript(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            var key: String = params.getString("key").trim { it <= ' ' }
            val stringToDecrypt: String = params.getString("stringToDecrypt").trim { it <= ' ' }
            if (key.length <= 16) {
                key += paddingMask.substring(0, 16 - key.length)
            } else {
                key = key.substring(0, 16)
            }
            val decryptedString = decryptString(key, stringToDecrypt, getSalt(key))

            if(decryptedString.isNullOrEmpty()){
                sendErrorCallback("Decrypted String is null.","APZ-CNT-046");
            }else{
                val successJson = JSONObject()
                successJson.put("text", decryptedString)
                apzPluginUtil.sendSuccess(callbackId, successJson, false, activity, webView, true)
            }

        } catch (e: Exception) {
            ApzLogger.e(Companion.TAG, e.toString())
            sendErrorCallback("Unable to Decrypt.","APZ-CNT-046");
        }
    }

    private fun sendErrorCallback(msg:String,eCode:String) {
        val eJson = JSONObject()
        eJson.put("errorCode", "")
        eJson.put("errorMessage", msg)
        apzPluginUtil.sendError(callbackId, eCode, eJson, activity, webView, true)
    }

    private fun hashPwd(params: JSONObject) {
        try {
            callbackId = params.getString("id")
            ApzLogger.d(Companion.TAG, "getOTP")
            var hashPin: String? = null
            val userId = params.getString("userId").trim { it <= ' ' }
            val pin = params.getString("pwd").trim { it <= ' ' }
            val timeStamp = params.getString("date").trim { it <= ' ' }
            hashPin = hashSHA256(
                pin, userId + getString(
                    StringUtils.SERVER_TOKEN
                )
            )
            val hashXor = HashXor()
            val otp = hashXor.hashValue("", "", "", userId, hashPin, timeStamp)
            val successJson = JSONObject()
            successJson.put("text", otp)
            apzPluginUtil.sendSuccess(callbackId, successJson, false, activity, webView, true)
        } catch (e: Exception) {
            ApzLogger.e(Companion.TAG, e.toString())
            sendErrorCallback("Unable to Hash.","")
        }
    }

    companion object {
        private var pluginObj: ApzPlugin? = null
        const val paddingMask = "$$$$$$$$$$$$$$$$"
        private val hex = charArrayOf(
            '0',
            '1',
            '2',
            '3',
            '4',
            '5',
            '6',
            '7',
            '8',
            '9',
            'a',
            'b',
            'c',
            'd',
            'e',
            'f'
        )
        private const val TAG = "ApzEncryptionPlugin"
        private var decryptionIDEToken = false
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = ApzEncryptionPlugin(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        fun encryptPassword(key: String, stringToEncrypt: String): String? {
            var encryptedText: String? = ""
            try {
                val iv = ByteArray(12)
                val secureRandom = SecureRandom()
                secureRandom.nextBytes(iv)
                val finalSalt = getSalt(key)
                encryptedText = encryptString(key, stringToEncrypt, finalSalt, iv)
            } catch (e: Exception) {
                ApzLogger.e(TAG, e.toString())
            }
            return encryptedText
        }

        fun hashPwdforNativeLogin(params: JSONObject): String {
            var otp = ""
            try {
                ApzLogger.d(TAG, "getOTP")
                //SharedPreferences settings = activity.getApplicationContext().getSharedPreferences(properties, 0);
                var hashPin: String? = null
                val userId = params.getString("userId").trim { it <= ' ' }
                val pin = params.getString("pwd").trim { it <= ' ' }
                val timeStamp = params.getString("date").trim { it <= ' ' }
                hashPin = hashSHA256(
                    pin, userId + getString(
                        StringUtils.SERVER_TOKEN
                    )
                )
                val hashXor = HashXor()
                otp = hashXor.hashValue("", "", "", userId, hashPin, timeStamp)
            } catch (e: Exception) {
                //Sonar fix
            }
            return otp
        }

        /**
         * Prepares the salt based on key
         *
         * @param key
         * @return
         */
        private fun getSalt(key: String): String {
            val c = key.toCharArray()

            // Replace with a "swap" function, if desired:
            var temp = c[0]
            c[0] = c[1]
            c[1] = temp
            temp = c[c.size - 1]
            c[c.size - 1] = c[c.size - 2]
            c[c.size - 2] = temp
            return String(c)
        }

        /**
         * prepares the IV from key
         *
         * @param key
         * @return
         */
        private fun getIV(key: String): ByteArray {
            val iv = ByteArray(16)
            Arrays.fill(iv, 0.toByte())
            val or = StringBuffer(key)
            val nw = or.reverse().toString()
            var keyBytes: ByteArray? = null
            try {
                keyBytes = nw.toByteArray(charset("UTF-8"))
            } catch (e: UnsupportedEncodingException) {
                //Sonar fix
            }
            val rawIV = ByteArray(keyBytes!!.size)
            for (i in keyBytes.indices) {
                rawIV[i] = (keyBytes[i].toInt() shr 1).toByte()
            }
            for (i in iv.indices) {
                iv[i] = rawIV[i]
            }
            return iv
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
                ApzLogger.e("ApzEncryptionPlugin", "Unsupported character set")
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

        // encrypt
        private fun encryptString(
            key: String,
            clearText: String,
            salt: String,
            iv: ByteArray
        ): String? {
            var iv = iv
            val skeySpec = SecretKeySpec(hmacSha1(salt, key), "AES")
            val cipher: Cipher?
            try {
                cipher = Cipher.getInstance(CRYPTO_ALGORITHM)
                iv = ByteArray(12)
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
                )
                return String(encryptedDataWithIv)
            } catch (e: Exception) {
                //Sonar fix
            }
            return null
        }

        // decrypt
        private fun decryptString(
            key: String,
            textToDecrypt: String,
            salt: String
        ): String? {
            val skeySpec: SecretKeySpec? = SecretKeySpec(hmacSha1(salt, key), "AES")
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

        fun hmacSha1(salt: String, key: String): ByteArray {
            var factory: SecretKeyFactory? = null
            var keyByte: Key? = null
            val keyLength: Int
            try {

                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                if(decryptionIDEToken){
                    keyLength = 128
                    decryptionIDEToken = false
                }else {
                    keyLength = 256
                }

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

        fun decryptPassword(keys : String, stringToDecrypt: String): String? {
            var key = keys
            decryptionIDEToken = true
            if (key.length <= 16) {
                key += paddingMask.substring(0, 16 - key.length)
            } else {
                key = key.substring(0, 16)
            }
            ApzLogger.i(TAG, "Key length : " + key.length)
            val finalSalt = getSalt(key)
            return decryptString(key, stringToDecrypt, finalSalt)
        }
    }
}
