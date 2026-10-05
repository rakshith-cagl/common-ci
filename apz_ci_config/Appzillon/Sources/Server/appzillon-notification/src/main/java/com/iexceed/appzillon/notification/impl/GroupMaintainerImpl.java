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

public class GroupMaintainerImpl implements INotificationMaintenance {
    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
                    GroupMaintainerImpl.class.getName());

    public void create(Message pMessage) {
        LOG.debug("{} Routing to Domain startup for Group Creation", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void update(Message pMessage) {
        LOG.debug("{} Routing to Domain startup for Group Updation", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void delete(Message pMessage) {
        LOG.debug("{} Routing to Domain startup for Group Deletion", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    public void search(Message pMessage) {
        LOG.debug("{} Routing to Domain startup for Group Search", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_NOTIFICATION_MAINTENANCE);
        DomainStartup.getInstance().processRequest(pMessage);
    }

}
