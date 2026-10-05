package com.iexceed.plugins.dataSecurity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.errorlog.ApzLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


@TargetApi(23)
public class BiometricAuth {

	private static final String KEY_NAME = "appzillon";
	private KeyguardManager keyguardManager;
	private KeyStore keyStore;
	private KeyGenerator keyGenerator;
	public static BiometricManager biometricManager;
	private Cipher cipher;
	public static BiometricPrompt.CryptoObject cryptoObject;
	protected String TAG = "DeviceFingerprintAccess";
	private String action;
	private String key;
	private String value;
	private WebView webView;
	private ApzActivity activity;
	private String callbackId;
	private String[] permissions;
	private JSONObject jsonReq;
	public BiometricAuth(WebView webView, ApzActivity activity, String callbackId) {
		this.webView = webView;
		this.activity = activity;
		this.callbackId = callbackId;
	}



	private void enableFingerScanner() {
		 biometricManager= BiometricManager.from(activity.getApplicationContext());
		int biometricStatus =biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);

		keyguardManager = (KeyguardManager) activity.getApplicationContext()
				.getSystemService(Context.KEYGUARD_SERVICE);
		if (!(Build.VERSION.SDK_INT > 22)) {
			callErrorCallback("Minimum SDK required is 23", "APZ-CNT-323");
			return;
		}

		if (biometricStatus ==BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE || biometricStatus==BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE) {
			callErrorCallback("No FingerPrint hardware found!", "APZ-CNT-215");
			return;
		}

		if (!keyguardManager.isKeyguardSecure()) {
			callErrorCallback("Lock screen security not enabled in Settings", "APZ-CNT-324");
			return;
		}

		/*if (ActivityCompat.checkSelfPermission(activity,
				Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
			callErrorCallback("Fingerprint authentication permission not enabled", "APZ-CNT-218");
			return;
		}*/

		if (biometricStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
			callErrorCallback("Register at least one biometric in Settings", "APZ-CNT-218");
			return;
		}
		
/*		if (biometricStatus ==BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ) {
			callErrorCallback("Biometric not supported", "APZ-CNT-000");
		}
		if (biometricStatus ==BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED) {
			callErrorCallback("Biometric security update required.", "APZ-CNT-215");
		}
		if (biometricStatus==BiometricManager.BIOMETRIC_STATUS_UNKNOWN) {
			callErrorCallback("An unknown error occured in biometric", "APZ-CNT-000");
		}*/
		//generateKey();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (cipherInit()) {
				ApzBiometricActivty.init(activity,  webView,  callbackId , jsonReq);
				cryptoObject = new BiometricPrompt.CryptoObject(cipher);
				Intent intent = new Intent(activity,ApzBiometricActivty.class);
				activity.startActivity(intent);
			}else{
				JSONObject resFull = new JSONObject();
				JSONObject params = new JSONObject();
				try {
					JSONObject paramsJson = jsonReq.getJSONObject("params");
					JSONObject reqFullJson = paramsJson.getJSONObject("reqFull");
					JSONObject appzillonHeader = reqFullJson.getJSONObject("appzillonHeader");
					appzillonHeader.put("status",false);
					reqFullJson.put("appzillonHeader",appzillonHeader);
					JSONArray jsonArray = new JSONArray();
					JSONObject appzillonErrors = new JSONObject();
					appzillonErrors.put("errorMessage","Lock screen security changed in Settings.");
					appzillonErrors.put("errorCode","APZ-CNT-325");
					jsonArray.put(appzillonErrors);

					reqFullJson.put("appzillonErrors",jsonArray);
					resFull.put("resFull",reqFullJson);
					resFull.put("status",false);
					resFull.put("ignoreDispMsg", true);
					params.put("params",resFull);
					params.put("reqId", jsonReq.getString("reqId"));
				} catch (Exception e) {
				}
				ApzPluginUtil.sendError(callbackId, "APZ-CNT-325", params, activity, webView, true);
			}
		}
	}

	@SuppressLint("InlinedApi")
	@TargetApi(23)
	protected void generateKey() {
		try {
			keyStore = KeyStore.getInstance("AndroidKeyStore");
		} catch (Exception e) {
			ApzLogger.e(TAG, e.toString());
		}

		try {
			keyGenerator = KeyGenerator.getInstance(
					KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			ApzLogger.e(TAG, e.toString());
			throw new RuntimeException("Failed to get KeyGenerator instance", e);
		}
		try {
			keyStore.load(null);
			keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
					KeyProperties.PURPOSE_ENCRYPT
							| KeyProperties.PURPOSE_DECRYPT)
					.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
					.setUserAuthenticationRequired(true)
					.setEncryptionPaddings(
							KeyProperties.ENCRYPTION_PADDING_PKCS7).build());
			keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
				| CertificateException | IOException e) {
			throw new RuntimeException(e);
		}

	}

//	public boolean cipherInit() {
//		try {
//			cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
//					+ KeyProperties.BLOCK_MODE_CBC + "/"
//					+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
//		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
//			throw new RuntimeException("Failed to get Cipher", e);
//		}
//
//		try {
//			keyStore.load(null);
//			SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
//			cipher.init(Cipher.ENCRYPT_MODE, key);
//			return true;
//		} catch (KeyPermanentlyInvalidatedException e) {
//			ApzLogger.e(TAG, e.toString());
//			return false;
//		} catch (KeyStoreException | CertificateException
//				| UnrecoverableKeyException | IOException
//				| NoSuchAlgorithmException | InvalidKeyException e) {
//			throw new RuntimeException("Failed to init Cipher", e);
//		}
//	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	public boolean cipherInit() {
		try {
			cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
					+ KeyProperties.BLOCK_MODE_CBC + "/"
					+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException("Failed to get Cipher", e);
		}

		SecretKey secretKey = getSecretKey();
		if (getSecretKey() == null){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				generateSecretKey(new KeyGenParameterSpec.Builder(
						KEY_NAME,
						KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
						.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
						.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
						.setUserAuthenticationRequired(true)
						// Invalidate the keys if the user has registered a new biometric
						// credential, such as a new fingerprint. Can call this method only
						// on Android 7.0 (API level 24) or higher. The variable
						.setInvalidatedByBiometricEnrollment(true)
						.build());
			}else{
				generateSecretKey(new KeyGenParameterSpec.Builder(
						KEY_NAME,
						KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
						.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
						.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
						.setUserAuthenticationRequired(true)
						// Invalidate the keys if the user has registered a new biometric
						// credential, such as a new fingerprint. Can call this method only
						// on Android 7.0 (API level 24) or higher. The variable
						.build());
			}
			secretKey = getSecretKey();
		}

		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return true;
		} catch (KeyPermanentlyInvalidatedException e) {
			ApzLogger.e(TAG, e.toString());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				generateSecretKey(new KeyGenParameterSpec.Builder(
						KEY_NAME,
						KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
						.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
						.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
						.setUserAuthenticationRequired(true)
						// Invalidate the keys if the user has registered a new biometric
						// credential, such as a new fingerprint. Can call this method only
						// on Android 7.0 (API level 24) or higher. The variable
						.setInvalidatedByBiometricEnrollment(true)
						.build());
			}else{
				generateSecretKey(new KeyGenParameterSpec.Builder(
						KEY_NAME,
						KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
						.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
						.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
						.setUserAuthenticationRequired(true)
						// Invalidate the keys if the user has registered a new biometric
						// credential, such as a new fingerprint. Can call this method only
						// on Android 7.0 (API level 24) or higher. The variable
						.build());
			}
			return false;
		}
		catch (InvalidKeyException e) {
			throw new RuntimeException("Failed to init Cipher", e);
		}
	}

	private void generateSecretKey(KeyGenParameterSpec keyGenParameterSpec) {
		KeyGenerator keyGenerator = null;
		try {
			keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
			keyGenerator.init(keyGenParameterSpec);
			keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

	}

	private SecretKey getSecretKey() {
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance("AndroidKeyStore");
			// Before the keystore can be accessed, it must be loaded.
			keyStore.load(null);
			return ((SecretKey)keyStore.getKey(KEY_NAME, null));
		} catch (KeyStoreException | CertificateException | UnrecoverableKeyException | NoSuchAlgorithmException | IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	void callErrorCallback(String errorMsg, String errorCode) {
		JSONObject json = new JSONObject();
		try {
			json.put("text", errorMsg);
			json.put("reqId", jsonReq.getString("reqId"));
		} catch (JSONException e) {
			ApzLogger.e(TAG, e.toString());
		}
		ApzPluginUtil.sendError(this.callbackId, errorCode, json, activity, webView,
				true);
		return;
	}



	public void biometricAuthentication(JSONObject params) {
		try {
			jsonReq = params;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.USE_FINGERPRINT)
							!= PackageManager.PERMISSION_GRANTED) {
						permissions = new String[]{Manifest.permission.USE_FINGERPRINT};
						requestForPermission();
					} else {

						enableFingerScanner();
					}

			} else {
				enableFingerScanner();
			}

		} catch (Exception e) {
			ApzLogger.e(TAG, e.toString());
		}
	}

	private void requestForPermission() {
		this.activity.startOnPermissionForResult(activity, permissions, ApzPlugin.APZ_REQ_FINGERPRINT, new OnPermissionsResultHandler() {
					@RequiresApi(api = Build.VERSION_CODES.N)
					@Override
					public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
						if (requestCode == ApzPlugin.APZ_REQ_FINGERPRINT) {
							boolean denied = false;
							boolean never_ask_again = false;
							for (String permission : permissions) {
								if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
									denied = true;
								} else {
									if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
										//callCamera();
									} else {
										never_ask_again = true;
									}
								}
							}
							if (never_ask_again) {
								PermissionDeniedCallback();
							} else if (denied) {
								displayReconfirmationMessage();
							} else {
								enableFingerScanner();
							}
						} else {
							PermissionDeniedCallback();
						}

					}
				}
		);
	}
	private void displayReconfirmationMessage() {
		String message = "To access fingerprint ,allow app to access by granting requested permissions";
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
		alertDialogBuilder.setTitle("Permission Denied");
		alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						requestForPermission();
					}
				}).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				PermissionDeniedCallback();
			}
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
	private void PermissionDeniedCallback(){
		ApzPluginUtil.sendPermissionDenied("Fingerprint",this.callbackId, this.activity,this.webView);
	}

}

