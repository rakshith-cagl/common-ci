package com.iexceed.appzillon.notification.handlers;

/**
 * @author Vinod Rawat
 */

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.notification.iface.INotificationMaintenance;
import com.iexceed.appzillon.utils.ServerConstants;

public class DeviceMaintainerRequestHandler {

    private static final Logger LOG = LoggerFactory.getLoggerFactory()
            .getNotificationsLogger(ServerConstants.LOGGER_NOTIFICATION,
                    DeviceMaintainerRequestHandler.class.getName());

    private INotificationMaintenance cDeviceMaintainer;

    public void processRequest(Message pMessage) {

        String mRequesttype = pMessage.getHeader().getInterfaceId();
        if (ServerConstants.INTERFACE_ID_SEARCH_DEVICE.equals(mRequesttype)) {
            LOG.debug("{} Routing to DeviceMaintainer Impl Search", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cDeviceMaintainer.search(pMessage);
        } else if (ServerConstants.INTERFACE_ID_CREATE_DEVICE
                .equals(mRequesttype)) {
            LOG.debug("{} Routing to DeviceMaintainer Impl Create", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cDeviceMaintainer.create(pMessage);

        } else if (ServerConstants.INTERFACE_ID_UPDATE_DEVICE
                .equals(mRequesttype)) {
            LOG.debug("{} Routing to DeviceMaintainer Impl Update", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cDeviceMaintainer.update(pMessage);

        } else if (ServerConstants.INTERFACE_ID_DELETE_DEVICE
                .equals(mRequesttype)) {
            LOG.debug("{} Routing to DeviceMaintainer Impl Delete", ServerConstants.LOGGER_PREFIX_NOTIFICAITON);
            cDeviceMaintainer.delete(pMessage);

        }
    }

    public INotificationMaintenance getCDeviceMaintainer() {
        return cDeviceMaintainer;
    }

    public void setCDeviceMaintainer(INotificationMaintenance cDeviceMaintainer) {
        this.cDeviceMaintainer = cDeviceMaintainer;
    }

}
