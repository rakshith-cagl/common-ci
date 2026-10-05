/**
 *
 */
package com.iexceed.appzillon.sms.handlers;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IHandler;
import com.iexceed.appzillon.sms.iface.ITenantCreation;
import com.iexceed.appzillon.utils.ServerConstants;

public class TenantCreationHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(ServerConstants.LOGGER_SMS,
            TenantCreationHandler.class.getName());

    private ITenantCreation cTenantCreation;

    public ITenantCreation getcTenantCreation() {
        return cTenantCreation;
    }

    public void setcTenantCreation(ITenantCreation cTenantCreation) {
        this.cTenantCreation = cTenantCreation;
    }

    public void handleRequest(Message pMessage) {
        String mRequesttype = pMessage.getHeader().getInterfaceId();
        if (ServerConstants.INTERFACE_ID_CREATE_TENANT.equals(mRequesttype)) {
            LOG.info("{} Routing to Appzillon TenantCreation Impl", ServerConstants.LOGGER_PREFIX_SMS);
            cTenantCreation.createTenant(pMessage);
        }
    }
}
