package com.iexceed.webcontainer.utils.hash;

import static com.iexceed.webcontainer.utils.AppzillonConstants.ACTION_ID;
import static com.iexceed.webcontainer.utils.AppzillonConstants.ACTION_ID_UPLOAD;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_BODY;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_ERRORS;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_ERROR_CODE;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_ERROR_DESC;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_ERROR_MSG;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_GET_APP_SEC_TOKENS_REQUEST;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_GET_APP_SEC_TOKENS_RESPONSE;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_HEADER;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_INTERFACEID;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_INTERFACE_FILEPUSH_WS;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_INTERFACE_FILE_PUSH;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_INTERFACE_FILE_PUSH_AUTH;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_INTERFACE_UPLOAD_FILE_WS;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_LOGIN_REQ;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_REQUEST_KEY;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_UPLOADFILE_RES;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_UPLOADWSFILE_RES;
import static com.iexceed.webcontainer.utils.AppzillonConstants.APPZILLON_UPLOAD_PATH;
import static com.iexceed.webcontainer.utils.AppzillonConstants.CLIENT_NONCE;
import static com.iexceed.webcontainer.utils.AppzillonConstants.CONTENT_TYPE_MULTIPART;
import static com.iexceed.webcontainer.utils.AppzillonConstants.EXPIRY_DATE_FORMAT;
import static com.iexceed.webcontainer.utils.AppzillonConstants.FAILURE;
import static com.iexceed.webcontainer.utils.AppzillonConstants.GENERATE_OTP;
import static com.iexceed.webcontainer.utils.AppzillonConstants.ID;
import static com.iexceed.webcontainer.utils.AppzillonConstants.PAYLOAD_ENCRYPTION;
import static com.iexceed.webcontainer.utils.AppzillonConstants.PAYLOAD_ENCRYPTION_ERROR_CODE;
import static com.iexceed.webcontainer.utils.AppzillonConstants.QOP;
import static com.iexceed.webcontainer.utils.AppzillonConstants.REQUESTKEY;
import static com.iexceed.webcontainer.utils.AppzillonConstants.REQUEST_PROCESSOR_BEAN;
import static com.iexceed.webcontainer.utils.AppzillonConstants.RESULT;
import static com.iexceed.webcontainer.utils.AppzillonConstants.SERVER_NONCE;
import static com.iexceed.webcontainer.utils.AppzillonConstants.STATUS;
import static com.iexceed.webcontainer.utils.AppzillonConstants.SUCCESS;
import static com.iexceed.webcontainer.utils.AppzillonConstants.SUCCESS_MESSAGE;
import static com.iexceed.webcontainer.utils.AppzillonConstants.ULOAD_FAILURE;
import static com.iexceed.webcontainer.utils.AppzillonConstants.YES;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.owasp.encoder.Encode;

import com.iexceed.appzillon.custom.IRequestProcessor;
import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.plugins.CryptoPlugin;
import com.iexceed.webcontainer.plugins.UploadFile;
import com.iexceed.webcontainer.utils.WebProperties;
import com.iexceed.webcontainer.utils.json.JSONArray;
import com.iexceed.webcontainer.utils.json.JSONException;
import com.iexceed.webcontainer.utils.json.JSONObject;

public class Utility {

	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(Utility.class.getName());

	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	private static final String A_TO_Z_a_TO_z = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	private static ApplicationContext appContext = null;


	private Utility() {
	}

	public static void setSessionTimeOut(HttpSession session) {
		String sessionTimeOut = WebProperties.getSessionTimeOut();
		int timeOutInt = -1;
		try {
			LOG.debug("Setting SessionTimeout...");
			timeOutInt = Integer.parseInt(sessionTimeOut);
			LOG.debug("SessionTimeOut : " + timeOutInt);
		} catch (NumberFormatException e) {
			LOG.error("SessionTimeOut is not set, hence setting to be eternal....");
		}
		session.setMaxInactiveInterval(timeOutInt);
	}

	public static String hashSHA256(String ptext, String psalt) {
		String pTextSalt = ptext + psalt;
		String pHashedText = "";
		byte[] ptextSaltbyte = new byte[200];
		byte[] hashbyte = new byte[200];
		try {
			MessageDigest msgdigest = MessageDigest.getInstance("SHA-256");
			ptextSaltbyte = pTextSalt.getBytes("UTF-8");
			msgdigest.reset();
			msgdigest.update(ptextSaltbyte);
			hashbyte = msgdigest.digest();
			pHashedText = toHexString(hashbyte);
		} catch (NoSuchAlgorithmException n) {
			LOG.error("NoSuchAlgorithmException: ", n);
		} catch (UnsupportedEncodingException unse) {
			LOG.error("Unsupported character set", unse);
		}
		LOG.info("Hashed Successfully");
		return pHashedText;
	}

	public static String toHexString(byte[] param) {
		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < param.length; i++) {
			int temp = ((param[i]) >>> 4) & 0xf;
			strBuilder.append(HEX[temp]);
			temp = param[i] & 0xf;
			strBuilder.append(HEX[temp]);
		}
		return strBuilder.toString();
	}

	public static byte[] getRandomByte(int len) {
		byte[] randomByte = null;
		SecureRandom secRand = new SecureRandom();
		randomByte = new byte[len];
		secRand.nextBytes(randomByte);
		return randomByte;
	}

	public static String getRandomString(int len) {
		String randomString = "";
		byte[] randomByte = getRandomByte(len);
		randomString = Base64.encodeBase64String(randomByte);
		return randomString;
	}

	public static String generateSecRandomOfLength(int length) {
		StringBuilder result = new StringBuilder();
		String alphanumeric = A_TO_Z_a_TO_z;
		int size = alphanumeric.length();
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < length; i++) {
			result.append(alphanumeric.charAt(random.nextInt(size)));
		}
		return result.toString();
	}

	public static String convertToString(InputStream inpStr) throws IOException {
		BufferedReader buffRdr = new BufferedReader(new InputStreamReader(inpStr));
		String line;
		StringBuilder lContent = new StringBuilder("");
		while ((line = buffRdr.readLine()) != null) {
			lContent.append(line);
		}
		buffRdr.close();
		inpStr.close();
		return lContent.toString();
	}

	public static String getStackTrace(Exception pEx) {
		StringWriter lSw = null;
		PrintWriter lPw = null;
		try {
			// Creating String writer Object
			lSw = new StringWriter();
			// Creating print writer object
			lPw = new PrintWriter(lSw);
			// Getting stack trace and storing it in print writer obj
			pEx.printStackTrace(lPw);
			// Storing the stack trace string to the string object
		} catch (Exception ex) {
			LOG.error("Exception", ex);
		}
		// returning stack trace string
		return lSw.toString();
	}

	public static String getStringFromInputStream(InputStream inpStrm) {
		LOG.debug("Converting InputStream to String");
		BufferedReader buffRdr = null;
		StringBuilder strBuilder = new StringBuilder();
		String line;
		try {
			buffRdr = new BufferedReader(new InputStreamReader(inpStrm, "UTF-8"));
			while ((line = buffRdr.readLine()) != null) {
				strBuilder.append(line);
			}
		} catch (IOException e) {
			LOG.error("IOException", e);
		} finally {
			if (buffRdr != null) {
				try {
					buffRdr.close();
				} catch (IOException e) {
					LOG.error("IOExecption", e);
				}
			}
		}
		return strBuilder.toString();
	}

	public static String getContextDetails(HttpServletRequest request, HttpServletResponse response) {
		LOG.debug("Getting request object details");
		JSONObject reqObj = null;
		try {
			reqObj = new JSONObject();
			if (request.getParameterNames() != null) {
				JSONObject parNames = null;
				JSONArray paramArray = new JSONArray();
				Enumeration<String> parameterNames = request.getParameterNames();
				while (parameterNames.hasMoreElements()) {
					parNames = new JSONObject();
					String key = (String) parameterNames.nextElement();
					String val = request.getParameter(key);
					parNames.put("name", key);
					parNames.put("value", val);
					paramArray.put(parNames);
				}
				reqObj.put("RequestParameters", paramArray);
			}
			if (request.getQueryString() != null) {
				reqObj.put("QueryString", request.getQueryString());
			}
			if (request.getInputStream() != null) {
				InputStream inpStr = request.getInputStream();
				try(BufferedReader buffRdr = new BufferedReader(new InputStreamReader(inpStr))) {
					String line;
					StringBuilder responseStream = new StringBuilder();
					while ((line = buffRdr.readLine()) != null) {
						responseStream.append(line);
					}
					reqObj.put("FormData", responseStream);
				}

			}
			if (request.getHeaderNames() != null) {
				JSONArray hdrArray = new JSONArray();
				Enumeration<String> headerNames = request.getHeaderNames();
				JSONObject hdr = null;
				while (headerNames.hasMoreElements()) {
					hdr = new JSONObject();
					String paramName = (String) headerNames.nextElement();
					String paramValue = request.getHeader(paramName);
					if (paramName.equalsIgnoreCase("cookie") || paramName.equalsIgnoreCase("X-Forwarded-For")
							|| paramName.equalsIgnoreCase("forwarded")) {
						LOG.debug("Skipping the following header : " + paramName);
					} else {
						hdr.put("name", paramName);
						hdr.put("value", paramValue);
					}
					hdrArray.put(hdr);
				}
				reqObj.put("HeaderParams", hdrArray);
			}
			LOG.debug("Request Json Details : " + reqObj);
		} catch (JSONException jsone) {
			LOG.error("JSONException ", jsone);
		} catch (IOException e) {
			LOG.error("IOException ", e);
		}
		return reqObj.toString();
	}

	public static void getRequestProcessorBean(HttpServletRequest request, HttpServletResponse response, String appId) {
		try {
			IRequestProcessor bean = (IRequestProcessor) getAppContext().getBean(REQUEST_PROCESSOR_BEAN);
			bean.requestProcessor(request, response, appId);
			return;
		} catch (Exception ex) {
			LOG.error("Bean not found ", ex);
		}
	}

	public static BeanFactory getAppContext() {
		try {
			if (appContext == null) {
				LOG.debug("Application context is getting intialized");
				appContext = new ClassPathXmlApplicationContext("META-INF/appzillon-beans.xml");
			}
		} catch (Exception ex) {
			LOG.error("Bean information not found ", ex);
		}
		return appContext;
	}

	public static JSONObject getOTPdetails(JSONObject requestJson) {
		JSONObject resJson = null;
		try {
			IAppzillonHashing bean = (IAppzillonHashing) ((BeanFactory) getAppContext()).getBean(GENERATE_OTP);
			resJson = bean.generateHashedPin(requestJson, APPZILLON_LOGIN_REQ);
		} catch (JSONException jse) {
			LOG.error("JSONException", jse);
		} catch (Exception ex) {
			LOG.error("Bean not found ", ex);
		}
		return resJson;
	}

	public static boolean validateLicense() {
		LOG.info("Checking for Application's Expiry");
		boolean validitity = false;
		try {
			LOG.info("Expiry Date format is: " + EXPIRY_DATE_FORMAT);
			SimpleDateFormat sDateFormat = new SimpleDateFormat(EXPIRY_DATE_FORMAT);
			String expDate = WebProperties.getExpiryDate();
			LOG.info("Application's Expiry Date is: " + expDate);
			if ((expDate == null) || expDate.isEmpty()) {
				validitity = true;
			} else {
				Date lExpiryDate = sDateFormat.parse(expDate);
				Date date = new Date();
				Date lCurrentDate = sDateFormat.parse(sDateFormat.format(date));
				if (lExpiryDate.compareTo(lCurrentDate) > 0) {
					validitity = true;
				}
			}
		} catch (ParseException ex) {
			LOG.error("ParseException ", ex);
			validitity = false;
		}
		return validitity;
	}

	public static void callFailure(HttpServletRequest request, HttpServletResponse response, Exception lException,
			String clsMsg, String identifier) {
		LOG.error(clsMsg, lException);
		try {
			JSONObject result = new JSONObject();
			result.put(STATUS, FAILURE);
			if ((identifier != null) && (!"".equals(identifier)) && (!"null".equalsIgnoreCase(identifier))) {
				result.put(ID, identifier);
			}
			buildAndSendResponse(request, response, result.toString());
		} catch (JSONException jse) {
			LOG.error(clsMsg, jse);
		} catch (IOException ioe) {
			LOG.error(clsMsg, ioe);
		}
	}

	public static String getfinalResponse(HttpServletRequest request, HttpServletResponse response, String result) {
		String payload = "";
		try {
			JSONObject resultJson = new JSONObject(result);
			if (resultJson.has(APPZILLON_ERRORS))
				result = encodeResponse(result);
		} catch (JSONException e) {
			LOG.error("Invalid JSONObject ", e);
		}
		if (YES.equalsIgnoreCase(WebProperties.getPayloadEncryptionReq())) {
			LOG.debug("Encrypting the request/response ...");
			payload = CryptoPlugin.encryptResponse(request, result);
			LOG.debug("Encrypted the request/response ...");
		} else {
			payload = result;
		}
		LOG.debug("Final Payload : " + payload);
		return payload;
	}

	public static String encodeResponse(String inputJson) {
		JSONObject obj = null;
		JSONObject responseObj = new JSONObject();
		try {
			obj = new JSONObject(inputJson);
			Iterator<?> it = obj.keys();
			while (it.hasNext()) {
				String key = (String) it.next();
				Object nodeValue = obj.get(key);
				if (nodeValue instanceof JSONArray) {
					JSONArray array = (JSONArray) nodeValue;
					JSONArray resarray = new JSONArray();
					int i = 0;
					while (i < array.length()) {
						JSONObject ob = array.getJSONObject(i);
						String res = encodeResponse(ob.toString());
						resarray.put(new JSONObject(res));
						i++;
					}
					if (obj.has(key)) {
						responseObj.put(key, resarray);
					}

				} else if (nodeValue instanceof JSONObject) {
					String res = encodeResponse(nodeValue.toString());
					if (obj.has(key)) {
						responseObj.put(key, new JSONObject(res));
					}
				} else {
					if (nodeValue instanceof String) {
						nodeValue = Encode.forHtml((String) nodeValue);
					}
					if (obj.has(key)) {
						responseObj.put(key, nodeValue);
					}
				}
			}
		} catch (JSONException e) {
			LOG.error("Invalid JSONObject ", e);
		}
		return responseObj.toString();
	}

	public static void validatePayload(HttpServletRequest request, HttpServletResponse response, String requestString)
			throws Exception {
		LOG.debug("validating request");
		int i = requestString.indexOf(QOP);
		JSONObject reqJson;
		reqJson = new JSONObject(requestString);
		String requestWithoutQOP = requestString.replaceAll(requestString.substring(i - 2, i + 80), "");
		String hashedPayload = Utility.hashSHA256(requestWithoutQOP,
				reqJson.getJSONObject(APPZILLON_HEADER).getString("clientNonce"));
		if(!hashedPayload.equals(reqJson.getString(QOP))) {
			LOG.error("invalid request");
			JSONObject appzHeader = reqJson.getJSONObject(APPZILLON_HEADER);
			JSONArray appzErrors = new JSONArray();
			JSONObject error = new JSONObject();
			appzHeader.put(STATUS, false);
			error.put(APPZILLON_ERROR_MSG, "Invalid request");
			error.put(APPZILLON_ERROR_CODE, "APZ-RS-010");
			appzErrors.put(0, error);
			reqJson.put(APPZILLON_HEADER, appzHeader);
			reqJson.put(APPZILLON_BODY, new JSONObject());
			reqJson.put(APPZILLON_ERRORS, appzErrors);
			reqJson.remove(QOP);
			Utility.buildAndSendResponse(request, response, reqJson.toString());
			Exception e = new Exception();
			throw e;
		}
	}

	public static void buildAndSendResponse(HttpServletRequest request, HttpServletResponse response, String result)
			throws IOException {
		LOG.trace("Building Response : " + result);
		// Building Response
		String finalResponse = getfinalResponse(request, response, result);
		LOG.debug("Response Built :" + finalResponse);
		// Sending Response
		LOG.trace("Sending Response.");
		sendResponse(request, response, finalResponse);
		LOG.debug("Final Response Sent :" + finalResponse);
	}

	public static void sendResponse(HttpServletRequest request, HttpServletResponse response, String responseStr)
			throws IOException {
		LOG.debug("Sending Response : " + responseStr);
		PrintWriter out = response.getWriter();
		try {
			//out.print(Encode.forHtmlContent(responseStr));
			out.print(StringEscapeUtils.unescapeHtml(Encode.forHtmlContent(responseStr)));
			out.flush();
			out.close();
		} catch (Exception e) {
			LOG.error("Exception", e);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}

		}
	}

	/**
	 * returns true if the string is not null and not empty otherwise returns false
	 * 
	 * @param pValue
	 * @return
	 */

	public static boolean isNotNullOrEmpty(String pValue) {
		return pValue != null && !pValue.isEmpty() ? true : false;
	}

	/**
	 * returns true if the string is null or empty otherwise returns false
	 * 
	 * @param pValue
	 * @return
	 */

	public static boolean isNullOrEmpty(String pValue) {
		return pValue == null || pValue.isEmpty() ? true : false;
	}

	public static JSONObject getJsonRequest(HttpServletRequest request, HttpServletResponse response) {
		InputStream inpStrm = null;
		String content = "";
		JSONObject requestJson = null;
		try {
			LOG.debug("ContentType : " + request.getContentType());
			if (request.getContentType() != null && !request.getContentType().isEmpty()) {
				if (request.getContentType().contains(CONTENT_TYPE_MULTIPART)) {
					requestJson = new JSONObject();
					requestJson.put(APPZILLON_BODY, new JSONObject().put(ACTION_ID, ACTION_ID_UPLOAD));

				} else if (request.getContentType().contains("application/x-www-form-urlencoded")) {
					// Do Nothing

				} else {
					inpStrm = request.getInputStream();
					content = Utility.convertToString(inpStrm);
					if ("Y".equalsIgnoreCase(WebProperties.getPayloadEncryptionReq())
							&& !content.contains(APPZILLON_INTERFACE_FILE_PUSH)
							&& !content.contains(APPZILLON_INTERFACE_FILEPUSH_WS)
							&& !content.contains(APPZILLON_INTERFACE_FILE_PUSH_AUTH)
							&& !getActionId(new JSONObject(content.trim())).equals("appzillonGetKey")) {

						HttpSession session = request.getSession(false);
						if (session != null) {
							// Decrypt Request coming from javascript
							LOG.debug("Decrypting Request : " + content);
							content = CryptoPlugin.decryptRequest(request, content);
							LOG.debug("Decrypted Request: " + content);
						} else {
							LOG.debug("A valid session does not exit");
							JSONObject result = new JSONObject();
							try {
								JSONArray appzErrors = new JSONArray();
								JSONObject error = new JSONObject();
								error.put(APPZILLON_ERROR_MSG, "A valid session does not exist");
								error.put(APPZILLON_ERROR_CODE, "APZ-SMS-EX-003");
								appzErrors.put(0, error);
								result.put(APPZILLON_HEADER, new JSONObject().put(STATUS, false));
								result.put(APPZILLON_BODY, new JSONObject());
								result.put(APPZILLON_ERRORS, appzErrors);
								content = result.toString();
							} catch (JSONException je) {
								LOG.error("JSONException ", je);
							}
						}

					}
					
					if(!"Y".equalsIgnoreCase(WebProperties.getPayloadEncryptionReq()) 
							&& !content.contains(APPZILLON_INTERFACE_FILE_PUSH)
							&& !content.contains(APPZILLON_INTERFACE_FILEPUSH_WS)
							&& !content.contains(APPZILLON_INTERFACE_FILE_PUSH_AUTH)
							&& !content.contains(APPZILLON_GET_APP_SEC_TOKENS_REQUEST)
							&& "Y".equalsIgnoreCase(WebProperties.getDataIntegrity()))
						validatePayload(request, response, content);
				
					if (!content.isEmpty()) {
						requestJson = new JSONObject(content.trim());
						LOG.debug("Request content : " + content);
					}
					if ("Y".equalsIgnoreCase(WebProperties.getPayloadEncryptionReq())
							&& requestJson.has(APPZILLON_ERRORS)) {
						LOG.debug("Processing for the failure Payload encryption details ...");
						requestJson.getJSONObject(APPZILLON_BODY).put(ACTION_ID, PAYLOAD_ENCRYPTION);
						LOG.debug("Sending the failure payload details : " + requestJson.toString(0));
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Input Stream is not proper");
			try {
				requestJson = new JSONObject(invalidPayloadResponse());
				requestJson.getJSONObject(APPZILLON_BODY).put(ACTION_ID, PAYLOAD_ENCRYPTION);
			} catch (JSONException e1) {
				LOG.error("JSONException ", e1);
			}
		}
		return requestJson;
	}

	public static String invalidPayloadResponse() {
		String errorString = null;
		JSONObject res = new JSONObject();
		JSONObject appzHeader = new JSONObject();
		JSONObject error = new JSONObject();
		JSONArray appzErrors = new JSONArray();
		try {
			appzHeader.put(STATUS, false);
			error.put(APPZILLON_ERROR_CODE, PAYLOAD_ENCRYPTION_ERROR_CODE);
			error.put(APPZILLON_ERROR_MSG, "invalid payload");
			appzErrors.put(0, error);
			res.put(APPZILLON_HEADER, appzHeader);
			res.put(APPZILLON_BODY, new JSONObject());
			res.put(APPZILLON_ERRORS, appzErrors);
			errorString = res.toString();
		} catch (JSONException e1) {
			LOG.error("JSONException ", e1);
		}
		return errorString;
	}

	public static boolean checkQualityOfPayload(String responseJson)
			throws JSONException, UnsupportedEncodingException {
		LOG.info("Data Integration is in process....");
		JSONObject res = new JSONObject(responseJson);
		String cNonce = res.getJSONObject(APPZILLON_HEADER).getString(CLIENT_NONCE);
		String sNonce = res.getJSONObject(APPZILLON_HEADER).getString(SERVER_NONCE);
		String hashedResponse = res.getString(QOP);
		int i = responseJson.indexOf(QOP);
		responseJson = responseJson.replaceAll(responseJson.substring(i - 1, i + 81), "");
		responseJson = StringEscapeUtils.escapeJava(responseJson);
		LOG.debug("Escaped java response string for qop calculation : " + responseJson);
		LOG.debug("Escaped java response string length : " + responseJson.length());
		String encodedPayload = Base64.encodeBase64String(responseJson.trim().getBytes());
		String lHashedCnonce = Utility.hashSHA256(cNonce, sNonce + WebProperties.getServerToken());
		String hashedPayLoad = Utility.hashSHA256(encodedPayload, lHashedCnonce);
		if (hashedResponse.equals(hashedPayLoad)) {
			return true;
		} else {
			return false;
		}
	}

	public static void upload(HttpServletRequest request, HttpServletResponse response, JSONObject appzillonHeader,
			MultipartEntityBuilder builder) throws JSONException, IOException, ClientProtocolException {
		String serverURL = WebProperties.getServerURL();
		LOG.debug("Server URL : " + serverURL);
		String internalServerURL = serverURL + APPZILLON_UPLOAD_PATH;
		LOG.debug("Upload Server url : " + internalServerURL);

		String interfaceId = appzillonHeader.getString(APPZILLON_INTERFACEID);

		HttpClient httpclient = HttpClientBuilder.create().build();
		HttpPost postrequest = new HttpPost(internalServerURL);

		HttpEntity entity = builder.build();
		postrequest.setEntity(entity);

		HttpResponse httpresponse = httpclient.execute(postrequest);
		int responseCode = httpresponse.getStatusLine().getStatusCode();
		String responseMessage = httpresponse.getStatusLine().getReasonPhrase();
		LOG.debug("Response code is : " + responseCode + " and Response Message is : " + responseMessage);
		if (responseCode == 200) {
			String responseJSON = EntityUtils.toString(httpresponse.getEntity());
			JSONObject jObjRes = new JSONObject(responseJSON);
			JSONObject jObjHeader = jObjRes.getJSONObject(APPZILLON_HEADER);
			JSONObject resultJson = null;
			if (jObjHeader.getBoolean(STATUS)) {
				String requestKey = jObjHeader.getString(APPZILLON_REQUEST_KEY);
				request.getSession(false).setAttribute(REQUESTKEY, requestKey);
				JSONObject jObjBody = jObjRes.getJSONObject(APPZILLON_BODY);

				JSONObject uploadRes = null;
				if (APPZILLON_INTERFACE_UPLOAD_FILE_WS.equalsIgnoreCase(interfaceId)) {
					uploadRes = jObjBody.getJSONObject(APPZILLON_UPLOADWSFILE_RES);
				} else {
					uploadRes = jObjBody.getJSONObject(APPZILLON_UPLOADFILE_RES);
				}
				resultJson = new JSONObject();
				resultJson.put(SUCCESS_MESSAGE, uploadRes);
				resultJson.put(RESULT, SUCCESS);
				Utility.buildAndSendResponse(request, response, resultJson.toString());

			} else {
				String errorString = new JSONObject(responseJSON).get(APPZILLON_ERRORS).toString();
				JSONArray errorArray = new JSONArray(errorString);
				resultJson = new JSONObject();
				resultJson.put(APPZILLON_ERRORS, errorArray.getJSONObject(0));
				resultJson.put(RESULT, FAILURE);
				Utility.buildAndSendResponse(request, response, resultJson.toString());
			}
		} else {
			UploadFile.sendFailureResponse(request, response, APPZILLON_ERROR_DESC, ULOAD_FAILURE);
		}
		postrequest.releaseConnection();
	}

	public static String getIpAddress(HttpServletRequest request) {
		LOG.debug("Inside getIpAddress.");
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("x-real-ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		LOG.debug("Return IP Address : " + ip);
		return ip;
	}

	public static JSONObject setGetAppSecResponse(HttpServletRequest request, HttpServletResponse response,
			JSONObject requestJson) throws JSONException {
		LOG.debug("Inside setGetAppSecResponse ");
		JSONObject lHeader = requestJson.getJSONObject(APPZILLON_HEADER);
		lHeader.put(STATUS, true);
		HttpSession session = request.getSession(false);
		JSONObject lBody = new JSONObject();
		lBody.put(SERVER_NONCE, session.getAttribute(SERVER_NONCE));
		lBody.put(STATUS, true);
		JSONObject lAppzReq = new JSONObject();
		lAppzReq.put(APPZILLON_HEADER, lHeader);
		lAppzReq.put(APPZILLON_BODY, new JSONObject().put(APPZILLON_GET_APP_SEC_TOKENS_RESPONSE, lBody));
		LOG.debug("setGetAppSecResponse : " + lAppzReq.toString());
		return lAppzReq;
	}

	public static void createCookie(HttpServletRequest request, HttpServletResponse response, String cName,
			String cValue) {
		LOG.debug("inside createCookie");
		Cookie c = new Cookie(Encode.forJava(cName), Encode.forJava(cValue));
		c.setHttpOnly(true);
		if (WebProperties.getSecureCookie().equals(YES)) {
			c.setSecure(true);
		}
		request.getSession(false).setMaxInactiveInterval(-1);
		c.setMaxAge(Integer.parseInt(WebProperties.getCookieAge()) * 60 * 60 * 24);
		response.addCookie(c);
	}

	public static String getActionId(JSONObject request) {
		String action = "";
		try {
			JSONObject appzillonHeader = null;
			if (request.has(APPZILLON_HEADER)) {
				appzillonHeader = request.getJSONObject(APPZILLON_HEADER);
			}
			JSONObject appzillonBody = request.getJSONObject(APPZILLON_BODY);
			if (appzillonBody.has(ACTION_ID) && !appzillonBody.getString(ACTION_ID).isEmpty()) {
				action = appzillonBody.getString(ACTION_ID);
			} else if (appzillonHeader.has(APPZILLON_INTERFACEID)) {
				action = appzillonHeader.getString(APPZILLON_INTERFACEID);
			}
			LOG.debug("Processing for action : " + action);
		} catch (JSONException e) {

		} catch (Exception e) {
			LOG.error("Input Stream Exception -:", e);
		}
		return action;
	}

	public static void createProductFiles(ServletContext servletContext, JSONObject apzBody, HttpServletRequest request,
			HttpServletResponse response, JSONObject apzHeader) throws IOException {
		String filePath = servletContext.getRealPath("/apps/" + WebProperties.getAppId() + "/screens/products/");
		for (Iterator iterator = apzBody.keys(); iterator.hasNext();) {
			String fileName = iterator.next().toString();
			String fileContent;
			try {
				fileContent = apzBody.getJSONObject(fileName).toString();
				createFile(filePath, fileName, fileContent, request, response, apzHeader);
			} catch (JSONException e) {
				LOG.error("JSONException : " + e);
				// UploadFile.sendFailureResponse(request, response, "JSON Parsing Failed",
				// "APZ-CNT-032");
				Utility.sendCitiFailureResponse(request, response, apzHeader);
			}
		}
	}

	public static void createFile(String filePath, String fileName, String fileContent, HttpServletRequest request,
			HttpServletResponse response, JSONObject apzHeader) throws JSONException, IOException {
		try {
			File file = new File(filePath);
			file.mkdirs();
			FileWriter fw = new FileWriter(file + "/" + fileName + ".json");
			fw.write(fileContent);
			fw.close();
		} catch (IOException ioe) {
			LOG.error("Failed to write json files : " + ioe);
			// UploadFile.sendFailureResponse(request, response, "IO Exception",
			// "APZ-CNT-033");
			Utility.sendCitiFailureResponse(request, response, apzHeader);
		}
	}

	public static void sendCitiSuccessResponse(HttpServletRequest request, HttpServletResponse response,
			JSONObject apzHeader) {
		try {
			JSONObject result = new JSONObject();
			JSONObject respJson = new JSONObject();
			respJson.put(RESULT, SUCCESS);
			result.put(APPZILLON_HEADER, apzHeader);
			result.put(APPZILLON_BODY, respJson);
			Utility.buildAndSendResponse(request, response, result.toString());
		} catch (JSONException e) {
			LOG.error("JSONException : " + e);
		} catch (IOException e) {
			LOG.error("IO Exception : " + e);
		}

	}

	public static void sendCitiFailureResponse(HttpServletRequest request, HttpServletResponse response,
			JSONObject apzHeader) throws IOException {
		try {
			JSONObject errorResp = new JSONObject();
			JSONObject error = new JSONObject();
			error.put(RESULT, FAILURE);
			apzHeader.put(STATUS, false);
			errorResp.put(APPZILLON_HEADER, apzHeader);
			errorResp.put(APPZILLON_BODY, error);
			Utility.buildAndSendResponse(request, response, errorResp.toString());
		} catch (JSONException e) {
			LOG.error("JSONException : " + e);
		}
	}

	public static boolean isExternalAuthEnabled() {
		return Utility.isNotNullOrEmpty(WebProperties.getIsThirdPartyAuthEnabled())
				&& YES.equalsIgnoreCase(WebProperties.getIsThirdPartyAuthEnabled());
	}

	public static String getAuthProvider() {
		return Utility.isNullOrEmpty(WebProperties.getAuthProvider()) ? "" : WebProperties.getAuthProvider();
	}

	public static String buildErrorResponse(HttpServletRequest request, HttpServletResponse response,
			JSONObject requestJson, String message, String msgCode) {
		try {
			JSONObject appzHeader = requestJson.getJSONObject(APPZILLON_HEADER);
			JSONArray appzErrors = new JSONArray();
			JSONObject error = new JSONObject();
			appzHeader.put(STATUS, false);
			error.put(APPZILLON_ERROR_MSG, message);
			error.put(APPZILLON_ERROR_CODE, msgCode);
			appzErrors.put(0, error);
			requestJson.put(APPZILLON_HEADER, appzHeader);
			requestJson.put(APPZILLON_BODY, new JSONObject());
			requestJson.put(APPZILLON_ERRORS, appzErrors);

		} catch (JSONException e) {
			LOG.error("Exception occurred while building the error response : ", e);
		}
		return requestJson.toString();
	}

	public static byte[] hexToBytes(String str) {
		if (str == null) {
			return null;
		} else if (str.length() < 2) {
			return null;
		} else {
			int len = str.length() / 2;
			byte[] buffer = new byte[len];
			for (int i = 0; i < len; i++) {
				buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
			}
			return buffer;
		}
	}

	public static String bytesToHex(byte[] buf)
	{
		char[] chars = new char[2 * buf.length];
		for
		(int i = 0; i < buf.length; ++i)
		{
			chars[2 * i] = HEX[(buf[i] & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX[buf[i] & 0x0F];
		}
		return new String(chars);
	}
}

