package com.iexceed.appzillon.notification.impl;

/**
 * @author Vinod Rawat
 */

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.INotificationMaintenance;
import com.iexceed.appzillon.utils.ServerConstants;

public class DeviceMaintainerImpl implements INotificationMaintenance {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
                    DeviceMaintainerImpl.class.getName());

    public void create(Message pMessage) {
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
        LOG.debug("{} Routing to Domain startup for Device Creation", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void update(Message pMessage) {
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
        LOG.debug("{} Routing to Domain startup for Device Updation", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void delete(Message pMessage) {
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
        LOG.debug("{} Routing to Domain startup for Device Deletion", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void search(Message pMessage) {
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
        LOG.debug("{} Routing to Domain startup for Device Search", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        DomainStartup.getInstance().processRequest(pMessage);
    }
}
