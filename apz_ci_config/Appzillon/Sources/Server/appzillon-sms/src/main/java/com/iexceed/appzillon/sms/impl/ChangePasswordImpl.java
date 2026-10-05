package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.domain.exception.DomainException;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.securityutils.HashUtils;
import com.iexceed.appzillon.securityutils.PasswordValidate;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.iface.IChangePassword;
import com.iexceed.appzillon.sms.utils.SmsUtils;
import com.iexceed.appzillon.utils.ServerConstants;

import static com.iexceed.appzillon.utils.ServerConstants.*;

public class ChangePasswordImpl implements IChangePassword {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, ChangePasswordImpl.class.getName());

    // changes made on 31/01/2017
    public void updatePassword(Message pMessage) {
        LOG.debug("{} Update Password ", ServerConstants.LOGGER_PREFIX_SMS);
        JSONObject lBody = pMessage.getRequestObject().getRequestJson();
        JSONObject lOutputJson = null;
        try {
            pMessage.getHeader().setServiceType("checkUserLocked");
            DomainStartup.getInstance().processRequest(pMessage);
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CHANGE_PIN);
            LOG.debug("{} ChangePasswordImpl- AppzillonBody : {}", ServerConstants.LOGGER_PREFIX_SMS, lBody);

            JSONObject passobj = lBody.getJSONObject(ServerConstants.CHANGEPINREQUEST);

            pMessage.getRequestObject().getRequestJson().put(ServerConstants.APPZILLON_ROOT_LOGIN_REQUEST, passobj);
            LOG.debug("{} ChangePasswordImpl - request : {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getRequestObject().getRequestJson());

            AuthenticationImpl authenticator = new AuthenticationImpl();
            LOG.debug("{} Validate User during change password", ServerConstants.LOGGER_PREFIX_SMS);
            lOutputJson = authenticator.validateUser(pMessage);

            LOG.debug("{} lOutputJson : {}", ServerConstants.LOGGER_PREFIX_SMS, lOutputJson);

            boolean lOtpmatch = false;
            String authenticationRequired = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.AUTHENTICATION_TYPE).trim();
            if (!ServerConstants.HASH_DEVICE_ID.equalsIgnoreCase(authenticationRequired)) {
                LOG.debug("{} Authentication Required is PlainText", ServerConstants.LOGGER_PREFIX_SMS);
                lOtpmatch = isPwdMatchesWhenAuthRequiredIsNotDeviceId(pMessage, passobj, lOutputJson);
            } else {
                LOG.debug("{} Authentication Required is #DeviceId", ServerConstants.LOGGER_PREFIX_SMS);
                lOtpmatch = isPwdMatchesWhenAuthRequiredIsDeviceId(pMessage, passobj, lOutputJson, authenticator);
            }
            LOG.debug("{} updatePassword -lOtpmatch: {}", ServerConstants.LOGGER_PREFIX_SMS, lOtpmatch);

            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_CHANGE_PIN);

            // changes made by sasidhar to send message to email,mobile and device upon
            // password change
            if (lOtpmatch) {
                DomainStartup.getInstance().processRequest(pMessage);
                sendNotificationIfPwdChangeIsSuccess(pMessage, lBody);

            } else {
                LOG.debug("{} Updating failed count in user table", ServerConstants.LOGGER_PREFIX_SMS);
                pMessage.getHeader().setServiceType("updateFailedChangePwdCount");
                DomainStartup.getInstance().processRequest(pMessage);
                DomainException dexp = DomainException.getDomainExceptionInstance();
                dexp.setMessage(dexp.getDomainExceptionMessage(DomainException.Code.APZ_DM_032));
                dexp.setCode(DomainException.Code.APZ_DM_032.toString());
                dexp.setPriority("1");
                throw dexp;
            }
        } catch (JSONException jsone) {
            LOG.error("{} JSONException : {}", ServerConstants.LOGGER_PREFIX_SMS, jsone);
            SmsException sexp = SmsException.getSMSExceptionInstance();
            sexp.setMessage(jsone.getMessage());
            sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
            sexp.setPriority("1");
            throw sexp;
        }
    }

    private void sendNotificationIfPwdChangeIsSuccess(Message pMessage, JSONObject lBody) {
        if (pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.CHANGEPINRESPONSE)
                .getString(ServerConstants.MESSAGE_HEADER_STATUS).equals(ServerConstants.SUCCESS)) {

            LOG.debug("password changed successfully and now sending notifications over communication channels");
            String pwdChaneComChannel = pMessage.getResponseObject().getResponseJson()
                    .getJSONObject(ServerConstants.CHANGEPINRESPONSE)
                    .getString(ServerConstants.PINCHANGECOMCHANNEL);
            JSONObject responseFromDomain = pMessage.getResponseObject().getResponseJson();

            LOG.debug("Fetching user email and phone number");
            String luserEml = SmsUtils.getInstance().fetchUserEmail(pMessage);
            String luserPhone = SmsUtils.getInstance().fetchUserPhoneNumber(pMessage);

            JSONObject mailRequiredDtls = new JSONObject();
            JSONObject pwdChange = new JSONObject();

            pwdChange.put(ServerConstants.MAIL_CONSTANTS_EMAIL_ID, luserEml);
            pwdChange.put(ServerConstants.MAIL_CONSTANTS_BODY, responseFromDomain.getJSONObject(ServerConstants.CHANGEPINRESPONSE).
                    getString(ServerConstants.MAIL_CONSTANTS_BODY));
            pwdChange.put(ServerConstants.MAIL_CONSTANTS_SUBJECT, responseFromDomain.getJSONObject(ServerConstants.CHANGEPINRESPONSE).
                    getString(ServerConstants.MAIL_CONSTANTS_SUBJECT));
            mailRequiredDtls.put(ServerConstants.CHANGEPINRESPONSE, pwdChange);

            String templateBody = responseFromDomain.getJSONObject(ServerConstants.CHANGEPINRESPONSE).
                    getString(ServerConstants.SMS_CONSTANTS_BODY);
            String notificationBody = responseFromDomain.getJSONObject(ServerConstants.CHANGEPINRESPONSE).
                    getString(ServerConstants.NOTIFICATION_CONSTANTS_BODY);
            pMessage.getResponseObject().setResponseJson(mailRequiredDtls);

            //changes made here on 07/02/17 to send notification to user devices
            // sending message through communication channel
            String reqinIerfaceId = pMessage.getHeader().getInterfaceId();
            LOG.debug("Response json is {}", pMessage.getResponseObject().getResponseJson());

            pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
            JSONObject notificationChannel = SmsUtils.getInstance().getNotificationChannels(pwdChaneComChannel);

            pMessage.getHeader().setAsyncMail(true);
            SmsUtils.getInstance().sendMessage(pMessage, luserPhone, notificationChannel.getBoolean(P_MOBILE), notificationChannel.getBoolean(P_EMAIL), notificationChannel.getBoolean(PNOTIFICATION), templateBody, notificationBody);
            pMessage.getHeader().setInterfaceId(reqinIerfaceId);
            pMessage.getRequestObject().setRequestJson(lBody);
            LOG.debug("Response json is {}", pMessage.getResponseObject().getResponseJson());

            LOG.debug("Message is delivered");
            JSONObject json;
            json = pMessage.getResponseObject().getResponseJson();
            //Setting back original response
            JSONObject finalResponse = new JSONObject();
            JSONObject res = new JSONObject();
            res.put(ServerConstants.MESSAGE_HEADER_STATUS, responseFromDomain.getJSONObject(ServerConstants.CHANGEPINRESPONSE).
                    getString(ServerConstants.MESSAGE_HEADER_STATUS));
            res.put(ServerConstants.MESSAGE, responseFromDomain.getJSONObject(ServerConstants.CHANGEPINRESPONSE).
                    getString(ServerConstants.MESSAGE));
            res.put(ServerConstants.MODE_OF_CHANNEL, pwdChaneComChannel);
            res.put(COMMUNICATION, json.getJSONObject(COMMUNICATION));
            finalResponse.put(ServerConstants.CHANGEPINRESPONSE, res);
            pMessage.getResponseObject().setResponseJson(finalResponse);
        }
    }

    private boolean isPwdMatchesWhenAuthRequiredIsDeviceId(Message pMessage, JSONObject passobj, JSONObject lOutputJson, AuthenticationImpl authenticator) {
        pMessage.getResponseObject().setResponseJson(lOutputJson);
        LOG.debug("{} request to compute OTP : {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());

        String lOtpValue = authenticator.computeHashedPwd(pMessage);
        LOG.debug("{} updatePassword -lOtpValue: {}", ServerConstants.LOGGER_PREFIX_SMS, lOtpValue);
        return authenticator.otpMatched(lOtpValue, passobj.getString(ServerConstants.PIN));
    }

    private boolean isPwdMatchesWhenAuthRequiredIsNotDeviceId(Message pMessage, JSONObject passobj, JSONObject lOutputJson) {
        String lServerToken = pMessage.getSecurityParams().getServerToken();
        boolean lOtpmatch = false;
        String lHashedPin = HashUtils.hashSHA256(passobj.getString(ServerConstants.PIN),
                passobj.getString(ServerConstants.MESSAGE_HEADER_USER_ID) + lServerToken);
        LOG.debug("{} updatePassword - ServerToken: {}, lHashedPin: {}", ServerConstants.LOGGER_PREFIX_SMS, lServerToken, lHashedPin);
        JSONObject lJsonObject = lOutputJson.getJSONObject("loginRequest");
        LOG.debug("{} updatePassword - lJsonObject: {}", ServerConstants.LOGGER_PREFIX_SMS, lJsonObject);

        if (lJsonObject.getString(ServerConstants.DBPIN).equals(lHashedPin)) {
            LOG.debug("{} Hashed pin matches DB Pin", ServerConstants.LOGGER_PREFIX_SMS);
            lOtpmatch = true;
        }
        return lOtpmatch;
    }

    /********** password validate function ***********/
    public void passwordValidate(Message pMessage) {
        LOG.debug("{} Inside PasswordValidate", ServerConstants.LOGGER_PREFIX_SMS);
        if (ServerConstants.INTERFACE_ID_PIN_VALIDATE.equals(pMessage.getHeader().getInterfaceId())) {
            try {
                JSONObject lBody = pMessage.getRequestObject().getRequestJson();
                JSONObject passwordObj = lBody.getJSONObject(ServerConstants.APPZILLON_ROOT_SMS_VAL_PIN_REQ);
                LOG.info("{} validatePass : {}", ServerConstants.LOGGER_PREFIX_SMS, passwordObj.getString("password"));
                String message = PasswordValidate.passwordValidate(passwordObj.getString("password"), pMessage.getHeader().getAppId());
                LOG.info("{} message : {}", ServerConstants.LOGGER_PREFIX_SMS, message);
                passwordObj.put("message", message);
                lBody.remove(ServerConstants.APPZILLON_ROOT_SMS_VAL_PIN_REQ);
                lBody.put(ServerConstants.APPZILLON_ROOT_SMS_VAL_PIN_RES, passwordObj);
                pMessage.getRequestObject().setRequestJson(lBody);
            } catch (JSONException jsonex) {
                LOG.error(ServerConstants.LOGGER_PREFIX_SMS, jsonex);
                SmsException sexp = SmsException.getSMSExceptionInstance();
                sexp.setMessage(jsonex.getMessage());
                sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
                sexp.setPriority("1");
                throw sexp;
            }
        }
    }

}
