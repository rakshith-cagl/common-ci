package com.iexceed.appzillon.securityutils;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utilsexception.UtilsException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by diganta.kumar@i-exceed.com on 6/2/18 3:10 PM
 */
public class RSACryptoUtils {
    static final Pattern _privateKey = Pattern.compile("-{5}BEGIN PRIVATE KEY-{5}(?:\\s|\\r|\\n)+"
            + "([a-zA-Z0-9+/=\r\n]+)" + "-{5}END PRIVATE KEY-{5}(?:\\s|\\r|\\n)+");
    static final Base64 _base64 = new Base64(-1, null, true);
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, RSACryptoUtils.class.toString());
    private static final String ALGORITHM = "RSA";
    private static final String ALGORITHM_WITH_PADDING = "RSA/NONE/OAEPWithSHA1AndMGF1Padding";
    public static String rsaEncryptionRequired = ServerConstants.NO;
    static PrivateKey privateKey = null;

    private RSACryptoUtils() {

    }

    public static String decryptRequestPayLoad(String inputString, HttpServletRequest request, Message pMessage) {
        String encryptionFlag = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.ENCRYPTION_FLAG);
        LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Encryption Required :" + encryptionFlag + " Encryption flag in http request object: " + request.getAttribute(ServerConstants.ENCRYPTION_FLAG));
        try {
            JSONObject jsonObject = new JSONObject(inputString);
            syntaxValidationOfRequest(inputString);
            jsonObject = jsonObject.getJSONObject(ServerConstants.MESSAGE_HEADER);
            String interfaceId = jsonObject.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID);
            if (ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE.equalsIgnoreCase(interfaceId)
                    || ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH.equalsIgnoreCase(interfaceId)
                    || ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS.equalsIgnoreCase(interfaceId)
                    || ServerConstants.INTERFACE_ID_CHECK_SERVER.equalsIgnoreCase(interfaceId)
                    || ServerConstants.INTERACE_ID_GET_USER_APPACCESS_TOKEN.equalsIgnoreCase(interfaceId)
                    || ServerConstants.INTERACE_ID_RELOAD_LOGGER.equalsIgnoreCase(interfaceId)
                    || Utils.isNotNullOrEmpty(request.getHeader(HttpHeaders.AUTHORIZATION))) {
                request.setAttribute(ServerConstants.ENCRYPTION_FLAG, ServerConstants.NO);
            } else {
                String appId = jsonObject.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
                String replayAttackFlag = PropertyUtils.getPropValue(appId, ServerConstants.REPLAY_REQUEST_REQUIRED);
                LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "replayAttackFlag for request:" + replayAttackFlag);
                if (Utils.isNotNullOrEmpty(replayAttackFlag) && replayAttackFlag.equalsIgnoreCase(ServerConstants.NO)) {
                    encryptionFlag = ServerConstants.NO;
                }

            }
        } catch (JSONException e) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Request may be encrypted :" + e);
        }
        try {
            if (ServerConstants.YES.equalsIgnoreCase(encryptionFlag)
                    && (request.getAttribute(ServerConstants.ENCRYPTION_FLAG) == null || ServerConstants.YES.equalsIgnoreCase((String) request.getAttribute(ServerConstants.ENCRYPTION_FLAG)))
                    && (request.getHeader(ServerConstants.APZCNTR) == null || !ServerConstants.APZRICT.equalsIgnoreCase(request.getHeader(ServerConstants.APZCNTR)))) {
                LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Encrypted Request PayLoad:" + inputString);
                rsaEncryptionRequired = ServerConstants.YES;
                if (privateKey == null) {
                    privateKey = readPrivateKey();
                }
                JSONObject jsonObject = new JSONObject(inputString);
                String encryptedKey = jsonObject.getString(ServerConstants.MESSAGE_SAFE);

                String decryptedKey = decryptData(encryptedKey);


                pMessage.getHeader().setServerToken(decryptedKey);
                int safeBit = 0;
                if (jsonObject.has(ServerConstants.APPZILLON_SAFE_BIT)
                        && (jsonObject.getInt(ServerConstants.APPZILLON_SAFE_BIT) == 1)) {
                    safeBit = 1;
                }
                int encMode = 0;
                if (jsonObject.has(ServerConstants.APPZILLON_ENC_MODE)
                        && (jsonObject.getInt(ServerConstants.APPZILLON_ENC_MODE) == 1)) {
                    encMode = 1;
                }

                int hash = jsonObject.has(ServerConstants.APPZILLON_KEY_LEN) ?
                        jsonObject.getInt(ServerConstants.APPZILLON_KEY_LEN) : 1;

                String headerDecrypted = getDecryptedPayload(decryptedKey, jsonObject.getString(ServerConstants.MESSAGE_HEADER), safeBit, encMode, hash);
                String bodyDecrypted = getDecryptedPayload(decryptedKey, jsonObject.getString(ServerConstants.MESSAGE_BODY), safeBit, encMode, hash);
                String qop = jsonObject.has(ServerConstants.QOP) ? jsonObject.getString(ServerConstants.QOP) : null;
                inputString = getAppzillonPayload(qop, headerDecrypted, bodyDecrypted);


            } else if (ServerConstants.YES.equalsIgnoreCase(encryptionFlag) && request.getHeader(ServerConstants.APZCNTR) != null && ServerConstants.APZRICT.equalsIgnoreCase(request.getHeader(ServerConstants.APZCNTR))) {
                // decryption for rict
                rsaEncryptionRequired = ServerConstants.YES;
                JSONObject jsonObject = new JSONObject(inputString);
                String appzillonHeader = jsonObject.getString(ServerConstants.MESSAGE_HEADER);
                String appzillonBody = jsonObject.getString(ServerConstants.MESSAGE_BODY);
                String appzillonSafe = jsonObject.getString(ServerConstants.MESSAGE_SAFE);
                appzillonHeader = decryptRequest(appzillonSafe, appzillonHeader);
                appzillonBody = decryptRequest(appzillonSafe, appzillonBody);
                inputString = getAppzillonPayload(null, appzillonHeader, appzillonBody);
            }
            LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Decrypted inputString:" + inputString);
        } catch (JSONException e) {
            LOG.error("Error in json request :", e);
            UtilsException utilsException = UtilsException.getUtilsExceptionInstance();
            utilsException.setCode("APZ_RS_001");
            utilsException.setMessage("Invalid Appzillon Request");
            throw utilsException;
        }
        return inputString;

    }

    private static void syntaxValidationOfRequest(String inputString) {
        JSONObject rawRequest = new JSONObject(inputString);
        if (rawRequest.has(ServerConstants.MESSAGE_HEADER) && rawRequest.has(ServerConstants.MESSAGE_BODY)) {
            boolean validRequest = true;
            JsonElement element = JsonParser.parseString(inputString);
            JsonObject obj = element.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                String key = entry.getKey();
                //System.out.println(key);
                if (key.equals(ServerConstants.MESSAGE_HEADER)
                        || key.equals(ServerConstants.MESSAGE_BODY)
                        || key.equals(ServerConstants.QOP)
                        || key.equals(ServerConstants.MESSAGE_SAFE)
                        || key.equals(ServerConstants.APPZILLON_ENC_MODE)
                        || key.equals(ServerConstants.APPZILLON_KEY_LEN)
                        || key.equals(ServerConstants.APPZILLON_SAFE_BIT)) {
                    validRequest = true;
                } else {
                    validRequest = false;
                }

                if (!validRequest) {
                    LOG.error("Error in json request syntax.");
                    UtilsException utilsException = UtilsException.getUtilsExceptionInstance();
                    utilsException.setCode("APZ_RS_011");
                    utilsException.setMessage("Invalid JSON syntax.");
                    throw utilsException;
                }
            }
        } else {
            LOG.error("Error in json request syntax.");
            UtilsException utilsException = UtilsException.getUtilsExceptionInstance();
            utilsException.setCode("APZ_RS_011");
            utilsException.setMessage("Invalid JSON syntax.");
            throw utilsException;
        }
    }

    public static String getEncryptedString(Header header, String toBeEncrypted, int safeBit) {
        String encryptData;
        LOG.debug("rsaEncryptionRequired :" + rsaEncryptionRequired);
        String secreteKey = header.getServerToken();
        encryptData = getEncryptePayload(secreteKey, toBeEncrypted, safeBit, header.getEncMode(), header.getEncyKeyLen());
        return encryptData;
    }

    public static String getEncryptePayload(String key, String pResponseBody, int safeBit, int encMode, int keyLen) {
        String encryptedAppzillonBody = null;

        LOG.debug("{} PlainText to be encrypted: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, pResponseBody);

        encryptedAppzillonBody = AppzillonAESUtils.encryptPayload(key, pResponseBody, ServerConstants.WEB, safeBit, encMode, keyLen);

        LOG.debug("{} Encrypted: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, encryptedAppzillonBody);
        return encryptedAppzillonBody;
    }


    public static String getDecryptedPayload(String key, String pRequestBody, int safeBit, int encMode, int hash) {
        String decryptedAppzillonBody = null;
        LOG.debug("{} CipherText to be decrypted: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, pRequestBody);
        decryptedAppzillonBody = AppzillonAESUtils.decryptUsingPayload(key, pRequestBody, ServerConstants.WEB, safeBit, encMode, hash);
        LOG.debug("{} PlainText after decryption: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, decryptedAppzillonBody);
        return decryptedAppzillonBody;
    }

    public static String decryptData(String p_dataToDecrypt) {
        String res = "";
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM_WITH_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] ciphertextBytes = Base64.decodeBase64(p_dataToDecrypt);
            byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
            res = new String(decryptedBytes);
        } catch (Exception e) {
            LOG.error("{} Error in Decrypting Data: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, e);
        }
        return res;
    }

    public static String encryptData(final String p_dataToEncrypt) {
        String res = "";
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM_WITH_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] encryptedBytes = cipher.doFinal(p_dataToEncrypt.getBytes(StandardCharsets.UTF_8));
            res = new String(Base64.encodeBase64(encryptedBytes));

        } catch (Exception exe) {
            LOG.error("{} Error in encrypting Data: {}", ServerConstants.LOGGER_PREFIX_RESTFULL, exe);
        }
        return res;
    }

    public static PrivateKey readPrivateKey() {
        String pKeyFileName = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.ENCRYPTION_KEY_FILENAME);
        KeyFactory keyFactory;
        PrivateKey privateKey = null;
        String cloudName = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.READ_PEM_FROM_VAULT);
        try {
            if (Utils.isNotNullOrEmpty(cloudName) && cloudName.equalsIgnoreCase(ServerConstants.CLOUD_AZURE)) {
                privateKey = readPrivateKeyFromAzure();
            } else {
                if (pKeyFileName.toUpperCase().endsWith(".PEM")) {
                    LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Reading PEM file.");
                    // Read PEM file
                    Security.addProvider(new BouncyCastleProvider());
                    KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
                    PemFile pemFile = new PemFile(pKeyFileName);
                    byte[] content = pemFile.getPemObject().getContent();
                    PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
                    privateKey = factory.generatePrivate(privKeySpec);
                } else if (pKeyFileName.toUpperCase().endsWith(".DER")) {
                    // Read DER file
                    LOG.debug(ServerConstants.LOGGER_PREFIX_RESTFULL + "Reading DER file.");
                    byte[] privateKeyContents = readFileContents(pKeyFileName);
                    PKCS8EncodedKeySpec privateSpec1 = new PKCS8EncodedKeySpec(privateKeyContents);
                    keyFactory = KeyFactory.getInstance(ALGORITHM);
                    privateKey = keyFactory.generatePrivate(privateSpec1);
                }
            }
        } catch (IOException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " IOException", e);
        } catch (NoSuchAlgorithmException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " NoSuchAlgorithmException", e);
        } catch (InvalidKeySpecException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " InvalidKeySpecException", e);
        } catch (NoSuchProviderException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " NoSuchProviderException", e);
        }
        return privateKey;
    }

    private static PrivateKey readPrivateKeyFromAzure() {
        try {
            LOG.debug("reading PEM from azure");
            String secretKey = loadAzureCertificate();
            privateKey = extractPrivateKeyFromPemContents(secretKey);
        } catch (InvalidKeySpecException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " InvalidKeySpecException", e);
        } catch (NoSuchAlgorithmException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " NoSuchAlgorithmException", e);
        }
        return privateKey;
    }

    private static String loadAzureCertificate() {
        return AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.ENCRYPTION_KEY_FILENAME)), AuthTokenUtil.getAuthTokenKeyVault(ServerConstants.SERVER_PROP_FILE_CONSTANT));
    }

    public static byte[] readFileContents(String file) throws IOException {

        byte[] privKeyBytes;
        if (Utils.isNotNullOrEmpty(Logger.propertiesPath)) {
            file = String.format("%s/%s/%s", Logger.propertiesPath, ServerConstants.SERVER_PROP_FILE_CONSTANT, file);
        }
        try (InputStream is = RSACryptoUtils.class.getClassLoader().getResourceAsStream(file)) {
            DataInputStream dis = new DataInputStream(is);
            privKeyBytes = new byte[dis.available()];
            dis.readFully(privKeyBytes);
        }
        return privKeyBytes;
    }

    //  RICT changes
    public static String encryptResponse(JSONObject lResponseJson, String apzHeader, String apzBody, String apzErrors) {
        LOG.debug("-----------------------------Encrypting--------------------");
        JSONObject appzillonResponse = new JSONObject();
        String header = apzHeader;
        String body = apzBody;
        String secretKey = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.APPZILLON_SAFE_KEY);
        LOG.debug("Key :" + secretKey);
        String finalSecretKey = getKey(secretKey);
        SecretKeySpec key = new SecretKeySpec(finalSecretKey.getBytes(), "AES");
        IvParameterSpec iv = new IvParameterSpec(getIV(finalSecretKey));
        String ivtobetransfered = "";
        ivtobetransfered = new String(java.util.Base64.getEncoder().encode(iv.getIV()));
        LOG.debug("iv -:" + ivtobetransfered);
        String encryptedHeader = getAESencryptedForJS(key, iv, header);
        String encryptedBody = getAESencryptedForJS(key, iv, body);
        String encryptedError = "";
        if (lResponseJson.has(ServerConstants.MESSAGE_ERROR)) {
            encryptedError = getAESencryptedForJS(key, iv, apzErrors);
            appzillonResponse.put(ServerConstants.MESSAGE_ERROR, encryptedError);
        }
        appzillonResponse.put(ServerConstants.MESSAGE_HEADER, encryptedHeader);
        appzillonResponse.put(ServerConstants.MESSAGE_BODY, encryptedBody);
        appzillonResponse.put(ServerConstants.MESSAGE_SAFE, ivtobetransfered);

        if (ServerConstants.FAILURE.equalsIgnoreCase(encryptedHeader) || ServerConstants.FAILURE.equalsIgnoreCase(encryptedBody)) {

            JSONObject res;
            try {
                res = new JSONObject();
                res.put(ServerConstants.STATUS, false);
                res.put(ServerConstants.APPZILLON_ERROR_CODE, ServerConstants.PAYLOAD_ENCRYPTION_ERROR_CODE);

            } catch (JSONException e) {
                LOG.error("JSONException Occurred!!! ", e);
            }
        }
        LOG.debug("Transitted Text encrypted Data encryptedHeader -:" + encryptedHeader);
        LOG.debug("Transitted Text encrypted Data encryptedBody -:" + encryptedBody);
        LOG.debug("Transitted Text encrypted Data encryptedError-:" + encryptedError);
        LOG.debug("-----------------------------Encrypted--------------------");
        return appzillonResponse.toString(0);
    }

    public static String decryptRequest(String ivString, String encryptedPayload) {
        LOG.debug("-----------------------------Decrypting--------------------");
        String decryptedString = "";
        LOG.debug("Incoming CipherText :" + encryptedPayload);
        String secretKey = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT, ServerConstants.APPZILLON_SAFE_KEY);
        LOG.debug("Key :" + secretKey);
        LOG.debug("iv :" + ivString);
        LOG.debug("Encrypted Text : " + encryptedPayload);
        String finalSecretKey = getKey(secretKey);
        byte[] keytobeUsed = Arrays.copyOfRange(finalSecretKey.getBytes(), 0, 16);
        SecretKeySpec key = new SecretKeySpec(keytobeUsed, "AES");
        byte[] ivtobeused = Arrays.copyOfRange(Base64.decodeBase64(ivString), 0, 16);
        IvParameterSpec iv = new IvParameterSpec(ivtobeused);
        decryptedString = getAESdecryptedFromJS(key, iv, encryptedPayload);
        if (ServerConstants.FAILURE.equalsIgnoreCase(decryptedString)) {
            JSONObject res = null;
            try {
                res = new JSONObject();
                res.put(ServerConstants.STATUS, false);
                res.put(ServerConstants.APPZILLON_ERROR_CODE, ServerConstants.PAYLOAD_ENCRYPTION_ERROR_CODE);
                decryptedString = res.toString();
            } catch (JSONException e) {
                LOG.error("JSONException Occurred!!! ", e);
            }
        }
        LOG.debug("Decrypted Text : " + decryptedString);
        LOG.debug("-----------------------------Decrypted--------------------");
        return decryptedString;
    }

    public static String getAESencryptedForJS(SecretKeySpec key, IvParameterSpec iv, String plainText) {
        LOG.debug("AESencryptedForJS PlainText :" + plainText);
        String cipherText = "";
        try {
            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCBC.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] encryptedData = aesCBC.doFinal(plainText.getBytes());
            cipherText = Base64.encodeBase64String(encryptedData);
            LOG.debug("encrypted Text -:" + cipherText);
        } catch (Exception ex) {
            LOG.error("Exception Occurred!!! ", ex);
            cipherText = ServerConstants.FAILURE;
        }
        return cipherText;
    }

    public static String getAESdecryptedFromJS(SecretKeySpec key, IvParameterSpec iv, String cipherText) {
        LOG.debug("AESdecryptedFromJS CipherText :" + cipherText);
        String decryptedText = "";
        try {
            byte[] cipherData = Base64.decodeBase64(cipherText);
            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decryptedData = aesCBC.doFinal(cipherData);
            decryptedText = new String(decryptedData, StandardCharsets.UTF_8);
            LOG.debug("Decrypted Text :" + decryptedText);
        } catch (Exception ex) {
            LOG.error("Exception Occurred!!! ", ex);
            decryptedText = ServerConstants.FAILURE;
        }
        return decryptedText;
    }

    private static byte[] getIV(String key) {
        byte[] iv = new byte[16];
        Arrays.fill(iv, (byte) 0);
        StringBuilder or = new StringBuilder(key);
        String nw = or.reverse().toString();
        byte[] keyBytes = null;
        keyBytes = nw.getBytes(StandardCharsets.UTF_8);
        byte[] rawIV = new byte[keyBytes.length];
        for (int i = 0; i < keyBytes.length; i++) {
            rawIV[i] = (byte) (keyBytes[i] >> 1);
        }
        for (int i = 0; i < iv.length; i++) {
            iv[i] = rawIV[i];
        }
        return iv;
    }

    private static String getKey(String key) {
        LOG.debug("get Key value");
        String result = null;
        if (key.length() < 16) {
            StringBuilder temp = new StringBuilder(key);
            for (int i = key.length(); i < 16; i++) {
                temp.append(ServerConstants.DOLLAR);
            }
            LOG.debug("temp Key: " + temp.toString());
            result = temp.toString();
        } else if (key.length() > 16) {
            String finalKey = key.substring(0, 16);
            LOG.debug("final Key: " + finalKey);
            result = finalKey;
        } else {
            LOG.debug("Key: " + key);
            result = key;
        }
        return result;
    }

    private static String getAppzillonPayload(String qop, String header, String body) {
        StringBuilder requestPayload = new StringBuilder();
        requestPayload.append("{\"").append(ServerConstants.QOP).append("\":\"").append(qop).append("\",").append("\"")
                .append(ServerConstants.MESSAGE_HEADER).append("\":").append(header).append(",").append("\"")
                .append(ServerConstants.MESSAGE_BODY).append("\":").append(body).append("}");
        return requestPayload.toString();
    }

    /**
     * Extracts private key from PEM contents
     *
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    private static PrivateKey extractPrivateKeyFromPemContents(String pemContents)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        Matcher matcher = _privateKey.matcher(pemContents);
        if (!matcher.find()) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + "No private key found in PEM contents found in azure key vault");
        }

        byte[] privateKeyBytes = _base64.decode(matcher.group(1));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
}
