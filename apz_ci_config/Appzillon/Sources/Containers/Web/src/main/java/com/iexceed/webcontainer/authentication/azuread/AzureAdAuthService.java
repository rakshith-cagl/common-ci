package com.iexceed.webcontainer.authentication.azuread;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.AppzillonConstants;
import com.iexceed.webcontainer.utils.hash.Utility;
import com.microsoft.aad.msal4j.*;
import org.owasp.encoder.Encode;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

/**
 * Processes incoming requests based on auth status
 */
public class AzureAdAuthService implements Serializable {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(AzureAdAuthService.class.getName());
    private static final long serialVersionUID = 1832706895335124049L;
    private AzureAdAuthService() {
    }

    public static AzureAdAuthService getInstance(){
        return new AzureAdAuthService();
    }

    public void performAzureAdAuth(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
        LOG.debug("Perform azure ad authorization");
        try {
            String currentUri = Encode.forJava(httpRequest.getRequestURL().toString());
            //String path = httpRequest.getServletPath();
            String queryStr = httpRequest.getQueryString();
            String fullUrl = currentUri + (queryStr != null ? "?" + queryStr : "");
            AzureAdAuthHelper authHelper = AzureAdAuthHelper.getInstance();

            if (Utility.isNotNullOrEmpty(queryStr) && queryStr.contains(AppzillonConstants.AD_SIGN_OUT)) {
                LOG.info("Redirecting the user to ad signOut...");
                authHelper.signOut(httpRequest, httpResponse);
                return;
            }
            LOG.info("Processing auth request...");
            if (containsAuthenticationCode(httpRequest)) {
                LOG.debug("response should have authentication code, which will be used to acquire access token");
                // response should have authentication code, which will be used to acquire access token
                authHelper.processAuthenticationCodeRedirect(httpRequest, currentUri, fullUrl);
                // remove query params so that containsAuthenticationCode will not be true on future requests
                httpResponse.sendRedirect(currentUri);
                return;
            }

            // check if user has a AuthData in the session
            if (!isAuthenticated(httpRequest)) {
                LOG.debug("user not authenticated, redirecting to login.microsoft.com so user can authenticate");
                // not authenticated, redirecting to login.microsoft.com so user can authenticate
                authHelper.sendAuthRedirect(
                        httpRequest,
                        httpResponse,
                        null,
                        AzureAdAuthHelper.REDIRECT_URI);
                return;
            }
            if (isAuthenticated(httpRequest)) {
                LOG.debug("user has a AuthData in the session");
                String isUserAuthorizeFromApzServer = (String) httpRequest.getSession().getAttribute(IS_USER_AUTHENTICATED);
                if(YES.equalsIgnoreCase(isUserAuthorizeFromApzServer) || NO.equalsIgnoreCase(isUserAuthorizeFromApzServer)){
                    LOG.error("Either user auth not happened from appzillon or user is trying to relaunch application ");
                    authHelper.signOut(httpRequest, httpResponse);
                    return;
                }
            }


        } catch(MsalException authException){
            LOG.error("MsalException occurred : ", authException);
            // something went wrong (like expiration or revocation of token)
            // we should invalidate AuthData stored in session and redirect to Authorization server
            AzureAdAuthHelper.getInstance().sendAuthRedirect(
                    httpRequest,
                    httpResponse,
                    null,
                    AzureAdAuthHelper.REDIRECT_URI);

        } catch(Throwable exc){
            LOG.error("Error Message : "+exc.getMessage());
            LOG.error("Exception occurred : ", (Exception) exc.fillInStackTrace());
            AzureAdAuthHelper.getInstance().sendAuthRedirect(
                    httpRequest,
                    httpResponse,
                    null,
                    AzureAdAuthHelper.REDIRECT_URI);
        }

    }

    private boolean containsAuthenticationCode(HttpServletRequest httpRequest) {
        Map<String, String[]> httpParameters = httpRequest.getParameterMap();

        boolean isPostRequest = httpRequest.getMethod().equalsIgnoreCase("POST");
        boolean containsErrorData = httpParameters.containsKey("error");
        boolean containIdToken = httpParameters.containsKey("id_token");
        boolean containsCode = httpParameters.containsKey("code");

        return isPostRequest && containsErrorData || containsCode || containIdToken;
    }

    private boolean isAccessTokenExpired(HttpServletRequest httpRequest) {
        IAuthenticationResult result = AzureAdSessionManagementHelper.getAuthSessionObject(httpRequest);
        return result.expiresOnDate().before(new Date());
    }

    public boolean isAuthenticated(HttpServletRequest request) {
        return request.getSession().getAttribute(AppzillonConstants.PRINCIPAL_SESSION_NAME) != null;
    }

    private void updateAuthDataUsingSilentFlow(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
            throws Throwable {
        IAuthenticationResult authResult = AzureAdAuthHelper.getInstance().getAuthResultBySilentFlow(httpRequest, httpResponse);
        AzureAdSessionManagementHelper.setSessionPrincipal(httpRequest, authResult, YES);
    }

    public boolean requestNewAzureAdToken(HttpServletRequest httpRequest,HttpServletResponse httpResponse){
        boolean result = false;
        if(httpRequest.getSession(false)==null
                || !isAuthenticated(httpRequest)){
            LOG.debug("azure ad auth data not found, re-authentication is required");
            return false;
        }
        try {
            if (isAccessTokenExpired(httpRequest)) {
                LOG.debug("AccessToken expired, Requesting for a new token using SilentFlow");
                updateAuthDataUsingSilentFlow(httpRequest, httpResponse);
            }
            else{
                httpRequest.getSession().setAttribute(AppzillonConstants.IS_TOKEN_REFRESHED, AppzillonConstants.NO);
            }
            result = true;
        }catch (Exception e){
            LOG.error("Exception occurred while checking for access token expiry");
        } catch (Throwable te) {
            LOG.error("Error while requesting for a new token from azure ad: " + te.fillInStackTrace());
        }
        return result;
    }
}
