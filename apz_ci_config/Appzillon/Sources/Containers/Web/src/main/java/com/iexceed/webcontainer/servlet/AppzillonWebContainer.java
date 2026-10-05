package com.iexceed.webcontainer.servlet;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;
import static com.iexceed.webcontainer.utils.AppzillonConstants.IS_USER_AUTHENTICATED;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.iexceed.webcontainer.authentication.AuthenticationService;
import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.plugins.CryptoPlugin;
import com.iexceed.webcontainer.plugins.DownloadFile;
import com.iexceed.webcontainer.plugins.UploadFile;
import com.iexceed.webcontainer.utils.CallInternalServer;
import com.iexceed.webcontainer.utils.FileUtils;
import com.iexceed.webcontainer.utils.PropertyUtils;
import com.iexceed.webcontainer.utils.WebProperties;
import com.iexceed.webcontainer.utils.hash.GenerateHashedPin;
import com.iexceed.webcontainer.utils.hash.IAppzillonHashing;
import com.iexceed.webcontainer.utils.hash.Utility;
import com.iexceed.webcontainer.utils.json.JSONArray;
import com.iexceed.webcontainer.utils.json.JSONException;
import com.iexceed.webcontainer.utils.json.JSONObject;
import org.owasp.encoder.Encode;

public class AppzillonWebContainer extends HttpServlet {

	private static final long serialVersionUID = -5726317205907600251L;

	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(AppzillonWebContainer.class.getName());

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcessRequest(request, response);
	}

	public enum ActionID {
		appzillonAuthenticationRequest, LOADSETTINGS, USERSETTINGS, appzillonReLoginRequest, UPLOAD, UPLOADAUTH, DOWNLOADAUTH, appzillonLogoutRequest, UPLOADWS, appzillonForgotPassword, appzillonChangePassword, DOWNLOADFILE, DOWNLOADWS, appzillonMaps, appzillonGetAppSecTokens, payloadEncryption, appzillonCheckCookie, apzParseMetaJSON, apzPersistHTMLInfo,appzillonReloadLogger, appzillonGetKey;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doProcessRequest(request, response);
	}

	private void doProcessRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> map = new HashMap<String, String>();
        // get header values from request object
		Enumeration<?> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}
		
		JSONObject requestJson = Utility.getJsonRequest(request, response);
		if((map.containsKey("Content-Length")||map.containsKey("content-length"))&&((map.containsKey("transfer-encoding")))||(map.containsKey("Transfer-Encoding"))){
			LOG.debug("Invalid Request...");
			JSONObject result = requestJson;
			try {
				JSONObject appzHeader = result.getJSONObject(APPZILLON_HEADER);
				JSONArray appzErrors = new JSONArray();
				JSONObject error = new JSONObject();
				appzHeader.put(STATUS, false);
				error.put(APPZILLON_ERROR_MSG, "invalid payload");
				error.put(APPZILLON_ERROR_CODE, PAYLOAD_ENCRYPTION_ERROR_CODE);
				appzErrors.put(0, error);
				result.put(APPZILLON_HEADER, appzHeader);
				result.put(APPZILLON_BODY, new JSONObject());
				result.put(APPZILLON_ERRORS, appzErrors);
				Utility.sendResponse(request,response, result.toString());
			} catch (JSONException je) {
				LOG.error("JSONException ", je);
			}
		}else {
					
		LOG.info("Processing a Request");
		// Setting Response Character Encoding
		response.setCharacterEncoding(UTF_8);
		// Setting Cache Control
		//response.setHeader(CACHE_CONTROL, CACHE_CONTROL_VAL);
		String action = "";
		// Reading ActionID from Request JSON Payload
		if (requestJson != null) {
			action = Utility.getActionId(requestJson);
		}
		// ActionID is either not present or is found to be null hence
		// considering it to be the launch of the application
		if (action == null || action.isEmpty()) {
			// Session handling
			String fedUrl = PropertyUtils.getPropertyValue(FEDERATION_URL);
			LOG.debug("Checking for URL from the properties file : " + fedUrl);
			if(Utility.isNotNullOrEmpty(fedUrl) || Utility.isExternalAuthEnabled()){
				HttpSession session = request.getSession(false);
				LOG.debug("Checking for the existing Session -: " + session);
				if(session == null){
					createSession(request, response, action);
				}
			}
			else {
				HttpSession session = request.getSession(false);
				if(session == null){
				 LOG.debug("Session does not exist at launch page. Creating new session");
				 createSession(request, response, action);
			    }else{
			    	LOG.debug("Session already exists at the launch of the page. Skipping session creation." + session);
			    }
			}

			// Setting Webcontainer's Default Settings and app properties in
			// request object to be set in JS layer
			request.setAttribute(DEFAULT_SETTINGS_ATTR, WebProperties.getLoadDefaultSettings());
			request.setAttribute(APP_PROP_ATTR, WebProperties.getAppProps());
			//request.setAttribute(FUSION_CHART_KEY, System.getenv(FUSION_CHART_KEY));
			request.setAttribute(APZ_FUSION_CHART_KEY, PropertyUtils.getFusionChartKey());
			// Reading all data from context
			String contextDetails = Utility.getContextDetails(request, response);
			request.setAttribute(APZILLON_ARGS_ATTR, contextDetails);
			//request.setAttribute(EXCHANGE, WebProperties.getAppzillonSafeKey());
			request.setAttribute(PAYLOAD_ENCRYPTION_FLAG, WebProperties.getPayloadEncryptionReq());

			// Looking up Request Processor Bean and invoking processor's
			// implementation
			LOG.debug("Checking for RequestProcessor Bean " + response.isCommitted());
			Utility.getRequestProcessorBean(request, response, WebProperties.getAppId());

			// Checking the application validity
			LOG.debug("Checking for the License validity");
			if (Utility.validateLicense()) {

				if(Utility.isExternalAuthEnabled() && !response.isCommitted()){
					LOG.debug("External auth enabled");
					AuthenticationService.performAuthentication(request,response);
				}

				if (!response.isCommitted()) {
					LOG.debug("Dispatching request to Appzillon FirstPage jsp to launch first HTML page....");
					RequestDispatcher dispatch = request.getRequestDispatcher(WebProperties.getAppId() + ".jsp");
					dispatch.forward(request, response);
					return;
				} else {
					LOG.debug("Cannot forward as response has been committed");
				}
			} else {
				request.setAttribute(MESSAGE, "Application has expired");
				RequestDispatcher dispatch = request
						.getRequestDispatcher("/apps/" + WebProperties.getAppId() + "/screens/AppMessage.jsp");
				dispatch.forward(request, response);
				return;
			}
		} else {
			//request for a new token when auth token is expired
			if(Utility.isExternalAuthEnabled() ) {
				LOG.debug("Checking if auth token is expired or not");
				AuthenticationService.checkIfAuthTokenHasExpired(request, response, requestJson);
			}
			// ActionID is present, hence creating session and processing the
			// request based on the Action ID

			// Processing Appzillon Action
			processAppzillonAction(request, response, action, requestJson, getServletContext());
		}
	  }
	}



	public void processAppzillonAction(HttpServletRequest request, HttpServletResponse response, String action,
									   JSONObject requestJson, ServletContext servletContext) throws IOException, ServletException {
		LOG.info("Processing Actions");
		HttpSession session = request.getSession(false);
		String lReqBody = null;
		try {
			lReqBody = requestJson.getString(APPZILLON_BODY);
		} catch (JSONException je) {
			LOG.error("JSONException", je);
		}
		if (session != null) {
			try {
				switch (ActionID.valueOf(action)) {
				case appzillonAuthenticationRequest:
					LOG.info("Calling Log in");
					appzillonLoginAction(request, response, requestJson, servletContext);
					break;

				case appzillonReLoginRequest:
					LOG.info("Calling ReLogin");
					appzillonLoginAction(request, response, requestJson, servletContext);
					break;
				
				case appzillonCheckCookie:
					LOG.info("checking for cookie");
					Cookie c[] = request.getCookies();
					boolean cookieExist = false;
					for(Cookie c1 : c) {
						if(c1.getName().equals(WebProperties.getAppId())) {
							cookieExist = true;
							LOG.info("cookie found for the app validating cookie");
							appzillonLoginActionUsingCookie(request, response, requestJson, servletContext, c1);
							break;
						}
					}
					if(!cookieExist) {
						Utility.buildAndSendResponse(request, response, requestJson.toString());
					}
					break;
					
					
				case USERSETTINGS:
					LOG.info("Calling updating user settings");
					createUpdateUserSettings(request, response, session, lReqBody);
					break;

				case LOADSETTINGS:
					LOG.info("Calling load settings");
					String userId = (String) request.getSession(false).getAttribute(USERID);
					if (userId != null && !userId.isEmpty()) {
						String userSettings = FileUtils.readUserSettings(userId, servletContext).toString();
						this.sendSuccessResponse(request, response, userSettings);
					} else {
						this.sendSuccessResponse(request, response, WebProperties.getLoadDefaultSettings());
					}
					break;

				case UPLOAD:
					LOG.info("Calling Upload");
					UploadFile.upload(request, response, servletContext);
					break;

				case UPLOADAUTH:
					LOG.info("Calling Upload with Auth");
					UploadFile.upload(request, response, servletContext);
					break;

				case UPLOADWS:
					LOG.info("Calling Upload without session");
					UploadFile.upload(request, response, servletContext);
					break;

				case DOWNLOADFILE:
					LOG.info("Calling Download");
					DownloadFile.download(request, response, requestJson, servletContext);
					break;

				case DOWNLOADAUTH:
					LOG.info("Calling Download with Auth");
					DownloadFile.download(request, response, requestJson, servletContext);
					break;

				case DOWNLOADWS:
					LOG.info("Calling Download without session");
					DownloadFile.download(request, response, requestJson, servletContext);
					break;
					
				case appzillonGetKey:
					LOG.info("Getting key for js encryption");
					JSONObject resultJSON = new JSONObject();
					String exchange = Utility.generateSecRandomOfLength(16);
					if(request.getSession(false).getAttribute(EXCHANGE)!= null) {
						exchange = (String) request.getSession(false).getAttribute(EXCHANGE);
					}
					try {
						resultJSON.put(EXCHANGE, exchange);
					} catch (JSONException je) {
						LOG.error("JSONException ", je);
					}
					request.getSession(false).setAttribute(EXCHANGE, exchange);
					PrintWriter out = response.getWriter();
					out.print(Encode.forHtmlContent(resultJSON.toString()));
					out.flush();
					out.close();
					break;

				/*case ENCRYPT:
					LOG.info("Calling Encrypt");
					CryptoPlugin.encryptData(request, response, lReqBody);
					break;

				case DECRYPT:
					LOG.info("Calling Decrypt");
					CryptoPlugin.decryptData(request, response, lReqBody);
					break;*/

				case appzillonLogoutRequest:
					LOG.debug("Calling Logging Out");
					CallInternalServer.callServer(requestJson, request, response, APPZILLON_LOGOUT_REQ, servletContext);
					break;

				case appzillonChangePassword:
					LOG.info("Calling Change password");
					changePassword(request, response, requestJson, servletContext);
					break;

				case appzillonForgotPassword:
					LOG.info("Calling Forgot password");
					forgotPassword(request, response, requestJson, servletContext);
					break;

				case appzillonMaps:
					LOG.info("Loading Maps");
					sendSuccessResponse(request, response, SUCCESSCAPS);
					break;
					
				case apzParseMetaJSON:
					CallInternalServer.callServer(requestJson, request, response, apzParseMetaJSON, servletContext);
					break;
					
				case apzPersistHTMLInfo:
					JSONObject apzBody = new JSONObject();
					JSONObject apzHeader = new JSONObject();
					try {
						String filePath = servletContext.getRealPath("/apps/"+ WebProperties.getAppId() + "/screens/products/");
						apzBody = requestJson.getJSONObject(APPZILLON_BODY);
						apzHeader = requestJson.getJSONObject(APPZILLON_HEADER);
						String fileName = apzBody.getString("scrId");
						apzBody.remove("scrId");
						Utility.createFile(filePath, fileName, apzBody.toString(),request, response, apzHeader);
						Utility.sendCitiSuccessResponse(request,response, apzHeader);
					} catch (JSONException e) {
						LOG.error("JSONException : " + e );
					}
					break;
	
				/*
				 * case appzillonGetAppSecTokens: LOG.info("Getting app server nonce");
				 * getAppSecTokens(request, response, requestJson,
				 * APPZILLON_GET_APP_SEC_TOKENS_REQUEST, servletContext); break;
				 */
					
				case appzillonGetAppSecTokens:
					LOG.info("Getting app server nonce");
					String sNonce = (String) request.getSession(false).getAttribute(SERVER_NONCE);
					//changes done to handle multiple calls of getappsecTokens with same webSessionId.
					LOG.debug("Checking for the sNonce does exist before calling appzillonGetAppSecTokens : " + sNonce);
					if (Utility.isNullOrEmpty(sNonce)) {
						getAppSecTokens(request, response, requestJson, APPZILLON_GET_APP_SEC_TOKENS_REQUEST,
								servletContext);
					} else {
						LOG.debug("constructing setGetAppSecResponse from webconatainer");
						JSONObject appSecTokenRes = null;
						try {
							appSecTokenRes = Utility.setGetAppSecResponse(request, response, requestJson);
							Utility.buildAndSendResponse(request, response, appSecTokenRes.toString());
						} catch (JSONException e) {
							LOG.error("JSONException ", e);
						}
					}
					break;
					
					
					
				case payloadEncryption:
					LOG.info("payload Encryption ...");
					try {
						requestJson.getJSONObject(APPZILLON_BODY).remove(ACTION_ID);
						LOG.debug("Removed action id from the payload ... " + requestJson.toString(0));
					} catch (JSONException je) {
						LOG.error("JSONException ", je);
					}
					Utility.buildAndSendResponse(request, response, requestJson.toString());
					break;
				case appzillonReloadLogger:
					LOG.info("Reload Logger");
					CallInternalServer.callServer(requestJson, request, response, appzillonReloadLogger, servletContext);
					break;
					
				}
			} catch (IllegalArgumentException e) {
				LOG.info("Calling other Server Requests");
				CallInternalServer.callServer(requestJson, request, response, "", servletContext);
			}
		} else {
			LOG.debug("A valid session does not exit");
			JSONObject result = requestJson;
			try {
				JSONObject reqObj = requestJson.getJSONObject(APPZILLON_HEADER);
				if(reqObj.has(APPZILLON_INTERFACEID) && reqObj.getString(APPZILLON_INTERFACEID).equals(apzParseProductJSON)) {
					CallInternalServer.getProductJson(requestJson, request, response, apzParseProductJSON, servletContext);
				} else {
				JSONObject appzHeader = result.getJSONObject(APPZILLON_HEADER);
				JSONArray appzErrors = new JSONArray();
				JSONObject error = new JSONObject();
				appzHeader.put(STATUS, false);
				error.put(APPZILLON_ERROR_MSG, "You have been logged out of the system due to inactivity.");
				error.put(APPZILLON_ERROR_CODE, "APZ-SMS-EX-003");
				appzErrors.put(0, error);
				result.put(APPZILLON_HEADER, appzHeader);
				result.put(APPZILLON_BODY, new JSONObject());
				result.put(APPZILLON_ERRORS, appzErrors);
				Utility.buildAndSendResponse(request, response, result.toString());
				}
			} catch (JSONException je) {
				LOG.error("JSONException ", je);
			}
		}
	}
	

	private void getAppSecTokens(HttpServletRequest request, HttpServletResponse response, JSONObject requestJson,
			String requestType, ServletContext servletContext) {
		String deviceId = request.getSession(false).getId();
		try {
			requestJson.getJSONObject(APPZILLON_BODY).getJSONObject(APPZILLON_GET_APP_SEC_TOKENS_REQUEST).put(DEVICE_ID,
						deviceId);
			CallInternalServer.callServer(requestJson, request, response, requestType, servletContext);
		} catch (JSONException e) {
			LOG.error("JSONException", e);
		} catch (IOException e) {
			LOG.error("IOException", e);
		}
	}

	private void forgotPassword(HttpServletRequest request, HttpServletResponse response, JSONObject requestJson,
			ServletContext servletContext) throws IOException {
		try {
			String password = requestJson.getJSONObject(APPZILLON_BODY).getJSONObject(APPZILLON_FORGOTPSWD_REQ)
					.getString("password").trim();
			String encryptedPwd = CryptoPlugin.process(1, WebProperties.getServerToken(), password);
			LOG.debug("forgotPassword CipherString :" + encryptedPwd);
			requestJson.getJSONObject(APPZILLON_BODY).getJSONObject(APPZILLON_FORGOTPSWD_REQ).put("password",
					encryptedPwd);
		} catch (JSONException e) {
			LOG.error("JSONException", e);
		} catch (InvalidKeyException e) {
			LOG.error("InvalidKeyException ", e);
		} catch (NoSuchAlgorithmException e) {
			LOG.error("NoSuchAlgorithmException ", e);
		} catch (InvalidKeySpecException e) {
			LOG.error("InvalidKeySpecException ", e);
		} catch (NoSuchPaddingException e) {
			LOG.error("NoSuchPaddingException ", e);
		} catch (InvalidAlgorithmParameterException e) {
			LOG.error("InvalidAlgorithmParameterException ", e);
		} catch (UnsupportedEncodingException e) {
			LOG.error("UnsupportedEncodingException ", e);
		} catch (IllegalBlockSizeException e) {
			LOG.error("IllegalBlockSizeException ", e);
		} catch (BadPaddingException e) {
			LOG.error("BadPaddingException ", e);
		}catch (Exception ex){
			LOG.error("Exception ", ex);
		}
		LOG.debug("Updated Request " + requestJson.toString());
		CallInternalServer.callServer(requestJson, request, response, APPZILLON_CHANGEPASSWORD, servletContext);
	}

	private void changePassword(HttpServletRequest request, HttpServletResponse response, JSONObject requestJson,
			ServletContext servletContext) throws IOException {
		LOG.debug("Is OTP generation required: " + WebProperties.getAuthenticationType());
		try {
			requestJson = generateOTP(request,response,requestJson,APPZILLON_CHANGEPASSWORD,servletContext);
		} catch (JSONException je) {
			LOG.error("JSONException", je);
		}
		CallInternalServer.callServer(requestJson, request, response, APPZILLON_CHANGEPASSWORD, servletContext);
	}

	private void createUpdateUserSettings(HttpServletRequest request,
										  HttpServletResponse response, HttpSession session, String lReqBody)
			throws IOException {
		String userId = (String) session.getAttribute(USERID);
		try {
			if ((userId == null) || (userId.isEmpty())) {
				userId = DEFAULT_SETTINGS;
			}
			String userPrefs = new JSONObject(lReqBody).getString("userPrefs");
			LOG.debug("UserId: " + userId);
			String settingsFilePath = WebProperties.getSettingsPath() + WebProperties.getAppId() + File.separator
					+ userId + File.separator + SETTINGSDATAJSON;
			File settingsFile = new File(settingsFilePath);
			FileUtils.writeFileContent(settingsFile, userPrefs, false);
			this.sendSuccessResponse(request, response, SUCCESS);

		} catch (IOException ioe) {
			this.sendFailureResponse(request, response);
			LOG.error("Error in updating User Settings ", ioe);
		} catch (JSONException e) {
			LOG.error("userPrefs not found ", e);
		}
	}

	public void appzillonLoginAction(HttpServletRequest request, HttpServletResponse response, JSONObject requestJson,
									 ServletContext servletContext) throws ServletException, IOException {
		try {
			LOG.debug(
					"Inside appzillon Login Action and Authentication type : " + WebProperties.getAuthenticationType());
			if (AUTHENTICATION_TYPE_DEVICE_ID.equals(WebProperties.getAuthenticationType())) {
				requestJson = generateOTP(request,response,requestJson, APPZILLON_LOGIN_REQ,servletContext);
			}
			if(!(requestJson.getJSONObject(APPZILLON_BODY).getJSONObject(LOGIN_REQUEST).has(KEE_ME_SIGNED_IN_FLAG) && 
					requestJson.getJSONObject(APPZILLON_BODY).getJSONObject(LOGIN_REQUEST)
					.getString(KEE_ME_SIGNED_IN_FLAG).equalsIgnoreCase(YES) && 
					!WebProperties.getKeepMeSignedInEnabled().equals(YES))) {
				CallInternalServer.callServer(requestJson, request, response, APPZILLON_LOGIN_REQ, servletContext);
			} else {
				JSONObject errorResp = new JSONObject();
				JSONObject apzHeader = requestJson.getJSONObject(APPZILLON_HEADER);
				JSONArray appzErrors = new JSONArray();
				JSONObject error = new JSONObject();
				apzHeader.put(STATUS, false);
				error.put(APPZILLON_ERROR_MSG, "Invalid request");
				error.put(APPZILLON_ERROR_CODE, "APZ_RS_010");
				appzErrors.put(0, error);
				errorResp.put(APPZILLON_HEADER, apzHeader);
				errorResp.put(APPZILLON_BODY, new JSONObject());
				errorResp.put(APPZILLON_ERRORS, appzErrors);
				Utility.buildAndSendResponse(request, response, errorResp.toString());
			}
		} catch (JSONException jse) {
			LOG.error("Error in executing Login Request JSONException", jse);
		} catch (Exception ex) {
			LOG.error("Error in executing Login Request", ex);
		}
	}

	public void appzillonLoginActionUsingCookie(HttpServletRequest request, HttpServletResponse response, JSONObject requestJson,
			ServletContext servletContext, Cookie cookie) throws ServletException, IOException {
		try {
				LOG.debug("Inside appzillon Login Action using Cookie : ");
				JSONObject reqObj = new JSONObject();
				reqObj.put("ifacesAccessType", "N");
				reqObj.put("controlsAccessType", "N");
				reqObj.put("scrsAccessType", "N");
				reqObj.put("deviceId", DEVICE_ID_WEB);
				requestJson.getJSONObject(APPZILLON_BODY).put(LOGIN_REQUEST, reqObj);
				HttpSession session = request.getSession(false);
				String cValue = CryptoPlugin.process(DECRYPT_REQ, WebProperties.getServerToken(), cookie.getValue());
				session.setAttribute(SESSIONID, cValue.substring(0, 32));
				session.setAttribute(SELECTOR, cValue.substring(32, 48));
				requestJson.getJSONObject(APPZILLON_HEADER).put(HEADER_USER_ID, cValue.substring(48, cValue.length()));
				CallInternalServer.callServer(requestJson, request, response, APPZILLON_LOGIN_REQ, servletContext);
		} catch (JSONException jse) {
			LOG.error("Error in executing Login Request JSONException", jse);
		} catch (Exception ex) {
			LOG.error("Error in executing Login Request", ex);
		}
	}
	
	private JSONObject generateOTP(HttpServletRequest request,HttpServletResponse response,JSONObject requestJson, String action,
			ServletContext servletContext) throws JSONException, IOException {
		LOG.debug("Generating OTP");
		String overrideOTP = WebProperties.getOverrideOTP();
		if (overrideOTP != null && YES.equalsIgnoreCase(overrideOTP)) {
			LOG.debug("Override OTP : " + overrideOTP);
			requestJson = Utility.getOTPdetails(requestJson);
		} else {
			IAppzillonHashing otp = new GenerateHashedPin();
			requestJson = otp.generateHashedPin(requestJson, action);
		}
		return requestJson;
	}

	private void createSession(HttpServletRequest request, HttpServletResponse response, String action) {
		HttpSession session = request.getSession(false);
		LOG.debug("Existing Session -: " + session);
		String serverNonce = null, sessionToken = null, safeToken = null, csrfToken = null,  exchange=null;
		//External auth changes
/*		Map<String, Object> states = new HashMap<>();
		String nonce = null;
		String state = null;*/
		String isUserAuthenticated = null;
		String userId=null;
		String authToken=null;
		//end
		String mastertoken = null;
		boolean setToken = false;
		if (session != null) {
			serverNonce = (String) session.getAttribute(SERVER_NONCE);
			sessionToken = (String) session.getAttribute(SESSION_TOKEN);
			safeToken = (String) session.getAttribute(SAFE_TOKEN);
			exchange = (String) session.getAttribute(EXCHANGE);
			//External auth changes
			if(Utility.isExternalAuthEnabled()) {
				/*states = ((Map<String, Object>) session.getAttribute("states"));
				nonce = CookieHelper.getCookie(request, "msal_web_app_auth_nonce");
				state = CookieHelper.getCookie(request, "msal_web_app_auth_state");*/
				isUserAuthenticated = (String) session.getAttribute(IS_USER_AUTHENTICATED);
				userId = (String) session.getAttribute(HEADER_USER_ID);
				authToken = (String) session.getAttribute(AUTH_TOKEN);
			}
			//End
			session.invalidate();
		}
		session = request.getSession(true);
		LOG.debug("Created New Session -: " + session);
		session.setAttribute(SERVER_NONCE, serverNonce);
		session.setAttribute(SESSION_TOKEN, sessionToken);
		session.setAttribute(SAFE_TOKEN, safeToken);

		if(Utility.isExternalAuthEnabled()) {
			/*if(states!=null){
				session.setAttribute("states", states);}
			CookieHelper.setStateNonceCookies(request, response, state, nonce);*/
			session.setAttribute(IS_USER_AUTHENTICATED, isUserAuthenticated);
			session.setAttribute(HEADER_USER_ID, userId);
			session.setAttribute(AUTH_TOKEN, authToken);
		}

		if (exchange != null) {
			session.setAttribute(EXCHANGE, exchange);
		}

		Utility.setSessionTimeOut(session);
	}

	public void sendFailureResponse(HttpServletRequest request,
									HttpServletResponse response) throws IOException {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put(RESULT, FAILURE);
		JSONObject jObj = new JSONObject(resultMap);
		Utility.buildAndSendResponse(request, response, jObj.toString());

	}

	public void sendSuccessResponse(HttpServletRequest request, HttpServletResponse response, String result)
			throws IOException {
		
		JSONObject responseJson = Utility.getJsonRequest(request, response);
		JSONObject appzHeader;
		try {
			appzHeader = responseJson.getJSONObject(APPZILLON_HEADER);
			JSONObject appzBody = responseJson.getJSONObject(APPZILLON_BODY);
			appzBody.put(RESULT, result);
			JSONObject jObj = new JSONObject();
			jObj.put(APPZILLON_HEADER, appzHeader);
			jObj.put(APPZILLON_BODY, appzBody);
			Utility.buildAndSendResponse(request, response, jObj.toString());
		} catch (JSONException e) {
			LOG.error("Invalid JSONException", e);
		}
	}
}
