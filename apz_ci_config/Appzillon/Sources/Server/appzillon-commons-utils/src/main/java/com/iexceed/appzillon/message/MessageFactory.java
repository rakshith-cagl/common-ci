/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.message;

import com.iexceed.appzillon.exception.LoggerException;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.intf.AppzillonInterface;
import com.iexceed.appzillon.intf.AppzillonInterfaceDetails;
import com.iexceed.appzillon.json.JSONArray;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.AppzillonAESUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.securityutils.RSACryptoUtils;
import com.iexceed.appzillon.utils.ServerConstants;
import com.iexceed.appzillon.utilsexception.UtilsException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author arthanarisamy
 */
public class MessageFactory {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, MessageFactory.class.getName());

    static MessageFactory sMessageFactory = null;

    private Message cMessage = null;

    private MessageFactory() {

    }

    private static MessageFactory getInstance() {
        sMessageFactory = new MessageFactory();
        return sMessageFactory;
    }

    public static Message getMessage(String requestPayLoad, FormDataMultiPart multiPart) {
        return MessageFactory.getInstance().buildRequestMessage(requestPayLoad, multiPart);
    }

    public static String buildResponseJson(Message pMessage) {
        Response lResponse = null;
        JSONObject lHeaderJson = new JSONObject();
        JSONObject lResponseJson = new JSONObject();
        String response = "";
        JSONArray lErrors = new JSONArray();

        Header lHeader = pMessage.getHeader();
        lHeaderJson.put(ServerConstants.MESSAGE_HEADER_APP_ID, lHeader.getAppId());
        lHeaderJson.put(ServerConstants.MESSAGE_HEADER_DEVICE_ID, lHeader.getDeviceId());
        lHeaderJson.put(ServerConstants.MESSAGE_HEADER_REQUEST_KEY, lHeader.getRequestKey());
        lHeaderJson.put(ServerConstants.MESSAGE_HEADER_SCREEN_ID, lHeader.getScreenId());
        lHeaderJson.put(ServerConstants.MESSAGE_HEADER_SESSION_ID, lHeader.getSessionId());
        lHeaderJson.put(ServerConstants.MESSAGE_HEADER_STATUS, lHeader.getStatus());
        lHeaderJson.put(ServerConstants.MESSAGE_HEADER_USER_ID, lHeader.getUserId());
        lHeaderJson.put(ServerConstants.MESSAGE_HEADER_INTERFACE_ID, lHeader.getInterfaceId());
        lHeaderJson.put(ServerConstants.SELECTOR, pMessage.getHeader().getSelector());
        lHeaderJson.put(ServerConstants.SOURCE, lHeader.getSource());
        lHeaderJson.put(ServerConstants.REQUEST_ID, lHeader.getRequestId());
        lHeaderJson.put(ServerConstants.MESSAGE_HEADER_LOCATION, lHeader.getLocation());
        lHeaderJson.put(ServerConstants.CLIENT_NONCE, lHeader.getClientNonce());
        lHeaderJson.put(ServerConstants.SERVER_NONCE, lHeader.getServerNonce());
        lHeaderJson.put(ServerConstants.MESSAGE_HEADER_REQ_REF_ID, lHeader.getReqRefId());
        lHeaderJson.put(ServerConstants.SESSION_TOKEN, lHeader.getSessionToken());
        lHeaderJson.put(ServerConstants.APPVERSION, lHeader.getAppVersion());
        if (!lHeader.getStatus()) {

            // Below changes are to create a List of error code and error desc
            List<Error> errors = pMessage.getErrors();
            if (!errors.isEmpty()) {
                for (Error lError : errors) {
                    JSONObject lErrorJson = new JSONObject();
                    if (Utils.isNotNullOrEmpty(lError.getErrorCode())) {
                        lErrorJson.put(ServerConstants.MESSAGE_HEADER_ERROR_CODE, lError.getErrorCode());
                        lErrorJson.put(ServerConstants.MESSAGE_HEADER_ERROR_MESSAGE, lError.getErrorDesc());
                        lErrors.put(lErrorJson);
                    }

                }
            }

            /**
             * Request's response status found to be failure, hence setting Request JSON as
             * body in the response.
             */
            lResponseJson.put(ServerConstants.MESSAGE_HEADER, lHeaderJson);

            if (lErrors.getJSONObject(0).get(ServerConstants.MESSAGE_HEADER_ERROR_CODE).equals("APZ-SMS-EX-016")) {
                lResponseJson.put(ServerConstants.MESSAGE_BODY, pMessage.getResponseObject().getResponseJson());
            } else {
                lResponseJson.put(ServerConstants.MESSAGE_BODY, pMessage.getRequestObject().getRequestJson());

                /*if (pMessage.getIntfDtls() != null
                        && ServerConstants.INTERFACE_CATEGORY_EXTERNAL.equals(pMessage.getIntfDtls().getType())
                        && pMessage.getResponseObject().getResponseJson().has(ServerConstants.HTTP_ERROR_BODY)) {*/
                if (pMessage.getIntfDtls() != null && ServerConstants.INTERFACE_CATEGORY_EXTERNAL.equals(pMessage.getIntfDtls().getType())) {
                    String resp = pMessage.getResponseObject().getResponseJson().getString(ServerConstants.HTTP_ERROR_BODY);
                    if(Utils.isValid(resp)){
                        JSONObject jsonOut = new JSONObject();
                        jsonOut.put(ServerConstants.HTTP_ERROR_BODY, new JSONObject(resp));
                        pMessage.getResponseObject().setResponseJson(jsonOut);
                    }
                    lResponseJson.put(ServerConstants.MESSAGE_BODY, pMessage.getResponseObject().getResponseJson());
                }
            }

            lResponseJson.put(ServerConstants.MESSAGE_ERROR, lErrors);
        } else {
            /**
             * Request's response status found to be success, hence setting Response JSON as
             * body in the response.
             */
            lResponse = pMessage.getResponseObject();
            lResponseJson.put(ServerConstants.MESSAGE_HEADER, lHeaderJson);

            lResponseJson.put(ServerConstants.MESSAGE_BODY, lResponse.getResponseJson());

        }
        String replayAttackFlag = "";
        try {
            replayAttackFlag = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(),
                    ServerConstants.REPLAY_REQUEST_REQUIRED);
        } catch (LoggerException e) {
            LOG.warn("Property not found replayAttackFlag");
        }

        response = lResponseJson.toString(0);

        String apzHeader = lResponseJson.getJSONObject(ServerConstants.MESSAGE_HEADER).toString(0);
        String apzBody = lResponseJson.getJSONObject(ServerConstants.MESSAGE_BODY).toString(0);
        String apzErrors = "";
        if (lResponseJson.has(ServerConstants.MESSAGE_ERROR)) {
            apzErrors = lResponseJson.getJSONArray(ServerConstants.MESSAGE_ERROR).toString(0);
        }

        if ((Utils.isNullOrEmpty(replayAttackFlag) || !ServerConstants.NO.equalsIgnoreCase(replayAttackFlag))
                && ServerConstants.YES.equals(pMessage.getSecurityParams().getDataIntegrity())
                && !ServerConstants.RICT.equalsIgnoreCase(pMessage.getHeader().getOs()) && !lHeader.isSmsType()
                && !pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERFACE_ID_GET_APP_SEC_TOKENS)
                && !pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERACE_ID_GET_USER_APPACCESS_TOKEN)
                && !pMessage.getHeader().getInterfaceId().equals(ServerConstants.INTERACE_ID_RELOAD_LOGGER)
                && Utils.isNullOrEmpty(pMessage.getHeader().getUserAppAccessToken())) {

            String lHashedCnonce = HashUtils.hashSHA256(lHeader.getClientNonce(),
                    lHeader.getServerNonce() + pMessage.getSecurityParams().getServerToken());
            LOG.trace("lResponseJson.toString(0) : {} ", lResponseJson.toString(0));
            String lFormatedString = Utils.getPayLoadForQop(apzHeader, apzBody, apzErrors);
            LOG.trace("lResponseJson after formatting : {}", lFormatedString);
            String responseString = lFormatedString;
            LOG.trace("Message Factory Response Building : {} and length : {}", responseString,
                    responseString.length());
            responseString = StringEscapeUtils.escapeJava(responseString);
            LOG.debug("Escaped java response string for qop calculation {}", responseString);
            LOG.debug("Escaped java response string length {}", responseString.length());
            responseString = Base64.encodeBase64String(responseString.getBytes());
            String hashedResponse = HashUtils.hashSHA256(responseString, lHashedCnonce);
            LOG.trace("Message Factory Hashed QOP : {} ", hashedResponse);

            response = Utils.appendQopWithPayload(lFormatedString, hashedResponse);

        }
        LOG.debug("Actual Response From Appzillon Server : {} ", response);
        // Response Encryption Started here
        if ((Utils.isNullOrEmpty(replayAttackFlag) || !ServerConstants.NO.equalsIgnoreCase(replayAttackFlag))
                && !ServerConstants.INTERFACE_ID_UPLOAD_FILE.equalsIgnoreCase(lHeader.getInterfaceId())
                && !ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS.equalsIgnoreCase(lHeader.getInterfaceId())
                && !ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH.equalsIgnoreCase(lHeader.getInterfaceId())
                && !ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE.equalsIgnoreCase(lHeader.getInterfaceId())
                && !ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH.equalsIgnoreCase(lHeader.getInterfaceId())
                && !ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS.equalsIgnoreCase(lHeader.getInterfaceId())
                && !ServerConstants.INTERACE_ID_RELOAD_LOGGER.equalsIgnoreCase(lHeader.getInterfaceId())
                && !lHeader.isSmsType()
                && !ServerConstants.INTERACE_ID_GET_USER_APPACCESS_TOKEN.equalsIgnoreCase(lHeader.getInterfaceId())
                && Utils.isNullOrEmpty(pMessage.getHeader().getUserAppAccessToken())) {
            if (RSACryptoUtils.rsaEncryptionRequired.equalsIgnoreCase(ServerConstants.YES)
                    && ServerConstants.RICT.equalsIgnoreCase(lHeader.getOs())) {
                response = RSACryptoUtils.encryptResponse(lResponseJson, apzHeader, apzBody, apzErrors);
            } else if (RSACryptoUtils.rsaEncryptionRequired.equalsIgnoreCase(ServerConstants.YES)) {
                String appzillonBody = "";
                int safeBit = pMessage.getHeader().getSafeBit();
                if (!(lResponseJson.has(ServerConstants.MESSAGE_ERROR) && lErrors.getJSONObject(0)
                        .get(ServerConstants.MESSAGE_HEADER_ERROR_CODE).equals("APZ_RS_001"))) {
                    appzillonBody = apzBody;
                }
                if (lResponseJson.has(ServerConstants.MESSAGE_ERROR)) {
                    lResponseJson.put(ServerConstants.MESSAGE_ERROR,
                            RSACryptoUtils.getEncryptedString(lHeader, apzErrors, safeBit));
                }
                lResponseJson.put(ServerConstants.MESSAGE_HEADER,
                        RSACryptoUtils.getEncryptedString(lHeader, apzHeader, safeBit));
                lResponseJson.put(ServerConstants.MESSAGE_BODY,
                        RSACryptoUtils.getEncryptedString(lHeader, appzillonBody, safeBit));

                String safeToken = lHeader.getServerToken();
                lResponseJson.put(ServerConstants.MESSAGE_SAFE, RSACryptoUtils.encryptData(safeToken));
                if (response.contains(ServerConstants.QOP)) {
                    int i = response.indexOf(ServerConstants.QOP);
                    String qop = response.substring(i + 15, i + 79);
                    lResponseJson.put(ServerConstants.QOP, qop);
                }
                response = lResponseJson.toString(0);
            }
        }
        return response;
    }

    /**
     * @param pRequestBody
     * @return
     */
    public static JSONObject getDecryptedBody(Header pHeader, String pRequestBody) {
        LOG.debug("inside getDecryptedBody");
        JSONObject decryptedAppzillonBody = null;
        String key = PropertyUtils.getPropValue(pHeader.getAppId(), ServerConstants.SERVER_SECURITY_ENCRYPTION_KEY);
        LOG.trace("{} Encryption key from properties file : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, key);
        LOG.trace("{} pRequestBody : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, pRequestBody);
        decryptedAppzillonBody = new JSONObject(AppzillonAESUtils.decryptUsingPayload(getKey(key, pHeader), pRequestBody, pHeader.getOs(), pHeader.getSafeBit(), pHeader.getEncMode(), pHeader.getEncyKeyLen()));
        LOG.debug("{} Decrypted AppzillonBody : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, decryptedAppzillonBody);

        return decryptedAppzillonBody;
    }

    public static String getEncrypteBody(Header pHeader, String pResponseBody) {
        String encryptedAppzillonBody = null;

        String key = PropertyUtils.getPropValue(pHeader.getAppId(), ServerConstants.SERVER_SECURITY_ENCRYPTION_KEY);
        LOG.trace("{} Encryption key from properties file : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, key);
        LOG.trace("{} pResponseBody : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, pResponseBody);
        encryptedAppzillonBody = AppzillonAESUtils.encryptPayload(getKey(key, pHeader), pResponseBody, pHeader.getOs(), pHeader.getSafeBit(), pHeader.getEncMode(), pHeader.getEncyKeyLen());
        LOG.debug("{} Encrypted Body : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, encryptedAppzillonBody);
        return encryptedAppzillonBody;
    }

    private static String getKey(String key, Header pHeader) {
        key = key.replace("$" + ServerConstants.MESSAGE_HEADER_APP_ID.toUpperCase(), pHeader.getAppId());
        key = key.replace("$" + ServerConstants.MESSAGE_HEADER_USER_ID.toUpperCase(), pHeader.getUserId());
        key = key.replace("$" + ServerConstants.MESSAGE_HEADER_DEVICE_ID.toUpperCase(), pHeader.getDeviceId());
        LOG.trace("{} Encryption key after replacing fillers : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, key);
        String paddingMask = "$$$$$$$$$$$$$$$$";
        if (key.length() <= 16) {
            key += paddingMask.substring(0, 16 - key.length());
        }
        if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        LOG.trace("{} Encryption key after checking the length : {}", ServerConstants.LOGGER_PREFIX_RESTFULL, key);
        return key;
    }

    private Message buildRequestMessage(String requestPayLoad, FormDataMultiPart multiPart) {
        try {
            JSONObject jSONObject = new JSONObject(requestPayLoad);
            cMessage = Message.getInstance();
            cMessage.setHeader(buildHeader(jSONObject.getJSONObject(ServerConstants.MESSAGE_HEADER)));
            cMessage.getErrors().add(buildError(requestPayLoad));
            cMessage.setIntfDtls(buildIntefDetails(cMessage.getHeader()));
            cMessage.setRequestObject(buildRequest(jSONObject.getJSONObject(ServerConstants.MESSAGE_BODY)));
            cMessage.setResponseObject(buildResponse(jSONObject.getJSONObject(ServerConstants.MESSAGE_BODY)));
            cMessage.setSession(buildSession());
            cMessage.setFormDataMultiPart(multiPart);
        } catch (JSONException e) {
            LOG.error("Error in json request :", e);
            UtilsException utilsException = UtilsException.getUtilsExceptionInstance();
            utilsException.setCode("APZ_RS_001");
            utilsException.setMessage("Invalid Appzillon Request");
            throw utilsException;
        }
        return cMessage;
    }

    private Header buildHeader(JSONObject requestPayLoad) {
        Header cHeader = cMessage.getHeader();

        if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_AUTH_TOKEN)) {
            cHeader.setAuthToken(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_AUTH_TOKEN));
        }

        if (requestPayLoad.has(ServerConstants.APP_LAUNCH)) {
            cHeader.setAppLaunch(requestPayLoad.getString(ServerConstants.APP_LAUNCH));
        }
        if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_APP_ID)) {
            cHeader.setAppId(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_APP_ID));
        }
        if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_SIGNATURE)) {
            cHeader.setSignature(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_SIGNATURE));
        }
        if (requestPayLoad.has(ServerConstants.MESSAGE_HEADER_DEVICE_ID)) {
            cHeader.setDeviceId(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_DEVICE_ID));
        }
        if (requestPayLoad.hasNotNull(ServerConstants.MESSAGE_HEADER_REQUEST_KEY)) {
            cHeader.setRequestKey(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_REQUEST_KEY));

        }
        if (requestPayLoad.hasNotNull(ServerConstants.MESSAGE_HEADER_SCREEN_ID)) {
            cHeader.setScreenId(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_SCREEN_ID));

        }

        if (requestPayLoad.hasNotNull(ServerConstants.MESSAGE_HEADER_SESSION_ID)) {
            cHeader.setSessionId(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_SESSION_ID));
        }
        if (requestPayLoad.hasNotNull(ServerConstants.MESSAGE_HEADER_STATUS)) {
            cHeader.setStatus(requestPayLoad.getBoolean(ServerConstants.MESSAGE_HEADER_STATUS));

        }
        if (requestPayLoad.hasNotNull(ServerConstants.MESSAGE_HEADER_USER_ID)) {
            cHeader.setUserId(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_USER_ID));
        }

        if (requestPayLoad.hasNotNull(ServerConstants.MESSAGE_HEADER_ASYNCH)) {
            cHeader.setAsynch(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_ASYNCH));

        }
        if (requestPayLoad.hasNotNull(ServerConstants.MESSAGE_HEADER_INTERFACE_ID)) {
            cHeader.setInterfaceId(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_INTERFACE_ID));
        }

		/*
		  Changes made by Ripu, newly introduced 'pin'(plain password) for accessing
		  the services from external application without session Appzillon 3.1 - 60 --
		  Start
		 */
        if (requestPayLoad.has(ServerConstants.PIN) && requestPayLoad.get(ServerConstants.PIN) != JSONObject.NULL) {
            cHeader.setPin(requestPayLoad.getString(ServerConstants.PIN));

        }
        /** Appzillon 3.1 - 60 -- END */

        /* Below changes was done by ripu on 11-12-2014 as part of OTA */
        if (requestPayLoad.hasNotNull(ServerConstants.PRELOGIN)) {
            cHeader.setPreLogin(requestPayLoad.getBoolean(ServerConstants.PRELOGIN));
        }
        /* ripu changes end */

        if (requestPayLoad.hasNotNull(ServerConstants.OS)) {
            cHeader.setOs(requestPayLoad.getString(ServerConstants.OS));
        }
        if (requestPayLoad.hasNotNull(ServerConstants.SOURCE)) {
            cHeader.setSource(requestPayLoad.getString(ServerConstants.SOURCE));
        }

        // adding requestID in header
        if (requestPayLoad.hasNotNull(ServerConstants.REQUEST_ID)) {
            cHeader.setRequestId(requestPayLoad.getString(ServerConstants.REQUEST_ID));
        }
        if (requestPayLoad.hasNotNull(ServerConstants.MESSAGE_HEADER_OTP_VALIDATE_STATUS)) {
            cHeader.setOtpValStatus(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_OTP_VALIDATE_STATUS));

        }
        if (requestPayLoad.hasNotNull(ServerConstants.MESSAGE_HEADER_ORIGINATION)) {
            cHeader.setOrigination(requestPayLoad.getString(ServerConstants.MESSAGE_HEADER_ORIGINATION));

        }
        if (requestPayLoad.hasNotNull(ServerConstants.MESSAGE_HEADER_LOCATION)) {
            cHeader.setLocation(requestPayLoad.getJSONObject(ServerConstants.MESSAGE_HEADER_LOCATION));
        }
        if (requestPayLoad.hasNotNull(ServerConstants.CAPTCHA_STRING)) {
            cHeader.setCaptchaString(requestPayLoad.getString(ServerConstants.CAPTCHA_STRING));
        }
        if (requestPayLoad.has(ServerConstants.CAPTCHA_REF)) {
            cHeader.setCaptchaRef(requestPayLoad.getString(ServerConstants.CAPTCHA_REF));
        }
        if (requestPayLoad.has(ServerConstants.CLIENT_NONCE)) {
            cHeader.setClientNonce(requestPayLoad.getString(ServerConstants.CLIENT_NONCE));
        }
        if (requestPayLoad.has(ServerConstants.SERVER_NONCE)) {
            cHeader.setServerNonce(requestPayLoad.getString(ServerConstants.SERVER_NONCE));
        }
        if (requestPayLoad.has(ServerConstants.SESSION_TOKEN)) {
            cHeader.setSessionToken(requestPayLoad.getString(ServerConstants.SESSION_TOKEN));
        }
        if (requestPayLoad.has("smsType")) {
            cHeader.setSmsType(requestPayLoad.getBoolean("smsType"));
        }
        if (requestPayLoad.has(ServerConstants.APPVERSION)) {
            cHeader.setAppVersion(requestPayLoad.get(ServerConstants.APPVERSION)+"");
        } else {
            cHeader.setAppVersion("");
        }
        if (Utils.existsAndNotNullOrEmpty(requestPayLoad, ServerConstants.SELECTOR)
                && PropertyUtils.getPropValue(cHeader.getAppId(), ServerConstants.KEEP_ME_SIGNED_IN_ENABLED) != null
                && PropertyUtils.getPropValue(cHeader.getAppId(), ServerConstants.KEEP_ME_SIGNED_IN_ENABLED)
                .equals(ServerConstants.YES)
                && requestPayLoad.get(ServerConstants.MESSAGE_HEADER_DEVICE_ID).equals(ServerConstants.WEB)) {
            cHeader.setSelector(requestPayLoad.getString(ServerConstants.SELECTOR));
            cHeader.setKeepUserSignedIn(true);
        } else {
            cHeader.setSelector(null);
        }
        cHeader.setStartTime(new Timestamp(new Date().getTime()));
        if (requestPayLoad.has(ServerConstants.TXN_REF_NO))
            cHeader.setMasterTxnRef(requestPayLoad.getString(ServerConstants.TXN_REF_NO));
        else
            cHeader.setMasterTxnRef(Utils.getTxnRefNum(cHeader.getUserId()));
        cHeader.setReqRefId(cHeader.getMasterTxnRef());

        return cHeader;
    }

    private Error buildError(String requestPayLoad) {
        Error cError = Error.getInstance();
        try {
            JSONObject jSONObject = new JSONObject(requestPayLoad);
            LOG.debug("Valid Json Length: {}", jSONObject.length());
        } catch (JSONException jse) {
            cError.setErrorCode(requestPayLoad);
            cError.setErrorDesc(jse.getLocalizedMessage());
        }
        return cError;
    }

    private Request buildRequest(JSONObject requestPayLoad) {
        Request cRequest = cMessage.getRequestObject();
        try {

            if (cMessage.getIntfDtls() != null) {

                cRequest.setRequestJson(requestPayLoad);
            } else {
                JSONObject requestBody = new JSONObject();
                requestBody.put("appzillonBody", requestPayLoad);
                cRequest.setRequestJson(requestBody);
            }

        } catch (JSONException jse) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " buildRequest JSONException -:", jse);
            Error cError = Error.getInstance();
            cError.setErrorCode(requestPayLoad.toString());
            cError.setErrorDesc(jse.getLocalizedMessage());
            cMessage.getErrors().add(cError);
        }
        return cRequest;
    }

    private Response buildResponse(JSONObject responsePayLoad) {
        Response cResponse = cMessage.getResponseObject();
        try {

            if (cMessage.getIntfDtls() != null) {
                JSONObject jSONObject = new JSONObject(responsePayLoad);
                cResponse.setResponseJson(jSONObject);
            } else {
                JSONObject responseBody = new JSONObject();
                responseBody.put("appzillonBody", responsePayLoad);
                cResponse.setResponseJson(responseBody);
            }

        } catch (JSONException jse) {
            LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " buildResponse JSONException : ", jse);
            Error cError = Error.getInstance();
            cError.setErrorCode(responsePayLoad.toString());
            cError.setErrorDesc(jse.getLocalizedMessage());
            cMessage.getErrors().add(cError);
        }

        return cResponse;
    }


    public InterfaceDetails buildIntefDetails(Header pHeader) {
        InterfaceDetails cinterfaceDetails = null;
        AppzillonInterface lInterface = AppzillonInterfaceDetails.getInstance()
                .getInterfaceDtls(pHeader.getInterfaceId());
        if (lInterface != null) {
            cinterfaceDetails = cMessage.getIntfDtls();
            cinterfaceDetails.setAppId(lInterface.getAppId());
            cinterfaceDetails.setCategory(lInterface.getCategory());
            cinterfaceDetails.setInterfaceDesc(lInterface.getDescription());
            cinterfaceDetails.setInterfaceId(lInterface.getInterfaceId());
            cinterfaceDetails.setType(lInterface.getType());
            cinterfaceDetails.setSessionRequired(lInterface.getSessionRequired());
            cinterfaceDetails.setTxnLogReq(lInterface.getTxnLogReq());
            cinterfaceDetails.setTxnPayLoadLogReq(lInterface.getTxnPayLoadLogReq());
            cinterfaceDetails.setAuthorizationReq(lInterface.getAuthorizationReq());
        }

        return cinterfaceDetails;
    }

    private Session buildSession() {
        return cMessage.getSession();
    }

}
