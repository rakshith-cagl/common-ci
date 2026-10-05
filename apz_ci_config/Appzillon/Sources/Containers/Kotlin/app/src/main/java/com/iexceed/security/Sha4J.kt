package com.iexceed.security

import java.io.IOException
import java.io.InputStream

/**
 * Sha4J implements SHA-1, SHA-224, SHA-256, SHA-384 and SHA-512 algorithms.<br></br>
 *
 * <pre>
 * Copyright (C) 2006 Softabar
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, * Inc., * 59 Temple Place, * Suite 330,
 * Boston, MA 02111-1307 USA
</pre> *
 *
 * @version 1.0
 */
class Sha4J @JvmOverloads constructor(closeInputStream: Boolean = true) {
    private var closeInputStream = true

    //Start SHA-1 implementation
    // variables and functions also used in other SHA
    // algorithms
    private var messageLength: Long = 0
    @Throws(IOException::class)
    fun sha1Digest(inputStream: InputStream): ByteArray {
        // read bytes
        val mb = ByteArray(64) // 512-bit block
        val m = IntArray(16)
        val w = IntArray(80)

        // SHA-1 constants, digest initial value
// Hash values
        val h0 = 0x67452301
        val h1 = -0x10325477
        val h2 = -0x67452302
        val h3 = 0x10325476
        val h4 = -0x3c2d1e10
        val h = intArrayOf(h0, h1, h2, h3, h4)

        // read bytes from inputstream
// and pad if necessary
        var padBlock = false
        var padded = false
        var bytesRead = inputStream.read(mb, 0, 64)
        while (true) {
            if (bytesRead == -1) {
                if (!padded) {
                    doPadBlock(h, m, w, true)
                }
                val digest = ByteArray(20)
                var i = 0
                var j = 0
                while (j < 5) {
                    digest[i] = (h[j] ushr 24).toByte()
                    digest[i + 1] = (h[j] ushr 16).toByte()
                    digest[i + 2] = (h[j] ushr 8).toByte()
                    digest[i + 3] = h[j].toByte()
                    i += 4
                    j++
                }
                if (closeInputStream) {
                    inputStream.close()
                }
                return digest
            }
            messageLength += (bytesRead shl 3).toLong() // messageLength in bits,
            if (bytesRead < 55) {
                padded = true
                // do padding if read bytes is smaller than 55 ==> total message length
                // can be added in this block as 64-bit value

                // add 1 after message
                mb[bytesRead] = 0x80.toByte()
                for (i in bytesRead + 1..55) {
                    mb[i] = 0
                }
                // add length as 64 bit value at the end of Mb
                addLength128(mb)
            }
            if (bytesRead == 55) {
                padded = true

                // need to add totally new 512 bit block with all zeros except
                // last 64 bits which is message length
                mb[55] = 0x80.toByte()
                addLength128(mb)
            }
            if (bytesRead > 55 && bytesRead < 64) {
                padded = true

                // need to add totally new 512 bit block with all zeros except
                // last 64 bits which is message length
                mb[bytesRead] = 0x80.toByte()
                for (i in bytesRead + 1..63) {
                    mb[i] = 0
                }
                padBlock = true // padBlock is 448-bits of zero + 64 bit length
            }

            // convert to int array. Java int is 32-bits
            // int is a 32-bit word in FIPS 180-2 specification
            var i = 0
            var j = 0
            while (i < 16) {
                m[i] = (mb[j].toInt() shl 24 or (mb[j + 1].toInt() and 0xff shl 16)
                        or (mb[j + 2].toInt() and 0xff shl 8) or (mb[j + 3].toInt() and 0xff))
                j += 4
                i++
            }

            // prepare message schedule
            prepareMessageSchedule(m, w)

            // init working variables
            var a = h[0] // 1732584193;
            var b = h[1] // 4023233417;
            var c = h[2] // 2562383102;
            var d = h[3] // 271733878;
            var e = h[4] // 3285377520;
            var t = 0
            while (t < 80) {
                val tMod = modulo32Add(
                    modulo32Add(
                        modulo32Add(ROTL(5, a), f(t, b, c, d)),
                        modulo32Add(e, K(t))
                    ), w[t]
                )
                e = d
                d = c
                c = ROTL(30, b)
                b = a
                a = tMod
                t++
            }

            h[0] = modulo32Add(h[0], a)
            h[1] = modulo32Add(h[1], b)
            h[2] = modulo32Add(h[2], c)
            h[3] = modulo32Add(h[3], d)
            h[4] = modulo32Add(h[4], e)

            // do last pad
            if (padBlock) {
                doPadBlock(h, m, w, false)
            }
            bytesRead = inputStream.read(mb, 0, 64)
        }
    }

    private fun addLength128(mb: ByteArray) {
        mb[56] = (messageLength ushr 56).toByte()
        mb[57] = (messageLength ushr 48).toByte()
        mb[58] = (messageLength ushr 40).toByte()
        mb[59] = (messageLength ushr 32).toByte()
        mb[60] = (messageLength ushr 24).toByte()
        mb[61] = (messageLength ushr 16).toByte()
        mb[62] = (messageLength ushr 8).toByte()
        mb[63] = messageLength.toByte()
    }

    private fun doPadBlock(height: IntArray, middle: IntArray, width: IntArray, addOne: Boolean) {

        // addOne is the required 1 before
        middle[0] = if (addOne) -0x80000000 else 0
        for (i in 1..13) {
            middle[i] = 0
        }
        middle[14] = (messageLength ushr 32).toInt()
        middle[15] = messageLength.toInt()

        // prepare message schedule
        prepareMessageSchedule(middle, width)

        // init working variables
        var a = height[0] // 1732584193;
        var b = height[1] // 4023233417;
        var c = height[2] // 2562383102;
        var d = height[3] // 271733878;
        var e = height[4] // 3285377520;
        var t = 0
        while (t < 80) {
            val tMod = modulo32Add(
                modulo32Add(
                    modulo32Add(ROTL(5, a), f(t, b, c, d)),
                    modulo32Add(e, K(t))
                ), width[t]
            )
            e = d
            d = c
            c = ROTL(30, b)
            b = a
            a = tMod
            t++
        }
        height[0] = modulo32Add(height[0], a)
        height[1] = modulo32Add(height[1], b)
        height[2] = modulo32Add(height[2], c)
        height[3] = modulo32Add(height[3], d)
        height[4] = modulo32Add(height[4], e)
    }

    private fun prepareMessageSchedule(middle: IntArray, width: IntArray) {
        for (t in 0..79) {
            if (t < 16) {
                width[t] = middle[t]
            } else {
                width[t] = ROTL(1, width[t - 3] xor width[t - 8] xor width[t - 14] xor width[t - 16])
            }
        }
    }

    private fun modulo32Add(x: Int, y: Int): Int {
        return (x xor -0x80000000) + (y xor -0x80000000)
    }

    private fun f(t: Int, x: Int, y: Int, z: Int): Int {
        if (t < 20) {
            return x and y xor (x.inv() and z)
        }
        if (t < 40) {
            return x xor y xor z
        }
        return if (t < 60) {
            x and y xor (x and z) xor (y and z)
        } else x xor y xor z
    }

    private fun Ch(x: Int, y: Int, z: Int): Int {
        return x and y xor (x.inv() and z)
    }

    private fun Maj(x: Int, y: Int, z: Int): Int {
        return x and y xor (x and z) xor (y and z)
    }

    private fun ROTL(n: Int, x: Int): Int {
        return x shl n or (x ushr 32 - n)
    }

    private fun K(t: Int): Int {
        if (t < 20) {
            return 0x5a827999
        }
        if (t < 40) {
            return 0x6ed9eba1
        }
        return if (t < 60) {
            -0x70e44324
        } else -0x359d3e2a
    }

    // End SHA-1 implementation
    // Start SHA-224/SHA-256 implementation
    @Throws(IOException::class)
    fun sha224Digest(inputStream: InputStream): ByteArray {
        return sha256Digest(inputStream, true)
    }

    @Throws(IOException::class)
    fun sha256Digest(inputStream: InputStream): ByteArray {
        return sha256Digest(inputStream, false)
    }

    @Throws(IOException::class)
    private fun sha256Digest(inputStream: InputStream, sha224: Boolean): ByteArray {
        // read bytes
        val mb = ByteArray(64) // 512-bit block
        val m = IntArray(16)
        val w = IntArray(64)

        // SHA-256 constants, digest initial value
        var h0 = 0x6a09e667
        var h1 = -0x4498517b
        var h2 = 0x3c6ef372
        var h3 = -0x5ab00ac6
        var h4 = 0x510e527f
        var h5 = -0x64fa9774
        var h6 = 0x1f83d9ab
        var h7 = 0x5be0cd19
        if (sha224) {
            h0 = -0x3efa6128
            h1 = 0x367cd507
            h2 = 0x3070dd17
            h3 = -0x8f1a6c7
            h4 = -0x3ff4cf
            h5 = 0x68581511
            h6 = 0x64f98fa7
            h7 = -0x4105b05c
        }
        val hArray = intArrayOf(h0, h1, h2, h3, h4, h5, h6, h7)

        // read bytes from inputstream
        // and pad if necessary
        var padBlock = false
        var padded = false
        var bytesRead = inputStream.read(mb, 0, 64)
        while (true) {
            if (bytesRead == -1) {
                if (!padded) {
                    doPadBlock256(hArray, m, w, true)
                }
                val digest = ByteArray(32)
                var i = 0
                var j = 0
                while (j < 8) {
                    digest[i] = (hArray[j] ushr 24).toByte()
                    digest[i + 1] = (hArray[j] ushr 16).toByte()
                    digest[i + 2] = (hArray[j] ushr 8).toByte()
                    digest[i + 3] = hArray[j].toByte()
                    i += 4
                    j++
                }
                if (closeInputStream) {
                    inputStream.close()
                }
                if (sha224) {
                    val digest2 = ByteArray(28)
                    for (k in digest2.indices) {
                        digest2[k] = digest[k]
                    }
                    return digest2
                }
                return digest
            }
            messageLength += (bytesRead shl 3).toLong() // messageLength in bits,
            if (bytesRead < 55) {
                padded = true
                // do padding if read bytes is smaller than 55 ==> total message length
                // can be added in this block as 64-bit value

                // add 1 after message
                mb[bytesRead] = 0x80.toByte()
                for (i in bytesRead + 1..55) {
                    mb[i] = 0
                }
                // add length as 64 bit value at the end of Mb
                addLength128(mb)
            }
            if (bytesRead == 55) {
                padded = true

                // need to add totally new 512 bit block with all zeros except
                // last 64 bits which is message length
                mb[55] = 0x80.toByte()
                addLength128(mb)
            }
            if (bytesRead > 55 && bytesRead < 64) {
                padded = true

                // need to add totally new 512 bit block with all zeros except
                // last 64 bits which is message length
                mb[bytesRead] = 0x80.toByte()
                for (i in bytesRead + 1..63) {
                    mb[i] = 0
                }
                padBlock = true // padBlock is 448-bits of zero + 64 bit length
            }

            // convert to int array. Java int is 32-bits
            // int is a 32-bit word in FIPS 180-2 specification
            var i = 0
            var j = 0
            while (i < 16) {
                m[i] = (mb[j].toInt() shl 24 or (mb[j + 1].toInt() and 0xff shl 16)
                        or (mb[j + 2].toInt() and 0xff shl 8) or (mb[j + 3].toInt() and 0xff))
                j += 4
                i++
            }

            // prepare message schedule
            prepareMessageSchedule256(m, w)

            // init working variables
            var a = hArray[0] // 1732584193;
            var b = hArray[1] // 4023233417;
            var c = hArray[2] // 2562383102;
            var d = hArray[3] // 271733878;
            var e = hArray[4] // 3285377520;
            var f = hArray[5] // 3285377520;
            var g = hArray[6] // 3285377520;
            var h = hArray[7] // 3285377520;
            var t = 0
            while (t < 64) {
                val t1 = modulo32Add(
                    modulo32Add(
                        modulo32Add(h, sigma2561(e)),
                        modulo32Add(Ch(e, f, g), K256[t])
                    ), w[t]
                )
                val t2 = modulo32Add(sigma2560(a), Maj(a, b, c))
                h = g
                g = f
                f = e
                e = modulo32Add(d, t1)
                d = c
                c = b
                b = a
                a = modulo32Add(t1, t2)
                t++
            }
            hArray[0] = modulo32Add(hArray[0], a)
            hArray[1] = modulo32Add(hArray[1], b)
            hArray[2] = modulo32Add(hArray[2], c)
            hArray[3] = modulo32Add(hArray[3], d)
            hArray[4] = modulo32Add(hArray[4], e)
            hArray[5] = modulo32Add(hArray[5], f)
            hArray[6] = modulo32Add(hArray[6], g)
            hArray[7] = modulo32Add(hArray[7], h)

            // do last pad
            if (padBlock) {
                doPadBlock256(hArray, m, w, false)
            }
            bytesRead = inputStream.read(mb, 0, 64)
        }
    }

    private fun doPadBlock256(height: IntArray, middle: IntArray, width: IntArray, addOne: Boolean) {
        // addOne is the required 1 before
        middle[0] = if (addOne) -0x80000000 else 0
        for (i in 1..13) {
            middle[i] = 0
        }
        middle[14] = (messageLength ushr 32).toInt()
        middle[15] = messageLength.toInt()

        // prepare message schedule
        prepareMessageSchedule256(middle, width)

        // init working variables
        var a = height[0] // 1732584193;
        var b = height[1] // 4023233417;
        var c = height[2] // 2562383102;
        var d = height[3] // 271733878;
        var e = height[4] // 3285377520;
        var f = height[5] // 3285377520;
        var g = height[6] // 3285377520;
        var h = height[7] // 3285377520;
        var t = 0
        var tt1 = 0
        var tt2 = 0
        while (t < 64) {
            tt1 = modulo32Add(
                modulo32Add(
                    modulo32Add(h, sigma2561(e)), modulo32Add(
                        Ch(e, f, g), K256[t]
                    )
                ), width[t]
            )
            tt2 = modulo32Add(sigma2560(a), Maj(a, b, c))
            h = g
            g = f
            f = e
            e = modulo32Add(d, tt1)
            d = c
            c = b
            b = a
            a = modulo32Add(tt1, tt2)
            t++
        }
        height[0] = modulo32Add(height[0], a)
        height[1] = modulo32Add(height[1], b)
        height[2] = modulo32Add(height[2], c)
        height[3] = modulo32Add(height[3], d)
        height[4] = modulo32Add(height[4], e)
        height[5] = modulo32Add(height[5], f)
        height[6] = modulo32Add(height[6], g)
        height[7] = modulo32Add(height[7], h)
    }

    private fun prepareMessageSchedule256(middle: IntArray, width: IntArray) {
        for (t in 0..63) {
            if (t < 16) {
                width[t] = middle[t]
            } else {
                width[t] = modulo32Add(
                    modulo32Add(sigma25611(width[t - 2]), width[t - 7]),
                    modulo32Add(sigma25600(width[t - 15]), width[t - 16])
                )
            }
        }
    }

    private fun sigma2560(x: Int): Int {
        return ROTR(2, x) xor ROTR(13, x) xor ROTR(22, x)
    }

    private fun sigma2561(x: Int): Int {
        return ROTR(6, x) xor ROTR(11, x) xor ROTR(25, x)
    }

    private fun sigma25600(x: Int): Int {
        return ROTR(7, x) xor ROTR(18, x) xor shr(3, x)
    }

    private fun sigma25611(x: Int): Int {
        return ROTR(17, x) xor ROTR(19, x) xor shr(10, x)
    }

    private fun ROTR(n: Int, x: Int): Int {
        return x ushr n or (x shl 32 - n)
    }

    private fun shr(n: Int, x: Int): Int {
        return x ushr n
    }

    // End SHA-224/256 implementation
    // Start SHA-512/384 implementation
    // SHA512 max length is 128bits
    private var messageLengthUpper: Long = 0
    private var messageLengthLower: Long = 0
    @Throws(IOException::class)
    fun sha384Digest(inputStream: InputStream): ByteArray {
        return sha512Digest(inputStream, true)
    }

    @Throws(IOException::class)
    fun sha512Digest(inputStream: InputStream): ByteArray {
        return sha512Digest(inputStream, false)
    }

    @Throws(IOException::class)
    private fun sha512Digest(inputStream: InputStream, sha384: Boolean): ByteArray {

        // read bytes
        val mb = ByteArray(128) // 1024-bit block
        val m = LongArray(16)
        val w = LongArray(80)

        // SHA-512 constants, digest initial value
        var h0 = 0x6a09e667f3bcc908L
        var h1 = -0x4498517a7b3558c5L
        var h2 = 0x3c6ef372fe94f82bL
        var h3 = -0x5ab00ac5a0e2c90fL
        var h4 = 0x510e527fade682d1L
        var h5 = -0x64fa9773d4c193e1L
        var h6 = 0x1f83d9abfb41bd6bL
        var h7 = 0x5be0cd19137e2179L
        if (sha384) {
            h0 = -0x344462a23efa6128L
            h1 = 0x629a292a367cd507L
            h2 = -0x6ea6fea5cf8f22e9L
            h3 = 0x152fecd8f70e5939L
            h4 = 0x67332667ffc00b31L
            h5 = -0x714bb57897a7eaefL
            h6 = -0x24f3d1f29b067059L
            h7 = 0x47b5481dbefa4fa4L
        }
        val longs = longArrayOf(h0, h1, h2, h3, h4, h5, h6, h7)

        // read bytes from inputstream
        // and pad if necessary
        var padBlock = false
        var padded = false
        var bytesRead = inputStream.read(mb, 0, 128)
        while (true) {
            if (bytesRead == -1) {
                if (!padded) {
                    doPadBlock512(longs, m, w, true)
                }
                val digest = ByteArray(64)
                var i = 0
                var j = 0
                while (j < 8) {
                    digest[i] = (longs[j] ushr 56).toByte()
                    digest[i + 1] = (longs[j] ushr 48).toByte()
                    digest[i + 2] = (longs[j] ushr 40).toByte()
                    digest[i + 3] = (longs[j] ushr 32).toByte()
                    digest[i + 4] = (longs[j] ushr 24).toByte()
                    digest[i + 5] = (longs[j] ushr 16).toByte()
                    digest[i + 6] = (longs[j] ushr 8).toByte()
                    digest[i + 7] = longs[j].toByte()
                    i += 8
                    j++
                }
                if (closeInputStream) {
                    inputStream.close()
                }
                if (sha384) {
                    val digest2 = ByteArray(48)
                    for (k in digest2.indices) {
                        digest2[k] = digest[k]
                    }
                    return digest2
                }
                return digest
            }
            messageLengthLower += (bytesRead shl 3).toLong() // messageLength in bits,
            if (bytesRead < 110) {
                padded = true
                // do padding if read bytes is smaller than 55 ==> total message length
                // can be added in this block as 64-bit value
                // add 1 after message
                mb[bytesRead] = 0x80.toByte()
                for (i in bytesRead + 1..111) {
                    mb[i] = 0
                }
                // add length as 128 bit value at the end of Mb
                addLength512(mb)
            }
            if (bytesRead == 110) {
                padded = true

                // need to add totally new 512 bit block with all zeros except
                // last 64 bits which is message length
                mb[110] = 0x80.toByte()
                addLength512(mb)
            }
            if (bytesRead > 110 && bytesRead < 128) {
                padded = true

                // need to add totally new 512 bit block with all zeros except
                // last 64 bits which is message length
                mb[bytesRead] = 0x80.toByte()
                for (i in bytesRead + 1..127) {
                    mb[i] = 0
                }
                padBlock = true // padBlock is 896-bits of zero + 128 bit length
            }

            // convert to long array. Java long is 64-bits
            var i = 0
            var j = 0
            while (i < 16) {
                m[i] = (mb[j].toLong() shl 56 or ((mb[j + 1].toInt() and 0xff).toLong() shl 48)
                        or ((mb[j + 2].toInt() and 0xff).toLong() shl 40)
                        or ((mb[j + 3].toInt() and 0xff).toLong() shl 32)
                        or ((mb[j + 4].toInt() and 0xff).toLong() shl 24)
                        or ((mb[j + 5].toInt() and 0xff).toLong() shl 16)
                        or ((mb[j + 6].toInt() and 0xff).toLong() shl 8) or (mb[j + 7].toLong() and 0xff))
                j += 8
                i++
            }

            // prepare message schedule
            prepareMessageSchedule512(m, w)
            // init working variables
            var a = longs[0] // 1732584193;
            var b = longs[1] // 4023233417;
            var c = longs[2] // 2562383102;
            var d = longs[3] // 271733878;
            var e = longs[4] // 3285377520;
            var f = longs[5] // 3285377520;
            var g = longs[6] // 3285377520;
            var h = longs[7] // 3285377520;
            var t = 0
            while (t < 80) {
                val t1 = modulo64Add(
                    modulo64Add(h, sigma5121(e)), modulo64Add(
                        modulo64Add(
                            Ch(e, f, g),
                            K512[t]
                        ), w[t]
                    )
                )
                val t2 = modulo64Add(sigma5120(a), Maj(a, b, c))
                h = g
                g = f
                f = e
                e = modulo64Add(d, t1)
                d = c
                c = b
                b = a
                a = modulo64Add(t1, t2)
                t++
            }
            longs[0] = modulo64Add(longs[0], a)
            longs[1] = modulo64Add(longs[1], b)
            longs[2] = modulo64Add(longs[2], c)
            longs[3] = modulo64Add(longs[3], d)
            longs[4] = modulo64Add(longs[4], e)
            longs[5] = modulo64Add(longs[5], f)
            longs[6] = modulo64Add(longs[6], g)
            longs[7] = modulo64Add(longs[7], h)

            // do last pad
            if (padBlock) {
                doPadBlock512(longs, m, w, false)
            }
            bytesRead = inputStream.read(mb, 0, 128)
        }
    }

    private fun doPadBlock512(height: LongArray, middle: LongArray, width: LongArray, addOne: Boolean) {
        // addOne is the required 1 before
        middle[0] = if (addOne) (-0x800000000000000L) else 0
        for (i in 1..13) {
            middle[i] = 0
        }
        middle[14] = messageLengthUpper
        middle[15] = messageLengthLower

        // prepare message schedule
        prepareMessageSchedule512(middle, width)

        // init working variables
        var a = height[0] // 1732584193;
        var b = height[1] // 4023233417;
        var c = height[2] // 2562383102;
        var d = height[3] // 271733878;
        var e = height[4] // 3285377520;
        var f = height[5] // 3285377520;
        var g = height[6] // 3285377520;
        var h = height[7] // 3285377520;
        var t = 0
        var t1: Long = 0
        var t2: Long = 0
        while (t < 80) {
            t1 = modulo64Add(
                modulo64Add(
                    modulo64Add(
                        modulo64Add(h, sigma5121(e)),
                        Ch(e, f, g)
                    ), K512[t]
                ), width[t]
            )
            t2 = modulo64Add(sigma5120(a), Maj(a, b, c))
            h = g
            g = f
            f = e
            e = modulo64Add(d, t1)
            d = c
            c = b
            b = a
            a = modulo64Add(t1, t2)
            t++
        }
        height[0] = modulo64Add(height[0], a)
        height[1] = modulo64Add(height[1], b)
        height[2] = modulo64Add(height[2], c)
        height[3] = modulo64Add(height[3], d)
        height[4] = modulo64Add(height[4], e)
        height[5] = modulo64Add(height[5], f)
        height[6] = modulo64Add(height[6], g)
        height[7] = modulo64Add(height[7], h)
    }

    private fun Ch(x: Long, y: Long, z: Long): Long {
        return x and y xor (x.inv() and z)
    }

    private fun Maj(x: Long, y: Long, z: Long): Long {
        return x and y xor (x and z) xor (y and z)
    }

    private fun sigma5120(x: Long): Long {
        return rotr(28, x) xor rotr(34, x) xor rotr(39, x)
    }

    private fun sigma5121(x: Long): Long {
        return rotr(14, x) xor rotr(18, x) xor rotr(41, x)
    }

    private fun prepareMessageSchedule512(middle: LongArray, width: LongArray) {
        for (t in 0..79) {
            if (t < 16) {
                width[t] = middle[t]
            } else {
                width[t] = modulo64Add(
                    modulo64Add(sigma51211(width[t - 2]), width[t - 7]),
                    modulo64Add(sigma51200(width[t - 15]), width[t - 16])
                )
            }
        }
    }

    private fun sigma51200(x: Long): Long {
        return rotr(1, x) xor rotr(8, x) xor shr(7, x)
    }

    private fun sigma51211(x: Long): Long {
        return rotr(19, x) xor rotr(61, x) xor shr(6, x)
    }

    private fun rotr(n: Int, x: Long): Long {
        return x ushr n or (x shl 64 - n)
    }

    private fun shr(n: Int, x: Long): Long {
        return x ushr n
    }

    private fun modulo64Add(x: Long, y: Long): Long {
        return (x xor (-0x800000000000000L)) + (y xor (-0x800000000000000L))
    }

    private fun addLength512(bytes: ByteArray) {
        bytes[112] = (messageLengthUpper ushr 56).toByte()
        bytes[113] = (messageLengthUpper ushr 48).toByte()
        bytes[114] = (messageLengthUpper ushr 40).toByte()
        bytes[115] = (messageLengthUpper ushr 32).toByte()
        bytes[116] = (messageLengthUpper ushr 24).toByte()
        bytes[117] = (messageLengthUpper ushr 16).toByte()
        bytes[118] = (messageLengthUpper ushr 8).toByte()
        bytes[119] = messageLengthUpper.toByte()
        bytes[120] = (messageLengthLower ushr 56).toByte()
        bytes[121] = (messageLengthLower ushr 48).toByte()
        bytes[122] = (messageLengthLower ushr 40).toByte()
        bytes[123] = (messageLengthLower ushr 32).toByte()
        bytes[124] = (messageLengthLower ushr 24).toByte()
        bytes[125] = (messageLengthLower ushr 16).toByte()
        bytes[126] = (messageLengthLower ushr 8).toByte()
        bytes[127] = messageLengthLower.toByte()
    }

    // ENd SHA-512 implementation
    fun reset() {
        // sha-1 variables
        messageLength = 0

        // sha-512 variables
        messageLengthUpper = 0L
        messageLengthLower = 0L
    }

    companion object {
        private val K256 = intArrayOf(
            0x428a2f98, 0x71374491, -0x4a3f0431, -0x164a245b, 0x3956c25b, 0x59f111f1,
            -0x6dc07d5c, -0x54e3a12b, -0x27f85568, 0x12835b01, 0x243185be, 0x550c7dc3,
            0x72be5d74, -0x7f214e02, -0x6423f959, -0x3e640e8c, -0x1b64963f, -0x1041b87a,
            0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            -0x67c1aeae, -0x57ce3993, -0x4ffcd838, -0x40a68039, -0x391ff40d, -0x2a586eb9,
            0x06ca6351, 0x14292967, 0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
            0x650a7354, 0x766a0abb, -0x7e3d36d2, -0x6d8dd37b, -0x5d40175f, -0x57e599b5,
            -0x3db47490, -0x3893ae5d, -0x2e6d17e7, -0x2966f9dc, -0xbf1ca7b, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a,
            0x5b9cca4f, 0x682e6ff3, 0x748f82ee, 0x78a5636f, -0x7b3787ec, -0x7338fdf8,
            -0x6f410006, -0x5baf9315, -0x41065c09, -0x398e870e
        )
        private val K512 = longArrayOf(
            0x428a2f98d728ae22L, 0x7137449123ef65cdL, -0x4a3f043013b2c4d1L,
            -0x164a245a7e762444L, 0x3956c25bf348b538L, 0x59f111f1b605d019L,
            -0x6dc07d5b50e6b065L, -0x54e3a12a25927ee8L, -0x27f855675cfcfdbeL,
            0x12835b0145706fbeL, 0x243185be4ee4b28cL, 0x550c7dc3d5ffb4e2L,
            0x72be5d74f27b896fL, -0x7f214e01c4e9694fL, -0x6423f958da38edcbL,
            -0x3e640e8b3096d96cL, -0x1b64963e610eb52eL, -0x1041b879c7b0da1dL,
            0x0fc19dc68b8cd5b5L, 0x240ca1cc77ac9c65L, 0x2de92c6f592b0275L,
            0x4a7484aa6ea6e483L, 0x5cb0a9dcbd41fbd4L, 0x76f988da831153b5L,
            -0x67c1aead11992055L, -0x57ce3992d24bcdf0L, -0x4ffcd8376704dec1L,
            -0x40a680384110f11cL, -0x391ff40cc257703eL, -0x2a586eb86cf558dbL,
            0x06ca6351e003826fL, 0x142929670a0e6e70L, 0x27b70a8546d22ffcL,
            0x2e1b21385c26c926L, 0x4d2c6dfc5ac42aedL, 0x53380d139d95b3dfL,
            0x650a73548baf63deL, 0x766a0abb3c77b2a8L, -0x7e3d36d1b812511aL,
            -0x6d8dd37aeb7dcac5L, -0x5d40175eb30efc9cL, -0x57e599b443bdcfffL,
            -0x3db4748f2f07686fL, -0x3893ae5cf9ab41d0L, -0x2e6d17e62910ade8L,
            -0x2966f9dbaa9a56f0L, -0xbf1ca7aa88edfd6L, 0x106aa07032bbd1b8L,
            0x19a4c116b8d2d0c8L, 0x1e376c085141ab53L, 0x2748774cdf8eeb99L,
            0x34b0bcb5e19b48a8L, 0x391c0cb3c5c95a63L, 0x4ed8aa4ae3418acbL,
            0x5b9cca4f7763e373L, 0x682e6ff3d6b2b8a3L, 0x748f82ee5defb2fcL,
            0x78a5636f43172f60L, -0x7b3787eb5e0f548eL, -0x7338fdf7e59bc614L,
            -0x6f410005dc9ce1d8L, -0x5baf9314217d4217L, -0x41065c084d3986ebL,
            -0x398e870d1c8dacd5L, -0x35d8c13115d99e64L, -0x2e794738de3f3df9L,
            -0x15258229321f14e2L, -0xa82b08011912e88L, 0x06f067aa72176fbaL,
            0x0a637dc5a2c898a6L, 0x113f9804bef90daeL, 0x1b710b35131c471bL,
            0x28db77f523047d84L, 0x32caab7b40c72493L, 0x3c9ebe0a15c9bebcL,
            0x431d67c49c100d4cL, 0x4cc5d4becb3e42b6L, 0x597f299cfc657e2aL,
            0x5fcb6fab3ad6faecL, 0x6c44198c4a475817L
        )
    }

    init {
        this.closeInputStream = closeInputStream
    }
}