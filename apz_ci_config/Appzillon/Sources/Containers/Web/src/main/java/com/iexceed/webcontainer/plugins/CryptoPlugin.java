package com.iexceed.webcontainer.plugins;

import static com.iexceed.webcontainer.utils.AppzillonAESUtils.hmacSha1;
import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpSession;

import com.iexceed.webcontainer.utils.AppzillonAESUtils;
import com.iexceed.webcontainer.utils.AppzillonConstants;
import org.apache.commons.codec.binary.Base64;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;

import com.iexceed.webcontainer.utils.hash.Utility;
import com.iexceed.webcontainer.utils.json.JSONException;
import com.iexceed.webcontainer.utils.json.JSONObject;

public class CryptoPlugin {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(CryptoPlugin.class.getName());
	private static final int GCM_IV_LENGTH = 12;
	private static final String AES = "AES";
	private static final String AES_GCM = "AES/GCM/NoPadding";
	private CryptoPlugin() {
	}

	public static String process(int request, String key, String reqText) throws Exception {
		LOG.debug("Inside encrypt/decrypt data process");
		String result = "";
		key = AppzillonAESUtils.getPaddedKey(key);
		String finalSalt = AppzillonAESUtils.getSalt(key);
		SecretKeySpec secretKeySpec = new SecretKeySpec(hmacSha1(finalSalt, key), AppzillonConstants.ALGORITHM);

		LOG.debug("checking for request sent encrypt/decrypt");

		switch (request) {
			case ENCRYPT_REQ:
				result = encryptUsingAesGcm(secretKeySpec,reqText);
				break;
			case DECRYPT_REQ:
				result = decryptUsingAesGcm(secretKeySpec,reqText);
				break;
			default:
				break;
		}
		return result;
	}

	public static String encryptResponse(HttpServletRequest request, String responseJson) {
		LOG.debug("-----------------------------Encrypting--------------------");
		String encryptedString = "";
		String appzillonHeader = "";
		String appzillonBody = "";
		String secretKey = "";
		try {

			HttpSession session = request.getSession(false);
			if(session != null){
				secretKey = (String) session.getAttribute(EXCHANGE);
			}else{
				LOG.debug("As session doesn't exist, hence final response being sent");
				secretKey = Utility.generateSecRandomOfLength(16);
			}
			JSONObject payload = new JSONObject(responseJson);
			if(payload.has(APPZILLON_HEADER)) {
				LOG.debug("Encrypting appzillonHeader : " + appzillonHeader);
				appzillonHeader = payload.getString(APPZILLON_HEADER);
				appzillonHeader = encryptJsPayload(secretKey,appzillonHeader);
				LOG.debug("Encrypted appzillonHeader : " + appzillonHeader);
			}

			if(payload.has(APPZILLON_BODY)) {
				LOG.debug("Encrypting appzillonBody : " + appzillonBody);
				appzillonBody = payload.getString(APPZILLON_BODY);
				appzillonBody = encryptJsPayload(secretKey,appzillonBody);
				LOG.debug("Encrypted appzillonBody : " + appzillonBody);
			}

			payload.put(APPZILLON_HEADER, appzillonHeader);
			payload.put(APPZILLON_BODY, appzillonBody);
			payload.put(APPZILLON_SAFE, "");
			if(payload.has(APPZILLON_ERRORS)){
				String errors = payload.getJSONArray(APPZILLON_ERRORS).toString();
				LOG.debug("Encrypting appzillonErrors : " + errors);
				String encryptedError = encryptJsPayload(secretKey, errors);
				LOG.debug("Encrypted appzillonErrors : " + encryptedError);
				payload.put(APPZILLON_ERRORS, encryptedError);
				if(session == null){
					payload.put(EXCHANGE, secretKey);
				}
			}
			encryptedString = payload.toString();
		} catch (JSONException e) {
			LOG.error("JSONException Occurred!!! ", e);
		}
		LOG.debug("Transitted Encrypted Data -:" + encryptedString);
		LOG.debug("-----------------------------Encrypted--------------------");
		return encryptedString;
	}

	public static String decryptRequest(HttpServletRequest request, String encryptedPayload) {
		LOG.debug("-----------------------------Decrypting--------------------");
		String decryptedString = "";
		try {
			LOG.debug("Incoming CipherText :" + encryptedPayload);
			String secretKey = (String) request.getSession(false).getAttribute(EXCHANGE);

			JSONObject payload = new JSONObject(encryptedPayload);
			String appzillonHeader = payload.getString(APPZILLON_HEADER);
			LOG.debug("Decrypting appzillonHeader : " + appzillonHeader);
			String appzillonBody = payload.getString(APPZILLON_BODY);
			LOG.debug("Decrypting appzillonBody : " + appzillonBody);

			appzillonHeader = decryptJsPayload(secretKey,appzillonHeader);
			appzillonBody = decryptJsPayload(secretKey,appzillonBody);

			LOG.debug("Decrypted appzillonHeader : " + appzillonHeader);

			LOG.debug("Decrypted appzillonBody : " + appzillonBody);

			if (!FAILURE.equalsIgnoreCase(appzillonHeader) || !FAILURE.equalsIgnoreCase(appzillonBody)) {
				payload.put(APPZILLON_HEADER, new JSONObject(appzillonHeader));
				payload.put(APPZILLON_BODY, new JSONObject(appzillonBody));
			}
			LOG.debug("Removing appzillonsafe from the request ...");
			payload.remove(APPZILLON_SAFE);
			decryptedString = payload.toString();
			if (FAILURE.equalsIgnoreCase(appzillonHeader) || FAILURE.equalsIgnoreCase(appzillonBody)) {
				decryptedString = Utility.invalidPayloadResponse();
			}
		} catch (Exception e) {
			LOG.error("Exception Occurred during decryption!!! ", e);
			decryptedString = Utility.invalidPayloadResponse();
		}
		LOG.debug("Transited Decrypted data : " + decryptedString);
		LOG.debug("-----------------------------Decrypted--------------------");
		return decryptedString;
	}

	public static String decryptJsPayload(String secretKey, String cipherText) throws Exception{
		String plainText;
		String finalSecretKey = getKey(secretKey);
		byte[] keyToBeUsed = Arrays.copyOfRange(finalSecretKey.getBytes(), 0, 16);
		SecretKeySpec key = new SecretKeySpec(keyToBeUsed, AES);
		plainText = decryptUsingAesGcmJS(key, cipherText, null);
		return plainText;
	}

	private static String decryptUsingAesGcmJS(SecretKeySpec secretKeySpec, String cipherText, byte[] associatedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		byte[] cipherT = cipherText.getBytes(StandardCharsets.UTF_8);
		byte[] iv = Arrays.copyOfRange(cipherT,0,24);
		byte[] inputText = Base64.decodeBase64(Arrays.copyOfRange(cipherT,24,cipherT.length));

		final Cipher decryptCipher = Cipher.getInstance(AES_GCM);
		decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new GCMParameterSpec(128, Utility.hexToBytes(new String(iv))));
		if (associatedData != null) {
			decryptCipher.updateAAD(associatedData);
		}
		return new String(decryptCipher.doFinal(inputText));
	}
	public static String encryptJsPayload(String secretKey, String plainText){
		String cipherText;
		try{
			String finalSecretKey = getKey(secretKey);
			SecretKeySpec key = new SecretKeySpec(finalSecretKey.getBytes(), AES);
			cipherText = encryptUsingAesGcmJS(key,plainText,null);
			return cipherText;
		}catch (Exception e){
			LOG.error("Exception during encryption: ",e);
			cipherText = FAILURE;
		}
		return cipherText;

	}

	private static String encryptUsingAesGcmJS(SecretKeySpec secretKeySpec, String plainText, byte[] associatedData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		String cipherText;
		byte[] iv = new byte[GCM_IV_LENGTH];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(iv);
		final Cipher cipher = Cipher.getInstance(AES_GCM);
		GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec);

		if (associatedData != null) {
			cipher.updateAAD(associatedData);
		}

		byte[] cipherTextByteArray = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
		cipherText = Utility.bytesToHex(iv) + new String(Base64.encodeBase64(cipherTextByteArray));
		return cipherText;
	}

	public static String encryptUsingAesGcm(SecretKeySpec secretKey, String plaintext) throws Exception{
		byte[] iv = generateIv(GCM_IV_LENGTH);
		final Cipher cipher = Cipher.getInstance(AES_GCM);
		//GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

		byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

		byte[] cipherTextWithIv = ByteBuffer.allocate(iv.length + cipherText.length)
				.put(iv)
				.put(cipherText)
				.array();
		return Base64.encodeBase64String(cipherTextWithIv);
	}

	public static String decryptUsingAesGcm(SecretKey secretKeySpec,String encryptedText) throws Exception{
		byte[] cipherText = Base64.decodeBase64(encryptedText.getBytes(StandardCharsets.UTF_8));

		final Cipher cipher = Cipher.getInstance(AES_GCM);
		//use first 12 bytes for iv
		//AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, cipherText, 0, GCM_IV_LENGTH);
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new GCMParameterSpec(128, cipherText, 0, GCM_IV_LENGTH));

		//use everything from 12 bytes on as ciphertext
		byte[] plainText = cipher.doFinal(cipherText, GCM_IV_LENGTH, cipherText.length - GCM_IV_LENGTH);
		return new String(plainText, StandardCharsets.UTF_8);
	}

	private static byte[] generateIv(int length) {
		byte[] iv = new byte[length];
		new SecureRandom().nextBytes(iv);
		return iv;
	}

	private static byte[] getSalt(String key) {
		LOG.debug("get Salt value in bytes");
		byte[] result;
		if (key.length() >= 2) {
			char char1 = key.charAt(0);
			char char2 = key.charAt(1);
			char[] charArr = key.toCharArray();
			charArr[0] = char2;
			charArr[1] = char1;
			if (key.length() >= 4) {
				char char3 = key.charAt(key.length() - 1);
				char char4 = key.charAt(key.length() - 2);
				charArr[key.length() - 1] = char4;
				charArr[key.length() - 2] = char3;
			}
			String temp = new String(charArr);
			LOG.debug(SALT + temp);
			result = temp.getBytes(StandardCharsets.UTF_8);
		} else {
			LOG.debug(SALT + key);
			result = key.getBytes(StandardCharsets.UTF_8);
		}
		return result;
	}

	private static String getKey(String key) {
		LOG.debug("get Key value");
		String result;
		if (key.length() < 16) {
			StringBuilder temp = new StringBuilder(key);
			for (int i = key.length(); i < 16; i++) {
				temp.append(DOLLAR);
			}

			result = temp.toString();
		} else if (key.length() > 16) {
			result = key.substring(0, 16);
		} else {

			result = key;
		}
		LOG.debug("Key: " + result);
		return result;
	}
}
