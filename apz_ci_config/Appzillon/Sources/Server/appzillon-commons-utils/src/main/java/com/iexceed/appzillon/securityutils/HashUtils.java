package com.iexceed.appzillon.securityutils;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    /* Function to convert SHA256 to Hexadecimal */
    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, HashUtils.class.getName());

    private HashUtils() {

    }

    public static String toHexString(byte[] b) {
        LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "converting to HexString");
        StringBuilder sb = new StringBuilder();
        for (byte value : b) {
            int c = (value >>> 4) & 0xf;
            sb.append(HEX[c]);
            c = (value & 0xf);
            sb.append(HEX[c]);
        }
        return sb.toString();
    }

    /* To encrypt ptext with salt using SHA-256 hash Algo */
    public static String hashSHA256(String pText, String pSalt) {
        String pTextSalt = pText + pSalt;
        String pHashedText = "";
        byte[] ptextSaltbyte;
        byte[] hashbyte;
        try {
            MessageDigest msgdigest = MessageDigest.getInstance("SHA-256");

            ptextSaltbyte = pTextSalt.getBytes(StandardCharsets.UTF_8);

            msgdigest.reset();
            msgdigest.update(ptextSaltbyte);
            hashbyte = msgdigest.digest();
            pHashedText = toHexString(hashbyte);


        } catch (NoSuchAlgorithmException n) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "NoSuchAlgorithmException...", n);

        }
        return pHashedText;
    }
}
