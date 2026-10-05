package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.*;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.exception.OtpStatusUpdationException;
import com.iexceed.appzillon.domain.repository.admin.*;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.maputils.MapUtils;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Timestamp;
import java.util.*;

import static com.iexceed.appzillon.domain.utils.Constants.DOUBLE_BRACES;

/**
 * @author arthanarisamy
 */
@Named("AuthenticationService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class AuthenticationService {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            AuthenticationService.class.getName());

    @Inject
    TbAstpLastLoginRepository cAstpLastLoginRepo;
    @Inject
    TbAsmiUserRepository cAsmiUserRepo;
    @Inject
    TbAsmiUserDevicesRepository cAsmiUserDevicesRepo;
    @Inject
    TbAsmiCookiesRepository cAsmiCookiesRepo;
    @Inject
    TbAsmiAppAccessTokenRepository cAsmiAppAccessTokenRepo;

    public void validateUser(Message pMessage) {
        LOG.debug("{} inside validateUser", ServerConstants.LOGGER_PREFIX_DOMAIN);
        JSONObject lAsmiUserDetJson = null;
        JSONObject lAstpLastLoginJson = null;
        JSONObject lValidateUserResp = null;

        TbAstpLastLogin lAstpLastLogin = null;
        List<TbAstpLastLogin> lAstpLastLoginList = cAstpLastLoginRepo.findByUserIdAndAppIdOrderByLoginTime(
                pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId());
        if (!lAstpLastLoginList.isEmpty()) {
            lAstpLastLogin = lAstpLastLoginList.get(0);
            LOG.debug("{} Fetched user last login record : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lAstpLastLogin);
        }
        TbAsmiUser lAsmiUserDet = cAsmiUserRepo.findUsersByAppIdUserIdUserActive(pMessage.getHeader().getUserId(),
                pMessage.getHeader().getAppId());

        if (lAsmiUserDet != null) {

            lValidateUserResp = new JSONObject();
            try {
                if (ServerConstants.UNAUTHORIZED.equalsIgnoreCase(lAsmiUserDet.getAuthStatus())) {
                    DomainException lDomainException = DomainException.getDomainExceptionInstance();
                    String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_053);
                    lDomainException.setMessage(emsg);
                    lDomainException.setCode(DomainException.Code.APZ_DM_053.toString());
                    lDomainException.setPriority("1");
                    LOG.error("{} User status is unauthorized ", ServerConstants.LOGGER_PREFIX_DOMAIN,
                            lDomainException);
                    throw lDomainException;
                }

                LOG.debug("{} Now looking for user registered devices ", ServerConstants.LOGGER_PREFIX_DOMAIN);
                TbAsmiUserDevicesPK tbAsmiUserDevicesPK = new TbAsmiUserDevicesPK(pMessage.getHeader().getDeviceId(),
                        pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId());

                if ((pMessage.getHeader().getPin() == null || pMessage.getHeader().getPin().isEmpty())
                        && (!cAsmiUserDevicesRepo.existsById(tbAsmiUserDevicesPK))) {
                    DomainException lDomainException = DomainException.getDomainExceptionInstance();
                    lDomainException
                            .setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_029));
                    lDomainException.setCode(DomainException.Code.APZ_DM_029.toString());
                    lDomainException.setPriority("1");
                    LOG.error("{} device doesnot matched with USER's devices for this App",
                            ServerConstants.LOGGER_PREFIX_DOMAIN, lDomainException);
                    throw lDomainException;
                }

                Map<String, String> userDetMap = MapUtils.convertObjectToMap(lAsmiUserDet);
                userDetMap.put(ServerConstants.AUTH_USER_ID, pMessage.getHeader().getUserId());
                userDetMap.put(ServerConstants.AUTH_APP_ID, pMessage.getHeader().getAppId());
                lAsmiUserDetJson = JSONUtils.getJsonStringFromMap(userDetMap);
                lValidateUserResp.put("UserDetails", lAsmiUserDetJson);

                if (lAstpLastLogin != null) {
                    Map<String, String> userlastLoginMap = MapUtils.convertObjectToMap(lAstpLastLogin);
                    userlastLoginMap.put(ServerConstants.AUTH_USER_ID, pMessage.getHeader().getUserId());
                    userlastLoginMap.put(ServerConstants.AUTH_APP_ID, pMessage.getHeader().getAppId());
                    lAstpLastLoginJson = JSONUtils.getJsonStringFromMap(userlastLoginMap);
                    lValidateUserResp.put(ServerConstants.AUTH_LAST_LOGIN, lAstpLastLoginJson);
                } else {
                    lValidateUserResp.put(ServerConstants.AUTH_LAST_LOGIN, new JSONObject());
                }
            } catch (IllegalArgumentException ex) {
                LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN,
                        ServerConstants.ILLEGAL_ARGUMENT_EXCEPTION, ex);
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                lDomainException
                        .setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_006));
                lDomainException.setCode(DomainException.Code.APZ_DM_006.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            }
        } else {
            LOG.info("{} User Does not Exist In Database", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_001));
            lDomainException.setCode(DomainException.Code.APZ_DM_001.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
        pMessage.getResponseObject().setResponseJson(lValidateUserResp);
    }

    public void getUserIdUsingCookie(Message pMessage) {
        String appId = pMessage.getHeader().getAppId();
        TbAsmiCookiesPK lAsmiCookiesPK = new TbAsmiCookiesPK(appId, pMessage.getHeader().getSelector());
        Optional<TbAsmiCookies> lAsmiCookie = cAsmiCookiesRepo.findById(lAsmiCookiesPK);
        String hashedValidator = (HashUtils.hashSHA256(pMessage.getHeader().getSessionId(),
                pMessage.getHeader().getSelector() + pMessage.getSecurityParams().getServerToken()));
        boolean throwEx = false;
        if (lAsmiCookie.isPresent()) {
            boolean cookieAlive = ((lAsmiCookie.get().getExpiryTs().getTime() - new Date().getTime()) / 1000 > 0);
            boolean otpStatus = checkOtpStatus(pMessage);
            if (lAsmiCookie.get().getValidator().equals(hashedValidator) && otpStatus) {
                if (cookieAlive) {
                    String userId = lAsmiCookie.get().getuserId();
                    LOG.info(
                            "{} validator matched, creating new validator and adding userId in the header for userId : {}",
                            ServerConstants.LOGGER_PREFIX_DOMAIN, userId);
                    pMessage.getHeader().setUserId(lAsmiCookie.get().getuserId());
                    pMessage.getRequestObject().getRequestJson()
                            .getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST)
                            .put(ServerConstants.STATUS, "ACTIVE");
                    pMessage.getRequestObject().getRequestJson()
                            .getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST)
                            .put("userId", lAsmiCookie.get().getuserId());
                } else {
                    DomainException lDomainException = DomainException.getDomainExceptionInstance();
                    String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_077);
                    lDomainException.setMessage(emsg);
                    lDomainException.setCode(DomainException.Code.APZ_DM_077.toString());
                    lDomainException.setPriority("1");
                    LOG.error("{} Cookie is expired.", ServerConstants.LOGGER_PREFIX_DOMAIN, lDomainException);
                    throw lDomainException;
                }
            } else
                throwEx = true;
        } else
            throwEx = true;
        if (throwEx) {
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_079);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_079.toString());
            lDomainException.setPriority("1");
            LOG.error("{} Invalid user cookie.", ServerConstants.LOGGER_PREFIX_DOMAIN, lDomainException);
            throw lDomainException;
        }
    }

    private boolean checkOtpStatus(Message pMessage) {
        TbAstpLastLoginPK tbAstpLastLoginPK = tbAstpLastLoginPK(pMessage);
        Optional<TbAstpLastLogin> lastLogin = cAstpLastLoginRepo.findById(tbAstpLastLoginPK);
        if (lastLogin.isPresent() && lastLogin.get().getOtpFlag().equals(ServerConstants.NO)) {
            LOG.info("invalid request when keep me signed in is enabled previous otp didn't validate");
            return false;
        } else {
            return true;
        }

    }

    public void populateLastSuccessLoginDetails(Message pMessage) throws DomainException {
        JSONObject lResponse = null;
        try {
            TbAsmiUserPK lAsmiUserId = new TbAsmiUserPK(pMessage.getHeader().getUserId(),
                    pMessage.getHeader().getAppId());
            Optional<TbAsmiUser> lAsmiUser = cAsmiUserRepo.findById(lAsmiUserId);
            if (lAsmiUser.isPresent()) {
                lAsmiUser.get().setLoginStatus(ServerConstants.YES);
                lAsmiUser.get().setUserLocked(ServerConstants.NO);
                lAsmiUser.get().setFailCount(0);
                lAsmiUser.get().setUserLockTs(null);
                cAsmiUserRepo.save(lAsmiUser.get());
                lResponse = new JSONObject();
                lResponse.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.RESP_BODY_STATUS_SUCCESS);
            } else {
                lResponse = new JSONObject();
                lResponse.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.RESP_BODY_STATUS_ERROR);
            }
        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(ex.getMessage());
            lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
        pMessage.getResponseObject().setResponseJson(lResponse);
    }

    public void updateLastReqTime(Message pMessage) {
        cAstpLastLoginRepo.updateLastReqTime(pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId(),
                pMessage.getHeader().getDeviceId(), pMessage.getHeader().getSessionId(),
                new Timestamp(new Date().getTime()));
    }

    public void upDateRequestKeySessionID(Message pMessage) {
        TbAstpLastLoginPK lAstpLastLoginId = tbAstpLastLoginPK(pMessage);
        Optional<TbAstpLastLogin> lAstpLastLoginOpt;
        TbAstpLastLogin lAstpLastLogin = null;
        JSONObject location = pMessage.getHeader().getLocation();
        String longitude = "0";
        String latitude = "0";
        String adminAreaLvl1 = "";
        String adminAreaLvl2 = "";
        String country = "";
        String sublocality = "";
        String formattedAddress = "";

        if (location != null) {
            LOG.debug("{} Location details {}", ServerConstants.LOGGER_PREFIX_DOMAIN, location.toString());
            longitude = getAttribute(location, ServerConstants.LONGITUDE, "0");
            latitude = getAttribute(location, ServerConstants.LATITUDE, "0");
            adminAreaLvl1 = getAttribute(location, ServerConstants.ADMIN_AREA_LVL_1, "");
            adminAreaLvl2 = getAttribute(location, ServerConstants.ADMIN_AREA_LVL_2, "");
            country = getAttribute(location, ServerConstants.COUNTRY, "");
            sublocality = getAttribute(location, ServerConstants.SUBLOCALITY, "");
            formattedAddress = getAttribute(location, ServerConstants.FORAMATTED_ADDRESS, ServerConstants.DEFAULT_FORMATTED_ADDRESS);
        }
        lAstpLastLoginOpt = cAstpLastLoginRepo.findById(lAstpLastLoginId);
        if (lAstpLastLoginOpt.isPresent())
            lAstpLastLogin = lAstpLastLoginOpt.get();
        Timestamp lLastreqtime = null;
        if (lAstpLastLogin != null) {
            LOG.debug("{} User Details present in last login table, requestKey : {} SESSIONID : {}",
                    ServerConstants.LOGGER_PREFIX_DOMAIN, lAstpLastLogin.getRequestKey(),
                    lAstpLastLogin.getSessionId());
            if (!pMessage.getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_LOGOUT)) {
                lLastreqtime = new Timestamp(new Date().getTime());
                lAstpLastLogin.setRequestKey(pMessage.getHeader().getRequestKey());
                lAstpLastLogin.setLastReqTime(lLastreqtime);
                lAstpLastLogin.setLatitude(latitude);
                lAstpLastLogin.setLongitude(longitude);
                LOG.debug("{} adminAreaLvl1 : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, adminAreaLvl1);
                lAstpLastLogin.setAdminAreaLvl1(adminAreaLvl1);
                lAstpLastLogin.setAdminAreaLvl2(adminAreaLvl2);
                lAstpLastLogin.setCountry(country);
                lAstpLastLogin.setFormattedAddress(formattedAddress);
                lAstpLastLogin.setSublocality(sublocality);
                lAstpLastLogin.setOrigination(pMessage.getHeader().getOrigination());
                lAstpLastLogin.setSessionId(pMessage.getHeader().getSessionId());
                handleAuthenticationAndRelogin(pMessage, lAstpLastLogin);
            } else {
                LOG.debug("{} Handling logout Updation of Astp Last Login", ServerConstants.LOGGER_PREFIX_DOMAIN);
                lLastreqtime = new Timestamp(new Date().getTime());
                lAstpLastLogin.setRequestKey(null);
                lAstpLastLogin.setSessionId(null);
                lAstpLastLogin.setLastReqTime(lLastreqtime);
                LOG.debug("User login time : ", lAstpLastLogin.getLoginTime());
                LOG.debug("User logout time : ", lLastreqtime);
                deleteCookie(pMessage);
            }
            lAstpLastLogin.setVersionNo(lAstpLastLogin.getVersionNo() + 1);
            cAstpLastLoginRepo.save(lAstpLastLogin);
        } else {
            LOG.debug("{} User Details not present in last login table.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            if (pMessage.getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN)
                    || pMessage.getHeader().getInterfaceId()
                    .equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)) {
                checkKeepUserSignedIn(pMessage);
                lAstpLastLogin = new TbAstpLastLogin();
                lAstpLastLogin.setTbAstpLastLoginPK(lAstpLastLoginId);
                lAstpLastLogin.setLoginTime(new Date());
                lAstpLastLogin.setLastReqTime(new Date());
                lAstpLastLogin.setCreateTs(new Date());
                lAstpLastLogin.setLatitude(latitude);
                lAstpLastLogin.setLongitude(longitude);
                LOG.debug("{} adminAreaLvl1 : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, adminAreaLvl1);
                lAstpLastLogin.setAdminAreaLvl1(adminAreaLvl1);
                lAstpLastLogin.setAdminAreaLvl2(adminAreaLvl2);
                lAstpLastLogin.setCountry(country);
                lAstpLastLogin.setFormattedAddress(formattedAddress);
                lAstpLastLogin.setSublocality(sublocality);
                lAstpLastLogin.setOrigination(pMessage.getHeader().getOrigination());
                lAstpLastLogin.setVersionNo(1);
                lAstpLastLogin.setRequestKey(pMessage.getHeader().getRequestKey());
                lAstpLastLogin.setSessionId(pMessage.getHeader().getSessionId());
                LOG.debug("{} SETTING THIS SESSION LOGIN TIME : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, new Date());
                cAstpLastLoginRepo.save(lAstpLastLogin);
            }

        }
        if (pMessage.getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_RE_LOGIN) || pMessage
                .getHeader().getInterfaceId().equalsIgnoreCase(ServerConstants.INTERFACE_ID_AUTHENTICATION)) {
            LOG.debug("{} Since interfaceId is appzillonReLoginRequest, so its going to clear all existing session.",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            clearOtherSessoin(pMessage);
            if (Utils
                    .isNotNullOrEmpty(PropertyUtils.getPropValue(pMessage.getHeader().getAppId(),
                            ServerConstants.KEEP_ME_SIGNED_IN_ENABLED))
                    && PropertyUtils
                    .getPropValue(pMessage.getHeader().getAppId(), ServerConstants.KEEP_ME_SIGNED_IN_ENABLED)
                    .equalsIgnoreCase(ServerConstants.YES)) {
                clearCookiesForUserId(pMessage);
            }
        }
    }

    private void checkKeepUserSignedIn(Message pMessage) {
        if (pMessage.getHeader().getKeepUserSignedIn()) {
            createCookie(pMessage);
        }
    }

    private void handleAuthenticationAndRelogin(Message pMessage, TbAstpLastLogin lAstpLastLogin) {
        if (pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_AUTHENTICATION)
                || pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_RE_LOGIN)) {

            lAstpLastLogin.setLoginTime(new Timestamp(new Date().getTime()));
            LOG.debug("{} Setting login time in tbAstpLastLogin table : {}",
                    ServerConstants.LOGGER_PREFIX_DOMAIN, lAstpLastLogin.getLoginTime());

            checkKeepUserSignedIn(pMessage);

        } else {
            LOG.debug("{} Updating lastReq time in tbAstpLastLogin table",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
        }
    }

    private String getAttribute(JSONObject location, String key, String value) {
        return location.has(key) ? location.getString(key) : value;
    }

    public void createCookie(Message pMessage) {
        LOG.debug("{} inside create Cookie", ServerConstants.LOGGER_PREFIX_DOMAIN);
        TbAsmiCookiesPK lAsmiCookiePK = new TbAsmiCookiesPK(pMessage.getHeader().getAppId(),
                pMessage.getHeader().getSelector());
        TbAsmiCookies lAsmiCookie = new TbAsmiCookies();
        lAsmiCookie.setId(lAsmiCookiePK);
        lAsmiCookie.setValidator(HashUtils.hashSHA256(pMessage.getHeader().getSessionId(),
                pMessage.getHeader().getSelector() + pMessage.getSecurityParams().getServerToken()));
        lAsmiCookie.setUserId(pMessage.getHeader().getUserId());
        Calendar c = Calendar.getInstance();
        lAsmiCookie.setCreateTs(c.getTime());
        int cookieExpiryDays = Integer
                .parseInt(PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.COOKIE_AGE));
        c.add(Calendar.DATE, cookieExpiryDays);
        lAsmiCookie.setExpiryTs(c.getTime());
        lAsmiCookie.setVersionNo(1);
        cAsmiCookiesRepo.save(lAsmiCookie);
    }

    public void deleteCookie(Message pMessage) {
        // need to check if selector is not null before deleting
        if (pMessage.getHeader().getKeepUserSignedIn()) {
            TbAsmiCookiesPK lAsmiCookiePK = new TbAsmiCookiesPK(pMessage.getHeader().getAppId(),
                    pMessage.getHeader().getSelector());
            cAsmiCookiesRepo.deleteById(lAsmiCookiePK);
        }
    }

    public void clearOtherSessoin(Message pMessage) {
        LOG.debug("{} inside clearOtherSession.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        String deviceId = pMessage.getHeader().getDeviceId();
        if (pMessage.getHeader().getKeepUserSignedIn())
            deviceId = pMessage.getHeader().getSelector();
        if (pMessage.getSecurityParams() != null
                && pMessage.getSecurityParams().getMultiDviceLoginAlowd().equalsIgnoreCase(ServerConstants.NO)) {
            List<TbAstpLastLogin> lAstpLastLoginList = cAstpLastLoginRepo.findByUserIdAndAppIdAndNotByDeviceId(
                    pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId(), deviceId);
            if (lAstpLastLoginList != null && !lAstpLastLoginList.isEmpty()) {
                cAstpLastLoginRepo.deleteAll(lAstpLastLoginList);
            }
        }
        List<TbAstpLastLogin> lAstpLastLoginList = cAstpLastLoginRepo
                .findByUserIdAndAppIdAndNotByDeviceIdAndNullSessionId(pMessage.getHeader().getUserId(),
                        pMessage.getHeader().getAppId(), deviceId);
        if (lAstpLastLoginList != null && !lAstpLastLoginList.isEmpty()) {
            cAstpLastLoginRepo.deleteAll(lAstpLastLoginList);

        }
    }

    public void clearCookiesForUserId(Message pMessage) {
        LOG.debug("{} inside clearOtherCookies.", ServerConstants.LOGGER_PREFIX_DOMAIN);
        if (pMessage.getSecurityParams().getMultiDviceLoginAlowd().equalsIgnoreCase(ServerConstants.NO)
                && pMessage.getHeader().getKeepUserSignedIn()) {
            List<TbAsmiCookies> lAsmiCookiesList = cAsmiCookiesRepo.findByAppIdUserIdNotSelector(
                    pMessage.getHeader().getAppId(), pMessage.getHeader().getSelector(),
                    pMessage.getHeader().getUserId());
            if (lAsmiCookiesList != null && !lAsmiCookiesList.isEmpty()) {
                cAsmiCookiesRepo.deleteAll(lAsmiCookiesList);

            }
        }
    }

    public void lockUser(Message pMessage) {
        try {
            JSONObject lUserRequestRes = pMessage.getRequestObject().getRequestJson().getJSONObject("loginRequest");
            TbAsmiUserPK lTbAsmiUserPK = new TbAsmiUserPK(
                    lUserRequestRes.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                    lUserRequestRes.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            LOG.info("{} inside lockUser userId : {}, appId : {}, deviceId : {}", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    lUserRequestRes.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                    lUserRequestRes.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                    lUserRequestRes.getString("deviceId"));
            Optional<TbAsmiUser> lTbAsmiUser = cAsmiUserRepo.findById(lTbAsmiUserPK);
            Timestamp logintime = new Timestamp(new Date().getTime());
            if (lTbAsmiUser.isPresent()) {
                lTbAsmiUser.get().setUserLocked("Y");
                lTbAsmiUser.get().setFailCount(lTbAsmiUser.get().getFailCount() + 1);
                lTbAsmiUser.get().setUserLockTs(logintime);
                cAsmiUserRepo.save(lTbAsmiUser.get());
            }
        } catch (Exception ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.EXCEPTION, ex);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(ex.getMessage());
            lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }

    public void logoutUser(Message pMessage) throws DomainException {
        LOG.debug("{} inside logoutUser", ServerConstants.LOGGER_PREFIX_DOMAIN);
        try {
            JSONObject pUserRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("logoutRequest");
            TbAsmiUserPK lTbAsmiUserPK = new TbAsmiUserPK(
                    pUserRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                    pUserRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            if (cAsmiUserRepo.existsById(lTbAsmiUserPK)) {
                Optional<TbAsmiUser> lTbAsmiUser = cAsmiUserRepo.findById(lTbAsmiUserPK);
                if (lTbAsmiUser.isPresent()) {
                    lTbAsmiUser.get().setLoginStatus(ServerConstants.NO);
                    cAsmiUserRepo.save(lTbAsmiUser.get());
                }
            }
        } catch (JSONException ex) {
            LOG.error(DOUBLE_BRACES, ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, ex);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(ex.getMessage());
            lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }

    public void unlockFailureUpdateFailCount(Message pMessage) {
        try {
            LOG.debug("{} inside unlockFailureUpdateFailCount", ServerConstants.LOGGER_PREFIX_DOMAIN);
            JSONObject pUserRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
            TbAsmiUserPK lTbAsmiUserPK = new TbAsmiUserPK(
                    pUserRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                    pUserRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            Optional<TbAsmiUser> lTbAsmiUser = cAsmiUserRepo.findById(lTbAsmiUserPK);
            if (lTbAsmiUser.isPresent()) {
                lTbAsmiUser.get().setFailCount(1);
                lTbAsmiUser.get().setUserLocked(ServerConstants.NO);
                lTbAsmiUser.get().setUserLockTs(null);
                cAsmiUserRepo.save(lTbAsmiUser.get());
                LOG.info("{} updated fail count", ServerConstants.LOGGER_PREFIX_DOMAIN);
            }

        } catch (Exception ex) {
            LOG.error("{} {} ", ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.EXCEPTION, ex);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(ex.getMessage());
            lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }

    public void populateLastFailureLoginDetails(Message pMessage) {
        try {
            LOG.debug("{} inside populateLastFailureLoginDetails", ServerConstants.LOGGER_PREFIX_DOMAIN);
            JSONObject pUserRequest = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
            TbAsmiUserPK lTbAsmiUserPK = new TbAsmiUserPK(
                    pUserRequest.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                    pUserRequest.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
            Optional<TbAsmiUser> lTbAsmiUser = cAsmiUserRepo.findById(lTbAsmiUserPK);
            if (lTbAsmiUser.isPresent()) {
                lTbAsmiUser.get().setLoginStatus("N");
                lTbAsmiUser.get().setFailCount(lTbAsmiUser.get().getFailCount() + 1);
                cAsmiUserRepo.save(lTbAsmiUser.get());
                LOG.info("updated fail count");
            }

        } catch (Exception ex) {
            LOG.error(" {} ", ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.EXCEPTION, ex);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(ex.getMessage());
            lDomainException.setCode(DomainException.Code.APZ_DM_000.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }

    public void persistOtp(Message pMessage) {
        LOG.debug("{} inside persistOtp", ServerConstants.LOGGER_PREFIX_DOMAIN);
        TbAstpLastLoginPK lAstpLastLoginId = tbAstpLastLoginPK(pMessage);
        Optional<TbAstpLastLogin> lAstpLastLoginOpt = cAstpLastLoginRepo.findById(lAstpLastLoginId);
        TbAstpLastLogin lAstpLastLogin = null;
        if (lAstpLastLoginOpt.isPresent())
            lAstpLastLogin = lAstpLastLoginOpt.get();
        String otp = pMessage.getRequestObject().getRequestJson().getString("otp");
        String iface = pMessage.getHeader().getInterfaceId();
        validateSession(pMessage, lAstpLastLogin, iface);
        if (lAstpLastLogin != null) {
            lAstpLastLogin.setOtp(otp);
            if (!iface.equals(ServerConstants.INTERFACE_ID_REGENERATE_OTP)) {
                lAstpLastLogin.setOtpValidationCount(0);
            }
            lAstpLastLogin.setOtpGenTime(new Date());
            lAstpLastLogin.setOtpFlag(ServerConstants.NO);
        } else {
            lAstpLastLogin = new TbAstpLastLogin();
            lAstpLastLogin.setTbAstpLastLoginPK(lAstpLastLoginId);
            lAstpLastLogin.setLoginTime(new Date());
            lAstpLastLogin.setLastReqTime(new Date());
            lAstpLastLogin.setCreateTs(new Date());
            lAstpLastLogin.setVersionNo(1);
            lAstpLastLogin.setRequestKey(null);
            lAstpLastLogin.setSessionId(null);
            lAstpLastLogin.setOtp(otp);
            lAstpLastLogin.setOtpValidationCount(0);
            lAstpLastLogin.setOtpGenTime(new Date());
            lAstpLastLogin.setOtpFlag(ServerConstants.NO);
            LOG.debug("{} SETTING THIS SESSION LOGIN TIME : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, new Date());
            cAstpLastLoginRepo.save(lAstpLastLogin);
        }
    }

    private void validateSession(Message pMessage, TbAstpLastLogin lAstpLastLogin, String iface) {
        boolean status = true;
        if (iface.equals(ServerConstants.INTERFACE_ID_REGENERATE_OTP)) {
            if (lAstpLastLogin != null) {
                if (!lAstpLastLogin.getSessionId().equals(pMessage.getHeader().getSessionId())) {
                    status = false;
                }
                String otpflag = lAstpLastLogin.getOtpFlag();
                if (otpflag == null || !otpflag.equals(ServerConstants.NO)) {
                    status = false;
                }

            } else {
                status = false;
            }
            if (!status) {
                LOG.info("{} user not authenticated", ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                lDomainException
                        .setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_045));
                lDomainException.setCode(DomainException.Code.APZ_DM_045.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            }
            if (!isSessionTimeout(pMessage)) {
                LOG.debug("{} Session Expired", ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                lDomainException
                        .setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_046));
                lDomainException.setCode(DomainException.Code.APZ_DM_046.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            }
        }
    }

    @Transactional(noRollbackFor = {OtpStatusUpdationException.class})
    public void validateOTP(Message pMessage) {
        LOG.debug("{} inside validateOTP", ServerConstants.LOGGER_PREFIX_DOMAIN);

        TbAstpLastLoginPK lAstpLastLoginId = tbAstpLastLoginPK(pMessage);
        Optional<TbAstpLastLogin> lAstpLastLoginOpt = cAstpLastLoginRepo.findById(lAstpLastLoginId);
        if (lAstpLastLoginOpt.isPresent()) {
            TbAstpLastLogin lAstpLastLogin = lAstpLastLoginOpt.get();
            int validationCount = pMessage.getSecurityParams().getOtpValidationCount();
            if (ServerConstants.NO.equalsIgnoreCase(lAstpLastLogin.getOtpFlag())) {
                if (lAstpLastLogin.getOtpValidationCount() < validationCount) {

                    cAstpLastLoginRepo.updateValidationCount(pMessage.getHeader().getAppId(),
                            pMessage.getHeader().getUserId(), pMessage.getHeader().getDeviceId(),
                            lAstpLastLogin.getOtpValidationCount() + 1);

                    String encryptedOtp = lAstpLastLogin.getOtp();
                    String lplainOTP = AppzillonAESUtils.decryptString(pMessage.getSecurityParams().getServerToken(),
                            encryptedOtp);
                    String luserOTP = pMessage.getRequestObject().getRequestJson().getJSONObject("validateOtpRequest")
                            .getString("otp");

                    boolean status = true;
                    status = validateStatus(pMessage, lAstpLastLogin, status);
                    validateOTPStatus(pMessage, lAstpLastLogin, lplainOTP, luserOTP, status);
                } else {
                    LOG.error("{} Maximum number of attempts to validate otp has exceeded",
                            ServerConstants.LOGGER_PREFIX_DOMAIN);
                    DomainException lDomainException = DomainException.getDomainExceptionInstance();
                    lDomainException
                            .setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_083));
                    lDomainException.setCode(DomainException.Code.APZ_DM_083.toString());
                    lDomainException.setPriority("1");
                    throw lDomainException;
                }
            } else {
                LOG.error("{} OTP is already validated", ServerConstants.LOGGER_PREFIX_DOMAIN);
                OtpStatusUpdationException lOtpException = OtpStatusUpdationException
                        .getOtpStatusUpdationExceptionInstance();
                lOtpException.setMessage("OTP is already validated.");
                lOtpException.setCode(OtpStatusUpdationException.Code.APZ_DM_084.toString());
                lOtpException.setPriority("1");
                throw lOtpException;
            }
        }

    }

    private boolean validateStatus(Message pMessage, TbAstpLastLogin lAstpLastLogin, boolean status) {
        if (!lAstpLastLogin.getSessionId().equals(pMessage.getHeader().getSessionId())
                || !isSessionTimeout(pMessage)) {
            status = false;
        }
        if (!status) {
            JSONObject jsresp = new JSONObject();
            jsresp.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.FAILURE);
            JSONObject finalres = new JSONObject();
            finalres.put("validateOtp", jsresp);
            pMessage.getResponseObject().setResponseJson(finalres);
        }
        return status;
    }

    private void validateOTPStatus(Message pMessage, TbAstpLastLogin lAstpLastLogin, String lplainOTP, String luserOTP, boolean status) {
        if (luserOTP.equals(lplainOTP) && status) {
            long timebetweenRequests = (new Date().getTime()
                    - Long.valueOf(lAstpLastLogin.getOtpGenTime().getTime())) / 1000;
            LOG.info("{} the difference in request time is : {} secs", ServerConstants.LOGGER_PREFIX_DOMAIN,
                    timebetweenRequests);
            String otpExpiry = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(),
                    ServerConstants.OTP_EXPIRY_TS);
            if (Utils.isNullOrEmpty(otpExpiry)) {
                LOG.debug("otpExpiry value not found setting to default value of 300 sec");
                otpExpiry = "300";
            }

            if (timebetweenRequests > Integer.parseInt(otpExpiry)) {

                LOG.error("{} otp has expired", ServerConstants.LOGGER_PREFIX_DOMAIN);
                OtpStatusUpdationException lOtpException = OtpStatusUpdationException
                        .getOtpStatusUpdationExceptionInstance();
                lOtpException.setMessage("OTP has expired.");
                lOtpException.setCode(OtpStatusUpdationException.Code.APZ_DM_047.toString());
                lOtpException.setPriority("1");
                throw lOtpException;
            }

            JSONObject jsresp = new JSONObject();
            jsresp.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            JSONObject finalres = new JSONObject();
            finalres.put("validateOtp", jsresp);
            pMessage.getResponseObject().setResponseJson(finalres);
            lAstpLastLogin.setOtpFlag("Y");
            cAstpLastLoginRepo.save(lAstpLastLogin);

        } else {
            LOG.debug("{} Incorrect OTP.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            OtpStatusUpdationException lOtpException = OtpStatusUpdationException
                    .getOtpStatusUpdationExceptionInstance();
            lOtpException.setMessage("Invalid user credentials / User is Session Expired/Incorrect OTP");
            lOtpException.setCode(OtpStatusUpdationException.Code.APZ_DM_046.toString());
            lOtpException.setPriority("1");
            throw lOtpException;
        }
    }

    public boolean isSessionTimeout(Message pMessage) {
        TbAstpLastLoginPK lAstpLastLoginId = tbAstpLastLoginPK(pMessage);
        Optional<TbAstpLastLogin> lAstpLastLogin = cAstpLastLoginRepo.findById(lAstpLastLoginId);
        long timebetweenRequests = 0;
        long sessionTimeoutValue = 0;
        if (lAstpLastLogin.isPresent()) {
            timebetweenRequests = (new Date().getTime() - lAstpLastLogin.get().getLastReqTime().getTime()) / 1000;
            sessionTimeoutValue = pMessage.getSecurityParams().getSessionTimeout();
        }
        return timebetweenRequests <= sessionTimeoutValue;

    }

    public TbAstpLastLoginPK tbAstpLastLoginPK(Message pMessage) {
        TbAstpLastLoginPK lAstpLastLoginId = null;
        if (Utils.isNotNullOrEmpty(pMessage.getHeader().getUserId())) {
            if (pMessage.getHeader().getKeepUserSignedIn()) {
                lAstpLastLoginId = new TbAstpLastLoginPK(pMessage.getHeader().getUserId(),
                        pMessage.getHeader().getAppId(), pMessage.getHeader().getSelector());
            } else {
                lAstpLastLoginId = new TbAstpLastLoginPK(pMessage.getHeader().getUserId(),
                        pMessage.getHeader().getAppId(), pMessage.getHeader().getDeviceId());
            }
        }
        return lAstpLastLoginId;
    }

    /* authorizing user with accessToken and serverNonce */
    public void validateUserAppAccessToken(Message pMessage) {
        LOG.debug("{} inside validateUserToken()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        Optional<TbAsmiAppAccessToken> tbAsmiExUser;
        int expiryTs = pMessage.getSecurityParams().getAccessTokenExpiry();
        String token = pMessage.getHeader().getUserAppAccessToken();
        if (token != null && !token.isEmpty()) {
            String appId = pMessage.getHeader().getAppId();
            String userId = pMessage.getHeader().getUserId();
            String clientNonce = pMessage.getHeader().getClientNonce();

            tbAsmiExUser = cAsmiAppAccessTokenRepo.findById(new TbAsmiAppAccessTokenPK(appId, userId, token, "N"));
            if (tbAsmiExUser.isPresent() && tbAsmiExUser.get().getId().getAccessToken().equals(token)) {
                Timestamp tokenExpTs = new Timestamp(tbAsmiExUser.get().getCreateTs().getTime() + (1000 * expiryTs));
                Timestamp currentTs = new Timestamp(System.currentTimeMillis());
                if (currentTs.before(tokenExpTs)) {
                    LOG.debug("{} Access Token is valid and user is authorized", ServerConstants.LOGGER_PREFIX_DOMAIN);
                    persistAccessToken(tbAsmiExUser, token, appId, userId, clientNonce);
                } else {
                    LOG.error("{} Access Token is expired", ServerConstants.LOGGER_PREFIX_DOMAIN);
                    DomainException ds = DomainException.getDomainExceptionInstance();
                    ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_076));
                    ds.setCode(DomainException.Code.APZ_DM_076.toString());
                    throw ds;
                }
            } else {
                LOG.error("{} Invalid Access Token", ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException ds = DomainException.getDomainExceptionInstance();
                ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_075));
                ds.setCode(DomainException.Code.APZ_DM_075.toString());
                ds.setPriority("1");
                throw ds;
            }

        }
    }

    private void persistAccessToken(Optional<TbAsmiAppAccessToken> tbAsmiExUser, String token, String appId, String userId, String clientNonce) {
        if (Utils.isNotNullOrEmpty(clientNonce)) {
            boolean brecordExists = cAsmiAppAccessTokenRepo
                    .existsById(new TbAsmiAppAccessTokenPK(appId, userId, token, clientNonce));
            if (!brecordExists && tbAsmiExUser.isPresent()) {
                TbAsmiAppAccessToken lnewAppAccessToken = new TbAsmiAppAccessToken();
                lnewAppAccessToken.setId(new TbAsmiAppAccessTokenPK(appId, userId,
                        tbAsmiExUser.get().getId().getAccessToken(), clientNonce));
                lnewAppAccessToken.setCreateTs(new Timestamp(System.currentTimeMillis()));
                cAsmiAppAccessTokenRepo.save(lnewAppAccessToken);
            } else {
                LOG.error("{} Either request is invalid or processed already.",
                        ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException ds = DomainException.getDomainExceptionInstance();
                ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_071));
                ds.setCode(DomainException.Code.APZ_DM_071.toString());
                ds.setPriority("1");
                throw ds;
            }
        } else {
            LOG.error("{} Client nonce not provided", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException ds = DomainException.getDomainExceptionInstance();
            ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_082));
            ds.setCode(DomainException.Code.APZ_DM_082.toString());
            ds.setPriority("1");
            throw ds;
        }
    }

    /*
     * Validates that, the user name and password is right and generates a new
     * AccessToken
     */

    public void generateUserAppAccessToken(Message pMessage) {
        LOG.debug("{} Validating external user credentials", ServerConstants.LOGGER_PREFIX_DOMAIN);
        String httpAuthString = pMessage.getHeader().getUserAppAccessToken();
        String userAppAccessToken = "";
        String appId = pMessage.getHeader().getAppId();
        String userId = Utils.getUserIdFromAuthString(httpAuthString);
        String password = Utils.getPasswordFromAuthString(httpAuthString);
        Optional<TbAsmiUser> tbAsmiUser = cAsmiUserRepo.findById(new TbAsmiUserPK(userId, appId));
        if (tbAsmiUser.isPresent() && tbAsmiUser.get().getUserLvl() == Integer.parseInt(ServerConstants.EXTERNAL_USER_LVL)) {
            String dbPin = tbAsmiUser.get().getPin();
            String hashedPin = HashUtils.hashSHA256(password, userId + pMessage.getSecurityParams().getServerToken());
            if (dbPin.equals(hashedPin)) {
                List<TbAsmiAppAccessToken> historyAppAccessToken = cAsmiAppAccessTokenRepo
                        .findByAppIdUserIduserAppAuthentication(appId, userId);
                if (!historyAppAccessToken.isEmpty()) {
                    LOG.debug("{} Deleting old records", ServerConstants.LOGGER_PREFIX_DOMAIN);
                    cAsmiAppAccessTokenRepo.deleteAll(historyAppAccessToken);
                }
                LOG.debug("{} Password matches and generating new accesstoken", ServerConstants.LOGGER_PREFIX_DOMAIN);
                userAppAccessToken = Utils.generateRandomOfLength(32, ServerConstants.OTP_ALPHA_NUMERIC);
                Timestamp createTs = new Timestamp(System.currentTimeMillis());
                TbAsmiAppAccessToken lUser = new TbAsmiAppAccessToken();
                lUser.setId(new TbAsmiAppAccessTokenPK(appId, userId, userAppAccessToken, "N"));
                lUser.setCreateTs(createTs);
                cAsmiAppAccessTokenRepo.save(lUser);
                JSONObject accTkResponse = new JSONObject();
                accTkResponse.put(ServerConstants.USER_APP_ACCESS_TOKEN, userAppAccessToken);
                pMessage.getResponseObject().setResponseJson(new JSONObject()
                        .put(ServerConstants.APPZILLON_ROOT_GET_USER_APP_ACCESS_TOKEN_RES, accTkResponse));
            } else {
                LOG.error("{} UserName/Password is invalid. Please check.", ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                lDomainException.setMessage("UserName/Password is invalid. Please check.");
                lDomainException.setCode(DomainException.Code.APZ_DM_074.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            }
        } else {
            LOG.debug("{} UserName/Password is invalid. Please check.", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage("UserName/Password is invalid. Please check.");
            lDomainException.setCode(DomainException.Code.APZ_DM_074.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
        LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Response from authentication service is "
                + pMessage.getResponseObject().getResponseJson());
    }
}
