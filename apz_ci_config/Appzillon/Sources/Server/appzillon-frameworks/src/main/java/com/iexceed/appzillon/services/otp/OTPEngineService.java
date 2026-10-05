package com.iexceed.appzillon.services.otp;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.message.SecurityParams;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.InvalidPayloadException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import static com.iexceed.appzillon.utils.Constants.*;

public class OTPEngineService {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    OTPEngineService.class.getName());

    //resend otp added by sasidhar
    public void resendOtp(Message pMessage) {
        SecurityParams lSecurityParams = pMessage.getSecurityParams();
        String resendOtpReq = lSecurityParams.getOtpResend();
        int resendOtpCount = lSecurityParams.getOtpResendCount();

        if (resendOtpReq.equalsIgnoreCase(ServerConstants.YES)) {//checking if resend feature is needed from security params.
            JSONObject lFetchOTPDetailsJson = new JSONObject();
            JSONObject lRequestJSON = pMessage.getRequestObject().getRequestJson().getJSONObject("resendOTPRequest");
            lFetchOTPDetailsJson.put("fetchOtpDetailsRequest", lRequestJSON);
            pMessage.getRequestObject().setRequestJson(lFetchOTPDetailsJson);
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_OTP_RESEND);
            DomainStartup.getInstance().processRequest(pMessage);
            JSONObject linfRequestJson = pMessage.getResponseObject().getResponseJson();
            JSONObject lIntfOtpDetails = linfRequestJson.getJSONObject(OTP_DETAILS);
            if (!lIntfOtpDetails.has(OTP_LOCKED_STATUS) && !lIntfOtpDetails.has(OTP_EXPIRED_STATUS) && !lIntfOtpDetails.has(OTP_PROCESSED_STATUS)) {//can resend otp
                if (lIntfOtpDetails.has("Otp")) {//OTP is fetched successfully and setting response with OTP and Refno and attempts left.
                    String lhashedotp = pMessage.getResponseObject().getResponseJson().getJSONObject(OTP_DETAILS).getString("Otp");
                    String otp = AppzillonAESUtils.decryptString(pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId(), lhashedotp);
                    LOG.debug("{} Decryted OTP Details -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, otp);
                    lFetchOTPDetailsJson.put("Otp", otp);
                    JSONObject lResponse = new JSONObject();
                    lResponse.put(REF_NO, lIntfOtpDetails.get(SERIAL_NO));
                    lResponse.put("Otp", otp);
                    lResponse.put("AttemptsLeft", resendOtpCount - Integer.parseInt(lIntfOtpDetails.getString("OtpResentCount")));
                    pMessage.getResponseObject().setResponseJson(lResponse);
                    LOG.debug("response in resendOtp {}", lFetchOTPDetailsJson);
                } else {//user is temporarily locked and error message is generated
                    LOG.debug("user is locked currently");
                }
            } else if (lIntfOtpDetails.has(OTP_LOCKED_STATUS) && lIntfOtpDetails.get(OTP_LOCKED_STATUS).equals("true")) {//user has attempted max allows for otp resend
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_059);
                lDomainException.setMessage(emsg);
                lDomainException.setCode(DomainException.Code.APZ_DM_059.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            } else if (lIntfOtpDetails.has(OTP_EXPIRED_STATUS) && lIntfOtpDetails.get(OTP_EXPIRED_STATUS).equals("true")) {//otp has expired due to timeout
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_047);
                lDomainException.setMessage(emsg);
                lDomainException.setCode(DomainException.Code.APZ_DM_047.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            } else if (lIntfOtpDetails.has(OTP_PROCESSED_STATUS) && lIntfOtpDetails.get(OTP_PROCESSED_STATUS).equals("true")) {//otp is already processed
                DomainException lDomainException = DomainException.getDomainExceptionInstance();
                String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_063);
                lDomainException.setMessage(emsg);
                lDomainException.setCode(DomainException.Code.APZ_DM_063.toString());
                lDomainException.setPriority("1");
                throw lDomainException;
            }
        } else if (resendOtpReq.equalsIgnoreCase(ServerConstants.NO)) {//if resend feature is not enabled.
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_062);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_062.toString());
            lDomainException.setPriority("1");
            LOG.error("{} OTP resend is not enabled {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lDomainException);
            throw lDomainException;
        }
    }

    /**
     * modified by sasidhar to add resend feature
     */
    public void generateOtp(Message pMessage) {
        SecurityParams lSecurityParams = pMessage.getSecurityParams();
        JSONObject jsonRequest = new JSONObject();
        jsonRequest = pMessage.getRequestObject().getRequestJson().getJSONObject("generateOTPRequest");
        LOG.debug("{} Request Payload : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, jsonRequest);
        //String otpExpirySecs = jsonRequest.getString("otpExpirySecs");
        String lOTPGenerated = Utils.getOTP(lSecurityParams);
        LOG.debug("{} OTP generated is : {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lOTPGenerated);
        String lOTP = null;
        //changes are added here
        //If resend feature is required then we are encrypting OTP value and persisting below
        if (lSecurityParams.getOtpResend().equalsIgnoreCase(ServerConstants.YES)) {
            lOTP = AppzillonAESUtils.encryptString(pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId(), lOTPGenerated);
        } else if (lSecurityParams.getOtpResend().equalsIgnoreCase(ServerConstants.NO)) {//If not, Hashing the OTP value and persisting below
            lOTP = HashUtils.hashSHA256(lOTPGenerated, pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId());
        }
        String lplainPayLoad = jsonRequest.getJSONObject("payload").toString();
        String lHashedPayLoad = AppzillonAESUtils.encryptString(pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId(), lplainPayLoad);
        jsonRequest.put("otp", lOTP);
        jsonRequest.put("payload", lHashedPayLoad);
        jsonRequest.put("otpExpirySecs", lSecurityParams.getOtpExpiry());

        LOG.debug("{} JSON request before setting -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        pMessage.getRequestObject().setRequestJson(jsonRequest);
        LOG.debug("{} JSON request After setting -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTP_VAL);
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject lRespJSON = pMessage.getResponseObject().getResponseJson();
        JSONObject lPersistResp = lRespJSON.getJSONObject("generateOTPResponse");
        jsonRequest = new JSONObject();
        jsonRequest.put(REF_NO, lPersistResp.get(REF_NO));
        jsonRequest.put("otp", lOTPGenerated);
        JSONObject lotpResponse = new JSONObject();
        lotpResponse.put("generateOTPResponse", jsonRequest);
        pMessage.getResponseObject().setResponseJson(lotpResponse);
    }

    public void reGenerateOTP(Message pMessage) {
        LOG.debug("{} Request Payload for regenerating otp: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson().getJSONObject(ServerConstants.RE_GENERATE_OTP_REQ));
        if (pMessage.getSecurityParams().getOtpRegenCount() > 0) {
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_TYPE_REGENARATE_OTP);
            DomainStartup.getInstance().processRequest(pMessage);
        } else {
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            lDomainException.setMessage(lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_081));
            lDomainException.setCode(DomainException.Code.APZ_DM_081.toString());
            lDomainException.setPriority("1");
            LOG.error("{} OTP Regeneration is not Enabled : {}", ServerConstants.LOGGER_PREFIX_DOMAIN, lDomainException);
            throw lDomainException;
        }

    }

    /**
     * changes added by sasidhar for otpresend.
     * changes are added on 1/12/16
     */
    public void validateOtp(Message pMessage) {
        JSONObject jsonRequest = pMessage.getRequestObject().getRequestJson()
                .getJSONObject("validateOtpRequest");
        String otp = jsonRequest.getString("otp");
        LOG.debug("{} OTP received in request to validate is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, otp);
        SecurityParams lSecurityParams = pMessage.getSecurityParams();
        String lOTPResendRequired = lSecurityParams.getOtpResend();
        int lValidationCount = lSecurityParams.getOtpValidationCount();

        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_VALIDATE_OTP);
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject lOTPResp = pMessage.getResponseObject().getResponseJson();
        JSONObject lOTPDetails = lOTPResp.getJSONObject(OTP_DETAILS);
        LOG.debug("{} OTPResp response from domain is: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lOTPDetails);
        String exceptionType = lOTPDetails.getString("Status");
        //changes are added on 1/12/16
        //throwing exception based on status set in doamin.
        if (exceptionType.equals(ServerConstants.OTP_EXPIRY)) {//otp expiry due to time out
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_047);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_047.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        } else if (exceptionType.equals(ServerConstants.VALIDATION_EXPIRY)) {//validation count has exceeded max value
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_060);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_060.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        } else if (exceptionType.equals(ServerConstants.RESEND_EXPIRY)) {//
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_061);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_061.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        } else if (exceptionType.equals(ServerConstants.OTP_PROCESSED)) {//if otp processed and again requested to validate
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_063);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_063.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        } else if (exceptionType.equals(ServerConstants.NO)) {//if no exceptions occur setting response value
            LOG.debug("no exception occurred ");
            String lotpString = lOTPDetails.getString("Otp");
            String lOTP = "";
            //changes added to do hashing or encrypt the OTP depending upon otp resend is not required or not.
            if (lOTPResendRequired.equalsIgnoreCase(ServerConstants.NO)) {
                lOTP = HashUtils.hashSHA256(otp, pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId());
                LOG.debug("{} Hashed OTP to validate -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lOTP);
                compareOTP(lOTP, lotpString, pMessage, lValidationCount, lOTPDetails);
            } else if (lOTPResendRequired.equalsIgnoreCase(ServerConstants.YES)) {
                lOTP = AppzillonAESUtils.decryptString(pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId(), lotpString);
                LOG.debug("{} Decrypted OTP to validate -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lOTP);
                compareOTP(lOTP, otp, pMessage, lValidationCount, lOTPDetails);
            }
        }
    }

    private void compareOTP(String lOTP, String lotpString, Message pMessage, int lValidationCount, JSONObject lOTPDetails) {
        JSONObject response = new JSONObject();
        response.put("Attempts Left", lValidationCount - Integer.parseInt(lOTPDetails.getString("OtpValidationCount")) - 1);
        JSONObject otpValServiceResponse = new JSONObject();
        if (lOTP.equals(lotpString)) {
            response.put(ServerConstants.STATUS, ServerConstants.YES);
            otpValServiceResponse.put("OTPValServiceResponse", response);
            pMessage.getResponseObject().setResponseJson(otpValServiceResponse);
        } else {
            response.put(ServerConstants.STATUS, ServerConstants.NO);
            otpValServiceResponse.put("OTPValServiceResponse", response);
            pMessage.getResponseObject().setResponseJson(otpValServiceResponse);
        }
    }

    /**
     * @param pMessage
     */
    public void updateOtp(Message pMessage) {
        try {
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTP_UPDATE);
            DomainStartup.getInstance().processRequest(pMessage);
        } catch (ObjectOptimisticLockingFailureException e) {
            LOG.error(LOGGER_EXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_063);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_063.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }


    }

    /**
     * @param pMessage
     */
    public void deleteOtp(Message pMessage) {
        try {
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_OTP_DELETE);
            DomainStartup.getInstance().processRequest(pMessage);
        } catch (ObjectOptimisticLockingFailureException e) {
            LOG.error(LOGGER_EXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_063);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_063.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }

    /**
     * @param pMessage
     * @throws JSONException
     * @throws ClassNotFoundException
     * @throws InvalidPayloadException
     * @throws ExternalServicesRouterException
     */
    public void processIface(Message pMessage) throws ExternalServicesRouterException, InvalidPayloadException, ClassNotFoundException, JSONException {
        LOG.debug("{} Processing Iface.... RequestJSON -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        JSONObject lRequestJSON = pMessage.getRequestObject().getRequestJson().getJSONObject("ProcessIFaceRequest");
        JSONObject lFetchOTPDetailsJson = new JSONObject();
        lFetchOTPDetailsJson.put("fetchOtpDetailsRequest", lRequestJSON);
        pMessage.getRequestObject().setRequestJson(lFetchOTPDetailsJson);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_FETCH_OTP);
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject linfRequestJson = pMessage.getResponseObject().getResponseJson();
        JSONObject lIntfOtpDetails = linfRequestJson.getJSONObject(OTP_DETAILS);
        String lIntfCipherPayLoad = lIntfOtpDetails.getString("RequestPayload");
        String lIntfPlainPayLoad = AppzillonAESUtils.decryptString(pMessage.getHeader().getUserId() + pMessage.getHeader().getSessionId(), lIntfCipherPayLoad);
        LOG.debug("{} OTP Interface Plain Payload -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lIntfPlainPayLoad);
        String lAppId = pMessage.getHeader().getAppId();
        String lUserId = pMessage.getHeader().getUserId();
        String lInterfaceId = pMessage.getHeader().getInterfaceId();
        String lIFaceInterfaceId = lIntfOtpDetails.getString("InterfaceId");
        Header lHeader = pMessage.getHeader();
        lHeader.setInterfaceId(lIFaceInterfaceId);
        String lIFaceAppId = lIntfOtpDetails.getString("AppId");
        lHeader.setAppId(lIFaceAppId);
        LOG.debug("{} ProcessIFace - Header -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lHeader);
        pMessage.setHeader(lHeader);
        pMessage.getRequestObject().setRequestJson(new JSONObject(lIntfPlainPayLoad));
        LOG.debug("{} Header and RequestJSON after setting processIFace InterfaceId & AppId -: {}, {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getHeader(), pMessage.getRequestObject().getRequestJson());
        String lResponseFromService = "";

        FrameworksStartup.getInstance().processRequest(pMessage);
        lResponseFromService = pMessage.getResponseObject().getResponseJson().toString();
        LOG.debug("{} Response from External Service is lResponseFromService -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lResponseFromService);
        LOG.debug("{} Setting back appid, interfaceid and user id from request header....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        pMessage.getHeader().setAppId(lAppId);
        pMessage.getHeader().setUserId(lUserId);
        pMessage.getHeader().setInterfaceId(lInterfaceId);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_UPDATE_PAYLOAD_STATUS);
        JSONObject lRequestJson = new JSONObject();
        lRequestJson.put("updatePayLoadStatusRequest", lRequestJSON);
        pMessage.getRequestObject().setRequestJson(lRequestJson);
        DomainStartup.getInstance().processRequest(pMessage);
        LOG.debug("{} Setting back service response....", ServerConstants.LOGGER_FRAMEWORKS);
        JSONObject response = new JSONObject();
        response.put(STATUS, lIntfOtpDetails.get(OTP_STATUS));
        response.put(SERIAL_NO, lIntfOtpDetails.get(SERIAL_NO));
        response.put("OtpValTime", lIntfOtpDetails.get("OtpValTime"));
        response.put("PayloadStatus", pMessage.getResponseObject().getResponseJson().
                getJSONObject("updatePayLoadStatusResponse").getString(ServerConstants.MESSAGE_HEADER_STATUS));
        if (lIntfOtpDetails.has("ReqLdRefNo")) {
            response.put("ReqLdRefNo", lIntfOtpDetails.get("ReqLdRefNo"));
        }
        JSONObject valResponse = new JSONObject(lResponseFromService);
        valResponse.put("OTPValidationResponse", response);
        LOG.debug("valResponse : {}", valResponse);
        pMessage.getResponseObject().setResponseJson(valResponse);
    }

    public void deleteOtpPayloadFrmLdRecs(Message pMessage) {
        try {
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_DELETE_LD_RECS);
            DomainStartup.getInstance().processRequest(pMessage);
        } catch (ObjectOptimisticLockingFailureException e) {
            LOG.error(LOGGER_EXCEPTION, ServerConstants.LOGGER_PREFIX_FRAMEWORKS, e);
            DomainException lDomainException = DomainException.getDomainExceptionInstance();
            String emsg = lDomainException.getDomainExceptionMessage(DomainException.Code.APZ_DM_063);
            lDomainException.setMessage(emsg);
            lDomainException.setCode(DomainException.Code.APZ_DM_063.toString());
            lDomainException.setPriority("1");
            throw lDomainException;
        }
    }
}
