package com.iexceed.appzillon.notification.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.NotificationStartup;
import com.iexceed.appzillon.notification.iface.IMobileNumberNotification;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Ripu
 */
public class MobileNumberNotificationImpl implements IMobileNumberNotification {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION, MobileNumberNotificationImpl.class.getName());

    @Override
    public void searchDeviceForNotification(Message pMessage) {
        pMessage.getHeader().setServiceType("notificationSenderService");
        LOG.debug("{} Routing to Domain StartUp", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        DomainStartup.getInstance().processRequest(pMessage);

        String reqIfaceId = pMessage.getHeader().getInterfaceId();
        pMessage.getHeader().setInterfaceId(ServerConstants.INTERFACE_ID_PUSH_NOTIFICATION);
        pMessage.getRequestObject().setRequestJson(pMessage.getResponseObject().getResponseJson());

        LOG.debug("{} Routing to Notification startup", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        NotificationStartup.getInstance().processRequest(pMessage);
        LOG.debug("{} Response From NotificationHandler : {}", ServerConstants.LOGGER_PREFIX_NOTIFICAITON,
                pMessage.getResponseObject().getResponseJson());

        /** Response changed here */
        JSONObject lNotificationRes = pMessage.getResponseObject().getResponseJson();
        JSONObject res = lNotificationRes.getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP);

        // 20-8-205 : Getting success as integer value
        int success = res.getInt("success");
        JSONObject mResponse = new JSONObject();
        JSONObject response = new JSONObject();
        if (success > 0) {
            response.put("status", ServerConstants.SUCCESS);
        } else {
            response.put("status", ServerConstants.FAILURE);
        }
        pMessage.getHeader().setInterfaceId(reqIfaceId);
        mResponse.put(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATION_RESP, response);
        pMessage.getResponseObject().setResponseJson(mResponse);
        LOG.debug("{} Final Response  : {}", ServerConstants.LOGGER_PREFIX_NOTIFICAITON,
                pMessage.getResponseObject().getResponseJson());
    }

}
