package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IAuthorization;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Vinod Rawat
 */
public class AuthorisationImpl implements IAuthorization {
    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
            ServerConstants.LOGGER_SMS, AuthorisationImpl.class.getName());

    @Override
    public void handleAuthorization(Message pMessage) {
        LOG.debug("{} Handling Authorization", ServerConstants.LOGGER_PREFIX_SMS);
        if (ServerConstants.INTERFACE_ID_FETCH_PRIVILEGE_SERVICE.equals(pMessage.getHeader().getInterfaceId())
                || ServerConstants.INTERFACE_ID_SCREEN_AUTH_REQ.equals(pMessage.getHeader().getInterfaceId())
                || ServerConstants.INTERFACE_ID_INTF_AUTH_REQ.equals(pMessage.getHeader().getInterfaceId())) {
            pMessage.getHeader().setServiceType(ServerConstants.SERVICE_AUTHORIZATION);
            DomainStartup.getInstance().processRequest(pMessage);
            pMessage.getHeader().setServiceType("");
        }
    }
}
