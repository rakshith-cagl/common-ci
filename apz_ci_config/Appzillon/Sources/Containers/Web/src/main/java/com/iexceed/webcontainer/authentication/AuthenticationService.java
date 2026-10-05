package com.iexceed.webcontainer.authentication;

import com.iexceed.webcontainer.authentication.azuread.AzureAdAuthService;
import com.iexceed.webcontainer.authentication.azureadb2c.AzureAdB2cAuthService;
import com.iexceed.webcontainer.authentication.keycloak.KeyCloakService;
import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.WebProperties;
import com.iexceed.webcontainer.utils.hash.Utility;
import com.iexceed.webcontainer.utils.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;
import static com.iexceed.webcontainer.utils.AppzillonConstants.AUTH_PROVIDER_KEYCLOAK;

public class AuthenticationService {
    static final Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(AuthenticationService.class.getName());

    private AuthenticationService(){

    }

    public static void performAuthentication(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (AUTH_PROVIDER_AAD.equalsIgnoreCase(WebProperties.getAuthProvider())) {
            LOG.debug("Auth type is Azure Ad");
            AzureAdAuthService.getInstance().performAzureAdAuth(request, response);
        } else if (AUTH_PROVIDER_AAD_B2C.equalsIgnoreCase(WebProperties.getAuthProvider())) {
            LOG.debug("Auth type is Azure Ad B2C");
            AzureAdB2cAuthService.getInstance().performAzureAdB2cAuth(request, response);
        }else if (AUTH_PROVIDER_KEYCLOAK.equalsIgnoreCase(WebProperties.getAuthProvider())){
            LOG.debug("Auth provider is Keycloak");
            KeyCloakService service = new KeyCloakService();
            service.performKeyCloakAuthentication(request,response);
        }  else {
            LOG.debug("Auth provider is not found");
            RequestDispatcher dispatch = request
                    .getRequestDispatcher("/apps/error.jsp");
            dispatch.forward(request, response);
            return;
        }
    }

    public static void checkIfAuthTokenHasExpired(HttpServletRequest request, HttpServletResponse response,
                                                  JSONObject requestJson){
        boolean result = false;
        if (AUTH_PROVIDER_AAD.equalsIgnoreCase(WebProperties.getAuthProvider())) {
            result = AzureAdAuthService.getInstance().requestNewAzureAdToken(request, response);
        } else if (AUTH_PROVIDER_AAD_B2C.equalsIgnoreCase(WebProperties.getAuthProvider())) {
            result = AzureAdB2cAuthService.getInstance().requestNewAzureAdB2cToken(request, response);
        }else if(AUTH_PROVIDER_KEYCLOAK.equalsIgnoreCase(WebProperties.getAuthProvider())){
            KeyCloakService service = new KeyCloakService();
            result = service.requestNewTokenIfExpired(request);
        }
        if(!result){
            String finalResponse = Utility.getfinalResponse(request, response,
                    Utility.buildErrorResponse(request, response,requestJson,"You have been logged out of the system due to inactivity.","APZ-SMS-EX-003"));
            try {
                Utility.sendResponse(request, response, finalResponse);
            } catch (IOException e) {
                LOG.error("Exception occurred while building the response : ",e);
            }
        }
    }

    public static void maintainThirdPartyAuthProviderSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        httpRequest.getSession().setAttribute(IS_USER_AUTHENTICATED, NO);
    }
}
