package com.iexceed.webcontainer.authentication.azuread;

import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.AppzillonConstants;
import com.iexceed.webcontainer.utils.WebProperties;
import com.microsoft.aad.msal4j.*;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import org.springframework.util.StringUtils;

import static com.iexceed.webcontainer.authentication.azuread.AzureAdSessionManagementHelper.FAILED_TO_VALIDATE_MESSAGE;

/**
 * Helpers for acquiring authorization codes and tokens from AAD
 */

class AzureAdAuthHelper implements Serializable {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(AzureAdAuthHelper.class.getName());

    private static final String CLIENT_ID = WebProperties.getAadClientId();
    private static final String CLIENT_SECRET = WebProperties.getAadClientSecret();
    private static final String TENANT_ID = WebProperties.getAadTenantId();
    static final String REDIRECT_URI = WebProperties.getAadRedirectUri();

    private AzureAdAuthHelper(){

    }
    public static AzureAdAuthHelper getInstance(){
        return new AzureAdAuthHelper();
    }

    private static final long serialVersionUID = 7664615760876584862L;
    void processAuthenticationCodeRedirect(HttpServletRequest httpRequest, String currentUri, String fullUrl)
            throws Throwable {

        Map<String, List<String>> params = new HashMap<>();
        for (String key : httpRequest.getParameterMap().keySet()) {
            params.put(key, Collections.singletonList(httpRequest.getParameterMap().get(key)[0]));
        }
        LOG.debug("validate that state in response equals to state in request");
        // validate that state in response equals to state in request
        AzureAdStateData stateData = AzureAdSessionManagementHelper.validateState(httpRequest.getSession(), params.get(AzureAdSessionManagementHelper.STATE).get(0));

        AuthenticationResponse authResponse = AuthenticationResponseParser.parse(new URI(fullUrl), params);
        if (AzureAdAuthHelper.isAuthenticationSuccessful(authResponse)) {
            AuthenticationSuccessResponse oidcResponse = (AuthenticationSuccessResponse) authResponse;
            LOG.debug(" validate that OIDC Auth Response matches Code Flow (contains only requested artifacts)");
            // validate that OIDC Auth Response matches Code Flow (contains only requested artifacts)
            validateAuthRespMatchesAuthCodeFlow(oidcResponse);

            IAuthenticationResult result = getAuthResultByAuthCode(
                    httpRequest,
                    oidcResponse.getAuthorizationCode(),
                    currentUri);

            // validate nonce to prevent reply attacks (code maybe substituted to one with broader access)
            validateNonce(stateData, getNonceClaimValueFromIdToken(result.idToken()));

            AzureAdSessionManagementHelper.setSessionPrincipal(httpRequest, result,AppzillonConstants.NO);

            String userId = (String) JWTParser.parse(result.idToken()).getJWTClaimsSet().getClaim("preferred_username");

            httpRequest.getSession().setAttribute("userId", userId);

        } else {
            AuthenticationErrorResponse oidcResponse = (AuthenticationErrorResponse) authResponse;
            throw new Exception(String.format("Request for auth code failed: %s - %s",
                    oidcResponse.getErrorObject().getCode(),
                    oidcResponse.getErrorObject().getDescription()));
        }
    }

    IAuthenticationResult getAuthResultBySilentFlow(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws Throwable {

        IAuthenticationResult result =  AzureAdSessionManagementHelper.getAuthSessionObject(httpRequest);

        IConfidentialClientApplication app = createClientApplication();

        Object tokenCache = httpRequest.getSession().getAttribute(AppzillonConstants.TOKEN_CACHE_SESSION_ATTRIBUTE);
        if (tokenCache != null) {
            app.tokenCache().deserialize(tokenCache.toString());
        }

        SilentParameters parameters = SilentParameters.builder(
                Collections.singleton("User.Read"),
                result.account()).build();

        CompletableFuture<IAuthenticationResult> future = app.acquireTokenSilently(parameters);
        IAuthenticationResult updatedResult = future.get();
        LOG.debug("update session with latest token cache");

        //update session with latest token cache
        AzureAdSessionManagementHelper.storeTokenCacheInSession(httpRequest, app.tokenCache().serialize());

        return updatedResult;
    }

    private void validateNonce(AzureAdStateData stateData, String nonce) throws Exception {
        if (StringUtils.isEmpty(nonce) || !nonce.equals(stateData.getNonce())) {
            throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "could not validate nonce");
        }
    }

    private String getNonceClaimValueFromIdToken(String idToken) throws ParseException {
        return (String) JWTParser.parse(idToken).getJWTClaimsSet().getClaim("nonce");
    }

    private void validateAuthRespMatchesAuthCodeFlow(AuthenticationSuccessResponse oidcResponse) throws Exception {
        if (oidcResponse.getIDToken() != null || oidcResponse.getAccessToken() != null ||
                oidcResponse.getAuthorizationCode() == null) {
            throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "unexpected set of artifacts received");
        }
    }

    void sendAuthRedirect(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String scope, String redirectURL)
            throws IOException {

        HttpSession session = httpRequest.getSession(false);

        if(session != null){
            AzureAdSessionManagementHelper.removePrincipalFromSession(httpRequest);
            session.invalidate();
        }

        // state parameter to validate response from Authorization server and nonce parameter to validate idToken
        String state = UUID.randomUUID().toString();
        String nonce = UUID.randomUUID().toString();

        AzureAdSessionManagementHelper.storeStateAndNonceInSession(httpRequest.getSession(true), state, nonce);

        httpResponse.setStatus(307);
        String authorizationCodeUrl = getAuthorizationCodeUrl(httpRequest.getParameter("claims"), scope, redirectURL, state, nonce);
        LOG.debug("Redirecting user to : "+authorizationCodeUrl);
        httpResponse.sendRedirect(authorizationCodeUrl);
    }

    String getAuthorizationCodeUrl(String claims, String scope, String registeredRedirectURL, String state, String nonce)
            throws MalformedURLException {

        String updatedScopes = scope == null ? "" : scope;

        PublicClientApplication pca = PublicClientApplication.builder(CLIENT_ID).authority(getAuthority(AppzillonConstants.AAD_AUTHORITY)).build();

        AuthorizationRequestUrlParameters parameters =
                AuthorizationRequestUrlParameters
                        .builder(registeredRedirectURL,
                                Collections.singleton(updatedScopes))
                        .responseMode(ResponseMode.QUERY)
                        .prompt(Prompt.LOGIN)
                        .state(state)
                        .nonce(nonce)
                        .claimsChallenge(claims)
                        .build();

        return pca.getAuthorizationRequestUrl(parameters).toString();
    }

    private IAuthenticationResult getAuthResultByAuthCode(
            HttpServletRequest httpServletRequest,
            AuthorizationCode authorizationCode,
            String currentUri) throws Throwable {

        IAuthenticationResult result;
        ConfidentialClientApplication app;
        try {
            app = createClientApplication();

            String authCode = authorizationCode.getValue();
            AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(
                    authCode,
                    new URI(currentUri)).
                    build();

            Future<IAuthenticationResult> future = app.acquireToken(parameters);

            result = future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }

        if (result == null) {
            throw new ServiceUnavailableException("authentication result was null");
        }

        AzureAdSessionManagementHelper.storeTokenCacheInSession(httpServletRequest, app.tokenCache().serialize());

        return result;
    }

    private ConfidentialClientApplication createClientApplication() throws MalformedURLException {
        return ConfidentialClientApplication.builder(CLIENT_ID, ClientCredentialFactory.createFromSecret(CLIENT_SECRET)).
                authority(getAuthority(AppzillonConstants.AAD_AUTHORITY)).
                build();
    }

    private static boolean isAuthenticationSuccessful(AuthenticationResponse authResponse) {
        return authResponse instanceof AuthenticationSuccessResponse;
    }

    public static String getAuthority(String authority){
        if (!authority.endsWith("/")) {
            authority += "/";
        }
        return authority.replace("{tenantId}",TENANT_ID);
    }

    public void signOut(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        httpRequest.getSession().invalidate();
        response.setStatus(302);
        response.sendRedirect(AppzillonConstants.AAD_ENDSESSION_ENDPOINT + "?post_logout_redirect_uri=" +
                URLEncoder.encode(REDIRECT_URI, AppzillonConstants.UTF_8));
    }

}

