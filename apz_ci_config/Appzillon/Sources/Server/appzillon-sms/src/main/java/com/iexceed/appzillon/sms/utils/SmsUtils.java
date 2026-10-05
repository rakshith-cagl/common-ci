package com.iexceed.appzillon.sms.utils;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.AppzillonException;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Header;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.NotificationStartup;
import com.iexceed.appzillon.services.SendSMSService;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ServerConstants;
import org.apache.camel.InvalidPayloadException;

import static com.iexceed.appzillon.utils.ServerConstants.*;

public class SmsUtils {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, SmsUtils.class.getName());
    private static SmsUtils smsUtils = null;

    private SmsUtils() {
    }

    public static SmsUtils getInstance() {
        if (smsUtils == null) {
            smsUtils = new SmsUtils();
        }
        return smsUtils;
    }

    public String fetchUserEmail(Message pMessage) {
        LOG.debug("Fetching user email");
        Header lHeader = pMessage.getHeader();
        /* DB User Details */
        lHeader.setServiceType(ServerConstants.APPZILLON_ROOT_USER_EMAILID_REQ);
        pMessage.setHeader(lHeader);
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject responseJson = pMessage.getResponseObject().getResponseJson();
        return responseJson.getString(ServerConstants.APPZILLON_ROOT_EMAILID);
    }


    public String fetchUserPhoneNumber(Message pMessage) {
        LOG.debug("Fetching user phone number");
        Header lHeader = pMessage.getHeader();
        lHeader.setServiceType("getUserMobileNumber");
        pMessage.setHeader(lHeader);
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject responseJson = pMessage.getResponseObject().getResponseJson();
        return responseJson.getString(ServerConstants.MOBILENUMBER);
    }


    public void sendSMSAndGetStatus(JSONObject comm, String lheaderAppId, String luserPhone, String templateBody, Message pMessage) {
        LOG.debug("{} sending sms and templateBody : {}", ServerConstants.LOGGER_PREFIX_SMS, templateBody);
        SendSMSService smsService = new SendSMSService();
        String response = smsService.sendSMS(lheaderAppId, luserPhone, templateBody);
        JSONObject json = new JSONObject(response);
        String status = json.getString(ServerConstants.MESSAGE_HEADER_STATUS);
        pMessage.getResponseObject()
                .setResponseJson(new JSONObject().put(ServerConstants.MESSAGE_HEADER_STATUS, status));
        comm.put("mobile", status);
        LOG.debug("{} End of sending sms", ServerConstants.LOGGER_PREFIX_SMS);
    }

    public void sendEmailAndGetStatus(Message pMessage, JSONObject comm) {
        LOG.debug("{} Sending mail", ServerConstants.LOGGER_PREFIX_SMS);
        sendMailToUser(pMessage);
        LOG.debug("{} End of Sending mail", ServerConstants.LOGGER_PREFIX_SMS);
        String status = pMessage.getResponseObject().getResponseJson()
                .getString(ServerConstants.MESSAGE_HEADER_STATUS);
        comm.put("email", status);
    }

    // added by sasidhar
    public void sendMailToUser(Message pMessage) {
        LOG.debug("{} inside sendMailToUser().", ServerConstants.LOGGER_PREFIX_SMS);
        JSONObject responseComingFromDomain = pMessage.getResponseObject().getResponseJson();
        LOG.debug("{} Response coming from Domain : {}", ServerConstants.LOGGER_PREFIX_SMS, responseComingFromDomain);
        try {
            JSONObject responseFromDomain = null;
            if (responseComingFromDomain.has(ServerConstants.CHANGEPINRESPONSE)) {
                responseFromDomain = responseComingFromDomain
                        .getJSONObject(ServerConstants.CHANGEPINRESPONSE);

            } else if (responseComingFromDomain.has(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES)) {
                responseFromDomain = responseComingFromDomain
                        .getJSONObject(ServerConstants.APPZILLON_ROOT_CREATE_USER_RES);
            } else {
                responseFromDomain = responseComingFromDomain;
            }
            JSONObject mailRequestJson = new JSONObject();
            mailRequestJson.put(ServerConstants.INTERFACE_ID_MAIL_REQ, responseFromDomain);
            LOG.debug("************ INTERFACE_ID_MAIL_REQ ****** : {}", mailRequestJson.getJSONObject(ServerConstants.INTERFACE_ID_MAIL_REQ));
            pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_MAIL_REQ);
            pMessage.getIntfDtls().setType(ServerConstants.APPZILLON_ROOT_MAIL_TYPE);
            pMessage.getRequestObject().setRequestJson(mailRequestJson);
            FrameworksStartup.getInstance().processRequest(pMessage);
        } catch (AppzillonException ex) {
            LOG.error("AbstractAppzillonException");
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                    .getExternalServicesRouterExceptionInstance();
            if (responseComingFromDomain.has(ServerConstants.CHANGEPINRESPONSE)) {
                exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050.toString());
                exsrvcallexp.setMessage(exsrvcallexp
                        .getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050));
            }
            throw exsrvcallexp;
        } catch (InvalidPayloadException e) {
            LOG.error("{} InvalidPayloadException: {}", ServerConstants.LOGGER_PREFIX_SMS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                    .getExternalServicesRouterExceptionInstance();
            if (responseComingFromDomain.has(ServerConstants.CHANGEPINRESPONSE)) {
                exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050.toString());
                exsrvcallexp.setMessage(exsrvcallexp
                        .getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050));
            }
            throw exsrvcallexp;
        } catch (ClassNotFoundException e) {
            LOG.error("{} ClassNotFoundException: {}", ServerConstants.LOGGER_PREFIX_SMS, e);
            ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException
                    .getExternalServicesRouterExceptionInstance();
            if (responseComingFromDomain.has(ServerConstants.CHANGEPINRESPONSE)) {
                exsrvcallexp.setCode(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050.toString());
                exsrvcallexp.setMessage(exsrvcallexp
                        .getFrameWorksExceptionMessage(ExternalServicesRouterException.EXCEPTION_CODE.APZ_FM_EX_050));
            }
            throw exsrvcallexp;
        }
    }

    public void sendNotificationToDevicesAndGetStatus(Message pMessage, JSONObject comm, String notifyTemplate) {
        LOG.debug("{} sending notification to devices notifyTemplate : {}", ServerConstants.LOGGER_PREFIX_SMS, notifyTemplate);
        LOG.debug("{} Fetching all the user registered devices", ServerConstants.LOGGER_PREFIX_SMS);
        JSONObject deviceReg = new JSONObject();
        pMessage.getHeader().setServiceType("notificationSenderService");
        deviceReg.put(ServerConstants.MESSAGE_HEADER_APP_ID, pMessage.getHeader().getAppId());
        deviceReg.put(ServerConstants.MESSAGE_HEADER_USER_ID, pMessage.getHeader().getUserId());
        deviceReg.put(ServerConstants.NOTIFICATION, notifyTemplate);
        pMessage.getRequestObject().setRequestJson(new JSONObject().put("notificationDetail", deviceReg));
        DomainStartup.getInstance().processRequest(pMessage);
        JSONObject respFromNotifSenderservice = pMessage.getResponseObject().getResponseJson();
        String status = "failure";
        if (respFromNotifSenderservice.has(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)) {
            LOG.debug("{} Response from notification sender, which contains push notification request", ServerConstants.LOGGER_PREFIX_SMS);
            if (respFromNotifSenderservice.getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
                    .getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE) != null
                    && respFromNotifSenderservice
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
                    .getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE).length() > 0) {

                LOG.debug("Setting interfaceid to push notification");
                pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_PUSH_NOTIFICATION);
                pMessage.getRequestObject().setRequestJson(pMessage.getResponseObject().getResponseJson());
                LOG.debug("{} before push notification {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getRequestObject().getRequestJson());
                NotificationStartup.getInstance().processRequest(pMessage);
                LOG.debug("{} Going to check whether notification is sent...", ServerConstants.LOGGER_PREFIX_SMS);

                if (pMessage.getResponseObject().getResponseJson()
                        .has(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP)) {
                    status = pMessage.getResponseObject().getResponseJson()
                            .getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP)
                            .getString(ServerConstants.MESSAGE_HEADER_STATUS);
                } else {
                    status = "failure";
                    LOG.debug("{} Failed in sending notification", ServerConstants.LOGGER_PREFIX_SMS);
                }
            } else {
                LOG.debug("{} No device ids found for user", ServerConstants.LOGGER_PREFIX_SMS);
            }

        }
        LOG.debug("{} status from notification {}", ServerConstants.LOGGER_PREFIX_SMS, status);
        comm.put("notification", status);
        LOG.debug("{} End of sending notification to devices", ServerConstants.LOGGER_PREFIX_SMS);
    }

    // added by sasidhar to send messages over communication channel.
    public void sendMessage(Message pMessage, String luserPhone, boolean pMobile, boolean pEmail, boolean pNotificaton,
                            String templateBody, String notifyTemplate) {
        LOG.debug("{} Request json before any communication happened is : {}", ServerConstants.LOGGER_PREFIX_SMS, pMessage.getRequestObject().getRequestJson());
        String lheaderAppId = pMessage.getHeader().getAppId();

        JSONObject comm = new JSONObject();

        if (pEmail) {
            sendEmailAndGetStatus(pMessage, comm);
        }

        if (pMobile) {
            sendSMSAndGetStatus(comm, lheaderAppId, luserPhone, templateBody, pMessage);
        }

        if (pNotificaton) {
            sendNotificationToDevicesAndGetStatus(pMessage, comm, notifyTemplate);
        }
        pMessage.getResponseObject().setResponseJson(new JSONObject().put(COMMUNICATION, comm));
    }

    public JSONObject getNotificationChannels(String pwdComChannel) {
        boolean pMobile = false;
        boolean pEmail = false;
        boolean pNotification = false;
        if (ServerConstants.ALL.equalsIgnoreCase(pwdComChannel)) {
            LOG.debug("{} ************** ALL ***********", ServerConstants.LOGGER_PREFIX_SMS);
            pMobile = true;
            pEmail = true;
            pNotification = true;
        } else if (ServerConstants.PEMAIL.equalsIgnoreCase(pwdComChannel)) {
            LOG.debug("{} ************** EMAIL ***********", ServerConstants.LOGGER_PREFIX_SMS);

            pEmail = true;
        } else if (ServerConstants.PMOBILE.equalsIgnoreCase(pwdComChannel)) {
            LOG.debug("{} ************** SMS ***********", ServerConstants.LOGGER_PREFIX_SMS);

            pMobile = true;
        } else if (ServerConstants.NOTIFICATION.equalsIgnoreCase(pwdComChannel)) {
            LOG.debug("{} ************** NOTIFICATION ***********", ServerConstants.LOGGER_PREFIX_SMS);

            pNotification = true;
        } else if (ServerConstants.PMOBILE_AND_PEMAIL.equalsIgnoreCase(pwdComChannel) ||
                ServerConstants.BOTH.equalsIgnoreCase(pwdComChannel)) {
            LOG.debug("{} ************** MOBILe AND EMAIL ***********", ServerConstants.LOGGER_PREFIX_SMS);

            pMobile = true;
            pEmail = true;
        } else if (ServerConstants.PMOBILE_AND_PNOTIFICATION.equalsIgnoreCase(pwdComChannel)) {
            LOG.debug("{} ************** MOBILE AND NOTIFICATION ***********", ServerConstants.LOGGER_PREFIX_SMS);

            pMobile = true;
            pNotification = true;
        } else if (ServerConstants.PEMAIL_AND_PNOTIFICATION.equalsIgnoreCase(pwdComChannel)) {
            LOG.debug("{} ************** EMAIL AND NOTIFICATION ***********", ServerConstants.LOGGER_PREFIX_SMS);

            pNotification = true;
            pEmail = true;
        } else {
            LOG.debug("{} ************** No Communication Channel ***********", ServerConstants.LOGGER_PREFIX_SMS);
        }
        JSONObject output = new JSONObject();
        output.put(P_MOBILE, pMobile);
        output.put(P_EMAIL, pEmail);
        output.put(PNOTIFICATION, pNotification);
        LOG.debug("Notification Channels: {}", output);
        return output;
    }
}
