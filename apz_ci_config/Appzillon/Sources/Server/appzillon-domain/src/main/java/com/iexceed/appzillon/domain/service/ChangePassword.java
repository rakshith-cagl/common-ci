package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAsmiSecurityParams;
import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.TbAstpLastLogin;
import com.iexceed.appzillon.domain.entity.TbAstpLastLoginPK;
import com.iexceed.appzillon.domain.entity.history.TbAshsUserPasswords;
import com.iexceed.appzillon.domain.entity.history.TbAshsUserPasswordsPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.*;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;

import static com.iexceed.appzillon.domain.utils.Constants.PROPERTIES;

/**
 * @author Vinod Rawat
 */
@Named("ChangePasswordService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class ChangePassword {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            ChangePassword.class.getName());
    @Inject
    private TbAsmiUserRepository userDetRepo;
    @Inject
    private TbAsmiSecurityParamsRepository securityParameterRepo;
    @Inject
    private TbAshsUserPasswordsRepository userpassrepo;
    @Inject
    private TbAsmiAppMasterRepository tbAsmiAppMasterRepository;
    @Inject
    private TbAstpLastLoginRepository astpLastLoginRepo;

    // changes made by sasidhar on 31/01/2017
    public void updatePassword(Message pMessage) {
        JSONObject changepwdmsg = new JSONObject();
        try {
            LOG.debug("{} inside updatePassword()", ServerConstants.LOGGER_PREFIX_DOMAIN);
            /**
             * Changes done by Ripu, Date : 23-01-2015. After validating user and password
             * from AuthenticationImpl, Reuest was coming as 'loginReques' so the above code
             * is commented, and below line added for loginRequest.
             */
            JSONObject passobj = pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST);
            String validatedPassword = getPasswordByApplyingPasswordRules(passobj, pMessage);
            JSONObject res = new JSONObject();
            String status = "";
            String msg = "";
            if (ServerConstants.VALID_PIN.equalsIgnoreCase(validatedPassword)) {
                TbAsmiUser result = userDetRepo.findUsersByAppIdUserId(
                        passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                        passobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
                String lNewPassword = getHashedPassword(passobj, ServerConstants.NO, pMessage);

                // Check for the user password history table
                List<String> pinlist = this.readpinByuserIdappId(
                        passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                        passobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
                int flag = getFlag(lNewPassword, pinlist);
                if (result.getPin().equals(lNewPassword))
                    flag = 1;
                if (flag == 1) {
                    DomainException dexp = DomainException.getDomainExceptionInstance();
                    dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_034));
                    dexp.setCode(DomainException.Code.APZ_DM_034.toString());
                    dexp.setPriority("1");
                    throw dexp;
                } else {
                    this.moveOldPwdToHistory(passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                            passobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID), result.getPin(),
                            result.getPinChangeTs(), pMessage);
                    this.deleteOlderRows(passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                            passobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
                    result.setPin(lNewPassword);
                    result.setPinChangeTs(new Date());
                    userDetRepo.save(result);
                    status = ServerConstants.SUCCESS;
                    msg = "password changed successfully";
                }
                // added by sasidhar
                // changes added here to send email after password change
                String lLanguage = "";
                lLanguage = getLanguage(passobj);

                Properties lPropfile = new Properties();
                String appId = pMessage.getHeader().getAppId();
                String appDesc = tbAsmiAppMasterRepository.findAppMasterByAppId(appId).getAppDescription();

                String pwdCommChannel = pMessage.getSecurityParams().getPwdChangeCommChannel();
                String lFileName = Utils.getFileNameForMailSMSTemplate(appId,
                        ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CHANGE_PIN + ServerConstants.PEMAIL + "_"
                                + lLanguage + PROPERTIES);
                try (InputStream isr = PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName)) {
                    lPropfile.load(isr);
                }

                String lEmailBody = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);

                JSONObject fillerJson = new JSONObject();
                fillerJson.put(ServerConstants.MAIL_FILLER_APP_ID, appId);
                fillerJson.put(ServerConstants.MAIL_FILLER_APP_DESC, appDesc);
                fillerJson.put(ServerConstants.MAIL_FILLER_USERID, pMessage.getHeader().getUserId());
                lEmailBody = Utils.getConstructedBody(lEmailBody, fillerJson);
                String lEmailSub = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);
                // load sms body from properties file
                lFileName = Utils.getFileNameForMailSMSTemplate(appId,
                        ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CHANGE_PIN + ServerConstants.PMOBILE + "_"
                                + lLanguage + PROPERTIES);
                try (InputStream isr = PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName)) {
                    lPropfile.load(isr);
                }

                String messageBody = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                messageBody = Utils.getConstructedBody(messageBody, fillerJson);

                // load notification body from properties file
                lFileName = Utils.getFileNameForMailSMSTemplate(appId,
                        ServerConstants.MAIL_CONSTANTS_FILE_NAME_PREFIX_CHANGE_PIN + ServerConstants.NOTIF + "_"
                                + lLanguage + PROPERTIES);
                try (InputStream isr = PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName)) {
                    lPropfile.load(isr);
                }
                String notificationMessage = lPropfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
                notificationMessage = Utils.getConstructedBody(notificationMessage, fillerJson);

                res.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
                res.put(ServerConstants.MESSAGE, msg);
                res.put(ServerConstants.PINCHANGECOMCHANNEL, pwdCommChannel);
                res.put(ServerConstants.SMS_CONSTANTS_BODY, messageBody);
                res.put(ServerConstants.NOTIFICATION_CONSTANTS_BODY, notificationMessage);
                res.put(ServerConstants.MAIL_CONSTANTS_BODY, lEmailBody);
                res.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, lEmailSub);
                changepwdmsg.put(ServerConstants.CHANGEPINRESPONSE, res);
            } else {
                status = ServerConstants.FAILURE;
                res.put(ServerConstants.MESSAGE_HEADER_STATUS, status);
                res.put(ServerConstants.MESSAGE, validatedPassword);
                pMessage.getHeader().setStatus(false);
                changepwdmsg.put(ServerConstants.CHANGEPINRESPONSE, res);
            }
        } catch (JSONException jsone) {
            LOG.error("{} {}", ServerConstants.LOGGER_PREFIX_DOMAIN, ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        } catch (IOException e) {
            LOG.error("{} IOException", ServerConstants.LOGGER_PREFIX_DOMAIN, e);
        }

        pMessage.getResponseObject().setResponseJson(changepwdmsg);
    }

    private String getLanguage(JSONObject passobj) {
        String lLanguage;
        if (passobj.has(ServerConstants.USER_LANAGUAGE)) {
            lLanguage = passobj.getString(ServerConstants.USER_LANAGUAGE);
            if ((lLanguage != null) && !"".equals(lLanguage)) {
                lLanguage = lLanguage.toLowerCase();
            } else {
                lLanguage = ServerConstants.APPZILLON_ROOT_LNGEN;
            }
        } else {
            lLanguage = ServerConstants.APPZILLON_ROOT_LNGEN;
        }
        return lLanguage;
    }

    private int getFlag(String lNewPassword, List<String> pinlist) {
        int flag = 0;
        for (int p = 0; p < pinlist.size(); p++) {
            if (pinlist.get(p).equals(lNewPassword)) {
                flag = 1;
            }
        }
        return flag;
    }

    private String getHashedPassword(JSONObject pInputJson, String pType, Message pMessage) throws JSONException {
        LOG.debug("{} inside getHashedPassword()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        String hashedPin = "";
        Optional<TbAsmiSecurityParams> obj = securityParameterRepo
                .findById(pInputJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        if (obj.isPresent() && ServerConstants.NO.equalsIgnoreCase(pType)) {
            // decrypting new password
            String key = obj.get().getServerToken();
            String encryotedCred = pInputJson.getString(ServerConstants.NEW_CRED);
            String decriptedNewCred = AppzillonAESUtils.decryptContainerString(encryotedCred, key, pMessage);
            hashedPin = HashUtils.hashSHA256(decriptedNewCred,
                    pInputJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID) + obj.get().getServerToken());
        } else if (obj.isPresent()) {
            hashedPin = HashUtils.hashSHA256(pInputJson.getString(ServerConstants.OLD_CRED),
                    pInputJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID) + obj.get().getServerToken());
        }
        return hashedPin;
    }

    private String getPasswordByApplyingPasswordRules(JSONObject pInputJson, Message pMessage) throws JSONException {
        LOG.debug("{} inside getPasswordByApplyingPasswordRules()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        Optional<TbAsmiSecurityParams> asminSecurityParameter = securityParameterRepo
                .findById(pInputJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        String validatedPasswordRes = "";
        boolean checkPwdStatus = false;
        if (asminSecurityParameter.isPresent()) {
            validatedPasswordRes = " Your new password should contain" + " 1.With minimum "
                    + asminSecurityParameter.get().getMinLength() + " and maxinum "
                    + asminSecurityParameter.get().getMaxLength() + " characters." + "2.With atleast "
                    + asminSecurityParameter.get().getMinNumUpperCaseChar() + " uppercase characters."
                    + "3.With atleast " + asminSecurityParameter.get().getMinNumLowerCaseChar() + " lower characters"
                    + "4.With atleast " + asminSecurityParameter.get().getMinNumSpclChar() + " special characters."
                    + "5.With " + asminSecurityParameter.get().getMinNumNum() + " numbers."
                    + "6.Without these restricted special characters "
                    + asminSecurityParameter.get().getRestrictedSplChars();
            // decrypting new password
            String key = asminSecurityParameter.get().getServerToken();
            // when encryption is not enabled and os is WEB then use aes-gcm mode for pwd
            // decryption.
            String encryptionFlag = PropertyUtils.getPropValue(ServerConstants.SERVER_PROP_FILE_CONSTANT,
                    ServerConstants.ENCRYPTION_FLAG);
            if (!ServerConstants.YES.equalsIgnoreCase(encryptionFlag)
                    && ServerConstants.WEB.equalsIgnoreCase(pMessage.getHeader().getOs())) {
                pMessage.getHeader().setEncMode(1);
            }

            //By chinmaya bug id 56968
            if (ServerConstants.ANDROID.equalsIgnoreCase(pMessage.getHeader().getOs())) {
                pMessage.getHeader().setEncMode(1);
            }

            checkPwdStatus = isPwdStatus(pInputJson, pMessage, asminSecurityParameter, validatedPasswordRes, checkPwdStatus, key);
        } else {
            LOG.debug("{} No record is there in security parameter table corresponding appid",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_030));
            dexp.setCode(DomainException.Code.APZ_DM_030.toString());
            dexp.setPriority("1");
            throw dexp;
        }

        if (checkPwdStatus) {
            validatedPasswordRes = ServerConstants.VALID_PIN;
            return validatedPasswordRes;
        } else {
            LOG.debug("{} Please check and enter your password again", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(validatedPasswordRes);
            dexp.setCode(DomainException.Code.APZ_DM_033.toString());
            dexp.setPriority("1");
            throw dexp;
        }

    }

    private boolean isPwdStatus(JSONObject pInputJson, Message pMessage, Optional<TbAsmiSecurityParams> asminSecurityParameter, String validatedPasswordRes, boolean checkPwdStatus, String key) {
        char[] lNewCred = AppzillonAESUtils
                .decryptContainerString(pInputJson.getString(ServerConstants.NEW_CRED), key, pMessage)
                .toCharArray();
        int i;
        int c2 = 0;
        int c3 = 0;
        int c4 = 0;
        int c5 = 0;
        char ch;
        for (i = 0; i < lNewCred.length; i++) {
            ch = lNewCred[i];
            if (Character.isUpperCase(ch)) {
                ++c4;
            } else if (Character.isLowerCase(ch)) {
                ++c5;
            } else if (Character.isDigit(ch)) {
                ++c2;
            } else if (!Character.isLetter(ch)) {
                ++c3;
            }
        }

        if (asminSecurityParameter.isPresent()) {
            if (lNewCred.length >= asminSecurityParameter.get().getMinLength()
                    && lNewCred.length <= asminSecurityParameter.get().getMaxLength()) {
                Integer[] inputPram = {c2, c3, c4, c5};

                checkPwdStatus = isCheckPwdStatus(asminSecurityParameter, validatedPasswordRes, checkPwdStatus, lNewCred, inputPram);
            } else {
                LOG.error("{} Please enter your password between {} and {} characters",
                        ServerConstants.LOGGER_PREFIX_DOMAIN, asminSecurityParameter.get().getMinLength(),
                        asminSecurityParameter.get().getMaxLength());
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(validatedPasswordRes);
                dexp.setCode(DomainException.Code.APZ_DM_033.toString());
                dexp.setPriority("1");
                throw dexp;
            }
        } else {
            LOG.error("{} No records found.",
                    ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException ds = DomainException.getDomainExceptionInstance();
            ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            ds.setCode(DomainException.Code.APZ_DM_008.toString());
            ds.setPriority("1");
            throw ds;
        }
        return checkPwdStatus;
    }

    private boolean isCheckPwdStatus(Optional<TbAsmiSecurityParams> asminSecurityParameter, String validatedPasswordRes, boolean checkPwdStatus, char[] lNewCred, Integer[] inputPram) {
        int c2 = inputPram[0];
        int c3 = inputPram[1];
        int c4 = inputPram[2];
        int c5 = inputPram[3];

        if (asminSecurityParameter.isPresent() && (c2 >= asminSecurityParameter.get().getMinNumNum()) && (c3 >= asminSecurityParameter.get().getMinNumSpclChar())
                && (c4 >= asminSecurityParameter.get().getMinNumUpperCaseChar())
                && c5 >= asminSecurityParameter.get().getMinNumLowerCaseChar()) {
            if (Utils.isNotNullOrEmpty(asminSecurityParameter.get().getRestrictedSplChars())) {
                char[] checkForRestChars = asminSecurityParameter.get().getRestrictedSplChars().toCharArray();
                for (char checkForRestChar : checkForRestChars) {
                    if (!new String(lNewCred).contains("" + checkForRestChar)) {
                        checkPwdStatus = true;
                    } else {
                        checkPwdStatus = false;
                        break;
                    }
                }
            } else {
                checkPwdStatus = true;

            }
        } else {
            LOG.debug("{} Please check and enter your password again", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(validatedPasswordRes);
            dexp.setCode(DomainException.Code.APZ_DM_033.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        return checkPwdStatus;
    }

    private List<String> readpinByuserIdappId(String userid, String appid) {
        LOG.debug("{} inside readpinByuserIdappId()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        int lastNPass = -1;
        Optional<TbAsmiSecurityParams> asmiSercurityParameter = securityParameterRepo.findById(appid);
        if (asmiSercurityParameter.isPresent()) {
            lastNPass = asmiSercurityParameter.get().getLastNPassNotToUse();
        }

        List<TbAshsUserPasswords> reslist = userpassrepo.findrowsByUserIdAppIdorderbytime(userid, appid);
        List<String> pinlist = new ArrayList<>();
        TbAshsUserPasswords ashsUserPassword = null;

        if (!reslist.isEmpty()) {
            for (int i = 0; i < reslist.size(); i++) {
                if (i < lastNPass) {
                    ashsUserPassword = reslist.get(reslist.size() - (i + 1));
                    pinlist.add(ashsUserPassword.getId().getPin());
                } else {
                    break;
                }
            }
        } else {
            LOG.debug("{} no prevoius pins for user", ServerConstants.LOGGER_PREFIX_DOMAIN);
        }
        return pinlist;

    }

    private void moveOldPwdToHistory(String userId, String appId, String pin, Date changeTs, Message pMessage) {
        LOG.debug("{} inside enterRowsContainingPwd().", ServerConstants.LOGGER_PREFIX_DOMAIN);
        TbAshsUserPasswordsPK pkobj = new TbAshsUserPasswordsPK(userId, appId, pin);
        TbAshsUserPasswords rec = new TbAshsUserPasswords();
        rec.setId(pkobj);
        rec.setChangeTime(changeTs);
        rec.setCreateUserId(pMessage.getHeader().getUserId());
        rec.setCreateTs(new Date());
        rec.setVersionNo(1);

        userpassrepo.save(rec);
    }

    private void deleteOlderRows(String pUserId, String pAppId) {
        LOG.debug("{} inside deleteOlderRows().", ServerConstants.LOGGER_PREFIX_DOMAIN);
        List<TbAshsUserPasswords> reslisttime = userpassrepo.findrowsByUserIdAppIdorderbytime(pUserId, pAppId);
        int pwcountforapp = -1;
        Optional<TbAsmiSecurityParams> asmiSecurityParameter = securityParameterRepo.findById(pAppId);
        if (asmiSecurityParameter.isPresent()) {
            pwcountforapp = asmiSecurityParameter.get().getPasswordCount();
        }
        TbAshsUserPasswords rec = null;
        int flag = reslisttime.size();
        if (reslisttime.size() > pwcountforapp) {
            for (int i = 0; i < reslisttime.size(); i++) {
                rec = reslisttime.get(i);
                if (flag == pwcountforapp) {
                    break;
                }
                flag--;
                userpassrepo.delete(rec);
            }
        }
    }

    public void updateFailedPasswordCount(Message pMessage) {
        LOG.debug("{} Inside updateFailedPasswordCount() ", ServerConstants.LOGGER_PREFIX_DOMAIN);

        JSONObject passobj = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.CHANGEPINREQUEST);

        TbAsmiUser result = userDetRepo.findUsersByAppIdUserId(
                passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                passobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID));

        // Locking user if he exceeds failed count
        int maxNoofFailCount = pMessage.getSecurityParams().getNooffailedcounts();
        int count = result.getFailCount() + 1;

        if (count > maxNoofFailCount) {
            // Fail count exceeded max limit. Locking the user now.
            Timestamp lockedtime = new Timestamp(new Date().getTime());
            result.setUserLocked("Y");
            result.setUserLockTs(lockedtime);
            result.setFailCount(count);
            userDetRepo.save(result);
            LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Going to terminate the session..");
            Optional<TbAstpLastLogin> tb = astpLastLoginRepo
                    .findById(new TbAstpLastLoginPK(passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                            passobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                            passobj.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID)));
            if (tb.isPresent()) {
                tb.get().setSessionId(null);
                tb.get().setRequestKey(null);
                astpLastLoginRepo.save(tb.get());
            } else {
                LOG.error("{} No records found.",
                        ServerConstants.LOGGER_PREFIX_DOMAIN);
                DomainException ds = DomainException.getDomainExceptionInstance();
                ds.setMessage(ds.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                ds.setCode(DomainException.Code.APZ_DM_008.toString());
                ds.setPriority("1");
                throw ds;
            }
        } else {
            result.setFailCount(count);
            userDetRepo.save(result);
        }

    }

    public void checkUserLocked(Message pMessage) {
        LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN + "Inside checkUserLocked() ");

        JSONObject passobj = pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.CHANGEPINREQUEST);

        TbAsmiUser result = userDetRepo.findUsersByAppIdUserId(
                passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                passobj.getString(ServerConstants.MESSAGE_HEADER_APP_ID));

        if (result.getUserLocked().equals(ServerConstants.YES)) {
            LOG.debug(ServerConstants.LOGGER_PREFIX_SMS + "User Locked");
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_013));
            lDomainException.setCode(DomainException.Code.APZ_DM_013.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }

    }
}
