package com.iexceed.appzillon.domain.service;

import com.iexceed.appzillon.domain.entity.TbAstpLdRecs;
import com.iexceed.appzillon.domain.entity.TbAstpLdRecsPK;
import com.iexceed.appzillon.domain.entity.TbAstpOtpEngine;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.domain.repository.admin.TbAstpLdRecsRepository;
import com.iexceed.appzillon.domain.repository.meta.TbAstpOtpEngineRepository;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.maputils.MapUtils;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.SecurityParams;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.LargeData;
import com.iexceed.appzillon.utils.SequenceGenerator;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.Timestamp;
import java.util.*;

import static com.iexceed.appzillon.domain.utils.Constants.*;

@Named(ServerConstants.SERVICE_OTP_VAL)
@Transactional(ServerConstants.TRANSACTION_APPZILLON_APP_META)
public class OTPEngineService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getDomainLogger(ServerConstants.LOGGER_DOMAIN,
            OTPEngineService.class.toString());

    @Inject
    TbAstpOtpEngineRepository astpOtpvalRepo;

    @Inject
    private TbAstpLdRecsRepository tbAstpLdRecsRepo;

    // Changes added on 30/11/16
    public void persistOtp(Message pMessage) {
        LOG.debug("Persisting otp");
        Header header = pMessage.getHeader();
        String userId = header.getUserId();
        String appId = header.getAppId();
        JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson();
        String lHashedOTP = jsonRequest.getString("otp");
        // changes are added to fetch otpExpiry seconds from security parameters
        SecurityParams lsecurityParams = pMessage.getSecurityParams();
        int otpExpirySecs = lsecurityParams.getOtpExpiry();
        Timestamp genTimeStamp = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(genTimeStamp.getTime());
        cal.add(Calendar.SECOND, otpExpirySecs);
        Timestamp expTimeStamp = new Timestamp(cal.getTime().getTime());
        LOG.debug("{} OTP generation timestamp  : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, genTimeStamp);
        TbAstpOtpEngine astpOtpVal = null;
        SequenceGenerator sq = SequenceGenerator.getInstance();


        astpOtpVal = new TbAstpOtpEngine();
        astpOtpVal.setSerialNo(sq.nextId());
        astpOtpVal.setAppId(appId);
        astpOtpVal.setUserId(userId);
        astpOtpVal.setInterfaceId(header.getInterfaceId());
        astpOtpVal.setSessionId(header.getSessionId());
        astpOtpVal.setOtp(lHashedOTP);
        astpOtpVal.setOtpGenTime(genTimeStamp);
        astpOtpVal.setOtpExpTime(expTimeStamp);
        astpOtpVal.setStatus("N"); // N - New
        String requestPayload = jsonRequest.getString("payload");

        astpOtpVal.setPayloadStatus("NP"); // NP - Not Processed
        astpOtpVal.setPayloadProcessTime(genTimeStamp);
        // added for otpresend
        // changes added for otpresend by sasidhar
        astpOtpVal.setOtpResendLock("N");

        // Changes to populate the additional encrypted payload of Request and Response
        // to separate table with Master TXN reference and sequence number
        // modified on 08/03/18

        List<LargeData> reqLdRecList = Utils.getPayloadList(requestPayload, pMessage);
        persistLdRecs(reqLdRecList);

        astpOtpVal.setReqLdRefNo(reqLdRecList.get(0).getRefNo());
        astpOtpVal.setReqNoRecs(reqLdRecList.size());

        astpOtpvalRepo.save(astpOtpVal);

        pMessage.getResponseObject().setResponseJson(
                new JSONObject().put("generateOTPResponse", new JSONObject().put(REF_NO, astpOtpVal.getSerialNo())));
    }

    private void persistLdRecs(List<LargeData> ldRecList) {
        TbAstpLdRecs tbAstpLdRecs = null;
        TbAstpLdRecsPK bAstpLdRecsPK = null;
        List<TbAstpLdRecs> ldRecs = new ArrayList<>();
        for (LargeData recs : ldRecList) {
            bAstpLdRecsPK = new TbAstpLdRecsPK(recs.getRefNo(), recs.getSeqNo());
            tbAstpLdRecs = new TbAstpLdRecs(bAstpLdRecsPK, recs.getDataChunk1(), recs.getDataChunk2(),
                    recs.getDataChunk3(), recs.getDataChunk4(), recs.getDataChunk5());
            ldRecs.add(tbAstpLdRecs);
        }
        tbAstpLdRecsRepo.saveAll(ldRecs);
    }

    /**
     * @param pMessage
     */
    public void updateOtpVal(Message pMessage) {
        JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("updateOtpStatusRequest");
        String serialNo = jsonRequest.getString(REF_NO);
        String status = jsonRequest.getString("status");
        if (status == null || status.equals("") || status.length() == 0) {
            status = "P";
        }
        List<TbAstpOtpEngine> tbAstpOtpEngine = astpOtpvalRepo.findAstpOtpValByRefNo(Long.parseLong(serialNo));
        if (!tbAstpOtpEngine.isEmpty() && tbAstpOtpEngine.size() > 0) {
            if (tbAstpOtpEngine.get(0).getStatus().equalsIgnoreCase("P")) {
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_063);
                lDomainException.setMessage(emsg);
                lDomainException.setCode(DomainException.Code.APZ_DM_063.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            }

            Timestamp lValidateTimeStamp = new Timestamp(System.currentTimeMillis());
            tbAstpOtpEngine.get(0).setStatus(status);
            tbAstpOtpEngine.get(0).setOtpValTime(lValidateTimeStamp);
            astpOtpvalRepo.save(tbAstpOtpEngine.get(0));
            pMessage.getResponseObject().setResponseJson(new JSONObject().put("updateOtpStatusResponse",
                    new JSONObject().put(ServerConstants.STATUS, ServerConstants.RESP_BODY_STATUS_SUCCESS)));

        } else {
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_063);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_063.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }

        Timestamp lValidateTimeStamp = new Timestamp(System.currentTimeMillis());
        tbAstpOtpEngine.get(0).setOtpValTime(lValidateTimeStamp);
        astpOtpvalRepo.save(tbAstpOtpEngine.get(0));
        pMessage.getResponseObject().setResponseJson(new JSONObject().put("updateOtpStatusResponse",
                new JSONObject().put(ServerConstants.STATUS, ServerConstants.RESP_BODY_STATUS_SUCCESS)));
    }

    public void deleteOtp(Message pMessage) {
        JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("deleteOtpRequest");
        String serialNo = jsonRequest.getString(REF_NO);
        astpOtpvalRepo.delete(astpOtpvalRepo.findAstpOtpValByRefNo(Long.parseLong(serialNo)).get(0));
    }

    /**
     * @param pMessage
     */
    // changes made by sasidhar on 30/11/16.
    public void validateOtpVal(Message pMessage) {
        JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("validateOtpRequest");
        String serialNo = jsonRequest.getString(REF_NO);
        List<TbAstpOtpEngine> astpOtpVal = astpOtpvalRepo.findAstpOtpValByRefNo(Long.parseLong(serialNo));

        // changes are made from here
        String exceptionType = null;

        if (!astpOtpVal.isEmpty() && astpOtpVal.size() > 0) {
            LOG.debug("{} inside validateOTP() OTP details found", ServerConstants.LOGGER_PREFIX_DOMAIN);
            String otpStatus = astpOtpVal.get(0).getStatus();
            int otpValidationCount = astpOtpVal.get(0).getOtpValidationCount();

            SecurityParams lSecurityParams = pMessage.getSecurityParams();
            int lotpValidationCount = lSecurityParams.getOtpValidationCount();

            int count = otpValidationCount + 1;
            int status = astpOtpvalRepo.updateValidationCount(count, Long.parseLong(serialNo));
            LOG.debug("validation count is updated in database and status is : {}", status);

            if (((otpValidationCount < lotpValidationCount) && !(astpOtpVal.get(0).getOtpExpTime().before(new Date()))
                    && !(otpStatus.equals(ServerConstants.VALIDATION_EXPIRY))
                    && !(otpStatus.equals(ServerConstants.OTP_EXPIRED)) && !(otpStatus.equals("P")))
                    || (otpStatus.equals(ServerConstants.RESEND_EXPIRY))) {
                // If otp is not expired by validation count,time expiry and not processed yet
                // then setting NO exception.
                exceptionType = ServerConstants.NO;
            } else if (otpStatus.equals("P")) {// otp already processed and request came again to validate
                exceptionType = ServerConstants.OTP_PROCESSED;
            } else if (otpValidationCount >= lotpValidationCount) {// If validation count exceeds specified max value,
                // we are throwing exception
                astpOtpvalRepo.updateOtpExpiryStatus(ServerConstants.VALIDATION_EXPIRY, Long.parseLong(serialNo));
                exceptionType = ServerConstants.VALIDATION_EXPIRY;
            } else if (astpOtpVal.get(0).getOtpExpTime().before(new Date())) {// If OTP is expired due to timeout , we
                // are throwing exception.
                astpOtpvalRepo.updateOtpExpiryStatus(ServerConstants.OTP_EXPIRED, Long.parseLong(serialNo));
                exceptionType = ServerConstants.OTP_EXPIRED;
            }
            astpOtpVal.get(0).setStatus(exceptionType);
            LOG.debug("otp status we are setting to sent to service class to throw any exception if OTP expired : {}",
                    astpOtpVal.get(0).getStatus());
            JSONObject lOTPDetails = new JSONObject();
            lOTPDetails.put(OTP_DETAILS, (MapUtils.convertObjectToMap(astpOtpVal.get(0))));
            pMessage.getResponseObject().setResponseJson(lOTPDetails);

        } else {// otp doesn't exist with given RefNo.
            LOG.debug("{} OTP Details Does not Exist", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_048);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_048.toString());
            lDomainException.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + OTP_DETAILS_NOT_EXIST, lDomainException);
            throw lDomainException;
        }
    }

    // changes made by sasi on 18/11/2016 for otp resend.
    // changes made on 28/11/16
    public void fetchOTPDetails(Message pMessage) {
        LOG.debug("inside fetchOTPDetails");
        Header header = pMessage.getHeader();
        String userId = header.getUserId();
        String appId = header.getAppId();
        JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("fetchOtpDetailsRequest");
        String serialNo = "";
        TbAstpOtpEngine astpOtpVal;
        List<TbAstpOtpEngine> tbAstpOtpEngineList;
        StringBuilder requestPayload = null;
        if (jsonRequest.has(REF_NO) && jsonRequest.getString(REF_NO) != null
                && !jsonRequest.getString(REF_NO).equals("")) {
            serialNo = jsonRequest.getString(REF_NO);
            tbAstpOtpEngineList = astpOtpvalRepo.findAstpOtpValByRefNo(Long.parseLong(serialNo));

        } else {
            tbAstpOtpEngineList = astpOtpvalRepo.findAstpOtpValByAppIdUserIdInterfaceIdSessionIdPayLoadStatus(appId,
                    userId, header.getInterfaceId(), header.getSessionId(), "NP");
        }

        if (CollectionUtils.isEmpty(tbAstpOtpEngineList) && tbAstpOtpEngineList.size() <= 0) {
            LOG.debug("{} OTP Details does not exist", ServerConstants.LOGGER_PREFIX_DOMAIN);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_048);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_048.toString());
            lDomainException.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + OTP_DETAILS_NOT_EXIST, lDomainException);
            throw lDomainException;
        }

        astpOtpVal = tbAstpOtpEngineList.get(0);
        requestPayload = getOtpPayload(astpOtpVal);
        JSONObject lOTPDetails = new JSONObject();
        Map<String, String> otpRequest = MapUtils.convertObjectToMap(astpOtpVal);
        otpRequest.put("RequestPayload", requestPayload.toString());
        lOTPDetails.put(OTP_DETAILS, (otpRequest));
        pMessage.getResponseObject().setResponseJson(lOTPDetails);

    }

    private StringBuilder getOtpPayload(TbAstpOtpEngine astpOtpVal) {
        StringBuilder requestPayload = new StringBuilder();

        if (astpOtpVal.getReqNoRecs() > 0) {
            List<Object[]> ldRecList = tbAstpLdRecsRepo.findLdRecsByRefNo(astpOtpVal.getReqLdRefNo());
            for (Object[] ldRecs : ldRecList) {
                for (Object payload : ldRecs) {
                    requestPayload.append(payload != null ? payload : "");
                }
            }
        }
        return requestPayload;
    }

    /*
     * Changes made by Sasidhar on 28/11/2016 Allowing user to resent OTP OTP will
     * be locked on lockout and will be unlocked on lock timeout
     */
    public void resendOTP(Message pMessage) {
        LOG.debug("Inside resendOTP");
        fetchOTPDetails(pMessage);// first we are fetching to read values
        JSONObject lotpDetails = pMessage.getResponseObject().getResponseJson();
        LOG.trace("otp fetched details are {}", lotpDetails);
        JSONObject lDetails = lotpDetails.getJSONObject(OTP_DETAILS);
        int otpResentCount = Integer.parseInt(lDetails.getString(OTP_RESENT_COUNT));
        String otpResendLock = lDetails.getString("OtpResendLock");
        Timestamp otpExpiryTime = Timestamp.valueOf(lDetails.getString("OtpExpTime"));
        LOG.trace("otp otpResent count {} ", otpResentCount);
        String refNo = lDetails.getString("SerialNo");
        String otpStatus = lDetails.getString("Status");
        SecurityParams lSecurityParams = pMessage.getSecurityParams();
        int otpUnlockTimeOutMs = lSecurityParams.getOtpResendLockTimeOut();
        int lOTPResendCount = lSecurityParams.getOtpResendCount();
        boolean otpLocked = false;
        boolean otpExpired = false;
        boolean otpProcessed = false;
        if (otpResendLock.equalsIgnoreCase(ServerConstants.NO)) {// if otp resend feature is not locked
            // changes are added here
            if (otpResentCount < lOTPResendCount && !otpExpiryTime.before(new Date())
                    && !otpStatus.equalsIgnoreCase(ServerConstants.VALIDATION_EXPIRY)
                    && !otpStatus.equalsIgnoreCase(ServerConstants.OTP_EXPIRED)
                    && otpStatus.equalsIgnoreCase("N")) {// if otp not expired and exceeded
                otpResentCount = otpResentCount + 1;
                astpOtpvalRepo.updateOtpResendCount(otpResentCount, Long.parseLong(refNo));
                lDetails.put(OTP_RESENT_COUNT, otpResentCount);
                lotpDetails.put(OTP_DETAILS, lDetails);
                pMessage.getResponseObject().setResponseJson(lotpDetails);
            } else {// if above condition is failed.
                if (otpStatus.equalsIgnoreCase("P")) {// if processed, resend shouldn't work.
                    otpProcessed = true;
                    LOG.trace("otpProcessed : {}", otpProcessed);
                    lDetails.put("otpProcessedStatus", String.valueOf(otpProcessed));
                    JSONObject otpdetails = new JSONObject();
                    otpdetails.put(OTP_DETAILS, lDetails);
                    pMessage.getResponseObject().setResponseJson(otpdetails);
                } else {
                    handleLockOrExpiredOTP(pMessage, lDetails, otpResentCount, otpExpiryTime, refNo, otpStatus, lOTPResendCount);
                }
            }
        } else if (otpResendLock.equalsIgnoreCase(ServerConstants.YES)) {// if locked to use resend feature
            List<TbAstpOtpEngine> astpOtpVal = astpOtpvalRepo.findAstpOtpValByRefNo(Long.parseLong(refNo));
            LOG.trace("Otp resend lock time is : {}", astpOtpVal.get(0).getOtpResendLockTime());
            Timestamp lockTime = (Timestamp) astpOtpVal.get(0).getOtpResendLockTime();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(lockTime.getTime());
            cal.add(Calendar.SECOND, otpUnlockTimeOutMs);
            Timestamp unlockTime = new Timestamp(cal.getTime().getTime());
            LOG.trace("UNLOCK TIME IS : {} ", unlockTime);
            checkOTPLockIsTimedOut(pMessage, lotpDetails, lDetails, refNo, unlockTime);
        }
    }

    private void checkOTPLockIsTimedOut(Message pMessage, JSONObject lotpDetails, JSONObject lDetails, String refNo, Timestamp unlockTime) {
        boolean otpLocked;
        if (unlockTime.before(new Date())) {// Checking if OTP lock is timedout
            LOG.trace("WE ARE UNLOCKING THE USER TO ACCESS RESEND");
            // Resetting resentLockCount to 1 and LockTS to null since resendotp lock is
            // timedout and otp status and otpresentLock to "N".
            astpOtpvalRepo.OtpUnlocking(ServerConstants.NO, 1, null, ServerConstants.NO, Long.parseLong(refNo));
            lDetails.put(OTP_RESENT_COUNT, 1);
            lotpDetails.put(OTP_DETAILS, lDetails);
            pMessage.getResponseObject().setResponseJson(lotpDetails);
        } else {
            otpLocked = true;
            LOG.trace("otpLocked : {}", otpLocked);
            lDetails.put("otpLockedStatus", String.valueOf(otpLocked));
            JSONObject otpdetails = new JSONObject();
            otpdetails.put(OTP_DETAILS, lDetails);
            pMessage.getResponseObject().setResponseJson(otpdetails);
        }
    }

    private void handleLockOrExpiredOTP(Message pMessage, JSONObject lDetails, int otpResentCount, Timestamp otpExpiryTime, String refNo, String otpStatus, int lOTPResendCount) {
        boolean otpLocked;
        boolean otpExpired;
        if (otpResentCount >= lOTPResendCount) {
            LOG.trace("otp Resend Count exceeded max no of times : {}", otpResentCount);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            // modified to update OTP-STATUS as RE(resend-expiry)
            astpOtpvalRepo.updateOtpResendLock(ServerConstants.YES, timestamp,
                    ServerConstants.RESEND_EXPIRY, Long.parseLong(refNo));
            otpLocked = true;
            LOG.trace("otpLocked : {}", otpLocked);
            lDetails.put("otpLockedStatus", String.valueOf(otpLocked));
            lDetails.put(OTP_RESENT_COUNT, otpResentCount + 1);
            JSONObject otpdetails = new JSONObject();
            otpdetails.put(OTP_DETAILS, lDetails);
            pMessage.getResponseObject().setResponseJson(otpdetails);
            // after otp expiry or otp validation count is exceeded max then we are setting
            // otp status as expired.So otp cannot be sent
        } else if (otpExpiryTime.before(new Date())
                || otpStatus.equalsIgnoreCase(ServerConstants.VALIDATION_EXPIRY)
                || otpStatus.equalsIgnoreCase(ServerConstants.OTP_EXPIRED)) {
            astpOtpvalRepo.updateOtpExpiryStatus(ServerConstants.OTP_EXPIRED, Long.parseLong(refNo));
            otpExpired = true;
            LOG.trace("otpExpired : {}", otpExpired);
            lDetails.put("otpExpiredStatus", String.valueOf(otpExpired));
            JSONObject otpdetails = new JSONObject();
            otpdetails.put(OTP_DETAILS, lDetails);
            pMessage.getResponseObject().setResponseJson(otpdetails);
        }
    }

    public void updatePayLoadStatus(Message pMessage) {
        JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject("updatePayLoadStatusRequest");
        String serialNo = jsonRequest.getString(REF_NO);
        List<TbAstpOtpEngine> astpOtpVal = null;
        astpOtpVal = astpOtpvalRepo.findAstpOtpValByRefNo(Long.parseLong(serialNo));
        if (!astpOtpVal.isEmpty() && astpOtpVal.size() > 0) {
            Timestamp genTimeStamp = new Timestamp(System.currentTimeMillis());
            astpOtpVal.get(0).setPayloadProcessTime(genTimeStamp);
            astpOtpVal.get(0).setPayloadStatus("P");
            pMessage.getResponseObject().setResponseJson(new JSONObject().put("updatePayLoadStatusResponse",
                    new JSONObject().put(ServerConstants.STATUS, ServerConstants.RESP_BODY_STATUS_SUCCESS)));

        } else {
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_048);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_048.toString());
            lDomainException.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + OTP_DETAILS_NOT_EXIST, lDomainException);
            throw lDomainException;
        }

    }

    public void invalidateUnsedUserOTP(Message pMessage) {
        LOG.debug("{} Invalidating all User's unused OTPs", ServerConstants.LOGGER_PREFIX_DOMAIN);
        astpOtpvalRepo.invalidateUserOTP("I", pMessage.getHeader().getUserId(), pMessage.getHeader().getAppId(),
                pMessage.getHeader().getSessionId(), "N");
    }

    // updating reGenCount and otp
    public void regenerateOTP(Message pMessage) {
        LOG.debug("{} regenerating OTP", ServerConstants.LOGGER_PREFIX_DOMAIN);
        SecurityParams lSecurityParams = pMessage.getSecurityParams();
        // need to check the count to validate
        List<TbAstpOtpEngine> tbAstpOtpEngine = astpOtpvalRepo.findAstpOtpValByRefNo(Long.parseLong(pMessage.getRequestObject().getRequestJson()
                .getJSONObject(ServerConstants.RE_GENERATE_OTP_REQ).getString(REF_NO)));
        if ((!tbAstpOtpEngine.isEmpty() && tbAstpOtpEngine.size() > 0) && (tbAstpOtpEngine.get(0).getOtpRegenCount() < lSecurityParams.getOtpRegenCount())
                && !(tbAstpOtpEngine.get(0).getStatus().equalsIgnoreCase(ServerConstants.OTP_EXPIRED)
                || tbAstpOtpEngine.get(0).getStatus().equalsIgnoreCase(ServerConstants.OTP_PROCESSED)
                || tbAstpOtpEngine.get(0).getStatus().equalsIgnoreCase(ServerConstants.VALIDATION_EXPIRY))) {
            String lOTPGenerated = Utils.getOTP(lSecurityParams);
            String lOTP = null;
            // If resend feature is required then we are encrypting OTP value and persisting
            // below
            if (lSecurityParams.getOtpResend().equalsIgnoreCase(ServerConstants.YES)) {
                lOTP = AppzillonAESUtils.encryptString(
                        pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId(), lOTPGenerated);
            } else if (lSecurityParams.getOtpResend().equalsIgnoreCase(ServerConstants.NO)) {// If not, Hashing the OTP
                // value and persisting
                // below
                lOTP = HashUtils.hashSHA256(lOTPGenerated,
                        pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId());
            }
            // changes are added to set expiry time for OTP
            SecurityParams lsecurityParams = pMessage.getSecurityParams();
            int otpExpirySecs = lsecurityParams.getOtpExpiry();
            Timestamp genTimeStamp = new Timestamp(System.currentTimeMillis());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(genTimeStamp.getTime());
            cal.add(Calendar.SECOND, otpExpirySecs);
            Timestamp expTimeStamp = new Timestamp(cal.getTime().getTime());
            tbAstpOtpEngine.get(0).setOtpGenTime(genTimeStamp);
            tbAstpOtpEngine.get(0).setOtpExpTime(expTimeStamp);
            tbAstpOtpEngine.get(0).setPayloadProcessTime(genTimeStamp);
            tbAstpOtpEngine.get(0).setOtp(lOTP);
            tbAstpOtpEngine.get(0).setStatus("N");
            tbAstpOtpEngine.get(0).setOtpValidationCount(0);
            tbAstpOtpEngine.get(0).setOtpRegenCount(tbAstpOtpEngine.get(0).getOtpRegenCount() + 1);
            astpOtpvalRepo.save(tbAstpOtpEngine.get(0));
            JSONObject lRespJSON = new JSONObject();
            lRespJSON.put(REF_NO, tbAstpOtpEngine.get(0).getSerialNo());
            lRespJSON.put("otp", lOTPGenerated);
            pMessage.getResponseObject().setResponseJson(new JSONObject().put("regenerateOTPResponse", lRespJSON));
        } else {
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_080));
            lDomainException.setCode(DomainException.Code.APZ_DM_080.toString());
            lDomainException.setPriority("1");
            LOG.error(ServerConstants.LOGGER_PREFIX_DOMAIN + "OTP Regeneration count exceeded/ already validated",
                    lDomainException);
            throw lDomainException;
        }
    }

}
