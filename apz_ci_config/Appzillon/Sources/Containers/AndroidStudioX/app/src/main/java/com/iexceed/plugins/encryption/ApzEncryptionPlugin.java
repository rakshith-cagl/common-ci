package com.iexceed.plugins.encryption;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.util.Base64;
import android.os.Build;
import android.webkit.WebView;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.common.ApzActivity;
import com.iexceed.common.StringUtils;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;
import com.iexceed.security.HashXor;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import static com.iexceed.common.StringUtils.CRYPTO_ALGORITHM;


public class ApzEncryptionPlugin extends ApzPlugin {

	private static ApzPlugin pluginObj;
	public static final String paddingMask = "$$$$$$$$$$$$$$$$";
	private final static char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8','9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static String TAG = "ApzEncryptionPlugin";
	private static boolean decryptionIDEToken = false;

	private ApzEncryptionPlugin(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
	
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity)
	{  if(pluginObj == null){ 
		pluginObj = new ApzEncryptionPlugin(webView, activity); 
	}  
	return pluginObj; 
	}

	@Override
	public void execute(JSONObject params) {
		
		try {
			switch ((String)params.get("command")) {
				case ApzPlugin.PLGN_ENCRPT_DATA:
					this.encript(params);
					break;
				case ApzPlugin.PLGN_DECRPT_DATA:
					this.decript(params);
					break;
				case ApzPlugin.PLGN_HASH_PWD:
					this.hashPwd(params);
					break;
				case ApzPlugin.APZ_PLUGIN_HASH_SHA256:
					this.hashSHA256(params);
					break;
			}
		} catch (JSONException e) {
			ApzLogger.e(TAG,e.toString());
			try{
				final JSONObject eJson = new JSONObject();
				eJson.put("errorCode", "");
				eJson.put("errorMessage", "Error in Encrypt Plugin.");
				ApzPluginUtil.sendError(this.callbackId, "",
						eJson, this.activity,
						this.webView, true);
			}
			catch(Exception ie){
				ApzLogger.e(TAG,ie.toString());
			}
		}

	}
	
	private void encript(JSONObject params){
		try {
			this.callbackId = params.getString("id");
			
			String stringToEncrypt = null;
			String key = null;
			
				
			key = params.getString("key").trim();
			stringToEncrypt = params.getString("stringToEncrypt").trim();				
		
			
			if (key.length() <= 16) {
				key += paddingMask.substring(0, 16 - key.length());
			}
			else {
				key = key.substring(0, 16);
			}
			ApzLogger.i(TAG, "Key length : "+key.length());
			
			byte[] iv = getIV(key);
			String finalSalt = getSalt(key);
			final String encryptedText = encryptString( key,stringToEncrypt, finalSalt, iv);
			
			final JSONObject successJson = new JSONObject();					
			successJson.put("text", encryptedText);
			ApzPluginUtil.sendSuccess(this.callbackId,
					successJson, false, this.activity,
					this.webView, true);
			
			
		} catch (Exception e) {
			ApzLogger.e(TAG,e.toString());
			try{
				final JSONObject eJson = new JSONObject();
				eJson.put("errorCode", "");
				eJson.put("errorMessage", "Unable to Encript.");
				ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-048",
						eJson, this.activity,
						this.webView, true);
			}
			catch(Exception ie){
				ApzLogger.e(TAG,ie.toString());
			}
		}
	}


	public static String encryptPassword(String key,String stringToEncrypt) {
		String encryptedText = "";
		try {

			byte[] iv = new byte[12];
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(iv);
			String finalSalt = getSalt(key);
			encryptedText = encryptString( key, stringToEncrypt, finalSalt, iv);


		} catch (Exception e) {
			ApzLogger.e(TAG, e.toString());
		}

		return encryptedText;
	}
	
	private void decript(JSONObject params){
		try {
			this.callbackId = params.getString("id");
			
			String stringToDecrypt = null;
			String key = null;
			
				
			key = params.getString("key").trim();
			stringToDecrypt = params.getString("stringToDecrypt").trim();				
		
			
			if (key.length() <= 16) {
				key += paddingMask.substring(0, 16 - key.length());
			}
			else {
				key = key.substring(0, 16);
			}
			ApzLogger.i(TAG, "Key length : "+key.length());

			byte[] iv = getIV(key);
			String finalSalt = getSalt(key);
			final String decryptedString = decryptString( key,	stringToDecrypt, finalSalt, iv);
			
			final JSONObject successJson = new JSONObject();					
			successJson.put("text", decryptedString);
			ApzPluginUtil.sendSuccess(this.callbackId,
					successJson, false, this.activity,
					this.webView, true);
			
			
		} catch (Exception e) {
			ApzLogger.e(TAG,e.toString());
			try{
				final JSONObject eJson = new JSONObject();
				eJson.put("errorCode", "");
				eJson.put("errorMessage", "Unable to Decript.");
				ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-046",
						eJson, this.activity,
						this.webView, true);
			}
			catch(Exception ie){
				ApzLogger.e(TAG,ie.toString());
			}
		}
	}
	
	
	private void hashPwd(JSONObject params){
		try {
			this.callbackId = params.getString("id");
			
			ApzLogger.d(TAG, "getOTP");
			SharedPreferences settings = activity.getApplicationContext().getSharedPreferences(properties, 0);

			String hashPin = null;
			
			String userId = params.getString("userId").trim();	;
			String pin =  params.getString("pwd").trim();	;
			String timeStamp = params.getString("date").trim();	;
			
			hashPin = hashSHA256(pin, userId + AppzillonMainScreen.stringUtils.getString(StringUtils.SERVER_TOKEN));
//			ApzLogger.i(TAG,"userDetails: PIN : " + pin + ",USER ID : " + userId + ",SERVER TOKEN : "+ getServerToken() + ", LOGIN HASH : " + hashPin);
			
			final HashXor hashXor = new HashXor();
//			ApzLogger.d(TAG, "HASHKEY 1 : "+UserSettings.getIMEI(settings));
//			ApzLogger.d(TAG, "HASHKEY 2 : "+UserSettings.getIMSI(settings));
			final String OTP = hashXor.hashValue("","","", userId, hashPin, timeStamp);
//			ApzLogger.d("OTP", OTP);

			final JSONObject successJson = new JSONObject();
			successJson.put("text", OTP);
			ApzPluginUtil.sendSuccess(this.callbackId,
					successJson, false, this.activity,
					this.webView, true);
			
			
		} catch (Exception e) {
			ApzLogger.e(TAG,e.toString());
			try{
				final JSONObject eJson = new JSONObject();
				eJson.put("errorCode", "");
				eJson.put("errorMessage", "Unable to Encript.");
				ApzPluginUtil.sendError(this.callbackId, "",
						eJson, this.activity,
						this.webView, true);
			}
			catch(Exception ie){
				ApzLogger.e(TAG,ie.toString());
			}
		}
	}

	public static String hashPwdforNativeLogin(JSONObject params){

		String OTP = "";
		try {
			ApzLogger.d("ApzEncryptionPlugin", "getOTP");
			//SharedPreferences settings = activity.getApplicationContext().getSharedPreferences(properties, 0);

			String hashPin = null;

			String userId = params.getString("userId").trim();	;
			String pin =  params.getString("pwd").trim();	;
			String timeStamp = params.getString("date").trim();	;

			hashPin = hashSHA256(pin, userId + AppzillonMainScreen.stringUtils.getString(StringUtils.SERVER_TOKEN));
//			ApzLogger.i(TAG,"userDetails: PIN : " + pin + ",USER ID : " + userId + ",SERVER TOKEN : "+ getServerToken() + ", LOGIN HASH : " + hashPin);

			final HashXor hashXor = new HashXor();
//			ApzLogger.d(TAG, "HASHKEY 1 : "+UserSettings.getIMEI(settings));
//			ApzLogger.d(TAG, "HASHKEY 2 : "+UserSettings.getIMSI(settings));
			OTP = hashXor.hashValue("","","", userId, hashPin, timeStamp);
//			ApzLogger.d("OTP", OTP);



			/*final JSONObject successJson = new JSONObject();
			successJson.put("text", OTP);
			ApzPluginUtil.sendSuccess(this.callbackId,
					successJson, false, this.activity,
					this.webView, true);*/


		} catch (Exception e) {
			/*ApzLogger.e("",e.toString());
			try{
				final JSONObject eJson = new JSONObject();
				eJson.put("errorCode", "");
				eJson.put("errorMessage", "Unable to Encript.");
				ApzPluginUtil.sendError(this.callbackId, "",
						eJson, this.activity,
						this.webView, true);*/
			/*}
			catch(Exception ie){
				//ApzLogger.e(TAG,ie.toString());
			}*/
		}

		return OTP;
	}
	
	
	private void hashSHA256(JSONObject params){
		try {
			this.callbackId = params.getString("id");
			
			String text = params.getString("text").trim();
			String salt =  params.getString("salt").trim();
			
			final String hashValue = this.hashSHA256(text, salt);
			
			final JSONObject successJson = new JSONObject();					
			successJson.put("text", hashValue);
			ApzPluginUtil.sendSuccess(this.callbackId,
					successJson, false, this.activity,
					this.webView, true);
			
			
		} catch (Exception e) {
			ApzLogger.e(TAG,e.toString());
			try{
				final JSONObject eJson = new JSONObject();
				eJson.put("errorCode", "");
				eJson.put("errorMessage", "Unable to Decript.");
				ApzPluginUtil.sendError(this.callbackId, "",
						eJson, this.activity,
						this.webView, true);
			}
			catch(Exception ie){
				ApzLogger.e(TAG,ie.toString());
			}
		}
	}
	
	
	/**
	 * Prepares the salt based on key
	 *
	 * @param key
	 * @return
	 */
	private static String getSalt(String key) {
		String originalString = key;

		char[] c = originalString.toCharArray();

		// Replace with a "swap" function, if desired:
		char temp = c[0];
		c[0] = c[1];
		c[1] = temp;

		temp = c[c.length - 1];
		c[c.length - 1] = c[c.length - 2];
		c[c.length - 2] = temp;
		String swappedString = new String(c);
		return swappedString;
	}

	/**
	 * prepares the IV from key
	 * 
	 * @param key
	 * @return
	 */
	private static byte[] getIV(String key) {
		byte[] iv = new byte[16];
		java.util.Arrays.fill(iv, (byte) 0);
		StringBuffer or = new StringBuffer(key);
		String nw = or.reverse().toString();
		byte[] keyBytes = null;
		try {
			keyBytes = nw.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			
		}
		byte[] rawIV = new byte[keyBytes.length];
		for (int i = 0; i < keyBytes.length; i++) {
			rawIV[i] = (byte) (keyBytes[i] >> 1);
		}
		for (int i = 0; i < iv.length; i++) {
			iv[i] = rawIV[i];
		}
		return iv;
	}
	
	/*private static String getServerToken() {
		
		String servertoken = StringUtils.getString(StringUtils.SERVER_TOKEN);
		return servertoken;
	}*/
	
	private static String hashSHA256(String ptext, String psalt) throws NoSuchAlgorithmException {
		String pTextSalt = ptext + psalt;
		String pHashedText = "";
		byte[] ptextSaltbyte = new byte[200];
		byte[] hashbyte = new byte[200];
		MessageDigest msgdigest = MessageDigest.getInstance("SHA-256");
		try {
			ptextSaltbyte = pTextSalt.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			ApzLogger.e("ApzEncryptionPlugin","Unsupported character set");
		}
		msgdigest.reset();
		msgdigest.update(ptextSaltbyte);
		hashbyte = msgdigest.digest();
		pHashedText = toHexString(hashbyte);
//		ApzLogger.i(TAG,"EncryptHash : "+ pHashedText);
		return pHashedText;
	}
	
	private static String toHexString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			int c = ((b[i]) >>> 4) & 0xf;
			sb.append(hex[c]);
			c = (b[i] & 0xf);
			sb.append(hex[c]);
		}
		return sb.toString();
	}

	// encrypt
	private static String encryptString( String key,String clearText, String salt,byte[] iv) {
			SecretKeySpec skeySpec = null;
		skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
			 iv = new byte[12];
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(iv);
			GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, parameterSpec);

			byte[] encryptedData = cipher.doFinal(clearText.getBytes("UTF-8"));
			byte[] encryptedDataWithIv = Base64.encode(ByteBuffer.allocate(iv.length + encryptedData.length)
					.put(iv)
					.put(encryptedData)
					.array(), Base64.NO_WRAP);
			return new String(encryptedDataWithIv);
			/*if (encryptedDataWithIv == null) {
				return null;
			}*/
		}catch (Exception e){

		}
		return null;
	}

	// decrypt

    private  static String decryptString( String key,String textToDecrypt, String salt,byte[] iv) {
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
				if(decryptionIDEToken){
					keyLength =128;
					decryptionIDEToken=false;
				}else {
					keyLength = 256;
				}
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


	public static String decryptPassword(String key, String stringToDecrypt){
		decryptionIDEToken=true;
		if (key.length() <= 16) {
			key += paddingMask.substring(0, 16 - key.length());
		}
		else {
			key = key.substring(0, 16);
		}
		ApzLogger.i(TAG, "Key length : "+key.length());

		byte[] iv = getIV(key);
		String finalSalt = getSalt(key);
		String decryptedString = decryptString(key,	stringToDecrypt, finalSalt, iv);
		return decryptedString;
	}

}


