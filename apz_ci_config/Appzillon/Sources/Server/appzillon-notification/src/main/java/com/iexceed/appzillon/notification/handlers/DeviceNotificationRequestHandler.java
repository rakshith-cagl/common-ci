package com.iexceed.appzillon.notification.handlers;

/**
 * @author Vinod Rawat
 */

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.INotificationSender;
import com.iexceed.appzillon.utils.ServerConstants;

public class DeviceNotificationRequestHandler {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getNotificationsLogger(
            ServerConstants.LOGGER_NOTIFICATION, DeviceNotificationRequestHandler.class.getName());
    private INotificationSender cSender;

    public INotificationSender getCSender() {
        return cSender;
    }

    public void setCSender(INotificationSender cSender) {
        this.cSender = cSender;
    }

    public void processRequest(Message pMessage) {

        String mRequesttype = pMessage.getHeader().getInterfaceId();

        if (ServerConstants.INTERFACE_ID_NOTIFICATION_APP_DETAIL.equals(mRequesttype)) {
            LOG.debug("{} Routing to NotificationImpl notificationAppDetail",
                    ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cSender.notificationAppDetail(pMessage);

        } else if (ServerConstants.INTERFACE_ID_PUSH_NOTIFICATION.equals(mRequesttype)) {
            if (pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
                    .has(ServerConstants.NOTIFICATION_OS_ID_MULTIPLE)
                    && pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
                    .getJSONArray(ServerConstants.NOTIFICATION_OS_ID_MULTIPLE).length() > 0)
                pMessage.getHeader().setnotifOsFlag(true);
            if (pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
                    .has(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE)
                    && pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
                    .getJSONArray(ServerConstants.NOTIFICATION_GROUP_ID_MULTIPLE).length() > 0)
                pMessage.getHeader().setnotifGroupFlag(true);
            if (pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
                    .has(ServerConstants.DEVICE_ID_MULTIPLE)
                    && pMessage.getRequestObject().getRequestJson()
                    .getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
                    .getJSONArray(ServerConstants.DEVICE_ID_MULTIPLE).length() > 0)
                pMessage.getHeader().setnotifDeviceFlag(true);
            pMessage.getHeader().setNotifOffset(-1);
            while (pMessage.getHeader().isnotifGroupFlag() || pMessage.getHeader().isnotifOsFlag()
                    || pMessage.getHeader().isnotifDeviceFlag()) {
                LOG.trace("{} isnotifGroupFlag : {}", ServerConstants.LOGGER_PREFIX_NOTIFICAITON,
                        pMessage.getHeader().isnotifGroupFlag());
                LOG.trace("{} isnotifOsFlag : ", ServerConstants.LOGGER_PREFIX_NOTIFICAITON,
                        pMessage.getHeader().isnotifOsFlag());
                LOG.trace("{} isnotifDeviceFlag : ", ServerConstants.LOGGER_PREFIX_NOTIFICAITON,
                        pMessage.getHeader().isnotifDeviceFlag());
                LOG.debug("{} Routing to NotificationImpl  to get regIds of device and grouped devices",
                        ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
                pMessage.getHeader().setNotifOffset(pMessage.getHeader().getNotifOffset() + 1);
                cSender.sendNotificationtoAll(pMessage);
            }

        } else if (ServerConstants.INTERFACE_ID_GET_GROUP_DETAIL.equals(mRequesttype)) {
            LOG.debug("{} Routing to NotificationImpl to GroupDetails", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cSender.getGroupDetails(pMessage);
        }
    }
}