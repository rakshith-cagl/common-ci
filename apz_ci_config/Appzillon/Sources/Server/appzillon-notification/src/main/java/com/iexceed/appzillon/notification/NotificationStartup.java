package com.iexceed.appzillon.notification;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.exception.NotificationException;
import com.iexceed.appzillon.notification.exception.NotificationException.Code;
import com.iexceed.appzillon.notification.handlers.*;
import com.iexceed.appzillon.utils.ServerConstants;
import org.springframework.web.context.WebApplicationContext;

public class NotificationStartup {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION, NotificationStartup.class.getName());
    private static NotificationStartup notificationStartup = null;
    private WebApplicationContext springContext;

    private NotificationStartup() {
    }

    public static NotificationStartup getInstance() {
        if (notificationStartup == null) {
            notificationStartup = new NotificationStartup();
        }
        return notificationStartup;
    }

    public void init(WebApplicationContext wac) {
        springContext = wac;
        LOG.info("{} notification Initialized", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
    }

    public void processRequest(Message pMessage) {
        LOG.debug("{} NotificationStartup.processRequest - pHeaderMap : {}", ServerConstants.LOGGER_PREFIX_NOTIFICAITON,
                pMessage.getHeader());
        LOG.info("{} NotificationStartup.processRequest - pInputjsonstr : {}",
                ServerConstants.LOGGER_PREFIX_NOTIFICAITON, pMessage.getRequestObject().getRequestJson());
        String requesttype = pMessage.getHeader().getInterfaceId();
        if ((ServerConstants.INTERFACE_ID_SEARCH_DEVICE.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_CREATE_DEVICE.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_DELETE_DEVICE.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_UPDATE_DEVICE.equals(requesttype))) {
            DeviceMaintainerRequestHandler devicemaintainRequest = (DeviceMaintainerRequestHandler) getInstance()
                    .getSpringContext()
                    .getBean(pMessage.getHeader().getAppId() + "_" + "deviceMaintainerRequestHandler");
            LOG.debug("{} Routing To DeviceMaintainerRequestHandler", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            devicemaintainRequest.processRequest(pMessage);
        } else if ((ServerConstants.INTERFACE_ID_CREATE_GROUP.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_UPDATE_GROUP.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_DELETE_GROUP.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_SEARCH_GROUP.equals(requesttype))) {
            DeviceGroupRequestHandler devicegroupRequest = (DeviceGroupRequestHandler) getInstance().getSpringContext()
                    .getBean(pMessage.getHeader().getAppId() + "_" + "deviceGroupRequestHandler");
            LOG.debug("{} Routing To DeviceGroupRequestHandler", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            devicegroupRequest.processRequest(pMessage);
        } else if ((ServerConstants.INTERFACE_ID_PUSH_NOTIFICATION.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_NOTIFICATION_APP_DETAIL.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_GET_GROUP_DETAIL.equals(requesttype))) {
            DeviceNotificationRequestHandler notificationhandler = null;
            if (ServerConstants.INTERFACE_ID_PUSH_NOTIFICATION.equals(requesttype)) {
                String appIdInRequest = pMessage.getRequestObject().getRequestJson()
                        .getJSONObject(ServerConstants.APPZILLON_ROOT_PUSH_NOTIFICATIONS_REQ)
                        .getString(ServerConstants.MESSAGE_HEADER_APP_ID);
                notificationhandler = (DeviceNotificationRequestHandler) getInstance().getSpringContext()
                        .getBean(appIdInRequest + "_" + "deviceNotificationRequestHandler");
            } else {
                notificationhandler = (DeviceNotificationRequestHandler) getInstance().getSpringContext()
                        .getBean(pMessage.getHeader().getAppId() + "_" + "deviceNotificationRequestHandler");
            }
            LOG.debug("{} Routing To DeviceNotificationRequestHandler", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            notificationhandler.processRequest(pMessage);

        } else if ((ServerConstants.INTERFACE_ID_DEVICE_REGISTRATION.equals(requesttype))) {
            RegistrationRequestHandler registerRequest = (RegistrationRequestHandler) getInstance().getSpringContext()
                    .getBean(pMessage.getHeader().getAppId() + "_" + "registrationRequestHandler");
            LOG.debug("{} Routing To RegistrationRequestHandler", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            registerRequest.handleRequest(pMessage);

        } else if ((ServerConstants.INTERFACE_ID_SEARCH_FILE.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_UPLOAD_FILE.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_UPLOAD_FILE_WS.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_DELETE_FILE.equals(requesttype))
                || (ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE.equals(requesttype))
                || ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_WS.equals(requesttype)
                || ServerConstants.INTERFACE_ID_FILE_PUSH_SERVICE_AUTH.equals(requesttype)
                || ServerConstants.INTERFACE_ID_UPLOAD_FILE_AUTH.equals(requesttype)) {
            FileRequestHandler request = (FileRequestHandler) getInstance().getSpringContext()
                    .getBean(pMessage.getHeader().getAppId() + "_" + "fileRequestHandler");

            LOG.debug("{} Routing To FileHandler", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            request.processRequest(pMessage);

        } /** changes for Demo Mobile Wallet by Ripu on 23-5-2015 */
        else if (ServerConstants.INTERFACE_ID_NOTIFY_MOBILE_NUMBER.equals(requesttype)) {
            LOG.debug("{} Routing To NotificationHandler", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            MobileNumberNotificationHandler logginghandler = (MobileNumberNotificationHandler) getInstance()
                    .getSpringContext().getBean(pMessage.getHeader().getAppId() + "_mobileNumberNotificationDetail");
            logginghandler.handleRequest(pMessage);
        } else if (ServerConstants.INTERFACE_ID_NOTIFY_DEVICE.equals(requesttype)) {
            LOG.debug("{} Routing To NotificationHandler", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            MobileNumberNotificationHandler logginghandler = (MobileNumberNotificationHandler) getInstance()
                    .getSpringContext().getBean(pMessage.getHeader().getAppId() + "_deviceNotificationDetail");
            logginghandler.handleRequest(pMessage);
        } /** changes End here */
        else {
            NotificationException e = NotificationException.getNotificationExceptionInstance();
            e.setCode((Code.APZ_NT_001).toString());
            e.setPriority("1");
            e.setMessage(e.getNotificationExceptionMessage(Code.APZ_NT_001));
            LOG.error("[NOTIFICATIONS] InterfaceId mismatched", e);
            throw e;
        }
    }

    public WebApplicationContext getSpringContext() {

        return springContext;
    }
}
