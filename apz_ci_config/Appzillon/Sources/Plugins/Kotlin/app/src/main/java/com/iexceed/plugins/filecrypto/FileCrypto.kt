package com.iexceed.plugins.filecrypto

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.webkit.MimeTypeMap
import android.webkit.WebView
import com.iexceed.appzillonapp.AppzillonMainScreen
import com.iexceed.appzillonapp.R
import com.iexceed.common.AppzillonConstants.CRYPTO_ALGORITHM
import com.iexceed.common.AppzillonUtils
import com.iexceed.common.ApzActivity
import com.iexceed.plugins.ApzPlugin
import com.iexceed.plugins.IapzPluginUtil
import com.iexceed.plugins.auditlog.AuditLog
import com.iexceed.plugins.dataencryption.EncryptDecryptUtility
import com.iexceed.plugins.errorlog.ApzLogger
import com.iexceed.utils.localstorage.FileAccessHelper
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.security.SecureRandom
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Copyright (c) 2022 Appzillon. All rights reserved.
 **/

class FileCrypto private constructor(webView: WebView, activity: ApzActivity<*>,
                                     override val apzPluginUtil: IapzPluginUtil) : ApzPlugin()
{
    var paddingMask = "$$$$$$$$$$$$$$$$"

    val errorCode = "APZ-CNT-077"

    init {
        super.aActivity = activity
        super.aWebview = webView
    }

    // encryption method
    private fun encryptFile(key: String, salt: String, jsonObj: JSONObject)
    {
        //var iv = aiv
        val appSandboxLoc: String =
            AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC
        var srcFilePath = ""
        var encDirPath = ""
        var desFolder = ""
        try {
            callbackId = jsonObj.getString("id")
            srcFilePath = jsonObj.getString("srcFilePath")
            encDirPath = jsonObj.getString("destFilePath")
            if (encDirPath.contains("/")) {
                val index = encDirPath.lastIndexOf("/")
                desFolder = encDirPath.subSequence(0, index) as String
                if (!desFolder.contains(AppzillonMainScreen.SANDBOX_LOC)) {
                    desFolder = appSandboxLoc + desFolder
                    val destDir = File(desFolder)
                    if (!destDir.exists() && destDir.mkdir()) {
                        //handle if file created
                    }
                }
            }
        } catch (e: JSONException) {
            apzPluginUtil.sendError(this.callbackId, errorCode, null, aActivity, aWebview, true)
        }
        // added separator
        if (!srcFilePath.contains(AppzillonMainScreen.SANDBOX_LOC)) {
            srcFilePath = appSandboxLoc + srcFilePath
        }
        if (!encDirPath.contains(AppzillonMainScreen.SANDBOX_LOC)) {
            encDirPath = appSandboxLoc + encDirPath
        }
        val lKeySpec = SecretKeySpec(EncryptDecryptUtility.hmacSha1(salt, key), algorithm)

        val clearText = readFromFileToByteArray(srcFilePath)
        if (clearText != null) {
            handleClearText(lKeySpec, clearText, encDirPath)
        } else {
            apzPluginUtil.sendError(
                this.callbackId,
                "APZ-CNT-002",
                null,
                aActivity,
                aWebview,
                true
            ) //File Not Found
        }
    }

    private fun handleClearText(
        lKeySpec: SecretKeySpec,
        clearText: ByteArray?,
        encDirPath: String
    ) {
        try {
            val cipher = Cipher.getInstance(CRYPTO_ALGORITHM)
            val iv = ByteArray(12)
            val secureRandom = SecureRandom()
            secureRandom.nextBytes(iv)
            val parameterSpec = GCMParameterSpec(128, iv) //128 bit auth tag length
            cipher.init(Cipher.ENCRYPT_MODE, lKeySpec, parameterSpec)
            val encryptedData = cipher.doFinal(clearText)
            val data = String(
                Base64.encode(
                    ByteBuffer.allocate(iv.size + encryptedData.size)
                        .put(iv)
                        .put(encryptedData)
                        .array(), Base64.NO_WRAP
                )
            )
            writeToEncryptedFile(encDirPath, data)
            val path = encDirPath
            ApzLogger.i(TAG, "File encrypted ")
            var result: JSONObject? = null
            try {
                result = JSONObject()
                result.put("filePath", path)
            } catch (ex: JSONException) {
                ApzLogger.e(TAG, ex.toString())
            }
            apzPluginUtil.sendSuccess(
                this.callbackId,
                result,
                false,
                aActivity,
                aWebview,
                true
            )
        } catch (e: Exception) {
            ApzLogger.i(TAG, "File encrypted exception $e")
            apzPluginUtil.sendError(
                this.callbackId,
                "APZ-CNT-209",
                null,
                aActivity,
                aWebview,
                true
            ) //File not found
        }
    }

    // decryption method
    fun decryptFile(key: String, salt: String, iv: ByteArray?, jsonObj: JSONObject)
    {
        val appSandboxLoc: String =
            AppzillonMainScreen.SANDBOX_LOC + File.separator + AppzillonMainScreen.ASSET_APP_LOC
        var encDirPath = ""
        var decDirPath = ""
        var desFolder: String
        try {
            callbackId = jsonObj.getString("id")
            encDirPath = jsonObj.getString("srcFilePath")
            decDirPath = jsonObj.getString("destFilePath")
            if (decDirPath.contains("/")) {
                val index = decDirPath.lastIndexOf("/")
                desFolder = decDirPath.subSequence(0, index) as String
                if (!desFolder.contains(AppzillonMainScreen.SANDBOX_LOC)) {
                    desFolder = appSandboxLoc + desFolder
                    val destDir = File(desFolder)
                    if (!destDir.exists() && destDir.mkdir()) {
                        //handle if file created
                    }
                }
            }
        } catch (e: JSONException) {
            ApzLogger.i(TAG, e.toString())
            apzPluginUtil.sendError(
                this.callbackId,
                errorCode,
                null,
                aActivity,
                aWebview,
                true
            )
        }
        if (!encDirPath.contains(AppzillonMainScreen.SANDBOX_LOC)) {
            encDirPath = appSandboxLoc + encDirPath
        }
        if (!decDirPath.contains(AppzillonMainScreen.SANDBOX_LOC)) {
            decDirPath = appSandboxLoc + decDirPath
        }
        val textToDecrypt = readFromFile(encDirPath)
        if (textToDecrypt.length > 1) {
            val lKeySpec = SecretKeySpec(EncryptDecryptUtility.hmacSha1(salt, key), algorithm)
            try {
                val ciphertext = Base64.decode(textToDecrypt, Base64.NO_WRAP or Base64.NO_PADDING)
                val cipher = Cipher.getInstance(CRYPTO_ALGORITHM)
                //use first 12 bytes for iv
                val gcmIv: AlgorithmParameterSpec = GCMParameterSpec(128, ciphertext, 0, 12)
                cipher.init(Cipher.DECRYPT_MODE, lKeySpec, gcmIv)
                val plaintext = cipher.doFinal(ciphertext, 12, ciphertext.size - 12)
                writeToDecyFile(decDirPath, plaintext)
                ApzLogger.i(TAG, "File decrypted ")
                val path = decDirPath
                AuditLog.makeString("FILECRYPTO", "Success")
                val result = JSONObject()
                result.put("filePath", path)
                apzPluginUtil.sendSuccess(
                    this.callbackId,
                    result,
                    false,
                    aActivity,
                    aWebview,
                    true
                )
            } catch (e: Exception) {
                ApzLogger.i(TAG, e.toString())
                apzPluginUtil.sendError(
                    this.callbackId,
                    "APZ-CNT-210",
                    null,
                    aActivity,
                    aWebview,
                    true
                )
            }
        } else {
            handleFailure(textToDecrypt)
        }
    }

    private fun handleFailure(textToDecrypt: String) {
        var message = ""
        var errorcode = ""
        val result = JSONObject()
        if (textToDecrypt.equals("0", ignoreCase = true)) {
            message = aActivity.applicationContext.resources
                .getString(R.string.file_not_found)
            errorcode = "APZ-CNT-002"
        } else if (textToDecrypt.equals("1", ignoreCase = true)) {
            message = aActivity.applicationContext.resources
                .getString(R.string.cannot_read_file)
            errorcode = "APZ-CNT-008" //Cannot Read the File
        }
        result.put("errorMessage", message)
        result.put("errorCode", errorcode)
        apzPluginUtil.sendError(
            this.callbackId,
            errorcode,
            result,
            aActivity,
            aWebview,
            true
        )
    }

    private fun writeToDecyFile(filePath: String, plaintext: ByteArray)
    {
        val file = File(filePath)
        val path = file.absolutePath
        var fileExtension: String = ""
        val dotPos = path.lastIndexOf('.')
        if (0 <= dotPos) {
            fileExtension = path.substring(dotPos + 1)
        }
        if (fileExtension.isNotEmpty())
        {
            val mime: String? = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.ROOT)
            )
            if (!mime.isNullOrEmpty())
            {
                if (mime.startsWith("image"))
                {
                    handleBitmap(plaintext, file)
                } else {
                    try {
                        val os = FileAccessHelper.getFileOutPutStream(file)
                        os.write(plaintext)
                        os.flush()
                        os.close()
                    } catch (e: Exception) {
                        ApzLogger.i(TAG, e.toString())
                    }
                }
            }
        }
    }

    private fun handleBitmap(plaintext: ByteArray, file: File) {
        // IMAGE FILE
        val bmp: Bitmap = BitmapFactory.decodeByteArray(plaintext, 0, plaintext.size)
        try {
            val out = FileAccessHelper.getFileOutPutStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            ApzLogger.i(TAG, e.toString())
        }
    }

    private fun readFromFileToByteArray(fileName: String): ByteArray?
    {
        var fileInputStream: FileInputStream? = null
        val file = File(fileName)
        val bFile = ByteArray(file.length().toInt())
        try {
            //convert file into array of bytes
            fileInputStream = FileInputStream(file)
            fileInputStream.read(bFile)
            fileInputStream.close()
        } catch (e: Exception) {
            ApzLogger.i(TAG, e.toString())
            return null
        }
        return bFile
    }

    override fun execute(params: JSONObject)
    {
        var key = ""
        var action = ""
        try {
            callbackId = params.getString("id")
            key = params.getString("key")
            action = params.getString("action")
            if (key.length <= 16) {
                key += paddingMask.substring(0, 16 - key.length)
            } else {
                key = key.substring(0, 16)
            }
            val iv = ByteArray(12)
            val secureRandom = SecureRandom()
            secureRandom.nextBytes(iv)
            val finalSalt: String = AppzillonUtils.getSalt(key)
            if (action == "ENCRYPT") {
                encryptFile(key, finalSalt, params)
            } else if (action == "DECRYPT") {
                decryptFile(key, finalSalt, iv, params)
            }
        } catch (ex: Exception) {
            apzPluginUtil.sendError(
                this.callbackId,
                errorCode,
                null,
                aActivity,
                aWebview,
                true
            )
        }
    }

    companion object {
        private const val algorithm = "AES"
        var TAG = "FileCrypto"
        private var pluginObj: ApzPlugin? = null
        fun createPlugin(webView: WebView, activity: ApzActivity<*>, apzPluginUtil: IapzPluginUtil): ApzPlugin? {
            if (pluginObj == null) {
                pluginObj = FileCrypto(webView, activity, apzPluginUtil)
            }
            return pluginObj
        }

        private fun readFromFile(filename: String): String
        {
            var ret = ""
            val inputStream = FileInputStream(filename)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            try {
                if (inputStream != null) {
                    var receiveString: String? = ""
                    val stringBuilder = StringBuilder()
                    while (bufferedReader.readLine().also { receiveString = it } != null) {
                        stringBuilder.append(receiveString)
                    }

                    ret = stringBuilder.toString()
                }
            } catch (e: FileNotFoundException) {
                ApzLogger.e(TAG, "File not found: $e")
                return "0"
            } catch (e: IOException) {
                ApzLogger.e(TAG, "Can not read file: $e")
                return "1"
            } finally {
                inputStream.close()
                bufferedReader.close()
            }
            return ret
        }

        private fun writeToEncryptedFile(fileName: String, encryptedData: String)
        {
            try {
                val newFile = File(fileName)
                val fos = FileAccessHelper.getFileOutPutStream(newFile)
                val charset = Charset.forName("UTF-8")

                OutputStreamWriter(fos, charset).use { writer ->
                    writer.write(encryptedData)
                }
            } catch (e: IOException) {
                ApzLogger.e(TAG, "File write failed: $e")
            }
        }

        val isFileCryptoPlugin: Boolean
            get() = true
    }
}