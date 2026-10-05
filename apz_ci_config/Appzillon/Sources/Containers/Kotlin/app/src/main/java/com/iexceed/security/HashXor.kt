package com.iexceed.security

import android.util.Base64
import com.iexceed.security.ShaUtil.toSha256String

class HashXor {
    fun hashValue(
        pimie: String,
        pimsi: String,
        puid: String?,
        puname: String,
        pInpin: String,
        pDate: String
    ): String {
        var encodedBytes: ByteArray? = null
        //String inpin = "1234";
        //String uname = "uname";
        val inconcatstr: String


        val day = pDate.substring(0, 3)
        val hr = pDate.substring(16, 18)
        val min = pDate.substring(19, 21)
        val sec = pDate.substring(22, 24)
        val yr = pDate.substring(13, 15)
        val dd = pDate.substring(5, 7)
        val mm = pDate.substring(8, 10)
        inconcatstr = hr + min + day + yr + dd + mm + sec + puname + pimie + pimsi


        //System.out.println("Concatenated Value : " + inconcatstr);
        //String shainstr;
        // Get the concatenated value
        try {
            // Sha-256 value of concatenated String
            //System.out.println("SHA-256 Concatenated String : " + ShaUtil.toSha256String(inconcatstr));
            // Sha-256 value of 4-digit pin repeated twice to get 8-digits
            //System.out.println("SHA-256 Pin : " + ShaUtil.toSha256String(inpin));
            val xx = HashXor()
            val hshxor = xx.xorHex(
                toSha256String(inconcatstr), toSha256String(
                    pInpin
                )
            )

            val fbyte = hshxor.substring(0, 16)
            val lbyte = hshxor.substring(48)
            // XOR First And Last Bytes
            val fblbxor = xx.xorHex(fbyte, lbyte)
            val hshxor2 = xx.xorHex(fblbxor, toSha256String(pInpin))

            // Round 2 -> First Byte of XOR'ed Sha-256 value of concatenated String and PIN
            //System.out.println("First Byte : " + hshxor2.substring(0,8));
            val fbyte2 = hshxor2.substring(0, 8)
            // Round 2 -> Last Byte of XOR'ed Sha-256 value of concatenated String and PIN
            //System.out.println("Last Byte : " + hshxor2.substring(8));
            val lbyte2 = hshxor2.substring(8)
            // Round 2 -> XOR First And Last Bytes
            val fblbxor2 = xx.xorHex(fbyte2, lbyte2)
            //System.out.println("XOR FB + LB : " + fblbxor2);
            // Round 2 -> XOR Above with Pin Hash
//			       System.out.println("XOR FB + LB + PinHash: " + xx.xorHex(fblbxor2, ShaUtil.toSha256String(inpin)));
            val finalstr = xx.xorHex(fblbxor2, toSha256String(pInpin))


            //System.out.println("Final Str"+finalstr);
            // Finally, the base64 encoded value
//			       BASE64Encoder encoder = new BASE64Encoder();
            //encodedBytes = Base64.encodeBase64(finalstr.getBytes());
            encodedBytes =
                Base64.encode(finalstr.toByteArray(), Base64.NO_WRAP)

            //System.out.println("Final OTP : " + encodedBytes);


            //System.out.println("new :"+ newcode);

//			       BASE64Decoder decoder = new BASE64Decoder();

            //System.out.println("imie :"+ imie);
            //System.out.println("val :"+ decodedstring);
        } catch (e: Exception) {
            //handle exception
        }
        return String(encodedBytes!!)
    }

    // XOR Truth Table from two Hex Strings 	   
    fun xorHex(a: String, b: String): String {
        val chars = CharArray(a.length)
        for (i in chars.indices) {

            //System.out.println(fromHex(a.charAt(i)) ^ fromHex(b.charAt(i)));
            chars[i] = toHex(
                fromHex(a[i]) xor fromHex(
                    b[i]
                )
            )
        }
        return String(chars)
    }

    //char wise int to Hex
    private fun toHex(nybble: Int): Char {
        require(!(nybble < 0 || nybble > 15))
        return "0123456789ABCDEF"[nybble]
    }

    companion object {
        // char wise Hex to int
        private fun fromHex(c: Char): Int {
            if (c >= '0' && c <= '9') {
                return c - '0'
            }
            if (c >= 'A' && c <= 'F') {
                return c - 'A' + 10
            }
            if (c >= 'a' && c <= 'f') {
                return c - 'a' + 10
            }
            throw IllegalArgumentException()
        }
    }
}