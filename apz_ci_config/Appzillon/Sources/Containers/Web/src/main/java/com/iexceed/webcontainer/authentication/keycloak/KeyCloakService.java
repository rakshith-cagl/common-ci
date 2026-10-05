package com.iexceed.webcontainer.authentication.keycloak;

import com.iexceed.webcontainer.authentication.AuthenticationService;
import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.AppzillonConstants;
import com.iexceed.webcontainer.utils.PropertyUtils;
import com.iexceed.webcontainer.utils.WebProperties;
import com.iexceed.webcontainer.utils.hash.Utility;
import org.keycloak.KeycloakSecurityContext;
import org.owasp.encoder.Encode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

public class KeyCloakService {
    static final Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(AuthenticationService.class.getName());

    public void performKeyCloakAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String queryStr = request.getQueryString();
        LOG.debug("queryStr:: "+queryStr);
        if (Utility.isNotNullOrEmpty(queryStr) && queryStr.contains(ACTION_LOGOUT)) {
            LOG.info("Perform logout operation");
            handleLogout(request,response);
            return;
        }

        if(isLoggedInKeycloak(request)){
            LOG.info("User is authenticated from authentication server(EIDP)");
            String isUserAuthFromApzServer = (String) request.getSession().getAttribute(IS_USER_AUTHENTICATED);
            if(YES.equalsIgnoreCase(isUserAuthFromApzServer) || NO.equalsIgnoreCase(isUserAuthFromApzServer)){
                LOG.info("User is not authenticated from apz server or trying to refresh the application");
                handleLogout(request,response);
                return;
            }
            KeycloakSecurityContext context = getSession(request);
            setSessionPrincipal(request,context,NO);
            request.getSession().setAttribute("userId",context.getIdToken().getPreferredUsername());
        }else{
            LOG.info("User is not authenticated from authentication server");
            LOG.debug("Redirecting user to login screen...");
            handleLogout(request,response);
            return;
        }

    }

    public boolean requestNewTokenIfExpired(HttpServletRequest httpRequest) {
        boolean result = false;
        if(httpRequest.getSession(false) == null
                || !isLoggedInKeycloak(httpRequest)){
            LOG.debug("user auth data not found, re-authentication is required");
            return false;
        }
        try {
            if (isAccessTokenExpired(httpRequest)) {
                LOG.debug("Access token expired getting new token from keycloak context");
                setSessionPrincipal(httpRequest,getSession(httpRequest),YES);
            } else {
                LOG.debug("Access token is not expired");
                httpRequest.getSession().setAttribute(AppzillonConstants.IS_TOKEN_REFRESHED, AppzillonConstants.NO);
            }
            result = true;
        }catch (Exception e){
            LOG.error("Exception occurred while checking for access token expiry");
        }
        return result;
    }

    private boolean isAccessTokenExpired(HttpServletRequest httpRequest) {
        Long expVal = (Long) httpRequest.getSession().getAttribute(AUTH_TOKEN_EXP);
        Date tokenExpTime = new Date(expVal*1000);
        LOG.debug("Token Exp:: "+tokenExpTime);
        return tokenExpTime.before(new Date());
    }

    public boolean isLoggedInKeycloak(HttpServletRequest req) {
        return getSession(req) != null;
    }

    private KeycloakSecurityContext getSession(HttpServletRequest req) {
        return (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
    }

    private void setSessionPrincipal(HttpServletRequest httpRequest, KeycloakSecurityContext context, String isTokenRefreshed) {
        LOG.debug("Setting session principal");
        httpRequest.getSession().setAttribute(AUTH_TOKEN_EXP,context.getToken().getExp());
        httpRequest.getSession().setAttribute(AUTH_TOKEN, context.getTokenString());
        httpRequest.getSession().setAttribute(IS_TOKEN_REFRESHED, isTokenRefreshed);
    }

    public void handleLogout(HttpServletRequest req, HttpServletResponse res) throws IOException {
        LOG.debug("Clearing the auth server session and redirecting to login page");
        req.getSession().invalidate();
        res.sendRedirect(getLogoutUrl());
    }

    private String getLogoutUrl(){
      return Encode.forJava(KeycloakUriBuilder.fromUri(PropertyUtils.getPropertyValue(AUTH_SERVER_URL)).path(ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH)
                .queryParam("redirect_uri", WebProperties.getEidpRedirectUri()).build(PropertyUtils.getPropertyValue(AUTH_SERVER_REALM)).toString());
    }
}
