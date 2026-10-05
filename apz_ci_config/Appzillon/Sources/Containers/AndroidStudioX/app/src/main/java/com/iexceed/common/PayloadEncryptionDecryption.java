package com.iexceed.common;

import android.content.res.AssetManager;
import android.util.Base64;
import android.util.Log;

import com.iexceed.appzillonapp.AppzillonMainScreen;
import android.os.Build;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

import static com.iexceed.common.StringUtils.CRYPTO_ALGORITHM;
import static com.iexceed.common.StringUtils.RSA_CRYPTO_ALGORITHM;


/**
 * Created by natasha.dawra on 5/2/18.
 */

public class PayloadEncryptionDecryption {

    private static String TAG = "PayloadEncryptionDecryption";
    private static final String ALGORITHM = "RSA";
    private static PublicKey publicKey = null;
    private static PrivateKey privateKey = null;
    public static int IV_LENGTH =12;


    public static PublicKey getKey() {

       /* String publicKey = "";
        try {
            AssetManager assetManager = AppzillonMainScreen.activity.getAssets();
           InputStream inputStream = assetManager.open("apps/" + StringUtils.getString(StringUtils.APP_ID) + "/sslCertificates/"+StringUtils.getString(AppzillonMainScreen.APP_ID)+"_Public.key");

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = br.readLine()) != null)
                lines.add(line);

            StringBuilder sb = new StringBuilder();
            for (String aLine: lines)
                sb.append(aLine);
                 publicKey = sb.toString();

        }catch (IOException e){
            Log.e(TAG, e.getMessage());
        }
        return publicKey.trim();*/
       if(publicKey == null) {
           try {
               AssetManager man = AppzillonMainScreen.activity.getAssets();
               String[] files = man.list("apps/" + StringUtils.getString(StringUtils.APP_ID) + "/rsakey");
               if (files.length > 0) {

                   for (int i = 0; i < files.length; i++) {

                       //Reading public key from .pem file
                       InputStream inputStream = man.open("apps/" + StringUtils.getString(StringUtils.APP_ID) + "/rsakey/" + files[i]);

                       if (files[i].toUpperCase().endsWith(".PEM")) {

                           BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                           String line = null;
                           StringBuilder stringBuilder = new StringBuilder();
                           while ((line = br.readLine()) != null) {
                               stringBuilder.append(line);
                           }
                           publicKey = getPublicKeyFromPEM(stringBuilder.toString());

                       } else if (files[i].toUpperCase().endsWith(".DER")) {
                           byte[] privateKeyContents = readFileContents(inputStream);
                           X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(privateKeyContents);
                           KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                           publicKey = keyFactory.generatePublic(publicSpec);
                       }
                   }
               }
           } catch (IOException ex) {
               //Log.e(TAG, ex.getMessage());
           } catch (NoSuchAlgorithmException e) {
               //Log.e(TAG, e.getMessage());
           } catch (InvalidKeySpecException e) {
               //Log.e(TAG, e.getMessage());
           }
           return publicKey;
       }else{
           return publicKey;
       }
    }

    public static String encryptDataWithRSAPublicKey(String data, PublicKey publicKey){
       
        String result = "";
        try {
            Cipher cipher = Cipher.getInstance(RSA_CRYPTO_ALGORITHM);
            OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec("SHA-1", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey,oaepParameterSpec);
            result = Base64.encodeToString(cipher.doFinal(data.getBytes(Charset.forName("UTF-8"))), Base64.DEFAULT);

        } catch (Exception exe) {
            
        }
        return result;

    }

    public static String decryptDataWithRSAPublicKey(String data, PublicKey publicKey){
      
        String result = "";
        try {
            Cipher oaepFromInit = Cipher.getInstance(RSA_CRYPTO_ALGORITHM);
            OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-1", "MGF1", new MGF1ParameterSpec("SHA-1"), PSource.PSpecified.DEFAULT);
            oaepFromInit.init(Cipher.DECRYPT_MODE, publicKey, oaepParams);
            byte[] bytNewData = oaepFromInit.doFinal(Base64.decode(data, Base64.DEFAULT));
            result = new String(bytNewData);


        } catch (Exception exe) {
            
        }
        return result;

    }
    public static String encryptUsingSecret(String cypher, String key,String clearText, String salt) {
        SecretKeySpec skeySpec = null;
        skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");
        Cipher cipher = null;
        try {
            IV_LENGTH=12;
            cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
        byte[] iv = new byte[IV_LENGTH];
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

    public  static String decryptUsingSecretKey(String cypher, String key, String textToDecrypt, String salt) {
        SecretKeySpec skeySpec = null;
        skeySpec = new SecretKeySpec(hmacSha1(salt, key), "AES");
        byte[] plainText;
        try {

            byte[] ciphertext = Base64.decode(textToDecrypt, Base64.NO_WRAP | Base64.NO_PADDING);
            final Cipher cipher = Cipher.getInstance(CRYPTO_ALGORITHM);
            //use first 12 bytes for iv
            AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, ciphertext, 0, IV_LENGTH);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, gcmIv);
            plainText = cipher.doFinal(ciphertext, IV_LENGTH, ciphertext.length - IV_LENGTH);
            String plainrStr = new String(plainText, "UTF-8");
            if (plainrStr == null)
                return null;

            return new String(plainrStr);
        } catch (Exception e) {
            Log.d("error",e.getMessage());
        }
        return null;
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
            KeySpec keyspec = new PBEKeySpec(key.toCharArray(),
                    salt.getBytes("UTF-8"), 2, keyLength);
            keyByte = factory.generateSecret(keyspec);
        } catch (NoSuchAlgorithmException e) {
            //Log.e(TAG, e.getMessage() );
        } catch (InvalidKeySpecException e) {
        
        }
        catch (UnsupportedEncodingException e) {
           
        }
        return keyByte.getEncoded();
    }

    public static String getDecryptedPayloadWithRSA(String encryptedString) {

        String decrptedString = decryptDataWithRSAPublicKey(encryptedString,getKey());
        return decrptedString;
    }

    public static String getDecryptedPayloadWithAES(String encryptedString,String key) {

        String paddingMask = "$$$$$$$$$$$$$$$$";
        if (key.length() <= 16) {
            key += paddingMask.substring(0, 16 - key.length());
        }
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        byte[] iv = getIV(key);
        String finalSalt = getSalt(key);
        String decrptedString = decryptUsingSecretKey(CRYPTO_ALGORITHM, key, encryptedString, finalSalt);
        return decrptedString;
    }


    public static String getEncryptePayloadwithRSA(String pResponseBody) {

        String encryptedPayload = encryptDataWithRSAPublicKey(pResponseBody,getKey());
        return encryptedPayload;
    }

    public static String getEncryptePayloadwithAES(String pResponseBody,String key){

        String paddingMask = "$$$$$$$$$$$$$$$$";
        if (key.length() <= 16) {
            key += paddingMask.substring(0, 16 - key.length());
        }
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        byte[] iv = getIV(key);
        String finalSalt = getSalt(key);
        String encryptedPayload = encryptUsingSecret(CRYPTO_ALGORITHM, key,pResponseBody, finalSalt);
       return encryptedPayload;
    }

    public static byte[] getIV(String key) {
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

    private static byte[] convertInputStreamToByteArray(InputStream inputStream)
    {
        byte[] bytes= null;
        try
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte data[] = new byte[1024];
            int count;
            while ((count = inputStream.read(data)) != -1) {
                bos.write(data, 0, count);
            }
            bos.flush();
            bos.close();
            inputStream.close();
            bytes = bos.toByteArray();
        }
        catch (IOException e)
        {
           
        }
        return bytes;
    }


    public static RSAPublicKey getPublicKeyFromPEM(String key){

        String publicKeyPEM = key;
        publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "");
        publicKeyPEM = publicKeyPEM.replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.decode(publicKeyPEM,Base64.NO_WRAP);//Base64.decodeBase64(publicKeyPEM);
        KeyFactory kf = null;
        RSAPublicKey pubKey = null;

        try {
            kf = KeyFactory.getInstance("RSA");
            pubKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));

        } catch (NoSuchAlgorithmException e) {
          
        } catch (InvalidKeySpecException e) {
           
        }

        return pubKey;
    }

    public static byte[] readFileContents(InputStream isr) throws IOException {

        DataInputStream dis ;
        byte[] privKeyBytes;
        dis = new DataInputStream(isr);
        privKeyBytes = new byte[dis.available()];
        dis.readFully(privKeyBytes);
        dis.close();
        return privKeyBytes;
    }

}



