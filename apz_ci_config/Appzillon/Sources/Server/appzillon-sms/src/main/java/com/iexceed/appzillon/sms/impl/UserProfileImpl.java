package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.SmsStartup;
import com.iexceed.appzillon.sms.exception.SmsException;
import com.iexceed.appzillon.sms.exception.SmsException.EXCEPTION_CODE;
import com.iexceed.appzillon.sms.iface.IPostUserCreation;
import com.iexceed.appzillon.sms.iface.IUserProfile;
import com.iexceed.appzillon.sms.utils.SmsUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.InvalidPayloadException;

import static com.iexceed.appzillon.utils.ServerConstants.*;

public class UserProfileImpl implements IUserProfile {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
            UserProfileImpl.class.getName());

    //changes made by sasidhar on 07/02/17
    public void createUserRequest(Message pMessage) {
        LOG.info("{} inside createUserRequest()", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
        try {
            /**
             * Changes made by Samy, Hook post user creation Appzillon 3.1 - 69
             * -- Start
             */
            IPostUserCreation postProcessor = (IPostUserCreation) SmsStartup.getInstance().getSpringContext()
                    .getBean(ServerConstants.BEAN_EXTENDED_USER_CREATION);
            LOG.debug("{} After injecting Post User Creation processor -: {}", ServerConstants.LOGGER_PREFIX_SMS, postProcessor);
            postProcessor.postUserCreationProcess(pMessage);
            LOG.debug("{} Response from Post User Creation Processor -: {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());
            /** Appzillon 3.1 - 69 -- END */
        } catch (Exception ex) {
            /**
             * Changes made by Ripu, checking for newly introduced auto password
             * generator flag to send User Password details over mail or not
             * Appzillon 3.1 - 69 -- Start
             */
            try {
                LOG.error("{} Exception occurred while injecting custom user creation bean {}", ServerConstants.LOGGER_PREFIX_SMS, ex.getMessage());
                LOG.debug("{} No Custom User Creation bean found hence proceeding with Appzillon's Send mail....", ServerConstants.LOGGER_PREFIX_SMS);
                //changes made on 03/04/17
                processPasswordOnAuthRequest(pMessage);


            } catch (JSONException jse) {
                LOG.error("JSONException -:", jse);
            }
            /** Appzillon 3.1 - 69 -- END */
        }
    }

    private void processPasswordOnAuthRequest(Message pMessage) {
        JSONObject lBody = pMessage.getRequestObject().getRequestJson();
        JSONObject outputJson = pMessage.getResponseObject().getResponseJson();
        JSONObject reqFromDomain = (JSONObject) outputJson.get(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES);
        String pswdOnAuth = (String) reqFromDomain.get(ServerConstants.PIN_ON_AUTHORIZATION);
        LOG.debug("{} password on authorization from domain response is : {}", ServerConstants.LOGGER_PREFIX_SMS, pswdOnAuth);
        if (pswdOnAuth.equalsIgnoreCase(ServerConstants.NO)) {

            LOG.debug("{} password on auth is NO,so we will communicate over commChannel", ServerConstants.LOGGER_PREFIX_SMS);
            String templateBody = (String) reqFromDomain.get(ServerConstants.SMS_CONSTANTS_BODY);
            String notificationTemplate = "";
            //getting mobile number from response
            String luserPhone = (String) reqFromDomain.get(ServerConstants.MOBILENUMBER);
            String allowUserPassEntry = (String) reqFromDomain.get(ServerConstants.ALLOW_USER_PIN_ENTRY);
            LOG.debug("{} allowUserPassEntry : {}", ServerConstants.LOGGER_PREFIX_SMS, allowUserPassEntry);
            String lpwdRstComChannel = (String) reqFromDomain.get(ServerConstants.PIN_COMM_CHANNEL);

            //changes made here to send email irrespective of allowUserPassEntry.

            //changes made here on 07/02/17 to send notification to user devices
            // sending message through communication channel

            LOG.debug(PIN_COMMUNICATION_CHANNEL, ServerConstants.LOGGER_PREFIX_SMS, lpwdRstComChannel);
            JSONObject notificationChannel = SmsUtils.getInstance().getNotificationChannels(lpwdRstComChannel);

            boolean pNotification = notificationChannel.getBoolean(PNOTIFICATION);
            String reqInInterfaceId = pMessage.getHeader().getInterfaceId();
            if (reqInInterfaceId.equalsIgnoreCase(ServerConstants.INTERFACE_ID_CREATE_USER)) {
                pNotification = false;
            }

            pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
            pMessage.getHeader().setAsyncMail(true);
            SmsUtils.getInstance().sendMessage(pMessage, luserPhone, notificationChannel.getBoolean(P_MOBILE), notificationChannel.getBoolean(P_EMAIL), pNotification, templateBody, notificationTemplate);
            pMessage.getHeader().setInterfaceId(reqInInterfaceId);
            pMessage.getRequestObject().setRequestJson(lBody);

            JSONObject json = pMessage.getResponseObject().getResponseJson();
            LOG.debug("{} Response from sendMessage is : {}", ServerConstants.LOGGER_PREFIX_SMS, json);

            JSONObject jsonresponse = new JSONObject();
            JSONObject mailobj = new JSONObject();

            jsonresponse.put(ServerConstants.MESSAGE, "User Created Successfully, Message has been sent.");
            jsonresponse.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.BOOLEAN_TRUE);
            jsonresponse.put(ServerConstants.MODE_OF_CHANNEL, lpwdRstComChannel);
            jsonresponse.put("allowUserPassEntry", allowUserPassEntry);
            jsonresponse.put(COMMUNICATION, json.getJSONObject(COMMUNICATION));
            mailobj.put(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES, jsonresponse);
            LOG.debug("{} content for creation response : {}", ServerConstants.LOGGER_PREFIX_SMS, mailobj);
            pMessage.getResponseObject().setResponseJson(mailobj);

        } else {//not sending mail here
            LOG.debug("{} password on authorization is YES, we will not communicate over commchannel", ServerConstants.LOGGER_PREFIX_SMS);
            JSONObject json = pMessage.getResponseObject().getResponseJson().getJSONObject(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES);
            JSONObject response = new JSONObject();
            response.put(ServerConstants.ALLOW_USER_PIN_ENTRY, json.get(ServerConstants.ALLOW_USER_PIN_ENTRY));
            response.put(ServerConstants.PIN_ON_AUTHORIZATION, json.get(ServerConstants.PIN_ON_AUTHORIZATION));
            response.put(ServerConstants.MESSAGE, "User Created Successfully");
            response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.BOOLEAN_TRUE);
            pMessage.getResponseObject().setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES, response));
            LOG.debug("{} Response from userMaintenance for create user is: {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());
        }
    }


    //Added by sasidhar to handle user registration, changes made on 07/02/2017
    public void userRegisterRequest(Message pMessage) {
        LOG.info("{} Inside User Registration ", ServerConstants.LOGGER_PREFIX_SMS);
        JSONObject lBody = pMessage.getRequestObject().getRequestJson();
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject outputJson = pMessage.getResponseObject().getResponseJson();
        JSONObject reqFromDomain = (JSONObject) outputJson.get(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES);
        String templateBody = (String) reqFromDomain.get(ServerConstants.SMS_CONSTANTS_BODY);
        String notificationBody = (String) reqFromDomain.get(ServerConstants.NOTIFICATION_CONSTANTS_BODY);
        String allowUserPassEntry = (String) reqFromDomain.get(ServerConstants.ALLOW_USER_PIN_ENTRY);
        LOG.debug("{} allowUserPassEntry : {}", ServerConstants.LOGGER_PREFIX_SMS, allowUserPassEntry);
        Header lHeader = pMessage.getHeader();
        lHeader.setServiceType("getUserMobileNumber");
        pMessage.setHeader(lHeader);
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject responseJson = pMessage.getResponseObject().getResponseJson();
        String luserPhone = responseJson.getString(ServerConstants.MOBILENUMBER);

        pMessage.getResponseObject().setResponseJson(reqFromDomain);
        String lpwdRstComChannel = pMessage.getSecurityParams().getPwdChangeCommChannel();

        // changes made here on 07/02/17 to send notification to user devices
        // sending message through communication channel

        String intfId = pMessage.getHeader().getInterfaceId();
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        LOG.debug(PIN_COMMUNICATION_CHANNEL, ServerConstants.LOGGER_PREFIX_SMS, lpwdRstComChannel);

        JSONObject notificationChannel = SmsUtils.getInstance().getNotificationChannels(lpwdRstComChannel);

        pMessage.getHeader().setAsyncMail(true);
        SmsUtils.getInstance().sendMessage(pMessage, luserPhone, notificationChannel.getBoolean(P_MOBILE), notificationChannel.getBoolean(P_EMAIL), notificationChannel.getBoolean(PNOTIFICATION), templateBody, notificationBody);
        pMessage.getHeader().setInterfaceId(intfId);
        pMessage.getRequestObject().setRequestJson(lBody);

        LOG.debug(RESPONSE_JSON, ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());
        JSONObject json = pMessage.getResponseObject().getResponseJson();
        JSONObject jsonresponse = new JSONObject();
        JSONObject mailobj = new JSONObject();

        jsonresponse.put(ServerConstants.MESSAGE, "User Registered Successfully.");
        jsonresponse.put(ServerConstants.MODE_OF_CHANNEL, lpwdRstComChannel);
        jsonresponse.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
        jsonresponse.put(COMMUNICATION, json.getJSONObject(COMMUNICATION));
        mailobj.put(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES, jsonresponse);
        LOG.debug("{} content for user register response : {}", ServerConstants.LOGGER_PREFIX_SMS, mailobj);

        pMessage.getResponseObject().setResponseJson(mailobj);
    }

    public void updateUser(Message pMessage) {
        LOG.info("{} Updating UserRequest", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void userDelete(Message pMessage) {
        LOG.info("{} Deleting UserRequest", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void searchUser(Message pMessage) {
        LOG.info("{} Searching UserRequest", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getRolesByAppIDUserID(Message pMessage) throws SmsException {
        LOG.info("{} Get Roles by AppId and UserId on UserRequest", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getRolesByAppID(Message pMessage) throws SmsException {
        LOG.info("{} Get Roles by AppId on User request", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    //changes made by sasidhar on 02/07/17
    public void passwordReset(Message pMessage) {
        LOG.info("{} Resetting password UserRequest", ServerConstants.LOGGER_PREFIX_SMS);
        String reqInterfaceId = pMessage.getHeader().getInterfaceId();
        JSONObject lBody = pMessage.getRequestObject().getRequestJson();
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);

        JSONObject responseJson = pMessage.getResponseObject().getResponseJson()
                .getJSONObject(ServerConstants.APPZILLON_ROOT_PIN_RESET_RES);
        String lreqPhoneno = responseJson.getString(ServerConstants.MOBILENUMBER);
        String lpwdRstComChannel = responseJson.getString(ServerConstants.PIN_COMM_CHANNEL);

        String templateBody = responseJson.getString(ServerConstants.SMS_CONSTANTS_BODY);
        String notificationTemplate = responseJson.getString(ServerConstants.NOTIFICATION_CONSTANTS_BODY);
        LOG.debug("{} SMS Body template : {}", ServerConstants.LOGGER_PREFIX_SMS, templateBody);

        //changes made by sasidhar on 07/02/17 to communicate over either email,mobile or notification.
        JSONObject notificationChannel = SmsUtils.getInstance().getNotificationChannels(lpwdRstComChannel);
        LOG.debug("{} Response json is before sending password: {} ", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());
        SmsUtils.getInstance().sendMessage(pMessage, lreqPhoneno, notificationChannel.getBoolean(P_MOBILE), notificationChannel.getBoolean(P_EMAIL), notificationChannel.getBoolean(PNOTIFICATION), templateBody, notificationTemplate);

        pMessage.getHeader().setInterfaceId(reqInterfaceId);
        pMessage.getRequestObject().setRequestJson(lBody);
        LOG.debug(RESPONSE_JSON, ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());

        JSONObject json = pMessage.getResponseObject().getResponseJson();
        JSONObject mailobj = new JSONObject();

        JSONObject finalResp = new JSONObject();
        finalResp.put("message", "Password got reset successfully");
        finalResp.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
        finalResp.put(ServerConstants.MODE_OF_CHANNEL, lpwdRstComChannel);
        finalResp.put(COMMUNICATION, json.getJSONObject(COMMUNICATION));
        mailobj.put(ServerConstants.APPZILLON_ROOT_PIN_RESET_RES, finalResp);
        LOG.debug("{} content for password reset response : {}", ServerConstants.LOGGER_PREFIX_SMS, mailobj);
        pMessage.getResponseObject().setResponseJson(mailobj);
    }

    /**
     * Changes made by Amar on 15/10/2015 Password Reset takes
     *
     * @param pMessage
     */
    public void forgotPassword(Message pMessage) {
        LOG.info("{} Resetting new password for UserRequest", ServerConstants.LOGGER_PREFIX_SMS);
        String reqInterfaceId = pMessage.getHeader().getInterfaceId();
        JSONObject lBody = pMessage.getRequestObject().getRequestJson();

        JSONObject reqJson = pMessage.getRequestObject().getRequestJson();
        LOG.debug("{} reqJson : {}", ServerConstants.LOGGER_PREFIX_SMS, reqJson);
        JSONObject lreqjson = reqJson.getJSONObject(ServerConstants.APPZILLON_FORGOT_PIN_RESET_REQ);
        String lreqPhoneno = lreqjson.getString(ServerConstants.PHNO1);
        String lpwdRstComChannel = pMessage.getSecurityParams().getPwdForgotCommChannel();
        boolean lflag = validateAndUpdate(pMessage, reqJson);
        if (lflag) {
            pMessage.getHeader().setStatus(true);
            String templateBody = pMessage.getResponseObject().getResponseJson()
                    .getJSONObject(ServerConstants.APPZILLON_FORGOT_PIN_RESET_RES)
                    .getString(ServerConstants.SMS_CONSTANTS_BODY);
            LOG.debug("{} Password Changed Successfully!", ServerConstants.LOGGER_PREFIX_SMS);
            String notificationTemplate = pMessage.getResponseObject().getResponseJson()
                    .getJSONObject(ServerConstants.APPZILLON_FORGOT_PIN_RESET_RES)
                    .getString(ServerConstants.NOTIFICATION_CONSTANTS_BODY);
            // Communicate to User via Comm Channel if System Generated Password
            // we will send message to communication channel even
            // lpwdRstAccptPwd is YES.To let know his password is changed.
            LOG.debug("{} ************** Validation done ***********", ServerConstants.LOGGER_PREFIX_SMS);
            pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
            // changes made by sasidhar on 07/02/17 to communicate over either
            // email,mobile or notification.

            JSONObject notificationChannel = SmsUtils.getInstance().getNotificationChannels(lpwdRstComChannel);

            LOG.debug("{} Response json before sending password {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());
            SmsUtils.getInstance().sendMessage(pMessage, lreqPhoneno, notificationChannel.getBoolean(P_MOBILE), notificationChannel.getBoolean(P_EMAIL), notificationChannel.getBoolean(PNOTIFICATION), templateBody, notificationTemplate);
            LOG.debug(RESPONSE_JSON, ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());

            pMessage.getHeader().setInterfaceId(reqInterfaceId);
            pMessage.getRequestObject().setRequestJson(lBody);

            JSONObject json = pMessage.getResponseObject().getResponseJson();
            JSONObject mailobj = new JSONObject();

            JSONObject finalResp = new JSONObject();
            finalResp.put("message", "successfull");
            finalResp.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
            finalResp.put(ServerConstants.MODE_OF_CHANNEL, lpwdRstComChannel);
            finalResp.put(COMMUNICATION, json.getJSONObject(COMMUNICATION));
            mailobj.put(ServerConstants.APPZILLON_FORGOT_PIN_RESET_RES, finalResp);
            pMessage.getResponseObject().setResponseJson(mailobj);
            LOG.debug("{} Final response json is : {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());

        } else {
            pMessage.getHeader().setStatus(false);
            LOG.debug("{} Error in Password Change!. Response Json : {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getResponseObject().getResponseJson());
        }
    }

    /**
     * Changes made by Amar on 15/10/2015 Password Validation and
     *
     * @param pMessage
     */
    private boolean validateAndUpdate(Message pMessage, JSONObject preqJson) {
        boolean validate = false;
        Header lHeader = pMessage.getHeader();

        String luserEml = SmsUtils.getInstance().fetchUserEmail(pMessage);
        String luserPhone = SmsUtils.getInstance().fetchUserPhoneNumber(pMessage);

        // String validateAgainst = pSecurityJson.getString("PwdRsetValidate");getPwdForgotValParams
        String validateAgainst = pMessage.getSecurityParams().getPwdForgotValParams();

        /* Request User Details */
        JSONObject lreqjson = preqJson.getJSONObject(ServerConstants.APPZILLON_FORGOT_PIN_RESET_REQ);
        LOG.debug("{} lreqjson : {}", ServerConstants.LOGGER_PREFIX_SMS, lreqjson.toString());
        String lreqPhoneno = lreqjson.getString(ServerConstants.PHNO1);
        String lreqEmailid = lreqjson.getString(ServerConstants.EML1);

        String lpwdRstAcceptPwd = pMessage.getSecurityParams().getPwdForgotAcceptUsrPwd();
        // // Validation to send mail or sms
        LOG.warn("{} Validating Against :- {}", ServerConstants.LOGGER_PREFIX_SMS, validateAgainst);
        EXCEPTION_CODE lexceptionCode = EXCEPTION_CODE.APZ_SMS_EX_011;
        if (ServerConstants.BOTH.equalsIgnoreCase(validateAgainst)) {
            validate = luserPhone.equals(lreqPhoneno) && luserEml.equals(lreqEmailid);
        } else if (ServerConstants.PMOBILE.equalsIgnoreCase(validateAgainst)) {
            if (luserPhone.equals(lreqPhoneno)) {
                validate = true;
            } else {
                lexceptionCode = EXCEPTION_CODE.APZ_SMS_EX_012;
                validate = false;
            }
        } else if (ServerConstants.PEMAIL.equalsIgnoreCase(validateAgainst)) {
            if (luserEml.equals(lreqEmailid)) {
                validate = true;
            } else {
                lexceptionCode = EXCEPTION_CODE.APZ_SMS_EX_013;
                validate = false;
            }
        } else if (ServerConstants.NONE.equalsIgnoreCase(validateAgainst)) {
            LOG.warn("{} No Validation Reqd", ServerConstants.LOGGER_PREFIX_SMS);
            validate = true;
        }
        // Validation of Password
        if (validate) {
            LOG.debug("{} Accept User Password -: {}", ServerConstants.LOGGER_PREFIX_SMS, lpwdRstAcceptPwd);
            JSONObject lpasswordResetReq = new JSONObject();
            lpasswordResetReq.put(ServerConstants.APPZILLON_FORGOT_PIN_RESET_REQ, lreqjson);
            pMessage.getRequestObject().setRequestJson(lpasswordResetReq);
            String lHeaderInterfaceId = lHeader.getInterfaceId();
            lHeader.setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
            lHeader.setInterfaceId(ServerConstants.INTERFACE_ID_FORGOT_PIN);
            pMessage.setHeader(lHeader);
            DomainStartup.getInstance().processRequest(pMessage);
            lHeader.setInterfaceId(lHeaderInterfaceId);
            pMessage.setHeader(lHeader);


        } else {
            LOG.error(ServerConstants.LOGGER_PREFIX_SMS, lexceptionCode.toString());
            SmsException sexp = SmsException.getSMSExceptionInstance();
            sexp.setMessage(sexp.getSMSExceptionMessage(lexceptionCode));
            sexp.setCode(lexceptionCode.toString());
            sexp.setPriority("1");
            throw sexp;
        }
        return validate;
    }

    public void unlockUser(Message pMessage) {
        LOG.info("{} Unlock the User", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void createPasswordRules(Message pMessage) throws SmsException {
        LOG.info("{} Create Password Rules", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void updatePasswordRules(Message pMessage) throws SmsException {
        LOG.info("{} Update Password Rules", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void deletePasswordRules(Message pMessage) throws SmsException {
        LOG.info("{} Delete Password Rules", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getPasswordRules(Message pMessage) throws SmsException {
        LOG.info("{} Get Password Rules", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void getUser(Message pMessage) {
        LOG.info("{} Get User", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void sendMailToUser(Message pMessage) {
        LOG.debug("{} inside sendMailToUser().", ServerConstants.LOGGER_PREFIX_SMS);
        JSONObject responseComingFromDomain = pMessage.getResponseObject().getResponseJson();
        LOG.debug("{} Response coming from Domain : {}", ServerConstants.LOGGER_PREFIX_SMS, responseComingFromDomain);
        try {
            JSONObject responseFromDomain = getRespFromDomain(responseComingFromDomain);

            LOG.debug("{} response from Domain : {}", ServerConstants.LOGGER_PREFIX_SMS, responseFromDomain);
            JSONObject mailRequestJson = new JSONObject();
            mailRequestJson.put(ServerConstants.INTERFACE_ID_MAIL_REQ, responseFromDomain);
            LOG.debug("{} ************ INTERFACE_ID_MAIL_REQ ****** {}", ServerConstants.LOGGER_PREFIX_SMS, mailRequestJson.getJSONObject(ServerConstants.INTERFACE_ID_MAIL_REQ));
            pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_MAIL_REQ);
            pMessage.getIntfDtls().setType(ServerConstants.APPZILLON_ROOT_MAIL_TYPE);
            pMessage.getRequestObject().setRequestJson(mailRequestJson);
            FrameworksStartup.getInstance().processRequest(pMessage);
        } catch (JSONException jsone) {
            LOG.error("{} json exception before sending mail : {}", ServerConstants.LOGGER_PREFIX_SMS, jsone);
            SmsException sexp = SmsException.getSMSExceptionInstance();
            sexp.setMessage(sexp.getSMSExceptionMessage(EXCEPTION_CODE.APZ_SMS_EX_002));
            sexp.setCode(EXCEPTION_CODE.APZ_SMS_EX_002.toString());
            sexp.setPriority("1");
            throw sexp;
        } catch (AppzillonException ex) {
            LOG.error("{} Abstract Appzillon Exception", ServerConstants.LOGGER_PREFIX_SMS);
            throw getExceptionObjWhenAppzillonExFound(responseComingFromDomain);
        } catch (InvalidPayloadException e) {
            LOG.error("{} InvalidPayloadException: {}", ServerConstants.LOGGER_PREFIX_SMS, e);
            throw getExceptionObjWhenInvalidPayloadExceptionFound(responseComingFromDomain);
        } catch (ClassNotFoundException e) {
            LOG.error("{} ClassNotFoundException: {}", ServerConstants.LOGGER_PREFIX_SMS, e);
            throw getExceptionObjWhenClassNotFoundExceptionFound(responseComingFromDomain);
        } catch (Exception e) {
            LOG.error("{} URIException: {}", ServerConstants.LOGGER_PREFIX_SMS, e);
            throw getExceptionObj(responseComingFromDomain);
        }
    }

    private ExternalServicesRouterException getExceptionObj(JSONObject responseComingFromDomain) {
        ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                .getExternalServicesRouterExceptionInstance();
        if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES)) {
            exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_021.toString());
            exsrvcallexp.setMessage(exsrvcallexp
                    .getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_021));
        } else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES)) {
            exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_051.toString());
            exsrvcallexp.setMessage(exsrvcallexp
                    .getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_051));
        } else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_PIN_RESET_RES) ||
                responseComingFromDomain.has(ServerConstants.APPZILLON_FORGOT_PIN_RESET_RES)) {
            exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022.toString());
            exsrvcallexp.setMessage(exsrvcallexp
                    .getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_022));
        } else if (responseComingFromDomain.has(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES)) {
            exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_053.toString());
            exsrvcallexp.setMessage(exsrvcallexp
                    .getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_053));
        }
        return exsrvcallexp;
    }

    private ExternalServicesRouterException getExceptionObjWhenClassNotFoundExceptionFound(JSONObject responseComingFromDomain) {
        return getExceptionObj(responseComingFromDomain);
    }

    private ExternalServicesRouterException getExceptionObjWhenInvalidPayloadExceptionFound(JSONObject responseComingFromDomain) {
        return getExceptionObj(responseComingFromDomain);
    }

    private ExternalServicesRouterException getExceptionObjWhenAppzillonExFound(JSONObject responseComingFromDomain) {
        return getExceptionObj(responseComingFromDomain);
    }

    private JSONObject getRespFromDomain(JSONObject responseComingFromDomain) {
        JSONObject responseFromDomain;
        if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES)) {
            responseFromDomain = responseComingFromDomain
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES);
        } else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES)) {
            responseFromDomain = responseComingFromDomain
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_REGISTER_USER_RES);
        } else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_PIN_RESET_RES)) {
            responseFromDomain = responseComingFromDomain
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_PIN_RESET_RES);
        } else if (responseComingFromDomain.has(ServerConstants.APPZILLON_FORGOT_PIN_RESET_RES)) {
            responseFromDomain = responseComingFromDomain
                    .getJSONObject(ServerConstants.APPZILLON_FORGOT_PIN_RESET_RES);
        } else if (responseComingFromDomain.has(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES)) {
            responseFromDomain = responseComingFromDomain
                    .getJSONObject(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES);
        } else {
            responseFromDomain = responseComingFromDomain;
        }
        return responseFromDomain;
    }

    @Override
    public void checkDeviceStatus(Message pMessage) {
        LOG.info("{} Routing to Domain to check Device Status", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    // added to authenticate user on 03/04/17
    public void authenticateUser(Message pMessage) {
        LOG.info("{} inside Authenticate User ", ServerConstants.LOGGER_PREFIX_SMS);
        LOG.debug("Request json is {}", pMessage.getRequestObject().getRequestJson());

        String reqInterfaceId = pMessage.getHeader().getInterfaceId();
        JSONObject lBody = pMessage.getRequestObject().getRequestJson();
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject finalResponse = pMessage.getResponseObject().getResponseJson();
        JSONObject userAuthResp = finalResponse.getJSONObject(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES);
        JSONObject commChannel = new JSONObject();
        if (userAuthResp.has(ServerConstants.SMS_CONSTANTS_BODY)
                || userAuthResp.has(ServerConstants.MAIL_CONSTANTS_BODY)) {
            String lpwdCommChannel = finalResponse.getJSONObject(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES)
                    .getString(ServerConstants.PIN_COMM_CHANNEL);
            LOG.debug("Going to communicate over commChannel {}", lpwdCommChannel);
            boolean pMobile;
            boolean pEmail = false;
            boolean pNotification = false;
            String luserPhone = "";

            luserPhone = userAuthResp.getString(ServerConstants.MOBILENUMBER);
            LOG.debug("User mobile number is {}", luserPhone);

            if (ServerConstants.BOTH.equalsIgnoreCase(lpwdCommChannel)) {
                pEmail = true;
            } else if (ServerConstants.PEMAIL.equalsIgnoreCase(lpwdCommChannel)) {
                pEmail = true;
            } else if (ServerConstants.PMOBILE.equalsIgnoreCase(lpwdCommChannel)) {
                LOG.debug("Password comm channel is pMobile");
            }

            pMobile = !luserPhone.isEmpty();

            pMessage.getResponseObject().setResponseJson(finalResponse);
            // sms temlate from response
            String templateBody = (String) finalResponse
                    .getJSONObject(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES)
                    .get(ServerConstants.SMS_CONSTANTS_BODY);
            String notificationTemplate = "";
            SmsUtils.getInstance().sendMessage(pMessage, luserPhone, pMobile, pEmail, pNotification, templateBody, notificationTemplate);
            pMessage.getHeader().setInterfaceId(reqInterfaceId);
            pMessage.getRequestObject().setRequestJson(lBody);
            commChannel = pMessage.getResponseObject().getResponseJson().getJSONObject(COMMUNICATION);
            // have to construct final response
        }
        JSONObject response = new JSONObject();
        response.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.BOOLEAN_TRUE);
        response.put(ServerConstants.MESSAGE, "User authorized successfully");
        if (userAuthResp.has(ServerConstants.SMS_CONSTANTS_BODY)
                || userAuthResp.has(ServerConstants.MAIL_CONSTANTS_BODY)) {
            response.put(COMMUNICATION, commChannel);
        }
        pMessage.getResponseObject()
                .setResponseJson(new JSONObject().put(ServerConstants.APPZILLON_USER_AUTHENTICATION_RES, response));
        LOG.debug("Response from  authenticateUser() is {}", pMessage.getResponseObject().getResponseJson());
    }

    @Override
    public void getDashBoardDetails(Message pMessage) {
        LOG.debug("{} Routing to Domain to get dashboard details", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void saveOrUpdateUserAppAccess(Message pMessage) {
        LOG.info("{} Routing to Domain to insert/update user app access", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
        DomainStartup.getInstance().processRequest(pMessage);

    }

}
