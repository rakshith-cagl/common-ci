package com.iexceed.webcontainer.authentication.azuread;

import com.iexceed.webcontainer.utils.AppzillonConstants;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Helpers for managing session
 */
class AzureAdSessionManagementHelper implements Serializable {

    static final String STATE = "state";
    private static final String STATES = "states";
    private static final Integer STATE_TTL = 3600;

    static final String FAILED_TO_VALIDATE_MESSAGE = "Failed to validate data received from Authorization service - ";
    private static final long serialVersionUID = -4311451839029783916L;

    static AzureAdStateData validateState(HttpSession session, String state) throws Exception {
        if (!StringUtils.isEmpty(state)) {
            AzureAdStateData stateDataInSession = removeStateFromSession(session, state);
            if (stateDataInSession != null) {
                return stateDataInSession;
            }
        }
        throw new Exception(FAILED_TO_VALIDATE_MESSAGE + "could not validate state");
    }

    private static AzureAdStateData removeStateFromSession(HttpSession session, String state) {
        Map<String, AzureAdStateData> states = (Map<String, AzureAdStateData>) session.getAttribute(STATES);
        if (states != null) {
            eliminateExpiredStates(states);
            AzureAdStateData stateData = states.get(state);
            if (stateData != null) {
                states.remove(state);
                return stateData;
            }
        }
        return null;
    }

    private static void eliminateExpiredStates(Map<String, AzureAdStateData> map) {
        Iterator<Map.Entry<String, AzureAdStateData>> it = map.entrySet().iterator();

        Date currTime = new Date();
        while (it.hasNext()) {
            Map.Entry<String, AzureAdStateData> entry = it.next();
            long diffInSeconds = TimeUnit.MILLISECONDS.
                    toSeconds(currTime.getTime() - entry.getValue().getExpirationDate().getTime());

            if (diffInSeconds > STATE_TTL) {
                it.remove();
            }
        }
    }

    public static void storeStateAndNonceInSession(HttpSession session, String state, String nonce) {

        // state parameter to validate response from Authorization server and nonce parameter to validate idToken
        if (session.getAttribute(STATES) == null) {
            session.setAttribute(STATES, new HashMap<String, AzureAdStateData>());
        }
        ((Map<String, AzureAdStateData>) session.getAttribute(STATES)).put(state, new AzureAdStateData(nonce, new Date()));



    }

    static void storeTokenCacheInSession(HttpServletRequest httpServletRequest, String tokenCache){
        httpServletRequest.getSession().setAttribute(AppzillonConstants.TOKEN_CACHE_SESSION_ATTRIBUTE, tokenCache);
    }

    static void setSessionPrincipal(HttpServletRequest httpRequest, IAuthenticationResult result,String isTokenRefreshed) {
        httpRequest.getSession().setAttribute(AppzillonConstants.PRINCIPAL_SESSION_NAME, result);
        httpRequest.getSession().setAttribute(AppzillonConstants.AUTH_TOKEN, result.accessToken());
        httpRequest.getSession().setAttribute(AppzillonConstants.IS_TOKEN_REFRESHED, isTokenRefreshed);

    }

    static void removePrincipalFromSession(HttpServletRequest httpRequest) {
        httpRequest.getSession().removeAttribute(AppzillonConstants.PRINCIPAL_SESSION_NAME);
        httpRequest.getSession().removeAttribute(AppzillonConstants.AUTH_TOKEN);
        httpRequest.getSession().removeAttribute(AppzillonConstants.IS_TOKEN_REFRESHED);
    }

    public static IAuthenticationResult getAuthSessionObject(HttpServletRequest request) {
        Object principalSession = request.getSession().getAttribute(AppzillonConstants.PRINCIPAL_SESSION_NAME);
        if(principalSession instanceof IAuthenticationResult){
            return (IAuthenticationResult) principalSession;
        } else {
            throw new IllegalStateException("Session does not contain principal session name");
        }
    }
}
