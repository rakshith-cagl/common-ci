package com.iexceed.plugins.dataSecurity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.webkit.WebView;

import com.iexceed.common.ApzActivity;
import com.iexceed.common.ServerUtilities;
import com.iexceed.common.StringUtils;
import com.iexceed.appzillonapp.AppzillonMainScreen;
import com.iexceed.plugins.ApzPluginUtil;
import com.iexceed.plugins.encryption.ApzEncryptionPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.security.auth.x500.X500Principal;

/**
 * Created by @appzillon.
 */

public class StoreCredentialsSecurely {

    private static Activity activity;
    public static String apzPrefs = "SecureData";
    private static byte[] iv;
    private static WebView webView;
    private static String callBackId;
    private static KeyStore keyStore;
    private static String requestId;

    public static String Encrypt(String textToEncrypt,String alias){

        String encryptedText = "";
        byte[] encryptionBytes;
        try {
            final KeyGenerator keyGenerator = KeyGenerator
                    .getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build();

                keyGenerator.init(keyGenParameterSpec);
                final SecretKey secretKey = keyGenerator.generateKey();

                final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                iv = cipher.getIV();

            SharedPreferences sharedPreferences = activity.getSharedPreferences(apzPrefs, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("iv", Base64.encodeToString(iv,Base64.NO_WRAP));
            editor.apply();

            encryptionBytes = cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8));
            encryptedText = Base64.encodeToString(encryptionBytes, Base64.NO_WRAP);


        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidAlgorithmParameterException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException ignored) {
        }
        return encryptedText;
    }


    public static String Decrypt(String textToDecrypt,String alias){

        KeyStore keyStore = null;
        String decryptedString = "";
        byte[] bytes = Base64.decode(textToDecrypt, Base64.NO_WRAP);
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                    .getEntry(alias, null);

            final SecretKey secretKey = secretKeyEntry.getSecretKey();

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SharedPreferences sharedPreferences = activity.getSharedPreferences(apzPrefs, Context.MODE_PRIVATE);
            String ivStr = sharedPreferences.getString("iv","");
            iv = Base64.decode(ivStr,Base64.NO_WRAP);

            final GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            final byte[] decodedData = cipher.doFinal(bytes);
            decryptedString = new String(decodedData, StandardCharsets.UTF_8);

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            //Log.e("StoreSecurely", e.getMessage());
        } catch (InvalidAlgorithmParameterException ignored) {
           
        }
        return decryptedString;
    }

    public static void createNewKeys(String alias) {
        try {
            // Create new key if needed
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(activity)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

                KeyPair keyPair = generator.generateKeyPair();
            }
        } catch (Exception ignored) {}
    }


//    public static String decryptString(String encryptedText, String alias) {
//        String decryptedText = "";
//
//        try {
//            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, null);
//            RSAPrivateKey privateKey = (RSAPrivateKey) privateKeyEntry.getPrivateKey();
//
//            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
//            output.init(Cipher.DECRYPT_MODE, privateKey);
//
//            CipherInputStream cipherInputStream = new CipherInputStream(
//                    new ByteArrayInputStream(Base64.decode(encryptedText, Base64.DEFAULT)), output);
//            ArrayList<Byte> values = new ArrayList<>();
//            int nextByte;
//            while ((nextByte = cipherInputStream.read()) != -1) {
//                values.add((byte) nextByte);
//            }
//
//            byte[] bytes = new byte[values.size()];
//            for (int i = 0; i < bytes.length; i++) {
//                bytes[i] = values.get(i);
//            }
//
//            decryptedText = new String(bytes, 0, bytes.length, StandardCharsets.UTF_8);
//        } catch (Exception ignored) {}
//        return decryptedText;
//    }

    public static String encryptString(String textToEncrypt,String alias) {
        String encryptedText = "";
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            // Encrypt the text
            Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            input.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, input);
            cipherOutputStream.write(textToEncrypt.getBytes());
            cipherOutputStream.close();

            byte[] values = outputStream.toByteArray();
            encryptedText = Base64.encodeToString(values, Base64.DEFAULT);
        } catch (Exception ignored) {}

        return encryptedText;
    }

    public static void storeCredentialsSecurely(String userId,String password) {
		if(userId.length() == 0 || password.length() == 0){
            ApzPluginUtil.sendError(callBackId,"APZ-CNT-231",null,activity,webView,true);
        }else{
            String encryptedText = Encrypt(userId + "###@@@###" + password, "credentials");

			if (!("".equalsIgnoreCase(encryptedText))) {
                SharedPreferences sharedPreferences = activity.getSharedPreferences(apzPrefs, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("credentials", encryptedText);
                editor.apply();
				ApzPluginUtil.sendSuccess(callBackId, null, false, activity, webView, true);
			} else {
				ApzPluginUtil.sendError(callBackId,"APZ-CNT-000",null,activity,webView,true);
			}
		}
    }

    public static void loginSecurely(JSONObject params, boolean isBiometric){
        String decryptedString;
        String userName = "";
        String password = "";
        String finalPassword;
        String sysDate = "";
        JSONObject appzillonBody;
        JSONObject loginRequest = null;
        JSONObject appzillonHeader = null;
        try {
            appzillonBody = params.getJSONObject(AppzillonMainScreen.APPZILLON_BODY);
            appzillonHeader = params.getJSONObject(AppzillonMainScreen.APPZILLON_HEADER);
            loginRequest = appzillonBody.getJSONObject("loginRequest");
        } catch (JSONException ignored) {}
        if(isBiometric){
            SharedPreferences sharedPreferences = activity.getSharedPreferences(apzPrefs, Context.MODE_PRIVATE);
            String credentials = sharedPreferences.getString("credentials","");
            if (credentials.equalsIgnoreCase("")) {
                sendError("Credentials empty");
                return;
            } else {
                decryptedString = Decrypt(credentials, "credentials");

                if (decryptedString.equalsIgnoreCase("")) {
                    sendError("Unable to decrypt credentials");
                    return;
                } else {
                    if (decryptedString.contains("###@@@###")) {
                    String[] arr = decryptedString.split("###@@@###");
                    userName = arr[0];
                    password = arr[1];
                    } else {
                        sendError("Mandatory fields are missing from credentials.");
                    return;
                }
                }

            }
        } else {
            try {
                if (loginRequest != null) {
                userName = loginRequest.getString("userId");
                password = loginRequest.getString("pwd");
                }

            } catch (JSONException ignored) {}
        }
        try{
            if (loginRequest != null) {
        sysDate = loginRequest.getString("sysDate");
            }

            if ("#DeviceId".equalsIgnoreCase(StringUtils.getString("authenticationType"))) {
                JSONObject json = new JSONObject();
                try {
                    json.put("userId", userName);
                    json.put("pwd", password);
                    json.put("date", sysDate);//new Date().

                } catch (JSONException ignored) {}
                finalPassword = ApzEncryptionPlugin.hashPwdforNativeLogin(json);
            } else {
                finalPassword = password;
            }
            if (loginRequest != null) {
                loginRequest.put("userId", userName);
                loginRequest.put("pwd", finalPassword);
                appzillonHeader.put("userId", userName);
            }


        } catch (JSONException ignored) {}
        new loginfromContainer(requestId, callBackId).execute(params.toString());

            }

    private static void sendError(String error) {
        JSONObject json = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            json.put("error", error);
            json.put("id", callBackId);
            json.put("keepAlive", false);
            json.put("reqId", requestId);

            params.put("status", false);
            params.put("resFull", new JSONObject());
            json.put("params", params);
            json.put("status", false);

            ApzPluginUtil.sendSuccess(callBackId, json, false, activity, webView, true);
        } catch (JSONException ignored) { }

        }



    public static void storeSecurely(String key,String value, String promptBiometric){
        String encryptedText;
            encryptedText = Encrypt(value,key);
        if(!("".equalsIgnoreCase(encryptedText))) {
            JSONObject jsonObject = null;
            try{
                jsonObject = new JSONObject();
                jsonObject.put("value",encryptedText);
                jsonObject.put("promptBiometric",promptBiometric);
            } catch (JSONException ignored) {}
            SharedPreferences sharedPreferences = activity.getSharedPreferences(apzPrefs, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, jsonObject.toString());
            editor.apply();

            ApzPluginUtil.sendSuccess(callBackId, null, false, activity, webView, true);
        }else{
            ApzPluginUtil.sendError(callBackId,"APZ-CNT-000",null,activity,webView,true);
        }
    }

    public static void retrieveSecurely(String key){
        String decryptedString;
        SharedPreferences sharedPreferences = activity.getSharedPreferences(apzPrefs, Context.MODE_PRIVATE);
        String credentials = sharedPreferences.getString(key,"");
        JSONObject jsonObject;
        String value = "";
        try{
            jsonObject = new JSONObject(credentials);
            value = jsonObject.getString("value");
        } catch (JSONException ignored) {}

                decryptedString = Decrypt(value,key);
            JSONObject json = null;
            try {
                json = new JSONObject();
                json.put("text", decryptedString);
        } catch (Exception ignored) {}
            if(!("".equalsIgnoreCase(decryptedString))){
                ApzPluginUtil.sendSuccess(callBackId, json, false, activity, webView, true);
            }else{
                ApzPluginUtil.sendError(callBackId,"APZ-CNT-000",null,activity,webView,true);
            }
    }


    static class loginfromContainer extends AsyncTask<String, Void, JSONObject>{

        String callbackId;
        String requestId;

        public loginfromContainer(String requestId,String callbackId){
            super();
            this.requestId = requestId;
            this.callbackId = callbackId;
        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            String serverUrl = StringUtils.getString(StringUtils.SERVER_URL);
            return ServerUtilities.sendRequestToServer(serverUrl, strings[0]);
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            super.onPostExecute(response);
            final JSONObject json = new JSONObject();
            try {
                json.put("id", this.callbackId);
                json.put("keepAlive", false);
                json.put("reqId", this.requestId);
                if (response != null) {

                    //If hashing fails
                    if(response.has("errorCode")){
                        JSONObject params = new JSONObject();
                        params.put("status", false);
                        params.put("resFull", response);
                        json.put("params", params);
                        json.put("status", false);
                        json.put("errorCode",response.getString("errorCode"));
                    }else {
                        //Proper respnse from server
                        JSONObject params = new JSONObject();
                        params.put("status", true);
                        params.put("resFull", response);
                        json.put("params", params);
                        json.put("status", true);
                    }
                } else {
                    //If server response is null
                    JSONObject params = new JSONObject();
                    params.put("status", false);
                    params.put("resFull", new JSONObject());
                    json.put("params", params);
                    json.put("status", false);
                }
                ApzPluginUtil.sendSuccess(this.callbackId, json, false, activity, webView, true);
            } catch (Exception ignored) {}
        }
    }

    public static void init(WebView mWebView, ApzActivity mActivity, String mCallBackId, String mRequestId) {
        activity = mActivity;
        webView = mWebView;
        callBackId = mCallBackId;
        requestId = mRequestId;
    }
}



