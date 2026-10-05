package com.iexceed.webcontainer.utils;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.plugins.CryptoPlugin;
import com.iexceed.webcontainer.utils.hash.Utility;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AppzillonAESUtils {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(AppzillonAESUtils.class.getName());

    private AppzillonAESUtils(){

    }
    public static byte[] hmacSha1(String salt, String key) {
        SecretKeyFactory factory = null;
        KeySpec keyspec;
        try {
            if(Utility.isNotNullOrEmpty(WebProperties.getSafeBit()) &&
                    Integer.parseInt(WebProperties.getSafeBit()) ==1){
                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                keyspec = new PBEKeySpec(key.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 2, 256);
            }else {
                factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                keyspec = new PBEKeySpec(key.toCharArray(),
                        salt.getBytes(StandardCharsets.UTF_8), 2, 128);
            }
            return factory.generateSecret(keyspec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOG.error(" error while hmacSha1  "+e);
        }
        return new byte[0];
    }
    public static String getDecryptedPayload(String key, String cipherText) {
        String decryptedAppzillonBody = "";
        try{
            key = getPaddedKey(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(hmacSha1(getSalt(key), key), AppzillonConstants.ALGORITHM);
            decryptedAppzillonBody = CryptoPlugin.decryptUsingAesGcm(secretKeySpec, cipherText);
            return decryptedAppzillonBody;
        }catch (Exception e){
            LOG.error("Exception during decryption: ",e);
        }
        return decryptedAppzillonBody;
    }

    public static String getEncryptPayload(String key, String plainText) {
        String encryptedAppzillonBody = "";
        try {
            key = getPaddedKey(key);
            String finalSalt = getSalt(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(hmacSha1(finalSalt, key), AppzillonConstants.ALGORITHM);
            encryptedAppzillonBody =  CryptoPlugin.encryptUsingAesGcm(secretKeySpec, plainText);
        }catch (Exception e){
            LOG.error("Exception during encryption: ",e);
        }
        return encryptedAppzillonBody;
    }
    
    public static String getDecryptedServerUrl(String key, String cipherText) {
    	String decryptedAppzillonBody = "";
        try{
            key = getPaddedKey(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(serverUrlHmacSha1(getSalt(key), key), AppzillonConstants.ALGORITHM);
            decryptedAppzillonBody = CryptoPlugin.decryptUsingAesGcm(secretKeySpec, cipherText);
            return decryptedAppzillonBody;
        }catch (Exception e){
            LOG.error("Exception during decryption: ",e);
        }
        return decryptedAppzillonBody;
    }
    
	public static byte[] serverUrlHmacSha1(String salt, String key) {
		SecretKeyFactory factory = null;
		KeySpec keyspec;
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			keyspec = new PBEKeySpec(key.toCharArray(), salt.getBytes(StandardCharsets.UTF_8), 2, 128);
			return factory.generateSecret(keyspec).getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			LOG.error(" error while hmacSha1  " + e);
		}
		return new byte[0];
	}

    public static String getPaddedKey(String pKey) {
        String lKey = pKey;
        String paddingMask = "$$$$$$$$$$$$$$$$";
        if (lKey.length() <= 16) {
            lKey += paddingMask.substring(0, 16 - lKey.length());
        }
        if (lKey.length() > 16) {
            lKey = lKey.substring(0, 16);
        }
        return lKey;
    }


    public static String getSalt(String key) {

        char[]c = key.toCharArray();

        // Replace with a "swap" function, if desired:
        char temp = c[0];
        c[0] = c[1];
        c[1] = temp;

        temp = c[c.length - 1];
        c[c.length - 1] = c[c.length - 2];
        c[c.length - 2] = temp;
        return new String(c);
    }
}
