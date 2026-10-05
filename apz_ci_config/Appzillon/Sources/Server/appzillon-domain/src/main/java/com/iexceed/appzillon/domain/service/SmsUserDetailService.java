/**
 *
 */
package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAsmiAppMaster;
import com.iexceed.appzillon.domain.entity.TbAsmiSmsUser;
import com.iexceed.appzillon.domain.entity.TbAsmiSmsUserPK;
import com.iexceed.appzillon.domain.entity.TbAsmiUser;
import com.iexceed.appzillon.domain.entity.history.TbAshsSmsUser;
import com.iexceed.appzillon.domain.entity.history.TbAshsSmsUserPK;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAshsSmsUserRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiAppMasterRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiSmsUserRepository;
import com.iexceed.appzillon.domain.repository.admin.TbAsmiUserRepository;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;
import java.util.Optional;

import static com.iexceed.appzillon.domain.utils.Constants.*;

/**
 * @author Ripu
 *
 */
@Named("smsUserDetailService")
@Transactional(ServerConstants.TRANSACTION_APPZILLON_ADMIN)
public class SmsUserDetailService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            SmsUserDetailService.class.getName());

    @Inject
    private TbAsmiSmsUserRepository smsUserRepo;
    @Inject
    private TbAsmiUserRepository userRepo;
    @Inject
    private TbAsmiAppMasterRepository appMasterRepo;
    @Inject
    private TbAshsSmsUserRepository smsUserHistoryRepo;

    public void getUserDetailsBasedOnMobileNumber(Message pMessage) {
        JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
        LOG.debug("{} Inside getUserDetailsBasedOnMobileNumber()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        try {
            JSONObject request = requestJson.getJSONObject("appzillonSmsUserRequest");
            String lAppId = request.getString(ServerConstants.MESSAGE_HEADER_APP_ID);
            String lMobileNumber = request.getString("mobileNum");
            String lflag = request.getString(ServerConstants.FLAG);
            TbAsmiSmsUser lSmsUser = smsUserRepo.findUserIdByAppIdAndMobileNum(lAppId, lMobileNumber);
            // LOG.debug(ServerConstants.LOGGER_PREFIX_DOMAIN +"SMS USER : "+ lSmsUser);
            if (lSmsUser != null && lflag.equals(ServerConstants.SMS)
                    && lSmsUser.getSmsReq().equals(ServerConstants.YES)) {
                LOG.debug("{} building object for SMS", ServerConstants.LOGGER_PREFIX_DOMAIN);
                TbAsmiUser userDetails = getUserDetails(lAppId, lSmsUser.getId().getUserId());
                TbAsmiAppMaster appMaster = getAppMasterDetail(lAppId);
                JSONObject response = new JSONObject();
                response.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
                response.put(ServerConstants.MESSAGE_HEADER_USER_ID, userDetails.getTbAsmiUserPK().getUserId());
                response.put(ServerConstants.USER_LANAGUAGE, userDetails.getLanguage());
                response.put("defaultLanguage", appMaster.getDefaultLanguage());

                pMessage.getResponseObject()
                        .setResponseJson(new JSONObject().put("appzillonSmsUserResponse", response));
            } else if (lSmsUser != null && lflag.equals(ServerConstants.USSD)
                    && lSmsUser.getUssdReq().equals(ServerConstants.YES)) {
                LOG.debug("{} building object for USSD", ServerConstants.LOGGER_PREFIX_DOMAIN);
                TbAsmiUser userDetails = getUserDetails(lAppId, lSmsUser.getId().getUserId());
                TbAsmiAppMaster appMaster = getAppMasterDetail(lAppId);
                JSONObject response = new JSONObject();
                response.put(ServerConstants.MESSAGE_HEADER_APP_ID, lAppId);
                response.put(ServerConstants.MESSAGE_HEADER_USER_ID, userDetails.getTbAsmiUserPK().getUserId());
                response.put(ServerConstants.USER_LANAGUAGE, userDetails.getLanguage());
                response.put("defaultLanguage", appMaster.getDefaultLanguage());
                pMessage.getResponseObject()
                        .setResponseJson(new JSONObject().put("appzillonSmsUserResponse", response));
            } else {
                LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN
                        + "Mobile Number is not registered, Please contact Administrator");
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
                dexp.setCode(DomainException.Code.APZ_DM_008.toString());
                dexp.setPriority("1");
                throw dexp;
            }
        } catch (JSONException jsonExp) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsonExp);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsonExp.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    public void createSmsUssdUser(JSONObject requestJson, String pHeaderUserId, String pAction) {
        LOG.debug("{} inside createSmsUssdUser()", ServerConstants.LOGGER_PREFIX_DOMAIN);
        try {
            if (requestJson.has(SMS_REQUIRED_PHONE1)
                    && ServerConstants.YES.equalsIgnoreCase(requestJson.getString(SMS_REQUIRED_PHONE1))
                    && (!requestJson.has(SMS_REQUIRED_PHONE2)
                    || ServerConstants.NO.equalsIgnoreCase(requestJson.getString(SMS_REQUIRED_PHONE2)))) {
                createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                        requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                        requestJson.getString(ServerConstants.PHNO1), requestJson.getString(SMS_REQUIRED_PHONE1),
                        requestJson.getString(USSD_REQUIRED), pHeaderUserId, pAction);
            } else if ((!requestJson.has(SMS_REQUIRED_PHONE1)
                    || ServerConstants.NO.equalsIgnoreCase(requestJson.getString(SMS_REQUIRED_PHONE1)))
                    && requestJson.has(SMS_REQUIRED_PHONE2)
                    && ServerConstants.YES.equalsIgnoreCase(requestJson.getString(SMS_REQUIRED_PHONE2))) {
                createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                        requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                        requestJson.getString(ServerConstants.PHNO2), requestJson.getString(SMS_REQUIRED_PHONE2),
                        ServerConstants.NO, pHeaderUserId, pAction);
            } else if (requestJson.has(SMS_REQUIRED_PHONE1)
                    && ServerConstants.YES.equalsIgnoreCase(requestJson.getString(SMS_REQUIRED_PHONE1))
                    && requestJson.has(SMS_REQUIRED_PHONE2)
                    && ServerConstants.YES.equalsIgnoreCase(requestJson.getString(SMS_REQUIRED_PHONE2))) {
                createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                        requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                        requestJson.getString(ServerConstants.PHNO1), requestJson.getString(SMS_REQUIRED_PHONE1),
                        requestJson.getString(USSD_REQUIRED), pHeaderUserId, pAction);

                createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                        requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                        requestJson.getString(ServerConstants.PHNO2), requestJson.getString(SMS_REQUIRED_PHONE2),
                        ServerConstants.NO, pHeaderUserId, pAction);
            } else if ((!requestJson.has(SMS_REQUIRED_PHONE1)
                    || ServerConstants.NO.equalsIgnoreCase(requestJson.getString(SMS_REQUIRED_PHONE1)))
                    && (!requestJson.has(SMS_REQUIRED_PHONE2)
                    || ServerConstants.NO.equalsIgnoreCase(requestJson.getString(SMS_REQUIRED_PHONE2)))) {
                if (requestJson.has(USSD_REQUIRED)
                        && ServerConstants.YES.equalsIgnoreCase(requestJson.getString(USSD_REQUIRED))
                        && !requestJson.getString(ServerConstants.PHNO1).isEmpty()) {
                    createSmsUser(requestJson.getString(ServerConstants.MESSAGE_HEADER_APP_ID),
                            requestJson.getString(ServerConstants.MESSAGE_HEADER_USER_ID),
                            requestJson.getString(ServerConstants.PHNO1), requestJson.getString(SMS_REQUIRED_PHONE1),
                            requestJson.getString(USSD_REQUIRED), pHeaderUserId, pAction);
                }
            }
        } catch (JSONException jsone) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsone);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsone.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
        /** Appzillon - 3.1 -create USSD User End */
    }

    /**
     * Below Method will fetch the details from Admin DB, TB_ASMI_USER table is part
     * of ADMIN. So Putting here transaction type -
     * ServerConstants.TRANSACTION_APPZILLON_ADMIN
     *
     * @param pAppId
     * @param pUserId
     * @return
     */
    private TbAsmiUser getUserDetails(String pAppId, String pUserId) {
        LOG.debug("{} Inside getUserDetails() - AppID : {}, UserID : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, pAppId,
                pUserId);
        TbAsmiUser asmiUser = userRepo.findUsersByAppIdUserId(pUserId, pAppId);
        if (asmiUser != null) {
            return asmiUser;
        } else {
            LOG.warn("{} Record does not exists in TbAsmiUser", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    private TbAsmiAppMaster getAppMasterDetail(String pAppId) {
        LOG.debug("{} Inside getAppMasterDetail() - AppID : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, pAppId);
        TbAsmiAppMaster appMaster = appMasterRepo.findAppMasterByAppId(pAppId);
        if (appMaster != null) {
            return appMaster;
        } else {
            LOG.warn("{} Record does not exists in TbAsmiAppMaster", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_008));
            dexp.setCode(DomainException.Code.APZ_DM_008.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    /**
     * Below method created by Ripu Appzillon - 3.1 - Send SMS to User
     *
     * @param pMobileNum
     * @param pUserId
     */
    private void createSmsUser(String pAppId, String pUserId, String pMobileNum, String pSmsRequired,
                               String pUssdRequired, String pHeaderUserId, String pAction) {
        LOG.debug(
                "{} inside createSmsUser() - AppId : {}, UserId : {}, Mobile No : {}, SMS Required : {}, UssdRequired : {}, Header UserId : {}, Action : {}",
                ServerConstants.LOGGER_PREFIX_DOMAIN, pAppId, pUserId, pMobileNum, pSmsRequired, pUssdRequired,
                pHeaderUserId, pAction);
        try {
            /*
             * Code Commented and changes done for bug id - 14886 TbAsmiSmsUserPK lSmsUserPk
             * = new TbAsmiSmsUserPK(pAppId, pMobileNum);
             */
            TbAsmiSmsUserPK lSmsUserPk = new TbAsmiSmsUserPK(pAppId, pUserId);
            if (!ServerConstants.APPZILLON_ROOT_UPDATE.equals(pAction)) {
                if (smsUserRepo.existsById(lSmsUserPk)) {
                    LOG.warn("{} Record exists in TbAsmiSmsUser", ServerConstants.LOGGER_PREFIX_DOMAIN);
                    DomainException dexp = DomainException.getDomainExceptionInstance();
                    dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_015));
                    dexp.setCode(DomainException.Code.APZ_DM_015.toString());
                    dexp.setPriority("1");
                    throw dexp;
                } else {
                    TbAsmiSmsUser lsmsUser = new TbAsmiSmsUser(lSmsUserPk);
                    lsmsUser.setMobileNumber(pMobileNum);
                    lsmsUser.setSmsReq(pSmsRequired);
                    lsmsUser.setUssdReq(pUssdRequired);
                    lsmsUser.setCreatedBy(pHeaderUserId);
                    lsmsUser.setCreateTs(new Date());
                    // fetch max version no from history table
                    Integer version = smsUserHistoryRepo.findMaxVersionNoByAppIdAndUserId(pAppId, pUserId);
                    LOG.debug("{} Fetching Max Version Number from TbAshsSmsUser : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, version);
                    if (version != null) {
                        lsmsUser.setVersionNo(version + 1);
                    } else {
                        lsmsUser.setVersionNo(1);
                    }

                    smsUserRepo.save(lsmsUser);
                }
            } else if (ServerConstants.APPZILLON_ROOT_UPDATE.equals(pAction)) {
                TbAsmiSmsUser lsmsUser;
                Optional<TbAsmiSmsUser> lsmsUserOpt = smsUserRepo.findById(lSmsUserPk);
                if (lsmsUserOpt.isPresent()) {
                    // insert data into history table starts
                    lsmsUser = lsmsUserOpt.get();
                    TbAshsSmsUser ashsSmsUser = new TbAshsSmsUser(
                            new TbAshsSmsUserPK(lSmsUserPk.getAppId(), lSmsUserPk.getUserId()));
                    ashsSmsUser.setSmsReq(lsmsUser.getSmsReq());
                    ashsSmsUser.setCreatedBy(lsmsUser.getCreatedBy());
                    ashsSmsUser.setCreateTs(lsmsUser.getCreateTs());
                    ashsSmsUser.setMobileNumber(lsmsUser.getMobileNumber());
                    ashsSmsUser.setUssdReq(lsmsUser.getUssdReq());
                    ashsSmsUser.getId().setVersionNo(lsmsUser.getVersionNo());
                    smsUserHistoryRepo.save(ashsSmsUser);

                    // insert data into history table ends

                    lsmsUser.setMobileNumber(pMobileNum);
                    lsmsUser.setSmsReq(pSmsRequired);
                    lsmsUser.setUssdReq(pUssdRequired);
                    lsmsUser.setCreatedBy(pHeaderUserId);
                    lsmsUser.setCreateTs(new Date());
                    lsmsUser.setVersionNo(lsmsUser.getVersionNo() + 1);
                    smsUserRepo.save(lsmsUser);
                } else {
                    lsmsUser = new TbAsmiSmsUser(lSmsUserPk);
                    // lsmsUser.setUserId(pUserId);
                    lsmsUser.setMobileNumber(pMobileNum);
                    lsmsUser.setSmsReq(pSmsRequired);
                    lsmsUser.setUssdReq(pUssdRequired);
                    lsmsUser.setCreatedBy(pHeaderUserId);
                    lsmsUser.setCreateTs(new Date());
                    // fetch max version no from history table
                    Integer version = smsUserHistoryRepo.findMaxVersionNoByAppIdAndUserId(pAppId, pUserId);
                    if (version != null)
                        lsmsUser.setVersionNo(version + 1);
                    else
                        lsmsUser.setVersionNo(1);
                    smsUserRepo.save(lsmsUser);
                }
            }
        } catch (JSONException jsonExp) {
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + ServerConstants.JSON_EXCEPTION, jsonExp);
            DomainException dexp = DomainException.getDomainExceptionInstance();
            dexp.setMessage(jsonExp.getMessage());
            dexp.setCode(DomainException.Code.APZ_DM_000.toString());
            dexp.setPriority("1");
            throw dexp;
        }
    }

    public void deleteSmsUserDetail(String pAppID, String pUserId) {
        LOG.debug("Inside Delete Sms User Detail, AppID : {}, UserId : {}", pAppID, pUserId);
        // TbAsmiSmsUserPK lSmsUserPk = new TbAsmiSmsUserPK(pAppID, pMobile);
        TbAsmiSmsUser tb = smsUserRepo.findMobileNumberByAppIdAndUserId(pAppID, pUserId);
        if (tb != null) {
            TbAshsSmsUser ashsSmsUser = new TbAshsSmsUser(
                    new TbAshsSmsUserPK(tb.getId().getAppId(), tb.getId().getUserId()));
            ashsSmsUser.setSmsReq(tb.getSmsReq());
            ashsSmsUser.setCreatedBy(tb.getCreatedBy());
            ashsSmsUser.setCreateTs(tb.getCreateTs());
            ashsSmsUser.setMobileNumber(tb.getMobileNumber());
            ashsSmsUser.setUssdReq(tb.getUssdReq());
            ashsSmsUser.getId().setVersionNo(tb.getVersionNo());
            smsUserHistoryRepo.save(ashsSmsUser);

            smsUserRepo.delete(tb);
        }
    }
}
