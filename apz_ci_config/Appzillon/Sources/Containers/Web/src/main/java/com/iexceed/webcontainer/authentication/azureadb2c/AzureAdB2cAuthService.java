package com.iexceed.webcontainer.authentication.azureadb2c;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.AppzillonConstants;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.MsalException;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import org.apache.commons.lang3.StringUtils;
import org.owasp.encoder.Encode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

public class AzureAdB2cAuthService {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(AzureAdB2cAuthService.class.getName());

	private static final String STATE = "state";
	private static final String CODE = "code";
	private static final String FAILED_TO_VALIDATE_MESSAGE = "Failed to validate data received from Authorization service - ";
	private static final String PIN_RESET_AFTER_LOGIN = "pinResetAfterLogin";

	private AzureAdB2cAuthService() {
	}

	public static AzureAdB2cAuthService getInstance(){
		return new AzureAdB2cAuthService();
	}

	public void performAzureAdB2cAuth(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
		LOG.debug("Perform azure ad b2c authorization");
		String currentUri = "";
		try {
			currentUri = Encode.forJava(httpRequest.getRequestURL().toString());
			//String path = httpRequest.getServletPath();
			String queryStr = httpRequest.getQueryString();
			String fullUrl = currentUri + (queryStr != null ? "?" + queryStr : "");
			LOG.debug("currentUri : "+currentUri);
			if (queryStr!=null && queryStr.contains(AppzillonConstants.AD_SIGN_OUT)) {
				LOG.info("Redirecting the user to ad signOut...");
				redirectToSignOutEndpoint(httpRequest, httpResponse);
				return;
			}
			if (queryStr!=null && queryStr.contains(AppzillonConstants.AD_PWD_RESET)) {
				LOG.info("Redirecting the user to ad signOut...");
				passwordReset(httpRequest, httpResponse);
				return;
			}
			// check if user has a AuthData in the session
			if (!AzureAdB2cAuthHelper.isAuthenticated(httpRequest)) {
				if (AzureAdB2cAuthHelper.containsAuthenticationCode(httpRequest)) {
					LOG.debug("Checking if request has auth code");
					// response should have authentication code, which will be used to acquire access token
					processAuthenticationCodeRedirect(httpRequest, currentUri, fullUrl);
					CookieHelper.removeStateNonceCookies(httpResponse);
					// remove query params so that containsAuthenticationCode will not be true on future requests
					httpResponse.sendRedirect(currentUri);
					return;
				}else if(AzureAdB2cAuthHelper.containsErrorCode(httpRequest)){
					LOG.debug("Checking if request has error code");
					processErrorCodes(httpRequest,httpResponse);
				}
				else {
					LOG.debug("not authenticated, redirecting user to re-authenticate");
					sendAuthRedirect(AzureAdB2cAuthHelper.getAuthority() + AzureAdB2cAuthHelper.SIGN_IN_POLICY, httpRequest,
							httpResponse);
					return;
				}
			}
			if(AzureAdB2cAuthHelper.isAuthenticated(httpRequest)){
				String isUserAuthorizeFromApzServer = (String) httpRequest.getSession().getAttribute(IS_USER_AUTHENTICATED);
				String pinResetAfterLogin = (String)httpRequest.getSession().getAttribute(PIN_RESET_AFTER_LOGIN);
				//when user cancels the password reset page after login, launch the dashboard.
				if(AzureAdB2cAuthHelper.containsErrorCode(httpRequest)){
					LOG.debug("Checking if request has error code");
					processErrorCodes(httpRequest,httpResponse);
				}
				else if(YES.equalsIgnoreCase(pinResetAfterLogin)){
					LOG.debug("The user has cancelled the password reset operation after login");
					httpRequest.getSession().removeAttribute(PIN_RESET_AFTER_LOGIN);
					return;
				}
				//check if user is trying to authenticate again
				else if(YES.equalsIgnoreCase(isUserAuthorizeFromApzServer) || NO.equalsIgnoreCase(isUserAuthorizeFromApzServer)){
					LOG.error("Either user auth not happened from appzillon or user is trying to relaunch application");
					httpRequest.getSession().invalidate();
					redirectToSignOutEndpoint(httpRequest,httpResponse);
					return;
				}
			}
			if (isAccessTokenExpired(httpRequest)) {
				LOG.debug("Access token expired requesting for new token");
				AzureAdB2cAuthHelper.getInstance().updateAuthDataUsingSilentFlow(httpRequest);
			}
		}catch (final AADPasswordResetException pre) {
			LOG.warn("redirect endpoint requests password reset");
			sendAuthRedirect(AzureAdB2cAuthHelper.getAuthority() + AzureAdB2cAuthHelper.PW_RESET_POLICY, httpRequest, httpResponse);
		}
		catch (final AADPasswordResetCancelException pre) {
			if(AppzillonConstants.YES.equalsIgnoreCase((String) httpRequest.getSession().getAttribute(IS_USER_AUTHENTICATED))){
				LOG.debug("User is authenticated,redirecting to launch dashboard");
				httpRequest.getSession().setAttribute(PIN_RESET_AFTER_LOGIN, YES);
				httpResponse.sendRedirect(currentUri);
				return;
			}
			LOG.warn("redirect user to sign in");
			sendAuthRedirect(AzureAdB2cAuthHelper.getAuthority() + AzureAdB2cAuthHelper.SIGN_IN_POLICY, httpRequest,
					httpResponse);}
		catch (MsalException authException) {
			// something went wrong (like expiration or revocation of token)
			// we should invalidate AuthData stored in session and redirect to Authorization server
			LOG.error("MsalException occurred : "+authException);
			AzureAdB2cAuthHelper.getInstance().removePrincipalFromSession(httpRequest);
			sendAuthRedirect(AzureAdB2cAuthHelper.getAuthority() + AzureAdB2cAuthHelper.SIGN_IN_POLICY, httpRequest,
					httpResponse);
		} catch (Throwable exc) {
			LOG.error("Exception occurred : "+ exc.fillInStackTrace());
			httpRequest.getSession().invalidate();
			httpResponse.setStatus(500);
			httpRequest.getRequestDispatcher("/apps/error.jsp").forward(httpRequest, httpResponse);
		}
	}

	private boolean isAccessTokenExpired(HttpServletRequest httpRequest) {
		IAuthenticationResult result = AzureAdB2cAuthHelper.getAuthSessionObject(httpRequest);
		return result.expiresOnDate().before(new Date());
	}

	private void processAuthenticationCodeRedirect(HttpServletRequest httpRequest, String currentUri, String fullUrl)
			throws Throwable {
		Map<String, List<String>> params = new HashMap<>();
		for (String key : httpRequest.getParameterMap().keySet()) {
			params.put(key, Collections.singletonList(httpRequest.getParameterMap().get(key)[0]));
		}
		// validate that state in response equals to state in request
		validateState(CookieHelper.getCookie(httpRequest, CookieHelper.MSAL_WEB_APP_STATE_COOKIE), params.get(STATE).get(0));

		AuthenticationResponse authResponse = AuthenticationResponseParser.parse(new URI(fullUrl), params);
		if (AzureAdB2cAuthHelper.isAuthenticationSuccessful(authResponse)) {
			AzureAdB2cAuthHelper authHelper = AzureAdB2cAuthHelper.getInstance();
			AuthenticationSuccessResponse oidcResponse = (AuthenticationSuccessResponse) authResponse;
			// validate that OIDC Auth Response matches Code Flow (contains only requested artifacts)
			validateAuthRespMatchesAuthCodeFlow(oidcResponse);

			IAuthenticationResult result = authHelper.getAuthResultByAuthCode(
					httpRequest,
					oidcResponse.getAuthorizationCode(),
					currentUri,
					Collections.singleton(AzureAdB2cAuthHelper.getScopes()));

			// validate nonce to prevent reply attacks (code maybe substituted to one with broader access)
			validateNonce(CookieHelper.getCookie(httpRequest, CookieHelper.MSAL_WEB_APP_NONCE_COOKIE),
					getNonceClaimValueFromIdToken(result.idToken()));

			authHelper.setSessionPrincipal(httpRequest, result,AppzillonConstants.NO);
			Object emails = JWTParser.parse(result.idToken()).getJWTClaimsSet().getClaim("emails");
			if (emails instanceof JSONArray) {
				httpRequest.getSession().setAttribute("userId", ((JSONArray) emails).get(0));
				LOG.debug("UserId :: " + ((JSONArray) emails).get(0));
			}
			//httpRequest.getSession().setAttribute("userId", result.account().username());
		} else {
			AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
			throw new Exception(String.format("Request for auth code failed: %s - %s",
					oidcResponse.getErrorObject().getCode(),
					oidcResponse.getErrorObject().getDescription()));
		}
	}

	void sendAuthRedirect(String authoriy, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		// state parameter to validate response from Authorization server and nonce parameter to validate idToken
		String state = UUID.randomUUID().toString();
		String nonce = UUID.randomUUID().toString();

		CookieHelper.setStateNonceCookies(httpRequest, httpResponse, state, nonce);

		httpResponse.setStatus(307);
		String redirectUrl = getRedirectUrl(authoriy, httpRequest.getParameter("claims"), state, nonce);
		httpResponse.sendRedirect(Encode.forJava(redirectUrl));
	}

	private String getNonceClaimValueFromIdToken(String idToken) throws ParseException {
		return (String) JWTParser.parse(idToken).getJWTClaimsSet().getClaim("nonce");
	}

	private void validateState(String cookieValue, String state) throws Exception {
		if (StringUtils.isEmpty(state) || !state.equals(cookieValue)) {
			throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "could not validate state");
		}
	}

	private void validateNonce(String cookieValue, String nonce) throws Exception {
		if (StringUtils.isEmpty(nonce) || !nonce.equals(cookieValue)) {
			throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "could not validate nonce");
		}
	}

	private void validateAuthRespMatchesAuthCodeFlow(AuthenticationSuccessResponse oidcResponse) throws Exception {
		if (oidcResponse.getIDToken() != null || oidcResponse.getAccessToken() != null ||
				oidcResponse.getAuthorizationCode() == null) {
			throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "unexpected set of artifacts received");
		}
	}

	private String getRedirectUrl(String authority, String claims, String state, String nonce)
			throws UnsupportedEncodingException {

		String redirectUrl = String.format("%soauth2/v2.0/authorize?response_type=code&response_mode=query&redirect_uri=%s&client_id=%s&scope=%s%s&prompt=login&state=%s&nonce=%s",
				authority.replace("/tfp", ""),
				URLEncoder.encode(AzureAdB2cAuthHelper.REDIRECT_URI, AppzillonConstants.UTF_8),
				AzureAdB2cAuthHelper.CLIENT_ID,
				URLEncoder.encode(AzureAdB2cAuthHelper.getScopes(), AppzillonConstants.UTF_8),
				StringUtils.isEmpty(claims) ? "" : "&claims=" + claims, state, nonce);
		LOG.info("Redirecting user to "+redirectUrl);

		return redirectUrl;
	}

	private void processErrorCodes(final HttpServletRequest req, final HttpServletResponse resp) throws Exception {
		final String errorDescription = req.getParameter("error_description");
		LOG.info( "error description is : "+ errorDescription);

		if (errorDescription != null) {
			if (errorDescription.contains(AppzillonConstants.AAD_B2C_FORGOT_PASSWORD_ERRCODE)){
				throw new AADPasswordResetException("Password reset error code in request.");
			}else if (errorDescription.contains(AppzillonConstants.AAD_B2C_PWD_RESET_CANCEL_ERRCODE)){
				throw new AADPasswordResetCancelException("User has canceled the password reset operation");
			}
			throw new Exception("Unknown error in request.");
		}
	}
	private static void redirectToSignOutEndpoint(final HttpServletRequest req, final HttpServletResponse resp)
			throws Exception {
		final String redirect = String.format("%s%s%s%s%s", AzureAdB2cAuthHelper.getAuthority().replace("/tfp", ""), AzureAdB2cAuthHelper.SIGN_IN_POLICY, AppzillonConstants.AAD_B2C_SIGN_OUT_ENDPOINT,
				AppzillonConstants.AAD_B2C_POST_SIGN_OUT_FRAGMENT, URLEncoder.encode(AzureAdB2cAuthHelper.REDIRECT_URI, "UTF-8"));
		LOG.info("Redirecting user to "+redirect);
		resp.setStatus(302);
		resp.sendRedirect(Encode.forJava(redirect));
	}

	public boolean requestNewAzureAdB2cToken(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse){
		LOG.debug("Request for a new token, if its expired");
		boolean result = false;
		if(httpRequest.getSession(false) == null
				|| !AzureAdB2cAuthHelper.isAuthenticated(httpRequest)){
			LOG.debug("user auth data not found, re-authentication is required");
			return false;
		}
		try {
			if (isAccessTokenExpired(httpRequest)) {
				LOG.debug("Access token expired requesting for new token");
				AzureAdB2cAuthHelper.getInstance().updateAuthDataUsingSilentFlow(httpRequest);
			} else {
				httpRequest.getSession().setAttribute(AppzillonConstants.IS_TOKEN_REFRESHED, AppzillonConstants.NO);
			}
			result = true;
		}catch (Exception e){
			LOG.error("Exception occurred while checking for access token expiry");
		} catch (Throwable te) {
			LOG.error("Error while requesting for a new token from azure ad b2c : " + te.fillInStackTrace());
		}
		return result;
	}

	public void passwordReset(HttpServletRequest request, HttpServletResponse response) throws IOException {
		LOG.debug("Redirect endpoint requests password reset");
		sendAuthRedirect(AzureAdB2cAuthHelper.getAuthority() + AzureAdB2cAuthHelper.PW_RESET_POLICY, request, response);
	}

}


