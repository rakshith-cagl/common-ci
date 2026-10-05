/**
 *
 */
package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IDeviceMasterHandler;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author ripu
 * This class is written for handling all the operation for DeviceMaster
 */
public class DeviceMasterHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS, DeviceMasterHandler.class.getName());

    private IDeviceMasterHandler cDeviceHandler;

    public IDeviceMasterHandler getcDeviceHandler() {
        return cDeviceHandler;
    }

    public void setcDeviceHandler(IDeviceMasterHandler cDeviceHandler) {
        this.cDeviceHandler = cDeviceHandler;
    }

    @Override
    public void handleRequest(Message pMessage) {
        String mRequesttype = pMessage.getHeader().getInterfaceId();
        LOG.debug("{} Interface id : {}", ServerConstants.LOGGER_PREFIX_SMS, mRequesttype);
        LOG.info("{} Routing to IDeviceMasterHandler", ServerConstants.LOGGER_PREFIX_SMS);
        if (ServerConstants.INTERFACE_ID_SEARCH_DEVICE_MASTER.equals(mRequesttype)) {
            cDeviceHandler.searchDeviceMaster(pMessage);
        } else if (ServerConstants.INTERFACE_MULTIFACTOR_DEVICE_REGISTRATION.equals(mRequesttype)) {
            cDeviceHandler.createDeviceMaster(pMessage);
        } else if (ServerConstants.INTERFACE_ID_DELETE_DEVICE_MASTER.equals(mRequesttype)) {
            cDeviceHandler.deleteDeviceMaster(pMessage);
        } else if (ServerConstants.INTERFACE_ID_UPDATE_DEVICE_MASTER.equals(mRequesttype)) {
            cDeviceHandler.updateDeviceMaster(pMessage);
        } else if (ServerConstants.INTERFACE_ID_USER_DEVICE_REGISTRATION.equals(mRequesttype)) {
            cDeviceHandler.registerUserDevice(pMessage);
        }
    }

}
