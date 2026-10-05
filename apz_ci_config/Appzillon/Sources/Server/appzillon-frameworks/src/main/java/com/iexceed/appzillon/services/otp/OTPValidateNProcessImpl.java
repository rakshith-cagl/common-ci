package com.iexceed.appzillon.services.otp;

import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;

import static com.iexceed.appzillon.utils.Constants.REF_NO;

public class OTPValidateNProcessImpl extends ExternalServicesRouter {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,
                    OTPValidateNProcessImpl.class.getName());

    public void serviceRequestDispatcher(Message pMessage,
                                         SpringCamelContext context) throws ExternalServicesRouterException,
            InvalidPayloadException, ClassNotFoundException, JSONException {

        LOG.info("{} OTPValidateService Implementation Dispatching request to Service Bean to process the request....", ServerConstants.LOGGER_PREFIX_FRAMEWORKS);
        String lInterfaceId = pMessage.getHeader().getInterfaceId();
        String lAppId = pMessage.getHeader().getAppId();
        String lCamelID = lAppId + "_" + lInterfaceId + ServerConstants.BEAN_APPEND_SERVICE;
        LOG.debug("{} OTPValidateService Application Id -: {}, InterfaceId -: {}, Service BeanId -: {}"
                , ServerConstants.LOGGER_PREFIX_FRAMEWORKS, lAppId, lInterfaceId, lCamelID);

        LOG.debug("{} Request in OTPValidateNProcessImpl is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
        JSONObject lValidateNProcessReqJSON = pMessage.getRequestObject().getRequestJson();
        JSONObject lValidateRequest = lValidateNProcessReqJSON.getJSONObject("validateNProcessRequest");
        pMessage.getRequestObject().setRequestJson(new JSONObject().put("validateOtpRequest", lValidateRequest));
        OTPEngineService otpEngine = new OTPEngineService();
        otpEngine.validateOtp(pMessage);
        LOG.debug("{} Validated OTP -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getResponseObject().getResponseJson());
        JSONObject lValidateOTPResp = pMessage.getResponseObject().getResponseJson();
        JSONObject lValidateOTPStatus = lValidateOTPResp.getJSONObject("OTPValServiceResponse");
        String lOTPValidateStatus = lValidateOTPStatus.getString("status");
        if (lOTPValidateStatus.equalsIgnoreCase(ServerConstants.YES)) {
            JSONObject validateOTPJSON = pMessage.getRequestObject().getRequestJson();
            JSONObject serialNoJSON = validateOTPJSON.getJSONObject("validateOtpRequest");
            String refNo = serialNoJSON.getString(REF_NO);
            JSONObject updateOtpStatus = new JSONObject();
            updateOtpStatus.put(REF_NO, refNo);
            updateOtpStatus.put("status", "P");
            pMessage.getRequestObject().setRequestJson(new JSONObject().put("updateOtpStatusRequest", updateOtpStatus));
            LOG.debug("{} Request JSON to Update OTP status is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
            otpEngine.updateOtp(pMessage);
            JSONObject lUpdateOTPStatusResp = pMessage.getResponseObject().getResponseJson();
            JSONObject lStatusJson = lUpdateOTPStatusResp.getJSONObject("updateOtpStatusResponse");
            String lOTPUpdateRespStatus = lStatusJson.getString(ServerConstants.MESSAGE_HEADER_STATUS);
            if (lOTPUpdateRespStatus.equalsIgnoreCase(ServerConstants.YES)) {
                pMessage.getHeader().setOtpValStatus(ServerConstants.YES);
                pMessage.getRequestObject().setRequestJson(new JSONObject().put("ProcessIFaceRequest", lValidateRequest));
                otpEngine.processIface(pMessage);
                pMessage.getRequestObject().setRequestJson(lValidateNProcessReqJSON);


            }
            JSONObject deleteOTP = new JSONObject();
            deleteOTP.put(REF_NO, refNo);
            //JSONObject jsonObj = pMessage.getResponseObject().getResponseJson();
            //JSONObject valResponse = jsonObj.getJSONObject("OTPValidationResponse");

            pMessage.getRequestObject().setRequestJson(new JSONObject().put("deleteOtpRequest", deleteOTP));
            LOG.debug("{} Request JSON to Delete OTP status is -: {}", ServerConstants.LOGGER_PREFIX_FRAMEWORKS, pMessage.getRequestObject().getRequestJson());
            otpEngine.deleteOtp(pMessage);

            //Deleting otp payload from LdRecs
			/*JSONObject deleteldRecs = new JSONObject();
			deleteldRecs.put("ReqLdRefNo", valResponse.getString("ReqLdRefNo"));
			
			jsonObj.getJSONObject("OTPValidationResponse").remove("ReqLdRefNo");
			pMessage.getRequestObject().setRequestJson(new JSONObject().put("deleteLdRecsRequest", deleteldRecs));
			otpEngine.deleteOtpPayloadFrmLdRecs(pMessage);
			pMessage.getResponseObject().setResponseJson(jsonObj);*/
//			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " OTP Deleted :" + pMessage.getResponseObject().getResponseJson());
        }
    }
}
