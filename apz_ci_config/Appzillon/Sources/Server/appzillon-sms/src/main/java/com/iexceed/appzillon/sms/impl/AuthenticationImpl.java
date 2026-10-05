/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.handler.IHandler;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.services.SendSMSService;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.handlers.SessionHandler;
import com.iexceed.appzillon.sms.iface.IAuthentication;
import com.iexceed.appzillon.sms.utils.HashXor;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.InvalidPayloadException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.iexceed.appzillon.utils.ServerConstants.*;

/**
 * @author arthanarisamy
 */
public class AuthenticationImpl implements IAuthentication {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, AuthenticationImpl.class.getName());

    private JSONObject cUserrecord = null;
    private JSONObject cLastloginrecord = null;

    @Override
    public void handleAuthentication(Message pMessage) {

        try {
            LOG.info("{} inside handle Authentication, CheckUser Response", ServerConstants.LOGGER_PREFIX_SMS);

            LOG.debug("{} Security Parameter Details : {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getSecurityParams());
            LOG.debug("{} Device details are not found for the user hence checking whether auto approve is required or not....", ServerConstants.LOGGER_PREFIX_SMS);
            if (ServerConstants.YES.equalsIgnoreCase(pMessage.getSecurityParams().getAutoApprove())) {
                if (!pMessage.getHeader().getKeepUserSignedIn()) {
                    processRequestWhenKeepMeSignedInIsFalse(pMessage);
                } else {
                    processRequestWhenKeepMeSignedInIsTrue(pMessage);
                }

            } else if (ServerConstants.NO.equalsIgnoreCase(pMessage.getSecurityParams().getAutoApprove())) {
                pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CHECK_DEVICE_MOBILE);
                pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST).put(ServerConstants.STATUS, "PENDING");
                DomainStartup.getInstance().processRequest(pMessage);
                if (!pMessage.getResponseObject().getResponseJson().has(ServerConstants.STATUS) || !pMessage.getResponseObject().getResponseJson().getString(ServerConstants.STATUS).equals(ServerConstants.SUCCESS)) {
                    LOG.error("{} Admin Authorization Required!!!", ServerConstants.LOGGER_PREFIX_SMS);
                    SmsException lSmsException = SmsException.getSMSExceptionInstance();
                    lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_009));
                    lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_009.toString());
                    lSmsException.setPriority("1");
                    throw lSmsException;
                }
            }

        } catch (JSONException ex) {
            LOG.error(ServerConstants.JSON_EXCEPTION, ex);
            SmsException lSmsException = SmsException.getSMSExceptionInstance();
            lSmsException.setMessage(ex.getMessage());
            lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
            lSmsException.setPriority("1");
            throw lSmsException;
        }
    }

    private void processRequestWhenKeepMeSignedInIsTrue(Message pMessage) {
        LOG.info("cookie details found so skipping request for ServiceCheckDeviceMobile and finding userId for this cookie");
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CHECK_AND_UPDATE_COOKIE);
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject lOutputJson = this.validateUser(pMessage);
        LOG.debug("{} Validate User Response -: {}", ServerConstants.LOGGER_PREFIX_SMS, lOutputJson);
        pMessage.getResponseObject().setResponseJson(lOutputJson);
        lOutputJson = this.authenticationSuccess(pMessage);
        pMessage.getResponseObject().setResponseJson(lOutputJson);
    }

    private void processRequestWhenKeepMeSignedInIsFalse(Message pMessage) {
        LOG.debug("{} Going To check for login deviceid is available in DEVICE MASTER TABLE.");
        JSONObject lBody = pMessage.getRequestObject().getRequestJson();
        String lHashValue;
        boolean lHashvaluematch = false;
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CHECK_DEVICE_MOBILE);
        pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST).put(ServerConstants.STATUS, "ACTIVE");
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject lOutputJson = this.validateUser(pMessage);
        LOG.debug("{} Validate User Response -: {}", ServerConstants.LOGGER_PREFIX_SMS, lOutputJson);

        String authenticationType = getAuthenticationType(pMessage);

        LOG.info("{} Authentication Type : {}", authenticationType, ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getResponseObject().setResponseJson(lOutputJson);
        if (ServerConstants.HASH_DEVICE_ID.equalsIgnoreCase(authenticationType)) {
            lHashValue = this.computeHashedPwd(pMessage);
            lHashvaluematch = this.otpMatched(lHashValue, lBody.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST).getString(ServerConstants.PIN));
        } else if (ServerConstants.PLAIN_TEXT.equalsIgnoreCase(authenticationType)) {
            JSONObject lAppzbody = lOutputJson.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
            String lPin = lAppzbody.getString(ServerConstants.DBPIN);
            String lHashedPin = HashUtils.hashSHA256(lAppzbody.getString(ServerConstants.PIN), lAppzbody.getString(ServerConstants.MESSAGE_HEADER_USER_ID)
                    + pMessage.getSecurityParams().getServerToken());
            LOG.debug("{} lHashedPin - {}, dbPin - {}", ServerConstants.LOGGER_PREFIX_SMS, lHashedPin, lPin);
            lHashvaluematch = lHashedPin.equalsIgnoreCase(lPin);
        } else if (ServerConstants.THIRD_PARTY_AUTH_ENABLED.equalsIgnoreCase(authenticationType)) {
            lHashvaluematch = true;
        } else {
            LOG.error("{} AuthenticationType is not Clear.", ServerConstants.LOGGER_PREFIX_SMS);
        }

        if (lHashvaluematch) {
            //generate selector here...
            if (lBody.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST).has(ServerConstants.KEEP_ME_SIGNED_IN_FLAG) &&
                    lBody.getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST)
                            .getString(ServerConstants.KEEP_ME_SIGNED_IN_FLAG)
                            .equalsIgnoreCase(ServerConstants.YES)
                    && PropertyUtils.getPropValue(pMessage.getHeader().getAppId(),
                    ServerConstants.KEEP_ME_SIGNED_IN_ENABLED) != null &&
                    PropertyUtils.getPropValue(pMessage.getHeader().getAppId(),
                            ServerConstants.KEEP_ME_SIGNED_IN_ENABLED).equalsIgnoreCase(ServerConstants.YES)
                    && pMessage.getHeader().getDeviceId().equals(ServerConstants.WEB)) {
                LOG.info("keep me signed in is checked so setting selector in the header");
                pMessage.getHeader().setSelector(Utils.generateRandomOfLength(16, ServerConstants.OTP_ALPHA_NUMERIC));
                pMessage.getHeader().setKeepUserSignedIn(true);
            }

            LOG.debug("{} Hash Value Matched for hashed pin from device and Database", ServerConstants.LOGGER_PREFIX_SMS);
            if (ServerConstants.INTERFACE_ID_AUTHENTICATION.equalsIgnoreCase(pMessage.getHeader().getInterfaceId())) {
                pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_VALIDATE_SESSION);
                SessionHandler lSessionHandler = (SessionHandler) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_" + ServerConstants.BEAN_SMS_SESSION_HANDLER);
                lSessionHandler.handleRequest(pMessage);
                LOG.debug("{} UserSession not found OR found timedOut, Proceeding to normal authentication", ServerConstants.LOGGER_PREFIX_SMS);
            }
            if (!ServerConstants.THIRD_PARTY_AUTH_ENABLED.equalsIgnoreCase(authenticationType)) {
                boolean passwordExp = checkPasswordExpiry(pMessage);
                LOG.debug("{} Checking for password expired or not : {}", ServerConstants.LOGGER_PREFIX_SMS, passwordExp);
            }

            LOG.info("{} Authentication Success!!!", ServerConstants.LOGGER_PREFIX_SMS);

            //Purging serverNonce for devices if login is success.
            lOutputJson = this.authenticationSuccess(pMessage);
            LOG.debug("{} Now purging all serverNonce's except current session serverNonce", ServerConstants.LOGGER_PREFIX_SMS);
            pMessage.getHeader().setServiceType(ServerConstants.CLEAR_SERVER_NONCES);
            IHandler domainHandler = (IHandler) SmsStartup.getInstance().getSpringContext().getBean(pMessage.getHeader().getAppId() + "_nonceHandler");
            domainHandler.handleRequest(pMessage);
            pMessage.getHeader().setServiceType("");
        } else {
            lOutputJson = this.authenticationFailed(pMessage);
        }
        pMessage.getResponseObject().setResponseJson(lOutputJson);
    }

    private String getAuthenticationType(Message pMessage) {
        String authenticationType = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.AUTHENTICATION_TYPE);
        String isThirdPartyAuthEnabled = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.THIRD_PARTY_AUTH_ENABLED);

        if (!ServerConstants.YES.equalsIgnoreCase(isThirdPartyAuthEnabled) && (authenticationType == null || authenticationType.isEmpty())) {
            authenticationType = ServerConstants.HASH_DEVICE_ID;
        } else if (ServerConstants.YES.equalsIgnoreCase(isThirdPartyAuthEnabled)) {
            authenticationType = ServerConstants.THIRD_PARTY_AUTH_ENABLED;
        }
        return authenticationType;
    }

    @Override
    public void handleLogout(Message pMessage) {
        LOG.debug("{} Logging out", ServerConstants.LOGGER_PREFIX_SMS);
        try {
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_LOGOUT);
            DomainStartup.getInstance().processRequest(pMessage);
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_INVALIDATE_OTP);
            DomainStartup.getInstance().processRequest(pMessage);
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_DELETE_SESSION_STORAGE);
            DomainStartup.getInstance().processRequest(pMessage);
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_PURGE_NONCE);
            IHandler smsHandler = (IHandler) SmsStartup.getInstance().getSpringContext()
                    .getBean(pMessage.getHeader().getAppId() + "_nonceHandler");
            smsHandler.handleRequest(pMessage);
            JSONObject logoutResponseobj = new JSONObject();
            pMessage.getHeader().setServiceType("");
            logoutResponseobj.put(ServerConstants.MESSAGE_HEADER_STATUS, true);
            pMessage.getResponseObject().setResponseJson(logoutResponseobj);
        } catch (JSONException jsone) {
            LOG.error(ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    @Override
    public void handleReLogin(Message pMessage) {
        LOG.debug("{} Handling ReLogin", ServerConstants.LOGGER_PREFIX_SMS);
        this.handleAuthentication(pMessage);
        try {
            JSONObject lLoginResponse = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_RES);
            LOG.debug("{} Handle ReLogin response: {}", ServerConstants.LOGGER_PREFIX_SMS, lLoginResponse);
            boolean lLoginStatus = lLoginResponse.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS);
            if (lLoginStatus) {
                LOG.debug("{} Handling ReLogin Success", ServerConstants.LOGGER_PREFIX_SMS);
            } else {
                LOG.warn("{} Handling ReLogin failed while relogin", ServerConstants.LOGGER_PREFIX_SMS);
            }
        } catch (JSONException e) {
            LOG.error(ServerConstants.JSON_EXCEPTION, e);
            SmsException lSmsException = SmsException.getSMSExceptionInstance();
            lSmsException.setMessage(lSmsException.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_005));
            lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_005.toString());
            lSmsException.setPriority("1");
            throw lSmsException;
        }
    }

    public JSONObject validateUser(Message pMessage) {
        LOG.info("{} Validating the user", ServerConstants.LOGGER_PREFIX_SMS);
        try {
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUTHENTICATION);
            DomainStartup.getInstance().processRequest(pMessage);

            if (pMessage.getResponseObject().getResponseJson() != null) {
                LOG.info("{} User present in Database", ServerConstants.LOGGER_PREFIX_SMS);
                cUserrecord = pMessage.getResponseObject().getResponseJson().getJSONObject("UserDetails");
                cLastloginrecord = pMessage.getResponseObject().getResponseJson().getJSONObject("LastLogin");
                pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST).put(ServerConstants.DBPIN, cUserrecord.get("Pin"));
                LOG.debug("{} Request Json to build compute OTP request -: {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST));
            } else {
                LOG.info("{} No user found matching to the user id : {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getHeader().getUserId());
            }

        } catch (JSONException ex) {
            SmsException lSmsException = SmsException.getSMSExceptionInstance();
            lSmsException.setMessage(ex.getMessage());
            lSmsException.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
            lSmsException.setPriority("1");
            LOG.error("{} JSONException : {}", ServerConstants.LOGGER_PREFIX_SMS, ex);
            throw lSmsException;
        }
        return pMessage.getRequestObject().getRequestJson();
    }

    public String computeHashedPwd(Message pMessage) {
        String lHashvalue = "";
        JSONObject lUserDetails = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
        LOG.debug("{} computeHashedPwd - User details from Domain's response -: {}", ServerConstants.LOGGER_PREFIX_SMS, lUserDetails);
        Map<String, String> usermap = JSONUtils.getJsonHashMap(lUserDetails.toString());

        String cLoginkey = usermap.get(ServerConstants.PIN);
        String cImei = usermap.get(ServerConstants.HASHKEY1) == null ? "" : usermap.get(ServerConstants.HASHKEY1);
        String cImsi = usermap.get(ServerConstants.HASHKEY2) == null ? "" : usermap.get(ServerConstants.HASHKEY2);
        String cPin = usermap.get(ServerConstants.DBPIN);
        String cDeviceId = usermap.get(ServerConstants.MESSAGE_HEADER_DEVICE_ID);

        if (cPin != null) {
            if (cImsi == null) {
                cImsi = "";
            }
            LOG.info("{} otpRequired is Y, otp Received from client <should be OTP not plain text ..if plain check for web container properties> {}", ServerConstants.LOGGER_PREFIX_SMS, cLoginkey);
            LOG.debug("{} appzillonBody:l_imei -- {}, appzillonBody:l_imsi -- {}, appzillonBody:lDeviceId -- {},dbpin is : {}", ServerConstants.LOGGER_PREFIX_SMS, cImei, cImsi
                    , cDeviceId, cPin);

            lHashvalue = new HashXor().hashValue(cImei, cImsi, "", usermap.get(ServerConstants.MESSAGE_HEADER_USER_ID), cPin, usermap.get(ServerConstants.SYSDATE));
        }
        LOG.info("{} OTP generated from dbpin is : {}", ServerConstants.LOGGER_PREFIX_SMS, lHashvalue);
        return lHashvalue;
    }

    public JSONObject authenticationSuccess(Message pMessage) {
        LOG.debug("{} The request to login user is : {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());
        JSONObject lOutputbodyobj = null;
        String userNameHeaderVal = "";
        String externalIdentifier = "";
        if (cUserrecord != null) {
            userNameHeaderVal = getValue(cUserrecord.has("UserName"), userNameHeaderVal, "UserName", cUserrecord);
            externalIdentifier = getValue(cUserrecord.has("Externalidentifier"), externalIdentifier, "Externalidentifier", cUserrecord);
        }
        Map<String, String> lUserbody = new HashMap<>();

        LOG.info("{} LoginUser : UserId = {}, DeviceId = {},userNameHeader Value = {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getHeader().getUserId(), pMessage.getHeader().getDeviceId(), userNameHeaderVal);

        LOG.debug("{} authenticationSuccess - cLastloginrecord : {}", ServerConstants.LOGGER_PREFIX_SMS, cLastloginrecord);

        if (cUserrecord != null) {
            try {
                boolean lockOpened = true;
                int timeforUserGetLock = 0;
                int failCountLimit = pMessage.getSecurityParams().getNooffailedcounts();
                LOG.info("{} Authentication Success and fail count limit -: {}", ServerConstants.LOGGER_PREFIX_SMS, failCountLimit);
                LOG.debug("{} cUserrecord details : {}", ServerConstants.LOGGER_PREFIX_SMS, cUserrecord);
                int failcount = cUserrecord.getInt(FAIL_COUNT) + 1;
                int countLeft = failCountLimit - failcount;
                if (ServerConstants.YES.equalsIgnoreCase(cUserrecord.getString(USER_LOCKED)) && countLeft < 0) {
                    timeforUserGetLock = pMessage.getSecurityParams().getFailCountTimeout();
                    lockOpened = isLockOpened(timeforUserGetLock);
                } else if (ServerConstants.YES.equalsIgnoreCase(cUserrecord.getString(USER_LOCKED)) && countLeft >= 0) {
                    LOG.error("{} User is either Locked or not active", ServerConstants.LOGGER_PREFIX_SMS);
                    throw getDomainExceptionObject(DomainException.Code.APZ_DM_054, null);
                }

                if (lockOpened) {
                    lOutputbodyobj = processRequestWhenUserLockOpened(pMessage, externalIdentifier, userNameHeaderVal);

                } else {
                    LOG.error("{} Status is false and Lock not opened", ServerConstants.LOGGER_PREFIX_SMS);
                    throw getDomainExceptionObject(DomainException.Code.APZ_DM_013, "User account is locked, please try after : " + timeforUserGetLock / 60 + " minutes");
                }
            } catch (JSONException ex) {
                LOG.error(ServerConstants.JSON_EXCEPTION, ex);
            }
        } else {
            LOG.warn("{} No user record and could not login the user", ServerConstants.LOGGER_PREFIX_SMS);
            lOutputbodyobj = getAppzillonBody(pMessage, lUserbody, false, pMessage.getHeader().getUserId(), "", "", ""); //ripu changes
        }
        JSONObject response = new JSONObject();
        response.put(ServerConstants.APPZILLON_ROOT_LOGIN_RES, lOutputbodyobj);
        return response;
    }

    private String getValue(boolean isKeyAvailable, String value, String key, JSONObject cUserrecord) {
        if (isKeyAvailable) {
            value = cUserrecord.getString(key);
        }
        return value;
    }

    private JSONObject processRequestWhenUserLockOpened(Message pMessage, String externalIdentifier, String userNameHeaderVal) {
        JSONObject lOutputbodyobj = null;
        Map<String, String> lUserbody = new HashMap<>();
        LOG.debug("{} lockOpened is true and updating login status", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.UPDATE_LAST_SUCCESS_LOGIN_SERVICE);
        DomainStartup.getInstance().processRequest(pMessage);

        String flag = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.STATUS);
        if (ServerConstants.YES.equalsIgnoreCase(flag)) {
            LOG.debug("{} login status updated", ServerConstants.LOGGER_PREFIX_SMS);
            String lastLogin = "";
            String profilePic = "";
            if (cLastloginrecord.has("LoginTime")) {
                lastLogin = cLastloginrecord.getString("LoginTime");
            }
            if (cUserrecord.has("ProfilePic")) {
                profilePic = cUserrecord.getString("ProfilePic");
            }

            lOutputbodyobj = getAppzillonBody(pMessage, lUserbody, true, userNameHeaderVal, lastLogin, externalIdentifier, profilePic); //ripu changes
            if (cUserrecord.has("AddInfo1")) {
                lOutputbodyobj.put(ServerConstants.ADDITIONAL_INFO1, cUserrecord.getString("AddInfo1"));
            }
            if (cUserrecord.has("AddInfo2")) {
                lOutputbodyobj.put(ServerConstants.ADDITIONAL_INFO2, cUserrecord.getString("AddInfo2"));
            }
            if (cUserrecord.has("AddInfo3")) {
                lOutputbodyobj.put(ServerConstants.ADDITIONAL_INFO3, cUserrecord.getString("AddInfo3"));
            }
            if (cUserrecord.has("AddInfo4")) {
                lOutputbodyobj.put(ServerConstants.ADDITIONAL_INFO4, cUserrecord.getString("AddInfo4"));
            }
            if (cUserrecord.has("AddInfo5")) {
                lOutputbodyobj.put(ServerConstants.ADDITIONAL_INFO5, cUserrecord.getString("AddInfo5"));
            }
        }
        return lOutputbodyobj;
    }

    private DomainException getDomainExceptionObject(DomainException.Code code, String message) {
        DomainException lDomainException = DomainException.getDomainExceptionInstance();
        lDomainException.setMessage(message == null ? lDomainException.getDomainExceptionMessage(code) : message);
        lDomainException.setCode(code.toString());
        lDomainException.setPriority("1");
        return lDomainException;
    }

    private boolean isLockOpened(int timeforUserGetLock) {
        Timestamp logintime = new Timestamp(new Date().getTime());
        LOG.debug("{} User was locked, checking for lock opened or not", ServerConstants.LOGGER_PREFIX_SMS);
        String lockedTime = cUserrecord.get("UserLockTs").toString();
        SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
        Timestamp timestamp = null;
        try {
            Date lockedTs = formatter.parse(lockedTime);
            timestamp = lockedTs != null ? new Timestamp(lockedTs.getTime()) : null;
        } catch (ParseException e) {
            LOG.error("ParseException -", e);
        }
        int currentLoginFail = cUserrecord.getInt(FAIL_COUNT);
        LOG.debug("{} Time for which user is locked {}, Current Login Failed {}", ServerConstants.LOGGER_PREFIX_SMS, timeforUserGetLock, currentLoginFail);
        return this.loginTimeDiff(logintime, timestamp, timeforUserGetLock, cUserrecord.getString(USER_LOCKED));
    }

    public boolean otpMatched(String pHashvalue, String pPin) {
        LOG.debug("{} matching these two otps {} {}", ServerConstants.LOGGER_PREFIX_SMS, pPin, pHashvalue);
        return pPin.equals(pHashvalue.replaceAll("[\n\r]", ""));
    }

    private boolean checkPasswordExpiry(Message pMessage) {
        LOG.debug("{} Checking whether the password has expired", ServerConstants.LOGGER_PREFIX_SMS);
        try {
            Timestamp currentTime = new Timestamp(new Date().getTime());
            LOG.debug("{} Security Param Pass Change Frequency -: {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getSecurityParams().getPwdChangeFreq());
            int numOfPwdExpDaysCount = pMessage.getSecurityParams().getPwdChangeFreq();
            String pwdCrtTime = "";
            pwdCrtTime = cUserrecord.get("PinChangeTs").toString();
            LOG.debug("{} cUserrecord - password last Changed on: {}", ServerConstants.LOGGER_PREFIX_SMS, pwdCrtTime);
            Date lastPinChangeTime = getLastPinChangeTime(pwdCrtTime);

            Calendar cal = Calendar.getInstance();
            cal.setTime(lastPinChangeTime);
            cal.add(Calendar.DAY_OF_YEAR, numOfPwdExpDaysCount);

            Timestamp pwdExpiryTime = new Timestamp(cal.getTime().getTime());
            LOG.debug("{} cUserrecord - pwdExpiryTime : {}", ServerConstants.LOGGER_PREFIX_SMS, pwdExpiryTime);
            if (currentTime.after(pwdExpiryTime)) {
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage("Password has expired, Please change your password");
                dexp.setCode(DomainException.Code.APZ_DM_031.toString());
                dexp.setPriority("1");
                LOG.error("{} Password has expired {}", ServerConstants.LOGGER_PREFIX_SMS, dexp);
                throw dexp;
            }
        } catch (JSONException e1) {
            LOG.error(ServerConstants.JSON_EXCEPTION, e1);
        }
        return false;
    }

    private Date getLastPinChangeTime(String pwdCrtTime) {
        Date lastPinChangeTime = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
            lastPinChangeTime = formatter.parse(pwdCrtTime);
        } catch (ParseException e) {
            LOG.error(ServerConstants.LOGGER_PREFIX_SMS, e);
        }
        return lastPinChangeTime;
    }

    public JSONObject authenticationFailed(Message pMessage) {
        LOG.info("{} Authentication failed", ServerConstants.LOGGER_PREFIX_SMS);
        try {
            JSONObject usermap = pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
            LOG.debug("{} {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());
            int failCountLimit = pMessage.getSecurityParams().getNooffailedcounts();
            LOG.info("{} Authentication failed and fail count limit : {}", ServerConstants.LOGGER_PREFIX_SMS, failCountLimit);
            LOG.debug("{} cUserrecord details : {}", ServerConstants.LOGGER_PREFIX_SMS, cUserrecord);
            int countLeft = failCountLimit - cUserrecord.getInt(FAIL_COUNT);
            LOG.debug("{} authentication failed - no of attempts left : {}", ServerConstants.LOGGER_PREFIX_SMS, countLeft);
            boolean lockUser = false;
            pMessage.getHeader().setServiceType(ServerConstants.UPDATELSTFLRLGNSERVICE);
            if (ServerConstants.YES.equals(cUserrecord.getString(USER_LOCKED)) && countLeft <= 0) {
                lockUser = isLockUser(pMessage);
            }
            //if user is locked manually it will not update
            else if (ServerConstants.YES.equalsIgnoreCase(cUserrecord.getString(USER_LOCKED)) && countLeft >= 0) {
                LOG.error("{} User is either Locked or not active", ServerConstants.LOGGER_PREFIX_SMS);
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_054));
                lDomainException.setCode(DomainException.Code.APZ_DM_054.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            } else if (ServerConstants.NO.equalsIgnoreCase(cUserrecord.getString(USER_LOCKED)) && countLeft <= 0) {
                LOG.info("{} Locking User", ServerConstants.LOGGER_PREFIX_SMS);
                lockUser = true;
                pMessage.getHeader().setServiceType(ServerConstants.LOCKUSER);
            }
            DomainStartup.getInstance().processRequest(pMessage);
            if (lockUser) {
                LOG.error("{} User Locked", ServerConstants.LOGGER_PREFIX_SMS);
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_013));
                lDomainException.setCode(DomainException.Code.APZ_DM_013.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            }
            usermap.remove(ServerConstants.DBPIN);
            JSONObject lResponse = new JSONObject();
            lResponse.put(ServerConstants.MESSAGE_HEADER_STATUS, false);
            lResponse.put(ServerConstants.FAILURE_ATTEMPTS_LEFT, countLeft - 1);
            lResponse.put(ServerConstants.MESSAGE_HEADER_USER_ID, pMessage.getHeader().getUserId());
            JSONObject lLoginResp = new JSONObject();
            lLoginResp.put(ServerConstants.APPZILLON_ROOT_LOGIN_RES, lResponse);
            return lLoginResp;
        } catch (JSONException ex) {
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(ex.getMessage());
            lDomainException.setCode(DomainException.Code.APZ_DM_001.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }

    private boolean isLockUser(Message pMessage) {
        Timestamp logintime = new Timestamp(new Date().getTime());
        boolean lockUser = false;
        try {
            boolean lockOpened;
            int timeforUserGetLock = 0;
            String lockedTime = cUserrecord.get("UserLockTs").toString();
            Timestamp timestamp = getLockedTS(lockedTime);

            timeforUserGetLock = pMessage.getSecurityParams().getFailCountTimeout();
            int currentLoginFail = cUserrecord.getInt(FAIL_COUNT);
            LOG.debug("{} Time for which user is locked {}, Current Login Failed {}", ServerConstants.LOGGER_PREFIX_SMS, timeforUserGetLock, currentLoginFail);
            lockOpened = this.loginTimeDiff(logintime, timestamp, timeforUserGetLock, cUserrecord.getString(USER_LOCKED));
            if (lockOpened) {
                LOG.debug("{} Checking for Lock opened or not", ServerConstants.LOGGER_PREFIX_SMS);
                pMessage.getHeader().setServiceType(ServerConstants.UNLOCK_UPDATE_FAIL_COUNT);
                DomainStartup.getInstance().processRequest(pMessage);
            } else {
                lockUser = true;
                pMessage.getHeader().setServiceType(ServerConstants.LOCKUSER);
            }
        } catch (JSONException ex) {
            LOG.error(ServerConstants.JSON_EXCEPTION, ex);
        }
        return lockUser;
    }

    private Timestamp getLockedTS(String lockedTime) {
        Timestamp timestamp = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
            Date lockedTs = formatter.parse(lockedTime);
            timestamp = lockedTs != null ? new Timestamp(lockedTs.getTime()) : null;
        } catch (ParseException e) {
            LOG.error("ParseException -", e);
        }
        return timestamp;
    }

    private boolean loginTimeDiff(Timestamp currentTime, Timestamp userLockedTime, int timeForUserGetLock, String userLocked) {
        LOG.debug("{} checking the time difference between locked time {} and current time {}", ServerConstants.LOGGER_PREFIX_SMS, userLockedTime, currentTime);
        if ("Y".equals(userLocked) && (userLockedTime != null)) {
            long timebetweenRequests = (currentTime.getTime() - userLockedTime.getTime()) / 1000;
            LOG.debug("{} Received request after {} sec after getting locked", ServerConstants.LOGGER_PREFIX_SMS, timebetweenRequests);
            LOG.debug("{} Should be > {} sec", ServerConstants.LOGGER_PREFIX_SMS, timeForUserGetLock);
            if (timebetweenRequests > timeForUserGetLock) {
                LOG.debug("{} User should get unlock", ServerConstants.LOGGER_PREFIX_SMS);
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    public JSONObject getAppzillonBody(Message pMessage, Map<String, String> userbody, boolean canProceed, String userName, String lastLogin, String pExternalIdentifier, String profilePic) {
        LOG.info("{} GetAppzillonBody.canProceed : {}, userName : {}", ServerConstants.LOGGER_PREFIX_SMS, canProceed, userName);

        String lotpRequired = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.OTP_REQUIRED_PROP);
        LOG.info("{} otpRequired is {}", ServerConstants.LOGGER_PREFIX_SMS, lotpRequired);

        JSONObject userDet = new JSONObject();

        if (ServerConstants.NO.equalsIgnoreCase(lotpRequired)) {
            userDet.put(ServerConstants.NAME, userName);
            userDet.put(ServerConstants.LAST_LOGIN, lastLogin);
            userDet.put(ServerConstants.OTP_REQUIRED_ELEMENT, ServerConstants.NO);
            userDet.put(ServerConstants.USERDET_EXTERNALIDENTIFIER, pExternalIdentifier);
            userDet.put(ServerConstants.ID, pMessage.getHeader().getUserId());
            userDet.put(ServerConstants.USER_PROFILE_PIC, profilePic);
        } else {
            this.generateOtp(pMessage);
            userDet.put(ServerConstants.OTP_REQUIRED_ELEMENT, ServerConstants.YES);
            userDet.put(ServerConstants.NAME, userName);
            userDet.put(ServerConstants.LAST_LOGIN, lastLogin);
            userDet.put(ServerConstants.USERDET_EXTERNALIDENTIFIER, pExternalIdentifier);
            userDet.put(ServerConstants.ID, pMessage.getHeader().getUserId());
            userDet.put(ServerConstants.USER_PROFILE_PIC, profilePic);
        }
        JSONObject obj = new JSONObject(userbody).put(ServerConstants.MESSAGE_HEADER_STATUS, canProceed);
        obj.put("userDet", userDet);
        LOG.debug("obj :: {}", obj);
        return obj;
    }

    private void generateOtp(Message pMessage) {
        LOG.info("{} Going to Generate Otp", ServerConstants.LOGGER_PREFIX_SMS);
        String lOTPType = pMessage.getSecurityParams().getOtpFormat();
        int lOtpLength = pMessage.getSecurityParams().getOtpLength();
        String otp = Utils.generateRandomOfLength(lOtpLength, lOTPType);
        String encrptedOtp = AppzillonAESUtils.encryptString(pMessage.getSecurityParams().getServerToken(), otp);
        JSONObject jsObj = new JSONObject();
        jsObj.put("otp", encrptedOtp);
        pMessage.getHeader().setServiceType("persistOtp");
        pMessage.getRequestObject().setRequestJson(jsObj);
        DomainStartup.getInstance().processRequest(pMessage);
        String iface = pMessage.getHeader().getInterfaceId();
        //to check otpchannel
        String lotpchannel = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.OTP_CHANNEL);
        pMessage.getHeader().setAsyncMail(true);
        if (lotpchannel.equalsIgnoreCase("Mail")) {
            this.sendMailtoUser(pMessage, otp);
            pMessage.getHeader().setInterfaceId(iface);
        } else if (lotpchannel.equalsIgnoreCase("SMS")) {
            LOG.debug("{} otp to be sent is {}", ServerConstants.LOGGER_PREFIX_SMS, otp);
            sendSMS(pMessage, otp);
        } else {
            this.sendMailtoUser(pMessage, otp);
            pMessage.getHeader().setInterfaceId(iface);
            LOG.debug("{} otp to be sent is  {}", ServerConstants.LOGGER_PREFIX_SMS, otp);
            sendSMS(pMessage, otp);
        }
    }

    public void sendMailtoUser(Message pMessage, String otp) {
        LOG.debug("{} Inside sendMailtoUser..", ServerConstants.LOGGER_PREFIX_SMS);
        String status = null;
        String emailId = null;
        JSONObject responseJson = null;

        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        pMessage.getHeader().setInterfaceId(ServerConstants.APPZILLON_ROOT_USER_EMAILID_REQ);

        try {
            DomainStartup.getInstance().processRequest(pMessage);
            responseJson = pMessage.getResponseObject().getResponseJson();
            emailId = (String) responseJson.get(ServerConstants.APPZILLON_ROOT_EMAILID);

            pMessage.getHeader().setServiceType("getUserLanguage");
            DomainStartup.getInstance().processRequest(pMessage);
            String language = ServerConstants.APPZILLON_ROOT_LNGEN;
            if (pMessage.getResponseObject().getResponseJson().has(ServerConstants.LANGUAGE)) {
                language = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.LANGUAGE);
            }
            LOG.debug("{} language is  :: {}", ServerConstants.LOGGER_PREFIX_SMS, language);
            pMessage.getHeader().setInterfaceId("appzillonMailRequest");
            pMessage.getIntfDtls().setType("MAIL");
            Properties propfile = new Properties();
            String lFileName = Utils.getOtpSendFileName(pMessage.getHeader().getAppId(), language, ServerConstants.EMAIL);

            LOG.debug("{} OTPSend - SendMail mail template file name - l_fileName: {}", ServerConstants.LOGGER_PREFIX_SMS, lFileName);

            try (InputStream is = PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName)) {
                propfile.load(is);
            }

            String templateBody = propfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
            templateBody = templateBody.replace("$password", otp);

            String templateSubject = propfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);

            String body = "{'appzillonMailRequest':{'emailid':'" + emailId + "', 'body':'" + templateBody + "', 'subject':'" + templateSubject + "'}}";
            JSONObject jsonBody = new JSONObject(body);
            pMessage.getRequestObject().setRequestJson(jsonBody);
            FrameworksStartup.getInstance().processRequest(pMessage);
            status = "success";
        } catch (ExternalServicesRouterException exp) {
            status = "Fail";
            LOG.error("{} ExternalServicesRouterException -: {}", ServerConstants.LOGGER_PREFIX_SMS, Utils.getStackTrace(exp));
        } catch (JSONException exp) {
            status = "Fail";
            LOG.error("{} JSONException -: {}", ServerConstants.LOGGER_PREFIX_SMS, Utils.getStackTrace(exp));
        } catch (IOException exp) {
            status = "Fail";
            LOG.error("{} IOException -: {}", ServerConstants.LOGGER_PREFIX_SMS, Utils.getStackTrace(exp));
        } catch (ClassNotFoundException exp) {
            status = "Fail";
            LOG.error("{} ClassNotFoundException -: {}", ServerConstants.LOGGER_PREFIX_SMS, Utils.getStackTrace(exp));
        } catch (InvalidPayloadException exp) {
            status = "Fail";
            LOG.error("{} InvalidPayloadException -: {}", ServerConstants.LOGGER_PREFIX_SMS, Utils.getStackTrace(exp));
        }
        LOG.debug("{} mail sending status :: {}", ServerConstants.LOGGER_PREFIX_SMS, status);
    }

    @Override
    public void validateOTP(Message pMessage) {
        LOG.debug("{} in validateOTP() ..Going to validate otp", ServerConstants.LOGGER_PREFIX_SMS);
        JSONObject lOutputbodyobj = null;
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUTHENTICATION);
        DomainStartup.getInstance().processRequest(pMessage);
        cUserrecord = pMessage.getResponseObject().getResponseJson().getJSONObject("UserDetails");
        cLastloginrecord = pMessage.getResponseObject().getResponseJson().getJSONObject("LastLogin");
        pMessage.getHeader().setServiceType("validateOTP");
        DomainStartup.getInstance().processRequest(pMessage);
        String status = pMessage.getResponseObject().getResponseJson().getJSONObject("validateOtp").getString(ServerConstants.MESSAGE_HEADER_STATUS);
        if (ServerConstants.SUCCESS.equalsIgnoreCase(status)) {
            pMessage.getHeader().setServiceType(ServerConstants.UPDATE_LAST_SUCCESS_LOGIN_SERVICE);
            DomainStartup.getInstance().processRequest(pMessage);

            String flag = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.STATUS);
            if (ServerConstants.YES.equalsIgnoreCase(flag)) {
                LOG.debug("{} Status is true and Lock opened", ServerConstants.LOGGER_PREFIX_SMS);
                lOutputbodyobj = new JSONObject().put(ServerConstants.STATUS, ServerConstants.YES);
            }

        } else {
            lOutputbodyobj = new JSONObject().put(ServerConstants.STATUS, ServerConstants.NO);
        }
        JSONObject response = new JSONObject();
        response.put("ValidateOtpResponse", lOutputbodyobj);
        LOG.debug("{} Login User output response : {}", ServerConstants.LOGGER_PREFIX_SMS, response);
        pMessage.getResponseObject().setResponseJson(response);
    }

    public void sendSMS(Message pMessage, String potp) {
        try {
            pMessage.getHeader().setServiceType("getUserMobileNumber");
            DomainStartup.getInstance().processRequest(pMessage);
            String mobileNumber = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.MOBILENUMBER);
            SendSMSService smsService = new SendSMSService();
            pMessage.getHeader().setServiceType("getUserLanguage");
            DomainStartup.getInstance().processRequest(pMessage);
            String language = ServerConstants.APPZILLON_ROOT_LNGEN;
            if (pMessage.getResponseObject().getResponseJson().has(ServerConstants.LANGUAGE)) {
                language = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.LANGUAGE);
            }
            LOG.debug("{} language is  :: {}", ServerConstants.LOGGER_PREFIX_SMS, language);
            Properties propfile = new Properties();
            String lFileName = Utils.getOtpSendFileName(pMessage.getHeader().getAppId(), language, ServerConstants.MSG);
            LOG.debug("{} OTPSend - SendMail mail template file name - l_fileName: {}", ServerConstants.LOGGER_PREFIX_SMS, lFileName);
            try (InputStream is = PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName)) {
                propfile.load(is);
            }
            String templateBody = propfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
            templateBody = templateBody.replace("$password", potp);
            smsService.sendSMS(pMessage.getHeader().getAppId(), mobileNumber, templateBody);
        } catch (Exception e) {
            LOG.error("{} in sendSMS() .. {}", ServerConstants.LOGGER_PREFIX_SMS, e.getMessage());
        }
    }

    @Override
    public void reGenerateOTP(Message pMessage) {
        LOG.debug("{} in reGenerateOTP() ..Going to reGenerate otp", ServerConstants.LOGGER_PREFIX_SMS);
        this.generateOtp(pMessage);
        JSONObject response = new JSONObject();
        response.put(ServerConstants.STATUS, ServerConstants.SUCCESS);
        pMessage.getResponseObject().setResponseJson(new JSONObject().put("ReGenerateOtpResponse", response));

    }

    @Override
    public void getUserAppAccessToken(Message pMessage) {
        LOG.debug("{} Inside getToken() and request is {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getRequestObject().getRequestJson());
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_GET_USER_APP_ACCESS_TOKEN);
        DomainStartup.getInstance().processRequest(pMessage);
    }

}
