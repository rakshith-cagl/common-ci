package com.iexceed.common

import android.content.Context
import android.util.Base64
import com.iexceed.common.AppzillonConstants.CRYPTO_ALGORITHM
import com.iexceed.common.AppzillonConstants.IV_LENGTH
import com.iexceed.common.AppzillonConstants.RSA_CRYPTO_ALGORITHM
import java.io.*
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.security.Key
import java.security.KeyFactory
import java.security.PublicKey
import java.security.SecureRandom
import java.security.interfaces.RSAPublicKey
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.KeySpec
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.*

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/
object PayloadEncryptionDecryption {

    private var publicKey: PublicKey? = null

    fun getKey(aContext: Context): PublicKey?
    {
        return if (publicKey == null) {
            fetchRSAKey(aContext)
            publicKey
        } else {
            publicKey
        }
    }

    private fun fetchRSAKey(aContext: Context) {
        var br: BufferedReader? = null
        var inputStream: InputStream? = null
        try {

            val man = aContext.assets
            val files =
                man.list("apps/" + StringUtils.getString(StringUtils.APP_ID) + "/rsakey")
            if (files!!.isNotEmpty()) {
                for (i in files.indices) {

                    //Reading public key from .pem file
                    inputStream =
                        man.open("apps/" + StringUtils.getString(StringUtils.APP_ID) + "/rsakey/" + files[i])
                    if (files[i].uppercase(Locale.getDefault()).endsWith(".PEM")) {
                        br = BufferedReader(InputStreamReader(inputStream))
                        var line: String?
                        val stringBuilder = StringBuilder()
                        while (br.readLine().also { line = it } != null) {
                            stringBuilder.append(line)
                        }
                        publicKey = getPublicKeyFromPEM(stringBuilder.toString())
                    } else if (files[i].uppercase(Locale.getDefault()).endsWith(".DER")) {
                        val privateKeyContents = readFileContents(inputStream)
                        val publicSpec = X509EncodedKeySpec(privateKeyContents)
                        val keyFactory = KeyFactory.getInstance("RSA")
                        publicKey = keyFactory.generatePublic(publicSpec)
                    }
                }
            }
        } catch (ex: Exception) {
            //Sonar fix
        } finally {
            inputStream?.close()
            br?.close()
        }
    }

    fun encryptDataWithRSAPublicKey(data: String, publicKey: PublicKey?): String? {
        var result: String? = ""
        try {
            val cipher = Cipher.getInstance(RSA_CRYPTO_ALGORITHM)
            val oaepParameterSpec = OAEPParameterSpec(
                "SHA-1",
                "MGF1",
                MGF1ParameterSpec("SHA-1"),
                PSource.PSpecified.DEFAULT
            )
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParameterSpec)
            result = Base64.encodeToString(
                cipher.doFinal(data.toByteArray(charset("UTF-8"))),
                Base64.DEFAULT
            )
        } catch (exe: Exception) {
            //Sonar fix
        }
        return result
    }

    fun decryptDataWithRSAPublicKey(data: String?, publicKey: PublicKey?): String? {
        var result: String? = ""
        try {
            val oaepFromInit = Cipher.getInstance(RSA_CRYPTO_ALGORITHM)
            val oaepParams = OAEPParameterSpec(
                "SHA-1",
                "MGF1",
                MGF1ParameterSpec("SHA-1"),
                PSource.PSpecified.DEFAULT
            )
            oaepFromInit.init(Cipher.DECRYPT_MODE, publicKey, oaepParams)
            val bytNewData = oaepFromInit.doFinal(Base64.decode(data, Base64.DEFAULT))
            result = String(bytNewData)
        } catch (exe: Exception) {
            //Sonar fix
        }
        return result
    }

    private fun encryptUsingSecret(key: String, clearText: String, salt: String): String? {
        val skeySpec = SecretKeySpec(hmacSha1(salt, key), "AES")
        try {
            val cipher = Cipher.getInstance(CRYPTO_ALGORITHM)
            val iv = ByteArray(IV_LENGTH)
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
        } catch (e: Exception) {
            //Sonar fix
        }
        return null
    }

    private fun decryptUsingSecretKey(
        key: String,
        textToDecrypt: String?,
        salt: String
    ): String? {
        val skeySpec = SecretKeySpec(hmacSha1(salt, key), "AES")
        val plainText: ByteArray
        try {
            val ciphertext = Base64.decode(textToDecrypt, Base64.NO_WRAP or Base64.NO_PADDING)
            val cipher = Cipher.getInstance(CRYPTO_ALGORITHM)
            //use first 12 bytes for iv
            val gcmIv: AlgorithmParameterSpec = GCMParameterSpec(128, ciphertext, 0, IV_LENGTH)
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, gcmIv)
            plainText = cipher.doFinal(
                ciphertext,
                IV_LENGTH,
                ciphertext.size - IV_LENGTH
            )
            val plainrStr = String(plainText, Charset.forName("UTF-8"))
            return String(plainrStr.toByteArray())
        } catch (e: Exception) {
            //Sonar fix
        }
        return null
    }

    fun hmacSha1(salt: String, key: String): ByteArray? {
        val factory: SecretKeyFactory?
        var keyByte: Key? = null
        val keyLength: Int
        try {
        	// Master sync
            /*val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val keyspec: KeySpec = PBEKeySpec(
                key.toCharArray(),
                salt.toByteArray(charset("UTF-8")), 2, 128
            )*/
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            keyLength = 256
            val keyspec: KeySpec =
                PBEKeySpec(key.toCharArray(),
                    salt.toByteArray(charset("UTF-8")),
                    2, keyLength)
            keyByte = factory.generateSecret(keyspec)
        } catch (e: Exception) {
            //Sonar fix
        }
        return keyByte!!.encoded
    }

    fun getDecryptedPayloadWithAES(
        encryptedString: String?,
        key: String
    ): String? {
        var key = key
        val paddingMask = "$$$$$$$$$$$$$$$$"
        if (key.length <= 16) {
            key += paddingMask.substring(0, 16 - key.length)
        }
        if (key.length > 16) {
            key = key.substring(0, 16)
        }
        getIV(key)
        val finalSalt = getSalt(key)
        return decryptUsingSecretKey(key, encryptedString, finalSalt)
    }


    fun getEncryptePayloadwithRSA(aContext: Context, pResponseBody: String): String? {
        return encryptDataWithRSAPublicKey(pResponseBody, getKey(aContext))
    }

    fun getEncryptePayloadwithAES(
        pResponseBody: String,
        key: String
    ): String? {
        var key = key
        val paddingMask = "$$$$$$$$$$$$$$$$"
        if (key.length <= 16) {
            key += paddingMask.substring(0, 16 - key.length)
        }
        if (key.length > 16) {
            key = key.substring(0, 16)
        }
        getIV(key)
        val finalSalt = getSalt(key)
        return encryptUsingSecret(key, pResponseBody, finalSalt)
    }

    private fun getIV(key: String?): ByteArray {
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

    private fun getPublicKeyFromPEM(key: String): RSAPublicKey? {
        var publicKeyPEM = key
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "")
        val encoded = Base64.decode(publicKeyPEM, Base64.NO_WRAP)
        var pubKey: RSAPublicKey? = null
        try {
            val kf = KeyFactory.getInstance("RSA")
            pubKey = kf.generatePublic(X509EncodedKeySpec(encoded)) as RSAPublicKey
        } catch (e: Exception) {
            //Sonar fix
        }
        return pubKey
    }

    @Throws(IOException::class)
    fun readFileContents(isr: InputStream?): ByteArray {
        val dis = DataInputStream(isr)
        val privKeyBytes = ByteArray(dis.available())
        dis.readFully(privKeyBytes)
        dis.close()
        return privKeyBytes
    }


}
