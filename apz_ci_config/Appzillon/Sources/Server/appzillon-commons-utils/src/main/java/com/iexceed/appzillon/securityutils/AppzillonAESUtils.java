package com.iexceed.appzillon.securityutils;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class AppzillonAESUtils {


    private static final String SHA_512 = "SHA-512";
    private static final String AES = "AES";
    private static final int GCM_IV_LENGTH = 12;
    private static final int CBC_IV_LENGTH = 16;
    private static final int TAG_BIT_LENGTH = 128;
    private static final int KEY_LENGTH_128 = 128;
    private static final int KEY_LENGTH_256 = 256;
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, AppzillonAESUtils.class.getName());

    private AppzillonAESUtils() {

    }

    public static String encryptString(String pkey, String poriginalstring) {
        LOG.debug("{} encrypting String ", ServerConstants.LOGGER_PREFIX_RESTFULL);
        String encyptedstring = "";
        try {
            byte[] key = (pkey).getBytes(StandardCharsets.UTF_8);

            MessageDigest sha = MessageDigest.getInstance(SHA_512);
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            LOG.debug("{} Key for encryption : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, new String(key));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);
            encyptedstring = encryptUsingAesGcm(secretKeySpec, poriginalstring);
            LOG.debug("{} length encrypted string : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, encyptedstring.length());
        } catch (Exception e) {
            LOG.error("{} Exception while encryptString: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e.getMessage());
        }
        return encyptedstring;
    }

    public static String decryptString(String pkey, String pencrypted) {
        LOG.debug("{} Decrypting String ", ServerConstants.LOGGER_PREFIX_RESTFULL);
        String originalString = "";
        try {
            byte[] key = (pkey).getBytes(StandardCharsets.UTF_8);

            MessageDigest sha = MessageDigest.getInstance(SHA_512);
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            LOG.debug("{} Key for encrytion: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, new String(key));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, AES);

            originalString = decryptUsingAesGcm(secretKeySpec, pencrypted);
            LOG.debug("{} originalString : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, originalString);
        } catch (Exception e) {
            LOG.error("{} Exception while decryptString: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e.getMessage());
        }
        return originalString;

    }

    public static String decryptContainerString(String pCipherString, String pKey, Message pMessage) {
        try {
            String finalKey = getPaddedKey(pKey);
            SecretKeySpec keySpec = getSecretKeySpec(pMessage.getHeader().getOs(), getSalt(finalKey), finalKey, pMessage.getHeader().getSafeBit(), pMessage.getHeader().getEncyKeyLen());
            String decryptedString = pMessage.getHeader().getEncMode() == 1 ? decryptUsingAesGcm(keySpec, pCipherString) : decryptUsingAesCbc(keySpec, pCipherString);
            LOG.debug("{} Decrypted payload -: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, decryptedString);
            return decryptedString;
        } catch (Exception e) {
            LOG.error("{} Exception while decrypting container String: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e.getMessage());
            return "";
        }
    }

    public static String encryptStringtoContainer(String pPlainText, String pKey, Message pMessage) {
        try {
            String finalKey = getPaddedKey(pKey);
            SecretKeySpec secretKeySpec = getSecretKeySpec(pMessage.getHeader().getOs(), getSalt(finalKey), finalKey, pMessage.getHeader().getSafeBit(), pMessage.getHeader().getEncyKeyLen());
            String encryptedString = pMessage.getHeader().getEncMode() == 1 ? encryptUsingAesGcm(secretKeySpec, pPlainText) : encryptUsingAesCbc(secretKeySpec, pPlainText);
            LOG.debug("{} encryptStringtoContainer Encrypted Body -: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, encryptedString);
            return encryptedString;
        } catch (Exception e) {
            LOG.error("{} Exception while encrypting container String: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e.getMessage());
            return "";
        }
    }

    public static String encryptPayload(String key, String payload, String os, int safeBit, int encMode, int keyLen) {
        try {
            String finalKey = getPaddedKey(key);
            SecretKeySpec secretKeySpec = getSecretKeySpec(os, getSalt(finalKey), finalKey, safeBit, keyLen);
            return encMode == 1 ? encryptUsingAesGcm(secretKeySpec, payload) : encryptUsingAesCbc(secretKeySpec, payload);
        } catch (Exception e) {
            LOG.error("{} Exception while encrypting payload: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e.getMessage());
            return "";
        }
    }

    public static String decryptUsingPayload(String key, String cipherText, String os, int safeBit, int encMode, int hash) {
        try {
            String finalKey = getPaddedKey(key);
            SecretKeySpec keySpec = getSecretKeySpec(os, getSalt(finalKey), finalKey, safeBit, hash);
            String decryptedString = (encMode == 1) ? decryptUsingAesGcm(keySpec, cipherText) : decryptUsingAesCbc(keySpec, cipherText);
            LOG.debug("{} decryptContainerString Decrypted AppzillonBody -: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, decryptedString);
            return decryptedString;
        } catch (Exception e) {
            LOG.error("{} Exception while decrypting payload: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e.getMessage());
            return "";
        }
    }

    // decryption using AEC-GCM
    private static String decryptUsingAesGcm(SecretKeySpec secretKeySpec, String encryptedPayload) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        LOG.debug("Decrypt payload using AES-GCM");
        byte[] cipherText = Base64.decodeBase64(encryptedPayload.getBytes(StandardCharsets.UTF_8));

        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        //use first 12 bytes for iv
        GCMParameterSpec gcmIv = new GCMParameterSpec(TAG_BIT_LENGTH, cipherText, 0, GCM_IV_LENGTH);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmIv);

        //use everything from 12 bytes on as ciphertext
        byte[] plainText = cipher.doFinal(cipherText, GCM_IV_LENGTH, cipherText.length - GCM_IV_LENGTH);
        return new String(plainText, StandardCharsets.UTF_8);
    }

    // encryption using AEC-GCM
    private static String encryptUsingAesGcm(SecretKeySpec secretKeySpec, String plaintext) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        LOG.debug("Encrypt payload using AES-GCM");
        byte[] iv = generateIv(GCM_IV_LENGTH);
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParamSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmParamSpec);

        byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        byte[] cipherTextWithIv = Base64.encodeBase64(ByteBuffer.allocate(iv.length + cipherText.length)
                .put(iv)
                .put(cipherText)
                .array());
        return new String(cipherTextWithIv, StandardCharsets.UTF_8);

    }

    // encryption using AEC-CBC
    private static String encryptUsingAesCbc(SecretKeySpec secretKeySpec, String clearText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        LOG.debug("Encrypt payload using AES-CBC");
        SecureRandom randomSecureRandom = new SecureRandom();
        byte[] iv = new byte[CBC_IV_LENGTH];
        randomSecureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
        byte[] encryptedData = cipher.doFinal(clearText.getBytes(StandardCharsets.UTF_8));
        if (encryptedData == null)
            return "";

        byte[] cipherTextWithIv = ByteBuffer.allocate(iv.length + encryptedData.length)
                .put(iv)
                .put(encryptedData)
                .array();
        return Base64.encodeBase64String(cipherTextWithIv);

    }

    // decryption using AEC-CBC
    private static String decryptUsingAesCbc(SecretKeySpec keySpec, String textToDecrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        LOG.debug("Decrypt payload using AES-CBC");
        byte[] cipherTextBytes = Base64.decodeBase64(textToDecrypt.getBytes(StandardCharsets.UTF_8));
        IvParameterSpec ivParameterSpec = new IvParameterSpec(cipherTextBytes, 0, CBC_IV_LENGTH);
        cipherTextBytes = Arrays.copyOfRange(cipherTextBytes, CBC_IV_LENGTH,
                cipherTextBytes.length);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
        byte[] plaintext = cipher.doFinal(cipherTextBytes);
        return new String(plaintext, StandardCharsets.UTF_8);

    }

    // key generator method
    private static byte[] hmacSha1(String salt, String key, int safeBit, int hash) {
        SecretKeyFactory factory = null;

        KeySpec keyspec = null;
        int keyLen = KEY_LENGTH_128;
        try {
            if (safeBit == 0) {
                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            } else {
                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                if (hash == 1) {
                    keyLen = KEY_LENGTH_256;
                }
            }
            LOG.debug("Key length used: {}", keyLen);
            keyspec = new PBEKeySpec(key.toCharArray(),
                    salt.getBytes(StandardCharsets.UTF_8), 2, keyLen);
            return factory.generateSecret(keyspec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOG.error("{} Exception: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e.getMessage());
            return new byte[0];
        }

    }

    private static byte[] generateIv(int length) {
        byte[] iv = new byte[length];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private static SecretKeySpec getSecretKeySpec(String pOS, String salt, String key, int safeBit, int hash) {
        SecretKeySpec skeySpec;
        if (ServerConstants.OS_BLACKBERRY_10.equalsIgnoreCase(pOS)) {
            skeySpec = new SecretKeySpec(salt.getBytes(), "AES");
        } else {
            skeySpec = new SecretKeySpec(hmacSha1(salt, key, safeBit, hash), "AES");
        }
        return skeySpec;
    }

    private static String getSalt(String key) {
        char[] c = key.toCharArray();

        // Replace with a "swap" function, if desired:
        char temp = c[0];
        c[0] = c[1];
        c[1] = temp;

        temp = c[c.length - 1];
        c[c.length - 1] = c[c.length - 2];
        c[c.length - 2] = temp;
        return new String(c);
    }

    private static String getPaddedKey(String pKey) {
        String lKey = pKey;
        String paddingMask = "$$$$$$$$$$$$$$$$";
        if (lKey.length() <= 16) {
            lKey += paddingMask.substring(0, 16 - lKey.length());
        }
        if (lKey.length() > 16) {
            lKey = lKey.substring(0, 16);
        }
        LOG.trace("{} Encryption key after checking the length : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, lKey);
        return lKey;
    }

    public static String getExpirableKey(boolean eXPInMin, boolean eXPInHr, boolean eXPInDay) {

        Timestamp t = new Timestamp(new Date().getTime());

        Calendar lCalendar = Calendar.getInstance();
        lCalendar.setTimeInMillis(t.getTime());
        if (eXPInMin) {
            lCalendar.set(Calendar.SECOND, 0);
            lCalendar.set(Calendar.MILLISECOND, 0);
        } else if (eXPInHr) {
            lCalendar.set(Calendar.MINUTE, 0);
            lCalendar.set(Calendar.SECOND, 0);
            lCalendar.set(Calendar.MILLISECOND, 0);

        } else if (eXPInDay) {
            lCalendar.set(Calendar.HOUR_OF_DAY, 0);
            lCalendar.set(Calendar.MINUTE, 0);
            lCalendar.set(Calendar.SECOND, 0);
            lCalendar.set(Calendar.MILLISECOND, 0);
        }
        Timestamp date = new Timestamp(lCalendar.getTimeInMillis());
        return date.toString();
    }
}
