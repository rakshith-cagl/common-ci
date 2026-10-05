package com.iexceed.webcontainer.authentication.azureadb2c;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.AppzillonConstants;
import com.iexceed.webcontainer.utils.WebProperties;
import com.microsoft.aad.msal4j.*;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import lombok.Getter;
import lombok.Setter;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Getter
@Setter
public class AzureAdB2cAuthHelper {

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(AzureAdB2cAuthHelper.class.getName());

	static final String SIGN_IN_POLICY = WebProperties.getAadB2cSignInPolicy() +"/";
	static final String PW_RESET_POLICY = WebProperties.getAadB2cPwdResetPolicy()+"/";
	static final String CLIENT_ID = WebProperties.getAadB2cClientId();
	static final String SECRET = WebProperties.getAadB2cClientSecret();
	static final String REDIRECT_URI = WebProperties.getAadB2cRedirectUri();
	static final String DOMAIN = WebProperties.getAadB2cDomainName();

	static String getScopes(){
		return AppzillonConstants.AAD_B2C_SCOPES.replace("{clientId}",CLIENT_ID);
	}

	public static String getAuthority(){
		return AppzillonConstants.AAD_B2C_AUTHORITY.replace("{domain}",DOMAIN);
	}

	private AzureAdB2cAuthHelper(){

	}
	public static AzureAdB2cAuthHelper getInstance(){
		return new AzureAdB2cAuthHelper();
	}


	private ConfidentialClientApplication createClientApplication() throws MalformedURLException {
		return ConfidentialClientApplication.builder(CLIENT_ID,
				ClientCredentialFactory.createFromSecret(SECRET))
				.b2cAuthority(getAuthority()+SIGN_IN_POLICY)
				.build();
	}

	IAuthenticationResult getAuthResultBySilentFlow(HttpServletRequest httpRequest, String scope) throws Throwable {
		IAuthenticationResult result =  AzureAdB2cAuthHelper.getAuthSessionObject(httpRequest);

		IAuthenticationResult updatedResult;
		ConfidentialClientApplication app;
		try {
			app = createClientApplication();

			Object tokenCache =  httpRequest.getSession().getAttribute(AppzillonConstants.TOKEN_CACHE_SESSION_ATTRIBUTE);
			if(tokenCache != null){
				app.tokenCache().deserialize(tokenCache.toString());
			}

			SilentParameters parameters = SilentParameters.builder(
					Collections.singleton(scope),
					result.account()).build();

			CompletableFuture<IAuthenticationResult> future = app.acquireTokenSilently(parameters);

			updatedResult = future.get();
		} catch (ExecutionException e) {
			LOG.debug("ExecutionException :"+e);
			throw e.getCause();
		}

		if (updatedResult == null) {
			throw new ServiceUnavailableException("authentication result was null");
		}

		//update session with latest token cache
		LOG.debug("update session with latest token cache");
		storeTokenCacheInSession(httpRequest, app.tokenCache().serialize());

		return updatedResult;
	}

	IAuthenticationResult getAuthResultByAuthCode(
			HttpServletRequest httpServletRequest,
			AuthorizationCode authorizationCode,
			String currentUri, Set<String> scopes) throws Throwable {

		IAuthenticationResult result;
		ConfidentialClientApplication app;
		try {
			app = createClientApplication();

			String authCode = authorizationCode.getValue();
			AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(
					authCode,
					new URI(currentUri))
					.scopes(scopes)
					.build();

			Future<IAuthenticationResult> future = app.acquireToken(parameters);

			result = future.get();
		} catch (ExecutionException e) {
			throw e.getCause();
		}

		if (result == null) {
			throw new ServiceUnavailableException("authentication result was null");
		}

		storeTokenCacheInSession(httpServletRequest, app.tokenCache().serialize());

		return result;
	}

	private void storeTokenCacheInSession(HttpServletRequest httpServletRequest, String tokenCache){
		httpServletRequest.getSession().setAttribute(AppzillonConstants.TOKEN_CACHE_SESSION_ATTRIBUTE, tokenCache);
	}

	void setSessionPrincipal(HttpServletRequest httpRequest, IAuthenticationResult result,String isTokenRefreshed) {
		httpRequest.getSession().setAttribute(AppzillonConstants.PRINCIPAL_SESSION_NAME, result);
		httpRequest.getSession().setAttribute(AppzillonConstants.AUTH_TOKEN, result.accessToken());
		httpRequest.getSession().setAttribute(AppzillonConstants.IS_TOKEN_REFRESHED,isTokenRefreshed);


	}

	void removePrincipalFromSession(HttpServletRequest httpRequest) {
		httpRequest.getSession().removeAttribute(AppzillonConstants.PRINCIPAL_SESSION_NAME);
		httpRequest.getSession().removeAttribute(AppzillonConstants.IS_TOKEN_REFRESHED);
		httpRequest.getSession().removeAttribute(AppzillonConstants.AUTH_TOKEN);
	}

	void updateAuthDataUsingSilentFlow(HttpServletRequest httpRequest) throws Throwable {
		IAuthenticationResult authResult = getAuthResultBySilentFlow(httpRequest, getScopes());
		setSessionPrincipal(httpRequest, authResult,AppzillonConstants.YES);
	}

	static boolean isAuthenticationSuccessful(AuthenticationResponse authResponse) {
		return authResponse instanceof AuthenticationSuccessResponse;
	}

	public static boolean isAuthenticated(HttpServletRequest request) {
		return request.getSession().getAttribute(AppzillonConstants.PRINCIPAL_SESSION_NAME) != null;
	}

	public static IAuthenticationResult getAuthSessionObject(HttpServletRequest request) {
		Object principalSession = request.getSession().getAttribute(AppzillonConstants.PRINCIPAL_SESSION_NAME);
		if(principalSession instanceof IAuthenticationResult){
			return (IAuthenticationResult) principalSession;
		} else {
			throw new IllegalStateException("Session does not contain principal session name");
		}
	}

	static boolean containsAuthenticationCode(HttpServletRequest httpRequest) {
		Map<String, String[]> httpParameters = httpRequest.getParameterMap();

		boolean isPostRequest = httpRequest.getMethod().equalsIgnoreCase("POST");
		boolean containsErrorData = httpParameters.containsKey("error");
		boolean containIdToken = httpParameters.containsKey("id_token");
		boolean containsCode = httpParameters.containsKey("code");

		return isPostRequest && containsErrorData || containsCode || containIdToken;
	}
	static boolean containsErrorCode(HttpServletRequest httpRequest) {
		Map<String, String[]> httpParameters = httpRequest.getParameterMap();
		return httpParameters.containsKey("error");
	}

}
