package com.iexceed.plugins.dataencryption;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.Build;
import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ApzActivity;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import android.content.Context;
import android.util.Base64;
import android.webkit.WebView;

import static com.iexceed.common.StringUtils.CRYPTO_ALGORITHM;

public class EncryptDecryptUtility extends ApzPlugin{

	private String paddingMask = "$$$$$$$$$$$$$$$$";
	private static ApzPlugin pluginObj;
	static String TAG = "EncryptDecryptUtility";

	private EncryptDecryptUtility(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity){
		if(pluginObj == null){
			pluginObj = new EncryptDecryptUtility(webView, activity);
		}
		return pluginObj;
	}

	public static byte[] hmacSha1(String salt, String key) {
		SecretKeyFactory factory = null;
		Key keyByte = null;
		int keyLength=0;
		try {
			if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)) {
				factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				keyLength =128;

			}else{
				factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
				keyLength =256;
			}
			KeySpec keyspec = new PBEKeySpec(key.toCharArray(),salt.getBytes("UTF-8"), 2, keyLength);
			keyByte = factory.generateSecret(keyspec);
		} catch (NoSuchAlgorithmException e) {
			ApzLogger.e(TAG, e.toString());
		} catch (InvalidKeySpecException e) {
			ApzLogger.e(TAG, e.toString());
		} catch (UnsupportedEncodingException e) {
			ApzLogger.e(TAG, e.toString());
		}
		return keyByte.getEncoded();
	}

// encrypt
	public  static String encryptString(String key,String clearText, String salt) {
		SecretKeySpec skeySpec = null;
		skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
			byte[] iv = new byte[12];
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(iv);
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, parameterSpec);

			byte[] encryptedData = cipher.doFinal(clearText.getBytes("UTF-8"));
			byte[] encryptedDataWithIv = Base64.encode(ByteBuffer.allocate(iv.length + encryptedData.length)
					.put(iv)
					.put(encryptedData)
					.array(),Base64.NO_WRAP);

			if (encryptedDataWithIv == null)
				return null;

			return new String(encryptedDataWithIv);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}catch (Exception e) {

		}
		return null;
	}

	// decrypt
	
	public  static String decryptString( String key,String textToDecrypt, String salt) {
		SecretKeySpec skeySpec = null;
		skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");
		byte[] plainText;
		String plainrStr = null;
		try {

			byte[] ciphertext = Base64.decode(textToDecrypt, Base64.NO_WRAP | Base64.NO_PADDING);
			final Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
			//use first 12 bytes for iv
			AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, ciphertext, 0, 12);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, gcmIv);
			plainText = cipher.doFinal(ciphertext, 12, ciphertext.length - 12);
			 plainrStr = new String(plainText, "UTF-8");
			if (plainrStr == null)
				return null;
		}catch(Exception e){
	
			}
			return plainrStr;
	}
	public static boolean isEncryptDecryptUtility() {
		return true;
	}

	@Override
	public void execute(JSONObject params) {
		callbackId = "";
		String key = "";
		String stringToEncrypt = "";
		String stringToDecrypt = "";
		String action = "";
		JSONObject result;
		try {
			result = new JSONObject();
			callbackId = params.getString("id");
			key = params.getString("key").trim();
			action = params.getString("action");
			if(action.equalsIgnoreCase("ENCRYPT")){
				stringToEncrypt = params.getString("stringToEncrypt");
			}else if(action.equalsIgnoreCase("DECRYPT")){
				stringToDecrypt = params.getString("stringToDecrypt");
			}
			if (key.length() <= 16) {
				key += paddingMask.substring(0, 16 - key.length());
			}
			else {
				key = key.substring(0, 16);
			}

			byte[] iv = AppzillonUtils.getIV(key);
			String finalSalt = AppzillonUtils.getSalt(key);
			if(action.equals("ENCRYPT")){
				final String encryptedText = encryptString( key,stringToEncrypt, finalSalt);
				if (encryptedText != null) {
					result.put("encryptedString", encryptedText);
					ApzPluginUtil.sendSuccess(this.callbackId, result, false, activity, webView, true);
				} else {
					ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-048", null, activity, webView, true);//Encryption Failed
				}
			}else if(action.equalsIgnoreCase("DECRYPT")){
				final String decryptedString = EncryptDecryptUtility.decryptString( key,	stringToDecrypt, finalSalt);
				if (decryptedString != null) {
					result.put("decryptedString", decryptedString);
					ApzPluginUtil.sendSuccess(this.callbackId, result, false, activity, webView, true);
				}else{
					ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-046", null, activity, webView, true);//Encryption Failed
				}
			}
		} catch (JSONException e) {
			ApzLogger.e(TAG, e.toString());
			ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-077", null, activity, webView, true);
		}
	}

}
