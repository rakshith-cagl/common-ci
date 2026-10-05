package com.iexceed.common

import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

/**
 * Copyright (c) 2021 Appzillon. All rights reserved.
 **/


object FingerprintUtils
{
    fun cipherInit(keyStore: KeyStore?, keyName: String): Boolean
    {
        val cipher = try
        {
            Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e)
        }
        return try {
            keyStore?.load(null)
            val key = keyStore?.getKey(keyName, null)
            if (key != null) {

                cipher.init(Cipher.ENCRYPT_MODE, key as SecretKey)
                true
            } else {

                false
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }
}
