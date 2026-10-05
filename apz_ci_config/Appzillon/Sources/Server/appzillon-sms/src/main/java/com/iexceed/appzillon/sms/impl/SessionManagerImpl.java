/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.*;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.iface.ISessionManager;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.InitializingBean;

import java.security.SecureRandom;
import java.util.Date;

/**
 * @author arthanarisamy
 */
public class SessionManagerImpl implements InitializingBean, ISessionManager {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, SessionManagerImpl.class.getName());

    @Override
    public void validateSession(Message pMessage) {
        LOG.debug("{} Validating Session", ServerConstants.LOGGER_PREFIX_SMS);
        Header lHeader = pMessage.getHeader();
        Request lRequest = pMessage.getRequestObject();
        LOG.debug("{} Header Map and Request Body in validateSession : {}, {}", ServerConstants.LOGGER_PREFIX_SMS, lHeader, lRequest.getRequestJson());
        if (pMessage.getHeader().getPin() != null && !pMessage.getHeader().getPin().isEmpty()) {
            validateUserPassword(pMessage);
        } else if (pMessage.getHeader().getUserAppAccessToken() != null && !pMessage.getHeader().getUserAppAccessToken().isEmpty()) {
            validateUserAppAccessToken(pMessage);
        } else {
            LOG.debug("{} Calling FetchUserSession", ServerConstants.LOGGER_PREFIX_SMS);
            checkUserSession(pMessage, lHeader);
        }
    }

    private void checkUserSession(Message pMessage, Header lHeader) {
        fetchSession(pMessage);

        if (pMessage.getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)) {
            isSessionTimedOut(pMessage);
        } else {
            Session cUserSession = pMessage.getSession();
            String lRequestKey = null;
            LOG.debug("{} Checking whether valid session exists or not", ServerConstants.LOGGER_PREFIX_SMS);
            if (cUserSession != null) {
                LOG.debug("{} Session exists and checking whether it is valid or not", ServerConstants.LOGGER_PREFIX_SMS);
                boolean isSessionTimedOut = this.isSessionTimedOut(pMessage);
                boolean isSessionValid = this.isSessionValid(pMessage);
                LOG.info("{} The session Times out : {},Session Valid Status : {} for user {}", ServerConstants.LOGGER_PREFIX_SMS, isSessionTimedOut, isSessionValid, cUserSession.getUserName());
                if (isSessionValid) {
                    if ("true".equalsIgnoreCase(lHeader.getAsynch())) {
                        LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "createRequestKey - async is true, hence requestkey is set to the key sent in request header....");
                        lRequestKey = lHeader.getRequestKey();
                        LOG.debug("{} createRequestKey - async is true hence requestkey is set to the key sent in request header -outputString: {}"
                                , ServerConstants.LOGGER_PREFIX_SMS, lRequestKey);
                    } else {
                        LOG.debug("{} createRequestKey - async is false, hence requestkey is creating and set to the key sent in request header....", ServerConstants.LOGGER_PREFIX_SMS);
                        this.createAuthtKey(pMessage);
                        lRequestKey = pMessage.getHeader().getRequestKey();
                        LOG.debug("{} Generated Request Key after Session validation -: {}", ServerConstants.LOGGER_PREFIX_SMS, lRequestKey);
                    }
                    cUserSession.setRequestKey(lRequestKey);
                } else {
                    SmsException lSessionInvalid = SmsException.getSMSExceptionInstance();
                    lSessionInvalid.setMessage(lSessionInvalid.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_003));
                    lSessionInvalid.setCode(EXCEPTION_CODE.APZ_SMS_EX_003.toString());
                    lSessionInvalid.setPriority("1");
                    LOG.error("{} A valid session does not exists", ServerConstants.LOGGER_PREFIX_SMS, lSessionInvalid);
                    throw lSessionInvalid;
                }

            } else {
                SmsException sexp = SmsException.getSMSExceptionInstance();
                sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_003));
                sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_003.toString());
                sexp.setPriority("1");
                throw sexp;
            }
        }
    }

    protected boolean validateUserPassword(Message pMessage) {
        LOG.debug("{} inside validateUserPassword()..", ServerConstants.LOGGER_PREFIX_SMS);
        boolean lOutputJson = false;
        try {
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUTHENTICATION);
            DomainStartup.getInstance().processRequest(pMessage);
            if (pMessage.getResponseObject().getResponseJson() != null) {
                LOG.info("{} User present in Database", ServerConstants.LOGGER_PREFIX_SMS);
                JSONObject cUserrecord = pMessage.getResponseObject().getResponseJson().getJSONObject("UserDetails");

                LOG.debug("Server Token : {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getSecurityParams().getServerToken());
                String reqPin = HashUtils.hashSHA256(pMessage.getHeader().getPin(), pMessage.getHeader().getUserId() + pMessage.getSecurityParams().getServerToken());
                LOG.debug("{} Password From User Request : {} and PIN from Request : {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getHeader().getPin(), reqPin);

                if (cUserrecord.getString("Pin").equals(reqPin)) {
                    LOG.info("{} Authentication Success", ServerConstants.LOGGER_PREFIX_SMS);
                    lOutputJson = true;
                } else {
                    LOG.info("{} Authentication failed", ServerConstants.LOGGER_PREFIX_SMS);
                    SmsException lSmsException = SmsException.getSMSExceptionInstance();
                    lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_008));
                    lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_008.toString());
                    lSmsException.setPriority("1");
                    throw lSmsException;
                }
            } else {
                LOG.info("{} No user found matching to the user id : {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getHeader().getUserId());
            }
        } catch (JSONException jsonExp) {
            SmsException lSmsException = SmsException.getSMSExceptionInstance();
            lSmsException.setMessage(jsonExp.getMessage());
            lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
            lSmsException.setPriority("1");
            LOG.error("{} JSONException : {}", ServerConstants.LOGGER_PREFIX_SMS, jsonExp);
            throw lSmsException;
        }
        LOG.debug("{} Final Output Json :: {}", ServerConstants.LOGGER_PREFIX_SMS, lOutputJson);
        return lOutputJson;
    }

    @Override
    public void fetchSession(Message pMessage) {
        LOG.debug("{} Fetching Session", ServerConstants.LOGGER_PREFIX_SMS);
        Session lCurrUser = null;
        Header lHeader = pMessage.getHeader();
        lHeader.setServiceType(ServerConstants.FETCH_USER_SESSION_REQUEST);
        DomainStartup.getInstance().processRequest(pMessage);
        try {
            JSONObject lUserSession = pMessage.getResponseObject().getResponseJson();
            if (lUserSession != null) {
                lCurrUser = Session.getInstance();
                lCurrUser.setLoginTime(lUserSession.getString(ServerConstants.LOGINTIME));
                lCurrUser.setLastRequestTime(lUserSession.getString(ServerConstants.LAST_REQUEST_TIME));
                if (lHeader.getKeepUserSignedIn()) {
                    lCurrUser.setDeviceId(lHeader.getSelector());
                } else {
                    lCurrUser.setDeviceId(lHeader.getDeviceId());
                }
                lCurrUser.setUserName(lHeader.getUserId());
                lCurrUser.setStatus(lHeader.getStatus());
                lCurrUser.setRequestKey(lUserSession.getString(ServerConstants.MESSAGE_HEADER_REQUEST_KEY));
                lCurrUser.setSessionID(lUserSession.getString(ServerConstants.MESSAGE_HEADER_SESSION_ID));
                if (lUserSession.has("otpFlag")) {
                    lCurrUser.setOtpFlag(lUserSession.getString("otpFlag"));
                }
                if (lUserSession.has("cookieExpiryTime")) {
                    lCurrUser.setCookieExpiryTime(lUserSession.getString("cookieExpiryTime"));
                }

                pMessage.setSession(lCurrUser);
            } else {
                pMessage.setSession(lCurrUser);
            }
        } catch (JSONException e) {
            LOG.error("{} Error in JSON, hence leaving the userSession object as it is {}", ServerConstants.LOGGER_PREFIX_SMS, e);
            pMessage.setSession(lCurrUser);
        }
        LOG.debug("{} CurrUser :: {}", ServerConstants.LOGGER_PREFIX_SMS, lCurrUser);
    }

    private boolean isSessionTimedOut(Message pMessage) {
        Session cUserSession = pMessage.getSession();

        if (!checkSessionTimeoutWhenKeepMeSignedInIsEnabled(pMessage, cUserSession)) {
            return false;
        }

        InterfaceDetails lInterfaceDetails = pMessage.getIntfDtls();
        fetchSecurityParamsIfRequired(pMessage);
        SecurityParams params = pMessage.getSecurityParams();
        if (cUserSession == null
                || (cUserSession.getSessionID() == null
                || "".equals(cUserSession.getSessionID())
                && (cUserSession.getRequestKey() == null
                || "".equals(cUserSession.getRequestKey())))) {
            if (lInterfaceDetails.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)) {
                LOG.info("{} User session not found OR Logged Out session found, will proceed to Normal Authentication", ServerConstants.LOGGER_PREFIX_SMS);

                LOG.debug("{} Checking, Is Application Allowed for Multiple Device Login?", ServerConstants.LOGGER_PREFIX_SMS);
                checkIfAppIsAllowedForMultiDeviceLogin(params, pMessage);
                return false;
            } else {
                SmsException sexp = SmsException.getSMSExceptionInstance();
                sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_003));
                sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_003.toString());
                sexp.setPriority("1");
                LOG.error("No Session record found", sexp);
                throw sexp;
            }
        }
        long timebetweenRequests = (new Date().getTime() - Long.parseLong(cUserSession.getLastRequestTime())) / 1000;
        LOG.info("{} the difference in request time is : {} secs", ServerConstants.LOGGER_PREFIX_SMS, timebetweenRequests);

        long sessionTimeoutValue;
        try {
            sessionTimeoutValue = params.getSessionTimeout();
            LOG.debug("{} Getting session Time out value from db....sessionTimeoutValue : {}", ServerConstants.LOGGER_PREFIX_SMS, sessionTimeoutValue);
        } catch (NumberFormatException ex) {
            LOG.error("{} Validating session from properties file, sessionTimeOut property not found : {}", ServerConstants.LOGGER_PREFIX_SMS, ex);
            LOG.warn("{} Setting the sessionTimeoutValue to 0 since the sessionTimeOut property is not found.", ServerConstants.LOGGER_PREFIX_SMS);
            timebetweenRequests = 0;
            sessionTimeoutValue = 1;
        }

        if (timebetweenRequests > sessionTimeoutValue) {
            LOG.info("{} SESSION IS TIMED OUT", ServerConstants.LOGGER_PREFIX_SMS);
            if (!lInterfaceDetails.getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)) {
                SmsException sexp = SmsException.getSMSExceptionInstance();
                sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_003));
                sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_003.toString());
                sexp.setPriority("1");
                LOG.error("Valid session doesn't exists : ", sexp);
                throw sexp;
            } else {
                LOG.info("{} User session found EXPIRED will proceed to Normal Authentication", ServerConstants.LOGGER_PREFIX_SMS);
                LOG.debug("{} Checking, Is Application Allowed for Multiple Device Login?", ServerConstants.LOGGER_PREFIX_SMS);
                checkIfAppIsAllowedForMultiDeviceLogin(params, pMessage);
                return true;
            }
        } else {
            LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "SESSION IS NOT TIMED OUT");
            checkIfSessionAlreadyExists(lInterfaceDetails);
            cUserSession.setLoginTime("" + new Date().getTime());
        }
        return false;
    }

    private boolean checkSessionTimeoutWhenKeepMeSignedInIsEnabled(Message pMessage, Session cUserSession) {
        if (pMessage.getHeader().getKeepUserSignedIn()) {
            if ((Long.parseLong(cUserSession.getCookieExpiryTime()) - new Date().getTime()) / 1000 > 0) {
                return false;
            } else {
                LOG.error("{} cookie expired", ServerConstants.LOGGER_PREFIX_SMS);
                SmsException sexp = SmsException.getSMSExceptionInstance();
                sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_003));
                sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_003.toString());
                sexp.setPriority("1");
                LOG.error("Valid session does not exists", sexp);
                throw sexp;
            }
        }
        return true;
    }

    private void checkIfSessionAlreadyExists(InterfaceDetails lInterfaceDetails) {
        LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "isUserSessionValid - lInterFaceId : " + lInterfaceDetails.getInterfaceId());
        if (ServerConstants.INTERFACE_ID_AUTHENTICATION.equals(lInterfaceDetails.getInterfaceId())) {
            SmsException sexp = SmsException.getSMSExceptionInstance();
            LOG.error("{} A Valid Session Actually Exist.", ServerConstants.LOGGER_PREFIX_SMS);
            sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_004));
            sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_004.toString());
            sexp.setPriority("1");
            throw sexp;
        }
    }

    private void checkIfAppIsAllowedForMultiDeviceLogin(SecurityParams params, Message pMessage) {
        String loginAllowdForMultipleDevice = params.getMultiDviceLoginAlowd();
        if (ServerConstants.NO.equalsIgnoreCase(loginAllowdForMultipleDevice)) {
            LOG.debug("{} No, This Application Only Allowed for Single Device Login", ServerConstants.LOGGER_PREFIX_SMS);
            pMessage.getHeader().setServiceType("checkMultipleDeviceLoginAllowed");
            DomainStartup.getInstance().processRequest(pMessage);
            JSONObject resFromDomain = pMessage.getResponseObject().getResponseJson();
            if (ServerConstants.YES.equals(resFromDomain.getString("otherDeviceExist"))) {
                SmsException sexp = SmsException.getSMSExceptionInstance();
                sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_014));
                sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_014.toString());
                sexp.setPriority("1");
                LOG.error("{} User is Already Logged-in by other device", ServerConstants.LOGGER_PREFIX_SMS);
                throw sexp;
            } else {
                LOG.debug("{} No Other Device Id Exist for this userId.", ServerConstants.LOGGER_PREFIX_SMS);
            }
        } else {
            LOG.debug("{} Yes, This Application Allowed for Multiple Device Login", ServerConstants.LOGGER_PREFIX_SMS);
        }
    }

    private void fetchSecurityParamsIfRequired(Message pMessage) {
        if (pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE) ||
                pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH) ||
                pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE) ||
                pMessage.getRequestObject().getRequestJson().has(ServerConstants.INTERFACE_TYPE) ||
                pMessage.getRequestObject().getRequestJson().has(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)){
            pMessage.getHeader().setServiceType(ServerConstants.FETCH_SECURITY_PARAMS);
            DomainStartup.getInstance().processRequest(pMessage);
            pMessage.getHeader().setServiceType("");
        }
    }

    @Override
    public boolean isSessionValid(Message pMessage) {
        LOG.info(ServerConstants.LOGGER_PREFIX_SMS + "Checking for the session whether it is valid or not");
        Session cUserSession = pMessage.getSession();
        Header lHeader = pMessage.getHeader();
        String lPrevReqKey = getLastRequestKey(cUserSession);
        String lAppReqKey = lHeader.getRequestKey();
        String lSessionId = cUserSession.getSessionID();
        String appSessionId = lHeader.getSessionId();
        String otpFlag = cUserSession.getOtpFlag();
        String lotpRequired = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.OTP_REQUIRED_PROP);
        boolean otp = true;
        if (lotpRequired.equals(ServerConstants.YES) && !ServerConstants.YES.equalsIgnoreCase(otpFlag)) {
            otp = false;
        }
        LOG.info("{} lPrevReqKey : {}, lAppReqKey : {}, appSessionId : {}, lSessionId : {}"
                , ServerConstants.LOGGER_PREFIX_SMS, lPrevReqKey, lAppReqKey, appSessionId, lSessionId);

        if ("true".equalsIgnoreCase(lHeader.getAsynch())) {
            if (appSessionId.equals(lSessionId) && otp) {
                LOG.debug("{} appzillonHeader.get(async) {}", ServerConstants.LOGGER_PREFIX_SMS, lHeader.getAsynch());
                return true;
            }
            return false;
        }
        return appSessionId.equals(lSessionId) && otp;
    }

    private String getLastRequestKey(Session cUserSession) {
        return cUserSession.getRequestKey();
    }

    @Override
    public void createAuthtKey(Message pMessage) {
        LOG.debug("{} Create Auth Key", ServerConstants.LOGGER_PREFIX_SMS);
        Header lHeader = pMessage.getHeader();
        SecureRandom lSecRand = new SecureRandom();
        Double lRand = lSecRand.nextDouble();
        lHeader.setRequestKey("" + lRand);
        pMessage.setHeader(lHeader);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // Override method
    }

    @Override
    public void createSessionID(Message pMessage) {
        LOG.debug("{} Create session Id", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setSessionId(getRandomString());
    }

    @Override
    public void clearSession(Message pMessage) {
        LOG.debug("{} Clear Session", ServerConstants.LOGGER_PREFIX_SMS);
        createSession(pMessage);
    }

    private String getRandomString() {
        byte[] randomByte = getRandomByte();
        return Base64.encodeBase64String(randomByte);
    }

    private byte[] getRandomByte() {
        SecureRandom secRand = new SecureRandom();
        byte[] randomByte = new byte[24];
        secRand.nextBytes(randomByte);
        return randomByte;
    }

    @Override
    public void createSession(Message pMessage) {
        LOG.debug("{} Create session", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(
                ServerConstants.UPDATE_REQUESTKEY_SERVICE);
        DomainStartup.getInstance().processRequest(pMessage);
        LOG.debug("Updated Request Key in DB.....");
    }

    @Override
    public void updateLastReqTime(Message pMessage) {
        DomainStartup.getInstance().processRequest(pMessage);
        LOG.debug("Updated last Request time in DB.");
    }

    private void validateUserAppAccessToken(Message pMessage) {
        LOG.debug("{} inside validateUserAppAccessToken()..", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.VALIDATE_USER_APP_ACCESS_TOKEN);
        DomainStartup.getInstance().processRequest(pMessage);
    }
}
