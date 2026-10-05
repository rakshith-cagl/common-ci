package com.iexceed.plugins.fingerprintscan;

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

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.OnPermissionsResultHandler;
import com.iexceed.plugins.ApzPlugin;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.camera.ApzCameraPlugin;
import com.iexceed.plugins.errorlog.ApzLogger;

@TargetApi(23)
public class DeviceFingerprintAccess extends ApzPlugin {

	private static ApzPlugin pluginObj;
	private static final String KEY_NAME = "appzillon";
	private FingerprintManager fingerprintManager;
	private KeyguardManager keyguardManager;
	private KeyStore keyStore;
	private KeyGenerator keyGenerator;
	private Cipher cipher;
	private FingerprintManager.CryptoObject cryptoObject;
	private String[] permissions;
	protected String TAG = "DeviceFingerprintAccess";

	private DeviceFingerprintAccess(WebView webView, ApzActivity activity) {
		super(webView, activity);

	}

	public static ApzPlugin createPlugin(WebView webView, ApzActivity activity) {
		if (pluginObj == null) {
			pluginObj = new DeviceFingerprintAccess(webView, activity);
		}
		return pluginObj;
	}

	private void enableFingerScanner() {

		keyguardManager = (KeyguardManager) activity.getApplicationContext()
				.getSystemService(Context.KEYGUARD_SERVICE);
		if (Build.VERSION.SDK_INT > 22) {
			fingerprintManager = (FingerprintManager) activity
					.getApplicationContext().getSystemService(
							Context.FINGERPRINT_SERVICE);
		} else {
			callErrorCallback("Minimum SDK required is 23", "APZ-CNT-323");
		}
		if (!fingerprintManager.isHardwareDetected()) {
			callErrorCallback("No FingerPrint hardware found!", "APZ-CNT-215");
		}
		if (!keyguardManager.isKeyguardSecure()) {
			callErrorCallback("Lock screen security not enabled in Settings", "APZ-CNT-324");
		}

		if (ActivityCompat.checkSelfPermission(activity,
				Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
			callErrorCallback("Fingerprint authentication permission not enabled", "APZ-CNT-218");
		}

		if (!fingerprintManager.hasEnrolledFingerprints()) {
			callErrorCallback("Register at least one fingerprint in Settings", "APZ-CNT-218");
		}

		generateKey();

		if (cipherInit()) {
			cryptoObject = new FingerprintManager.CryptoObject(cipher);
			FingerprintHandler helper = new FingerprintHandler(this.webView,
					this.activity, this.callbackId);
			helper.startAuth(fingerprintManager, cryptoObject);
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

	public boolean cipherInit() {
		try {
			cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
					+ KeyProperties.BLOCK_MODE_CBC + "/"
					+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException("Failed to get Cipher", e);
		}

		try {
			keyStore.load(null);
			SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return true;
		} catch (KeyPermanentlyInvalidatedException e) {
			ApzLogger.e(TAG, e.toString());
			return false;
		} catch (KeyStoreException | CertificateException
				| UnrecoverableKeyException | IOException
				| NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException("Failed to init Cipher", e);
		}
	}

	void callErrorCallback(String errorMsg, String errorCode) {
		JSONObject json = new JSONObject();
		try {
			json.put("text", errorMsg);
		} catch (JSONException e) {
			ApzLogger.e(TAG, e.toString());
		}
		ApzPluginUtil.sendError(this.callbackId, errorCode, json, activity, webView,
				true);
		return;
	}

	@Override
	public void execute(JSONObject params) {
		try {
			this.callbackId = params.getString("id");

			if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
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
		String message = "To access Fingerprint ,allow app to access by granting requested permissions";
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

	public static boolean isPlugin() {
		// TODO Auto-generated method stub
		return true;

	}
}
