package com.iexceed.webcontainer.utils;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.iexceed.webcontainer.authentication.AuthenticationService;
import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.plugins.CryptoPlugin;
import com.iexceed.webcontainer.utils.hash.Utility;
import com.iexceed.webcontainer.utils.json.JSONArray;
import com.iexceed.webcontainer.utils.json.JSONException;
import com.iexceed.webcontainer.utils.json.JSONObject;
import com.iexceed.webcontainer.vault.AuthTokenUtil;
import org.owasp.encoder.Encode;

import org.apache.commons.lang.StringEscapeUtils;

public class CallInternalServer {

	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(CallInternalServer.class.getName());
	static boolean securityFlag = true;

	private CallInternalServer() {
	}

	public static void callServer(JSONObject requestJson, HttpServletRequest request, HttpServletResponse response,
			String requestType, ServletContext servletContext) throws IOException {
		InputStream inpStrm = null;
		JSONObject resObj = null;
		int responseCode;
		String responseMessage = "";
		HttpURLConnection urlConnHttp = null;
		HttpsURLConnection urlConnHttps = null;
		DataOutputStream dtOpStrm = null;
		try {
			HttpSession session = request.getSession(false);
			JSONObject appzHeader = requestJson.getJSONObject(APPZILLON_HEADER);
			appzHeader.put(HEADER_ORIGINATION, Utility.getIpAddress(request));
			String appzSessID = (String) session.getAttribute(SESSIONID);
			String appzReqKey = (String) session.getAttribute(REQUESTKEY);
			String selector = (String) session.getAttribute(SELECTOR);

			// changes for external auth provider
			String authToken = (String) session.getAttribute(AUTH_TOKEN);
			String isTokenRefreshed = (String) session.getAttribute(IS_TOKEN_REFRESHED);
			appzHeader.put(AUTH_TOKEN, Utility.isNullOrEmpty(authToken) ? "" : authToken);
			appzHeader.put(IS_TOKEN_REFRESHED, Utility.isNullOrEmpty(isTokenRefreshed) ? "" : isTokenRefreshed);
			// End
			if (appzSessID != null) {
				appzHeader.put(APPZILLON_SESSION_ID, appzSessID);
			} else {
				// LOG.debug("Setting appzillon header session id as empty");
				appzHeader.put(APPZILLON_SESSION_ID, "");
			}

			if (appzReqKey != null) {
				appzHeader.put(APPZILLON_REQUEST_KEY, appzReqKey);
			}
			if (selector != null) {
				appzHeader.put(SELECTOR, selector);
			}
			appzHeader.put(TXN_REF_NO, UUID.randomUUID().toString());
			requestJson.put(APPZILLON_HEADER, appzHeader);
			boolean invalidsNonce = false;
			JSONObject errorResp = new JSONObject();
			String replayAttackFlag = PropertyUtils.getPropertyValue(REPLAY_REQUEST_REQUIRED);
			if (Utility.isNotNullOrEmpty(replayAttackFlag) && NO.equalsIgnoreCase(replayAttackFlag)) {
				securityFlag = false;
			}
			// serverNonce check

			if (securityFlag && !APPZILLON_GET_APP_SEC_TOKENS_REQUEST.equals(requestType)) {
				String sNonce = requestJson.getJSONObject(APPZILLON_HEADER).getString(SERVER_NONCE);
				String sessionServerNonce = (String) request.getSession(false).getAttribute(SERVER_NONCE);
				if (Utility.isNullOrEmpty(sessionServerNonce)) {
					sessionServerNonce = "";
				}
				if (!sessionServerNonce.equals(sNonce)) {
					invalidsNonce = true;
					try {
						JSONObject apzHeader = requestJson.getJSONObject(APPZILLON_HEADER);
						if (apzHeader.has(HEADER_ORIGINATION)) {
							appzHeader.remove(HEADER_ORIGINATION);
						}
						JSONArray appzErrors = new JSONArray();
						JSONObject error = new JSONObject();
						apzHeader.put(STATUS, false);
						error.put(APPZILLON_ERROR_MSG, "Invalid serverNonce");
						error.put(APPZILLON_ERROR_CODE, "APZ-CNT-331");
						appzErrors.put(0, error);
						errorResp.put(APPZILLON_HEADER, apzHeader);
						errorResp.put(APPZILLON_BODY, new JSONObject());
						errorResp.put(APPZILLON_ERRORS, appzErrors);
					} catch (JSONException je) {
						LOG.error("JSONException ", je);
					}
				}
			}

			if (!invalidsNonce) {
				// replay attack
				if (securityFlag && !APPZILLON_GET_APP_SEC_TOKENS_REQUEST.equals(requestType)) {
					requestJson.getJSONObject(APPZILLON_HEADER).put(SERVER_NONCE,
							request.getSession(false).getAttribute(SERVER_NONCE));
					requestJson.getJSONObject(APPZILLON_HEADER).put(SESSION_TOKEN,
							request.getSession(false).getAttribute(SESSION_TOKEN));
				}

				LOG.debug("Updated request json after replay changes: " + requestJson.toString());
				String requestString = requestJson.toString();

				String lHeader = requestJson.getJSONObject(APPZILLON_HEADER).toString();
				String lBody = requestJson.getJSONObject(APPZILLON_BODY).toString();
				JSONObject headerJSON = requestJson.getJSONObject(APPZILLON_HEADER);

				// when ency is disabled, ency setting for change pwd/forget pwd request
				if (NO.equals(WebProperties.getPayloadEncryptionReq())
						&& (APPZILLON_CHANGEPASSWORD.equals(requestType) || APPZILLON_FORGOTPSWD.equals(requestType))) {
					LOG.debug("Ency is N and Request is either change pwd or forgot pwd, adding safe bit");
					requestJson.getJSONObject(APPZILLON_HEADER).put(APPZILLON_SAFE_BIT, WebProperties.getSafeBit());
					requestString = requestJson.toString();
				}

				// data integrity for request
				if (securityFlag && YES.equals(WebProperties.getDataIntegrity())
						&& !appzillonReloadLogger.equals(requestType)) {
					if (!APPZILLON_GET_APP_SEC_TOKENS_REQUEST.equals(requestType)) {
						requestString = RSACryptoUtils.generatePayloadWithQOP(requestJson, lHeader, lBody);
					}
					LOG.debug("Updated request json string after data integrity: " + requestString);
				}

				// RSA Encryption for request
				if (securityFlag && YES.equals(WebProperties.getPayloadEncryptionReq())
						&& !appzillonReloadLogger.equals(requestType)) {
					String appzillonSafe = "";
					if (!APPZILLON_GET_APP_SEC_TOKENS_REQUEST.equals(requestType)) {
						appzillonSafe = (String) request.getSession(false).getAttribute(SAFE_TOKEN);
					} else {
						appzillonSafe = Utility.generateSecRandomOfLength(10);
					}
					if (RSACryptoUtils.publicKey == null) {
						RSACryptoUtils.readPublicKey();
					}
					requestString = RSACryptoUtils.encryptPayloadWithKey(requestString, lHeader, lBody, appzillonSafe,
							requestType);
					LOG.debug("Updated request json after payload encryption : " + requestString);
				}

				LOG.debug("Connecting to Appzillon Server url : " + WebProperties.getServerURL());
				URL url = null;

				// adding interfaceId id as part of the url
				LOG.debug("IFaceId required in URI :" + WebProperties.getIfaceIdInUri());
				if (WebProperties.getIfaceIdInUri().equalsIgnoreCase(YES) && headerJSON.has("interfaceId")) {
					LOG.debug("Inside IF : " + WebProperties.getServerURL() + "/services/"
							+ headerJSON.get("interfaceId"));
					url = new URL(WebProperties.getServerURL() + "/services/" + headerJSON.get("interfaceId"));
				} else {
					url = new URL(WebProperties.getServerURL());
				}

				String protocolType = url.getProtocol();

				if (protocolType.equalsIgnoreCase(HTTPS)) {
					LOG.debug("Connection protocol is https");
					// Installing SSL Certificates
					applySSL();
					// Establishing Connection
					urlConnHttps = (HttpsURLConnection) url.openConnection();
					// Setting Header Method type to POST
					urlConnHttps.setRequestMethod(POST);
					// Setting Header Content-Type to application/JSON
					urlConnHttps.setRequestProperty(CONTENT_TYPE, CONTENT_TYPE_APP_JSON);
					urlConnHttps.setDoOutput(true);
					// Getting OutputStream
					dtOpStrm = new DataOutputStream(urlConnHttps.getOutputStream());
					// Writing request to the output stream
					// dtOpStrm.write(Encode.forHtmlContent(requestString).getBytes());
					dtOpStrm.write(StringEscapeUtils.unescapeHtml(Encode.forHtmlContent(requestString)).getBytes());
					// Getting Response Code and Resposne Status Message
					responseCode = urlConnHttps.getResponseCode();
					responseMessage = urlConnHttps.getResponseMessage();
				} else {
					LOG.debug("Connection protocol is http");
					urlConnHttp = (HttpURLConnection) url.openConnection();
					// Setting Header Method type to POST
					urlConnHttp.setRequestMethod(POST);
					// Setting Header Content-Type to application/JSON
					urlConnHttp.setRequestProperty(CONTENT_TYPE, CONTENT_TYPE_APP_JSON);
					urlConnHttp.setDoOutput(true);
					// Getting OutputStream
					dtOpStrm = new DataOutputStream(urlConnHttp.getOutputStream());
					// Writing request to the ourestput stream
					// dtOpStrm.write(Encode.forHtmlContent(requestString).getBytes());
					dtOpStrm.write(StringEscapeUtils.unescapeHtml(Encode.forHtmlContent(requestString)).getBytes());
					// Getting Response Code and Resposne Status Message
					responseCode = urlConnHttp.getResponseCode();
					responseMessage = urlConnHttp.getResponseMessage();
				}
				LOG.debug("Response code is: " + responseCode + " Response Message is: " + responseMessage);
				if (responseCode == 200) {
					// Reading Response from Inputstream
					if (protocolType.equalsIgnoreCase(HTTPS)) {
						inpStrm = urlConnHttps.getInputStream();
					} else {
						inpStrm = urlConnHttp.getInputStream();
					}
					String responseJson = Utility.getStringFromInputStream(inpStrm);
					LOG.info("Response String Received from Appzillon Server -: " + responseJson);

					// RSA Response Decryption
					if (securityFlag && YES.equals(WebProperties.getPayloadEncryptionReq())
							&& !appzillonReloadLogger.equals(requestType)) {
						responseJson = RSACryptoUtils.decryptPayloadWithKey(responseJson);
						LOG.debug("Response after RSA decryption: " + responseJson);
					}

					// qop check for response
					if (securityFlag && YES.equals(WebProperties.getDataIntegrity())
							&& !APPZILLON_GET_APP_SEC_TOKENS_REQUEST.equals(requestType)
							&& !appzillonReloadLogger.equals(requestType)) {
						boolean lStat = Utility.checkQualityOfPayload(responseJson);
						LOG.info("DataIntegrity check is: " + lStat);
						if (!lStat) {
							JSONObject result = new JSONObject(responseJson);
							try {
								JSONObject apzHeader = result.getJSONObject(APPZILLON_HEADER);
								JSONArray appzErrors = new JSONArray();
								JSONObject error = new JSONObject();
								apzHeader.put(STATUS, false);
								error.put(APPZILLON_ERROR_MSG, "Invalid response");
								error.put(APPZILLON_ERROR_CODE, "APZ-CNT-330");
								appzErrors.put(0, error);
								result.put(APPZILLON_HEADER, apzHeader);
								result.put(APPZILLON_BODY, new JSONObject());
								result.put(APPZILLON_ERRORS, appzErrors);
								responseJson = result.toString();
							} catch (JSONException je) {
								LOG.error("JSONException ", je);
							}
						}
						LOG.info("Response String after Data integrity check : " + responseJson);
					}

					if (APPZILLON_LOGIN_REQ.equals(requestType)) {
						resObj = new JSONObject(responseJson);
						JSONObject appzillonHeader = resObj.getJSONObject(APPZILLON_HEADER);
						boolean respHeaderStatus = appzillonHeader.getBoolean(STATUS);
						if (respHeaderStatus) {
							JSONObject appzillonBody = resObj.getJSONObject(APPZILLON_BODY);
							JSONObject loginResponse = appzillonBody.getJSONObject(LOGIN_RESPONSE);
							boolean canProceed = loginResponse.getBoolean(STATUS);
							if (canProceed) {
								if (servletContext != null) {
									if (resObj.getJSONObject(APPZILLON_BODY).getJSONObject(LOGIN_RESPONSE)
											.has(USER_DET)) {
										JSONObject userSettings = FileUtils.readUserSettings(
												appzillonHeader.getString(HEADER_USER_ID), servletContext);
										resObj.getJSONObject(APPZILLON_BODY).getJSONObject(LOGIN_RESPONSE)
												.getJSONObject(USER_DET).put(USER_PREFS, userSettings);
									}
									populateCookieDetails(requestJson, request, resObj, response);
								} else {
									LOG.debug("ServerContext is null");
								}
								maintainServerSession(resObj, request, response, APPZILLON_LOGIN_REQ, servletContext);
							} else {
								if (Utility.isExternalAuthEnabled()) {
									AuthenticationService.maintainThirdPartyAuthProviderSession(request, response);
								}
							}
						} else {
							deleteCookie(request, response);
							if (Utility.isExternalAuthEnabled()) {
								AuthenticationService.maintainThirdPartyAuthProviderSession(request, response);
							}
						}
					} else if (APPZILLON_LOGOUT_REQ.equals(requestType)) {
						resObj = new JSONObject(responseJson);
						String finalResponse = Utility.getfinalResponse(request, response, resObj.toString());
						maintainServerSession(resObj, request, response, APPZILLON_LOGOUT_REQ, servletContext);
						Utility.sendResponse(request, response, finalResponse);

					} else if (apzParseMetaJSON.equals(requestType)) {
						resObj = new JSONObject(responseJson);
						JSONObject apzBody = resObj.getJSONObject(APPZILLON_BODY);
						JSONObject apzHeader = resObj.getJSONObject(APPZILLON_HEADER);
						if (!resObj.has(APPZILLON_ERRORS)) {
							Utility.createProductFiles(servletContext, apzBody, request, response, apzHeader);
							String scrId = requestJson.getJSONObject(APPZILLON_BODY).getString("scrId");
							JSONObject scrData = apzBody.getJSONObject(scrId);
							resObj.put(APPZILLON_BODY, scrData);
						} else {
							errorResp = new JSONObject();
							JSONObject error = new JSONObject();
							error.put(RESULT, FAILURE);
							apzHeader.put(STATUS, false);
							errorResp.put(APPZILLON_HEADER, apzHeader);
							errorResp.put(APPZILLON_BODY, error);
							resObj = errorResp;
						}
					} else {
						resObj = new JSONObject(responseJson);
						maintainServerSession(resObj, request, response, APPZILLON_SERVER_REQ, servletContext);
					}
					if (securityFlag) {
						// binding serverNonce for this websession.(Replay attack)
						if (APPZILLON_GET_APP_SEC_TOKENS_REQUEST.equals(requestType)) {
							HttpSession webSession = request.getSession(false);
							JSONObject lResObj = resObj.getJSONObject(APPZILLON_BODY)
									.getJSONObject(APPZILLON_GET_APP_SEC_TOKENS_RESPONSE);

							webSession.setAttribute(SERVER_NONCE, lResObj.getString(SERVER_NONCE));
							webSession.setAttribute(SESSION_TOKEN, lResObj.getString(SESSION_TOKEN));
							webSession.setAttribute(SAFE_TOKEN, lResObj.getString(SAFE_TOKEN));

							// lResObj.remove(SERVER_NONCE);
							lResObj.remove(SESSION_TOKEN);
							lResObj.remove(SAFE_TOKEN);

							resObj.getJSONObject(APPZILLON_BODY).put(APPZILLON_GET_APP_SEC_TOKENS_RESPONSE, lResObj);
						} else {// removing sessionToken,serverNonce and QOP for other reqs.
							resObj.getJSONObject(APPZILLON_HEADER).remove(SERVER_NONCE);
							resObj.getJSONObject(APPZILLON_HEADER).remove(SESSION_TOKEN);
							if (YES.equals(WebProperties.getDataIntegrity())) {
								resObj.remove(QOP);
							}
						}
					}
					if (resObj.has(APPZILLON_SAFE_BIT)) {
						resObj.remove(APPZILLON_SAFE_BIT);
					}
					responseJson = resObj.toString();
					LOG.debug("Response payload : " + responseJson);
					if (!APPZILLON_LOGOUT_REQ.equals(requestType))
						Utility.buildAndSendResponse(request, response, responseJson);
				} else {
					LOG.debug("ERROR !!!");
				}
			} else {
				Utility.buildAndSendResponse(request, response, errorResp.toString());
			}
		} catch (JSONException e) {
			LOG.error("CallInternalServer JSONException", e);
		} finally {
			if (inpStrm != null) {
				inpStrm.close();
			}
			if (dtOpStrm != null) {
				dtOpStrm.flush();
				dtOpStrm.close();
			}
		}
	}

	private static void populateCookieDetails(JSONObject requestJson, HttpServletRequest request,
			JSONObject responseJson, HttpServletResponse response) throws JSONException {
		JSONObject appzillonHeader = responseJson.getJSONObject(APPZILLON_HEADER);
		JSONObject appzillonBody = responseJson.getJSONObject(APPZILLON_BODY);
		JSONObject loginResponse = appzillonBody.getJSONObject(LOGIN_RESPONSE);
		if (WebProperties.getKeepMeSignedInEnabled().equals(YES) && appzillonHeader.has(SELECTOR)
				&& appzillonHeader.getString(SELECTOR) != null && !appzillonHeader.getString(SELECTOR).isEmpty()) {
			String encStr;
			try {
				encStr = CryptoPlugin.process(ENCRYPT_REQ, WebProperties.getServerToken(),
						appzillonHeader.getString(APPZILLON_SESSION_ID) + appzillonHeader.getString(SELECTOR)
								+ appzillonHeader.getString(HEADER_USER_ID));
				Utility.createCookie(request, response, WebProperties.getAppId(), encStr);
				request.getSession().setAttribute(HEADER_USER_ID, loginResponse.getJSONObject(USER_DET).getString(ID));
			} catch (Exception e) {
				LOG.error("failed to create cookie : " + e);
			}
		}
	}

	private static void maintainServerSession(JSONObject pRequestJSON, HttpServletRequest request,
			HttpServletResponse response, String requestType, ServletContext servletContext) throws JSONException {
		LOG.info("Maintaining Server Session for the Request Type : " + requestType);
		HttpSession session = request.getSession(false);
		JSONObject appzHeader = pRequestJSON.getJSONObject(APPZILLON_HEADER);
		String appzUserId = "";
		String appzSessId = "";
		String appzReqKey = "";
		JSONObject jsonObj = new JSONObject();

		if (requestType.equals(APPZILLON_LOGIN_REQ)) {
			String mastertoken = null;
			String csrfToken = (String) session.getAttribute(OWASP_CSRFTOKEN);
			String serverNonce = (String) session.getAttribute(SERVER_NONCE);
			String sessionToken = (String) session.getAttribute(SESSION_TOKEN);
			String safeToken = (String) session.getAttribute(SAFE_TOKEN);
			String exchange = (String) session.getAttribute(EXCHANGE);
			// External auth provider changes
			String authToken = (String) session.getAttribute(AUTH_TOKEN);
			String isTokenRefreshed = (String) session.getAttribute(IS_TOKEN_REFRESHED);
			Object principalSession = null;
			Object tokenCache = null;
			String authProvider = Utility.getAuthProvider();
			boolean isAzureAuthProvider = AUTH_PROVIDER_AAD.equalsIgnoreCase(authProvider)
					|| AUTH_PROVIDER_AAD_B2C.equalsIgnoreCase(authProvider);
			if (Utility.isExternalAuthEnabled()) {
				if (isAzureAuthProvider) {
					principalSession = request.getSession().getAttribute(PRINCIPAL_SESSION_NAME);
					tokenCache = request.getSession().getAttribute(TOKEN_CACHE_SESSION_ATTRIBUTE);
				}
			}
			// End
			if (!AUTH_PROVIDER_KEYCLOAK.equalsIgnoreCase(authProvider)) {
				LOG.debug("Invalidating existing session ...");
				session.invalidate();
				session = request.getSession(true);
				LOG.debug("Created New Session after successful Login : " + session);
			}
			Utility.setSessionTimeOut(session);
			session.setAttribute(OWASP_CSRFTOKEN, csrfToken);
			session.setAttribute(SERVER_NONCE, serverNonce);
			session.setAttribute(SESSION_TOKEN, sessionToken);
			session.setAttribute(SAFE_TOKEN, safeToken);
			session.setAttribute(EXCHANGE, exchange);

			if (appzHeader.has(SELECTOR) && appzHeader.getString(SELECTOR) != null
					&& !appzHeader.getString(SELECTOR).isEmpty()) {
				session.setAttribute(SELECTOR, appzHeader.getString(SELECTOR));
			}
			appzUserId = appzHeader.getString(HEADER_USER_ID);
			appzSessId = appzHeader.getString(APPZILLON_SESSION_ID);
			appzReqKey = appzHeader.getString(APPZILLON_REQUEST_KEY);
			session.setAttribute(SESSIONID, appzSessId);
			session.setAttribute(REQUESTKEY, appzReqKey);
			session.setAttribute(USERID, appzUserId);

			if (Utility.isExternalAuthEnabled()) {
				session.setAttribute(HEADER_USER_ID, appzUserId);
				session.setAttribute(IS_USER_AUTHENTICATED, YES);
				session.setAttribute(AUTH_TOKEN, authToken);
				session.setAttribute(IS_TOKEN_REFRESHED, isTokenRefreshed);
				session.setAttribute(PRINCIPAL_SESSION_NAME, principalSession);
				if (isAzureAuthProvider) {
					session.setAttribute(TOKEN_CACHE_SESSION_ATTRIBUTE, tokenCache);
				}
			}

		} else if (requestType.equals(APPZILLON_SERVER_REQ)) {
			appzReqKey = appzHeader.getString(APPZILLON_REQUEST_KEY);
			session.setAttribute(REQUESTKEY, appzReqKey);

		} else if (requestType.equals(APPZILLON_LOGOUT_REQ)) {
			LOG.debug("Invalidating session ...");
			deleteCookie(request, response);
			if (!AUTH_PROVIDER_KEYCLOAK.equalsIgnoreCase(Utility.getAuthProvider())) {
				session.invalidate();
			}
		}
		appzHeader.put(APPZILLON_SESSION_ID, DUMMYSESSION);
		appzHeader.put(APPZILLON_REQUEST_KEY, DUMMYREQUESTKEY);
		appzHeader.remove(SELECTOR);
		jsonObj.put(APPZILLON_HEADER, appzHeader);
	}

	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] c = request.getCookies();
		request.getSession().removeAttribute(SELECTOR);
		for (Cookie c1 : c) {
			String cookieName = Encode.forJava(c1.getName());
			if (cookieName.equals(WebProperties.getAppId())) {
				Cookie cookie = new Cookie(cookieName, Encode.forJava(c1.getValue()));
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}

	public static void applySSL() {
		String keyStorePath = null;
		String keyStoreCred = null;
		String trustStorePath = null;
		String trustStoreCred = null;
		String trustStrPath = WebProperties.getTrustStorePath();
		String trustStrPwd = WebProperties.getKeyStorePassword();
		LOG.debug("Reading trustStrPath :" + trustStrPath);
		String cloudName = PropertyUtils.getPropertyValue(AppzillonConstants.READ_PEM_FROM_VAULT);
		if (Utility.isNotNullOrEmpty(cloudName) && Utility.isNotNullOrEmpty(trustStrPath)
				&& Utility.isNotNullOrEmpty(trustStrPath) && cloudName.equalsIgnoreCase(CLOUD_AZURE)) {
			try {
				trustStorePath = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(trustStrPath),
						AuthTokenUtil.getAuthTokenKeyVault());
				trustStoreCred = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(trustStrPwd),
						AuthTokenUtil.getAuthTokenKeyVault());
			} catch (Exception e) {
				LOG.error(" Exception while getting details from azure vault", e);
			}
		} else {
			// LOG.debug("Reading trustStrPwd :" + trustStrPwd);
			if (trustStrPath != null && trustStrPath.startsWith(ENV_VARIABLE_PREFIX)) {
				// reading from environment variable
				LOG.debug("Reading truststore path from env variable :" + trustStrPath);
				trustStorePath = System
						.getenv(trustStrPath.replace(ENV_VARIABLE_PREFIX, "").replace(VARIABLE_SUFFIX, ""));
			} else if (trustStrPath != null && trustStrPath.startsWith(SYS_VARIABLE_PREFIX)) {
				// reading from system property
				LOG.debug("Reading truststore path from system property :" + trustStrPath);
				trustStorePath = System
						.getProperty(trustStrPath.replace(SYS_VARIABLE_PREFIX, "").replace(VARIABLE_SUFFIX, ""));
			} else {
				trustStorePath = trustStrPath;
			}
			if (trustStrPwd != null && trustStrPwd.startsWith(ENV_VARIABLE_PREFIX)) {
				// reading from environment variable
				LOG.debug("Reading truststore password from env variable :" + trustStrPwd);
				trustStoreCred = System
						.getenv(trustStrPwd.replace(ENV_VARIABLE_PREFIX, "").replace(VARIABLE_SUFFIX, ""));
			} else if (trustStrPwd != null && trustStrPwd.startsWith(SYS_VARIABLE_PREFIX)) {
				// reading from system property
				LOG.debug("Reading truststore password from system property :" + trustStrPwd);
				trustStoreCred = System
						.getProperty(trustStrPwd.replace(SYS_VARIABLE_PREFIX, "").replace(VARIABLE_SUFFIX, ""));
			} else {
				trustStoreCred = trustStrPwd;
			}
		}

		String keyStrPath = WebProperties.getKeyStorePath();
		String keyStrPwd = WebProperties.getKeyStorePassword();
		// LOG.debug("Reading keyStrPath :"+keyStrPath);
		// LOG.debug("Reading keyStrPwd :"+keyStrPwd);
		if (Utility.isNotNullOrEmpty(cloudName) && Utility.isNotNullOrEmpty(keyStrPath)
				&& Utility.isNotNullOrEmpty(keyStrPath) && cloudName.equalsIgnoreCase(CLOUD_AZURE)) {
			try {
				keyStorePath = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(keyStrPath),
						AuthTokenUtil.getAuthTokenKeyVault());
				keyStoreCred = AuthTokenUtil.getSecretValue(AuthTokenUtil.getVaultUrl(keyStrPwd),
						AuthTokenUtil.getAuthTokenKeyVault());
			} catch (Exception e) {
				LOG.error(" Exception while getting details from azure vault", e);
			}
		} else {
			if (keyStrPath != null && keyStrPath.startsWith(ENV_VARIABLE_PREFIX)) {
				// reading from environment variable
				LOG.debug("Reading keystore path from env variable :" + keyStrPath);
				keyStorePath = System.getenv(keyStrPath.replace(ENV_VARIABLE_PREFIX, "").replace(VARIABLE_SUFFIX, ""));
			} else if (keyStrPath != null && keyStrPath.startsWith(SYS_VARIABLE_PREFIX)) {
				// reading from system property
				LOG.debug("Reading keystore path from system property :" + keyStrPath);
				keyStorePath = System
						.getProperty(keyStrPath.replace(SYS_VARIABLE_PREFIX, "").replace(VARIABLE_SUFFIX, ""));
			} else {
				keyStorePath = keyStrPath;
			}
			if (keyStrPwd != null && keyStrPwd.startsWith(ENV_VARIABLE_PREFIX)) {
				// reading from environment variable
				LOG.debug("Reading keystore password from env variable :" + keyStrPwd);
				keyStoreCred = System.getenv(keyStrPwd.replace(ENV_VARIABLE_PREFIX, "").replace(VARIABLE_SUFFIX, ""));
			} else if (keyStrPwd != null && keyStrPwd.startsWith(SYS_VARIABLE_PREFIX)) {
				// reading from system property
				LOG.debug("Reading keystore password from system property :" + keyStrPwd);
				keyStoreCred = System
						.getProperty(keyStrPwd.replace(SYS_VARIABLE_PREFIX, "").replace(VARIABLE_SUFFIX, ""));
			} else {
				keyStoreCred = keyStrPwd;
			}
		}

		LOG.debug("Actual Keystore Path  :" + keyStorePath);
		// LOG.debug("Actual Keystore Password :" + keyStorePassword);
		if (keyStorePath != null && keyStorePath.trim().length() > 0) {
			System.setProperty(SYSTEM_PROPERTY_SSL_KEY_STORE, keyStorePath.trim());
			System.setProperty(SYSTEM_PROPERTY_SSL_KEY_STORE_PASSWORD, keyStoreCred.trim());
		}

		// LOG.debug("Actual truststore Path :"+trustStorePath);
		// LOG.debug("Actual truststore Password :"+trustStorePassword);
		if (trustStorePath != null && trustStorePath.trim().length() > 0) {
			System.setProperty(SYSTEM_PROPERTY_SSL_TRUST_STORE, trustStorePath.trim());
			System.setProperty(SYSTEM_PROPERTY_SSL_TRUST_STORE_PASSWORD, trustStoreCred.trim());
		}
	}

	public static void getProductJson(JSONObject requestJson, HttpServletRequest request, HttpServletResponse response,
			String requestType, ServletContext servletContext) throws IOException {
		InputStream inpStrm = null;
		JSONObject resObj = null;
		int responseCode;
		String responseMessage = "";
		HttpURLConnection urlConnHttp = null;
		HttpsURLConnection urlConnHttps = null;
		DataOutputStream dtOpStrm = null;
		PrintWriter out = response.getWriter();
		try {
			JSONObject appzHeader = requestJson.getJSONObject(APPZILLON_HEADER);
			appzHeader.put(HEADER_ORIGINATION, request.getRemoteAddr());
			requestJson.put(APPZILLON_HEADER, appzHeader);
			boolean invalidsNonce = false;
			JSONObject errorResp = new JSONObject();
			String replayAttackFlag = PropertyUtils.getPropertyValue(REPLAY_REQUEST_REQUIRED);
			if (Utility.isNotNullOrEmpty(replayAttackFlag) && NO.equalsIgnoreCase(replayAttackFlag)) {
				securityFlag = false;
			}
			String sNonce = appzHeader.getString(SERVER_NONCE);
			if (sNonce == null) {
				invalidsNonce = true;
				try {
					JSONObject apzHeader = requestJson.getJSONObject(APPZILLON_HEADER);
					JSONArray appzErrors = new JSONArray();
					JSONObject error = new JSONObject();
					apzHeader.put(STATUS, false);
					error.put(APPZILLON_ERROR_MSG, "Invalid ServerNonce");
					error.put(APPZILLON_ERROR_CODE, "APZ-CNT-331");
					appzErrors.put(0, error);
					errorResp.put(APPZILLON_HEADER, apzHeader);
					errorResp.put(APPZILLON_BODY, new JSONObject());
					errorResp.put(APPZILLON_ERRORS, appzErrors);
				} catch (JSONException je) {
					LOG.error("JSONException ", je);
				}
			}

			if (!invalidsNonce) {
				LOG.debug("Updated request json after replay changes: " + requestJson.toString());
				String requestString = requestJson.toString();

				String lHeader = requestJson.getJSONObject(APPZILLON_HEADER).toString();
				String lBody = requestJson.getJSONObject(APPZILLON_BODY).toString();

				// data integrity for request
				if (securityFlag && YES.equals(WebProperties.getDataIntegrity())) {
					if (!APPZILLON_GET_APP_SEC_TOKENS_REQUEST.equals(requestType)) {
						requestString = RSACryptoUtils.generatePayloadWithQOP(requestJson, lHeader, lBody);
					}
					LOG.debug("Updated request json string after data integrity: " + requestString);
				}

				// RSA Encryption for request
				if (securityFlag && YES.equals(WebProperties.getPayloadEncryptionReq())) {
					String appzillonSafe = "";
					if (!APPZILLON_GET_APP_SEC_TOKENS_REQUEST.equals(requestType)) {
						appzillonSafe = appzHeader.getString(SAFE_TOKEN);
					} else {
						appzillonSafe = Utility.generateSecRandomOfLength(10);
						RSACryptoUtils.readPublicKey();
					}
					requestString = RSACryptoUtils.encryptPayloadWithKey(requestString, lHeader, lBody, appzillonSafe,
							requestType);
					LOG.debug("Updated request json after payload encryption : " + requestString);
				}
				LOG.debug("Connecting to Appzillon Server url : " + WebProperties.getServerURL());

				URL url = new URL(WebProperties.getServerURL());
				String protocolType = url.getProtocol();

				if (protocolType.equalsIgnoreCase(HTTPS)) {
					LOG.debug("Connection protocol is https");
					// Installing SSL Certificates
					applySSL();
					// Establishing Connection
					urlConnHttps = (HttpsURLConnection) url.openConnection();
					// Setting Header Method type to POST
					urlConnHttps.setRequestMethod(POST);
					// Setting Header Content-Type to application/JSON
					urlConnHttps.setRequestProperty(CONTENT_TYPE, CONTENT_TYPE_APP_JSON);
					urlConnHttps.setDoOutput(true);
					// Getting OutputStream
					dtOpStrm = new DataOutputStream(urlConnHttps.getOutputStream());
					// Writing request to the output stream
					dtOpStrm.write(Encode.forHtmlContent(requestString).getBytes());
					// Getting Response Code and Resposne Status Message
					responseCode = urlConnHttps.getResponseCode();
					responseMessage = urlConnHttps.getResponseMessage();
				} else {
					LOG.debug("Connection protocol is http");
					urlConnHttp = (HttpURLConnection) url.openConnection();
					// Setting Header Method type to POST
					urlConnHttp.setRequestMethod(POST);
					// Setting Header Content-Type to application/JSON
					urlConnHttp.setRequestProperty(CONTENT_TYPE, CONTENT_TYPE_APP_JSON);
					urlConnHttp.setDoOutput(true);
					// Getting OutputStream
					dtOpStrm = new DataOutputStream(urlConnHttp.getOutputStream());
					// Writing request to the ourestput stream
					dtOpStrm.write(Encode.forHtmlContent(requestString).getBytes());
					// Getting Response Code and Resposne Status Message
					responseCode = urlConnHttp.getResponseCode();
					responseMessage = urlConnHttp.getResponseMessage();
				}
				LOG.debug("Response code is: " + responseCode + " Response Message is: " + responseMessage);
				if (responseCode == 200) {
					// Reading Response from Inputstream
					if (protocolType.equalsIgnoreCase(HTTPS)) {
						inpStrm = urlConnHttps.getInputStream();
					} else {
						inpStrm = urlConnHttp.getInputStream();
					}
					String responseJson = Utility.getStringFromInputStream(inpStrm);
					LOG.info("Response String Received from Appzillon Server -: " + responseJson);

					// RSA Response Decryption
					if (securityFlag && YES.equals(WebProperties.getPayloadEncryptionReq())) {
						responseJson = RSACryptoUtils.decryptPayloadWithKey(responseJson);
						LOG.debug("Response after RSA decryption: " + responseJson);
					}
					resObj = new JSONObject(responseJson);
					// qop check for response
					if (securityFlag && YES.equals(WebProperties.getDataIntegrity())
							&& !APPZILLON_GET_APP_SEC_TOKENS_REQUEST.equals(requestType)) {
						boolean lStat = Utility.checkQualityOfPayload(responseJson);
						LOG.info("DataIntegrity check is: " + lStat);
						if (!lStat) {
							JSONObject result = new JSONObject(responseJson);
							try {
								JSONObject apzHeader = result.getJSONObject(APPZILLON_HEADER);
								JSONArray appzErrors = new JSONArray();
								JSONObject error = new JSONObject();
								apzHeader.put(STATUS, false);
								error.put(APPZILLON_ERROR_MSG, "Invalid response");
								error.put(APPZILLON_ERROR_CODE, "APZ-CNT-330");
								appzErrors.put(0, error);
								result.put(APPZILLON_HEADER, apzHeader);
								result.put(APPZILLON_BODY, new JSONObject());
								result.put(APPZILLON_ERRORS, appzErrors);
								responseJson = result.toString();
							} catch (JSONException je) {
								LOG.error("JSONException ", je);
							}
						}
						LOG.info("Response String after Data integrity check : " + responseJson);
					}

					if (apzParseProductJSON.equals(requestType)) {
						JSONObject apzBody = resObj.getJSONObject(APPZILLON_BODY);
						JSONObject apzHeader = resObj.getJSONObject(APPZILLON_HEADER);
						if (!resObj.has(APPZILLON_ERRORS)) {
							Utility.createProductFiles(servletContext, apzBody, request, response, apzHeader);
							// yet to do changes
							// Utility.buildAndSendResponse(request,response,
							// requestJson.getJSONObject(APPZILLON_HEADER).toString());
							Utility.sendCitiSuccessResponse(request, response, apzHeader);
						} else {
							Utility.sendCitiFailureResponse(request, response, apzHeader);
							// UploadFile.sendFailureResponse(request,response,
							// resObj.getJSONObject(APPZILLON_ERRORS).getString(APPZILLON_ERROR_MSG),resObj.getJSONObject(APPZILLON_ERRORS).getString(APPZILLON_ERROR_CODE));
						}
					}
					// LOG.debug("Response payload : " + responseJson);
				} else {
					LOG.debug("ERROR !!!");
				}
			} else {
				Utility.sendResponse(request, response, errorResp.toString());
			}
		} catch (JSONException e) {
			LOG.error("CallInternalServer JSONException", e);
		} finally {

			if (inpStrm != null) {
				inpStrm.close();
			}
			if (out != null) {
				out.flush();
				out.close();
			}
			if (dtOpStrm != null) {
				dtOpStrm.flush();
				dtOpStrm.close();
			}

		}

	}
}
