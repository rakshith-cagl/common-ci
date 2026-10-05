package com.iexceed.webcontainer.utils;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;


import com.iexceed.webcontainer.startup.WebContextListener;
import com.iexceed.webcontainer.vault.AuthTokenUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.hash.Utility;
import com.iexceed.webcontainer.utils.json.JSONException;
import com.iexceed.webcontainer.utils.json.JSONObject;

public class RSACryptoUtils {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(RSACryptoUtils.class.getName());

	public static PublicKey publicKey = null ;
	static final Pattern _certificate = Pattern.compile("-{5}BEGIN CERTIFICATE-{5}(?:\\s|\\r|\\n)+"
			+ "([a-zA-Z0-9+/=\r\n]+)" + "-{5}END CERTIFICATE-{5}(?:\\s|\\r|\\n)+");

	static final Base64 _base64 = new Base64(-1, null, true);

	static final String X509 = "X.509";
	private static final String ALGORITHM_WITH_PADDING = "RSA/NONE/OAEPWithSHA1AndMGF1Padding";

	private RSACryptoUtils(){

	}
	public static void readPublicKey() {
		String pkeyFileName = WebProperties.getEncryptionKeyFileName();
		KeyFactory keyFactory;
		LOG.debug("KeyFileName: " + pkeyFileName);
		String cloudName = PropertyUtils.getPropertyValue(AppzillonConstants.READ_PEM_FROM_VAULT);
		try {
			if(Utility.isNotNullOrEmpty(cloudName) && cloudName.equalsIgnoreCase(CLOUD_AZURE)){
				publicKey = readPublicKeyFromAzure();
			}else{
				if (pkeyFileName.toUpperCase().endsWith(".PEM")) {
					LOG.debug("Reading PEM file");
					// Read PEM file
					Security.addProvider(new BouncyCastleProvider());
					KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
					PemFile pemFile = new PemFile(pkeyFileName);
					byte[] content = pemFile.getPemObject().getContent();
					X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
					publicKey = factory.generatePublic(pubKeySpec);
				} else if (pkeyFileName.toUpperCase().endsWith(".DER")) {
					// Read DER file
					LOG.debug("Reading DER file");
					byte[] publicKeyContents = readFileContents(pkeyFileName);
					X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyContents);
					keyFactory = KeyFactory.getInstance("RSA");
					publicKey = keyFactory.generatePublic(pubKeySpec);
				}
			}
		} catch (IOException e) {
			LOG.error("IOException ", e);
		} catch (NoSuchAlgorithmException e) {
			LOG.error("NoSuchAlgorithmException ", e);
		} catch (InvalidKeySpecException e) {
			LOG.error("InvalidKeySpecException ", e);
		} catch (NoSuchProviderException e) {
			LOG.error("NoSuchProviderException ", e);
		}
	}

	public static byte[] readFileContents(String file) throws IOException {
		DataInputStream dis;
		byte[] pubKeyBytes;
		if(WebContextListener.propertiesPath !=null && !"".equals(WebContextListener.propertiesPath)) {
			file = WebContextListener.propertiesPath+"/"+file;
		}
		try(InputStream is = RSACryptoUtils.class.getClassLoader().getResourceAsStream(file)){
			dis = new DataInputStream(is);
			pubKeyBytes = new byte[dis.available()];
			dis.readFully(pubKeyBytes);
			dis.close();
		}
		return pubKeyBytes;
	}

	// encryption using appzillonSafe
	public static String encryptPayloadWithKey(String dataToEncript, String lHeader,String lBody,String secretKey, String requestType)
			throws JSONException {
		JSONObject jsonObject = new JSONObject(dataToEncript);
		String encryptedheader = AppzillonAESUtils.getEncryptPayload(secretKey,lHeader);
		String encryptedbody = AppzillonAESUtils.getEncryptPayload(secretKey,lBody);
		String encryptedSafeToken = encryptData(secretKey);

		StringBuilder lEncryptedReq = new StringBuilder();
		if (YES.equals(WebProperties.getDataIntegrity())
				&& (!APPZILLON_GET_APP_SEC_TOKENS_REQUEST.equals(requestType))) {
			String hashedQop = jsonObject.getString(QOP);
			lEncryptedReq.append("{\"").append(QOP).append("\":\"").append(hashedQop).append("\",").append("\"")
					.append(APPZILLON_BODY).append("\":\"").append(encryptedbody).append("\",").append("\"")
					.append(APPZILLON_HEADER).append("\":\"").append(encryptedheader).append("\",").append("\"")
					.append(APPZILLON_SAFE).append("\":\"").append(encryptedSafeToken).append("\"").append("}");
		} else {
			lEncryptedReq.append("{\"").append(APPZILLON_BODY).append("\":\"").append(encryptedbody).append("\",")
					.append("\"").append(APPZILLON_HEADER).append("\":\"").append(encryptedheader).append("\",")
					.append("\"").append(APPZILLON_SAFE).append("\":\"").append(encryptedSafeToken).append("\"")
					.append("}");
		}

		JSONObject encyUpdated = new JSONObject(lEncryptedReq.toString());
		encyUpdated.put(ENC_MODE,1);

		if(Utility.isNotNullOrEmpty(WebProperties.getSafeBit()) &&
				Integer.parseInt(WebProperties.getSafeBit()) ==1){
			try {

				encyUpdated.put(APPZILLON_SAFE_BIT,1);

			} catch (JSONException e) {
				LOG.error("exception while updating safe bit", e);
			}
		}
		return encyUpdated.toString();
	}

	// decryption using appzillonSafe
	public static String decryptPayloadWithKey(String encryptedString) throws JSONException {
		JSONObject jsonObject = new JSONObject(encryptedString);
		String secretKey = decryptData(jsonObject.getString(APPZILLON_SAFE));
		jsonObject.remove(APPZILLON_SAFE);
		String decryptederror = "";
		String qop = "";

		String decryptedheader = AppzillonAESUtils.getDecryptedPayload(secretKey,
				jsonObject.getString(APPZILLON_HEADER));
		String decryptedbody = AppzillonAESUtils.getDecryptedPayload(secretKey, jsonObject.getString(APPZILLON_BODY));

		if (jsonObject.has(QOP)) {
			qop = jsonObject.getString(QOP);
		}
		if (jsonObject.has(APPZILLON_ERRORS)) {
			decryptederror = AppzillonAESUtils.getDecryptedPayload(secretKey, jsonObject.getString(APPZILLON_ERRORS));
		}

		StringBuilder lReqString = new StringBuilder();

		if (jsonObject.has(QOP) && jsonObject.has(APPZILLON_ERRORS)) {
			lReqString.append("{\"").append(QOP).append("\":\"").append(qop).append("\",").append("\"")
					.append(APPZILLON_ERRORS).append("\":").append(decryptederror).append(",").append("\"");
		} else if (jsonObject.has(QOP) && !jsonObject.has(APPZILLON_ERRORS)) {
			lReqString.append("{\"").append(QOP).append("\":\"").append(qop).append("\",").append("\"");
		} else if (!jsonObject.has(QOP) && jsonObject.has(APPZILLON_ERRORS)) {
			lReqString.append("{\"").append(APPZILLON_ERRORS).append("\":").append(decryptederror).append(",")
					.append("\"");
		} else {
			lReqString.append("{\"");
		}

		lReqString.append(APPZILLON_HEADER).append("\":").append(decryptedheader).append(",\"").append(APPZILLON_BODY)
				.append("\":").append(decryptedbody).append("}");

		return lReqString.toString().trim();
	}

	// encryption using public key
	public static String encryptData(String plainText) {
		String ecKey = "";
		try {
			Cipher cipher2 = Cipher.getInstance(ALGORITHM_WITH_PADDING);
			cipher2.init(Cipher.ENCRYPT_MODE, publicKey);
			ecKey = new String(Base64
					.encodeBase64(cipher2.doFinal(plainText.getBytes(StandardCharsets.UTF_8))));

		} catch (Exception exe) {
			LOG.error("Exception", exe);
		}
		return ecKey;
	}

	// decryption using public key
	public static String decryptData(String encryptedString) {
		String dcKey = "";
		try {
			Cipher cipher3 = Cipher.getInstance(ALGORITHM_WITH_PADDING);
			cipher3.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] bytNewData = cipher3.doFinal(Base64.decodeBase64(encryptedString));
			dcKey = new String(bytNewData);
		} catch (Exception e) {
			LOG.error("Exception", e);
		}
		return dcKey;

	}

	public static String removeSpaceFromElements(String payloadJson) {
		Pattern p = Pattern.compile("\\s*[\"\"]");
		Matcher m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("\"");

		p = Pattern.compile("[\"\"]\\s*");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("\"");

		p = Pattern.compile("[}]\\s*");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("}");

		p = Pattern.compile("\\s*[{]");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("{");

		p = Pattern.compile("[]]\\s*");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("]");

		p = Pattern.compile("[\\[]\\s*");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("[");

		p = Pattern.compile("[:]\\s*");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll(":");

		p = Pattern.compile("[\n]");
		m = p.matcher(payloadJson);
		payloadJson = m.replaceAll("");

		return payloadJson;
	}

	public static String generatePayloadWithQOP(JSONObject payloadJson,String apzHeader,String apzBody) throws JSONException {
		LOG.debug("Data integration is in process...");
		JSONObject requestHeader = payloadJson.getJSONObject(APPZILLON_HEADER);
		String cNonce = requestHeader.getString(CLIENT_NONCE);
		String serverNonce = requestHeader.getString(SERVER_NONCE);
		String lServerToken = WebProperties.getServerToken();
		String payload = getPayLoadForQop(apzHeader,apzBody);
		String escapedpayload = StringEscapeUtils.escapeJava(payload);
		LOG.debug("Escaped java request string for qop calculation : " + escapedpayload);
		LOG.debug("Escaped java request string length : " + escapedpayload.length());
		String encodedPayload = Base64.encodeBase64String(escapedpayload.getBytes());
		String lHashedCnonce = Utility.hashSHA256(cNonce, serverNonce + lServerToken);
		String hashedPayLoad = Utility.hashSHA256(encodedPayload,lHashedCnonce);
		StringBuilder lReqString = new StringBuilder();
		lReqString.append("{\"").append(QOP).append("\":\"").append(hashedPayLoad).append("\",")
				.append(payload.substring(1));
		return lReqString.toString();
	}


	public static String getPayLoadForQop(String apzHeader, String apzBody) {
		StringBuilder br = new StringBuilder();
		br.append("{\"").append(APPZILLON_HEADER).append("\":").append(apzHeader).append(",\"").append(APPZILLON_BODY)
				.append("\":").append(apzBody).append("}");
		return br.toString();
	}

	public static PublicKey readPublicKeyFromAzure(){
		String secretKey = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(PropertyUtils.getPropertyValue(ENCRYPTION_KEY_FILENAME)), AuthTokenUtil.getAuthTokenKeyVault());
		// Extract certificates from PEM
		List<X509Certificate> certificates = null;
		try {
			certificates = extractCertificatesFromPemContents(secretKey);
		} catch (CertificateException e) {
			LOG.error("CertificateException ", e);
		} catch (IOException e) {
			LOG.error("IOException ", e);
		}
		X509Certificate secretCertificate = certificates.get(0);
		return secretCertificate.getPublicKey();
	}

	/**
	 * Extracts certificates from PEM contents
	 *
	 * @throws CertificateException
	 * @throws IOException
	 */
	private static List<X509Certificate> extractCertificatesFromPemContents(String pemContents)
			throws CertificateException, IOException {
		Matcher matcher = _certificate.matcher(pemContents);
		if (!matcher.find()) {
			LOG.error("No certificate found in PEM contents.");
		}
		List<X509Certificate> result = new ArrayList<X509Certificate>();
		int offset = 0;
		while (true) {
			if (!matcher.find(offset)) {
				break;
			}
			byte[] certBytes = _base64.decode(matcher.group(1));
			ByteArrayInputStream certStream = new ByteArrayInputStream(certBytes);
			CertificateFactory certificateFactory = CertificateFactory.getInstance(X509);
			X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(certStream);
			certStream.close();

			result.add(x509Certificate);
			offset = matcher.end();
		}
		return result;
	}


}
