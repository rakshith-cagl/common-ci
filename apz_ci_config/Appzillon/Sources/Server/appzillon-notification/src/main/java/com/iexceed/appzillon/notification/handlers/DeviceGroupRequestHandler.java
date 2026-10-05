package com.iexceed.appzillon.notification.handlers;

/**
 * @author Vinod Rawat
 */

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.INotificationMaintenance;
import com.iexceed.appzillon.utils.ServerConstants;

public class DeviceGroupRequestHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
                    DeviceGroupRequestHandler.class.getName());
    private INotificationMaintenance cGroupMaintainer;

    public INotificationMaintenance getCGroupMaintainer() {
        return cGroupMaintainer;
    }

    public void setCGroupMaintainer(INotificationMaintenance cGroupMaintainer) {
        this.cGroupMaintainer = cGroupMaintainer;
    }

    public void processRequest(Message pMessage) {

        String mRequesttype = pMessage.getHeader().getInterfaceId();

        if (ServerConstants.INTERFACE_ID_SEARCH_GROUP.equals(mRequesttype)) {
            LOG.debug("{} Routing to GroupMaintenance Impl search()", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cGroupMaintainer.search(pMessage);

        } else if (ServerConstants.INTERFACE_ID_CREATE_GROUP
                .equals(mRequesttype)) {
            LOG.debug("{} Routing to GroupMaintenance Impl create()", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cGroupMaintainer.create(pMessage);

        } else if (ServerConstants.INTERFACE_ID_UPDATE_GROUP
                .equals(mRequesttype)) {
            LOG.debug("{} Routing to GroupMaintenance Impl update()", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cGroupMaintainer.update(pMessage);

        } else if (ServerConstants.INTERFACE_ID_DELETE_GROUP
                .equals(mRequesttype)) {
            LOG.debug("{} Routing to GroupMaintenance Impl delete()", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cGroupMaintainer.delete(pMessage);

        }

    }
}