package com.iexceed.appzillon.sms.impl;

import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.sms.iface.IScreenMaintainer;
import com.iexceed.appzillon.utils.ServerConstants;

/**
 * @author Vinod Rawat
 */
public class ScreenMaintainerImpl implements IScreenMaintainer {

    private static final Logger LOG = LoggerFactory.getLoggerFactory().getSmsLogger(
            ServerConstants.LOGGER_SMS, ScreenMaintainerImpl.class.getName());

    @Override
    public void create(Message pMessage) {
        LOG.debug("{} Creating Screen", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_SCREEN_MAINTENANCE);
        LOG.info("{} Routing to Domain Create SCREEN MAINTENANCE SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void update(Message pMessage) {
        LOG.debug("{} Update Screen", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_SCREEN_MAINTENANCE);
        LOG.info("{} Routing to Domain Update SCREEN MAINTENANCE SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void delete(Message pMessage) {
        LOG.debug("{} Delete Screen", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(ServerConstants.SERVICE_SCREEN_MAINTENANCE);
        LOG.info("{} Routing to Domain Delete SCREEN MAINTENANCE SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }

    @Override
    public void search(Message pMessage) {
        LOG.debug("{} Search Screen", ServerConstants.LOGGER_PREFIX_SMS);
        pMessage.getHeader().setServiceType(
                ServerConstants.SERVICE_SCREEN_MAINTENANCE);
        LOG.info("{} Routing to Domain Fetch SCREEN MAINTENANCE SERVICE", ServerConstants.LOGGER_PREFIX_SMS);
        DomainStartup.getInstance().processRequest(pMessage);
    }
}
