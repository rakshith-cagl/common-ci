package com.iexceed.security

import java.io.*

object ShaUtil {
    private val sha4j: Sha4J = Sha4J()

    //SHA-1
    @Throws(IOException::class)
    fun sha1(input: String): ByteArray {
        sha4j.reset()
        return sha4j.sha1Digest(ByteArrayInputStream(input.toByteArray()))
    }

    @Throws(IOException::class)
    fun sha1(input: InputStream): ByteArray {
        sha4j.reset()
        return sha4j.sha1Digest(input)
    }

    @Throws(IOException::class)
    fun sha1(input: File?): ByteArray {
        sha4j.reset()
        val fis = FileInputStream(input)
        return sha4j.sha1Digest(fis)
    }

    @Throws(IOException::class)
    fun toSha1String(input: String): String {
        return toHexString(sha1(input))
    }

    @Throws(IOException::class)
    fun toSha1String(input: InputStream): String {
        return toHexString(sha1(input))
    }

    @Throws(IOException::class)
    fun toSha1String(input: File?): String {
        return toHexString(sha1(input))
    }

    //SHA-224
    @Throws(IOException::class)
    fun sha224(input: String): ByteArray {
        sha4j.reset()
        return sha4j.sha224Digest(ByteArrayInputStream(input.toByteArray()))
    }

    @Throws(IOException::class)
    fun sha224(input: InputStream): ByteArray {
        sha4j.reset()
        return sha4j.sha224Digest(input)
    }

    @Throws(IOException::class)
    fun sha224(input: File?): ByteArray {
        sha4j.reset()
        val fis = FileInputStream(input)
        return sha4j.sha224Digest(fis)
    }

    @Throws(IOException::class)
    fun toSha224String(input: String): String {
        return toHexString(sha224(input))
    }

    @Throws(IOException::class)
    fun toSha224String(input: InputStream): String {
        return toHexString(sha224(input))
    }

    @Throws(IOException::class)
    fun toSha224String(input: File?): String {
        return toHexString(sha224(input))
    }

    //SHA-256
    @Throws(IOException::class)
    fun sha256(input: String): ByteArray {
        sha4j.reset()
        return sha4j.sha256Digest(ByteArrayInputStream(input.toByteArray()))
    }

    @Throws(IOException::class)
    fun sha256(input: InputStream): ByteArray {
        sha4j.reset()
        return sha4j.sha256Digest(input)
    }

    @Throws(IOException::class)
    fun sha256(input: File?): ByteArray {
        sha4j.reset()
        val fis = FileInputStream(input)
        return sha4j.sha256Digest(fis)
    }

    @Throws(IOException::class)
    fun toSha256String(input: String): String {
        return toHexString(sha256(input))
    }

    @Throws(IOException::class)
    fun toSha256String(input: InputStream): String {
        return toHexString(sha256(input))
    }

    @Throws(IOException::class)
    fun toSha256String(input: File?): String {
        return toHexString(sha256(input))
    }

    //SHA-384
    @Throws(IOException::class)
    fun sha384(input: String): ByteArray {
        sha4j.reset()
        return sha4j.sha384Digest(ByteArrayInputStream(input.toByteArray()))
    }

    @Throws(IOException::class)
    fun sha384(input: InputStream): ByteArray {
        sha4j.reset()
        return sha4j.sha384Digest(input)
    }

    @Throws(IOException::class)
    fun sha384(input: File?): ByteArray {
        sha4j.reset()
        val fis = FileInputStream(input)
        return sha4j.sha384Digest(fis)
    }

    @Throws(IOException::class)
    fun toSha384String(input: String): String {
        return toHexString(sha384(input))
    }

    @Throws(IOException::class)
    fun toSha384String(input: InputStream): String {
        return toHexString(sha384(input))
    }

    @Throws(IOException::class)
    fun toSha384String(input: File?): String {
        return toHexString(sha384(input))
    }

    //SHA-512
    @Throws(IOException::class)
    fun sha512(input: String): ByteArray {
        sha4j.reset()
        return sha4j.sha512Digest(ByteArrayInputStream(input.toByteArray()))
    }

    @Throws(IOException::class)
    fun sha512(input: InputStream): ByteArray {
        sha4j.reset()
        return sha4j.sha512Digest(input)
    }

    @Throws(IOException::class)
    fun sha512(input: File?): ByteArray {
        sha4j.reset()
        val fis = FileInputStream(input)
        return sha4j.sha512Digest(fis)
    }

    @Throws(IOException::class)
    fun toSha512String(input: String): String {
        return toHexString(sha512(input))
    }

    @Throws(IOException::class)
    fun toSha512String(input: InputStream): String {
        return toHexString(sha512(input))
    }

    @Throws(IOException::class)
    fun toSha512String(input: File?): String {
        return toHexString(sha512(input))
    }

    private val hex =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    fun toHexString(b: ByteArray): String {
        val sb = StringBuffer()
        for (i in b.indices) {
            var c: Int = b[i].toInt() ushr 4 and 0xf
            sb.append(hex[c])
            c = b[i].toInt() and 0xf
            sb.append(hex[c])
        }
        return sb.toString()
    }
}