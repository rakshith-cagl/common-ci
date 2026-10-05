package com.iexceed.plugins.filecrypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;

import com.iexceed.common.AppzillonUtils;
import com.iexceed.common.ApzActivity;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.appzillonapp.R;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.auditlog.AuditLog;
import com.iexceed.plugins.dataencryption.EncryptDecryptUtility;
import com.iexceed.plugins.errorlog.ApzLogger;

import static com.iexceed.common.StringUtils.CRYPTO_ALGORITHM;


public class FileCrypto extends ApzPlugin{

	private static String algorithm = "AES";
    
    static String TAG = "FileCrypto";
    
    
    String paddingMask = "$$$$$$$$$$$$$$$$";
    
    private static ApzPlugin pluginObj;
    
    private FileCrypto(WebView webView, ApzActivity activity) {
		super(webView, activity);
	}
    
	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new FileCrypto(webView, activity);
		}
		return pluginObj;
	}

	// encryption method
		public void encryptFile(String key, String salt,byte[] iv, JSONObject jsonObj) {	
			
//			AuditLog.makeString("FILECRYPTO","encryptString");
			String appSandboxLoc = AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC;
			String srcFilePath = "";
			String encDirPath = "";
			String desFolder = "";
			try {
				callbackId = jsonObj.getString("id");
				srcFilePath = jsonObj.getString("srcFilePath");
				encDirPath = jsonObj.getString("destFilePath");

				if(encDirPath.contains("/")){
					int index = encDirPath.lastIndexOf("/");
					desFolder = (String) encDirPath.subSequence(0, index);
					if(!desFolder.contains(AppzillonMainScreen.SANDBOX_LOC)){
						desFolder = appSandboxLoc+desFolder;
						File destDir = new File(desFolder);
						if(!destDir.exists()){
							destDir.mkdir();
						}
					}

				}
			} catch (final JSONException e) {
				ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-077", null, activity, webView, true);
				
			}
			//Abhishek For bug id 4275, added seperator
			if(!srcFilePath.contains(AppzillonMainScreen.SANDBOX_LOC)){
				srcFilePath = appSandboxLoc+srcFilePath;
			}
			if(!encDirPath.contains(AppzillonMainScreen.SANDBOX_LOC)){
				encDirPath = appSandboxLoc+encDirPath;
			}

			SecretKeySpec skeySpec = new SecretKeySpec(EncryptDecryptUtility.hmacSha1(salt, key), algorithm);



	        
//	        String clearText=readFromFile(sourceFilePath);
	        byte[] clearText=readFromFileToByteArray(srcFilePath);
	        if(clearText != null){
	        	
	        	try {
				       Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
					 iv = new byte[12];
					SecureRandom secureRandom = new SecureRandom();
					secureRandom.nextBytes(iv);
					GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
					cipher.init(Cipher.ENCRYPT_MODE, skeySpec, parameterSpec);

					byte[] encryptedData = cipher.doFinal(clearText);
					String data = new String(Base64.encode(ByteBuffer.allocate(iv.length + encryptedData.length)
							.put(iv)
							.put(encryptedData)
							.array(),Base64.NO_WRAP));

					writeToEncryptedFile(encDirPath,data);
					final String path = encDirPath;
					ApzLogger.i(TAG, "File encrypted ");
					JSONObject result = null;
					try{
						result = new JSONObject();
						result.put("filePath", path);
					}catch(JSONException ex){
						ApzLogger.e(TAG, ex.toString());
					}
					ApzPluginUtil.sendSuccess(this.callbackId, result, false, this.activity, this.webView, true);
				} catch (final Exception e) {
					ApzLogger.i(TAG, "File encrypted exception "+e);
					ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-209", null, this.activity, this.webView, true);//File not found
				}
	        	
	        }else{
	        	ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-002", null, this.activity, this.webView, true);//File Not Found
	        }			
		
		}
		
			
		// decryption method
		public void decryptFile(String key,String salt,byte[] iv, JSONObject jsonObj) {		
//			AuditLog.makeString("FILECRYPTO","decryptString");
			String appSandboxLoc = AppzillonMainScreen.SANDBOX_LOC+File.separator+AppzillonMainScreen.ASSET_APP_LOC;
			String encDirPath = "";
			String decDirPath = "";
			String desFolder;
			try {
				callbackId = jsonObj.getString("id");
				encDirPath = jsonObj.getString("srcFilePath");
				decDirPath = jsonObj.getString("destFilePath");

				if(decDirPath.contains("/")){
					int index = decDirPath.lastIndexOf("/");
					desFolder = (String) decDirPath.subSequence(0, index);
					if(!desFolder.contains(AppzillonMainScreen.SANDBOX_LOC)){
						desFolder = appSandboxLoc+desFolder;
						File destDir = new File(desFolder);
						if(!destDir.exists()){
							destDir.mkdir();
						}
					}

				}
			} catch (final JSONException e) {
				ApzLogger.i(TAG, e.toString());
				ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-077", null, this.activity, this.webView, true);
				
			}
			
			if(!encDirPath.contains(AppzillonMainScreen.SANDBOX_LOC)){
				encDirPath = appSandboxLoc+encDirPath;
			}
			if(!decDirPath.contains(AppzillonMainScreen.SANDBOX_LOC)){
				decDirPath = appSandboxLoc+decDirPath;
			}
			String textToDecrypt = readFromFile(encDirPath);
			
			if(textToDecrypt.length()>1){
				
				SecretKeySpec skeySpec = new SecretKeySpec(EncryptDecryptUtility.hmacSha1(salt, key), algorithm);
				try {
					byte[] ciphertext = Base64.decode(textToDecrypt, Base64.NO_WRAP | Base64.NO_PADDING);
					final Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
					//use first 12 bytes for iv
					AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, ciphertext, 0, 12);
					cipher.init(Cipher.DECRYPT_MODE, skeySpec, gcmIv);
					byte[] plaintext = cipher.doFinal(ciphertext, 12, ciphertext.length - 12);
					writeToDecyFile(decDirPath,plaintext);
					ApzLogger.i(TAG, "File decrypted ");
					final String path = decDirPath; AuditLog.makeString("FILECRYPTO","Success");
					JSONObject result = null;
					try{
						result = new JSONObject();
						result.put("filePath", path);
					}catch(Exception ex){
						ApzLogger.i(TAG, ex.toString());
					}
					ApzPluginUtil.sendSuccess(this.callbackId, result, false, this.activity, this.webView, true);

				} catch (final Exception e) {
					ApzLogger.i(TAG, e.toString());			
					ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-210", null, this.activity, this.webView, true);
				}
			}else{
				String message = "" ;
				
				if(textToDecrypt.equalsIgnoreCase("0")){
					message = activity.getApplicationContext().getResources().getString(R.string.file_notfound);
					ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-002", null, this.activity, this.webView, true);//File Not Found

				}else if(textToDecrypt.equalsIgnoreCase("1")){
					message = activity.getApplicationContext().getResources().getString(R.string.cannot_read_file);
					ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-008", null, this.activity, this.webView, true);//Cannot Read the File

				}
			}					
		}		
		
		private void writeToDecyFile(String filePath, byte[] plaintext) {
//			AuditLog.makeString("FILECRYPTO","writeToDecyFile");
			File file = new File(filePath);
			
			String path = file.getAbsolutePath();
			String fileExtension = null;
			int dotPos = path.lastIndexOf('.');
			if (0 <= dotPos) {
			    fileExtension = path.substring(dotPos + 1);
			}
			
			if (!TextUtils.isEmpty(fileExtension )) {
				
			    String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
			    if (!TextUtils.isEmpty(mime)) {
			        if (mime.startsWith("image")) {
			        // IMAGE FILE
			        	Bitmap bmp = BitmapFactory.decodeByteArray(plaintext, 0, plaintext.length);
				        try {				        	
				            FileOutputStream out = new FileOutputStream(file);
				            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
				            out.flush();
				            out.close();

				     } catch (Exception e) {
				    	 ApzLogger.i(TAG, e.toString());
				     }
			        }else{
			        	try {
			            	FileOutputStream os = new FileOutputStream(file, false);
							os.write(plaintext);
				            os.flush();
				            os.close();
						} catch (FileNotFoundException e) {						
							ApzLogger.i(TAG, e.toString());
						} catch (IOException e) {
							ApzLogger.i(TAG, e.toString());
						}
			        }
			    }
			} 
			
		}
		
		private static String readFromFile(String filename) {
//			AuditLog.makeString("FILECRYPTO","readFromFile");
		    String ret = "";

		    try {
		    	FileInputStream inputStream = new FileInputStream(filename);

		        if ( inputStream != null ) {
		            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		            String receiveString = "";
		            StringBuilder stringBuilder = new StringBuilder();

		            while ( (receiveString = bufferedReader.readLine()) != null ) {
		                stringBuilder.append(receiveString);
		            }

		            inputStream.close();
		            ret = stringBuilder.toString();
		        }
		    }
		    catch (FileNotFoundException e) {
		        ApzLogger.e(TAG, "File not found: " + e.toString());
		        return "0";
		    } catch (IOException e) {
		        ApzLogger.e(TAG, "Can not read file: " + e.toString());
		        return "1";
		    }

		    return ret;
		}
		
		private byte[] readFromFileToByteArray(String fileName){
//			AuditLog.makeString("FILECRYPTO","readFromFileToByteArray");
			FileInputStream fileInputStream=null;
			 
	        File file = new File(fileName);
	 
	        byte[] bFile = new byte[(int) file.length()];
	 
	        try {
	            //convert file into array of bytes
		    fileInputStream = new FileInputStream(file);
		    fileInputStream.read(bFile);
		    fileInputStream.close();
	 
		   // for (int i = 0; i < bFile.length; i++) {
//		       	System.out.print((char)bFile[i]);
	            //}
	 
		   // System.out.println("Done");
	        }catch(Exception e){
	        	ApzLogger.i(TAG, e.toString());
	        	return null;
	        }
			return bFile;
			
		}
		
		private static void writeToEncryptedFile(String fileName,String encryptedData) {
//			AuditLog.makeString("FILECRYPTO","writeToEncryptedFile");
		    try {
		    	File newFile = new File(fileName);
		    	FileOutputStream fos = new FileOutputStream(newFile);
		        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
		        outputStreamWriter.write(encryptedData);
		        outputStreamWriter.close();

		    }
		    catch (IOException e) {
		        ApzLogger.e(TAG, "File write failed: " + e.toString());
		    } 
		}

		public static boolean isFileCryptoPlugin() {
			return true;
		}


		@Override
		public void execute(JSONObject params) {
			String key = "";
			String action = "";
			try{
			callbackId = params.getString("id");
			key = params.getString("key");
			action = params.getString("action");
			}catch(Exception ex){
				ApzPluginUtil.sendError(this.callbackId, "APZ-CNT-077", null, this.activity, this.webView, true);
			}
			if (key.length() <= 16) {
				key += paddingMask.substring(0, 16 - key.length());
			}
			//Abhishek 24 April 2015, if key is greater then 16 make it of 16 length, check Nagaraj mail START
			else {
				key = key.substring(0, 16);
			}
			byte[] iv = new byte[12];
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(iv);
			String finalSalt = AppzillonUtils.getSalt(key);
			if(action.equals("ENCRYPT")){
				encryptFile(key, finalSalt, iv, params);
			}else if(action.equals("DECRYPT")){
				decryptFile(key, finalSalt,iv, params);
			}
		}
}
